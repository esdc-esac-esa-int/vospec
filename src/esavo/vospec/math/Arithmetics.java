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
 * Arithmetics.java
 *
 * Created on March 30, 2007, 11:51 AM
 *
 */

/**
 *
 * @author Andrea Laruelo
 */

import esavo.vospec.spectrum.*;



public class Arithmetics {
    
    double  cte;
    
    //boolean multivalued = false;
    boolean  disjoint = false;
    
    Spectrum spectrum1;
    Spectrum spectrum2;
    
    /** Creates a new instance of Arithmetics */
    public Arithmetics() {
    }
    
    public Arithmetics(Spectrum spectrum1, Spectrum spectrum2) {
        
//        this.spectrum1 = (Spectrum) (new Smoothing()).multivaluedSpectrumSmoothing(spectrum1);
//        boolean multivalued1 = Smoothing.multivalued;
// 
//        this.spectrum2 = (Spectrum) (new Smoothing()).multivaluedSpectrumSmoothing(spectrum2);
//        boolean multivalued2 = Smoothing.multivalued;
        
        this.spectrum1 = spectrum1;
        this.spectrum2 = spectrum2;
       
    }
    
    public Arithmetics(Spectrum inputSpectrum1, double cte) {
        this.spectrum1 = inputSpectrum1;
        this.cte = cte;
        
    }
    
    
    
    public void initializeData(){
        
        OrderedSpectrum orderedSpectrum1 = new OrderedSpectrum(spectrum1);
        OrderedSpectrum orderedSpectrum2 = new OrderedSpectrum(spectrum2);
        
        double[] inputWaveValues1 = orderedSpectrum1.getWaveValues();
        double[] inputFluxValues1 = orderedSpectrum1.getFluxValues();
        
        double[] inputWaveValues2 = orderedSpectrum2.getWaveValues();
        double[] inputFluxValues2 = orderedSpectrum2.getFluxValues();
        
        double xMin = Math.max(inputWaveValues1[0], inputWaveValues2[0]);
        double xMax = Math.min(inputWaveValues1[inputWaveValues1.length-1], inputWaveValues2[inputWaveValues2.length-1]);
        
        System.out.println("xmin spec1= "+inputWaveValues1[0]+"xmin spec2= "+inputWaveValues2[0]);
        
        int i = 0;
        int j = 0;
        int k = 0;
        int numPoints = 0;
        
        //-1 entries for out of range and repeated waveValues
        double[] samplingAux = new double[inputWaveValues1.length + inputWaveValues2.length];
        
        while(i < inputWaveValues1.length) {
            if(xMin <= inputWaveValues1[i] && inputWaveValues1[i] <= xMax){
                samplingAux[k] = inputWaveValues1[i];
                numPoints++;
                
            }else{
                samplingAux[k]= -1;
            }
            
            i++;
            k++;
        }
        
        while(j < inputWaveValues2.length) {
            
            if(xMin <= inputWaveValues2[j] && inputWaveValues2[j] <= xMax){
                if((boolean) MathUtils.contains(inputWaveValues1, inputWaveValues2[j]) == false){
                    samplingAux[k] = inputWaveValues2[j];
                    numPoints++;
                }else{
                    samplingAux[k] = -1;
                }
            }else{
                samplingAux[k] = -1;
            }
            
            j++;
            k++;
        }
        
        
        double[] sampling = new double[numPoints];
        
        i = 0;
        
        for(int l = 0; l < samplingAux.length; l++){
            if(xMin <=samplingAux[l] && samplingAux[l]<=xMax && samplingAux[l] != -1){
                sampling[i] = samplingAux[l];
                
                i++;
            }
            
        }
        
        double[] fluxValues1 = new double[sampling.length];
        double[] fluxValues2 = new double[sampling.length];
        
        i = 0;
        j = 0;
        k = 0;
        
        int commonSamples = sampling.length;
        
        if(commonSamples==0) disjoint = true;
        
        sampling = (double[]) MathUtils.orderedArray(sampling);
        
        this.spectrum1 = MathUtils.linearInterpolation(spectrum1, sampling);
        this.spectrum2 = MathUtils.linearInterpolation(spectrum2, sampling);
        
    }
    
    
    public Spectrum sum() {
        
        Spectrum sum = new Spectrum();
        
        double[] sampling = (double[]) this.spectrum1.getWaveValues();
        double[] fluxValues1 = (double[]) this.spectrum1.getFluxValues();
        double[] fluxValues2 = (double[]) this.spectrum2.getFluxValues();
        double[] fluxValues = new double[sampling.length];
        
        for(int k = 0; k < sampling.length; k++) {
            fluxValues[k] = fluxValues1[k] + fluxValues2[k];
        }
        
        sum.setWaveValues(sampling);
        sum.setFluxValues(fluxValues);
        
        
        return sum;
    }
    
    
    /*spectrum1 - spectrum2*/
    public Spectrum diff() {
        
        Spectrum diff = new Spectrum();
        
        double[] sampling = (double[]) spectrum1.getWaveValues();
        double[] fluxValues1 = (double[]) spectrum1.getFluxValues();
        double[] fluxValues2 = (double[]) spectrum2.getFluxValues();
        double[] fluxValues = new double[sampling.length];
        
        
        for(int k = 0; k < sampling.length; k++) {
            fluxValues[k] = fluxValues1[k] - fluxValues2[k];
        }
        
        diff.setWaveValues(sampling);
        diff.setFluxValues(fluxValues);
        
        return diff;
    }
  
