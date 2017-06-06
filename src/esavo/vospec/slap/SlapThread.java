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
package esavo.vospec.slap;

import esavo.vospec.dataingestion.SSAIngestor;

public class SlapThread implements Runnable {
	
	private static  SLAPViewer 	SLAPVIEWER;
	private         SlapServer      slapServer;
        
        public SlapThread(SLAPViewer SLAPVIEWER, SlapServer slapServer) {
            
            this.SLAPVIEWER	= SLAPVIEWER;
            this.slapServer	= slapServer;
        }    

	public void run() {

            String serverName   = slapServer.getSlapName();
            String serverURL    = slapServer.getSlapUrl();
            
            try {
               
                LineSet lineSet = SSAIngestor.getLines(serverURL);
                if(lineSet.size() == 0) {
                    SLAPVIEWER.errorsFromSLAPSearch("No result for serverName: " + serverName);
                }else{
                    SLAPVIEWER.showLineSet(slapServer.getSlapName(), lineSet);
                }
                
            } catch (Exception e) {
                SLAPVIEWER.errorsFromSLAPSearch("Problems with " + serverName + ":" + e.getMessage());
            }    
	}
}
