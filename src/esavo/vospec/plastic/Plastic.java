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
package esavo.vospec.plastic;

import esavo.utils.units.parser.*;
import esavo.vospec.main.*;
import esavo.vospec.spectrum.*;
import esavo.vospec.util.EnvironmentDefs;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.JOptionPane;
import org.votech.plastic.PlasticHubListener;
import org.votech.plastic.PlasticListener;
import uk.ac.starlink.plastic.PlasticUtils;


/**
 *
 * @author  ibarbarisi
 */

public class Plastic implements PlasticListener{
    
    public String LOAD                      = "ivo://votech.org/votable/load";
    public String LOAD_FROM_URL             = "ivo://votech.org/votable/loadFromURL";
    public String LOAD_SPECTRUM_FROM_URL    = "ivo://votech.org/spectrum/loadFromURL";
    public String LOAD_SPECTRUM             = "ivo://votech.org/spectrum/load";
    public String LOAD_LINE                 = "ivo://votech.org/fits/line/loadFromURL";
    
    public String GET_DESCRIPTION           = "ivo://votech.org/info/getDescription";
    public String GET_ICON_URL              = "ivo://votech.org/info/getIconURL";
    public String GET_VERSION               = "ivo://votech.org/info/getVersion";
    
    public String HUB_STOP                  = "ivo://votech.org/hub/event/HubStopping";
    public String APPLICATION_REGISTERED    = "ivo://votech.org/hub/event/ApplicationRegistered";
    public String APPLICATION_UNREGISTERED  = "ivo://votech.org/hub/event/ApplicationUnregistered";
    public String SHOW_OBJECTS              = "ivo://votech.org/votable/showObjects";
    
    public String LOAD_SPECTRUM_FROM_VOESPACE = "ivo://votech.org/spectrum/loadFromVOEspace";
    
    public URI id = null;
    public PlasticHubListener  hub;
    
    public VOSpecDetached AIOSPECTOOLDETACHED;
    public String version     = "";
    public URL icon           = null;
    public String description = "";
    
    boolean isSpectrumLoadWithoutHashmap   = false;
    
    public List listTmp = null;
    
    /** Creates a new instance of MyPlasticListener */
    //public Plastic(AioSpecToolDetached aiospecToolDetached) throws java.rmi.RemoteException{
    public Plastic(VOSpecDetached aiospecToolDetached) {
        
        this.AIOSPECTOOLDETACHED = aiospecToolDetached;
        getLocalHub();
        id = registerRMI();

    }
    
    /** Get PLASTIC identifier */
    public URI getId() {
        return this.id;
    }
    
    /** Get VOSpec version */
    public String getVersion() {
        return "VOSpecv2.5";
    }
    
    /** Get VOSpec description */
    public String getDescription() {
        return "VOSpec";
    }
    
    /** Get VOSpec icon */
    public URL getIconURL() {
        try{
            icon = new URL("http://"+EnvironmentDefs.getSERVERHOST()+":"+EnvironmentDefs.getSERVERPORT()+"/vospec/images/vologo.gif");
            
        }catch (Exception e){
            System.out.println("Exception "+e);
        }
        return icon;
    }
    
    
    public String getName(URI id) {
        if(!isConnected()) return null;
        else return hub.getName(id);
    }
    
    /** Get list of registered PLASTIC client ids */
    public List getRegisteredIds() {
        if(!isConnected()) return null;
        else return hub.getRegisteredIds();
    }
    
    /** Get list of registered PLASTIC client ids  */
    public List getRegisteredIds(boolean skipMyself) {
        if(!isConnected()) return null;
        if(skipMyself) {
            List ids = new ArrayList();
            Iterator it = getRegisteredIds().iterator();
            URI value = null;
            while(it.hasNext()) {
                value = (URI)it.next();
                if(!value.equals(getId())) ids.add(value);
            }
            return ids;
        } else {
            return getRegisteredIds();
        }
    }
    
