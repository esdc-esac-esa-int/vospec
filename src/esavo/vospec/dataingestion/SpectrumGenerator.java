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

import esavo.vospec.spectrum.Spectrum;
import esavo.vospec.util.Utils;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author jgonzale
 */
public class SpectrumGenerator {

    // Datamodel
    private String datamodel = "";

    /////////////////////////
    //Mandatory information//
    /////////////////////////
    private String title,  format,  url = "";
    private String spectralAxisName,  fluxAxisName = "";

    //Option A - Units & Dimensions together - free format
    private String spectralUnits,  fluxUnits = "";

    //Option B - Units & Dimensions in separate parameters Scale/Dim
    private String scaleQ,  dimeQ = "";

    //Option C - Units & Dimensions in separate parameters Spectral/Flux
    private String spectralSI,  fluxSI = "";
    ////////////////////////
    //Optional information//
    ////////////////////////
    private Vector<String> metadata_identifiers = new Vector();
    private Hashtable metadata = new Hashtable();
    private String ra,  dec;
    //output
    private Spectrum spectrum = null;

    public Spectrum getSpectrum() {

        spectrum = new Spectrum();
        spectrum.setTitle(title);
        spectrum.setFormat(format);
        spectrum.setUrl(url);

        //Column names
        if (fluxAxisName != null && !fluxAxisName.equals("")) {
            spectrum.setFluxColumnName(fluxAxisName);
        } else {
            spectrum.setFluxColumnName("FLUX");
        }
        if (spectralAxisName != null && !spectralAxisName.equals("")) {
            spectrum.setWaveLengthColumnName(spectralAxisName);
        } else {
            spectrum.setWaveLengthColumnName("WAVE");
        }

        //Units (only relevant as text to display, unless no other option available)
        if (fluxUnits.toUpperCase().equals("UNKNOWN") || fluxUnits.toUpperCase().equals("DIMENSIONLESS") || fluxUnits.toUpperCase().equals("RELATIVE") || fluxUnits.toUpperCase().equals("ARBITRARY")) {
            //System.out.println("YES " + spectrum.getUrl());
            spectrum.setToBeNormalized(true);
            spectrum.setUnitsW(spectralUnits);

        } else {
            spectrum.setUnitsW(spectralUnits);
            spectrum.setUnitsF(fluxUnits);

        }


        //Real UNITS

        try {
            populateSpectralFlux();
        } catch (Exception e2) {
            try {
                populateScaleDim();
            } catch (Exception e3) {
                try {
                    populateFreeFormat();
                } catch (Exception e4) {
                }
            }
        }

        spectrum.setMetadata_identifiers(metadata_identifiers);
        spectrum.setMetaDataComplete(metadata);
        spectrum.setRa(ra);
        spectrum.setDec(dec);

        return spectrum;

    }

    /*
     *
     * Populates the spectrum using FLUXSI and SPECTRALSI parameters
     *
     * */
    private void populateSpectralFlux() {
        String spectral = spectralSI.substring(spectralSI.indexOf(" ") + 1) + " " + spectralSI.substring(0, spectralSI.indexOf(" "));
        String[] axes;
        axes = spectral.split(" ");
        //set spectrum with axes name from ISO/XMM Votable format
        if (!axes[0].equals("") && !axes[1].equals("")) {
            spectrum.setWaveFactor(axes[1]);
            spectrum.setDimeQWave(axes[0]);
        }

        String flux = fluxSI.substring(fluxSI.indexOf(" ") + 1) + " " + fluxSI.substring(0, fluxSI.indexOf(" "));
        axes = flux.split(" ");
        if (!axes[0].equals("") && !axes[1].equals("")) {
            spectrum.setFluxFactor(axes[1]);
            spectrum.setDimeQ(axes[0]);
        }
    }

