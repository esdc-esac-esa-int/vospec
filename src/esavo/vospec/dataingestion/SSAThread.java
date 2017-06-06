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
package esavo.vospec.dataingestion;

import esavo.vospec.main.VOSpecDetached;
import esavo.vospec.spectrum.*;
import java.util.*;

public class SSAThread extends Thread {

    private VOSpecDetached AIOSPECTOOLDETACHED;
    private SsaServer ssaServer;

    public SSAThread(VOSpecDetached aioSpecToolDetached, SsaServer ssaServer) {
        this.AIOSPECTOOLDETACHED = aioSpecToolDetached;
        this.ssaServer = ssaServer;
    }

    public void run() {

        String url = this.ssaServer.getSsaUrl();

        try {

            SpectrumSet sv = new SpectrumSet();

            if (ssaServer.getType() == SsaServer.SSAP) {
                url = url + "&REQUEST=queryData";
            } else if (ssaServer.getType() == SsaServer.CONE_SEARCH) {


                url = url.replace("&POS=", "-c=");
                url = url.replace(",", "%2B");
                url = url.replace("&SIZE=", "&-c.rs=");

                System.out.println("ConeSearch URL " + url);

            }

            if (ssaServer.getType() == SsaServer.CONE_SEARCH) {

                sv = ConeSearchIngestor.getSpectra(url);

            } else if(ssaServer.getType() == SsaServer.SSA_PHOTO) {
                
                sv = SsaPhotometryIngestor.getSpectra(url);

            }else{

                sv = SSAIngestor.getSpectra(url);

            }




            if (sv.getSpectrumSet().size() != 0) {

                System.out.print(ssaServer.getSsaName() + " returned " + sv.spectrumSet.size() + " results.");
                Vector nodesVector = AIOSPECTOOLDETACHED.addSpectraToNodes(ssaServer.getSsaName(), sv, ssaServer.getType()==SsaServer.TSAP, true);
                AIOSPECTOOLDETACHED.addNodesToTable(ssaServer.getSsaName(), ssaServer.getSsaName()+" "+url, nodesVector, url);
                //AIOSPECTOOLDETACHED.refreshJTree();
                AIOSPECTOOLDETACHED.successFromSSASearch(ssaServer.getSsaName() + " returned " + sv.spectrumSet.size() + " results.");

            } else {
                AIOSPECTOOLDETACHED.errorsFromSSASearch("No results for " + ssaServer.getSsaName());
            }
            AIOSPECTOOLDETACHED.ssaThreadReady(this);

        } catch (Exception e) {
            System.out.println("No results for " + ssaServer.getSsaName() + ":" + "Please check Server Response:");
            //System.out.println(url);
            //e.printStackTrace();
            AIOSPECTOOLDETACHED.errorsFromSSASearch("No results for " + ssaServer.getSsaName() + ":" + "Please check Server Response");
            AIOSPECTOOLDETACHED.ssaThreadReady(this);
        }
    }
}
