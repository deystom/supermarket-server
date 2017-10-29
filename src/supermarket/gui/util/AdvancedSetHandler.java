/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui.util;
import supermarket.gui.AdminFrame;
import supermarket.database.ConfigureAllTables;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;

/**
 *
 * @author MUSTAFA
 */
public class AdvancedSetHandler {

    private Connection con;
    private Statement stat;
    private ResultSet result;

    private Vector<String> companyInfoV;
    private Vector<String> tables;
    private ConfigureAllTables config;

    private String currentStatus = "";
    private JDialog parent;
    private AdminFrame adminFrame;

    private ConfigModuleHandler conf;

    public AdvancedSetHandler(Connection c){
        con = c;

        //
        companyInfoV = new Vector<String>();
        config = new ConfigureAllTables(con);
        //conf = new ConfigModuleHandler(); //initializes the configuration file
    }

    public Vector<String> getCompanyInfo(){
        companyInfoV.clear(); //empty the vector
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            result = stat.executeQuery("SELECT * FROM reg_info");
            if(result.first()){
                do{
                    companyInfoV.addElement(result.getString("name")); //company name
                    companyInfoV.addElement(result.getString("address")); // location
                    companyInfoV.addElement(result.getString("tel")); //telephone
                    companyInfoV.addElement(result.getString("email")); //email
                }
                while(result.next());
            }
        }
        catch(SQLException sE){
            System.err.println("Error getting company info from database : " + sE.getMessage());
        }
        return companyInfoV;
    }

    public int getAutoTimeOut(){
        int timeOut = 0;
        conf = new ConfigModuleHandler();
        timeOut = Integer.parseInt(conf.read(2));
        /*
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            result = stat.executeQuery("SELECT seconds FROM auto_logout");
            if(result.first()){
                timeOut = result.getInt("seconds");
            }
        }
        catch(SQLException sE){
            System.err.println("Error at loading data from AutoLogoutHandler : " + sE.getMessage());
        }
         *
         */
        return timeOut;
    }

    public String getAdminPassword(){
        String pwd = "";
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            result = stat.executeQuery("SELECT * FROM admin WHERE user='Administrator'");
            if(result.absolute(1)){
                pwd = result.getString("password");
            }
            else{
                System.out.println("NO RESULT");
            }
        }
        catch(SQLException sE){
            System.err.println("Error getting admin password from database : " + sE.getMessage());
        }
        return pwd;
    }

    public int updateCompanyInfo(Vector<String> d){
        Vector <String>newComInfo = d;
        int upDate = 0;
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            upDate = stat.executeUpdate("UPDATE reg_info SET name='" + newComInfo.elementAt(0) + "', address='" + newComInfo.elementAt(1)
                    + "', tel='" + newComInfo.elementAt(2) + "', email='" + newComInfo.elementAt(3) + "'");
        }
        catch(SQLException sE){
            System.err.println("Error occured while updating company info : " + sE.getMessage());
        }
        return upDate;
    }

    public int updatePassword(String p){
        int upDate = 0;
        try{
           stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
           upDate = stat.executeUpdate("UPDATE admin SET password='" + p + "'");
        }
        catch(SQLException sE){
            System.err.println("Error occured while updating password info : " + sE.getMessage());
        }
        return upDate;
    }

    public int updateTimeOut(int t){
        int upDate = t * 60;
        conf = new ConfigModuleHandler();
        conf.write(String.valueOf(upDate), 2);
        /*
        try{
           stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
           upDate = stat.executeUpdate("UPDATE auto_logout SET seconds=" + (t * 60) + ""); //multiply time passed in by 60seconds
        }
        catch(SQLException sE){
            System.err.println("Error occured while updating password info : " + sE.getMessage());
        }
         *
         */
        return upDate;
    }

    public void formatDb(JDialog p, AdminFrame ad){
        parent = p;
        adminFrame = ad;
        new AdvancedSetHandler.BackgroundWork().execute();

    }

    public String getCurrentStatus(){
        return currentStatus;
    }

    /*
    public void doMe(){
        try{
           stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
           stat.executeUpdate("INSERT INTO auto_logout (seconds) VALUES (" + 120 + ")");
        }
        catch(SQLException sE){
            System.err.println("Error occured while updating password info : " + sE.getMessage());
        }
    }
     * 
     */
    private class BackgroundWork extends SwingWorker<Void, Void>{
        BackgroundWork(){
            tables = config.getAllTables();
        }

        public Void doInBackground(){
            doTruncates();
            return null;
        }

        @Override
        public void done(){
            new AdvancedSetHandler.BackgroundWork_2().execute();
        }

        private void doTruncates(){
            int upDate = 0;
            try{
                for(int i = 0; i < tables.size(); i++){
                    currentStatus = "Removing TABLE " + (i + 1) + "-- Of --" + tables.size() + " ...............";
                    try{
                        Thread.sleep(4000);
                    }
                    catch(InterruptedException inE){
                        System.err.println(inE.getMessage());
                    }
                    stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    stat.execute("TRUNCATE TABLE " + tables.elementAt(i));

                        currentStatus = "Successfully Removed TABLE " + (i + 1) + "-- Of --" + tables.size() +
                                "  From the DB ............";
                        try{
                            Thread.sleep(4000);
                        }
                        catch(InterruptedException inE){
                            System.err.println(inE.getMessage());
                        }
                        
                }
            }
            catch(SQLException sE){
                System.err.println("Error at doing TRUNCATES " + sE.getMessage());
            }

        }
    }

    private class BackgroundWork_2 extends SwingWorker<Void, Void>{
        BackgroundWork_2(){
            //tables = config.getAllTables();
        }

        public Void doInBackground(){
            dropDatabase();
            return null;
        }

        @Override
        public void done(){
            parent.dispose();
            adminFrame.dispose();
            adminFrame.getStartup().deActivator(20);
            adminFrame.getStartup().setVisible(true);
        }

        private void dropDatabase(){
            try{
                currentStatus = "Trying to DROP SUPER MARKET DATABASE ..............";
                try{
                    Thread.sleep(2000);
                    stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    stat.execute("DROP DATABASE super_market");
                    Thread.sleep(3000);
                }
                catch(InterruptedException inE){
                    System.err.println(inE.getMessage());
                }
            }
            catch(SQLException sE){
                System.err.println("Error Dropping database " + sE.getMessage());
            }
            finally{
                currentStatus = "***** SUCCESSFULLY Removed SUPER MARKET DATABASE *****";
                try{
                    Thread.sleep(5000);
                }
                catch(InterruptedException inE){
                    System.err.println(inE.getMessage());
                }
                currentStatus = "##### Preparing to SHUT DOWN Application #####";
                try{
                    Thread.sleep(3000);
                }
                catch(InterruptedException inE){
                    System.err.println(inE.getMessage());
                }
            }

        }
    }

}
