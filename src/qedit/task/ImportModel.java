/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qedit.task;

import java.net.URISyntaxException;
import javax.swing.JFrame;
import org.opentox.toxotis.client.VRI;
import org.opentox.toxotis.core.component.Algorithm;
import org.opentox.toxotis.core.component.Model;
import org.opentox.toxotis.exceptions.ISecurityException;
import org.opentox.toxotis.exceptions.impl.ServiceInvocationException;
import qedit.QEditApp;
import qedit.QEditView;
import qedit.ReportIF;
import qedit.helpers.Authenticate;

/**
 *
 * @author chung
 */
public class ImportModel extends AbstractTask {

    private final ReportIF intFrame;

    public ImportModel(ReportIF intFrame) {
        super();
        this.intFrame = intFrame;
        taskName = "Importing Model";
    }

    @Override
    protected Object doInBackground() throws Exception {
        intFrame.getReport().setModel(null);
        intFrame.setEnabledModelDetailsButtons(false);
        String modelUriString = intFrame.getModelUriField().getText();
        if (modelUriString == null || (modelUriString != null && modelUriString.isEmpty())) {
            exceptionMessage = "No model URI was provided - no model loaded.";
            throw new Exception();
        }

        VRI modelVri = null;
        try {
            java.net.URI temp = new java.net.URI(modelUriString);
            modelVri = new VRI(modelUriString);
        } catch (URISyntaxException ex) {
            exceptionMessage = "The model URI you sumbitted is not valid!";
            throw ex;
        }
        Model model = new Model(modelVri);
        boolean modelOK = false; // whether the model was loaded from the remote location.
        try {
            model.loadFromRemote(QEditApp.getAuthentication());
            modelOK = true;
        } catch (final ServiceInvocationException sie) {
            if (sie instanceof ISecurityException) {
                JFrame jframe = QEditApp.getView().getFrame();
                Authenticate authenticate = new Authenticate(intFrame, jframe, false);
                intFrame.displayDiagol(authenticate, jframe);
                Login loginTask = authenticate.getLoginTask();
                /*
                 * Wait for loginTask to be created
                 */
                while (loginTask == null) {
                    loginTask = authenticate.getLoginTask();
                    Thread.sleep(500);
                }
                /*
                 * Wait for loginTask to complete
                 */
                while (!loginTask.isDone()) {
                    Thread.sleep(500);
                }
            } else {
                exceptionMessage = "The Model could not be loaded from " + modelVri;
                throw new Exception(sie);
            }

        }
        if (!modelOK) {
            try {
                model.loadFromRemote(QEditApp.getAuthentication());
            } catch (final ServiceInvocationException sie) {
                if (sie instanceof ISecurityException) {
                    exceptionMessage = "You don't have access rights to " + modelVri;
                    throw new Exception(sie);
                } else {
                    exceptionMessage = "The Model could not be loaded from " + modelVri;
                    throw new Exception(sie);
                }
            }
        }
        intFrame.getReport().setModel(model);
        intFrame.setEnabledModelDetailsButtons(true);
        /*
         * Make an attempt to download algorithm info.
         * If it fails it doesn't matter at all!
         */
        try {
            Algorithm algo = model.getAlgorithm().loadFromRemote(QEditApp.getAuthentication());
            model.setAlgorithm(algo);
            System.out.println(algo.getMeta().getSubjects());
        } catch (ServiceInvocationException ex) {
            ex.printStackTrace();
        }

        return new Object();
    }

    @Override
    protected void cancelled() {
        intFrame.setEnabledModelDetailsButtons(false);
        super.cancelled();
    }

    @Override
    protected void failed(Throwable cause) {
        intFrame.setEnabledModelDetailsButtons(false);
        super.failed(cause);
    }
}
