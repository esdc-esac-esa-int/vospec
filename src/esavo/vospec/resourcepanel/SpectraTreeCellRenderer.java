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

import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author  ibarbarisi
 */
public class SpectraTreeCellRenderer extends JPanel implements TreeCellRenderer {

    public JCheckBox check;
    public boolean call = true;
    protected JLabel label;

    protected boolean isSelected;
    protected boolean hasFocus;

    public SpectraTreeCellRenderer() {

        setLayout(null);
        check = new JCheckBox();
        check.setEnabled(true);
        check.setBackground(new Color(255, 255, 255));

        check.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                check.setSelected(!check.isSelected());

            }
        });

        label = new JLabel();
        label.setFont(new Font("Verdana", Font.PLAIN, 10));
        label.setForeground(new Color(61, 60, 60));
        label.setBackground(new Color(255, 255, 255));

    //label.setBorder(null);
    }

    public JCheckBox getCheckboxRenderer() {
        return check;
    }

    public boolean isOnHotspot(int x, int y) {
	// TODO: alternativa (ma funge???)
	// return this.checkBox.contains(x, y);
	return (this.check.getBounds().contains(x, y));
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean isSelected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {

        String stringValue = tree.convertValueToText(value, isSelected, true, leaf, row, hasFocus);

        setEnabled(tree.isEnabled());

        Color highColor = new Color(255, 255, 255);

        label.setText(stringValue);
        label.setBorder(null);
        label.setToolTipText(((Node) value).getToolTipText());
        label.setBackground(new Color(255, 255, 255));

        this.isSelected = isSelected;
        this.hasFocus = hasFocus;



        //foglia
        if (leaf && row != 0) {

            check.setIcon((new JCheckBox()).getIcon());
            check.setSelected(((Node) value).getIsSelected());
            check.setVisible(true);

            //Set background according to selection
            if(isSelected){
                label.setBackground(Color.LIGHT_GRAY);
                label.setOpaque(true);
            }else{
                //label.setBackground(new Color(255, 255, 255));
                //label.setText("focus");
            }

            //Possible retrieval status

            // READY --> gray plain
            if ((((Node) value).getReady())) {
                label.setFont(new Font("Verdana", Font.PLAIN, 10));
                highColor = new Color(61, 60, 60);
                label.setForeground(highColor);
                label.setBorder(null);
                check.setSelected(false);
            }
            // SELECTED --> gray bold
            if ((((Node) value).getIsSelected())) {
                label.setFont(new Font("Verdana", Font.BOLD, 10));
                highColor = new Color(61, 60, 60);
                label.setForeground(highColor);
                label.setBorder(null);
                check.setSelected(true);
            }

            // WAITING --> orange bold
            if ((((Node) value).getWaiting())) {
                label.setFont(new Font("Verdana", Font.BOLD, 10));
                highColor = new Color(255, 160, 0);
                label.setForeground(highColor);
                label.setBorder(null);
            }

            // DOWNLOADED --> green bold
            if ((((Node) value).getDownloaded())) {
                label.setFont(new Font("Verdana", Font.BOLD, 10));
                highColor = new Color(50, 200, 50);
                label.setForeground(highColor);
                label.setBorder(null);
                check.setSelected(((Node) value).getIsSelected());
            }

            // FAILED --> red bold
            if ((((Node) value).getFailed())) {
                label.setFont(new Font("Verdana", Font.BOLD, 10));
                highColor = new Color(255, 0, 0);
                label.setForeground(highColor);
                label.setBorder(null);
                check.setSelected(((Node) value).getIsSelected());
            }

            if ((((Node) value).getHighLight())) {
                label.setBorder(new javax.swing.border.EtchedBorder(javax.swing.border.EtchedBorder.RAISED, highColor, null));
            }

            add(check);
            add(label);

        //radice
        } else if (!leaf && row != 0) {

            boolean checked = true;
            
            //Set selected if all the leaves are selected
            Enumeration allchildren = ((Node) value).breadthFirstEnumeration();

            allchildren.nextElement();

            while (allchildren.hasMoreElements()) {
                Node child = (Node) allchildren.nextElement();
                if((child).isLeaf()){
                    if (!child.getIsSelected()) {
                        checked = false;
                    }
                }
            }

            
            ((Node) value).setIsSelected(checked);
            
            check.setSelected(checked);
            check.setIcon((new JCheckBox()).getIcon());
            check.setVisible(true);
            label.setFont(new Font("Verdana", Font.PLAIN, 10));
            label.setForeground(new Color(61, 60, 60));
            label.setBackground(new Color(255, 255, 255));
            label.setBorder(null);


            // COMPATIBLE --> green bold
            if ((((Node) value).getDownloaded())) {
                label.setFont(new Font("Verdana", Font.BOLD, 10));
                highColor = new Color(50, 200, 50);
                label.setForeground(highColor);
                label.setBackground(new Color(255, 255, 255));
                label.setBorder(null);
            }

            add(check);
            add(label);

        //root
        } else if (row == 0) {
            String iconImage = null;
            try {
                iconImage = "/esavo/vospec/images/spectrumList.gif";
                ImageIcon ic = new ImageIcon(getClass().getResource(iconImage));
                check.setIcon(ic);
                check.setPreferredSize((new JCheckBox()).getPreferredSize());
                label.setFont(new Font("Verdana", Font.PLAIN, 10));
                label.setForeground(new Color(61, 60, 60));

                label.setBorder(null);
                check.setSelected(false);
                check.setVisible(true);

                add(label);
                add(check);
            } catch (Exception e) {
                //System.out.println("Ico Image " + iconImage);
                e.printStackTrace();
            }
        }

        return this;
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        return label.getToolTipText();
    }

    @Override
    public Dimension getPreferredSize() {
            Dimension d_check = check.getPreferredSize();
            Dimension d_label = label.getPreferredSize();
            //d_label.height=d_label.height*2;
            //d_label.width=d_label.width*2;

            return new Dimension((int) (d_check.width + Math.floor((d_label.width * 1.1) + 10)),
                            (d_check.height < d_label.height ?
                             d_label.height : d_check.height));
        }

    @Override
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

    @Override
    public void setBackground(Color color) {
        if (color instanceof ColorUIResource) {
            color = null;
        } else {
            color = new Color(254, 254, 254);
        }
        super.setBackground(color);
    }
}

