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

import esac.archive.absi.modules.cl.samp.Interop;
import esac.archive.absi.modules.cl.samp.InteropMenuBuilder;
import esavo.vospec.main.*;
import esavo.vospec.samp.MessageSenderMenu;
import esavo.vospec.samp.SingleSpectrumSenderListener;
import esavo.vospec.spectrum.Spectrum;
import esavo.vospec.spectrum.SpectrumSet;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXTable;


/**
 *
 * @author jgonzale
 */


public class TablesPanel extends javax.swing.JTabbedPane {

    Hashtable tables = new Hashtable();
    Hashtable urls = new Hashtable();
    Hashtable ids = new Hashtable();



    SpectraTableModel tablemodel = null;
    JXTable table = null;
    //TableRowSorter<SpectraTableModel> sorter = null;
    JScrollPane scrollpane = null;


    public MessageSenderMenu singleSpectrumSenderMenu;
    public MessageSenderMenu singleTableSenderMenu;
    public MessageSenderMenu tableSenderMenu;

    public SingleSpectrumSenderListener singleSpectrumSenderListener;

    public JPopupMenu popup;

    public MouseListener mouselistener;
    public Interop interop;





    public TablesPanel() {
        
        this.setPreferredSize(new Dimension(600, 400));

    }

    public void setListeners(MouseListener mouselistener, Interop interop) {
            this.interop=interop;
            this.mouselistener=mouselistener;
            initializeSpectrumContextMenu();
    }

    
    public void resetPanel(){
        this.removeAll();
        tables=new Hashtable();
        urls=new Hashtable();
        ids=new Hashtable();
    }


