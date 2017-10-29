/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui.dialogs.util;
import supermarket.gui.util.ConfigModuleHandler;
import java.util.List;
import java.util.ArrayList;
import javax.swing.SwingWorker;

/**
 *
 * @author Segun
 */
public class ItemsModuleHandler {
    
    private ConfigModuleHandler config;
    private List<String> combo;
    private SwingWorker<Void, Void> configWork;
    private char c;

    public ItemsModuleHandler(){
        config = new ConfigModuleHandler();
        combo = new ArrayList<String>();
        loadConfiguration();
    }

    private void loadConfiguration(){
        combo.clear(); //clear the list coz its gonna be repopulated here in this method
        configWork = new SwingWorker<Void, Void>(){
            public Void doInBackground(){
                switch(Integer.parseInt(config.read(0))){
                    case 0:
                        //wholesales
                        combo.add("Wholesales Only");
                        c = 'W';
                        break;
                    case 1:
                        //retails
                        combo.add("Retail Only");
                        c = 'R';
                        break;
                    case 2:
                        //generic
                        combo.add("Generic Wholesales / Retail");
                        combo.add("Wholesales Only");
                        combo.add("Retail Only");
                        c = 'G';
                        break;
                    default:
                        combo.add("Select a Module ......");
                        c = 'N';
                        break;
                }
                return null;
            }
        };
        configWork.execute();
    }

    public String[] getModulesCombo(){ 
        return combo.toArray(new String[combo.size()]);
    }

    public char getCurrentModuleInUse(){
        return c;
    }
}
