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

//import esavo.fit.OrderedSpectrum;
import esavo.vospec.spectrum.*;
import java.util.Vector;

public class Smoothing {
    
    int numPoints = 3;          //average considering 3 backward and 3 forward points
    int numSubIntervals = 999;  //average considering points inside ranges
       
    Spectrum    spectrum;
    
    static boolean multivalued;   //becomes true if the input spectra for multivaluedSpectraSmoothing method is really multivalued
    
    /** Creates a new instance of Smoothing */
    public Smoothing(Spectrum spectrum) {
        this.spectrum = spectrum;
    }
    
    public Smoothing(Spectrum spectrum, int numSubIntervals) {
        this.spectrum = spectrum;
        this.numSubIntervals = numSubIntervals;
    }
    
    public Smoothing(){
        
    }
    
    public Spectrum mean() {
        return mean(numSubIntervals);
    }
    
    public Spectrum mean(int numSubIntervals) {
        
        Spectrum averageSpectrum = new Spectrum();
        
        OrderedSpectrum orderedSpectrum = new OrderedSpectrum(spectrum);
        
        double[] waveValues = (double[]) orderedSpectrum.getWaveValues();
        double[] fluxValues = (double[]) orderedSpectrum.getFluxValues();
        
        double xMin = waveValues[0];
        double xMax = waveValues[waveValues.length-1];
        
        double[] averageWaveValues = new double[numSubIntervals+1];
        double[] averageFluxValues = new double[numSubIntervals+1];
        
        int i = 0;
        
        for(int k = 0; k <= numSubIntervals; k++){
                        
            //ToModify
            //i = 0;
            int pointsThisRange = 0;
            
            double xMinThisRange = xMin + (k-0.5)*(xMax-xMin)/(numSubIntervals);
            double xMaxThisRange = xMin + (k+0.5)*(xMax-xMin)/(numSubIntervals);
            
            averageWaveValues[k] = xMin + k*(xMax-xMin)/(numSubIntervals);
            averageFluxValues[k] = 0;
            
            boolean exit = false;
            
            while(xMinThisRange <= waveValues[i] && waveValues[i] < xMaxThisRange && exit == false) {
                
                averageFluxValues[k] = averageFluxValues[k] + fluxValues[i];
                                
                if(fluxValues[i] != 0) {
                    pointsThisRange++;
                }
                
                if(i < waveValues.length-1) {
                    i++;
                }else{
                    exit = true;
                }
                
                
            }//while
            
            if(pointsThisRange != 0) {
                averageFluxValues[k] = averageFluxValues[k]/pointsThisRange;
            }
            
        }//for k
        
        
        averageSpectrum.setWaveValues(averageWaveValues);
        averageSpectrum.setFluxValues(averageFluxValues);
        
        //rejects zeros because we set to zero the averageFluxValues with
        //no points in its range
        return MathUtils.rejectZeros(averageSpectrum);
    }
    
    
    public Spectrum nPointsMean(){
        return nPointsAverage(this.numPoints);
    }
    
    public Spectrum nPointsAverage(int numPoints) {
        
        Spectrum averagedSpectrum = new Spectrum();
        
        OrderedSpectrum orderedSpectrum = new OrderedSpectrum(spectrum);
        
        double[] waveValues = (double[]) orderedSpectrum.getWaveValues();
        double[] fluxValues = (double[]) orderedSpectrum.getFluxValues();
        
        double[] averagedFluxValues = new double[fluxValues.length];
        
        //replacing each fluxValue by an averaged flux value
        for(int k = 0; k < waveValues.length; k++){
            
            //returns fluxValues indices(GAPS ARE AVOIDED!!!!!!)
            int[] neighbourIndex = neighboursIndex(waveValues, k, numPoints);
            
            double[] neighbourFluxValues = new double[neighbourIndex.length];
            
            for(int i = 0; i < neighbourIndex.length; i++){
                int index = neighbourIndex[i];
                
                neighbourFluxValues[i] = fluxValues[index];
            }
            averagedFluxValues[k] = MathUtils.average(neighbourFluxValues);
        }
        
        averagedSpectrum.setWaveValues(waveValues);
        averagedSpectrum.setFluxValues(averagedFluxValues);
        
        return averagedSpectrum;
    }
    
    
    
    public Spectrum medianFilter(){
        return medianFilter(1000);
    }
    
