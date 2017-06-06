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


import esavo.vospec.dataingestion.TsaServerParam;
import esavo.vospec.spectrum.Spectrum;
import esavo.vospec.spectrum.Unit;
import java.util.*;


public class TSAPmodel {

	
	private double			norm;
	
	private Vector			param;
		
	private String			url;
	
	private OrderedSpectrum		tSpectrum;
	
	private SED			sed;
        
        public  static int                     griddingFactor = 1000;

    private LevenbergMarquardt levenberg;
	
	
	
	public TSAPmodel(Vector param, String url, LevenbergMarquardt levenberg) throws Exception {
				
		this.url        = url;
		this.param      = param;
                this.levenberg  = levenberg;
	
		TsapCallRead tsapCallRead   = new TsapCallRead(url, param, levenberg);
		Spectrum spectrum           = tsapCallRead.tsapSpectrum();
		
		if(spectrum!=null) this.tSpectrum = new OrderedSpectrum(spectrum);
			
	}
	
	public TSAPmodel(Vector param, String url, LevenbergMarquardt levenberg, SED sed) throws Exception {
		this(param, url, levenberg);
		
		this.sed = sed;
		if(tSpectrum!=null) this.norm = evaluateNorm(sed);
		
	}
	
	
	public TSAPmodel(TSAPmodel inputModel, LevenbergMarquardt levenberg) throws Exception {
        this.levenberg = levenberg;
		this.param = new Vector();
		for(int i = 0; i < inputModel.getParam().size(); i++) {
						
			TsaServerParam tsaServerParam = new TsaServerParam();
			tsaServerParam = (TsaServerParam)inputModel.getParam().elementAt(i);
			
			TsaServerParam tsaServerParamAux = new TsaServerParam(tsaServerParam);
			//tsaServerParamAux.setName(tsaServerParam.getName());
			//tsaServerParamAux.setSelectedValue(tsaServerParam.getSelectedValue());
			//tsaServerParamAux.setValues(tsaServerParam.getValues());
			
			this.param.add(tsaServerParamAux);
		}
		
		this.url = inputModel.getUrl();
		
		
		TsapCallRead tsapCallRead = new TsapCallRead(url, param, levenberg);
		Spectrum spectrum = tsapCallRead.tsapSpectrum();
		
	       	/*if param is changed tSpectrum must be changed too*/
		if(spectrum!=null) this.tSpectrum = new OrderedSpectrum(spectrum);
	
		this.sed = inputModel.getSed();
		this.norm = inputModel.getNorm();
	}
	
	
	
	public double evaluateNorm(SED sed) throws Exception {
							
		TSAPmodel[] model = new TSAPmodel[1];
		model[0] = this;
		
		SamplingData samplingData = new SamplingData(sed, model, levenberg);
		
		double[] samplingToNormalize = samplingData.getSampling();
		
		double numerator = 0;
		double denominator = 0;
				
		double[] fluxValues = sed.reSampledFluxValues(samplingData);
		double[] tFluxValues = linearInterpolationToNormalize(samplingToNormalize);
		
		for(int j = 0; j < samplingToNormalize.length; j++) {
			numerator = numerator + fluxValues[j]*tFluxValues[j];
			denominator = denominator + Math.pow(tFluxValues[j], 2);
		}
		
		double normAux = 0;
		if(denominator != 0)
			normAux = numerator/denominator;
		else System.out.println("denominator=0 !!");
		
	return normAux;
	}
	
	
	public double[] linearInterpolationToNormalize(double[] sampling) {
					
			int cont = 0;
			int i = 0;
						
			double[] fluxValues=new double[sampling.length];
			
			double[] tWaveValues = this.tSpectrum.getWaveValues();
			double[] tFluxValues = this.tSpectrum.getFluxValues();
						
			for(int n=0;n<sampling.length;n++) {
			
				i = 0;
				boolean goOn = true;
				
				while (i < tWaveValues.length & goOn == true) {
					if(tWaveValues[i]==sampling[n]) {
						fluxValues[cont] = tFluxValues[i];
						goOn = false;
						cont = cont + 1;
					}
					if(i > 0) 
						if(tWaveValues[i-1]<sampling[n] & sampling[n]<tWaveValues[i]) {
							fluxValues[cont]=tFluxValues[i-1]+((tFluxValues[i]-tFluxValues[i-1])/(tWaveValues[i]-tWaveValues[i-1]))*(sampling[n]-tWaveValues[i-1]);
							goOn = false;
							cont = cont + 1;	
						}	
				i++;
				}
			}
	return fluxValues;
	}

