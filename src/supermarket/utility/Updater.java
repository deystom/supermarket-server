package supermarket.utility;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;
import java.net.*;
import java.util.Vector;

public class Updater{

  private String emailAdd;
  private String name;
  private String address;
  private String telephone;
  private String version;
  //accepted the user's email address
  private JDialog dialog;
  private JPanel wrapper;
  private JButton btn;
        
  private GridBagLayout gridbag;
  private GridBagConstraints c;
      
  private Connection connection;
  private Vector<String> fNamesV;//vectors of file names to replace
  private Vector<String> fDirV; //vector of file derectory to place the file names gotten
          
  private Statement statement = null;
  private ResultSet result = null;
      
  private String serverVersion;
  private int accessStatus;

  private JFrame parent;
  //private UpdateDBA upDb;
  private int o = 0;

  public Updater(){

  }
  
  public Updater(JFrame p, String a, String n, String add, String tel, String v, int s){
    parent = p;
    emailAdd = a;
    name = n;
    address = add;
    telephone = tel;
    version = v;
    accessStatus = s;
    fNamesV = new Vector<String>();
    fDirV = new Vector<String>();
    gridbag = new GridBagLayout();
    c = new GridBagConstraints();
    c.fill = GridBagConstraints.CENTER;
    c.ipady = 0;
    c.weightx = 0.0;
    c.gridwidth = 0; //912;
    c.gridheight = 0; //430;
    //gbConst.anchor = GridBagConstraints.CENTER;
    c.gridx = 0;
    c.gridy = 0;
    TheGUI(); //init the GUI
  }


  public static void main(String []args){
    try{
      new Updater(null, args[0], args[1], args[2], args[3], args[4], Integer.parseInt(args[5]));
    }
    catch(ArrayIndexOutOfBoundsException aE){
      new Updater(null, "", "", "", "", "", 0);
    }
  }


  private void popUpManager(){
      if(parent != null){
          new SwingWorker<Void, Void>(){
              public Void doInBackground(){
                  new Timer(800, new ActionListener(){
                      public void actionPerformed(ActionEvent e){
                          try{
                              if(dialog.isVisible()){
                                  parent.setEnabled(false);
                              }
                              else{
                                  parent.setEnabled(true);
                                  return;
                              }
                          }
                          catch(NullPointerException nE){}
                      }
                  }).start();
                  return null;
              }
          }.execute();
      }
  }
  
  public void TheGUI(){
    dialog = new JDialog();
    wrapper = new JPanel(gridbag);
    btn = new JButton("Checking for update configurations");
    btn.setEnabled(false);
    dialog.setTitle("E-SUPER MARKET UNIVERSAL SOLUTION Update Task");
    dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    dialog.setSize(500, 100);
    dialog.setIconImage(new ImageIcon(getClass().getResource("download-icon.png")).getImage());
    wrapper.setPreferredSize(new Dimension(500, 100));
    wrapper.add(btn, c);
    dialog.setAlwaysOnTop(true);
    dialog.setResizable(false);
    dialog.setLocationRelativeTo(null);
    dialog.getContentPane().add(wrapper);
    dialog.addWindowListener(new WindowAdapter(){
      @Override
      public void windowClosing(WindowEvent w){
        try{
          connection.close();
          //upDb.close();
        }
        catch(SQLException sE){
          System.err.println(sE.getMessage());
        }
        catch(NullPointerException nE){}
        finally{
          dialog.dispose();
        }
      }
    });
    dialog.setVisible(true);
    popUpManager(); //then starts the pop up manager also

    try{
        if(accessStatus == 0){
            btn.setText("Trying to Connect to update server ...");
        }
        else{
            btn.setText("Re - Trying to Connect to update server ...");
        }
      Thread.sleep(900);
      new AbstractConnection().execute();
    }
    catch(InterruptedException iE){}
  }
  
