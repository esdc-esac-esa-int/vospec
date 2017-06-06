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

import esavo.vospec.dataingestion.SSAIngestor;
import esavo.vospec.dataingestion.SsaServer;

/**
 *
 * @author jgonzale
 */
public class FormatMetadataThread extends Thread{

    VOSpecAdvancedSelector selector;
    SsaServer server;

    FormatMetadataThread(VOSpecAdvancedSelector selector, SsaServer server){
        this.selector=selector;
        this.server=server;
    }

    boolean done = false;

    public void ready(){
        selector.threadReady();
    }

    public void run() {

        
        (new Thread() {

            public void run() {
                try {
                    SSAIngestor.populateInputParameters(server);
                    done = true;
                    ready();
                } catch (Exception e) {
                    //do nothing (silent)
                }
                

            }
        }).start();

        try {

            sleep(5000);

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if(!done){
            selector.threadReady();
        }
        
        
    }



}
