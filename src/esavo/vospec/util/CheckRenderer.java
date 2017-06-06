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
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.*;



/**
 *
 * @author  ibarbarisi
 */
public class CheckRenderer extends JPanel implements TreeCellRenderer {
        protected JCheckBox check;
        protected JLabel label;

        public CheckRenderer() {
            setLayout(null);
            check = new JCheckBox();
            label = new JLabel();
            label.setForeground(UIManager.getColor("Tree.textForeground"));
            label.setBackground(new Color(255,255,255));
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                boolean isSelected, boolean expanded,
                                boolean leaf, int row, boolean hasFocus) {
            String stringValue = tree.convertValueToText(value, isSelected, true, leaf, row, hasFocus);
            setEnabled(tree.isEnabled());
            check.setSelected(((Node)value).getIsSelected());
            check.setBackground(new Color(254,254,254));
            label.setFont(tree.getFont());
            label.setText(stringValue);
            label.setBackground(new Color(255,255,255));
        
            if (leaf) {
                add(check);
                add(label);
                check.setVisible(true); 
            } else if (expanded) {
                check.setVisible(false);
                add(label);
                //label.setIcon(UIManager.getIcon("Tree.openIcon"));
            } else {
                check.setVisible(false);    
                add(label);
                //label.setIcon(UIManager.getIcon("Tree.closedIcon"));
            }
            return this;
        }

        public Dimension getPreferredSize() {
            Dimension d_check = check.getPreferredSize();
            Dimension d_label = label.getPreferredSize();
            return new Dimension(d_check.width + d_label.width,
                            (d_check.height < d_label.height ?
                             d_label.height : d_check.height));
        }

        public void doLayout() {
            Dimension d_check = check.getPreferredSize();
            Dimension d_label = label.getPreferredSize();
            int y_check = 0;
            int y_label = 0;
            if (d_check.height < d_label.height) {
                y_check = (d_label.height - d_check.height)/2;
            } else {
                y_label = (d_check.height - d_label.height)/2;
            }
            check.setLocation(0,y_check);
            check.setBounds(0,y_check,d_check.width,d_check.height);
            label.setLocation(d_check.width,y_label);
            label.setBounds(d_check.width,y_label,d_label.width,d_label.height);
        }

        public void setBackground(Color color) {
            if (color instanceof ColorUIResource){
                color = null;
            }else{
                color = new Color(254,254,254);
            }
            super.setBackground(color);
        }

}