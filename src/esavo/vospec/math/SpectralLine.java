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

public class SpectralLine extends Spectrum{
    
    int     peakPosition;
    int     lineOrientation;
    
    double  lineMin;
    double  lineMax;
    double  lineRef;            //lineRef = Max(x0, xN)
    double  lineInter;          //lineInter = (y0+yN)/2
    
    //boolean alreadySmoothed = false;
    //boolean peakAlreadyCalculated = false;
    
    Spectrum    spectralLine;
    Spectrum    smoothedLine;
    
    
    /** Creates a new instance of SpectralLine */
    public SpectralLine() {
    }
    
    
    public SpectralLine(Spectrum spectrum){
        this.spectralLine = new OrderedSpectrum(spectrum);
        
        lineSmoothing();
        minMaxRefValues();
        lineOrientation();
        peakPosition();
    }
    
    
    
    
    public SpectralLine(Spectrum spectrum, boolean automatedSmoothing){
        this.spectralLine = new OrderedSpectrum(spectrum);
        
        if(automatedSmoothing)  lineSmoothing();
        else                    this.smoothedLine = new OrderedSpectrum(spectrum);
        
//      minMaxRefValues();
        lineOrientation();
        peakPosition();
    }
    
    
    
    
    /*Evaluates the peak position of an input spectral line.
     *Uses lineOrientation method to know the spectral line orientation.
     *In order to avoid outliers points, an smoothed line is used.
     *Spectral line is supposed to be already ordered!!
     */
    public void peakPosition() {
        
       double[] smoothedFluxValues;   
        //lineSmoothing();
        
        smoothedFluxValues = this.smoothedLine.getFluxValues();
        
        //lineOrientation();
        
        if(this.lineOrientation > 0){
            
//          int peakPositionSmoothedLine = MathUtils.minValuePosition(smoothedFluxValues);
            this.peakPosition = MathUtils.minValuePosition(smoothedFluxValues);
//          this.peakPosition = MathUtils.approxWaveValue(inputWaveValues, peakPositionSmoothedLine);
            
        }else{
            
//          int peakPositionSmoothedLine = MathUtils.maxValuePosition(smoothedFluxValues);
            this.peakPosition = MathUtils.maxValuePosition(smoothedFluxValues);
//          this.peakPosition =  MathUtils.approxWaveValue(inputWaveValues, peakPositionSmoothedLine);
            
        }
        
        
        
        System.out.println("peak position = "+peakPosition);
        System.out.println("peak value = "+smoothedFluxValues[peakPosition]);
//        System.out.println("next flux to peak value = "+smoothedFluxValues[peakPosition+1]);
        
    }
    
    public void lineSmoothing(){
        
        //if peakPosition method is called before lineSmoothing method,
        //the smoothedLine has already been calculated
        //with the following "if" we avoid to calculate the smoothed line again
        //if(this.alreadySmoothed == false){
        //Averaging the input spectrum
        //calculating 10% of input points
        int numTotalPoints      = (int) (spectralLine.getWaveValues()).length;
        int numPercentPoints    = (int) Math.floor((double) numTotalPoints/20);
        int numFinalPoints      = (int) Math.floor((double) numPercentPoints/2)-1;
        
//        System.out.println("numFinalPoints for smoothing = "+numFinalPoints);
        
        if(numFinalPoints >= 0) this.smoothedLine       = (new Smoothing(spectralLine)).nPointsMedianFilter(numFinalPoints);
        else                    this.smoothedLine       = (new Smoothing(spectralLine)).nPointsMedianFilter(0);
        
//        this.smoothedLine       = (new Smoothing(spectralLine)).medianFilter();
        
        
        //this.alreadySmoothed    = true;
        //}
    }
    
    
/*    public void lineOrientation(){
 
        if(this.peakAlreadyCalculated == false){
            PolynomialFitting pf = new PolynomialFitting(spectralLine,true,true,2);
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
            this.lineOrientation        =  parameterPol[2];
            this.peakAlreadyCalculated  = true;
        }
    }
 */
    
