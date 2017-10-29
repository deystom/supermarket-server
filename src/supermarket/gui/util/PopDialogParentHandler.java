/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui.util;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 *
 * @author Segun
 */
public class PopDialogParentHandler {

    private JFrame parent;
    private JDialog waitDialog;
    private JDialog parentDialog;
    private JDialog child;
    private Thread th;
    private Timer t;
    private boolean parentState;
    private PopDialogParentHandler.BackgroundWork backWork;
    private Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    private JLabel label;
    private JDialog caller;
    private Timer workT;

    public PopDialogParentHandler(JFrame p, JDialog d){
        parent = p;
        child = d;
        backWork = new PopDialogParentHandler.BackgroundWork(child);
        parentState = false;
        th = new Thread(new Runnable(){
            public void run(){
                backWork.execute();
                handleIt();
            }
        });
        th.start();
    }
/*
    public PopDialogParentHandler(AdminFrame p, JDialog d){
        admin = p;
        child = d;
        parentState = false;
        th = new Thread(new Runnable(){
            public void run(){
                handleAdminIt();
            }
        });
        th.start();
    }
 *
 */

    public PopDialogParentHandler(JDialog p, JDialog d){
        parentDialog = p;
        child = d;
        backWork = new PopDialogParentHandler.BackgroundWork(child);
        parentState = false;
        th = new Thread(new Runnable(){
            public void run(){
                backWork.execute();
                handleDialogIt();
            }
        });
        th.start();
    }

    public boolean isParentEnabled(){
        return parentState;
    }

    private void handleIt(){
        t = new Timer(1000, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(!workT.isRunning()){
                    backWork.execute();
                }
                /*
                if(waitDialog.isShowing() || child.isShowing()){
                    parentState = false;//dont enable the parent
                    //com.sun.awt.AWTUtilities.setWindowOpacity(parent, 0.90f);
                }
                else{
                    //if the child is disposed... then enable parent
                    parentState = true;
                    //com.sun.awt.AWTUtilities.setWindowOpacity(parent, 1f);
                }
                 * 
                 */
                parent.setEnabled(parentState);
            }
        });
        if(!t.isRunning()){
            t.start();
        }
    }
/*
    private void handleAdminIt(){
        t = new Timer(50, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(child.isShowing()){
                    parentState = false;//dont enable the parent
                    //com.sun.awt.AWTUtilities.setWindowOpacity(parent, 0.90f);
                }
                else{
                    //if the child is disposed... then enable parent
                    parentState = true;
                    //com.sun.awt.AWTUtilities.setWindowOpacity(parent, 1f);
                }
                admin.setEnabled(parentState);
            }
        });
        if(!t.isRunning()){
            t.start();
        }
    }
 *
 */

    private void handleDialogIt(){
        t = new Timer(1000, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(!workT.isRunning()){
                    backWork.execute();
                }
                /*
                if(child.isShowing()){
                    parentState = false;//dont enable the parent
                    //com.sun.awt.AWTUtilities.setWindowOpacity(parentDialog, 0.90f);
                }
                else{
                    //if the child is disposed... then enable parent
                    parentState = true;
                    //com.sun.awt.AWTUtilities.setWindowOpacity(parentDialog, 1f);
                }
                 * 
                 */
                parentDialog.setEnabled(parentState);
            }
        });
        if(!t.isRunning()){
            t.start();
        }
    }

    public void stopPopHandler(){
        if(t.isRunning()){
            t.stop();
            if(workT.isRunning()){
                workT.stop();
                waitDialog.hide();
            }
            parent.setEnabled(true);
            //com.sun.awt.AWTUtilities.setWindowOpacity(parent, 1f);
        }
    }
/*
    public void stopAdminPopHandler(){
        if(t.isRunning()){
            t.stop();
            admin.setEnabled(true);
            //com.sun.awt.AWTUtilities.setWindowOpacity(parent, 1f);
        }
    }
 * 
 */

    public void stopDialogPopHandler(){
        if(t.isRunning()){
            t.stop();
            if(workT.isRunning()){
                workT.stop();
                waitDialog.hide();
            }
            parentDialog.setEnabled(true);
            //com.sun.awt.AWTUtilities.setWindowOpacity(parentDialog, 1f);
        }
    }

    class BackgroundWork extends SwingWorker<Void, Void>{

        BackgroundWork(JDialog c){
            caller = c;
            waitDialog = new JDialog();
            label = new JLabel();
            label.setIcon(new ImageIcon(getClass().getResource("ajax-loader.gif")));
            waitDialog.setSize(130, 17);
            waitDialog.setUndecorated(true);
            waitDialog.setLocation((d.width  - 130 )/ 2, (d.height  - 17)/ 2);
            waitDialog.setAlwaysOnTop(true);
            label.setPreferredSize(new Dimension(128, 15));
            waitDialog.getContentPane().add(label);
            waitDialog.setVisible(true);
        }

        public Void doInBackground(){
            int wait = 0;
            //System.out.println(caller.toString().substring(24, 43));
            if(caller.toString().substring(24, 42).equals("StockSummaryDialog") || caller.toString().substring(24, 43).equals("AdminSettingsDialog")){
                wait = 6800;
            }
            else{
                wait = 1600;
            }
            workT = new Timer(wait, new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    try{
                    Thread.sleep(1000);
                    caller.setVisible(true); //set the called cisible
                    }
                    catch(InterruptedException ex){}
                    if(!caller.isVisible()){
                        //if caller is not yet showing
                        if(!waitDialog.isVisible()){
                            waitDialog.show();
                            workT.setRepeats(true);
                        }
                        workT.setRepeats(false);
                    }
                    else{
                        workT.setRepeats(false);
                        waitDialog.hide();
                    }
                    parentState = false;//dont enable the parent
                }//end of actionPerformed
            });

            if(!workT.isRunning()){
                workT.start();
            }
            return null;
        }

        @Override
        public void done(){
            //when its done ... then

        }
    }
}
