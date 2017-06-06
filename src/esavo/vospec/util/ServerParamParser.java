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

/**
 *
 * @author ibarbarisi
 */
public class ServerParamParser{
    

    public void ServerParamParser(){
        
    }
    /*
    public SsaServerList parseFile(File paramInfo){
        
        SsaServerList ssaServerList = new SsaServerList();
        try {
            
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(paramInfo);
            
            // normalize text representation
            doc.getDocumentElement().normalize();
            System.out.println("Root element of the doc is " +
                    doc.getDocumentElement().getNodeName());
            
            NodeList listOfServers = doc.getElementsByTagName("server");
            int totalServers = listOfServers.getLength();
            //System.out.println("Total no of servers : " + totalServers);
            
            for(int s=0; s<listOfServers.getLength() ; s++){
                
                SsaServer server = new SsaServer();
                Node firstServerNode = listOfServers.item(s);
                if(firstServerNode.getNodeType() == Node.ELEMENT_NODE){
                    
                    Element firstServerElement = (Element)firstServerNode;
                    
                    //-------
                    NodeList firstNameList = firstServerElement.getElementsByTagName("name");
                    Element firstNameElement = (Element)firstNameList.item(0);
                    
                    NodeList textFNList = firstNameElement.getChildNodes();
                    String serverName = ((Node)textFNList.item(0)).getNodeValue().trim();
                    server.setSsaName(serverName);
                    //System.out.println("Server Name : " + serverName);
                    
                    //-------
                    NodeList lastNameList = firstServerElement.getElementsByTagName("url");
                    Element lastNameElement = (Element)lastNameList.item(0);
                    
                    NodeList textLNList = lastNameElement.getChildNodes();
                    String url = ((Node)textLNList.item(0)).getNodeValue().trim();
                    server.setSsaUrl(url);
                    //System.out.println("URL : " + url);
                    
                     //-------
                    NodeList isTsaList = firstServerElement.getElementsByTagName("isTsa");
                    Element isTsaElement = (Element)isTsaList.item(0);
                    
                    NodeList textIsTsaList = isTsaElement.getChildNodes();
                    String isTsa = ((Node)textIsTsaList.item(0)).getNodeValue().trim();
                    if(isTsa.equals("true")){
                        server.setTsa(true);
                        //System.out.println("isTsa : " + isTsa);
                    }else{
                       server.setTsa(false);
                       //System.out.println("isTsa : " + isTsa); 
                    }
                    
                    
                    server.setSelected(false);
                    //----
                    NodeList listOfParams = firstServerElement.getElementsByTagName("param");
                    int totalParams = listOfParams.getLength();
                    
                    //System.out.println("Total no of params : " + totalParams);
                    Vector inputParams = new Vector();
                    
                    if(!isTsa.equals("true")){
                        for(int i=0; i<totalParams ; i++){
                            String[] paramDesc = new String[2];
                            Node firstParamNode = listOfParams.item(i);
                            
                            if(firstParamNode.getNodeType() == Node.ELEMENT_NODE){
                                
                                Element firstParamElement = (Element)firstParamNode;

                                //-------
                                NodeList paramNameList = firstParamElement.getElementsByTagName("nameParam");
                                Element paramNameElement = (Element)paramNameList.item(0);
                                
                                NodeList textParamNameList = paramNameElement.getChildNodes();
                                String paramName = ((Node)textParamNameList.item(0)).getNodeValue().trim();
                                //System.out.println("Param Name : " +paramName);
                                paramDesc[0]= paramName;
                                
                                //-------
                                NodeList descriptionList = firstParamElement.getElementsByTagName("description");
                                Element descriptionElement = (Element)descriptionList.item(0);
                                
                                NodeList textDescriptionList = descriptionElement.getChildNodes();
                                if(textDescriptionList.item(0) != null){
                                    
                                    String desc = ((Node)textDescriptionList.item(0)).getNodeValue().trim();
                                    //System.out.println("Description : " + desc);
                                    paramDesc[1]= desc;
                                    
                                }else{
                                    paramDesc[1]= "";
                                }
                            }
                            inputParams.add(paramDesc);
                            server.setInputParams(inputParams);
                        }
                    }else{
                        for(int i=0; i<totalParams ; i++){
                            TsaServerParam paramDesc = new TsaServerParam();
                            Node firstParamNode = listOfParams.item(i);
                            
                            if(firstParamNode.getNodeType() == Node.ELEMENT_NODE){
                                
                                Element firstParamElement = (Element)firstParamNode;

                                //-------
                                NodeList paramNameList = firstParamElement.getElementsByTagName("nameParam");
                                Element paramNameElement = (Element)paramNameList.item(0);
                                
                                NodeList textParamNameList = paramNameElement.getChildNodes();
                                String paramName = ((Node)textParamNameList.item(0)).getNodeValue().trim();
                                //System.out.println("Param Name : " +paramName);
                                paramDesc.setName(paramName);
                                
                                //-------
                                NodeList descriptionList = firstParamElement.getElementsByTagName("description");
                                Element descriptionElement = (Element)descriptionList.item(0);
                                
                                NodeList textDescriptionList = descriptionElement.getChildNodes();
                                if(textDescriptionList.item(0) != null){
                                    
                                    String desc = ((Node)textDescriptionList.item(0)).getNodeValue().trim();
                                    //System.out.println("Description : " + desc);
                                    paramDesc.setDescription(desc);
                                    
                                }else{
                                    paramDesc.setDescription("");
                                }
                                
                                //-------
                                NodeList isComboList = firstParamElement.getElementsByTagName("isCombo");
                                Element isComboElement = (Element)isComboList.item(0);
                                
                                NodeList isComboDescription = isComboElement.getChildNodes();
                                if(isComboDescription.item(0) != null){
                                    
                                    String isCombo = ((Node)isComboDescription.item(0)).getNodeValue().trim();
                                    //System.out.println("isCombo : " + isCombo);
                                    if(isCombo.equals("true")){
                                        paramDesc.setIsCombo(true);
                                    }else{
                                        paramDesc.setIsCombo(false);
                                    }
                                }
                                
                                //-------
                                NodeList ucd = firstParamElement.getElementsByTagName("ucd");
                                Element ucdElement = (Element)ucd.item(0);
                                
                                NodeList ucdDescription = ucdElement.getChildNodes();
                                if(ucdDescription.item(0) != null){
                                    
                                    String ucdString = ((Node)ucdDescription.item(0)).getNodeValue().trim();
                                    //System.out.println("Ucd : " + ucdString);
                                    paramDesc.setUcd(ucdString);
                                    
                                }

                                //-------
                                NodeList utype = firstParamElement.getElementsByTagName("utype");
                                Element utypeElement = (Element)utype.item(0);

                                NodeList utypeDescription = utypeElement.getChildNodes();
                                if(utypeDescription.item(0) != null){

                                    String utypeString = ((Node)utypeDescription.item(0)).getNodeValue().trim();
                                    //System.out.println("Ucd : " + ucdString);
                                    paramDesc.setUtype(utypeString);

                                }

                                //-------
                                NodeList unit = firstParamElement.getElementsByTagName("unit");
                                Element unitElement = (Element)unit.item(0);

                                NodeList unitDescription = unitElement.getChildNodes();
                                if(unitDescription.item(0) != null){

                                    String unitString = ((Node)unitDescription.item(0)).getNodeValue().trim();
                                    //System.out.println("Ucd : " + ucdString);
                                    paramDesc.setUnit(unitString);

                                }

                                
                                //-------
                                NodeList values = firstParamElement.getElementsByTagName("values");
                                Element valuesElement = (Element)values.item(0);
                                if(valuesElement != null){
                                    NodeList valuesDescription = valuesElement.getChildNodes();
                                    if(valuesDescription.item(0) != null){
                                        
                                        String valuesString = ((Node)valuesDescription.item(0)).getNodeValue().trim();
                                        Vector value = new Vector();
                                        String[] valuesParam = valuesString.split(",");
                                        for (int j=0;j<valuesParam.length;j++){
                                            value.add(valuesParam[j]);
                                        }
                                        paramDesc.setValues(value);
                                        
                                    }
                                }
                                
                                //-------
                                NodeList selectedValue = firstParamElement.getElementsByTagName("selectedValue");
                                Element selectedValueElement = (Element)selectedValue.item(0);
                                if(selectedValueElement != null){
                                    NodeList selectedValueDescription = selectedValueElement.getChildNodes();
                                    if(selectedValueDescription.item(0) != null){
                                        
                                        String selectedValueString = ((Node)selectedValueDescription.item(0)).getNodeValue().trim();
                                        paramDesc.setUcd(selectedValueString);
                                        
                                    }
                                }
                            }
                            inputParams.add(paramDesc);
                            server.setInputParams(inputParams);
                        }
                    }

                }//end of if clause
                ssaServerList.addSsaServer(s,server);
            }//end of for loop with s var
            
            
        }catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line "
                    + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());
            
        }catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
            
        }catch (Throwable t) {
            t.printStackTrace();
        }
        //System.exit (0);
        
        return ssaServerList;
        
        
    }//end of main
    */
    
}