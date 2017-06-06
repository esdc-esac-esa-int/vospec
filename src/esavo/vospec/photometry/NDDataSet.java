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

import java.util.Vector;

/**
 *
 * @author ibarbarisi
 */
public class NDDataSet {

    private String unitsW;

    private Vector<Number> waveValues = new Vector();
    private Vector<Number> transmissionValues = new Vector();

    
    public void addPoint(Number wave, Number transmission){
        waveValues.add(wave);
        transmissionValues.add(transmission);
    }


    /**
     * @return the unitsW
     */
    public String getUnitsW() {
        return unitsW;
    }

    /**
     * @param unitsW the unitsW to set
     */
    public void setUnitsW(String unitsW) {
        this.unitsW = unitsW;
    }

    /**
     * @return the waveValues
     */
    public Vector<Number> getWaveValues() {
        return waveValues;
    }

    /**
     * @param waveValues the waveValues to set
     */
    public void setWaveValues(Vector<Number> waveValues) {
        this.waveValues = waveValues;
    }

    /**
     * @return the transmissionValues
     */
    public Vector<Number> getTransmissionValues() {
        return transmissionValues;
    }

    /**
     * @param transmissionValues the transmissionValues to set
     */
    public void setTransmissionValues(Vector<Number> transmissionValues) {
        this.transmissionValues = transmissionValues;
    }







}