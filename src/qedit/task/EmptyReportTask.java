package qedit.task;

import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;
import qedit.QEditApp;
import qedit.QEditView;
import qedit.ReportIF;

/**
 *
 * @author Pantelis Sopasakis
 * @author Charalampos Chomenides
 */
public class EmptyReportTask extends AbstractTask {

    private javax.swing.JDesktopPane desktopPane;

    public EmptyReportTask() {        
        super();
        super.taskName="Creating New Report";
    }    

    public javax.swing.JDesktopPane getDesktopPane() {
        return desktopPane;
    }

    public void setDesktopPane(javax.swing.JDesktopPane desktopPane) {
        this.desktopPane = desktopPane;
    }

    @Override
    protected Object doInBackground() throws Exception {
        desktopPane.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        QEditApp.getView().getStatusLabel().setText("Loading new Report... Please Wait!");
        ReportIF nd = new ReportIF();
        nd.setVisible(true);
        QEditApp.getView().getDesktopPane().add(nd);
        nd.revalidate();
        nd.setLocation(new java.awt.Point(40 + 10 * QEditView.getNumOpenDocuments(), 40 + 10 * QEditView.getNumOpenDocuments()));
        nd.setTitle("Document " + (QEditView.getNumOpenDocuments() + 1));
        nd.setName(nd.getTitle());
        QEditView.increaseNumOpenDocuments();
        try {
            nd.setSelected(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(QEditView.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            desktopPane.setCursor(java.awt.Cursor.getDefaultCursor());
        }
        setProgress(90);
        QEditApp.getView().getStatusLabel().setText("A new Report has been created");
        setProgress(100);
        return new Object();
    }
}
