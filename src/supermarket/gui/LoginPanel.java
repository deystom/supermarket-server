/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui;
import supermarket.gui.util.*;
import supermarket.utility.InputsManager;
import javax.swing.*;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author Segun
 */
public class LoginPanel extends ImagePanel{

    private PanelManager manager;
    private int width, height;
    private JPanel inPanel;
    private JPasswordField passTxt;
    private JButton loginBtn;

    //
    private String pass = ""; //the password
    private String correctPwd = "";
    private Connection con = null;
    private Statement stat = null;
    private ResultSet result;
    private Thread dbThread;

    public LoginPanel(PanelManager m, int x, int y){
        super();
        manager = m;
        width = x;
        height = y;
        setSize(width, height);
        //setPreferredSize(new Dimension(width, height));
        //setMinimumSize(new Dimension(width, height));
        setImage(new ImageIcon(getClass().getResource("images/admin_home_bkg.gif")).getImage());
        setLayout(new FlowLayout(FlowLayout.CENTER, 130, 130));
        //
        MyLookAndFeel.setLook();
        //
        //setOpaque(true);
        
        initComponents();
        myActions();
        //setBorder(BorderFactory.createLineBorder(new Color(80, 185, 80), 1));
        //System.out.println("Width : " + getWidth() + " and Height : " + getHeight());
    }

    public void startDbWork(){
        dbThread = new Thread(new Runnable(){
            @SuppressWarnings("static-access")
            public void run(){
                try{
                    con = manager.getAvailableConnection();
                    stat = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    result = stat.executeQuery("SELECT * FROM admin WHERE user='Administrator'");
                    if(result.absolute(1)){
                        correctPwd = result.getString("password");
                    }
                    else{
                        System.out.println("NO RESULT");
                    }

                    dbThread.sleep(3000); //after 3 seconds.. do, the below
                }
                catch(InterruptedException e){
                    System.err.println("Interrupted : " + e.getMessage());
                }
                catch(SQLException s){
                    System.err.println("Error executing query : " + s.getMessage());
                }
            }
        });
        if(!dbThread.isAlive()){
            dbThread.start(); //start the db class if its not alive before
        }
    }
    
    private void initComponents(){

        inPanel = new ImagePanel(new ImageIcon(getClass().getResource("images/login_panel.gif")).getImage());
        inPanel.setOpaque(true);
        //com.sun.awt.AWTUtilities.setWindowOpacity((Component) inPanel, 0.3f);
        inPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inPanel.setPreferredSize(new Dimension(300, 100));
        inPanel.setMinimumSize(new Dimension(300, 100));
        //inPanel.setLocation(this.getSize());

        passTxt = new JPasswordField();
        passTxt.setPreferredSize(new Dimension(210, 30));

        loginBtn = new JButton("Login As Administrator");
        loginBtn.setBackground(new Color(90, 122, 87));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setPreferredSize(new Dimension(170, 30));
        
        inPanel.add(passTxt);
        inPanel.add(loginBtn);
        
        add(inPanel);

    }

    private void myActions(){        
        passTxt.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                validateCredential();                
            }
        });

        loginBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                validateCredential();
            }
        });
    }

    private void validateCredential(){
        char []pwd = passTxt.getPassword();
        for(int i = 0; i < pwd.length; i++){
            pass += pwd[i];
        }
        InputsManager m = new InputsManager(pass);
        if(m.isGoodInput()){
            //if the crdential provided is valid characters ... then pass the data to the database
            if(pass.equals(correctPwd)){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        //the password is valie ... authenticate user
                        pass = "";
                        passTxt.setText("");
                        manager.getPreviousPanel().setVisible(false);
                        manager.setPanelIndex(1);
                    }
                });
            }
            else{
                //means tha character is valid but the password is not corerct
                pass = "";
                passTxt.setText("");
                JOptionPane.showMessageDialog(this, "- Invalid Password Provided -", "AUTHENTICATION ERROR", JOptionPane.PLAIN_MESSAGE);
            }
        }
        else{
            passTxt.setText("");
            pass = "";
           JOptionPane.showMessageDialog(this, "- Invalid Characters provided - \n Contact Service for VALID Character Combinations", "Characters ERROR", JOptionPane.PLAIN_MESSAGE);
        }
    }

}