    public Spectrum medianFilter(int numRanges){
        
        OrderedSpectrum orderedSpectrum = new OrderedSpectrum(spectrum);
        
        double[] waveValues = (double[]) orderedSpectrum.getWaveValues();
        double[] fluxValues = (double[]) orderedSpectrum.getFluxValues();
        
        System.out.println(" numRanges = "+ numRanges);
        
        int length = numRanges + 1;
        
        //double[] filteredWaveValues = new double[length];
        //double[] filteredFluxValues = new double[length];
        
        double[] filteredWaveValues = new double[1001];
        double[] filteredFluxValues = new double[1001];
        
        
        System.out.println("filteredWaveValues.length = "+filteredWaveValues.length);
        
        double xMin = waveValues[0];
        double xMax = waveValues[waveValues.length-1];
        
        int i = 0;
        
        System.out.println(" i = 0");
        
        for(int k = 0; k <= numRanges; k++){
            
            System.out.println(" k = "+ k);
            
            double xMinThisRange = xMin + (k-0.5)*(xMax-xMin)/numRanges;
            double xMaxThisRange = xMin + (k+0.5)*(xMax-xMin)/numRanges;
            
            filteredWaveValues[k] = xMin + k*(xMax-xMin)/numRanges;
            
            System.out.println("filteredWaveValues["+k+"] = "+filteredWaveValues[k]);
            
            Vector neighboursVector = new Vector();
            
            boolean stop = false;
            //i = 0;
            //while(stop == false){
            
            while(xMinThisRange <= waveValues[i] && waveValues[i] < xMaxThisRange && stop == false){
                Double fluxToDouble = new Double(fluxValues[i]);
                neighboursVector.addElement(fluxToDouble);
                if(i < (waveValues.length-1)){
                    i++;
                }else{
                    stop = true;
                }
                
                
            }//while
            
            System.out.println(" i = " + i);
            
            
            double[] neighboursArray;
            
            if(neighboursVector.size() !=0){
                System.out.println(" neighboursArray size != 0 = "+neighboursVector.size());
                
                neighboursArray = new double[neighboursVector.size()];
                
                for(int j = 0; j < neighboursVector.size(); j++){
                    double flux = (double) ((Double) neighboursVector.elementAt(j)).doubleValue();
                    neighboursArray[j] = flux;
                }
                
                if (neighboursArray.length % 2 == 0) {
                    // even(par)
                    System.out.println(" neighboursArray even ");
                    filteredFluxValues[k] = (neighboursArray[neighboursArray.length/2]+neighboursArray[neighboursArray.length/2-1])/2;
                }
                
                if (neighboursArray.length % 2 != 0) {
                    // odd
                    System.out.println(" neighboursArray odd ");
                    filteredFluxValues[k] = neighboursArray[(neighboursArray.length-1)/2];
                }
            }else{
                System.out.println(" neighboursArray size == 0 ");
                
                filteredFluxValues[k] = 0.0;
                
                System.out.println("filteredFluxValues["+k+"] ="+  filteredFluxValues[k]);
            }
            
            
        }//for k
        
        System.out.println("exit for k");
        
        Spectrum filteredSpectrum = new Spectrum();
        
        filteredSpectrum.setWaveValues(filteredWaveValues);
        filteredSpectrum.setFluxValues(filteredFluxValues);
        
        return MathUtils.rejectZeros(filteredSpectrum);
    }
    
    
    public Spectrum nPointsMedianFilter(int numPoints){
        
        Spectrum filteredSpectrum = new Spectrum();
        
        OrderedSpectrum orderedSpectrum = new OrderedSpectrum(spectrum);
        
        double[] waveValues = (double[]) orderedSpectrum.getWaveValues();
        double[] fluxValues = (double[]) orderedSpectrum.getFluxValues();
        
        double[] filteredFluxValues = new double[fluxValues.length];
        
        //replacing each fluxValue by an averaged flux value
        for(int k = 0; k < waveValues.length; k++){
            
            //returns fluxValues indices(GAPS ARE AVOIDED)
            int[] neighbourIndex = neighboursIndex(waveValues, k, numPoints);
            
            double[] neighbourFluxValues = new double[neighbourIndex.length];
            
            for(int i = 0; i < neighbourIndex.length; i++){
                int index = neighbourIndex[i];
                
                neighbourFluxValues[i] = fluxValues[index];
            }
            filteredFluxValues[k] = MathUtils.median(neighbourFluxValues);
        }
        
        filteredSpectrum.setWaveValues(waveValues);
        filteredSpectrum.setFluxValues(filteredFluxValues);
        
        return filteredSpectrum;
        
    }
    
    
    public Spectrum nPointsMedianFilter(){
        return nPointsMedianFilter(3);
    }
    
    
/*    public Spectrum nPointsMedianFilter(int numPoints) {
 
        OrderedSpectrum orderedSpectrum = new OrderedSpectrum(spectrum);
 
        double[] waveValues = (double[]) orderedSpectrum.getWaveValues();
        double[] fluxValues = (double[]) orderedSpectrum.getFluxValues();
 
        double[] filteredFluxValues = new double[waveValues.length];
 
 
        for(int k = 0; k < waveValues.length; k++){
 
            int length;
 
            if(k < numPoints){
 
                length = k + numPoints + 1;
 
            } else if(k + numPoints > waveValues.length-1){
 
                length = waveValues.length-(k - numPoints);
 
            } else{
 
                length = 2*numPoints + 1;
            }
 
            double[] neighboursArray   = new double[length];     //points of the input spectrum to be considered
 
            int cont = 0;
 
            for(int i = numPoints; i > 0; i --){
                if(0 <= (k - i)){
                    neighboursArray[cont] = fluxValues[k-i];
                    cont++;
                }
            }
 
            for(int i = 0; i <= numPoints; i ++){
                if((k + i) < waveValues.length){
                    neighboursArray[cont] = fluxValues[k+i];
                    cont++;
                }
            }
 
            neighboursArray = (double[]) MathUtils.orderedArray(neighboursArray);
 
            if (length % 2 == 0) {
                // even
                filteredFluxValues[k] = (neighboursArray[length/2]+neighboursArray[length/2-1])/2;
            } else if (length % 2 != 0) {
                // odd
                filteredFluxValues[k] = neighboursArray[(length-1)/2];
            }
 
        }//k
 
 
 
        Spectrum filteredSpectrum = new Spectrum();
 
        filteredSpectrum.setWaveValues(waveValues);
        filteredSpectrum.setFluxValues(filteredFluxValues);
 
        return filteredSpectrum;
 
    }
 */
    
