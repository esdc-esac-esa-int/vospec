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
/*
 * ChiSquareFitting.java
 *
 * @author Andrea Laruelo - ESAC/ESA - Madrid, Spain 
 *
 */



public class ChiSquareFitting {

	private SED		sed;
	
	private TSAPmodel[]	models;

    private LevenbergMarquardt levenberg;
	
	public ChiSquareFitting(SED sed, TSAPmodel[] models, LevenbergMarquardt levenberg) {
		this.sed = sed;
		this.models = models;
        this.levenberg = levenberg;
	
	}
	
	public double[] chiSquare() throws Exception {
		
		double[] chi = new double[models.length];
		
		SamplingData samplingData = new SamplingData(sed, models, levenberg);
		
		double[] chiSquareSampling = (double[]) samplingData.getSampling();   
				
		for(int k = 0; k < models.length; k++) {
			//WARNING: Avoiding models with related spectrum=null
            TSAPmodel currentModel = models[k];
			if(currentModel.getSpectrum() != null) {
				double[] tFluxValues = (double[]) currentModel.getNormalizedFluxValues(sed, samplingData);
				double[] fluxValues = (double[]) sed.reSampledFluxValues(samplingData);
			
				for(int j = 0; j < chiSquareSampling.length; j++) 
					chi[k]=chi[k]+Math.pow((fluxValues[j]-tFluxValues[j]), 2); 
			}else { 
				chi[k] = -1; 
			}
		}
	
	/*returns -1 entries for models with related spectra = null*/
	return chi;			
	}
	
	
	public double[] getValues() throws Exception {
		return chiSquare();
	}
}
