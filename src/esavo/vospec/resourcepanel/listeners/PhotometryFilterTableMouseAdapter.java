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

import esavo.vospec.resourcepanel.TablesPanel;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ListSelectionModel;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author jgonzale
 */
public class PhotometryFilterTableMouseAdapter extends MouseAdapter {

    TablesPanel tablespanel;


    public PhotometryFilterTableMouseAdapter(TablesPanel tablespanel) {

        this.tablespanel=tablespanel;
    }

    @Override
    public void mousePressed(MouseEvent evt) {

        if (!evt.isPopupTrigger()) {

            //nothing

        } else {

            //select row under the right click
            JXTable table = (JXTable) evt.getComponent();

            Point p = evt.getPoint();
            int rowNumber = table.rowAtPoint(p);
            ListSelectionModel model = table.getSelectionModel();

            if(!model.isSelectedIndex(rowNumber)){
                if(evt.isControlDown()){
                    model.addSelectionInterval(rowNumber, rowNumber);
                }else{
                    model.setSelectionInterval(rowNumber, rowNumber);
                }
            }




        }


    }



}
