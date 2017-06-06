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
package esavo.vospec.slap;

import esavo.vospec.main.VOSpecDetached;
import esavo.vospec.spectrum.SpectrumConverter;
import esavo.vospec.spectrum.Unit;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author  jsalgado
 */

public class SlapUtils {

    public static VOSpecDetached   AIOSPECTOOLDETACHED = null;
    public static SLAPViewer            SLAPVIEWER = null;

    /** Forbid instantiation */
    private  SlapUtils() {}

    public static void setAioSpecToolDetached(VOSpecDetached aioSpecToolDetached) {
        AIOSPECTOOLDETACHED = aioSpecToolDetached;
    }

   public static void slapViewer(SlapRequest slapRequest) {
    
    	double min = slapRequest.getW1();
    	double max = slapRequest.getW2();
	
	String originalWave = AIOSPECTOOLDETACHED.getWaveChoice();
	String originalFlux = AIOSPECTOOLDETACHED.getFluxChoice();
	
	min = ((new SpectrumConverter()).convertPoint(min, 0., new Unit(originalWave,originalFlux), new Unit("m","Jy")))[0];
	max = ((new SpectrumConverter()).convertPoint(max, 0., new Unit(originalWave,originalFlux), new Unit("m","Jy")))[0];
	 	
	slapRequest.setW1(min);
	slapRequest.setW2(max);
	
    	if(SLAPVIEWER == null) SLAPVIEWER = new SLAPViewer(AIOSPECTOOLDETACHED);
	SLAPVIEWER.setSlapRequest(slapRequest);
	SLAPVIEWER.setVisible(true);
	AIOSPECTOOLDETACHED.setDefaultCursor();
    }
  
    
    public static void setSLAPViewer(SLAPViewer slapViewer) {
        SLAPVIEWER = slapViewer;
    }
    
    public static void markTableOnSlap(double x) {
	
	String originalWave = AIOSPECTOOLDETACHED.getWaveChoice();
	String originalFlux = AIOSPECTOOLDETACHED.getFluxChoice();
	
	x = ((new SpectrumConverter()).convertPoint(x, 0., new Unit(originalWave,originalFlux), new Unit("m","Jy")))[0];
	
    	if(SLAPVIEWER != null)
		if(SLAPVIEWER.isVisible()) SLAPVIEWER.markTable(x);
	
    }   
    
    
    public static void drawLineOnPlot(double newValue,String identification,boolean definitive) {
    
	String originalWave = AIOSPECTOOLDETACHED.getWaveChoice();
	String originalFlux = AIOSPECTOOLDETACHED.getFluxChoice();
	
	newValue = ((new SpectrumConverter()).convertPoint(newValue, 0., new Unit("m","Jy"), new Unit(originalWave,originalFlux)))[0];
   
    	AIOSPECTOOLDETACHED.plot.drawLine(newValue,identification,definitive);	
    }	
    
    
    
    public static DefaultTableModel getTableModel(LineSet lineSet) throws Exception {
    
            DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(lineSet.fields,0);
                        
            Line line;
            if (lineSet.size()!=0) {
                    for(int j = 0 ; j<lineSet.size() ; j++) {

                            line = lineSet.getLine(j);
                            
                            Object[] objectArray = new Object[line.fields.size()];                     
                            for(int i=0; i < line.fields.size(); i++) {
                                String field = (String) line.fields.elementAt(i);
                                objectArray[i] = line.getValue(field);
                            }
                            tableModel.addRow(objectArray);
                    }
            } else {
                throw new Exception("No results");
            }

            return tableModel;
        }   

}
