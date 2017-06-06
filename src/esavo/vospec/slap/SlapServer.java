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

import java.io.*;
/**
 *
 * 
 *
 * <pre>
 * %full_filespec: SlapServer.java,1:java:1 %
 * %derived_by: ibarbarisi %
 * %date_created: Tue Aug  2 11:46:49 2005 %
 * 
 * </pre>
 *
 * @author
 * @version %version: 1 %
 */

public class SlapServer implements Serializable{
    
    public  String 	slapUrl 	= "";
    public  String 	slapName 	= "";
    public  boolean     selected        = false;
    public  int         row;
    
    public SlapServer() {	
    }
    
    public SlapServer(SlapServer slapServer) {
        
        this();
        setSlapUrl(slapServer.slapUrl);
        setSlapName(slapServer.slapName);
        setSelected(slapServer.selected);
 
    }
    
    public String getSlapUrl() {
        return slapUrl;
    }
    
    public String getSlapName() {
        return slapName;
    }
    
     public boolean getSelected() {
	return selected;
    }
    
    public void setSlapUrl(String slapUrl) {
        this.slapUrl = slapUrl;
    }

    public void setSlapName(String slapName){
        this.slapName = slapName;
    }
     
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
 
}
