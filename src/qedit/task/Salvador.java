/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qedit.task;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import javax.swing.JFileChooser;
import org.opentox.toxotis.core.component.qprf.QprfReport;
import qedit.QEditApp;
import qedit.ReportIF;

/**
 *
 * @author chung
 */
public class Salvador extends AbstractTask {
    
    private static final String reportExtension = ".ro";

    private final JFileChooser fileChooser;
    private final ReportIF intFrame;

    public Salvador(JFileChooser fileChooser, ReportIF intFrame) {
        super();
        this.fileChooser = fileChooser;
        this.intFrame = intFrame;
        taskName = "Saving QPRF report";
    }

    @Override
    protected Object doInBackground() throws Exception {
        java.io.File selectedFile = intFrame.getRelatedFile()!=null ? 
                intFrame.getRelatedFile(): fileChooser.getSelectedFile();        
        if (selectedFile == null) {
            exceptionMessage = "No report is saved";
            cancel(true);
        }
        String filePath = selectedFile.getAbsolutePath();
        if (!filePath.contains(reportExtension)) {
            filePath += reportExtension;
        }
        java.io.File f = new java.io.File(filePath);
        intFrame.updateReportObject();
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
        oos.writeObject((QprfReport) intFrame.getReport());
        intFrame.setTitle(selectedFile.getName().replaceAll(reportExtension, ""));        
        return 0;
    }
}
