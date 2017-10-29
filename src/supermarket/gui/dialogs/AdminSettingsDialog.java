/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui.dialogs;
import supermarket.gui.AdminFrame;
import supermarket.gui.util.*;
import supermarket.utility.InputsManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

/**
 *
 * @author MUSTAFA
 */
public class AdminSettingsDialog extends JDialog implements FocusListener{

    private JDialog self = this;

    private AdminFrame parent;
    private Connection con;

    private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

    private JPanel outPanel;
    private JPanel downPanel;
    private JTabbedPane tabTab;
    private JPanel mainSetPanel;
    private JPanel marketSetPanel;
    private JPanel secSetPanel;
    private JPanel advSetPanel;

    private JTextField []fields;
    private JPasswordField oldPass;
    private JTextField newTxt;
    private JTextField newTxt_2;
    private JTextField timeTxt;
    private JPasswordField verPass;
    private JTextArea status;
    private JButton resBtn;
    private JCheckBox check;
    private JScrollPane scroll;
    private Timer t;
    
    private JButton cancelBtn;
    private JButton saveBtn;
    private JComboBox modCombo;
    private String modDesc; //the description of the module in selection
    private JLabel descLbl;
    private PopDialogParentHandler popH;
    ///////
    private InputsManager checkIn;
    private Thread th;

    //////////////////////////
    private AdvancedSetHandler advHand; //manager for the getting and setting of advanced settingss
    /////////////////////////

    private Vector<String> comInfoV;
    private String curStatus = "Please wait ......";
    private String prevStatus = "";

    private boolean validState = false;
    private String adminPass = "";
    private String enteredPass = "";
    private String autoMins = "";

