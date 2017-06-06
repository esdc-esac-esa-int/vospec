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
package esavo.vospec.samp;

import esavo.vospec.dataingestion.SSAIngestor;
import esavo.vospec.dataingestion.VOTable;
import esavo.vospec.main.VOSpecDetached;
import esavo.vospec.spectrum.Spectrum;
import esavo.vospec.spectrum.SpectrumSet;
import java.util.Map;
import java.util.Vector;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.client.AbstractMessageHandler;
import org.astrogrid.samp.client.HubConnection;

public class VOSpecMessageReceiver extends AbstractMessageHandler {
	
    VOSpecDetached aiospectooldetached;

	public VOSpecMessageReceiver(String mType, VOSpecDetached aiospectooldetached) {
		super(mType);
        this.aiospectooldetached=aiospectooldetached;
	}
	
	public VOSpecMessageReceiver(String[] mTypes, VOSpecDetached aiospectooldetached) {
		super(mTypes);
        this.aiospectooldetached=aiospectooldetached;
	}

    public Map processCall(HubConnection connection, String senderId, Message message)
            throws Exception {

        System.out.println("Received a message from " + senderId);

        if (message.getMType().equals("spectrum.load.ssa-generic")) {
            System.out.println("mType: " + message.getMType());
            System.out.println("url: " + message.getParam("url"));
            System.out.println("table-id: " + message.getParam("table-id"));
            System.out.println("name: " + message.getParam("name"));



            try{

                Map metadata = (Map) message.getParam("meta");

                VOTable table = new VOTable(metadata);

                SpectrumSet set = SSAIngestor.getSpectra(table);

                Spectrum spectrum = set.getSpectrum(0);
                spectrum.setAioSpecToolDetached(aiospectooldetached);


                //this.aiospectooldetached.createNewSpectraViewer();
                //this.aiospectooldetached.addSpectrum("SAMP", spectrum, (javax.swing.JTextArea) null);
                this.aiospectooldetached.localDataDialog.addSpectrum(spectrum);
                this.aiospectooldetached.localDataDialog.addLocalData();


            }catch(Throwable e){

                aiospectooldetached.localDataDialog.addNewDirectly((String) message.getParam("url"), (String) message.getParam("name"));
            
            }


        }

        if (message.getMType().equals("table.load.votable")) {


            System.out.println("mType: " + message.getMType());
            System.out.println("url: " + message.getParam("url"));
            System.out.println("table-id: " + message.getParam("table-id"));
            System.out.println("name: " + message.getParam("name"));

            String url = (String) message.getParam("url");
            String tableId =(String) message.getParam("table-id");
            String name =(String) message.getParam("name");

            if(tableId==null || tableId.equals("")){
                tableId=url;
                if(name==null|| name.equals("")){
                    name=url;
                }
            }else{
                if(name==null|| name.equals("")){
                    name=tableId;
                }
            }

            SpectrumSet sv = SSAIngestor.getSpectra(url);


            if (sv.getSpectrumSet().size() != 0) {


                    Vector nodesVector = aiospectooldetached.addSpectraToNodes((String) message.getParam("name"), sv, false, true);
                    aiospectooldetached.addNodesToTable((String) message.getParam("name"), (String) message.getParam("table-id"), nodesVector, (String) message.getParam("url"));

           }


            //aiospectooldetached.localDataDialog.addNewDirectly((String) message.getParam("url"), (String) message.getParam("name"));
        }

        if (message.getMType().equals("table.load.fits")) {
            System.out.println("mType: " + message.getMType());
            System.out.println("url: " + message.getParam("url"));
            System.out.println("table-id: " + message.getParam("table-id"));
            System.out.println("name: " + message.getParam("name"));
            aiospectooldetached.localDataDialog.addNewDirectly((String) message.getParam("url"), (String) message.getParam("name"));
        }




        if (message.getMType().equals("table.highlight.row")) {


            System.out.println("mType: " + message.getMType());
            System.out.println("table-id: " + message.getParam("table-id"));
            System.out.println("url: " + message.getParam("url"));
            System.out.println("row: " + message.getParam("row"));

            aiospectooldetached.viewTable();

            aiospectooldetached.resourcespanelmanager.higlightTablePos((String) message.getParam("table-id"), (String) message.getParam("url"),
                    Integer.valueOf((String) message.getParam("row")));



        }


        // We do not want to return anything
        return null;
    }

}
