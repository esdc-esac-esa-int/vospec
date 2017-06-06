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

import cds.savot.model.FieldSet;
import cds.savot.model.SavotField;
import cds.savot.model.SavotFieldRef;
import cds.savot.model.SavotGroup;
import cds.savot.model.SavotResource;
import cds.savot.model.SavotTable;
import cds.savot.model.SavotVOTable;
import cds.savot.model.TRSet;
import cds.savot.pull.SavotPullParser;
import esavo.vospec.spectrum.Spectrum;
import esavo.vospec.spectrum.SpectrumSet;
import esavo.vospec.spectrum.Unit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 *
 * @author jgonzale
 */
public class ConeSearchIngestor {

    public ConeSearchIngestor() {
    }
    private static int TIMEOUT = 100000;

    public static SpectrumSet getSpectra(String url) throws Exception {


        SavotPullParser sb;

        InputStream instream = null;

        //Map to be populated with points added per catalogue (catalogue name, spectrum)
        Map<String, Spectrum> catalogSpectra = new Hashtable<String, Spectrum>();
        //Final returned spectrum objects
        SpectrumSet outputSpectra = new SpectrumSet();


        try {

            if (url.startsWith("file:")) {
                instream = new FileInputStream(new File(url.substring(5)));
            } else {
                HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                con.setInstanceFollowRedirects(true);
                HttpURLConnection.setFollowRedirects(true);
                con.setReadTimeout(TIMEOUT);
                con.setConnectTimeout(TIMEOUT);
                instream = con.getInputStream();
            }

            sb = new SavotPullParser(instream, 0, "UTF8");
            instream.close();

            SavotVOTable sv = sb.getVOTable();

            if (sb.getResourceCount() == 0) {
                throw new Exception("Table empty or incorrect format");
            }


            for (int l = 0; l < sb.getResourceCount(); l++) {

                SavotResource currentResource = (SavotResource) (sv.getResources().getItemAt(l));

                if (currentResource != null) {

                    //For each table of the current resource
                    for (int m = 0; m < currentResource.getTableCount(); m++) {

                        SavotTable table = (SavotTable) currentResource.getTables().getItemAt(m);

                        ////////////////////////////////////
                        //   Process Reference Columns    //
                        ////////////////////////////////////

                        //Parse the referenced columns (FIELDref) into a Map (column, utype)

                        Map<Integer, String> columnsRefs = new HashMap<Integer, String>();


                        for (int g = 0; g < table.getGroups().getItemCount(); g++) {

                            SavotGroup group = (SavotGroup) table.getGroups().getItemAt(g);

                            if (group.getUtype().toUpperCase().contains("PHOTOMETRYPOINT")) {

                                //Populate refs: "ref" reference value and utype value

                                Map<String, String> refs = new HashMap<String, String>();

                                for (int f = 0; f < group.getFieldsRef().getItemCount(); f++) {

                                    SavotFieldRef fieldRef = (SavotFieldRef) group.getFieldsRef().getItemAt(f);
                                    ///System.out.println("SAVING UTYPE: " + fieldRef.getUtype() + " UCD: " + fieldRef.getUcd() + " REF: " + fieldRef.getRef());
                                    refs.put(fieldRef.getRef(), fieldRef.getUtype());

                                }

                                //Populate columnsRefs: column number and utype value

                                FieldSet fields = currentResource.getFieldSet(0);
                                int number_field = fields.getItemCount();

                                for (int i = 0; i < number_field; i++) {
                                    SavotField currentField = (SavotField) fields.getItemAt(i);

                                    String utype = refs.get(currentField.getId());

                                    //Save columns with uTypes referenced

                                    if (utype != null) {
                                        //System.out.println("Saving into columnsRefs: [" + i + "]  [" + utype + "]");
                                        columnsRefs.put(i, utype);
                                    }



                                }
                            }
                        }


                        ////////////////////////////////////////
                        ///    PROCCESS ACTUAL TABLE         ///
                        ////////////////////////////////////////

                        //Get all the rows of the table

                        TRSet tr;

                        try {

                            tr = currentResource.getTRSet(m);

                        } catch (Exception e) {
                            continue;
                        }



                        //For each row

                        for (int k = 0; k < tr.getItemCount(); k++) {

                            List<Integer> columns = new ArrayList<Integer>(columnsRefs.keySet());


                            //Populate row values
                            String waveUnits = null;
                            String fluxUnits = null;

                            Double waveValue = null;
                            Double fluxValue = null;

//                            Double fluxErr = null;

                            String filter = null;
                            String catalogue = null;

                            String ra = null;
                            String dec = null;



                            //For each  column fill row values

                            for (int c = 0; c < tr.getTDSet(k).getItemCount(); c++) {

                                String itemValue = "";
                                String utype = "";

                                if (columnsRefs.get(c) != null && !columnsRefs.get(c).equals("")) {
                                    utype = columnsRefs.get(c).toUpperCase();

                                }

                                if (utype.equals("PHOTDM:PHOTOMETRYFILTER.SPECTRALAXIS.COVERAGE.LOCATION.VALUE")) {

                                    itemValue = tr.getTDSet(k).getContent(c);
                                    waveValue = new Double(itemValue).doubleValue();
                                    waveUnits = ((SavotField) currentResource.getFieldSet(0).getItemAt(c)).getUnit();
                                }

                                if (utype.equals("SPEC:PHOTOMETRYPOINT")) {
                                    itemValue = tr.getTDSet(k).getContent(c);
                                    if (!itemValue.equals("")) {
                                        fluxValue = new Double(itemValue).doubleValue();
                                    }
                                    fluxUnits = ((SavotField) currentResource.getFieldSet(0).getItemAt(c)).getUnit();
                                }

//                                if (utype.equals("SPEC:PHOTOMETRYPOINTERROR")) {
//                                    itemValue = tr.getTDSet(k).getContent(c);
//                                    if(itemValue!=null){
//                                        fluxErr = new Double(itemValue).doubleValue();
//                                    }
//
//                                }

                                if (utype.equals("PHOTDM:PHOTOMETRYFILTER.IDENTIFIER")) {

                                    //catalogue = (new StringTokenizer(tr.getTDSet(k).getContent(c), ":")).nextToken();

                                    String id = tr.getTDSet(k).getContent(c);

                                    if (id.indexOf(":") > -1) {
                                        catalogue = id.substring(0, id.indexOf(":"));
                                    }

                                    if (catalogue == null || catalogue.equals("")) {
                                        catalogue = "Undefined";
                                    }

                                    filter = id;

                                    //System.out.println("ID=" + id + " CUT=" + catalogue);


                                }



                                SavotField currentField = (SavotField) (currentResource.getFieldSet(m).getItemAt(c));
                                String ucd = currentField.getUcd();
                               

                                if (ucd.toUpperCase().contains("RA")) {
                                    ra = tr.getTDSet(k).getContent(c);
                                }

                                if (ucd.toUpperCase().contains("DEC")) {
                                    dec = tr.getTDSet(k).getContent(c);
                                }




                            }


                            if (waveValue == null || fluxValue == null) {
                                System.out.println("Photometric point " + filter + " missing wavelength or flux");
                                continue;
                            }


                            //Pick Spectrum corresponding to this catalogue
                            // or create new and associate to the map

                          /*
                            Spectrum currentSpectrum = (Spectrum) catalogSpectra.get(catalogue);

                            if (currentSpectrum == null) {
                            currentSpectrum = new Spectrum();

                            currentSpectrum.setName(catalogue);
                            currentSpectrum.setTitle(catalogue);
                            currentSpectrum.setFormat("spectrum/photometry");
                            //outputSpectrum.setUnitsF(spectrum.getUnitsF());
                            //outputSpectrum.setUnitsW(((PhotometryFilter) nodes.get(0).getRelatedObject()).getWavelengthMean().getUnit());

                            currentSpectrum.setUnits(new Unit(waveUnits, fluxUnits));
                            currentSpectrum.setWaveValues(new double[0]);
                            currentSpectrum.setFluxValues(new double[0]);


                            catalogSpectra.put(catalogue, currentSpectrum);

                            }*/
                             



                            Spectrum currentSpectrum = new Spectrum();

                            currentSpectrum.setName(filter);
                            currentSpectrum.setTitle(filter);
                            currentSpectrum.setFormat("spectrum/photometry");
                            currentSpectrum.setUnits(new Unit(waveUnits, fluxUnits));
                            currentSpectrum.setWaveValues(new double[0]);
                            currentSpectrum.setFluxValues(new double[0]);
                            currentSpectrum.addMetadata_identifier("Catalogue");
                            currentSpectrum.addMetaData("Catalogue", catalogue);

                           
                            currentSpectrum.setRa(ra);
                            currentSpectrum.setDec(dec);


                            catalogSpectra.put(filter, currentSpectrum);














                            //populate output spectrum

                            currentSpectrum.setWaveValues(addElementToArray(currentSpectrum.getWaveValues(), waveValue));
                            currentSpectrum.setFluxValues(addElementToArray(currentSpectrum.getFluxValues(), fluxValue));





                        }
                    }
                }
            }




            Set<String> catalogIds = catalogSpectra.keySet();


            for (String s : catalogIds) {
                outputSpectra.addSpectrum(outputSpectra.getSpectrumSet().size(), catalogSpectra.get(s));

            }


        } catch (FileNotFoundException ex) {
            ex.printStackTrace();

        } catch (MalformedURLException ex) {
            ex.printStackTrace();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //return getSpectra(table);
        return outputSpectra;

    }//end getSpectra class Method

    private static double[] addElementToArray(double[] inputArray, double value) {

        double[] resultingValues = new double[inputArray.length + 1];
        for (int i = 0; i < inputArray.length; i++) {
            resultingValues[i] = inputArray[i];
        }
        resultingValues[resultingValues.length - 1] = value;
        return resultingValues;
    }
}
