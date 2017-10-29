/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package supermarket.database.util;
import java.sql.*;
import javax.swing.*;

/**
 *
 * @author Segun
 */
public class SearchReciept {
    
    private Connection connection;
    private Statement statment;
    private ResultSet result;
    
    private boolean resultStat = false;
    
    public SearchReciept(Connection c){
        connection =  c;
    }
    
    private void init(){
        
    }
    
}
