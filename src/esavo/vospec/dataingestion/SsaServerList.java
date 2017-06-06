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

import java.io.*;
import java.util.*;

/**
 *
 * 
 *
 * <pre>
 * %full_filespec: SsaServerList.java,2:java:2 %
 * %derived_by: ibarbarisi %
 * %date_created: Fri Apr 29 10:17:47 2005 %
 * 
 * </pre>
 *
 * @author ibarbarisi
 * @version %version: 2 %
 */
public class SsaServerList implements Serializable {

    public Hashtable ssaServerList;
    public boolean localFileSelected;
    public Vector localName;

    public SsaServerList() { //constructor
        ssaServerList = new Hashtable();
        localName = new Vector();
    }

    public void addSsaServer(int ct, SsaServer ssaServer) {
        ssaServerList.put("" + ct, ssaServer);
    }

    public Hashtable getSsaServerList() {
        return ssaServerList;
    }

    // getSsaServer() return a specified element in the Hashtable
    public SsaServer getSsaServer(int ct) {
        SsaServer ssaServer = (SsaServer) ssaServerList.get("" + ct);
        return ssaServer;
    }

    public void addSsaServerList(SsaServerList ssaServerList) {
        int thisSize = this.ssaServerList.size();

        for (int j = 0; j < ssaServerList.getSsaServerList().size(); j++) {
            int position = thisSize + j;
            SsaServer ssaServer = ssaServerList.getSsaServer(j);
            addSsaServer(position, ssaServer);
        }
    }
}
