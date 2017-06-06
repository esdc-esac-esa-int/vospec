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
package esavo.vospec.util;

import java.util.*;
import javax.xml.parsers.*;
import org.xml.sax.*;


/** Parses the Environment Definitions defined in the xml input file
*   "aioSpecEnvironmentDefs.xml"
**/


public class ParseEnvironment extends HandlerBase {
   static String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";

   protected Properties serverProperties;

/** The constructor receives a(n XML) filename and parses it
*/
  public ParseEnvironment(String fileName) throws Exception {
    SAXParserFactory parserFact = SAXParserFactory.newInstance();
    parserFact.setValidating(true);
    org.xml.sax.Parser parser = parserFact.newSAXParser().getParser();
    parser.setDocumentHandler(this);
    parser.setErrorHandler(this);

    serverProperties = new Properties();

    parser.parse(fileName);
  }


  /** This data member handles the incoming xml values. */
  private StringBuffer buffer = null;

/**Standard parser method
*/
  public void startDocument() {};


/**Standard parser method
*/
  public void characters(char ch[], int start, int length) {
    buffer.append(ch, start, length);
  }


/**Standard parser method
*/
  public void startElement(String s, AttributeList attributelist) {

    if(s.equals("property")) {
      String  name  = attributelist.getValue("name");
      String  value = attributelist.getValue("value");

      serverProperties.put(name, value);     
    }

    buffer = new StringBuffer();
  }

/**Returns a Properties files with the just parsed info
*/
  public Properties getProperties() {
    return serverProperties;
  }

  
  public void error(SAXParseException exception) {exception.printStackTrace();}
  public void fatalError(SAXParseException exception) {exception.printStackTrace();}
  public void warning(SAXParseException exception) {exception.printStackTrace();}

}
