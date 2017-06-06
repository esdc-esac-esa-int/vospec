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
package esavo.vospec.math;

// Importing VOSpec classes
import esavo.vospec.spectrum.*;



public class OrderedSpectrum extends Spectrum{

		
	private double[]	waveValues;
	private double[]	fluxValues;
	
	private Spectrum	spectrum;


/*	public OrderedSpectrum(Vector param, String url) {
	
		TsapCallRead tsapCallRead = new TsapCallRead(url, param);
		this.spectrum = tsapCallRead.tsapSpectrum();
		
		//if spectrum=null, returns wave/fluxValues = null;
		//=================================================
		evaluateWaveFluxValues();
	}
*/	
	
	public OrderedSpectrum(Spectrum inputSpectrum, Unit units) {
		super(inputSpectrum);
		
		this.spectrum = switchUnits(inputSpectrum, units);
		
		
		//if spectrum=null, returns wave/fluxValues = null;
		orderedWaveFluxValues();
	}
	
	public OrderedSpectrum(Spectrum spectrum) {
		super(spectrum);
		
		this.spectrum = spectrum;
		
		//if spectrum=null, returns wave/fluxValues = null;
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
        
               
/*       public void evaluateWaveFluxValues() {
				
		if(spectrum==null) {
			waveValues = null;
			fluxValues = null;
		}
		else {
		
			int points = ((double[])spectrum.getWaveValues()).length;
		
       	   	
				
			for(int i = 0; i < waveValuesAux.length; i++) {
				waveValuesAux[i] = Math.log(waveValuesAux[i])/Math.log(10);
				fluxValuesAux[i] = Math.log(fluxValuesAux[i])/Math.log(10);
			}
		
		
		
			System.out.println("WAVE VALUES LENGTH BEFORE = " + points);
			System.out.println("=======================================");
		
			if(points > 10000) 

				cutreWaveFluxValues();
				//waveFluxValues();	
				
			else{
				waveValues = (double[])orderedWaveFluxValues.elementAt(0);
				fluxValues = (double[])orderedWaveFluxValues.elementAt(1);
			}
	
	}
       
 */      
       	
	
       
     	
/*	public void cutreWaveFluxValues() {
	
				
		Vector orderedWaveFluxValues = (Vector) orderedWaveFluxValues(spectrum);
		
		double[] waveReal = (double[])orderedWaveFluxValues.elementAt(0);
		
		double waveMin = waveReal[0];
		double waveMax = waveReal[0];
		
		
		for(int i = 0; i < waveReal.length; i++) {
			
			if(waveReal[i] < waveMin) waveMin = waveReal[i];
			if(waveReal[i] > waveMax) waveMax = waveReal[i];			
		}
		
		System.out.println("minWaveReal " + waveMin);
		System.out.println("maxWaveReal " + waveMax);
		
		waveValues 		= new double[2000];
		fluxValues 		= new double[2000];
		
		int[] 	pointsInIntervalReal 	= new int[2000];
		
		double[] fluxReal = (double[])orderedWaveFluxValues.elementAt(1);
		
		double thisRangeWaveMin,thisRangeWaveMax, thisRangeWaveMedium;
		double mediumFluxReal;
				
		for(int i=0; i < fluxValues.length; i++) {
			
			pointsInIntervalReal[i] = 0;
			
			thisRangeWaveMax = (waveMax - waveMin) * (i + 0.5)/ 2000 + waveMin;
			thisRangeWaveMin = (waveMax - waveMin) * (i - 0.5)/ 2000 + waveMin;
			thisRangeWaveMedium = (waveMax - waveMin) * i / 2000 + waveMin;
			
			mediumFluxReal = 0;
			
						
			for(int k = 0; k < fluxReal.length; k++) {
				if(waveReal[k] >= thisRangeWaveMin && waveReal[k] < thisRangeWaveMax) {
					
					mediumFluxReal = mediumFluxReal + fluxReal[k];
					pointsInIntervalReal[i] = pointsInIntervalReal[i] + 1;	
				}
			}
			
			if(pointsInIntervalReal[i] > 0) mediumFluxReal = mediumFluxReal/pointsInIntervalReal[i];
			
			fluxValues[i] = mediumFluxReal;
			waveValues[i] = thisRangeWaveMedium;
		}

		

	}
	
			
	public void waveFluxValues() {
			//numOfSubintervals ~ numOfIntervals*numOfIntervals
			//32 = Math.round(sqrt(1000))
			int numOfIntervals = 32;
			
			Vector orderedWaveFluxValues = (Vector) orderedWaveFluxValues(spectrum);
		
			double[] waveValuesAux = (double[])orderedWaveFluxValues.elementAt(0);
			double[] fluxValuesAux = (double[])orderedWaveFluxValues.elementAt(1);
						
			double xMin = waveValuesAux[0];
			double xMax = waveValuesAux[waveValuesAux.length-1];
		
			Vector waveValuesVector = new Vector();
			Vector fluxValuesVector = new Vector();
			
			for(int k=0; k < numOfIntervals; k++) {
            
	    			double waveMax = (k+1) * (xMax - xMin)/numOfIntervals + xMin;          
	    			double waveMin = k* (xMax - xMin)/numOfIntervals + xMin;  

	    			int pointsInThisInterval = 0;	
	    	
	    			if(k==numOfIntervals-1) {
					for(int i=0; i < waveValuesAux.length; i++) 
	    					if(waveValuesAux[i]<=waveMax & waveValuesAux[i]>=waveMin) 
							pointsInThisInterval++;
				}			
				else {
					for(int i=0; i < waveValuesAux.length; i++) 
	    					if(waveValuesAux[i]<waveMax & waveValuesAux[i]>=waveMin) 
							pointsInThisInterval++;
				}
            			if(pointsInThisInterval > 0) {
			
					int numOfSubIntervals = (int) Math.round(waveValuesAux.length/pointsInThisInterval);
				
					for(int j=0; j < numOfSubIntervals; j++) {					
						
						int pointsInThisSubInterval = 0;
					
						double waveMinSubInterval = j* (waveMax - waveMin)/numOfSubIntervals + waveMin;  
						double waveMedium = (j+0.5) * (waveMax - waveMin)/numOfSubIntervals + waveMin; 
						double waveMaxSubInterval = (j+1) * (waveMax - waveMin)/numOfSubIntervals + waveMin;          
	    					
						double fluxMedium = 0;
					
						if(k==numOfIntervals-1) {
							if(j==numOfSubIntervals-1) {
								for(int i=0; i < waveValuesAux.length; i++) 
									if(waveValuesAux[i]<=waveMaxSubInterval & waveValuesAux[i]>=waveMinSubInterval) {
										fluxMedium = fluxMedium + fluxValuesAux[i];
										pointsInThisSubInterval++;
									}
							}
							else
								for(int i=0; i < waveValuesAux.length; i++) 
									if(waveValuesAux[i]<waveMaxSubInterval & waveValuesAux[i]>=waveMinSubInterval) {
										fluxMedium = fluxMedium + fluxValuesAux[i];
										pointsInThisSubInterval++;
									}
						}			
						else {
							for(int i=0; i < waveValuesAux.length; i++) 
								if(waveValuesAux[i]<waveMaxSubInterval & waveValuesAux[i]>=waveMinSubInterval) {
									fluxMedium = fluxMedium + fluxValuesAux[i];
									pointsInThisSubInterval++;
								}
						}
						
						
						if(pointsInThisSubInterval > 0) {
							fluxMedium = fluxMedium/pointsInThisSubInterval;
							Double waveMediumDouble = new Double(waveMedium); 
							Double fluxMediumDouble = new Double(fluxMedium);
							waveValuesVector.add(waveMediumDouble);
							fluxValuesVector.add(fluxMediumDouble);
						}
            				}//for j

	    			}//pointsInThisInterval	
        		}//for k
		
			System.out.println("WAVE VALUES VECTOR SIZE = " + waveValuesVector.size());
			waveValues = new double[waveValuesVector.size()];
			fluxValues = new double[waveValuesVector.size()];
			
			for(int i = 0; i < waveValuesVector.size(); i++) {
				Double waveValueDouble = (Double) waveValuesVector.elementAt(i);
				Double fluxValueDouble = (Double) fluxValuesVector.elementAt(i);
			
				double waveValue = waveValueDouble.doubleValue();
				double fluxValue = fluxValueDouble.doubleValue();
				
				waveValues[i] = waveValue;
				fluxValues[i] = fluxValue;
				//waveValues[i] = Math.pow(10, waveValue);
				//fluxValues[i] = Math.pow(10, fluxValue);
				
			}
	}
	
	
	
*/	
	
}
