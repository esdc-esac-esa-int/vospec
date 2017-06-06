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
 * MathUtils.java
 *
 * Created on April 24, 2007, 9:48 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */



/**
 *
 * @author alaruelo
 */

import esavo.vospec.spectrum.*;

public class MathUtils {
    
    /**
     * Creates a new instance of MathUtils
     */
    public MathUtils() {
    }
    
    public static Spectrum evenlySpacedSpectrum(Spectrum spectrum, int samplingPoints){
        
        //Spectrum evenlySpacedSpectrum;
        
        OrderedSpectrum orderedSpectrum = new OrderedSpectrum(spectrum);
        
        double[] waveValues = (double[]) orderedSpectrum.getWaveValues();
        double[] fluxValues = (double[]) orderedSpectrum.getFluxValues();
        
/*       //eliminating zero entries
        double[] waveValuesAux = (double[]) orderedSpectrum.getWaveValues();
        double[] fluxValuesAux = (double[]) orderedSpectrum.getFluxValues();
 
        int contAux = 0;
        for(int k = 0; k < waveValuesAux.length; k++){
            if(Double.compare(fluxValuesAux[k], 0.0)==0){
                contAux++;
            }
        }
 
        int j = 0;
 
        double[] waveValues = new double[waveValuesAux.length - contAux];
        double[] fluxValues = new double[waveValuesAux.length - contAux];
 
        for(int k = 0; k < waveValuesAux.length; k++){
            if(fluxValuesAux[k] != 0){
                waveValues[j] = waveValuesAux[k];
                fluxValues[j] = fluxValuesAux[k];
                j++;
            }
        }
 
        Spectrum nonZeroSpectrum = new Spectrum();
        nonZeroSpectrum.setWaveValues(waveValues);
        nonZeroSpectrum.setFluxValues(fluxValues);
 */
        
        
        //is the input spectrum already evenly spaced?
/*        double step = Math.abs(waveValues[1] - waveValues[0]);
        boolean evenlySpaced = true;
        int k = 2;
        while( k < waveValues.length && evenlySpaced == true) {
            if(Math.abs(waveValues[k] - waveValues[k-1]) != step) {
                evenlySpaced = false;
            } else {
                k++;
            }
        }
 
        if(evenlySpaced == true){
 
            //evenlySpacedSpectrum = new Spectrum(spectrum);
            return spectrum;
 
        } else{
 
 */          double[] sampling = new double[samplingPoints];
 
 double xMin = waveValues[0];
 double xMax = waveValues[waveValues.length-1];
 
 for(int i = 0; i < samplingPoints; i++){
     sampling[i] = xMin + i*(xMax-xMin)/(samplingPoints-1);
 }
 
 Spectrum evenlySpacedSpectrum = (Spectrum) linearInterpolation(orderedSpectrum, sampling);
 //Spectrum evenlySpacedSpectrum = (Spectrum) linearInterpolation(nonZeroSpectrum, sampling);
 
 return evenlySpacedSpectrum;
 //       }
 
 // return evenlySpacedSpectrum;
    }
    
    
    
    /*
     *returns a Spectrum which flux values are linear interpolation of the
     *flux values of the input spectrum with respect of the input sampling
     */
    public static Spectrum linearInterpolation(Spectrum spectrum, double[] sampling) {
        
        int cont = 0;
        int i = 0;
        
        double[] inputWaveValues = spectrum.getWaveValues();
        double[] inputFluxValues = spectrum.getFluxValues();
        double[] interpolatedFluxValues = new double[sampling.length];
        
//        for(int k = 0; k < sampling.length; k++){
//            interpolatedFluxValues[k] = 0.0;
//        }
        
        // System.out.println("interpolated flux values length = "+interpolatedFluxValues.length);
        
        Spectrum interpolatedSpectrum = new Spectrum();
        
        for(int n=0;n<sampling.length;n++) {
            
            i = 0;
            boolean goOn = true;
            
            while (i < inputFluxValues.length && goOn == true) {
                if(inputWaveValues[i]==sampling[n]) {
                    interpolatedFluxValues[cont] = inputFluxValues[i];
                    goOn = false;
                    cont = cont + 1;
                } else if(i > 0){
                    //spectrum is supposed to be ordered
                    if(inputWaveValues[i-1]<sampling[n] & sampling[n]<inputWaveValues[i]) {
                        interpolatedFluxValues[cont]=inputFluxValues[i-1]+((inputFluxValues[i]-inputFluxValues[i-1])/(inputWaveValues[i]-inputWaveValues[i-1]))*(sampling[n]-inputWaveValues[i-1]);
                        goOn = false;
                        cont = cont + 1;
                    }
                } else if(sampling[n] < inputWaveValues[0] | inputWaveValues[inputWaveValues.length-1] < sampling[n]){
                    interpolatedFluxValues[cont] = 0.0;
                    goOn = false;
                    cont = cont + 1;
                }
                i++;
            }//while
        }
        
        interpolatedSpectrum.setWaveValues(sampling);
        interpolatedSpectrum.setFluxValues(interpolatedFluxValues);
        
        return interpolatedSpectrum;
    }
    
