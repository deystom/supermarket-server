/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui.util;
import supermarket.database.PublicMXJdataEmbedded;
import supermarket.gui.*;
import javax.swing.*;
import java.awt.Dimension;
import java.sql.*;
import java.util.*;

/**
 *
 * @author Segun
 */
public class PanelManager {

    /////////////////////////////////////////////////////////////////////////
    //this panel manager hold the connection to database for Panels thats gonna need it
    /////////////////////////////////////////////////////////////////////////
    private Connection con = null;
    ////////////////////////////////////////////////////////////////////////
    private AdminFrame parent;
    /////////////////////
    ////////////////////
    private int index = 0;
    private JPanel curPanel, prevPanel;
    private boolean event = false;
    private LoginPanel login;
    private WelcomePanel welcome;
    private Dimension pDim;
    private List<JComponent> item;

    public PanelManager(){
        login = new LoginPanel(this, 912, 434);
        welcome = new WelcomePanel(this, 912, 434);
    }

    public void passConnectionToPanelManager(Connection c){
        con = c;
        login.startDbWork();
        Thread t = new Thread(new Runnable(){
            public void run(){
                welcome.initDbConnections();
            }
        });
        t.start();
    }

    public WelcomePanel getWelcomeInstance(){
        return welcome;
    }

    public void reCreateNewInstanceOfPanels(){
        try{
            login = null;
            welcome = null;
            Thread.sleep(300);
            System.gc();
            Thread.sleep(200);
        }
        catch(InterruptedException iE){
            System.err.println("Thread was interrupted when trying to create new Instance of Panels : " + iE.getMessage());
        }
        catch(NullPointerException nE){
            System.err.println(nE.getMessage());
        }
        finally{
            login = new LoginPanel(this, 912, 434);
            welcome = new WelcomePanel(this, 912, 434);
            welcome.passMenuVector(item);
            welcome.passFrameInstance(parent);
        }
    }

    public WelcomePanel.DbRegistration getRegisteredInfoInst(){
        return welcome.getRegisteredInfoInst();
    }

    public int getSelectedIndexOfItems(){
        return welcome.getSelectedIndexOfItems();
    }

    public JTable getOnlineUsersTable(){
        return welcome.getUserOnlineTable();
    }
    
    public void passMenuItemsVector(List<JComponent> it){
        item = it;
        welcome.passMenuVector(item);
    }

    public void passFrameInstance(AdminFrame p){
        parent = p;
        welcome.passFrameInstance(parent);
    }

    public AdminFrame getFrameInstance(){
        return parent;
    }
    
    public Connection getAvailableConnection(){
        try{
            try{
                if(con.isValid(2000)){
                    //System.out.println("connection is still valid ");
                }
                else{
                    //maake another connection here
                    con = PublicMXJdataEmbedded.getNewConnection();
                    System.out.println("The previous connection is no more valid... i had to renew it ");
                }
            }
            catch(SQLException e){
                System.err.println("Error trying to check if connection is still valid : " + e.getMessage());
            }
        }
        catch(NullPointerException ne){
            System.err.println("Conection instance is currently NULL : " + ne.getMessage());
        }
        return con;
    }
    
    public void setPanelIndex(int a){
        index = a;
        event = true;
    }

    public boolean isSmthHappened(){
        return event;
    }

    public void setSmthHappened(boolean s){
        event = s;
    }
    
    public JPanel getPreviousPanel(){
        return prevPanel;
    }
    
    public JPanel getCurrentPanelToDisplay(){
        init();     
        return curPanel;
    }

    public Dimension getCurrentDimesion(){
        return pDim;
    }

    public void passNewInstanceOfPanel(JPanel p){
        curPanel = p;
        event = true; //      
    }

    private void init(){

        switch(index){
            case 0:
                curPanel = login;
                prevPanel = login;
                pDim = login.getSize();
                curPanel.setVisible(true);
                break;
            case 1:
                curPanel = welcome;
                prevPanel = welcome;
                pDim = welcome.getSize();
                curPanel.setVisible(true);
                break;
            default:
                curPanel = login;
                prevPanel = login;
                pDim = login.getSize();
                curPanel.setVisible(true);
                break;
        }
        
    }

}