    private void initializeSpectrumContextMenu(){

        popup = new JPopupMenu();

         //SAMP menu

        List menuBlocks = new LinkedList();

        singleSpectrumSenderMenu =
                new MessageSenderMenu(interop, "Send selected Spectra to", "All", "spectrum.load.ssa-generic");

        singleTableSenderMenu =
                new MessageSenderMenu(interop, "Send Spectra as tables to", "All", "table.load.votable");

        tableSenderMenu =
                new MessageSenderMenu(interop, "Send the Table to", "All", "table.load.votable");



        menuBlocks.add(singleSpectrumSenderMenu);
        menuBlocks.add(singleTableSenderMenu);
        menuBlocks.add(tableSenderMenu);



        InteropMenuBuilder interopMenu = new InteropMenuBuilder("SAMP", interop, menuBlocks);

        popup.add(interopMenu);



        //PROPERTIES menu

        JMenuItem properties = new JMenuItem("Properties");
        properties.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {

                Vector<Node> nodes = getHighlightedNodes();

                Vector<VOSpecInfo> infos = new Vector();

                for (int i = 0; i < nodes.size(); i++) {
                    //tablespanel.singleSpectrumSenderMenu.addActionListener((new SingleSpectrumSenderListener("spectrum.load.ssa-generic", aiospectooldetached.interop, nodes.elementAt(i).getSpectrum())));
                    VOSpecInfo info = new VOSpecInfo(nodes.get(i).getMetadata(), (Spectrum)nodes.get(i).getRelatedObject());
                    info.setVisible(true);
                    infos.add(info);
                }
            }
                
        });

        popup.add(properties);
        
        
        //SAVE AS menu

        JMenuItem saveAs = new JMenuItem("Save as...");

        saveAs.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {

                Vector<Node> nodes = getHighlightedNodes();

                Vector<VOSpecInfo> infos = new Vector();

                for (int i = 0; i < nodes.size(); i++) {

                    Node node = nodes.get(i);

                    VOSpecInfo info = new VOSpecInfo(node.getMetadata(), (Spectrum)node.getRelatedObject());
                    info.setVisible(false);
                    info.saveSpectrum();
                    info.dispose();

                }
            }
        });

        popup.add(saveAs);


        
    }

    public String getSelectedTableUrl(){
        
        return (String) urls.get(this.getTitleAt(this.getSelectedIndex()));
        
    }

    public String getSelectedTableName(){

        return (String)this.getTitleAt(this.getSelectedIndex());

    }

    public String getSelectedTableId(){

        return (String) ids.get(this.getTitleAt(this.getSelectedIndex()));

    }

    
    /*
     * Uncheck all the entrys in the table
     * 
     * */


    public void uncheckAll(){


        Vector tables_vector = new Vector(tables.values());

        for (int i = 0; i < tables_vector.size(); i++) {


            Vector<Node> selected_nodes = ((SpectraTableModel) ((Table) tables_vector.get(i)).getTablemodel()).getSelectedNodes();

            for(int j=0;j<selected_nodes.size();j++){
                ((Spectrum)selected_nodes.get(j).getRelatedObject()).setSelected(false);
                selected_nodes.get(j).setIsSelected(false);
            }


        }

        this.repaint();

    }




    /*
     *
     * Deletes Tabs and tables wich are not the Local Data one
     * (execute before adding the results of an SSA query)
     *
     * */


    public void deleteSSATables(){


            Vector tablesvector = new Vector(tables.values());
            for(int i=0;i<tablesvector.size();i++){
                //System.out.println("tree sees title "+((Table)tablesvector.get(i)).getTitle());
                if(!((Table)tablesvector.get(i)).getTitle().equals("Local Data")&&
                        !((Table)tablesvector.get(i)).getTitle().equals("SED")){
                        this.removeTabAt(this.indexOfComponent(((Table)tablesvector.get(i)).getScrollpane()));

                        //System.out.println("tables deleting "+((Table)tablesvector.get(i)).getTitle());
                        tables.remove(((Table)tablesvector.get(i)).getTitle());
                        urls.remove(((Table)tablesvector.get(i)).getTitle());
                        ids.remove(((Table)tablesvector.get(i)).getTitle());

                }
            }
            this.repaint();

        
    }




    /*
     * 
     * Returns all the selected tablenodes
     * 
     * */


    public Vector getSelectedNodes() {

        Vector selected_tablenodes = new Vector();

        //for each table (server)

        Vector tables_vector = new Vector(tables.values());

        for (int i = 0; i < tables_vector.size(); i++) {

            //for each selected node get the spectrum

            Vector<Node> selected_nodes = ((SpectraTableModel) ((Table) tables_vector.get(i)).getTablemodel()).getSelectedNodes();

            selected_tablenodes.addAll(selected_nodes);


        }

        return selected_tablenodes;

    }

    /*
     * 
     * Returns all the non-selected tablenodes
     * 
     * */


    public Vector getNonSelectedNodes() {

        Vector nonselected_tablenodes = new Vector();

        //for each table (server)

        Vector tables_vector = new Vector(tables.values());

        for (int i = 0; i < tables_vector.size(); i++) {

            //for each selected node get the spectrum

            Vector<Node> selected_nodes = ((SpectraTableModel) ((Table) tables_vector.get(i)).getTablemodel()).getNonSelectedNodes();

            nonselected_tablenodes.addAll(selected_nodes);


        }

        return nonselected_tablenodes;



    }



    /*
     *
     * Returns all the highlighted tablenodes
     *
     * */


    public Vector getHighlightedNodes() {

        Vector selected_tablenodes = new Vector();

        //Pick the JXtable being displayed now


        JScrollPane scroll = (JScrollPane)(this.getComponentAt(this.getSelectedIndex()));
        
        JViewport viewPort = (JViewport)scroll.getComponent(0);

        JXTable selectedTable = (JXTable) viewPort.getComponent(0);

        //JXTable selectedTable = (JXTable) ((JScrollPane)((JViewport)this.getComponentAt(this.getSelectedIndex())).getComponent(0)).getComponent(0);

        //Pick the rows selected
        int [] selectedRows = selectedTable.getSelectedRows();

        //For each selected row add the selected nodes
        for(int i=0; i<selectedRows.length;i++){
            selected_tablenodes.add(((SpectraTableModel)selectedTable.getModel()).getNode(selectedRows[i]));
        }

        //System.out.println(selected_tablenodes.size());

        return selected_tablenodes;

    }





    /*
     * 
     * Highlights a TableNode (selects the correct Tab, places the scrollpanel
     * in his position and selects the node)
     * 
     * */


    public void higlightNode(Node node) {

        //System.out.println("highlighting node");

        //for each table
        Vector tablesvector = new Vector(tables.values());
        for (int i = 0; i < tablesvector.size(); i++) {
            JXTable thistable = ((Table) tablesvector.get(i)).getTable();
            SpectraTableModel thistablemodel = (SpectraTableModel) ((Table) tablesvector.get(i)).getTablemodel();
            JScrollPane thisscrollpanel = (JScrollPane) ((Table) tablesvector.get(i)).getScrollpane();
            //for each node
            for (int j = 0; j < thistablemodel.getRowCount(); j++) {

                //if it's this node, then select it
                if (thistablemodel.getNode(j).equals(node)) {

                    //Selects the element
                    thistable.setRowSelectionInterval(thistable.convertRowIndexToView(j), thistable.convertRowIndexToView(j));

                    //Choooses the tab of this table
                    this.setSelectedComponent(((Table) tablesvector.get(i)).getScrollpane());

                    //Scrolls JScrollPane to the element
                    thisscrollpanel.getViewport().setViewPosition(new Point(0, (thistable.convertRowIndexToView(j) * thistable.getRowHeight())));
                    thisscrollpanel.repaint();

                }
            }

        }


    }

    public void highlightTablePos(String tableId, String url , int tablePos){



         Vector tablesvector = new Vector(tables.values());
        for (int i = 0; i < tablesvector.size(); i++) {
            JXTable thistable = ((Table) tablesvector.get(i)).getTable();
            JScrollPane thisscrollpanel = (JScrollPane) ((Table) tablesvector.get(i)).getScrollpane();


            String thisTableName = ((Table) tablesvector.get(i)).getTitle();
            if (thisTableName==null) thisTableName= "";
            String thisTableUrl = (String) urls.get(thisTableName);
            if (thisTableUrl==null) thisTableUrl="";
            String thisTableId = (String) ids.get(thisTableName);
            if (thisTableId==null) thisTableId="";

            if( thisTableName.trim().equalsIgnoreCase(tableId) ||thisTableName.trim().equalsIgnoreCase(url) ||
                    thisTableUrl.trim().equalsIgnoreCase(url) || thisTableUrl.trim().equalsIgnoreCase(tableId) ||
                    thisTableId.trim().equalsIgnoreCase(url) || thisTableId.trim().equalsIgnoreCase(tableId)){
                //Selects the element
                    thistable.setRowSelectionInterval(thistable.convertRowIndexToView(tablePos), thistable.convertRowIndexToView(tablePos));

                    //Choooses the tab of this table
                    this.setSelectedComponent(((Table) tablesvector.get(i)).getScrollpane());

                    //Scrolls JScrollPane to the element
                    thisscrollpanel.getViewport().setViewPosition(new Point(0, (thistable.convertRowIndexToView(tablePos) * thistable.getRowHeight())));
                    thisscrollpanel.repaint();
            }

        }

    }


    /*
     *
     * Determines wether an spectrumset contains an spectrum and
     * on which position.
     *
     * */

