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
public class StockSummary extends MyTablesTemplate{

    private Vector<String> headerV;
    //private Vector<String> datasV;
    private Connection con = null;
    private Statement stat = null;
    private ResultSet result;
    private String []sql;

    private int module = 0;

    public StockSummary(Connection c, int m){
        //sql = "SELECT * FROM comodities";
       //initialize the columns/ header to be displayed in the registered users table
        headerV = new Vector<String>();
        //datasV = new Vector<String>();
        con = c;
        module = m;

        headerV.addElement("ID");
        headerV.addElement("ITEM NAME");
        headerV.addElement("PRICE");
        headerV.addElement("STOCK");
        headerV.addElement("COST VALUE");

        initSQL();
        
        getDataFromDatabase();
        setHeaderVector(headerV);
    }

    private void initSQL(){
        switch(module){
            case 0:
                //says bulk
                sql = new String[]{"SELECT * FROM comodities_bulk"};
                break;
            case 1:
                //says unit
                sql = new String[]{"SELECT * FROM comodities_unit"};
                break;
            case 2:
                //says both
                sql = new String[]{"SELECT * FROM comodities_unit", "SELECT * FROM comodities_bulk"};
                break;
        }
    }

    public void setModule(int m){
        module = m;
        initSQL();
        this.clearTable();
        this.refreshTable();
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

    private void getDataFromDatabase(){
        Vector temp_V = new Vector();
        int total = 0;
        int sumStock = 0;
        int id = 1;
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            for(String s: sql){
                result = stat.executeQuery(s);
                if(result.first()){
                    do{
                        int sumPrice = result.getInt("stock_qty") * result.getInt("unit_price");
                        Vector re_DataV = new Vector();
                        re_DataV.addElement(String.valueOf(id));
                        re_DataV.addElement(result.getString("name"));
                        re_DataV.addElement(InputsManager.formatNairaTextField(String.valueOf(result.getInt("unit_price"))));
                        re_DataV.addElement(String.valueOf(result.getInt("stock_qty")));
                        re_DataV.addElement(InputsManager.formatNairaTextField(String.valueOf(sumPrice))); //the cost value
                        temp_V.addElement(re_DataV);
                        total += sumPrice;
                        sumStock += result.getInt("stock_qty");
                        id ++;
                    }
                    while(result.next());
                }
                else{
                    temp_V.addElement(new Vector());
                }
            }

                //append datas
                for(int i=0; i < 3; i++){
                   Vector re_DataV = new Vector();
                   if(i == 1){
                       re_DataV.addElement("TOTAL");
                       re_DataV.addElement(" ");
                       re_DataV.addElement(" ");
                       re_DataV.addElement(sumStock);
                       re_DataV.addElement(InputsManager.formatNairaTextField(String.valueOf(total)));
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
