/*
 * QEditView.java
 */
package qedit;

import com.thoughtworks.xstream.XStream;
import java.awt.Point;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.jdesktop.application.ApplicationContext;
import qedit.helpers.Authenticate;
import qedit.hints.ExitQuestion;
import qedit.hints.TooManyOpenDocsWarning;
import qedit.task.AbstractTask;
import qedit.task.EmptyReportTask;
import qedit.task.Logout;
import qedit.task.PDFExportTask;
import qedit.task.ReportLoader;
import qedit.task.Salvador;

/**
 * The application's main frame.
 */
public class QEditView extends FrameView {

    private static boolean doShowTooManyDocsWarning = true;
    private static SessionHistory sessionHistory = null;
    public static final String QEDIT_SESSION_HISTORY = "qedit_session_hitory";

    public QEditView(SingleFrameApplication app) {
        super(app);
        initComponents();
        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    cancelRunningTaskButton.setEnabled(true);
                    kindlyStopTaskButton.setEnabled(true);
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    progressBar.setVisible(false);
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setValue(0);
                    kindlyStopTaskButton.setEnabled(false);
                    cancelRunningTaskButton.setEnabled(false);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
        QEditApp.splash.setVisible(false);
        QEditApp.splash.dispose();
    }

    public static int getNumOpenDocuments() {
        return numOpenInternalFrames;
    }

    public static void increaseNumOpenDocuments() {
        numOpenInternalFrames++;
    }
    
    @Action
    public void authenticationAction(){
        JFrame jframe = QEditApp.getView().getFrame();
        Authenticate authenticate = new Authenticate(null, jframe, false);
        int frameWidth = jframe.getWidth();
        int frameHeight = jframe.getHeight();
        int dialogWidht = authenticate.getWidth();
        int dialogHeight = authenticate.getHeight();
        int dialog_x = (frameWidth - dialogWidht) / 2;
        int dialog_y = (frameHeight - dialogHeight) / 2;
        authenticate.setBounds(dialog_x, dialog_y, dialogWidht, dialogHeight);
        authenticate.setVisible(true);
    }

    @Action
    public void quit() {
        ExitQuestion eq = new ExitQuestion(getFrame());
        eq.setLocation(new Point(desktopPane.getWidth() / 2, desktopPane.getHeight() / 2));
        eq.setVisible(true);
        int RS = eq.getReturnStatus();
        if (RS == ExitQuestion.BUTTON_YES) {
            System.exit(0);
        }
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = QEditApp.getApplication().getMainFrame();
            aboutBox = new QEditAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        QEditApp.getApplication().show(aboutBox);
    }

    @Action
    public void openFileAction() {
        localFileChooserWindow = new JFileChooser();
        localFileChooserWindow.setFileFilter(new FileNameExtensionFilter("Reports", "ro"));
        localFileChooserWindow.setMultiSelectionEnabled(false);
        localFileChooserWindow.showOpenDialog(mainPanel);
        if (localFileChooserWindow.getSelectedFile() == null || !localFileChooserWindow.getSelectedFile().exists()) {
            getStatusLabel().setText("No report loaded!");
            return;
        }

        ReportLoader loaderTask = new ReportLoader(localFileChooserWindow);
        loaderTask.runInBackground();

    }

    @Action
    public void exportAsPrf() {
        final ReportIF rif = (ReportIF) desktopPane.getSelectedFrame();
        if (rif == null) {
            getStatusLabel().setText("No document was selected to be exported as PDF!");
            return;
        }
        exportPdfFileChooser = new JFileChooser();
        exportPdfFileChooser.setFileFilter(new FileNameExtensionFilter("QPRF Reports", "ro"));
        exportPdfFileChooser.setMultiSelectionEnabled(false);
        exportPdfFileChooser.setDialogTitle("Export " + rif.getTitle() + " as PDF");
        exportPdfFileChooser.showSaveDialog(mainPanel);
        
        PDFExportTask task = new PDFExportTask(exportPdfFileChooser, rif);
        task.runInBackground();
        
    }

    @Action
    public void saveDialogBox() {
        final ReportIF rif = (ReportIF) desktopPane.getSelectedFrame();
        if (rif == null) {
            getStatusLabel().setText("No document was selected to be saved!");
            return;
        }
        if (rif.getRelatedFile() == null) {
            saveFileChooserWindow = new JFileChooser();
            saveFileChooserWindow.setFileFilter(new FileNameExtensionFilter("QPRF Reports", "ro"));
            saveFileChooserWindow.setMultiSelectionEnabled(false);
            saveFileChooserWindow.setDialogTitle("Save " + rif.getTitle());
            saveFileChooserWindow.showSaveDialog(mainPanel);
        } else {
            saveFileChooserWindow = null;
        }
        Salvador saveFileTask = new Salvador(saveFileChooserWindow, rif);
        saveFileTask.runInBackground();
        if (saveFileChooserWindow != null) {
            rif.setRelatedFile(saveFileChooserWindow.getSelectedFile());
        }

    }

