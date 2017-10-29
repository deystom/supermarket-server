/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui.util;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.*;
import java.awt.Color;
//import java.awt.Color;

/**
 *
 * @author MUSTAFA
 */
public class MyLookAndFeel {

    public MyLookAndFeel(){

    }

    public static void setLook(){
        UIManager.put("textHighlightText", new Color(134, 206, 134));
        UIManager.put("textBackground", new Color(134, 206, 134));
        UIManager.put("Panel.background", new Color(134, 206, 134));
        UIManager.put("MenuBar.background", new Color(134, 206, 134));
        UIManager.put("MenuBar:Menu[Selected].backgroundPainter", new Color(134, 206, 134));
        UIManager.put("nimbusBase", new Color(134, 206, 134));
        UIManager.put("nimbusBlueGrey", new Color(134, 206, 134));
        UIManager.put("control", new Color(134, 206, 134));
        try{
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch(Exception e){
           System.err.println(e.getMessage());
        }
    }
/*
    public static void setMacLook(){
        try{
            UIManager.setLookAndFeel( new it.unitn.ing.swing.plaf.macos.MacOSLookAndFeel() );
        }
        catch(UnsupportedLookAndFeelException e){
            System.err.println("Look and Feel not supported : " + e.getMessage());
        }
    }
 * 
 */

    public static void setNativeLook(){
        try{
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e){
            System.err.println("Look and Feel not supported : " + e.getMessage());
        }
    }

    public static void setWindowsLook(){
        try{
            UIManager.setLookAndFeel( new com.sun.java.swing.plaf.windows.WindowsLookAndFeel());
        }
        catch(Exception e){
            System.err.println("Look and Feel not supported : " + e.getMessage());
        }
    }

    public static void setClassicLook(){
        try{
            UIManager.setLookAndFeel( new com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel());
        }
        catch(Exception e){
            System.err.println("Look and Feel not supported : " + e.getMessage());
        }
    }

    public static void setMotifLook(){
        try{
            UIManager.setLookAndFeel( new com.sun.java.swing.plaf.motif.MotifLookAndFeel());
        }
        catch(Exception e){
            System.err.println("Look and Feel not supported : " + e.getMessage());
        }
    }
/*
    public static void setAlloyLook(){
        com.incors.plaf.alloy.AlloyLookAndFeel.setProperty("alloy.licenseCode", "2010/10/07#segsalerty@yahoo.com#17b2m3b#18f3q7");
        try{
            UIManager.setLookAndFeel( new com.incors.plaf.alloy.AlloyLookAndFeel());
        }
        catch(Exception e){
            System.err.println("Look and Feel not supported : " + e.getMessage());
        }
    }
 *
 */


}
