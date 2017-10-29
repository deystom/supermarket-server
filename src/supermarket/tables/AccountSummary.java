/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.tables;
import supermarket.tables.util.MyTablesTemplate;
import supermarket.utility.InputsManager;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import java.awt.event.*;

/**
 *
 * @author Segun
 */
public class AccountSummary extends MyTablesTemplate implements Runnable{

    private AccountSummary self = this;
    
    private Vector<String> headerV;
    //private Vector<String> datasV;
    private Connection con = null;
    private Statement stat = null;
    private ResultSet result;
    private String sql;

    private Vector<String> usersV;
    private Vector<String> datesV;

    private Vector<String> usersV_bck; //back up vector for users
    private Vector<String> datesV_bck; //back up vector for dates

    private Vector<Object> temp_V;

    private SwingWorker<Void, Void> work;

    private int dateRowCount = 0;

    private int grossTotal = 0;
    private int netTotal = 0;

    private JComboBox dateCombo;
    private JComboBox userCombo;

    private JTable theTable;
    private boolean loaded;

    public AccountSummary(Connection c, JTable t, JComboBox da, JComboBox us){
        //sql = "SELECT * FROM comodities";
       //initialize the columns/ header to be displayed in the registered users table
        headerV = new Vector<String>();

        usersV = new Vector<String>();
        datesV = new Vector<String>();

        usersV_bck = new Vector<String>();
        datesV_bck = new Vector<String>();

        temp_V = new Vector<Object>();

        con = c;
        dateCombo = da;
        userCombo = us;
        theTable = t;

        headerV.addElement("DATE");
        headerV.addElement("USER");

        headerV.addElement("ITEM NAME");
        headerV.addElement("QTY SOLD");
        headerV.addElement("CASH");
        headerV.addElement("TIME");
        
        headerV.addElement("^TOTAL CASH");
        headerV.addElement("^GROSS SUM");
        headerV.addElement("^NET SUM");

        setHeaderVector(headerV);
        loadUsers(); //first load users
        loadDates(); //then load dates .
        init();
    }

    private void init(){
        work = new SwingWorker<Void, Void>(){
            public Void doInBackground(){
                for(int i = 0; i < datesV.size(); i++){
                    for(String u : usersV){
                        getData(i, datesV.elementAt(i), u);
                    }
                    //then append
                       Vector re_DataV = new Vector();
                           re_DataV.addElement("...............................");
                           re_DataV.addElement("...............................");
                           re_DataV.addElement("...............................");
                           re_DataV.addElement("...............................");
                           re_DataV.addElement("...............................");
                           re_DataV.addElement("...............................");
                           re_DataV.addElement("................................");
                           re_DataV.addElement(InputsManager.formatNairaTextField(String.valueOf(grossTotal)));
                           re_DataV.addElement(" ");

                       temp_V.addElement(re_DataV);
                    netTotal = netTotal + grossTotal;
                }
                //then append
                Vector re_DataV = new Vector();
                re_DataV.addElement("...............................");
                re_DataV.addElement("...............................");
                re_DataV.addElement("...............................");
                re_DataV.addElement("...............................");
                re_DataV.addElement("...............................");
                re_DataV.addElement("...............................");
                re_DataV.addElement("...............................");
                re_DataV.addElement("...............................");
                re_DataV.addElement(InputsManager.formatNairaTextField(String.valueOf(netTotal)));
                temp_V.addElement(re_DataV);
                setDataVector(temp_V); //data vector is sethere
                fireTableDataChanged();
                return null;
            }
            @Override
            public void done(){
                //dateCombo = new JComboBox();
                //userCombo = new JComboBox();
                if(!loaded){
                    Vector d = new Vector();
                    d.addElement("Query a Date");
                    d.addAll(datesV_bck);
                    Vector u = new Vector();
                    u.addElement("Quary a User");
                    u.addAll(usersV_bck);
                    //then populate the comboboxes
                    for(Object o: d){
                        dateCombo.addItem(o);
                    }
                    for(Object o: u){
                        userCombo.addItem(o);
                    }
                    loadActions();
                    dateCombo.revalidate();
                    userCombo.revalidate();
                    loaded = true;
                }
                
                // when the background is done .. then query the database to populate designated data per date
              
            }
        };
    }

    public void run(){
        work.execute(); //execute this swingworker from this thread
    }

    public void setShuffle(String d, String u){        
        if(!d.equals("") && u.equals("")){
            //date is set but the users not set
            datesV.clear();
            usersV.clear();
            usersV.addAll(usersV_bck);
            datesV.addElement(d);
        }
        else if(d.equals("") && !u.equals("")){
            //if users set and date not set
            usersV.clear();
            datesV.clear();
            datesV.addAll(datesV_bck);
            usersV.addElement(u);
        }
        else if(!d.equals("") && !u.equals("")){
            //if both are set
            datesV.clear();
            usersV.clear();
            datesV.addElement(d);
            usersV.addElement(u);
        }
        else{
            //if both are not set
            usersV.clear();
            datesV.clear();
            usersV.addAll(usersV_bck);
            datesV.addAll(datesV_bck);
        }
    }

    private void loadUsers(){
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            result = stat.executeQuery("SELECT name FROM users");
            if(result.first()){
                do{
                    usersV.addElement(result.getString("name"));
                }
                while(result.next());
            }
        }
        catch(SQLException sE){
            System.err.println("Error occured at Load users : " + sE.getMessage());
        }
        finally{
            usersV_bck.addAll(usersV);
        }
    }
