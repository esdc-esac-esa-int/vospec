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

import esavo.vospec.main.VOSpecDetached;
import esavo.vospec.resourcepanel.listeners.PhotometryFilterTableMouseAdapter;
import esavo.vospec.resourcepanel.listeners.PhotometryFilterTreeMouseAdapter;
import esavo.vospec.resourcepanel.listeners.SpectraTableMouseAdapter;
import esavo.vospec.resourcepanel.listeners.SpectraTreeMouseAdapter;
import esavo.vospec.spectrum.*;
import java.util.Vector;
import javax.swing.JToolBar;


/**
 *
 * @author jgonzale
 */
public class ResourcesPanelManager{

    public static int SPECTRA = 0;
    public static int PHOTOMETRY = 1;

    TablesPanel tablespanel;
    TreePanel treepanel;


    public ResourcesPanelManager(JToolBar toolbar, VOSpecDetached aiospectooldetached){

        tablespanel = new TablesPanel();
        tablespanel.setListeners(new SpectraTableMouseAdapter(aiospectooldetached, tablespanel), aiospectooldetached.interop);
        tablespanel.setVisible(false);
        toolbar.add(tablespanel);

        treepanel = new TreePanel();
        treepanel.setListeners(new SpectraTreeMouseAdapter(aiospectooldetached, treepanel), aiospectooldetached.interop);
        treepanel.setVisible(true);
        toolbar.add(treepanel);

    }

    public ResourcesPanelManager(JToolBar toolbar, int type){

        if(type==ResourcesPanelManager.PHOTOMETRY){

            tablespanel = new TablesPanel();
            tablespanel.setListeners(new PhotometryFilterTableMouseAdapter(tablespanel), null);
            tablespanel.setVisible(false);
            toolbar.add(tablespanel);

            treepanel = new TreePanel();
            treepanel.setListeners(new PhotometryFilterTreeMouseAdapter(treepanel), null);
            treepanel.setVisible(true);
            toolbar.add(treepanel);

        }else{

        }



    }

    public void viewTable(){

        tablespanel.setVisible(true);
        treepanel.setVisible(false);

    }

    public void viewTree(){

        tablespanel.setVisible(false);
        treepanel.setVisible(true);

    }







    public void resetPanel(){
        tablespanel.resetPanel();
        treepanel.resetPanel();
    }


    public void uncheckAll(){
        tablespanel.uncheckAll();
        treepanel.repaint();
    }






    /*
     *
     * Returns all the selected nodes
     *
     * */


    public Vector getSelectedNodes() {

        return tablespanel.getSelectedNodes();

    }

    /*
     *
     * Returns all the non-selected tablenodes
     *
     * */


    public Vector getNonSelectedNodes() {

        return tablespanel.getNonSelectedNodes();

    }



    /*
     *
     * Highlights a Node in the table (selects the correct Tab, places the scrollpanel
     * in his position and selects the node), and highlights the tree node.
     *
     * */


    public void higlightNode(Node node) {

       tablespanel.higlightNode(node);
       treepanel.higlightNode(node);

    }

    public void higlightTablePos(String tableId, String url, int pos) {

        tablespanel.highlightTablePos(tableId, url, pos);


    }






    public void addSpectrumToTable(String nodeTitle, Spectrum spectrum) {

        tablespanel.addSpectrumToTable(nodeTitle, spectrum);
        treepanel.addSpectrumToTable(nodeTitle, spectrum);

    }




    /*
     * Generates TableNodes for the new set of spectrums
     *
     * @param newlocalset New Set of spectrums relative to Local Data, both new and old
     * */


    public SpectrumSet addLocal(SpectrumSet newlocalset) {

        SpectrumSet newLocalFiltered = tablespanel.addLocal(newlocalset);

        treepanel.createTableWithSpectrumSet("Local Data", newLocalFiltered);

        return newLocalFiltered;

    }






    //////////////////////////////////////////
    //////////////// SSA Operations //////////
    //////////////////////////////////////////

    /*
     *
     * Deletes Tabs and tables wich are not the Local Data one
     * (execute before adding the results of an SSA query)
     *
     * */

    public void deleteSSATables(){

        tablespanel.deleteSSATables();
        treepanel.deleteSSATables();

    }

    /*
     * Inserts a new JTable in the JTabbedPane (with the results of an SSA query)
     *
     * */


    public void addSSATable(String tablename, String tableId, Vector nodes, String url) {

        tablespanel.addSSATable(tablename, tableId, nodes, url);
        treepanel.addSSATable(tablename, nodes);

    }






}
