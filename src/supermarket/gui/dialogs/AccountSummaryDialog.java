/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui.dialogs;
import supermarket.gui.util.*;
import supermarket.gui.AdminFrame;
import supermarket.tables.AccountSummary;
//import supermarket.utility.InputsManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.util.Vector;

/**
 *
 * @author Segun
 */
public class AccountSummaryDialog extends JDialog{

    private AccountSummaryDialog self = this;

    private AdminFrame parent;
    private Connection con;

    private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

    private JPanel outPanel;
    private JPanel tablePanel;
    private JPanel topPanel;
    private JPanel downPanel;

    private JButton viewGraphBtn;
    private JButton queryBtn;
    private JButton cancelBtn;


    private JComboBox dateCombo;
    private JComboBox userCombo;


    private GridBagLayout gridL;
    private GridBagConstraints gridConst;
    private JTable resultTable;
    private JScrollPane scroll;


    private PopDialogParentHandler popH;

    ///////
    private Thread th;
    private Vector<String> itemsV;
    private AccountSummary accTM;


    public AccountSummaryDialog(AdminFrame p, Connection c){
        parent = p;
        con = c;
        itemsV = new Vector<String>();
        itemsV.addElement("All Items ...");

        ////////////////////////////////////////////////////////////
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                popH = new PopDialogParentHandler(parent, self);
            }
        });
        ///////////////////////////////////////////////////////////

        setSize(900, 390);
        setLocation((d.width - 900) / 2, (d.height - 390) /2);
        setAlwaysOnTop(true);
        setUndecorated(true);
        ///////
        MyLookAndFeel.setLook();
        /////
        initComponents();
        setContentPane(outPanel);
        /*
         *
         */
        //setVisible(true);
        loadActions();
        th = new Thread(new Runnable(){
            public void run(){
                accTM = new AccountSummary(con, resultTable, dateCombo, userCombo); //the stock summary table model with the market model
                accTM.run();
                resultTable.setModel(accTM);
                repaint();
                validate();
            }
        });
        if(!th.isAlive()){
            th.start();
        }
    }

    private void initComponents(){
        gridL = new GridBagLayout();
        gridConst = new GridBagConstraints();

        outPanel = new JPanel(new BorderLayout(7, 7));
        tablePanel = new JPanel(new BorderLayout(3, 3));
        topPanel = new JPanel(gridL);
        downPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));

        resultTable = new JTable(); //pass the table model to be rendered in the table

        scroll = new JScrollPane();
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setViewportView(resultTable);

        viewGraphBtn = new JButton("VIEW GRAPHICAL REPRESENTATION OF RESULT");
        queryBtn = new JButton("QUERY");
        cancelBtn = new JButton("CANCEL");

        outPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        outPanel.setPreferredSize(new Dimension(900, 390));
        outPanel.setBackground(new Color(152, 213, 152));

        topPanel.setPreferredSize(new Dimension(900, 40));
        topPanel.setBackground(new Color(152, 213, 152));

        tablePanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        tablePanel.setPreferredSize(new Dimension(900, 340));
        tablePanel.setBackground(new Color(152, 213, 152));

        downPanel.setPreferredSize(new Dimension(900, 40));
        downPanel.setBackground(new Color(152, 213, 152));

        dateCombo = new JComboBox();
        dateCombo.setPreferredSize(new Dimension(250, 30));
        dateCombo.setFont(new Font("Cursive", 1, 12));
        dateCombo.setBackground(new Color(152, 213, 152));

        userCombo = new JComboBox();
        userCombo.setPreferredSize(new Dimension(250, 30));
        userCombo.setFont(new Font("Cursive", 1, 12));
        userCombo.setBackground(new Color(152, 213, 152));

        viewGraphBtn.setPreferredSize(new Dimension(380, 30));
        viewGraphBtn.setHorizontalAlignment(SwingConstants.CENTER);
        viewGraphBtn.setBackground(new Color(65, 105, 225));
        viewGraphBtn.setForeground(Color.WHITE);
        viewGraphBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        queryBtn.setPreferredSize(new Dimension(180, 30));
        queryBtn.setHorizontalAlignment(SwingConstants.CENTER);
        queryBtn.setBackground(new Color(65, 105, 225));
        queryBtn.setForeground(Color.WHITE);
        queryBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        cancelBtn.setPreferredSize(new Dimension(180, 30));
        cancelBtn.setHorizontalAlignment(SwingConstants.CENTER);
        cancelBtn.setBackground(new Color(65, 105, 225));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 10));

        topPanel.add(viewGraphBtn, gridConst);

        tablePanel.add(scroll, BorderLayout.CENTER);

        downPanel.add(dateCombo);
        downPanel.add(userCombo);
        downPanel.add(cancelBtn);

        outPanel.add(topPanel, BorderLayout.NORTH);
        outPanel.add(tablePanel, BorderLayout.CENTER);
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
}