    @Action
    public void createNewEmptyReport() {
        if (doShowTooManyDocsWarning && desktopPane.getAllFrames().length > 2) {
            if (warningDialog == null) {
                warningDialog = new TooManyOpenDocsWarning(QEditApp.getView().getFrame(), true);
                warningDialog.setLocation(new Point(desktopPane.getWidth() / 2, desktopPane.getHeight() / 2));
            }
            warningDialog.setVisible(true);
            doShowTooManyDocsWarning = warningDialog.doShowAgain();
            if (warningDialog.getReturnStatus() == TooManyOpenDocsWarning.CANCEL_NEW_DOCUMENT) {
                return;
            }
        }
        EmptyReportTask internalFrameCreationTask = new EmptyReportTask();
        internalFrameCreationTask.setDesktopPane(desktopPane);
        internalFrameCreationTask.runInBackground();
    }

    public JDesktopPane getDesktopPane() {
        return desktopPane;
    }

    public javax.swing.JLabel getStatusLabel() {
        return statusMessageLabel;
    }

    public static SessionHistory getSessionHistory() {
        return sessionHistory;
    }

    public static void setSessionHistory(SessionHistory sessionHistory) {
        QEditView.sessionHistory = sessionHistory;
    }

    public void refreshSessionsFromPreferences() {
        /*
         * Get Session History from User Preferences:
         */
        Preferences prefs = Preferences.userRoot();
        String sessionHistoryXml = prefs.get(QEDIT_SESSION_HISTORY, null);
        if (sessionHistoryXml != null) {
            sessionHistory = (SessionHistory) new XStream().fromXML(sessionHistoryXml);
        }

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Recent Sessions");

        for (int stack = sessionHistory.size() - 1; stack >= 0; stack--) {
            Session s = sessionHistory.get(stack);
            javax.swing.tree.DefaultMutableTreeNode subNode =
                    new javax.swing.tree.DefaultMutableTreeNode(s.getName() != null ? s.getName() : "Anonymous");
            String compoundURI = (s.getCompoundUri() == null || (s.getCompoundUri() != null && s.getCompoundUri().isEmpty())) ? "N/A" : s.getCompoundUri();
            javax.swing.tree.DefaultMutableTreeNode node =
                    new javax.swing.tree.DefaultMutableTreeNode("Compound URI : " + compoundURI);
            subNode.add(node);
            String modelURI = (s.getModelUri() == null || (s.getModelUri() != null && s.getModelUri().isEmpty())) ? "N/A" : s.getModelUri();
            node = new javax.swing.tree.DefaultMutableTreeNode("Model URI : " + s.getModelUri() != null ? s.getModelUri() : "N/A");
            subNode.add(node);
            node = new javax.swing.tree.DefaultMutableTreeNode("Location : " + s.getFile());
            subNode.add(node);
            rootNode.add(subNode);
        }

        recentSessionsTree.setModel(new DefaultTreeModel(rootNode));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        mainSplitPane = new javax.swing.JSplitPane();
        leftSplittedPanel = new javax.swing.JPanel();
        leftScrollable = new javax.swing.JScrollPane();
        recentSessionsTree = new javax.swing.JTree();
        rightSplittedPanel = new javax.swing.JPanel();
        desktopPane = new javax.swing.JDesktopPane();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        newProjectSubMenu = new javax.swing.JMenu();
        newEmptyReport = new javax.swing.JMenuItem();
        openFileMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        closeMenuItem = new javax.swing.JMenuItem();
        closeAllMenuItem = new javax.swing.JMenuItem();
        firstFileMenuSeparatorItem = new javax.swing.JPopupMenu.Separator();
        qprfOptionsMenuItem = new javax.swing.JMenuItem();
        secondFileMenuSeparatorItem = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        reportMenu = new javax.swing.JMenu();
        wordReportMenuItem = new javax.swing.JMenuItem();
        exportAsPdfMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        toolsOptionsMenuItem = new javax.swing.JMenuItem();
        toolsReportSeparator = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        helpItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        licenseInfoMenuItem = new javax.swing.JMenuItem();
        localFileChooserWindow = new javax.swing.JFileChooser();
        saveFileChooserWindow = new javax.swing.JFileChooser();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        statusFace = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        basicToolbar = new javax.swing.JToolBar();
        newBlankReportButton = new javax.swing.JButton();
        openLocalResourceButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        exportDocButton = new javax.swing.JButton();
        exportPdfButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        aboutToolButton = new javax.swing.JButton();
        ejectButton = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        kindlyStopTaskButton = new javax.swing.JButton();
        cancelRunningTaskButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        loginButton = new javax.swing.JButton();
        logOutButton = new javax.swing.JButton();
        sessionPopupMenu = new javax.swing.JPopupMenu();
        openSessionItem = new javax.swing.JMenuItem();
        clearSessionItem = new javax.swing.JMenuItem();
        clearEntireSessionMenuItem = new javax.swing.JMenuItem();
        exportPdfFileChooser = new javax.swing.JFileChooser();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(QEditView.class);
        mainPanel.setBackground(resourceMap.getColor("mainPanel.background")); // NOI18N
        mainPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(704, 410));

