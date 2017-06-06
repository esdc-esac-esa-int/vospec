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
package esavo.vospec.standalone;

import esavo.vospec.dataingestion.*;
import esavo.vospec.main.VOSpecDetached;
import esavo.vospec.spectrum.FitsSpectrum;
import esavo.vospec.spectrum.Spectrum;
import esavo.vospec.spectrum.SpectrumSet;
import esavo.vospec.spectrum.VoTableSpectrum;
import esavo.vospec.util.EnvironmentDefs;
import esavo.vospec.util.Utils;
import java.applet.AppletContext;
import java.util.Properties;

/**
 *
 * 
<pre>
 * %full_filespec: VoSpec.java,11:java:2 %
 *
 * @author Jesus Salgado
 * @version 
 */
public class VoSpec {

    public VOSpecDetached aioSpecToolDetached;
    public Properties props;
    public String SERVERHOST;
    public String SERVERPORT;
    public String RMIPORT;
    public String SERVERNAME;
    public Float declination;
    public Float rightAscension;
    public Float size;
    //public Float band;
    //public Float time;

    public VoSpec(String serverhost, String serverport, String rmiport, String servername) {
        this.SERVERHOST = serverhost;
        this.SERVERPORT = serverport;
        this.RMIPORT = rmiport;
        this.SERVERNAME = servername;

        setPropertiesAndOpenVOSpec();

    }

    public VoSpec() {

        SERVERHOST = "esavo.esa.int";
        SERVERPORT = "80";
        RMIPORT = "1099";
        SERVERNAME = "AioSpecServer";

        setPropertiesAndOpenVOSpec();

    }

    public void setPropertiesAndOpenVOSpec() {

        props = new Properties();
        props.setProperty("SERVERHOST", SERVERHOST);
        props.setProperty("SERVERPORT", SERVERPORT);
        props.setProperty("RMIPORT", RMIPORT);
        props.setProperty("SERVERNAME", SERVERNAME);

        Utils.rmiPropertiesDefinition(props);

        System.setProperty("http.agent", "VOSpec "+EnvironmentDefs.getVERSION());

       try {
            String thisTitle = "AIO Spectra Search Tool\n";
            aioSpecToolDetached = new VOSpecDetached(thisTitle, null, props);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show() {
        aioSpecToolDetached.setVisible(true);
    }

    public void hide() {
        aioSpecToolDetached.setVisible(false);
    }

    public void toFront() {
        aioSpecToolDetached.toFront();
    }

    public void reset() {
        aioSpecToolDetached.resetButton2ActionPerformed();
    }

    public void resetAll() {
        aioSpecToolDetached.setVisible(false);
        try {
            String thisTitle = "AIO Spectra Search Tool\n";
            AppletContext parentAppletContext = null;
            aioSpecToolDetached = new VOSpecDetached(thisTitle, null, props);
        } catch (Exception e) {
            e.printStackTrace();
        }
        aioSpecToolDetached.setVisible(true);
    }

    public void setRa(String ra) {
        aioSpecToolDetached.setRa(ra);
    }

    public void setDec(String dec) {
        aioSpecToolDetached.setDec(dec);
    }

    public void setSize(String size) {
        aioSpecToolDetached.setSize(size);
    }

    public synchronized void setSsaServerList(SsaServerList ssaServerList) {

        try {
            declination = new Float(aioSpecToolDetached.decValue);
        } catch (java.lang.NumberFormatException e) {
            declination = null;
            System.out.println("NumberFormatException " + e);
        }

        try {
            rightAscension = new Float(aioSpecToolDetached.raValue);
        } catch (Exception e) {
            rightAscension = null;
        }

        try {
            size = new Float(aioSpecToolDetached.sizeValue);
        } catch (Exception e) {
            size = null;
        }

        try {
            SsaServerList newSsaServerList = new SsaServerList();
            for (int i = 0; i < ssaServerList.getSsaServerList().size(); i++) {
                SsaServer ssaServer = new SsaServer(ssaServerList.getSsaServer(i));
                String url = ssaServer.getSsaUrl();
                if (!ssaServer.getLocal()) {

                    if (url.indexOf("?") < 0) {
                        url = url + "?";
                    }

                    if (rightAscension == null || declination == null) {
                        url = url;
                    } else {
                        if (size == null) {
                            url = url + "&POS=" + rightAscension + "," + declination;
                        } else {
                            url = url + "&POS=" + rightAscension + "," + declination + "&SIZE=" + size;
                        }
                    }
                    ssaServer.setSsaUrl(url);
                }
                newSsaServerList.addSsaServer(i, ssaServer);
            }

            aioSpecToolDetached.ssaServerList = newSsaServerList;
            aioSpecToolDetached.showResults();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void loadSpectrumSet(String description, SpectrumSet oldSpectrumSet) {
        
        if (aioSpecToolDetached.spectrumSet == null) {
            aioSpecToolDetached.spectrumSet = new SpectrumSet();
        }

        boolean addedInSpectrumSet = false;
        for (int j = 0; j < oldSpectrumSet.spectrumSet.size(); j++) {


            Spectrum spectrum = (Spectrum) oldSpectrumSet.getSpectrum(j);
            spectrum = Utils.setMetadata(spectrum);

            if (spectrum.getFormat() == null) {
                aioSpecToolDetached.localDataDialog.addNewDirectly(spectrum.getUrl(), spectrum.getTitle());

            } else if (spectrum.getFormat().toUpperCase().indexOf("FITS") > -1) {

                FitsSpectrum fitsSpectrum = new FitsSpectrum(spectrum);
                fitsSpectrum.setSelected(true);
                fitsSpectrum.setToWait(true);
                aioSpecToolDetached.localDataDialog.addSpectrum(fitsSpectrum);
                addedInSpectrumSet = true;


            } else if (spectrum.getFormat().toUpperCase().indexOf("VOTABLE") > -1) {

                VoTableSpectrum voTableSpectrum = new VoTableSpectrum(spectrum);
                voTableSpectrum.setSelected(true);
                voTableSpectrum.setToWait(true);
                aioSpecToolDetached.localDataDialog.addSpectrum(voTableSpectrum);
                addedInSpectrumSet = true;

            } else {
                spectrum.setSelected(true);
                spectrum.setToWait(true);
                aioSpecToolDetached.localDataDialog.addSpectrum(spectrum);
                addedInSpectrumSet = true;

            }



        }
        if (addedInSpectrumSet) {
            aioSpecToolDetached.addLocalData();
        }

    }
}
