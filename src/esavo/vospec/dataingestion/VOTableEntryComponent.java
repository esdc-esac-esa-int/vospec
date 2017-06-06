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

/**
 *
 * @author jgonzale
 */
public class VOTableEntryComponent {


    private String name = new String();
    private String ID = new String();
    private String datatype = new String();
    private String arraysize = new String();
    private String width = new String();
    private String precision = new String();
    private String xtype = new String();
    private String unit = new String();
    private String ucd = new String();
    private String utype = new String();
    private String ref = new String();
    private String type = new String();

    private String value = new String();


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the ID
     */
    public String getID() {
        return ID;
    }

    /**
     * @param ID the ID to set
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * @return the datatype
     */
    public String getDatatype() {
        return datatype;
    }

    /**
     * @param datatype the datatype to set
     */
    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    /**
     * @return the arraysize
     */
    public String getArraysize() {
        return arraysize;
    }

    /**
     * @param arraysize the arraysize to set
     */
    public void setArraysize(String arraysize) {
        this.arraysize = arraysize;
    }

    /**
     * @return the width
     */
    public String getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * @return the precision
     */
    public String getPrecision() {
        return precision;
    }

    /**
     * @param precision the precision to set
     */
    public void setPrecision(String precision) {
        this.precision = precision;
    }

    /**
     * @return the xtype
     */
    public String getXtype() {
        return xtype;
    }

    /**
     * @param xtype the xtype to set
     */
    public void setXtype(String xtype) {
        this.xtype = xtype;
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

    /**
     * @return the ucd
     */
    public String getUcd() {
        return ucd;
    }

    /**
     * @param ucd the ucd to set
     */
    public void setUcd(String ucd) {
        this.ucd = ucd;
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
     * @return the ref
     */
    public String getRef() {
        return ref;
    }

    /**
     * @param ref the ref to set
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

}
