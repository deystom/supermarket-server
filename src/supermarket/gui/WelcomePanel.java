/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui;
//import supermarket.database.PublicMXJdataEmbedded;
import supermarket.gui.util.*;
import supermarket.gui.dialogs.*;
import supermarket.utility.*;
import supermarket.tables.*;
import supermarket.database.util.DataResources;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Segun
 */
public class WelcomePanel extends JPanel implements MouseListener{

    private WelcomePanel me = this;
    private PanelManager manager;
    private int width, height;
    private SetUpPanel homePanel;
    ////////////////////////////////////////
    ///////////////////////////////////////
    private Statement stat;
    private ResultSet result;
    ///////////////////////////////////////
    /////////////////
    private static JTextField fields[];
    private JComboBox salesModuleCombo;
    /////////////////////////////
    private DbRegistration reg;
    private Connection con;
    private boolean regS = false;
    private Timer ti; //timer to determine the conponents to be on the left panel
    private JLabel topInfoLbl = new JLabel(); ///


    private JTable regUsersTable;
    private JTable userOnlineTable;
    private JTable unitCatTable;
    private JTable bulkCatTable;
    ///////////
    ////all my tables model here
    //////////
    private RegisteredUsers regUsersTM;
    private Catalogs catTM;
    private Catalogs catTM_bulk;
    //private OnlineUsers onlineTM;
    private Sales salesTM;
    ////
    //class that gets data resources
    ////
    private AdminFrame parent;

    private DataResources data;
    private List<JComponent> menuItemsV;
    private AutoLogoutHandler autoLogout;
    private boolean autoState = true; //intentionally initilized to true coz it manges the autoLogout start method

    /*
     * extended and modified functionality for the right side of this WELCOME PANEL below
     * using a JTabbedPane to put reg users and online users in a tab and the Items for sale in another tab
     * bulk and per unit sales in second tab which is detemined by the configured sales module in configuration file
     */
    private JTabbedPane usersPane;
    private JTabbedPane itemsPane;

    private ConfigModuleHandler moduleHandler;
    private SwingWorker<Void, Void> w;
    private int moduleInUse = 0;
    
    public WelcomePanel(PanelManager m, int a, int b){
        menuItemsV = new ArrayList<JComponent>();
        manager = m;
        width = a;
        height = b;
        setSize(width, height);
        setMinimumSize(new Dimension(width, height));
        moduleHandler = new ConfigModuleHandler();
        //
        MyLookAndFeel.setLook();
        //////////////
        ////init all my tables
        ///////////
        initComponents();
        setBorder(BorderFactory.createLineBorder(new Color(80, 185, 80), 1));
        //regS = reg.isAlreadyRegistered();
        /////////////////////////
    }

    ////////////////////
    /////////////////////
    /////////////////

    public void passMenuVector(List<JComponent> m){
        if(menuItemsV.addAll(m)){
            return;
        }
        //handle and check if the menu items should be displayed
    }

    public void refresh(int s){
        //pass in the selected item's index
        homePanel.repainter(s);
    }

    public JTable getUserOnlineTable(){
        return userOnlineTable;
    }

    public DbRegistration getRegisteredInfoInst(){
        return reg;
    }
    
