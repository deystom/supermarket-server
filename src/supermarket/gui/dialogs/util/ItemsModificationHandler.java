/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui.dialogs.util;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import java.awt.event.*;

/**
 *
 * @author MUSTAFA
 */
public class ItemsModificationHandler {
    
    //private String item;
    //private int price;
    private List<String> itemList;
    private List<String> bulkList;
    private Connection con;
    private Statement stat;
    private ResultSet result;

    private Timer t;

    private ItemsLoaderWork work;

    public ItemsModificationHandler(Connection c){
        con = c;
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        }
        catch(SQLException sqlE){
            System.err.println("Something wrong with the CONNECTION : " + sqlE.getMessage());
        }
        work = new ItemsLoaderWork();
        work.execute(); //do the first execution
        t = new Timer(10000, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                    work.execute();
                /*
                else{
                    try{
                        this.wait();
                        work.notifyAll();
                    }
                    catch(InterruptedException iE){
                        System.err.println(iE.getMessage());
                    }
                }
                 * 
                 */
            }
        });
        if(!t.isRunning()){
            t.start();
        }
    }

    public String genBulkName(String b){
        String n = "";
        if(isBulkItemExist("BULK_" + b)){
            //means this particular item exist in the db
            n = "ALREADY IN STORE";
        }
        else{
            n = "BULK_" + b;
        }
        return n;
    }

    public String genUnitName(String b){
        String n = "";
        if(isUnitItemExist(b)){
            //means this particular item exist in the db
            n = "ALREADY IN STORE";
        }
        else{
            n = b;
        }
        return n;
    }


    private void loadUnitItemExist(){
        try{
            result = stat.executeQuery("SELECT name FROM comodities_unit");
            if(result.first()){
                do{
                    //start to populate the arraylist
                    itemList.add(result.getString("name"));
                }
                while(result.next());
            }
            else{
                itemList.add("");
            }
        }
        catch(SQLException sqlE){
            System.err.println("Something wrong while checking the " + sqlE.getMessage());
        }
    }

    private void loadBulkItemExist(){
        try{
            result = stat.executeQuery("SELECT name FROM comodities_bulk");
            if(result.first()){
                do{
                    //start to populate the arraylist
                    bulkList.add(result.getString("name"));
                }
                while(result.next());
            }
            else{
                bulkList.add("");
            }
        }
        catch(SQLException sqlE){
            System.err.println("Something wrong while checking the " + sqlE.getMessage());
        }
    }

    public boolean isUnitItemExist(String s){
        boolean status = false;
        for(String i : itemList){
            if(i.equals(s.trim())){
                status = true;
                //System.out.println("EXIST OOO : " + i);
            }
            else{
                status = false;
            }
        }
        return status;
    }

    public boolean isBulkItemExist(String s){
        boolean status = false;
        for(String i : bulkList){
            if(i.equals(s.trim())){
                //System.out.println("EXIST OOO : " + i);
                status = true;
            }
            else{
                status = false;
            }
        }
        return status;
    }

    private class ItemsLoaderWork extends SwingWorker<Void, Void>{
        public ItemsLoaderWork(){

        }

        public Void doInBackground(){
            itemList = new ArrayList<String>(); //initialize the itemList
            bulkList = new ArrayList<String>(); //the bulk items list
            loadUnitItemExist();
            loadBulkItemExist();
            return null;
        }

        @Override
        public void done(){
        }
    }
}
