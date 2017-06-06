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

public class Mirroring {
    
    //int gaussianAxisIndex; /*This index can be evaluated using getGaussianAxisIndex method
                             /*It's also evaluated when calling getMirroredGaussian() */
            
    double[] inputWaveValues;
    double[] inputFluxValues;
    
    double   refWavelength;
    double   axis;
    
    Spectrum spectrum;
    //SpectralLine spectralLine;
    
    /** Creates a new instance of Bisector */
    public Mirroring() {
    }
    
    public Mirroring(Spectrum spectrum){
        orderSpectrum(spectrum);
        
        //this.spectralLine = new SpectralLine(spectrum);
        
        //SpectralLine class returns an already ordered spectrum :)
        //this.inputWaveValues = this.spectralLine.getWaveValues();
        //this.inputFluxValues = this.spectralLine.getFluxValues();
    }
    
       
    /*Orders input spectrum */
    public void orderSpectrum(Spectrum spectrum){
        OrderedSpectrum orderedSpectrum = new OrderedSpectrum(spectrum);
        
        this.inputWaveValues = orderedSpectrum.getWaveValues();
        this.inputFluxValues = orderedSpectrum.getFluxValues();
        this.spectrum = orderedSpectrum;
    }
    
    /*
     *Evaluates the mirrored line with respect to the axis of the given line
     */
    public Spectrum getMirroredLine() {
        
        double[] mirroredWaveValues = new double[inputWaveValues.length];
        double[] mirroredFluxValues = new double[inputWaveValues.length];
        
        SpectralLine spectralLine = new SpectralLine(this.spectrum, true);
        
        int peakPosition = (int) spectralLine.getPeakPosition();
        
        this.axis = inputWaveValues[peakPosition];
        
        int i = 0;

        for(int j = inputWaveValues.length-1; j > -1 ; j--){
            mirroredWaveValues[inputWaveValues.length-1-j] = 2*inputWaveValues[peakPosition] - inputWaveValues[j];
            mirroredFluxValues[inputWaveValues.length-1-j] = inputFluxValues[j];
        }
        
        Spectrum mirroredSpectrum = new Spectrum();
        
        mirroredSpectrum.setWaveValues(mirroredWaveValues);
        mirroredSpectrum.setFluxValues(mirroredFluxValues);
        
        return mirroredSpectrum;
    }
    
    /*
     *Evaluates a mirrored gaussian with respect to the input axis
     */
/*    public Spectrum getMirroredGaussian(double axis){
 
        //Spectrum mirroredSpectrum = (Spectrum) getMirroredGaussian();   //this method calls getGaussianAxisIndex
 
        //double[] mirroredWaveValues = (double[]) mirroredSpectrum.getWaveValues();
        //double[] mirroredFluxValues = (double[]) mirroredSpectrum.getFluxValues();
 
        double[] mirroredWaveValues = (double[]) spectrum.getWaveValues();
        double[] mirroredFluxValues = (double[]) spectrum.getFluxValues();
 
        //translation to the new axis of the already mirrored gaussian
        for(int k = 0; k < mirroredWaveValues.length; k++){
            //gaussianAxisIndex has been already calculated in getMirroredGaussian method
            double displacement = axis - inputWaveValues[this.gaussianAxisIndex];
            mirroredWaveValues[k] = mirroredWaveValues[k] + displacement ;
        }
 
        Spectrum mirroredSpectrum = new Spectrum();
 
        mirroredSpectrum.setWaveValues(mirroredWaveValues);
        mirroredSpectrum.setFluxValues(mirroredFluxValues);
 
        return mirroredSpectrum;
 
    }
 */
    
    /*
     *Returns a mirrored spectrum with respect to the input axis
     */
    public Spectrum getMirroredSpectrum(double axis){
        
        this.axis = axis;
        
        System.out.println("axis value in Mirroring.java = "+this.axis);
        
        double[] mirroredWaveValues = new double[inputWaveValues.length];
        double[] mirroredFluxValues = new double[inputWaveValues.length];
      
        for(int j = inputWaveValues.length-1; j > -1 ; j--){
          
            //mirroredWaveValues[inputWaveValues.length-1-j] = 2*inputWaveValues[axisIndex] - inputWaveValues[j];
            mirroredWaveValues[inputWaveValues.length-1-j] = 2*axis - inputWaveValues[j];
            mirroredFluxValues[inputWaveValues.length-1-j] = inputFluxValues[j];
            
            int cont = inputWaveValues.length-1-j;
            
        }
        
        Spectrum mirroredSpectrum = new Spectrum();
        
        mirroredSpectrum.setWaveValues(mirroredWaveValues);
        mirroredSpectrum.setFluxValues(mirroredFluxValues);
        
        return mirroredSpectrum;
    }
    
    /*
     *If there isn't input axis, default axis = mid-point of the range where
     *the spectrum is defined
     */
    public Spectrum getMirroredSpectrum(){
        
        this.axis = (inputWaveValues[0]+inputWaveValues[inputWaveValues.length-1])/2;
        
        return getMirroredSpectrum(axis);
    }
    
   
    
    public double getAxis(){
        return this.axis;
    }

}


