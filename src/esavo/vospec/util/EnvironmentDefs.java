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

import esavo.vospec.dataingestion.VOSpecServer;
import java.io.*;


/**
 * A <code>EnvironmentDefs</code> object has got<code> static</code> attributes which define the
 * general environment variables for the AIOSPEC. Project specific environment variables will
 * be attached in each of the Project objects 
 *   @version 0.1
 *   @author Isa Barbarisi
 */
public class EnvironmentDefs implements Serializable {


    
    
    private EnvironmentDefs(){
    }

    private static String SERVERHOST = "esavo.esac.esa.int";
    private static String SERVERNAME = "AioSpecServer";
    private static String SERVERPORT = "80";
    private static String RMIPORT = "31099";
    private static String RMIPORT_DYNAMIC_MIN = "31098";
    private static String RMIPORT_DYNAMIC_MAX = "31098";
    private static String SSAPURL = "http://esavo.esac.esa.int/vospec/conf/SSAPVOTable.xml";
    private static String TSAURL = "http://esavo.esac.esa.int/vospec/conf/TsaListExample.xml";
    private static String SLAPURL = "http://esavo.esac.esa.int/vospec/conf/SlapListExample.xml";
    private static String REGISTRYESAVOURL = "http://registry.euro-vo.org/services/RegistrySearch";
    private static String REGISTRYNVOURL = "http://nvo.stsci.edu/vor10/NVORegInt.asmx";
    private static String REGISTRYNVOURLMIRROR = "http://nvo.stsci.edu/vor10/NVORegInt.asmx";
    private static String SSAPPARAMETERS = "http://esavo.esac.esa.int/vospec/conf/SSAPVersion.xml";
    private static String VERSION = "6.6";
    private static VOSpecServer SERVER;
    
       
    public static void setServerHost(String serverHost) {
        setSERVERHOST(serverHost);
    }
    
    public static void setServerPort(String serverPort) {
        setSERVERPORT(serverPort);
    }

    public static void setServerName(String serverName) {
        setSERVERNAME(serverName);
    }
    
    public static void setRMIPort(String RMIPort) {
        setRMIPORT(RMIPort);
    }

    public static void setRMIPortDynamicMin(String RMIPortDynamicMin) {
        setRMIPORT_DYNAMIC_MIN(RMIPortDynamicMin);
    }

    public static void setRMIPortDynamicMax(String RMIPortDynamicMax) {
        setRMIPORT_DYNAMIC_MAX(RMIPortDynamicMax);
    }
    
    public static void setRegistryEsavoUrl(String registryEsavoUrl) {
        setREGISTRYESAVOURL(registryEsavoUrl);
    }
    
    public static void setRegistryNvoUrl(String registryNvoUrl) {
        setREGISTRYNVOURL(registryNvoUrl);
    }
    
    public static void setRegistryNvoUrlMirror(String registryNvoUrlMirror) {
        setREGISTRYNVOURLMIRROR(registryNvoUrlMirror);
    }
    
    public static void setServer(VOSpecServer server) {
        setSERVER(server);
    }

    public static void setVersion(String version) {
        setVERSION(version);
    }
    
    public static void setSsapUrl(String ssapUrl) {
        setSSAPURL(ssapUrl);
    } 
    
    //public static void setSsapNew(String ssapV0_97) {
    //    SSAPV0_97 = ssapV0_97;
    //}
    
    public static void setTsaUrl(String tsaUrl) {
        setTSAURL(tsaUrl);
    }  
    
    public static void setSlapUrl(String slapUrl) {
        setSLAPURL(slapUrl);
    }
    
    public static void setSSAPParameters(String ssapParameters) {
        setSSAPPARAMETERS(ssapParameters);
    }


        /**
     * @return the SERVERHOST
     */
    public static String getSERVERHOST() {
        return SERVERHOST;
    }

    /**
     * @param aSERVERHOST the SERVERHOST to set
     */
    public static void setSERVERHOST(String aSERVERHOST) {
        SERVERHOST = aSERVERHOST;
    }

    /**
     * @return the SERVERNAME
     */
    public static String getSERVERNAME() {
        return SERVERNAME;
    }

    /**
     * @param aSERVERNAME the SERVERNAME to set
     */
    public static void setSERVERNAME(String aSERVERNAME) {
        SERVERNAME = aSERVERNAME;
    }

    /**
     * @return the SERVERPORT
     */
    public static String getSERVERPORT() {
        return SERVERPORT;
    }

    /**
     * @param aSERVERPORT the SERVERPORT to set
     */
    public static void setSERVERPORT(String aSERVERPORT) {
        SERVERPORT = aSERVERPORT;
    }