    static{
        UIManager.put("textHighlightText", new Color(134, 206, 134));
        UIManager.put("textBackground", new Color(134, 206, 134));
        UIManager.put("Panel.background", new Color(134, 206, 134));
        UIManager.put("MenuBar.background", new Color(134, 206, 134));
        UIManager.put("MenuBar:Menu[Selected].backgroundPainter", new Color(134, 206, 134));
        UIManager.put("nimbusBase", new Color(134, 206, 134));
        UIManager.put("nimbusBlueGrey", new Color(134, 206, 134));
        UIManager.put("control", new Color(134, 206, 134));
        try{
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch(Exception e){
           System.err.println(e.getMessage());
        }
    }
    
    class ServerWork extends SwingWorker<Void, Void>{
      //this looks for user's credential
      public Void doInBackground(){
        return null;
      }
    }
    
    class UpdateFiles extends SwingWorker<Void, Void>{
      URL u;
      URLConnection urlCon;
      BufferedInputStream in;
      OutputStream out;
      File fOut;
      byte[] buf = new byte[1024];
      int len;
      
      public Void doInBackground(){
        btn.setText("Please wait for the Necessary update COMPONENTS to complete");
        btn.setEnabled(false);
        try{
          for(int x = 0; x < fNamesV.size(); x++){
            u = new URL("http://technoglobalprogrammers.net/updating/super_market/" + fNamesV.elementAt(x));
            fOut = new File(new File("").getAbsolutePath() + fDirV.elementAt(x) + fNamesV.elementAt(x));
            if(!new File(new File("").getAbsolutePath() + fDirV.elementAt(x)).isDirectory()){
                new File(new File("").getAbsolutePath() + fDirV.elementAt(x)).mkdir(); //make the file directory if it does not exist before
            }
            out = new FileOutputStream(fOut);
            urlCon = u.openConnection();
            in = new BufferedInputStream(urlCon.getInputStream());
            while ((len = in.read(buf)) > 0) {
              out.write(buf, 0, len);
            }
             in.close();
             out.close();
            btn.setText("UPDATING " + (x +1) + " OF " + fNamesV.size() + " Necessary update COMPONENTS");
          }
        }
        catch(Exception iO){
          System.err.println("Error with server folder opening " + iO.getMessage());
        }
        return null;
      }
      
      @Override
      public void done(){


        //when update is successful ... update the version details of this software db
          /*
            try{
              upDb =  new UpdateDBA();
              Statement st = upDb.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
              int w = st.executeUpdate("UPDATE user SET version='" + serverVersion + "'");
            }
            catch(SQLException sE){
              System.err.println(sE.getMessage());
            }
            finally{
              btn.setText("UPDATE IS COMPLETE ..... please close this dialog and relaunch APPLICATION");
              btn.setEnabled(false);
            }
           *
           */
      }
    }
    
    class AbstractConnection extends SwingWorker<Void, Void>{
        String driverName = "org.gjt.mm.mysql.Driver";
        String url = "jdbc:mysql://technoglobalprogrammers.net/techno_supermarket"; // a JDBC url
        String username = "techno_remote";
        String password = "alwaysalert2011";
      
        AbstractConnection(){
          try{
            Class.forName(driverName);
          }
            catch(ClassNotFoundException cE){
              System.exit(7);
            }
        }

        private void registerCredentials(){
            try{
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                int up = statement.executeUpdate("INSERT INTO users VALUES ('" + name + "', '" + emailAdd + "', '" +
                        address + "', '" + telephone + "', '" + version + "')");
            }
            catch(SQLException sE){
                //do nothing ... means this user don get him details online before
            }
        }

      public Void doInBackground(){
        try{
          connection = DriverManager.getConnection(url, username, password);
          //since connection was successfully made
          //then regiister this user's data in remote database
          registerCredentials(); //register user's credentials
        }
        catch(SQLException sE){
          btn.setText(" **** Error Connecting to UPDATE server");
        }
        return null;
      }
      @Override
      public void done(){
        try{
          if(connection.isValid(800)){
            btn.setText("SUCCESSFULLY Connected to UPDATE server ....");
            try{
              Thread.sleep(3000);
              //then load file names to update
              if(accessStatus == 0){
                btn.setText("Preloading update files and components ....");
              }
              else{
                btn.setText("Re-Preloading update files and components ....");
              }
              statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
              result = statement.executeQuery("SELECT * FROM update_files");
              while(result.next()){
                //while there is a next value in the result
                fNamesV.addElement(result.getString("name"));
                fDirV.addElement(result.getString("dir"));
              }
              Thread.sleep(2000);
            }
            catch(InterruptedException iE){}

            finally{
                if(accessStatus == 0){
                    btn.setText("Determining update ....");
                }
                else{
                    btn.setText("Re-Determining update ....");
                }
              //determine if the button shld be enabled or not
              if(fNamesV.size() > 0){
                result = statement.executeQuery("SELECT * FROM updates");
                while(result.next()){
                  serverVersion = result.getString("update");
                }
                if(version.equals(serverVersion)){
                    btn.setText("Your Program is UP-TO-DATE");
                    btn.setEnabled(false);
                }
                else{
                    //get it set to update
                    if(accessStatus == 0){
                        btn.setText("Available Update is Version : " + serverVersion + "");
                        btn.setEnabled(true);
                        btn.addActionListener(new ActionListener(){
                          public void actionPerformed(ActionEvent e){
                              o = JOptionPane.showConfirmDialog(wrapper, "Main Application will Exit to perform UPDATE", "ALERT !",
                                      JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                              if(o == 0){
                                  //then the user clicked ok niyen
                                try{
                                    String pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
                                    String x[] = pid.split("@");
                                    parent.dispose();
                                    //parent = null;
                                    dialog.dispose();
                                    System.gc();
                                    //System.out.println(x[0]);
                                    Thread.sleep(1000);
                                    String n = name.replace(" ", "-");
                                    String a = address.replace(" ", "-");
                                    String exe = "java -jar Updater.jar null " + emailAdd + " " + n + " " +
                                             a + " " + telephone + " " + version + " " +  1;
                                    //String exe = "java -jar Updater.jar null ";
                                    //System.out.println(exe);
                                    Runtime.getRuntime().exec(exe);
                                    Thread.sleep(1000);
                                    Runtime.getRuntime().exec("cmd /c taskkill /F /IM " + x[0]);
                                }
                                catch(Exception ee){
                                    System.err.println(ee.getMessage());
                                }
                              }
                              else{
                                  //dispose this guy here
                                  //dialog.dispose();
                              }
                          }
                        });

                    }
                    else{
                        //if accessStatus is '1' means say the GUI don close be that
                        btn.setText("Update your application to Version : " + serverVersion + "");
                        btn.setEnabled(true);
                        btn.addActionListener(new ActionListener(){
                          public void actionPerformed(ActionEvent e){
                              new UpdateFiles().execute(); //then run the update
                          }
                        });
                    }
                }
              }
              else{
                btn.setText("Nothing to Update at this time");
              }
            }
          }
          else{
          }
        }
        catch(SQLException sE){
          System.err.println(sE.getMessage());
        }
        catch(NullPointerException sE){}
      }
    }

    /*
    class UpdateDBA{

    private String url = "jdbc:mysql-embedded/Database";
    private Properties props;
    private com.mysql.embedded.jdbc.MyConnection conn= null;

    public UpdateDBA(){
      
        try{
            DriverManager.registerDriver(new com.mysql.embedded.jdbc.MySqlEmbeddedDriver());
            //Class.forName("com.mysql.jdbc.Driver").newInstance();
        }
        catch(Exception sE){
            System.err.println("Error trying to register Driver : " + sE.getMessage());
        }
        
        props = new Properties();
        //initialize the properties            
        props.put("--datadir", "db\\data");
        props.put("--basedir", new File("").getAbsolutePath() + "\\db");
        props.put("--default-character-set", "utf8");
        props.put("--default-collation", "utf8_general_ci");
        props.put("library.path", new File("").getAbsolutePath() + "\\lib");

        connectDB();
    }

    public com.mysql.embedded.jdbc.MyConnection getConnection(){
        return conn;
    }

    private void connectDB(){
        try{
            conn = (com.mysql.embedded.jdbc.MyConnection) DriverManager.getConnection(url, props);
        }
        catch(SQLException sE){
            System.err.println("Error trying to get connection : " + sE.getMessage());
        }
    }

    public void close(){
      try{
        conn.close();
      }
      catch(SQLException sE){
        System.err.println(sE.getMessage());
      }
    }
    
  }
     * 
     */
}