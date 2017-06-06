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
 * @author ibarbarisi
 * @version %version: 5 %
 */
public class SsaServer implements Serializable, Comparable {

    public String ssaUrl = "";
    public String ssaName = "";
    public boolean selected = false;
    public boolean local = false;
    private String generalDesc;

    // Input parameters for each service
    public Vector inputParams = new Vector();
    public Hashtable params = new Hashtable();
    public boolean toWait;
    public static int SSAP = 0;
    public static int PSAP = 1;
    public static int TSAP = 2;
    public static int CONE_SEARCH = 3;
    public static int SSA_PHOTO = 4;
    private int type = 0;

    public SsaServer() {
    }

    public SsaServer(SsaServer ssaServer) {

        this();

        setSsaUrl(ssaServer.ssaUrl);
        setSsaName(ssaServer.ssaName);
        setSelected(ssaServer.selected);
        setLocal(ssaServer.local);
        //setTsa(ssaServer.tsa);
        setType(ssaServer.getType());
        setInputParams(ssaServer.inputParams);
        setParams(ssaServer.params);
    }

    // Get Methods
    public String getSsaUrl() {
        return ssaUrl;
    }

    public String getSsaName() {
        return ssaName;
    }

    public boolean getSelected() {
        return selected;
    }

    public boolean getLocal() {
        return local;
    }

    public Vector getInputParams() {
        return inputParams;
    }

    public void removeParam(String paramToRemove) {
        this.params.remove(paramToRemove);
    }

    public Hashtable getParams() {
        return params;
    }

    public void addParam(String paramName, String paramValue) {
        this.params.put(paramName, paramValue);
    }

    //Set Methods
    public void setParams(Hashtable params) {
        this.params = params;
    }

    public void setSsaUrl(String ssaUrl) {
        this.ssaUrl = ssaUrl;
    }

    public void setSsaName(String ssaName) {
        this.ssaName = ssaName;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public void setInputParams(Vector inputParams) {
        this.inputParams = inputParams;
    }

    public void resetParams() {
        params = new Hashtable();
    }

    public boolean getToWait() {
        return toWait;
    }

    public void setToWait(boolean toWait) {
        this.toWait = toWait;
    }

    public int compareTo(Object o) {
        return this.getSsaName().compareTo((((SsaServer) o).getSsaName()));
    }

    /**
     * @return the generalDesc
     */
    public String getGeneralDesc() {
        return generalDesc;
    }

    /**
     * @param generalDesc the generalDesc to set
     */
    public void setGeneralDesc(String generalDesc) {
        this.generalDesc = generalDesc;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }


}