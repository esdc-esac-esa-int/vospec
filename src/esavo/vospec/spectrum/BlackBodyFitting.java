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

/**
 *
 * @author  jsalgado
 */

import java.text.DecimalFormat;
import java.util.*;


public class BlackBodyFitting extends ChiSquareFittingSpectrum {
    
    
    private Unit originalUnits;
    private Unit finalUnits;
    private boolean FORCED;
    
    public static double 	BOLTZMANN 		= 1.381E-23;  	// J/K
    public static double 	SPEEDLIGHT		= 299792458.; 	// m/s
    public static double 	PLANCK			= 6.626E-34;	// Js
    
    public static double    WIEN			= PLANCK*SPEEDLIGHT/BOLTZMANN/4.965114225;
    
    public static int	TEMPERATURE_PAR		= 0;
    public static int	SCALING_PAR		= 1;
    
    private double forcedTemperature;
    
    private double xMinOriginal;    
    private double xMaxOriginal;    

    
    public BlackBodyFitting() {
        super();
    }
    
    /** Creates a new instance of BlackBodyFitting */
    public BlackBodyFitting(Vector dataToFit,Unit originalUnits,boolean logX,boolean logY) {
        
        super(dataToFit,logX,logY);  ;
        
        this.setTitle("Black Body Fitting ");
        
        this.finalUnits = new Unit("L","1.","ML-1T-3","1.");
        this.setUnits(finalUnits);
        this.originalUnits = originalUnits;
        
        this.FORCED = false;
        this.forcedTemperature = 0.;
    }
    
    
    public void getXDataYData() {
        
        numberOfPoints = dataToFit.size();
        xData = new double[numberOfPoints];
        yData = new double[numberOfPoints];
        
        for(int i=0; i < numberOfPoints; i++) {
            
            double[] element = (double[]) dataToFit.elementAt(i);
            
            xData[i] = element[0];
            yData[i] = element[1];
            
	    
	    if(logX)    xData[i] = Math.pow(10. , xData[i]);
            if(logY)    yData[i] = Math.pow(10. , yData[i]);
            
	    if(i==0) {
                xMinOriginal = xData[i];
                xMaxOriginal = xData[i];
            }
            if(xData[i] < xMinOriginal)	xMinOriginal	= xData[i];
            if(xData[i] > xMaxOriginal)	xMaxOriginal 	= xData[i];
	    
        }
        
        Spectrum spectrum = new Spectrum();
        spectrum.setUnits(originalUnits);
        spectrum.setWaveValues(xData);
        spectrum.setFluxValues(yData);
        
        try {
        
	    SpectrumConverter spectrumConverter = new SpectrumConverter();
            spectrum = spectrumConverter.convertSpectrum(spectrum,finalUnits);
        
	} catch(Exception e) {
		e.printStackTrace();
	}
        
        xData = spectrum.getWaveValues();
        yData = spectrum.getFluxValues();
        
//        logX = false;
//        logY = false;
        
        // calculate minimum and maximum values
        numberOfPoints = xData.length;
        for(int i=0; i < numberOfPoints; i++) {
            if(i==0) {
                xMin = xData[i];
                xMax = xData[i];
            }
            if(xData[i] < xMin) xMin = xData[i];
            if(xData[i] > xMax) xMax = xData[i];
	    
       }
       
    }
    
    
    public void getWaveAndFluxValues() {
    	
	xMin = xMinOriginal;
    	xMax = xMaxOriginal;
	
	if(logX) {
		xMinOriginal = Math.log(xMinOriginal) / Math.log(10.0);
		xMaxOriginal = Math.log(xMaxOriginal) / Math.log(10.0);
	}
	
       	// XY Arrray generations (for representation)
        waveValues = new double[NUMBER_OF_POINTS];
        fluxValues = new double[NUMBER_OF_POINTS];
        
        for(int k=0; k<NUMBER_OF_POINTS; k++) {
 
        	double  waveDouble	= k * (xMaxOriginal - xMinOriginal)/(NUMBER_OF_POINTS-1) + xMinOriginal;
		if(logX) waveDouble 	= Math.pow(10. ,waveDouble);
		
		waveDouble = ((new SpectrumConverter()).convertPoint(waveDouble, 0., originalUnits, finalUnits))[0];
		
       		double  fluxDouble = fittedFunction(waveDouble);
      
       		waveValues[k] = waveDouble;
       		fluxValues[k] = fluxDouble;
            			
     	}     
    	
    }
    
    
    public void writeMetadata() {
        
        // Setting the metadata
        addMetadataLine("Black Body Fit");
        
        DecimalFormat 	decimalFormat 	= new DecimalFormat("#.##");
        String  	tempString 	= decimalFormat.format(parameter[TEMPERATURE_PAR]);
        
        addMetadataLine("Temperature\t: " + tempString + " K");
        addMetadataLine("Scaling\t: " + parameter[SCALING_PAR]);
        
        addMetadataLine("");
        if(DIVERGE) 	addMetadataLine("Levenberg-Marquardt Method did not improve the solution");
        addMetadataLine("Chi-Square\t: " + CHI_SQUARE);
    }
    
