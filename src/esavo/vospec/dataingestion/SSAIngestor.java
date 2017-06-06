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

import cds.savot.model.*;
import cds.savot.pull.SavotPullParser;
import esavo.vospec.slap.*;
import esavo.vospec.spectrum.*;
import esavo.vospec.util.EnvironmentDefs;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This class includes access methods to implement SSA / TSA/ SLAP protocols
 *
 *
 * @author jgonzale/ibarbarisi
 */
public class SSAIngestor {



    public static SpectrumSet getSpectra(String url){

        VOTable table = null;

        try {
            table = new VOTable(url, 100000);
        } catch (Exception ex) {
            //System.out.println(ex.getMessage());
        }

        return getSpectra(table);


    }




    public static SpectrumSet getSpectra(VOTable table){

        SpectrumSet specSet = new SpectrumSet();
 

        Vector<VOTableEntry> entries = new Vector();

        try {
            entries = table.getEntries();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        for (int i = 0; i < entries.size(); i++) {

            SpectrumGenerator generator = new SpectrumGenerator();

            boolean hasName = false;
            String[] unit = null;
            String datamodel = new String();

            for (int j = 0; j < entries.get(i).components.size(); j++) {


                String name = entries.get(i).components.get(j).getName();
                String utype = entries.get(i).components.get(j).getUtype();
                String ucd = entries.get(i).components.get(j).getUcd();
                //String unit = entries.get(i).components.get(j).getUnit();
                String value = entries.get(i).components.get(j).getValue();
                String id = entries.get(i).components.get(j).getID();
                String datatype = entries.get(i).components.get(j).getDatatype();



                try {

                    // parse datamodel (SED compliant or not)
                    if ((utype.toUpperCase().indexOf("DATASET.DATAMODEL") > -1)) {
                        datamodel = value;
                    }

                    //TOCHANGE: if standard protocol changes and use an external file
                    // parse Format
                    if (ucd.toUpperCase().indexOf("TITLE") > -1 ||
                            (name.toUpperCase().indexOf("TITLE") > -1) ||
                            (id.toUpperCase().indexOf("TITLE") > -1) ||
                            (utype.toUpperCase().indexOf("ssa:DATA_ID.TITLE")) > -1) {
                        generator.setTitle(value);
                        hasName = true;
                    }

                    // parse Spectra Url
                    if (ucd.toUpperCase().indexOf("ACCESSREFERENCE") > -1 ||
                            (ucd.indexOf("DATA_LINK") > -1) ||
                            (utype.toUpperCase().indexOf("ACCESS.REFERENCE") > -1)) {
                        generator.setUrl(value);
                        if (!hasName) {
                            generator.setTitle(value);
                        }
                    }

                    //TOCHANGE: if standard protocol changes and use an external file
                    // parse Format
                    if ((ucd.toUpperCase().indexOf("FORMAT") > -1) ||
                            (utype.toUpperCase().indexOf("ACCESS.FORMAT") > -1)) {
                        generator.setFormat(value);
                    }

                    if (ucd.toUpperCase().indexOf("AXES") > -1) {

                        String[] axes;
                        axes = value.split(" ");
                        //set spectrum with axes name from ISO/XMM Votable format
                        if (!axes[0].equals("") && !axes[1].equals("")) {
                            generator.setSpectralAxisName(axes[0]);
                            generator.setFluxAxisName(axes[1]);
                        }
                    }

                    //SSAP 1.0
                    //------------------------------------------------
                    if ((utype.toUpperCase().indexOf("CHAR.SPECTRALAXIS.NAME") > -1) ||
                            (utype.toUpperCase().indexOf("DATASET.SPECTRALAXIS") > -1) ||
                            (id.toUpperCase().indexOf("SPECTRALNAMEAXIS") > -1)) {
                        generator.setSpectralAxisName(value);
                    }


                    if ((utype.toUpperCase().indexOf("CHAR.FLUXAXIS.NAME") > -1) ||
                            (utype.toUpperCase().indexOf("DATASET.FLUXAXIS") > -1) ||
                            id.toUpperCase().indexOf("FLUXNAMEAXIS") > -1) {
                        generator.setFluxAxisName(value);
                    }

                    if (ucd.toUpperCase().indexOf("UNITS") > -1) {

                        unit = value.split(" ");
                        //set spectrum with axes name from ISO/XMM Votable format
                        if (!unit[0].equals("") && !unit[1].equals("")) {
                            generator.setFluxUnits(unit[1]);
                            generator.setSpectralUnits(unit[0]);
                        }
                    }

                    //SSAP 1.0
                    //------------------------------------------------
                    if (utype.toUpperCase().indexOf("CHAR.SPECTRALAXIS.UNIT") > -1) {
                        generator.setSpectralUnits(value);

                    }

                    if (utype.toUpperCase().indexOf("CHAR.FLUXAXIS.UNIT") > -1) {
                        generator.setFluxUnits(value);
                    }

                    if (ucd.toUpperCase().indexOf("SCALEQ") > -1) {
                        generator.setScaleQ(value);
                    }

                    if (ucd.toUpperCase().indexOf("DIMEQ") > -1) {
                        generator.setDimeQ(value);
                    }

                    //SSAP 1.0
                    //------------------------------------------------
                    if (utype.toUpperCase().indexOf("SPECTRALSI") > -1) {
                        generator.setSpectralSI(value);
                    }

                    if (utype.toUpperCase().indexOf("FLUXSI") > -1) {
                        generator.setFluxSI(value);
                    }

                    // create hashtable with all the others data (like ObsId : 4001501)
                    //Select key: UTYPE -> UCD -> ID
                    String identifier = new String();

                    if (!utype.equals("")) {
                        identifier = utype;
                    } else if (!ucd.equals("")) {
                        identifier = ucd;
                    } else if (!id.equals("")) {
                        identifier = id;
                    } else {
                        identifier = "";
                    }

                    generator.addMetadata_identifier(new String(identifier));

                    //Parse values (strings) to their respective types
                    try {
                        if (datatype.toLowerCase().contains("boolean")) {
                            generator.setMetaData(identifier, new Boolean(value));
                            //} else if (datatype[j].toUpperCase().contains("bit")) {
                            //    spectrum.setMetaData(identifier, );
                        } else if (datatype.toLowerCase().contains("unsignedByte")) {
                            generator.setMetaData(identifier, new Byte(value));
                        } else if (datatype.toLowerCase().contains("short")) {
                            generator.setMetaData(identifier, new Short(value));
                        } else if (datatype.toLowerCase().contains("int")) {
                            generator.setMetaData(identifier, new Integer(value));
                        } else if (datatype.toLowerCase().contains("long")) {
                            generator.setMetaData(identifier, new Long(value));
                        } else if (datatype.toLowerCase().contains("char")) {
                            generator.setMetaData(identifier, new String(value));
                        } else if (datatype.toLowerCase().contains("unicodeChar")) {
                            generator.setMetaData(identifier, new String(value));
                        } else if (datatype.toLowerCase().contains("float")) {
                            generator.setMetaData(identifier, new Float(value));
                        } else if (datatype.toLowerCase().contains("double")) {
                            generator.setMetaData(identifier, new Double(value));
                            //} else if (datatype[j].toUpperCase().contains("floatComplex")) {
                            //    spectrum.setMetaData(identifier, );
                            //} else if (datatype[j].toUpperCase().contains("doubleComplex")) {
                            //    spectrum.setMetaData(identifier, );
                        } else {
                            generator.setMetaData(identifier, value);
                        }
                    } catch (NumberFormatException e) {
                        //e.printStackTrace();
                        //spectrum.setMetaData(identifier, itemCount);
                        if (datatype.toLowerCase().contains("boolean")) {
                            generator.setMetaData(identifier, new Boolean(false));
                        } else if (datatype.toLowerCase().contains("unsignedByte")) {
                            generator.setMetaData(identifier, new Byte((byte) 0));
                        } else if (datatype.toLowerCase().contains("short")) {
                            generator.setMetaData(identifier, new Short((short) 0));
                        } else if (datatype.toLowerCase().contains("int")) {
                            generator.setMetaData(identifier, new Integer(0));
                        } else if (datatype.toLowerCase().contains("long")) {
                            generator.setMetaData(identifier, new Long(0));
                        } else if (datatype.toLowerCase().contains("char")) {
                            generator.setMetaData(identifier, new String(""));
                        } else if (datatype.toLowerCase().contains("unicodeChar")) {
                            generator.setMetaData(identifier, new String(""));
                        } else if (datatype.toLowerCase().contains("float")) {
                            generator.setMetaData(identifier, new Float(0));
                        } else if (datatype.toLowerCase().contains("double")) {
                            generator.setMetaData(identifier, new Double(0));
                        }
                    }

                    if ((ucd.indexOf("POS_EQ_RA_MAIN") > -1) || (ucd.toUpperCase().indexOf("POS.EQ.RA") > -1)) {
                        generator.setRa(value);
                    }

                    if (ucd.indexOf("POS_EQ_DEC_MAIN") > -1 || (ucd.toUpperCase().indexOf("POS.EQ.DEC") > -1)) {
                        generator.setDec(value);
                    }


                } catch (Exception e) {
                    System.out.println("Units parsing failed");
                    e.printStackTrace();
                }


            }


            //Create SPECTRUM

            if(generator.getUrl().length()!=0){

                Spectrum spectrum = generator.getSpectrum();
                if (spectrum.getFormat().toUpperCase().indexOf("PHOTOMETRY") > -1) {
                    VOTablePhotometry photometrySpectrum = new VOTablePhotometry(spectrum);
                    specSet.addSpectrum(i, photometrySpectrum);
                } else if ((((datamodel.toUpperCase().contains("SPECTRUM")) && (datamodel.toUpperCase().contains("1.")) && (spectrum.getFormat().toUpperCase().indexOf("VOTABLE") > -1)) ||
                        (datamodel.toUpperCase().contains("SED"))) && (spectrum.getFormat().toUpperCase().indexOf("VOTABLE") > -1)) {
                    SedSpectrum sedSpectrum = new SedSpectrum(spectrum);
                    specSet.addSpectrum(i, sedSpectrum);
                } else {
                    if (spectrum.getFormat().toUpperCase().indexOf("FITS") > -1) {
                        FitsSpectrum fitsSpectrum = new FitsSpectrum(spectrum);
                        specSet.addSpectrum(i, fitsSpectrum);
                    } else if (spectrum.getFormat().toUpperCase().indexOf("VOTABLE") > -1) {
                        VoTableSpectrum voTableSpectrum = new VoTableSpectrum(spectrum);
                        specSet.addSpectrum(i, voTableSpectrum);
                    } else {
                        specSet.addSpectrum(i, spectrum);
                    }
                }

            }



        }


        return specSet;
    }









    public static SlapServerList getSlapServerList() {

        InputStream ts = null;
        SlapServerList slapServerList = new SlapServerList();

        try {
            HttpURLConnection con = (HttpURLConnection) new URL(EnvironmentDefs.getSLAPURL()).openConnection();
            HttpURLConnection.setFollowRedirects(true);
            ts = con.getInputStream();
        } catch (Exception e) {
            System.out.println("Problems calling Slap List xmlFile...");
        }

        SavotPullParser tb = new SavotPullParser(ts, 0, "UTF8");
        slapServerList = setSlapServerFromSavot(tb);

        try {
            ts.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return slapServerList;
    }

    public static SlapServerList setSlapServerFromSavot(SavotPullParser sb) {

        SlapServerList slapServerList = new SlapServerList();

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
                    String[] ucd = new String[number_field];

                    for (int g = 0; g < number_field; g++) {
                        SavotField currentField = (SavotField) (currentResource.getFieldSet(m).getItemAt(g));
                        ucd[g] = currentField.getUcd();
                    }

                    // for each row (each spectra)
                    for (int k = 0; k < tr.getItemCount(); k++) {

                        SlapServer slapServer = new SlapServer();

                        // get all the data of the row
                        TDSet theTDs = tr.getTDSet(k);
                        int internal_ct = 0;

                        // for each data of the row
                        for (int j = 0; j < theTDs.getItemCount(); j++) {
                            String itemCount = theTDs.getContent(j);
                            //TOCHANGE: if standard protocol changes and use an external file
                            // parse Name
                            if (ucd[j].indexOf("Title") > -1) {
                                slapServer.setSlapName(itemCount);
                            }

                            // parse Image Url
                            if (ucd[j].indexOf("AccessReference") > -1 || ucd[j].indexOf("DATA_LINK") > -1) {
                                slapServer.setSlapUrl(itemCount);
                            }
                        }
                        slapServerList.addSlapServer(k, slapServer);
                    }//end for k
                }//end for m

            }//end if

        }//end for l

