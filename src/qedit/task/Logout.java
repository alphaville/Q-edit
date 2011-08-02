/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qedit.task;

import org.opentox.toxotis.util.aa.AuthenticationToken;
import qedit.QEditApp;

/**
 *
 * @author chung
 */
public class Logout extends AbstractTask {

    public Logout() {
        super();
        taskName = "Log Out";
    }

    @Override
    protected Object doInBackground() throws Exception {
        if (QEditApp.getAuthentication() == null) {
            return 0;
        }
        QEditApp.getAuthentication().invalidate();
        QEditApp.setAuthentication(null);
        return 1;
    }
}
