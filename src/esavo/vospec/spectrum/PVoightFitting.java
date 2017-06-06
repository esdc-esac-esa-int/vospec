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


public class PVoightFitting extends ChiSquareFittingSpectrum{
    
    
    private static int A_PAR 		= 0;
    private static int X0_PAR 		= 1;
    private static int SIGMA_PAR 	= 2;
    private static int B_PAR 		= 3;
    // private static int NU_PAR 		= 4;
    
    private double     nu               = 0.5;
    
    //private GaussianFitting             gaussian;
    //private LorentzianFitting           lorentzian;
    
    
    /** Creates a new instance of pVoightFitting */
    public PVoightFitting() {
    }
    
    
    
    /** Creates a new instance of PVoightFitting */
    public PVoightFitting(Vector dataToFit,Unit originalUnits,boolean logX,boolean logY) {
        
        super(dataToFit,logX,logY);
        this.setTitle("pVoight Fitting ");
        
        this.setUnits(originalUnits);
        
        //gaussian            = new GaussianFitting(dataToFit, originalUnits, logX,logY);
        //lorentzian          = new LorentzianFitting(dataToFit, originalUnits, logX, logY);
        
//        System.out.println("I'm here 1");
//        System.out.println("dataVector size = " + dataToFit.size());
    }
    
    
    public void writeMetadata() {
        
        // Setting the metadata
        addMetadataLine("pVoight Fit:");
        addMetadataLine("f = nu * Lorentzian + (1-nu) * Gaussian + B");
        addMetadataLine("A\t:" + parameter[A_PAR]);
        addMetadataLine("x0\t:" + parameter[X0_PAR]);
        addMetadataLine("sigma\t:" + parameter[SIGMA_PAR]);
        addMetadataLine("B\t:" + parameter[B_PAR]);
        //addMetadataLine("nu\t:" + parameter[NU_PAR]);
        
        addMetadataLine("");
        if(DIVERGE) 	addMetadataLine("Levenberg-Marquardt Method did not improve the solution");
        addMetadataLine("Chi-Square\t:" + CHI_SQUARE/DEG_FREEDOM);
        //addMetadataLine("Chi-Square\t:"+ CHI_SQUARE);
        if(logX) 	addMetadataLine("X axis in logarithm scale");
        if(logY) 	addMetadataLine("Y axis in logarithm scale");
        
        
    }
    
/*    public void calculateInitialParameters() {
        
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
        
	double sigmaGinitial = (xMax - xMin) /3.;
        double sigmaLinitial = (xMax - xMin) /2;
        
        parameter[SIGMA_PAR] = (sigmaGinitial+sigmaLinitial)/2;
        
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
        
//        System.out.println("I'm here 2");
      
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
        
        
        //parameter = new double[5];
        
        parameter = new double[4];
        
        double A = yMax - yMin;
        parameter[X0_PAR] = -b/2/a;
        double sigmaGinitial = (xMax - xMin) /3.;
        double sigmaLinitial = (xMax - xMin) /2;
        
        parameter[SIGMA_PAR] = (sigmaGinitial+sigmaLinitial)/2;
        
        if(a < 0.) {
            parameter[A_PAR] = A;
            parameter[B_PAR] = yMin;
        } else {
            parameter[A_PAR] = -A;
            parameter[B_PAR] = yMax;
        }
        
        //parameter[NU_PAR] = 0.5;
        
//        System.out.println("initialParam = "+ parameter[X0_PAR] +" "+ parameter[SIGMA_PAR]+" "+parameter[A_PAR]+" "+parameter[B_PAR]);
//        
//        System.out.println("I'm here 3");
    }
    
    
    public double fittedFunction(double x,double[] tmpParameter) {
        
//        System.out.println("I'm here 4");
        
        /* pVoight function:
         
                f = B + nu * Lorentzian + (1-nu) * Gaussian
         */
        
        double A 	= tmpParameter[A_PAR];
        double x0 	= tmpParameter[X0_PAR];
        double sigma	= tmpParameter[SIGMA_PAR];
        double B	= tmpParameter[B_PAR];
      //double nu       = tmpParameter[NU_PAR];
        
