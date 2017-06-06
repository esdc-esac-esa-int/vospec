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

import esavo.vospec.resourcepanel.*;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.swing.tree.TreePath;

/**
 *
 * @author jgonzale
 */
public class PhotometryFilterTreeMouseAdapter extends MouseAdapter {

    TreePanel treepanel;

    public PhotometryFilterTreeMouseAdapter(TreePanel treepanel) {
        this.treepanel = treepanel;
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



                //Select it if checkbox is clicked

                TreePath path = treepanel.serverListTree.getPathForLocation(evt.getX(), evt.getY());


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





        } else {

            //nothing

        }
    }




}
