/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui.util;
import supermarket.gui.AdminFrame;
import supermarket.gui.StartUpFrame;
import supermarket.utility.InputsManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 *
 * @author MUSTAFA
 */
public class AutoLogoutHandler extends JDialog{
    
    private JDialog self = this;
    private AdminFrame parent;
    private StartUpFrame start;
    private Connection con;
    private Statement stat;
    private ResultSet result;
    private PopDialogParentHandler popH;

    private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

    private Thread th;

    private JPanel outPanel;
    private JPanel topPanel;
    private JPanel downPanel;
    private JLabel topLbl;
    private JLabel imgLbl;
    private JPasswordField passTxt;
    private JButton loginBtn;
    private JButton exitBtn;

    private BackgroundWork backWork;
    private String pass = "";
    private String correctPwd = "";
    private int timeOut = 0;
    private int autoTime = 0;
    private int tick = 0;

    private ConfigModuleHandler conf;

    private int moduleInUse = 0;
    private Timer moduleT;

    public AutoLogoutHandler(AdminFrame p, Connection c){
        parent = p;
        con = c;

        setSize(300, 190);
        setLocation((d.width - 300) / 2, (d.height - 190) /2);
        setAlwaysOnTop(true);
        setUndecorated(true);
        ///////
        MyLookAndFeel.setLook();
        //////

        th = new Thread(new Runnable(){
            public void run(){
                try{
                    loadData();
                }
                catch(Exception e){
                    //means the frame was closed or whatever
                    //then stop the auto logout from apearing
                    stop();
                }
            }
        });
        backWork = new BackgroundWork();
        //then initialize the components
        initComponents();
        setContentPane(outPanel);
        loadActions();
        moduleT = new Timer(3000, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                moduleInUse = Integer.parseInt(conf.read(0));
            }
        });
        loadModule(); //load module
    }

    private void initComponents(){
        //initialize the startup frame
        start = parent.getStartup();
        outPanel = new JPanel(new BorderLayout(5, 2));
        topPanel = new JPanel(new BorderLayout(5, 2));
        downPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));

        topLbl = new JLabel("Automatically TIMED-OUT");
        imgLbl = new JLabel();
        passTxt = new JPasswordField();
        loginBtn = new JButton("Authenticate");
        exitBtn = new JButton("Exit Application");

        topLbl.setPreferredSize(new Dimension(300, 30));
        topLbl.setHorizontalAlignment(SwingConstants.CENTER);
        topLbl.setBackground(new Color(152, 213, 152));
        topLbl.setForeground(Color.RED);
        topLbl.setFont(new Font("Tahoma", Font.BOLD, 11));

        
        imgLbl.setIcon(new ImageIcon(getClass().getResource("icn_profile.gif")));
        imgLbl.setPreferredSize(new Dimension(70, 70));
        imgLbl.setHorizontalAlignment(SwingConstants.CENTER);
        imgLbl.setBackground(new Color(152, 213, 152));

        loginBtn.setPreferredSize(new Dimension(120, 30));
        loginBtn.setHorizontalAlignment(SwingConstants.CENTER);
        loginBtn.setBackground(new Color(65, 105, 225));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        exitBtn.setPreferredSize(new Dimension(120, 30));
        exitBtn.setHorizontalAlignment(SwingConstants.CENTER);
        exitBtn.setBackground(new Color(65, 105, 225));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        passTxt.setPreferredSize(new Dimension(250, 30));

        outPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        outPanel.setPreferredSize(new Dimension(300, 190));
        outPanel.setBackground(new Color(152, 213, 152));

        //topPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        topPanel.setPreferredSize(new Dimension(300, 105));
        topPanel.setBackground(new Color(152, 213, 152));

        //downPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        downPanel.setPreferredSize(new Dimension(300, 80));
        downPanel.setBackground(new Color(152, 213, 152));

        topPanel.add(topLbl, BorderLayout.NORTH);
        topPanel.add(imgLbl, BorderLayout.CENTER);

        downPanel.add(passTxt);
        downPanel.add(loginBtn);
        downPanel.add(exitBtn);

        outPanel.add(topPanel, BorderLayout.NORTH);
        outPanel.add(downPanel, BorderLayout.CENTER);
        //////////////////////////////
        /////////////////////////////
        if(!th.isAlive()){
            th.start();
        }
    }
    
    public void displayMe(){
        com.sun.awt.AWTUtilities.setWindowOpacity(parent, 0.5f);
        //show();
        ////////////////////////////////////////////////////////////
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                popH = new PopDialogParentHandler(parent, self);
            }
        });
        ///////////////////////////////////////////////////////////
    }

    public void hideMe(){
        com.sun.awt.AWTUtilities.setWindowOpacity(parent, 1f);
        popH.stopPopHandler();
        hide();
    }

    private void loadActions(){
        exitBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                parent.dispose();
                self.dispose();
                start.deActivator(0);
                start.setVisible(true);
            }
        });

        passTxt.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                validateCredential();
            }
        });

        loginBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                validateCredential();
            }
        });
    }

    private synchronized void loadModule(){
        new SwingWorker<Void, Void>(){
            public Void doInBackground(){
                moduleT.start();
                return null;
            }
        }.execute();
    }

    private void loadData() throws Exception{
        conf = new ConfigModuleHandler();
        try{
            timeOut = Integer.parseInt(conf.read(2));
        }
        catch(NumberFormatException E){
            System.err.println("The timeout thats suppose to be got returns : " + E.getMessage());
        }
        finally{
            parent.passModuleValue(moduleInUse); //pass in the module in use to the admin frame
        }

        //try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            //then get admin password
            result = stat.executeQuery("SELECT * FROM admin WHERE user='Administrator'");
            if(result.absolute(1)){
                correctPwd = result.getString("password");
            }
            else{
                System.out.println("NO RESULT");
            }
        //}
        //catch(SQLException sE){
            //System.err.println("Error at loading data from AutoLogoutHandler : " + sE.getMessage());
            //then stop the auto logout from apearing
            //stop();
        //}
    }

    public int getDelay(){
        try{
            loadData();
        }
        catch(Exception e){
            //then stop the auto logout from apearing
            stop();
        }
        return timeOut;
    }

    public void setTick(int t){
        tick = t;
    }

    public void start(){
        backWork.execute();
    }

    public void pause(){
        if(backWork.t.isRunning()){
            backWork.t.setRepeats(false); //set repeats to false
        }
    }

    public void resume(){
        if(!backWork.t.isRepeats()){
            backWork.t.setRepeats(true); //set repeats to false
            backWork.t.restart();
        }
    }

    public boolean isStarted(){
        return backWork.t.isRunning();
    }

    public void stop(){
        backWork.t.stop();
        //backWork.cancel(true);
        backWork = null;
    }

    private void validateCredential(){
        char []pwd = passTxt.getPassword();
        for(int i = 0; i < pwd.length; i++){
            pass += pwd[i];
        }
        InputsManager m = new InputsManager(pass);
        if(m.isGoodInput()){
            //if the crdential provided is valid characters ... then pass the data to the database
            if(pass.equals(correctPwd)){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        //the password is valie ... authenticate user
                        pass = "";
                        passTxt.setText("");
                        stop();
                        hideMe();
                        backWork = new BackgroundWork(); //initialize with another
                        tick = 0;
                        start();//start again
                        System.gc();
                    }
                });
            }
            else{
                //means tha character is valid but the password is not corerct
                pass = "";
                passTxt.setText("");
                JOptionPane.showMessageDialog(this, "- Invalid Password Provided -", "AUTHENTICATION ERROR", JOptionPane.PLAIN_MESSAGE);
            }
        }
        else{
            passTxt.setText("");
            pass = "";
           JOptionPane.showMessageDialog(this, "- Invalid Characters provided - \n Contact Service for VALID Character Combinations", "Characters ERROR", JOptionPane.PLAIN_MESSAGE);
        }
    }

    private class BackgroundWork extends SwingWorker<Void, Void>{
        protected Timer t;
        public BackgroundWork(){
            t = new Timer(1000, new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    autoTime = getDelay();
                    tick++;
                    //System.out.println("I count " + tick);
                    //start checking if tick is up to the auto logout
                    if(tick >= autoTime){
                        //show the autologouthandlerDialog
                        t.stop();
                        displayMe();
                    }
                }
            });
        }

        public Void doInBackground(){
            if(!t.isRunning()){
                t.start();
            }
            return null;
        }

        @Override
        public void done(){
            System.gc();
        }
    }
}
