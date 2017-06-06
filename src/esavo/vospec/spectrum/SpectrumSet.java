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
package esavo.vospec.spectrum;

import esavo.vospec.util.*;
import java.io.*;
import java.util.*;


/**
 *
 * <pre>
 * %full_filespec: SpectrumSet.java,3:java:2 %
 * %derived_by: isa barbarisi %
 * %date_created: Thu Nov 17 10:22:18 2005 %
 * 
 * </pre>
 *
 * @author
 * @version %version: 3 %
 */

public class SpectrumSet implements Serializable {

    public Hashtable spectrumSet; 
    
    
    public SpectrumSet() {
    	//constructor
	spectrumSet = new Hashtable();
    }
    
    public void addSpectrum(int ct,Spectrum spectrum) {
	spectrum.setRow(ct);
    	spectrumSet.put(""+ct,spectrum);
    }
    /*
    public void removeSpectrum(int ct){
        spectrumSet.remove(""+ct);
        Vector values = new Vector(spectrumSet.values());
        for(int j = 0;j<values.size();j++) {
               spectrumSet.values().
        }
    }*/
    
    public Hashtable getSpectrumSet() {
    	return spectrumSet;
    }
        
    public Spectrum getSpectrum(int ct) {
	return (Spectrum) spectrumSet.get(""+ct);
    }
    
    public void addSpectrumSet(SpectrumSet spectrumSet) {
          int thisSize = this.spectrumSet.size();
          
          for(int j = 0;j<spectrumSet.getSpectrumSet().size();j++) {
                int position = thisSize + j;
          	Spectrum spectrum = spectrumSet.getSpectrum(j);
                addSpectrum(position, spectrum);
          }      
    }	
  
    
    public void setAllToWait(boolean waitBoolean) {
        for(int j = 0 ; j<spectrumSet.size() ; j++) {
            ((Spectrum) this.getSpectrum(j)).setToWait(waitBoolean);
        }
    }
      
     public ExtendedTableModel refreshTableModel(String description) {
      return null;
         // return Utils.refreshTableModel(this,description);
     }
}
