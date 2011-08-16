/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qedit.task;

import org.opentox.toxotis.ontology.LiteralValue;
import org.opentox.toxotis.util.SuperPredictor;
import qedit.QEditApp;
import qedit.ReportIF;

/**
 *
 * @author chung
 */
public class PredictionTask extends AbstractTask {
    
    private ReportIF intFrame;

    public PredictionTask(ReportIF intFrame) {
        super();
        taskName = "Performing Prediction";
        this.intFrame = intFrame;
    }
    
    

    @Override
    protected Object doInBackground() throws Exception {
        SuperPredictor predictor = new SuperPredictor(intFrame.getReport().getCompound(),
                intFrame.getReport().getModel(), QEditApp.getAuthentication());
        LiteralValue litVal = predictor.prediction();
        System.out.println("Prediction result : " + litVal);
        if (litVal != null) {
            intFrame.getPredictionResult().setText(litVal.getValueAsString());
        }
        return 0;
    }
}
