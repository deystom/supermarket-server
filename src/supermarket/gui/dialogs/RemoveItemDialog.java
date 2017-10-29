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
 * @author Segun
 */

public class RemoveItemDialog extends JDialog{

    private JDialog self = this;

    private JFrame parent;
    private Connection con;
    private Statement stat;
    private ResultSet result;

    private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();;

    private JPanel outPanel;
    private JPanel topPanel;
    private JPanel downPanel;
    private JLabel itemLbl;
    private JButton removeBtn;
    private JButton cancelBtn;
    private JComboBox itemsCombo;

    private PopDialogParentHandler popH;

    ///////
    private Vector <String>itemsV;
    private Thread th;

    ///
    private String selectedItem = "";
    ///
    private int index = 0;
    
    public RemoveItemDialog(JFrame p, Connection c, int i){
        parent = p;
        con = c;
        itemsV = new Vector<String>();
        itemsV.addElement("Select an Item ....");
        index = i;

        ////////////////////////////////////////////////////////////
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                popH = new PopDialogParentHandler(parent, self);
            }
        });
        ///////////////////////////////////////////////////////////

        setSize(400, 100);
        setLocation((d.width - 400) / 2, (d.height - 100) /2);
        setAlwaysOnTop(true);
        setUndecorated(true);
        ///////
        MyLookAndFeel.setLook();
        //////
        th = new Thread(new Runnable(){
            public void run(){
                try{
                    loadItemsNameFromDB();
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
        topPanel = new JPanel(new BorderLayout(10, 10));
        downPanel = new JPanel(new BorderLayout(10, 10));

        itemLbl = new JLabel("Select Item :");
        removeBtn = new JButton("REMOVE");
        cancelBtn = new JButton("CANCEL");
        itemsCombo = new JComboBox(itemsV);

        outPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        outPanel.setPreferredSize(new Dimension(400, 100));
        outPanel.setBackground(new Color(152, 213, 152));

        topPanel.setPreferredSize(new Dimension(380, 35));
        topPanel.setBackground(new Color(152, 213, 152));

        downPanel.setPreferredSize(new Dimension(380, 35));
        downPanel.setBackground(new Color(152, 213, 152));

        itemLbl.setHorizontalAlignment(SwingConstants.CENTER);
        itemLbl.setFont(new Font("Tahoma", 0, 12));
        itemLbl.setPreferredSize(new Dimension(180, 40));

        itemsCombo.setFont(new Font("Verdana", 0, 12));
        itemsCombo.setPreferredSize(new Dimension(180, 40));
        
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

        topPanel.add(itemsCombo, BorderLayout.CENTER);
        topPanel.add(itemLbl, BorderLayout.WEST);

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
            }
        });

        removeBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try{
                    delete_items_from_db();
                }
                catch(SQLException se){
                    System.err.println("Error occured at trying to delete comodity from Database : " + se.getMessage());
                }
            }
        });
    }

    private void loadItemsNameFromDB() throws SQLException{
        switch(index){
            case 1:
                stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                result = stat.executeQuery("SELECT name FROM comodities_unit");
                if(result.first()){
                    do{
                        itemsV.addElement(result.getString("name"));
                    }
                    while(result.next());
                }
                else{
                    itemsV.addElement("");
                }
                break;
            case 2:
                stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                result = stat.executeQuery("SELECT name FROM comodities_bulk");
                if(result.first()){
                    do{
                        itemsV.addElement(result.getString("name"));
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

    public void delete_items_from_db() throws SQLException{
        int status = 0;
        switch(index){
            case 1:
                if(itemsCombo.getSelectedIndex() == 0){
                    //means the user hasnt selected an Item
                    JOptionPane.showMessageDialog(self, "Select an ITEM to delete", "COMMAND ERROR", JOptionPane.PLAIN_MESSAGE);
                }
                else{
                    stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    status = stat.executeUpdate("DELETE FROM comodities_unit WHERE name='" + selectedItem + "'");
                    if(status != 0){
                        //delete was successful
                        JOptionPane.showMessageDialog(self, "- Item '" + selectedItem + "' was successfully removed -", "SUCCESS", JOptionPane.PLAIN_MESSAGE);
                    }
                    else{
                        //not successful
                        JOptionPane.showMessageDialog(self, "- Cannot remove the Item '" + selectedItem + "' at this time. -",
                                "REMOVE ERROR", JOptionPane.PLAIN_MESSAGE);
                    }
                    //then dispose and remove the dialog from display
                    popH.stopPopHandler();
                    dispose();
                }
                break;
            case 2:
                if(itemsCombo.getSelectedIndex() == 0){
                    //means the user hasnt selected an Item
                    JOptionPane.showMessageDialog(self, "Select an ITEM to delete", "COMMAND ERROR", JOptionPane.PLAIN_MESSAGE);
                }
                else{
                    stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    status = stat.executeUpdate("DELETE FROM comodities_bulk WHERE name='" + selectedItem + "'");
                    if(status != 0){
                        //delete was successful
                        JOptionPane.showMessageDialog(self, "- Item '" + selectedItem + "' was successfully removed -", "SUCCESS", JOptionPane.PLAIN_MESSAGE);
                    }
                    else{
                        //not successful
                        JOptionPane.showMessageDialog(self, "- Cannot remove the Item '" + selectedItem + "' at this time. -",
                                "REMOVE ERROR", JOptionPane.PLAIN_MESSAGE);
                    }
                    //then dispose and remove the dialog from display
                    popH.stopPopHandler();
                    dispose();
                }
                break;
                default:
        }
    }
}