/*
    public int containsSpectrum(SpectrumSet sp, Spectrum s) {

        for(int j=0; j <sp.getSpectrumSet().size(); j++) {
            Spectrum spectrum = sp.getSpectrum(j);
            if(spectrum.getUrl().equals(s.getUrl())) return j;
        }

        return -1;
    }*/



    public void addSpectrumToTable(String nodeTitle, Spectrum spectrum) {

        this.createTable(nodeTitle);


        Table thistable = (Table) tables.get(nodeTitle);


        Node newNode = new Node(spectrum.getTitle());
        newNode.setIsSelected(true);
        newNode.setDownloaded(true);
        newNode.setRelatedObject(spectrum);
        
        newNode.setMetadata_identifiers(spectrum.getMetadata_identifiers());
        newNode.setMetadata(spectrum.getMetaData());

        newNode.setRelatedObject(spectrum);
        spectrum.setNode(newNode);

        ((SpectraTableModel) thistable.getTablemodel()).addNode(newNode);



    }


    /*
     * Generates Nodes for the new set of spectrums
     *
     * @param newlocalset New Set of spectrums relative to Local Data, both new and old
     * */


    public SpectrumSet addLocal(SpectrumSet newlocalset) {


        //If there are no local spectra, delete that table (if exists) and return

        if(newlocalset.spectrumSet.size()==0){

            Vector tablesvector = new Vector(tables.values());
            for(int i=0;i<tablesvector.size();i++){

                if(((Table)tablesvector.get(i)).getTitle().equals("Local Data")){
                        this.removeTabAt(this.indexOfComponent(((Table)tablesvector.get(i)).getScrollpane()));
                        tables.remove(((Table)tablesvector.get(i)).getTitle());

                }
            }
            this.repaint();
            return new SpectrumSet();

        }


        
        this.createTable("Local Data");
        

        SpectraTableModel table_model = ((SpectraTableModel) ((Table) tables.get("Local Data")).getTablemodel());

        Vector<Node> resultant_nodes = new Vector() ;

        for (int j = 0; j < newlocalset.getSpectrumSet().size(); j++) {
            Spectrum newspectrum = newlocalset.getSpectrum(j);

            boolean already_exists = false;
            Node found_tablenode=null;

            for (int i = 0; i < table_model.getRowCount(); i++) {
                Node node = table_model.getNode(i);

                if (((Spectrum)node.getRelatedObject()).getUrl().equals(newspectrum.getUrl())) {
                    already_exists = true;
                    found_tablenode=node;
                }
            }

            if (already_exists) {
                //link node to this spectrum
                found_tablenode.setRelatedObject(newspectrum);
                newspectrum.setNode(found_tablenode);
                resultant_nodes.add(found_tablenode);

             } else {

                //add node

                Node newnode = new Node(newspectrum.getTitle());
                newnode.setRelatedObject(newspectrum);
                newnode.setIsSelected(true);
                newnode.setMetadata(newspectrum.getMetaData());
                newspectrum.setNode(newnode);
                table_model.addNode(newnode);
                resultant_nodes.add(newnode);


            }




            newlocalset.addSpectrum(j, newspectrum);

        }

        table_model.removeAll();

        for(int i=0;i<resultant_nodes.size();i++){
            table_model.addNode(resultant_nodes.get(i));
        }


        return newlocalset;

    }






    /*
     * Creates new JTable
     *
     *
     * */

    private void createTable(String name) {

        if(tables.get(name)!=null){
             return;
         }


        //create the tab
        tablemodel = new SpectraTableModel(2, null);


        //create JTable, link TableRowSorter
        table = new JXTable() {

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {

                if (column == 0) {
                    return this.getDefaultRenderer(Boolean.class);
                } else {

                    Node node = ((SpectraTableModel) (this.getModel())).getNode(this.convertRowIndexToModel(row));

                    ColorCellRenderer renderer = new ColorCellRenderer();

                    if (node.isSelected) {
                        renderer.isSelected(true);
                    } else {
                        renderer.isSelected(false);
                    }

                    if (node.isFailed) {
                        renderer.setColor(ColorCellRenderer.RED);
                        return renderer;
                    } else if (node.isDownloaded) {
                        renderer.setColor(ColorCellRenderer.GREEN);
                        return renderer;
                    } else if (node.isWaiting) {
                        renderer.setColor(ColorCellRenderer.YELLOW);
                        return renderer;
                    } else {
                        renderer.setColor(ColorCellRenderer.WHITE);
                        return renderer;
                    }

                }
            }
        };


        table.setModel(tablemodel);

        table.getColumnModel().getColumn(1).setPreferredWidth(517);

        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        

        table.addMouseListener(mouselistener);


        table.setDragEnabled(true);


        TransferHandler handler = new TransferHandler() {

            @Override
            public int getSourceActions(JComponent comp) {
                return COPY_OR_MOVE;
            }

            @Override
            public Transferable createTransferable(JComponent comp) {
                return new TransferableTable((JXTable)comp);
            }

            @Override
            public void exportDone(JComponent comp, Transferable trans, int action) {
            }
        };
        table.setTransferHandler(handler);

        //Create the scroll pane and add the table to it.
        scrollpane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        this.addTab(name, scrollpane);

        tables.put(name, new Table(tablemodel, table,/* sorter,*/ scrollpane, name, null));
        this.repaint();


    }






    /*
     * Inserts a new JTable in the JTabbedPane (with the results of an SSA query)
     *
     * */


    public void addSSATable(String tablename, String tableId, Vector nodes, String url) {


        ///////////////////
        //CREATE TABLE TAB
        ///////////////////

        if(nodes.size()==0)return;

        Node samplenode = (Node) nodes.get(0);


        tablemodel = new SpectraTableModel(samplenode.getMetadata_identifiers().size() + 2, samplenode.getMetadata_identifiers());



        //create JTable, link TableRowSorter
        table = new JXTable() {


            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {

                if (column == 0) {
                    return this.getDefaultRenderer(Boolean.class);



                } else {

                    Node node = ((SpectraTableModel) (this.getModel())).getNode(this.convertRowIndexToModel(row));

                    ColorCellRenderer renderer = new ColorCellRenderer();

                    if (node.isSelected) {
                        renderer.isSelected(true);
                    } else {
                        renderer.isSelected(false);
                    }

                    if (node.isFailed) {
                        renderer.setColor(ColorCellRenderer.RED);
                        return renderer;
                    } else if (node.isDownloaded) {
                        renderer.setColor(ColorCellRenderer.GREEN);
                        return renderer;
                    } else if (node.isWaiting) {
                        renderer.setColor(ColorCellRenderer.YELLOW);
                        return renderer;
                    } else {
                        renderer.setColor(ColorCellRenderer.WHITE);
                        return renderer;
                    }

                }
            }
        };


        table.setModel(tablemodel);

        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        table.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);

        table.setToolTipText("Right-click for options");

        table.addMouseListener(mouselistener);
        

        table.setDragEnabled(true);
        //TableDragSource ds = new TableDragSource(table, DnDConstants.ACTION_COPY_OR_MOVE);


        TransferHandler handler = new TransferHandler() {

            @Override
            public int getSourceActions(JComponent comp) {
                return COPY_OR_MOVE;
            }

            @Override
            public Transferable createTransferable(JComponent comp) {
                //System.out.println(((JTable)comp).getName());
                //return new StringSelection("hello");
                //return new JTable();
                return new TransferableTable((JXTable)comp);
            }

            @Override
            public void exportDone(JComponent comp, Transferable trans, int action) {
            }
        };
        table.setTransferHandler(handler);


        //Create the scroll pane and add the table to it.
        scrollpane = new JScrollPane(table);


        //Add the scroll pane to this panel.
        this.addTab(tablename, scrollpane);

        //this.addTab(tablename, new JLabel("Hey!"));

        tables.put(tablename, new Table(tablemodel, table, /*sorter,*/ scrollpane, tablename, handler));
        urls.put(tablename, url);
        ids.put(tablename, tableId);



        /////////////
        // ADD NODES
        /////////////

        for (int i = 0; i < nodes.size(); i++) {

            Node node = (Node) nodes.get(i);

            tablemodel.addNode(node);

        }


    //return table;





    }
}