    public static Spectrum multivaluedSpectrumSmoothing(Spectrum spectrum) {
        
        multivalued = false;
        
        OrderedSpectrum orderedSpectrum = new OrderedSpectrum(spectrum);
        
        double[] waveValues = (double[]) orderedSpectrum.getWaveValues();
        double[] fluxValues = (double[]) orderedSpectrum.getFluxValues();
        
        //I don't work with arrays becuase the length is unknown
        Vector smoothedWaveValuesVector = new Vector();
        Vector smoothedFluxValuesVector = new Vector();
        
        int i =1;
        int cont = 0;
        
        while(i < waveValues.length){
            
            int numPoints = 1;
            //boolean multi = false;
            
            cont++;
            
            Double vectorWaveElement = new Double(waveValues[i-1]);
            smoothedWaveValuesVector.addElement(vectorWaveElement);
            
            double fluxAverage = fluxValues[i-1];
            
            double diff = waveValues[i]- waveValues[i-1];
            
            boolean stop = false;
            
            while(Double.compare(diff, 0)==0 && stop==false){
                int j = i-1;
                
                fluxAverage = fluxAverage + fluxValues[i];
                numPoints++;
                
                i++;
                
                if(i < waveValues.length){
                    diff = waveValues[i]- waveValues[i-1];
                }else{
                    stop =true;
                }
                
                multivalued = true;
                           
            }
            
            //if(numPoints != 0){
            fluxAverage = fluxAverage/numPoints;
            //}
            
            Double vectorFluxElement = new Double(fluxAverage);
            smoothedFluxValuesVector.addElement(vectorFluxElement);
            
            if(i == waveValues.length-1){
                vectorWaveElement = new Double(waveValues[i]);
                smoothedWaveValuesVector.addElement(vectorWaveElement);
                
                
                vectorFluxElement = new Double(fluxValues[i]);
                smoothedFluxValuesVector.addElement(vectorFluxElement);
            }
            
            i++;
            
        }//while i
        
        double[] smoothedWaveValues = new double[smoothedWaveValuesVector.size()];
        double[] smoothedFluxValues = new double[smoothedWaveValuesVector.size()];
        
        //Creating arrays from Vectors
        for(int k = 0; k < smoothedWaveValuesVector.size(); k++){
            double waveElement = ((Double) smoothedWaveValuesVector.elementAt(k)).doubleValue();
            double fluxElement = ((Double) smoothedFluxValuesVector.elementAt(k)).doubleValue();
            
            smoothedWaveValues[k] = waveElement;
            smoothedFluxValues[k] = fluxElement;
            
        }
        
        Spectrum smoothedSpectrum = new Spectrum();
        
        smoothedSpectrum.setWaveValues(smoothedWaveValues);
        smoothedSpectrum.setFluxValues(smoothedFluxValues);
        
        return smoothedSpectrum;
    }
    
    
    
