/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui;
import supermarket.gui.util.*;
import supermarket.rmi.ServerRMIimpl;
import supermarket.gui.dialogs.*;
import supermarket.database.util.SearchReciept;
import supermarket.network.HandleNetwork;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.rmi.RemoteException;
import java.util.Collections;

/**
 *
 * @author Segun
 */
public class AdminFrame extends JFrame implements WindowListener{

    private AdminFrame me = this;
    /////////
    private Connection con = null;
    /////////
    private HandleNetwork network;
    private Timer networkTime;

    private StartUpFrame start;
    private Dimension userDimension;
    private JPanel topPanel;
    private JPanel dynamicPanel; //this particular panel changes dynamically
    private JPanel wrapperPanel; //the outter panel to wrap every other panel to be displayed in the JFrame
    private JPanel downPanel;
    private JLabel conStatusLbl;
    private Thread panelChangerThread; //thread that manages the panel changes
    private Timer panelChangeTimer;//timer for the change of panels
    private PanelManager manager;

    private GridBagLayout gbLayout;
    private GridBagConstraints gbConst;

    private JMenuBar myMenu;
    private JMenu fileM;
    private JMenu configM;
    private JMenu viewM;
    private JMenu backUp;
    private JMenu helpM;
    private JTextField searchBox;
    private JMenuItem addUserItem;
    private JMenuItem remUserItem;
    private JMenuItem remoteSetItem;
    private JMenuItem exitItem;
    private JMenuItem adminSetItem;
    //private JMenuItem cartSetItem;
    //private JMenuItem userItem;
    private JMenuItem updateItem;
    private JMenuItem aboutItem;
    private JMenuItem accSumItem;
    private JMenuItem stockViewItem;
    private JMenuItem backDbItem;
    private JMenuItem restoreDbItem;


    private java.util.List<JComponent> menuItemsV;
    //////
    //my rmi code that instantiate it
    //////
    private ServerRMIimpl rmi;
    
    private int moduleInUse = 0;
    private static String version = "";
    
    private SearchReciept reciept;