        mainSplitPane.setBackground(resourceMap.getColor("mainSplitPane.background")); // NOI18N
        mainSplitPane.setBorder(new javax.swing.border.MatteBorder(null));
        mainSplitPane.setResizeWeight(0.1);
        mainSplitPane.setInheritsPopupMenu(true);
        mainSplitPane.setName("mainSplitPane"); // NOI18N

        leftSplittedPanel.setName("leftSplittedPanel"); // NOI18N

        leftScrollable.setName("leftScrollable"); // NOI18N

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Recent Sessions");
        recentSessionsTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        Preferences prefs = Preferences.userRoot();
        String sessionHistoryXML = prefs.get(QEDIT_SESSION_HISTORY, null);
        if (sessionHistoryXML!=null){
            sessionHistory = (SessionHistory) new XStream().fromXML(sessionHistoryXML);
        }
        if (sessionHistory==null){
            sessionHistory = new SessionHistory();// Empty Session History
        }
        for (int stack = sessionHistory.size() - 1; stack >= 0; stack--) {
            Session s = sessionHistory.get(stack);
            if (! new java.io.File(s.getFile()).exists()){
                continue;
            }
            javax.swing.tree.DefaultMutableTreeNode subNode =
            new javax.swing.tree.DefaultMutableTreeNode(s.getName() != null ? s.getName() : "Anonymous");
            String compoundURI = (s.getCompoundUri() == null || (s.getCompoundUri() != null && s.getCompoundUri().isEmpty())) ? "N/A" : s.getCompoundUri();
            javax.swing.tree.DefaultMutableTreeNode node =
            new javax.swing.tree.DefaultMutableTreeNode("Compound URI : " + compoundURI);
            subNode.add(node);
            String modelURI = (s.getModelUri() == null || (s.getModelUri() != null && s.getModelUri().isEmpty())) ? "N/A" : s.getModelUri();
            node = new javax.swing.tree.DefaultMutableTreeNode("Model URI : " + s.getModelUri() != null ? s.getModelUri() : "N/A");
            subNode.add(node);
            node = new javax.swing.tree.DefaultMutableTreeNode("Location : " + s.getFile());
            subNode.add(node);
            treeNode1.add(subNode);
        }

