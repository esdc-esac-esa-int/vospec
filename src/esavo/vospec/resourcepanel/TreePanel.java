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
import esavo.vospec.spectrum.*;
import java.awt.Dimension;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;



/**
 *
 * @author jgonzale
 */
public class TreePanel extends javax.swing.JScrollPane {


    public Node rootNode;
    
    public Node nameNode;
    
    public JTree serverListTree;
    public TreePath path;
    public SpectraTreeCellRenderer cre;


    public MessageSenderMenu singleSpectrumSenderMenu;
    public MessageSenderMenu singleTableSenderMenu;

    public JPopupMenu spectrumPopup;
    public JPopupMenu serverPopup;

    public MouseListener mouselistener;
    public Interop interop;

    
    public TreePanel(){
        this.setPreferredSize(new Dimension(600, 400));
           
    }

    public void setListeners(MouseListener mouselistener, Interop interop) {
            this.interop=interop;
            this.mouselistener=mouselistener;
            initialize();
    }


    private void initialize(){
        
        rootNode = new Node("Spectra List");
        serverListTree = new ExtendedJTree(new DefaultTreeModel(rootNode));

        cre = new SpectraTreeCellRenderer();
        serverListTree.setCellRenderer(cre);
        serverListTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION
                );
      
        serverListTree.putClientProperty("JTree.lineStyle", "Angled");

        serverListTree.addMouseListener(mouselistener);

        serverListTree.setEditable(false);


        serverListTree.setDragEnabled(true);

        TransferHandler handler = new TransferHandler() {

            @Override
            public int getSourceActions(JComponent comp) {
                return COPY_OR_MOVE;
            }

            @Override
            public Transferable createTransferable(JComponent comp) {

                TreePath path = ((JTree)comp).getSelectionPath();

                if ((path == null) || (path.getPathCount() <= 1)) {
                    // We can't move the root node or an empty selection
                    return null;
                }
                Node node = (Node) path.getLastPathComponent();

                Spectrum spectrumToAdd = (Spectrum)node.getRelatedObject();

                Spectrum spectrum = new Spectrum(spectrumToAdd);

                spectrum.setNode(null);
                spectrum.setAioSpecToolDetached(null);
                spectrum.setColorNode(null);

                return new TransferableTreeNode(spectrum);
            }

            @Override
            public void exportDone(JComponent comp, Transferable trans, int action) {
            }
        };

        serverListTree.setTransferHandler(handler);


        this.setViewportView(serverListTree);




        initializeSpectrumContextMenu();

