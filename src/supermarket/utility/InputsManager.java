/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.utility;
import java.awt.Color;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.util.Vector;

/**
 *
 * @author MUSTAFA
 */
public class InputsManager {

    private Pattern partern;
    private Matcher match;
    private static final String REG = "[^a-zA-Z0-9 ]";
    private String inString;
    private boolean check;
    private boolean inputStat;

    public InputsManager(){
        //empty constructor 
    }

    public InputsManager(String in){
        inString = in;
        checkValid();
    }
    
    public InputsManager(int in){
        inString = String.valueOf(in);
        checkValid();
    }

    public void passAnotherInput(String in){
        inString = String.valueOf(in);
        checkValid();
    }

    private void checkValid(){
        partern = Pattern.compile(REG);
        match = partern.matcher(inString);
        check = match.find();
        if(check){
            inputStat = false;
        }
        else{
            inputStat = true;
        }
    }

    public boolean isGoodInput(){
        return inputStat;
    }

    public static String makeUppercase(String in){
        return in.toUpperCase();
        
    }

    public static String makeLowercase(String in){
        return in.toLowerCase();
    }

    public static boolean isValidateEmail(String in){
        boolean valid = false;
        if(in.indexOf("@") != (-1)){
            if(in.indexOf(".") != (-1)){
                valid = true;
            }
        }
        return valid;
    }

    public static void isRestrictedLenght(JTextField f, String s, int r){
        char []c = s.toCharArray();
        if(c.length > r){
           JOptionPane.showMessageDialog(f, "Charater provided too Long \n VALID TEXT LENGTH = " + r, "INPUT ERROR", JOptionPane.PLAIN_MESSAGE);
           f.setText("");
        }
        else{
        }
    }

    public static void paint_unpaintTextFields(JTextField f, boolean op){
        if(!op){
            f.setForeground(Color.WHITE);
            f.setBackground(Color.RED);
        }
        else{
            f.setForeground(Color.BLACK);
            f.setBackground(Color.WHITE);
        }
    }

    public static String formatNairaTextField(JTextField f){
        int temp = 0;
        String output = "";
        if(!f.getText().equals("") || f.getText().startsWith("=N= ")){
            try{
                temp = Integer.valueOf(f.getText());
                output = "=N= " + temp;
            }
            catch(NumberFormatException e){
                //System.err.println("Cannot convert text to Integer : " + e.getMessage());
                //JOptionPane.showMessageDialog(f, "- Values Provided not an INTEGER or contains invalid contents -", "INPUT ERROR", JOptionPane.PLAIN_MESSAGE);
                paint_unpaintTextFields(f, false);
            }
        }
        return output;
    }

    public static String formatNairaTextField(String txt){
        String output = "";
        if(!txt.equals("")){
            try{
                String s = "";
                Vector<Character> a = new Vector<Character>();
                Vector<String> b = new Vector<String>();
                Vector<String> c = new Vector<String>();
                s = txt;
                char []ch = s.toCharArray();
                for(int i=0; i < ch.length; i++){
                    a.addElement(ch[i]); //put the characters in the vector
                }
                int loop = a.size() - 1;
                int count = 0;
                do{                                        
                    if(count == 3){
                        b.addElement(", ");
                        //loop++;
                        count = -1;
                    }
                    else{
                        b.addElement(a.elementAt(loop).toString());
                        loop--;
                    }
                    count++;

                }
                while(loop > -1);

                int loop2 = b.size() - 1;
                do{
                    c.addElement(b.elementAt(loop2));
                    loop2--;
                }
                while(loop2 > -1);

                s = "";
                for(int i=0; i<c.size(); i++){
                    s+= c.elementAt(i);
                }
                output = "=N= " + s;
            }
            catch(NumberFormatException e){
                output = "=N= " + 0;
                System.err.println(e.getMessage());
            }
        }
        return output;
    }

    public static long allowOnlyIntegers(String txt){
        long output = 0;
        String re = "[^0-9]";
        Pattern partern = Pattern.compile(re);
        Matcher match = partern.matcher(txt);
        boolean check = match.find();
        if(!txt.equals("")){
            if(!check){
                //meaning the value provided is a valid integer
                try{
                    output = Integer.valueOf(txt);
                }
                catch(NumberFormatException N){
                    System.err.println("Num Form Error at allowOnly Int " + N.getMessage());
                }
            }
            else{
                //
                output = 0;
            }
        }
        return output;
        
    }
}
