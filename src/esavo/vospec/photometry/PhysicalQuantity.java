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
package esavo.vospec.photometry;


/**
 *
 * @author ibarbarisi
 */

public class PhysicalQuantity {
    private Number value = null;
    private String unit = null;
    private String ucd = "";

    public PhysicalQuantity (){

    }

    public PhysicalQuantity (Number value, String unit, String ucd){
        this.value = value;
        this.unit = unit;
        this.ucd = ucd;
    }

    public void setValue(Number value){
        this.value = value;
    }

    public Number getValue(){
        return this.value;
    }

    public void setUnit(String unit){
        this.unit = unit;
    }

    public String getUnit(){
        return this.unit;
    }

    public void setUcd(String ucd){
        this.ucd = ucd;
    }

    public String getUcd(){
        return this.ucd;
    }

}
