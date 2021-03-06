/*
 * CompoundDetails.java
 *
 * Created on Aug 1, 2011, 11:57:12 AM
 */
package qedit.helpers;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.opentox.toxotis.client.VRI;
import org.opentox.toxotis.core.component.Compound;
import org.opentox.toxotis.core.component.Conformer;
import org.opentox.toxotis.exceptions.impl.ServiceInvocationException;
import qedit.ReportIF;

/**
 *
 * @author chung
 */
public final class CompoundDetails extends javax.swing.JDialog {

    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;
    private Compound compound;

    public void setCompound(Compound compound) {
        this.compound = compound;
    }

    public CompoundDetails(Compound compound, java.awt.Frame parent, boolean modal) {
        this(parent, modal);
        setCompound(compound);
        uriField.setText(compound.getUri() != null ? compound.getUri().toString() : "N/A");
        casField.setText(compound.getCasrn() != null ? compound.getCasrn() : "N/A");
        einecsField.setText(compound.getEinecs() != null ? compound.getEinecs() : "N/A");
        inchiField.setText(compound.getInchi() != null ? compound.getInchi() : "N/A");
        inchiKeyField.setText(compound.getInchiKey() != null ? compound.getInchiKey() : "N/A");
        iupacField.setText(compound.getIupacName() != null ? compound.getIupacName() : "N/A");
        smilesField.setText(compound.getSmiles() != null ? compound.getSmiles() : "N/A");
        registrDateField.setText(compound.getRegistrationDate() != null ? compound.getRegistrationDate() : "N/A");
        conformersList.setModel(new DefaultListModel());


        for (Conformer conformer : compound.getConformers()) {
            System.out.println("   L " + conformer.getUri());
            ((DefaultListModel) conformersList.getModel()).addElement(conformer.getUri().toString());
        }




    }

    /** Creates new form CompoundDetails */
    public CompoundDetails(java.awt.Frame parent, boolean modal) {
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        conformersList = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        casField = new javax.swing.JTextField();
        einecsField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        smilesField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        inchiField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        inchiKeyField = new javax.swing.JTextField();
        registrDateField = new javax.swing.JTextField();
        iupacField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        uriField = new javax.swing.JTextField();
        enableEditingChkBox = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();

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

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/qedit/resources/office-chart-polar.png"))); // NOI18N
        jLabel1.setText("Compound Info.");
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText("List of available conformers (online)");
        jLabel2.setName("jLabel2"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        conformersList.setToolTipText("Available Conformers");
        conformersList.setName("conformersList"); // NOI18N
        jScrollPane1.setViewportView(conformersList);

        jLabel3.setText("CAS Registration Number:");
        jLabel3.setName("jLabel3"); // NOI18N

        casField.setEditable(false);
        casField.setName("casField"); // NOI18N

        einecsField.setEditable(false);
        einecsField.setName("einecsField"); // NOI18N

        jLabel4.setText("EINECS:");
        jLabel4.setName("jLabel4"); // NOI18N

        smilesField.setEditable(false);
        smilesField.setName("smilesField"); // NOI18N

        jLabel5.setText("SMILES String:");
        jLabel5.setName("jLabel5"); // NOI18N

        inchiField.setEditable(false);
        inchiField.setName("inchiField"); // NOI18N

        jLabel6.setText("InChI:");
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setText("InChI Key:");
        jLabel7.setName("jLabel7"); // NOI18N

        inchiKeyField.setEditable(false);
        inchiKeyField.setName("inchiKeyField"); // NOI18N

        registrDateField.setEditable(false);
        registrDateField.setName("registrDateField"); // NOI18N

        iupacField.setEditable(false);
        iupacField.setName("iupacField"); // NOI18N

        jLabel9.setText("IUPAC Name:");
        jLabel9.setName("jLabel9"); // NOI18N

        jLabel10.setText("URI:");
        jLabel10.setName("jLabel10"); // NOI18N

        uriField.setEditable(false);
        uriField.setForeground(java.awt.Color.blue);
        uriField.setToolTipText("URI of the current compound");
        uriField.setName("uriField"); // NOI18N

        enableEditingChkBox.setText("Make all fields editable");
        enableEditingChkBox.setName("enableEditingChkBox"); // NOI18N
        enableEditingChkBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enableEditingChkBoxActionPerformed(evt);
            }
        });

        jLabel8.setText("REACH Reg. Date:");
        jLabel8.setName("jLabel8"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(276, 276, 276)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel8))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(inchiKeyField, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                                    .addComponent(inchiField, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                                    .addComponent(smilesField, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                                    .addComponent(einecsField, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                                    .addComponent(registrDateField, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                                    .addComponent(iupacField, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                                    .addComponent(casField, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)))
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(18, 18, 18)
                                .addComponent(uriField, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(enableEditingChkBox))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uriField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(casField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(einecsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(smilesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inchiField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inchiKeyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(registrDateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iupacField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(enableEditingChkBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
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

    private void enableEditingChkBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enableEditingChkBoxActionPerformed
        // TODO add your handling code here:
        boolean enableEditing = enableEditingChkBox.isSelected();
        inchiField.setEditable(enableEditing);
        inchiKeyField.setEditable(enableEditing);
        iupacField.setEditable(enableEditing);
        registrDateField.setEditable(enableEditing);
        smilesField.setEditable(enableEditing);
        casField.setEditable(enableEditing);
        einecsField.setEditable(enableEditing);
        uriField.setEditable(enableEditing);
    }//GEN-LAST:event_enableEditingChkBoxActionPerformed

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
                CompoundDetails dialog = new CompoundDetails(new javax.swing.JFrame(), true);
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
    private javax.swing.JTextField casField;
    private javax.swing.JList conformersList;
    private javax.swing.JTextField einecsField;
    private javax.swing.JCheckBox enableEditingChkBox;
    private javax.swing.JTextField inchiField;
    private javax.swing.JTextField inchiKeyField;
    private javax.swing.JTextField iupacField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField registrDateField;
    private javax.swing.JTextField smilesField;
    private javax.swing.JTextField uriField;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = RET_CANCEL;
}