    public Spectrum adaptiveIDSGaussian(){
        
        //double[] waveValuesAux = (double[]) spectrum.getWaveValues();
        //double[] fluxValuesAux = (double[]) spectrum.getFluxValues();
        
        double[] waveValues = (double[]) spectrum.getWaveValues();
        double[] fluxValues = (double[]) spectrum.getFluxValues();
        
        double mean = MathUtils.average(fluxValues);
        
/*        double range = Math.abs(waveValuesAux[waveValuesAux.length-1]-waveValuesAux[0])/999;
        //double rangeY = Math.abs(fluxValuesAux[fluxValuesAux.length-1]-fluxValuesAux[0])/999;
 
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
 */
        //double[] waveValues = (double[]) spectrum.getWaveValues();
        //double[] fluxValues = (double[]) spectrum.getFluxValues();
        
        double[] smoothedFluxValues = new double[waveValues.length];
        
        double range = Math.abs(waveValues[waveValues.length-1]-waveValues[0])/999;
        
        for(int k = 0; k < waveValues.length; k++){
            
            double sigma;
            
            if(fluxValues[k] !=0){
                sigma = range + range*Math.abs(1/(fluxValues[k]-mean));
            }else{
                sigma = range;
            }
            
            while(sigma >= 2*range) {
                
                sigma = sigma/10;
            }
            
            int i = 0;
            int firstIndex = 0;
            int lastIndex = waveValues.length;
            
            double xMin = waveValues[k]-3*sigma;
            double xMax = waveValues[k]+3*sigma;
            
            boolean first = false;
            boolean last = false;
            
            while(i < waveValues.length && last==false){
                if(xMin <= waveValues[i] && waveValues[i] <= xMax && first ==false){
                    first = true;
                    firstIndex = i;
                }
                if(waveValues[i] > xMax && last ==false){
                    last = true;
                    lastIndex = i;
                }
                i++;
            }//end while
            
            System.out.println("firstIndex = "+firstIndex+" lastIndex = "+lastIndex);
            
            int length = (int)lastIndex-firstIndex;
            
            double[] spectrumArray = new double[length];
            double[] gaussianArray = new double[length];
            
            for(int ii = 0; ii < length; ii++){
                spectrumArray[ii] = fluxValues[firstIndex+ii];
                gaussianArray[ii] = Math.exp(-(Math.pow((waveValues[firstIndex+ii]-waveValues[k]),2))/(2*sigma*sigma));
            }
            
            smoothedFluxValues[k] = 0;
            
            double gaussianArea = 0;
            
            for(int ii = 0; ii < spectrumArray.length; ii ++){
                gaussianArea = gaussianArea + gaussianArray[ii]*range;
                
                smoothedFluxValues[k] = smoothedFluxValues[k] + spectrumArray[ii]*gaussianArray[ii];
            }
            
            
            gaussianArea = Math.abs(gaussianArea);
            
            if(xMin < waveValues[0] | xMax > waveValues[waveValues.length-1]){
                smoothedFluxValues[k] =  (1/gaussianArea)*smoothedFluxValues[k]*range;
            } else{
                //smoothedFluxValues[k] =  (1/(sigma*Math.sqrt(2*Math.PI)))*smoothedFluxValues[k]*range;
                
                smoothedFluxValues[k] =  (1/gaussianArea)*smoothedFluxValues[k]*range;
            }
            
            System.out.println("GaussianArea = "+gaussianArea);
            
            // }//if flux != 0
            
            
            // else if (fluxValues[k] ==0){
            //     System.out.println("FLUX VALUES == 0");
            //    smoothedFluxValues[k] = fluxValues[k];
            // }
            
        }//for k
        
        Spectrum smoothedSpectrum = new Spectrum();
        
        smoothedSpectrum.setWaveValues(waveValues);
        smoothedSpectrum.setFluxValues(smoothedFluxValues);
        //smoothedSpectrum.setFluxValues(fluxValues);
        
        //return MathUtils.rejectZeros(smoothedSpectrum);
        return smoothedSpectrum;
    }
    
