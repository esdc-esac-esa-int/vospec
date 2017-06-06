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
import esavo.vospec.spectrum.Unit;





public class SED extends OrderedSpectrum{

	
	public SED (Spectrum inputSpectrum, Unit units) {
		super(inputSpectrum, units);
	}
	

	/**SED sampled by models*/
	public double[] reSampledFluxValues(SamplingData samplingData) {
		
		int numberOfPoints = 3;
		
	       /*
 		* wave/fluxValues are ordered because this class inherites 
		* getWave/FluxValues method from OrderedSpectrum
		*/
		double[] waveValues = this.getWaveValues();
		double[] fluxValues = this.getFluxValues();
		
		double[] sampling = (double[]) samplingData.getSampling();
		int[] waveValuesInfo = (int[]) samplingData.getWaveValuesDistributionInfo();
		double[] reSampledFluxValues = new double[sampling.length];
		
				
		for(int i = 0; i < sampling.length; i++) {
			int k = 0;
			boolean goOn = true;
				
			while (k < waveValues.length & goOn == true) {
				
				if(waveValues[k]==sampling[i]) {
					reSampledFluxValues[i] = fluxValues[k];
					goOn = false;
				}
				if(k > 0) {
					if(waveValues[k-1] < sampling[i] & sampling[i] < waveValues[k]) {
					       /*Evaluates fluxValue for the SED related to samplinPoint 
						*as the average of some of the most approximate backward 
						*and forward points of the SED to the samplingPoint
						*/
						
						double backwardFluxMedium= 0.0;
						double forwardFluxMedium = 0.0;
							
						double[] backwardFluxValues = new double[1];
						double[] forwardFluxValues = new double[1]; 
							
						if(k >= numberOfPoints & k <= waveValues.length-numberOfPoints) {
							if(waveValuesInfo[i] == -1)	{
								backwardFluxValues = (double[]) backwardFluxValues(numberOfPoints, sampling[i]);
								
								forwardFluxValues = new double[numberOfPoints];
								for(int l = 0; l < numberOfPoints; l++)
									forwardFluxValues[l] = 0.0;
							}
							if(waveValuesInfo[i] == 1)	{
								backwardFluxValues = new double[numberOfPoints];
								for(int l = 0; l < numberOfPoints; l++)
									backwardFluxValues[l] = 0.0;
								
								forwardFluxValues = (double[]) forwardFluxValues(numberOfPoints, sampling[i]);
							}
							if(waveValuesInfo[i] == 0) {
								backwardFluxValues = (double[]) backwardFluxValues(numberOfPoints, sampling[i]);
								forwardFluxValues = (double[]) forwardFluxValues(numberOfPoints, sampling[i]);
							}
						}else {
							backwardFluxValues = (double[]) backwardFluxValues(1, sampling[i]);
							forwardFluxValues = (double[]) forwardFluxValues(1, sampling[i]);
						}
						for(int l = 0; l < backwardFluxValues.length; l++) {
							backwardFluxMedium = backwardFluxMedium + backwardFluxValues[l];
							forwardFluxMedium = forwardFluxMedium + forwardFluxValues[l];	
						}
							
						int denominator = 2*backwardFluxValues.length;
						reSampledFluxValues[i] = (backwardFluxMedium+forwardFluxMedium)/denominator; 
						
						
						goOn = false;
					}
				}
			k++;
			}// k 
			
		}// i
	return reSampledFluxValues;
	}
	

   
  /**
   * @param numberOfBackwardPoints 
   * @param samplingPoint 
   *
   * @return an array of length=numberOfBackwardPoints containing 
   *	     the fluxValues of the SED related to the most approx. 
   *	     waveValues to the samplingPoint
   */
	public double[] backwardFluxValues(int numberOfBackwardPoints, double samplingPoint) {
	
	       /*
 		*wave/fluxValues are ordered because this class inherites 
		*getWave/FluxValues method from OrderedSpectrum
		*/
		double[] waveValues = this.getWaveValues();
		double[] fluxValues = this.getFluxValues();
		
		double[] values = new double[numberOfBackwardPoints];
		
		double mostApproxBackwardValue =  waveValues[0];
		double d = samplingPoint - mostApproxBackwardValue;
			
		int i = 1;
		int mostApproxIndex = 0;
		
		while(i < waveValues.length & d >= 0) {
			if(samplingPoint - waveValues[i] < d) {
				mostApproxBackwardValue = waveValues[i];
				d = samplingPoint - mostApproxBackwardValue;
				
				mostApproxIndex = i;
			}
		i++;
		}
		
		for(int k = 0; k < numberOfBackwardPoints; k++) 
			values[k] = fluxValues[mostApproxIndex - k];
	return values;
	}
	
	 
  /**
   * @param numberOfForwardPoints 
   * @param samplingPoint 
   *
   * @return an array of length=numberOfForwardPoints containing 
   *	     the fluxValues of the SED related to the most approx. 
   *	     waveValues to the samplingPoint
   */	public double[] forwardFluxValues(int numberOfForwardPoints, double samplingPoint) {
	
	       /*
 		*wave/fluxValues are ordered because this class inherites 
		*getWave/FluxValues method from OrderedSpectrum
		*/
		double[] waveValues = this.getWaveValues();
		double[] fluxValues = this.getFluxValues();
		
		double[] values = new double[numberOfForwardPoints];
		
		double mostApproxForwardValue =  waveValues[waveValues.length-1];
		double d = mostApproxForwardValue - samplingPoint;
			
		int i = 1;
		int mostApproxIndex = waveValues.length-1;
		
		while(i >= 0 & d >= 0) {
			if(waveValues[i] - samplingPoint < d) {
				mostApproxForwardValue = waveValues[i];
				d = mostApproxForwardValue - samplingPoint;
				
				mostApproxIndex = i;
			}
		i = i - 1;
		}
		
		for(int k = 0; k < numberOfForwardPoints; k++) 
			values[k] = fluxValues[mostApproxIndex + k];
	return values;
	}
	

}
