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

import java.util.*;



public class SamplingData {
	
	/**
	*this array has the same length as the array containing
	*the waveValues of the SED. Its components are: 
	*-1 if the related waveValue has just backwardWaveValues 
	*0 if the related waveValue has  backward and forward waveValues
	*+1 if the related waveValue has just forwardWaveValues 
	*/
	private int[]		waveValuesDistributionInfo;
	
	private double[]	sampling;
	
	private SED		sed;
	
	private TSAPmodel[]	models;

    private LevenbergMarquardt levenberg;
	
	public SamplingData(SED sed, TSAPmodel[] models, LevenbergMarquardt levenberg) throws Exception {
		this.sed = sed;
		this.models = models;
        this.levenberg = levenberg;
		
			
		evalSamplingData();
	}
	
	
		
	/**Returns a sampling containing waveValues of all the input models(waveValues
	*can be different for each model) and which extremes are the maximum of the 
	*minimum waveValues and the minimum of all the maximum waveValues(to evaluate 
	*the extremes of the sampling, waveValues of the SED are also taken into account)
	*/
	public double[] initialSampling() throws Exception {
		
		Vector sampling = new Vector();
		
		double[] waveValues = sed.getWaveValues();
		double[] fluxValues = sed.getFluxValues();
		
                //System.out.println("INITIALSAMPLING waveValues "+waveValues.length);

		double xMin = waveValues[0];
		double xMax = waveValues[waveValues.length-1];
		
		for(int k = 0; k < models.length; k++) {
		
			//WARNING: Avoid models with no related spectrum
			if((OrderedSpectrum) ((TSAPmodel)(models[k])).getSpectrum() != null) {
			
				double[] tWaveValues = (double[]) ((TSAPmodel)(models[k])).getWaveValues();
		
				if(tWaveValues[0] > xMin) xMin = tWaveValues[0];
				if(tWaveValues[tWaveValues.length-1] < xMax) xMax = tWaveValues[tWaveValues.length-1];
			
				//Adding waveValues of TSAPmodels
				for(int i = 0; i < tWaveValues.length; i++) {
					boolean contains = contains(sampling, tWaveValues[i]);
				
					if(contains == false) {
						Double tWaveValueDouble = new Double(tWaveValues[i]);
						sampling.add(tWaveValueDouble);
					}
				}
			}
		}
	
		//ordering sampling Vector
		for(int i = 0; i < sampling.size(); i++) {
			double aux1 = ((Double)(sampling.elementAt(i))).doubleValue();
			for(int j = i+1; j<sampling.size(); j++) {
				double aux2 = ((Double)(sampling.elementAt(j))).doubleValue();
				if(aux2<aux1 ) {
					Double aux1Double = new Double(aux1);
					Double aux2Double = new Double(aux2);
					sampling.set(i, aux2Double);
					sampling.set(j, aux1Double);
					aux1 = aux2;
				}
			}
		}
	
		int samplingLength = 0;
		
		//Evaluating length for initialSampling
		for(int i = 0; i < sampling.size(); i++) {
			double aux = ((Double)(sampling.elementAt(i))).doubleValue();
			if(xMin <= aux & aux <= xMax){
				samplingLength = samplingLength + 1;
			}	
		}
		
		
		//Vector sampling is already ordered
		int cont = 0;
		
		double[] initialSampling = new double[samplingLength];
		
		for(int i = 0; i < sampling.size(); i++) {
			double aux = ((Double)(sampling.elementAt(i))).doubleValue();
			if(xMin <= aux & aux <= xMax){
				initialSampling[cont] = aux;
				cont = cont + 1;	
			}	
		}	
	
                //System.out.println("INITIALSAMPLING initialSampling "+initialSampling.length);

	return initialSampling;
	}
	
	
	public void evalSamplingData() throws Exception {
		
		double[] waveValues = sed.getWaveValues();
		double[] fluxValues = sed.getFluxValues();
		
		double[] initialSampling = initialSampling();
		
		Vector samplingVector = new Vector();
		
		for(int i = 0; i < initialSampling.length; i ++) {
			int backwardPoints = 0;
			int forwardPoints = 0;
			int pointsInThisRange = 0;
			
			for(int j = 0; j < waveValues.length; j++) {
				if(i==0) {
					if(initialSampling[i]<=waveValues[j] & waveValues[j]<initialSampling[i+1])
						forwardPoints++;
				}
				if(0 < i & i < initialSampling.length-1) {
					if(initialSampling[i-1]<=waveValues[j] & waveValues[j]<initialSampling[i])
						backwardPoints++;
					if(initialSampling[i]<=waveValues[j] & waveValues[j]<initialSampling[i+1])
						forwardPoints++;
				}
				if(i == initialSampling.length-1)  {
					if(initialSampling[i-1]<=waveValues[j] & waveValues[j]<=initialSampling[i])
						backwardPoints++;
					
				}	
				pointsInThisRange = backwardPoints + forwardPoints;
			}
			
			
			//add just the sampling points with waveValues around
			if(pointsInThisRange > 0) {
				double[] element = new double[2];
			
				element[0] = (double) initialSampling[i];
				
				if(backwardPoints!=0 & forwardPoints!=0) element[1] = 0;
				if(backwardPoints!=0 & forwardPoints==0) element[1] = -1;
				if(backwardPoints==0 & forwardPoints!=0) element[1] = 1;
				
				samplingVector.add(element);
			}
		}
		
		sampling = new double[samplingVector.size()];
		waveValuesDistributionInfo = new int[samplingVector.size()];
		
		for(int i = 0; i < samplingVector.size(); i++) {
				double[] element = (double[]) samplingVector.elementAt(i);
				
				sampling[i] = (double) element[0];
                //System.out.println("sampling "+sampling[i]);
                if(levenberg.getStop())throw new Exception("Stop operations");
				waveValuesDistributionInfo[i] = (int) element[1];
		}
			
	}

	
	//Vector v contains Doubles
	public boolean contains(Vector v, double element) throws Exception {
		
		boolean contains = false;
		for(int i = 0; i < v.size(); i++) {
            if(levenberg.getStop())throw new Exception("Stop operations");
			double vElement = (double) ((Double)v.elementAt(i)).doubleValue();
			if(vElement == element) contains = true;
		}
	return contains;
	}
	
	public double[] getSampling() {
	
	return sampling;
	}
	
	public int[] getWaveValuesDistributionInfo() {
	return waveValuesDistributionInfo;
	}


}
