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


import esavo.vospec.dataingestion.*;
import esavo.vospec.spectrum.Spectrum;
import esavo.vospec.spectrum.SpectrumSet;
import esavo.vospec.util.Cache;
import java.util.*;


public class TsapCallRead {

	private String 	urlString;
	private Vector param;
    private LevenbergMarquardt levenberg;
	
	public TsapCallRead(String urlString, Vector param, LevenbergMarquardt levenberg) {
		this.urlString = urlString;
		this.param = param;
        this.levenberg = levenberg;
	}
	
    /**
     *Returns the theoretical model related to the input param
     *from the TSA server defined by the input url
     *
     */
    public Spectrum tsapSpectrum() throws Exception {

        SpectrumSet st = null;
        Spectrum tsp = null;

        if (levenberg.getStop()) {
            throw new Exception("Stop operations");
        }

        for (int i = 0; i < param.size(); i++) {
            TsaServerParam tsaServerParam;

            tsaServerParam = (TsaServerParam) param.elementAt(i);
            urlString = urlString + "&" + tsaServerParam.getName() + "=" + tsaServerParam.getSelectedValue();
        }


        try {
            //System.out.println("URLSTRING"+urlString);
            st = SSAIngestor.getSpectra(urlString);

            tsp = st.getSpectrum(0);
            
            //System.out.println("URLSTRING2"+tsp.getUrl());

        } catch (Exception e) {
        }



        Cache.putCacheInMemory();
        if (tsp != null) {
            tsp.setRow(-1);

            new Thread(tsp).start();
        }
        try {
            while (tsp.getToWait()) {
                //System.out.println("waiting");
                Thread.sleep(10);
            }
        } catch (Exception e) {
        }
        //System.out.println("FINISHED");



        return tsp;

    }
}
