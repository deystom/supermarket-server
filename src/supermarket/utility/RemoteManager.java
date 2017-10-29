/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.utility;
import supermarket.tables.OnlineUsers;
import javax.swing.JTable;
import java.sql.*;

/**
 *
 * @author Segun
 */
public class RemoteManager{

    private RemoteManager self = this;
    private JTable theT;
    private Connection conn;

    private OnlineUsers onlineTM;

    private java.util.Vector<String> info;

    public RemoteManager(JTable t, Connection c){
        conn = c;
        theT = t;
        onlineTM = new OnlineUsers(conn, self);
        javax.swing.SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                init();
            }
        });
    }

    private void init(){
        theT.setModel(onlineTM);
        changeHappened();
    }

    public void changeHappened(){
        new javax.swing.SwingWorker<Void, Void>(){
            public Void doInBackground(){
                new javax.swing.Timer(6000, new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        onlineTM.refreshTable();
                    }
                }).start();
                return null;
            }
        }.execute();
    }

    @SuppressWarnings("static-access")
    public java.util.Vector<String> getClientInformation(final String n){
        //pass in the name of the user inside
        info = new java.util.Vector<String>();
        //new javax.swing.SwingWorker<Void, Void>(){
            //public Void doInBackground(){
                try{
                    Statement stat = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet result = stat.executeQuery("SELECT * FROM online_sys WHERE user_logged='" + n + "'");
                    if(result.first()){
                        info.addElement(result.getString("sys_name"));
                        info.addElement(result.getString("user_logged")); //the system's IP address
                    }
                }
                catch(SQLException e){
                    System.err.println("Error getting Client HOST : " + e.getMessage());
                }
                //return null;
            //}
        //}.execute();
        return info;
    }
}
