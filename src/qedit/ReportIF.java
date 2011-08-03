/*
 * ReportIF.java
 *
 * Created on Jul 31, 2011, 6:13:15 PM
 */
package qedit;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
import org.opentox.toxotis.client.VRI;
import org.opentox.toxotis.core.component.Compound;
import org.opentox.toxotis.core.component.Feature;
import org.opentox.toxotis.core.component.Model;
import org.opentox.toxotis.core.component.qprf.QprfAuthor;
import org.opentox.toxotis.core.component.qprf.QprfReport;
import org.opentox.toxotis.exceptions.ISecurityException;
import org.opentox.toxotis.exceptions.impl.ServiceInvocationException;
import org.opentox.toxotis.ontology.LiteralValue;
import qedit.helpers.AddAuthor;
import qedit.helpers.AddSynonym;
import qedit.helpers.AlgorithmDialog;
import qedit.helpers.Authenticate;
import qedit.helpers.CompoundDetails;
import qedit.helpers.FeatureDetails;
import qedit.helpers.ModelDetailsDialog;
import qedit.helpers.Stereo;
import qedit.task.CompoundInfo;
import qedit.task.ImportModel;

/**
 *
 * @author Pantelis Sopasakis
 */
public class ReportIF extends javax.swing.JInternalFrame {

    /** Creates new form ReportIF */
    public ReportIF() {
        initComponents();
    }

    public QprfReport getReport() {
        return report;
    }

    public void setReport(QprfReport report) {
        this.report = report;
    }

    public void deleteCompoundFields() {
        ImageIcon noImage = org.jdesktop.application.Application.getInstance(qedit.QEditApp.class).getContext().getResourceMap(ReportIF.class).getImageIcon("structureImage.icon");
        depiction.setIcon(noImage);
        synonymsList.setModel(new DefaultListModel());
    }

    private void removeSelectedRows(JList list) {
        int[] selectedRows = list.getSelectedIndices();
        ListModel listModel = list.getModel();
        while (selectedRows.length > 0) {
            ((DefaultListModel) listModel).remove(selectedRows[0]);
            selectedRows = list.getSelectedIndices();
        }
        list.clearSelection();
    }

    private void removeSelectedRows(JTable table) {
        int[] selectedRows = table.getSelectedRows();
        TableModel tableModel = table.getModel();
        while (selectedRows.length > 0) {
            ((DefaultTableModel) tableModel).removeRow(table.convertRowIndexToModel(selectedRows[0]));
            selectedRows = table.getSelectedRows();
        }
        table.clearSelection();
    }

