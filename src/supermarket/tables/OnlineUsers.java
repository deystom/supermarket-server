/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.tables;
import supermarket.tables.util.MyTablesTemplate;
import supermarket.utility.RemoteManager;
import java.sql.*;
import java.util.Vector;
import javax.swing.SwingUtilities;
import java.net.*;

/**
 *
 * @author Segun
 */
public class OnlineUsers extends MyTablesTemplate{
    private Vector<String> headerV;
    //private Vector<String> datasV;
    private Connection con;
    private Statement stat;
    private ResultSet result;

    private RemoteManager remote;

    private Vector<Vector> remUserInfoV;

    private String u = ""; //the user name
    private String ip; //the user ip address to be pinged

    //this class constructor must me initialized with an instance of a connection to be passed as an argument
    public OnlineUsers(Connection c, RemoteManager r){
       //initialize the columns/ header to be displayed in the registered users table
        headerV = new Vector<String>();
        remUserInfoV = new Vector<Vector>();
        //datasV = new Vector<String>();
        con = c;
        remote = r;

        headerV.addElement("NAME");
        headerV.addElement("TIME STAMP");

        getDataFromDatabase(); //get them data from database

        setHeaderVector(headerV);
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                try{
                    OnlineUsersMonitor();
                }
                catch(Exception e){
                    System.err.println("Error occured at init OnlineUsersMonitor : " + e.getMessage());
                }
            }
        });
    }

    private void getDataFromDatabase(){
        remUserInfoV.clear(); //first clear this guy
        Vector temp_V = new Vector();
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            //stat.executeUpdate("DELETE FROM sales");
            //stat.executeUpdate("DROP TABLE online_users");
            //stat.executeUpdate("DROP TABLE online_sys");
            result = stat.executeQuery("SELECT * FROM online_users");
            if(result.first()){
                do{
                    Vector re_DataV = new Vector();
                    re_DataV.addElement(result.getString("name"));
                    re_DataV.addElement(result.getString("time_stamp"));

                    remUserInfoV.addElement(remote.getClientInformation(result.getString("name")));
                    
                    temp_V.addElement(re_DataV);
                    Thread.sleep(890);
                    //System.out.printf("%s", "#");
                }
                while(result.next());
            }
        }
        catch(SQLException s){
            System.err.println("Getting Online users from Database : " + s.getMessage());
        }
        catch(InterruptedException iE){}
        finally{
            //after the datahas being gotten from the database and populated to the re_DataVector... then pass it by calling inherited
            //method setDataV
            setDataVector(temp_V); //data vector is sethere       
        }
    }

    private void OnlineUsersMonitor(){
        new javax.swing.SwingWorker<Void, Void>(){
            public Void doInBackground(){
                new javax.swing.Timer(5000, new java.awt.event.ActionListener(){
                    public void actionPerformed(java.awt.event.ActionEvent e){
                        if(!remUserInfoV.isEmpty()){
                            //if the remote user's info is not empty... try to ping each IP Address that all users have
                            //int loop = 0;
                            for(Vector<String> i : remUserInfoV){
                                ip = i.elementAt(0);
                                u = i.elementAt(1);                                
                                //now... first ping the IP
                                try{
                                    //System.out.println(ip);
                                    InetAddress add = InetAddress.getByName(ip);
                                    //if this is successful... that means the user online are connected to the server
                                }
                                catch(UnknownHostException uE){
                                    System.out.println("Host Unknows " + uE);
                                    //that means the host cannot be found.. then delete the user that has logged in with this IP
                                    try{
                                        stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                                        stat.executeUpdate("DELETE FROM online_users WHERE name='" + u + "'");
                                        Thread.sleep(1200);
                                        stat.executeUpdate("DELETE FROM online_sys WHERE user_logged='" + u + "'");
                                    }
                                    catch(Exception sE){
                                        System.err.println("SQL , OnlineUsersMonitor : " + sE.getMessage());
                                    }
                                    finally{
                                        new javax.swing.SwingWorker<Void, Void>(){
                                            public Void doInBackground(){
                                                getDataFromDatabase(); //to reload the populated data
                                                return null;
                                            }
                                        }.execute();
                                    }
                                    //System.err.println("Exception at getting by Address in OnlineUsersMonitor : " + uE.getMessage());
                                }

                            }
                        }
                    }
                }).start();
                return null;
            }
        }.execute();
    }
    
    public void refreshTable(){
        new javax.swing.SwingWorker<Void, Void>(){
            public Void doInBackground(){
                getDataFromDatabase();
                return null;
            }
            @Override
            public void done(){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        fireTableDataChanged();
                    }
                });         
            }
        }.execute();
    }
}
