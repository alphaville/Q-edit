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
public class Login extends AbstractTask {

    private String username;
    private String password;

    public Login(String username, String password) {
        super();
        this.username = username;
        this.password = password;        
    }

    @Override
    protected Object doInBackground() throws Exception {
        AuthenticationToken token = new AuthenticationToken(username, password);
        QEditApp.setAuthentication(token);
        return token;
    }
}