    /** Get list of registered PLASTIC client names */
    public List getRegisteredNames() {
        if(!isConnected()) return null;
        
        List names = new ArrayList();
        Iterator it = getRegisteredIds().iterator();
        while(it.hasNext()) {
            names.add(hub.getName((URI)it.next()));
        }
        return names;
    }
    
    /** Get list of registered PLASTIC client names */
    public List getRegisteredNames(boolean skipMyself) {
        if(!isConnected()) return null;
        
        if(skipMyself) {
            List names = new ArrayList();
            Iterator it = getRegisteredNames().iterator();
            String name = null;
            while(it.hasNext()) {
                name = (String)it.next();
                if(!name.equals(getName(this.id))) names.add(name);
            }
            return names;
        } else {
            return getRegisteredNames();
        }
    }
    
    /** Get list of registered PLASTIC clients supporting a given message
     * @param message PLASTIC message
     */
    public List getRegistered(URI message) {
        if(!isConnected()) return null;
        
        List list = new ArrayList();
        URI idApp = null;       

        Iterator it = getRegisteredIds().iterator();
        while(it.hasNext()) {
            idApp = (URI)it.next();
            if(hub.getMessageRegisteredIds(idApp).contains(message))
                list.add(idApp);
        }
        return list;
    }
    
    
    public boolean getLocalHub() {
        try {
            hub = PlasticUtils.getLocalHub();
            System.out.println("Connected to PLASTIC Hub. ");
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            //JOptionPane.showMessageDialog(this.AIOSPECTOOLDETACHED,"Unable to connect to PLASTIC Hub");
            this.AIOSPECTOOLDETACHED.plasticButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/hubBroken.gif")));
            this.AIOSPECTOOLDETACHED.plasticButton.setToolTipText("Register with the Plastic Hub");
            
            return false;
        }
    }
    
    public boolean isConnected() {
        return !(hub == null);
    }
    
    public boolean isRegistered() {
        if(isConnected() && this.id!= null)
            return hub.getRegisteredIds().contains(this.id);
        else return false;
    }
    
    public boolean isMsgSupported(URI id, URI message) {
        if(!isConnected()) return false;
        return hub.getUnderstoodMessages(id).contains(message);
    }
    
    public boolean isLoadVOTableSupported(URI id) {
        boolean value = false;
        try {
            value = isMsgSupported(id, new URI(LOAD));
        }catch (URISyntaxException e) {
            System.out.println(e.getMessage());
        }
        return value;
    }
    
    public URI register() {
        if(!isConnected()) return null;
        this.id = hub.registerNoCallBack("VOSpec");
        return this.id;
    }
    
    public void unregister() {
        
        if(isConnected() && this.id!= null) hub.unregister(this.id);
       
    }
    