        return slapServerList;
    }

    /**
     * return the list of Spectra after parsing the VOTable in that InputStream
     *
     * @param finalUrl : Url containig VoTable
     *
     */
    public static LineSet getLines(String finalUrl) throws java.rmi.RemoteException {

        System.out.println("getLines(" + finalUrl + ")");

        LineSet ls = new LineSet();

        try {
            InputStream instream;
            if (finalUrl.startsWith("file://")) {
                instream = new FileInputStream(new File(finalUrl.substring(7)));
            } else {
                HttpURLConnection con = (HttpURLConnection) new URL(finalUrl).openConnection();
                HttpURLConnection.setFollowRedirects(true);
                instream = con.getInputStream();
                System.out.println("Reading SLAP Url..." + finalUrl);
            }
            SavotPullParser sb = new SavotPullParser(instream, 0, "UTF8");

            ls = getLineSetFromSavot(sb);

            int number_lines = ls.size();
            System.out.println("Number lines ..." + number_lines);

            instream.close();

        } catch (IOException e1) {
            System.out.println("IOException. Problems trying to read the URL");
            e1.printStackTrace();
        } catch (NullPointerException e2) {
            System.out.println("No results for..." + finalUrl);
            e2.printStackTrace();
        } catch (Exception e) {
            throw new java.rmi.RemoteException("Invalid Format");
        }

        return ls;
    }//end getLines()

    /**
     * parse  VOTable to LineSet
     *
     */
    private static LineSet getLineSetFromSavot(SavotPullParser sb) throws Exception {

        LineSet lineSet = new LineSet();

        // get the VOTable object
        SavotVOTable sv = sb.getVOTable();
        BufferedWriter bw = null;

        if (sb.getResourceCount() == 0) {
            throw new Exception("Incorrect Format");
        }

        // for each resource
        for (int l = 0; l < sb.getResourceCount(); l++) {

            SavotResource currentResource = (SavotResource) (sv.getResources().getItemAt(l));

            if (currentResource != null) {

                // for each table of the current resource
                for (int m = 0; m < currentResource.getTableCount(); m++) {

                    // get all the rows of the table
                    TRSet tr = currentResource.getTRSet(m);
                    int number_field = currentResource.getFieldSet(m).getItemCount();

                    String utype = "";
                    String id = "";
                    String name = "";
                    String ucd = "";
                    String dataType = "";
                    String arraySize = "";
                    String description = "";

                    Vector fieldsVector = new Vector();
                    Hashtable allFieldProperties = new Hashtable();

                    String[] field = new String[number_field];
                    for (int g = 0; g < number_field; g++) {

                        boolean fieldNotSet = true;

                        SavotField currentField = (cds.savot.model.SavotField) (currentResource.getFieldSet(m).getItemAt(g));

                        utype = currentField.getUtype();
                        id = currentField.getId();
                        name = currentField.getName();
                        ucd = currentField.getUcd();
                        dataType = currentField.getDataType();
                        arraySize = currentField.getArraySize();
                        description = currentField.getDescription();

                        Hashtable fieldProperties = new Hashtable();

                        if (notNull(utype)) {
                            fieldProperties.put("uType", utype);
                            if (fieldNotSet) {
                                field[g] = utype;
                                fieldNotSet = false;
                            }
                        }

                        if (notNull(id)) {
                            fieldProperties.put("id", id);
                            if (fieldNotSet) {
                                field[g] = id;
                                fieldNotSet = false;
                            }
                        }

                        if (notNull(name)) {
                            fieldProperties.put("name", name);
                            if (fieldNotSet) {
                                field[g] = name;
                                fieldNotSet = false;
                            }
                        }

                        if (notNull(ucd)) {
                            fieldProperties.put("UCD", ucd);
                            if (fieldNotSet) {
                                field[g] = ucd;
                                fieldNotSet = false;
                            }
                        }

                        if (notNull(dataType)) {
                            fieldProperties.put("dataType", dataType);
                        }
                        if (notNull(arraySize)) {
                            fieldProperties.put("arraySize", arraySize);
                        }
                        if (notNull(description)) {
                            fieldProperties.put("description", description);
                        }

                        allFieldProperties.put(field[g], fieldProperties);
                        fieldsVector.add(field[g]);
                    }
                    lineSet.setFields(fieldsVector);
                    lineSet.setAllFieldProperties(allFieldProperties);

                    // for each row (each line)
                    for (int k = 0; k < tr.getItemCount(); k++) {

                        Line line = new Line();
                        // get all the data of the row
                        TDSet theTDs = tr.getTDSet(k);

                        for (int j = 0; j < theTDs.getItemCount(); j++) {

                            String value = theTDs.getContent(j);
                            line.setValue(field[j], value);
                        }
                        lineSet.addLine(line);
                    }
                }
            }

        }

        return lineSet;
    }

    //accessory methods
    private static boolean notNull(String thisString) {
        if (thisString != null) {
            if (!thisString.equals("")) {
                return true;
            }
        }
        return false;
    }

   

    ///////////////////////////////////////////////////////////////////////
    /////////////// FORMAT=METADATA QUERIES ///////////////////////////////
    ///////////////////////////////////////////////////////////////////////
    /*
     * This method performs request=metadata queries over the server's url
     * and returns the server with the obtained input parameters.
     *
     * It also populates the general description parameter
     *
     * */
    public static SsaServer populateInputParameters(SsaServer server) {

        try {

            Vector paramVector = new Vector();
            String url = server.getSsaUrl();
            if (!url.contains("FORMAT=METADATA")) {
                url = server.getSsaUrl() + "&FORMAT=METADATA";
            }

            String minString = "";
            String maxString = "";

            String generalDesc = "";

            //URL url = new URL(urlMetadata);
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            HttpURLConnection.setFollowRedirects(true);
            con.setReadTimeout(5000);
            con.setConnectTimeout(5000);
            paramVector = new Vector();

            SavotPullParser sb = new SavotPullParser(con.getInputStream(), 0, "UTF8");
            SavotVOTable sv = sb.getVOTable();
            BufferedWriter bw = null;
            int ct = 0;

            if (sb.getResourceCount() == 0) {
                //throw new Exception("Incorrect Format");
                }

            // for each resource
            for (int l = 0; l < sb.getResourceCount(); l++) {

                SavotResource currentResource = (SavotResource) (sv.getResources().getItemAt(l));

                if (currentResource != null) {
                    // get all the params of the table
                    ParamSet ps = currentResource.getParams();
                    //get the number of params
                    int number_field = currentResource.getParams().getItemCount();

                    generalDesc = currentResource.getDescription();

                    if (server.getType() != SsaServer.TSAP) {
                        //for each param
                        for (int g = 0; g < number_field; g++) {
                            SavotParam currentParam = (SavotParam) (currentResource.getParams().getItemAt(g));
                            if (currentParam.getName().indexOf("INPUT") > -1) {

                                String[] param1 = currentParam.getName().split(":");
                                String param = param1[1];
                                String description = currentParam.getDescription();
                                String[] paramDesc = new String[2];
                                paramDesc[0] = param;
                                paramDesc[1] = description;
                               paramVector.add(paramDesc);
                           }
                        }
                    } else { //isTsa
                        //for each param
                        for (int g = 0; g < number_field; g++) {

                            SavotParam currentParam = (SavotParam) (currentResource.getParams().getItemAt(g));
                            SavotValues sValues = currentParam.getValues();

                            if (sValues != null) {
                                String tmpValue;
                                String selectedValue = null;
                                Vector values = new Vector();

                                // Getting the selected value
                                tmpValue = currentParam.getValue();

                                if (tmpValue != null) {
                                    if (!tmpValue.equals("")) {
                                        selectedValue = tmpValue;
                                    }
                                }

                                boolean isCombo = true;
                                boolean hasMin = false;
                                boolean hasMax = false;
                                minString = "";
                                maxString = "";

                                if (((OptionSet) sValues.getOptions()).getItems() == null) {
                                    isCombo = false;
                                    tmpValue = currentParam.getValue();
                                    values.add(tmpValue);

                                    if (sValues.getMin() != null) {
                                        hasMin = true;
                                        minString = sValues.getMin().getValue();
                                    }

                                    if (sValues.getMax() != null) {
                                        hasMax = true;
                                        maxString = sValues.getMax().getValue();
                                    }

                                } else {
                                    minString = "";
                                    maxString = "";
                                    OptionSet os = sValues.getOptions();
                                    Vector valuesObject = os.getItems();

                                    for (int counter = 0; counter < valuesObject.size(); counter++) {
                                        tmpValue = ((SavotOption) valuesObject.elementAt(counter)).getValue();
                                        values.add(tmpValue);
                                    }

                                }

                                if (currentParam.getName().indexOf("INPUT") > -1) {
                                    TsaServerParam tsaServerParam = new TsaServerParam();
                                    String[] param1 = currentParam.getName().split(":");
                                    String param = param1[1];
                                    if (!param.toUpperCase().equals("POS") && !param.toUpperCase().equals("SIZE") && !param.toUpperCase().equals("BAND") && !param.toUpperCase().equals("TIME")) {
                                        tsaServerParam.setName(param);
                                        tsaServerParam.setUcd(currentParam.getUcd());
                                        tsaServerParam.setUtype(currentParam.getUtype());
                                        tsaServerParam.setUnit(currentParam.getUnit());
                                        tsaServerParam.setDescription(currentParam.getDescription());
                                        tsaServerParam.setValues(values);
                                        tsaServerParam.setIsCombo(isCombo);
                                        if (hasMin) {
                                            tsaServerParam.setMinString(minString);
                                        }
                                        if (hasMax) {
                                            tsaServerParam.setMaxString(maxString);
                                        }
                                        if (selectedValue != null) {
                                            tsaServerParam.setSelectedValue(selectedValue);
                                        }
                                        paramVector.add(tsaServerParam);

                                    }
                                }
                            } else {
                                //in case param doesn't have any option or min/maxstopt
                                if (currentParam.getName().indexOf("INPUT") > -1) {
                                    TsaServerParam tsaServerParam = new TsaServerParam();
                                    String[] param1 = currentParam.getName().split(":");
                                    String param = param1[1];
                                    if (!param.toUpperCase().equals("POS") && !param.toUpperCase().equals("SIZE") && !param.toUpperCase().equals("BAND") && !param.toUpperCase().equals("TIME")) {
                                        tsaServerParam.setName(param);
                                        tsaServerParam.setUcd(currentParam.getUcd());
                                        tsaServerParam.setUtype(currentParam.getUtype());
                                        tsaServerParam.setUnit(currentParam.getUnit());
                                        tsaServerParam.setDescription(currentParam.getDescription());
                                        tsaServerParam.setIsCombo(false);
                                        tsaServerParam.setSelectedValue(currentParam.getValue());
                                        paramVector.add(tsaServerParam);
                                    }
                                }
                            }
                        }

                    }//end for l

                }//end if

            }//end for l

            server.setInputParams(paramVector);
            server.setGeneralDesc(generalDesc);

        } catch (Exception e) {
            //e.printStackTrace();
        }

        return server;

    }

}
