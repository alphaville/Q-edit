/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qedit.task;

import org.opentox.toxotis.core.component.Compound;
import org.opentox.toxotis.util.SimilarityRetriever;
import qedit.QEditApp;
import qedit.ReportIF;

/**
 *
 * @author chung
 */
public class StructAnalogues extends AbstractTask {

    private ReportIF intFrame;

    public StructAnalogues(ReportIF intFrame) {
        this.intFrame = intFrame;
        super.taskName = "Retrieving Structural Analogues from OpenTox WSs";
    }

    @Override
    protected Object doInBackground() throws Exception {
        double similarity = Double.parseDouble(intFrame.getSimilarityField().getText());
        Compound c = intFrame.getReport().getCompound();
        SimilarityRetriever sr = new SimilarityRetriever(similarity, c);
        sr.authenticate(QEditApp.getAuthentication());
        intFrame.getReport().setStructuralAnalogues(sr.similarCompounds());
        intFrame.updateStrAnal();
        return 0;
    }
}
