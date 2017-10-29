/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui.dialogs;
import supermarket.gui.AdminFrame;
import supermarket.gui.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 *
 * @author Segun
 */
public class AddOptionDialog extends JDialog{

    private AdminFrame parent;
    private JDialog self = this;
    
    private Connection con;
    private Statement stat;
    private ResultSet result;

    private JPanel outPanel;
    private JButton addBtn;
    private JButton updateBtn;
    private JButton cancelBtn;
    private boolean updateState = false;
    private Thread dbTh;

    private PopDialogParentHandler popH;

    private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

    public AddOptionDialog(AdminFrame p, Connection c){
        parent = p;
        con = c;
        ////////////////////////////////////////////////////////////
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                popH = new PopDialogParentHandler(parent, self);
            }
        });
        ///////////////////////////////////////////////////////////
        

        dbTh = new Thread(new Runnable(){
            public void run(){
                try{
                   checkUpdateBtn();
                }
                catch(SQLException sE){
                    System.err.println("Error checking if comodities exist in DB : " + sE.getMessage());
                }
            }
        });
        ////////////////
        if(!dbTh.isAlive()){
            dbTh.start();
            //then change the button state
            repaint();
            validate();
        }
        ///////////////
        setAlwaysOnTop(true);
        setUndecorated(true);
        ///////
        MyLookAndFeel.setLook();
        //////
        setSize(400, 50);
        //parent.setLocation((d.width - parent.getWidth()) / 2, (d.height = parent.getHeight()) / 2);
        setLocation((d.width  - 400 )/ 2, (d.height  - 50 )/ 2);
        initComponents();
        setContentPane(outPanel);
        loadActions();
        /*
         * 
         */
        //setVisible(true);
        /////////////////////
    }

    private void initComponents(){
        outPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 10));
        outPanel.setPreferredSize(new Dimension(400, 50));
        outPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        outPanel.setBackground(new Color(152, 213, 152));

        addBtn = new JButton("Add Item");
        updateBtn = new JButton("Update Items");
        cancelBtn = new JButton("Cancel");

        addBtn.setPreferredSize(new Dimension(125, 30));
        addBtn.setHorizontalAlignment(SwingConstants.CENTER);
        addBtn.setBackground(new Color(65, 105, 225));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        updateBtn.setPreferredSize(new Dimension(125, 30));
        updateBtn.setHorizontalAlignment(SwingConstants.CENTER);
        updateBtn.setBackground(new Color(65, 105, 225));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
        updateBtn.setEnabled(updateState);

        cancelBtn.setPreferredSize(new Dimension(125, 30));
        cancelBtn.setHorizontalAlignment(SwingConstants.CENTER);
        cancelBtn.setBackground(new Color(65, 105, 225));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        outPanel.add(addBtn);
        outPanel.add(updateBtn);
        outPanel.add(cancelBtn);
    }
    
    private void loadActions(){
        addBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                new AddItemDialog(parent, con);
                popH.stopPopHandler();
                dispose();
            }
        });

        updateBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                new UpdateItemDialog(parent, con, parent.getPanelManagerInstance().getSelectedIndexOfItems()); //collect the index of the selected dialog
                popH.stopPopHandler();
                dispose();
            }
        });

        cancelBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                popH.stopPopHandler();
                dispose();
            }
        });
    }
    
    private void checkUpdateBtn() throws SQLException{
        stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        result = stat.executeQuery("SELECT * FROM comodities_unit");
        if(result.first()){
            updateState = true;
        }
        else{
            result = stat.executeQuery("SELECT * FROM comodities_bulk");
            if(result.first()){
                updateState = true;
            }
            else{
                updateState = false;
            }
        }
    }

}

