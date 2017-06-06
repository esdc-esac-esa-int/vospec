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

import cds.savot.model.*;
import cds.savot.pull.*;
import esavo.vospec.util.*;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 *
 * 
 *
 * <pre>
 * %full_filespec)) { Photometry.java,1)) {java)) {1 %
 * %derived_by)) { ibarbarisi %
 * %date_created)) { Fri Nov 12 10)) {38)) {59 2004 %
 * 
 * </pre>
 *
 * @author
 * @version %version 1 %
 */
public class PhotometrySpectrum extends Spectrum implements Serializable, Runnable {

    public double referFlux;
    public double referWave;
    public double factorMultiply = 2.512;
    public String fileName;
    public File localFile = null;
    public boolean isBand;

    public PhotometrySpectrum() {
        super();
        setToWait(true);
    }

    public PhotometrySpectrum(Spectrum spectrum) {
        super(spectrum);
        setToWait(true);

    }

    public boolean chooseBand(String band) {

        isBand = false;

        if (band.toUpperCase().equals("PHOT_JHN_B")) {

            referFlux = 4260;
            referWave = 0.44;
            isBand = true;
        }
        if (band.toUpperCase().equals("PHOT_JHN_H")) {

            referFlux = 1080;
            referWave = 1.60;
            isBand = true;
        }
        if (band.toUpperCase().equals("PHOT_JHN_I")) {

            referFlux = 2550;
            referWave = 0.79;
            isBand = true;
        }
        if (band.toUpperCase().equals("PHOT_JHN_J")) {

            referFlux = 1600;
            referWave = 1.26;
            isBand = true;
        }
        if (band.toUpperCase().equals("PHOT_JHN_K")) {

            referFlux = 670;
            referWave = 2.22;
            isBand = true;
        }
        if (band.toUpperCase().equals("PHOT_JHN_R")) {

            referFlux = 3080;
            referWave = 0.64;
            isBand = true;
        }
        if (band.toUpperCase().equals("PHOT_JHN_U")) {

            referFlux = 1810;
            referWave = 0.36;
            isBand = true;
        }
        if (band.toUpperCase().equals("PHOT_JHN_V")) {

            referFlux = 3640;
            referWave = 0.55;
            isBand = true;
        }

        return isBand;

    }

    public double[] getColumnValues(String axe) {

        double[] value = null;

        try {
            InputStream instream;

            //URL url = new URL(this.getUrl());
            //instream = url.openStream();

            instream = new FileInputStream(localFile);
            SavotPullParser sb = new SavotPullParser(instream, 0, "UTF8");

            // get the VOTable object
            SavotVOTable sv = sb.getVOTable();
            BufferedWriter bw = null;

            // for each resource
            for (int l = 0; l < sb.getResourceCount(); l++) {

                SavotResource currentResource = (SavotResource) (sv.getResources().getItemAt(l));

                if (currentResource != null) {

                    // for each table of the current resource
                    for (int m = 0; m < currentResource.getTableCount(); m++) {

                        // get all the rows of the table
                        TRSet tr = currentResource.getTRSet(m);
                        int number_field = currentResource.getFieldSet(m).getItemCount();
                        String[] nameValue = new String[number_field];
                        String[] ucdValue = new String[number_field];

                        for (int g = 0; g < number_field; g++) {
                            SavotField currentField = (SavotField) (currentResource.getFieldSet(m).getItemAt(g));
                            nameValue[g] = currentField.getName();
                            ucdValue[g] = currentField.getUcd();
                        }


                        // for each row (each spectra)
                        Vector valueVector = new Vector();

                        for (int k = 0; k < tr.getItemCount(); k++) {

                            // get all the data of the row
                            TDSet theTDs = tr.getTDSet(k);
                            int internal_ct = 0;

                            // for each data of the row
                            for (int j = 0; j < theTDs.getItemCount(); j++) {
                                String itemCount = theTDs.getContent(j);

                                //TOCHANGE: if standard protocol changes and use an external file
                                // parse Format
                                chooseBand(ucdValue[j]);

                                if (isBand && !itemCount.equals("")) {
                                    if (axe.equals("WAVE")) {
                                        valueVector.addElement(new Double(referWave));
                                        ;
                                    }
                                    if (axe.equals("FLUX")) {
                                        double thisFlux = convert(new Double(itemCount).doubleValue());
                                        valueVector.addElement(new Double(thisFlux));
                                    }

                                }

                            }
                        }

                        value = new double[valueVector.size()];
                        for (int i = 0; i < valueVector.size(); i++) {
                            value[i] = ((Double) valueVector.elementAt(i)).doubleValue();
                        }

                    }
                }
            }//end for l
            instream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        

        return value;

    }

    /*******************************************************
     * /**
     * NAME: getColumnsNameAndUnits()
     *
     * PURPOSE:
     *
     * Implement a Vector with all the fits columns name and Units
     *
     * INPUT PARAMETERS: None
     *
     * OUTPUT PARAMETERS: None.
     *
     * RETURN VALUE: Vector columnsName
     *
     */
    public static Vector getColumnsNameAndUnits(String urlString) throws Exception {

        Vector resultVector = new Vector();
        InputStream instream;
        URL url = new URL(urlString);
        instream = url.openStream();

        SavotPullParser sb = new SavotPullParser(instream, 0, "UTF8");

        // get the VOTable object
        SavotVOTable sv = sb.getVOTable();
        BufferedWriter bw = null;

        // for each resource
        for (int l = 0; l < sb.getResourceCount(); l++) {

            SavotResource currentResource = (SavotResource) (sv.getResources().getItemAt(l));

            if (currentResource != null) {

                // for each table of the current resource
                for (int m = 0; m < currentResource.getTableCount(); m++) {

                    // get all the rows of the table
                    TRSet tr;
                    try {
                        tr = currentResource.getTRSet(m);
                    } catch (Exception e) {
                        continue;
                    }
                    int number_field = currentResource.getFieldSet(m).getItemCount();
                    String[] nameValue = new String[number_field];
                    boolean containData = false;


                    for (int g = 0; g < number_field; g++) {
                        SavotField currentField = (SavotField) (currentResource.getFieldSet(m).getItemAt(g));

                        String[] column = new String[2];

                        column[0] = currentField.getName();
                        column[1] = currentField.getUnit();

                        resultVector.addElement(column);

                    }

                }
            }
        }//end for l
        instream.close();

        return resultVector;
    }

    /*********************************************************************
    /**
     * NAME: convert(double enterFlux)
     *
     * PURPOSE:
     *
     * Return the converted flux value
     *
     * INPUT PARAMETERS:  value non converted
     *
     * OUTPUT PARAMETERS: None.
     *
     * RETURN VALUE:  none
     *
     *
     */
    public double convert(double enterPhot) {
        double exitFlux = referFlux * Math.pow(10., (-1.) * enterPhot / 2.5);
        return exitFlux;
    }

    /*********************************************************************
    /**
     * NAME: getWaveValues()
     *
     * PURPOSE:
     *
     * Get the wavelength values for this PhotometrySpectrum
     *
     * INPUT PARAMETERS:  none
     *
     * OUTPUT PARAMETERS: None.
     *
     * RETURN VALUE:  none
     *
     *
     */
    public double[] getWaveValues() {
        return getColumnValues("WAVE");
    }

    /*********************************************************************
    /**
     * NAME: getFluxValues()
     *
     * PURPOSE:
     *
     * Get the Flux values for this PhotometryeSpectrum
     *
     * INPUT PARAMETERS:  none
     *
     * OUTPUT PARAMETERS: None.
     *
     * RETURN VALUE:  none
     *
     *
     */
    public double[] getFluxValues() {
        return getColumnValues("FLUX");
    }

    /*********************************************************************
    /**
     * NAME: calculateData()
     *
     * PURPOSE:
     *
     * Operations to be done for this class of Spectrum. In this case, we just
     * need to copy the file. This method overrides the Spectrum mother method
     *
     *
     * INPUT PARAMETERS:  none
     *
     *
     *
     * OUTPUT PARAMETERS: None.
     *
     * RETURN VALUE:  none
     *
     *
     */
    public void calculateData() {

        setToWait(!Cache.alreadyLoaded(url));

        this.localFile = Cache.getFile(url);

        setToWait(false);

    }
}
