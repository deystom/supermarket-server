/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.later;
import supermarket.gui.util.*;
import supermarket.utility.InputsManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import supermarket.gui.dialogs.SpecifyUsersDialog;

/**
 *
 * @author MUSTAFA
 */
public class AddConstraintDialog extends JDialog{

    private JDialog self = this;

    private JFrame parent;
    private Connection con;
    private Statement stat;
    private ResultSet result;

    private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();;

    private JPanel outPanel;
    private JPanel centerPanel;
    private JPanel formPanel;
    private JPanel downPanel;
    private JLabel topLbl;
    private JLabel minLbl;
    private JLabel showNotLbl;
    private JLabel iconLbl;
    private JTextField minTxt;
    private JCheckBox showNotBox;
    private JButton specUserBtn;
    private JButton doneBtn;
    private JButton cancelBtn;

    //
    private PopDialogParentHandler popH;
    //

    private SpecifyUsersDialog specU;

    private InputsManager checkIn;
    private String itemName;
    private Timer selectedTimer;
    private Vector <String>userV;

    public AddConstraintDialog(JFrame p, Connection c, String it){
        parent = p;
        con = c;
        itemName = it;
        checkIn = new InputsManager("");
        userV = new Vector<String>();

        ////////////////////////////////////////////////////////////
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                popH = new PopDialogParentHandler(parent, self);
            }
        });
        ///////////////////////////////////////////////////////////

        setSize(380, 200);
        setLocation((d.width - 380) / 2, (d.height - 200) /2);
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
        //setVisible(true);
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                specU = new SpecifyUsersDialog(self, con);
                //inistialize the timer thread to wait for user to click before starting
                triggerUserSelectedTimer();
            }
        });
        repaint();
        validate();
        //after the Dialog is visible.. work on the database
    }

    private void initComponents(){

        outPanel = new JPanel(new BorderLayout());
        centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        downPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        topLbl = new JLabel(itemName + " CONSTRAINT CONFIGURATION");
        doneBtn = new JButton("Done");
        specUserBtn = new JButton("Unique Remote Users to Sell : " + itemName);
        cancelBtn = new JButton("Cancel");
        showNotBox = new JCheckBox();
        minLbl = new JLabel("SET MINIMUM STOCK");
        showNotLbl = new JLabel("SHOW NOTIFICATION ?");
        minTxt = new JTextField();
        iconLbl = new JLabel();

        outPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        outPanel.setPreferredSize(new Dimension(380, 200));
        outPanel.setBackground(new Color(152, 213, 152));

        centerPanel.setPreferredSize(new Dimension(370, 110));
        centerPanel.setBackground(new Color(152, 213, 152));
        //constPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        downPanel.setPreferredSize(new Dimension(370, 40));
        downPanel.setBackground(new Color(152, 213, 152));
        downPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        formPanel.setPreferredSize(new Dimension(350, 80));
        formPanel.setBackground(new Color(152, 213, 152));

        topLbl.setPreferredSize(new Dimension(370, 30));
        topLbl.setFont(new Font("Tahoma", 1, 11));
        topLbl.setHorizontalAlignment(SwingConstants.CENTER);
        topLbl.setBackground(new Color(152, 213, 152));

        showNotBox.setPreferredSize(new Dimension(120, 30));
        showNotBox.setToolTipText("Click to answer YES!");
        showNotBox.setHorizontalAlignment(SwingConstants.CENTER);

        minLbl.setPreferredSize(new Dimension(120, 30));
        minLbl.setFont(new Font("Tahoma", 0, 11));
        minLbl.setHorizontalAlignment(SwingConstants.CENTER);

        minTxt.setPreferredSize(new Dimension(120, 30));
        minTxt.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 12));
        minTxt.setHorizontalAlignment(SwingConstants.CENTER);

        showNotLbl.setPreferredSize(new Dimension(120, 30));
        showNotLbl.setFont(new Font("Tahoma", 0, 11));
        showNotLbl.setHorizontalAlignment(SwingConstants.CENTER);

        doneBtn.setPreferredSize(new Dimension(100, 30));
        doneBtn.setHorizontalAlignment(SwingConstants.CENTER);
        doneBtn.setBackground(new Color(65, 105, 225));
        doneBtn.setForeground(Color.WHITE);
        doneBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        cancelBtn.setPreferredSize(new Dimension(100, 30));
        cancelBtn.setHorizontalAlignment(SwingConstants.CENTER);
        cancelBtn.setBackground(new Color(65, 105, 225));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        specUserBtn.setPreferredSize(new Dimension(280, 30));
        specUserBtn.setHorizontalAlignment(SwingConstants.CENTER);
        specUserBtn.setBackground(new Color(65, 105, 225));
        specUserBtn.setForeground(Color.WHITE);
        specUserBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        iconLbl.setPreferredSize(new Dimension(50, 30));
        iconLbl.setHorizontalAlignment(SwingConstants.CENTER);
        iconLbl.setBackground(new Color(152, 213, 152));

        cancelBtn.setPreferredSize(new Dimension(100, 30));
        cancelBtn.setHorizontalAlignment(SwingConstants.CENTER);
        cancelBtn.setBackground(new Color(65, 105, 225));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        formPanel.add(minLbl);
        formPanel.add(minTxt);
        formPanel.add(showNotLbl);
        formPanel.add(showNotBox);


        centerPanel.add(formPanel);
        centerPanel.add(specUserBtn);

        downPanel.add(doneBtn);
        downPanel.add(cancelBtn);

        outPanel.add(topLbl, BorderLayout.NORTH);
        outPanel.add(centerPanel, BorderLayout.CENTER);
        outPanel.add(downPanel, BorderLayout.SOUTH);
        //pack();

    }

    private void loadActions(){
        cancelBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(selectedTimer.isRunning()){
                    selectedTimer.stop();
                }
                popH.stopPopHandler();
                dispose();
            }
        });

        doneBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(!minTxt.getText().equals("")){
                    performAdd();
                }
                else{
                    JOptionPane.showMessageDialog(self, "- Form Contains Invalid DATA -", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        specUserBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                specU.triggerDialog();
                if(!selectedTimer.isRunning()){
                    selectedTimer.start();
                }
            }
        });

    }

    private void performAdd(){
        //first check the integer value
        checkIn.passAnotherInput(minTxt.getText());
        if(checkIn.isGoodInput()){
            //then ... allow only integer
            @SuppressWarnings("static-access")
            long min = checkIn.allowOnlyIntegers(minTxt.getText());
            int status = 0;
            if(showNotBox.isSelected()){
                status = 1;
            }
            //then start the database work
            try{
                stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                int update = stat.executeUpdate("INSERT INTO items_const_stock (name, min_stock, show_status) VALUES ('" + itemName +
                        "', " + min + ", " + status + ")");
                if(update != 0){
                    //if first update is successful... do second one
                    for(int i = 0; i < userV.size(); i++){
                        update = stat.executeUpdate("INSERT INTO items_const_users (item_name, user) VALUES ('" + itemName +
                                "', '" + userV.elementAt(i) + "')");
                    }
                    if(update != 0){
                        JOptionPane.showMessageDialog(self, "- Constraint Successfully added to '" + itemName + "' -", "SUCCESS", JOptionPane.PLAIN_MESSAGE);
                    }
                    //after update is complete ... check !
                    if(selectedTimer.isRunning()){
                        selectedTimer.stop();
                    }
                    popH.stopPopHandler();
                    dispose();
                }
                else{
                    JOptionPane.showMessageDialog(self, "- Cannot add CONSTRAINTS at this time. (Try Later) -", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
                }
            }
            catch(SQLException e){
                System.err.println("Error occured at Adding Constraint to the Item : " + e.getMessage());
            }
        }
    }

    private void saveSpecifiedUsers(){
        userV = specU.getListOfSelectedUsers();
        if(!userV.isEmpty()){
            //then start to update table constraint
            iconLbl.setIcon(new ImageIcon(getClass().getResource("icn_profile.gif")));
            centerPanel.add(iconLbl);
        }
        else{
            centerPanel.remove(iconLbl); //remove the icon

        }
        repaint();
        validate();
    }

    private void triggerUserSelectedTimer(){
        Thread t = new Thread(new Runnable(){
            public void run(){
                selectedTimer = new Timer(500, new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        saveSpecifiedUsers();
                    }
                });
            }
        });
        t.start();
    }
}
