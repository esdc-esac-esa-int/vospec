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

import esavo.vospec.resourcepanel.Node;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;


/**
 *
 * @author ibarbarisi
 */
public class NodeSelectionListener extends MouseAdapter {

    JTree tree;

    public NodeSelectionListener(JTree tree) {
        this.tree = tree;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        int x = e.getX();
        int y = e.getY();
        int row = tree.getRowForLocation(x, y);
        TreePath path = tree.getPathForRow(row);
        if (path != null) {
            Node node = (Node) path.getLastPathComponent();
            boolean isSelected = !(node.getIsSelected());

            node.setIsSelected(isSelected);

            if (node.getSelectionMode() == Node.DIG_IN_SELECTION) {

                if (isSelected) {
                    tree.expandPath(path);

                } else {
                    tree.collapsePath(path);
                }
            }
            ((DefaultTreeModel) tree.getModel()).nodeChanged(node);
            if (row == 0) {
                tree.revalidate();
                tree.repaint();
            }
        }
         
    }

}
