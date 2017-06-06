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
import esavo.vospec.spectrum.SedSpectrum;
import esavo.vospec.spectrum.Spectrum;
import java.io.File;
import java.util.Random;
import org.astrogrid.samp.Message;

/**
 *
 * @author jgonzale
 */
public class SingleTableSenderListener extends NotificationSenderListener {

    private Spectrum spectrum;

    public SingleTableSenderListener(String messageType, Interop interopAccessor, Spectrum spectrum) {
        super(messageType, interopAccessor);
        this.spectrum = spectrum;
    }

    protected Message buildMessage() {

        //Gather data
        Random r = new Random();
        String filepath = System.getProperty("java.io.tmpdir") + File.separator + "spectrum" + r.nextInt() + ".votable";
        File file = new File(filepath);

        //Populate the spectrum if it is not already downloaded
        spectrum.run();

        //Serialize spectrum to local VOTable
        SedSpectrum sedSpectrum = new SedSpectrum(spectrum);
        sedSpectrum.serializeToVOTable(filepath);

        //Build message for specific MType table.load.votable
        Message message = new Message(this.getMType());

        message.addParam("url", "file:/" + "/localhost/" + file);
        message.addParam("table-id", "file:/" + "/localhost/" + file);

        System.out.println("file:/" + "/localhost/" + file);
        System.out.println("Message " + message.toString());

        return message;
    }

}