    public Spectrum prod() {
        
        Spectrum prod = new Spectrum();
        
        double[] sampling = (double[]) spectrum1.getWaveValues();
        double[] fluxValues1 = (double[]) spectrum1.getFluxValues();
        double[] fluxValues2 = (double[]) spectrum2.getFluxValues();
        double[] fluxValues = new double[sampling.length];
        
        
        for(int k = 0; k < sampling.length; k++) {
            fluxValues[k] = fluxValues1[k] * fluxValues2[k];
        }
        
        prod.setWaveValues(sampling);
        prod.setFluxValues(fluxValues);
        
        return prod;
    }
    
    
    public Spectrum div() {
        
        Spectrum div = new Spectrum();
        
        double[] sampling = (double[]) spectrum1.getWaveValues();
        double[] fluxValues1 = (double[]) spectrum1.getFluxValues();
        double[] fluxValues2 = (double[]) spectrum2.getFluxValues();
        double[] fluxValues = new double[sampling.length];
        
        double[] divFluxValues = new double[sampling.length];
        
        int k = 0;
        
        while(k < sampling.length){
            
            if(fluxValues2[k] != 0) divFluxValues[k] = fluxValues1[k]/fluxValues2[k];
            else {
                System.out.println("Divisor spectrum contains zero values!!!!");
                divFluxValues[k] = 0.0;
                //k = sampling.length + 1;//exit
            }
            
            k++;
        }
        
        div.setWaveValues(sampling);
        div.setFluxValues(divFluxValues);
        
        return MathUtils.rejectZeros(div);
    }
    
    public Spectrum sumConstant() {
        
        Spectrum sum = new Spectrum();
        
        double[] inputWaveValues = (double[]) this.spectrum1.getWaveValues();
        double[] inputFluxValues = (double[]) this.spectrum1.getFluxValues();
        
        double[] fluxValues = new double[inputWaveValues.length];
        
        for(int k = 0; k < fluxValues.length; k++) {
            fluxValues[k] = inputFluxValues[k] + cte;
        }
        
        sum.setWaveValues(inputWaveValues);
        sum.setFluxValues(fluxValues);
        
        //return sum;
        return sum;
    }
    
    public Spectrum substractConstant() {
        
        Spectrum diff = new Spectrum();
        
        double[] inputWaveValues = (double[]) spectrum1.getWaveValues();
        double[] inputFluxValues = (double[]) spectrum1.getFluxValues();
        
        double[] fluxValues = new double[inputWaveValues.length];
        
        for(int k = 0; k < fluxValues.length; k++) {
            fluxValues[k] = inputFluxValues[k] - cte;
        }
        
        diff.setWaveValues(inputWaveValues);
        diff.setFluxValues(fluxValues);
        
        
        return diff;
    }
    
    public Spectrum multConstant() {
        
        Spectrum prod = new Spectrum();
        
        double[] inputWaveValues = (double[]) spectrum1.getWaveValues();
        double[] inputFluxValues = (double[]) spectrum1.getFluxValues();
        
        double[] fluxValues = new double[inputWaveValues.length];
        
        for(int k = 0; k < fluxValues.length; k++) {
            
            fluxValues[k] = inputFluxValues[k] * cte;
        }
        
        prod.setWaveValues(inputWaveValues);
        prod.setFluxValues(fluxValues);
        
        return prod;
    }
    
    public Spectrum divConstant() {
        
        Spectrum div = new Spectrum();
        
        double[] inputWaveValues = (double[]) spectrum1.getWaveValues();
        double[] inputFluxValues = (double[]) spectrum1.getFluxValues();
        
        double[] fluxValues = new double[inputWaveValues.length];
        
 
        for(int k = 0; k < fluxValues.length; k++) {
            try{
                fluxValues[k] = inputFluxValues[k] / cte;
            }catch(Exception e){
                System.out.println("Zero divisor!!!!");
            }
        }
        
        
        div.setWaveValues(inputWaveValues);
        div.setFluxValues(fluxValues);
        
        return div;
    }
    
    public boolean disjointSpectra(){
        return disjoint;
    }
    
}

