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

import esavo.vospec.util.*;
import java.io.*;
import java.util.Vector;
import javax.xml.namespace.QName;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPBodyElement;


/**
 * ESAVO Registry WebServices Access Class
 * @author Aurelien STEBE
 * @version 5.0
 */
public class EsavoAccess {
    /**
     * creates a new instance of EsavoAccess
     */
    public EsavoAccess() {
    }

    /**
     * get the Resources from the ESAVO Registry
     * @param predicate the predicate parameter for the query
     * @return the XML resources returned by the service
     * @throws Exception throws back any exception
     */
    public String callQueryResource(String predicate) throws Exception {
        
        QName portName = new QName("http://esavo.esa.int/wsdl/searchableRegistry.wsdl", "RegistrySearchPortSOAP");
        QName queryOp = new QName("http://esavo.esa.int/wsdl/searchableRegistry.wsdl", "XPathQLSearch");
        
        // the return value of the service
        String xmlResult = null;

        try {
        	// create the service call
        	Service service = new Service();
        	Call call = (Call) service.createCall();

        	// set the endpoint and operation
        	call.setTargetEndpointAddress(EnvironmentDefs.getREGISTRYESAVOURL());
        	call.setOperation(portName, queryOp);
            call.setTimeout(3000);

        	// prepare the input parameter
        	SOAPBodyElement[] input = new SOAPBodyElement[1];
        	input[0] = new SOAPBodyElement(queryOp);
        	input[0].addChildElement("xPathQL").addTextNode("#ServiceType# = '" + predicate + "'");

        	// make the call and get the output
        	Vector outputVect = (Vector) call.invoke(input);
        	SOAPBodyElement outputElem = (SOAPBodyElement) outputVect.get(0);
        	Object result = outputElem.getChildElements().next();

        	// return the result
        	xmlResult = ((MessageElement) result).getAsString();
                
        } catch (Exception e) {
            throw e;
        }

        // return the XML value
        return xmlResult;
    }

     /**
      * transforms the Resources into VOTable
      * @param voResource the VOResource XML string
      * @param xslFileUrl the XSL file direction
      * @return the XML resources in a VOTable
      * @throws Exception throws back any exception
      */
    public String xsltResource2Table(String voResource, String xslFileUrl, String predicate)
            throws Exception {
        // the result of the transformation
        String xmlResult = null;

        try {
            // prepare the transformation
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer(new StreamSource(xslFileUrl));
            transformer.setParameter("serviceType", predicate);
            StringWriter voTable = new StringWriter();

            // apply the transformation
            transformer.transform(new StreamSource(new StringReader(voResource)),new StreamResult(voTable));
            xmlResult = voTable.toString();
        } catch (Exception e) {
            throw e;
        }

        // return the XML value
        return xmlResult;
    }

   /**
     * the main method
     * @param args the command line args
     */
    public static void main(String[] args) {
        // the XML call result
        String xmlResult = null;

        // the predicate arg for the service call
        String predicate = "SimpleSpectrumAccess";

        // the url of the XSL file for the transformation
        String xslFileUrl = "http://"+EnvironmentDefs.getSERVERHOST()+":"+EnvironmentDefs.getSERVERPORT()+"/vospec/conf/"+"ResourceExtractor.xsl";

        try {
            // call the ESAVO Registry WebServices
            EsavoAccess esavoAccess = new EsavoAccess();
            String queryResult = esavoAccess.callQueryResource(predicate);

            // make the XSLT transformation
            xmlResult = esavoAccess.xsltResource2Table(queryResult, xslFileUrl, predicate);
        } catch (Exception e) {
            System.out.println("main: " + e);
        }

        // print result to terminal
        System.out.println(xmlResult);
    }

}
