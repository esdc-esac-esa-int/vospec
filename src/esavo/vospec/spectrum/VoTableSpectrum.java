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
import java.net.*;
import java.util.*;

/**
 *
 *
 *
 * <pre>
 * %full_filespec: VoTableSpectrum.java,3:java:2 %
 * %derived_by: ibarbarisi %
 * %date_created: Wed Nov 07 11:56:40 2007 %
 *
 * </pre>
 *
 * @author
 * @version %version: 3 %
 */
public class VoTableSpectrum extends Spectrum implements Serializable, Runnable {

    String fileName;
    public File localFile = null;

    public VoTableSpectrum() {
        super();
        setToWait(true);
    }

    public VoTableSpectrum(Spectrum spectrum) {
        super(spectrum);
        setToWait(true);
    }

    public double[] getColumnValues(String name) {

        double[] value = null;
        //System.out.println("Process  !!!!");

        
        try {

            InputStream instream = null;

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
                        TRSet tr;
                        try {
                            tr = currentResource.getTRSet(m);
                        } catch (Exception e) {
                            continue;
                        }
                        int number_field = currentResource.getFieldSet(m).getItemCount();
                        String[] nameValue = new String[number_field];
                        String[] uTypeValue = new String[number_field];

                        boolean containData = false;

                        for (int g = 0; g < number_field; g++) {
                            SavotField currentField = (SavotField) (currentResource.getFieldSet(m).getItemAt(g));
                            nameValue[g] = currentField.getName();
                            uTypeValue[g] = currentField.getUtype();
                            if (nameValue[g].equals(name) || uTypeValue[g].contains(name)) {
                                containData = true;
                            }

                        }

                        if (containData) {
                            // for each row (each spectra)
                            value = new double[tr.getItemCount()];

                            for (int k = 0; k < tr.getItemCount(); k++) {

                                // get all the data of the row
                                TDSet theTDs = tr.getTDSet(k);
                                int internal_ct = 0;

                                // for each data of the row

                                for (int j = 0; j < theTDs.getItemCount(); j++) {
                                    String itemCount = theTDs.getContent(j);

                                    //TOCHANGE: if standard protocol changes and use an external file
                                    // parse columns
                                    if (nameValue[j].equals(name) || (uTypeValue[j].contains(name) && name.contains("."))) {
                                        value[k] = new Double(itemCount).doubleValue();
                                    }
                                }
                            }
                        }
                    }
                }
            }//end for l

            instream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


        //System.out.println("getcolumnvalues "+name+" "+value[0]+" "+value[1]);
        return value;

    }

    /*********************************************************************
     * /**
     * NAME: getWaveValues()
     *
     * PURPOSE:
     *
     * Get the wavelength values for this VoTableSpectrum
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
        if (waveLengthColumnName != null) {
            return getColumnValues(waveLengthColumnName);
        } else {
            return getColumnValues("Spectrum.Data.SpectralAxis.Value");
        }
    }

    /*********************************************************************
     * /**
     * NAME: getFluxValues()
     *
     * PURPOSE:
     *
     * Get the Flux values for this VoTableSpectrum
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
        if (fluxColumnName != null) {
            return getColumnValues(fluxColumnName);
        } else {
            return getColumnValues("Spectrum.Data.FluxAxis.Value");
        }
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
     * /**
     * NAME: calculateData()
     *
     * PURPOSE:
     *
     * Operations to be done for this class of Spectrum. In this case, we just
     * need to copy the FitsFile. This method overrides the Spectrum mother method
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
    @Override
    public void calculateData() {

        //return the name of the spectrum associated with url
        setToWait(!Cache.alreadyLoaded(url));


        this.localFile = Cache.getFile(url);


        setToWait(false);

    }


    /*
     * Overridden method to supply error values taken from the column
     * */
    @Override
    public double[] getFluxErrorLower() {
        if (fluxErrorsPresent) {
            return getColumnValues(this.getFluxErrorLowerColumnName());
        } else {
            return null;
        }
    }


    /*
     * Overridden method to supply error values taken from the column
     * */
    @Override
    public double[] getFluxErrorUpper() {
        if (fluxErrorsPresent) {
            return getColumnValues(this.getFluxErrorUpperColumnName());
        } else {
            return null;
        }
    }

    /*
     * Overridden method to supply error values taken from the column
     * */
    @Override
    public double[] getWaveErrorLower() {
        if (waveErrorsPresent) {
            return getColumnValues(this.getWaveErrorLowerColumnName());
        } else {
            return null;
        }
    }

    /*
     * Overridden method to supply error values taken from the column
     * */
    @Override
    public double[] getWaveErrorUpper() {
        if (waveErrorsPresent) {
            return getColumnValues(this.getWaveErrorUpperColumnName());
        } else {
            return null;
        }
    }
}