        recentSessionsTree.setModel(new DefaultTreeModel(treeNode1));
        recentSessionsTree.setName("recentSessionsTree"); // NOI18N
        recentSessionsTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                recentSessionsTreeMouseClicked(evt);
            }
        });
        recentSessionsTree.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                recentSessionsTreeKeyPressed(evt);
            }
        });
        leftScrollable.setViewportView(recentSessionsTree);

        javax.swing.GroupLayout leftSplittedPanelLayout = new javax.swing.GroupLayout(leftSplittedPanel);
        leftSplittedPanel.setLayout(leftSplittedPanelLayout);
        leftSplittedPanelLayout.setHorizontalGroup(
            leftSplittedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(leftScrollable, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
        );
        leftSplittedPanelLayout.setVerticalGroup(
            leftSplittedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(leftScrollable, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
        );

        mainSplitPane.setLeftComponent(leftSplittedPanel);

        rightSplittedPanel.setName("rightSplittedPanel"); // NOI18N

        desktopPane.setDragMode(javax.swing.JDesktopPane.OUTLINE_DRAG_MODE);
        desktopPane.setName("desktopPane"); // NOI18N

        javax.swing.GroupLayout rightSplittedPanelLayout = new javax.swing.GroupLayout(rightSplittedPanel);
        rightSplittedPanel.setLayout(rightSplittedPanelLayout);
        rightSplittedPanelLayout.setHorizontalGroup(
            rightSplittedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 751, Short.MAX_VALUE)
            .addGroup(rightSplittedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(desktopPane, javax.swing.GroupLayout.DEFAULT_SIZE, 751, Short.MAX_VALUE))
        );
        rightSplittedPanelLayout.setVerticalGroup(
            rightSplittedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
            .addGroup(rightSplittedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(desktopPane, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE))
        );

        mainSplitPane.setRightComponent(rightSplittedPanel);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 864, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setMnemonic('F');
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        newProjectSubMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/document-new.png"))); // NOI18N
        newProjectSubMenu.setText(resourceMap.getString("newProjectSubMenu.text")); // NOI18N
        newProjectSubMenu.setName("newProjectSubMenu"); // NOI18N

        newEmptyReport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newEmptyReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/archive-insert.png"))); // NOI18N
        newEmptyReport.setText(resourceMap.getString("newEmptyReport.text")); // NOI18N
        newEmptyReport.setName("newEmptyReport"); // NOI18N
        newEmptyReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newEmptyReportActionPerformed(evt);
            }
        });
        newProjectSubMenu.add(newEmptyReport);

        fileMenu.add(newProjectSubMenu);

        openFileMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openFileMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/open.png"))); // NOI18N
        openFileMenuItem.setMnemonic('O');
        openFileMenuItem.setText(resourceMap.getString("openFileMenuItem.text")); // NOI18N
        openFileMenuItem.setToolTipText(resourceMap.getString("openFileMenuItem.toolTipText")); // NOI18N
        openFileMenuItem.setName("openFileMenuItem"); // NOI18N
        openFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFileMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openFileMenuItem);

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/media-optical-blu-ray.png"))); // NOI18N
        saveMenuItem.setMnemonic('S');
        saveMenuItem.setText(resourceMap.getString("saveMenuItem.text")); // NOI18N
        saveMenuItem.setToolTipText(resourceMap.getString("saveMenuItem.toolTipText")); // NOI18N
        saveMenuItem.setName("saveMenuItem"); // NOI18N
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);

        closeMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        closeMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/document-close.png"))); // NOI18N
        closeMenuItem.setText(resourceMap.getString("closeMenuItem.text")); // NOI18N
        closeMenuItem.setName("closeMenuItem"); // NOI18N
        closeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(closeMenuItem);

        closeAllMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        closeAllMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/media-playback-stop.png"))); // NOI18N
        closeAllMenuItem.setText(resourceMap.getString("closeAllMenuItem.text")); // NOI18N
        closeAllMenuItem.setName("closeAllMenuItem"); // NOI18N
        closeAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAllMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(closeAllMenuItem);

        firstFileMenuSeparatorItem.setName("firstFileMenuSeparatorItem"); // NOI18N
        fileMenu.add(firstFileMenuSeparatorItem);

        qprfOptionsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        qprfOptionsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/preferences-other.png"))); // NOI18N
        qprfOptionsMenuItem.setMnemonic('E');
        qprfOptionsMenuItem.setText(resourceMap.getString("qprfOptionsMenuItem.text")); // NOI18N
        qprfOptionsMenuItem.setToolTipText(resourceMap.getString("qprfOptionsMenuItem.toolTipText")); // NOI18N
        qprfOptionsMenuItem.setName("qprfOptionsMenuItem"); // NOI18N
        qprfOptionsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qprfOptionsMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(qprfOptionsMenuItem);

        secondFileMenuSeparatorItem.setName("secondFileMenuSeparatorItem"); // NOI18N
        fileMenu.add(secondFileMenuSeparatorItem);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(QEditView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        exitMenuItem.setIcon(resourceMap.getIcon("exitMenuItem.icon")); // NOI18N
        exitMenuItem.setText(resourceMap.getString("exitMenuItem.text")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        reportMenu.setMnemonic('R');
        reportMenu.setText(resourceMap.getString("reportMenu.text")); // NOI18N
        reportMenu.setName("reportMenu"); // NOI18N

        wordReportMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_MASK));
        wordReportMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/application-msword.png"))); // NOI18N
        wordReportMenuItem.setMnemonic('D');
        wordReportMenuItem.setText(resourceMap.getString("wordReportMenuItem.text")); // NOI18N
        wordReportMenuItem.setEnabled(false);
        wordReportMenuItem.setName("wordReportMenuItem"); // NOI18N
        reportMenu.add(wordReportMenuItem);

        exportAsPdfMenuItem.setAction(actionMap.get("exportDocumentAsPDF")); // NOI18N
        exportAsPdfMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_6, java.awt.event.InputEvent.CTRL_MASK));
        exportAsPdfMenuItem.setIcon(resourceMap.getIcon("exportAsPdfMenuItem.icon")); // NOI18N
        exportAsPdfMenuItem.setText(resourceMap.getString("exportAsPdfMenuItem.text")); // NOI18N
        exportAsPdfMenuItem.setName("exportAsPdfMenuItem"); // NOI18N
        exportAsPdfMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportAsPdfMenuItemActionPerformed(evt);
            }
        });
        reportMenu.add(exportAsPdfMenuItem);

        menuBar.add(reportMenu);

        toolsMenu.setMnemonic('T');
        toolsMenu.setText(resourceMap.getString("toolsMenu.text")); // NOI18N
        toolsMenu.setName("toolsMenu"); // NOI18N

        toolsOptionsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/run-build-configure.png"))); // NOI18N
        toolsOptionsMenuItem.setMnemonic('O');
        toolsOptionsMenuItem.setText(resourceMap.getString("toolsOptionsMenuItem.text")); // NOI18N
        toolsOptionsMenuItem.setName("toolsOptionsMenuItem"); // NOI18N
        toolsMenu.add(toolsOptionsMenuItem);

        toolsReportSeparator.setName("toolsReportSeparator"); // NOI18N
        toolsMenu.add(toolsReportSeparator);

        jMenuItem2.setAction(actionMap.get("authenticationAction")); // NOI18N
        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem2.setIcon(resourceMap.getIcon("jMenuItem2.icon")); // NOI18N
        jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        toolsMenu.add(jMenuItem2);

        menuBar.add(toolsMenu);

        helpMenu.setMnemonic('H');
        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        helpItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        helpItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/help-hint.png"))); // NOI18N
        helpItem.setMnemonic('H');
        helpItem.setText(resourceMap.getString("helpItem.text")); // NOI18N
        helpItem.setToolTipText(resourceMap.getString("helpItem.toolTipText")); // NOI18N
        helpItem.setName("helpItem"); // NOI18N
        helpItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpItemActionPerformed(evt);
            }
        });
        helpMenu.add(helpItem);

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        aboutMenuItem.setIcon(resourceMap.getIcon("aboutMenuItem.icon")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        licenseInfoMenuItem.setIcon(resourceMap.getIcon("licenseInfoMenuItem.icon")); // NOI18N
        licenseInfoMenuItem.setText(resourceMap.getString("licenseInfoMenuItem.text")); // NOI18N
        licenseInfoMenuItem.setName("licenseInfoMenuItem"); // NOI18N
        helpMenu.add(licenseInfoMenuItem);

        menuBar.add(helpMenu);

        localFileChooserWindow.setDialogTitle(resourceMap.getString("localFileChooserWindow.dialogTitle")); // NOI18N
        localFileChooserWindow.setName("localFileChooserWindow"); // NOI18N

        saveFileChooserWindow.setName("saveFileChooserWindow"); // NOI18N

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setText(resourceMap.getString("statusMessageLabel.text")); // NOI18N
        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        statusFace.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/face-smile.png"))); // NOI18N
        statusFace.setName("statusFace"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 868, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelLayout.createSequentialGroup()
                        .addComponent(statusAnimationLabel)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelLayout.createSequentialGroup()
                        .addComponent(statusFace)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(statusMessageLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 525, Short.MAX_VALUE)
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43))))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(statusMessageLabel)
                            .addComponent(statusFace))
                        .addGap(4, 4, 4)
                        .addComponent(statusAnimationLabel))
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        basicToolbar.setRollover(true);
        basicToolbar.setName("basicToolbar"); // NOI18N

        newBlankReportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/archive-insert.png"))); // NOI18N
        newBlankReportButton.setToolTipText(resourceMap.getString("newBlankReportButton.toolTipText")); // NOI18N
        newBlankReportButton.setFocusable(false);
        newBlankReportButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newBlankReportButton.setName("newBlankReportButton"); // NOI18N
        newBlankReportButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        newBlankReportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newBlankReportButtonActionPerformed(evt);
            }
        });
        basicToolbar.add(newBlankReportButton);

        openLocalResourceButton.setAction(actionMap.get("openFileAction")); // NOI18N
        openLocalResourceButton.setFocusable(false);
        openLocalResourceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openLocalResourceButton.setName("openLocalResourceButton"); // NOI18N
        openLocalResourceButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        basicToolbar.add(openLocalResourceButton);

        saveButton.setAction(actionMap.get("saveDialogBox")); // NOI18N
        saveButton.setIcon(resourceMap.getIcon("saveButton.icon")); // NOI18N
        saveButton.setToolTipText(resourceMap.getString("saveButton.toolTipText")); // NOI18N
        saveButton.setFocusable(false);
        saveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveButton.setName("saveButton"); // NOI18N
        saveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        basicToolbar.add(saveButton);

        jSeparator2.setName("jSeparator2"); // NOI18N
        basicToolbar.add(jSeparator2);

        exportDocButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/application-msword.png"))); // NOI18N
        exportDocButton.setToolTipText(resourceMap.getString("exportDocButton.toolTipText")); // NOI18N
        exportDocButton.setEnabled(false);
        exportDocButton.setFocusable(false);
        exportDocButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exportDocButton.setName("exportDocButton"); // NOI18N
        exportDocButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        basicToolbar.add(exportDocButton);

        exportPdfButton.setAction(actionMap.get("exportAsPrf")); // NOI18N
        exportPdfButton.setIcon(resourceMap.getIcon("exportPdfButton.icon")); // NOI18N
        exportPdfButton.setToolTipText(resourceMap.getString("exportPdfButton.toolTipText")); // NOI18N
        exportPdfButton.setFocusable(false);
        exportPdfButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exportPdfButton.setName("exportPdfButton"); // NOI18N
        exportPdfButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        basicToolbar.add(exportPdfButton);

        jSeparator1.setName("jSeparator1"); // NOI18N
        basicToolbar.add(jSeparator1);

        aboutToolButton.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutToolButton.setIcon(resourceMap.getIcon("aboutToolButton.icon")); // NOI18N
        aboutToolButton.setText(resourceMap.getString("aboutToolButton.text")); // NOI18N
        aboutToolButton.setFocusable(false);
        aboutToolButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        aboutToolButton.setName("aboutToolButton"); // NOI18N
        aboutToolButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        basicToolbar.add(aboutToolButton);

        ejectButton.setAction(actionMap.get("quit")); // NOI18N
        ejectButton.setIcon(resourceMap.getIcon("ejectButton.icon")); // NOI18N
        ejectButton.setText(resourceMap.getString("ejectButton.text")); // NOI18N
        ejectButton.setFocusable(false);
        ejectButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ejectButton.setName("ejectButton"); // NOI18N
        ejectButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        basicToolbar.add(ejectButton);

        jSeparator6.setName("jSeparator6"); // NOI18N
        basicToolbar.add(jSeparator6);

        kindlyStopTaskButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/edit-bomb.png"))); // NOI18N
        kindlyStopTaskButton.setToolTipText(resourceMap.getString("kindlyStopTaskButton.toolTipText")); // NOI18N
        kindlyStopTaskButton.setEnabled(false);
        kindlyStopTaskButton.setFocusable(false);
        kindlyStopTaskButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        kindlyStopTaskButton.setName("kindlyStopTaskButton"); // NOI18N
        kindlyStopTaskButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        kindlyStopTaskButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kindlyStopTaskButtonActionPerformed(evt);
            }
        });
        basicToolbar.add(kindlyStopTaskButton);

        cancelRunningTaskButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/dialog-close.png"))); // NOI18N
        cancelRunningTaskButton.setToolTipText(resourceMap.getString("cancelRunningTaskButton.toolTipText")); // NOI18N
        cancelRunningTaskButton.setEnabled(false);
        cancelRunningTaskButton.setName("cancelRunningTaskButton"); // NOI18N
        cancelRunningTaskButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelRunningTaskButtonActionPerformed(evt);
            }
        });
        basicToolbar.add(cancelRunningTaskButton);

        jSeparator3.setName("jSeparator3"); // NOI18N
        basicToolbar.add(jSeparator3);

        loginButton.setIcon(resourceMap.getIcon("loginButton.icon")); // NOI18N
        loginButton.setText(resourceMap.getString("loginButton.text")); // NOI18N
        loginButton.setToolTipText(resourceMap.getString("loginButton.toolTipText")); // NOI18N
        loginButton.setFocusable(false);
        loginButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        loginButton.setName("loginButton"); // NOI18N
        loginButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });
        basicToolbar.add(loginButton);

        logOutButton.setIcon(resourceMap.getIcon("logOutButton.icon")); // NOI18N
        logOutButton.setText(resourceMap.getString("logOutButton.text")); // NOI18N
        logOutButton.setToolTipText(resourceMap.getString("logOutButton.toolTipText")); // NOI18N
        logOutButton.setFocusable(false);
        logOutButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        logOutButton.setName("logOutButton"); // NOI18N
        logOutButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        logOutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logOutButtonActionPerformed(evt);
            }
        });
        basicToolbar.add(logOutButton);

        sessionPopupMenu.setName("sessionPopupMenu"); // NOI18N

        openSessionItem.setIcon(resourceMap.getIcon("openSessionItem.icon")); // NOI18N
        openSessionItem.setText(resourceMap.getString("openSessionItem.text")); // NOI18N
        openSessionItem.setName("openSessionItem"); // NOI18N
        openSessionItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openSessionItemActionPerformed(evt);
            }
        });
        sessionPopupMenu.add(openSessionItem);

        clearSessionItem.setIcon(resourceMap.getIcon("clearSessionItem.icon")); // NOI18N
        clearSessionItem.setText(resourceMap.getString("clearSessionItem.text")); // NOI18N
        clearSessionItem.setName("clearSessionItem"); // NOI18N
        clearSessionItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearSessionItemActionPerformed(evt);
            }
        });
        sessionPopupMenu.add(clearSessionItem);

        clearEntireSessionMenuItem.setIcon(resourceMap.getIcon("clearEntireSessionMenuItem.icon")); // NOI18N
        clearEntireSessionMenuItem.setText(resourceMap.getString("clearEntireSessionMenuItem.text")); // NOI18N
        clearEntireSessionMenuItem.setName("clearEntireSessionMenuItem"); // NOI18N
        clearEntireSessionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearEntireSessionMenuItemActionPerformed(evt);
            }
        });
        sessionPopupMenu.add(clearEntireSessionMenuItem);

        exportPdfFileChooser.setName("exportPdfFileChooser"); // NOI18N

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
        setToolBar(basicToolbar);
    }// </editor-fold>//GEN-END:initComponents

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        saveDialogBox();
    }//GEN-LAST:event_saveMenuItemActionPerformed

    private void helpItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_helpItemActionPerformed

    private void openFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFileMenuItemActionPerformed
        openFileAction();
}//GEN-LAST:event_openFileMenuItemActionPerformed

    private void newEmptyReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newEmptyReportActionPerformed
        createNewEmptyReport();
    }//GEN-LAST:event_newEmptyReportActionPerformed

    private void newBlankReportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newBlankReportButtonActionPerformed
        createNewEmptyReport();
    }//GEN-LAST:event_newBlankReportButtonActionPerformed

    private void qprfOptionsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qprfOptionsMenuItemActionPerformed
