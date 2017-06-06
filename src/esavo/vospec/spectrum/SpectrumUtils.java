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

import esavo.vospec.main.VOSpecDetached;
import java.util.Vector;

/**
 *
 * @author  jsalgado
 */
public class SpectrumUtils {
    
    private SpectrumUtils() {}
    
    
    public static Spectrum getSpectrum(Vector data) {
        
        double[] waveValues = new double[data.size()];
        double[] fluxValues = new double[data.size()];
        
        
        for(int i=0; i < data.size(); i++) {
            
            double[] point	= (double[]) data.elementAt(i);
            
            waveValues[i] 	= point[0];
            fluxValues[i] 	= point[1];
            
        }
        
        Spectrum spectrum = new Spectrum();
        spectrum.setWaveValues(waveValues);
        spectrum.setFluxValues(fluxValues);
        
        return spectrum;
    }
    
    public static void checkVelocity(VOSpecDetached aiospectooldetached, Spectrum spectrum){
        String waveChoice = aiospectooldetached.getWaveChoice();
        String fluxChoice = aiospectooldetached.getFluxChoice();
        
        double refWaveInAioSpecUnits = aiospectooldetached.getRefWaveValueAioSpecUnits();
        
        Unit units;
        
        if(aiospectooldetached.isInVelocitySpace() && spectrum.getBB()==false) {
            
            double refWave      = aiospectooldetached.getRefWaveValue();//already in meters
            
            String title = spectrum.getTitle();
            spectrum.setTitle(title+ "(Velocity spectrum,refWave=" + refWaveInAioSpecUnits + waveChoice +")");
            
            waveChoice = "m";
            
            spectrum.setRefWavelength(refWave);
            spectrum.setToBeNormalized(true);
            
            units        = new Unit("L","1.","ML-1T-3","1.");
            spectrum.setUnits(units);
            
        } else if(spectrum.getUnitsF()==null && spectrum.getBB()==false){
            
            
            System.out.println("UnitsF == null");
                
            units       = new Unit(waveChoice, fluxChoice);
            spectrum.setUnits(units);
        }

    }
    
    
    
    
    
    public static void setParameters(VOSpecDetached aiospectooldetached, Spectrum spectrum, String title, int execution){
        String waveChoice = aiospectooldetached.getWaveChoice();
        String fluxChoice = aiospectooldetached.getFluxChoice();
        
        
        System.out.println("UnitsF ="+spectrum.getUnitsF());
        
        double refWaveInAioSpecUnits = aiospectooldetached.getRefWaveValueAioSpecUnits();
        
        Unit units;
        
        if(aiospectooldetached.isInVelocitySpace() && spectrum.getBB()==false) {
            
            title = title+ "(Velocity spectrum,refWave=" + refWaveInAioSpecUnits + waveChoice +")";
            
            double refWave      = aiospectooldetached.getRefWaveValue();//already in meters
            
            waveChoice = "m";
            
            spectrum.setRefWavelength(refWave);
            spectrum.setToBeNormalized(true);
            
            units        = new Unit("L","1.","ML-1T-3","1.");
            spectrum.setUnits(units);
            
            
            
            System.out.println("setParameters method. AioSpecToolDetached in velocity");
        }else if(spectrum.getUnitsF()==null && spectrum.getBB()==false) {
            
            System.out.println("UnitsF == null"+spectrum.getUnitsF());
            
                units = new Unit(waveChoice,fluxChoice);
                spectrum.setUnits(units);            
            
        }
        
        
        //spectrum.addMetaData(" TITLE ", title);
        spectrum.setTitle(title);
        spectrum.setUrl(title+execution);
        spectrum.setRedShift(aiospectooldetached.getRedShift());
        // to be displayed with the others
        spectrum.setSelected(true);
        spectrum.setFormat("spectrum/spectrum");
        
        SpectrumSet sv = new SpectrumSet();
        sv.addSpectrum(0, spectrum);
        
        //add to the previous spectrumSet a new Spectrum trasformed
        aiospectooldetached.spectrumSet.addSpectrumSet(sv);
        
        //if(aiospectooldetached.remoteSpectrumSet == null) aiospectooldetached.remoteSpectrumSet = new SpectrumSet();
        //aiospectooldetached.remoteSpectrumSet.addSpectrumSet(sv);
        
        spectrum.setRow(aiospectooldetached.spectrumSet.getSpectrumSet().size() - 1);
        
        
    }
    
    
    
}
