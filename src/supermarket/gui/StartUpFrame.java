/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui;
import supermarket.database.*;
import supermarket.gui.util.MyLookAndFeel;
import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 *
 * @author Segun
 */
public class StartUpFrame extends JDialog{

    private StartUpFrame self = this;
    private Dimension userDimension;
    private JPanel inPanel;
    private JProgressBar bar;
    private JLabel statLbl;
    private Task task;
    private AdminFrame admin;
    private String status = "";
    private int opt = 0;
    private PublicMXJdataEmbedded db;
    private ConfigureAllTables tableConfig;
    //private String myHost = "";

    private static String version;
    private supermarket.gui.util.ConfigModuleHandler conf;

    public StartUpFrame(){        
        userDimension = Toolkit.getDefaultToolkit().getScreenSize();
        admin = new AdminFrame();
        setSize(300, 90);
        setPreferredSize(new Dimension(300, 90));
        setLocation((userDimension.width - 300) / 2, (userDimension.height - 90) / 2);
        //
        MyLookAndFeel.setLook();
        //
        setUndecorated(true);
        setAlwaysOnTop(true);
        initComponents();
        add(inPanel);
        db = new PublicMXJdataEmbedded(); //let start up frame initialize the embedded database instace for me here
        conf = new supermarket.gui.util.ConfigModuleHandler();
    }

    private void initComponents(){
        inPanel = new JPanel(new BorderLayout());
        statLbl = new JLabel(status);
        bar = new JProgressBar(0, 100);

        bar.setStringPainted(true);

        statLbl.setHorizontalAlignment(SwingConstants.CENTER);
        statLbl.setFont(new Font("Tahoma", 0, 12));

        inPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 185, 80), 1));
        
        inPanel.add(bar, BorderLayout.CENTER);
        inPanel.add(statLbl, BorderLayout.SOUTH);
    }

    public void initializeOption(){
        opt = 1;
        status = "Initializing necessary Components";
        statLbl.setText(status);
        task = new Task();
        task.execute();
    }

    public void deActivator(int x){
        if(x == 0){
            //if argument is 0.... shut the db down and close application
            opt = 2;
            status = "Deactivating running components in Use";
            statLbl.setText(status);
            task = new Task();
            task.execute();
        }
        else{
            opt = 10;
            status = "Refreshing & Applying changes";
            statLbl.setText(status);
            task = new Task();
            task.execute();
        }
    }
    /*
     * inner class that contains swing worker
     */

    private class Task extends SwingWorker<Void, Void>{
        private int prog = 0;
        private Thread t;

        Task(){
            t = new Thread(new Runnable(){
                @SuppressWarnings("static-access")
                public void run(){
                    switch(opt){
                        case 1:
                            db.start_andConnectDatabase();
                            tableConfig = new ConfigureAllTables(db.getConnection()); //load the class that manages tables configuration in database
                            tableConfig.start(); //start the tables configuration (NOTE) its gonna ahandle himself if to make changes to databse
                            break;
                        case 2:
                            db.close_andShutDownDatabase();
                            break;
                        default:
                            //shut down and then restart database
                            try{
                                db.close_andShutDownDatabase();
                                Thread.sleep(2000);
                                db.reStart_andConnectDatabase(db);
                                Thread.sleep(2000);
                                tableConfig = new ConfigureAllTables(db.getConnection());
                                tableConfig.start();
                                Thread.sleep(2000);
                            }
                            catch(InterruptedException iE){
                                System.err.println(iE.getMessage());
                            }
                            break;
                    }
                    try{
                        t.sleep(2800);
                    }
                    catch(InterruptedException e){}
                    finally{
                        
                    }
                }
            });
        }

        public Void doInBackground(){
            Random random = new Random();
            t.start();
            while(t.isAlive()){
                try{
                    Thread.sleep(random.nextInt(1000));
                }
                catch(InterruptedException e){}
                prog += random.nextInt(10);
                setProgress(Math.min(prog, 100));
                prog = getProgress();
                bar.setValue(getProgress());
                repaint();
                validate();
            }
            version = conf.read(4);
            return null;
        }

        @Override
        @SuppressWarnings("deprecation")
        public void done(){
            prog = 0;
            //Toolkit.getDefaultToolkit().beep();
             //dispose this Dialog
            admin.putStartInstance(self);
            if(opt == 1){
                admin.passDatabaseConnectionReference(db.getConnection());
                admin.passVersionDetails(version);
                dispose();
                admin.setVisible(true);
            }
            else if(opt == 2){
                System.exit(0);
            }
            else{
                //destroy the parent frame and relaunch it .. meaning the Application refreshed
                try{
                    //admin = null;
                    //admin = new AdminFrame();
                    admin.dispose();
                    Thread.sleep(2900);
                    admin.putStartInstance(self);
                    Thread.sleep(2900);
                    admin.passDatabaseConnectionReference(db.getConnection());
                    dispose();
                    admin.changePanel(0);
                    Thread.sleep(1900);
                    admin.setVisible(true);

                }
                catch(NullPointerException nE){
                    System.err.println("Null was thrown when setting ADMIN frame to null" + nE.getMessage());
                }
                catch(InterruptedException iE){
                    System.err.println("Sleeping Error thrown in new ADMIN frame" + iE.getMessage());
                }
                
            }
        }
    }

}
