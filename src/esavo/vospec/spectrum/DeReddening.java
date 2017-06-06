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

import esavo.vospec.main.VOSpecDetached;
import esavo.vospec.main.DeReddeningWindow;

/**
 *
 * @author  jsalgado
 */
public class DeReddening {
     
     private VOSpecDetached vospec;

     private  DeReddening() {}

     public DeReddening(VOSpecDetached vospec){
         this.vospec = vospec;
     }

     public double getDeRedFlux(double waveInMeters, double flux) {
                  
         DeReddeningWindow dr = vospec.deReddeningWindow;
         
         if(dr.getCalzetti_Check()) {
             flux = getFluxCalzetti(waveInMeters, flux, dr.getCalzetti_EBV(), dr.getCalzetti_RV());
         }
         if(dr.getCardelli_Check()) {
            flux = getFluxCardelli(waveInMeters, flux, dr.getCardelli_EBV(), dr.getCardelli_RV());
         }
         if(dr.getLMC_Check())      {
            flux = getFluxLMC(waveInMeters, flux, dr.getLMC_EBV(), dr.getLMC_RV());
         }
         
         return flux;
     }
     
    public boolean deReddening() {
    	if(vospec == null) return false;
        return vospec.deRedSelected();
    }
     
	    
    
    public static double  getFluxCalzetti(double waveInMeters, double flux, double E_BV, double R_V) {
    	
    	double waveInAngstrom 	= waveInMeters*1.E10;
	double X		= 10000./waveInAngstrom;
	double kappa 		= 1.;
	
	double returnFlux	= flux;
	boolean setFormula	= false;
	
	if(waveInAngstrom >= 6300. && waveInAngstrom <= 22000.) {
		kappa 		= 2.659 * (-1.857 + 1.040 * X) + R_V;
		setFormula 	= true;
	}

	if(waveInAngstrom >= 912. && waveInAngstrom <= 6300.) {
		kappa 		= 2.659 * (-2.156 + 1.509 * X - 0.198 * X * X + 0.011 * X * X * X) + R_V; 
		setFormula 	= true;
	}

	if(setFormula) returnFlux = flux * Math.pow(10., 0.4 * E_BV * kappa);
	
	return returnFlux;
    
    }
    
    public static double  getFluxCardelli(double waveInMeters, double flux, double E_BV, double R_V) {
    	
	double waveInAngstrom 	= waveInMeters*1.E10;
	double X		= 10000./waveInAngstrom;
	double a = 0.;
	double b = 0.;
	
	double returnFlux	= flux;
	boolean setFormula	= false;

	if(waveInAngstrom >= 9090. && waveInAngstrom <= 33333.) {
		a 		= 0.574 * Math.pow(X,1.61);
		b 		= -0.527 * Math.pow(X,1.61);
		setFormula 	= true;
	}

	if(waveInAngstrom >= 3030. && waveInAngstrom <= 9090.) {

		a = 	1. 
		 	+ 0.104 * (X - 1.82) 
			- 0.609 * Math.pow(X -1.82,2) 
			+ 0.701 * Math.pow(X -1.82,3) 
			+ 1.137 * Math.pow(X -1.82,4) 
			- 1.718 * Math.pow(X -1.82,5) 
			- 0.827 * Math.pow(X -1.82,6) 
			+ 1.647 * Math.pow(X -1.82,7) 
			- 0.505 * Math.pow(X -1.82,8); 

		b = 	1.952  * (X - 1.82) 
			+ 2.908  * Math.pow(X -1.82,2) 
			- 3.989  * Math.pow(X -1.82,3) 
			- 7.985  * Math.pow(X -1.82,4) 
			+ 11.102 * Math.pow(X -1.82,5) 
			+ 5.491  * Math.pow(X -1.82,6) 
			- 10.805 * Math.pow(X -1.82,7) 
			+ 3.347  * Math.pow(X -1.82,8); 
		
		
		setFormula 	= true;
	}
	
	if(waveInAngstrom >= 1250. && waveInAngstrom <= 3030.) {

                double Fa = 0.;
                if(waveInAngstrom > 1695.) Fa = - 0.04473 * Math.pow(X - 5.9,2)	- 0.009779 * Math.pow(X-5.9, 3);
                double Fb = 0.;
                if(waveInAngstrom > 1695.) Fb = + 0.2130 * Math.pow(X - 5.9,2)	+ 0.1207 * Math.pow(X-5.9, 3);
                        
                        
		a = 	1.752 - 0.316 * X - 0.104/ (Math.pow(X-4.67,2) + 0.341) + Fa;
			
		
		b = 	- 3.090 + 1.825 * X + 0.1206/ (Math.pow(X-4.62,2) + 0.263) + Fb;
			
		
		setFormula 	= true;
		
	}
	
	if(waveInAngstrom >= 1000. && waveInAngstrom <= 1250.) {

		a = 	-1.073 - 0.628 * (X - 8.) + 0.137 * Math.pow(X - 8.,2) - 0.070 * Math.pow(X - 8.,3);
		
		b = 	13.670 + 4.257 * (X - 8.) - 0.420 * Math.pow(X - 8.,2) + 0.374 * Math.pow(X - 8.,3);
		
		setFormula 	= true;
		
	}
	
	if(setFormula) returnFlux = flux * Math.pow(10., 0.4 * (R_V * E_BV * (a + b/R_V)));
	
	return returnFlux;
    }

    public static double  getFluxLMC(double waveInMeters, double flux, double E_BV, double R_V) {

	double waveInAngstrom 	= waveInMeters*1.E10;
	double X		= 10000./waveInAngstrom;
	double kappa = 0.;
	
	double returnFlux	= flux;
	boolean setFormula	= false;


	if(waveInAngstrom <= 3636.&& waveInAngstrom >= 1200.) {
		
		kappa = R_V - 0.236 + 0.462 * X + 0.105 * X * X + 0.454/((X - 4.557) * (X - 4.557) + 0.293);

		setFormula 	= true;
	}

	if(waveInAngstrom <= 5464.&& waveInAngstrom >= 3636.) {
		
		kappa = R_V + 2.04 * (X - 1.83) + 0.094 * Math.pow(X - 1.83,2);

		setFormula 	= true;
	}
		
	if(waveInAngstrom <= 47600.&& waveInAngstrom >= 5464.) {
		
		kappa = ((1.86 - 0.48 * X) * X -0.1) * X; 

		setFormula 	= true;
	}

	if(setFormula) returnFlux = flux * Math.pow(10., 0.4 * E_BV * kappa);
	
	return returnFlux;
    }

}
