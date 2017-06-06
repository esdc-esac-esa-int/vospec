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

import cds.savot.model.SavotField;
import cds.savot.model.SavotResource;
import cds.savot.model.SavotVOTable;
import cds.savot.model.TDSet;
import cds.savot.model.TRSet;
import cds.savot.pull.SavotPullParser;
import esavo.vospec.slap.SlapServer;
import esavo.vospec.slap.SlapServerList;
import esavo.vospec.util.EnvironmentDefs;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 *
 * @author jgonzale
 */
public class RegistryIngester {

    public static SsaServerList getSsaServerList() {

        InputStream is;
        SsaServerList ssaServerList = new SsaServerList();

        try {

            // the XML call result
            String xmlResult = null;

            // the predicate arg for the service call
            String esavoPredicate1 = "SimpleSpectralAccess";
            String esavoPredicate2 = "ProtoSpectralAccess";
            String esavoPredicate3 = "TheoreticalSpectralAccess";
            String nvoPredicate1 = "SimpleSpectralAccess";
            String nvoPredicate2 = "ProtoSpectralAccess";
            String nvoPredicate3 = "TheoreticalSpectralAccess";

            // the url of the XSL file for the transformation
            //FIXME
            System.out.println("Connecting to host " + "http://" + EnvironmentDefs.getSERVERHOST() + ":" + EnvironmentDefs.getSERVERPORT() + "/vospec/conf/" + "ResourceExtractor.xsl");
            String xslFileUrl = "http://" + EnvironmentDefs.getSERVERHOST() + ":" + EnvironmentDefs.getSERVERPORT() + "/vospec/conf/" + "ResourceExtractor.xsl";
            try {

                //SSA

                // call the ESAVO Registry WebService
                EsavoAccess esavoAccess = new EsavoAccess();
                String queryResult = esavoAccess.callQueryResource(esavoPredicate1);
                // make the XSLT transformation
                xmlResult = esavoAccess.xsltResource2Table(queryResult, xslFileUrl, esavoPredicate1);
                is = (InputStream) new StringBufferInputStream(xmlResult);

                SavotPullParser sb = new SavotPullParser(is, 0, "UTF8");
                ssaServerList = getSsaServerListFromSavot(sb, SsaServer.SSAP);


                //PSA

                // call the ESAVO Registry WebService
                esavoAccess = new EsavoAccess();
                queryResult = esavoAccess.callQueryResource(esavoPredicate2);
                // make the XSLT transformation
                xmlResult = esavoAccess.xsltResource2Table(queryResult, xslFileUrl, esavoPredicate2);
                is = (InputStream) new StringBufferInputStream(xmlResult);

                sb = new SavotPullParser(is, 0, "UTF8");

                SsaServerList ssaServerListPsa = getSsaServerListFromSavot(sb, SsaServer.PSAP);
                
                ssaServerList.addSsaServerList(ssaServerListPsa);


                //TSA
                // call the ESAVO Registry WebService
                esavoAccess = new EsavoAccess();
                queryResult = esavoAccess.callQueryResource(esavoPredicate3);
                // make the XSLT transformation
                xmlResult = esavoAccess.xsltResource2Table(queryResult, xslFileUrl, esavoPredicate3);
                is = (InputStream) new StringBufferInputStream(xmlResult);

                sb = new SavotPullParser(is, 0, "UTF8");

                SsaServerList ssaServerListTsa = getSsaServerListFromSavot(sb, SsaServer.TSAP);
               
                ssaServerList.addSsaServerList(ssaServerListTsa);

                is.close();

                //if no results then this server is not working properly
                if (ssaServerList.ssaServerList.size() == 0) {
                    throw new Exception();
                }


            } catch (Exception e) {
                System.out.println("Problems calling Esavo... calling Nvo instead");
                try {

                    //SSA

                    // call the NVO Registry WebService
                    NvoAccess nvoAccess = new NvoAccess();
                    String queryResult = nvoAccess.callQueryResource(nvoPredicate1);
                    // make the XSLT transformation
                    xmlResult = nvoAccess.xsltResource2Table(queryResult, xslFileUrl);
                    is = (InputStream) new StringBufferInputStream(xmlResult);

                    SavotPullParser sb = new SavotPullParser(is, 0, "UTF8");
                    ssaServerList = getSsaServerListFromSavot(sb, SsaServer.SSAP);



                    //PSA
                    nvoAccess = new NvoAccess();
                    queryResult = nvoAccess.callQueryResource(nvoPredicate2);
                    // make the XSLT transformation
                    xmlResult = nvoAccess.xsltResource2Table(queryResult, xslFileUrl);
                    is = (InputStream) new StringBufferInputStream(xmlResult);

                    sb = new SavotPullParser(is, 0, "UTF8");

                    SsaServerList ssaServerListPsa = getSsaServerListFromSavot(sb, SsaServer.PSAP);

                    ssaServerList.addSsaServerList(ssaServerListPsa);

                    //TSA
                    nvoAccess = new NvoAccess();
                    queryResult = nvoAccess.callQueryResource(nvoPredicate3);
                    // make the XSLT transformation
                    xmlResult = nvoAccess.xsltResource2Table(queryResult, xslFileUrl);
                    is = (InputStream) new StringBufferInputStream(xmlResult);

                    sb = new SavotPullParser(is, 0, "UTF8");

                    SsaServerList ssaServerListTsa = getSsaServerListFromSavot(sb, SsaServer.TSAP);

                    ssaServerList.addSsaServerList(ssaServerListTsa);

                    is.close();


                } catch (Exception em) {
                    System.out.println("Problems calling Nvo... opening default file");
                    //URL url = new URL(EnvironmentDefs.SSAPURL);
                    //is = url.openStream();
                    HttpURLConnection con = (HttpURLConnection) new URL(EnvironmentDefs.getSSAPURL()).openConnection();
                    HttpURLConnection.setFollowRedirects(true);
                    is = con.getInputStream();

                    SavotPullParser sb = new SavotPullParser(is, 0, "UTF8");
                    ssaServerList = getSsaServerListFromSavot(sb, SsaServer.SSAP);

                    is.close();

                }



            }

        //v0.97 SSAP
        //URL url = new URL(EnvironmentDefs.SSAPV0_97);
        //is = url.openStream();
        //sb = new SavotPullParser(is, 0,"UTF8");
        //SsaServerList ssaServerList2 = new SsaServerList();
        //ssaServerList2 = setSsaServerFromSavot(sb,false);
        //ssaServerList.addSsaServerList(ssaServerList);
        is.close();

        } catch (IOException e) {
            System.err.println(e);
        } catch (Exception e) {
            System.err.println(e);
        }



        //TRICK for PHOTOMETRY

        
        
        SsaServer server = new SsaServer();
        server = new SsaServer();
        server.setType(SsaServer.CONE_SEARCH);
        server.setSsaUrl("http://cdsarc.u-strasbg.fr/viz-bin/test/votable/P?-phot&");
        server.setSsaName("CDS Multicatalogue Photometry Service");
        ssaServerList.addSsaServer(ssaServerList.ssaServerList.size(), server);



        server = new SsaServer();
        server.setType(SsaServer.SSA_PHOTO);
        server.setSsaUrl("http://vo.ned.ipac.caltech.edu/services/accessSED?REQUEST=queryData&");
        server.setSsaName("NED Multicatalogue Photometry Service");
        ssaServerList.addSsaServer(ssaServerList.ssaServerList.size(), server);



        return ssaServerList;
    }



/*
    public static SsaServerList getTsaServerList() {

        InputStream ts = null;

        SsaServerList tsaServerList = new SsaServerList();

        //TOCHANGE: with the correct file and address
        try {
            //URL tsaUrl = new URL(EnvironmentDefs.TSAURL);
            //System.out.println("tsaUrl " + tsaUrl);
            //ts = (InputStream) tsaUrl.openStream();

            HttpURLConnection con = (HttpURLConnection) new URL(EnvironmentDefs.TSAURL).openConnection();
            ts = con.getInputStream();
            System.out.println("tsaUrl " + con.getURL());
        } catch (Exception e) {
            System.out.println("Problems calling Tsa List xmlFile... opening default file");
        }

        SavotPullParser tb = new SavotPullParser(ts, 0, "UTF8");
        tsaServerList = getSsaServerListFromSavot(tb, true);

        return tsaServerList;
    }
*/




