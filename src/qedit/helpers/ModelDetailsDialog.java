/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ModelDetailsDialog.java
 *
 * Created on Aug 2, 2011, 4:10:08 PM
 */
package qedit.helpers;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.table.DefaultTableModel;
import org.opentox.toxotis.core.component.Feature;
import org.opentox.toxotis.core.component.Model;
import org.opentox.toxotis.core.component.Parameter;
import qedit.ReportIF;

/**
 *
 * @author chung
 */
public final class ModelDetailsDialog extends javax.swing.JDialog {

    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;
    private ReportIF intFrame;

    public ModelDetailsDialog(final ReportIF intFrame, java.awt.Frame parent, boolean modal) {
        this(parent, modal);
        this.intFrame = intFrame;
        Model model = intFrame.getReport().getModel();
        if (model != null) {
            /*
             * Model URI
             */
            if (model.getUri() != null) {
                modelUriField.setText(model.getUri().toString());
            } else {
                modelUriField.setText("N/A");
            }
            /*
             * Algorithm
             */
            if (model.getAlgorithm() != null && model.getAlgorithm().getUri() != null) {
                trainAlgoField.setText(model.getAlgorithm().getUri().toString());
            } else {
                trainAlgoField.setText("N/A");
            }
            /*
             * Dataset
             */
            if (model.getDataset() != null) {
                trainingDatasetUriField.setText(model.getDataset().toString());
            } else {
                trainingDatasetUriField.setText("N/A");
            }
            /*
             * Predicted Features
             */
            if (model.getPredictedFeatures() != null && !model.getPredictedFeatures().isEmpty()) {
                predFeatField.setText(model.getPredictedFeatures().get(0).getUri().toString());
            }
            /*
             * Dependent Feautures
             */
            if (model.getDependentFeatures() != null && !model.getDependentFeatures().isEmpty()) {
                depFeatField.setText(model.getDependentFeatures().get(0).getUri().toString());
            }
            /*
             * Model Title
             */
            String modelTile = "N/A";
            if (model.getMeta() != null && model.getMeta().getTitles() != null && !model.getMeta().getTitles().isEmpty()) {
                modelTile = model.getMeta().getTitles().iterator().next().getValueAsString();
            }
            modelTitleField.setText(modelTile);
            /*
             * Model Independent Features
             */
            List<Feature> independentFeatures = model.getIndependentFeatures();
            if (independentFeatures != null && !independentFeatures.isEmpty()) {
                ListModel indListModel = indepFeatList.getModel();
                if (indListModel == null
                        || (indListModel != null && !DefaultListModel.class.isAssignableFrom(indListModel.getClass()))) {
                    indepFeatList.setModel(new DefaultListModel());
                    indListModel = indepFeatList.getModel();
                }
                DefaultListModel defaultListModel = (DefaultListModel) indListModel;
                for (Feature indFeature : model.getIndependentFeatures()) {
                    defaultListModel.addElement(indFeature.getUri().toString());
                }
            }
            /*
             * Model Parameters
             */
            Set<Parameter> modelParameters = model.getParameters();
            if (modelParameters != null && !modelParameters.isEmpty()) {
                DefaultTableModel tableModel = (DefaultTableModel) modelParamTable.getModel();                
                for (Parameter p : modelParameters) {
                    tableModel.addRow(new String[]{
                        p.getName().getValueAsString(), 
                        p.getValue().toString(), 
                        p.getScope().toString(), 
                        p.getTypedValue().getType()!=null?p.getTypedValue().getType().getURI().split("#")[1] :""});
                }
            }
        } else {
            System.out.println("!!!! Model is Null !!!");
        }
    }

    /** Creates new form ModelDetailsDialog */
    private ModelDetailsDialog(java.awt.Frame parent, boolean modal) {
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
        generalPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        modelTitleField = new javax.swing.JTextField();
        trainAlgoField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        depFeatField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        predFeatField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        modelUriField = new javax.swing.JTextField();
        trainingDatasetUriField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        indepFeatList = new javax.swing.JList();
        modParamsPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        modelParamTable = new javax.swing.JTable();

        setTitle("OpenTox Model Information");
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

        generalPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("General"));
        generalPanel.setName("generalPanel"); // NOI18N

        jLabel5.setText("Dependent Feature (Link) :");
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel3.setText("Training Dataset (Link) :");
        jLabel3.setName("jLabel3"); // NOI18N

        modelTitleField.setName("modelTitleField"); // NOI18N

        trainAlgoField.setName("trainAlgoField"); // NOI18N

        jLabel6.setText("Independent Features :");
        jLabel6.setName("jLabel6"); // NOI18N

        depFeatField.setName("depFeatField"); // NOI18N

        jLabel2.setText("Model Link :");
        jLabel2.setName("jLabel2"); // NOI18N

        predFeatField.setName("predFeatField"); // NOI18N

        jLabel4.setText("Prediction Feature (Link) :");
        jLabel4.setName("jLabel4"); // NOI18N

        modelUriField.setName("modelUriField"); // NOI18N

        trainingDatasetUriField.setName("trainingDatasetUriField"); // NOI18N

        jLabel1.setText("Model Title :");
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel7.setText("Training Algorithm :");
        jLabel7.setName("jLabel7"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        indepFeatList.setName("indepFeatList"); // NOI18N
        jScrollPane1.setViewportView(indepFeatList);

        javax.swing.GroupLayout generalPanelLayout = new javax.swing.GroupLayout(generalPanel);
        generalPanel.setLayout(generalPanelLayout);
        generalPanelLayout.setHorizontalGroup(
            generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalPanelLayout.createSequentialGroup()
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(generalPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)))
                    .addGroup(generalPanelLayout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(jLabel7)))
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, generalPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(modelTitleField, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                            .addComponent(modelUriField, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                            .addComponent(trainingDatasetUriField, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                            .addComponent(predFeatField, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                            .addComponent(depFeatField, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, generalPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(trainAlgoField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))))
                .addContainerGap())
        );
        generalPanelLayout.setVerticalGroup(
            generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalPanelLayout.createSequentialGroup()
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(modelTitleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(modelUriField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(7, 7, 7)
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trainingDatasetUriField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(predFeatField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(depFeatField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(trainAlgoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("General", generalPanel);

        modParamsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Model Parameters"));
        modParamsPanel.setName("modParamsPanel"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        modelParamTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Param Name", "Value", "Scope", "Comment"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        modelParamTable.setName("modelParamTable"); // NOI18N
        jScrollPane2.setViewportView(modelParamTable);

        javax.swing.GroupLayout modParamsPanelLayout = new javax.swing.GroupLayout(modParamsPanel);
        modParamsPanel.setLayout(modParamsPanelLayout);
        modParamsPanelLayout.setHorizontalGroup(
            modParamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
        );
        modParamsPanelLayout.setVerticalGroup(
            modParamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(modParamsPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Model Parameters", modParamsPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(371, 371, 371)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addContainerGap())
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                ModelDetailsDialog dialog = new ModelDetailsDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField depFeatField;
    private javax.swing.JPanel generalPanel;
    private javax.swing.JList indepFeatList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel modParamsPanel;
    private javax.swing.JTable modelParamTable;
    private javax.swing.JTextField modelTitleField;
    private javax.swing.JTextField modelUriField;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField predFeatField;
    private javax.swing.JTextField trainAlgoField;
    private javax.swing.JTextField trainingDatasetUriField;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;
}