    public void initDbConnections(){
        con = manager.getAvailableConnection();
        //con = PublicMXJdataEmbedded.getNewConnection();
        reg = new DbRegistration();

        //
        //initialize the data getter
        data = new DataResources(con);
        //
        ///////////////////////////////////////////
        regUsersTM = new RegisteredUsers(con);
        catTM = new Catalogs(con, "comodities_unit");
        catTM_bulk = new Catalogs(con, "comodities_bulk");
        //onlineTM = new OnlineUsers(con);
        salesTM = new Sales(con, TimeAndDateRecord.getDate());
        //////////////////////////////////////////
        regUsersTable.setModel(regUsersTM);
        TableColumn tCol = null;       
        unitCatTable.setModel(catTM);
        ////////////
        tCol = unitCatTable.getColumnModel().getColumn(0);
        tCol.setPreferredWidth(28);
        tCol = unitCatTable.getColumnModel().getColumn(1);
        tCol.setPreferredWidth(98);
        tCol = unitCatTable.getColumnModel().getColumn(2);
        tCol.setPreferredWidth(88);
        tCol = unitCatTable.getColumnModel().getColumn(3);
        tCol.setPreferredWidth(58);
        ////////////

        TableColumn tCol_2 = null;
        bulkCatTable.setModel(catTM_bulk);
        ////////////
        tCol_2 = bulkCatTable.getColumnModel().getColumn(0);
        tCol_2.setPreferredWidth(28);
        tCol_2 = bulkCatTable.getColumnModel().getColumn(1);
        tCol_2.setPreferredWidth(98);
        tCol_2 = bulkCatTable.getColumnModel().getColumn(2);
        tCol_2.setPreferredWidth(88);
        tCol_2 = bulkCatTable.getColumnModel().getColumn(3);
        tCol_2.setPreferredWidth(58);
        ////////////

        //userOnlineTable.setModel(onlineTM); //set model for online users

        try{
            reg.performRegCheck();
            ti.start(); //start the timer that manages the components to display on the left hand side of the panel
            ti.setRepeats(false); //make sure it performs just once
        }
        catch(SQLException e){
            System.err.println("Error checking the if Someone already registered the software before : " + e.getMessage());
        }
        //// table refresher here
        new BackgroundWork().execute();
        autoLogout = new AutoLogoutHandler(parent, con); //initialize the autologout but dont start yet
        ///////////
        repaint();
        validate();
    }
    /////////////////////
    //////////////////
    ///////////////////
    
    private void initComponents(){
        usersPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        itemsPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        usersPane.setPreferredSize(new Dimension(400, 220));
        usersPane.setFocusable(false);
        itemsPane.setPreferredSize(new Dimension(400, 220));
        itemsPane.setFocusable(false);

        regUsersTable = new JTable(); //added users
        userOnlineTable = new JTable(); //online users

        unitCatTable = new JTable(); //unit catalog tables
        bulkCatTable = new JTable(); //bulk catalog table
        
        
        homePanel = new SetUpPanel();
        add(homePanel);
    }

    public void passFrameInstance(AdminFrame pa){
        parent = pa;
    }

    public int getSelectedIndexOfItems(){
        int ret = 0;
        if(moduleInUse == 1){
            ret = 1;
        }
        else if(moduleInUse == 0){
            ret = 2;
        }
        else{
            ret = itemsPane.getSelectedIndex() + 1;
        }
        return ret;
    }

/*
 * worker for table changes below
 */
    private class BackgroundWork extends SwingWorker<Void, Void>{

        private Timer t;
        BackgroundWork(){
        }

