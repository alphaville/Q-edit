/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qedit.task;

import javax.swing.JFileChooser;
import qedit.QEditApp;
import qedit.ReportIF;
import qedit.export.PDFReporter;

/**
 *
 * @author chung
 */
public class PDFExportTask extends AbstractTask{

    
    private final JFileChooser fileChooser;
    private final ReportIF intFrame;

    public PDFExportTask(JFileChooser fileChooser, ReportIF intFrame) {
        super();
        this.fileChooser = fileChooser;
        this.intFrame = intFrame;
        taskName = "Exporting QPRF report as PDF";
    }
    
    @Override
    protected Object doInBackground() throws Exception {
        java.io.File selectedFile = fileChooser.getSelectedFile();

        if (selectedFile == null) {
            cancel(true);
        }

        String filePath = selectedFile.getAbsolutePath();
        if (!filePath.contains(".pdf")) {
            filePath += ".pdf";
        }
        java.io.File f = new java.io.File(filePath);
        intFrame.updateReportObject();
        PDFReporter reporter = new PDFReporter(intFrame.getReport());
        java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
        reporter.createPdf().publish(fos);
        fos.close();
        return 0;        
    }
    
}
