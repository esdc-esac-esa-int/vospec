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

import java.io.Serializable;

/**
 *
 * 
 *
 * <pre>
 * %full_filespec: SsaRequest.java,2:java:2 %
 * %derived_by: ibarbarisi %
 * %date_created: Thu Oct 18 10:41:57 2007 %
 * 
 * </pre>
 *
 * @author ibarbarisi
 * @version %version: 2 %
 */
public class SsaRequest implements Serializable {

    private String dec;
    private String ra;
    private String target;
    private String size;
    private String pos;

    public SsaRequest() {
    }

    public void setDec(String dec) {
        this.dec = dec;
    }

    public String getDec() {
        return dec;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    public String getRa() {
        return ra;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getPos() {
        return pos;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTarget() {
        return target;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSize() {
        return size;
    }
} // end class