        void startRefreshingTable(){
            t = new Timer(15000, new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    new SwingWorker<Void, Void>(){
                        public Void doInBackground(){
                            try{
                                if(!con.isClosed()){
                                    catTM.refreshTable();
                                    catTM_bulk.refreshTable();
                                    regUsersTM.refreshTable();
                                    //onlineTM.refreshTable();
                                    salesTM.refreshTable();
                                }
                            }
                            catch(SQLException s){
                                System.err.println("New Error happened while testing if connection is closed" + s.getMessage());
                            }
                            return null;
                        }
                    }.execute();

                    repaint();
                    validate();
                }
            });
            if(!t.isRunning()){
                t.start(); //start the timer
            }
        }
        public Void doInBackground(){
            startRefreshingTable(); //start refreshing catalog table here            
            return null;
        }

        @Override
        public void done(){

        }
    }

    /*
     * inner class that renders the setup panel which
     * contains its forms whenever the user is new
     * and hasnt registered this application
     *
     */
    private class SetUpPanel extends JPanel implements FocusListener{
        private JPanel leftPanel;
        private JPanel rightPanel;
        private JPanel rTopPanel;
        private JPanel rDownPanel;

        private JPanel tp1;
        private JPanel tp2;

        private JPanel catOptionPanel;
        private JButton addCatBtn;
        private JButton removeCatBtn;
        private JTextField searchTxt;
        
        private JScrollPane rDownScroll;
        private JScrollPane rDownScroll_2;
        private JScrollPane rTtopScroll;
        private JScrollPane rTdownScroll;


        private JComboBox comodCombo;
        private JComboBox userCombo;
        private JComboBox datesCombo;
        
        //////////////////////////////////////
        ////////////

        public SetUpPanel(){
            //reg = new DbRegistration(); //initialize db Registration class which will also check if this app is already reg
            w = new SwingWorker<Void, Void>(){
                public Void doInBackground(){
                    //System.out.println("EXEC");
                    //moduleReady = false; //set the ready state of the moduleInUse to false and let it be true when swingworker is done

                    moduleInUse = Integer.parseInt(moduleHandler.read(0));
                    return null;
                }
            };
            initComponents();
        }

        private void initComponents(){
            w.execute();//execute the swingWorker

            addCatBtn = new JButton("Add / Edit");
            removeCatBtn = new JButton("Remove");
            searchTxt = new JTextField();

            addCatBtn.setHorizontalAlignment(SwingConstants.CENTER);
            addCatBtn.setPreferredSize(new Dimension(96, 23));
            addCatBtn.setBackground(new Color(65, 105, 225));
            addCatBtn.setForeground(Color.WHITE);
            addCatBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
            addCatBtn.addMouseListener(me);

            addCatBtn.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    new AddOptionDialog(manager.getFrameInstance(), con);
                }
            });

            removeCatBtn.setHorizontalAlignment(SwingConstants.CENTER);
            removeCatBtn.setPreferredSize(new Dimension(96, 23));
            removeCatBtn.setBackground(new Color(65, 105, 225));
            removeCatBtn.setForeground(Color.WHITE);
            removeCatBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
            removeCatBtn.addMouseListener(me);

            removeCatBtn.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    new RemoveOptionDialog(manager.getFrameInstance(), con);
                }
            });

            searchTxt.setPreferredSize(new Dimension(96, 23));
            searchTxt.setToolTipText("Type an item you want to search");
            searchTxt.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
            searchTxt.addMouseListener(me);

            searchTxt.addKeyListener(new KeyListener(){
                public void keyTyped(KeyEvent e){
                }
                public void keyPressed(KeyEvent e){
                    //attach listener to this event
                }
                public void keyReleased(KeyEvent e){
                }

            });

            tp1 = new JPanel(new BorderLayout(2, 2));
            tp1.setPreferredSize(new Dimension(290, 80));
            //tp1.setBorder(BorderFactory.createLineBorder(Color.PINK, 1));
            tp2 = new JPanel(new BorderLayout(2, 2));
            tp2.setPreferredSize(new Dimension(290, 80));
            //tp2.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
            
            leftPanel = new JPanel();
            leftPanel.setPreferredSize(new Dimension(580, 400));
            leftPanel.setBorder(BorderFactory.createLineBorder(new Color(134, 206, 134), 1));

            rightPanel = new JPanel(new BorderLayout(3, 3));
            rightPanel.setPreferredSize(new Dimension(300, 400));
            //rightPanel.setBorder(BorderFactory.createLineBorder(Color.green, 1));

            rTopPanel = new JPanel(new BorderLayout(3, 3));
            rTopPanel.setPreferredSize(new Dimension(300, 90));
            rTopPanel.setBorder(BorderFactory.createLineBorder(new Color(134, 206, 134), 1));

            rDownPanel = new JPanel(new BorderLayout(2, 2));
            rDownPanel.setPreferredSize(new Dimension(300, 290));
            rDownPanel.setBorder(BorderFactory.createLineBorder(new Color(134, 206, 134), 2));

            catOptionPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 2, 5));
            catOptionPanel.add(addCatBtn);
            catOptionPanel.add(removeCatBtn);
            catOptionPanel.add(searchTxt);

            rDownScroll = new JScrollPane();
            rDownScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            rDownScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            rDownScroll.setViewportView(unitCatTable);
            unitCatTable.addMouseListener(me);

            rDownScroll_2 = new JScrollPane();
            rDownScroll_2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            rDownScroll_2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            rDownScroll_2.setViewportView(bulkCatTable);
            bulkCatTable.addMouseListener(me);
            
            rTtopScroll = new JScrollPane();
            rTtopScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            rTtopScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            rTtopScroll.setViewportView(userOnlineTable);
            userOnlineTable.addMouseListener(me);

            rTdownScroll = new JScrollPane();
            rTdownScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            rTdownScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            rTdownScroll.setViewportView(regUsersTable);
            regUsersTable.addMouseListener(me);

            tp1.add(rTtopScroll, BorderLayout.CENTER);
            tp2.add(rTdownScroll, BorderLayout.CENTER);

            //add the unit item and bulk item tab to the sales tab
            //add the user online and users reg to the users pane tab
            usersPane.add("ONLINE USERS", tp1);
            usersPane.add("REGISTERED USERS", tp2);
            ///
            
            if(w.isDone()){
                listenModuleChanges();
            }
            //itemsPane.add("@ UNIT ITEMS", rDownScroll); //unit items scroll pane
            //itemsPane.add("@ BUlK ITEMS", rDownScroll_2); //bulk items scroll

            rTopPanel.add(usersPane, BorderLayout.CENTER);
            //rTopPanel.add(tp2, BorderLayout.SOUTH);
            
            itemsPane.revalidate();
            
            rDownPanel.add(itemsPane, BorderLayout.CENTER); //add the items tabbed pane that consist of the unit and buld items
            rDownPanel.add(catOptionPanel, BorderLayout.SOUTH); //add the catalog options panel for search and adding of items
            
            rDownPanel.revalidate();

            rightPanel.add(rTopPanel, BorderLayout.CENTER);
            //rightPanel.add(new JSeparator(JSeparator.VERTICAL));
            rightPanel.add(rDownPanel, BorderLayout.SOUTH);
            
            rightPanel.revalidate();
            
            add(leftPanel, BorderLayout.CENTER);

            add(rightPanel, BorderLayout.EAST);
            ////////////////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////////
            ti = new Timer(1700, new ActionListener(){
                public void actionPerformed(ActionEvent ev){
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                            if(regS){
                                //means the app is already registered .. show welcome back HOME panel
                                //System.out.println("ALready registered");
                                setLeftPanelHome();
                                ti.stop(); //stop the timer since i already determined what i am looking for 
                                //if(theWork.execute())
                            }
                            else{
                                setLeftPanelForm(); //call the methods that dynamically display what shuld be painted at the leftPanel if not resgistered
                            }
                            repaint();
                            validate();
                        }
                    });
                }
            });            
            //////////////////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////

        }

        public void focusLost(FocusEvent ev){
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    reg.checkAllInputs();
                }
            });
        }

        public void focusGained(FocusEvent ev){
            if(!ev.isTemporary() && !ev.getSource().equals(fields[0])){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        reg.checkAllInputs();
                    }
                });
            }
        }

        private void repainter(int i){
            moduleInUse = i;
            new SwingWorker<Void, Void>(){
                public Void doInBackground(){
                    itemsPane.removeAll();
                    listenModuleChanges();
                    return null;
                }
                @Override
                public void done(){
                    rightPanel.repaint();
                    rightPanel.validate();
                }
            }.execute();

                /*
                Thread.sleep(5000);
                w.execute();
                listenModuleChanges();
                rightPanel.repaint();
                rightPanel.validate();
                homePanel.repaint();
                homePanel.validate();
                repaint();
                validate();
                 *
                 */
        }
        
        public void listenModuleChanges(){
                switch(moduleInUse){
                    case 0:
                        //means its a wholesales Store
                        itemsPane.add("@ BUlK ITEMS", rDownScroll_2); //bulk items scroll
                        break;
                    case 1:
                        //means its a retail store
                        itemsPane.add("@ UNIT ITEMS", rDownScroll); //unit items scroll pane
                        break;
                    case 2:
                        //means its a eneric store
                        itemsPane.add("@ UNIT ITEMS", rDownScroll); //unit items scroll pane
                        itemsPane.add("@ BUlK ITEMS", rDownScroll_2); //bulk items scroll
                        break;
                    default:
                        break;
                }
                itemsPane.repaint();
                itemsPane.validate();
        }

        private void setLeftPanelForm(){
            String info = "\tThis Application is developed to handle numerous events and management of SUPER MARKETS or any SALES POINT in Respect of SMALL," +
                    " MEDIUM or LARGE SCALE Market. We guarantee secure DATA processing, FLEXIBLE usage, OPTIMAL Sales/Stock recording and many more functionalities thats " +
                    "needed to DELIVER great USABILITY your FIRM can BENEFIT. Call 08025481373, 07042572687 or 07051830762 for more info.";
            //JPanel wrap = new ImagePanel(new ImageIcon(getClass().getResource("images/left_panel.gif")).getImage());
            JPanel wrap = new JPanel();
            wrap.setLayout(new BorderLayout(2, 12));
            JPanel top = new JPanel(new BorderLayout(2, 5));
            JPanel theForm = new JPanel(new GridLayout(5, 2, 10, 10));
            JLabel topInfoLbl = new JLabel("COMPLETE SOFTWARE REGISTRATION FORM");
            JLabel labels[] = new JLabel[5];
            fields = new JTextField[4];
            String[] combo = {"Select a Module ......", "Wholesales Store", "Retail Store", "Generic Store-Whole/Retail"};
            salesModuleCombo = new JComboBox(combo);
            JButton regBtn = new JButton("Validate Application");
            JTextArea infoTxt = new JTextArea(info);

            infoTxt.setLineWrap(true);
            infoTxt.setWrapStyleWord(true);
            //infoTxt.setTabSize(-13);
            infoTxt.setFont(new Font("Vrinda", Font.ROMAN_BASELINE, 12));
            infoTxt.setPreferredSize(new Dimension(theForm.getWidth(), 130));
            infoTxt.setEditable(false);

            labels[0] = new JLabel("Company's Name :");
            labels[1] = new JLabel("Company's Location :");
            labels[2] = new JLabel("Contact Telephones :");
            labels[3] = new JLabel("Company's E-mail :");
            labels[4] = new JLabel("* SELECT MARKET MODULE :");

            topInfoLbl.setHorizontalAlignment(SwingConstants.CENTER);
            topInfoLbl.setFont(new Font("AR JULIAN", Font.TRUETYPE_FONT, 13));
            topInfoLbl.setForeground(new Color(0, 0, 255));

            regBtn.setHorizontalAlignment(SwingConstants.CENTER);
            regBtn.setPreferredSize(new Dimension(225, 30));
            regBtn.setBackground(new Color(65, 105, 225));
            regBtn.setForeground(Color.WHITE);

            regBtn.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ev){
                    reg.checkAllInputs(); //check the inputs again and see if they are valid
                    if(reg.areInputsValidated() && (salesModuleCombo.getSelectedIndex() != 0)){
                        try{
                            reg.performRegistration(); //perform the registration
                            //then pass new instance of this class to the panelManager to reassign this to whats in
                            ///////////////////
                            manager.passNewInstanceOfPanel(new WelcomePanel(manager, 912, 434));
                            ///////////////////
                            repaint();
                            validate();
                        }
                        catch(SQLException e){
                            System.err.println("Error performing Registration : " + e.getMessage());
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(me, "Form Submitted contains INVALID DATA", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
                    }
                }
            });

            for(int i = 0; i < labels.length; i++){
                labels[i].setFont(new Font("Poor Richard", Font.PLAIN, 14));
                labels[i].setHorizontalAlignment(SwingConstants.LEADING);
                labels[i].setPreferredSize(new Dimension(225,25));
            }

            for(int i = 0; i < fields.length; i++){
                fields[i] = new JTextField();
                fields[i].setFont(new Font("Vandana", 0, 12));
                fields[i].setPreferredSize(new Dimension(225,25));
                fields[i].addFocusListener(this); //add focus listener to the textfields
            }
            salesModuleCombo.setPreferredSize(new Dimension(225, 25));
            salesModuleCombo.setFont(new Font("Vandana", 0, 12));
            //add action listener to it
            salesModuleCombo.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    //System.out.println("I SELECTED index " + salesModuleCombo.getSelectedIndex());
                    if(salesModuleCombo.getSelectedIndex() != 0){
                        reg.setInputStat(true);
                    }
                    else{
                        reg.setInputStat(false);
                    }
                }
            });

            theForm.add(labels[0]);
            theForm.add(fields[0]);
            theForm.add(labels[1]);
            theForm.add(fields[1]);
            theForm.add(labels[2]);
            theForm.add(fields[2]);
            theForm.add(labels[3]);
            theForm.add(fields[3]);
            theForm.add(labels[4]);
            theForm.add(salesModuleCombo);

            top.add(theForm, BorderLayout.CENTER);
            top.add(regBtn, BorderLayout.SOUTH);

            wrap.add(top, BorderLayout.CENTER);
            wrap.add(topInfoLbl, BorderLayout.NORTH);
            wrap.add(infoTxt, BorderLayout.SOUTH);
            
            leftPanel.add(wrap);
        }

        private void setLeftPanelHome(){
            String []regInfo = reg.getWhoLicensedInfo(); //array of registartion info
            //////////////////////
            //// vectors used in the combo boxes above
            /////////////////////

            JPanel wrap = new JPanel();
            wrap.setLayout(new BorderLayout(2, 12));
            JPanel top = new JPanel(new BorderLayout(2, 5));
            JPanel tablePanel = new JPanel(new BorderLayout(2, 2));
            JPanel tCenterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
            JTable salesTable = new JTable();
            JPanel topTpanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 2, 2));
            //JPanel downTpanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 2, 2));
            //set the model of the sales table
            salesTable.setModel(salesTM);
            salesTable.addMouseListener(me);
            JScrollPane tableScroll = new JScrollPane(salesTable); //put the sales table in the Scroll Pane
            userCombo = new JComboBox(data.getUsers());
            switch(moduleInUse){
                case 0:
                    comodCombo = new JComboBox(data.get_bulkItems()); //bulk
                    break;
                case 1:
                    comodCombo = new JComboBox(data.get_unitItems()); //unit
                    break;
                case 2:
                    java.util.Vector<String> v = new java.util.Vector<String>();
                    for(String a : data.get_unitItems()){
                        v.addElement(a);
                    }
                    for(String b : data.get_bulkItems()){
                        if(b.equals("Select an Item ....."))
                            continue;
                        v.addElement(b);
                    }
                    comodCombo = new JComboBox(v); //both
                    break;
                default:
            }
            
            datesCombo = new JComboBox(data.getDates());
            JButton queryBtn = new JButton("Query");

            queryBtn.setHorizontalAlignment(SwingConstants.CENTER);
            queryBtn.setPreferredSize(new Dimension(133, 23));
            queryBtn.setBackground(new Color(65, 105, 225));
            queryBtn.setForeground(Color.WHITE);
            queryBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
            queryBtn.addMouseListener(me);
            queryBtn.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    //query the data and refresh the sales table
                    salesTM.queryData(comodCombo.getSelectedItem().toString(), userCombo.getSelectedItem().toString(),
                            datesCombo.getSelectedItem().toString());
                    repaint();
                    validate();
                }
            });

            userCombo.setPreferredSize(new Dimension(133, 23));
            userCombo.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
            userCombo.addMouseListener(me);
            comodCombo.setPreferredSize(new Dimension(133, 23));
            comodCombo.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
            comodCombo.addMouseListener(me);
            datesCombo.setPreferredSize(new Dimension(133, 23));
            datesCombo.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
            datesCombo.addMouseListener(me);
            
            tableScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            tableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            salesTable.addMouseListener(me);

            topTpanel.setPreferredSize(new Dimension(555, 25));
            //topTpanel.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
            //downTpanel.setPreferredSize(new Dimension(555, 25));
            //downTpanel.setBorder(BorderFactory.createLineBorder(Color.RED, 1));

            //////////////

            Thread t = new Thread(new Runnable(){
                public void run(){
                    Timer ti = new Timer(1000, new ActionListener(){
                        public void actionPerformed(ActionEvent e){
                            SwingUtilities.invokeLater(new Runnable(){
                                public void run(){
                                //then enable disabled jmenuitem when topInfoLbl is showing
                                    String val = supermarket.utility.TimeAndDateRecord.getDayOfTheWeek() + ", " + TimeAndDateRecord.getDate() + " : " + TimeAndDateRecord.getTime();
                                    if(topInfoLbl.isShowing()){
                                        for(JComponent it : menuItemsV){
                                            it.setEnabled(true);
                                            it.addMouseListener(me);
                                        }
                                        if(autoState){
                                            autoLogout.start(); //means the left panel is showing
                                            autoState = false;//since the autologout has started once
                                        }
                                        else{
                                            //meaning autologout has started
                                            //start checking when Admin frame is disabled so that timer can purse
                                            if(!parent.isEnabled()){
                                                //if not enabled... purse autologout timing
                                                autoLogout.pause();
                                            }
                                            else{
                                                //continue it
                                                autoLogout.resume();
                                            }
                                        }
                                    }
                                
                                    topInfoLbl.setText(val);
                                    repaint();
                                    validate();
                                }
                            });
                        }
                    });
                    if(!ti.isRunning()){
                        ti.start();
                    }
                }
            });
            if(!t.isAlive()){
                t.start();
            }

            /////////////
            JLabel licToLbl = new JLabel("Licensed to : ");
            JTextField userNameTxt = new JTextField(regInfo[0]);
            JLabel contInfoLbl = new JLabel(regInfo[1]);

            contInfoLbl.setHorizontalAlignment(SwingConstants.CENTER);
            contInfoLbl.setPreferredSize(new Dimension(250, 30));

            licToLbl.setPreferredSize(new Dimension(150, 30));
            licToLbl.setHorizontalAlignment(SwingConstants.CENTER);

            userNameTxt.setEnabled(false);
            userNameTxt.setHorizontalAlignment(SwingConstants.CENTER);
            userNameTxt.setPreferredSize(new Dimension(300, 30));
            
            topInfoLbl.setHorizontalAlignment(SwingConstants.CENTER);
            topInfoLbl.setFont(new Font("AR JULIAN", Font.TRUETYPE_FONT, 13));
            topInfoLbl.setForeground(new Color(0, 0, 255));

            tablePanel.setBorder(BorderFactory.createLineBorder(new Color(134, 206, 134), 2));
            tablePanel.setPreferredSize(new Dimension(559, 289));

            topTpanel.add(userCombo);
            topTpanel.add(comodCombo);
            topTpanel.add(datesCombo);
            topTpanel.add(queryBtn);

            tablePanel.add(topTpanel, BorderLayout.NORTH);
            tablePanel.add(tableScroll, BorderLayout.CENTER);
            //tablePanel.add(downTpanel, BorderLayout.SOUTH);

            tCenterPanel.add(licToLbl);
            tCenterPanel.add(userNameTxt);
            
            top.add(topInfoLbl, BorderLayout.NORTH);
            top.add(tCenterPanel, BorderLayout.CENTER);
            top.add(contInfoLbl, BorderLayout.SOUTH);

            wrap.add(top, BorderLayout.NORTH);
            wrap.add(tablePanel, BorderLayout.CENTER);
            
            leftPanel.add(wrap);
        }
    }

    public class DbRegistration{

        private InputsManager check;
        private boolean inputStat;

        private String regName, regAddress, regTelephone, regEmail;

        DbRegistration(){           
            check = new InputsManager(); //initialize it will an empty constructor
            try{
                stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY); //default creation of statements
                result = stat.executeQuery("SELECT * from reg_info");
                if(result.absolute(1)){
                   regName = result.getString("name");
                   regAddress = result.getString("address");
                   regEmail = result.getString("email");
                   regTelephone = result.getString("tel");
                }
            }
            catch(SQLException e){
                System.err.println("Error getting the name of USER : " + e.getMessage());
            }
        }

        public void checkAllInputs(){
            for(int i = 0; i < fields.length; i++){
                check.passAnotherInput(fields[i].getText());
                if(i != 3){
                    //to ignore the email field input check
                    if(check.isGoodInput() && !fields[i].getText().equals("")){
                        fields[i].setText(InputsManager.makeUppercase(fields[i].getText()));
                        InputsManager.paint_unpaintTextFields(fields[i], true);
                        //InputsManager.isRestrictedLenght(fields[i], fields[i].getText(), 30);
                        inputStat = true; //means all inputs are valid and can be posted to DB
                    }
                    else{
                        //JOptionPane.showMessageDialog(me, "- Invalid Characters provided - \n Contact Service for VALID Character Combinations", "Characters ERROR", JOptionPane.PLAIN_MESSAGE);
                        InputsManager.paint_unpaintTextFields(fields[i], false);
                        inputStat = false;
                        break; // break to stop the look when a wrong input is detected
                    }
                }
                else{
                    if(InputsManager.isValidateEmail(fields[3].getText()) || fields[3].getText().equals("")){
                        InputsManager.isRestrictedLenght(fields[3], fields[3].getText(), 50);
                        fields[3].setText(InputsManager.makeLowercase(fields[3].getText()));
                        inputStat = true;
                    }
                    else{
                        //show error coz the email is tested and its not valid
                        //JOptionPane.showMessageDialog(me, "- Email Provided is not VALID", "EMAIL ERROR", JOptionPane.PLAIN_MESSAGE);
                        fields[3].setText("");
                        inputStat = false;
                        break; // break to stop the look when a wrong input is detected
                    }
                }
            }
        }

        public boolean areInputsValidated(){
            return inputStat;
        }

        public void setInputStat(boolean s){
            inputStat = s;
        }

        public void performRegistration() throws SQLException{
            int s;
            stat = con.createStatement(); //default creation of statements
            if(isAlreadyRegistered()){
                //dont do anything coz someone has already registered this software
                System.out.println("Already registered before");
            }
            else{
                //then execute update by registering the software
                Thread t = new Thread(new Runnable(){
                    public void run(){
                       SwingUtilities.invokeLater(new Runnable(){
                           public void run(){
                               new ConfigModuleHandler().write(String.valueOf((salesModuleCombo.getSelectedIndex() - 1)), 0);
                               //write the market module to be used in the file
                           }
                       });
                    }
                });
                t.start();
                s = stat.executeUpdate("INSERT INTO reg_info (name, address, tel, email) VALUES ('" + fields[0].getText() + "', '" + fields[1].getText() + "', '"
                        + fields[2].getText() + "', '" + fields[3].getText() + "')");
                if(s == 1){
                    //if one row is updated.. shut this application down
                    JOptionPane.showMessageDialog(me, "- REGISTRATION SUCCESSFUL - \n - Application will close, Please relaunch - ", "REGISTRATION COMPLETE", JOptionPane.PLAIN_MESSAGE);
                    parent.dispose();
                    parent.getStartup().deActivator(0);
                    parent.getStartup().setVisible(true);

                    //start
                }
            }
        }

        public boolean isAlreadyRegistered(){
            return regS;
        }

        public String getRegName(){
            return regName;
        }

        public String getRegAddress(){
            return regAddress;
        }

        public String getRegTelephone(){
            return regTelephone;
        }

        public String getRegEmail(){
            return regEmail;
        }
        
        private void performRegCheck() throws SQLException{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY); //default creation of statements
            result = stat.executeQuery("SELECT id FROM reg_info");
            if(result.first()){
                regS = true;
            }
            else{
                regS = false;
            }
        }

        public String[] getWhoLicensedInfo(){
            String []n = new String[2];
            String contact = "";
            n[0] = regName;
            contact = "Telephone : ( " + regTelephone + " ) , Email : ( " + regEmail + " )";
            n[1] = contact;
            return n; //return array of registration info ... contains tha name of license and contact
        }
    }

    public void mouseClicked(MouseEvent e){
        //if any action is performed in this widow, always reset the tick;
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                //set the auto logout tick to 0;
                autoLogout.setTick(0);
            }
        });

    }

    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mousePressed(MouseEvent e){
        //if any action is performed in this widow, always reset the tick;
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                //set the auto logout tick to 0;
                autoLogout.setTick(0);
            }
        });
    }
}
