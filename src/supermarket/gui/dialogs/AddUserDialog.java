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
public class AddUserDialog extends JDialog implements FocusListener{

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
    private JTextField nameTxt;
    private JTextField passwordTxt;
    private JButton addBtn;
    private JButton cancelBtn;

    private PopDialogParentHandler popH;

    ///////
    private Vector <String>usersV;
    private Thread th;

    private InputsManager checkIn;
    private boolean validateState = false;

    public AddUserDialog(JFrame p, Connection c){
        parent = p;
        con = c;
        usersV = new Vector<String>();
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
    }

    private void initComponents(){
        
        GridLayout grid = new GridLayout(3, 2, 4, 4);

        outPanel = new JPanel(new BorderLayout(10, 10));
        formPanel = new JPanel(grid);

        nameLbl = new JLabel("User Name :");
        passwordLbl = new JLabel("User Password :");
        nameTxt = new JTextField();
        passwordTxt = new JTextField();
        addBtn = new JButton("ADD");
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

        nameTxt.setFont(new Font("Tahoma", 0, 12));
        nameTxt.setPreferredSize(new Dimension(180, 30));

        passwordTxt.setFont(new Font("Tahoma", 0, 12));
        passwordTxt.setPreferredSize(new Dimension(180, 30));

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

        formPanel.add(nameLbl);
        formPanel.add(nameTxt);
        formPanel.add(passwordLbl);
        formPanel.add(passwordTxt);
        formPanel.add(addBtn);
        formPanel.add(cancelBtn);

        outPanel.add(formPanel, BorderLayout.CENTER);
    }

    private void loadActions(){
        nameTxt.addFocusListener(this);
        passwordTxt.addFocusListener(this);

        addBtn.addActionListener(new ActionListener(){
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
        if(e.getSource() == nameTxt){
            //check it
            checkIn.passAnotherInput(nameTxt.getText());
            if(checkIn.isGoodInput()){
                nameTxt.setText(InputsManager.makeUppercase(nameTxt.getText()));
                InputsManager.paint_unpaintTextFields(nameTxt, true);
                validateState = true;
            }
            else{
                InputsManager.paint_unpaintTextFields(nameTxt, false);
                validateState = false;
            }
        }
        else if(e.getSource() == passwordTxt){
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
        if(!nameTxt.getText().equals("") && !passwordTxt.getText().equals("") && validateState){
            //check if the comodity name provided already exist
            if(!usersV.contains(nameTxt.getText())){
                try{
                    add_user_to_Db();
                }
                catch(SQLException e){
                    System.err.println("Error occured while trying to Add Item to db : " + e.getMessage());
                }
            }
            else{
                JOptionPane.showMessageDialog(self, "- New User '" + nameTxt.getText() + "' already Exist -", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
            }
        }
        else{
           JOptionPane.showMessageDialog(self, "- Form Contains Invalid DATA -", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
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

    public void add_user_to_Db() throws SQLException{
        //use the prvious statement and connection available
        int updateStatus = 0;
        updateStatus = stat.executeUpdate("INSERT INTO users (name, password) VALUES ('" + nameTxt.getText() + "', '"
                + passwordTxt.getText() + "')");
        if(updateStatus != 0){
            //show that the update was successfull
            JOptionPane.showMessageDialog(self, "- New User '" + nameTxt.getText() + "' was Sucessfully ADDED -", "SUCCESS", JOptionPane.PLAIN_MESSAGE);
            popH.stopPopHandler();
            dispose();
        }
        else{
            JOptionPane.showMessageDialog(self, "- Cannot add this user at this time. (Try Later) -", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
        }
    }

}