    public Spectrum gaussianFiltering(double sigma, boolean sigmaFitted){
        
        //double[] waveValuesAux = (double[]) spectrum.getWaveValues();
        //double[] fluxValuesAux = (double[]) spectrum.getFluxValues();
        
        double[] waveValues = (double[]) spectrum.getWaveValues();
        double[] fluxValues = (double[]) spectrum.getFluxValues();
                       
        double range = Math.abs(waveValues[waveValues.length-1]-waveValues[0])/999;
        
        if(sigmaFitted== true) sigma = sigma*range;
        
        double[] smoothedFluxValues = new double[waveValues.length];
               
        for(int k = 0; k < waveValues.length; k++){
            
            
            int i = 0;
            int firstIndex = 0;
            int lastIndex = waveValues.length;
            
            double xMin = waveValues[k]-3*sigma;
            double xMax = waveValues[k]+3*sigma;
            
            boolean first = false;
            boolean last = false;
            
            while(i < waveValues.length && last==false){
                if(xMin <= waveValues[i] && waveValues[i] <= xMax && first ==false){
                    first = true;
                    firstIndex = i;
                }
                if(waveValues[i] > xMax && last ==false){
                    last = true;
                    lastIndex = i;
                }
                i++;
            }//end while
            
            int length = (int)lastIndex-firstIndex;
            
            double[] spectrumArray = new double[length];
            double[] gaussianArray = new double[length];
            
            for(int ii = 0; ii < length; ii++){
                spectrumArray[ii] = fluxValues[firstIndex+ii];
                gaussianArray[ii] = Math.exp(-(Math.pow((waveValues[firstIndex+ii]-waveValues[k]),2))/(2*sigma*sigma));
            }
            
            smoothedFluxValues[k] = 0;
            
            double gaussianArea = 0;
            
            for(int ii = 0; ii < spectrumArray.length; ii ++){
                gaussianArea = gaussianArea + gaussianArray[ii]*range;
                
                smoothedFluxValues[k] = smoothedFluxValues[k] + spectrumArray[ii]*gaussianArray[ii];
            }
            
            
            //gaussianArea = Math.abs(gaussianArea*(1/(sigma*Math.sqrt(2*Math.PI))));
            gaussianArea = Math.abs(gaussianArea);
            
            if(xMin < waveValues[0] | xMax > waveValues[waveValues.length-1]){
                smoothedFluxValues[k] =  (1/gaussianArea)*smoothedFluxValues[k]*range;
            } else{
                //smoothedFluxValues[k] =  (1/(sigma*Math.sqrt(2*Math.PI)))*smoothedFluxValues[k]*range;
                
                smoothedFluxValues[k] =  (1/gaussianArea)*smoothedFluxValues[k]*range;
            }
                        
            // }//if flux != 0
            
            
            // else if (fluxValues[k] ==0){
            //     System.out.println("FLUX VALUES == 0");
            //    smoothedFluxValues[k] = fluxValues[k];
            // }
            
        }//for k
        
        Spectrum smoothedSpectrum = new Spectrum();
        
        smoothedSpectrum.setWaveValues(waveValues);
        smoothedSpectrum.setFluxValues(smoothedFluxValues);
        //smoothedSpectrum.setFluxValues(fluxValues);
        
        // return MathUtils.rejectZeros(smoothedSpectrum);
        return smoothedSpectrum;
    }
    
    
    public Spectrum adaptiveIDSLorentzian(){
        
        //double[] waveValuesAux = (double[]) spectrum.getWaveValues();
        //double[] fluxValuesAux = (double[]) spectrum.getFluxValues();
        
        double[] waveValues = (double[]) spectrum.getWaveValues();
        double[] fluxValues = (double[]) spectrum.getFluxValues();
        
        double mean = MathUtils.average(fluxValues);
        
        double range = Math.abs(waveValues[waveValues.length-1]-waveValues[0])/999;
        
/*        int contAux = 0;
        for(int k = 0; k < waveValuesAux.length; k++){
            if(fluxValuesAux[k] <= 0){
                contAux++;
            }
        }
 
        int j = 0;
 
        double[] waveValues = new double[waveValuesAux.length - contAux];
        double[] fluxValues = new double[waveValuesAux.length - contAux];
 
        for(int k = 0; k < waveValuesAux.length; k++){
            if(fluxValuesAux[k] > 0){
                waveValues[j] = waveValuesAux[k];
                fluxValues[j] = fluxValuesAux[k];
                j++;
            }
        }
 */
        //double[] waveValues = (double[]) spectrum.getWaveValues();
        //double[] fluxValues = (double[]) spectrum.getFluxValues();
        
        double[] smoothedFluxValues = new double[waveValues.length];
        
        // double range = Math.abs(waveValues[waveValues.length-1]-waveValues[0])/999;
        
        System.out.println("range = " + range);
        
        for(int k = 0; k < waveValues.length; k++){
            
            //double sigma = 5;
            
            //if(fluxValues[k] != 0){
            System.out.println("fluxValues["+k+"] = " + fluxValues[k]);
            
            double sigma;
            
            if(fluxValues[k] !=0){
                
                sigma = range + range*Math.abs(1/(fluxValues[k]-mean));
                
            }else{
                
                sigma = range;
            }
            
            while(sigma >= 2*range) {
                
                sigma = sigma/10;
            }
            
            
            System.out.println("sigma = " + sigma);
            //}
            int i = 0;
            int firstIndex = 0;
            int lastIndex = waveValues.length;
            
            double xMin = waveValues[k]-4.2*sigma;
            double xMax = waveValues[k]+4.2*sigma;
            
            boolean first = false;
            boolean last = false;
            
            while(i < waveValues.length && last==false){
                if(xMin <= waveValues[i] && waveValues[i] <= xMax && first ==false){
                    first = true;
                    firstIndex = i;
                }
                if(waveValues[i] > xMax && last ==false){
                    last = true;
                    lastIndex = i;
                }
                i++;
            }//end while
            
            System.out.println("firstIndex = "+firstIndex+" lastIndex = "+lastIndex);
            
            int length = (int)lastIndex-firstIndex;
            
            double[] spectrumArray = new double[length];
            double[] lorentzianArray = new double[length];
            
            
            //sigma = (1/2)*FWHM
            for(int ii = 0; ii < length; ii++){
                spectrumArray[ii] = fluxValues[firstIndex+ii];
                lorentzianArray[ii] = sigma/(Math.pow((waveValues[firstIndex+ii]-waveValues[k]), 2) + sigma*sigma);
            }
            
            smoothedFluxValues[k] = 0;
            
            double lorentzianArea = 0;
            
            for(int ii = 0; ii < spectrumArray.length; ii ++){
                lorentzianArea = lorentzianArea + lorentzianArray[ii]*range;
                
                smoothedFluxValues[k] = smoothedFluxValues[k] + spectrumArray[ii]*lorentzianArray[ii];
            }
            
            
            //gaussianArea = Math.abs(gaussianArea*(1/(sigma*Math.sqrt(2*Math.PI))));
            lorentzianArea = Math.abs(lorentzianArea);
            
            if(xMin < waveValues[0] | xMax > waveValues[waveValues.length-1]){
                smoothedFluxValues[k] =  (1/lorentzianArea)*smoothedFluxValues[k]*range;
            } else{
                //smoothedFluxValues[k] =  (1/Math.PI)*smoothedFluxValues[k]*range;
                smoothedFluxValues[k] =  (1/lorentzianArea)*smoothedFluxValues[k]*range;
            }
            
        }//for k
        
        
        Spectrum smoothedSpectrum = new Spectrum();
        
        smoothedSpectrum.setWaveValues(waveValues);
        smoothedSpectrum.setFluxValues(smoothedFluxValues);
        
        //return MathUtils.rejectZeros(smoothedSpectrum);
        return smoothedSpectrum;
    }
    
