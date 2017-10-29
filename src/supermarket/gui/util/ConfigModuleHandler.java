/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.gui.util;
import java.util.*;
import java.io.*;

/**
 *
 * @author mustafa
 */
public class ConfigModuleHandler {

    private String fileDir = new File("").getAbsolutePath();
    private String fileName = "/super_market_settings.ini";
    private Scanner scan;
    private File f;
    private List<String> lines;
    private FileWriter writeF;

    public ConfigModuleHandler(){
        //if(new File(fileDir).isDirectory()){
            f = new File(fileDir + fileName);
            lines = new LinkedList<String>();
            if(!f.exists()){
                try{
                    f.createNewFile();
                    FileWriter wF = new FileWriter(fileDir + fileName);
                    wF.write("0\n"); //default market module value to be written in config file
                    wF.write("###end of Market Module Settings###\n");
                    wF.write("120\n"); //default autologout time value to be written in config file
                    wF.write("###end of Auto Logout Settings###\n");
                    wF.write("1.0.0.2\n"); //default autologout time value to be written in config file
                    wF.write("###end of version details###\n");
                    wF.flush();
                    wF.close();
                    //write on line 1 the value "0".the configuration for market module handlern
                }
                catch(IOException iE){
                    System.err.println(iE.getMessage());
                }
            }
        //}
    }

    public String read(int ln){
        try{
            scan = new Scanner(f);
        }
        catch(FileNotFoundException fE){
            System.err.println(fE.getMessage());
        }
        String out = "";
        lines.clear();
        try{
            if(!scan.hasNextLine()){
               lines.add(scan.nextLine());
               out = lines.get(ln);
            }
            else{
                while(scan.hasNextLine()){
                    lines.add(scan.nextLine());
                }
                out = lines.get(ln);
            }
        }
        catch(NullPointerException nE){
            out = "NOT CURRENTLY AVAILABLE";
            System.err.println(nE.getMessage());
        }
        scan.close();
        return out;
    }

    public void write(String s, int ln){
        //write to a file String Text and Integer Line index
        try{
            scan = new Scanner(f);
        }
        catch(FileNotFoundException fE){
            System.err.println(fE.getMessage());
        }
        String allString = "";
        lines.clear();
        if(!scan.hasNextLine()){
            lines.add(s);
        }
        else{
            while(scan.hasNextLine()){
                lines.add(scan.nextLine());
            }
        }
        scan.close();
        
        if(ln <= lines.size()){
            lines.set(ln, s);
        }
        else{
            lines.set(lines.size(), s);
        }
        Iterator<String> i = lines.iterator();

        while(i.hasNext()){
            allString += i.next() + "\n";
        }
        try{
            writeF = new FileWriter(fileDir + fileName);
            writeF.write(allString);
            writeF.flush();
            Thread.sleep(1000);
            writeF.close();
        }
        catch(IOException e){
            System.err.println(e.getMessage());
        }
        catch(InterruptedException iE){
            System.err.println(iE.getMessage());
        }
    }
}