    public static boolean multivaluedSpectrum(Spectrum spectrum){
        
        int i = 1;
        
        boolean multivalued = false;
        
        //OrderedSpectrum orderedSpectrum = new OrderedSpectrum(spectrum);
        //double[] waveValues = (double[]) orderedSpectrum.getWaveValues();
        
        double[] waveValues = (double[]) spectrum.getWaveValues();
        waveValues = (double[]) orderedArray(waveValues);
        
        while(i < waveValues.length && multivalued == false){
            if(Double.compare(waveValues[i], waveValues[i-1]) == 0){
                multivalued = true;
            }
            
            i++;
        }
        return multivalued;
    }
    
    public static boolean contains(double[] array, double element){
        
        boolean contains = false;
        
        int i = 0;
        
        while(i < array.length && contains == false) {
            //if(array[i] == element) {
            if(Double.compare(array[i], element)==0){
                contains = true;
                //i = array.length;
            }
            i++;
        }
        return contains;
    }
    
    public static double[] orderedArray(double[] array) {
        
        
        for(int i=0; i < array.length; i++) {
            
            double xElement1 = array[i];
            
            for(int j = i+1; j < array.length; j++) {
                
                double xElement2 = array[j];
                
                if(xElement2 < xElement1) {
                    array[i] = xElement2;
                    array[j] = xElement1;
                    
                    xElement1 = xElement2;
                }
            }//j
        }//i
        
        return array;
    }
    
    public static int backwardWaveIndex(double[] waveValues, double inputWaveValue) {
        
        double mostApproxBackwardValue =  waveValues[0];
        double d = inputWaveValue - mostApproxBackwardValue;
        
        int i = 1;
        int mostApproxIndex = 0;
        
        while(i < waveValues.length && d >= 0) {
            if(inputWaveValue - waveValues[i] < d) {
                
                mostApproxBackwardValue = waveValues[i];
                d = inputWaveValue - mostApproxBackwardValue;
                
                mostApproxIndex = i;
            }
            i++;
        }
        
        return mostApproxIndex;
    }
    
    public static int backwardWaveIndex(double[] waveValues, double inputWaveValue, int minIndex, int maxIndex) {
        
        double[] partialArray = new double[maxIndex-minIndex+1];
        
        //generate a new array containing just values from the input
        //array between min and max indices
        for(int k = 0; k < partialArray.length; k++){
            partialArray[k] = waveValues[minIndex+k];
        }
        
        return backwardWaveIndex(partialArray, inputWaveValue);
    }
    
    
    public static int forwardWaveIndex(double[] waveValues, double inputWaveValue) {
        
        double mostApproxBackwardValue =  waveValues[waveValues.length-1];
        double d = mostApproxBackwardValue -inputWaveValue;
        
        int i = waveValues.length-2;
        int mostApproxIndex = waveValues.length-1;
        
        while(i > -1 && d >= 0) {
            if(waveValues[i] - inputWaveValue < d) {
                
                mostApproxBackwardValue = waveValues[i];
                d = mostApproxBackwardValue - inputWaveValue;
                
                mostApproxIndex = i;
            }
            i--;
        }
        
        return mostApproxIndex;
    }
    
    public static int forwardWaveIndex(double[] waveValues, double inputWaveValue, int minIndex, int maxIndex) {
        
        double[] partialArray = new double[maxIndex-minIndex+1];
        
        //generate a new array containing just values from the input
        //array between min and max indices
        for(int k = 0; k < partialArray.length; k++){
            partialArray[k] = waveValues[minIndex+k];
        }
        
        return forwardWaveIndex(partialArray, inputWaveValue);
    }
    
    
    public static int approxWaveValue(double[] waveValues, double wavePoint) {
        
        int mostApproxIndex = 0;
        int i = 1;
        
        double dist = Math.abs(wavePoint - waveValues[0]);
        
        while(i < waveValues.length) {
            if(Math.abs(wavePoint - waveValues[i]) < dist) {
                dist = Math.abs(wavePoint - waveValues[i]);
                mostApproxIndex = i;
            }
            i++;
        }
        
        return mostApproxIndex;
    }
    