        double[] tmpParameterLorentzian = new double[4];
        double[] tmpParameterGaussian = new double[4];
        
/*      tmpParameterLorentzian[A_PAR] = A;
        tmpParameterLorentzian[X0_PAR] = x0;
        tmpParameterLorentzian[SIGMA_PAR] = sigma;
        tmpParameterLorentzian[B_PAR] = 0;
 
        tmpParameterGaussian[A_PAR] = A;
        tmpParameterGaussian[X0_PAR] = x0;
        tmpParameterGaussian[SIGMA_PAR] = sigma/2/Math.sqrt(2*Math.log(2));
        tmpParameterGaussian[B_PAR] = 0;
 */
        double sigmaG = sigma/2/Math.sqrt(2*Math.log(2));
        
        //double function = B + nu * lorentzian.fittedFunction(x, tmpParameterLorentzian) + (1-nu) * gaussian.fittedFunction(x, tmpParameterGaussian);
        
        double lorentzianFunction = A * sigma * sigma / (4 * (x - x0) * (x - x0) + sigma*sigma);
        double gaussianFunction = A * Math.exp( (-1.) * (x - x0) * (x - x0) / 2. / sigmaG / sigmaG);
        
        double function = B + nu * lorentzianFunction + (1-nu) * gaussianFunction;
        
//        System.out.println("I'm here 5");
        
        return function;
        
    }
    
    
    public double functionDerivate(double x, int index) {
        
//        System.out.println("I'm here 6");
        
        double A 	= parameter[A_PAR];
        double x0 	= parameter[X0_PAR];
        double sigma	= parameter[SIGMA_PAR];
        double B	= parameter[B_PAR];
        //double nu       = parameter[NU_PAR];
        
//        System.out.println("A = "+A);
//        System.out.println("x0 = "+x0);
//        System.out.println("sigma = "+sigma);
//        System.out.println("B = "+B);
     
        double sigmaG = sigma/2/Math.sqrt(2*Math.log(2));
        
        double lorentzianFunction = A * sigma * sigma / (4 * (x - x0) * (x - x0) + sigma*sigma);
        double gaussianFunction = A * Math.exp( (-1.) * (x - x0) * (x - x0) / 2. / sigmaG / sigmaG);
        
/*        double[] parameterLorentzian = new double[4];
        double[] parameterGaussian = new double[4];
 
        parameterLorentzian[A_PAR] = A;
        parameterLorentzian[X0_PAR] = x0;
        parameterLorentzian[SIGMA_PAR] = sigma;
        parameterLorentzian[B_PAR] = 0;
 
        parameterGaussian[A_PAR] = A;
        parameterGaussian[X0_PAR] = x0;
        parameterGaussian[SIGMA_PAR] = sigma/2/Math.sqrt(2*Math.log(2));
        parameterGaussian[B_PAR] = 0;
 */
        double function = 0;
        double lorentzianFactor = 8 * (x-x0) / (4 * (x-x0)*(x-x0) + sigma*sigma);
        
        if(index == A_PAR) {
            
            function = nu * lorentzianFunction/A + (1 - nu) * gaussianFunction/A;
            
        } else if(index == X0_PAR) {
            
            function = nu * lorentzianFactor * lorentzianFunction + (1 - nu) * (x-x0)*gaussianFunction/sigmaG/sigmaG;
            
        } else if(index == SIGMA_PAR) {
            
            function = nu * lorentzianFunction* lorentzianFactor * (x-x0)/sigma  + (1 - nu) *gaussianFunction*8*(x-x0)*(x-x0)*Math.log(2)/(sigma*sigma*sigma);
            
        } else if(index == B_PAR) {
                /*
                        df/dB = 1
                 */
            
            function = 1.;
        }// else if(index == NU_PAR) {
        
        //    function = lorentzianFunction - gaussianFunction;
        // }
        
//        System.out.println("I'm here 7");
//        
//        System.out.println("iter = "+iter);
//        System.out.println("===================");
//        
//        System.out.println("CHI = "+CHI_SQUARE);
//        System.out.println("===================");
//        
        return function;
        
        
    }
}
