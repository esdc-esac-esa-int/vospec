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
package esavo.vospec.resourcepanel;

import javax.swing.JScrollPane;
import javax.swing.TransferHandler;
import javax.swing.table.TableModel;
import org.jdesktop.swingx.JXTable;
//import javax.swing.table.TableRowSorter;

/**
 *
 * @author jgonzale
 */
public class Table {

    private SpectraTableModel tablemodel = null;
    private JXTable table = null;
  //  private TableRowSorter sorter = null;
    private JScrollPane scrollpane = null;
    private String title = null;
    //private TableDragSource tabledragsource = null;
    private TransferHandler transferhandler = null;
    

    Table(){

    }

    Table(SpectraTableModel tablemodel, JXTable table/*, TableRowSorter sorter*/,
            JScrollPane scrollpane, String title, TransferHandler handler){
        this.tablemodel=tablemodel;
        this.table=table;
        //this.sorter=sorter;
        this.scrollpane=scrollpane;
        this.title=title;
        this.transferhandler=handler;
    }

    /**
     * @return the tablemodel
     */
    public TableModel getTablemodel() {
        return tablemodel;
    }

    /**
     * @param tablemodel the tablemodel to set
     */
    public void setTablemodel(SpectraTableModel tablemodel) {
        this.tablemodel = tablemodel;
    }

    /**
     * @return the table
     */
    public JXTable getTable() {
        return table;
    }

    /**
     * @param table the table to set
     */
    public void setTable(JXTable table) {
        this.table = table;
    }

    /**
     * @return the sorter
     */
   /* public TableRowSorter getSorter() {
        return sorter;
    }*/

    /**
     * @param sorter the sorter to set
     */
    /*public void setSorter(TableRowSorter sorter) {
        this.sorter = sorter;
    }*/

    /**
     * @return the scrollpane
     */
    public JScrollPane getScrollpane() {
        return scrollpane;
    }

    /**
     * @param scrollpane the scrollpane to set
     */
    public void setScrollpane(JScrollPane scrollpane) {
        this.scrollpane = scrollpane;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the transferhandler
     */
    public TransferHandler getTransferhandler() {
        return transferhandler;
    }

    /**
     * @param transferhandler the transferhandler to set
     */
    public void setTransferhandler(TransferHandler transferhandler) {
        this.transferhandler = transferhandler;
    }





}
