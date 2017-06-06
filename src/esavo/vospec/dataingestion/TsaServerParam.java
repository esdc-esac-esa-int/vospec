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
import java.util.Vector;

/**
 *
 * @author ibarbarisi
 * @version %version: 4 %
 */

public class TsaServerParam implements Serializable {
    
    private String      name;
    private String      ucd;
    private String      utype;
    private String      unit;
    private Vector      values;
    private String      description;
    private String      selectedValue;
    private boolean     isCombo;
    
    private String      minString = "";
    private String      maxString = "";
    
    public TsaServerParam() {
        this.name           = "";
        this.ucd            = "";
        this.utype          = "";
        this.unit           = "";
        this.values         = new Vector();
        this.description    = "";
        this.selectedValue  = null;
        this.isCombo        = true;
        this.minString      = "";
        this.maxString      = "";
    }

    public TsaServerParam(TsaServerParam param){
        this.name           = param.getName();
        this.ucd            = param.getUcd();
        this.utype          = param.getUtype();
        this.unit           = param.getUnit();
        this.values         = param.getValues();
        this.description    = param.getDescription();
        this.selectedValue  = param.getSelectedValue();
        this.isCombo        = param.getIsCombo();
        this.minString      = param.getMinString();
        this.maxString      = param.getMaxString();
    }
    
    //Getter Methods
    public String getName() {
        return name;
    }
    public String getUcd() {
        return ucd;
    }
    public Vector getValues() {
        return values;
    }
    public String getDescription() {
        String descriptionResult = description;
        if(!minString.equals("")) descriptionResult = descriptionResult + " Value should be greater than " + minString;
        if(!maxString.equals("")) descriptionResult = descriptionResult + " Value should be minor than " + maxString;
        
        return descriptionResult;
    }
    
    public String getSelectedValue() {
        return selectedValue;
    }
    
    public boolean getIsCombo() {
        return isCombo;
    }
    
    public String getMinString() {
        return minString;
    }
    
    public String getMaxString() {
        return maxString;
    }
    
    // Setter Methods
    public void setName(String name) {
        this.name = name;
    }
    public void setUcd(String ucd) {
        this.ucd = ucd;
    }
    public void setValues(Vector values) {
        this.values = values;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
    }
    public void setIsCombo(boolean isCombo) {
        this.isCombo = isCombo;
    }
    public void setMinString(String minString) {
        this.minString = minString;
    }
    public void setMaxString(String maxString) {
        this.maxString = maxString;
    }

    /**
     * @return the utype
     */
    public String getUtype() {
        return utype;
    }

    /**
     * @param utype the utype to set
     */
    public void setUtype(String utype) {
        this.utype = utype;
    }

    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }
}
