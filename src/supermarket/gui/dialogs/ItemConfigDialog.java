/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui.dialogs;
import supermarket.later.AddConstraintDialog;
import supermarket.later.EditConstraintDialog;
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
public class ItemConfigDialog extends JDialog{

    private JDialog self = this;

    private JFrame parent;
    private Connection con;
    private Statement stat;
    private ResultSet result;

    private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();;

    private JPanel outPanel;
    private JPanel topPanel;
    private JPanel downPanel;
    private JPanel downTopPanel;
    private JPanel constPanel;
    private JLabel itemLbl;
    private JComboBox itemsCombo;
    private JCheckBox selectAllBox;
    private JLabel itemNameLbl;
    private JLabel constValLbl;
    private JLabel remarkLbl;
    private JButton addConstBtn;
    private JButton removeConstBtn;
    private JButton editConstBtn;
    private JButton cancelBtn;
    private JScrollPane downScroll;

    private PopDialogParentHandler popH;

    ///////
    private Vector <String>itemsV;
    //////
    /*
     * constraint vectors
     */
    private Vector <String>constItemV;
    private Vector <String>constStatusV;
    private Vector <String>constRemarkV;
    /*
     *
     */
    private Thread th;

    //private InputsManager checkIn;
    private int count = 0;
    private JCheckBox []check;
    private String prevCombo = "";
    private boolean selectAllState = false;
    //private JCheckBox box = null;