      /** 
	*Normalization and linear interpolation of the fluxValues 
	* of the model in the points given by input samplingData
	*/
	public double[] normalizedFluxValues(SED sed, SamplingData samplingData) {
					
		int cont = 0;
		
		double[] sampling = samplingData.getSampling();
		
                //System.out.println("WAVEVALUES "+tSpectrum.getWaveValues().length+" "+tSpectrum.getUrl());

		double[] tWaveValues = this.tSpectrum.getWaveValues();
		double[] tFluxValues = this.tSpectrum.getFluxValues();
		
		double[] normalizedTfluxValues = new double[tFluxValues.length];
		double[] fluxValues=new double[sampling.length];

		//model <-- normalized model
		for(int j = 0; j < tFluxValues.length; j++) 
			normalizedTfluxValues[j] = this.norm*tFluxValues[j];
					
		for(int n=0; n<sampling.length; n++) {
			int i = 0;
			boolean goOn = true;
				
			while (i < tWaveValues.length & goOn == true) {
				if(tWaveValues[i]==sampling[n]) {
					fluxValues[cont] = normalizedTfluxValues[i];
					goOn = false;
					cont = cont + 1;
				}
				
				if(i > 0) {
					if(tWaveValues[i-1]<sampling[n] & sampling[n]<tWaveValues[i]) {
						fluxValues[cont]=normalizedTfluxValues[i-1]+((normalizedTfluxValues[i]-normalizedTfluxValues[i-1])/(tWaveValues[i]-tWaveValues[i-1]))*(sampling[n]-tWaveValues[i-1]);
						goOn = false;
						cont = cont + 1;	
					}
				}
												
				i = i + 1;
			}
		}
	return fluxValues;
	}
	
	
	public int numberOfEffecParam() {
	     int posInParam = 0;
	     int numberOfEffecParam = 0;	
	     
	     while(posInParam < param.size()) {
			
		String name = (String) this.getName(posInParam).toUpperCase();
										
		if(name.indexOf("MIN") != -1 || name.indexOf("MAX") != -1) posInParam = posInParam + 2; 
		else posInParam++;
		
		numberOfEffecParam++;
				
             }  
	       
	return numberOfEffecParam;
	}
	
	public SED getSed() {
	return this.sed;
	}
	
	
	public void setNorm(SED sed) throws Exception {
		this.sed = sed;
		this.norm = evaluateNorm(sed);
	}
	
	public double getNorm() {
	return norm;
	}		
	
		
	public String getUrl() {
	return this.url;
	}
	
	public void setUrl(String url) {
	this.url = url;
	}
		
	 
	public Vector getParam() {
	return this.param;
	}
	
	public void setParam(Vector param) throws Exception {

		TsapCallRead tsapCallRead = new TsapCallRead(url, param, levenberg);
		Spectrum spectrum = tsapCallRead.tsapSpectrum();
			
		if(spectrum!=null) this.tSpectrum = new OrderedSpectrum(spectrum);
		else tSpectrum=null;
	}
	
			
	public double[] getWaveValues() {
	return tSpectrum.getWaveValues();
	}
	 
	public double[] getFluxValues() {
	return tSpectrum.getFluxValues();
	}
	 
	public double[] getNormalizedFluxValues(SED sed, SamplingData samplingData) {
	return  normalizedFluxValues(sed, samplingData);
	}
	
	
	public Unit getUnits() {
	return tSpectrum.getUnits();
	}
	

	public String getSelectedValue(int posInParam) {
		String selectedValue = ((TsaServerParam) this.param.elementAt(posInParam)).getSelectedValue();
	return selectedValue;
	}
	
