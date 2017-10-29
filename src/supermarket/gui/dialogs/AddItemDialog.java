/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui.dialogs;
import supermarket.gui.util.*;
import supermarket.utility.InputsManager;
import supermarket.gui.dialogs.util.*;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 *
 * @author Segun
 */
public class AddItemDialog extends JDialog implements FocusListener{

    private JDialog self = this;
    
    private JFrame parent;
    private Connection con;
    private Statement stat;

    private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();;

    private JPanel outPanel;
    private JPanel formPanel;
    private JTabbedPane downPane;
    private JPanel downPanel;
    private JLabel itemLbl;
    private JTextField itemTxt;
    private JLabel salesModLbl;
    private JComboBox salesModCombo;
    private JLabel descLbl;//description of items
    private JLabel priceLbl;//price of the items
    private JLabel stockLbl; //the stock
    private JTextField unitItemTxt;
    private JTextField unitPriceTxt;
    private JTextField unitStockTxt;
    private JTextField bulkItemTxt;
    private JTextField bulkPriceTxt;
    private JTextField bulkStockTxt;
    private JButton addBtn;
    private JButton cancelBtn;
    
    private PopDialogParentHandler popH;

    ///////
    private Thread th;

    private InputsManager checkIn;
    private boolean validateState = false;

    private Timer t;
    private int salesModule = 0;
    private int prevModuleSelected = 5;
    private boolean smthHappened = false;

    /////////////////////////////
    ////////////////////////////

    private ItemsModificationHandler item;
    private ItemsModuleHandler itemsModule;
    private Timer checkT;
    private int count = 3;

    private String itName = "";
    private int customModule = 0; //if 1=retail, 2=wholesales, 3 = both .. will be used to validate when adding items
    
