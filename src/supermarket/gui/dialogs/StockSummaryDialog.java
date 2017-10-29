/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui.dialogs;
import supermarket.gui.util.*;
import supermarket.gui.AdminFrame;
import supermarket.tables.StockSummary;
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
public class StockSummaryDialog extends JDialog{

    private StockSummaryDialog self = this;

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


    private JComboBox moduleCombo;


    private GridBagLayout gridL;
    private GridBagConstraints gridConst;
    private JTable resultTable;
    private JScrollPane scroll;


    private PopDialogParentHandler popH;

    ///////
    private Thread th;
    private Vector<String> itemsV;
    private StockSummary stockTM;

    
    public StockSummaryDialog(AdminFrame p, Connection c){
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
        loadActions();
        /*
         *
         */
        //setVisible(true);
        th = new Thread(new Runnable(){
            public void run(){
                stockTM = new StockSummary(con, parent.getModuleInUse()); //the stock summary table model with the market model
                resultTable.setModel(stockTM);
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

        switch(parent.getModuleInUse()){
            case 0:
                moduleCombo = new JComboBox(new Object[]{"Wholesales Only"});
                break;
            case 1:
                moduleCombo = new JComboBox(new Object[]{"Retail Only"});
                break;
            case 2:
                moduleCombo = new JComboBox(new Object[]{"Generic Store", "Wholesales Only", "Retail Only"});
                break;
        }

        moduleCombo.setPreferredSize(new Dimension(250, 30));
        moduleCombo.setFont(new Font("Cursive", 1, 12));
        moduleCombo.setBackground(new Color(152, 213, 152));



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

        downPanel.add(moduleCombo);
        //downPanel.add(queryBtn);
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

        moduleCombo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                switch(parent.getModuleInUse()){
                    case 0:
                        stockTM.setModule(0);
                        break;
                    case 1:
                        stockTM.setModule(1);
                        break;
                    case 2:
                        if(moduleCombo.getSelectedIndex() == 0){
                            stockTM.setModule(2);
                        }
                        else if(moduleCombo.getSelectedIndex() == 1){
                            stockTM.setModule(0);
                        }
                        else{
                            stockTM.setModule(1);
                        }
                        break;
                }
                
                resultTable.revalidate();
            }
        });
    }
}
