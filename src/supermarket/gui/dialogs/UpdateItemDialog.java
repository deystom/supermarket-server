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
public class UpdateItemDialog extends JDialog implements FocusListener{

    private JDialog self = this;

    private AdminFrame parent;
    private Connection con;
    private Statement stat;
    private ResultSet unit_result;
    private ResultSet bulk_result;

    private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();;

    private JPanel outPanel;
    private JPanel formPanel;
    private JLabel selectComLbl;
    private JLabel updatePriceLbl;
    private JLabel curStockLbl;
    private JLabel addStockLbl;
    private JComboBox itemsCombo;
    private JTextField curPriceTxt;
    private JTextField curStockTxt;
    private JTextField newStockTxt;
    private JButton updateBtn;
    private JButton cancelBtn;

    private PopDialogParentHandler popH;

    ///////
    private Vector <String>itemsV;
    private Vector <String>priceV;
    private Vector <Integer>stockV;

    private Thread th;

    private InputsManager checkIn;
    private boolean validateState = false;
    private int index = 0;
    
    public UpdateItemDialog(AdminFrame p, Connection c, int i){        
        parent = p;
        con = c;
        index = i;
        itemsV = new Vector<String>();
        priceV = new Vector<String>();
        stockV  = new Vector<Integer>();

        ///////
        itemsV.addElement("Select an Item .....");
        priceV.addElement("=N= 0");
        stockV.addElement(0);

        checkIn = new InputsManager("");

        ////////////////////////////////////////////////////////////
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                popH = new PopDialogParentHandler(parent, self);
            }
        });
        ///////////////////////////////////////////////////////////

        setSize(400, 200);
        setLocation((d.width - 400) / 2, (d.height - 200) /2);
        setAlwaysOnTop(true);
        setUndecorated(true);
        ///////
        MyLookAndFeel.setLook();
        //////
        //work on the database
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
        GridLayout grid = new GridLayout(5, 2, 4, 4);

        selectComLbl = new JLabel("Item Name :");
        updatePriceLbl = new JLabel("Update Price :");
        curStockLbl = new JLabel("Current Stock :");
        addStockLbl = new JLabel("Add to Stock :");
        itemsCombo = new JComboBox(itemsV);
        curPriceTxt = new JTextField("=N= 0");
        curPriceTxt.addFocusListener(this);
        curStockTxt = new JTextField("0");
        newStockTxt = new JTextField();
        newStockTxt.addFocusListener(this);
        updateBtn = new JButton("UPDATE");
        cancelBtn = new JButton("CANCEL");

        selectComLbl.setHorizontalAlignment(SwingConstants.CENTER);
        selectComLbl.setFont(new Font("Tahoma", 0, 12));
        selectComLbl.setPreferredSize(new Dimension(180, 30));

        updatePriceLbl.setHorizontalAlignment(SwingConstants.CENTER);
        updatePriceLbl.setFont(new Font("Tahoma", 0, 12));
        updatePriceLbl.setPreferredSize(new Dimension(180, 30));

        curStockLbl.setHorizontalAlignment(SwingConstants.CENTER);
        curStockLbl.setFont(new Font("Tahoma", 0, 12));
        curStockLbl.setPreferredSize(new Dimension(180, 30));

        addStockLbl.setHorizontalAlignment(SwingConstants.CENTER);
        addStockLbl.setFont(new Font("Tahoma", 0, 12));
        addStockLbl.setPreferredSize(new Dimension(180, 30));

        itemsCombo.setFont(new Font("Verdana", 0, 12));
        itemsCombo.setPreferredSize(new Dimension(180, 30));

        curPriceTxt.setFont(new Font("Verdana", 0, 12));
        curPriceTxt.setPreferredSize(new Dimension(180, 30));

        newStockTxt.setFont(new Font("Verdana", 0, 12));
        newStockTxt.setPreferredSize(new Dimension(180, 30));

        curStockTxt.setFont(new Font("Verdana", 0, 12));
        curStockTxt.setPreferredSize(new Dimension(180, 30));
        curStockTxt.setEnabled(false);

        updateBtn.setPreferredSize(new Dimension(180, 30));
        updateBtn.setHorizontalAlignment(SwingConstants.CENTER);
        updateBtn.setBackground(new Color(65, 105, 225));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        cancelBtn.setPreferredSize(new Dimension(180, 30));
        cancelBtn.setHorizontalAlignment(SwingConstants.CENTER);
        cancelBtn.setBackground(new Color(65, 105, 225));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        
        outPanel = new JPanel(new BorderLayout(10, 10));
        outPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        outPanel.setPreferredSize(new Dimension(400, 200));

        formPanel = new JPanel(grid);
        formPanel.setPreferredSize(new Dimension(380, 150));
        formPanel.setBackground(new Color(152, 213, 152));

        formPanel.add(selectComLbl);
        formPanel.add(itemsCombo);
        formPanel.add(updatePriceLbl);
        formPanel.add(curPriceTxt);
        formPanel.add(curStockLbl);
        formPanel.add(curStockTxt);
        formPanel.add(addStockLbl);
        formPanel.add(newStockTxt);
        formPanel.add(updateBtn);
        formPanel.add(cancelBtn);

        outPanel.add(formPanel, BorderLayout.CENTER);
    }

    public void focusGained(FocusEvent e){
        if(e.getSource() == curPriceTxt){
            //do nothing
        }
        else if(e.getSource() == newStockTxt){
            checkIn.passAnotherInput(newStockTxt.getText());
            if(checkIn.isGoodInput()){
                //newStockTxt.setText(String.valueOf(InputsManager.allowOnlyIntegers(curPriceTxt.getText())));
                InputsManager.paint_unpaintTextFields(newStockTxt, true);
                validateState = true;
            }
            else{
                InputsManager.paint_unpaintTextFields(newStockTxt, false);
                validateState = false;
            }
        }
    }

    public void focusLost(FocusEvent e){
        String x = "";
        if(e.getSource() == curPriceTxt){
            //checkIn.passAnotherInput(curPriceTxt.getText());
            try{
                x = String.valueOf(InputsManager.allowOnlyIntegers(curPriceTxt.getText().substring(4)));
            }
            catch(StringIndexOutOfBoundsException so){
                System.err.println("Was trying to fix an invalid naira data before : " + so.getMessage());
                x = curPriceTxt.getText();
            }
            curPriceTxt.setText("=N= " + x);
            //InputsManager.paint_unpaintTextFields(curPriceTxt, true);
        }
        else if(e.getSource() == newStockTxt){
            checkIn.passAnotherInput(newStockTxt.getText());
            if(checkIn.isGoodInput()){
                newStockTxt.setText(String.valueOf(InputsManager.allowOnlyIntegers(newStockTxt.getText())));
                InputsManager.paint_unpaintTextFields(newStockTxt, true);
                validateState = true;
            }
            else{
                InputsManager.paint_unpaintTextFields(newStockTxt, false);
                validateState = false;
            }
        }
    }
    
    private void loadItemsInfoFromDB() throws SQLException{
        switch(index){
            case 1:
                stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                unit_result = stat.executeQuery("SELECT name, unit_price, stock_qty FROM comodities_unit");
                if(unit_result.first()){
                    do{
                        itemsV.addElement(unit_result.getString("name"));
                        priceV.addElement("=N= " + String.valueOf(unit_result.getInt("unit_price")));
                        stockV.addElement(unit_result.getInt("stock_qty"));
                    }
                    while(unit_result.next());
                }
                else{
                    itemsV.addElement("");
                }
                break;
            case 2:
                stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                bulk_result = stat.executeQuery("SELECT name, unit_price, stock_qty FROM comodities_bulk");
                if(bulk_result.first()){
                    do{
                        itemsV.addElement(bulk_result.getString("name"));
                        priceV.addElement("=N= " + String.valueOf(bulk_result.getInt("unit_price")));
                        stockV.addElement(bulk_result.getInt("stock_qty"));
                    }
                    while(bulk_result.next());
                }
                else{
                    itemsV.addElement("");
                }
                break;
                default:
        }
    }

    private void loadActions(){
        itemsCombo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        loadItemDetails(itemsCombo.getSelectedIndex());
                    }
                });

            }
        });

        curPriceTxt.addFocusListener(new FocusListener(){
            String txt = curPriceTxt.getText();
            public void focusGained(FocusEvent e){               
                if(txt.startsWith("=N= ")){
                    curPriceTxt.setText("=N= ");
                }
                else{
                    curPriceTxt.addKeyListener(new KeyListener(){
                        public void keyPressed(KeyEvent e){
                            if(txt.startsWith("=N= ")){
                                //leave it
                            }
                            else{
                                txt = "=N= " + txt;
                                txt = txt + e.getKeyChar();
                                curPriceTxt.setText(txt);
                            }
                        }
                        public void keyReleased(KeyEvent e){}
                        public void keyTyped(KeyEvent e){}
                    });
                }
            }
            public void focusLost(FocusEvent e){
            }
        });

        updateBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        validateAnd_Add();
                    }
                });
            }
        });

        cancelBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                popH.stopPopHandler();
                dispose();
            }
        });
    }

    private void loadItemDetails(int i){
       curPriceTxt.setText(priceV.elementAt(i));
       curStockTxt.setText(stockV.elementAt(i).toString());
       repaint();
       validate();
    }

    private void validateAnd_Add(){
        if(validateState){
            //check if the comodity name provided already exist
                try{
                    doUpdates();
                }
                catch(SQLException e){
                    System.err.println("Error occured while trying to Add Item to db : " + e.getMessage());
                }
        }
        else{
           JOptionPane.showMessageDialog(self, "- Form Contains Invalid DATAS -", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
        }
    }
    
    private void doUpdates() throws SQLException{
        new SwingWorker<Void, Void>(){
            public Void doInBackground() throws SQLException{
                int updateStat = 0;
                int newStock = Integer.parseInt(curStockTxt.getText()) + Integer.parseInt(newStockTxt.getText()); //add old and new stock together
                int newPrice = Integer.parseInt(curPriceTxt.getText().substring(4));
                switch(index){
                    case 1:
                        stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        updateStat = stat.executeUpdate("UPDATE comodities_unit SET stock_qty=" + newStock + ", unit_price=" + newPrice + " WHERE name='" +
                        itemsCombo.getSelectedItem().toString() + "'");
                        if(updateStat != 0){
                            //then the update is successful
                            JOptionPane.showMessageDialog(self, "- Item '" + itemsCombo.getSelectedItem().toString() + "' was Sucessfully UPDATED -",
                                "SUCCESS", JOptionPane.PLAIN_MESSAGE);
                            popH.stopPopHandler();
                            dispose();
                        }
                        else{
                            JOptionPane.showMessageDialog(self, "- Cannot update this item at this time. (Try Later) -", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
                        }
                        break;
                    case 2:
                        stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        updateStat = stat.executeUpdate("UPDATE comodities_bulk SET stock_qty=" + newStock + ", unit_price=" + newPrice + " WHERE name='" +
                        itemsCombo.getSelectedItem().toString() + "'");
                        if(updateStat != 0){
                            //then the update is successful
                            JOptionPane.showMessageDialog(self, "- Item '" + itemsCombo.getSelectedItem().toString() + "' was Sucessfully UPDATED -",
                                "SUCCESS", JOptionPane.PLAIN_MESSAGE);
                            popH.stopPopHandler();
                            dispose();
                        }
                        else{
                            JOptionPane.showMessageDialog(self, "- Cannot update this item at this time. (Try Later) -", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
                        }
                        break;
                    default:
                }
                return null;
            }
        }.execute();
    }
}