    //if inputFluxValue belongs to fluxValues[]=>backwardFluxIndex == inputFluxValue index
    /*Returns index -1 if backwardFluxValue <= inputFluxValue doesn't exist.
     */
    public static int backwardFluxIndex(double[] fluxValues, double inputFluxValue){
        
        int i = 0;
        int backwardIndex = -1;
        
        double d = -1;
        
        int minFluxPosition = MathUtils.minValuePosition(fluxValues);
        int maxFluxPosition = MathUtils.maxValuePosition(fluxValues);
        
        double epsilon = Math.abs(fluxValues[maxFluxPosition]-fluxValues[minFluxPosition])/10000;
        
        
        boolean initialized = false;
        
        while(i < fluxValues.length) {
            
            //if(Double.compare(inputFluxValue, fluxValues[i])==0){
            if(Math.abs(inputFluxValue - fluxValues[i]) <= epsilon){
                backwardIndex = i;
            }
            
            if(0 <= inputFluxValue -fluxValues[i] && initialized == false){
                d = inputFluxValue - fluxValues[i];
                backwardIndex = i;
                initialized = true;
            }
            
            if(0 <= inputFluxValue - fluxValues[i] && inputFluxValue - fluxValues[i] < d ) {
                
                d = inputFluxValue - fluxValues[i];
                backwardIndex = i;
            }
            
            i++;
        }
        
        return backwardIndex;
    }
    
    //if inputFluxValue belongs to fluxValues[]=>forwardFluxIndex == inputFluxValue index
    /*Returns index -1 if forwardFluxValue >= inputFluxValue doesn't exist.
     */
    public static int forwardFluxIndex(double[] fluxValues, double inputFluxValue){
        
        int i = 0;
        int forwardIndex = -1;
        
        int minFluxPosition = MathUtils.minValuePosition(fluxValues);
        int maxFluxPosition = MathUtils.maxValuePosition(fluxValues);
        
        double epsilon = Math.abs(fluxValues[maxFluxPosition]-fluxValues[minFluxPosition])/10000;
        
        double d = -1;
        
        boolean initialized = false;
        
        while(i < fluxValues.length) {
            //System.out.println("fluxValues = "+fluxValues[i]+" inputFluxValue = "+inputFluxValue);
            
            //if(Double.compare(inputFluxValue, fluxValues[i])==0){
            if(Math.abs(inputFluxValue - fluxValues[i]) <= epsilon){
                forwardIndex = i;
            }
            
            //if(0 <= fluxValues[i] - inputFluxValue && initialized == false){
            if(0 < fluxValues[i] - inputFluxValue && initialized == false){
                d = fluxValues[i] - inputFluxValue;
                forwardIndex = i;
                initialized = true;
            }
            
            if(0 < fluxValues[i] - inputFluxValue && fluxValues[i] - inputFluxValue < d ) {
                
                d = fluxValues[i] - inputFluxValue;
                forwardIndex = i;
            }
            
            i++;
        }
        
        return forwardIndex;
    }
    
    public static int forwardFluxIndex(double[] fluxValues, double inputFluxValue, int minIndex, int maxIndex) {
        
        double[] partialArray = new double[maxIndex-minIndex+1];
        
        //generate a new array containing just values from the input
        //array between min and max indices
        for(int k = 0; k < partialArray.length; k++){
            partialArray[k] = fluxValues[minIndex+k];
            //System.out.println("partial flux array ["+k+"] = "+partialArray[k]);
        }
        
        int forwardFluxIndex =  forwardFluxIndex(partialArray, inputFluxValue)+minIndex;
        
        return forwardFluxIndex;
    }
    
