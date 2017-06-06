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
 * <pre>
 * %full_filespec: LineSet.java,6:java:1 %
 * %derived_by: jsalgado %
 * %date_created: Fri Jul  7 16:56:23 2006 %
 *
 * </pre>
 *
 * @author
 * @version %version: 6 %
 */

public class LineSet implements Serializable {
    
    public Vector       fields;
    public Vector       lineVector;
    
    public Hashtable    allFieldProperties;
    
    
    public LineSet() {
        this.fields             = new Vector();
        this.lineVector         = new Vector();
        this.allFieldProperties = new Hashtable();
    }
    
    public void setFields(Vector fields) {
        this.fields = fields;
    }
    
    public Vector getFields() {
        return this.fields;
    }
    
    public void setAllFieldProperties(Hashtable allFieldProperties) {
        this.allFieldProperties = allFieldProperties;
    }
        
    public Hashtable getFieldProperties(String field) {
        return (Hashtable) allFieldProperties.get(field);
    }
    
    public String getPropertiesString(String field) {
        
        Hashtable   ht      = (Hashtable) allFieldProperties.get(field);
        Enumeration keys    = ht.keys();
        
        String thisKey      = "";
        String thisKeyValue = "";
        String resultString = "<HTML><TABLE>";
        
        while(keys.hasMoreElements()) {
        
            resultString =  resultString + "<TR>";
            thisKey         = (String) keys.nextElement();
            thisKeyValue    = (String) ht.get(thisKey);
             
            resultString    = resultString + "<TD><B>" + thisKey + "</B></TD><TD>" + thisKeyValue + "</TD>";  
            resultString =  resultString + "</TR>";
        }
        resultString =  resultString + "</TABLE></HTML>";
        
        return resultString;
    }
    
    public void addLine(Line line) {
        line.setFields(this.fields);
        this.lineVector.add(line);
    }
    
    public Line getLine(int i) {
        return (Line) this.lineVector.elementAt(i);
    }
    
    public int size() {
        return this.lineVector.size();
    }
}