//        editorOptionsDialogBox();
    }//GEN-LAST:event_qprfOptionsMenuItemActionPerformed

    private void cancelRunningTaskButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelRunningTaskButtonActionPerformed
        ApplicationContext appC = QEditApp.getInstance().getContext();
        TaskMonitor taskMonitor = appC.getTaskMonitor();
        taskMonitor.getForegroundTask().cancel(true);
    }//GEN-LAST:event_cancelRunningTaskButtonActionPerformed

    private void kindlyStopTaskButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kindlyStopTaskButtonActionPerformed
        ApplicationContext appC = QEditApp.getInstance().getContext();
        TaskMonitor taskMonitor = appC.getTaskMonitor();
        taskMonitor.getForegroundTask().cancel(false);
    }//GEN-LAST:event_kindlyStopTaskButtonActionPerformed

    private void closeAllMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeAllMenuItemActionPerformed
        JInternalFrame[] frames = desktopPane.getAllFrames();
        if (frames.length > 0) {
            for (JInternalFrame frame : frames) {
                frame.setVisible(false);
                frame.dispose();
            }
            getStatusLabel().setText("All documents closed!");
        } else {
            getStatusLabel().setText("No documents are open...");
        }
        QEditView.numOpenInternalFrames = 0;
    }//GEN-LAST:event_closeAllMenuItemActionPerformed

    private void closeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeMenuItemActionPerformed
        javax.swing.JInternalFrame frame = desktopPane.getSelectedFrame();
        if (frame != null) {
            frame.setVisible(false);
            frame.dispose();
            getStatusLabel().setText(frame.getTitle() + " closed!");
            if (desktopPane.getAllFrames().length > 0) {
                try {
                    desktopPane.getAllFrames()[0].setSelected(true);
                } catch (PropertyVetoException ex) {
                    Logger.getLogger(QEditView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            getStatusLabel().setText("No document selected");
        }
    }//GEN-LAST:event_closeMenuItemActionPerformed

    private void openSessionItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openSessionItemActionPerformed
//        String filename = sessionPopupChoice.getLastChild().toString().replaceAll("Location : ", "");
//        AbstractTask task = new LoadSessionTask(filename);
//        task.runInBackground();
    }//GEN-LAST:event_openSessionItemActionPerformed

    private void clearEntireSessionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearEntireSessionMenuItemActionPerformed
        sessionHistory = new SessionHistory();
        Preferences prefs = Preferences.userRoot();
        prefs.put(QEDIT_SESSION_HISTORY, new XStream().toXML(sessionHistory));
        refreshSessionsFromPreferences();
    }//GEN-LAST:event_clearEntireSessionMenuItemActionPerformed

    private void clearSessionItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearSessionItemActionPerformed
        int nodeToRemove = ((DefaultMutableTreeNode) recentSessionsTree.getModel().getRoot()).getIndex(sessionPopupChoice);
        sessionHistory.remove(sessionHistory.size() - nodeToRemove - 1);
        Preferences.userRoot().put(QEDIT_SESSION_HISTORY, new XStream().toXML(sessionHistory));
        refreshSessionsFromPreferences();
    }//GEN-LAST:event_clearSessionItemActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
