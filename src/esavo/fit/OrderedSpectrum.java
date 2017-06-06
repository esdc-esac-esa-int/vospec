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
package esavo.fit;



import esavo.vospec.spectrum.Spectrum;
import esavo.vospec.spectrum.SpectrumConverter;
import esavo.vospec.spectrum.Unit;




/**
*Methods getWaveValues() and getFluxValues() of this class return
*ordered waveValues and ordered fluxValues unlike class Spectrum 
*/

public class OrderedSpectrum extends Spectrum{

		
	private double[]	waveValues;
	private double[]	fluxValues;
	
	private Spectrum	spectrum;
	
	
	public OrderedSpectrum(Spectrum inputSpectrum, Unit units) {
		
		this.spectrum = switchUnits(inputSpectrum, units);
		
		orderedWaveFluxValues();
	}
	
	public OrderedSpectrum(Spectrum spectrum) {
		super(spectrum);
		
		this.spectrum = spectrum;
		
		orderedWaveFluxValues();
	}
	
	public Spectrum switchUnits(Spectrum spectrumToConvert, Unit finalUnits) {
	
		Spectrum spectrumConverted = null;
		
		try {
			spectrumConverted = (new SpectrumConverter()).convertSpectrum(spectrumToConvert, finalUnits);
		} catch (Exception e) {
			e.printStackTrace();
		}
	return spectrumConverted;
	}
	
		
		
	
	public void orderedWaveFluxValues() {

        	
		if(spectrum==null) {
			//if spectrum=null, returns wave/fluxValues = null;
			waveValues = null;
			fluxValues = null;
		}else{
		
			waveValues = spectrum.getWaveValues();
			fluxValues = spectrum.getFluxValues();
        	
			//logarithmic scale
/*			for(int i = 0; i < waveValuesAux.length; i++) {
				waveValuesAux[i] = Math.log(waveValuesAux[i])/Math.log(10);
				fluxValuesAux[i] = Math.log(fluxValuesAux[i])/Math.log(10);
			}
		
*/			for(int i=0; i < waveValues.length; i++) {
               		
				double xElement1 = waveValues[i];
				double yElement1 = fluxValues[i];
			
				for(int j = i+1; j < waveValues.length; j++) {
				
					double xElement2 = waveValues[j];
					double yElement2 = fluxValues[j];
				
					if(xElement2 < xElement1) {
						waveValues[i] = xElement2;	
						waveValues[j] = xElement1;
					
						fluxValues[i] = yElement2;
						fluxValues[j] = yElement1;
					
						xElement1 = xElement2;
						yElement1 = yElement2;
					}
				}//j 
	    		}//i
		
       		}//else
       }
       
       public double[] getWaveValues(){
		return waveValues;
	}
	
	public double[] getFluxValues(){
		return fluxValues;
	}
	
	
}
