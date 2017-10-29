/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.tables;
import supermarket.tables.util.MyTablesTemplate;
import java.sql.*;
import java.util.Vector;

/**
 *
 * @author Segun
 */
public class Sales extends MyTablesTemplate{

    private Vector<String> headerV;
    //private Vector<String> datasV;
    private Connection con = null;
    private Statement stat = null;
    private ResultSet result;

    private String date = "%";

    private String sql = "";
    
    public Sales(Connection c, String d){
       //initialize the columns/ header to be displayed in the registered users table
        headerV = new Vector<String>();
        //datasV = new Vector<String>();
        con = c;
        date = d;
        sql = "SELECT * FROM sales WHERE date LIKE '%" + date + "'";

        headerV.addElement("ITEM NAME");
        headerV.addElement("QTY SOLD");
        headerV.addElement("CASH");
        headerV.addElement("TIME");
        headerV.addElement("USER");

        getDataFromDatabase();
        setHeaderVector(headerV);
    }

    private void getDataFromDatabase(){
        Vector temp_V = new Vector();
        int total = 0;
        int qty = 0;
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            result = stat.executeQuery(sql);
            //System.out.println(sql);
            if(result.first()){
                do{
                    Vector re_DataV = new Vector();
                    re_DataV.addElement(result.getString("com_name"));
                    re_DataV.addElement(String.valueOf(result.getInt("qty_purchased")));
                    qty = qty + result.getInt("qty_purchased");
                    re_DataV.addElement(supermarket.util.InputsManager.formatNairaTextField(String.valueOf(result.getInt("total"))));
                    total = total + result.getInt("total");
                    re_DataV.addElement(result.getString("date"));
                    re_DataV.addElement(result.getString("user"));
                    temp_V.addElement(re_DataV);
                }
                while(result.next());
                //after the datahas being gotten from the database and populated to the re_DataVector... then pass it by calling inherited
                //method setDataV
                //append datas
                for(int i=0; i < 3; i++){
                   Vector re_DataV = new Vector();
                   if(i == 1){
                       re_DataV.addElement("TOTAL");
                       re_DataV.addElement(qty);
                       re_DataV.addElement(supermarket.util.InputsManager.formatNairaTextField(String.valueOf(total)));
                       re_DataV.addElement(" ");
                       re_DataV.addElement(" ");
                   }
                   else{
                       re_DataV.addElement("--------------------");
                       re_DataV.addElement("--------------------");
                       re_DataV.addElement("--------------------");
                       re_DataV.addElement("--------------------");
                       re_DataV.addElement("--------------------");
                   }
                   temp_V.addElement(re_DataV);
                }
                //after the datahas being gotten from the database and populated to the re_DataVector... then pass it by calling inherited
                //method setDataV
                setDataVector(temp_V); //data vector is sethere
            }
            else{
                setDataVector(new Vector()); //data vector is sethere
            }
        }
        catch(SQLException s){
            System.err.println("Problem getting result from DB : " + s.getMessage());
        }
    }

    public void queryData(String it, String uN, String da){
        if(it.startsWith("Select") && uN.startsWith("Select") && da.startsWith("Select")){
            sql = "SELECT * FROM sales WHERE date LIKE '%" + date + "'";
        }
        else if(it.startsWith("Select") && !uN.startsWith("Select") && da.startsWith("Select")){
            sql = "SELECT * FROM sales WHERE user='" + uN + "' AND date LIKE '%" + date + "'";
        }
        else if(it.startsWith("Select") && uN.startsWith("Select") && !da.startsWith("Select")){
            sql = "SELECT * FROM sales WHERE date LIKE '%" + da + "'";
        }
        else if(!it.startsWith("Select") && uN.startsWith("Select") && da.startsWith("Select")){
            sql = "SELECT * FROM sales WHERE com_name='" + it + "' AND date LIKE '%" + date + "'";
        }
        else if(!it.startsWith("Select") && !uN.startsWith("Select") && da.startsWith("Select")){
            sql = "SELECT * FROM sales WHERE com_name='" + it + "' AND user='" + uN + "' AND date LIKE '%" + date + "'";
        }
        else if(it.startsWith("Select") && !uN.startsWith("Select") && !da.startsWith("Select")){
            sql = "SELECT * FROM sales WHERE user='" + uN + "' AND date LIKE '%" + da + "'";
        }
        else if(!it.startsWith("Select") && uN.startsWith("Select") && !da.startsWith("Select")){
            sql = "SELECT * FROM sales WHERE com_name='" + it + "' AND date LIKE '%" + da + "'";
        }
        else{
            sql = "SELECT * FROM sales WHERE com_name='" + it + "' AND user='" + uN + "' AND date LIKE '%" + da + "'";
        }
        this.clearTable();
        refreshTable();
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
