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
 * DiscreteConvolution.java
 *
 * Created on April 2, 2007, 4:05 PM
 *
 */

/**
 *
 * @author Andrea Laruelo
 */

import esavo.vospec.spectrum.*;


public class DiscreteConvolution {
    
    int firstIndex1;
    int lastIndex1;
    int firstIndex2;
    int lastIndex2;
    int spectrumWithMinRange;
    
    double  xMin;
    double  xMax;
    double  samplingInterval;
    
    double[] sampling;
    //double[] fluxValues1;
    //double[] fluxValues2;
    
    Spectrum spectrum1;
    Spectrum spectrum2;
    
    
    /** Creates a new instance of DiscreteConvolution */
    public DiscreteConvolution() {
        
    }
    
    //public DiscreteConvolution(double[] array1, double[] array2) {
    // }
    
    public DiscreteConvolution(Spectrum spectrum1, Spectrum spectrum2) {
        this.spectrum1 = spectrum1;
        this.spectrum2 = spectrum2;
        
        initializeData();
        
    }
    
    
 /*   public void initializeData() {
        OrderedSpectrum orderedSpectrum1 = new OrderedSpectrum(spectrum1);
        OrderedSpectrum orderedSpectrum2 = new OrderedSpectrum(spectrum2);
  
        double[] waveValues1        = orderedSpectrum1.getWaveValues();
        double[] inputFluxValues1   = orderedSpectrum1.getFluxValues();
  
        double[] waveValues2        = orderedSpectrum2.getWaveValues();
        double[] inputFluxValues2   = orderedSpectrum2.getFluxValues();
  
        //range that contains both spectra
        xMin = Math.min(waveValues1[0], waveValues2[0]);
        xMax = Math.max(waveValues1[waveValues1.length-1], waveValues2[waveValues2.length-1]);
  
  
        //To eval sampling with such points as number of different wave values
        int numPoints = waveValues1.length;
        int l = 0;
  
        while(l < waveValues2.length) {
            if(MathUtils.contains(waveValues1, waveValues2[l]) == false){
                numPoints ++;
  
                l++;
            }
        }
  
        int numOfSamplingPoints = numPoints;
  
        sampling = new double[numOfSamplingPoints];
        samplingInterval = (xMax-xMin)/(numOfSamplingPoints-1);
  
        for(int k = 0; k < numOfSamplingPoints; k++) {
            sampling[k] = xMin + k*(xMax-xMin)/(numOfSamplingPoints-1);
        }
  
        int i = 0;
        int j = 0;
        //int k = 0;
  
        fluxValues1 = new double[sampling.length];
        fluxValues2 = new double[sampling.length];
  
        for(int k = 0; k < sampling.length; k++) {
  
            fluxValues1[k] = 0.0;
            fluxValues2[k] = 0.0;
  
            int pointsThisRange1 = 0;
            int pointsThisRange2 = 0;
  
            double xMinThisRange = xMin + (k-0.5)*(xMax-xMin)/(numOfSamplingPoints-1);
            double xMaxThisRange = xMin + (k+0.5)*(xMax-xMin)/(numOfSamplingPoints-1);
  
            while(xMinThisRange <= waveValues1[i] && waveValues1[i] < xMaxThisRange ) {
  
                if(i==0) firstIndex1 = k;
                if(i==waveValues1.length-1) lastIndex1 = k;
  
  
                fluxValues1[k] = fluxValues1[k] + inputFluxValues1[i];
                pointsThisRange1++;
  
                if(i < waveValues1.length-1) i++;
            }
  
            while(xMinThisRange <= waveValues2[j] && waveValues2[j] < xMaxThisRange ) {
  
                if(j==0) firstIndex2 = k;
                if(j==waveValues2.length-1) lastIndex2 = k;
  
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
  
            //k++;
        }//for k
  
  
    }
  */
    public void initializeData(){
        
        OrderedSpectrum orderedSpectrum1 = new OrderedSpectrum(this.spectrum1);
        OrderedSpectrum orderedSpectrum2 = new OrderedSpectrum(this.spectrum2);
        
        double[] waveValues1        = orderedSpectrum1.getWaveValues();
        double[] inputFluxValues1   = orderedSpectrum1.getFluxValues();
        
        double[] waveValues2        = orderedSpectrum2.getWaveValues();
        double[] inputFluxValues2   = orderedSpectrum2.getFluxValues();
        
        
/*        for(int j = 0; j < inputFluxValues1.length; j++) {
            System.out.println("fluxValues1 in orderedSpectrum1 = "+ inputFluxValues1[j]);
        }
 
        for(int j = 0; j < inputFluxValues2.length; j++) {
            System.out.println("fluxValues2 in orderedSpectrum2 = "+ inputFluxValues2[j]);
        }
 
 */
        
        double range1 = Math.abs(waveValues1[waveValues1.length-1]-waveValues1[0]);
        double range2 = Math.abs(waveValues2[waveValues2.length-1]-waveValues2[0]);
        
        if(range1 <= range2) this.spectrumWithMinRange = 1;
        else                 this.spectrumWithMinRange = 2;
        
        System.out.println("spectrum with min range = "+this.spectrumWithMinRange);
        
        //range that contains both spectra
        this.xMin = Math.min(waveValues1[0], waveValues2[0]);
        this.xMax = Math.max(waveValues1[waveValues1.length-1], waveValues2[waveValues2.length-1]);
        
        //TESTING!!!!!!!!!!
//        this.xMin = waveValues1[0];
//        this.xMax = waveValues1[waveValues1.length-1];
        
        System.out.println("xMin = "+this.xMin+" xMax = "+xMax);
        
        this.sampling = new double[1001];
        
        for(int k = 0; k < sampling.length; k++){
            this.sampling[k] = this.xMin + k*(this.xMax-this.xMin)/1000;
        }
        
        this.samplingInterval = (this.xMax-this.xMin)/1000;
        
        int sampling1Length = 0;
        int sampling2Length = 0;
        
        boolean first = true;
        
        for(int k = 0; k < sampling.length; k++) {
            if(waveValues1[0] <= sampling[k] && sampling[k] <= waveValues1[waveValues1.length-1]){
                if(first == true){
                    this.firstIndex1 = k;
                    first = false;
                }
                
                this.lastIndex1 = k;
                
                sampling1Length++ ;
            }
        }
        
        first = true;
        
        for(int k = 0; k < sampling.length; k++){
            if(waveValues2[0]<= sampling[k] && sampling[k]<=waveValues2[waveValues2.length-1]){
                if(first == true){
                    this.firstIndex2 = k;
                    first = false;
                }
                
                this.lastIndex2 = k;
                
                sampling2Length++ ;
            }
        }
        
        double[] sampling1 = new double[lastIndex1-firstIndex1+1];
        double[] sampling2 = new double[lastIndex2-firstIndex2+1];
        
        int cont = 0;
        
        for(int k = 0; k <= lastIndex1 - firstIndex1; k++){
            sampling1[cont] = sampling[firstIndex1+k];
            cont++;
        }
        
        cont = 0;
        
        for(int k = 0; k <= lastIndex2 - firstIndex2; k++){
            sampling2[cont] = sampling[firstIndex2+k];
            cont++;
        }
        
        System.out.println("sampling1 length = "+ sampling1.length);
        System.out.println("sampling2 length = "+ sampling2.length);
        this.spectrum1 = MathUtils.linearInterpolation(orderedSpectrum1, sampling1);
        this.spectrum2 = MathUtils.linearInterpolation(orderedSpectrum2, sampling2);
        
        //TESTING!!!!!!!
//        this.spectrum2.setWaveValues(sampling1);
        
        
        //this.spectrum1 = MathUtils.rejectZeros(this.spectrum1);
        //this.spectrum2 = MathUtils.rejectZeros(this.spectrum2);
    }
    
