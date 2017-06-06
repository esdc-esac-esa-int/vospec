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
 * @author jgonzale
 */


public class PhotometryPoint {
    
    private String ID;
    private PhysicalQuantity wavelength;

    //Points shall have either magnitude or flux to be converted properly to an spectrum
    private PhysicalQuantity magnitude;
    private PhysicalQuantity flux;

    private PhotometryFilter filter;

    void PhotometryPoint(String ID, PhysicalQuantity wavelength, PhysicalQuantity magnitude, PhysicalQuantity flux, PhotometryFilter filter){
        this.ID=ID;
        this.setWavelength(wavelength);
        this.setMagnitude(magnitude);
        this.setFlux(flux);
        this.setFilter(filter);
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
     * @return the wavelength
     */
    public PhysicalQuantity getWavelength() {
        return wavelength;
    }

    /**
     * @param wavelength the wavelength to set
     */
    public void setWavelength(PhysicalQuantity wavelength) {
        this.wavelength = wavelength;
    }

    /**
     * @return the magnitude
     */
    public PhysicalQuantity getMagnitude() {
        return magnitude;
    }

    /**
     * @param magnitude the magnitude to set
     */
    public void setMagnitude(PhysicalQuantity magnitude) {
        this.magnitude = magnitude;
    }

    /**
     * @return the filter
     */
    public PhotometryFilter getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     */
    public void setFilter(PhotometryFilter filter) {
        this.filter = filter;
    }

    /**
     * @return the flux
     */
    public PhysicalQuantity getFlux() {
        return flux;
    }

    /**
     * @param flux the flux to set
     */
    public void setFlux(PhysicalQuantity flux) {
        this.flux = flux;
    }

}