/*
    public Vector<String> getUsers(){
        return usersV;
    }

    public Vector<String> getDates(){
        return datesV;
    }

 * 
 */
    private void loadDates(){
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            result = stat.executeQuery("SELECT DISTINCT date FROM sales");
            if(result.first()){
                do{
                    String a[] = result.getString("date").split(" # ");
                    if(!datesV.contains(a[1])){
                        datesV.addElement(a[1]);
                    }
                }
                while(result.next());
            }
        }
        catch(SQLException sE){
            System.err.println("Error occured at Load users : " + sE.getMessage());
        }
        finally{
            datesV_bck.addAll(datesV);
        }
    }

    /*
    public void passSqlKey(String item, int minS, int maxS, int minP, int maxP){
        sql = "SELECT * FROM comodities WHERE name LIKE '" + item + "%' AND (stock_qty >= " + minS + " && stock_qty <= " + maxS +
                ") AND (unit_price >= " + minP + " && unit_price <= " + maxP + ")";
        getAllDataFromDatabase();
        refreshTable();
    }
     *
     */

    private void getData(int index, String d, String u){
        int totalCash = 0;
        sql = "SELECT * FROM sales WHERE user='" + u + "' AND date LIKE '%" + d + "'";
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            result = stat.executeQuery(sql);
            if(result.first()){
                do{
                    Vector re_DataV = new Vector(); //the result vector
                    if(dateRowCount == index){
                        re_DataV.addElement(d); //since its the first date row... add the date to show a start
                        dateRowCount++;
                    }
                    else{
                        re_DataV.addElement(" ");//since its not the first row.. add blank
                    }
                    re_DataV.addElement(u);//continue by adding the user's name
                    re_DataV.addElement(result.getString("com_name"));
                    re_DataV.addElement(result.getString("qty_purchased"));
                    re_DataV.addElement(InputsManager.formatNairaTextField(result.getString("total")));
                    String []t  = result.getString("date").split(" # ");
                    re_DataV.addElement(t[0]);//the time of sales
                    totalCash = totalCash + Integer.parseInt(result.getString("total"));
                    re_DataV.addElement(" ");
                    re_DataV.addElement(" ");
                    re_DataV.addElement(" ");//empty for the net total

                    temp_V.addElement(re_DataV);
                }
                while(result.next());
                //then append
                
                for(int i=0; i < 3; i++){
                   Vector re_DataV = new Vector();
                   if(i == 1){
                       re_DataV.addElement(" ");
                       re_DataV.addElement("**************");
                       re_DataV.addElement("**************");
                       re_DataV.addElement("**************");
                       re_DataV.addElement("**************");
                       re_DataV.addElement("**************");
                       re_DataV.addElement(InputsManager.formatNairaTextField(String.valueOf(totalCash)));
                       re_DataV.addElement(" ");
                       re_DataV.addElement(" ");
                   }
                   else{
                       re_DataV.addElement(" ");
                       re_DataV.addElement("--------------------");
                       re_DataV.addElement("--------------------");
                       re_DataV.addElement("--------------------");
                       re_DataV.addElement("--------------------");
                       re_DataV.addElement("--------------------");
                       re_DataV.addElement("--------------------");
                       re_DataV.addElement(" ");
                       re_DataV.addElement(" ");
                   }
                   temp_V.addElement(re_DataV);
                }                
                grossTotal = grossTotal + totalCash;
            }
        }
        catch(SQLException sE){
            System.err.println("Error getting data of : DATE=" + d + " &USER=" + u + " CAUSE : " + sE.getMessage());
        }
    }


    private void loadActions(){
        dateCombo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        if(dateCombo.getSelectedIndex() == 0 && userCombo.getSelectedIndex() == 0){
                            setShuffle("", "");
                        }
                        else if(dateCombo.getSelectedIndex() == 0 && userCombo.getSelectedIndex() != 0){
                            setShuffle("", userCombo.getSelectedItem().toString());
                        }
                        else if(dateCombo.getSelectedIndex() != 0 && userCombo.getSelectedIndex() == 0){
                            setShuffle(dateCombo.getSelectedItem().toString(), "");
                        }
                        else{
                            setShuffle(dateCombo.getSelectedItem().toString(), userCombo.getSelectedItem().toString());
                        }
                        refreshTable();
                    }
                });
            }
        });

        userCombo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        if(dateCombo.getSelectedIndex() == 0 && userCombo.getSelectedIndex() == 0){
                            setShuffle("", "");
                        }
                        else if(dateCombo.getSelectedIndex() == 0 && userCombo.getSelectedIndex() != 0){
                            setShuffle("", userCombo.getSelectedItem().toString());
                        }
                        else if(dateCombo.getSelectedIndex() != 0 && userCombo.getSelectedIndex() == 0){
                            setShuffle(dateCombo.getSelectedItem().toString(), "");
                        }
                        else{
                            setShuffle(dateCombo.getSelectedItem().toString(), userCombo.getSelectedItem().toString());
                        }
                        refreshTable();
                    }
                });
            }
        });
    }

    public void refreshTable(){
        temp_V.clear();
        dateRowCount = 0;
        grossTotal = 0;
        netTotal = 0;
        new SwingWorker<Void, Void>(){
            public Void doInBackground(){
                init();
                work.execute(); //execute this swingworker from this thread
                return null;
            }
            @Override
            public void done(){
                fireTableDataChanged();
                theTable.revalidate();
            }
        }.execute();
    }
}
