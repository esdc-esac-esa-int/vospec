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

import java.util.LinkedList;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;


/**
 *
 * @author jgonzale
 */
public class SpectraTableModel extends AbstractTableModel {

    private LinkedList nodelist;
    private int columns;
    private Vector<String> metadata_identifiers;


    SpectraTableModel(int columns, Vector<String> metadata_identifiers){
        super();
        this.columns=columns;
        this.metadata_identifiers=metadata_identifiers;
        this.nodelist=new LinkedList();
        //System.out.println("columns "+columns+" metatata size "+metadata_identifiers.size());
    }





    public void addNode(Node node){

        nodelist.add(node);

        this.fireTableDataChanged();

    }

    public void removeAll(){
        
        int size=nodelist.size();

        nodelist.clear();

        if(size>0)this.fireTableRowsDeleted(0, size-1);
    }

    public Node getNode(int row){
        return (Node) nodelist.get(row);
    }




    public Vector<Node> getSelectedNodes(){
        
        Vector <Node> selected_nodes=new Vector();
        
        for(int i=0;i<nodelist.size();i++){
            if(((Node)nodelist.get(i)).getIsSelected()==true){
                selected_nodes.add((Node)nodelist.get(i));
            }
        }
        //System.out.println(selected_nodes.size()+" selected nodes");
        return selected_nodes;
        
    }

    public Vector<Node> getNonSelectedNodes(){

        Vector <Node> nonselected_nodes=new Vector();

        for(int i=0;i<nodelist.size();i++){
            if(((Node)nodelist.get(i)).getIsSelected()==false){
                nonselected_nodes.add((Node)nodelist.get(i));
            }
        }
        //System.out.println(selected_nodes.size()+" selected nodes");
        return nonselected_nodes;

    }




    @Override
    public String getColumnName(int column){

        if(column==0) return "To retrieve";
        else if(column==1) return "Name";
        else if(metadata_identifiers.elementAt(column-2)!=null){
            return metadata_identifiers.elementAt(column-2);
        }else{
            return new String();
        }
    }



    public int getRowCount() {
        return nodelist.size();
    }

    public int getColumnCount() {
        return columns;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        //System.out.println("getting value at "+rowIndex+" "+columnIndex);
        Node aux = (Node) nodelist.get(rowIndex);

        if (columnIndex == 0) {
            return aux.getIsSelected();
        } else if (columnIndex == 1) {
            return aux.getName();
        } else if ((columnIndex < columns) && (columnIndex >= 0)) {
            return aux.getMetadata().get(metadata_identifiers.elementAt(columnIndex-2));
        } else {
            return new String();
        }
    }


    @Override
    public void setValueAt (Object dato, int rowIndex, int columnIndex) {


        Node aux = (Node)nodelist.get(rowIndex);


        if (columnIndex == 0) {
            if((Boolean)dato==true)aux.setIsSelected(true);
            else aux.setIsSelected(false);


           //this.fireTableCellUpdated(rowIndex, columnIndex);
            this.fireTableRowsUpdated(rowIndex, rowIndex);
            //this.fireTableDataChanged();
        }

    }


    @Override
    public Class getColumnClass(int c) {
        //System.out.println("getting class of column "+c);
        return getValueAt(0, c).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int col){
        if(col==0)return true;
        else return false;
    }





}