    public static int backwardFluxIndex(double[] fluxValues, double inputFluxValue, int minIndex, int maxIndex) {
        
        double[] partialArray = new double[maxIndex-minIndex+1];
        
        //generate a new array containing just values from the input
        //array between min and max indices
        for(int k = 0; k < partialArray.length; k++){
            partialArray[k] = fluxValues[minIndex+k];
        }
        
        int backwardFluxIndex =  backwardFluxIndex(partialArray, inputFluxValue)+minIndex;
        
        return backwardFluxIndex;
    }
    
    
    public static int[] adyacentPoints(Spectrum spectrum , double samplingPoint) {
        
        OrderedSpectrum orderedSpectrum = new OrderedSpectrum(spectrum);
        
        return adyacentPoints(orderedSpectrum, samplingPoint);
    }
    
    /*
     *Evaluates adyacent backward and forward points >>!! with non zero !!<< related fluxValues
     *for calculating the line crossing them
     */
    public static int[] adyacentPoints(OrderedSpectrum orderedSpectrum , double samplingPoint) {
        
        int[] pointsIndex = new int[2];
        
        double[] waveValues = (double[]) orderedSpectrum.getWaveValues();
        double[] fluxValues = (double[]) orderedSpectrum.getFluxValues();
        
        int backwardIndex = backwardWaveIndex(waveValues, samplingPoint);
        //int forwardIndex = forwardIndex(waveValues, samplingPoint);
        int forwardIndex = backwardIndex+1;
        
        if(forwardIndex >= waveValues.length){
            forwardIndex=backwardIndex;
        }
        while(fluxValues[backwardIndex] == 0 && backwardIndex > -1){
            backwardIndex--;
        }
        
        while(fluxValues[forwardIndex] == 0 && forwardIndex < fluxValues.length){
            forwardIndex++;
        }
        
        pointsIndex[0] = backwardIndex;
        pointsIndex[1] = forwardIndex;
        
        return pointsIndex;
    }
    
    public static double linearInterpolation(Spectrum spectrum, double samplingPoint) {
        
        OrderedSpectrum orderedSpectrum = new OrderedSpectrum(spectrum);
        
        
        return linearInterpolation(orderedSpectrum, samplingPoint);
        
    }
    
    
    /*Evaluates the interpolation of the spectrum in wave value == samplingPoint*/
    public static double linearInterpolation(OrderedSpectrum orderedSpectrum, double samplingPoint) {
        
        int[] linearInterpolationPoints = (int[]) adyacentPoints((OrderedSpectrum) orderedSpectrum, samplingPoint);
        
        int backwardIndex = linearInterpolationPoints[0];
        int forwardIndex = linearInterpolationPoints[1];
        
        
        double[] waveValues = (double[]) orderedSpectrum.getWaveValues();
        double[] fluxValues = (double[]) orderedSpectrum.getFluxValues();
        
        double interpolatedFluxValue;
        
        if(forwardIndex != backwardIndex){
            interpolatedFluxValue = fluxValues[backwardIndex]+((fluxValues[forwardIndex]-fluxValues[backwardIndex])/(waveValues[forwardIndex]-waveValues[backwardIndex]))*(samplingPoint-waveValues[backwardIndex]);
            
        }else{
            interpolatedFluxValue = fluxValues[backwardIndex];
        }
        
        return interpolatedFluxValue;
        
    }
    
    /*Returns a spectrum containing all fluxValues of the input spectrum
     *greater that zero and different from NaN
     */
    public static Spectrum rejectZeros(Spectrum spectrum){
        
        double[] waveValues = (double[]) spectrum.getWaveValues();
        double[] fluxValues = (double[]) spectrum.getFluxValues();
        
        int length = 0;
        
        /*calculating the length of the new wave and flux values after
         *rejecting nagative, zero and NaN flux values
         */
        for(int k = 0; k < waveValues.length; k++){
            if(fluxValues[k] > 0 && Double.isNaN(fluxValues[k])==false){
                length++;
            }
        }
        
        double[] newWaveValues = new double[length];
        double[] newFluxValues = new double[length];
        
        int i = 0;
        
        for(int k = 0; k < waveValues.length; k++){
            if(fluxValues[k] > 0 && Double.isNaN(fluxValues[k])==false){
                newWaveValues[i] = waveValues[k];
                newFluxValues[i] = fluxValues[k];
                
                i++;
            }
        }
        
        Spectrum newSpectrum = new Spectrum();
        
        newSpectrum.setWaveValues(newWaveValues);
        newSpectrum.setFluxValues(newFluxValues);
        
        return newSpectrum;
    }
    