    public void updateCompoundFields() {
        Compound compound = report.getCompound();
        compound.setDepiction(null);
        ImageIcon compoundIcon = compound.getDepiction(null);
        if (compoundIcon != null) {
            int height = depiction.getHeight();
            int width = depiction.getWidth();
            int imageH = compoundIcon.getIconHeight();
            int imageW = compoundIcon.getIconWidth();
            if (imageH > 0 && imageW > 0) {
                if (imageH > height || imageW > width) { // Scaling Needed!!!
                    double scalingFactor = Math.min((double) width / (double) imageW, (double) height / (double) imageH);
                    int scaledW = (int) Math.round(scalingFactor * imageW);
                    int scaledH = (int) Math.round(scalingFactor * imageH);
                    BufferedImage dst = new BufferedImage(
                            scaledW,
                            scaledH,
                            BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2 = dst.createGraphics();
                    g2.drawImage(compoundIcon.getImage(), 0, 0, (int) scaledW, scaledH, compoundIcon.getImageObserver());
                    g2.dispose();
                    depiction.setIcon(new ImageIcon(dst));
                } else { // No scaling Needed!!!
                    depiction.setIcon(compoundIcon);
                }
                getRefreshDepictionButton().setEnabled(true);
            }
        } else {
            getRefreshDepictionButton().setEnabled(false);
        }
    }

    public void displayDiagol(JDialog dialog, JFrame jframe) {
        int frameWidth = jframe.getWidth();
        int frameHeight = jframe.getHeight();
        int dialogWidht = dialog.getWidth();
        int dialogHeight = dialog.getHeight();
        int dialog_x = (frameWidth - dialogWidht) / 2;
        int dialog_y = (frameHeight - dialogHeight) / 2;
        dialog.setBounds(dialog_x, dialog_y, dialogWidht, dialogHeight);
        dialog.setVisible(true);

    }

    private void addSynonym() {
        JFrame jframe = QEditApp.getView().getFrame();
        AddSynonym stereo = new AddSynonym(this, jframe, true);
        displayDiagol(stereo, jframe);
    }

    private void stereoClicked() {
        JFrame jframe = QEditApp.getView().getFrame();
        Stereo stereo = new Stereo(this, jframe, true);
        displayDiagol(stereo, jframe);
    }

    private void addAuthor() {
        JFrame jframe = QEditApp.getView().getFrame();
        AddAuthor addAuthor = new AddAuthor(this, jframe, true);
        displayDiagol(addAuthor, jframe);
    }

    private void accessCompoundDetails() {
        JFrame jframe = QEditApp.getView().getFrame();
        compoundDetails = new CompoundDetails(this, jframe, true);
        displayDiagol(compoundDetails, jframe);
    }

    private void loadModel() {
        ImportModel modelImporterTask = new ImportModel(this);
        modelImporterTask.runInBackground();
    }

    private void modelDetails() {
        JFrame jframe = QEditApp.getView().getFrame();
        ModelDetailsDialog mdlDetails = new ModelDetailsDialog(this, jframe, true);
        displayDiagol(mdlDetails, jframe);
    }
    
    private void predFeatureDetails(){
        JFrame jframe = QEditApp.getView().getFrame();
        FeatureDetails mdlDetails = new FeatureDetails(getReport().getModel().getPredictedFeatures().get(0), jframe, true);
        displayDiagol(mdlDetails, jframe);
    }
    private void depFeatureDetails(){
        JFrame jframe = QEditApp.getView().getFrame();
        FeatureDetails mdlDetails = new FeatureDetails(getReport().getModel().getDependentFeatures().get(0), jframe, true);
        displayDiagol(mdlDetails, jframe);
    }

    private void algoDetails() {
        JFrame jframe = QEditApp.getView().getFrame();
        AlgorithmDialog algoDetails = new AlgorithmDialog(this, jframe, true);
        displayDiagol(algoDetails, jframe);
    }

    private void enableIfCaretUpdate(JTextComponent text, JComponent... components) {
        String t = text.getText().trim();
        boolean doEnable = t != null && !t.isEmpty();
        for (JComponent jc : components) {
            jc.setEnabled(doEnable);
        }
    }

    public void setEnabledModelDetailsButtons(boolean enable) {
        modelDetails.setEnabled(enable);
        algorithmDetails.setEnabled(enable);
        predFeatDetails.setEnabled(enable);
        depFeatDetails.setEnabled(enable);
    }

    /*
     * 
     * 
     * 
     * GETTERS
     * GETTERS
     * GETTERS
     * GETTERS
     * GETTERS
     * 
     */
    public JTextField getModelUriField() {
        return modelUriField;
    }

    public JList getSynonymsList() {
        return synonymsList;
    }

    public JButton getCompDetailsButton() {
        return compDetailsButton;
    }

    public JButton getLoadCompoundButton() {
        return loadCompoundButton;
    }

    public JButton getStereoFeaturesButton() {
        return stereoFeaturesButton;
    }

    public JButton getRefreshDepictionButton() {
        return refreshDepictionButton;
    }

    public JList getAuthorsList() {
        return authorsList;
    }

    public Map<String, QprfAuthor> getAUTHORSMAP() {
        return AUTHORS_MAP;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        basePanel = new javax.swing.JTabbedPane();
        compoundTab = new javax.swing.JPanel();
        compoundToolbar = new javax.swing.JToolBar();
        loadCompoundButton = new javax.swing.JButton();
        compDetailsButton = new javax.swing.JButton();
        stereoFeaturesButton = new javax.swing.JButton();
        coumpoundIdentifierLabel = new javax.swing.JLabel();
        compoundIdentifier = new javax.swing.JTextField();
        depictionPanel = new javax.swing.JPanel();
        depictionToolbar = new javax.swing.JToolBar();
        refreshDepictionButton = new javax.swing.JButton();
        depiction = new javax.swing.JLabel();
        synonymsPanel = new javax.swing.JPanel();
        synonymsScollable = new javax.swing.JScrollPane();
        synonymsList = new javax.swing.JList();
        synonymsToolbar = new javax.swing.JToolBar();
        addSynonymButton = new javax.swing.JButton();
        removeSynonymButton = new javax.swing.JButton();
        removeAllSynonymsButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        moveCompoundUpButton = new javax.swing.JButton();
        moveCompoundDownButton = new javax.swing.JButton();
        generalTab = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        authorsList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        addAuthorButton = new javax.swing.JButton();
        removeAuthorButton = new javax.swing.JButton();
        moveUpAuthor = new javax.swing.JButton();
        moveDownAuthor = new javax.swing.JButton();
        emailLabel = new javax.swing.JLabel();
        webPageLabel = new javax.swing.JLabel();
        affilLabel = new javax.swing.JLabel();
        addressLabel = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jComboBox3 = new javax.swing.JComboBox();
        emailField = new javax.swing.JLabel();
        authWebPageField = new javax.swing.JLabel();
        authAffiliationField = new javax.swing.JLabel();
        authAddressField = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        labelModelUri = new javax.swing.JLabel();
        modelUriField = new javax.swing.JTextField();
        jToolBar2 = new javax.swing.JToolBar();
        loadModelButton = new javax.swing.JButton();
        modelDetails = new javax.swing.JButton();
        algorithmDetails = new javax.swing.JButton();
        predFeatDetails = new javax.swing.JButton();
        depFeatDetails = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jComboBox6 = new javax.swing.JComboBox();
        jComboBox5 = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();
        jPanel8 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel10 = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        jButton6 = new javax.swing.JButton();
        dwnLoadExpValueButton = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTextArea10 = new javax.swing.JTextArea();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        jTextArea11 = new javax.swing.JTextArea();
        jScrollPane13 = new javax.swing.JScrollPane();
        jTextArea12 = new javax.swing.JTextArea();
        jLabel18 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextArea6 = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTextArea7 = new javax.swing.JTextArea();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTextArea8 = new javax.swing.JTextArea();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTextArea9 = new javax.swing.JTextArea();
        jLabel14 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        basePanel.setName("basePanel"); // NOI18N

        compoundTab.setName("compoundTab"); // NOI18N

        compoundToolbar.setFloatable(false);
        compoundToolbar.setRollover(true);
        compoundToolbar.setName("compoundToolbar"); // NOI18N

        loadCompoundButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/download.png"))); // NOI18N
        loadCompoundButton.setText("Load");
        loadCompoundButton.setToolTipText("Load compound info from OpenTox services");
        loadCompoundButton.setEnabled(false);
        loadCompoundButton.setFocusable(false);
        loadCompoundButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        loadCompoundButton.setName("loadCompoundButton"); // NOI18N
        loadCompoundButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        loadCompoundButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadCompoundButtonActionPerformed(evt);
            }
        });
        compoundToolbar.add(loadCompoundButton);

        compDetailsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/documentation.png"))); // NOI18N
        compDetailsButton.setText("Details");
        compDetailsButton.setToolTipText("<html><p width=\"300\">Access downloaded compound details</p>");
        compDetailsButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        compDetailsButton.setEnabled(false);
        compDetailsButton.setFocusable(false);
        compDetailsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        compDetailsButton.setName("compDetailsButton"); // NOI18N
        compDetailsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        compDetailsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compDetailsButtonActionPerformed(evt);
            }
        });
        compoundToolbar.add(compDetailsButton);

        stereoFeaturesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/draw-spiral.png"))); // NOI18N
        stereoFeaturesButton.setText("Stereo");
        stereoFeaturesButton.setToolTipText("<html><p width=\"300\">View/Edit notes on the stereochemical features of the compound that might affect the reliability of the prediction</p>");
        stereoFeaturesButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        stereoFeaturesButton.setEnabled(false);
        stereoFeaturesButton.setFocusable(false);
        stereoFeaturesButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stereoFeaturesButton.setName("stereoFeaturesButton"); // NOI18N
        stereoFeaturesButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        stereoFeaturesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stereoFeaturesButtonActionPerformed(evt);
            }
        });
        compoundToolbar.add(stereoFeaturesButton);

        coumpoundIdentifierLabel.setLabelFor(compoundIdentifier);
        coumpoundIdentifierLabel.setText("Compound Identifier :");
        coumpoundIdentifierLabel.setToolTipText("<html><p width=\"300\">Any identifier of the compound (IUPAC Name, SMILES, CAS-RN, etc)");
        coumpoundIdentifierLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        coumpoundIdentifierLabel.setName("coumpoundIdentifierLabel"); // NOI18N

        compoundIdentifier.setName("compoundIdentifier"); // NOI18N
        compoundIdentifier.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                compoundIdentifierCaretUpdate(evt);
            }
        });

        depictionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Depiction"));
        depictionPanel.setName("depictionPanel"); // NOI18N

        depictionToolbar.setFloatable(false);
        depictionToolbar.setRollover(true);
        depictionToolbar.setName("depictionToolbar"); // NOI18N

        refreshDepictionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/view-refresh.png"))); // NOI18N
        refreshDepictionButton.setEnabled(false);
        refreshDepictionButton.setFocusable(false);
        refreshDepictionButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refreshDepictionButton.setName("refreshDepictionButton"); // NOI18N
        refreshDepictionButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        refreshDepictionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshDepictionButtonActionPerformed(evt);
            }
        });
        depictionToolbar.add(refreshDepictionButton);

        depiction.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        depiction.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/NoImageIcon.jpg"))); // NOI18N
        depiction.setToolTipText("<html><p width=\"300\">Depiction of the compound retrieved from AMBIT</p>");
        depiction.setName("depiction"); // NOI18N

        javax.swing.GroupLayout depictionPanelLayout = new javax.swing.GroupLayout(depictionPanel);
        depictionPanel.setLayout(depictionPanelLayout);
        depictionPanelLayout.setHorizontalGroup(
            depictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(depictionToolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
            .addGroup(depictionPanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(depiction, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                .addContainerGap())
        );
        depictionPanelLayout.setVerticalGroup(
            depictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(depictionPanelLayout.createSequentialGroup()
                .addComponent(depictionToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(depiction, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                .addContainerGap())
        );

        synonymsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Synonyms"));
        synonymsPanel.setName("synonymsPanel"); // NOI18N

        synonymsScollable.setName("synonymsScollable"); // NOI18N

        synonymsList.setName("synonymsList"); // NOI18N
        synonymsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                synonymsListValueChanged(evt);
            }
        });
        synonymsScollable.setViewportView(synonymsList);

        synonymsToolbar.setFloatable(false);
        synonymsToolbar.setRollover(true);
        synonymsToolbar.setName("synonymsToolbar"); // NOI18N

        addSynonymButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/list-add.png"))); // NOI18N
        addSynonymButton.setFocusable(false);
        addSynonymButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addSynonymButton.setName("addSynonymButton"); // NOI18N
        addSynonymButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addSynonymButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSynonymButtonActionPerformed(evt);
            }
        });
        synonymsToolbar.add(addSynonymButton);

        removeSynonymButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/list-remove.png"))); // NOI18N
        removeSynonymButton.setToolTipText("<html><p width=\"300\">Remove the selected synonym(s) from the list</p>");
        removeSynonymButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        removeSynonymButton.setEnabled(false);
        removeSynonymButton.setFocusable(false);
        removeSynonymButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeSynonymButton.setName("removeSynonymButton"); // NOI18N
        removeSynonymButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        removeSynonymButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeSynonymButtonActionPerformed(evt);
            }
        });
        synonymsToolbar.add(removeSynonymButton);

        removeAllSynonymsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/trash-empty.png"))); // NOI18N
        removeAllSynonymsButton.setToolTipText("<html><p width=\"300\">Empty the list of synonyms - delete all</p>");
        removeAllSynonymsButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        removeAllSynonymsButton.setFocusable(false);
        removeAllSynonymsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeAllSynonymsButton.setName("removeAllSynonymsButton"); // NOI18N
        removeAllSynonymsButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        removeAllSynonymsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAllSynonymsButtonActionPerformed(evt);
            }
        });
        synonymsToolbar.add(removeAllSynonymsButton);

        jSeparator1.setName("jSeparator1"); // NOI18N
        synonymsToolbar.add(jSeparator1);

        moveCompoundUpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/go-up-search.png"))); // NOI18N
        moveCompoundUpButton.setToolTipText("<html><p width=\"300\">Move all selected synonyms one position up</p>");
        moveCompoundUpButton.setAutoscrolls(true);
        moveCompoundUpButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        moveCompoundUpButton.setEnabled(false);
        moveCompoundUpButton.setFocusable(false);
        moveCompoundUpButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        moveCompoundUpButton.setName("moveCompoundUpButton"); // NOI18N
        moveCompoundUpButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        moveCompoundUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveCompoundUpButtonActionPerformed(evt);
            }
        });
        synonymsToolbar.add(moveCompoundUpButton);

        moveCompoundDownButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/go-down-search.png"))); // NOI18N
        moveCompoundDownButton.setToolTipText("<html><p width=\"300\">Move all selected synonyms one position down</p>");
        moveCompoundDownButton.setEnabled(false);
        moveCompoundDownButton.setFocusable(false);
        moveCompoundDownButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        moveCompoundDownButton.setName("moveCompoundDownButton"); // NOI18N
        moveCompoundDownButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        moveCompoundDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveCompoundDownButtonActionPerformed(evt);
            }
        });
        synonymsToolbar.add(moveCompoundDownButton);

        javax.swing.GroupLayout synonymsPanelLayout = new javax.swing.GroupLayout(synonymsPanel);
        synonymsPanel.setLayout(synonymsPanelLayout);
        synonymsPanelLayout.setHorizontalGroup(
            synonymsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(synonymsToolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
            .addGroup(synonymsPanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(synonymsScollable, 0, 0, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );
        synonymsPanelLayout.setVerticalGroup(
            synonymsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(synonymsPanelLayout.createSequentialGroup()
                .addComponent(synonymsToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(synonymsScollable, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout compoundTabLayout = new javax.swing.GroupLayout(compoundTab);
        compoundTab.setLayout(compoundTabLayout);
        compoundTabLayout.setHorizontalGroup(
            compoundTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(compoundTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(compoundTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(synonymsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(coumpoundIdentifierLabel)
                    .addComponent(compoundIdentifier, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(depictionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(compoundToolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
        );
        compoundTabLayout.setVerticalGroup(
            compoundTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(compoundTabLayout.createSequentialGroup()
                .addComponent(compoundToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(compoundTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(compoundTabLayout.createSequentialGroup()
                        .addComponent(coumpoundIdentifierLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(compoundIdentifier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(synonymsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(depictionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        basePanel.addTab("Compound", compoundTab);

        generalTab.setName("generalTab"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        authorsList.setModel(new DefaultListModel());
        authorsList.setToolTipText("<html><p width=\"300\">List of authors - click on an author to view details</p>");
        authorsList.setName("authorsList"); // NOI18N
        authorsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                authorsListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(authorsList);

        jLabel1.setText("List of Authors:");
        jLabel1.setToolTipText("<html><p width=\"300\">List of authors of the QPRF report - click on each to view the details on the right</p>");
        jLabel1.setName("jLabel1"); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        addAuthorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/list-add-user.png"))); // NOI18N
        addAuthorButton.setText("Add Author");
        addAuthorButton.setToolTipText("<html><p width=\"300\">Add a new author for the QprfReport</p>");
        addAuthorButton.setFocusable(false);
        addAuthorButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addAuthorButton.setName("addAuthorButton"); // NOI18N
        addAuthorButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addAuthorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAuthorButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(addAuthorButton);

        removeAuthorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/list-remove-user.png"))); // NOI18N
        removeAuthorButton.setText("Remove Author");
        removeAuthorButton.setToolTipText("<html><p width=\"300\">Remove the selected authors from the list</p>");
        removeAuthorButton.setEnabled(false);
        removeAuthorButton.setFocusable(false);
        removeAuthorButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeAuthorButton.setName("removeAuthorButton"); // NOI18N
        removeAuthorButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        removeAuthorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAuthorButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(removeAuthorButton);

        moveUpAuthor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/go-up-search.png"))); // NOI18N
        moveUpAuthor.setText("Move up");
        moveUpAuthor.setToolTipText("<html><p width=\"300\">Move all selected authors one position up</p>");
        moveUpAuthor.setEnabled(false);
        moveUpAuthor.setFocusable(false);
        moveUpAuthor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        moveUpAuthor.setName("moveUpAuthor"); // NOI18N
        moveUpAuthor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        moveUpAuthor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveUpAuthorActionPerformed(evt);
            }
        });
        jToolBar1.add(moveUpAuthor);

        moveDownAuthor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/go-down-search.png"))); // NOI18N
        moveDownAuthor.setText("Move down");
        moveDownAuthor.setToolTipText("<html><p width=\"300\">Move all selected authors one position lower</p>");
        moveDownAuthor.setEnabled(false);
        moveDownAuthor.setFocusable(false);
        moveDownAuthor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        moveDownAuthor.setName("moveDownAuthor"); // NOI18N
        moveDownAuthor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        moveDownAuthor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDownAuthorActionPerformed(evt);
            }
        });
        jToolBar1.add(moveDownAuthor);

        emailLabel.setText("e-mail:");
        emailLabel.setEnabled(false);
        emailLabel.setName("emailLabel"); // NOI18N

        webPageLabel.setText("web page:");
        webPageLabel.setEnabled(false);
        webPageLabel.setName("webPageLabel"); // NOI18N

        affilLabel.setText("Affiliation:");
        affilLabel.setEnabled(false);
        affilLabel.setName("affilLabel"); // NOI18N

        addressLabel.setText("Address:");
        addressLabel.setEnabled(false);
        addressLabel.setName("addressLabel"); // NOI18N

        jLabel11.setText("Date:");
        jLabel11.setName("jLabel11"); // NOI18N

        // Days 1-31:
        String[] days = new String[31];
        for (int dayI = 1; dayI<=31 ; dayI++){
            days[dayI-1] = ""+dayI;
        }
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(days));
        jComboBox1.setName("jComboBox1"); // NOI18N

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }));
        jComboBox2.setName("jComboBox2"); // NOI18N

        // Days 1-31:
        String[] years = new String[31];
        for (int yI = 1; yI<=31 ; yI++){
            years[yI-1] = ""+(yI+1997);
        }
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(years));
        jComboBox3.setName("jComboBox3"); // NOI18N

        emailField.setName("emailField"); // NOI18N

        authWebPageField.setName("authWebPageField"); // NOI18N

        authAffiliationField.setName("authAffiliationField"); // NOI18N

        authAddressField.setName("authAddressField"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(emailLabel)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(webPageLabel)
                            .addComponent(affilLabel)
                            .addComponent(addressLabel))
                        .addGap(58, 58, 58)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(authAddressField, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                            .addComponent(authAffiliationField, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                            .addComponent(emailField, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                            .addComponent(authWebPageField, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(emailLabel)
                    .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(webPageLabel)
                            .addComponent(authWebPageField, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(affilLabel)
                            .addComponent(authAffiliationField, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(addressLabel)
                            .addComponent(authAddressField, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, 0, 0, Short.MAX_VALUE))
                .addContainerGap(83, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Authorship", jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/document-edit.png"))); // NOI18N
        jLabel8.setText("Notes on the QPRF report");
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel9.setText("Title:");
        jLabel9.setName("jLabel9"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setText("QSAR Prediction Reporting Format (QPRF)\n(version 1.1, May 2008)");
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane2.setViewportView(jTextArea1);

        jLabel10.setText("Description:");
        jLabel10.setName("jLabel10"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jTextArea2.setName("jTextArea2"); // NOI18N
        jScrollPane3.setViewportView(jTextArea2);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addGap(29, 29, 29)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane3)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE))))
                .addContainerGap(146, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Report", jPanel2);

        javax.swing.GroupLayout generalTabLayout = new javax.swing.GroupLayout(generalTab);
        generalTab.setLayout(generalTabLayout);
        generalTabLayout.setHorizontalGroup(
            generalTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
        );
        generalTabLayout.setVerticalGroup(
            generalTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
        );

        basePanel.addTab("General", generalTab);

        jPanel3.setName("jPanel3"); // NOI18N

        jTabbedPane2.setName("jTabbedPane2"); // NOI18N

        jPanel7.setName("jPanel7"); // NOI18N

        labelModelUri.setText("OpenTox Model URI:");
        labelModelUri.setName("labelModelUri"); // NOI18N

        modelUriField.setName("modelUriField"); // NOI18N
        modelUriField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                modelUriFieldCaretUpdate(evt);
            }
        });

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setName("jToolBar2"); // NOI18N

        loadModelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/download.png"))); // NOI18N
        loadModelButton.setText("Load");
        loadModelButton.setToolTipText("<html><p width=\"300\">Load the model from the remote location</p>");
        loadModelButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        loadModelButton.setEnabled(false);
        loadModelButton.setFocusable(false);
        loadModelButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        loadModelButton.setName("loadModelButton"); // NOI18N
        loadModelButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        loadModelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadModelButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(loadModelButton);

        modelDetails.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/documentation.png"))); // NOI18N
        modelDetails.setText("Details");
        modelDetails.setToolTipText("<html><p width=\"300\">Inspect details of the downloaded model resource</p>");
        modelDetails.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        modelDetails.setEnabled(false);
        modelDetails.setFocusable(false);
        modelDetails.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        modelDetails.setName("modelDetails"); // NOI18N
        modelDetails.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        modelDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modelDetailsActionPerformed(evt);
            }
        });
        jToolBar2.add(modelDetails);

        algorithmDetails.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/configure.png"))); // NOI18N
        algorithmDetails.setText("Algorithm");
        algorithmDetails.setEnabled(false);
        algorithmDetails.setFocusable(false);
        algorithmDetails.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        algorithmDetails.setName("algorithmDetails"); // NOI18N
        algorithmDetails.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        algorithmDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                algorithmDetailsActionPerformed(evt);
            }
        });
        jToolBar2.add(algorithmDetails);

        predFeatDetails.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/feature.png"))); // NOI18N
        predFeatDetails.setText("Predicted Feature");
        predFeatDetails.setEnabled(false);
        predFeatDetails.setFocusable(false);
        predFeatDetails.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        predFeatDetails.setName("predFeatDetails"); // NOI18N
        predFeatDetails.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        predFeatDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                predFeatDetailsActionPerformed(evt);
            }
        });
        jToolBar2.add(predFeatDetails);

        depFeatDetails.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/feature.png"))); // NOI18N
        depFeatDetails.setText("Dependent Feature");
        depFeatDetails.setEnabled(false);
        depFeatDetails.setFocusable(false);
        depFeatDetails.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        depFeatDetails.setName("depFeatDetails"); // NOI18N
        depFeatDetails.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        depFeatDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                depFeatDetailsActionPerformed(evt);
            }
        });
        jToolBar2.add(depFeatDetails);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Version Information"));
        jPanel9.setName("jPanel9"); // NOI18N

        // Days 1-31:
        String[] yearsM = new String[31];
        for (int yI = 1; yI<=31 ; yI++){
            yearsM[yI-1] = ""+(yI+1997);
        }
        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel(yearsM));
        jComboBox6.setName("jComboBox6"); // NOI18N

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Jan", "Feb", "Mar", "APr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }));
        jComboBox5.setName("jComboBox5"); // NOI18N

        jLabel6.setText("Date:");
        jLabel6.setName("jLabel6"); // NOI18N

        // Days 1-31:
        String[] daysM = new String[31];
        for (int dayI = 1; dayI<=31 ; dayI++){
            daysM[dayI-1] = ""+dayI;
        }
        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(daysM));
        jComboBox4.setName("jComboBox4"); // NOI18N

        jLabel5.setText("Model Version:");
        jLabel5.setName("jLabel5"); // NOI18N

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        jTextArea5.setColumns(20);
        jTextArea5.setRows(5);
        jTextArea5.setName("jTextArea5"); // NOI18N
        jScrollPane6.setViewportView(jTextArea5);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6)
                    .addComponent(jComboBox6, 0, 77, Short.MAX_VALUE)
                    .addComponent(jComboBox5, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                        .addComponent(labelModelUri)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(modelUriField, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(93, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelModelUri)
                    .addComponent(modelUriField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Model Info", jPanel7);

        jPanel8.setName("jPanel8"); // NOI18N

        jLabel3.setText("QMRF report reference:");
        jLabel3.setName("jLabel3"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        jTextArea3.setColumns(20);
        jTextArea3.setRows(5);
        jTextArea3.setName("jTextArea3"); // NOI18N
        jScrollPane4.setViewportView(jTextArea3);

        jLabel4.setText("QMRF report discussion:");
        jLabel4.setName("jLabel4"); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        jTextArea4.setColumns(20);
        jTextArea4.setRows(5);
        jTextArea4.setName("jTextArea4"); // NOI18N
        jScrollPane5.setViewportView(jTextArea4);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(131, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(88, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("QMRF Report", jPanel8);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
        );

        basePanel.addTab("Model", jPanel3);

        jPanel4.setName("jPanel4"); // NOI18N

        jTabbedPane3.setName("jTabbedPane3"); // NOI18N

        jPanel10.setName("jPanel10"); // NOI18N

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);
        jToolBar3.setName("jToolBar3"); // NOI18N

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/pi.png"))); // NOI18N
        jButton6.setText("Predict");
        jButton6.setToolTipText("<html><p width=\"300\">Use OpenTox web services to predict the activity value - might be time consuming</p>");
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setName("jButton6"); // NOI18N
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar3.add(jButton6);

        dwnLoadExpValueButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/download.png"))); // NOI18N
        dwnLoadExpValueButton.setText("Download Exp. Value");
        dwnLoadExpValueButton.setFocusable(false);
        dwnLoadExpValueButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        dwnLoadExpValueButton.setName("dwnLoadExpValueButton"); // NOI18N
        dwnLoadExpValueButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        dwnLoadExpValueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dwnLoadExpValueButtonActionPerformed(evt);
            }
        });
        jToolBar3.add(dwnLoadExpValueButton);

        jLabel16.setText("Experimental value - if available :");
        jLabel16.setName("jLabel16"); // NOI18N

        jLabel15.setText("3.2. Prediction (output of the model) :");
        jLabel15.setName("jLabel15"); // NOI18N

        jTextField1.setToolTipText("value");
        jTextField1.setName("jTextField1"); // NOI18N

        jTextField4.setToolTipText("units");
        jTextField4.setName("jTextField4"); // NOI18N

        jTextField3.setToolTipText("value");
        jTextField3.setName("jTextField3"); // NOI18N

        jTextField2.setToolTipText("units");
        jTextField2.setName("jTextField2"); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField3)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField4, 0, 0, Short.MAX_VALUE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 168, Short.MAX_VALUE))
                    .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane3.addTab("Prediction", jPanel10);

        jPanel11.setName("jPanel11"); // NOI18N

        jLabel2.setText("3.2.e. Comment on the prediction");
        jLabel2.setToolTipText("<html>3.2.e. Comment on the predicted value");
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel2.setName("jLabel2"); // NOI18N

        jScrollPane11.setName("jScrollPane11"); // NOI18N

        jTextArea10.setColumns(20);
        jTextArea10.setRows(5);
        jTextArea10.setName("jTextArea10"); // NOI18N
        jScrollPane11.setViewportView(jTextArea10);

        jLabel17.setText("<html>3.4. Comment on the uncertainty of the prediction :");
        jLabel17.setName("jLabel17"); // NOI18N

        jScrollPane12.setName("jScrollPane12"); // NOI18N

        jTextArea11.setColumns(20);
        jTextArea11.setRows(5);
        jTextArea11.setName("jTextArea11"); // NOI18N
        jScrollPane12.setViewportView(jTextArea11);

        jScrollPane13.setName("jScrollPane13"); // NOI18N

        jTextArea12.setColumns(20);
        jTextArea12.setRows(5);
        jTextArea12.setName("jTextArea12"); // NOI18N
        jScrollPane13.setViewportView(jTextArea12);

        jLabel18.setText("3.5. Chemical/Biological Mechanisms");
        jLabel18.setToolTipText("<html><p width=\"300\">3.5. The chemical and biological mechanisms according to the model underpinning the predicted result (OECD principle 5).</p>");
        jLabel18.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel18.setName("jLabel18"); // NOI18N

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane12)
                    .addComponent(jScrollPane11)
                    .addComponent(jLabel2)
                    .addComponent(jLabel17, 0, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane13)
                    .addComponent(jLabel18))
                .addGap(23, 23, 23))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel11Layout.createSequentialGroup()
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane3.addTab("Discussion", jPanel11);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
        );

        basePanel.addTab("Prediction", jPanel4);

        jPanel5.setName("jPanel5"); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 583, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 335, Short.MAX_VALUE)
        );

        basePanel.addTab("DoA", jPanel5);

        jPanel6.setName("jPanel6"); // NOI18N

        jScrollPane7.setName("jScrollPane7"); // NOI18N

        jTextArea6.setColumns(20);
        jTextArea6.setRows(5);
        jTextArea6.setName("jTextArea6"); // NOI18N
        jScrollPane7.setViewportView(jTextArea6);

        jLabel7.setText("4.1. Regulatory Purpose");
        jLabel7.setName("jLabel7"); // NOI18N

        jScrollPane8.setName("jScrollPane8"); // NOI18N

        jTextArea7.setColumns(20);
        jTextArea7.setRows(5);
        jTextArea7.setName("jTextArea7"); // NOI18N
        jScrollPane8.setViewportView(jTextArea7);

        jLabel12.setText("<html>4.2. Regulatory interpretation of the result");
        jLabel12.setName("jLabel12"); // NOI18N

        jLabel13.setText("4.3. Outcome");
        jLabel13.setName("jLabel13"); // NOI18N

        jScrollPane9.setName("jScrollPane9"); // NOI18N

        jTextArea8.setColumns(20);
        jTextArea8.setRows(5);
        jTextArea8.setName("jTextArea8"); // NOI18N
        jScrollPane9.setViewportView(jTextArea8);

        jScrollPane10.setName("jScrollPane10"); // NOI18N

        jTextArea9.setColumns(20);
        jTextArea9.setRows(5);
        jTextArea9.setName("jTextArea9"); // NOI18N
        jScrollPane10.setViewportView(jTextArea9);

        jLabel14.setText("4.4. Conclusion");
        jLabel14.setName("jLabel14"); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(55, Short.MAX_VALUE))
        );

        basePanel.addTab("Discussion", jPanel6);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(basePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(basePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void compoundIdentifierCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_compoundIdentifierCaretUpdate
        if (!this.compoundIdentifier.getText().trim().isEmpty()) {
            loadCompoundButton.setEnabled(true);
        } else {
            loadCompoundButton.setEnabled(false);
        }

    }//GEN-LAST:event_compoundIdentifierCaretUpdate

    private void loadCompoundButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadCompoundButtonActionPerformed
        CompoundInfo task = new CompoundInfo(this, compoundIdentifier.getText(), basePanel);
        task.runInBackground();

    }//GEN-LAST:event_loadCompoundButtonActionPerformed

    private void refreshDepictionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshDepictionButtonActionPerformed
        updateCompoundFields();
    }//GEN-LAST:event_refreshDepictionButtonActionPerformed

    private void compDetailsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compDetailsButtonActionPerformed
        accessCompoundDetails();
    }//GEN-LAST:event_compDetailsButtonActionPerformed

    private void stereoFeaturesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stereoFeaturesButtonActionPerformed
        stereoClicked();
    }//GEN-LAST:event_stereoFeaturesButtonActionPerformed

    private void addSynonymButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSynonymButtonActionPerformed
        addSynonym();
    }//GEN-LAST:event_addSynonymButtonActionPerformed

    private void removeSynonymButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeSynonymButtonActionPerformed
        removeSelectedRows(synonymsList);
    }//GEN-LAST:event_removeSynonymButtonActionPerformed

    private void synonymsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_synonymsListValueChanged
        Object[] selected = synonymsList.getSelectedValues();
        int sel = selected.length;
        boolean enableButtons = sel > 0;
        removeSynonymButton.setEnabled(enableButtons);
        moveCompoundDownButton.setEnabled(enableButtons);
        moveCompoundUpButton.setEnabled(enableButtons);
    }//GEN-LAST:event_synonymsListValueChanged

    private void removeAllSynonymsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAllSynonymsButtonActionPerformed
        synonymsList.setModel(new DefaultListModel());
    }//GEN-LAST:event_removeAllSynonymsButtonActionPerformed

    private void addAuthorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAuthorButtonActionPerformed
        addAuthor();
}//GEN-LAST:event_addAuthorButtonActionPerformed

    private void authorsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_authorsListValueChanged
        int[] indices = authorsList.getSelectedIndices();
        boolean authorsSelected = (indices.length > 0);
        removeAuthorButton.setEnabled(authorsSelected);
        moveDownAuthor.setEnabled(authorsSelected);
        moveUpAuthor.setEnabled(authorsSelected);
        boolean oneIsSelected = indices.length == 1;
        addressLabel.setEnabled(oneIsSelected);
        webPageLabel.setEnabled(oneIsSelected);
        emailLabel.setEnabled(oneIsSelected);
        affilLabel.setEnabled(oneIsSelected);

        if (oneIsSelected) {//One author selected!            
            String name = authorsList.getSelectedValue().toString();
            QprfAuthor author = AUTHORS_MAP.get(name);
            authAddressField.setText(author.getAddress());
            emailField.setText(author.getEmail());
            authWebPageField.setText(author.getURL());
            authAffiliationField.setText(author.getAffiliation());
        } else {
            authAddressField.setText("");
            emailField.setText("");
            authWebPageField.setText("");
            authAffiliationField.setText("");
        }
    }//GEN-LAST:event_authorsListValueChanged

    private void loadModelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadModelButtonActionPerformed
        loadModel();
    }//GEN-LAST:event_loadModelButtonActionPerformed

    private void modelUriFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_modelUriFieldCaretUpdate
        enableIfCaretUpdate(modelUriField, loadModelButton);
    }//GEN-LAST:event_modelUriFieldCaretUpdate

    private void modelDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modelDetailsActionPerformed
        modelDetails();
    }//GEN-LAST:event_modelDetailsActionPerformed

    private void algorithmDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_algorithmDetailsActionPerformed
        algoDetails();
    }//GEN-LAST:event_algorithmDetailsActionPerformed

    private void removeAuthorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAuthorButtonActionPerformed
        // Remove from the MAP:
        Object[] selectedValues = authorsList.getSelectedValues();
        for (Object o : selectedValues) {
            AUTHORS_MAP.remove(o.toString());
        }
        // Remove from the table:
        removeSelectedRows(authorsList);
    }//GEN-LAST:event_removeAuthorButtonActionPerformed

    private void moveUpSelected(JList list) {
        int[] selectedRows = list.getSelectedIndices();
        DefaultListModel listModel = (DefaultListModel) list.getModel();
        for (int rowSelected : selectedRows) {
            if (rowSelected > 0) {
                Object o = listModel.get(rowSelected);
                listModel.remove(rowSelected);
                listModel.add(rowSelected - 1, o);
            }
        }
        for (int i = 0; i < selectedRows.length; i++) {
            if (selectedRows[i] > 0) {
                selectedRows[i]--;
            }
        }
        list.setSelectedIndices(selectedRows);
    }

    private void moveDownSelected(JList list) {
        int[] selectedRows = list.getSelectedIndices();
        DefaultListModel listModel = (DefaultListModel) list.getModel();
        int Nelements = listModel.size();
        for (int rowSelected : selectedRows) {
            if (rowSelected < Nelements - 1) {
                Object o = listModel.get(rowSelected);
                listModel.remove(rowSelected);
                listModel.add(rowSelected + 1, o);
            }
        }
        for (int i = 0; i < selectedRows.length; i++) {
            if (selectedRows[i] < Nelements - 1) {
                selectedRows[i]++;
            }
        }
        list.setSelectedIndices(selectedRows);
    }

    private void moveUpAuthorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveUpAuthorActionPerformed
        moveUpSelected(authorsList);
    }//GEN-LAST:event_moveUpAuthorActionPerformed

    private void moveDownAuthorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDownAuthorActionPerformed
        moveDownSelected(authorsList);
    }//GEN-LAST:event_moveDownAuthorActionPerformed

    private void moveCompoundUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveCompoundUpButtonActionPerformed
        moveUpSelected(synonymsList);
    }//GEN-LAST:event_moveCompoundUpButtonActionPerformed

    private void moveCompoundDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveCompoundDownButtonActionPerformed
        moveDownSelected(synonymsList);
    }//GEN-LAST:event_moveCompoundDownButtonActionPerformed

    private void predFeatDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_predFeatDetailsActionPerformed
        predFeatureDetails();
    }//GEN-LAST:event_predFeatDetailsActionPerformed

    private void depFeatDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_depFeatDetailsActionPerformed
        depFeatureDetails();
    }//GEN-LAST:event_depFeatDetailsActionPerformed

    private void dwnLoadExpValueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dwnLoadExpValueButtonActionPerformed
        try {
            Feature depFeat = getReport().getModel().getDependentFeatures().get(0);
            Compound cmp = getReport().getCompound();
            LiteralValue lv = cmp.getProperty(depFeat, QEditApp.getAuthentication());
            System.out.println(lv);
        } catch (ServiceInvocationException ex){
            
        }
    }//GEN-LAST:event_dwnLoadExpValueButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAuthorButton;
    private javax.swing.JButton addSynonymButton;
    private javax.swing.JLabel addressLabel;
    private javax.swing.JLabel affilLabel;
    private javax.swing.JButton algorithmDetails;
    private javax.swing.JLabel authAddressField;
    private javax.swing.JLabel authAffiliationField;
    private javax.swing.JLabel authWebPageField;
    private javax.swing.JList authorsList;
    private javax.swing.JTabbedPane basePanel;
    private javax.swing.JButton compDetailsButton;
    private javax.swing.JTextField compoundIdentifier;
    private javax.swing.JPanel compoundTab;
    private javax.swing.JToolBar compoundToolbar;
    private javax.swing.JLabel coumpoundIdentifierLabel;
    private javax.swing.JButton depFeatDetails;
    private javax.swing.JLabel depiction;
    private javax.swing.JPanel depictionPanel;
    private javax.swing.JToolBar depictionToolbar;
    private javax.swing.JButton dwnLoadExpValueButton;
    private javax.swing.JLabel emailField;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JPanel generalTab;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JComboBox jComboBox5;
    private javax.swing.JComboBox jComboBox6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea10;
    private javax.swing.JTextArea jTextArea11;
    private javax.swing.JTextArea jTextArea12;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JTextArea jTextArea5;
    private javax.swing.JTextArea jTextArea6;
    private javax.swing.JTextArea jTextArea7;
    private javax.swing.JTextArea jTextArea8;
    private javax.swing.JTextArea jTextArea9;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JLabel labelModelUri;
    private javax.swing.JButton loadCompoundButton;
    private javax.swing.JButton loadModelButton;
    private javax.swing.JButton modelDetails;
    private javax.swing.JTextField modelUriField;
    private javax.swing.JButton moveCompoundDownButton;
    private javax.swing.JButton moveCompoundUpButton;
    private javax.swing.JButton moveDownAuthor;
    private javax.swing.JButton moveUpAuthor;
    private javax.swing.JButton predFeatDetails;
    private javax.swing.JButton refreshDepictionButton;
    private javax.swing.JButton removeAllSynonymsButton;
    private javax.swing.JButton removeAuthorButton;
    private javax.swing.JButton removeSynonymButton;
    private javax.swing.JButton stereoFeaturesButton;
    private javax.swing.JList synonymsList;
    private javax.swing.JPanel synonymsPanel;
    private javax.swing.JScrollPane synonymsScollable;
    private javax.swing.JToolBar synonymsToolbar;
    private javax.swing.JLabel webPageLabel;
    // End of variables declaration//GEN-END:variables
    /* User-defined variables */
    private QprfReport report = new QprfReport();
    private CompoundDetails compoundDetails;
    private Map<String, QprfAuthor> AUTHORS_MAP = new HashMap<String, QprfAuthor>();
}