    public AdminSettingsDialog(AdminFrame p, Connection c){
    //public AdminSettingsDialog(){
        parent = p;
        con = c;
        checkIn = new InputsManager("");
        advHand = new AdvancedSetHandler(con); //initialize
        modDesc = "waiting for user to determine the sales module in use ......";
        ////////////////////////////////////////////////////////////
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                popH = new PopDialogParentHandler(parent, self);
            }
        });
        ///////////////////////////////////////////////////////////

        setSize(400, 250);
        setLocation((d.width - 400) / 2, (d.height - 250) /2);
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
                factoryLoader(); //load all the datas
                repaint();
                validate();
            }
        });
    }

    private void initComponents(){
        outPanel = new JPanel(new BorderLayout(1, 1));
        tabTab = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

        outPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        outPanel.setPreferredSize(new Dimension(400, 250));
        outPanel.setBackground(Color.WHITE);
        //com.sun.awt.AWTUtilities.setWindowOpacity(outPanel, 0.0f);
        
        downPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 1));
        downPanel.setPreferredSize(new Dimension(400, 30));
        downPanel.setBackground(new Color(206, 234, 206));

        tabTab.setPreferredSize(new Dimension(400, 220));
        //tabTab.setBorder(BorderFactory.createLineBorder(new Color(152, 213, 152), 1));
        tabTab.setBackground(new Color(0, 228, 153));
        tabTab.add("Main", mainSettingsPanel());
        //tabTab.add("Sales", marketSettingsPanel());
        tabTab.add("Security", securitySettingsPanel());
        tabTab.add("Sales Module", marketSettingsPanel());
        tabTab.add("Advanced", advancedSettingsPanel());
        tabTab.setFocusable(false);

        cancelBtn = new JButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(90, 28));
        cancelBtn.setHorizontalAlignment(SwingConstants.CENTER);
        cancelBtn.setBackground(new Color(65, 105, 225));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        downPanel.add(cancelBtn);
        
        outPanel.add(tabTab, BorderLayout.CENTER);
        outPanel.add(downPanel, BorderLayout.SOUTH);

    }

    private void loadActions(){
        cancelBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                popH.stopPopHandler();
                dispose();
            }
        });
    }

    private JPanel mainSettingsPanel(){
        mainSetPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 2));
        mainSetPanel.setPreferredSize(new Dimension(390, 200));
        mainSetPanel.setBackground(new Color(152, 213, 152));

        formPanel.setPreferredSize(new Dimension(390, 160));
        formPanel.setBackground(new Color(152, 213, 152));
        //formPanel.setBorder(BorderFactory.createLineBorder(new Color(65, 105, 225), 1));

        btnPanel.setPreferredSize(new Dimension(390, 26));
        btnPanel.setBackground(new Color(152, 213, 152));
        
        JLabel infoLbl = new JLabel("Modify Company Information");
        JLabel []labels = new JLabel[4];
        fields = new JTextField[4];
        JButton updateBtn = new JButton("Update");
        JButton resetBtn = new JButton("Reset");

        updateBtn.setPreferredSize(new Dimension(90, 25));
        updateBtn.setHorizontalAlignment(SwingConstants.CENTER);
        updateBtn.setBackground(new Color(65, 105, 225));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        updateBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                                if(validState){
                                    //get the new data entered in a new vector
                                    Vector <String>newCompV = new Vector<String>();
                                    for(int j = 0; j < fields.length; j++){
                                        newCompV.addElement(fields[j].getText());
                                    }
                                    int up = advHand.updateCompanyInfo(newCompV);
                                    if(up != 0){
                                        //means update was successful
                                        JOptionPane.showMessageDialog(self, "Company info UPDATED \n- Changes will occur at next Launch of Application -"
                                                , "SUCCESS", JOptionPane.PLAIN_MESSAGE);
                                    }
                                    else{
                                        //cannot update at this time
                                        JOptionPane.showMessageDialog(self, "- Cannot UPTADE at this time (Try Again)-", "CRITICAL ERROR", JOptionPane.PLAIN_MESSAGE);
                                    }
                                }
                                else{
                                    //form contains invalid data
                                    JOptionPane.showMessageDialog(self, "- Form Submitted contains INVALID DATA -", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
                                }//end of if and else of validateState
                    }
                });
            }
        });

        resetBtn.setPreferredSize(new Dimension(90, 25));
        resetBtn.setHorizontalAlignment(SwingConstants.CENTER);
        resetBtn.setBackground(new Color(65, 105, 225));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        resetBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                factoryLoader();
                repaint();
                validate();
            }
        });

        labels[0] = new JLabel("Company's Name :");
        labels[1] = new JLabel("Company's Location :");
        labels[2] = new JLabel("Contact Telephones :");
        labels[3] = new JLabel("Company's E-mail :");

        infoLbl.setHorizontalAlignment(SwingConstants.CENTER);
        infoLbl.setFont(new Font("AR JULIAN", Font.TRUETYPE_FONT, 12));
        infoLbl.setForeground(new Color(0, 0, 255));
        infoLbl.setPreferredSize(new Dimension(390, 30));

        for(int i = 0; i < fields.length; i++){
            fields[i] = new JTextField();
            fields[i].setFont(new Font("Vandana", 0, 12));
            fields[i].setPreferredSize(new Dimension(180, 25));
            fields[i].addFocusListener(this); //add focus listener to the textfields
        }

        for(int i = 0; i < labels.length; i++){
            labels[i].setFont(new Font("Poor Richard", Font.PLAIN, 14));
            labels[i].setHorizontalAlignment(SwingConstants.LEADING);
            labels[i].setPreferredSize(new Dimension(180, 25));
        }
        
        formPanel.add(labels[0]);
        formPanel.add(fields[0]);
        formPanel.add(labels[1]);
        formPanel.add(fields[1]);
        formPanel.add(labels[2]);
        formPanel.add(fields[2]);
        formPanel.add(labels[3]);
        formPanel.add(fields[3]);

        btnPanel.add(updateBtn);
        btnPanel.add(resetBtn);

        mainSetPanel.add(infoLbl, BorderLayout.NORTH);
        mainSetPanel.add(formPanel, BorderLayout.CENTER);
        mainSetPanel.add(btnPanel, BorderLayout.SOUTH);

        return mainSetPanel;
    }

    private JPanel marketSettingsPanel(){
        marketSetPanel = new JPanel(new BorderLayout());
        String txt = "Modify Sales Scheme to SUITE your Market";
        String[] combo = {"Wholesales Store", "Retail Store", "Generic Store-Whole/Retail"};
        JLabel top = new JLabel(txt);
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        JPanel wholePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JTextArea txtArea = new JTextArea("Administrator can Simply modify MARKET MODULE uprading or downgrading the SALES plan to suit the " +
                "current SUPER MARKET scheme.\tThis function is optimized and safe to use and we guaratee 100% DATA SAFETY !");
        txtArea.setFont(new Font("Verdana", Font.PLAIN, 10));
        txtArea.setLineWrap(true);
        txtArea.setWrapStyleWord(true);
        txtArea.setPreferredSize(new Dimension(390, 80));
        txtArea.setBackground(Color.WHITE);
        txtArea.setForeground(new Color(65, 105, 225));
        txtArea.setEditable(false);
        wholePanel.setPreferredSize(new Dimension(300, 180));
        descLbl = new JLabel(modDesc);
        modCombo = new JComboBox(combo);
        modCombo.setPreferredSize(new Dimension(225, 25));
        modCombo.setFont(new Font("Vandana", 0, 12));
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                switch(Integer.parseInt(new ConfigModuleHandler().read(0))){
                    case 0:
                        //means its a wholesales Store
                        modCombo.setSelectedIndex(0);
                        modDesc = "Current Sales Module in USE is 'A WHOLESALES STORE'.";
                        descLbl.setText(modDesc);
                        break;
                    case 1:
                        //means its a retail store
                        modCombo.setSelectedIndex(1);
                        modDesc = "Current Sales Module in USE is 'A RETAIL STORE'.";
                        descLbl.setText(modDesc);
                        break;
                    case 2:
                        //means its a eneric store
                        modCombo.setSelectedIndex(2);
                        modDesc = "Current Sales Module in USE is 'A GENERIC STORE'.";
                        descLbl.setText(modDesc);
                        break;
                    default:
                        modCombo.setSelectedIndex(2);
                        descLbl.setText(modDesc);
                        break;
                }
                modCombo.repaint();
                modCombo.validate();
                descLbl.repaint();
                descLbl.validate();
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnPanel.setPreferredSize(new Dimension(390, 25));
        saveBtn = new JButton("Save");

        saveBtn.setPreferredSize(new Dimension(150, 25));
        saveBtn.setHorizontalAlignment(SwingConstants.CENTER);
        saveBtn.setBackground(new Color(65, 105, 225));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        saveBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SwingWorker<Void, Void> w = new SwingWorker<Void, Void>(){
                    public Void doInBackground(){
                        saveBtn.setEnabled(false);
                        new ConfigModuleHandler().write(String.valueOf((modCombo.getSelectedIndex())), 0);
                        return null;
                    }
                    @Override
                    public void done(){
                        JOptionPane.showMessageDialog(self, "- NEW SALES MODULE SUCCESSFULLY SELECTED -", "SUCCESS", JOptionPane.PLAIN_MESSAGE);
                        try{
                            Thread.sleep(900);
                            parent.getPanelManagerInstance().getWelcomeInstance().refresh(modCombo.getSelectedIndex()); //pass in the index selected
                            saveBtn.setEnabled(true);
                        }
                        catch(InterruptedException err){
                            System.err.println(err.getMessage());
                        }
                    }
                };
                w.execute();
            }
        });
        btnPanel.add(saveBtn);

        top.setHorizontalAlignment(SwingConstants.CENTER);
        top.setFont(new Font("AR JULIAN", Font.TRUETYPE_FONT, 13));
        top.setForeground(new Color(0, 0, 255));
        top.setPreferredSize(new Dimension(390, 30));

        centerPanel.setPreferredSize(new Dimension(370, 30));
        
        marketSetPanel.setPreferredSize(new Dimension(360, 200));
        marketSetPanel.setBackground(new Color(152, 213, 152));

        centerPanel.add(modCombo);

        wholePanel.add(top);
        wholePanel.add(centerPanel);
        wholePanel.add(descLbl);
        wholePanel.add(txtArea);
        wholePanel.add(btnPanel);

        marketSetPanel.add(wholePanel, BorderLayout.CENTER);
        return marketSetPanel;
    }

    private JPanel securitySettingsPanel(){
        secSetPanel = new JPanel(new BorderLayout());
        JPanel topSetPanel = new JPanel(new BorderLayout());
        JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 2));
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 5));

        JPanel downSetPanel = new JPanel(new BorderLayout());
        JPanel downDownPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 16, 5));
        JPanel downRightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 5));

        JLabel oldLbl = new JLabel("Old Password : ");
        JLabel newLbl = new JLabel("New Password : ");
        JLabel newLbl_2 = new JLabel("Re-Type New Password : ");

        oldLbl.setFont(new Font("Poor Richard", Font.PLAIN, 13));
        oldLbl.setHorizontalAlignment(SwingConstants.LEADING);
        oldLbl.setPreferredSize(new Dimension(130, 25));

        newLbl.setFont(new Font("Poor Richard", Font.PLAIN, 13));
        newLbl.setHorizontalAlignment(SwingConstants.LEADING);
        newLbl.setPreferredSize(new Dimension(130, 25));

        newLbl_2.setFont(new Font("Poor Richard", Font.PLAIN, 13));
        newLbl_2.setHorizontalAlignment(SwingConstants.LEADING);
        newLbl_2.setPreferredSize(new Dimension(130, 25));

        oldPass = new JPasswordField();
        newTxt = new JTextField();
        newTxt_2 = new JTextField();

        oldPass.setFont(new Font("Vandana", 0, 12));
        oldPass.setPreferredSize(new Dimension(130, 25));

        newTxt.setFont(new Font("Vandana", 0, 12));
        newTxt.setPreferredSize(new Dimension(130, 25));

        newTxt_2.setFont(new Font("Vandana", 0, 12));
        newTxt_2.setPreferredSize(new Dimension(130, 25));

        JButton changeBtn = new JButton("Change");
        JButton clearBtn = new JButton("Clear");

        changeBtn.setPreferredSize(new Dimension(80, 35));
        changeBtn.setHorizontalAlignment(SwingConstants.CENTER);
        changeBtn.setBackground(new Color(65, 105, 225));
        changeBtn.setForeground(Color.WHITE);
        changeBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        changeBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(oldPass.getPassword().length == 0 || newTxt.getText().equals("") || newTxt_2.getText().equals("")){
                    //dont perform anything , and show error thats some fields are empty
                    JOptionPane.showMessageDialog(self, "- Form Contains Empty Data -", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
                }
                else{
                    if(newTxt.getText().equals(newTxt_2.getText())){
                        //then pass the texts to the inputManager
                        checkIn.passAnotherInput(newTxt.getText());
                        if(checkIn.isGoodInput()){
                               char []p = oldPass.getPassword();
                               enteredPass = "";// set entered password to empty
                               for(int i = 0; i < p.length; i++){
                                   enteredPass += p[i]; //get the string value of the password enetered
                               }
                                InputsManager.paint_unpaintTextFields(newTxt, true);
                                InputsManager.paint_unpaintTextFields(newTxt_2, true);
                               //then compare pasword
                               if(enteredPass.equals(adminPass)){
                                   //then try pass the value to perform the chage
                                   changePassword(newTxt.getText());
                                   oldPass.setText("");
                               }
                               else{
                                   //show that old password provided is not valid
                                   InputsManager.paint_unpaintTextFields(oldPass, false);
                                   JOptionPane.showMessageDialog(self, "- Old Password Provided NOT-VALID -", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
                               }
                        }
                        else{
                            InputsManager.paint_unpaintTextFields(newTxt, false);
                            InputsManager.paint_unpaintTextFields(newTxt_2, false);
                        }
                    }
                    else{
                        //show that the two new passwords do not match
                        InputsManager.paint_unpaintTextFields(newTxt, false);
                        InputsManager.paint_unpaintTextFields(newTxt_2, false);
                        JOptionPane.showMessageDialog(self, "- New Passwords Provided NOT-MATCH -", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
                    }
                }
            }
        });

        clearBtn.setPreferredSize(new Dimension(80, 35));
        clearBtn.setHorizontalAlignment(SwingConstants.CENTER);
        clearBtn.setBackground(new Color(65, 105, 225));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        clearBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                oldPass.setText("");
                newTxt.setText("");
                newTxt_2.setText("");
                InputsManager.paint_unpaintTextFields(oldPass, true);
                InputsManager.paint_unpaintTextFields(newTxt, true);
                InputsManager.paint_unpaintTextFields(newTxt_2, true);
            }
        });

        JLabel downLbl = new JLabel("Auto TIME-OUT Configuration");
        JLabel outLbl = new JLabel("Time-Out After (Mins): ");
        timeTxt = new JTextField();
        JLabel verifyLbl = new JLabel("Verify : ");
        verPass = new JPasswordField();
        JButton updateBtn = new JButton("Update");
        JButton defaultBtn = new JButton("Default");

        outLbl.setFont(new Font("Poor Richard", Font.PLAIN, 14));
        outLbl.setHorizontalAlignment(SwingConstants.LEADING);
        outLbl.setPreferredSize(new Dimension(130, 28));

        verifyLbl.setFont(new Font("Poor Richard", Font.PLAIN, 14));
        verifyLbl.setHorizontalAlignment(SwingConstants.LEADING);
        verifyLbl.setPreferredSize(new Dimension(130, 28));

        verPass.setFont(new Font("Vandana", 0, 12));
        verPass.setPreferredSize(new Dimension(130, 28));

        timeTxt.setFont(new Font("Vandana", 0, 12));
        timeTxt.setPreferredSize(new Dimension(130, 28));

        updateBtn.setPreferredSize(new Dimension(80, 27));
        updateBtn.setHorizontalAlignment(SwingConstants.CENTER);
        updateBtn.setBackground(new Color(65, 105, 225));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        updateBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                changeTimeOut(timeTxt.getText());
            }
        });

        defaultBtn.setPreferredSize(new Dimension(80, 27));
        defaultBtn.setHorizontalAlignment(SwingConstants.CENTER);
        defaultBtn.setBackground(new Color(65, 105, 225));
        defaultBtn.setForeground(Color.WHITE);
        defaultBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        defaultBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                timeTxt.setText(autoMins);
            }
        });

        
        downLbl.setPreferredSize(new Dimension(390, 27));
        downLbl.setHorizontalAlignment(SwingConstants.CENTER);
        downLbl.setFont(new Font("AR JULIAN", Font.TRUETYPE_FONT, 12));
        downLbl.setForeground(new Color(0, 0, 255));
        downLbl.setBackground(Color.WHITE);
        
        secSetPanel.setPreferredSize(new Dimension(390, 200));
        secSetPanel.setBackground(new Color(152, 213, 152));

        topLeftPanel.setPreferredSize(new Dimension(300, 120));
        topLeftPanel.setBackground(new Color(152, 213, 152));
        topLeftPanel.add(oldLbl);
        topLeftPanel.add(oldPass);
        topLeftPanel.add(newLbl);
        topLeftPanel.add(newTxt);
        topLeftPanel.add(newLbl_2);
        topLeftPanel.add(newTxt_2);

        topRightPanel.setPreferredSize(new Dimension(90, 120));
        topRightPanel.setBackground(new Color(206, 234, 206));
        topRightPanel.add(changeBtn);
        topRightPanel.add(clearBtn);

        downDownPanel.setPreferredSize(new Dimension(300, 80));
        downDownPanel.setBackground(new Color(152, 213, 152));
        //downDownPanel.setBorder(BorderFactory.createLineBorder(new Color(214, 217, 223), 3));
        downDownPanel.add(outLbl);
        downDownPanel.add(timeTxt);
        downDownPanel.add(verifyLbl);
        downDownPanel.add(verPass);

        downRightPanel.setPreferredSize(new Dimension(90, 60));
        downRightPanel.setBackground(new Color(206, 234, 206));
        downRightPanel.add(updateBtn);
        downRightPanel.add(defaultBtn);

        topSetPanel.setPreferredSize(new Dimension(390, 100));
        topSetPanel.add(topLeftPanel, BorderLayout.CENTER);
        topSetPanel.add(topRightPanel, BorderLayout.EAST);

        downSetPanel.setPreferredSize(new Dimension(390, 100));
        downSetPanel.add(downLbl, BorderLayout.NORTH);
        downSetPanel.add(downDownPanel, BorderLayout.CENTER);
        downSetPanel.add(downRightPanel, BorderLayout.EAST);

        secSetPanel.add(topSetPanel, BorderLayout.CENTER);
        secSetPanel.add(downSetPanel, BorderLayout.SOUTH);

        return secSetPanel;
    }

    private JPanel advancedSettingsPanel(){
        advSetPanel = new JPanel(new BorderLayout());
        advSetPanel.setPreferredSize(new Dimension(390, 200));
        advSetPanel.setBackground(new Color(152, 213, 152));

        JPanel advTopPanel = new JPanel();
        JPanel advDownPanel = new JPanel();
        JPanel advCenterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 7, 1));
        status = new JTextArea();

        JLabel factSetLbl = new JLabel("Restore Application");
        JLabel eraseLbl = new JLabel("Erase all DATA in Database ?");
        check = new JCheckBox();
        resBtn = new JButton("Reset");
        scroll = new JScrollPane();
        scroll.setAutoscrolls(true);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setPreferredSize(new Dimension(380, 60));
        scroll.setViewportView(status);

        check.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(check.isSelected()){
                    resBtn.setEnabled(true);
                }
                else{
                    resBtn.setEnabled(false);
                }
            }
        });

        status.setFont(new Font("Verdana", Font.PLAIN, 11));
        status.setBackground(Color.WHITE);
        status.setForeground(new Color(65, 105, 225));
        status.setEditable(false);

        factSetLbl.setFont(new Font("Poor Richard", Font.PLAIN, 14));
        factSetLbl.setHorizontalAlignment(SwingConstants.CENTER);
        factSetLbl.setPreferredSize(new Dimension(390, 30));

        eraseLbl.setFont(new Font("Tahoma", Font.PLAIN, 14));
        eraseLbl.setHorizontalAlignment(SwingConstants.LEADING);
        eraseLbl.setPreferredSize(new Dimension(250, 30));

        resBtn.setPreferredSize(new Dimension(130, 30));
        resBtn.setHorizontalAlignment(SwingConstants.CENTER);
        resBtn.setBackground(new Color(65, 105, 225));
        resBtn.setForeground(Color.WHITE);
        resBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));
        resBtn.setEnabled(false);

        resBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                int op = JOptionPane.showConfirmDialog(self,
                        "Are you sure you want to RESTORE to DEFAULT ?\n - NOTE : All Strored DATA will be Completely Erased - ",
                        "CONFIRM RESTORE", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(op == 0){
                    //that means 0 was selected
                    self.setEnabled(false); //diable this component
                    t = new Timer(1000, new ActionListener(){
                        public void actionPerformed(ActionEvent e){
                            curStatus = advHand.getCurrentStatus();
                            if(!curStatus.equals(prevStatus)){
                                status.append(curStatus + "\n");
                                status.setCaretPosition(status.getDocument().getLength());
                                prevStatus = curStatus;
                            }
                        }
                    });
                    th = new Thread(new Runnable(){
                        public void run(){
                            if(!t.isRunning()){
                                t.start();
                            }
                            advHand.formatDb(self, parent);
                        }
                    });
                    if(!th.isAlive()){
                        th.start();
                    }
                }
                else{
                   check.setSelected(false);
                   resBtn.setEnabled(false);
                }
            }
        });
        advCenterPanel.setPreferredSize(new Dimension(390, 30));
        advCenterPanel.setBackground(new Color(255, 52, 81));
        advCenterPanel.add(eraseLbl);
        advCenterPanel.add(check);
        
        advTopPanel.setPreferredSize(new Dimension(390, 130));
        advTopPanel.setBackground(new Color(255, 52, 81));
        advTopPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        advTopPanel.add(factSetLbl, BorderLayout.NORTH);
        advTopPanel.add(advCenterPanel, BorderLayout.CENTER);
        advTopPanel.add(resBtn, BorderLayout.SOUTH);

        advDownPanel.setPreferredSize(new Dimension(380, 70));
        advDownPanel.setBackground(new Color(152, 213, 152));
        advDownPanel.add(scroll);
        
        advSetPanel.add(advTopPanel, BorderLayout.CENTER);
        advSetPanel.add(advDownPanel, BorderLayout.SOUTH);

        return advSetPanel;
    }
