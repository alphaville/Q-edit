/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AlgorithmDialog.java
 *
 * Created on Aug 2, 2011, 5:41:56 PM
 */
package qedit.helpers;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import org.opentox.toxotis.core.component.Algorithm;
import org.opentox.toxotis.core.component.Parameter;
import org.opentox.toxotis.ontology.LiteralValue;
import org.opentox.toxotis.ontology.MetaInfo;
import org.opentox.toxotis.ontology.OntologicalClass;
import qedit.ReportIF;

/**
 *
 * @author chung
 */
public class AlgorithmDialog extends javax.swing.JDialog {

    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;
    private ReportIF intFrame;

    /** Creates new form AlgorithmDialog */
    public AlgorithmDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // Close the dialog when Esc is pressed
        String cancelName = "cancel";
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelName);
        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put(cancelName, new AbstractAction() {
            
            public void actionPerformed(ActionEvent e) {
                doClose(RET_CANCEL);
            }
        });
    }
    
    public AlgorithmDialog(ReportIF intFrame, JFrame jframe, boolean modal) {
        this(jframe, modal);
        this.intFrame = intFrame;
        Algorithm algo = intFrame.getReport().getModel().getAlgorithm();
        MetaInfo meta = algo.getMeta();
        uriField.setText(algo.getUri().toString());
        StringBuilder titles = new StringBuilder();
        Iterator<LiteralValue> titlesIterator = meta.getTitles().iterator();
        int i = 0;
        while (titlesIterator.hasNext()) {
            if (i > 0) {
                titles.append(", ");
            }
            titles.append(titlesIterator.next().getValueAsString());
            i++;
        }
        titleField.setText(titles.toString());
        String description = "";
        if (meta.getDescriptions() != null && !meta.getDescriptions().isEmpty()) {
            description = meta.getDescriptions().iterator().next().getValueAsString();
        }
        algorithmDescriptionText.setText(description);
        /*
         * algo types
         */
        Set<OntologicalClass> algOntologies = algo.getOntologies();
        if (algOntologies != null && !algOntologies.isEmpty()) {
            DefaultListModel algoTypeListModel = (DefaultListModel) algoTypesList.getModel();
            for (OntologicalClass oc : algOntologies) {
                algoTypeListModel.addElement(oc.getName());
            }
        }
        /*
         * Subjects
         */
        HashSet<LiteralValue> subjects = meta.getSubjects();
        if (subjects != null && !subjects.isEmpty()) {
            DefaultListModel subjectsListModel = (DefaultListModel) subjectsList.getModel();
            for (LiteralValue subj : subjects) {
                subjectsListModel.addElement(subj.getValueAsString());
            }
        }

        /*
         * Contributors
         */
        HashSet<LiteralValue> contributors = meta.getContributors();
        if (contributors != null && !contributors.isEmpty()) {
            DefaultListModel contributorsListModel = (DefaultListModel) contributorsList.getModel();
            for (LiteralValue contr : contributors) {
                contributorsListModel.addElement(contr.getValueAsString());
            }
        }
        /*
         * date
         */
        LiteralValue date = meta.getDate();
        if (date != null) {
            dateField.setText(date.getValueAsString());
        }
        /*
         * Publisher
         */
        HashSet<LiteralValue> publishers = meta.getPublishers();
        if (publishers != null && !publishers.isEmpty()) {
            publisherField.setText(publishers.iterator().next().getValueAsString());
        }
        /*
         * Copyright
         */
        if (meta.getRights() != null && !meta.getRights().isEmpty()) {
            copyrightField.setText(meta.getRights().iterator().next().getValueAsString());
        }
        /*
         * Algorithm parameters
         */
        Set<Parameter> algoParameters = algo.getParameters();
        if (algoParameters != null && !algoParameters.isEmpty()) {
            PARAMS_MAP = new HashMap<String, Parameter>();
            DefaultListModel prmListModel = (DefaultListModel) paramsList.getModel();
            for (Parameter prm : algoParameters) {
                prmListModel.addElement(prm.getName().getValueAsString());
                PARAMS_MAP.put(prm.getName().getValueAsString(), prm);
            }
        }
        
    }

    /** @return the return status of this dialog - one of RET_OK or RET_CANCEL */
    public int getReturnStatus() {
        return returnStatus;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        titleField = new javax.swing.JTextField();
        uriField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        algorithmDescriptionText = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        algoTypesList = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        subjectsList = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        contributorsList = new javax.swing.JList();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        dateField = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        copyrightField = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        publisherField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        paramsList = new javax.swing.JList();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        paramTitleValue = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        prmScopeValue = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        prmDefaultValue = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        prmDescriptionValue = new javax.swing.JLabel();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("General Information"));
        jPanel1.setName("jPanel1"); // NOI18N

        jLabel1.setText("Algorithm Title(s) :");
        jLabel1.setName("jLabel1"); // NOI18N

        titleField.setName("titleField"); // NOI18N

        uriField.setName("uriField"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        algorithmDescriptionText.setColumns(20);
        algorithmDescriptionText.setLineWrap(true);
        algorithmDescriptionText.setRows(5);
        algorithmDescriptionText.setWrapStyleWord(true);
        algorithmDescriptionText.setName("algorithmDescriptionText"); // NOI18N
        jScrollPane1.setViewportView(algorithmDescriptionText);

        jLabel3.setText("Algorithm URI :");
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel2.setText("Description :");
        jLabel2.setName("jLabel2"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        algoTypesList.setModel(new DefaultListModel());
        algoTypesList.setName("algoTypesList"); // NOI18N
        jScrollPane2.setViewportView(algoTypesList);

        jLabel4.setText("Algorithm Types :");
        jLabel4.setName("jLabel4"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                    .addComponent(uriField, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                    .addComponent(titleField, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE))
                .addGap(60, 60, 60))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uriField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(71, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("General", jPanel1);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Meta-information about the training algorithm"));
        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        subjectsList.setModel(new DefaultListModel());
        subjectsList.setName("subjectsList"); // NOI18N
        jScrollPane3.setViewportView(subjectsList);

        jLabel5.setText("Subjects :");
        jLabel5.setName("jLabel5"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        contributorsList.setModel(new DefaultListModel());
        contributorsList.setName("contributorsList"); // NOI18N
        jScrollPane4.setViewportView(contributorsList);

        jLabel6.setText("Contributors :");
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setText("Date (Last update) :");
        jLabel7.setName("jLabel7"); // NOI18N

        dateField.setName("dateField"); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        copyrightField.setColumns(20);
        copyrightField.setLineWrap(true);
        copyrightField.setRows(5);
        copyrightField.setName("copyrightField"); // NOI18N
        jScrollPane5.setViewportView(copyrightField);

        jLabel8.setText("Copyright Note :");
        jLabel8.setName("jLabel8"); // NOI18N

        publisherField.setName("publisherField"); // NOI18N

        jLabel9.setText("Publisher :");
        jLabel9.setName("jLabel9"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(publisherField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                    .addComponent(dateField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE))
                .addGap(67, 67, 67))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(publisherField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Meta Info", jPanel2);

        jPanel3.setName("jPanel3"); // NOI18N

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        paramsList.setModel(new DefaultListModel());
        paramsList.setName("paramsList"); // NOI18N
        paramsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                paramsListValueChanged(evt);
            }
        });
        jScrollPane6.setViewportView(paramsList);

        jLabel10.setText("<html>List of parameters (click on item to see details) :");
        jLabel10.setName("jLabel10"); // NOI18N

        jLabel11.setText("Title :");
        jLabel11.setEnabled(false);
        jLabel11.setName("jLabel11"); // NOI18N

        paramTitleValue.setName("paramTitleValue"); // NOI18N

        jLabel12.setText("Scope :");
        jLabel12.setEnabled(false);
        jLabel12.setName("jLabel12"); // NOI18N

        prmScopeValue.setName("prmScopeValue"); // NOI18N

        jLabel14.setText("Default Value :");
        jLabel14.setEnabled(false);
        jLabel14.setName("jLabel14"); // NOI18N

        prmDefaultValue.setName("prmDefaultValue"); // NOI18N

        jLabel16.setText("Description :");
        jLabel16.setEnabled(false);
        jLabel16.setName("jLabel16"); // NOI18N

        prmDescriptionValue.setName("prmDescriptionValue"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(318, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(jLabel14)
                            .addComponent(jLabel16))
                        .addGap(31, 31, 31)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(paramTitleValue, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                                    .addComponent(prmDefaultValue, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                                    .addComponent(prmScopeValue, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE))
                                .addGap(149, 149, 149))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(prmDescriptionValue, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(paramTitleValue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(prmScopeValue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(prmDefaultValue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jLabel16)
                    .addComponent(prmDescriptionValue, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43))
        );

        jTabbedPane1.addTab("Parameters", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(388, Short.MAX_VALUE)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addContainerGap())
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        getRootPane().setDefaultButton(okButton);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void paramsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_paramsListValueChanged
        if (PARAMS_MAP==null){
            return;
        }
        int[] indices = paramsList.getSelectedIndices();
        boolean oneIsSelected = indices.length == 1;
        jLabel11.setEnabled(oneIsSelected);
        jLabel12.setEnabled(oneIsSelected);
        jLabel14.setEnabled(oneIsSelected);
        jLabel16.setEnabled(oneIsSelected);
        if (oneIsSelected){
            Parameter prm = PARAMS_MAP.get(paramsList.getSelectedValue().toString());
            paramTitleValue.setText(prm.getName().getValueAsString());
            prmScopeValue.setText(prm.getScope().toString());
            prmDefaultValue.setText(prm.getTypedValue().getValueAsString());
            prmDescriptionValue.setText("<html>"+prm.getMeta().getDescriptions().iterator().next().getValueAsString());
        }else{
            paramTitleValue.setText("");
            prmScopeValue.setText("");
            prmDescriptionValue.setText("");
            prmDefaultValue.setText("");
        }
    }//GEN-LAST:event_paramsListValueChanged
    
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            
            public void run() {
                AlgorithmDialog dialog = new AlgorithmDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList algoTypesList;
    private javax.swing.JTextArea algorithmDescriptionText;
    private javax.swing.JButton cancelButton;
    private javax.swing.JList contributorsList;
    private javax.swing.JTextArea copyrightField;
    private javax.swing.JTextField dateField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel paramTitleValue;
    private javax.swing.JList paramsList;
    private javax.swing.JLabel prmDefaultValue;
    private javax.swing.JLabel prmDescriptionValue;
    private javax.swing.JLabel prmScopeValue;
    private javax.swing.JTextField publisherField;
    private javax.swing.JList subjectsList;
    private javax.swing.JTextField titleField;
    private javax.swing.JTextField uriField;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;
    private Map<String, Parameter> PARAMS_MAP = null;
}