    public ItemConfigDialog(JFrame p, Connection c){
        parent = p;
        con = c;
        itemsV = new Vector<String>();
        constItemV = new Vector<String>();
        constStatusV = new Vector<String>();
        constRemarkV = new Vector<String>();
        
        itemsV.addElement("Select an Item...");
        //checkIn = new InputsManager("");

        ////////////////////////////////////////////////////////////
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                popH = new PopDialogParentHandler(parent, self);
            }
        });
        ///////////////////////////////////////////////////////////

        setSize(570, 310);
        setLocation((d.width - 570) / 2, (d.height - 310) /2);
        setAlwaysOnTop(true);
        setUndecorated(true);
        ///////
        MyLookAndFeel.setLook();
        //////
        initComponents();
        setContentPane(outPanel);
        loadActions();
        /*
         *
         */
        th = new Thread(new Runnable(){
            public void run(){
                try{
                    loadItemsDataFromDB();
                }
                catch(SQLException e){
                    System.err.println("Error at preloading data that are in the DB : " + e.getMessage());
                }
            }
        });
        if(!th.isAlive()){
            th.start();
        }
        //setVisible(true);
        //after the Dialog is visible.. work showing the constraint
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                initConstraintPanel();
            }
        });
    }

    private void initComponents(){
        outPanel = new JPanel(new BorderLayout());
        topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        downPanel = new JPanel(new BorderLayout(5, 5));
        downTopPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        constPanel = new JPanel();
        itemLbl = new JLabel("Configure Constraints : ");
        itemsCombo = new JComboBox(itemsV);
        addConstBtn = new JButton("Add");
        removeConstBtn = new JButton("Remove");
        editConstBtn = new JButton("Edit");
        cancelBtn = new JButton("Cancel");
        selectAllBox = new JCheckBox("");
        itemNameLbl = new JLabel("--------ITEM NAME--------");
        constValLbl = new JLabel("--------CONSTRAINT VALUE--");
        remarkLbl = new JLabel("--------REMARK--------");
        downScroll = new JScrollPane();

        ////
        addConstBtn.setEnabled(false);
        removeConstBtn.setEnabled(false);
        editConstBtn.setEnabled(false);
        ////

        downScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        downScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        downScroll.setPreferredSize(new Dimension(555, 190));

        outPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        outPanel.setPreferredSize(new Dimension(570, 310));

        topPanel.setPreferredSize(new Dimension(570, 40));
        topPanel.setBackground(new Color(152, 213, 152));
        topPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        downPanel.setPreferredSize(new Dimension(570, 230));
        downPanel.setBackground(new Color(152, 213, 152));
        downPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        downTopPanel.setPreferredSize(new Dimension(570, 28));
        downTopPanel.setBackground(new Color(152, 213, 152));
        downTopPanel.setBorder(BorderFactory.createLineBorder(new Color(152, 213, 152), 4));

        itemLbl.setPreferredSize(new Dimension(120, 30));
        itemLbl.setFont(new Font("Tahoma", 1, 10));

        selectAllBox.setPreferredSize(new Dimension(20, 25));
        selectAllBox.setToolTipText("Click to select all");
        selectAllBox.setHorizontalAlignment(SwingConstants.CENTER);

        itemNameLbl.setPreferredSize(new Dimension(170, 25));
        itemNameLbl.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
        itemNameLbl.setHorizontalAlignment(SwingConstants.CENTER);

        constValLbl.setPreferredSize(new Dimension(170, 25));
        constValLbl.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
        constValLbl.setHorizontalAlignment(SwingConstants.CENTER);

        remarkLbl.setPreferredSize(new Dimension(170, 25));
        remarkLbl.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
        remarkLbl.setHorizontalAlignment(SwingConstants.CENTER);

        itemsCombo.setPreferredSize(new Dimension(100, 30));
        itemsCombo.setFont(new Font("Tahoma", 0, 10));

        addConstBtn.setPreferredSize(new Dimension(100, 30));
        addConstBtn.setHorizontalAlignment(SwingConstants.CENTER);
        addConstBtn.setBackground(new Color(65, 105, 225));
        addConstBtn.setForeground(Color.WHITE);
        addConstBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        removeConstBtn.setPreferredSize(new Dimension(100, 30));
        removeConstBtn.setHorizontalAlignment(SwingConstants.CENTER);
        removeConstBtn.setBackground(new Color(65, 105, 225));
        removeConstBtn.setForeground(Color.WHITE);
        removeConstBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        editConstBtn.setPreferredSize(new Dimension(100, 30));
        editConstBtn.setHorizontalAlignment(SwingConstants.CENTER);
        editConstBtn.setBackground(new Color(65, 105, 225));
        editConstBtn.setForeground(Color.WHITE);
        editConstBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        cancelBtn.setPreferredSize(new Dimension(100, 30));
        cancelBtn.setHorizontalAlignment(SwingConstants.CENTER);
        cancelBtn.setBackground(new Color(65, 105, 225));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        constPanel.add(downScroll);

        topPanel.add(itemLbl);
        topPanel.add(itemsCombo);
        topPanel.add(addConstBtn);
        topPanel.add(removeConstBtn);
        topPanel.add(editConstBtn);

        downTopPanel.add(selectAllBox);
        downTopPanel.add(itemNameLbl);
        downTopPanel.add(constValLbl);
        downTopPanel.add(remarkLbl);

        downPanel.add(downTopPanel, BorderLayout.NORTH);
        downPanel.add(constPanel, BorderLayout.CENTER);

        outPanel.add(topPanel, BorderLayout.NORTH);
        outPanel.add(downPanel, BorderLayout.CENTER);
        outPanel.add(cancelBtn, BorderLayout.SOUTH);

    }

    private void loadActions(){
        cancelBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                popH.stopPopHandler();
                dispose();
            }
        });

        itemsCombo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        //check of the selected item is not default "select an item..."
                        if(itemsCombo.getSelectedIndex() != 0){
                            SwingUtilities.invokeLater(new Runnable(){
                                public void run(){
                                    if(!prevCombo.equals(itemsCombo.getSelectedItem().toString())){
                                        manageButtonsState(itemsCombo.getSelectedItem().toString());
                                        selectAllState = false; //the select shoud be stactically in false
                                    }//dont perform any action if the combo box wasnt changed
                                    prevCombo = itemsCombo.getSelectedItem().toString();
                                }
                            });
                        }
                        else{
                            //dont bother to perform any shit in the database
                            //just set all the buttons to false
                            addConstBtn.setEnabled(false);
                            //removeConstBtn.setEnabled(false);
                            editConstBtn.setEnabled(false);
                        }
                        repaint();
                        validate();
                    }
                });
            }
        });

        addConstBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        new AddConstraintDialog(parent, con, itemsCombo.getSelectedItem().toString());
                        popH.stopPopHandler();
                        dispose();
                    }
                });
            }
        });

        editConstBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        new EditConstraintDialog(parent, con, itemsCombo.getSelectedItem().toString());
                        popH.stopPopHandler();
                        dispose();
                    }
                });
            }
        });

        removeConstBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        if(selectAllState){
                            int op = JOptionPane.showConfirmDialog(self, "Are you sure you want to REMOVE all constraint",
                                    "CONFIRM REMOVAL", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            if(op == 0){
                                //that means u wanna delete it .. so, perform removal
                                Thread t = new Thread(new Runnable(){
                                    public void run(){
                                        self.setEnabled(false);
                                        if(performRemoveAll()){
                                            //means the removal was successful
                                        }
                                        else{
                                            //show Message that the update cannot be done at this time
                                            JOptionPane.showMessageDialog(self, "Cannot remove constraint at this time. Try Again",
                                                    "UPDATE ERROR", JOptionPane.PLAIN_MESSAGE);
                                        }
                                    }
                                });
                                t.start();
                                try{
                                    Thread.sleep(2000);
                                    popH.stopPopHandler();
                                    dispose();
                                }
                                catch(InterruptedException e){}
                            }
                        }
                        else{
                                //that means user wanna remove one
                                int op2 = JOptionPane.showConfirmDialog(self, "Are you sure you want to REMOVE '" + itemsCombo.getSelectedItem().toString()
                                        + "' constraint", "CONFIRM REMOVAL", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                                if(op2 == 0){
                                //that means u wanna delete it .. so, perform removal
                                Thread t = new Thread(new Runnable(){
                                    public void run(){
                                        self.setEnabled(false);
                                        if(performRemoveOne(itemsCombo.getSelectedItem().toString())){
                                            //means the removal was successful
                                        }
                                        else{
                                            //show error message
                                            JOptionPane.showMessageDialog(self, "Cannot remove constraint at this time. Try Again",
                                                    "UPDATE ERROR", JOptionPane.PLAIN_MESSAGE);
                                        }
                                    }
                                });
                                t.start();
                                try{
                                    Thread.sleep(1000);
                                    popH.stopPopHandler();
                                    dispose();
                                }
                                catch(InterruptedException e){}
                                }
                        }
                    }//end of run
                }); //end of inner class
            }//end of action performed
        }); //end of action listener inner class
    }

    private void loadItemsDataFromDB() throws SQLException{
        stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        result = stat.executeQuery("SELECT name FROM comodities");
        if(result.first()){
            do{
                itemsV.addElement(result.getString("name"));
            }
            while(result.next());
        }
        else{
            itemsV.addElement("");
        }
    }

    private void manageButtonsState(String it){
        String itemName = it; //get the item name
        boolean addState = false;
        boolean removeState = false;
        boolean editState = false;
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            result = stat.executeQuery("SELECT name FROM items_const_stock WHERE name='" + itemName + "'");
            if(result.first()){
                //that means there is a constraint set for this item already
                //so therefore, enable edit and remove btn
                addState = false;
                removeState = true;
                editState = true;
            }
            else{
                //enable only add button
                addState = true;
                removeState = false;
                editState = false;
            }
        }
        catch(SQLException sE){
            System.err.println("SQL Error at managing buttons State :" + sE.getMessage());
        }

        //at the end of the database checks
        addConstBtn.setEnabled(addState);
        removeConstBtn.setEnabled(removeState);
        editConstBtn.setEnabled(editState);
    }

    private void getConstrainted(){
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            result = stat.executeQuery("SELECT * FROM items_const_stock");
            if(result.first()){
                do{
                   constItemV.addElement(result.getString("name"));
                   constStatusV.addElement("MIN stock of " + result.getInt("min_stock"));
                   if(result.getInt("show_status") != 0){
                       constRemarkV.addElement("NOTIFY");
                   }
                   else{
                       constRemarkV.addElement("DONT NOTIFY");
                   }
                   count++;
                }
                while(result.next());
            }
        }
        catch(SQLException sE){
            System.err.println("SQL Error at getting constrainted values from DB :" + sE.getMessage());
        }
    }

    private boolean performRemoveAll(){
        boolean status = false;
        int upDate = 0;
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            for(int i = 0; i < constItemV.size(); i++){
                upDate = stat.executeUpdate("DELETE FROM items_const_stock WHERE name='" + constItemV.elementAt(i) + "'");
                if(upDate != 0){
                    status = checkUsersAttached_Remove(constItemV.elementAt(i));
                }
                else{
                    status = false; //something is wrong when trying to update it
                }
            }
        }
        catch(SQLException seE){
            System.err.println("Error at performing remove all Query : " + seE.getMessage());
        }
        return status;
    }

    private boolean performRemoveOne(String it){
        boolean status = false;
        int upDate = 0;
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            upDate = stat.executeUpdate("DELETE FROM items_const_stock WHERE name='" + it + "'");
            if(upDate != 0){
                status = checkUsersAttached_Remove(it);
            }
            else{
                status = false;
            }
        }
        catch(SQLException seE){
            System.err.println("Error at performing remove '" + it + "' Query : " + seE.getMessage());
        }
        return status;
    }

    private boolean checkUsersAttached_Remove(String it){
        boolean status = false;
        int upDate = 0;
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            result = stat.executeQuery("SELECT * FROM items_const_users WHERE item_name='" + it + "'");
            if(result.first()){
                upDate = stat.executeUpdate("DELETE FROM items_const_users WHERE item_name='" + it + "'");
                if(upDate != 0){
                    status = true;
                }
                else{
                    status = false; //something is wrong when trying to update it
                }
            }
            else{
                status = true; //db is checked but no item found here so, check is done well
            }
        }
        catch(SQLException sE){
            System.err.println("Error at checking user's attached and deleting them from database items_cont_users : " + sE.getMessage());
        }
        return status;

    }

    private void initConstraintPanel(){
        JPanel cPanel = new JPanel();
        getConstrainted(); //get the constrained items and details from the db
        //after this thread is done
        try{
            Thread.sleep(100);
        }
        catch(InterruptedException e){}
        //create an array of the components needs to be painted
        JPanel []rowPanel = new JPanel[count];
        check = new JCheckBox[count];
        JLabel []itmLbl = new JLabel[count];
        JLabel []statLbl = new JLabel[count];
        JLabel []remLbl = new JLabel[count];
        //cPanel.setLayout(new GridLayout(count, 4, 5, 5));
        cPanel.setPreferredSize(new Dimension(570, (32 * count)));
        cPanel.setBackground(Color.WHITE);
        for(int i = 0; i < count; i++){

            rowPanel[i] = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            rowPanel[i].setPreferredSize(new Dimension(570, 30));
            rowPanel[i].setBackground(Color.WHITE);
            
            check[i] = new JCheckBox();
            itmLbl[i] = new JLabel(constItemV.elementAt(i));
            statLbl[i] = new JLabel(constStatusV.elementAt(i));
            remLbl[i] = new JLabel(constRemarkV.elementAt(i));

            check[i].setPreferredSize(new Dimension(30, 30));
            check[i].setEnabled(false);
            itmLbl[i].setPreferredSize(new Dimension(170, 30));
            itmLbl[i].setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
            itmLbl[i].setHorizontalAlignment(SwingConstants.CENTER);

            statLbl[i].setPreferredSize(new Dimension(170, 30));
            statLbl[i].setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
            statLbl[i].setHorizontalAlignment(SwingConstants.CENTER);

            remLbl[i].setPreferredSize(new Dimension(170, 30));
            remLbl[i].setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
            remLbl[i].setHorizontalAlignment(SwingConstants.CENTER);


            rowPanel[i].add(check[i]);
            rowPanel[i].add(itmLbl[i]);
            rowPanel[i].add(statLbl[i]);
            rowPanel[i].add(remLbl[i]);
        }
        //then paste all the row panels in add to cPanel
        for(int x = 0; x < count; x++){
            cPanel.add(rowPanel[x]);
        }

        selectAllBox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(selectAllBox.isSelected()){
                    for(int x = 0; x < count; x++){
                        check[x].setSelected(true);
                    }
                    try{
                        Thread.sleep(200);
                        itemsCombo.setEnabled(false);
                        itemsCombo.setSelectedIndex(0);
                        addConstBtn.setEnabled(false);
                        removeConstBtn.setEnabled(true);
                        editConstBtn.setEnabled(false);
                        //set select all state
                        selectAllState = true;
                    }
                    catch(InterruptedException er){}

                }
                else{
                    for(int x = 0; x < count; x++){
                        check[x].setSelected(false);
                    }
                    itemsCombo.setEnabled(true);
                    itemsCombo.setSelectedIndex(0);
                    addConstBtn.setEnabled(false);
                    removeConstBtn.setEnabled(false);
                    editConstBtn.setEnabled(false);
                    //set select all state to false
                    selectAllState = false;
                }
                //repaint();
                //validate();
            }
        });

        downScroll.setViewportView(cPanel);
        repaint();
        validate();

    }
}