        initializeServerContextMenu();


        
    }







    public void resetPanel(){
        initialize();
        repaintServerListTree();
    }


    private void initializeServerContextMenu(){

        //Create Pop-up and add SAMP sending menu to it

        serverPopup = new JPopupMenu();


        //ORGANIZE menu

        JMenuItem properties = new JMenuItem("Reorganize tree");

        properties.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {

                TreePath path = serverListTree.getSelectionPath();

                TreeRebuilder rebuilder = new TreeRebuilder((Node) path.getLastPathComponent(), serverListTree);
                rebuilder.setVisible(true);
                rebuilder.toFront();


            }
        });

        serverPopup.add(properties);



    }



    private void initializeSpectrumContextMenu(){

        //Create Pop-up

        spectrumPopup = new JPopupMenu();

        //add SAMP sending menu to it




        List menuBlocks = new LinkedList();


        singleSpectrumSenderMenu =
                new MessageSenderMenu(interop, "Send as Spectrum to", "All", "spectrum.load.ssa-generic");
        singleTableSenderMenu =
                new MessageSenderMenu(interop, "Send as Table to", "All", "table.load.votable");


        menuBlocks.add(singleSpectrumSenderMenu);
        menuBlocks.add(singleTableSenderMenu);

        InteropMenuBuilder interopMenu = new InteropMenuBuilder("SAMP", interop, menuBlocks);

        spectrumPopup.add(interopMenu);



        //PROPERTIES menu

        JMenuItem properties = new JMenuItem("Properties");

        properties.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {

                TreePath[] paths = serverListTree.getSelectionPaths();



                Vector<VOSpecInfo> infos = new Vector();

                for (int i = 0; i < paths.length; i++) {

                    Node node = (Node) paths[i].getLastPathComponent();
                    
                    VOSpecInfo info = new VOSpecInfo(node.getMetadata(), (Spectrum)node.getRelatedObject());
                    info.setVisible(true);
                    infos.add(info);
                }
            }
        });

        spectrumPopup.add(properties);



        //SAVE AS menu

        JMenuItem saveAs = new JMenuItem("Save as...");

        saveAs.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {

                TreePath[] paths = serverListTree.getSelectionPaths();



                //Vector<AioSpecInfo> infos = new Vector();

                for (int i = 0; i < paths.length; i++) {

                    Node node = (Node) paths[i].getLastPathComponent();

                    VOSpecInfo info = new VOSpecInfo(node.getMetadata(), (Spectrum)node.getRelatedObject());
                    info.setVisible(false);
                    info.saveSpectrum();
                    info.dispose();

                }
            }
        });

        spectrumPopup.add(saveAs);












    }



    /*
     *
     * Deletes Tabs and tables wich are not the Local Data one
     * (execute before adding the results of an SSA query)
     *
     * */


    public void deleteSSATables(){

        Vector <Node> nodesToRemove = new Vector();

        for(int i=0; i < rootNode.getChildCount(); i++) {

            Node tmpNode = (Node) rootNode.getChildAt(i);
            if(!tmpNode.toString().equals("Local Data")&&!tmpNode.toString().equals("SED")) {
                tmpNode.removeAllChildren();
                nodesToRemove.add(tmpNode);
            }
        }

        for(int i=0;i<nodesToRemove.size();i++){
            rootNode.remove(nodesToRemove.get(i));
        }


        repaintServerListTree();

    }







    /*
     *
     * Highlights a TableNode (selects the correct Tab, places the scrollpanel
     * in his position and selects the node)
     *
     * */


    public void higlightNode(Node node) {


        Enumeration allchildren = rootNode.breadthFirstEnumeration();
        allchildren.nextElement();



        while (allchildren.hasMoreElements()) {
            ((Node) allchildren.nextElement()).highLight=false;
        }

        node.highLight=true;
        serverListTree.scrollPathToVisible(new TreePath(node.getPath()));


    }




    /*
     * Adds a new spectrum to an existing or non-existing table
     *
     * */

    public void addSpectrumToTable(String serverTitle, Spectrum spectrum) {


        boolean found_server = false;

        for(int i=0; i < rootNode.getChildCount(); i++) {

            Node tmpNode = (Node) rootNode.getChildAt(i);
            if(tmpNode.toString().equals(serverTitle)) {
                tmpNode.add(spectrum.getNode());
                found_server=true;
                break;

            }
        }


        //if not found that server create it

        if (!found_server) {
            Node serverNode = new Node(serverTitle);

            serverNode.add(spectrum.getNode());

            rootNode.add(serverNode);

        }


        repaintServerListTree();

    }



    /*
     *
     * Creates a new table with this title and spectrum set
     *
     * */


    public void createTableWithSpectrumSet(String serverTitle, SpectrumSet newSet) {


        //Remove older table

        for(int i=0; i < rootNode.getChildCount(); i++) {
            Node tmpNode = (Node) rootNode.getChildAt(i);

            if(tmpNode.toString().equals(serverTitle)) {

                tmpNode.removeAllChildren();
                rootNode.remove(tmpNode);
            }
        }


        //if the spectrum set is empty, return without adding anything
        
        if(newSet.spectrumSet.size()==0){
            repaintServerListTree();
            return;
        }


        //add new table

        Node serverNode = new Node(serverTitle);

        for (int i=0;i<newSet.getSpectrumSet().size();i++) {

            Spectrum spectrum = newSet.getSpectrum(i);

            serverNode.add(spectrum.getNode());

        }

        rootNode.add(serverNode);
        repaintServerListTree();
        
    }






    /*
     * Creates a new table with the results of an SSA query
     *
     * */


    public void addSSATable(String tablename, Vector nodesVector) {


        Node newNameNode = new Node(tablename);

        Node thisNode;
        for(int i = 0; i < nodesVector.size(); i++) {
            thisNode = (Node) nodesVector.elementAt(i);
            newNameNode.add(thisNode);
        }

        rootNode.add(newNameNode);    

        repaintServerListTree();


    }


    ///////// Repanint Server List Tree ////////
    private void repaintServerListTree(){
        ((DefaultTreeModel) serverListTree.getModel()).reload();
        this.setViewportView(serverListTree);
        serverListTree.repaint();
    }

    public synchronized void repaintServerListTree_NT() {
        ((DefaultTreeModel) serverListTree.getModel()).reload();
        this.setViewportView(serverListTree);
        serverListTree.repaint();
    }


    ///////// Expand Node ////////
   /* public void expandNode(Node node) {
        Runnable swingThreadSafe = new SwingThreadSafe("expandNode_NT", this, node);
        SwingUtilities.invokeLater(swingThreadSafe);
    }

    public synchronized void expandNode_NT(Node node) {
        TreeNode[] pathToRoot = node.getPath();
        TreePath path = new TreePath(pathToRoot);

        int row = serverListTree.getRowForPath(path);
        if(!serverListTree.isExpanded(row)) {
            serverListTree.expandRow(row);
            //jScrollPanel.setViewportView(serverListTree);
            //TableJToolBar.removeAll();
            //TableJToolBar.add(resourcespanelmanager);
            serverListTree.scrollPathToVisible(path);

            repaintServerListTree();
        }
    }*/


}




class TransferableTreeNode implements Transferable {

    public static DataFlavor SPECTRUM = new DataFlavor(Spectrum.class,
            "Spectrum");
    DataFlavor flavors[] = {SPECTRUM};
    Spectrum spectrum;

    public TransferableTreeNode(Spectrum s) {
        spectrum = s;
    }

    public synchronized DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (flavor.getRepresentationClass() == Spectrum.class);
    }

    public synchronized Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor)) {
            return (Object) spectrum;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}


















