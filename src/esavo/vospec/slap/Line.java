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
 * %full_filespec: Line.java,3:java:1 %
 * %derived_by: jsalgado %
 * %date_created: Tue Jul  4 11:16:18 2006 %
 *
 * </pre>
 *
 * @author
 * @version %version: 3 %
 */

public class Line implements Serializable {
    
    public Vector       fields;
    public Hashtable    metadata;
    
    
    public Line() {
        this.fields   = new Vector();
        this.metadata = new Hashtable();
    }
    
    public Line(Line line) {
        this();
        this.metadata   = line.getMetadata();
    }
    
    public void setFields(Vector fields) {
        this.fields = fields;
    }
    
    public Vector getFields() {
        return this.fields;
    }
    
    
    public void setMetadata(Hashtable metadata) {
        this.metadata = metadata;
    }
    
    public Hashtable getMetadata() {
        return this.metadata;
    }
    
    
    public String getValue(String key) {
        if(this.metadata.get(key) == null) return "n/a";
        return (String) this.metadata.get(key);
    }
    
    public void setValue(String key, String value) {
        this.metadata.put(key,value);
    }
    
    public String toString() {
        String returnString = this.getClass().getName();
        
        returnString = returnString + "[";
        returnString = returnString + getString();
        returnString = returnString + "]";
        
        return returnString;
    }

    public String getString() {
        
        String returnString = "";
        
        for(int i=0; i < this.fields.size(); i++) {
            returnString = returnString + getValue((String) this.fields.elementAt(i)) + "\n";
        }
        
        return returnString;
    }
    
}
