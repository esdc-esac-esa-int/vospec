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
package esavo.fit;


import esavo.vospec.spectrum.Spectrum;
import java.util.*;


public class FitUtils {

    /** Forbid instantiation */
    private  FitUtils() {}
    
    public Vector getTSAPBestFit(Spectrum spectrum, String urlString, Vector initialParam) {
        
	TsapBestFit tsapBestFit = new TsapBestFit(spectrum, urlString, initialParam);
	tsapBestFit.solve();
	
	Vector returnParameters = tsapBestFit.getBestParameters();
	
	//This must be modified to return also the normalization
	return returnParameters;
    }
}