    public Spectrum lorentzianFiltering(double sigma, boolean sigmaFitted){
        
        double[] waveValues = (double[]) spectrum.getWaveValues();
        double[] fluxValues = (double[]) spectrum.getFluxValues();
        
        
        double range = Math.abs(waveValues[waveValues.length-1]-waveValues[0])/999;
        
        if(sigmaFitted==true) sigma = sigma*range;
        
        double[] smoothedFluxValues = new double[waveValues.length];
  
        
        for(int k = 0; k < waveValues.length; k++){
           
            int i = 0;
            int firstIndex = 0;
            int lastIndex = waveValues.length;
            
            double xMin = waveValues[k]-4.2*sigma;
            double xMax = waveValues[k]+4.2*sigma;
            
            boolean first = false;
            boolean last = false;
            
            while(i < waveValues.length && last==false){
                if(xMin <= waveValues[i] && waveValues[i] <= xMax && first ==false){
                    first = true;
                    firstIndex = i;
                }
                if(waveValues[i] > xMax && last ==false){
                    last = true;
                    lastIndex = i;
                }
                i++;
            }//end while
                                   
            int length = (int)lastIndex-firstIndex;
            
            double[] spectrumArray = new double[length];
            double[] lorentzianArray = new double[length];
            
            
            //sigma = (1/2)*FWHM
            for(int ii = 0; ii < length; ii++){
                spectrumArray[ii] = fluxValues[firstIndex+ii];
                lorentzianArray[ii] = sigma/(Math.pow((waveValues[firstIndex+ii]-waveValues[k]), 2) + sigma*sigma);
            }
            
            smoothedFluxValues[k] = 0;
            
            double lorentzianArea = 0;
            
            for(int ii = 0; ii < spectrumArray.length; ii ++){
                lorentzianArea = lorentzianArea + lorentzianArray[ii]*range;
                
                smoothedFluxValues[k] = smoothedFluxValues[k] + spectrumArray[ii]*lorentzianArray[ii];
            }
                      
            lorentzianArea = Math.abs(lorentzianArea);
            
            if(xMin < waveValues[0] | xMax > waveValues[waveValues.length-1]){
                smoothedFluxValues[k] =  (1/lorentzianArea)*smoothedFluxValues[k]*range;
            } else{
                //smoothedFluxValues[k] =  (1/Math.PI)*smoothedFluxValues[k]*range;
                smoothedFluxValues[k] =  (1/lorentzianArea)*smoothedFluxValues[k]*range;
            }
            
        }//for k
        
        
        Spectrum smoothedSpectrum = new Spectrum();
        
        smoothedSpectrum.setWaveValues(waveValues);
        smoothedSpectrum.setFluxValues(smoothedFluxValues);
        
        //return MathUtils.rejectZeros(smoothedSpectrum);
        return smoothedSpectrum;
    }
    
    
    public Spectrum adaptiveIDSPVoight(double nu){
        
        //double[] waveValuesAux = (double[]) spectrum.getWaveValues();
        //double[] fluxValuesAux = (double[]) spectrum.getFluxValues();
        
        double[] waveValues = (double[]) spectrum.getWaveValues();
        double[] fluxValues = (double[]) spectrum.getFluxValues();
        
        double mean = MathUtils.average(fluxValues);
        
        //sigma = sigma*((waveValuesAux[waveValuesAux.length-1]-waveValuesAux[0])/1000);
        
        double range = Math.abs(waveValues[waveValues.length-1]-waveValues[0])/999;
/*
        int contAux = 0;
        for(int k = 0; k < waveValuesAux.length; k++){
            if(fluxValuesAux[k] <= 0){
                contAux++;
            }
        }
 
        int j = 0;
 
        double[] waveValues = new double[waveValuesAux.length - contAux];
        double[] fluxValues = new double[waveValuesAux.length - contAux];
 
        for(int k = 0; k < waveValuesAux.length; k++){
            if(fluxValuesAux[k] > 0){
                waveValues[j] = waveValuesAux[k];
                fluxValues[j] = fluxValuesAux[k];
                j++;
            }
        }
 */
        //double[] waveValues = (double[]) spectrum.getWaveValues();
        //double[] fluxValues = (double[]) spectrum.getFluxValues();
        
        double[] smoothedFluxValues = new double[waveValues.length];
        
        for(int k = 0; k < waveValues.length; k++){
            
            double sigma;
            
            if(fluxValues[k] !=0){
                sigma = range + range*Math.abs(1/(fluxValues[k]-mean));
            }else{
                sigma = range;
            }
            
            while(sigma >= 2*range) {
                
                sigma = sigma/10;
            }
            
            
            double sigmaG = sigma/2/Math.sqrt(2*Math.log(2));
            
            //}
            int i = 0;
            int firstIndex = 0;
            int lastIndex = waveValues.length;
            
            double xMin = waveValues[k]-3.6*sigma;
            double xMax = waveValues[k]+3.6*sigma;
            
            if(nu == 0){
                xMin = waveValues[k]-3*sigma;
                xMax = waveValues[k]+3*sigma;
            }
            if(nu == 1){
                xMin = waveValues[k]-4.2*sigma;
                xMax = waveValues[k]+4.2*sigma;
            }
            
            boolean first = false;
            boolean last = false;
            
            while(i < waveValues.length && last==false){
                if(xMin <= waveValues[i] && waveValues[i] <= xMax && first ==false){
                    first = true;
                    firstIndex = i;
                }
                if(waveValues[i] > xMax && last ==false){
                    last = true;
                    lastIndex = i;
                }
                i++;
            }//end while
            
            int length = (int)lastIndex-firstIndex;
            
            double[] spectrumArray = new double[length];
            double[] pVoightArray = new double[length];
            
            double lorentzianFunction;
            double gaussianFunction;
            
            //sigma = (1/2)*FWHM
            for(int ii = 0; ii < length; ii++){
                spectrumArray[ii] = fluxValues[firstIndex+ii];
                lorentzianFunction = sigma/(Math.pow((waveValues[firstIndex+ii]-waveValues[k]), 2) + sigma*sigma);
                //gaussianFunction = Math.exp(-(Math.pow((waveValues[firstIndex+ii]-waveValues[k]),2))/(2*sigmaG*sigmaG));
                gaussianFunction = Math.exp(-(Math.pow((waveValues[firstIndex+ii]-waveValues[k]),2))/(2*sigma*sigma));
                pVoightArray[ii] = nu * lorentzianFunction + (1-nu) * gaussianFunction;
            }
            
            smoothedFluxValues[k] = 0;
            
            double pVoightArea = 0;
            
            for(int ii = 0; ii < spectrumArray.length; ii ++){
                pVoightArea = pVoightArea + pVoightArray[ii]*range;
                
                smoothedFluxValues[k] = smoothedFluxValues[k] + spectrumArray[ii]*pVoightArray[ii];
            }
            
            
            //gaussianArea = Math.abs(gaussianArea*(1/(sigma*Math.sqrt(2*Math.PI))));
            pVoightArea = Math.abs(pVoightArea);
            
            if(xMin < waveValues[0] | xMax > waveValues[waveValues.length-1]){
                smoothedFluxValues[k] =  (1/pVoightArea)*smoothedFluxValues[k]*range;
            } else{
                //smoothedFluxValues[k] =  (1/Math.PI)*smoothedFluxValues[k]*range;
                smoothedFluxValues[k] =  (1/pVoightArea)*smoothedFluxValues[k]*range;
            }
            
            System.out.println("smoothedFluxValues["+k+"] = "+smoothedFluxValues[k]);
            System.out.println("lorentzianArea = "+pVoightArea);
            
        }//for k
        
        Spectrum smoothedSpectrum = new Spectrum();
        
        smoothedSpectrum.setWaveValues(waveValues);
        smoothedSpectrum.setFluxValues(smoothedFluxValues);
        
        //return MathUtils.rejectZeros(smoothedSpectrum);
        return smoothedSpectrum;
    }
    
