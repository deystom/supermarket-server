/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.network;
import javax.swing.SwingWorker;
import java.net.*;

/**
 *
 * @author MUSTAFA
 */
public class HandleNetwork extends SwingWorker<Void, Void>{
    
    private InetAddress myAd;
    private String hostName;
    private String hostIp;
    private boolean conStatus;

    public HandleNetwork(){
    }

    private void performTask(){
        try{
            myAd = InetAddress.getLocalHost();
            conStatus = myAd.isSiteLocalAddress() ? false: true;
            if(!conStatus){
                hostName = myAd.getHostName();
                hostIp = myAd.getHostAddress();
            }
        }
        catch(UnknownHostException he){
            System.err.println("Error at getting the localhost : " + he.getMessage());
        }
        //System.out.println(hostName + " : " + hostIp);
    }

    public String getHostName(){
        return hostName;
    }

    public String getHostIp(){
        return hostIp;
    }

    public boolean getConnectionStatus(){
        performTask();
        return conStatus;
    }

    public boolean isConnected(){
        return conStatus;
    }

    public Void doInBackground(){
        performTask();
        return null;
    }

    @Override
    public void done(){
        //do nothing when done
    }
}