    /**
     * @return the RMIPORT
     */
    public static String getRMIPORT() {
        return RMIPORT;
    }

    /**
     * @param aRMIPORT the RMIPORT to set
     */
    public static void setRMIPORT(String aRMIPORT) {
        RMIPORT = aRMIPORT;
    }

    /**
     * @return the RMIPORT_DYNAMIC_MIN
     */
    public static String getRMIPORT_DYNAMIC_MIN() {
        return RMIPORT_DYNAMIC_MIN;
    }

    /**
     * @param aRMIPORT_DYNAMIC_MIN the RMIPORT_DYNAMIC_MIN to set
     */
    public static void setRMIPORT_DYNAMIC_MIN(String aRMIPORT_DYNAMIC_MIN) {
        RMIPORT_DYNAMIC_MIN = aRMIPORT_DYNAMIC_MIN;
    }

    /**
     * @return the RMIPORT_DYNAMIC_MAX
     */
    public static String getRMIPORT_DYNAMIC_MAX() {
        return RMIPORT_DYNAMIC_MAX;
    }

    /**
     * @param aRMIPORT_DYNAMIC_MAX the RMIPORT_DYNAMIC_MAX to set
     */
    public static void setRMIPORT_DYNAMIC_MAX(String aRMIPORT_DYNAMIC_MAX) {
        RMIPORT_DYNAMIC_MAX = aRMIPORT_DYNAMIC_MAX;
    }

    /**
     * @return the SSAPURL
     */
    public static String getSSAPURL() {
        return SSAPURL;
    }

    /**
     * @param aSSAPURL the SSAPURL to set
     */
    public static void setSSAPURL(String aSSAPURL) {
        SSAPURL = aSSAPURL;
    }

    /**
     * @return the TSAURL
     */
    public static String getTSAURL() {
        return TSAURL;
    }

    /**
     * @param aTSAURL the TSAURL to set
     */
    public static void setTSAURL(String aTSAURL) {
        TSAURL = aTSAURL;
    }

    /**
     * @return the SLAPURL
     */
    public static String getSLAPURL() {
        return SLAPURL;
    }

    /**
     * @param aSLAPURL the SLAPURL to set
     */
    public static void setSLAPURL(String aSLAPURL) {
        SLAPURL = aSLAPURL;
    }

    /**
     * @return the REGISTRYESAVOURL
     */
    public static String getREGISTRYESAVOURL() {
        return REGISTRYESAVOURL;
    }

    /**
     * @param aREGISTRYESAVOURL the REGISTRYESAVOURL to set
     */
    public static void setREGISTRYESAVOURL(String aREGISTRYESAVOURL) {
        REGISTRYESAVOURL = aREGISTRYESAVOURL;
    }

    /**
     * @return the REGISTRYNVOURL
     */
    public static String getREGISTRYNVOURL() {
        return REGISTRYNVOURL;
    }

    /**
     * @param aREGISTRYNVOURL the REGISTRYNVOURL to set
     */
    public static void setREGISTRYNVOURL(String aREGISTRYNVOURL) {
        REGISTRYNVOURL = aREGISTRYNVOURL;
    }

    /**
     * @return the REGISTRYNVOURLMIRROR
     */
    public static String getREGISTRYNVOURLMIRROR() {
        return REGISTRYNVOURLMIRROR;
    }

    /**
     * @param aREGISTRYNVOURLMIRROR the REGISTRYNVOURLMIRROR to set
     */
    public static void setREGISTRYNVOURLMIRROR(String aREGISTRYNVOURLMIRROR) {
        REGISTRYNVOURLMIRROR = aREGISTRYNVOURLMIRROR;
    }

    /**
     * @return the SSAPPARAMETERS
     */
    public static String getSSAPPARAMETERS() {
        return SSAPPARAMETERS;
    }

    /**
     * @param aSSAPPARAMETERS the SSAPPARAMETERS to set
     */
    public static void setSSAPPARAMETERS(String aSSAPPARAMETERS) {
        SSAPPARAMETERS = aSSAPPARAMETERS;
    }

    /**
     * @return the VERSION
     */
    public static String getVERSION() {
        return VERSION;
    }

    /**
     * @param aVERSION the VERSION to set
     */
    public static void setVERSION(String aVERSION) {
        VERSION = aVERSION;
    }

    /**
     * @return the SERVER
     */
    public static VOSpecServer getSERVER() {
        return SERVER;
    }

    /**
     * @param aSERVER the SERVER to set
     */
    public static void setSERVER(VOSpecServer aSERVER) {
        SERVER = aSERVER;
    }



}
