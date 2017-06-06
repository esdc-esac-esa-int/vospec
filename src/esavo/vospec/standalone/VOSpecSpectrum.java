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

import esavo.vospec.main.*;
import esavo.vospec.util.*;
import java.applet.AppletContext;
import java.io.*;
import java.util.*;
import javax.swing.*;


/**
 *
 * @author  ibarbarisi
 */

public class VOSpecSpectrum extends JApplet {
    
    public VOSpecDetached aioSpecToolDetached = null;
    public String localFile;
    
    public Properties props;

    public String SERVERHOST;
    public String RMIPORT;
    public String SERVERNAME;
    public String SERVERPORT;
    
    public VOSpecSpectrum() {
        
    }
    
      
    public void init() {
        
        SERVERHOST  = getParameter("SERVERHOST");
        SERVERPORT  = getParameter("SERVERPORT");
        RMIPORT     = getParameter("RMIPORT");
        SERVERNAME  = getParameter("SERVERNAME");
        
        String type = getParameter("TYPE");
        System.out.println("Type "+type);
        
        try{
            localFile = getParameter("LOCALFILE");
            System.out.println("LOCALFILE "+localFile);
            
            setPropertiesAndOpenVOSpec();
        
        }catch(NullPointerException e){
            localFile="false";
        }
        
        
        if(!localFile.contains("http://")){
            localFile = "file://"+localFile;
        }
        
        File file = new File(localFile);
        
        aioSpecToolDetached.localDataDialog.addNewDirectly(localFile,file.getName());
        aioSpecToolDetached.show();
    }
    
    public void setPropertiesAndOpenVOSpec() {
        
        props = new Properties();
    	props.setProperty("SERVERHOST",SERVERHOST);
        props.setProperty("SERVERPORT",SERVERPORT);
    	props.setProperty("RMIPORT",RMIPORT);
    	props.setProperty("SERVERNAME",SERVERNAME);

    	Utils.rmiPropertiesDefinition(props);

     	try {
      		String thisTitle="AIO Spectra Search Tool\n";
		AppletContext parentAppletContext = null;
        aioSpecToolDetached = new VOSpecDetached(thisTitle,null,props);
               
	} catch (Exception e) {
		e.printStackTrace();
	}	
    }
    
}
