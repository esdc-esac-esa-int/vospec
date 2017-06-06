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

public class Bisector {
    
//    int           numBisectorPoints = 10;
    
    //double velocitySpan;
    //double curvature;
    
    double[] bisectorWaveValues;
    
    Spectrum      spectrum;
    //SpectralLine    spectralLine;
    
    /** Creates a new instance of Bisector */
    public Bisector() {
    }
    
    public Bisector(Spectrum spectrum){
        //this.spectralLine = new SpectralLine(spectrum);
        this.spectrum = spectrum;
    }
    
    
    
    public Spectrum getPointsAutomatedForm(int numBisectorPoints) {
        
        //spectral line data
        //int peakPosition        = this.spectralLine.getPeakPosition();
        
        SpectralLine spectralLine = new SpectralLine(this.spectrum, true);
        
        int lineOrientation  = spectralLine.getLineOrientation();
        
//        System.out.println("lineorientation = "+lineOrientation);
        
        //First, spectral line smoothing
        Spectrum smoothedLine   = spectralLine.getSmoothedLine();
        
        //Second, evenly spaced spectrum from smoothed spectral line spectrum
        Spectrum evenlySpacedLineSpectrum = MathUtils.evenlySpacedSpectrum(smoothedLine, 1000);
        
        //Creating spectral line from evenly spaced smoothed spectral line spectrum
        SpectralLine evenlySpacedLine = new SpectralLine(evenlySpacedLineSpectrum);
        
        int peakPosition        = evenlySpacedLine.getPeakPosition();
        
//        System.out.println("peakPosition = "+peakPosition);
//        System.out.println("==============================");
        
        //double[] smoothedWaveValues = evenlySpacedLine.getWaveValues();
        //double[] smoothedFluxValues = evenlySpacedLine.getFluxValues();
        
        
        //waveValues of smoothedSpectrum are equals to waveValues of previous spectrum
        //double[] evenlySpacedWaveValues     = evenlySpacedLine.getWaveValues();
        //double[] evenlySpacedFluxValues     = evenlySpacedLine.getFluxValues();
        double[] evenlySpacedWaveValues     = evenlySpacedLineSpectrum.getWaveValues();
        double[] evenlySpacedFluxValues     = evenlySpacedLineSpectrum.getFluxValues();
        
        double startingFluxValue;
        
        /*   //minFlux is the one beetween minLeft and minRigth bisectorFluxValues that is "closest" to maxFlux
         *
         *                                  * *<--maxFlux
         *                               *       *
         *                             *           *
         *    minLflux = minFlux-->  *               *
         *                                             *
         *                                                 *<--minRflux
         */
        
        if(lineOrientation < 0){
            //minFlux rigth and left sides positions
            int minLIndex = MathUtils.minValuePosition(evenlySpacedFluxValues, 0, peakPosition-1);
            int minRIndex = MathUtils.minValuePosition(evenlySpacedFluxValues, peakPosition,evenlySpacedFluxValues.length-1);
            
//            System.out.println("minLFluxValue = "+evenlySpacedFluxValues[minLIndex]);
//            System.out.println("minRFluxValue = "+evenlySpacedFluxValues[minRIndex]);
            
            startingFluxValue = Math.max(evenlySpacedFluxValues[minLIndex], evenlySpacedFluxValues[minRIndex]);
            
        } else{
            
            int maxLIndex = MathUtils.maxValuePosition(evenlySpacedFluxValues, 0, peakPosition-1);
            int maxRIndex = MathUtils.maxValuePosition(evenlySpacedFluxValues, peakPosition,evenlySpacedFluxValues.length-1);
            
            startingFluxValue = Math.min(evenlySpacedFluxValues[maxLIndex], evenlySpacedFluxValues[maxRIndex]);
        }
        
        //double h = Math.abs(fluxValues[peakPosition]-startingFluxValue);
        double h = evenlySpacedFluxValues[peakPosition]-startingFluxValue;
        
        
//        System.out.println("startingFluxValue = "+startingFluxValue);
//        System.out.println("h = "+h);
        
        
        double[] bisectorFluxValues = new double[numBisectorPoints];
        
        //bisectorFluxValues[0] and bisectorFluxValues[numBisectorPOints-1] belongs to evenlySpacedFluxValues
        for(int i = 0; i < numBisectorPoints; i++){
            bisectorFluxValues[i] = startingFluxValue + h*i/(numBisectorPoints-1);
//            System.out.println("bisectorFluxValue["+i+" = "+bisectorFluxValues[i]);
        }
        
        //double[] leftWaveValues     = new double[numBisectorPoints];
        //double[] rightWaveValues    = new double[numBisectorPoints];
        
        //double[] bisectorWaveValues = new double[numBisectorPoints];
         bisectorWaveValues = new double[numBisectorPoints];
        
        //bisectorWaveValues[0] = evenlySpacedWaveValues[peakPosition];
        //bisectorFluxValues[0] = evenlySpacedFluxValues[peakPosition];
        
        //most approx Lflux and Rflux to bisectorFluxValues[i]
        for(int i = 0; i < numBisectorPoints; i++){
            //for(int i = 1; i < numBisectorPoints; i++){
            
            int leftBackwardIndex = MathUtils.backwardFluxIndex(evenlySpacedFluxValues, bisectorFluxValues[i], 0, peakPosition);
            int leftForwardIndex  = MathUtils.forwardFluxIndex(evenlySpacedFluxValues, bisectorFluxValues[i], 0, peakPosition);
            
//           System.out.println("=======================================================================");
//           System.out.println("leftBakwardFluxValue = "+evenlySpacedFluxValues[leftBackwardIndex]);
//           System.out.println("bisectorFluxValue = "+bisectorFluxValues[i]);
//           System.out.println("leftForwardFluxValue = "+evenlySpacedFluxValues[leftForwardIndex]);
//           System.out.println("=======================================================================");
            
            double leftWaveValue = (evenlySpacedWaveValues[leftBackwardIndex]+evenlySpacedWaveValues[leftForwardIndex])/2;
            
            int rightBackwardIndex = MathUtils.backwardFluxIndex(evenlySpacedFluxValues, bisectorFluxValues[i], peakPosition, evenlySpacedFluxValues.length-1);
            int rightForwardIndex  = MathUtils.forwardFluxIndex(evenlySpacedFluxValues, bisectorFluxValues[i], peakPosition, evenlySpacedFluxValues.length-1);
            
//            System.out.println("=======================================================================");
//            System.out.println("rightBakwardFluxValue = "+evenlySpacedFluxValues[rightBackwardIndex]);
//            System.out.println("bisectorFluxValue = "+bisectorFluxValues[i]);
//            System.out.println("rightForwardFluxValue = "+evenlySpacedFluxValues[rightForwardIndex]);
//            System.out.println("=======================================================================");
            
            
            double rightWaveValue = (evenlySpacedWaveValues[rightBackwardIndex]+evenlySpacedWaveValues[rightForwardIndex])/2;
            
            bisectorWaveValues[i] = (leftWaveValue + rightWaveValue)/2;
            
//            System.out.println("bisector wave value["+i+"] = "+bisectorWaveValues[i]);
            
        }//for i < numBisectorPoints
        
        
        
        Spectrum bisectorSpectrum = new Spectrum();
        
        bisectorSpectrum.setWaveValues(bisectorWaveValues);
        bisectorSpectrum.setFluxValues(bisectorFluxValues);
        
        return bisectorSpectrum;
    }
    
    
    public Spectrum getPointsManualForm(int numBisectorPoints) {
        
        boolean automatedForm = false;
        
        Spectrum evenlySpacedSpectrum = MathUtils.evenlySpacedSpectrum(this.spectrum, 1000);
        
        SpectralLine spectralLine = new SpectralLine(evenlySpacedSpectrum, automatedForm);
        
//      SpectralLine spectralLine = new SpectralLine(this.spectrum, automatedForm);
        
        int lineOrientation  = spectralLine.getLineOrientation();
        
        System.out.println("line orientation in bisector class = "+ lineOrientation);
        
        int peakPosition     = spectralLine.getPeakPosition();
        
        System.out.println("peakPosition in bisector class = "+peakPosition);
        
        
        
//        double[] waveValues     = evenlySpacedLineSpectrum.getWaveValues();
//        double[] fluxValues     = evenlySpacedLineSpectrum.getFluxValues();
        
        
        
        double[] waveValues     = spectralLine.getWaveValues();
        double[] fluxValues     = spectralLine.getFluxValues();
        
//         double[] waveValues     = this.spectrum.getWaveValues();
//         double[] fluxValues     = this.spectrum.getFluxValues();
        
        
        
        // System.out.println(" flux values length = "+lineFluxValues.length);
        double startingFluxValue;
        
        /*   //minFlux is the one beetween minLeft and minRigth bisectorFluxValues that is "closest" to maxFlux
         *
         *                                  * *<--maxFlux
         *                               *       *
         *                             *           *
         *    minLflux = minFlux-->  *               *
         *                                             *
         *                                                 *<--minRflux
         */
        
        if(lineOrientation < 0){
            //minFlux rigth and left sides positions
            int minLIndex = MathUtils.minValuePosition(fluxValues, 0, peakPosition-1);
            int minRIndex = MathUtils.minValuePosition(fluxValues, peakPosition,fluxValues.length-1);
            startingFluxValue = Math.max(fluxValues[minLIndex], fluxValues[minRIndex]);
            
        } else{
            
            int maxLIndex = MathUtils.maxValuePosition(fluxValues, 0, peakPosition-1);
            int maxRIndex = MathUtils.maxValuePosition(fluxValues, peakPosition,fluxValues.length-1);
            
            startingFluxValue = Math.min(fluxValues[maxLIndex], fluxValues[maxRIndex]);
        }
        
        double h = fluxValues[peakPosition]-startingFluxValue;
        
        double[] bisectorFluxValues = new double[numBisectorPoints];
        
        //bisectorFluxValues[0] and bisectorFluxValues[numBisectorPOints-1] belongs to evenlySpacedFluxValues
        for(int i = 0; i < numBisectorPoints; i++){
            bisectorFluxValues[i] = startingFluxValue + h*i/(numBisectorPoints-1);
            
        }
        
        //double[] bisectorWaveValues = new double[numBisectorPoints];
        bisectorWaveValues = new double[numBisectorPoints];
        
        //most approx Lflux and Rflux to bisectorFluxValues[i]
        for(int i = 0; i < numBisectorPoints; i++){
            //for(int i = 1; i < numBisectorPoints; i++){
            
            int leftBackwardIndex = MathUtils.backwardFluxIndex(fluxValues, bisectorFluxValues[i], 0, peakPosition);
            int leftForwardIndex  = MathUtils.forwardFluxIndex(fluxValues, bisectorFluxValues[i], 0, peakPosition);
            
            double leftWaveValue = (waveValues[leftBackwardIndex]+waveValues[leftForwardIndex])/2;
            
            int rightBackwardIndex = MathUtils.backwardFluxIndex(fluxValues, bisectorFluxValues[i], peakPosition, fluxValues.length-1);
            int rightForwardIndex  = MathUtils.forwardFluxIndex(fluxValues, bisectorFluxValues[i], peakPosition, fluxValues.length-1);
            
            
            double rightWaveValue = (waveValues[rightBackwardIndex]+waveValues[rightForwardIndex])/2;
            
            bisectorWaveValues[i] = (leftWaveValue + rightWaveValue)/2;
            
            
        }//for i < numBisectorPoints
        
        
        
        Spectrum bisectorSpectrum = new Spectrum();
        
        bisectorSpectrum.setWaveValues(bisectorWaveValues);
        bisectorSpectrum.setFluxValues(bisectorFluxValues);
        
        return bisectorSpectrum;
    }
    
    public double getVelocitySpan(){
        double velocitySpan = bisectorWaveValues[bisectorWaveValues.length-2]-bisectorWaveValues[1];
        return velocitySpan;
    }
    
//    public double getVelocitySpan(double[] wave){
//        double velocitySpan = wave[wave.length-2]-wave[1];
//        return velocitySpan;
//    }
    
    
    public double getCurvature(){
        int midFluxPosition = (int) Math.ceil(bisectorWaveValues.length/2);
        double velocitySpan1 = bisectorWaveValues[bisectorWaveValues.length-2]-bisectorWaveValues[midFluxPosition];
        double velocitySpan2 = bisectorWaveValues[midFluxPosition]-bisectorWaveValues[1];
        
        double curvature = velocitySpan1-velocitySpan2;
        
        return curvature;
    }
}
