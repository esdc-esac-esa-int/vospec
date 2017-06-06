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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

/**
 *
 * @author ibarbarisi
 */
public class ParamsTree extends CheckRenderer {
        
    protected JLabel label;
    protected JLabel label2;

        public ParamsTree() {
            setLayout(null);
            label = new JLabel();
            label.setForeground(new Color(204,222,238));
            label.setBackground(new Color(255,255,255));
            label.setFont(new Font("Verdana",Font.PLAIN,11));
            
            label2 = new JLabel();
            label2.setForeground(new Color(204,222,238));
            label2.setBackground(new Color(255,255,255));
            label2.setFont(new Font("Verdana",Font.PLAIN,11));
            
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                boolean isSelected, boolean expanded,
                                boolean leaf, int row, boolean hasFocus) {
            String stringValue = tree.convertValueToText(value, isSelected, true, leaf, row, hasFocus);
            setEnabled(tree.isEnabled());
            label.setFont(new Font("Verdana",Font.PLAIN,11));
            label.setText(stringValue);
                       
            label.setBorder(null);
            
            label2.setText("");
            label2.setBorder(null);
            label2.setVisible(false);
	    
            Color highColor = new Color(255,255,255);
            Color gray  = new Color(204,222,238);
            Color white  = new Color(255,255,255);
	    
            label.setToolTipText(((Node) value).getToolTipTextParam());
            
            //foglia
            if (leaf && row!=0) {
                
                // READY --> gray plain 
                if((((Node)value).getReady())){
                   label.setFont(new Font("Verdana",Font.PLAIN,11));                   
                   highColor = new Color(61,60,60);
                   label.setForeground(highColor);
                   label.setBackground(white);                   
                   label.setBorder(null);
                   //label2.setText("");
                   label2.setVisible(true);
                   label2.setText(" "+((Node)value).getValueSelected());
                   label2.setForeground(gray);
                   
                }
                
                // SELECTED --> gray bold
                if((((Node)value).getIsSelected())){
                    label.setFont(new Font("Verdana",Font.BOLD,11));                    
                    highColor = new Color(61,60,60);
                    label.setForeground(highColor);
                    label.setBackground(white);                   
                    label.setBorder(null);                    
                    label2.setForeground(new Color(255,0,0));
                    label2.setBackground(white);
                    label2.setText(" "+((Node)value).getValueSelected());
                    label2.setVisible(true);
                }
                
                // IS VALUE SELECTED --> double label
                if((((Node)value).getIsValueSelected())){
                    label.setFont(new Font("Verdana",Font.BOLD,11));                   
                    highColor = new Color(61,60,60);
                    label.setForeground(highColor);
                    label.setBackground(white);
                    label.setBorder(null);                   
                    label2.setFont(new Font("Verdana",Font.PLAIN,11));
                    label2.setForeground(new Color(255,0,0));
                    label2.setBackground(white);
                    label2.setText(" "+((Node)value).getValueSelected());
                    label2.setVisible(true);
                }
                                
                // WAITING --> orange bold
                if((((Node)value).getWaiting())){
                   label.setFont(new Font("Verdana",Font.BOLD,11)); 
                   highColor = new Color(255,160,0);
                   label.setForeground(highColor);
                   label.setBackground(white);
 		   label.setBorder(null);
                   label2.setText("");
                   label2.setVisible(false);
                }
                
                // DOWNLOADED --> green bold
                if((((Node)value).getDownloaded())){
                   label.setFont(new Font("Verdana",Font.BOLD,11));                  
                   highColor = new Color(50,200,50);
                   label.setForeground(highColor);
                   label.setBackground(white);                   
                   label.setBorder(null);
                   label2.setText("");
                   label2.setVisible(false); 
                }
                
                // FAILED --> red bold
                if((((Node)value).getFailed())){
                   label.setFont(new Font("Verdana",Font.BOLD,11));                   
                   highColor = new Color(255,0,0);
                   label.setForeground(highColor);
                   label.setBackground(white);                   
                   label.setBorder(null);
                   label2.setText("");
                   label2.setVisible(false); 
                }
                
                add(label);
                add(label2);
                
            //radice    
            } else if(!leaf && row!=0){
 
                label.setFont(new Font("Verdana",Font.PLAIN,11));
                label.setForeground(new Color(61,60,60)); 
                label.setBackground(white);
                label.setBorder(null);
                
		add(label);

            //root    
            }else if(row==0){

                ImageIcon ic = new ImageIcon(getClass().getResource("/esavo/vospec/images/spectrumList.gif"));
                label.setFont(new Font("Verdana",Font.PLAIN,11));
                label.setForeground(new Color(61,60,60));
                label.setBackground(white);
                label.setBorder(null);
                
                add(label);
                
            }
            
            return this;
        }
        
        public String getToolTipText(MouseEvent e){
            return label.getToolTipText();  
        }
        
         public Dimension getPreferredSize() {
            Dimension d_check = label.getPreferredSize();
            Dimension d_label = label2.getPreferredSize();
            return new Dimension(d_check.width + d_label.width,
                            (d_check.height < d_label.height ?
                             d_label.height : d_check.height));
        }

        public void doLayout() {
            Dimension d_check = label.getPreferredSize();
            Dimension d_label = label2.getPreferredSize();
            int y_check = 0;
            int y_label = 0;
            if (d_check.height < d_label.height) {
                y_check = (d_label.height - d_check.height)/2;
            } else {
                y_label = (d_check.height - d_label.height)/2;
            }
            label.setLocation(0,y_check);
            label.setBounds(0,y_check,d_check.width,d_check.height);
            label2.setLocation(d_check.width,y_label);
            label2.setBounds(d_check.width,y_label,d_label.width,d_label.height);
        }
        
  
        public void setBackground(Color color) {
            if (color instanceof ColorUIResource)
            color = null;
            super.setBackground(color);
        }

}