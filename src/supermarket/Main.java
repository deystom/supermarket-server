/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket;
import supermarket.gui.StartUpFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author Segun
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                StartUpFrame start = new StartUpFrame();
                start.initializeOption();
                start.setVisible(true);
            }
        });
    }

}
