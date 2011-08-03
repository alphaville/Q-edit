/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qedit.task;

import org.opentox.toxotis.core.component.Compound;
import org.opentox.toxotis.core.component.Feature;
import org.opentox.toxotis.ontology.LiteralValue;
import qedit.QEditApp;
import qedit.ReportIF;

/**
 *
 * @author chung
 */
public class ExperimentalValueRetriever extends AbstractTask {

    private ReportIF intFrame;

    public ExperimentalValueRetriever(ReportIF intFrame) {
        super();
        this.intFrame = intFrame;
        super.taskName = "Downloading experimental value for "+intFrame.getReport().getCompound().getIupacName();
    }

    @Override
    protected Object doInBackground() throws Exception {
        Compound cmp = intFrame.getReport().getCompound();
        if (cmp == null) {
            exceptionMessage = "No compound in the current report";
            throw new NullPointerException();
        }
        Feature feat = intFrame.getReport().getModel().getDependentFeatures().get(0);
        LiteralValue lv = cmp.getProperty(feat, QEditApp.getAuthentication());
        if (lv != null) {
            intFrame.getExpValueField().setText(lv.getValueAsString());
        }else{
            exceptionMessage = "No values found for this compound!";
            throw new NullPointerException();
        }
        if (feat.getUnits() != null) {
            intFrame.getExpValueUnitsField().setText(feat.getUnits());
        }
        return 0;
    }
}