//        AuthenticationBox box = new AuthenticationBox(QEditApp.getView().getFrame(), true);
//        box.setLocation(new Point(desktopPane.getWidth() / 2, desktopPane.getHeight() / 2));
//        box.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void recentSessionsTreeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_recentSessionsTreeKeyPressed

        if (evt.getKeyCode() == 525) {
            TreePath path = recentSessionsTree.getSelectionPath();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            if (((DefaultMutableTreeNode) recentSessionsTree.getModel().getRoot()).isNodeChild(node)) {
                sessionPopupChoice = node;
                sessionPopupMenu.show(recentSessionsTree, 100, 100);
            }
        }
}//GEN-LAST:event_recentSessionsTreeKeyPressed

    private void recentSessionsTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_recentSessionsTreeMouseClicked
//        if (evt.getButton() == MouseEvent.BUTTON3) {
//            Point point = evt.getPoint();
//            TreePath path = recentSessionsTree.getClosestPathForLocation(point.x, point.y);
//            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
//            if (((DefaultMutableTreeNode) recentSessionsTree.getModel().getRoot()).isNodeChild(node)) {
//                sessionPopupChoice = node;
//                sessionPopupMenu.show(recentSessionsTree, evt.getX(), evt.getY());
//            }
//        }
//        if (evt.getClickCount() == 2) {
//            Point point = evt.getPoint();
//            TreePath path = recentSessionsTree.getClosestPathForLocation(point.x, point.y);
//            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
//            if (((DefaultMutableTreeNode) recentSessionsTree.getModel().getRoot()).isNodeChild(node)) {
//                sessionPopupChoice = node;
//                String filename = sessionPopupChoice.getLastChild().toString().replaceAll("Location : ", "");
//                AbstractTask task = new LoadSessionTask(filename);
//                task.runInBackground();
//            }
//        }
}//GEN-LAST:event_recentSessionsTreeMouseClicked

    private void logOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logOutButtonActionPerformed
        Logout logOutTask = new Logout();
        logOutTask.runInBackground();
    }//GEN-LAST:event_logOutButtonActionPerformed

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        authenticationAction();

    }//GEN-LAST:event_loginButtonActionPerformed

    private void exportAsPdfMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportAsPdfMenuItemActionPerformed
        exportAsPrf();
    }//GEN-LAST:event_exportAsPdfMenuItemActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutToolButton;
    private javax.swing.JToolBar basicToolbar;
    private javax.swing.JButton cancelRunningTaskButton;
    private javax.swing.JMenuItem clearEntireSessionMenuItem;
    private javax.swing.JMenuItem clearSessionItem;
    private javax.swing.JMenuItem closeAllMenuItem;
    private javax.swing.JMenuItem closeMenuItem;
    private javax.swing.JDesktopPane desktopPane;
    private javax.swing.JButton ejectButton;
    private javax.swing.JMenuItem exportAsPdfMenuItem;
    private javax.swing.JButton exportDocButton;
    private javax.swing.JButton exportPdfButton;
    private javax.swing.JFileChooser exportPdfFileChooser;
    private javax.swing.JPopupMenu.Separator firstFileMenuSeparatorItem;
    private javax.swing.JMenuItem helpItem;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JButton kindlyStopTaskButton;
    private javax.swing.JScrollPane leftScrollable;
    private javax.swing.JPanel leftSplittedPanel;
    private javax.swing.JMenuItem licenseInfoMenuItem;
    private javax.swing.JFileChooser localFileChooserWindow;
    private javax.swing.JButton logOutButton;
    private javax.swing.JButton loginButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JSplitPane mainSplitPane;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton newBlankReportButton;
    private javax.swing.JMenuItem newEmptyReport;
    private javax.swing.JMenu newProjectSubMenu;
    private javax.swing.JMenuItem openFileMenuItem;
    private javax.swing.JButton openLocalResourceButton;
    private javax.swing.JMenuItem openSessionItem;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JMenuItem qprfOptionsMenuItem;
    private javax.swing.JTree recentSessionsTree;
    private javax.swing.JMenu reportMenu;
    private javax.swing.JPanel rightSplittedPanel;
    private javax.swing.JButton saveButton;
    private javax.swing.JFileChooser saveFileChooserWindow;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JPopupMenu.Separator secondFileMenuSeparatorItem;
    private javax.swing.JPopupMenu sessionPopupMenu;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusFace;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JMenuItem toolsOptionsMenuItem;
    private javax.swing.JPopupMenu.Separator toolsReportSeparator;
    private javax.swing.JMenuItem wordReportMenuItem;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
    private JDialog enterUriBox;
    private JDialog statisticsBox;
    private JDialog editorOptionsBox;
    private static int numOpenInternalFrames = 0;
    private static TooManyOpenDocsWarning warningDialog;
    private static DefaultMutableTreeNode sessionPopupChoice;
}