    /*Returns the position of the maximum positive flux value*/
/*    public static int maxFluxValueIndex(double[] fluxValues){
 
        int index = 0;
 
        for(int i = 1; i < fluxValues.length; i++){
            if(fluxValues[i] > fluxValues[index]){
                index = i;
            }
        }
 
        return index;
    }
 */
    /**Returns the average value of the input array entries
     */
    public static double average(double[] y){
        
        double average = 0;
        
        for(int k = 0; k < y.length; k++){
            average = average + y[k];
        }
        
        average = average/y.length;
        
        return average;
    }
    
    /**Returns an estimation of the variance of the input array entries
     */
    public static double variance(double[] y){
        
        double average = average(y);
        double variance = 0;
        
        for(int k = 0; k < y.length; k++){
            variance = variance + Math.pow(y[k]- average, 2);
        }
        
        /* unbiased estimator for the variance
         * var = sum(x(i)- E(x))/(N-1)
         */
        variance = variance/(y.length-1);
        
        return variance;
    }
          
     public static double standardDeviation(double[] y){
         double variance = variance(y);
         return Math.sqrt(variance);
     }
     
     public static double range(double[] y){
         double max = maxValue(y);
         double min = minValue(y);
         return max-min;
     }
     
         
    public static double median(double[] x){
        
        int n = x.length;
        
        double median;
        
        x = orderedArray(x);
        
        if (n % 2 == 0) {
            // even number of elements
            median = (x[n/2]+x[n/2-1])/2;
            
        }else {
            // odd
            median = x[(n-1)/2];
        }
        
        return median;
    }
    
    public static double mad(double[] x){
        /*M.A.D.(Median Absolute Deviation) == median(|x(i) - median(x(j))|)
         *                                        i              j
         */
        double median = median(x);
        
        double[] y = new double[x.length];
        
        for(int k = 0; k < x.length; k++){
            y[k] = Math.abs(x[k] - median);
        }
        
        double mad = median(y);
        
        return mad;
    }
    
    /**Returns an estimation of the spectrum noise variance.
     *The noise is supossed to be additive, uncorrelated, stationary and with zero mean,
     *i.e., white noise.
     **/
    public static double noiseEstimation(Spectrum spectrum){
        
        double[] fluxValues = spectrum.getFluxValues();
        
        Spectrum averageSpectrum = (new Smoothing(spectrum)).mean(999);
        
        /*fluxValues and averageFluxValues can be evaluated in different wavelengths!!
         *This problem is solved by initilizeData method of the Arithmetics class
         */
        Arithmetics arithmetics = new Arithmetics(spectrum, averageSpectrum);
        arithmetics.initializeData();
        
        /*[spectrum - averageSpectrum] give us the values of the noise distribution*/
        double[] diff = (double[])((Spectrum)arithmetics.diff()).getFluxValues();
        
        double noise = variance(diff);
        
        return noise;
    }
    
    public static Spectrum waveleghtToVelocity(Spectrum spec, double lambdaRef){
        
        double[] waveValues = spec.getWaveValues();
        
        /*light velocity km/s*/
        double c = 299792.458;
        
        for(int k = 0; k < waveValues.length; k++){
            
            waveValues[k] = c*(waveValues[k] - lambdaRef)/lambdaRef;
        }
        
        spec.setWaveValues(waveValues);
        
        return spec;
    }
    
    public static Spectrum velocityToWaveleght(Spectrum spec, double lambdaRef){
        
        //spec waveValues in velocity units
        double[] waveValues = spec.getWaveValues();
        
        /*light velocity km/s*/
        double c = 299792.458;
        
        for(int k = 0; k < waveValues.length; k++){
            
            waveValues[k] = lambdaRef*waveValues[k]/c + lambdaRef;
        }
        
        spec.setWaveValues(waveValues);
        
        return spec;
    }
    
    
    /*Due to problems with pol. fitting of order 2, this method has been
     *modified and implemented in SPectralLine class*/
    public static double lineOrientation(Spectrum spectrum){
        
/*       Vector thisDataToFit = new Vector();
        for(int i=0; i < xDataLinear.length; i++) {
 
                double[] element = new double[2];
 
                element[0] = xDataLinear[i];
                element[1] = yDataLinear[i];
 
                thisDataToFit.addElement(element);
        }
 
 */
        PolynomialFitting pf = new PolynomialFitting(spectrum,true,true,2);
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
        
        return a;
    }
    
