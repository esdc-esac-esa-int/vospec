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
package esavo.vospec.spectrum;

import java.util.*;

public class FittingSpectrum extends Spectrum {

	Vector dataToFit; 
        Spectrum spectrum;
	
	public FittingSpectrum() {
		super();
		dataToFit = new Vector();
		setRealData(false);
 	}

	public FittingSpectrum(Spectrum  spectrum) {
		//super(spectrum);
		//dataToFit = new Vector();
                this();
                this.spectrum = spectrum;
                dataToFit = dataToFit(spectrum);
		setRealData(false);
 	}

	public FittingSpectrum(Vector dataToFit) {
		this();
		this.dataToFit = dataToFit;
		setRealData(false);
	}


     /*********************************************************************
	/**
   	* NAME: calculateData()
   	*
   	* PURPOSE: 
	*
	* Operations to be done for this class of Spectrum. In this case, we need
	* to calculate the points of the interpolation formula
	* 
	* 
   	* INPUT PARAMETERS:  none
	*
	*
   	*
   	* OUTPUT PARAMETERS: None.
   	*
   	* RETURN VALUE:  none
   	*
	* 
	*/
	public void calculateData() {
		if(toWait)calculatePoints();
	}


        // This is to be implemented on the selected child class    
	public void calculatePoints() {
	
	} 
        
        public Vector dataToFit(Spectrum spectrum){
        Vector dataToFit = new Vector();
        
        double[] waveValues = spectrum.getWaveValues();
        double[] fluxValues = spectrum.getFluxValues();
            
            for (int index = 0; index < waveValues.length; index++) {
                               
                    double[] point = new double[2];
                    
                    point[0] = waveValues[index];
                    point[1] = fluxValues[index];
                    
                    dataToFit.add(point);
                }
        return dataToFit;
    }
}
