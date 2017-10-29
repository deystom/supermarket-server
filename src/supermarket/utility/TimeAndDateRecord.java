/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.utility;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

/**
 *
 * @author Segun
 */
public class TimeAndDateRecord {

    //private enum Dayz{SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY};

    public TimeAndDateRecord(){
        
    }

    public static String getDate(){
        DateFormat dateForm = new SimpleDateFormat("dd/MM/yyyy");
        Date dateD = new Date();
        return dateForm.format(dateD);
    }

    public static String getTime(){
        DateFormat dateForm = new SimpleDateFormat("HH:mm:ss");
        Date dateD = new Date();
        return dateForm.format(dateD);
    }

    public static String getDayOfTheWeek(){
        String today = "";
/*
        DateFormat dateForm = new SimpleDateFormat("yyyy"); //date formatter
        Date dateD = new Date(); //date instance
        int year = Integer.parseInt(dateForm.format(dateD));
        dateForm = new SimpleDateFormat("MM");
        int month = Integer.parseInt(dateForm.format(dateD)) + 1;
        dateForm = new SimpleDateFormat("dd");
        int day = Integer.parseInt(dateForm.format(dateD));

 * 
 */
        Calendar can = Calendar.getInstance();
        can.setTimeZone(TimeZone.getDefault());
        //can.set(Calendar.YEAR, year);
        //can.set(Calendar.MONTH, month);
        //can.set(Calendar.DAY_OF_MONTH, day);
        int to = can.get(Calendar.DAY_OF_WEEK);
        
        switch(to){
            case 1:
                today = "SUNDAY";
                //today = "FRIDAY";
                break;
            case 2:
                today = "MONDAY";
                //today = "SATURDAY";
                break;
            case 3:
                today = "TUESDAY";
                //today = "SUNDAY";
                break;
            case 4:
                today = "WEDNESDAY";
                //today = "MONDAY";
                break;
            case 5:
                today = "THURSDAY";
                //today = "TUESDAY";
                break;
            case 6:
                today = "FRIDAY";
                //today = "WEDNESDAY";
                break;
            case 7:
                today = "SATURDAY";
                //today = "THURSDAY";
                break;
            default:
                today = "INVALID";
                break;
        }
        return today;
    }
}
