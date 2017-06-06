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

import cds.savot.model.GroupSet;
import cds.savot.model.ParamSet;
import cds.savot.model.SavotField;
import cds.savot.model.SavotGroup;
import cds.savot.model.SavotParam;
import cds.savot.model.SavotResource;
import cds.savot.model.SavotTable;
import cds.savot.model.SavotVOTable;
import cds.savot.model.TDSet;
import cds.savot.model.TRSet;
import cds.savot.pull.SavotPullParser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 *
 * This class is intended to parse a VOTable to a vector of VOTableEntry
 *
 * @author jgonzale
 */
public class VOTable {



    private Vector<VOTableEntry> entries = new Vector<VOTableEntry>();



    public VOTable(String url, int timeout) throws Exception{

        SavotPullParser sb;

        InputStream instream = null;

        try{

            if (url.startsWith("file:")) {
                instream = new FileInputStream(new File(url.substring(5)));
            } else {
                HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                con.setInstanceFollowRedirects(true);
                HttpURLConnection.setFollowRedirects(true);
                con.setReadTimeout(timeout);
                con.setConnectTimeout(timeout);
                instream = con.getInputStream();
            }
            sb = new SavotPullParser(instream, 0, "UTF8");
            instream.close();

            entries = parseEntries(sb);
        
        } catch (FileNotFoundException ex) {
           ex.printStackTrace();

        } catch (MalformedURLException ex) {
           ex.printStackTrace();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }


    public VOTable(Map metadata){

        Iterator it = metadata.keySet().iterator();

        VOTableEntry entry = new VOTableEntry();

        while(it.hasNext()){

            String key = (String) it.next();
            String value = (String) metadata.get(key);

            VOTableEntryComponent component = new VOTableEntryComponent();
            component.setName(key);
            component.setID(key);
            component.setUtype(key);
            component.setUcd(key);
            component.setValue(value);

            entry.components.add(component);

        }

        
        Vector<VOTableEntry> entries = new Vector<VOTableEntry>();
        entries.add(entry);

        this.entries = entries;

    }

    
    public Vector<VOTableEntry> getEntries(){
        
        return entries;
    
    }





    /*
     * Parse the VOTable to a Vector of entries
     *
     * */


    private Vector<VOTableEntry> parseEntries(SavotPullParser sb) throws Exception {




        Vector<VOTableEntry> entriesReturn = new Vector();


        SavotVOTable sv = sb.getVOTable();
        BufferedWriter bw = null;


        if (sb.getResourceCount() == 0) {
            throw new Exception("Table empty or incorrect format");
        }

        // for each resource
        for (int l = 0; l < sb.getResourceCount(); l++) {

            SavotResource currentResource = (SavotResource) (sv.getResources().getItemAt(l));

            if (currentResource != null) {



                /////////////////////////////////////////////
                /////// PROCESS PARAMS FOR THIS RESOURCE ////
                //put all params into a single vector,
                //so it can be applied to all the entries found in the table
                /////////////////////////////////////////////

                Vector<VOTableEntryComponent> resourceParams = new Vector();

                ParamSet resourceParamset = currentResource.getParams();

                //TODO support groups

                /*GroupSet groups = currentResource.getGroups();

                for (int i = 0; i < groups.getItemCount(); i++) {
                    ParamSet groupParams = ((ParamSet) ((SavotGroup) groups.getItemAt(i)).getParams());
                    for (int j = 0; j < groupParams.getItemCount(); j++) {
                        paramset.addItem(groupParams.getItemAt(j));
                    }
                }*/

                for (int i = 0; i < resourceParamset.getItemCount(); i++) {
                    VOTableEntryComponent param = new VOTableEntryComponent();

                    param.setArraysize(((SavotParam) resourceParamset.getItemAt(i)).getArraySize());
                    param.setDatatype(((SavotParam) resourceParamset.getItemAt(i)).getDataType());
                    param.setID(((SavotParam) resourceParamset.getItemAt(i)).getId());
                    param.setName(((SavotParam) resourceParamset.getItemAt(i)).getName());
                    param.setPrecision(((SavotParam) resourceParamset.getItemAt(i)).getPrecision());
                    param.setRef(((SavotParam) resourceParamset.getItemAt(i)).getRef());
                    //param.setType(((SavotParam) paramset.getItemAt(i)).getType());
                    param.setUcd(((SavotParam) resourceParamset.getItemAt(i)).getUcd());
                    param.setUnit(((SavotParam) resourceParamset.getItemAt(i)).getUnit());
                    param.setUtype(((SavotParam) resourceParamset.getItemAt(i)).getUtype());
                    param.setValue(((SavotParam) resourceParamset.getItemAt(i)).getValue());
                    param.setWidth(((SavotParam) resourceParamset.getItemAt(i)).getWidth());
                    //param.setXtype(((SavotParam) paramset.getItemAt(i)).)

                    resourceParams.add(param);

                }





                // for each table of the current resource
                for (int m = 0; m < currentResource.getTableCount(); m++) {


                    /////////////////////////////////////////
                    /////// PROCESS PARAMS FOR THIS TABLE ////
                    //put all params into a single vector,
                    //so it can be applied to all the entries found in the table
                    /////////////////////////////////////////

                    Vector<VOTableEntryComponent> tableParams = new Vector();

                    ParamSet tableParamset = ((SavotTable) currentResource.getTables().getItemAt(m)).getParams();
                    GroupSet groups = ((SavotTable) currentResource.getTables().getItemAt(m)).getGroups();

                    for (int i = 0; i < groups.getItemCount(); i++) {
                        ParamSet groupParams = ((ParamSet) ((SavotGroup) groups.getItemAt(i)).getParams());
                        for (int j = 0; j < groupParams.getItemCount(); j++) {
                            tableParamset.addItem(groupParams.getItemAt(j));
                        }
                    }

                    for (int i = 0; i < tableParamset.getItemCount(); i++) {
                        VOTableEntryComponent param = new VOTableEntryComponent();

                        param.setArraysize(((SavotParam) tableParamset.getItemAt(i)).getArraySize());
                        param.setDatatype(((SavotParam) tableParamset.getItemAt(i)).getDataType());
                        param.setID(((SavotParam) tableParamset.getItemAt(i)).getId());
                        param.setName(((SavotParam) tableParamset.getItemAt(i)).getName());
                        param.setPrecision(((SavotParam) tableParamset.getItemAt(i)).getPrecision());
                        param.setRef(((SavotParam) tableParamset.getItemAt(i)).getRef());
                        //param.setType(((SavotParam) paramset.getItemAt(i)).getType());
                        param.setUcd(((SavotParam) tableParamset.getItemAt(i)).getUcd());
                        param.setUnit(((SavotParam) tableParamset.getItemAt(i)).getUnit());
                        param.setUtype(((SavotParam) tableParamset.getItemAt(i)).getUtype());
                        param.setValue(((SavotParam) tableParamset.getItemAt(i)).getValue());
                        param.setWidth(((SavotParam) tableParamset.getItemAt(i)).getWidth());
                        //param.setXtype(((SavotParam) paramset.getItemAt(i)).)

                        tableParams.add(param);

                    }


                    ////////////////////////////////////////
                    ////// PROCCESS INFO IN <FIELD> TAGS ///
                    ////////////////////////////////////////


                    TRSet tr = currentResource.getTRSet(m);
                    int number_field = currentResource.getFieldSet(m).getItemCount();
                    String[] arraysize = new String[number_field];
                    String[] datatype = new String[number_field];
                    String[] id = new String[number_field];
                    String[] name = new String[number_field];
                    String[] precision = new String[number_field];
                    String[] ref = new String[number_field];
                    String[] type = new String[number_field];
                    String[] ucd = new String[number_field];
                    String[] unit = new String[number_field];
                    String[] utype = new String[number_field];
                    String[] width = new String[number_field];


                    for (int g = 0; g < number_field; g++) {

                        SavotField currentField = (SavotField) (currentResource.getFieldSet(m).getItemAt(g));

                        arraysize[g] = currentField.getArraySize();
                        datatype[g] = currentField.getDataType();
                        id[g] = currentField.getId();
                        name[g] = currentField.getName();
                        precision[g] = currentField.getPrecision();
                        ref[g] = currentField.getRef();
                        type[g] = currentField.getType();
                        ucd[g] = currentField.getUcd();
                        unit[g] = currentField.getUnit();
                        utype[g] = currentField.getUtype();
                        width[g] = currentField.getWidth();

                    }

                    // for each row (entry)

                    for (int k = 0; k < tr.getItemCount(); k++) {

                        VOTableEntry entry = new VOTableEntry();

                        entry.components.addAll(resourceParams);

                        entry.components.addAll(tableParams);

                        // get all the data of the row
                        TDSet theTDs = tr.getTDSet(k);

                        // for each data of the row
                        for (int j = 0; j < theTDs.getItemCount(); j++) {

                            VOTableEntryComponent component = new VOTableEntryComponent();

                            component.setArraysize(arraysize[j]);
                            component.setDatatype(datatype[j]);
                            component.setID(id[j]);
                            component.setName(name[j]);
                            component.setPrecision(precision[j]);
                            component.setRef(ref[j]);
                            //component.setType(param.getType());
                            component.setUcd(ucd[j]);
                            component.setUnit(unit[j]);
                            component.setUtype(utype[j]);

                            String itemCount = theTDs.getContent(j);
                            component.setValue(itemCount);

                            component.setWidth(width[j]);
                            //param.setXtype(((SavotParam) paramset.getItemAt(i)).)

                            entry.components.add(component);

                        }

                        entriesReturn.add(entry);


                    }
                    
             
                    //If the TABLE is empty add one entry with its parameters

                    if (tr.getItemCount() == 0) {

                        VOTableEntry entry = new VOTableEntry();
                        entry.components.addAll(resourceParams);
                        entry.components.addAll(tableParams);
                        entriesReturn.add(entry);

                    }


                }


                //If the RESOURCE is empty add one entry with the parameters

                if (entriesReturn.size() == 0) {

                    VOTableEntry entry = new VOTableEntry();
                    entry.components.addAll(resourceParams);
                    entriesReturn.add(entry);

                }


            }

        }




        return entriesReturn;

    }

}