    public static SlapServerList getSlapServerList() {

        InputStream ts = null;

        SlapServerList slapServerList = new SlapServerList();

        try {
            //URL slapUrl = new URL(EnvironmentDefs.SLAPURL);
            //ts = (InputStream) slapUrl.openStream();
            HttpURLConnection con = (HttpURLConnection) new URL(EnvironmentDefs.getSLAPURL()).openConnection();
            HttpURLConnection.setFollowRedirects(true);
            ts = con.getInputStream();
        } catch (Exception e) {
            System.out.println("Problems calling Slap List xmlFile...");
        }


        SavotPullParser tb = new SavotPullParser(ts, 0, "UTF8");
        slapServerList = getSlapServerFromSavot(tb);

        try {
            ts.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return slapServerList;
    }




    private static SlapServerList getSlapServerFromSavot(SavotPullParser sb) {

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
     * Converts a VOTable into a ServerList
     * @param sb
     * @param isTsa
     * @return
     */

    private static SsaServerList getSsaServerListFromSavot(SavotPullParser sb, int ssaServerType) {

        SsaServerList ssaServerList = new SsaServerList();

        // get the VOTable object
        SavotVOTable sv = sb.getVOTable();

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

                    // for each row (each server)
                    for (int k = 0; k < tr.getItemCount(); k++) {

                        SsaServer ssaServer = new SsaServer();

                        // get all the data of the row
                        TDSet theTDs = tr.getTDSet(k);

                        // for each data of the row
                        for (int j = 0; j < theTDs.getItemCount(); j++) {
                            String itemCount = theTDs.getContent(j);
                            //TOCHANGE: if standard protocol changes and use an external file
                            // parse Name
                            if (ucd[j].indexOf("Title") > -1) {
                                ssaServer.setSsaName(itemCount);
                            }

                            // parse Image Url
                            if (ucd[j].indexOf("AccessReference") > -1 || ucd[j].indexOf("DATA_LINK") > -1) {
                                ssaServer.setSsaUrl(itemCount);
                            }

                            // parse Type
                            //System.out.println(ucd[j]);
                            if (ucd[j].indexOf("TYPE") > -1) {
                                if(itemCount.toUpperCase().contains("SIMULATION")){
                                    ssaServer.setType(SsaServer.TSAP);
                                }else{
                                    ssaServer.setType(ssaServerType);
                                }
                            }
                            /*
                            if (isTsa) {
                                ssaServer.setTsa(true);
                            } else {
                                ssaServer.setTsa(false);
                            }
                            */
                        }
                        ssaServer.setSelected(false);



                        ssaServerList.addSsaServer(k, ssaServer);


                    }//end for k


                }//end for m

            }//end if

        }//end for l

        return ssaServerList;

    }










}
