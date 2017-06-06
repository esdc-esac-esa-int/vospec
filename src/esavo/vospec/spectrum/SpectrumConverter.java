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

import esavo.vospec.main.*;
import javax.swing.SwingUtilities;




public class SpectrumConverter extends Thread {
    
    public Unit             finalUnits;
    public SpectraViewer    sv;
    public Spectrum         spectrum;
    
    public double           redShift;
    public double           properRedShift;
    
    public static double    SPEED_LIGHT = 299792458.;
    public static Unit      mksUnits = new Unit("L","1.","ML-1T-3","1.");
    
    double[]    wav;
    double[]    flu;
    boolean     realData;

    double[]    wavErrorUpper;
    double[]    wavErrorLower;
    double[]    fluErrorUpper;
    double[]    fluErrorLower;
    boolean wavErrors = false;
    boolean fluErrors = false;
    
    esavo.utils.units.dimeq.SpectrumConverter specConverterFromOriginalToMKS;
    esavo.utils.units.dimeq.SpectrumConverter specConverterFromMKSToFinal;
    
    double[]    fluxCoeficients;
    double      fluxFactor;
    
    long[]      mksWCoefficients;
    double      mksWFactor;
    
    double[]    waveCoeficients;
    double      waveFactor;
    
    int         ct;
    boolean     con;
    boolean     toDisplay;
    
    public double       refWave          = 0.;
    public double       refWaveInMeters  = 0.;
    public double       refWaveInOrigUnits = 0.;
    
    boolean     refWaveSelected;
    public VOSpecDetached vospec;

  
    public SpectrumConverter() {
        this.redShift       = 0.;
        this.refWave        = 0.;
        this.toDisplay      = false;
        this.refWaveSelected= false;
        this.sv             = null;
        
    }
    
    public SpectrumConverter(String waveUnits, String fluxUnits, SpectraViewer sv) {
        
        this();
        
        this.finalUnits     = new Unit(waveUnits,fluxUnits);
        this.sv             = sv;
        this.redShift       = 0.;
        this.refWave        = 0.;
    }
    
    public SpectrumConverter(String waveUnits, String fluxUnits, SpectraViewer sv, double refWave) {
        
        this();
        
        this.finalUnits     = new Unit(waveUnits,fluxUnits);
        this.sv             = sv;
        this.redShift       = 0.;
        this.refWave        = refWave;
        //System.out.println("refWave at constructor = "+refWave);
    }

    public void setAioSpecToolDetached(VOSpecDetached vospec){
        this.vospec = vospec;
    }
    
