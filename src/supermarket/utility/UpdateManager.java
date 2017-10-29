/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.utility;
import supermarket.gui.AdminFrame;

/**
 *
 * @author Segun
 */
public class UpdateManager {

    private String user;
    private String email;
    private String tel;
    private String add;
    private String version;

    private AdminFrame parent;

    public UpdateManager(AdminFrame admin, String u, String e, String t, String a, String v){
        parent = admin;
        user = u;
        email = e;
        tel = t;
        add = a;
        version = v;
        new javax.swing.SwingWorker<Void, Void>(){
            public Void doInBackground(){
                new DunnyUpdater();
                return null;
            }
        }.execute();
        
    }
/*
    public static void main(String []args){
        new UpdateManager(null, "", "", "", "", "");
    }

 * 
 */
    class DunnyUpdater extends Updater{
        
        DunnyUpdater(){
            super(parent, email, user, add, tel, version, 0);
            //System.out.println(email + user + add + tel + version);
        }
    }
}

