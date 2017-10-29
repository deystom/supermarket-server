/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.tables;
import supermarket.tables.util.MyTablesTemplate;
import supermarket.utility.InputsManager;
import java.sql.*;
import java.util.Vector;
import javax.swing.SwingWorker;

/**
 *
 * @author Segun
 */
public class Catalogs extends MyTablesTemplate{

    private Vector<String> headerV;
    //private Vector<String> datasV;
    private Connection con = null;
    private Statement stat = null;
    private ResultSet result;
    private String tableName;

    public Catalogs(Connection c, String tn){
       //initialize the columns/ header to be displayed in the registered users table
        headerV = new Vector<String>();
        //datasV = new Vector<String>();
        con = c;
        tableName = tn; //name of the table to look into

        headerV.addElement("ID");
        headerV.addElement("DESCRIPTION");
        headerV.addElement("PRICE");
        headerV.addElement("STOCK");

        getDataFromDatabase();
        setHeaderVector(headerV);
    }

    private void getDataFromDatabase(){
        Vector<Vector<String>> temp_V = new Vector<Vector<String>>();
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            result = stat.executeQuery("SELECT * FROM " + tableName);
            if(result.first()){
                int id = 1;
                do{
                    Vector <String>re_DataV = new Vector<String>();
                    re_DataV.addElement(String.valueOf(id));
                    re_DataV.addElement(result.getString("name"));
                    re_DataV.addElement(InputsManager.formatNairaTextField(String.valueOf(result.getInt("unit_price"))));
                    re_DataV.addElement(String.valueOf(result.getInt("stock_qty")));
                    temp_V.addElement(re_DataV);
                    id ++;
                }
                while(result.next());
                //after the datahas being gotten from the database and populated to the re_DataVector... then pass it by calling inherited
                //method setDataV
                setDataVector(temp_V); //data vector is sethere
            }
        }
        catch(SQLException s){
            System.err.println("Problem getting result from DB : " + s.getMessage());
        }
    }

    public void get_searchDataFromDatabase(String key){
        Vector <Vector<String>>temp_V = new Vector<Vector<String>>();
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            result = stat.executeQuery("SELECT * FROM " + tableName + " WHERE name LIKE '%" + key + "%'");
            if(result.first()){
                int id = 1;
                do{
                    Vector <String>re_DataV = new Vector<String>();
                    re_DataV.addElement(String.valueOf(id));
                    re_DataV.addElement(result.getString("name"));
                    re_DataV.addElement("=N= " + String.valueOf(result.getInt("unit_price")));
                    re_DataV.addElement(String.valueOf(result.getInt("stock_qty")));
                    temp_V.addElement(re_DataV);
                    id ++;
                }
                while(result.next());
                //after the datahas being gotten from the database and populated to the re_DataVector... then pass it by calling inherited
                //method setDataV
                setDataVector(temp_V); //data vector is sethere
            }
        }
        catch(SQLException s){
            System.err.println("Problem getting result from DB : " + s.getMessage());
        }
    }

    public void refreshTable(){
        new SwingWorker<Void, Void>(){
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