    public void sendVOTable(String votable, boolean async) {
        try {
            URI msgId = new URI(LOAD);
            List params = new ArrayList();
            params.add(votable);
            params.add("ID");
            if(async) hub.requestAsynch(getId(),msgId,params);
            else hub.request(getId(),msgId,params);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void sendVOTableToTopcat(List subset, String votable, boolean async) {
        try {
            URI msgId = new URI(LOAD);
            List params = new ArrayList();
            params.add(votable);
            if(async) hub.requestToSubsetAsynch(getId(),msgId,params,subset);
            else hub.requestToSubset(getId(),msgId,params, subset);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void sendVOTableToSubSet(List subset, String votable, boolean async) {
        try {
            URI msgId = new URI(LOAD);
            List params = new ArrayList();
            params.add(votable);
            params.add("VOSpec");
            if(async) hub.requestToSubsetAsynch(getId(),msgId,params,subset);
            else hub.requestToSubset(getId(),msgId,params, subset);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    //map has to stay in third position
    public void sendSpectra(HashMap map, boolean async) {
        try {
            URI msgId = new URI(LOAD_SPECTRUM_FROM_URL);
            List params = new ArrayList();
            params.add(getId().toString());
            params.add(msgId.toString());
            params.add(map);
            
            if(async) hub.requestAsynch(getId(),msgId,params);
            else hub.request(getId(),msgId,params);
            
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    
    
    public void sendPoints(String id, int[] points,boolean async){
        try {
            URI msgId = new URI(SHOW_OBJECTS);
            List params = new ArrayList();
            params.add(id);
            params.add(points);
            if(async) hub.requestAsynch(getId(),msgId,params);
            else hub.request(getId(),msgId,params);
            
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    
    //map has to stay in third position
    public void sendSpectraToSubset(List subset, HashMap map, boolean async) {
        try {
            URI msgId = new URI(LOAD_SPECTRUM_FROM_URL);
            List params = new ArrayList();
            params.add(getId().toString());
            params.add(msgId.toString());
            params.add(map);
            if(async) hub.requestToSubsetAsynch(getId(),msgId,params,subset);
            else hub.requestToSubset(getId(),msgId,params, subset);
            
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    
    public URI registerRMI() {
        try{
            List msgs = new ArrayList();
            msgs = getSupportedMessages();
            this.id = hub.registerRMI("VOSpec", msgs, this);
        }catch(Exception e){
            System.out.println("Exception "+e);
        }
        return this.id;
    }
    
    
    public List getSupportedMessages() {
        List msgList = new ArrayList();
        try{
            msgList.add(new URI(HUB_STOP));
            msgList.add(new URI("ivo://votech.org/test/echo"));
            msgList.add(new URI("ivo://votech.org/info/getName"));
            msgList.add(new URI("ivo://votech.org/info/getIVORN"));
            msgList.add(new URI(GET_VERSION));
            msgList.add(new URI(GET_DESCRIPTION));
            msgList.add(new URI(GET_ICON_URL));
            msgList.add(new URI(APPLICATION_REGISTERED));
            msgList.add(new URI(APPLICATION_UNREGISTERED));
            msgList.add(new URI("ivo://votech.org/hub/event/HubStopping"));
            msgList.add(new URI("ivo://votech.org/hub/Exception"));
            msgList.add(new URI(LOAD_FROM_URL));
            msgList.add(new URI(LOAD));
            msgList.add(new URI(LOAD_LINE));
            msgList.add(new URI(LOAD_SPECTRUM_FROM_URL));
            msgList.add(new URI(LOAD_SPECTRUM));
            
            msgList.add(new URI(LOAD_SPECTRUM_FROM_VOESPACE));
            
        }catch(Exception e){
            
        }
        return  msgList;
    }
   
    
    //Example: from Topcat to VOSpec, from VOQuest to VOSpec
    public Object perform(URI sender, URI msg, List list) {
        
        System.out.println("Sender "+sender);
        System.out.println("Message "+msg);
        System.out.println("Arguments "+list);
        
        setList(list);
        
        try{
            
            /** In case hub is shout down externally VOSpec unregister */
            if (msg.toString().equals(HUB_STOP)) {
                System.out.println("Hub stopping..");
                this.AIOSPECTOOLDETACHED.putil.registerToPlastic();
            }
            
            /** In case other application register refresh the Interoperability Menu */
            if (msg.toString().equals(APPLICATION_REGISTERED)) {
                System.out.println("New Application registering...");
                this.AIOSPECTOOLDETACHED.putil.checkApplicationRegistered();
            }
            
            /** In case other application unregister refresh the Interoperability Menu */
            if (msg.toString().equals(APPLICATION_UNREGISTERED)) {
                System.out.println("Application unregistering...");
                //this.AIOSPECTOOLDETACHED.sendVOTable.removeAll();
                this.AIOSPECTOOLDETACHED.sendSpectrum.removeAll();
                
                this.AIOSPECTOOLDETACHED.putil.checkApplicationRegistered();
            }
            
            /** Get VOSpec description */
            if(msg.toString().equals(GET_DESCRIPTION)){
                return "Tool for Processing Spectra";
            }
            
            /** Get VOSpec Icon */
            if(msg.toString().equals(GET_ICON_URL)){
                return "http://"+EnvironmentDefs.getSERVERHOST()+":"+EnvironmentDefs.getSERVERPORT()+"/vospec/images/vologo.gif";
            }
            /** Get VOSpec version */
            if(msg.toString().equals(GET_VERSION)){
                return "2.5";
            }
            
            /** Load VOTable from URL */
            if (msg.toString().equals(LOAD_FROM_URL)){
                System.out.println("Getting votable "+LOAD_FROM_URL);
                for(int i=0;i<list.size();i++){
                    String file = (String)list.get(i);
                    //!! AIOSPECTOOLDETACHED.localDataDialog.addNewDirectly(file,"spectrum "+sender);
                    this.AIOSPECTOOLDETACHED.localDataDialog.addSpectrumSetFromURL(file);
                    this.AIOSPECTOOLDETACHED.localDataDialog.setSelectedForAll(false);
                    this.AIOSPECTOOLDETACHED.localDataDialog.addLocalData();     
                }
            }
            
            /** Load VOTable */
            if (msg.toString().equals(LOAD)){
                
                System.out.println("Getting votable "+LOAD);
                
                String file = (String)list.get(0);
                
                // Create temp file.
                //File temp = File.createTempFile((String)list.get(1), null);
                File temp = File.createTempFile("tmpVOTable", null);
                // Delete temp file when program exits.
                temp.deleteOnExit();
                
                // Write to temp file
                BufferedWriter out = new BufferedWriter(new FileWriter(temp));
                out.write(file);
                out.close();
                
                String tmpFileAddress = temp.toURI().toString();
                
                System.out.println("tmpFileAddress "+ tmpFileAddress);
                
                //!! AIOSPECTOOLDETACHED.localDataDialog.addNewDirectly(tmpFileAddress,""+sender.toString());
                this.AIOSPECTOOLDETACHED.localDataDialog.addSpectrumSetFromURL(tmpFileAddress);
                this.AIOSPECTOOLDETACHED.localDataDialog.setSelectedForAll(false);
                this.AIOSPECTOOLDETACHED.localDataDialog.addLocalData();
                
            }
            
           
            if (msg.toString().equals(LOAD_SPECTRUM)){
                System.out.println("Getting votable "+LOAD_SPECTRUM);
                
                String file = (String)list.get(0);
                
                // Create temp file.
                //File temp = File.createTempFile((String)list.get(1), null);
                File temp = File.createTempFile("tmpVOTable", null);
                // Delete temp file when program exits.
                temp.deleteOnExit();
                
                // Write to temp file
                BufferedWriter out = new BufferedWriter(new FileWriter(temp));
                out.write(file);
                out.close();
                
                String tmpFileAddress = temp.toURI().toString();
                
                // We update the file in position 0 of list, so we have now a URL (the tmpFileAddress)
                // and we rename the msg so the next if is used as a normal spectrum/loadfromURL
                list.add(0,tmpFileAddress);
                
                msg = new URI(LOAD_SPECTRUM_FROM_URL);
            }
            
            if (msg.toString().equals(LOAD_LINE)){
                System.out.println("Getting line "+LOAD_LINE);
                
//                String file = (String)list.get(0);
//                
//                // Create temp file.
//                //File temp = File.createTempFile((String)list.get(1), null);
//                File temp = File.createTempFile("tmpVOTable", null);
//                // Delete temp file when program exits.
//                temp.deleteOnExit();
//                
//                // Write to temp file
//                BufferedWriter out = new BufferedWriter(new FileWriter(temp));
//                out.write(file);
//                out.close();
//                
//                String tmpFileAddress = temp.toURI().toString();
//                
//                // We update the file in position 0 of list, so we have now a URL (the tmpFileAddress)
//                // and we rename the msg so the next if is used as a normal spectrum/loadfromURL
//                list.add(0,tmpFileAddress);
                
                //msg = new URI(LOAD_SPECTRUM_FROM_URL);
                
                //new call for VirGO, before it was sending empty hashmap as well 
                this.AIOSPECTOOLDETACHED.localDataDialog.addNewDirectly((String)list.get(0),(String)list.get(0));
            }
            
            /** Load spectrum from URL */
            boolean showWindow       = false;
            boolean unitsInfo        = true;
            
            if (msg.toString().equals(LOAD_SPECTRUM_FROM_URL)){
                
                String wavColName       = "";
                String fluColName       = "";
                String waveUnits        = "";
                String fluxUnits        = "";
                String units            = "";
                
                String dqWave           = "";
                String dqFlux           = "";
                String scWave           = "";
                String scFlux           = "";
                String title            = "";
                String ra               = "";
                String dec              = "";
                String format           = "";
                String accessReference  = "";
                
                //parse hashTable (map) and set SpectrumSet
                HashMap map = new HashMap();
                Hashtable mapTable = new Hashtable();
                
                String classMap = list.getClass().toString();
                System.out.println("classMap "+classMap);
                
                if (classMap.toUpperCase().indexOf("VECTOR") > -1) {
                    try {
                        //ad hoc solution for Virgo on feb 12 2008
                        mapTable = (Hashtable) list.get(0);
                        if (mapTable.containsValue("NO_VALUE")) {
                            isSpectrumLoadWithoutHashmap = true;
                        } else {
                            isSpectrumLoadWithoutHashmap = false;
                        }
                    } catch (Exception e) {
                        //do nothing
                    }
                } else {
                    try {
                        //ad hoc solution for Igor (removed on feb 28 2008)
                        map = (HashMap) list.get(2);
                        if (map.containsValue("NO_VALUE")) {
                            isSpectrumLoadWithoutHashmap = true;
                        } else {
                            isSpectrumLoadWithoutHashmap = false;
                        }
                    } catch (Exception e) {
                        //do nothing
                    }
                }
                               
                Spectrum spectrum = new Spectrum();
 
                if(!getValueFromMap("VOX:SPECTRUM_AXES").equals("")){
                    String axes = getValueFromMap("VOX:SPECTRUM_AXES");
                    //String axes = getValueFromKey(map,"VOX:Spectrum_axes");
                    String[] ax = axes.split(" ");
                    wavColName = ax[0];
                    fluColName = ax[1];
                }else{
                    //SPLAT case
                    showWindow = true;
                }
                
                if(!getValueFromMap("VOX:SPECTRUM_UNITS").equals("")){
                    units = getValueFromMap("VOX:SPECTRUM_UNITS");
                    String[] unit  = units.split(" ");
                    String wave = unit[0];
                    String flux = unit[1];
                    if (wave.toUpperCase().equals("UNKNOWN")){
                        wave = "Angstrom";
                    }
                    if (flux.toUpperCase().equals("UNKNOWN")){
                        flux = "Jy";
                    }
                    units = wave +" "+flux;
                    waveUnits = wave;
                    fluxUnits = flux;
                }
                                
                if(!getValueFromMap("VOX:SPECTRUM_DIMEQ").equals("")){
                    
                    String specDimeq = getValueFromMap("VOX:SPECTRUM_DIMEQ");
                    String[] dq  = specDimeq.split(" ");
                    dqWave = dq[0];
                    dqFlux = dq[1];
                    
                    if(dqWave.toUpperCase().equals("UNKNOWN") || dqFlux.toUpperCase().equals("UNKNOWN")) unitsInfo = false;
                    
                } 
                
                if(!getValueFromMap("VOX:SPECTRUM_SCALEQ").equals("")){
                    
                    String specScaleq = getValueFromMap("VOX:SPECTRUM_SCALEQ");
                    String[] sc  = specScaleq.split(" ");
                    //System.out.println("SpecScaleQ "+specScaleq);
                    scWave = sc[0];
                    scFlux = sc[1];
                    
                    if(scWave.toUpperCase().equals("UNKNOWN") || scFlux.toUpperCase().equals("UNKNOWN")) unitsInfo = false;
                    
                } 
                 
                System.out.println("unitsInfo "+unitsInfo);
                System.out.println("waveUnit "+waveUnits);
                System.out.println("fluxUnits "+fluxUnits);
                System.out.println("isSpectrumLoadWithoutHashmap "+isSpectrumLoadWithoutHashmap);
                
                
                if(unitsInfo && !waveUnits.equals("")  && !fluxUnits.equals("") && !isSpectrumLoadWithoutHashmap) {
                    try {
                        String[] dimEq = getDimensionalEquation(waveUnits);
                        dqWave = dimEq[0];
                        scWave = dimEq[1];
                        System.out.println("dqWave "+dimEq[0]);
                        System.out.println("scWave "+dimEq[1]);
                        
                        
                        dimEq = getDimensionalEquation(fluxUnits);
                        dqFlux = dimEq[0];
                        scFlux = dimEq[1];
                        
                        System.out.println("dqFlux "+dimEq[0]);
                        System.out.println("scFlux "+dimEq[1]);
                        
                    } catch(Exception e) {
                        showWindow = true;
                    }
                }
                //removed on 7/Nov/2008 - if all the info is sent but not the AXES values (SPLAT case) showWindow has to be true    
                //else{
                    //showWindow = false;
                //}
                
                
               if(!getValueFromMap("VOX:SPECTRUM_FORMAT").equals("")){
                    format = getValueFromMap("VOX:SPECTRUM_FORMAT");
                }
                
                if(!getValueFromMap("VOX:IMAGE_ACCESSREFERENCE").equals("")){
                    accessReference = getValueFromMap("VOX:Image_AccessReference");
                }
                
                if(!getValueFromMap("DATA_LINK").equals("")){
                    accessReference = getValueFromMap("DATA_LINK");
                }
                              
                if(!getValueFromMap("VOX:IMAGE_TITLE").equals("")){
                    title = getValueFromMap("VOX:IMAGE_TITLE");
                }else{
                    title = (String)list.get(0);
                }
                
                if(!getValueFromMap("POS_EQ_RA_MAIN").equals("")){
                    ra  = getValueFromMap("POS_EQ_RA_MAIN");
                }
                if(!getValueFromMap("POS_EQ_DEC_MAIN").equals("")){
                    dec = getValueFromMap("POS_EQ_DEC_MAIN");
                }
               
                if(!isSpectrumLoadWithoutHashmap){
                    System.out.println("image_title "+title);
                    System.out.println("units "+units);
                    
                    String url = (String)list.get(0);
                    spectrum = new Spectrum();
                    spectrum.setUrl(accessReference);
                    spectrum.setWaveLengthColumnName(wavColName);
                    spectrum.setFluxColumnName(fluColName);
                    if(!showWindow && !isSpectrumLoadWithoutHashmap) spectrum.setUnits(new Unit(dqWave,scWave,dqFlux,scFlux));
                    //if(!showWindow) spectrum.setUnits(new Unit(dqWave,scWave,dqFlux,scFlux));
                    spectrum.setTitle(title);
                    spectrum.setRa(ra);
                    spectrum.setDec(dec);
                    spectrum.setFormat(format);
                    
                    spectrum.setMetaDataComplete(new Hashtable(map));
                }else{
                    showWindow = true;
                    String url = (String)list.get(0);
                    spectrum = new Spectrum();
                    spectrum.setUrl(url);
                    spectrum.setTitle("Local Spectrum");
                }
                
                
                if(showWindow) {
                    this.AIOSPECTOOLDETACHED.localDataDialog.addNewDirectly((String)list.get(0),spectrum.getTitle());
                } else if(spectrum.getFormat().toUpperCase().indexOf("FITS") > -1) {
                    
                    FitsSpectrum fitsSpectrum = new FitsSpectrum(spectrum);
                    fitsSpectrum.setSelected(true);
                    fitsSpectrum.setToWait(true);
                    fitsSpectrum.setUnits(new Unit(dqWave,scWave,dqFlux,scFlux));

                    this.AIOSPECTOOLDETACHED.localDataDialog.addSpectrum(fitsSpectrum);
                    this.AIOSPECTOOLDETACHED.localDataDialog.addLocalData();
                    
                    
                } else if(spectrum.getFormat().toUpperCase().indexOf("VOTABLE") > -1){
                    
                    VoTableSpectrum voTableSpectrum = new VoTableSpectrum(spectrum);
                    voTableSpectrum.setSelected(true);
                    voTableSpectrum.setToWait(true);
                    voTableSpectrum.setUnits(new Unit(dqWave,scWave,dqFlux,scFlux));
                    this.AIOSPECTOOLDETACHED.localDataDialog.addSpectrum(voTableSpectrum);
                    this.AIOSPECTOOLDETACHED.localDataDialog.addLocalData();
                    
                } else {
                    spectrum.setSelected(true);
                    spectrum.setToWait(true);
                    spectrum.setUnits(new Unit(dqWave,scWave,dqFlux,scFlux));
                    this.AIOSPECTOOLDETACHED.localDataDialog.addSpectrum(spectrum);
                    this.AIOSPECTOOLDETACHED.localDataDialog.addLocalData();
                    
                }
                
            }
            
            
            if (msg.toString().equals(LOAD_SPECTRUM_FROM_VOESPACE)){
                
                String wavColName       = "";
                String fluColName       = "";
                String waveUnits        = "";
                String fluxUnits        = "";
                String units            = "";
                
                String dqWave           = "";
                String dqFlux           = "";
                String scWave           = "";
                String scFlux           = "";
                String title            = "";
                String ra               = "";
                String dec              = "";
                String format           = "";
                String accessReference  = "";
                
                //parse hashTable (map) and set SpectrumSet
                HashMap map = new HashMap();
                Hashtable mapTable = new Hashtable();
                               
                Spectrum spectrum = new Spectrum();
 
                if(!getValueFromMap("VOX:SPECTRUM_AXES").equals("")){
                    String axes = getValueFromMap("VOX:SPECTRUM_AXES");
                    //String axes = getValueFromKey(map,"VOX:Spectrum_axes");
                    String[] ax = axes.split(" ");
                    wavColName = ax[0];
                    fluColName = ax[1];
                }else{
                    //SPLAT doesn't send AXES values
                    showWindow = true;
                }
                
                if(!getValueFromMap("VOX:SPECTRUM_UNITS").equals("")){
                    units = getValueFromMap("VOX:SPECTRUM_UNITS");
                    String[] unit  = units.split(" ");
                    String wave = unit[0];
                    String flux = unit[1];
                    if (wave.toUpperCase().equals("UNKNOWN")){
                        wave = "Angstrom";
                    }
                    if (flux.toUpperCase().equals("UNKNOWN")){
                        flux = "Jy";
                    }
                    units = wave +" "+flux;
                    waveUnits = wave;
                    fluxUnits = flux;
                }
                                
                if(!getValueFromMap("VOX:SPECTRUM_DIMEQ").equals("")){
                    
                    String specDimeq = getValueFromMap("VOX:SPECTRUM_DIMEQ");
                    String[] dq  = specDimeq.split(" ");
                    dqWave = dq[0];
                    dqFlux = dq[1];
                    
                    if(dqWave.toUpperCase().equals("UNKNOWN") || dqFlux.toUpperCase().equals("UNKNOWN")) unitsInfo = false;
                    
                } 
                
                if(!getValueFromMap("VOX:SPECTRUM_SCALEQ").equals("")){
                    
                    String specScaleq = getValueFromMap("VOX:SPECTRUM_SCALEQ");
                    String[] sc  = specScaleq.split(" ");
                    //System.out.println("SpecScaleQ "+specScaleq);
                    scWave = sc[0];
                    scFlux = sc[1];
                    
                    if(scWave.toUpperCase().equals("UNKNOWN") || scFlux.toUpperCase().equals("UNKNOWN")) unitsInfo = false;
                    
                } 
                 
                if(unitsInfo && !waveUnits.equals("")  && !fluxUnits.equals("") && !isSpectrumLoadWithoutHashmap) {
                    try {
                        String[] dimEq = getDimensionalEquation(waveUnits);
                        dqWave = dimEq[0];
                        scWave = dimEq[1];
                        
                        dimEq = getDimensionalEquation(fluxUnits);
                        dqFlux = dimEq[0];
                        scFlux = dimEq[1];
                        
                    } catch(Exception e) {
                        showWindow = true;
                    }
                }
                //removed on 7/Nov/2008 - if all the info is sent but not the AXES values (SPLAT case) showWindow has to be true    
                //else{
                    //showWindow = false;
                //}
                
                
               if(!getValueFromMap("VOX:SPECTRUM_FORMAT").equals("")){
                    format = getValueFromMap("VOX:SPECTRUM_FORMAT");
                }
                
                if(!getValueFromMap("VOX:IMAGE_ACCESSREFERENCE").equals("")){
                    accessReference = getValueFromMap("VOX:Image_AccessReference");
                }
                
                if(!getValueFromMap("DATA_LINK").equals("")){
                    accessReference = getValueFromMap("DATA_LINK");
                }
                              
                if(!getValueFromMap("VOX:IMAGE_TITLE").equals("")){
                    title = getValueFromMap("VOX:IMAGE_TITLE");
                }else{
                    title = (String)list.get(0);
                }
                
                if(!getValueFromMap("POS_EQ_RA_MAIN").equals("")){
                    ra  = getValueFromMap("POS_EQ_RA_MAIN");
                }
                if(!getValueFromMap("POS_EQ_DEC_MAIN").equals("")){
                    dec = getValueFromMap("POS_EQ_DEC_MAIN");
                }
               
                if(!isSpectrumLoadWithoutHashmap){
                    System.out.println("image_title "+title);
                    System.out.println("units "+units);
                    
                    String url = (String)list.get(0);
                    spectrum = new Spectrum();
                    spectrum.setUrl(accessReference);
                    spectrum.setWaveLengthColumnName(wavColName);
                    spectrum.setFluxColumnName(fluColName);
                    if(!showWindow && !isSpectrumLoadWithoutHashmap) spectrum.setUnits(new Unit(dqWave,scWave,dqFlux,scFlux));
                    //if(!showWindow) spectrum.setUnits(new Unit(dqWave,scWave,dqFlux,scFlux));
                    spectrum.setTitle(title);
                    spectrum.setRa(ra);
                    spectrum.setDec(dec);
                    spectrum.setFormat(format);
                    
                    spectrum.setMetaDataComplete(new Hashtable(map));
                }else{
                    showWindow = true;
                    String url = (String)list.get(0);
                    spectrum = new Spectrum();
                    spectrum.setUrl(url);
                    spectrum.setTitle("Local Spectrum");
                }
                this.AIOSPECTOOLDETACHED.openVOEspaceURI(spectrum);
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        
        return null;
    }
    
   public String getValueFromMap(String key){
       
       
       List list = getList();
       
       if(list.size() < 2) return "";
       
       Set set = null;
       String classMap = list.getClass().toString();
       
       if(classMap.toUpperCase().indexOf("VECTOR")>-1){
           
           Hashtable mapTable = new Hashtable();
           mapTable = (Hashtable)list.get(2);
           set = mapTable.keySet();
           Iterator it = set.iterator();
           while(it.hasNext()){
               String element = (String)it.next();
               if(key.equals(element.toUpperCase())){
                   String value = (String)mapTable.get(element);
                   return value;
               }
           }
       }else{
           
           HashMap map = (HashMap)list.get(2);
           set = map.keySet();
           Iterator it = set.iterator();
           while(it.hasNext()){
               String element = (String)it.next();
               if(key.equals(element.toUpperCase())){
                   String value = (String)map.get(element);
                   return value;
               }
           }
       }
 
       return "";
   } 
   
   public void setList(List list){
       this.listTmp = list;
   }
   
   public List getList(){
       return listTmp;
   }
    
    public String[] getDimensionalEquation(String unitString)  {
        
        
        String[] result = null;
        try{
            UnitEquation equation;
            
            UnitEquationFactory factory 	= new UnitEquationFactory();
            
            equation 			= (UnitEquation) factory.resolveEquation(unitString);
            result                          = new String[2];
            result[0]                       = equation.getDimeEq();
            result[1]                       = equation.getScaleEq();

        }catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(AIOSPECTOOLDETACHED, "Units sent are not correct");
        }
        return result;
    }
    
    
    
    
    
    
    
}
