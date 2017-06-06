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
 * @author alaruelo
 */
import java.util.*;



public class LorentzianFitting extends ChiSquareFittingSpectrum {
        
 
    private static int A_PAR 		= 0;
    private static int X0_PAR 		= 1;
    private static int SIGMA_PAR 	= 2;
    private static int B_PAR 		= 3;
    
    public Spectrum spectrum;
    
    
    public LorentzianFitting() {
        super();
    }    

    /** Creates a new instance of PolynomialFitting */
    public LorentzianFitting(Vector dataToFit,Unit originalUnits,boolean logX,boolean logY) {
 
        super(dataToFit,logX,logY);                
        this.setTitle("Lorentzian Fitting ");
 	
	this.setUnits(originalUnits);
   }   
    
    public LorentzianFitting(Spectrum spectrum, Unit originalUnits,boolean logX,boolean logY) {
        
        super(spectrum,logX,logY);                
        this.setTitle("Lorentzian Fitting ");
 	
	this.setUnits(originalUnits);
        
        this.spectrum = spectrum;
    }
    

    public void writeMetadata() { 
	
     	// Setting the metadata
	addMetadataLine("Lorentzian Fit:");
	addMetadataLine("f = A * sigma^2 / (4 * (x - x0)^2 + sigma^2)+B");
       	addMetadataLine("A\t:" + parameter[A_PAR]);
       	addMetadataLine("x0\t:" + parameter[X0_PAR]);
       	addMetadataLine("sigma\t:" + parameter[SIGMA_PAR]);
     	addMetadataLine("B\t:" + parameter[B_PAR]);
	
	addMetadataLine("");
	if(DIVERGE) 	addMetadataLine("Levenberg-Marquardt Method did not improve the solution");
	addMetadataLine("Chi-Square\t:" + CHI_SQUARE/DEG_FREEDOM);
        //addMetadataLine("Chi-Square\t:"+ CHI_SQUARE);
        if(logX) 	addMetadataLine("X axis in logarithm scale");
        if(logY) 	addMetadataLine("Y axis in logarithm scale");
	

  }
    
    
 /*   public void calculateInitialParameters() {
        
        double[] waveValues = new double[xDataLinear.length]; 
         double[] fluxValues = new double[xDataLinear.length];
        
       	for(int i=0; i < xDataLinear.length; i++) {

	    	double[] element = new double[2];
	 
		waveValues[i] = xDataLinear[i];
		fluxValues[i] = yDataLinear[i];
	}	         
        spectrum = new Spectrum();
        
        spectrum.setWaveValues(waveValues);
        spectrum.setFluxValues(fluxValues);
         
         SpectralLine spectralLine   = new SpectralLine(spectrum);
        
        Spectrum smoothedLine       = spectralLine.getSmoothedLine();
        
        double[] smoothedWaveValues         = smoothedLine.getWaveValues();
        
        int peakPosition            = spectralLine.getPeakPosition();
        int lineOrientation         = spectralLine.getLineOrientation();
        
        double lineMax              = spectralLine.getLineMax();
        double lineMin              = spectralLine.getLineMin();
        
        parameter = new double[4];
	
        double A = lineMax - lineMin;
	
        parameter[X0_PAR] = smoothedWaveValues[peakPosition];
        
        System.out.println("peak wave value = "+parameter[X0_PAR]);
        
	parameter[SIGMA_PAR] = (xMax - xMin) /2;
	
	if(lineOrientation < 0.) { 
		parameter[A_PAR] = A;
		parameter[B_PAR] = lineMin;
	} else {
		parameter[A_PAR] = -A;
		parameter[B_PAR] = lineMax;	
	}  
        
        System.out.println("initialParam = "+ parameter[X0_PAR] +" "+ parameter[SIGMA_PAR]+" "+parameter[A_PAR]+" "+parameter[B_PAR]);
        
    }
*/

    public void calculateInitialParameters() {

	Vector thisDataToFit = new Vector();
       	for(int i=0; i < xDataLinear.length; i++) {

	    	double[] element = new double[2];
	 
		element[0] = xDataLinear[i];
		element[1] = yDataLinear[i];
			
	    	thisDataToFit.addElement(element);	
	}	

	PolynomialFitting pf = new PolynomialFitting(thisDataToFit,true,true,2);
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
	
	// polynomial y=a x^2 + b x + c
	double a =  parameterPol[2];
	double b =  parameterPol[1];
	double c =  parameterPol[0];
	
	
	parameter = new double[4];
	double A = yMax - yMin;
	parameter[X0_PAR] = -b/2/a;
	parameter[SIGMA_PAR] = (xMax - xMin) /2;
	
	if(a < 0.) { 
		parameter[A_PAR] = A;
		parameter[B_PAR] = yMin;
	} else {
		parameter[A_PAR] = -A;
		parameter[B_PAR] = yMax;	
	}  
        
        System.out.println("initialParam = "+ parameter[X0_PAR] +" "+ parameter[SIGMA_PAR]+" "+parameter[A_PAR]+" "+parameter[B_PAR]);
    }  	



    public double fittedFunction(double x,double[] tmpParameter) {
    	
	/* Lorentzian function:
		
		f = B + A * sigma^2 / (4 * (x - x0)^2 + sigma^2)
	*/	
	
	double A 	= tmpParameter[A_PAR];
	double x0 	= tmpParameter[X0_PAR];
	double sigma	= tmpParameter[SIGMA_PAR];
	double B	= tmpParameter[B_PAR];
        	
	double function = B + A * sigma * sigma / (4 * (x - x0) * (x - x0) + sigma*sigma);
	return function;	
    }	    
    
    
    public double functionDerivate(double x, int index) {
	
	double A 	= parameter[A_PAR];
	double x0 	= parameter[X0_PAR];
	double sigma	= parameter[SIGMA_PAR];
	double B	= parameter[B_PAR];
    	
	double function = sigma / (4 * (x-x0) * (x-x0) + sigma * sigma);
	
    	if(index == A_PAR) {
		/* 
			df/dA = exp ( -(x -x0)^2 / (2 sigma^2) )
			df/dA = (f-B)/A;
		*/	
		
		function = function * sigma;
	
	} else if(index == X0_PAR) {
		/* 
			df/dx0 = A * (x - x0) exp ( -(x -x0)^2 / (2 sigma^2) ) / sigma / sigma
			df/dx0 = (f-B) * (x - x0) / sigma^2
		*/	
	
		function =function * 8 * A * sigma * (x - x0) / (4 * (x-x0) * (x-x0) + sigma * sigma);
	
    	} else if(index == SIGMA_PAR) {
		/* 
			df/dsigma = A * (x - x0)^2 exp ( -(x -x0)^2 / (2 sigma^2) ) / sigma^3
			df/dsigma = (f - B) * (x - x0)^2 / sigma^3
		*/	
	
		function = function * 8 * A * (x - x0)* (x - x0) / (4 * (x-x0) * (x-x0) + sigma * sigma);
	
	} else if(index == B_PAR) {
		/* 	
			df/dB = 1
		*/
		
		function = 1.;
	}
	
	return function;
	
    }	
}
