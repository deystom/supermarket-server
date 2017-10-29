/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket.tables.util;
import javax.swing.table.AbstractTableModel;
import java.util.Vector;

/**
 *
 * @author Segun
 */
public class MyTablesTemplate extends AbstractTableModel{
    private Vector<Object> headV;
    private Vector<Object> dataV;

    public MyTablesTemplate(){
        headV = new Vector<Object>();
        dataV = new Vector<Object>();
    }

    public Object getValueAt(int row, int col){
        Object obj = null;
        if(row < dataV.size()){
            Vector r = (Vector) dataV.elementAt(row);
            if(col < r.size()){
                obj = r.elementAt(col);
            }
        }
        return obj;
    }

    public int getColumnCount(){
        return headV.size();
    }

    public int getRowCount(){
        return dataV.size();
    }

    @Override
    public String getColumnName(int c){
        if(c < headV.size()){
            return (String) headV.elementAt(c);
        }
        else{
            return "unknown";
        }
    }
    
    /**
     * my designed methods are below for setting and getting and manipulating the header and data vecotrs
     */

    public void setHeaderVector(Vector h){
        headV = h;
    }

    public void setDataVector(Vector d){
        dataV = d;
    }

    public void addToHeaderVector(Object o){
        headV.addElement(o);
    }

    public void addToDataVector(Object d){
        dataV.addElement(d);
    }
    
    public Vector getHeaderVector(){
        return headV;
    }

    public Vector getDataVector(){
        return dataV;
    }

    public void clearTable(){
      if(!headV.isEmpty() && !dataV.isEmpty()){
        //headV.clear();
        dataV.clear();
      }
    }
}