    public void minMaxRefValues(){
        double[] waveValues = this.smoothedLine.getWaveValues();
        double[] fluxValues = this.smoothedLine.getFluxValues();
        //double[] absFLuxValues = new double[fluxValues.length];
        
        int lineMinPosition     = MathUtils.minValuePosition(fluxValues);
        int lineMaxPosition     = MathUtils.maxValuePosition(fluxValues);
        
        this.lineMin          = fluxValues[lineMinPosition];
        this.lineMax          = fluxValues[lineMaxPosition];
        this.lineRef          = Math.max(fluxValues[0], fluxValues[fluxValues.length-1]);
        this.lineInter          = (fluxValues[0] + fluxValues[fluxValues.length-1])/2;
        
    }
/*
    public void lineOrientation(){
 
        //if(alreadySmoothed == false) lineSmoothing();
 
        double[] waveValues = this.smoothedLine.getWaveValues();
        double[] fluxValues = this.smoothedLine.getFluxValues();
        //double[] absFLuxValues = new double[fluxValues.length];
 
        // for(int k = 0; k < fluxValues.length; k++){
        //     absFLuxValues[k] = Math.abs(fluxValues[k]);
        //}
 
/*            System.out.println("smoothed line length = "+waveValues.length);
 
            int lineMinPosition     = MathUtils.minValuePosition(fluxValues);
          //int absMaxPosition     = MathUtils.maxValuePosition(absFluxValues);
            int lineMaxPosition     = MathUtils.maxValuePosition(fluxValues);
 
            this.lineMin          = fluxValues[lineMinPosition];
          //double lineAverage      = MathUtils.average(fluxValues);
            this.lineMax          = fluxValues[lineMaxPosition];
 
            //double lineBase         = (fluxValues[0]+fluxValues[fluxValues.length-1])/2;
 
 
            this.lineRef = Math.max(fluxValues[0], fluxValues[fluxValues.length-1]);
            double epsilon = Math.abs(fluxValues[lineMaxPosition] - fluxValues[lineMinPosition])/10000;
 
 */
/*       double epsilon = Math.abs(this.lineMax-this.lineMin)/100000;
 
        if(Math.abs(this.lineMax-this.lineRef) > epsilon){
            this.lineOrientation = -1;
        }else{
            this.lineOrientation = 1;
        }
 
        System.out.println("line orientation = "+lineOrientation);
 
        //double[] logWaveValues = new double[waveValues.length];
        //double[] logFluxValues = new double[waveValues.length];
 
            System.out.println("evaluatind log wave and flux values");
 
 
            for(int k = 0; k < waveValues.length; k++){
                //logWaveValues[k] = Math.pow(10, waveValues[k]);
                //logFluxValues[k] = Math.pow(10, fluxValues[k]);
 
                logWaveValues[k] = 1000*waveValues[k];
                logFluxValues[k] = 1000*fluxValues[k];
                System.out.println("wave and flux values = "+waveValues[k]+"  "+fluxValues[k]);
                System.out.println("log wave and flux values = "+logWaveValues[k]+"  "+logFluxValues[k]);
            }
 
            logSpectrum.setWaveValues(logWaveValues);
            logSpectrum.setFluxValues(logFluxValues);
 
 
        //}
    }
 */
    public void lineOrientation(){
        PolynomialFitting pf = new PolynomialFitting(this.spectralLine,true,true,2);
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
        if(a > 0)   this.lineOrientation = 1;
        else        this.lineOrientation = -1;
    }
    
    
    public Spectrum getSmoothedLine(){
        return this.smoothedLine;
    }
    
    public double getLineMin(){
        return this.lineMin;
    }
    
    public double getLineMax(){
        return this.lineMax;
    }
    
    public double getLineInter(){
        return this.lineInter;
    }
    
    public int getLineOrientation(){
        return this.lineOrientation;
    }
    
    public int getPeakPosition(){
        return this.peakPosition;
    }
    
    public double[] getWaveValues(){
        return this.spectralLine.getWaveValues();
    }
    
    public double[] getFluxValues(){
        return this.spectralLine.getFluxValues();
    }
    
    
    
    
    
    
    
}
