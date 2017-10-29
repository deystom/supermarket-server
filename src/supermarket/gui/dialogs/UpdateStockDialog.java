/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui.dialogs;
import supermarket.gui.util.*;
import supermarket.gui.AdminFrame;
import supermarket.utility.InputsManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

/**
 *
 * @author Segun
 */
public class UpdateStockDialog extends JDialog{

    private JDialog self = this;

    private AdminFrame parent;
    private Connection con;
    private Statement stat;
    private ResultSet result;

    private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();;

    private JPanel outPanel;
    private JPanel topPanel;
    private JPanel downPanel;
    private JLabel itemLbl;
    private JLabel curStockLbl;
    private JLabel redStockLbl;
    private JTextField curStockTxt;
    private JTextField redStockTxt;
    private JButton removeBtn;
    private JButton cancelBtn;
    private JComboBox itemsCombo;

    private PopDialogParentHandler popH;

    ///////
    private Vector <String>itemsV;
    private Vector <String>stockV;
    private Thread th;
    private String selectedItem = "";
    private int curStock = 0; //the currect stock in the database
    private int redStock = 0; //new stock to be updated in database

    private int index = 0;

    public UpdateStockDialog(AdminFrame p, Connection c, int i){
        parent = p;
        con = c;
        index = i;
        itemsV = new Vector<String>();
        stockV = new Vector<String>();
        itemsV.addElement("Select an Item ....");
        stockV.addElement("0");

        ////////////////////////////////////////////////////////////
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                popH = new PopDialogParentHandler(parent, self);
            }
        });
        ///////////////////////////////////////////////////////////

        setSize(400, 160);
        setLocation((d.width - 400) / 2, (d.height - 160) /2);
        setAlwaysOnTop(true);
        setUndecorated(true);
        ///////
        MyLookAndFeel.setLook();
        //////
        th = new Thread(new Runnable(){
            public void run(){
                try{
                    loadItemsInfoFromDB();
                }
                catch(SQLException e){
                    System.err.println("Error at preloading name of COMODITIES already in the DB : " + e.getMessage());
                }
            }
        });
        if(!th.isAlive()){
            th.start();
        }
        initComponents();
        setContentPane(outPanel);
        loadActions();
        /*
         *
         */
        //setVisible(true);
    }

    private void initComponents(){

        outPanel = new JPanel(new BorderLayout(10, 10));
        topPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        downPanel = new JPanel(new BorderLayout(5, 5));

        itemLbl = new JLabel("Select Item :");
        curStockLbl = new JLabel("Currect Stock :");
        redStockLbl = new JLabel("Reduce Stock :");
        removeBtn = new JButton("REDUCE");
        cancelBtn = new JButton("CANCEL");
        itemsCombo = new JComboBox(itemsV);
        curStockTxt = new JTextField("0");
        redStockTxt = new JTextField("");

        outPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        outPanel.setPreferredSize(new Dimension(400, 160));
        outPanel.setBackground(new Color(152, 213, 152));

        topPanel.setPreferredSize(new Dimension(380, 120));
        topPanel.setBackground(new Color(152, 213, 152));

        downPanel.setPreferredSize(new Dimension(380, 35));
        downPanel.setBackground(new Color(152, 213, 152));

        itemLbl.setHorizontalAlignment(SwingConstants.CENTER);
        itemLbl.setFont(new Font("Tahoma", 0, 12));
        itemLbl.setPreferredSize(new Dimension(180, 30));

        curStockLbl.setHorizontalAlignment(SwingConstants.CENTER);
        curStockLbl.setFont(new Font("Tahoma", 0, 12));
        curStockLbl.setPreferredSize(new Dimension(180, 30));

        redStockLbl.setHorizontalAlignment(SwingConstants.CENTER);
        redStockLbl.setFont(new Font("Tahoma", 0, 12));
        redStockLbl.setPreferredSize(new Dimension(180, 30));

        itemsCombo.setFont(new Font("Verdana", 0, 12));
        itemsCombo.setPreferredSize(new Dimension(180, 30));

        curStockTxt.setFont(new Font("Verdana", 0, 12));
        curStockTxt.setPreferredSize(new Dimension(180, 30));
        curStockTxt.setEnabled(false);

        redStockTxt.setFont(new Font("Verdana", 0, 12));
        redStockTxt.setPreferredSize(new Dimension(180, 30));

        removeBtn.setPreferredSize(new Dimension(180, 30));
        removeBtn.setHorizontalAlignment(SwingConstants.CENTER);
        removeBtn.setBackground(new Color(65, 105, 225));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        cancelBtn.setPreferredSize(new Dimension(180, 30));
        cancelBtn.setHorizontalAlignment(SwingConstants.CENTER);
        cancelBtn.setBackground(new Color(65, 105, 225));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        topPanel.add(itemLbl);
        topPanel.add(itemsCombo);
        topPanel.add(curStockLbl);
        topPanel.add(curStockTxt);
        topPanel.add(redStockLbl);
        topPanel.add(redStockTxt);
        

        downPanel.add(removeBtn, BorderLayout.CENTER);
        downPanel.add(cancelBtn, BorderLayout.EAST);

        outPanel.add(topPanel, BorderLayout.CENTER);
        outPanel.add(downPanel, BorderLayout.SOUTH);
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
                selectedItem = itemsCombo.getSelectedItem().toString();
                curStock = Integer.parseInt(stockV.elementAt(itemsCombo.getSelectedIndex()));
                curStockTxt.setText(stockV.elementAt(itemsCombo.getSelectedIndex()));
                repaint();
                validate();
            }
        });

        removeBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                validate_andUpdateStock();
            }
        });
    }

    private void loadItemsInfoFromDB() throws SQLException{
        switch(index){
            case 1:
                stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                result = stat.executeQuery("SELECT name, stock_qty FROM comodities_unit");
                if(result.first()){
                    do{
                        itemsV.addElement(result.getString("name"));
                        stockV.addElement(String.valueOf(result.getInt("stock_qty")));
                    }
                    while(result.next());
                }
                else{
                    itemsV.addElement("");
                }
                break;
            case 2:
                stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                result = stat.executeQuery("SELECT name, stock_qty FROM comodities_bulk");
                if(result.first()){
                    do{
                        itemsV.addElement(result.getString("name"));
                        stockV.addElement(String.valueOf(result.getInt("stock_qty")));
                    }
                    while(result.next());
                }
                else{
                    itemsV.addElement("");
                }
                break;
            default:

        }
    }

    private void validate_andUpdateStock(){
        long getInput = 0;
        if(itemsCombo.getSelectedIndex() == 0){
            //means the user hasnt selected an Item
            JOptionPane.showMessageDialog(self, "Select an ITEM to Reduce its Stock", "COMMAND ERROR", JOptionPane.PLAIN_MESSAGE);
        }
        else{
            getInput = InputsManager.allowOnlyIntegers(redStockTxt.getText()); //make sure the input is an Integer
            if(getInput != 0){
                redStock = curStock - (int)getInput;
                try{
                    update_items_stock();
                }
                catch(SQLException se){
                    System.err.println("Error occured at trying to delete comodity from Database : " + se.getMessage());
                }
            }
            else{
                //that means the input value is not valid or is equal to zero
                JOptionPane.showMessageDialog(self, "Invalid value provided to reduce from stock", "INPUT ERROR", JOptionPane.PLAIN_MESSAGE);
            }
            popH.stopPopHandler();
            dispose();
        }
    }

    public void update_items_stock() throws SQLException{
        int status;
        switch(index){
            case 1:
                stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                status = stat.executeUpdate("UPDATE comodities_unit SET stock_qty=" + redStock + " WHERE name='" + selectedItem + "'");
                if(status != 0){
                    //that means update was successful
                    JOptionPane.showMessageDialog(self, "- Item '" + selectedItem + "' stock was successfully updated -", "SUCCESS", JOptionPane.PLAIN_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(self, "- Cannot update the Item '" + selectedItem + "' stock at this time. -",
                            "REMOVE ERROR", JOptionPane.PLAIN_MESSAGE);
                }
                break;
            case 2:
                stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                status = stat.executeUpdate("UPDATE comodities_bulk SET stock_qty=" + redStock + " WHERE name='" + selectedItem + "'");
                if(status != 0){
                    //that means update was successful
                    JOptionPane.showMessageDialog(self, "- Item '" + selectedItem + "' stock was successfully updated -", "SUCCESS", JOptionPane.PLAIN_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(self, "- Cannot update the Item '" + selectedItem + "' stock at this time. -",
                            "REMOVE ERROR", JOptionPane.PLAIN_MESSAGE);
                }
                break;
            default:
        }

    }
    
}
