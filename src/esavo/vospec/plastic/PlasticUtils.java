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
package esavo.vospec.plastic;

import esavo.vospec.main.VOSpecDetached;
import esavo.vospec.util.Utils;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JMenuItem;

/**
 *
 * @author ibarbarisi
 */
public class PlasticUtils {

    public static void startExternalHub(boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public VOSpecDetached vospec = null;
    public List applicationsRegistered;
    public String item = ""; //used in checkApplicationRegistered()
    public Plastic plastic;
    public boolean plasticActive = false;

    /** Creates a new instance of PlasticUtils */
    public PlasticUtils() {

    }


    /** Creates a new instance of PlasticUtils */
    public PlasticUtils(VOSpecDetached vospec) {
        this.vospec= vospec;
    }


    public void registerToPlastic() {

        applicationsRegistered = new ArrayList();
        item = "";

        try {
            if (!plasticActive) {
                System.out.println("Plastic not active... registering....");
                plastic = new Plastic(vospec);
                vospec.plasticButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/hub.gif")));
                vospec.plasticButton.setToolTipText("Unregister with the Plastic Hub");
                //URI uri = plastic.registerRMI();
                vospec.interOpMenu.getItem(0).setEnabled(false);
                vospec.interOpMenu.getItem(1).setEnabled(true);
                vospec.interOpMenu.getItem(2).setEnabled(false);
                vospec.interOpMenu.getItem(3).setEnabled(false);
                vospec.interOpMenu.getItem(5).setEnabled(false);

                checkApplicationRegistered();

                plasticActive = true;

            } else {

                applicationsRegistered = null;

                vospec.plasticButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/hubBroken.gif")));
                vospec.plasticButton.setToolTipText("Register with the Plastic Hub");
                plastic.unregister();
                vospec.interOpMenu.getItem(0).setEnabled(true);
                vospec.interOpMenu.getItem(1).setEnabled(false);
                vospec.interOpMenu.getItem(2).setEnabled(true);
                vospec.interOpMenu.getItem(3).setEnabled(true);
                vospec.interOpMenu.getItem(5).setEnabled(false);

                plasticActive = false;
            }
        } catch (Exception e) {
            vospec.plasticButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/hubBroken.gif")));
            vospec.plasticButton.setToolTipText("Register with the Plastic Hub");
            plastic.unregister();
            vospec.interOpMenu.getItem(0).setEnabled(true);
            vospec.interOpMenu.getItem(1).setEnabled(false);
            vospec.interOpMenu.getItem(2).setEnabled(true);
            vospec.interOpMenu.getItem(3).setEnabled(true);
            vospec.interOpMenu.getItem(5).setEnabled(false);

            plasticActive = false;
        }
    }

    public void checkApplicationRegistered() {

        java.util.List applicationsID = new ArrayList();
        int ct1 = 0;
        int ct2 = 0;

        try {
            //List
            applicationsRegistered = plastic.getRegisteredNames(true);
            applicationsID = plastic.getRegisteredIds(true);
            System.out.println("Number applications registered " + applicationsRegistered.size());

            for (int i = 0; i < applicationsRegistered.size(); i++) {
                item = (String) applicationsRegistered.get(i);
                URI id = (URI) applicationsID.get(i);

                JMenuItem it = new JMenuItem(item);

                if (plastic.isMsgSupported(id, new URI("ivo://votech.org/spectrum/loadFromURL")) && vospec.spectrumSet != null) {
                    ct1++;
                    vospec.interOpMenu.getItem(5).setEnabled(true);
                    System.out.println("message spectrm/loadFromURL supported");

                    it.addActionListener(new java.awt.event.ActionListener() {

                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            if (vospec.spectrumSet != null) {
                                HashMap map = createMap();
                                plastic.sendSpectraToSubset(plastic.getRegisteredNames(true), map, true);
                            } else {
                                vospec.interOpMenu.getItem(5).setEnabled(false);
                                vospec.errorPlastic();

                            }
                        }
                    });
                    //if(!(vospec.interOpMenu.getItem(5).paramString()).equals(it)){
                        vospec.interOpMenu.getItem(5).add(it);
                    //}
                }

            }
            if (ct1 == 0) {
                vospec.interOpMenu.getItem(5).setEnabled(false);
            }

        } catch (Exception e) {
            //e.printStackTrace();
            //JOptionPane.showMessageDialog(this, " Problems : " +e);
        }
    }

    public HashMap createMap() {
        String waveUnit = (String) vospec.waveChoice.getSelectedItem();
        String fluxUnit = (String) vospec.fluxChoice.getSelectedItem();

        ////////////////////////////////////////////////////////
        String[] dimEqWave = Utils.getDimensionalEquation(waveUnit);
        String dq1 = dimEqWave[0];
        String dq2 = dimEqWave[1];


        String[] dimEqFlux = Utils.getDimensionalEquation(fluxUnit);
        String dq1F = dimEqFlux[0];
        String dq2F = dimEqFlux[1];

        String dimeQ = dq1 + " " + dq1F;
        String scaleQ = dq2 + " " + dq2F;
        ///////////////////////////////////////////////////////////

        String format = "application/fits";
        String waveAxes = vospec.spectrumSet.getSpectrum(0).getWaveLengthColumnName();
        String fluxAxes = vospec.spectrumSet.getSpectrum(0).getFluxColumnName();
        String url = vospec.spectrumSet.getSpectrum(0).getUrl();
        String image_title = vospec.spectrumSet.getSpectrum(0).getTitle();

        HashMap map = new HashMap();
        map.put("VOX:Spectrum_Unit", waveUnit + " " + fluxUnit);
        //map.put("VOX:fluxUnit", fluxUnit);
        //map.put("VOX:Spectrum_dimeq", dimeQ);
        //map.put("VOX:Spectrum_scaleq", scaleQ);
        map.put("VOX:Spectrum_Format", format);
        map.put("VOX:spectrum_axes", waveAxes + " " + fluxAxes);
        map.put("VOX:DATA_LINK", url);
        map.put("VOX:Image_Title", image_title);
        map.put("POS_EQ_RA_MAIN", vospec.raValue);
        map.put("POS_EQ_RA_MAIN", vospec.decValue);

        System.out.println(map.toString());
        return map;
    }

    
}
