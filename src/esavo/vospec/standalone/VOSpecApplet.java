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
package esavo.vospec.standalone;

import esavo.vospec.dataingestion.RegistryIngester;
import esavo.vospec.dataingestion.SsaServer;
import esavo.vospec.dataingestion.SsaServerList;
import javax.swing.*;


/**
 *
 * @author  ibarbarisi
 */

public class VOSpecApplet extends JApplet {
    
    public VoSpec voSpec;
    public SsaServerList ssaServerList;
    public String localFile;
    
    public VOSpecApplet() {
        
    }
    
    @Override
    public void init() {
               
        String serverHost = getParameter("SERVERHOST");
        String serverPort = getParameter("SERVERPORT");
        String rmiPort = getParameter("RMIPORT");
        String serverName = getParameter("SERVERNAME");
        String type = getParameter("TYPES");
        
        String pos = getParameter("POS");
        String[] temp = pos.split(",");
        String ra = temp[0];
        String dec = temp[1];
        String size = getParameter("SIZE");
        
        String band = getParameter("BAND");
        String time = getParameter("TIME");
        
        String servers = getParameter("SSASERVERNAME");
        String url = getParameter("SSASERVERURL");
        
        String ct = getParameter("CT");
        try{
            localFile = getParameter("LOCALFILE");
        }catch(NullPointerException e){
            localFile="false";
        }
        
        voSpec = new VoSpec(serverHost,serverPort,rmiPort,serverName);
               
        voSpec.setRa(ra);
        voSpec.setDec(dec);
        voSpec.setSize(size);
        	       
        String[] ssaServers = servers.split("--");
        String[] ssaServersUrl = url.split("--");
        //String[] types = type.split("--");
        ssaServerList = new SsaServerList();	
        
        if (!ct.equals("0")){
            
            for(int i=0;i<ssaServersUrl.length;i++){
                SsaServer ssaServer = new SsaServer();

                if (ssaServersUrl[i].indexOf("http")<0){
                    ssaServer.setLocal(true);
                }else {
                    ssaServer.setLocal(false);
                }
                
                if (ssaServersUrl[i].indexOf("http")>-1 && localFile.equals("true")){
                    ssaServer.setLocal(true);
                    ssaServersUrl[i] = ssaServersUrl[i]+"&POS="+ra+","+dec+"&SIZE="+size+"&BAND="+band+"&TIME="+time;
                }

                int typeInt = 0;
                //if(types[i].equals("psap")){
                //    typeInt = 1;
                //}else{
                //    typeInt = 0;
                //}
                System.out.println("Server "+ssaServers[i]+" Type "+typeInt);
                ssaServer.setType(typeInt);
                ssaServer.setSsaName(ssaServers[i]);
                ssaServer.setSsaUrl(ssaServersUrl[i]);
                System.out.println("ssaServer.getSsaUrl() "+ssaServer.getSsaUrl());
                ssaServerList.addSsaServer(i,ssaServer);
            }
	          
            voSpec.setSsaServerList(ssaServerList);	
        }else{
            try {
                SsaServerList ssaList = new SsaServerList();
                
                ssaServerList = RegistryIngester.getSsaServerList();
                
                if (localFile.equals("true") && localFile.indexOf("http")>-1){
                
                    for (int i=0;i<ssaServerList.getSsaServerList().size();i++){ 
                        SsaServer ssaServ = ssaServerList.getSsaServer(i);
                        ssaServ.setLocal(true);
                        ssaServ.setSsaUrl(ssaServerList.getSsaServer(i).getSsaUrl()+"&POS="+ra+","+dec+"&SIZE="+size+"&BAND="+band+"&TIME="+time);
                        ssaList.addSsaServer(i,ssaServ);
                    }
                    
                }else{
                    ssaList = ssaServerList;
                }
                
                voSpec.setSsaServerList(ssaList);
                
            }catch (Exception e){
                System.out.println("Error getting SsaServerList ");
                e.printStackTrace();
            }
        }
        
        
        voSpec.show();

    }
    
}