	public void setSelectedValue(String valueToSet, int posInParam) throws Exception {
		TsaServerParam tsaServerParam = (TsaServerParam) this.param.elementAt(posInParam);
		tsaServerParam.setSelectedValue(valueToSet);
		
		String name =  this.getName(posInParam).toUpperCase();
										
		if(name.indexOf("MIN") != -1 || name.indexOf("MAX") != -1) {
			tsaServerParam = (TsaServerParam) this.param.elementAt(posInParam+1);
			tsaServerParam.setSelectedValue(valueToSet);
		}
			
		Spectrum spectrum = (Spectrum) (new TsapCallRead(url, param, levenberg)).tsapSpectrum();
		
		//sed is null when looking for initialModel
		if(spectrum!=null & this.sed!=null) {
			this.tSpectrum = new OrderedSpectrum(spectrum);
			this.norm = evaluateNorm(this.sed);
		}
		else if(spectrum!=null & this.sed==null) {
			this.tSpectrum = new OrderedSpectrum(spectrum);	
		}
		else tSpectrum = null;
		
		
	}
	
	public boolean equals(TSAPmodel modelToCompare) {
	
		int posInParam = 0;
		
		boolean equals = true;
		
		while(posInParam < this.param.size() & equals==true){
					
			Double selectedValueInitial = new Double((String)this.getSelectedValue(posInParam));
			double auxInitial = selectedValueInitial.doubleValue();
				
			Double selectedValueToTry = new Double((String)modelToCompare.getSelectedValue(posInParam));
			double auxToTry = selectedValueToTry.doubleValue();
						
			if(auxInitial != auxToTry) {
				System.out.println("INITIAL PARAM =/ PARAM TO TRY");
				equals = false; 
						
			}
										
		String name =  this.getName(posInParam).toUpperCase();
		if(name.indexOf("MIN") != -1 || name.indexOf("MAX") != -1) posInParam = posInParam + 2;
		else posInParam++;
		
		}
			
	return equals;
	}
	
	public OrderedSpectrum getSpectrum() {
	return tSpectrum;
	} 
	
		
	public void setEquals(TSAPmodel model) {
		this.url 	= model.getUrl();
		this.param 	= model.getParam();
		this.tSpectrum 	= model.getSpectrum();
		this.norm 	= model.getNorm();
			
	}
	
	
    //returns a vector already ordered
    public Vector getValues(int posInParam) {

        TsaServerParam param = ((TsaServerParam) this.param.elementAt(posInParam));

        Vector values = param.getValues();

        //case of continuous parameter

        if (param.getIsCombo()) {

            return orderVectorOfStrings(values);

        } else {
            Vector discretizedValues = new Vector();

            String minValueString = ((TsaServerParam) this.param.elementAt(posInParam)).getMinString();
            String maxValueString = ((TsaServerParam) this.param.elementAt(posInParam)).getMaxString();

            Double minValueDouble = new Double(minValueString);
            Double maxValueDouble = new Double(maxValueString);

            for (int k = 0; k < griddingFactor; k++) {
                double griddedValue = k * (maxValueDouble - minValueDouble) / (griddingFactor - 1) + minValueDouble;
                discretizedValues.add(String.valueOf(griddedValue));
            }

            return discretizedValues;
        }


    }


	public String getName(int posInParam) {
		TsaServerParam tsaServerParam   = (TsaServerParam) this.param.elementAt(posInParam);
		String name                     = tsaServerParam.getName();
	return name;
	}
	
	public Vector orderVectorOfStrings(Vector vector) {
		for(int i = 0; i < vector.size(); i++) {
			String elementString1   = (String) vector.elementAt(i);
			Double elementDouble1   = new Double(elementString1);
			double element1         = elementDouble1.doubleValue();
			
			for(int j = i+1; j < vector.size(); j++) {
				String elementString2   = (String) vector.elementAt(i);
				Double elementDouble2   = new Double(elementString2);
				double element2         = elementDouble2.doubleValue();
			
				if(element2 < element1 ) {					
					vector.set(i, elementString2);
					vector.set(j, elementString1);
					element1        = element2;
				}
			}
		}
	return vector;//ordered vector of Doubles
	}
		
	
}//class
