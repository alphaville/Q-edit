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
        intFrame.clearStrAnalogues();
        intFrame.getDwnExpValuesButton().setEnabled(false);
        intFrame.getSimilarityField().setEnabled(false);
        intFrame.getAcquireStrAnaloguesButton().setEnabled(false);
        double similarity = Double.parseDouble(intFrame.getSimilarityField().getText());
        Compound c = intFrame.getReport().getCompound();
        SimilarityRetriever sr = new SimilarityRetriever(similarity, c);
        sr.setRetrieveDepiction(true);
        sr.authenticate(QEditApp.getAuthentication());
        intFrame.getReport().setStructuralAnalogues(sr.similarCompounds());
        intFrame.updateStrAnal();
        return 0;
    }

    @Override
    protected void finished() {
        intFrame.getSimilarityField().setEnabled(true);
        intFrame.getAcquireStrAnaloguesButton().setEnabled(true);
        if (intFrame.getReport().getModel() != null
                && intFrame.getReport().getModel().getDependentFeatures() != null
                && !intFrame.getReport().getModel().getDependentFeatures().isEmpty()) {
            intFrame.getDwnExpValuesButton().setEnabled(true);
        }
        super.finished();
    }
}
