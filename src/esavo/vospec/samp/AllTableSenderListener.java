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
package esavo.vospec.samp;

import esac.archive.absi.modules.cl.samp.Interop;
import esac.archive.absi.modules.cl.samp.NotificationSenderListener;
import esavo.vospec.main.VOSpecDetached;
import esavo.vospec.spectrum.SedSpectrum;
import esavo.vospec.spectrum.Spectrum;
import java.io.*;
import java.util.Random;
import java.util.Vector;
import org.astrogrid.samp.Message;
/**
 *
 * @author ibarbarisi
 */
public class AllTableSenderListener extends NotificationSenderListener{
    
    private VOSpecDetached aiospectooldetached;

	public AllTableSenderListener(String messageType,Interop interopAccessor, VOSpecDetached aiospectooldetached) {
		super(messageType,interopAccessor);
        this.aiospectooldetached = aiospectooldetached;
	}

     protected Message buildMessage() {

        Random r = new Random();
        String filepath = System.getProperty("java.io.tmpdir") + File.separator + "spectrum"+r.nextInt()+".votable";
        File file = new File(filepath);
        String wave = (String) (aiospectooldetached).waveChoice.getSelectedItem();
        String flux = (String) (aiospectooldetached).fluxChoice.getSelectedItem();
        Vector dataValues = aiospectooldetached.plot.getPointsLinear();

        Spectrum spectrum = new Spectrum();
        spectrum.setUnitsW(wave);
        spectrum.setUnitsF(flux);

        double[] waveValues = new double[dataValues.size()];
        double[] fluxValues = new double[dataValues.size()];

        for(int i=0;i<dataValues.size();i++){
            waveValues[i] = ((double[])dataValues.get(i))[0];
        }
        
        for(int i=0;i<dataValues.size();i++){
            fluxValues[i] = ((double[])dataValues.get(i))[1];
        }
        
        spectrum.setWaveValues(waveValues);
        spectrum.setFluxValues(fluxValues);

        SedSpectrum sedSpectrum = new SedSpectrum(spectrum);
        sedSpectrum.serializeToVOTable(filepath);

        //Build message for specific MType table.load.votable
        Message message = new Message(this.getMType());

        message.addParam("url", "file:/" + "/localhost/" + file);
        message.addParam("name", "file:/" + "/localhost/" + file);
        message.addParam("table-id", "file:/" + "/localhost/" + file);

        System.out.println("file:/" + "/localhost/" + file);
        System.out.println("Message " + message.toString());

        return message;
	}



}
