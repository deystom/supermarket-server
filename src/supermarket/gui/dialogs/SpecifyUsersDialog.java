/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui.dialogs;
import supermarket.gui.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

/**
 *
 * @author MUSTAFA
 */
public class SpecifyUsersDialog extends JDialog{

    private JDialog self = this;

    private JDialog parent;
    private Connection con;
    private Statement stat;
    private ResultSet result;

    private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();;

    private JPanel outPanel;
    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel downPanel;
    private JLabel userLbl;
    private JCheckBox selectAllBox;
    private JButton saveBtn;
    private JButton cancelBtn;
    private JScrollPane downScroll;

    private PopDialogParentHandler popH;

    ///////
    private String itemName = "";
    private Vector <String>usersV;
    private Vector <String>selectedUsersV;
    //////

    //private InputsManager checkIn;
    private int count = 0;
    //private int spec = 0;
    private JCheckBox []check;
    private JLabel []nameLbl;
    private Timer selectedTimer;
    //private JCheckBox box = null;

    public SpecifyUsersDialog(JDialog p, Connection c){ //constructor for adding
        parent = p;
        con = c;
        usersV = new Vector<String>();
        setUndecorated(true);

        setSize(200, 130);
        setLocation((d.width - 200) / 2, (d.height - 130) /2);
        setAlwaysOnTop(true);
        ///////
        MyLookAndFeel.setLook();
        //////
        initComponents();
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                initUsersPanel();
            }
        });
        selectedUsersV = new Vector<String>();
    }

    public SpecifyUsersDialog(JDialog p, Connection c, String it, Timer t){ //constructor for editting
        parent = p;
        con = c;
        itemName = it;
        selectedTimer = t;
        usersV = new Vector<String>();
        setUndecorated(true);

        setSize(200, 130);
        setLocation((d.width - 200) / 2, (d.height - 130) /2);
        setAlwaysOnTop(true);
        ///////
        MyLookAndFeel.setLook();
        //////
        initComponents();
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                if(!selectedTimer.isRunning()){
                    selectedTimer.start();
                }
                preloadDataFrom_Db();//preload item from database if any record can be found
                initUsersPanel();
            }
        });
    }

    public void triggerDialog(){
        //checkIn = new InputsManager("");
        ////////////////////////////////////////////////////////////
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){                
                popH = new PopDialogParentHandler(parent, self);
            }
        });
        ///////////////////////////////////////////////////////////

        setContentPane(outPanel);
        loadActions();
        /*
         *
         */
        show();
        com.sun.awt.AWTUtilities.setWindowOpacity(parent, 0.7f);
        //after the Dialog is visible.. work showing the constraint
        checkBoxLoader(); //selected the box found in database
        repaint();
        validate();
    }

    private void initComponents(){
        outPanel = new JPanel(new BorderLayout());
        topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
        centerPanel = new JPanel(new BorderLayout(5, 2));
        downPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
        userLbl = new JLabel("USER NAMES");
        saveBtn = new JButton("Save");
        cancelBtn = new JButton("Cancel");
        selectAllBox = new JCheckBox("");
        downScroll = new JScrollPane();

        downScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        downScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        downScroll.setPreferredSize(new Dimension(195, 70));

        outPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        outPanel.setPreferredSize(new Dimension(200, 130));

        topPanel.setPreferredSize(new Dimension(200, 28));
        topPanel.setBackground(new Color(152, 213, 152));
        topPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        centerPanel.setPreferredSize(new Dimension(200, 70));
        centerPanel.setBackground(new Color(152, 213, 152));
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        downPanel.setPreferredSize(new Dimension(200, 27));
        downPanel.setBackground(new Color(152, 213, 152));
        //downPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        selectAllBox.setPreferredSize(new Dimension(20, 25));
        selectAllBox.setToolTipText("Click to select all");
        selectAllBox.setHorizontalAlignment(SwingConstants.CENTER);

        userLbl.setPreferredSize(new Dimension(160, 25));
        userLbl.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
        userLbl.setHorizontalAlignment(SwingConstants.CENTER);

        saveBtn.setPreferredSize(new Dimension(90, 25));
        saveBtn.setHorizontalAlignment(SwingConstants.CENTER);
        saveBtn.setBackground(new Color(65, 105, 225));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        cancelBtn.setPreferredSize(new Dimension(90, 25));
        cancelBtn.setHorizontalAlignment(SwingConstants.CENTER);
        cancelBtn.setBackground(new Color(65, 105, 225));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));


        topPanel.add(selectAllBox);
        topPanel.add(userLbl);

        centerPanel.add(downScroll, BorderLayout.CENTER);

        downPanel.add(saveBtn);
        downPanel.add(cancelBtn);

        outPanel.add(topPanel, BorderLayout.NORTH);
        outPanel.add(centerPanel, BorderLayout.CENTER);
        outPanel.add(downPanel, BorderLayout.SOUTH);

    }

    private void loadActions(){

        cancelBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                popH.stopDialogPopHandler();
                com.sun.awt.AWTUtilities.setWindowOpacity(parent, 1f);
                dispose();
            }
        });

        selectAllBox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(selectAllBox.isSelected()){
                    for(int x = 0; x < count; x++){
                        check[x].setSelected(true);
                        selectedUsersV.clear(); //clear the vector
                        selectedUsersV = new Vector<String>();
                    }
                }
                else{
                    for(int x = 0; x < count; x++){
                        check[x].setSelected(false);
                    }
                }
            }
        });

        saveBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                checkBoxMonitor();
                com.sun.awt.AWTUtilities.setWindowOpacity(parent, 1f);
                popH.stopDialogPopHandler();
                hide();
            }
        });
    }

    private void loadUsers(){
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            result = stat.executeQuery("SELECT name FROM users");
            if(result.first()){
                do{
                    usersV.addElement(result.getString("name"));
                }
                while(result.next());
            }
        }
        catch(SQLException sE){
            System.err.println("Error trying to get users in users table : " + sE.getMessage());
        }
    }

    private void initUsersPanel(){
        Thread t = new Thread(new Runnable(){
            public void run(){
                loadUsers();
                count = usersV.size();
            }
        });
        if(!t.isAlive()){
            t.start();
        }
        //after the usersV is loaded... then prepare the Jpanel
        JPanel cPanel = new JPanel();
        try{
            Thread.sleep(100);
        }
        catch(InterruptedException e){}
        JPanel []rowPanel = new JPanel[count];
        check = new JCheckBox[count];
        nameLbl = new JLabel[count];
        cPanel.setPreferredSize(new Dimension(190, (29 * count)));
        cPanel.setBackground(Color.WHITE);

        for(int i = 0; i < count; i++){
            rowPanel[i] = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 1));
            rowPanel[i].setPreferredSize(new Dimension(190, 25));
            rowPanel[i].setBackground(Color.WHITE);

            check[i] = new JCheckBox();
            nameLbl[i] = new JLabel(usersV.elementAt(i));
            check[i].setPreferredSize(new Dimension(20, 25));
            //check[i].setEnabled(false);
            nameLbl[i].setPreferredSize(new Dimension(160, 25));
            nameLbl[i].setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
            nameLbl[i].setHorizontalAlignment(SwingConstants.CENTER);

            rowPanel[i].add(check[i]);
            rowPanel[i].add(nameLbl[i]);
        }

        //then paste all the row panels in add to cPanel and load them action listeners
        for(int x = 0; x < count; x++){
            cPanel.add(rowPanel[x]);
        }

        for(int spec = 0; spec < count; spec++){
            check[spec].addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    if(selectAllBox.isSelected()){
                        selectAllBox.setSelected(false);
                    }
                    else{
                        //checkBoxMonitor(); //call this method thats gonna populate the seleced names
                    }
                }
            });
        }
         downScroll.setViewportView(cPanel);
    }

    private void checkBoxLoader(){
        //compare names gotten for the database with the one on the list
        for(int i = 0; i < nameLbl.length; i++){
            for(int x = 0; x < selectedUsersV.size(); x++){
                if(nameLbl[i].getText().equals(selectedUsersV.elementAt(x))){
                    check[i].setSelected(true); //check the box which its name was found
                }
            }
        }
    }

    private void checkBoxMonitor(){
        Vector <Integer>theIndex = new Vector<Integer>();
        for(int x = 0; x < check.length; x++){
            if(check[x].isSelected()){
                theIndex.addElement(x); //add the index of the slected checkBox
            }
            else{
                //dont do anything
            }
        }
        selectedUsersV.clear();//clear this vector
        selectedUsersV = new Vector<String>();
        for(int i = 0; i < theIndex.size(); i++){
            try{
                selectedUsersV.addElement(usersV.elementAt(theIndex.elementAt(i)));
            }
            catch(ArrayIndexOutOfBoundsException e){
                selectedUsersV.clear();
                System.err.println(e.getMessage());
            }
        }
    }
    
    public Vector<String> getListOfSelectedUsers(){
        return selectedUsersV;
    }

    private void preloadDataFrom_Db(){
        Vector <String>getU_V = new Vector<String>();
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            result = stat.executeQuery("SELECT user FROM items_const_users WHERE item_name='" + itemName + "'");
            if(result.first()){
                do{
                    getU_V.addElement(result.getString("user"));
                }
                while(result.next());
            }
        }
        catch(SQLException sE){
            System.err.println("Error preloading selected users from Db : " + sE.getMessage());
        }
        selectedUsersV = getU_V;

    }
}