    public Spectrum getConvolution() {
        
        //System.out.println("sampling interval = "+samplingInterval);
        
        //this.samplingInterval = 1;
        
        //double[] sampling = {0, 1, 2, 3};
        
        //double[] fluxValues1Aux = {2, -2, 1};
        //double[] fluxValues2Aux = {1, 3, 0.5, -1};
        
        //double[] convolutionFluxValues = new double[sampling.length];
        
        double[] sampling1;
        double[] fluxValues1Aux;
        
        double[] sampling2;
        double[] fluxValues2Aux;
        
        
        if(this.spectrumWithMinRange == 1){
            sampling1        = this.spectrum2.getWaveValues();
            fluxValues1Aux   = this.spectrum2.getFluxValues();
            
            sampling2        = this.spectrum1.getWaveValues();
            fluxValues2Aux   = this.spectrum1.getFluxValues();
        }else{
            sampling1        = this.spectrum1.getWaveValues();
            fluxValues1Aux   = this.spectrum1.getFluxValues();
            
            sampling2        = this.spectrum2.getWaveValues();
            fluxValues2Aux   = this.spectrum2.getFluxValues();
        }
        
        double[] convolutionFluxValues = new double[sampling1.length+sampling2.length-1];
        
        
//      double intFlux = (double) (new IntegralTools(this.spectrum1)).getIntegratedFlux();
        
      /*  double area = 0;
       
          for(int k = 0; k < fluxValues1Aux.length ; k++) {
       
                area = area + fluxValues1Aux[k]*this.samplingInterval;
          }
       */
        int index = (int) Math.ceil(fluxValues1Aux.length/2);
        
        for(int j = 0; j < convolutionFluxValues.length; j++) {
            convolutionFluxValues[j]  = 0.0;
            
            // for(int k = firstIndex1; k <= lastIndex1 ; k++) {
            //     if(firstIndex2 <= j-k && j-k <= lastIndex2) {
            //         convolutionFluxValues[j] = convolutionFluxValues[j] + fluxValues1[k]*fluxValues2[j-k];
            //     }
            //}
            
            //int indexMaxFlux = MathUtils.maxFluxValueIndex(fluxValues1Aux);
            int denominator = 0;
            
            double area = 0;
            
            
            for(int k = 0; k < fluxValues1Aux.length ; k++) {
                
                if(0 <= j-k && j-k < fluxValues2Aux.length) {
                    
                    convolutionFluxValues[j] = convolutionFluxValues[j] + fluxValues1Aux[k]*fluxValues2Aux[j-k];
                    
                    area = area + fluxValues2Aux[j-k]*this.samplingInterval;
//                  area = area + fluxValues1Aux[k]*this.samplingInterval;
                    
                    denominator++;
                    
                }
            }
            
            convolutionFluxValues[j] = convolutionFluxValues[j]*this.samplingInterval/area;
//            convolutionFluxValues[j] = convolutionFluxValues[j]*this.samplingInterval;
        }
        
        
        //for(int k = 0; k < )
        
        
  /*      for(int j = 0 ; j < convolutionFluxValues.length-index; j++){
   
            convolutionFluxValues[j] = convolutionFluxValues[j+index];
   
            System.out.println("convolution values = "+convolutionFluxValues[j]);
        }
   */
        double[] convolutionWaveValues = new double[convolutionFluxValues.length];
        
        for(int i = 0; i < convolutionWaveValues.length; i++){
            convolutionWaveValues[i] = this.xMin+i*this.samplingInterval;
        }
        
        Spectrum convolution = new Spectrum();
        
        convolution.setWaveValues(convolutionWaveValues);
        convolution.setFluxValues(convolutionFluxValues);
        
        return convolution;
        //return this.spectrum1;
    }
    
    
    
    
    public Spectrum convolutionToTest(Spectrum spec, Spectrum spec2){
        
        OrderedSpectrum orderedSpectrum1 = new OrderedSpectrum(spec);
        OrderedSpectrum orderedSpectrum2 = new OrderedSpectrum(spec2);
        
/*      double[] waveValues1    = orderedSpectrum1.getWaveValues();
        double[] waveValues2    = orderedSpectrum2.getWaveValues();
 
        double[] fluxValues1      = orderedSpectrum1.getFluxValues();
        double[] fluxValues2      = orderedSpectrum2.getFluxValues();
 */
        double[] inputWaveValues1    = orderedSpectrum1.getWaveValues();
        double[] inputWaveValues2    = orderedSpectrum2.getWaveValues();
        
        double[] inputFluxValues1      = orderedSpectrum1.getFluxValues();
        double[] inputFluxValues2      = orderedSpectrum2.getFluxValues();
        
//TO CONSIDER RUNNING SPECTRUM == SPECTRUM WITH SMALLER RANGE OF DEFINITION
/*      double range1 = Math.abs(inputWaveValues1[inputWaveValues1.length-1]-inputWaveValues1[0]);
        double range2 = Math.abs(inputWaveValues2[inputWaveValues2.length-1]-inputWaveValues2[0]);
        
        Spectrum orderedSpec1 = new Spectrum();
        Spectrum orderedSpec2 = new Spectrum();
 
        //the running spectrum is the spectrum with smaller range of definition(spectrum2 by default)
        if(range1 < range2){
 
            System.out.println("range1 < range2!!!!!");
 
 
            orderedSpec1.setWaveValues(waveValues2);
            orderedSpec1.setFluxValues(fluxValues2);
 
            orderedSpec2.setWaveValues(waveValues1);
            orderedSpec2.setFluxValues(fluxValues1);
        }else{
            orderedSpec1.setWaveValues(waveValues1);
            orderedSpec1.setFluxValues(fluxValues1);
 
            orderedSpec2.setWaveValues(waveValues2);
            orderedSpec2.setFluxValues(fluxValues2);
        }
 
        double[] inputWaveValues1    = orderedSpec1.getWaveValues();
        double[] inputWaveValues2    = orderedSpec2.getWaveValues();
        
        double[] inputFluxValues1    = orderedSpec1.getFluxValues();
        double[] inputFluxValues2    = orderedSpec2.getFluxValues();
*/        
        double[] convolvedFluxValues = new double[inputWaveValues1.length];
        
        int midPosition = (int) Math.floor(inputWaveValues2.length/2);
        
        
        for(int i = 0; i < inputWaveValues1.length; i++){
            int numPoints = 0;
            int firstIndex = 0;
            int lastIndex = inputWaveValues2.length-1;
            
            boolean first = false;
//          boolean last  = false;
            
            double area = 0;
            
            convolvedFluxValues[i] = 0;
	    double denom = 0;
	    
            for(int k = 0; k < inputWaveValues2.length; k++){
                if(0 <= i- midPosition + k && i- midPosition + k < inputWaveValues1.length){
                    convolvedFluxValues[i] = convolvedFluxValues[i] + inputFluxValues1[i-midPosition+k]*inputFluxValues2[k];
                    
                    if(first == false)  {
                        firstIndex = k;
                        
                        first = true;
                    }
                    
                    //lastIndex will have the last value of k that verifies the if condition
                    lastIndex = k;
                    
                    numPoints++;
		    denom = denom + inputFluxValues2[k];
                    
                }//if
            }//for k
            
//            double range = Math.abs(inputWaveValues2[lastIndex]-inputWaveValues2[firstIndex])/numPoints;
//            
//            double runningArea = 0;
//            
//            for(int j = firstIndex; j <= lastIndex; j ++) runningArea = runningArea + inputFluxValues2[j]*range;
//            
//            convolvedFluxValues[i] = convolvedFluxValues[i]*range/runningArea;
            
            convolvedFluxValues[i] = convolvedFluxValues[i]/denom;
 
        }//for i
        
        Spectrum convolution = new Spectrum();
        
        convolution.setWaveValues(inputWaveValues1);
        convolution.setFluxValues(convolvedFluxValues);
        
        return convolution;
    }
    
    
    public Spectrum runningConvolution(Spectrum spec, Spectrum spec2){
        
        System.out.println("NEW VERSION2");
        
        OrderedSpectrum orderedSpectrum1 = new OrderedSpectrum(spec);
        OrderedSpectrum orderedSpectrum2 = new OrderedSpectrum(spec2);
        
        
//        double[] inputFluxValues1   = orderedSpectrum1.getFluxValues();
//        double[] inputFluxValues2   = orderedSpectrum2.getFluxValues();
        
        double[] inputWaveValues1   = orderedSpectrum1.getWaveValues();
        double[] inputWaveValues2   = orderedSpectrum2.getWaveValues();
        
        
/*        double range1 = Math.abs(inputWaveValues1[inputWaveValues1.length-1]-inputWaveValues1[0]);
        double range2 = Math.abs(inputWaveValues2[inputWaveValues2.length-1]-inputWaveValues2[0]);
 
 
        if(range1 <= range2) {
            //running spectrum == spectrum1
            orderedSpectrum1 = new OrderedSpectrum(spec2);
            orderedSpectrum2 = new OrderedSpectrum(spec);
 
            inputWaveValues1   = orderedSpectrum1.getWaveValues();
            inputWaveValues2   = orderedSpectrum2.getWaveValues();
 
        }//else the previous initialized ordered spectrum1 and orderedSpectrum2 are not modified
 */
        double[] inputFluxValues1   = orderedSpectrum1.getFluxValues();
        double[] inputFluxValues2   = orderedSpectrum2.getFluxValues();
        
        
        double[] convolvedFluxValues = new double[inputFluxValues1.length];
        
        for(int k = 0; k < inputWaveValues1.length; k++){
            
            double[] runningWaveValues = new double[inputWaveValues2.length];
            
            int midPointApproxPosition = (int) Math.floor(inputWaveValues2.length/2);
            
            double translation = inputWaveValues1[k] - inputWaveValues2[midPointApproxPosition];
            
            //spectrum sub-region translated to inputFluxValues[k]
            for(int j = 0; j < inputWaveValues2.length; j++){
                runningWaveValues[j] = inputWaveValues2[j] + translation;
                
            }
            
//            Spectrum runningSpectrum = new Spectrum();
            
//            runningSpectrum.setWaveValues(runningWaveValues);
//            runningSpectrum.setFluxValues(inputFluxValues2);
            
            Spectrum baseSpectrum = MathUtils.linearInterpolation(orderedSpectrum1, runningWaveValues);
            
            convolvedFluxValues[k] = 0;
            
            int firstIndex = 0;
            int lastIndex = runningWaveValues.length-1;
            
            boolean first = false;
            boolean last = false;
            
            for(int j = 0; j < runningWaveValues.length; j++){
                if(inputWaveValues1[0] <= runningWaveValues[j] & first == false){
                    first = true;
                    firstIndex = j;
                }
                if(runningWaveValues[j] > inputWaveValues1[inputWaveValues1.length-1] & last == false){
                    last = true;
                    //runningWaveValues[j-1] <= inputWaveValues1[inputWaveValues1.length-1]
                    lastIndex = j-1;
                }
            }
            
            
//            double xMin = runningWaveValues[firstIndex];
//            double xMax = runningWaveValues[lastIndex];
            
//            double[] sampling = new double[1000];
            
//            for(int ii = 0; ii < sampling.length; ii++){
//                sampling[ii]= xMin + ii*(xMax-xMin)/999;
//            }
            
            //within input base spectrum range
            int numPoints = lastIndex-firstIndex+1;
            
            
            double range = Math.abs(runningWaveValues[lastIndex]-runningWaveValues[firstIndex])/numPoints;
            //double range = Math.abs(runningWaveValues[runningWaveValues.length-1]-runningWaveValues[0])/numPoints;
            
//            System.out.println("range = "+range);
            double runningArea = 0;
            
//            Spectrum runningSpec = MathUtils.linearInterpolation(spec2, sampling);
//            double[] runningFluxValues = runningSpec.getFluxValues();
            
//            Spectrum baseSpectrum = MathUtils.linearInterpolation(spec, sampling);
            double[] baseSpectrumFluxValues = baseSpectrum.getFluxValues();
            
            for(int ii = firstIndex; ii <= lastIndex; ii ++){
                //for(int ii = 0; ii < runningWaveValues.length; ii ++){
                runningArea = runningArea + inputFluxValues2[ii]*range;
                
                convolvedFluxValues[k] = convolvedFluxValues[k] + baseSpectrumFluxValues[ii]*inputFluxValues2[ii];
                
//                runningArea = runningArea + runningFluxValues[ii]*range;
                
//                convolvedFluxValues[k] = convolvedFluxValues[k] + baseSpectrumFluxValues[ii]*runningFluxValues[ii];
            }
            
            //gaussianArea = Math.abs(gaussianArea*(1/(sigma*Math.sqrt(2*Math.PI))));
            runningArea = Math.abs(runningArea);
            
            convolvedFluxValues[k] =  (1/runningArea)*convolvedFluxValues[k]*range;
            //convolvedFluxValues[k] =  convolvedFluxValues[k]*range;
            
        }//for k
        
        Spectrum convolutionSpectrum = new Spectrum();
        
        convolutionSpectrum.setWaveValues(inputWaveValues1);
        convolutionSpectrum.setFluxValues(convolvedFluxValues);
        
        // return MathUtils.rejectZeros(smoothedSpectrum);
        return convolutionSpectrum;
    }
    
    
/*    public Spectrum getMatrixConvolution(){
 
 
        double[] fluxValues1Aux   = this.spectrum1.getFluxValues();
        double[] fluxValues2Aux   = this.spectrum2.getFluxValues();
 
        int M = fluxValues1Aux.length;
        int N = fluxValues2Aux.length;
 
        double[] convolutionFluxValues = new double[M+N-1];
 
        double[][] m = new double[2*M-1][N];
 
        for(int k = 0; k < M-1; k++){
            for(int l = 0; l < k; l++){
                m[k][l] = fluxValues1Aux[k-l];
            }
        }
 
        for(int k = M; k < 2*M-1; k++){
            for(int l = M-1; l < k-M+1; l--){
                m[k][l] = fluxValues1Aux[k-l];
            }
        }
 
        //Matrixmultiplication!!!!
        ToBeDone
 
    }
 */
    
    
}
