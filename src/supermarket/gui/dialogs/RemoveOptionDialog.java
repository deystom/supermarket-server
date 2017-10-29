/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui.dialogs;
import supermarket.gui.util.*;
import supermarket.gui.AdminFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 *
 * @author Segun
 */
public class RemoveOptionDialog extends JDialog{

    private AdminFrame parent;
    private JDialog self = this;

    private Connection con;
    private Statement stat;
    private ResultSet result;

    private JPanel outPanel;
    private JButton removeItemBtn;
    private JButton removeStockBtn;
    private JButton cancelBtn;
    private boolean removeState = false;
    private Thread dbTh;

    private PopDialogParentHandler popH;

    private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    
    public RemoveOptionDialog(AdminFrame p, Connection c){
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

        removeItemBtn = new JButton("Remove Item");
        removeStockBtn = new JButton("Reduce Stock");
        cancelBtn = new JButton("Cancel");

        removeItemBtn.setPreferredSize(new Dimension(125, 30));
        removeItemBtn.setHorizontalAlignment(SwingConstants.CENTER);
        removeItemBtn.setBackground(new Color(65, 105, 225));
        removeItemBtn.setForeground(Color.WHITE);
        removeItemBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
        removeItemBtn.setEnabled(removeState);

        removeStockBtn.setPreferredSize(new Dimension(125, 30));
        removeStockBtn.setHorizontalAlignment(SwingConstants.CENTER);
        removeStockBtn.setBackground(new Color(65, 105, 225));
        removeStockBtn.setForeground(Color.WHITE);
        removeStockBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
        removeStockBtn.setEnabled(removeState);

        cancelBtn.setPreferredSize(new Dimension(125, 30));
        cancelBtn.setHorizontalAlignment(SwingConstants.CENTER);
        cancelBtn.setBackground(new Color(65, 105, 225));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        outPanel.add(removeItemBtn);
        outPanel.add(removeStockBtn);
        outPanel.add(cancelBtn);
    }

    private void loadActions(){
        removeItemBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                new RemoveItemDialog(parent, con, parent.getPanelManagerInstance().getSelectedIndexOfItems());
                popH.stopPopHandler();
                dispose();
            }
        });

        removeStockBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                new UpdateStockDialog(parent, con, parent.getPanelManagerInstance().getSelectedIndexOfItems());
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
            removeState = true;
        }
        else{
            result = stat.executeQuery("SELECT * FROM comodities_unit");
            if(result.first()){
                removeState = true;
            }
            else{
                removeState = false;
            }
        }
    }

}
