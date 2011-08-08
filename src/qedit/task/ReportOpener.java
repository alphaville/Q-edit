/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qedit.task;

import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import javax.swing.JFileChooser;
import org.opentox.toxotis.core.component.qprf.QprfReport;
import qedit.QEditView;

/**
 *
 * @author chung
 */
public class ReportOpener extends AbstractTask {

    private final java.io.File file;

    public ReportOpener(java.io.File file) {
        this.file = file;
        taskName = "Opening QPRF report from file";
    }

    @Override
    protected Object doInBackground() throws Exception {
        InputStream is = null;
        ObjectInputStream ois = null;
        is = new FileInputStream(file);
        ois = new ObjectInputStream(is);
        final QprfReport report = (QprfReport) ois.readObject();

        qedit.ReportIF newReportFrame = new qedit.ReportIF();
        newReportFrame.setReport(report);
        newReportFrame.synchronizeFieldsWRTReport();
        newReportFrame.setVisible(true);
        qedit.QEditApp.getView().getDesktopPane().add(newReportFrame);
        newReportFrame.revalidate();
        newReportFrame.setLocation(new java.awt.Point(40 + 10 * QEditView.getNumOpenDocuments(), 40 + 10 * QEditView.getNumOpenDocuments()));
        newReportFrame.setTitle(file.getName().replaceAll(".ro", ""));
        QEditView.increaseNumOpenDocuments();
        newReportFrame.setSelected(true);
        newReportFrame.setRelatedFile(file);
        /*
         * Important:
         * If one attempts to set the depiction within the method
         * 'synchronizeFieldsWRTReport()' then the dimensions of the label
         * where the icon is places are zero (because the view has not been 
         * created yet). So it should be placed here after the invocation
         * of setVisible() and revalidate().
         */
        newReportFrame.displayCompoundDepiction();
        return 0;
    }
}
