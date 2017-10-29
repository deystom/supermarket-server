/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.database.util;
import java.sql.*;
import java.util.Vector;

/**
 *
 * @author Segun
 */
public class DataResources {
    
    private Connection con;
    private Statement stat;
    private ResultSet result;

    public DataResources(Connection c){
        con = c;
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        }
        catch(SQLException s){
            System.err.println("Error making statement : " + s.getMessage());
        }
    }

    public Vector<String> getUsers(){
        Vector<String> users = new Vector<String>();
        users.addElement("Select a User .....");
        try{
            result = stat.executeQuery("SELECT name FROM users");
            if(result.first()){
                do{
                    users.addElement(result.getString("name"));
                }
                while(result.next());
            }
        }
        catch(SQLException e){
            System.err.println("Error trying to get users from the table : " + e.getMessage());
        }
        return users;
    }

    public Vector<String> get_unitItems(){
        Vector<String> items = new Vector<String>();
        items.addElement("Select an Item .....");
        try{
            result = stat.executeQuery("SELECT name FROM comodities_unit");
            if(result.first()){
                do{
                    items.addElement(result.getString("name"));
                }
                while(result.next());
            }
        }
        catch(SQLException e){
            System.err.println("Error trying to get unit items from the table : " + e.getMessage());
        }
        return items;
    }

    public Vector<String> get_bulkItems(){
        Vector<String> items = new Vector<String>();
        items.addElement("Select an Item .....");
        try{
            result = stat.executeQuery("SELECT name FROM comodities_bulk");
            if(result.first()){
                do{
                    items.addElement(result.getString("name"));
                }
                while(result.next());
            }
        }
        catch(SQLException e){
            System.err.println("Error trying to get bulk items from the table : " + e.getMessage());
        }
        return items;
    }

    public Vector<String> getDates(){
        Vector<String> dates = new Vector<String>();
        dates.addElement("Select a Date .....");
        try{
            result = stat.executeQuery("SELECT DISTINCT date FROM sales");
            if(result.first()){
                do{
                    String a[] = result.getString("date").split(" # ");
                    if(!dates.contains(a[1])){
                        dates.addElement(a[1]);
                    }
                }
                while(result.next());
            }
        }
        catch(SQLException e){
            System.err.println("Error trying to get users from the table : " + e.getMessage());
        }
        return dates;
    }
}