    public AdminFrame(){
        super();
        userDimension = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(new Dimension(940, 520));
        setMinimumSize(new Dimension(940, 520));
        setLocation((userDimension.width - 940) / 2, (userDimension.height - 520) / 2);
        //setAlwaysOnTop(true);
        menuItemsV = new ArrayList<JComponent>();
        network = new HandleNetwork();
        networkTime = new Timer(600, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                network.execute();
                //System.out.println(network.getHostName());
                if(network.isDone()){
                    if(networkTime.getDelay() == 600){
                        networkTime.setDelay(15000); //set a new timer delay after first loaded
                        networkTime.restart();
                    }
                    else{
                        //leave it this way | means its already repeating itself every 15 seconds
                    }
                    setConnectionLabel();
                    repaint();
                    validate();
                }
            }
        });
        if(!networkTime.isRunning()){
            networkTime.start();
        }

        manager = new PanelManager();//initialize the panel manager class here
        manager.passFrameInstance(me);
        //

        MyLookAndFeel.setLook();
        //
        initComponents();
        manager.passMenuItemsVector(menuItemsV); //pass the menu item to the panel manager to manage
        loadActions();
        setContentPane(wrapperPanel);
        //addMouseListener(this); //add the mouse listener to this frame

        /*
         * start rmi work beneath
         */
        new AdminFrame.BackgroundWork().execute(); //execute
    }

    public PanelManager getPanelManagerInstance(){
        return manager;
    }

    public int getModuleInUse(){
        return moduleInUse;
    }

    public void passModuleValue(int v){
        moduleInUse = v;
    }

    public void passVersionDetails(String v){
        version = v;
        setTitle("E-SUPER MARKET UNIVERSAL SOLUTION V " + version + " || *****NOT FOR SALE");
    }

    public String getVersionDetails(){
        return version;
    }

    public void changePanel(int i){
        //other class have access to change panel by invoking this method through AdminFrame class
        manager.reCreateNewInstanceOfPanels(); //create another instance of the frames in PanelsManager ...
        //something that works like a refresh of the panels to get new instances initialized
        manager.getPreviousPanel().setVisible(false);
        try{
            Thread.sleep(900);
            manager.setPanelIndex(i);
        }
        catch(InterruptedException iE){

        }
        finally{
            //manager.setSmthHappened(true);
            repaint();
            validate();
        }
    }

    private void initComponents(){
        gbLayout = new GridBagLayout();
        gbConst = new GridBagConstraints();
        myMenu = new JMenuBar();
        fileM = new JMenu("File");
        viewM = new JMenu("View");
        configM = new JMenu("Configuration");
        backUp = new JMenu("Back Up");
        helpM = new JMenu("Help");
        searchBox = new JTextField("Query for a Sales Reciept");

        addUserItem = new JMenuItem("Add Remote User");
        remUserItem = new JMenuItem("Remove Remote User");
        remoteSetItem = new JMenuItem("Remote Settings");
        exitItem = new JMenuItem("Exit");
        //cartSetItem = new JMenuItem("Catalog Settings");
        //userItem = new JMenuItem("Remote Users PRIV Settings");
        adminSetItem = new JMenuItem("Administrator Settings");
        updateItem = new JMenuItem("Run Live Update");
        aboutItem = new JMenuItem("About");
        accSumItem = new JMenuItem("Account Summary");
        stockViewItem = new JMenuItem("Stock Summary");
        backDbItem = new JMenuItem("Back Database up");
        restoreDbItem = new JMenuItem("Restore Data");

        menuItemsV.add(addUserItem);
        menuItemsV.add(remUserItem);
        //menuItemsV.add(cartSetItem);
        menuItemsV.add(remoteSetItem);
        //menuItemsV.addElement(userItem);
        menuItemsV.add(adminSetItem);
        menuItemsV.add(accSumItem);
        menuItemsV.add(stockViewItem);
        menuItemsV.add(backDbItem);
        menuItemsV.add(restoreDbItem);
        menuItemsV.add(searchBox);
        //load them as disabled as default

        for(JComponent it : menuItemsV){
            it.setEnabled(false);
        }

        fileM.setFont(new Font("Verdana", 0, 10));
        fileM.setForeground(new Color(0, 0, 255));
        viewM.setFont(new Font("Verdana", 0, 10));
        viewM.setForeground(new Color(0, 0, 255));
        configM.setFont(new Font("Verdana", 0, 10));
        configM.setForeground(new Color(0, 0, 255));
        backUp.setFont(new Font("Verdana", 0, 10));
        backUp.setForeground(new Color(0, 0, 255));
        helpM.setFont(new Font("Verdana", 0, 10));
        helpM.setForeground(new Color(0, 0, 255));
        searchBox.setFont(new Font("Verdana", 0, 10));
        searchBox.setForeground(new Color(0, 0, 255));
        searchBox.setToolTipText("Enter a valid Reciept u want to search for");
        //searchBox.setPreferredSize(new Dimension(200, 30));
        addUserItem.setFont(new Font("Verdana", 0, 10));
        addUserItem.setForeground(new Color(0, 0, 255));
        remUserItem.setFont(new Font("Verdana", 0, 10));
        remUserItem.setForeground(new Color(0, 0, 255));
        remoteSetItem.setFont(new Font("Verdana", 0, 10));
        remoteSetItem.setForeground(new Color(0, 0, 255));
        exitItem.setFont(new Font("Verdana", 0, 10));
        exitItem.setForeground(new Color(0, 0, 255));
        //cartSetItem.setFont(new Font("Verdana", 0, 10));
        //cartSetItem.setForeground(new Color(0, 0, 255));
        //userItem.setFont(new Font("Verdana", 0, 10));
        //userItem.setForeground(new Color(0, 0, 255));
        adminSetItem.setFont(new Font("Verdana", 0, 10));
        adminSetItem.setForeground(new Color(0, 0, 255));
        updateItem.setFont(new Font("Verdana", 0, 10));
        updateItem.setForeground(new Color(0, 0, 255));
        aboutItem.setFont(new Font("Verdana", 0, 10));
        aboutItem.setForeground(new Color(0, 0, 255));
        stockViewItem.setFont(new Font("Verdana", 0, 10));
        stockViewItem.setForeground(new Color(0, 0, 255));
        backDbItem.setFont(new Font("Verdana", 0, 10));
        backDbItem.setForeground(new Color(0, 0, 255));
        restoreDbItem.setFont(new Font("Verdana", 0, 10));
        restoreDbItem.setForeground(new Color(0, 0, 255));
        accSumItem.setFont(new Font("Verdana", 0, 10));
        accSumItem.setForeground(new Color(0, 0, 255));
        
        fileM.add(addUserItem);
        fileM.addSeparator();
        fileM.add(remUserItem);
        fileM.addSeparator();
        fileM.add(remoteSetItem);
        fileM.addSeparator();
        fileM.add(exitItem);

        viewM.add(stockViewItem);
        viewM.addSeparator();
        viewM.add(accSumItem);
        viewM.addSeparator();
        viewM.add(searchBox);

        //configM.add(cartSetItem);
        //configM.addSeparator();
        //configM.add(userItem);
        //configM.addSeparator();
        configM.add(adminSetItem);

        backUp.add(backDbItem);
        backUp.addSeparator();
        backUp.add(restoreDbItem);

        helpM.add(updateItem);
        helpM.addSeparator();
        helpM.add(aboutItem);

        myMenu.add(fileM);
        myMenu.add(viewM);
        myMenu.add(configM);
        myMenu.add(backUp);
        myMenu.add(helpM);

        setJMenuBar(myMenu);


        conStatusLbl = new JLabel();
        setConnectionLabel();

        conStatusLbl.setHorizontalAlignment(SwingConstants.CENTER);
        conStatusLbl.setFont(new Font("Verdana", 0, 14));

        topPanel = new JPanel();
        topPanel.setLayout(gbLayout);
        topPanel.setBackground(new Color(119, 152, 255));
        downPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        //downPanel.setPreferredSize(manager.getCurrentDimesion());
        downPanel.setBorder(BorderFactory.createLineBorder(new Color(134, 206, 134), 1));
        downPanel.setBackground(Color.WHITE);
        downPanel.add(conStatusLbl);
        
        wrapperPanel = new JPanel();
        wrapperPanel.setPreferredSize(getSize());
        wrapperPanel.setBorder(BorderFactory.createLineBorder(new Color(134, 206, 134), 3));
        wrapperPanel.setLayout(new BorderLayout(2, 2));

        gbConst.fill = GridBagConstraints.CENTER;
        gbConst.ipady = 0;
        gbConst.weightx = 0.0;
        gbConst.gridwidth = 0; //912;
        gbConst.gridheight = 0; //430;
        //gbConst.anchor = GridBagConstraints.CENTER;
        gbConst.gridx = 0;
        gbConst.gridy = 0;

        dynamicPanel = manager.getCurrentPanelToDisplay();
        dynamicPanel.setBackground(new Color(201, 196, 200));
        panelM(); // this private function manages the dynamic panel swapping
        topPanel.add(dynamicPanel, gbConst);

        wrapperPanel.add(topPanel);
        wrapperPanel.add(downPanel, BorderLayout.SOUTH);
        pack();

    }

    private void panelM(){ //admin frame panel manager function
        panelChangerThread = new Thread(new Runnable(){
            public void run(){
                panelChangeTimer = new Timer(1080, new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        //try to get the current panel to display every 1,480 millin seconds                        
                        SwingUtilities.invokeLater(new Runnable(){
                            public void run(){
                                dynamicPanel = manager.getCurrentPanelToDisplay();
                                if(manager.isSmthHappened()){
                                    topPanel.add(dynamicPanel, gbConst); //re-add the dynamic panel to the top panel
                                    //if something has happened means the panel has changed and user has logged in
                                    //logoutHandler = new AutoLogoutHandler(me, con);
                                    //new AdminFrame.BackgroundWork().execute();
                                    repaint();
                                    validate();
                                    manager.setSmthHappened(false);
                                }
                            }
                        });
                    }
                });
                if(!panelChangeTimer.isRunning()){
                    panelChangeTimer.start();
                }
            }
        });
        if(!panelChangerThread.isAlive()){
            panelChangerThread.start();
            //System.out.println("I have started thread here");
        }
    }

    public StartUpFrame getStartup(){
        return start;
    }
    
    private void loadActions(){
        exitItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try{
                    con.close();
                    System.out.println("Connection to database closed appropriately");
                }
                catch(SQLException sE){
                    System.err.println("Error occured when trying to close connection to the Database : " + sE.getMessage());
                }
                me.dispose();
                start.deActivator(0);
                start.setVisible(true);
            }
        });

        addUserItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        new AddUserDialog(me, con);
                    }
                });
            }
        });

        remUserItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        new RemoveUserDialog(me, con);
                    }
                });
            }
        });

        remoteSetItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        JOptionPane.showMessageDialog(me, "- YOUR CURRENT PRODUCT VERSION DOESNT SUPPORT THIS FUNCTIONALITY, CHECK FOR UPDATE OR UPGRADE - ",
                                "Sorry, PLEASE -- SUPPORT : softwares@technoglobalprogrammers.net, 08025481373", JOptionPane.PLAIN_MESSAGE);
                    }
                });
            }
        });

        stockViewItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        new StockSummaryDialog(me, con);
                    }
                });
            }
        });


        accSumItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        new AccountSummaryDialog(me, con);
                    }
                });
            }
        });

        updateItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        WelcomePanel.DbRegistration data = manager.getRegisteredInfoInst();
                        new supermarket.utility.UpdateManager(me, data.getRegName(), data.getRegEmail(), data.getRegTelephone(),
                                data.getRegAddress(), version);
                    }
                });
            }
        });


        adminSetItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        new AdminSettingsDialog(me, con);
                    }
                });
            }
        });

        backDbItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        JOptionPane.showMessageDialog(me, "- YOUR CURRENT PRODUCT VERSION DOESNT SUPPORT THIS FUNCTIONALITY, CHECK FOR UPDATE OR UPGRADE - ",
                                "Sorry, PLEASE -- SUPPORT : softwares@technoglobalprogrammers.net, 08025481373", JOptionPane.PLAIN_MESSAGE);
                    }
                });
            }
        });

        restoreDbItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        JOptionPane.showMessageDialog(me, "- YOUR CURRENT PRODUCT VERSION DOESNT SUPPORT THIS FUNCTIONALITY, CHECK FOR UPDATE OR UPGRADE - ",
                                "Sorry, PLEASE -- SUPPORT : softwares@technoglobalprogrammers.net, 08025481373", JOptionPane.PLAIN_MESSAGE);
                    }
                });
            }
        });