    /*Evaluates the peak position of an input spectral line.
     *Uses lineOrientation method to know the spectral line orientation.
     *In order to avoid outliers points, an smoothed line is used.
     *Spectral line is supposed to be already ordered!!
     */
    public static int peakPosition(Spectrum spectralLine, boolean alreadySmoothed) {
        
        //double[] smoothedWaveValues;
        double[] smoothedFluxValues;
        
        if(alreadySmoothed==false){
            
            //Averaging the input spectrum
            //calculating 10% of input points
            int numTotalPoints      = (int) (spectralLine.getWaveValues()).length;
            int numPercentPoints    = (int) Math.floor((double) numTotalPoints/10);
            int numFinalPoints      = (int) Math.floor((double) numPercentPoints/2);
            
            Spectrum smoothedSpectrum = (new Smoothing(spectralLine)).nPointsMedianFilter(numFinalPoints);
            
            //Smoothing returns an ordered spectrum
            //smoothedWaveValues = (double[]) smoothedSpectrum.getWaveValues();
            smoothedFluxValues = (double[]) smoothedSpectrum.getFluxValues();
            
        }else{
            //fluxValues must be ordered
            smoothedFluxValues = spectralLine.getFluxValues();
        }
        
        //peak position of the smoothed spectral line
        int peakPosition;
        
        if(MathUtils.lineOrientation(spectralLine) > 0){
            
            peakPosition = MathUtils.minValuePosition(smoothedFluxValues);
            
        }else{
            
            peakPosition = MathUtils.maxValuePosition(smoothedFluxValues);
        }
        
        /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
         *We don't need to look for the most approx waveValue Index because now average/media
         *smoothing methods returns spectra evaluated at the same waveValues than the input spectrum
         *!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
         */
        //looks for the most approx wave value to the axis of the smoothed gaussian
        //int gaussianAxisIndex = MathUtils.approxWaveValue(inputWaveValues, smoothedWaveValues[peakFluxIndex]);
        
        return peakPosition;
    }
    
    
    public static double maxValue(double[] array){
        
        double maxValue = array[0];
        
        for(int i = 1; i < array.length; i++){
            if(array[i] > maxValue) {
                maxValue = array[i];
            }
        }
        
        return maxValue;
    }
    
    
    public static int maxValuePosition(double[] array){
        
        int maxValuePosition = 0;
        
        for(int i = 1; i < array.length; i++){
            if(array[i] > array[maxValuePosition]) {
                maxValuePosition = i;
            }
        }
        
        return maxValuePosition;
    }
    
    public static int maxValuePosition(double[] array, int minIndex, int maxIndex){
        
        double[] partialArray = new double[maxIndex-minIndex+1];
        
        //generate a new array containing just values from the input
        //array between min and max indices
        for(int k = 0; k < partialArray.length; k++){
            partialArray[k] = array[minIndex+k];
        }
        
        int maxValuePosition = maxValuePosition(partialArray) + minIndex;
        
        return maxValuePosition;
    }
    
    
     public static double minValue(double[] array){
        
        double minValue = array[0];
        
        for(int i = 1; i < array.length; i++){
            if(array[i] < minValue) {
                minValue = array[i];
            }
        }
        
        return minValue;
    }
    public static int minValuePosition(double[] array){
        
        int minValuePosition = 0;
        
        for(int i = 1; i < array.length; i++){
            if(array[i] < array[minValuePosition]) {
                minValuePosition = i;
            }
        }
        
        return minValuePosition;
    }
    
    public static int minValuePosition(double[] array, int minIndex, int maxIndex){
        
        double[] partialArray = new double[maxIndex-minIndex+1];
        
        //generate a new array containing just values from the input
        //array between min and max indices
        
//        System.out.println("arrayLength = "+array.length);
        for(int k = 0; k < partialArray.length; k++){
            int l = minIndex+k;
            //System.out.println("array["+l+"] = "+array[minIndex+k]);
            partialArray[k] = array[minIndex+k];
        }
        
        int minValuePosition = minValuePosition(partialArray) + minIndex;
        
        return minValuePosition;
    }
    
    private boolean isNumber(String s)
  {
    String validChars = "0123456789.,";
    boolean isNumber = true;
 
    for (int i = 0; i < s.length() && isNumber; i++) 
    { 
      char c = s.charAt(i); 
      if (validChars.indexOf(c) == -1) 
      {
        isNumber = false;
      }
      else
      {
        isNumber = true;
      }
    }
    return isNumber;
  }
    
    
}
