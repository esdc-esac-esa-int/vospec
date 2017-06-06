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

import esavo.utils.units.parser.UnitEquation;
import esavo.utils.units.parser.UnitEquationFactory;
import esavo.vospec.main.*;
import esavo.vospec.spectrum.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import javax.swing.*;

/**
 *
 * @author  jsalgado
 */
public class Utils {
     

    public VOSpecDetached AIOSPECTOOLDETACHED = null;
    
    public static String FITS_TYPE 		= "spectrum/fits";
    public static String VOTABLE_TYPE 		= "spectrum/votable";
    public static String PHOTOMETRY_TYPE 	= "spectrum/photometry";
    public static String FITS1D_TYPE    = "spectrum/fits1D";
    public static String SED_TYPE    = "spectrum/SED";
    
    public HashSet runningSpectra        = new HashSet();
    public HashSet runningServer         = new HashSet();
        
    public static double x = 0;
    public static double y = 0;

    public static boolean exitMan = false;
    
    
    public void Util(){        
    }
    
    public void setAioSpecToolDetached(VOSpecDetached aioSpecToolDetached) {

        AIOSPECTOOLDETACHED = aioSpecToolDetached;
        runningSpectra = new HashSet();
        runningServer  = new HashSet();
    }
      
    
    public static void rmiPropertiesDefinition(Properties environmentDefs) {
        EnvironmentDefs.setServerHost(environmentDefs.getProperty("SERVERHOST"));
        EnvironmentDefs.setRMIPort(environmentDefs.getProperty("RMIPORT"));
        EnvironmentDefs.setServerName(environmentDefs.getProperty("SERVERNAME"));
    }
    
    public static void propertiesDefinition(Properties environmentDefs) {
        
        ParseEnvironment environment = null;
        Properties props = null;
        
        if(environmentDefs == null){
            
            try {
                environment = new ParseEnvironment("http://esavo.esac.esa.int/vospec/conf/aioSpecEnvironmentDefs.xml");
                props = environment.getProperties();
            } catch (Exception e) {
                e.printStackTrace();
            }
            environmentDefs = environment.getProperties();
        }
        
        EnvironmentDefs.setServerHost(environmentDefs.getProperty("SERVERHOST"));
        EnvironmentDefs.setServerName(environmentDefs.getProperty("SERVERNAME"));
        EnvironmentDefs.setRMIPort(environmentDefs.getProperty("RMIPORT"));
        EnvironmentDefs.setServerPort(environmentDefs.getProperty("SERVERPORT"));
        System.out.println("setting server port to " + environmentDefs.getProperty("SERVERPORT"));
        EnvironmentDefs.setRMIPortDynamicMin(environmentDefs.getProperty("RMIPORT_DYNAMIC_MIN"));
        EnvironmentDefs.setRMIPortDynamicMax(environmentDefs.getProperty("RMIPORT_DYNAMIC_MAX"));
        EnvironmentDefs.setRegistryEsavoUrl(environmentDefs.getProperty("REGISTRYESAVOURL"));
        EnvironmentDefs.setRegistryNvoUrl(environmentDefs.getProperty("REGISTRYNVOURL"));
        EnvironmentDefs.setRegistryNvoUrlMirror(environmentDefs.getProperty("REGISTRYNVOURLMIRROR"));
        EnvironmentDefs.setVersion(environmentDefs.getProperty("VERSION"));
        EnvironmentDefs.setSsapUrl(environmentDefs.getProperty("SSAPURL"));
        EnvironmentDefs.setTsaUrl(environmentDefs.getProperty("TSAURL"));
        EnvironmentDefs.setSlapUrl(environmentDefs.getProperty("SLAPURL"));
        
    }
    
    public static Vector getColumns(String urlString, String type) throws Exception {




        urlString = "file:"+Cache.getFile(urlString);


        Vector columnsVector = new Vector();

        if (type.equals(Utils.FITS_TYPE)) {

            columnsVector = FitsSpectrum.getColumnsNameAndUnits(urlString);

        } else if (type.equals(Utils.FITS1D_TYPE)) {

            columnsVector = Fits1DSpectrum.getColumnsNameAndUnits(urlString);

        } else if (type.equals(Utils.SED_TYPE)) {

            columnsVector = SedSpectrum.getColumnsNameAndUnits(urlString);

        } else if (type.equals(Utils.VOTABLE_TYPE)) {

            columnsVector = VoTableSpectrum.getColumnsNameAndUnits(urlString);

        }

        return columnsVector;
    }

/** Rounding of double numbers **/
    public static String roundDouble(double initialDouble, int decimalDigits) {

	String roundedDoubleString = "";
	String pattern = "#.";
	
	try {	
		// Creating the pattern	
		for(int i=0; i<decimalDigits; i++) {
			pattern = pattern + "#";
		}
	
                pattern = pattern + "E0";
		DecimalFormat decimalFormat = new DecimalFormat(pattern);
	    	roundedDoubleString = decimalFormat.format((new Double(initialDouble)).doubleValue());	
		
	} catch(Exception e) {
		
		roundedDoubleString = "";
	
	}  
    
    	return roundedDoubleString;
    }
    
