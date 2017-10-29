/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.tables;
import supermarket.tables.util.MyTablesTemplate;
import java.util.Vector;
import java.sql.*;

/**
 *
 * @author Segun
 */
public class RegisteredUsers extends MyTablesTemplate{

    private Vector<String> headerV;
    //private Vector<String> datasV;
    private Connection con;
    private Statement stat;
    private ResultSet result;

    //this class constructor must me initialized with an instance of a connection to be passed as an argument
    public RegisteredUsers(Connection c){
       //initialize the columns/ header to be displayed in the registered users table
        headerV = new Vector<String>();
        //datasV = new Vector<String>();
        con = c;

        headerV.addElement("USER ID");
        headerV.addElement("NAME");

        getDataFromDatabase();
        setHeaderVector(headerV);        
    }

    private void getDataFromDatabase(){
        Vector temp_V = new Vector();
        int id = 1;
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            result = stat.executeQuery("SELECT name FROM users");
            if(result.first()){
                do{
                    Vector re_DataV = new Vector();
                    re_DataV.addElement(String.valueOf(id));
                    re_DataV.addElement(result.getString("name"));
                    temp_V.addElement(re_DataV);
                    id++;
                }
                while(result.next());
            }
        }
        catch(SQLException s){
            System.err.println("" + s.getMessage());
        }

        //after the datahas being gotten from the database and populated to the re_DataVector... then pass it by calling inherited
        //method setDataV
        setDataVector(temp_V); //data vector is sethere
    }

    public void refreshTable(){
        new javax.swing.SwingWorker<Void, Void>(){
            public Void doInBackground(){
                getDataFromDatabase();
                return null;
            }
            @Override
            public void done(){
                fireTableDataChanged();
            }
        }.execute();
    }
}