/*
        remoteSetItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        JOptionPane.showMessageDialog(me, "- YOUR CURRENT PRODUCT VERSION DOESNT SUPPORT THIS FUNCTIONALITY, CHECK FOR UPDATE OR UPGRADE - ",
                                "Sorry, PLEASE -- SUPPORT : mustafa@technoglobalprogrammers.net, 08025481373", JOptionPane.PLAIN_MESSAGE);
                    }
                });
            }
        });
         * 
         */

        aboutItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        JOptionPane.showMessageDialog(me,
                                "<html><div style='line-height:80%;text-align:center;color:blue;font-family:tahoma,san-serif'><h4 style='color:black'>DEPLOYED BY</h4>" +
                                "<span>TECHNO GLOBAL WORLD OF PROGRAMMERS</span> <h4 style='color:black'>PROGRAMMERS</h4>" +
                                "<span>Mustafa Segun Azeez (08025481373, 07051830762)</span><h4 style='color:black'>FEEDBACK &amp; SUPPORT</h4>" +
                                "<span>softwares@technoglobalprogrammers.net</span><br /><span style='color:white'>&copy 2010-2011</span></div><html>",
                                "E-SUPER MARKET UNIVERSAL SOLUTION", JOptionPane.PLAIN_MESSAGE);
                    }
                });
            }
        });
        
        searchBox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                searchBox.setText("");
            }
        });
        addWindowListener(this); //add window listener to this frame
    }

    private void setConnectionLabel(){
        if(!network.getConnectionStatus()){
            conStatusLbl.setText(String.format("READY FOR CONNECTIONS ON ADDRESS : ( %s ) ------ SYSTEM NAME : ( %s )", network.getHostIp(),
                network.getHostName()));
            conStatusLbl.setForeground(new Color(103, 105, 255));
        }
        else{
            conStatusLbl.setText("NO NETWORK CONNECTION DISCOVERED ------- REMOTE ACCESS MIGHT BE IMPOSSIBLE");
            conStatusLbl.setForeground(Color.RED);
        }
    }

    public void passDatabaseConnectionReference(Connection c){
        con = c;
        manager.passConnectionToPanelManager(con);
        reciept = new SearchReciept(con);
    }

    public java.util.List<JComponent> getMenuItemsVector(){
        return Collections.unmodifiableList(menuItemsV);
    }
    
    public void putStartInstance(StartUpFrame f){
        start = f;
    } //get the instance of the start up frame here incase of shutting it down

    public void windowActivated(WindowEvent e){

    }
    public void windowClosing(WindowEvent e){
        //if window is closing .. shut the database down
        //also display the start dialog too to show status
        try{
            con.close();
            System.out.println("Connection to database closed appropriately");
        }
        catch(SQLException sE){
            System.err.println("Error occured when trying to close connection to the Database : " + sE.getMessage());
        }
        start.deActivator(0);
        start.setVisible(true);

    }
    public void windowClosed(WindowEvent e){

    }
    public void windowDeactivated(WindowEvent e){

    }
    public void windowDeiconified(WindowEvent e){

    }
    public void windowIconified(WindowEvent e){
        
    }
    public void windowOpened(WindowEvent e){
        
    }

    private class BackgroundWork extends SwingWorker<Void, Void>{
        private String host= "";

        BackgroundWork(){
        }

        public Void doInBackground(){
            try{
                host = network.getHostName();
                //System.out.println("My Host name on Linux is : " + host);
                if(host.equals("")){
                    host = "localhost";
                }
            }
            catch(NullPointerException e){
                host = "localhost";
            }
            //in anther thread initialize the rmi and start it
            try{
                rmi = new ServerRMIimpl(); //initialize the rmi here
                try{
                    Thread.sleep(10000);
                    rmi.setHost(host);
                    rmi.connectionBind();
                    System.out.println("Connected to RMI with : " + host);

                    //pass conection reference
                    /*
                    try{
                        if(con.isValid(1000)){
                            rmi.passConnection(con);
                            System.out.println("Connection is still valid ooo");
                        }
                        else{
                            System.out.println("Connection is still valid ooo");
                        }
                    }
                    catch(SQLException sE){
                        System.err.println("Error at checking valid SQL : " + sE.getMessage());
                    }
                     * 
                     */
                }
                catch(InterruptedException e){}
                finally{
                    new supermarket.utility.RemoteManager(manager.getOnlineUsersTable(), con);
                }
                System.out.println("RMI network started Successfully :");
            }
            catch(RemoteException e){
                System.err.println("Error at RMI : " + e.getMessage());
            }

            return null;
        }

        @Override
        public void done(){
        }
    }

}