    public static boolean isCompressed(String pathFile) {
    	try {
		(new GZIPInputStream(new FileInputStream(new File(pathFile)))).read();
		return true;
	} catch (Exception e) {
		return false;
	}
    }
 
    public static void createHeader(File localFile,String wave,String flux) {
               
        try {
                    //adding row to file cache    
                    BufferedWriter out = new BufferedWriter(new FileWriter(localFile, false));
                    out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                    out.write("  <VOTABLE version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.ivoa.net/xml/VOTable/v1.1 http://www.ivoa.net/xml/VOTable/v1.1\" xmlns:spec=\"http://www.ivoa.net/xml/SpectrumModel/v1.01\" xmlns=\"http://www.ivoa.net/xml/VOTable/v1.1\">\n");
                    out.write("       <DESCRIPTION>\n");
                    out.write("          European Science Astronomy Centre - Created by VOSpec: http://esavo.esa.int/vospec\n"); 
                    out.write("       </DESCRIPTION>\n");
                    out.write("       <RESOURCE>\n");
                    out.write("          <TABLE name=\"tabel1\"  >\n");
                    out.write("            <FIELD utype=\"spec:Data.SpectralAxis.Value\" unit=\""+wave+"\"  datatype=\"float\"  precision=\"6\"  width=\"10\"  name=\"WAVE\"  />\n");
                    out.write("            <FIELD utype=\"spec:Data.FluxAxis.Value\" unit=\""+flux+"\"  datatype=\"float\"  precision=\"8\"  width=\"20\"  name=\"FLUX\"  />\n");
                    out.write("               <DATA>\n");
                    out.write("                <TABLEDATA>\n");
                    out.close();

              }catch (Exception e){
                    System.out.println(e);
              }    
        }
        
        public static void fillData(File localFile,Vector data){
                        
            try {
                        
                BufferedWriter out = new BufferedWriter(new FileWriter(localFile, true));
                for(int i=0;i<data.size();i++){
                 
                    double[] value = (double[]) data.elementAt(i);
        
                        //adding row to file cache    
                        out.write("                     <TR>\n");
                        out.write("                         <TD>"+value[0]+"</TD>\n");
                        out.write("                         <TD>"+value[1]+"</TD>\n");
                        out.write("                     </TR>\n");
                }       
                
                out.close();
           
            } catch (Exception e){
              
                System.out.println(e);
            }  
        }
        
        public static void closeFile(File localFile) {
            try {
                    //adding row to file cache    
                    BufferedWriter out = new BufferedWriter(new FileWriter(localFile, true));
                    out.write("                  </TABLEDATA>\n");
                    out.write("                 </DATA>\n");
                    out.write("              </TABLE>\n");
                    out.write("         </RESOURCE>\n");
                    out.write("   </VOTABLE>\n");
                    out.close();

              }catch (Exception e){
                    System.out.println(e);
              }    
        }
        
 
           public ExtendedTableModel refreshTableModel(SpectrumSet spectrumSet,String description) {
    
            ExtendedTableModel tableModel = AIOSPECTOOLDETACHED.tableModel;
            
            if (spectrumSet.spectrumSet.size()!=0) {
                    for(int j = 0 ; j<spectrumSet.spectrumSet.size() ; j++) {
                            Spectrum spectrum = new Spectrum();
                            spectrum = spectrumSet.getSpectrum(j);
                            String s2 = spectrum.getTitle();
                            String s3 = spectrum.getRa();
                            String s4 = spectrum.getDec();
                            String s5 = spectrum.getFormat();
                            String urlSpectrum = spectrum.getUrl();
                            String formatSpectrum = spectrum.getFormat();
                            tableModel.addRow(new Object[]{null,description,s2, s3, s4, s5, null, "ready"});
                    }
            }else {
                    //this.tableModel.addRow(new Object[]{null,description,"","","No results","",null,""});
                   JOptionPane.showMessageDialog(null,"No results for "+description);
            }

            return tableModel;
        }
        
           
        
        public void setTableModel(ExtendedTableModel tableModel) {
            
            AIOSPECTOOLDETACHED.tableModel = tableModel;
        }
        


