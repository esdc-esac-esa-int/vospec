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

/**
 *
 * @author alaruelo
 */

import esavo.vospec.spectrum.*;
import javax.swing.*;

public class IntegralTools{
    
    int numOfSamplingPoints = 1000;
    
    boolean multivalued = false;
    
    //double[] sampling;
    
    Spectrum spectrum1;
    Spectrum spectrum2;
    
    
    /** Creates a new instance of IntegralTools */
    public IntegralTools(Spectrum inputSpectrum1, Spectrum inputSpectrum2) {
//        SpectralLine line1 = new SpectralLine(inputSpectrum1, true);
//        SpectralLine line2 = new SpectralLine(inputSpectrum2, true);
//        
//        this.spectrum1 = line1.getSmoothedLine();
//        this.spectrum2 = line2.getSmoothedLine();
        //initializeData(inputSpectrum1, inputSpectrum2);
        
        this.spectrum1 = inputSpectrum1;
        this.spectrum2 = inputSpectrum2;
        
        
    }
    
    public IntegralTools(Spectrum inputSpectrum1) {
        
        this.spectrum1 = inputSpectrum1;
        
        boolean multivalued1 = (boolean) MathUtils.multivaluedSpectrum(spectrum1);
        
        if(multivalued1 == true){
            multivalued = true;
            // System.out.println("WARNING!! MULTIVALUED SPECTRUM");
            
            //custom title, warning icon
            JFrame frame = new JFrame();
            
            JOptionPane.showMessageDialog(frame,
                    "Multivalued Input Spectrum. The requested value is going to be calculated using a smoothed spectrum",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
        
        if (multivalued) {
            spectrum1 = (Spectrum) (new Smoothing()).multivaluedSpectrumSmoothing(spectrum1);
        }
        
        
        
    }
    
    
    
/*    public void initializeData(Spectrum sp1, Spectrum sp2) {
 
        OrderedSpectrum orderedSpectrum1 = new OrderedSpectrum(sp1);
        OrderedSpectrum orderedSpectrum2 = new OrderedSpectrum(sp2);
 
        double[] waveValues1 = orderedSpectrum1.getWaveValues();
        double[] inputFluxValues1 = orderedSpectrum1.getFluxValues();
 
        double[] waveValues2 = orderedSpectrum2.getWaveValues();
        double[] inputFluxValues2 = orderedSpectrum2.getFluxValues();
 
        //range where both spectra are defined simoultaneously
        double xMin = Math.max(waveValues1[0], waveValues2[0]);
        double xMax = Math.min(waveValues1[waveValues1.length-1], waveValues2[waveValues2.length-1]);
 
 
        double[] sampling = new double[numOfSamplingPoints];
        double samplingInterval = (xMax-xMin)/(numOfSamplingPoints-1);
 
        for(int k = 0; k < numOfSamplingPoints; k++) {
            sampling[k] = xMin + k*(xMax-xMin)/(numOfSamplingPoints-1);
        }
 
        int i = 0;
        int j = 0;
        int k = 0;
 
        double[] fluxValues1 = new double[sampling.length];
        double[] fluxValues2 = new double[sampling.length];
 
        while(k < sampling.length) {
 
            fluxValues1[k] = 0.0;
            fluxValues2[k] = 0.0;
 
            int pointsThisRange1 = 0;
            int pointsThisRange2 = 0;
 
            double xMinThisRange = xMin + (k-0.5)*(xMax-xMin)/(numOfSamplingPoints-1);
            double xMaxThisRange = xMin + (k+0.5)*(xMax-xMin)/(numOfSamplingPoints-1);
 
 
            while(xMinThisRange <= waveValues1[i] && waveValues1[i] < xMaxThisRange ) {
 
 
                fluxValues1[k] = fluxValues1[k] + inputFluxValues1[i];
                pointsThisRange1++;
 
                if(i < waveValues1.length-1) i++;
            }
 
            while(xMinThisRange <= waveValues2[j] && waveValues2[j] < xMaxThisRange ) {
 
                fluxValues2[k] = fluxValues2[k] + inputFluxValues2[j];
                pointsThisRange2++;
 
                if(j < waveValues2.length-1) j++;
            }
 
 
 
            if(pointsThisRange1 != 0) fluxValues1[k] = fluxValues1[k]/pointsThisRange1;
            if(pointsThisRange2 != 0) fluxValues2[k] = fluxValues2[k]/pointsThisRange2;
 
            if(pointsThisRange1 == 0){
                if(waveValues1[0] <=sampling[k] && sampling[k] <= waveValues1[waveValues1.length-1]) {
                    int backwardIndex = MathUtils.backwardIndex(waveValues1, sampling[k]);
 
                        fluxValues1[k] = inputFluxValues1[backwardIndex]+((inputFluxValues1[backwardIndex+1]-inputFluxValues1[backwardIndex])/(waveValues1[backwardIndex+1]-waveValues1[backwardIndex]))*(sampling[k]-waveValues1[backwardIndex]);
 
                }
            }
 
            if(pointsThisRange2 == 0){
                if(waveValues2[0] <=sampling[k] && sampling[k] <= waveValues2[waveValues2.length-1]) {
                    int backwardIndex = MathUtils.backwardIndex(waveValues2, sampling[k]);
                    fluxValues2[k] = inputFluxValues2[backwardIndex]+((inputFluxValues2[backwardIndex+1]-inputFluxValues2[backwardIndex])/(waveValues2[backwardIndex+1]-waveValues2[backwardIndex]))*(sampling[k]-waveValues2[backwardIndex]);
                }
            }
 
            k++;
        }//while
 
        this.spectrum1 = new Spectrum();
        this.spectrum2 = new Spectrum();
 
        this.spectrum1.setWaveValues(sampling);
        this.spectrum2.setWaveValues(sampling);
        this.spectrum1.setFluxValues(fluxValues1);
        this.spectrum2.setFluxValues(fluxValues2);
    }
 
    public void initializeData(Spectrum sp1) {
        System.out.println("STARTING INITIALIZE DATA");
 
        OrderedSpectrum orderedSpectrum1 = new OrderedSpectrum(sp1);
 
        double[] waveValues1 = orderedSpectrum1.getWaveValues();
        double[] inputFluxValues1 = orderedSpectrum1.getFluxValues();
 
 
        //range that contains both spectra
        double xMin = waveValues1[0];
        double xMax = waveValues1[waveValues1.length-1];
 
 
        double[] sampling = new double[numOfSamplingPoints];
        double samplingInterval = (xMax-xMin)/(numOfSamplingPoints-1);
 
        for(int k = 0; k < numOfSamplingPoints; k++) {
            sampling[k] = xMin + k*(xMax-xMin)/(numOfSamplingPoints-1);
        }
 
        int i = 0;
        int k = 0;
 
        double[] fluxValues1 = new double[sampling.length];
 
        while(k < sampling.length) {
 
             System.out.println("k = "+k);
            fluxValues1[k] = 0.0;
 
            i = 0;
 
            int pointsThisRange1 = 0;
 
            double xMinThisRange = xMin + (k-0.5)*(xMax-xMin)/(numOfSamplingPoints-1);
            double xMaxThisRange = xMin + (k+0.5)*(xMax-xMin)/(numOfSamplingPoints-1);
 
 
            while(xMinThisRange <= waveValues1[i] && waveValues1[i] < xMaxThisRange ) {
 
 
                fluxValues1[k] = fluxValues1[k] + inputFluxValues1[i];
                pointsThisRange1++;
 
                if(i < waveValues1.length-1) i++;
            }
 
            if(pointsThisRange1 != 0) {
                System.out.println("points this range = "+  pointsThisRange1);
 
                fluxValues1[k] = fluxValues1[k]/pointsThisRange1;
            }
 
            //linear interpolation
            else if(pointsThisRange1 == 0){
                 System.out.println("Linear Interpolation");
                 System.out.println("=====================");
                //if(waveValues1[0] <=sampling[k] && sampling[k] <= waveValues1[waveValues1.length-1]) {
                    int backwardIndex = MathUtils.backwardIndex(waveValues1, sampling[k]);
                    System.out.println("backward Index = " + backwardIndex);
 
                    if(backwardIndex + 1 < waveValues1.length) {
                        fluxValues1[k] = inputFluxValues1[backwardIndex]+((inputFluxValues1[backwardIndex+1]-inputFluxValues1[backwardIndex])/(waveValues1[backwardIndex+1]-waveValues1[backwardIndex]))*(sampling[k]-waveValues1[backwardIndex]);
                    }
                    else{
                        fluxValues1[k] = inputFluxValues1[inputFluxValues1.length-1];
                    }
               //}
            }
 
             System.out.println("fluxValues[+"+k+"] = " +fluxValues1[k]);
 
            k++;
        }//while
 
        this.spectrum1 = new Spectrum();
 
        this.spectrum1.setWaveValues(sampling);
        this.spectrum1.setFluxValues(fluxValues1);
 
    }
 */
    
    
    public double getTransmissionFunction() {
        
        Arithmetics arithmetics = new Arithmetics(spectrum1, spectrum2);
        arithmetics.initializeData();
        Spectrum prod = arithmetics.prod();
        
        double[] sampling = (double[]) spectrum1.getWaveValues();
        double a = sampling[0];
        double b = sampling[sampling.length-1];
        //double integralValue = (new NumIntegration(prod, a, b).getTrapezoidalIntegral());
        
        return (new NumIntegration(prod, a, b).getTrapezoidalIntegral());
    }
    
   /* public double getEquivWidth() {
    
        double[] sampling = (double[]) this.spectrum1.getWaveValues();
        double[] fluxValues1 = (double[]) this.spectrum1.getFluxValues();
        double[] fluxValues2 = new double[sampling.length];
        System.out.println("1");
    
        System.out.println("sampling length in equiv width = "+ sampling.length);
    
        for(int i = 0; i < sampling.length; i++) {
            fluxValues2[i] = fluxValues1[0] + ((fluxValues1[sampling.length-1]-fluxValues1[0])/(sampling[sampling.length-1]-sampling[0]))*(sampling[i]-sampling[0]);
        }
    
        this.spectrum2 = new Spectrum();
    
        this.spectrum2.setWaveValues(sampling);
        this.spectrum2.setFluxValues(fluxValues2);
    
        Spectrum diff = (new Arithmetics(spectrum1, spectrum2)).diff();
        System.out.println("2");
        double[] diffFluxValues = (double[]) diff.getFluxValues();
    
        System.out.println("diff flux values length in equiv width = "+ diffFluxValues.length);
    
        for(int i = 0; i < diffFluxValues.length; i++) {
            diffFluxValues[i] = Math.abs(diffFluxValues[i]);
        }
        System.out.println("3");
    
        diff.setFluxValues(diffFluxValues);
        System.out.println("4");
    
        double area = (double) (new NumIntegration(diff, sampling[0], sampling[sampling.length-1]).getTrapezoidalIntegral());
        double h = (fluxValues1[0]+fluxValues1[fluxValues1.length-1])/2;
        double equivWidth = area/h;
        System.out.println("5");
        return equivWidth;
    }*/
    
    public double getEquivWidth() {
        
        double[] waveValues = (double[]) spectrum1.getWaveValues();
        double[] fluxValues = (double[]) spectrum1.getFluxValues();
        double[] continuum  = new double[fluxValues.length];
        
        //continuum is the line croosing initial and final points of the selected gaussian
        for(int i = 0; i < waveValues.length; i++) {
            continuum[i] = fluxValues[0] + ((fluxValues[waveValues.length-1]-fluxValues[0])/(waveValues[waveValues.length-1]-waveValues[0]))*(waveValues[i]-waveValues[0]);
        }
        
        this.spectrum2 = new Spectrum();
        
        //spectrum2 == continuum
        this.spectrum2.setWaveValues(waveValues);
        this.spectrum2.setFluxValues(continuum);
        
        //Not necessary to initialize Arithmetics data because both spectra are defined in the same waveValues!!
        Spectrum diff = (new Arithmetics(spectrum2, spectrum1)).diff();
        
        //Normalizing to unity
        double[] diffFluxValues = (double[]) diff.getFluxValues();
        
        //emission spectral lines have negative eq. width(spectrum above continuum)
        for(int i = 0; i < diffFluxValues.length; i++) {
            diffFluxValues[i] = diffFluxValues[i]/continuum[i];
        }
        
        
        //double[] diffFluxValues = (double[]) diff.getFluxValues();
        
        //System.out.println("diff flux values length in equiv width = "+ diffFluxValues.length);
        
        //emission spectral lines have negative eq. width(spectrum above continuum)
/*        for(int i = 0; i < diffFluxValues.length; i++) {
            diffFluxValues[i] = Math.abs(diffFluxValues[i]);
        }
 */
        diff.setFluxValues(diffFluxValues);
        
        /*getLineFlux is not used here because it calculates integral(abs(Fcontinuum-Flambda)) and here we need 
         *integral(Fcontinuum-Flambda)
         */ 
        double flux = (double) (new NumIntegration(diff, waveValues[0], waveValues[waveValues.length-1]).getTrapezoidalIntegral());
        
        // h is the value of the continuum at the center of the line
        //double h = (fluxValues[0]+fluxValues[fluxValues.length-1])/2;
        
        //double h = 1;//if the spectrum is normalized to unity
        //if(h==0) ??
        
        //double equivWidth = flux/h;
        double equivWidth = flux;
        
        return equivWidth;
    }
    
    
    
    public double getLineFlux() {
        
        double[] waveValues = (double[]) spectrum1.getWaveValues();
        double[] fluxValues = (double[]) spectrum1.getFluxValues();
        double[] continuum  = new double[fluxValues.length];
        
        //continuum is the line croosing initial and final points of the selected gaussian
        for(int i = 0; i < waveValues.length; i++) {
            continuum[i] = fluxValues[0] + ((fluxValues[waveValues.length-1]-fluxValues[0])/(waveValues[waveValues.length-1]-waveValues[0]))*(waveValues[i]-waveValues[0]);
        }
        
        //spectrum2 == linear continuum
        this.spectrum2 = new Spectrum();
        
        this.spectrum2.setWaveValues(waveValues);
        this.spectrum2.setFluxValues(continuum);
        
        //Not necessary to initialize Arithmetics data because both spectra are defined in the same waveValues
        Spectrum diff = (new Arithmetics(spectrum2, spectrum1)).diff();
        
        //Normalizing to unity
        double[] diffFluxValues = (double[]) diff.getFluxValues();
        
        //emission spectral lines have negative eq. width(spectrum above continuum)
        for(int i = 0; i < diffFluxValues.length; i++) {
            diffFluxValues[i] = Math.abs(diffFluxValues[i]);
        }
                
       
        diff.setFluxValues(diffFluxValues);
        
        double flux = (double) (new NumIntegration(diff, waveValues[0], waveValues[waveValues.length-1]).getTrapezoidalIntegral());
                
        
        return flux;
    }
    
    
    public double getIntegratedFlux() {
        System.out.println("STARTING INTEGRATED FLUX");
        
        //double[] sampling = (double[]) spectrum1.getWaveValues();
        //double a = sampling[0];
        //double b = sampling[sampling.length-1];
        
        System.out.println("STARTING NUMERICAL INTEGRATION");
        //return (new NumIntegration(spectrum1, a, b).getTrapezoidalIntegral());
        
        double integral = new NumIntegration(spectrum1).getTrapezoidalIntegral();
        
        return Math.abs(integral);
    }
}