    public Spectrum pVoightFiltering(double nu, double sigma, boolean sigmaFitted){
        
        double[] waveValues = (double[]) spectrum.getWaveValues();
        double[] fluxValues = (double[]) spectrum.getFluxValues();
        
       
        double range = Math.abs(waveValues[waveValues.length-1]-waveValues[0])/999;
        
        if(sigmaFitted==true) sigma = sigma*range;

        double[] smoothedFluxValues = new double[waveValues.length];
        
        for(int k = 0; k < waveValues.length; k++){
                                   
            double sigmaG = sigma/2/Math.sqrt(2*Math.log(2));
            
            int i = 0;
            int firstIndex = 0;
            int lastIndex = waveValues.length;
            
            double xMin = waveValues[k]-3.6*sigma;
            double xMax = waveValues[k]+3.6*sigma;
            
            if(nu == 0){
                xMin = waveValues[k]-3*sigma;
                xMax = waveValues[k]+3*sigma;
            }
            if(nu == 1){
                xMin = waveValues[k]-4.2*sigma;
                xMax = waveValues[k]+4.2*sigma;
            }
            
            boolean first = false;
            boolean last = false;
            
            while(i < waveValues.length && last==false){
                if(xMin <= waveValues[i] && waveValues[i] <= xMax && first ==false){
                    first = true;
                    firstIndex = i;
                }
                if(waveValues[i] > xMax && last ==false){
                    last = true;
                    lastIndex = i;
                }
                i++;
            }//end while
            
            int length = (int)lastIndex-firstIndex;
            
            double[] spectrumArray = new double[length];
            double[] pVoightArray = new double[length];
            
            double lorentzianFunction;
            double gaussianFunction;
            
            //sigma = (1/2)*FWHM
            for(int ii = 0; ii < length; ii++){
                spectrumArray[ii] = fluxValues[firstIndex+ii];
                lorentzianFunction = sigma/(Math.pow((waveValues[firstIndex+ii]-waveValues[k]), 2) + sigma*sigma);
                //gaussianFunction = Math.exp(-(Math.pow((waveValues[firstIndex+ii]-waveValues[k]),2))/(2*sigmaG*sigmaG));
                gaussianFunction = Math.exp(-(Math.pow((waveValues[firstIndex+ii]-waveValues[k]),2))/(2*sigma*sigma));
                pVoightArray[ii] = nu * lorentzianFunction + (1-nu) * gaussianFunction;
            }
            
            smoothedFluxValues[k] = 0;
            
            double pVoightArea = 0;
            
            for(int ii = 0; ii < spectrumArray.length; ii ++){
                pVoightArea = pVoightArea + pVoightArray[ii]*range;
                
                smoothedFluxValues[k] = smoothedFluxValues[k] + spectrumArray[ii]*pVoightArray[ii];
            }
            
            
            //gaussianArea = Math.abs(gaussianArea*(1/(sigma*Math.sqrt(2*Math.PI))));
            pVoightArea = Math.abs(pVoightArea);
            
            if(xMin < waveValues[0] | xMax > waveValues[waveValues.length-1]){
                smoothedFluxValues[k] =  (1/pVoightArea)*smoothedFluxValues[k]*range;
            } else{
                //smoothedFluxValues[k] =  (1/Math.PI)*smoothedFluxValues[k]*range;
                smoothedFluxValues[k] =  (1/pVoightArea)*smoothedFluxValues[k]*range;
            }
            
        }//for k
        
        Spectrum smoothedSpectrum = new Spectrum();
        
        smoothedSpectrum.setWaveValues(waveValues);
        smoothedSpectrum.setFluxValues(smoothedFluxValues);
        
        //return MathUtils.rejectZeros(smoothedSpectrum);
        return smoothedSpectrum;
    }
    
    
    
    
        /*Looks for indices of the neighbour waveValues of the input referenceWaveValue.
         *If neighbourWaveValue < xMin or > xMax index = -1 is returned.
         *Array waveValues is supposed to be ordered.
         */
    public int[] neighboursIndex(double[] waveValues, int referenceWaveValueIndex, int numPoints){
        
        int numSubIntervals = 10;
        
        double xMin = waveValues[0];
        double xMax = waveValues[waveValues.length-1];
        
        //waveValues are supposesd to be ordered
        double xMinRange = waveValues[referenceWaveValueIndex]-0.5*(xMax-xMin)/numSubIntervals;
        double xMaxRange = waveValues[referenceWaveValueIndex]+0.5*(xMax-xMin)/numSubIntervals;
        
        int length = 0;
        int finalLength = 0;
        
        if(referenceWaveValueIndex < numPoints){
            
            length = referenceWaveValueIndex + numPoints + 1;
            
        } else if(referenceWaveValueIndex + numPoints > waveValues.length-1){
            
            length = waveValues.length-(referenceWaveValueIndex - numPoints);
            
        } else{
            
            length = 2*numPoints + 1;
        }
        
        int[] neighboursIndex = new int[length];     //Index of the input waveValues array to be considered
        
        int cont = 0;
        
        //from waveValues[refIndex-numPoints] to waveValues[refIndex-1]
        for(int i = numPoints; i > 0; i --){
            if(0 <= (referenceWaveValueIndex - i)){
                //avoiding backward gaps
                if(xMinRange <= waveValues[referenceWaveValueIndex-i]){
                    neighboursIndex[cont] = referenceWaveValueIndex-i;
                    finalLength++;
                    cont++;
                }else{
                    neighboursIndex[cont] = -1;
                    cont++;
                }
            }
        }
        
        //from waveValues[refIndex] to waveValues[refIndex+numPoints]
        for(int i = 0; i <= numPoints; i ++){
            if((referenceWaveValueIndex + i) < waveValues.length){
                //avoiding forward gaps
                if(waveValues[referenceWaveValueIndex+i] <= xMaxRange){
                    neighboursIndex[cont] = referenceWaveValueIndex+i;
                    finalLength++;
                    cont++;
                } else {
                    neighboursIndex[cont] = -1;
                    cont++;
                }
            }
        }
        
        int[] finalIndex = new int[finalLength];
        cont = 0;
        
        //rejecting -1 entries of neighboursIndex
        for(int k = 0; k < neighboursIndex.length; k++){
            if(neighboursIndex[k] != -1){
                finalIndex[cont] = neighboursIndex[k];
                cont++;
            }
        }
        
        //This array contains neighbour indices inside xMinRange and xMaxRange(AVOIDS GAPS)
        return finalIndex;
        
    }
}
