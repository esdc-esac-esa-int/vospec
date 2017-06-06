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
import java.util.*;

/**
 *
 * 
 *
 * <pre>
 * %full_filespec: SlapServerList.java,1:java:1 %
 * %derived_by: ibarbarisi %
 * %date_created: Tue Aug  2 11:47:09 2005 %
 * 
 * </pre>
 *
 * @author
 * @version %version: 1 %
 */

public class SlapServerList implements Serializable{

	public Hashtable slapServerList;
        public boolean localFileSelected;
        public Vector localName;
	
	public SlapServerList() { //constructor
		slapServerList = new Hashtable();
                localName = new Vector();
	}
	
	public void addSlapServer(int ct,SlapServer slapServer) {		
		slapServerList.put(""+ct,slapServer);	
	}
 	
	public Hashtable getSlapServerList() {
		return slapServerList;
	}
	
	// getSlapServer() return a specified element in the Hashtable
	public SlapServer getSlapServer(int ct) {
		SlapServer slapServer = (SlapServer) slapServerList.get(""+ct);
		return slapServer;
	}
        
        public void addSlapServerList(SlapServerList slapServerList) {
              int thisSize = this.slapServerList.size();

              for(int j = 0;j<slapServerList.getSlapServerList().size();j++) {
                    int position = thisSize + j;
                    SlapServer slapServer = slapServerList.getSlapServer(j);
                    addSlapServer(position, slapServer);
              }      
        }	
            
        
}
