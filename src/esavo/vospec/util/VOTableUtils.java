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

import esavo.vospec.spectrum.*;
import java.io.*;
/**
 *
 * @author  jsalgado
 */
public class VOTableUtils {
    
    public static String FILE = "";
    
    /** Avoids Instantation */
    private VOTableUtils() {
    }
    
    public static void saveSpectrumSetInVOTable(SpectrumSet spectrumSet, File wrapperName) throws Exception {
        BufferedWriter out = new BufferedWriter(new FileWriter(wrapperName));
        createHeader(out);
        
        for(int i=0; i < spectrumSet.getSpectrumSet().size(); i++) {
            Spectrum spectrum = spectrumSet.getSpectrum(i);
            fillData(out, spectrum);
        }
        closeFile(out);
    }
    
    public static String getStringFromFile(File tmpFile){
        try {
            BufferedReader in = new BufferedReader(new FileReader(tmpFile));
            String str;
            while ((str = in.readLine()) != null) {
                
                FILE = FILE + str.trim();
            }
            in.close();
        } catch (IOException e) {
        }
        return FILE;
    }
    
    public static void createHeader(BufferedWriter out)  throws Exception {
        
        out.write("<?xml version=\"1.0\"?>\n");
        out.write("<!DOCTYPE VOTABLE SYSTEM \"http://us-vo.org/xml/VOTable.dtd\">\n");
        out.write("<VOTABLE>\n");
        out.write("<DESCRIPTION>\n");
        out.write("VOSpec Wrapper Creator\n");
        out.write("</DESCRIPTION>\n");
        out.write("<RESOURCE>\n");
        out.write("<TABLE>\n");
        out.write("<FIELD ID=\"Title\" ucd=\"VOX:Image_Title\" datatype=\"char\" arraysize=\"*\" />\n");
        out.write("<FIELD ID=\"RA\" ucd=\"POS_EQ_RA_MAIN\" datatype=\"double\" unit=\"deg\" />\n");
        out.write("<FIELD ID=\"DEC\" ucd=\"POS_EQ_DEC_MAIN\" datatype=\"double\" unit=\"deg\" />\n");
        out.write("<FIELD ID=\"AXES\" ucd=\"VOX:Spectrum_axes\" datatype=\"char\" arraysize=\"*\"/>\n");
        out.write("<FIELD ID=\"UNITS\" ucd=\"VOX:Spectrum_units\" datatype=\"char\" arraysize=\"*\" />\n");
        out.write("<FIELD ID=\"DIMEQ\" ucd=\"VOX:Spectrum_dimeq\" datatype=\"char\" arraysize=\"*\" />\n");
        out.write("<FIELD ID=\"SCALEQ\" ucd=\"VOX:Spectrum_scaleq\" datatype=\"char\" arraysize=\"*\" />\n");
        out.write("<FIELD ID=\"FORMAT\" ucd=\"VOX:Spectrum_format\" datatype=\"char\" arraysize=\"*\" />\n");
        out.write("<FIELD ID=\"Spectrum\" ucd=\"DATA_LINK\" datatype=\"char\" arraysize=\"*\" />\n");
        out.write("<DATA>\n");
        out.write("<TABLEDATA>\n");
    }
    
    public static void fillData(BufferedWriter out, Spectrum spectrum)  throws Exception {
        out.write("\t<TR>\n");
        out.write("\t<TD>"+spectrum.getTitle()+"</TD>\n");
        out.write("\t<TD>"+spectrum.getRa()+"</TD>\n");
        out.write("\t<TD>"+spectrum.getDec()+"</TD>\n");
        out.write("\t<TD>"+spectrum.getWaveLengthColumnName()+" "+spectrum.getFluxColumnName()+"</TD>\n");
        out.write("\t<TD>"+spectrum.getUnitsW()+" "+spectrum.getUnitsF()+"</TD>\n");
        out.write("\t<TD>"+spectrum.getDimeQWave()+" "+spectrum.getDimeQ()+"</TD>\n");
        out.write("\t<TD>"+spectrum.getWaveFactor()+" "+spectrum.getFluxFactor()+"</TD>\n");
        out.write("\t<TD>" + spectrum.getFormat() + "</TD>\n");
        out.write("\t<TD><![CDATA["+spectrum.getUrl()+"]]></TD>\n");
        out.write("\t</TR>\n");
    }
    
    
    public static void closeFile(BufferedWriter out)  throws Exception {
        
        out.write("</TABLEDATA>\n");
        out.write("</DATA>\n");
        out.write("</TABLE>\n");
        out.write("</RESOURCE>\n");
        out.write("</VOTABLE>\n");
        out.close();
    }
    
    
}
