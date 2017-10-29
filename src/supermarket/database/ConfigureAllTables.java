/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.database;
import java.sql.*;
import java.util.Vector;

/**
 *
 * @author Segun
 */
public class ConfigureAllTables extends Thread{

    private Connection con = null;
    private Statement stat = null;
    private ResultSet result;
    private String []sqlStrings;
    private String []tableStrings;

    public ConfigureAllTables(Connection c){
          con = c;
          sqlStrings = new String[13];
          tableStrings = new String[10];
          loadTableNames();//load my table names that need to check which one that isnt created
          loadSQLconfig();//load all my QUERIES
          /////////////////////////////////////////////////////////////////////////////
            try{
                stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY); 
            }
            catch(SQLException e){
                System.err.println("Error happened when trying to make statement out of the connection : " + e.getMessage());
            }
         //////////////////////////////////////////////////////////////////////////////

    }

    @Override
    public void run(){
        new javax.swing.SwingWorker<Void, Void>(){
            public Void doInBackground(){
                startConfiguration();
                return null;
            }
        }.execute();
    }
    
    private void startConfiguration(){
                  try{
                      //stat.executeUpdate("CREATE USER 'sup_remote'@'%' IDENTIFIED BY 'alert01'");
                      //stat.executeUpdate("GRANT ALL PRIVILEGES ON *.* TO 'sup_remote'@'%' WITH GRANT OPTION");
                      for(int i = 0; i < tableStrings.length; i++){
                          result = stat.executeQuery("SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '" + tableStrings[i] + "'");
                          if(!result.absolute(1)){
                              //that means the table doesnt exist... so, create it
                              int x = stat.executeUpdate(sqlStrings[i]);
                              if(i == 0){
                                  x = stat.executeUpdate(sqlStrings[10]);
                                  x = stat.executeUpdate(sqlStrings[11]);
                                  x = stat.executeUpdate(sqlStrings[12]);
                                  System.out.println("Admin's Account was successfully created");
                                  //shows is just created.. so, insert admin values
                              }
                              System.out.printf("UPDATE STATUS OF SQL (%s) is : " + x + "\n", sqlStrings[i]);
                          }
                          else{}//if tables already exists.. dont do anythings
                      }
                  }
                  catch(SQLException e){
                      System.err.println("Error with checking if a table exists in the database : " + e.getMessage());
                  }
    }


    private void loadSQLconfig(){
        sqlStrings[0] = "CREATE TABLE admin (user VARCHAR( 225 ) NOT NULL , password VARCHAR( 225 ) NOT NULL) ENGINE = InnoDB";
        sqlStrings[1] = "CREATE TABLE users ( id INT NOT NULL AUTO_INCREMENT , name VARCHAR( 225 ) NOT NULL , " +
                "password VARCHAR( 225 ) NOT NULL , stat INT NOT NULL DEFAULT 0, PRIMARY KEY ( id ) , UNIQUE ( name ) ) ENGINE = InnoDB";
        sqlStrings[2] = "CREATE TABLE online_users ( id INT NOT NULL AUTO_INCREMENT , name VARCHAR( 225 ) NOT NULL " +
                ", time_stamp TIME NOT NULL , PRIMARY KEY ( id ) ) ENGINE = InnoDB ";
        sqlStrings[3] = "CREATE TABLE online_sys ( id INT NOT NULL AUTO_INCREMENT , sys_name VARCHAR( 225 ) NOT NULL " +
                ", user_logged VARCHAR( 225 ) NOT NULL , PRIMARY KEY ( id ) ) ENGINE = InnoDB ";
        sqlStrings[4] = "CREATE TABLE reg_info ( id INT NOT NULL AUTO_INCREMENT , name VARCHAR( 225 ) NOT NULL , address VARCHAR( 225 ) NOT NULL , " +
                "tel VARCHAR( 225 ) NOT NULL , email VARCHAR( 225 ) NOT NULL , license INT( 1 ) NOT NULL DEFAULT 0, PRIMARY KEY ( id ) ) ENGINE = InnoDB";
        sqlStrings[5] = "CREATE TABLE comodities_unit ( id INT NOT NULL AUTO_INCREMENT , name VARCHAR( 225 ) NOT NULL , stock_qty INT( 10 ) NOT NULL ," +
                " unit_price INT( 10 ) NOT NULL , PRIMARY KEY ( id ) ,UNIQUE (name) ) ENGINE = InnoDB ";
        sqlStrings[6] = "CREATE TABLE comodities_bulk ( id INT NOT NULL AUTO_INCREMENT , name VARCHAR( 225 ) NOT NULL , stock_qty INT( 10 ) NOT NULL ," +
                " unit_price INT( 10 ) NOT NULL , PRIMARY KEY ( id ) ,UNIQUE (name) ) ENGINE = InnoDB ";
        sqlStrings[7] = "CREATE TABLE sales (id INT NOT NULL AUTO_INCREMENT , com_name VARCHAR( 225 ) NOT NULL , qty_purchased INT( 10 ) NOT NULL , " +
                "total INT( 20 ) NOT NULL , date VARCHAR( 225 ) NOT NULL , user VARCHAR( 225 ) NOT NULL , reciept INT(35) NOT NULL , PRIMARY KEY ( id ) ) ENGINE = InnoDB";
        sqlStrings[8] = "CREATE TABLE items_const_users (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY , item_name VARCHAR( 225 ) NOT NULL , " +
                "user VARCHAR( 225 ) NOT NULL) ENGINE = InnoDB";
        sqlStrings[9] = "CREATE TABLE items_const_stock (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY , name VARCHAR( 225 ) NOT NULL , " +
                "min_stock INT( 25 ) NOT NULL , show_status INT( 1 ) NOT NULL DEFAULT 0) ENGINE = InnoDB";
        //sqlStrings[9] = "CREATE TABLE auto_logout (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY , " +
                //"seconds VARCHAR( 225 ) NOT NULL DEFAULT 0) ENGINE = InnoDB";
        sqlStrings[10] = "INSERT INTO admin (user, password) VALUES ('Administrator', 'admin')";
        //sqlStrings[10] = "INSERT INTO auto_logout (seconds) VALUES (120)";
        sqlStrings[11] = "CREATE USER 'sup_remote'@'%' IDENTIFIED BY 'alert01'";
        sqlStrings[12] = "GRANT ALL PRIVILEGES ON *.* TO 'sup_remote'@'%' WITH GRANT OPTION";

    }

    private void loadTableNames(){
        tableStrings[0] = "admin";
        tableStrings[1] = "users";
        tableStrings[2] = "online_users";
        tableStrings[3] = "online_sys";
        tableStrings[4] = "reg_info";
        tableStrings[5] = "comodities_unit";
        tableStrings[6] = "comodities_bulk";
        tableStrings[7] = "sales";
        tableStrings[8] = "items_const_users";
        tableStrings[9] = "items_const_stock";
        //tableStrings[9] = "auto_logout";
    }

    public Vector<String> getAllTables(){
        Vector <String>allTables = new Vector<String>();
        for(int i = 0; i < tableStrings.length; i++){
            allTables.addElement(tableStrings[i]);
        }
        return allTables;

    }
    

    /*
    public static void main(String []args){
        PublicMXJdataEmbedded d = new PublicMXJdataEmbedded();
        d.start_andConnectDatabase();
        ConfigureAllTables c = new ConfigureAllTables(d.getConnection());
        c.startConfiguration();
        try{
            Thread.sleep(10000);
        }catch(InterruptedException e){}
        d.close_andShutDownDatabase();
    }

     * 
     */
}
