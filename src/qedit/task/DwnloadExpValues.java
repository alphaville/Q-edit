/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qedit.task;

import java.util.ArrayList;
import org.opentox.toxotis.core.component.Compound;
import org.opentox.toxotis.core.component.Feature;
import org.opentox.toxotis.ontology.LiteralValue;
import qedit.QEditApp;
import qedit.ReportIF;

/**
 *
 * @author chung
 */
public class DwnloadExpValues extends AbstractTask {

    private final ReportIF intFrame;

    public DwnloadExpValues(ReportIF intFrame) {
        super();
        this.intFrame = intFrame;
        taskName = "Downloading experimental values for analogues";
    }

    @Override
    protected Object doInBackground() throws Exception {
        ArrayList<Compound> analogues = intFrame.getReport().getStructuralAnalogues();
        Feature dependentFeature = intFrame.getReport().getModel().getDependentFeatures().get(0);
        LiteralValue currentValue = null;
        ArrayList<String> experimentalValues = new ArrayList<String>(analogues.size());
        int indexRow = 0;
        for (Compound c : analogues) {
            currentValue = c.getProperty(dependentFeature, QEditApp.getAuthentication());
            if (currentValue != null) {
                intFrame.getAnaloguesTable().setValueAt(currentValue.getValueAsString(), indexRow, 1);
            }
            experimentalValues.add(currentValue != null ? currentValue.getValueAsString() : "");
            indexRow++;
        }
        intFrame.getReport().setExperimentalValues(experimentalValues);
        return 0;
    }
}
