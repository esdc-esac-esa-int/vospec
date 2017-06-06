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
package esavo.vospec.main;

import java.awt.Color;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author jgonzale
 */
public class ColorCellRenderer extends DefaultTableCellRenderer {

    public static int WHITE = 0;
    public static int GREEN = 1;
    public static int YELLOW = 2;
    public static int RED = 3;


    public void setColor(int color){

        if(color==WHITE) this.setBackground(Color.WHITE);
        if(color==GREEN) this.setBackground(new Color(152,251,152));
        if(color==YELLOW) this.setBackground(Color.yellow);
        if(color==RED) this.setBackground(new Color(205,92,92));

    }

    public void isSelected(boolean selected){
        if(selected){
            this.setForeground(new Color(0,0,139));
            //this.setFont(this.getFont().deriveFont(Font.ITALIC));
            //this.setBackground(Color.BLUE);//setFont(new Font("Helvetica",Font.BOLD,13));
        }else{
            //this.setFont(new Font("Helvetica",Font.PLAIN,12));
            this.setForeground(Color.BLACK);
        }

    }



}
