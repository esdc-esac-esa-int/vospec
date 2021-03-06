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

import javax.swing.*;

public class ExtendedJTable extends JTable
{
	private boolean setEditable 	= false;

	public ExtendedJTable() {
        	super();
        }
                
	
	public boolean isCellEditable(int row, int column) {
		if(setEditable){
			return true;
		} else {	
			return false;
		}
	}
	
	public void setEditable(boolean setEditable) {
		this.setEditable=setEditable;
	}
	
}
