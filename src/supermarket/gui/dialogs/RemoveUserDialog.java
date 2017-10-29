/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui.dialogs;
import supermarket.gui.util.*;
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
public class RemoveUserDialog extends JDialog implements FocusListener{

    private JDialog self = this;

    private JFrame parent;
    private Connection con;
    private Statement stat;
    private ResultSet result;

    private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

    private JPanel outPanel;
    private JPanel formPanel;
    private JLabel nameLbl;
    private JLabel passwordLbl;
    private JComboBox nameCombo;
    private JTextField passwordTxt;
    private JButton removeBtn;
    private JButton cancelBtn;

    private PopDialogParentHandler popH;

    ///////
    private Vector <String>usersV;
    private Thread th;

    private InputsManager checkIn;
    private boolean validateState = false;

    public RemoveUserDialog(JFrame p, Connection c){
        parent = p;
        con = c;
        usersV = new Vector<String>();
        usersV.addElement("Select a NAME .....");
        checkIn = new InputsManager("");

        ////////////////////////////////////////////////////////////
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                popH = new PopDialogParentHandler(parent, self);
            }
        });
        ///////////////////////////////////////////////////////////

        setSize(400, 110);
        setLocation((d.width - 400) / 2, (d.height - 110) /2);
        setAlwaysOnTop(true);
        setUndecorated(true);
        ///////
        MyLookAndFeel.setLook();
        //////
        th = new Thread(new Runnable(){
            public void run(){
                try{
                    loadUserNameFromDB();
                }
                catch(SQLException e){
                    System.err.println("Error at preloading names of users already in the DB : " + e.getMessage());
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
        GridLayout grid = new GridLayout(3, 2, 4, 4);

        outPanel = new JPanel(new BorderLayout(10, 10));
        formPanel = new JPanel(grid);

        nameLbl = new JLabel("User Name :");
        passwordLbl = new JLabel("Verify Password :");
        nameCombo = new JComboBox(usersV);
        passwordTxt = new JTextField();
        removeBtn = new JButton("REMOVE");
        cancelBtn = new JButton("CANCEL");

        outPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        outPanel.setPreferredSize(new Dimension(400, 170));

        formPanel.setPreferredSize(new Dimension(380, 150));
        formPanel.setBackground(new Color(152, 213, 152));

        nameLbl.setHorizontalAlignment(SwingConstants.CENTER);
        nameLbl.setFont(new Font("Tahoma", 0, 12));
        nameLbl.setPreferredSize(new Dimension(180, 30));

        passwordLbl.setHorizontalAlignment(SwingConstants.CENTER);
        passwordLbl.setFont(new Font("Tahoma", 0, 12));
        passwordLbl.setPreferredSize(new Dimension(180, 30));

        nameCombo.setFont(new Font("Tahoma", 0, 12));
        nameCombo.setPreferredSize(new Dimension(180, 30));

        passwordTxt.setFont(new Font("Tahoma", 0, 12));
        passwordTxt.setPreferredSize(new Dimension(180, 30));

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

        formPanel.add(nameLbl);
        formPanel.add(nameCombo);
        formPanel.add(passwordLbl);
        formPanel.add(passwordTxt);
        formPanel.add(removeBtn);
        formPanel.add(cancelBtn);

        outPanel.add(formPanel, BorderLayout.CENTER);
    }

    private void loadActions(){
        nameCombo.addFocusListener(this);
        passwordTxt.addFocusListener(this);

        removeBtn.addActionListener(new ActionListener(){
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

    public void focusLost(FocusEvent e){
        if(e.getSource() == passwordTxt){
            checkIn.passAnotherInput(passwordTxt.getText());
            if(checkIn.isGoodInput()){
                InputsManager.paint_unpaintTextFields(passwordTxt, true);
                validateState = true;
            }
            else{
                InputsManager.paint_unpaintTextFields(passwordTxt, false);
                validateState = false;
            }
        }
    }

    public void focusGained(FocusEvent e){
        if(e.getSource() == passwordTxt){
            checkIn.passAnotherInput(passwordTxt.getText());
            if(checkIn.isGoodInput()){
                validateState = true;
            }
            else{
                InputsManager.paint_unpaintTextFields(passwordTxt, false);
                validateState = false;
            }
        }
    }

    private void validateAnd_Add(){
        if(nameCombo.getSelectedIndex() == 0){
            JOptionPane.showMessageDialog(self, "Select a USER to remove", "COMMAND ERROR", JOptionPane.PLAIN_MESSAGE);
        }
        else{
            if(!passwordTxt.getText().equals("") && validateState){
                try{
                    remove_user_from_Db();
                }
                catch(SQLException e){
                    System.err.println("Error occured while trying to Add Item to db : " + e.getMessage());
                }
            }
            else{
                JOptionPane.showMessageDialog(self, "- Form Contains Invalid DATA -", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
            }
        }
    }


    private void loadUserNameFromDB() throws SQLException{
        stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        result = stat.executeQuery("SELECT name FROM users");
        if(result.first()){
            do{
                usersV.addElement(result.getString("name"));
            }
            while(result.next());
        }
        else{
            usersV.addElement("");
        }
    }

    public void remove_user_from_Db() throws SQLException{
        //use the prvious statement and connection available
        int updateStatus = 0;
        updateStatus = stat.executeUpdate("DELETE FROM users WHERE name='" + nameCombo.getSelectedItem().toString() 
                + "' AND password='" + passwordTxt.getText() + "'");
        if(updateStatus != 0){
            //show that the update was successfull
            JOptionPane.showMessageDialog(self, "- New User '" + nameCombo.getSelectedItem().toString() + "' was Sucessfully REMOVED -", "SUCCESS", JOptionPane.PLAIN_MESSAGE);
            popH.stopPopHandler();
            dispose();
        }
        else{
            JOptionPane.showMessageDialog(self, "- User's Password Provided is not VALID -", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
        }
    }

}