    public AddItemDialog(JFrame p, Connection c){
        parent = p;
        con = c;
        checkIn = new InputsManager("");
        item = new ItemsModificationHandler(con);//initialize the items modification handler
        itemsModule = new ItemsModuleHandler();
        //
        /*
         * init necessary TextField here
         */
        bulkItemTxt = new JTextField();
        bulkItemTxt.setEditable(false);
        bulkPriceTxt = new JTextField();
        bulkStockTxt = new JTextField();
        itemTxt = new JTextField();
        unitItemTxt = new JTextField();
        unitItemTxt.setEditable(false);
        unitPriceTxt = new JTextField();
        unitStockTxt = new JTextField();
        bulkPriceTxt.addFocusListener(this);
        bulkStockTxt.addFocusListener(this);
        unitPriceTxt.addFocusListener(this);
        unitStockTxt.addFocusListener(this);
        /*
         *
         */
        //init statement
        try{
            stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        }
        catch(SQLException sE){
            System.err.println("Error init STATEMENT trying to add Items : " + sE.getMessage());
        }
        //
        checkT = new Timer(1000, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(count > 0){
                    count --; //decreament the counter that triggers an event after 5second to validate the item name
                    checkItemNames(); //private method that checks the item
                }
            }
        });

        ////////////////////////////////////////////////////////////
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                popH = new PopDialogParentHandler(parent, self);
            }
        });
        ///////////////////////////////////////////////////////////
        
        setSize(400, 270);
        setLocation((d.width - 400) / 2, (d.height - 270) /2);
        setAlwaysOnTop(true);
        setUndecorated(true);
        ///////
        MyLookAndFeel.setLook();
        //////
        tabHandler();
        initComponents();
        setContentPane(outPanel);
        loadActions();
        /*
         *
         */
        //setVisible(true);
        //after the Dialog is visible.. work on the database
        th = new Thread(new Runnable(){
            public void run(){
                /*
                try{
                    loadItemsNameFromDB();
                }
                catch(SQLException e){
                    System.err.println("Error at preloading name of COMODITIES already in the DB : " + e.getMessage());
                }
                 * 
                 */
            }
        });
        if(!th.isAlive()){
            th.start();
        }

    }

    private void tabHandler(){

        downPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        downPane.setFocusable(false);
        downPane.setPreferredSize(new Dimension(390, 150));

        switch(itemsModule.getCurrentModuleInUse()){
            case 'W':
                customModule = 2;
                downPane.add("@ Wholesales", wholeSalesPane());
                break;
            case 'R':
                customModule = 1;
                downPane.add("@ Retail", retailSalesPane());
                break;
            case 'G':
                customModule = 3;
                downPane.add("@ Retail", retailSalesPane());
                downPane.add("@ Wholesales", wholeSalesPane());
                new BackgroundJob().execute(); //do the background work only when its a generic module
                //thats the only time the combo box needs to be selected
                break;
            default:
                break;
        }
    }

    private void tabModifier(int i){

        if(prevModuleSelected != i){
            salesModule = i;
            prevModuleSelected = i;
            smthHappened = true;
        }
        else{
            //dont do anything
        }
    }

    private void initComponents(){
        String[] combo = itemsModule.getModulesCombo();
       
        outPanel = new JPanel(new BorderLayout(10, 10));
        formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        downPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));

        downPanel.setPreferredSize(new Dimension(390, 35));

        itemLbl = new JLabel("Item Name :");

        salesModLbl = new JLabel("Unique Item Module :");
        salesModCombo = new JComboBox(combo);

        addBtn = new JButton("ADD");
        cancelBtn = new JButton("CANCEL");

        outPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        outPanel.setPreferredSize(new Dimension(400, 280));

        formPanel.setPreferredSize(new Dimension(380, 70));
        formPanel.setBackground(new Color(152, 213, 152));

        itemLbl.setHorizontalAlignment(SwingConstants.CENTER);
        itemLbl.setFont(new Font("Tahoma", 0, 12));
        itemLbl.setPreferredSize(new Dimension(180, 30));

        salesModLbl.setHorizontalAlignment(SwingConstants.CENTER);
        salesModLbl.setFont(new Font("Tahoma", 0, 12));
        salesModLbl.setPreferredSize(new Dimension(180, 30));

        salesModCombo.setFont(new Font("Tahoma", 0, 12));
        salesModCombo.setPreferredSize(new Dimension(180, 30));

        itemTxt.setFont(new Font("Tahoma", 0, 12));
        itemTxt.setPreferredSize(new Dimension(180, 30));

        addBtn.setPreferredSize(new Dimension(180, 30));
        addBtn.setHorizontalAlignment(SwingConstants.CENTER);
        addBtn.setBackground(new Color(65, 105, 225));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        cancelBtn.setPreferredSize(new Dimension(180, 30));
        cancelBtn.setHorizontalAlignment(SwingConstants.CENTER);
        cancelBtn.setBackground(new Color(65, 105, 225));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        downPanel.add(addBtn);
        downPanel.add(cancelBtn);
        
        formPanel.add(itemLbl);
        formPanel.add(itemTxt);
        itemTxt.addFocusListener(this);
        itemTxt.getDocument().addDocumentListener(new DocumentListener(){
            public void changedUpdate(DocumentEvent e){}
            //do somthing when text is removed and inserted
            public void removeUpdate(DocumentEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        itName = itemTxt.getText().toUpperCase();
                        //unitItemTxt.setText(itName);
                        //bulkItemTxt.setText("BULK_" + itName);
                        count = 3;//since an even is trigerred ... postpone the validation of itemName to 5more sec
                    }
                });
            }
            public void insertUpdate(DocumentEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        itName = itemTxt.getText().toUpperCase();
                        //unitItemTxt.setText(itName);
                        //bulkItemTxt.setText("BULK_" + itName);
                        count = 3;//since an even is trigerred ... postpone the validation of itemName to 5more sec
                    }
                });
            }
        }); //add a document listener to the itemTxt field

        formPanel.add(salesModLbl);
        formPanel.add(salesModCombo);

        outPanel.add(formPanel, BorderLayout.NORTH);
        outPanel.add(downPane, BorderLayout.CENTER);
        outPanel.add(downPanel, BorderLayout.SOUTH);
    }

    private void checkItemNames(){
        if(!itName.equals("") && count == 0){
                SwingWorker<Void, Void> w = new SwingWorker<Void, Void>(){
                    String u = "";
                    String b = "";
                    public Void doInBackground(){
                        u = item.genUnitName(itName);
                        b = item.genBulkName(itName);
                        return null;
                    }
                    @Override
                    public void done(){
                        unitItemTxt.setText(u);
                        bulkItemTxt.setText(b);
                        itemTxt.setText(itName);
                    }
                };
                w.execute();
        }
    }

    private JPanel wholeSalesPane(){
        JPanel output = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        descLbl = new JLabel("Description :");
        priceLbl = new JLabel("Price :");
        stockLbl = new JLabel("Opening Stock :");

        descLbl.setHorizontalAlignment(SwingConstants.CENTER);
        descLbl.setFont(new Font("Tahoma", 0, 12));
        descLbl.setPreferredSize(new Dimension(180, 30));

        priceLbl.setHorizontalAlignment(SwingConstants.CENTER);
        priceLbl.setFont(new Font("Tahoma", 0, 12));
        priceLbl.setPreferredSize(new Dimension(180, 30));

        stockLbl.setHorizontalAlignment(SwingConstants.CENTER);
        stockLbl.setFont(new Font("Tahoma", 0, 12));
        stockLbl.setPreferredSize(new Dimension(180, 30));
        //set the generic panels

        bulkItemTxt.setFont(new Font("Tahoma", 0, 12));
        bulkItemTxt.setPreferredSize(new Dimension(180, 30));

        bulkPriceTxt.setFont(new Font("Tahoma", 0, 12));
        bulkPriceTxt.setPreferredSize(new Dimension(180, 30));

        bulkStockTxt.setFont(new Font("Tahoma", 0, 12));
        bulkStockTxt.setPreferredSize(new Dimension(180, 30));

        output.add(descLbl);
        output.add(bulkItemTxt);
        output.add(priceLbl);
        output.add(bulkPriceTxt);
        output.add(stockLbl);
        output.add(bulkStockTxt);

        return output;
    }

    private JPanel retailSalesPane(){
        JPanel output = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        descLbl = new JLabel("Description :");
        priceLbl = new JLabel("Price :");
        stockLbl = new JLabel("Opening Stock :");

        descLbl.setHorizontalAlignment(SwingConstants.CENTER);
        descLbl.setFont(new Font("Tahoma", 0, 12));
        descLbl.setPreferredSize(new Dimension(180, 30));

        priceLbl.setHorizontalAlignment(SwingConstants.CENTER);
        priceLbl.setFont(new Font("Tahoma", 0, 12));
        priceLbl.setPreferredSize(new Dimension(180, 30));

        stockLbl.setHorizontalAlignment(SwingConstants.CENTER);
        stockLbl.setFont(new Font("Tahoma", 0, 12));
        stockLbl.setPreferredSize(new Dimension(180, 30));
        //set the generic panels

        unitItemTxt.setFont(new Font("Tahoma", 0, 12));
        unitItemTxt.setPreferredSize(new Dimension(180, 30));

        unitPriceTxt.setFont(new Font("Tahoma", 0, 12));
        unitPriceTxt.setPreferredSize(new Dimension(180, 30));

        unitStockTxt.setFont(new Font("Tahoma", 0, 12));
        unitStockTxt.setPreferredSize(new Dimension(180, 30));

        output.add(descLbl);
        output.add(unitItemTxt);
        output.add(priceLbl);
        output.add(unitPriceTxt);
        output.add(stockLbl);
        output.add(unitStockTxt);
        
        return output;
    }

    private void loadActions(){
        salesModCombo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                tabModifier(salesModCombo.getSelectedIndex());
            }
        });
        
        addBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                addBtn.setEnabled(false);
                SwingWorker<Void, Void> w = new SwingWorker<Void, Void>(){
                    public Void doInBackground(){
                        validateAnd_Add();
                        return null;
                    }
                    @Override
                    public void done(){
                        addBtn.setEnabled(true);
                    }
                };
                w.execute();
            }
        });

        cancelBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                popH.stopPopHandler();
                if(salesModule == 2){
                    //the timer is only started at generic module
                    //so, make sure its on generic before trying to stop the timer
                    if(t.isRunning()){
                        t.stop();
                    }
                }
                dispose();
            }
        });
    }

    public void focusGained(FocusEvent e){
        if(e.getSource() == itemTxt){
            checkIn.passAnotherInput(itemTxt.getText());
            if(checkIn.isGoodInput()){
                //stockTxt.setText(String.valueOf(InputsManager.allowOnlyIntegers(stockTxt.getText())));
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        if(!checkT.isRunning()){
                            count = 3;
                            checkT.start();
                        }
                    }
                });
                validateState = true;
            }
            else{
                InputsManager.paint_unpaintTextFields(itemTxt, false);
                validateState = false;
            }
        }
    }

    public void focusLost(FocusEvent e){
        if(e.getSource() == itemTxt){
            //check it
            checkIn.passAnotherInput(itemTxt.getText());
            if(checkIn.isGoodInput()){
                itemTxt.setText(InputsManager.makeUppercase(itemTxt.getText()));
                InputsManager.paint_unpaintTextFields(itemTxt, true);
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        if(checkT.isRunning()){
                            checkT.stop();
                            count = 0;
                            checkItemNames();
                        }
                    }
                });
                validateState = true;
            }
            else{
                InputsManager.paint_unpaintTextFields(itemTxt, false);
                validateState = false;
            }
        }
        else if(e.getSource() == bulkPriceTxt){
            bulkPriceTxt.setText(InputsManager.formatNairaTextField(bulkPriceTxt));
            InputsManager.paint_unpaintTextFields(bulkPriceTxt, true);
        }
        else if(e.getSource() == bulkStockTxt){
            checkIn.passAnotherInput(bulkStockTxt.getText());
            if(checkIn.isGoodInput()){
                bulkStockTxt.setText(String.valueOf(InputsManager.allowOnlyIntegers(bulkStockTxt.getText())));
                InputsManager.paint_unpaintTextFields(bulkStockTxt, true);
                validateState = true;
            }
            else{
                InputsManager.paint_unpaintTextFields(bulkStockTxt, false);
                validateState = false;
            }
        }
        else if(e.getSource() == unitPriceTxt){
            unitPriceTxt.setText(InputsManager.formatNairaTextField(unitPriceTxt));
            InputsManager.paint_unpaintTextFields(unitPriceTxt, true);
        }
        else if(e.getSource() == unitStockTxt){
            checkIn.passAnotherInput(unitStockTxt.getText());
            if(checkIn.isGoodInput()){
                unitStockTxt.setText(String.valueOf(InputsManager.allowOnlyIntegers(unitStockTxt.getText())));
                InputsManager.paint_unpaintTextFields(unitStockTxt, true);
                validateState = true;
            }
            else{
                InputsManager.paint_unpaintTextFields(unitStockTxt, false);
                validateState = false;
            }
        }
    }

    private void validateAnd_Add(){
        switch(customModule){
            case 1:
                //retails validation
                if(!itName.equals("") && !unitPriceTxt.getText().equals("") && !unitStockTxt.getText().equals("") && validateState){
                    //shows that all necessary fields are well filled
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                            try{
                                int s = add_Retails_Item_to_Db();
                                if(s == 1){
                                    //means the add was successful show success and ask for user to add another or not
                                    int op = JOptionPane.showConfirmDialog(self, "- New 'Retails' Item '" + itName +
                                            "' was Sucessfully ADDED -\nADD ANOTHER ITEM ?", "SUCCESS", JOptionPane.YES_NO_OPTION,
                                            JOptionPane.QUESTION_MESSAGE);
                                    if(op == 0){
                                        SwingWorker<Void, Void> w = new SwingWorker<Void, Void>(){
                                            public Void doInBackground(){
                                                new AddItemDialog(parent, con);
                                                return null;
                                            }
                                            @Override
                                            public void done(){
                                                popH.stopPopHandler();
                                                dispose();
                                                System.gc();
                                            }
                                        };
                                        w.execute();
                                    }
                                    else{
                                        popH.stopPopHandler();
                                        dispose();
                                    }
                                }
                                else{
                                    //its not successful ... show error dialog
                                    JOptionPane.showMessageDialog(self, "- Cannot add this item at this time. (Try Again) -", "DATABASE ERROR",
                                            JOptionPane.PLAIN_MESSAGE);
                                }
                            }
                            catch(SQLException sE){
                                System.err.println(sE.getMessage());
                                JOptionPane.showMessageDialog(self, "- Cannot add this item at this time. (ALREADY EXIST) -",
                                    "ADD ERROR", JOptionPane.PLAIN_MESSAGE);
                            }
                        }
                    });
                }
                else{
                    JOptionPane.showMessageDialog(self, "- Form Contains Invalid DATAS -", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
                }
                break;
            case 2:
                //wholesales validations
                if(!itName.equals("") && !bulkPriceTxt.getText().equals("") && !bulkStockTxt.getText().equals("") && validateState){
                    //shows that all necessary fields are well filled
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                            try{
                                int s = add_Wholesales_Item_to_Db();
                                if(s == 1){
                                    //means the add was successful show success and ask for user to add another or not
                                    int op = JOptionPane.showConfirmDialog(self, "- New 'WholeSales' Item '" + itName +
                                            "' was Sucessfully ADDED -\nADD ANOTHER ITEM ?", "SUCCESS", JOptionPane.YES_NO_OPTION,
                                            JOptionPane.QUESTION_MESSAGE);
                                    if(op == 0){
                                        SwingWorker<Void, Void> w = new SwingWorker<Void, Void>(){
                                            public Void doInBackground(){
                                                new AddItemDialog(parent, con);
                                                return null;
                                            }
                                            @Override
                                            public void done(){
                                                popH.stopPopHandler();
                                                dispose();
                                                System.gc();
                                            }
                                        };
                                        w.execute();
                                    }
                                    else{
                                        popH.stopPopHandler();
                                        dispose();
                                    }
                                }
                                else{
                                    //its not successful ... show error dialog
                                    JOptionPane.showMessageDialog(self, "- Cannot add this item at this time. (Try Again) -", "DATABASE ERROR",
                                            JOptionPane.PLAIN_MESSAGE);
                                }
                            }
                            catch(SQLException sE){
                                System.err.println(sE.getMessage());
                                JOptionPane.showMessageDialog(self, "- Cannot add this item at this time. (ALREADY EXIST) -",
                                    "ADD ERROR", JOptionPane.PLAIN_MESSAGE);
                            }
                        }
                    });
                }
                else{
                    JOptionPane.showMessageDialog(self, "- Form Contains Invalid DATAS -", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
                }
                break;
            case 3:
                //validate both module
                if(!itName.equals("") && !bulkPriceTxt.getText().equals("") && !bulkStockTxt.getText().equals("")
                        && !unitPriceTxt.getText().equals("") && !unitStockTxt.getText().equals("") && validateState){
                    //shows that all necessary fields are well filled
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                            try{
                                int s = add_Generic_Item_to_Db();
                                if(s == 1){
                                    //means the add was successful show success and ask for user to add another or not
                                    int op = JOptionPane.showConfirmDialog(self, "- New 'Bulk & Retail' Items '" + itName +
                                            "' was Sucessfully ADDED -\nADD ANOTHER ITEM ?", "SUCCESS", JOptionPane.YES_NO_OPTION,
                                            JOptionPane.QUESTION_MESSAGE);
                                    if(op == 0){
                                        SwingWorker<Void, Void> w = new SwingWorker<Void, Void>(){
                                            public Void doInBackground(){
                                                new AddItemDialog(parent, con);
                                                return null;
                                            }
                                            @Override
                                            public void done(){
                                                popH.stopPopHandler();
                                                dispose();
                                                System.gc();
                                            }
                                        };
                                        w.execute();
                                    }
                                    else{
                                        popH.stopPopHandler();
                                        dispose();
                                    }
                                }
                                else{
                                    //its not successful ... show error dialog
                                    JOptionPane.showMessageDialog(self, "- Cannot add this item at this time. (Try Again) -", "DATABASE ERROR",
                                            JOptionPane.PLAIN_MESSAGE);
                                }
                            }
                            catch(SQLException sE){
                                System.err.println(sE.getMessage());
                                JOptionPane.showMessageDialog(self, "- Cannot add this item at this time. (ALREADY EXIST) -",
                                    "ADD ERROR", JOptionPane.PLAIN_MESSAGE);
                            }
                        }
                    });
                }
                else{
                    JOptionPane.showMessageDialog(self, "- Form Contains Invalid DATAS -", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
                }
                break;
            default:
                //do not validate.. show an error
                JOptionPane.showMessageDialog(self, "- Cannot add this item at this time. (Restart Application) -", "CRITICAL ERROR",
                        JOptionPane.PLAIN_MESSAGE);
                break;
        }
    }

    private int add_Retails_Item_to_Db() throws SQLException{
        int state = 0;
        String s = new String(unitPriceTxt.getText());
        int p = Integer.valueOf(s.substring(4));
        state = stat.executeUpdate("INSERT INTO comodities_unit (name, stock_qty, unit_price) VALUES ('" + unitItemTxt.getText() + "', '"
                + unitStockTxt.getText() + "', '" + p + "')");
        return state;
    }

    private int add_Wholesales_Item_to_Db() throws SQLException{
        int state = 0;
        String s = new String(bulkPriceTxt.getText());
        int p = Integer.valueOf(s.substring(4));
        state = stat.executeUpdate("INSERT INTO comodities_bulk (name, stock_qty, unit_price) VALUES ('" + bulkItemTxt.getText() + "', '"
                + bulkStockTxt.getText() + "', '" + p + "')");
        return state;
    }

    private int add_Generic_Item_to_Db() throws SQLException{
        int state = 0;
        String s = new String(bulkPriceTxt.getText());
        int p = Integer.valueOf(s.substring(4));
        try{
            Thread.sleep(100);
            state = stat.executeUpdate("INSERT INTO comodities_bulk (name, stock_qty, unit_price) VALUES ('" + bulkItemTxt.getText() + "', '"
                + bulkStockTxt.getText() + "', '" + p + "')");
            Thread.sleep(100);
            s = new String(unitPriceTxt.getText()); //re-assign new values of unit
            p = Integer.valueOf(s.substring(4));
            if(state == 1){
                state = stat.executeUpdate("INSERT INTO comodities_unit (name, stock_qty, unit_price) VALUES ('" + unitItemTxt.getText() + "', '"
                        + unitStockTxt.getText() + "', '" + p + "')");
            }
        }
        catch(InterruptedException iE){}
        return state;
    }

    private class BackgroundJob extends SwingWorker<Void, Void>{
        public BackgroundJob(){

        }

        public Void doInBackground(){
            work();
            return null;
        }

        private void work(){
            t = new Timer(700, new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        //in this timer... set the
                        if(smthHappened){
                            switch(salesModule){
                                case 0:
                                    downPane.removeAll();
                                    downPane.add("@ Retail", retailSalesPane());//means the user selected retails
                                    downPane.add("@ Wholesales", wholeSalesPane());//add wholesales panel only
                                    customModule = 3;
                                    break;
                                case 1:
                                    //means the user selected wholesales
                                    downPane.removeAll();
                                    downPane.add("@ Wholesales", wholeSalesPane());//add wholesales panel only
                                    customModule = 2;
                                    break;
                                case 2:
                                    downPane.removeAll();
                                    //means the user selected wholesales and retails
                                    downPane.add("@ Retail", retailSalesPane());//means the user selected retails
                                    customModule = 1;
                                    break;
                                default:
                                    //the user hasnt selected anything yet
                                    break;
                            }
                            downPane.repaint();
                            downPane.validate();
                            repaint();
                            validate();
                            smthHappened = false;
                        }
                    }
                });
                if(!t.isRunning()){
                    t.start();
                }
        }
    }
}
