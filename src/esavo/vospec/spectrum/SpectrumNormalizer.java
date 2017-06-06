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

/**
 *
 * @author  ibarbarisi
 */
public class SpectrumNormalizer {
    
    /** Prevent instance of SpectrumNormalizer */
    private SpectrumNormalizer() {
    }
    
    public static double distance(double value, double center) {
        return (value - center) * (value - center);
    }    
    
    public static Spectrum calculateNorm(Spectrum spectrum,String wave,String flux,double x,double y) {
           
            Unit finalUnits = new Unit(wave,flux);

            spectrum.setNorm(1.);
            double[] spectrumWaveInitial = spectrum.getWaveValues();
            double[] spectrumFluxInitial = spectrum.getFluxValues();
            
	    Spectrum spectrumFinalUnits = new Spectrum();
            try {	
		
		SpectrumConverter spectrumConverter = new SpectrumConverter();      
            	spectrumFinalUnits = spectrumConverter.convertSpectrum(spectrum,finalUnits);
	    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }	

            double[] spectrumWave = spectrumFinalUnits.getWaveValues();
            double[] spectrumFlux = spectrumFinalUnits.getFluxValues();
            int next = spectrumWave.length;
            
            //
            int numberOfElements = 2;
            if(spectrumWave.length < 5) numberOfElements = spectrumWave.length;
            int[] closerElements = new int[numberOfElements];
            
            for(int i=0; i < spectrumWave.length; i++) {
                
                // Initialize closer values
                if(i < numberOfElements) {
                    closerElements[i] = i;
                    continue;
                }    
                
                for(int k=0; k < numberOfElements; k++) {
                    if(distance(spectrumWave[i],x) < distance(spectrumWave[closerElements[k]],x)) {
                        closerElements[k] = i;
                        break;
                    }    
                }
             }
            
            double yReal = 0;
            int kIndex,iIndex;
            
            for(int i=0; i < numberOfElements; i++) {

                    double topPart      = 1.;
                    double bottomPart   = 1.;
                    iIndex = closerElements[i];
 
                    for(int k=0; k < numberOfElements; k++) {

                        kIndex = closerElements[k];
                        if(i != k) topPart    = topPart     * (x-spectrumWave[kIndex]);
                        if(i != k) bottomPart = bottomPart  * (spectrumWave[iIndex] - spectrumWave[kIndex]);
                    }

                    if(Math.abs(bottomPart) > 1.E-40) yReal = yReal + Math.abs(spectrumFlux[iIndex] * topPart / bottomPart);

            }   

            double norm = y/yReal;
            spectrum.setNorm(norm);
            spectrum.addMetaData("Norm. Factor",""+norm);

            return spectrum;
    }

}
