package supermarket.database;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import com.mysql.management.MysqldResource;
import com.mysql.management.MysqldResourceI;

public class PublicMXJdataEmbedded {
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String dbName = "super_market";
    private static final String user = "segsalerty";
    private static final String password = "alert01";
    private static final int port = Integer.parseInt(System.getProperty("c-mxj_super_market_port", "3336"));
    private static final String url = "jdbc:mysql://" + "localhost:" + port + "/" + dbName + "?createDatabaseIfNotExist=true";
    private MysqldResource mysqldResource;
    private File ourAppDir;
    private File databaseDir;
    private static Connection con;

    public PublicMXJdataEmbedded(){
        ourAppDir = new File(new File("").getAbsolutePath());
        databaseDir = new File(ourAppDir, "Techno Global");
        loadDriver();
        //startDatabase();
        //connectDatabase();
    }

    private void loadDriver(){
        try{
            Class.forName(DRIVER);
        }
        catch(ClassNotFoundException e){
            System.err.println("Driver cannot be loaded : " + e.getMessage());
        }
    }
    
    private void startDatabase(){
        mysqldResource = dbStarter(databaseDir, port, user, password);
        if (!mysqldResource.isRunning()) {
            throw new RuntimeException("MySQL did not start.");
        }
        else{
            System.out.println("MySQL is running.");
        }
    }

    private static void connectDatabase(){
        try{
            con = DriverManager.getConnection(url, user, password);
        }
        catch(SQLException e){
            System.err.println("Error connecting to DATABASE : " + e.getMessage());
        }
    }

    public static Connection getNewConnection(){
        connectDatabase();
        return con;
    }

    public void close_andShutDownDatabase(){
        try{
            con.close();
            mysqldResource.shutdown();
        }
        catch(Exception e){
            System.err.println("Error occured at closing and shutting down Database : " + e.getMessage());
        }
    }

    public void start_andConnectDatabase(){
        startDatabase();
        connectDatabase();
    }

    @SuppressWarnings("static-access")
    public void reStart_andConnectDatabase(PublicMXJdataEmbedded d){
        //this process will initialize another instance of this class and reference it to
        //the current and running class in action passed to its aguement
        d = new PublicMXJdataEmbedded();
        d.startDatabase();
        d.connectDatabase();
    }

    public Connection getConnection(){
        return con;
    }

    @SuppressWarnings("unchecked")
    private MysqldResource dbStarter(File dbDir, int port, String u, String p) {
        MysqldResource mysql = new MysqldResource(dbDir);

        Map database_options = new HashMap();
        database_options.put(MysqldResourceI.PORT, Integer.toString(port));
        database_options.put(MysqldResourceI.INITIALIZE_USER, "true");
        database_options.put(MysqldResourceI.INITIALIZE_USER_NAME, u);
        database_options.put(MysqldResourceI.INITIALIZE_PASSWORD, p);
        //database_options.put(MysqldResourceI.SOCKET, "/tmp/mysql.sock");

        mysql.start("super-market-mysqld-thread", database_options);
        return mysql;
    }
}