    /*
     *
     * Populates the spectrum using SCALEQ and DIMEQ parameters
     *
     * */
    private void populateScaleDim() {

        String[] axes;
        axes = scaleQ.split(" ");
        //set spectrum with axes name from ISO/XMM Votable format
        if (!axes[0].equals("") && !axes[1].equals("")) {
            if (axes[1].equals("UNKNOWN")) {
                spectrum.setToBeNormalized(true);
                spectrum.setWaveFactor(axes[0]);
                spectrum.setFluxFactor("1");
            } else {
                spectrum.setWaveFactor(axes[0]);
                spectrum.setFluxFactor(axes[1]);
            }
        }

        axes = dimeQ.split(" ");
        //set spectrum with axes name from ISO/XMM Votable format
        if (!axes[0].equals("") && !axes[1].equals("")) {
            spectrum.setDimeQWave(axes[0]);
            spectrum.setDimeQ(axes[1]);
        }

    }

    /*
     *
     * Populates the spectrum using UNITS parameters
     *
     * */
    private void populateFreeFormat() {

        String[] waveUnit = Utils.getDimensionalEquation(spectralUnits);
        String[] fluxUnit = Utils.getDimensionalEquation(fluxUnits);

        spectrum.setWaveFactor(waveUnit[0]);
        spectrum.setDimeQWave(waveUnit[1]);
        spectrum.setFluxFactor(fluxUnit[0]);
        spectrum.setDimeQ(fluxUnit[1]);

    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the spectralAxisName
     */
    public String getSpectralAxisName() {
        return spectralAxisName;
    }

    /**
     * @param spectralAxisName the spectralAxisName to set
     */
    public void setSpectralAxisName(String spectralAxisName) {
        this.spectralAxisName = spectralAxisName;
    }

    /**
     * @return the fluxAxisName
     */
    public String getFluxAxisName() {
        return fluxAxisName;
    }

    /**
     * @param fluxAxisName the fluxAxisName to set
     */
    public void setFluxAxisName(String fluxAxisName) {
        this.fluxAxisName = fluxAxisName;
    }

    /**
     * @return the spectralUnits
     */
    public String getSpectralUnits() {
        return spectralUnits;
    }

    /**
     * @param spectralUnits the spectralUnits to set
     */
    public void setSpectralUnits(String spectralUnits) {
        this.spectralUnits = spectralUnits;
    }

    /**
     * @return the fluxUnits
     */
    public String getFluxUnits() {
        return fluxUnits;
    }

    /**
     * @param fluxUnits the fluxUnits to set
     */
    public void setFluxUnits(String fluxUnits) {
        this.fluxUnits = fluxUnits;
    }

    /**
     * @return the scaleQ
     */
    public String getScaleQ() {
        return scaleQ;
    }

    /**
     * @param scaleQ the scaleQ to set
     */
    public void setScaleQ(String scaleQ) {
        this.scaleQ = scaleQ;
    }

    /**
     * @return the dimeQ
     */
    public String getDimeQ() {
        return dimeQ;
    }

    /**
     * @param dimeQ the dimeQ to set
     */
    public void setDimeQ(String dimeQ) {
        this.dimeQ = dimeQ;
    }

    /**
     * @return the spectralSI
     */
    public String getSpectralSI() {
        return spectralSI;
    }

    /**
     * @param spectralSI the spectralSI to set
     */
    public void setSpectralSI(String spectralSI) {
        this.spectralSI = spectralSI;
    }

    /**
     * @return the fluxSI
     */
    public String getFluxSI() {
        return fluxSI;
    }

    /**
     * @param fluxSI the fluxSI to set
     */
    public void setFluxSI(String fluxSI) {
        this.fluxSI = fluxSI;
    }

    public void addMetadata_identifier(String identifier) {
        metadata_identifiers.add(identifier);
    }

    public void setMetaData(String identifier, Object object) {
        metadata.put(identifier, object);
    }

    /**
     * @return the ra
     */
    public String getRa() {
        return ra;
    }

    /**
     * @param ra the ra to set
     */
    public void setRa(String ra) {
        this.ra = ra;
    }

    /**
     * @return the dec
     */
    public String getDec() {
        return dec;
    }

    /**
     * @param dec the dec to set
     */
    public void setDec(String dec) {
        this.dec = dec;
    }

    /**
     * @return the datamodel
     */
    public String getDatamodel() {
        return datamodel;
    }

    /**
     * @param datamodel the datamodel to set
     */
    public void setDatamodel(String datamodel) {
        this.datamodel = datamodel;
    }
}

