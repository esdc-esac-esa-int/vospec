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


public class TsapBestFit extends Fit {

	private double		norm;
	
	private Vector	 	initialParam;
	private Vector 		bestParam;
	
	private String 		urlString;
    private Spectrum spectrum;
	

	
	public TsapBestFit() {
		
		super();
		
		this.initialParam 	= new Vector();
		this.urlString		= "";
		this.norm		= 1.;
	
	}
	
	public TsapBestFit(Spectrum spectrum, String urlString, Vector initialParam) {
		
		this();
		
		this.spectrum 		= spectrum;
		this.urlString 		= urlString;
		this.initialParam 	= initialParam;
			
	}
	
	public void solve() {
		//LevenbergMarquardt lm = new LevenbergMarquardt(spectrum, urlString, initialParam);
		//lm.evalBestParam();
		//bestParam = lm.getBestParam();
		//norm = lm.getNorm();
	}
	
	/*public void solveParamToTry() {
		//LM lm = new LM(spectrum, urlString, initialParam);
		lm.evalParamToTry();	
		paramToTry = lm.getParamToTry();
		norm = lm.getNorm();
		bestFit = lm.getBestFit();
		//lambda = lm.getLambda();
		//iter = lm.getIter();
		//lm = new LM(spectrum, urlString, paramToTry, lambda, iter);
	
	}*/	
	
	public Vector getBestParameters() {
		return bestParam;
	}
	
//	public Vector getParamToTry() {
//		return initialParam;
//	}


	public double getNorm() {
		return norm;
	}
	
//	public boolean getBestFit() {
//		return bestFit;
//	}

}