    public double[] convertPoint(double wave,double flux, Unit originalUnits, Unit finalUnits) {
        
        double[] waveValues = new double[1];
        double[] fluxValues = new double[1];
        waveValues[0] = wave;
        fluxValues[0] = flux;
        
        Spectrum sp = new Spectrum();
        sp.setWaveValues(waveValues);
        sp.setFluxValues(fluxValues);
        sp.setUnits(originalUnits);
        
        try {
            sp = convertSpectrum(sp,finalUnits);
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        double[] finalValue = new double[2];
        finalValue[0] = (sp.getWaveValues())[0];
        finalValue[1] = (sp.getFluxValues())[0];
        
        return finalValue;
    }
    
    public void setRedShift(double redShift) {
        this.redShift = redShift;
    }
    
    public void setWaveToVel(double refWave) {
        this.refWave = refWave;
        this.refWaveSelected = true;
        System.out.println("rerfwave in spectrum converter class, setWaveToVel method = "+this.refWave);
    }
    
    public void calculateCoefficients(Spectrum spectrum) throws Exception {
        
        Unit originalUnits;
        
        
        wav                         = spectrum.getWaveValues();
        flu                         = spectrum.getFluxValues();

        wavErrorUpper = spectrum.getWaveErrorUpper();
        wavErrorLower = spectrum.getWaveErrorLower();
        fluErrorUpper = spectrum.getFluxErrorUpper();
        fluErrorLower = spectrum.getFluxErrorLower();

        if(wavErrorUpper==null)wavErrorUpper = new double[wav.length];
        if(wavErrorLower==null)wavErrorLower = new double[wav.length];
        if(fluErrorUpper==null)fluErrorUpper = new double[flu.length];
        if(fluErrorLower==null)fluErrorLower = new double[flu.length];

        wavErrors = spectrum.isWaveErrorsPresent();
        fluErrors = spectrum.isFluxErrorsPresent();


        originalUnits               = spectrum.getUnits();
        realData                    = spectrum.getRealData();
        
        properRedShift              = spectrum.getRedShift();
        
        //  Extraction of scaleq/dimeq from spectrum units
        double waveScalingOriginal  = ((Double) originalUnits.getWaveVector().elementAt(0)).doubleValue();
        String waveDimeqOriginal    = (String)              originalUnits.getWaveVector().elementAt(1);
        
        double fluxScalingOriginal  = ((Double) originalUnits.getFluxVector().elementAt(0)).doubleValue();
        String fluxDimeqOriginal    = (String)              originalUnits.getFluxVector().elementAt(1);
        
        
        //  Extraction of scaleq/dimeq from International System units [L,ML-1T-3] [m,W/m^2/m] (we need to go first to these units to calculate the redshift
        //  and/or to correct from rederening)
        double waveScalingMKS       = ((Double)             mksUnits.getWaveVector().elementAt(0)).doubleValue();
        String waveDimeqMKS         = (String)              mksUnits.getWaveVector().elementAt(1);
        
        double fluxScalingMKS       = ((Double)             mksUnits.getFluxVector().elementAt(0)).doubleValue();
        String fluxDimeqMKS         = (String)              mksUnits.getFluxVector().elementAt(1);
        
        
        
        //  Extraction of scaleq/dimeq from final units
        double waveScalingFinal     = ((Double)             finalUnits.getWaveVector().elementAt(0)).doubleValue();
        String waveDimeqFinal       = (String)              finalUnits.getWaveVector().elementAt(1);
        
        double fluxScalingFinal     = ((Double)             finalUnits.getFluxVector().elementAt(0)).doubleValue();
        String fluxDimeqFinal       = (String)              finalUnits.getFluxVector().elementAt(1);
         
        
        //System.out.println(waveScalingOriginal+" "+ waveDimeqOriginal+" "+ fluxScalingOriginal+" "+ fluxDimeqOriginal+" "+
        //        waveScalingMKS+" "+      waveDimeqMKS+" "+      fluxScalingMKS+" "+      fluxDimeqMKS);

        specConverterFromOriginalToMKS    = new esavo.utils.units.dimeq.SpectrumConverter(waveScalingOriginal, waveDimeqOriginal, fluxScalingOriginal, fluxDimeqOriginal,
                waveScalingMKS,      waveDimeqMKS,      fluxScalingMKS,      fluxDimeqMKS);
        
        
        specConverterFromMKSToFinal       = new esavo.utils.units.dimeq.SpectrumConverter(waveScalingMKS,   waveDimeqMKS,   fluxScalingMKS,   fluxDimeqMKS,
                waveScalingFinal, waveDimeqFinal, fluxScalingFinal, fluxDimeqFinal);
        
        
    }
    
    public void setSpectrum(Spectrum spectrum){
        this.spectrum = spectrum;
    }
    
    public Spectrum getSpectrum(){
        return this.spectrum;
    }
    
    public SpectraViewer convertSpectrum(Spectrum spectrum,int ct, boolean con) throws Exception {
        
        this.ct         = ct;
        this.con        = con;
        this.toDisplay  = true;
        
        Spectrum returnSpectrum = convertSpectrum(spectrum, finalUnits);
        return sv;
    }
    
    public Spectrum convertSpectrum(Spectrum spectrum,final Unit finalUnits) throws Exception {
        
        //if(toDisplay) vospec.utils.addRunningSpectrum(spectrum);
        
        Spectrum returnSpectrum = new Spectrum(spectrum);
        
        this.redShift   = redShift;
        this.refWave    = refWave;
        this.finalUnits = finalUnits;
        
        calculateCoefficients(spectrum);
        
        boolean isTsap = spectrum.getToBeNormalized();
        double[] waved = null;
        double[] fluxd = null;
        double[] wavdErrorLower = null;
        double[] wavdErrorUpper = null;
        double[] fludErrorLower = null;
        double[] fludErrorUpper = null;



        try{
            waved = new double[wav.length];
            fluxd = new double[wav.length];
            wavdErrorLower = new double[wav.length];
            wavdErrorUpper = new double[wav.length];
            fludErrorLower = new double[wav.length];
            fludErrorUpper = new double[wav.length];


        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Flux column name "+this.spectrum.getFluxColumnName()+" wave column name "+this.spectrum.getWaveLengthColumnName());
            //JOptionPane.showMessageDialog(null, "Cannot retrieve wave or flux values for spectrum "+ spectrum.getTitle()+". Check if AXES name have been sent");
            //return null;
        }

        if(wav == null || flu == null){
            throw new Exception();
        }
        
        double      wave;
        double      flux;
        
        double      velocity =0.;
        double[]    point = new double[2];


        for (int u=0 ; u<wav.length ; u++) {
            
            double waveOrig    = wav[u];
            double fluxOrig    = flu[u];

            wave    = wav[u];
            flux    = flu[u];
            
            //System.out.println("flux in 1 = "+flux);
            
            point   = specConverterFromOriginalToMKS.getConvertedPoint(wave, flux);
            wave    = point[0];
            flux    = point[1];
            
            //System.out.println("flux in 2 = "+flux);
            
            if(spectrum.isInVelocity()){
                velocity = wave;
                //System.out.println("velocity in 1 = "+velocity);
                
            }
            if(!refWaveSelected && spectrum.isInVelocity()) {
                //System.out.println("spectrum.getRefWave = "+ spectrum.getRefWavelength());
                wave = removeProperWave(spectrum.getRefWavelength(), velocity);   //meters
            }
            
            //System.out.println("flux in 3 = "+flux);
            
            wave    = removeProperRedShift(wave);
            
            if(spectrum.getToBeNormalized()) flux = flux * spectrum.getNorm();
            
            wave    = applyRedShift(wave);
            
            if(refWaveSelected && spectrum.isInVelocity()==false) velocity = applyWaveToVel(wave);
            
            //I do not know if this is here or before the redshift correction
            DeReddening dr = new DeReddening(vospec);
            if(dr.deReddening() && !spectrum.getToBeNormalized()) {
                flux    = dr.getDeRedFlux(wave,flux);
            }
            
            
            if(refWaveSelected)   waved[u] = velocity;
            else{
                point   = specConverterFromMKSToFinal.getConvertedPoint(wave,flux);
                wave    = point[0];
                flux    = point[1];
                
                //System.out.println("flux in 4 = "+flux);
                
                waved[u] = wave;
            }
            
            fluxd[u] = flux;
            
            //Convert error values to the proper units
            
            try {

                point   = specConverterFromOriginalToMKS.getConvertedPoint(waveOrig - wavErrorLower[u], fluxOrig);
                point = specConverterFromMKSToFinal.getConvertedPoint(point[0], point[1]);
                wavdErrorLower[u] = wave - point[0];

                point = specConverterFromOriginalToMKS.getConvertedPoint(waveOrig + wavErrorUpper[u], fluxOrig);
                point = specConverterFromMKSToFinal.getConvertedPoint(point[0], point[1]);
                wavdErrorUpper[u] = point[0] - wave;

                point = specConverterFromOriginalToMKS.getConvertedPoint(waveOrig, fluxOrig - fluErrorLower[u]);
                point = specConverterFromMKSToFinal.getConvertedPoint(point[0], point[1]);
                fludErrorLower[u] = flux - point[1];

                point = specConverterFromOriginalToMKS.getConvertedPoint(waveOrig, fluxOrig + fluErrorUpper[u]);
                point = specConverterFromMKSToFinal.getConvertedPoint(point[0], point[1]);
                fludErrorUpper[u] = point[1] -flux ;
                
            } catch (Exception e) {
            }

            
            
            
        }

        final double wavedf[] = waved;
        final double fluxdf[] = fluxd;
        final double wavdfErrorLower[] = wavdErrorLower;
        final double wavdfErrorUpper[] = wavdErrorUpper;;
        final double fludfErrorLower[] = fludErrorLower;
        final double fludfErrorUpper[] = fludErrorLower;
        final boolean isTsapf = isTsap;
        
        if (toDisplay) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    for (int u = 0; u < wav.length; u++) {

                        if(vospec.launchThreads.finishing()) return;

                        if (wavErrors && fluErrors) {
                            sv.drawSpectrumConvertedErrorDouble(ct, wavedf[u], fluxdf[u], wavdfErrorLower[u], wavdfErrorUpper[u], fludfErrorLower[u], fludfErrorUpper[u], con, realData, isTsapf);
                        } else if (wavErrors) {
                            sv.drawSpectrumConvertedErrorWave(ct, wavedf[u], fluxdf[u], wavdfErrorLower[u], wavdfErrorUpper[u], con, realData, isTsapf);
                        } else if (fluErrors) {
                            sv.drawSpectrumConvertedErrorFlux(ct, wavedf[u], fluxdf[u], fludfErrorLower[u], fludfErrorUpper[u], con, realData, isTsapf);
                        } else {
                            sv.drawSpectrumConverted(ct, wavedf[u], fluxdf[u], con, realData, isTsapf);
                        }
                        con = true;
                    }
                    sv.updateViewer(finalUnits.getWaveUnits(),finalUnits.getFluxUnits());
                    
               }
            });

        }
        
        returnSpectrum.setWaveErrorLower(wavdErrorLower);
        returnSpectrum.setWaveErrorUpper(wavdErrorUpper);
        returnSpectrum.setWaveValues(waved);
        returnSpectrum.setFluxErrorUpper(fludErrorUpper);
        returnSpectrum.setFluxErrorLower(fludErrorLower);
        returnSpectrum.setFluxValues(fluxd);
        returnSpectrum.setUnits(finalUnits);
        
        return returnSpectrum;
    }
    
    
    
    
    
    
    public double applyRedShift(double mksWaved) {
        
        double redShiftToApply  = redShift;
        double newMksWaved      = mksWaved /(1. + redShiftToApply);
        
        return newMksWaved;
    }
    
    public double applyWaveToVel(double mksWaved) {
        
        /*light velocity km/s*/
        double c = 299792.458;
        double newMksWaved      = c*(mksWaved - refWave)/refWave;
        
        return newMksWaved;
    }
    
    public double removeProperRedShift(double mksWaved) {
        
        double redShiftToApply = properRedShift;
        
        double newMksWaved = mksWaved + mksWaved * redShiftToApply;
        return newMksWaved;
    }
    
    public double removeProperWave(double refWave, double velocity) {
        
        /*light velocity km/s*/
        double c = 299792.458;
        
        //velocity = velocity/1000;//velocity in meters --> velocity in Km
        
        double newMksWaved      = refWave*(1. + velocity/c);//in meters
        
        // Velocity in km/s and output in meters!
        return newMksWaved;
    }
    
    public void setRunProperties(Spectrum spectrum, int ct, boolean con) {
        
        this.ct         = ct;
        this.con        = con;
        this.toDisplay  = true;
        this.spectrum = spectrum;
        
    }

    public void run() {


        try {
            sv = convertSpectrum(this.spectrum, this.ct, this.con);
        } catch (Exception e) {
            vospec.utils.problemsInSpectrumConverter(ct);
            System.out.println("Problems converting " + spectrum.getUrl());
            //e.printStackTrace();
        } finally {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                   vospec.spectrumDownloaded();
                }
            });
        }

    }
}