/*
    public static void main(String []args){
        new AdminSettingsDialog();
    }
 *
 */
    private void changePassword(String np){
        int upDate = 0;
        upDate = advHand.updatePassword(np);
        if(upDate != 0){
            JOptionPane.showMessageDialog(self, "- Password Successfully Changed -", "SUCCESS", JOptionPane.PLAIN_MESSAGE);
        }
        else{
            JOptionPane.showMessageDialog(self, "- Cannot change Password at this time -", "CRITICAL ERROR", JOptionPane.PLAIN_MESSAGE);
        }
    }

    private void changeTimeOut(String txt){
        int time = 0;
        if(!txt.equals("")){
           time = (int)InputsManager.allowOnlyIntegers(txt);
           timeTxt.setText(String.valueOf(time));
           if(time != 0){
               InputsManager.paint_unpaintTextFields(timeTxt, true);
               if(verPass.getPassword().length != 0){
                   //then try perform change
                   InputsManager.paint_unpaintTextFields(verPass, true);
                   String pwd = "";
                   char []p = verPass.getPassword();
                   for(int i = 0; i < p.length; i++){
                       pwd += p[i];
                   }
                   if(pwd.equals(adminPass)){
                       //then perform change
                       advHand.updateTimeOut(time);
                       JOptionPane.showMessageDialog(self, "- New Security Auto Time-Out set -", "SUCCESS", JOptionPane.PLAIN_MESSAGE);
                       InputsManager.paint_unpaintTextFields(verPass, true);
                       verPass.setText("");
                   }
                   else{
                       JOptionPane.showMessageDialog(self, "- Invalid Verification Password Provided -", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
                       verPass.setText("");
                       InputsManager.paint_unpaintTextFields(verPass, false);
                   }
               }
               else{
                   InputsManager.paint_unpaintTextFields(verPass, false);
                   JOptionPane.showMessageDialog(self, "- Provide a Password to Validate Change -", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
               }
           }
           else{
               InputsManager.paint_unpaintTextFields(timeTxt, false);
               JOptionPane.showMessageDialog(self, "- Time-Out cannot be set to '0' -", "FORM ERROR", JOptionPane.PLAIN_MESSAGE);
           }
        }
    }

    private void factoryLoader(){
        Thread factL = new Thread(new Runnable(){
            public void run(){
                comInfoV = advHand.getCompanyInfo();
                adminPass = advHand.getAdminPassword(); //get the administrator password
                autoMins = String.valueOf(advHand.getAutoTimeOut() / 60);
                for(int i = 0; i < fields.length; i++){
                    fields[i].setText(comInfoV.elementAt(i));
                }
                timeTxt.setText(autoMins);
            }
        });
        if(!factL.isAlive()){
            factL.start();
        }
    }

    public void focusGained(FocusEvent e){
        for(int i = 0; i < fields.length; i++){
            if(e.getSource().equals(fields[0])){
                //do nothing to the first field
            }
            else if(e.getSource().equals(fields[i])){
                fields[i].setText("");
            }
        }
    }

    public void focusLost(FocusEvent e){
        //validate the inputs when focus lost
        for(int i = 0; i < fields.length; i++){
            if(e.getSource().equals(fields[i])){
                if(i == (fields.length - 1)){
                    //means i need a valid email add
                    if(InputsManager.isValidateEmail(fields[i].getText())){
                        InputsManager.paint_unpaintTextFields(fields[i], true);
                        validState = true;
                    }
                    else{
                        InputsManager.paint_unpaintTextFields(fields[i], false);
                        validState = false;
                    }
                    fields[i].setText(InputsManager.makeLowercase(fields[i].getText()));
                }
                else if(i == (fields.length - 2)){
                    //means only integers is needed here
                    long tel = InputsManager.allowOnlyIntegers(fields[i].getText());
                    if(tel != 0){
                        InputsManager.paint_unpaintTextFields(fields[i], true);
                        validState = true;
                    }
                    else{
                        InputsManager.paint_unpaintTextFields(fields[i], false);
                        validState = false;
                    }
                }
                else{
                    checkIn.passAnotherInput(fields[i].getText());
                    if(checkIn.isGoodInput()){
                        //means the text if valid
                        InputsManager.paint_unpaintTextFields(fields[i], true);
                        validState = true;
                    }
                    else{
                        InputsManager.paint_unpaintTextFields(fields[i], false);
                        validState = false;
                    }
                    fields[i].setText(InputsManager.makeUppercase(fields[i].getText())); //make upper case
                }//end of last else
            }//get source if
        }//for ends here
    }//end of fucos lost
}
