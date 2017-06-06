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
import esavo.vospec.samp.TableSenderListener;
import esavo.vospec.spectrum.Spectrum;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.ListSelectionModel;
import org.astrogrid.samp.Message;
import org.jdesktop.swingx.JXTable;





public class SpectraTableMouseAdapter extends MouseAdapter {

    VOSpecDetached aiospectooldetached;
    TablesPanel tablespanel;


    public SpectraTableMouseAdapter(VOSpecDetached aiospectooldetached, TablesPanel tablespanel) {
        this.aiospectooldetached = aiospectooldetached;
        this.tablespanel=tablespanel;
    }


    //For multi-platform portability, the PopupTrigger has to be coded
    //both at mousePressed (linux, mac) and mouseReleased (windows)

    @Override
    public void mouseReleased(MouseEvent evt) {
        if (evt.isPopupTrigger()) {


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


            showPopup(evt);



        }


    }


    @Override
    public void mousePressed(MouseEvent evt) {

        if (!evt.isPopupTrigger()) {

            if (evt.getClickCount() == 1) {


                JXTable table = (JXTable) evt.getComponent();
                Node node = ((SpectraTableModel) ((JXTable) evt.getComponent()).getModel()).getNode(table.convertRowIndexToModel(table.getSelectedRow()));

                //AioSpecToolDetached aioSpecToolDetached = ((AioSpecToolDetached) (((JXTable) evt.getComponent()).getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent()));
                aiospectooldetached.highlightColor((Spectrum)node.getRelatedObject());
              
                Message sampMessage = new Message("table.highlight.row");
                sampMessage.addParam("name", tablespanel.getSelectedTableName());
                sampMessage.addParam("table-id", tablespanel.getSelectedTableId());
                sampMessage.addParam("url", tablespanel.getSelectedTableUrl());
                sampMessage.addParam("row", String.valueOf(table.convertRowIndexToModel(table.getSelectedRow())));
                try {
                    aiospectooldetached.interop.notifyAll(sampMessage);
                } catch (Exception ex) {
                    //ex.printStackTrace();
                }
                

            }
            if (evt.getClickCount() == 2) {

                JXTable table = (JXTable) evt.getComponent();
                Node node = ((SpectraTableModel) ((JXTable) evt.getComponent()).getModel()).getNode(table.convertRowIndexToModel(table.getSelectedRow()));

                VOSpecInfo info = new VOSpecInfo(node.getMetadata(), (Spectrum)node.getRelatedObject());
                info.setVisible(true);
            }


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


            showPopup(evt);


        }


    }

    private void showPopup(MouseEvent e) {

        Vector<Node> nodes = tablespanel.getHighlightedNodes();

        tablespanel.singleSpectrumSenderMenu.removeActionListeners();
        tablespanel.singleTableSenderMenu.removeActionListeners();
        tablespanel.tableSenderMenu.removeActionListeners();

        for (int i = 0; i < nodes.size(); i++) {
            tablespanel.singleSpectrumSenderMenu.addActionListener((new SingleSpectrumSenderListener("spectrum.load.ssa-generic", aiospectooldetached.interop, (Spectrum)nodes.elementAt(i).getRelatedObject())));
            tablespanel.singleTableSenderMenu.addActionListener((new SingleTableSenderListener("table.load.votable", aiospectooldetached.interop, (Spectrum)nodes.elementAt(i).getRelatedObject())));
            tablespanel.tableSenderMenu.addActionListener((new TableSenderListener("table.load.votable", aiospectooldetached.interop, tablespanel.getSelectedTableUrl(), tablespanel.getSelectedTableId(), tablespanel.getSelectedTableName())));

        }

        tablespanel.popup.show(e.getComponent(), e.getX(), e.getY());

    }





}



