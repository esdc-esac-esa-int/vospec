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
package esavo.vospec.resourcepanel.listeners;

import esavo.vospec.main.VOSpecInfo;
import esavo.vospec.main.VOSpecDetached;
import esavo.vospec.resourcepanel.*;
import esavo.vospec.samp.SingleSpectrumSenderListener;
import esavo.vospec.samp.SingleTableSenderListener;
import esavo.vospec.spectrum.Spectrum;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.tree.TreePath;




public class SpectraTreeMouseAdapter extends MouseAdapter {

    VOSpecDetached aiospectooldetached;
    TreePanel treepanel;

    public SpectraTreeMouseAdapter(VOSpecDetached aiospectooldetached, TreePanel treepanel) {
        this.treepanel = treepanel;
        this.aiospectooldetached = aiospectooldetached;
    }

    //For multi-platform portability, the PopupTrigger has to be coded
    //both at mousePressed (linux, mac) and mouseReleased (windows)


    @Override
    public void mouseReleased(MouseEvent evt) {
        if(evt.isPopupTrigger()){
            
            //select node under the right click
            TreePath path = treepanel.serverListTree.getPathForLocation(evt.getX(), evt.getY());

            if (path != null) {

                //Leaf (spectrum)

                if (((Node) path.getLastPathComponent()).isLeaf()) {

                    if (!treepanel.serverListTree.isPathSelected(path)) {
                        if (evt.isControlDown()) {
                            treepanel.serverListTree.addSelectionPath(path);
                        } else {
                            treepanel.serverListTree.setSelectionPath(path);
                        }
                    }

                    showSpectrumPopup(evt);


                //Server

                } else if (path.getPathCount() == 2) {

                    treepanel.serverListTree.setSelectionPath(path);

                    showServerPopup(evt);

                }


            }
        }
    }

    @Override
    public void mousePressed(MouseEvent evt) {

        if (!evt.isPopupTrigger()) {

            if (evt.getClickCount() == 1) {


                // we use mousePressed instead of mouseClicked for performance
                int x = evt.getX();
                int y = evt.getY();
                int row = treepanel.serverListTree.getRowForLocation(x, y);
                if (row == -1) {
                    // click outside any node
                    return;
                }
                Rectangle rect = treepanel.serverListTree.getRowBounds(row);
                if (rect == null) {
                    // clic on an invalid node
                    return;
                }


                //Highlight related display color

                TreePath path = treepanel.serverListTree.getPathForLocation(evt.getX(), evt.getY());

                if (path != null) {
                    Node node = (Node) path.getLastPathComponent();
                    if (node != null && node.isLeaf()) {
                        //AioSpecToolDetached aioSpecToolDetached = ((AioSpecToolDetached) (((ExtendedJTree) evt.getComponent()).getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent()));
                        if ((Spectrum)node.getRelatedObject() != null) {
                            aiospectooldetached.highlightColor((Spectrum)node.getRelatedObject());
                        }
                    }
                }


                //Select it if checkbox is clicked

                if (((SpectraTreeCellRenderer) treepanel.serverListTree.getCellRenderer()).isOnHotspot(x - rect.x, y - rect.y)) {
                    if (path != null) {

                        //select the node itself
                        Node node = (Node) path.getLastPathComponent();
                        node.setIsSelected(!node.getIsSelected());

                        //select all its leaf children
                        Enumeration allchildren = node.breadthFirstEnumeration();

                        allchildren.nextElement();

                        while (allchildren.hasMoreElements()) {
                            ((Node) allchildren.nextElement()).setIsSelected(node.getIsSelected());
                        }

                        treepanel.serverListTree.repaint();

                    }

                }


            }



            if (evt.getClickCount() == 2) {
                TreePath path = treepanel.serverListTree.getPathForLocation(evt.getX(), evt.getY());
                if (path != null) {
                    Node node = (Node) path.getLastPathComponent();
                    if (node != null) {
                        if (node.getLevel() == 0) {
                            //do nothing, is root node
                        } else if (node.getLevel() == 1) {
                            TreeRebuilder rebuilder = new TreeRebuilder(node, treepanel.serverListTree);
                            rebuilder.setVisible(true);
                            rebuilder.toFront();
                        } else {
                            VOSpecInfo info = new VOSpecInfo(node.getMetadata(), (Spectrum)node.getRelatedObject());
                            info.setVisible(true);
                            info.toFront();
                        }
                    }
                }

            }

        } else {


            //select node under the right click
            TreePath path = treepanel.serverListTree.getPathForLocation(evt.getX(), evt.getY());

            if (path != null) {

                //Leaf (spectrum)

                if (((Node) path.getLastPathComponent()).isLeaf()) {

                    if (!treepanel.serverListTree.isPathSelected(path)) {
                        if (evt.isControlDown()) {
                            treepanel.serverListTree.addSelectionPath(path);
                        } else {
                            treepanel.serverListTree.setSelectionPath(path);
                        }
                    }

                    showSpectrumPopup(evt);


                //Server

                } else if (path.getPathCount() == 2) {

                    treepanel.serverListTree.setSelectionPath(path);

                    showServerPopup(evt);

                }




            }


        }
    }

    private void showSpectrumPopup(MouseEvent evt) {


        Vector <Node> nodes = new Vector();

        TreePath path = treepanel.serverListTree.getPathForLocation(evt.getX(), evt.getY());

        if (path != null) {

            TreePath [] paths = treepanel.serverListTree.getSelectionPaths();

            for(int i=0;i<paths.length;i++){
                nodes.add((Node) paths[i].getLastPathComponent());
            }


        }

        treepanel.singleSpectrumSenderMenu.removeActionListeners();
        treepanel.singleTableSenderMenu.removeActionListeners();

        for (int i = 0; i < nodes.size(); i++) {
            treepanel.singleSpectrumSenderMenu.addActionListener((new SingleSpectrumSenderListener("spectrum.load.ssa-generic", treepanel.interop, (Spectrum)nodes.elementAt(i).getRelatedObject())));
            treepanel.singleTableSenderMenu.addActionListener((new SingleTableSenderListener("table.load.votable", treepanel.interop, (Spectrum)nodes.elementAt(i).getRelatedObject())));
        }

        treepanel.spectrumPopup.show(evt.getComponent(), evt.getX(), evt.getY());


    }


    private void showServerPopup(MouseEvent evt) {

        treepanel.serverPopup.show(evt.getComponent(), evt.getX(), evt.getY());


    }



}