    public void calculateTemperature() {

        Vector thisDataToFit = new Vector();
        
        double thisXMin = 0.;
        double thisXMax = 0.;
        
	double thisFluxMax = 0.;
	double thisWaveForFluxMax = 0.;
	
        boolean first 	= true;
        for(int i=0; i < xData.length; i++) {
            
            double[] element = new double[2];
            
            if(xData[i] > 0. && yData[i] > 0.) {
                element[0] = Math.log(xData[i])/ Math.log(10.);
                element[1] = Math.log(yData[i])/ Math.log(10.);
                
                thisDataToFit.addElement(element);
                
                
                if(first) {
                    thisXMin 		= element[0];
                    thisXMax 		= element[0];
		    
                    thisWaveForFluxMax 	= element[0];
                    thisFluxMax 	= element[1];

                    first = false;
                }
                if(element[0] < thisXMin) thisXMin = element[0];
                if(element[0] > thisXMax) thisXMax = element[0];

                if(element[1] > thisFluxMax) {
			thisWaveForFluxMax 	= element[0];
			thisFluxMax 		= element[1];
                } 
                
            }
        }
        
        PolynomialFitting pf = new PolynomialFitting(thisDataToFit,true,true,3);
        pf.setRow(-1);
        try {
            new Thread(pf).start();
            while(pf.getToWait()){
                Thread.sleep(500);
            }
        } catch(Exception epf) {
            epf.printStackTrace();
        }
        
        double[] parameterPol = pf.getParameters();
        
        // polynomial y=a x^3 + b x^2 + c x + d
        double aPar =  parameterPol[3];
        double bPar =  parameterPol[2];
        double cPar =  parameterPol[1];
        double dPar =  parameterPol[0];
        

	double coefficient = bPar * bPar - 3.* aPar * cPar;
	
	boolean setCorrectly = false;
	double waveMaximum = -1.;
	
	if(coefficient > 0.) {
		
		double firstSol	 = (-bPar + Math.sqrt(coefficient))/3./aPar;
		double secondSol = (-bPar - Math.sqrt(coefficient))/3./aPar;
	
		double secondDerivFirstSol 	= 6. * aPar * firstSol + 2. * bPar;
		double secondDerivSecondSol 	= 6. * aPar * secondSol + 2. * bPar;
		
		if(secondDerivFirstSol  < 0.) 	waveMaximum = firstSol;
		if(secondDerivSecondSol < 0.) 	waveMaximum = secondSol;
		
		setCorrectly = true;
	
	} 
       
        waveMaximum =  Math.pow(10. , waveMaximum);
        
        parameter = new double[2];
        parameter[SCALING_PAR] 		= 1.;
        parameter[TEMPERATURE_PAR] 	= WIEN / waveMaximum;
        	
	if(parameter[TEMPERATURE_PAR] < 0.) {
		parameter[TEMPERATURE_PAR] 	= WIEN / thisWaveForFluxMax;			
	}
        
        
    }
    
    public void calculateScaling() {
        
        double thisYiFi = 0.;
        double thisFiFi = 0.;
	double thisScaling = 1.;

	parameter[SCALING_PAR] 	= thisScaling;
        
	double totalWeight = 0.;
        for(int i=0; i < xDataLinear.length; i++) {
            
            if(yDataLinear[i] > 0.) {
                
                double thisWave = xDataLinear[i];
                double weight = 1.;
                
                thisYiFi += weight * yDataLinear[i] * fittedFunction(thisWave);
                thisFiFi += weight * fittedFunction(thisWave) * fittedFunction(thisWave);
                totalWeight += weight;
            }
        }
        
        if(thisFiFi > 0.) {
            thisScaling = thisYiFi / thisFiFi;
        } 
        
        parameter[SCALING_PAR] 	= thisScaling;
        
    }
    
    public void calculateInitialParameters() {
        
        if(this.FORCED) {
            setTemperature(this.forcedTemperature);
        } else {
            calculateTemperature();
        }
        
        calculateScaling();
    }
    
    public void fit() {
        super.fit();
        
        if(parameter[TEMPERATURE_PAR] < 0.) {
            calculateInitialParameters();
            DIVERGE = true;
        }
    }
    
    public double fittedFunction(double x) {
        return fittedFunction(x,parameter);
    }
    
    
    public double fittedFunction(double x,double[] tempParameter) {
        
        /* Gaussian function:
                                                2 pi h
                f = scaling * 	-----------------------------------------
                                 x^5  ( exp( hc / x kT)  - 1)
         */
        
        double T  = tempParameter[TEMPERATURE_PAR];
        double S  = tempParameter[SCALING_PAR];
        
        double factor1 = 2. * Math.PI * PLANCK / Math.pow(x,5.);
        double factor2 = Math.exp(PLANCK * SPEEDLIGHT / x / BOLTZMANN / T) - 1.;
        double function = S * factor1/factor2;
        
        return function;
    }
    
    
    
    public double functionDerivate(double x, int index) {
        
        double T  = parameter[TEMPERATURE_PAR];
        double S  = parameter[SCALING_PAR];
        
        double factor1 = 2. * Math.PI * PLANCK / Math.pow(x,5.);
        double factor2 = Math.exp(PLANCK * SPEEDLIGHT / x / BOLTZMANN / T) - 1.;
        double function = S * factor1/factor2;
        
        if(index == TEMPERATURE_PAR) {
                /*
                        df/dT = f * [ (-1) / ( exp( hc / x kT)  - 1)] * exp( hc / x kT) * ( hc / x k) [(-1) / T^2] =
                 
                             = f * ( hc / x k)  * exp( hc / x kT) / ( exp( hc / x kT)  - 1)] / T
                 */
            
            double factor3 = PLANCK * SPEEDLIGHT / BOLTZMANN / x;
            double factor4 = Math.exp(factor3 / T);
            double factor5 = (factor4 - 1.) * T * T;
            
            function = function * factor3 * factor4 / factor5;
            
        } else if(index == SCALING_PAR) {
                /*
                        df/dS = f/S
                 */
            function = factor1/factor2;
            
        }
        
        return function;
        
    }
    
    public void setTemperature(double thisTemperature) {
        
        parameter = new double[2];
        parameter[TEMPERATURE_PAR] = thisTemperature;
        parameter[SCALING_PAR] 	   = 1.;
        
        this.FORCED = true;
        this.forcedTemperature = thisTemperature;
    }
}
