/* 
 * Copyright (C) 2017 ESDC/ESA 2017
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package esavo.vospec.util;

import javax.swing.event.*;
import javax.swing.table.*;

/**
 *
 * 
 *
 * <pre>
 * %full_filespec: ExtendedTableModel.java,3:java:2 %
 * %derived_by: ibarbarisi %
 * %date_created: Tue Sep  6 16:07:28 2005 %
 * 
 * </pre>
 *
 * @author
 * @version %version: 3 %
 */

public class ExtendedTableModel extends DefaultTableModel{
    
    public static final long serialVersionUID = -6981298742743094876L;
  
    private TableModelListener tableModelListener;
  
    public ExtendedTableModel() {
        super();
    }
    
    public ExtendedTableModel(Object [][] obj,String [] s) {
        super(obj,s);
    }
    
    @Override
    public void addTableModelListener(TableModelListener tableModelListener) {
      this.tableModelListener = tableModelListener;   
    }
    public void removeTableModelListener() {
      this.tableModelListener = null;   
    }
   
    
    public synchronized void setValueAt(Object value, int ct, int column,boolean event){
        super.setValueAt(value,ct,column); 
        if(!event){
            column = 0;
         }
         if(this.tableModelListener!= null) {
            TableModelEvent tableModelEvent = new TableModelEvent(this,ct,ct,column);
            tableModelListener.tableChanged(tableModelEvent);
         }   
   }

    

}
