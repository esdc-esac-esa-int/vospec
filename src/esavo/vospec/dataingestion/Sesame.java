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

/**
 * Name resolver : Resolves names in Simbad, VizieR and/or NED
 * Based in CDS Sesame resolver 1.0
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import javax.xml.rpc.ParameterMode;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;


public class Sesame {

    /**
     * A local method which contains all the code to access the remote resolving method
     * @param name a name to resolve
     * @return a result which has to be parsed to extract informations
     */
    public static String[] getNameResolved(String name){

        String ret = null;



        ////////////////////////////////////
        // First Option - SOAP mirror one //
        ////////////////////////////////////

        try {
            // URL corresponding to the XML Web Service which will be called
            String endpoint = "http://cdsws.u-strasbg.fr/axis/services/Sesame";

            // Name of the method which will be invoked
            String method = "sesame";

            // Creates a Service object
            Service service = new Service();

            // Creates a Call object
            Call call = (Call) service.createCall();

            // Initialization of the call with the parameters
            call.setTargetEndpointAddress(new java.net.URL(endpoint));
            call.setOperationName(method);
            call.addParameter("op1", XMLType.XSD_STRING, ParameterMode.IN);
            call.setReturnType(XMLType.XSD_STRING);
            call.setTimeout(new Integer(5000));
            call.setProperty(Call.CONNECTION_TIMEOUT_PROPERTY, 2000);

            // Invokes the remote method and gets a result
            ret = (String) call.invoke(new Object[]{name});

            if(ret==null) throw new Exception("Error on 1");
            System.out.println("Resolving object " + name + " using SOAP call to CDS mirror");



        /////////////////////////////////////
        // Second Option - SOAP mirror two //
        /////////////////////////////////////

        } catch (Exception e) {
            try {
                // URL corresponding to the XML Web Service which will be called
                String endpoint = "http://vizier.cfa.harvard.edu:8080/axis/services/Sesame";

                // Name of the method which will be invoked
                String method = "sesame";

                // Creates a Service object
                Service service = new Service();

                // Creates a Call object
                Call call = (Call) service.createCall();

                // Initialization of the call with the parameters
                call.setTargetEndpointAddress(new java.net.URL(endpoint));
                call.setOperationName(method);
                call.addParameter("op1", XMLType.XSD_STRING, ParameterMode.IN);
                call.setReturnType(XMLType.XSD_STRING);
                call.setTimeout(new Integer(5000));
                call.setProperty(Call.CONNECTION_TIMEOUT_PROPERTY, 2000);

                // Invokes the remote method and gets a result
                ret = (String) call.invoke(new Object[]{name});

                if(ret==null) throw new Exception("Error on 2");
                System.out.println("Resolving object " + name + " using SOAP call to ADS mirror");


            //////////////////////////////////////
            // Third Option - SOAP mirror three //
            //////////////////////////////////////

            } catch (Exception ex3) {
                try {
                    // URL corresponding to the XML Web Service which will be called
                    String endpoint = "http://vizier.nao.ac.jp:8080/axis/services/Sesame";

                    /// Name of the method which will be invoked
                    String method = "sesame";

                    // Creates a Service object
                    Service service = new Service();

                    // Creates a Call object
                    Call call = (Call) service.createCall();

                    // Initialization of the call with the parameters
                    call.setTargetEndpointAddress(new java.net.URL(endpoint));
                    call.setOperationName(method);
                    call.addParameter("op1", XMLType.XSD_STRING, ParameterMode.IN);
                    call.setReturnType(XMLType.XSD_STRING);
                    call.setTimeout(new Integer(5000));
                    call.setProperty(Call.CONNECTION_TIMEOUT_PROPERTY, 2000);

                    // Invokes the remote method and gets a result
                    ret = (String) call.invoke(new Object[]{name});
                    if(ret==null) throw new Exception("Error on 3");
                    System.out.println("Resolving object " + name + " using SOAP call to ADAC mirror");


                //////////////////////////////////////
                // Fourth Option - SOAP mirror four //
                //////////////////////////////////////

                } catch (Exception ex4) {
                    try {
                        // URL corresponding to the XML Web Service which will be called
                        String endpoint = "http://vizier.hia.nrc.ca:8080/axis/services/Sesame";

                        /// Name of the method which will be invoked
                        String method = "sesame";

                        // Creates a Service object
                        Service service = new Service();

                        // Creates a Call object
                        Call call = (Call) service.createCall();

                        // Initialization of the call with the parameters
                        call.setTargetEndpointAddress(new java.net.URL(endpoint));
                        call.setOperationName(method);
                        call.addParameter("op1", XMLType.XSD_STRING, ParameterMode.IN);
                        call.setReturnType(XMLType.XSD_STRING);
                        call.setTimeout(new Integer(5000));
                        call.setProperty(Call.CONNECTION_TIMEOUT_PROPERTY, 2000);

                        // Invokes the remote method and gets a result
                        ret = (String) call.invoke(new Object[]{name});
                        if(ret==null) throw new Exception("Error on 4");
                        System.out.println("Resolving object " + name + " using SOAP call to CADC mirror");


                        

                    ///////////////////////////////////
                    // Fifth option - CGI mirror one //
                    ///////////////////////////////////

                    } catch (Exception ex) {
                        try {
                            HttpURLConnection con = (HttpURLConnection) new URL("http://cdsweb.u-strasbg.fr/cgi-bin/nph-sesame/-oI?" + name).openConnection();
                            HttpURLConnection.setFollowRedirects(true);
                            con.setInstanceFollowRedirects(true);
                            ret = convertStreamToString(con.getInputStream());
                            if(ret==null) throw new Exception("Error on 5");




                        ////////////////////////////////////
                        // Sixth option - CGI mirror two ///
                        ////////////////////////////////////


                        } catch (Exception ex1) {
                            try {
                                HttpURLConnection con = (HttpURLConnection) new URL("http://vizier.cfa.harvard.edu/viz-vin/nph-sesame/-oI?" + name).openConnection();
                                HttpURLConnection.setFollowRedirects(true);
                                con.setInstanceFollowRedirects(true);
                                ret = convertStreamToString(con.getInputStream());
                                if(ret==null) throw new Exception("Error on 6");
                                ex1.printStackTrace();





                            } catch (Exception ex2) {
                                System.out.println("No name servers available for "+name);
                            }
                        }

                    }
                }
            }
        }

        String[] coordinatesArray = null;

        int index = ret.indexOf("%J");
        if (index < 0) {
            return coordinatesArray;
        }

        String coordinates = ret.substring(index, ret.length());
        StringTokenizer st = new StringTokenizer(coordinates, " ");

        String first = st.nextToken();
        String second = st.nextToken();
        String third = st.nextToken();

        coordinatesArray = new String[2];
        coordinatesArray[0] = (new String(second)).toString();
        coordinatesArray[1] = (new String(third)).toString();

        return coordinatesArray;
    }



    private static String convertStreamToString(InputStream is) throws IOException {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }



    public static void main(String[] args) throws Exception {


        // Calls the the resolving method with the command line argument
        String[] result = Sesame.getNameResolved(args[0]);
        if (result != null) {
            System.out.println("The result is ra: " + result[0] + "dec: " + result[1]);
        } else {
            System.out.println("Sorry but no result found ");
        }
    }
}


