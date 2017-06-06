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
/*
 * NumIntegration.java
 *
 * Created on April 9, 2007, 11:32 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author alaruelo
 */
import esavo.vospec.spectrum.*;


public class NumIntegration {
    
    double  a;
    double  b;
    
    Spectrum    spectrum;
    
    //MathUtils       utils = new MathUtils();
    
    /** Creates a new instance of NumIntegration */
    public NumIntegration() {
    }
        
    public NumIntegration(Spectrum spectrum, double a, double b) {
        this.a = a;
        this.b = b;
        this.spectrum = spectrum;
        
    }
    
    public NumIntegration(Spectrum spectrum) {
        double[] wavevalues = spectrum.getWaveValues();
        
        this.a = wavevalues[0] ;
        this.b = wavevalues[wavevalues.length-1];
        this.spectrum = spectrum;
        
    }
    
    
    /*Numerical integration of the spectrum in 
     *the range where is definded
     *using the extendend trapezoidal rule
     */
    public double getTrapezoidalIntegral() {
        
        //OrderedSpectrum orderedSpectrum = new OrderedSpectrum(spectrum);
        
        int samplingPoints = 1000;
        
        //Trapezoidal rule works just with evenly spaced data
	Spectrum evenlySpacedSpectrum = MathUtils.evenlySpacedSpectrum(spectrum, samplingPoints);
        
        System.out.println("SPECTRUM ALREADY EVENLY SPACED");
        
        double[] waveValues = evenlySpacedSpectrum.getWaveValues();
        double[] fluxValues = evenlySpacedSpectrum.getFluxValues();
        
        //The integration extremes are waveValues[0] and waveValues[waveValues.length-1]
        
        double h = Math.abs(waveValues[1] - waveValues[0]);
                
                //Extended trapezoidal rule
                double integral = (h/2)*(fluxValues[0] + fluxValues[fluxValues.length-1]);
        
        for(int i = 1; i < waveValues.length-1; i++){
            integral = integral + h*fluxValues[i];
        }
        System.out.println("integral value = " + integral);
                
        return integral;
    }
    
    
}
