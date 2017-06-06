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
public class PhotometryFilter {
    private    String id = "";
    private String filterName = "";
    private PhysicalQuantity zeroPoint = new PhysicalQuantity();
    private PhysicalQuantity wavelengthMean = new PhysicalQuantity();
    private PhysicalQuantity wavelengthMin = new PhysicalQuantity();
    private PhysicalQuantity wavelengthMax = new PhysicalQuantity();
    private PhysicalQuantity widthEff = new PhysicalQuantity();
    private String instrument = "";
    private String facility = "";
    private String photSystem = "";
    private String description = "";
    private String filterTransmissionCurveUrl = "";
    private NDDataSet filterTransmissionCurve = new NDDataSet();

    void PhotometryFilter(PhotometryFilter filter){
        this.setId(filter.getId());
        this.setFilterName(filter.getFilterName());
        this.setZeroPoint(filter.getZeroPoint());
        this.setWavelengthMean(filter.getWavelengthMean());
        this.setWavelengthMin(filter.wavelengthMin);
        this.setWavelengthMax(filter.wavelengthMax);
        this.setInstrument(filter.instrument);
        this.setFacility(filter.facility);
        this.setPhotSystem(filter.photSystem);
        this.setWidthEff(filter.widthEff);
        this.setDescription(filter.description);
        this.setFilterTransmissionCurve(filter.filterTransmissionCurve);
        this.setFilterTransmissionCurveUrl(filter.filterTransmissionCurveUrl);

    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the filterName
     */
    public String getFilterName() {
        return filterName;
    }

    /**
     * @param filterName the filterName to set
     */
    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    /**
     * @return the zeroPoint
     */
    public PhysicalQuantity getZeroPoint() {
        return zeroPoint;
    }

    /**
     * @param zeroPoint the zeroPoint to set
     */
    public void setZeroPoint(PhysicalQuantity zeroPoint) {
        this.zeroPoint = zeroPoint;
    }

    /**
     * @return the wavelengthMean
     */
    public PhysicalQuantity getWavelengthMean() {
        return wavelengthMean;
    }

    /**
     * @param wavelengthMean the wavelengthMean to set
     */
    public void setWavelengthMean(PhysicalQuantity wavelengthMean) {
        this.wavelengthMean = wavelengthMean;
    }

    /**
     * @return the wavelengthMin
     */
    public PhysicalQuantity getWavelengthMin() {
        return wavelengthMin;
    }

    /**
     * @param wavelengthMin the wavelengthMin to set
     */
    public void setWavelengthMin(PhysicalQuantity wavelengthMin) {
        this.wavelengthMin = wavelengthMin;
    }

    /**
     * @return the wavelengthMax
     */
    public PhysicalQuantity getWavelengthMax() {
        return wavelengthMax;
    }

    /**
     * @param wavelengthMax the wavelengthMax to set
     */
    public void setWavelengthMax(PhysicalQuantity wavelengthMax) {
        this.wavelengthMax = wavelengthMax;
    }

    /**
     * @return the instrument
     */
    public String getInstrument() {
        return instrument;
    }

    /**
     * @param instrument the instrument to set
     */
    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    /**
     * @return the facility
     */
    public String getFacility() {
        return facility;
    }

    /**
     * @param facility the facility to set
     */
    public void setFacility(String facility) {
        this.facility = facility;
    }

    /**
     * @return the photSystem
     */
    public String getPhotSystem() {
        return photSystem;
    }

    /**
     * @param photSystem the photSystem to set
     */
    public void setPhotSystem(String photSystem) {
        this.photSystem = photSystem;
    }

    /**
     * @return the widthEff
     */
    public PhysicalQuantity getWidthEff() {
        return widthEff;
    }

    /**
     * @param widthEff the widthEff to set
     */
    public void setWidthEff(PhysicalQuantity widthEff) {
        this.widthEff = widthEff;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the filterTrasmissionCurve
     */
    public NDDataSet getFilterTransmissionCurve() {
        return filterTransmissionCurve;
    }

    /**
     * @param filterTrasmissionCurve the filterTrasmissionCurve to set
     */
    public void setFilterTransmissionCurve(NDDataSet filterTrasmissionCurve) {
        this.filterTransmissionCurve = filterTransmissionCurve;
    }

    /**
     * @return the filterTransmissionCurveUrl
     */
    public String getFilterTransmissionCurveUrl() {
        return filterTransmissionCurveUrl;
    }

    /**
     * @param filterTransmissionCurveUrl the filterTransmissionCurveUrl to set
     */
    public void setFilterTransmissionCurveUrl(String filterTransmissionCurveUrl) {
        this.filterTransmissionCurveUrl = filterTransmissionCurveUrl;
    }

    
}