      public synchronized void problemsInSpectrumConverter(int i) {

        //removeRunningSpectrum(AIOSPECTOOLDETACHED.spectrumSet.getSpectrum(i));
        //System.out.println("setting failed "+AIOSPECTOOLDETACHED.spectrumSet.getSpectrum(i).getTitle());

        AIOSPECTOOLDETACHED.spectrumSet.getSpectrum(i).getNode().setFailed(true);

        //AIOSPECTOOLDETACHED.refreshJTree();
    }
        
        
        

	
	public double getCoordinateValue(String coordinate) throws Exception {


		boolean decType = true;
		if(coordinate.indexOf("h") != -1) decType = false;


		StringTokenizer st = new StringTokenizer(coordinate," +-dhms':\"");

		double sign = 1.;
		if(coordinate.startsWith("-")) sign = -1.;

		double result = 0.;
		if(st.countTokens() == 1) {
			result = (new Double(coordinate)).doubleValue();
		} else if(st.countTokens() > 1) {

			double token1 = 0.;
			double token2 = 0.;
			double token3 = 0.;

			if(st.hasMoreElements()) token1 = (new Double((String) st.nextElement())).doubleValue();
			if(st.hasMoreElements()) token2 = (new Double((String) st.nextElement())).doubleValue();
			if(st.hasMoreElements()) token3 = (new Double((String) st.nextElement())).doubleValue();

			if(decType) 	result = token1 + token2/60. + token3/3600.;
			else 		result = (token1 + token2/60. + token3/3600.) * 360./24.;

			result = result * sign;

		}

		return result;

	}
	
	public static Spectrum setMetadata(Spectrum spectrum) {
            
            int ct = 0;
            if(spectrum.getTitle() != null) 
                if(!spectrum.getTitle().equals("")) 
                    spectrum.addMetaData(ct++ + "","Image_Title:\t" + spectrum.getTitle());
            
            if(spectrum.getUrl() != null) 
                if(! spectrum.getUrl().equals("")) 
                    spectrum.addMetaData(ct++ + "","DATA_LINK:\t" + spectrum.getUrl());
	   
            if(spectrum.getWaveLengthColumnName() != null && spectrum.getFluxColumnName() != null)
                if(!spectrum.getWaveLengthColumnName().equals("") && !spectrum.getFluxColumnName().equals(""))
                    spectrum.addMetaData(ct++ + "","AXES:\t" + spectrum.getWaveLengthColumnName() + " " + spectrum.getFluxColumnName());
            
            if(spectrum.getWaveFactor() != null && spectrum.getFluxFactor() != null)
                if(!spectrum.getWaveFactor().equals("") && !spectrum.getFluxFactor().equals(""))
                    spectrum.addMetaData(ct++ + "","SCALEQ:\t" + spectrum.getWaveFactor() + " " + spectrum.getFluxFactor());
                    
            if(spectrum.getDimeQWave() != null && spectrum.getDimeQ() != null)
                if(!spectrum.getDimeQWave().equals("") && !spectrum.getDimeQ().equals(""))
                    spectrum.addMetaData(ct++ + "","DIMEQ:\t" + spectrum.getDimeQWave() + " " + spectrum.getDimeQ());
            
            if(spectrum.getFormat() != null)
                if(!spectrum.getFormat().equals(""))
                    spectrum.addMetaData(ct++ + "","FORMAT:\t" + spectrum.getFormat());
            
            
            return spectrum;
	}
        
        /*
        public synchronized boolean addRunningSpectrum(Spectrum spectrum) {

            AIOSPECTOOLDETACHED.setWaitCursor();
            return runningSpectra.add(spectrum.getUrl());
        }
        
        public synchronized void removeRunningSpectrum(Spectrum spectrum) {

            runningSpectra.remove(spectrum.getUrl());
            if(runningSpectra.size() == 0) {
                AIOSPECTOOLDETACHED.setDefaultCursor();
            }   
        }

        public synchronized void removeAllRunningSpectra() {
            runningSpectra = new HashSet();
            AIOSPECTOOLDETACHED.setDefaultCursor();
        }    
        
        public synchronized boolean addRunningServer(SsaServer server) {

            AIOSPECTOOLDETACHED.setWaitCursor();
            return runningServer.add(server.getSsaUrl());
        }
        
        public synchronized void removeRunningServer(String serverUrl) {

            runningServer.remove(serverUrl);
            if(runningServer.size() == 0) {
                AIOSPECTOOLDETACHED.setDefaultCursor();
            }   
        }

        public synchronized void removeAllRunningServer() {
            runningServer = new HashSet();
            AIOSPECTOOLDETACHED.setDefaultCursor();
        }    */
        
        public void debug(String message) {
            System.out.println(message);
            try{
                Thread.sleep(2000);
            }catch(Exception e){
                
            }
        }

    public static String[] getDimensionalEquation(String unitString) {

        //System.out.println("getDimensionalEquation(" + unitString + ")");
        String[] result = null;
        UnitEquation equation;
        UnitEquationFactory factory = new UnitEquationFactory();
        try{
            equation = (UnitEquation) factory.resolveEquation(UnitFilter.filterUnit(unitString));
            result = new String[2];
            result[0] = equation.getScaleEq();
            result[1] = equation.getDimeEq();
        }catch(Throwable t){

        }


        return result;
    }

    public static void setExitMan(boolean isStandalone){
        exitMan = isStandalone;
    }

    public static boolean getExitMan(){
        return exitMan;
    }
        
        
}
