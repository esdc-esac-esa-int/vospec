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
package esavo.vospec.main;

import esavo.vospec.spectrum.*;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 *
 *
 * <pre>
 * %full_filespec: LaunchThreads.java,8:java:1 %
 * %derived_by: jsalgado %
 * %date_created: Fri Jun 30 11:40:11 2006 %
 *
 * </pre>
 *
 * @author
 * @version %version: 8 %
 */
public class LaunchThreads extends Thread {

    private VOSpecDetached aioSpecToolDetached;
    HashSet toBeDownloaded = new HashSet();
    boolean onlyHashSet = false;
    boolean stop = false;

    public LaunchThreads(VOSpecDetached aioSpecToolDetached) {
        this.aioSpecToolDetached = aioSpecToolDetached;
    }

    public LaunchThreads(VOSpecDetached aioSpecToolDetached, HashSet spectrumHashSet) {
        this.aioSpecToolDetached = aioSpecToolDetached;
        toBeDownloaded = spectrumHashSet;
        onlyHashSet = true;
    }

    public void finish() {
        stop = true;
    }

    public boolean finishing() {
        return stop;
    }

    public void run() {

        Spectrum spectrum = null;
        try {

            if (!onlyHashSet) {
                toBeDownloaded = new HashSet();

                // the ones already downloaded are launched asyn
                for (int ct = 0; ct < this.aioSpecToolDetached.spectrumSet.getSpectrumSet().size(); ct++) {

                    spectrum = this.aioSpecToolDetached.spectrumSet.getSpectrum(ct);
                    spectrum.setAioSpecToolDetached(this.aioSpecToolDetached);
                    //Utils.setAioSpecToolDetached(this.aioSpecToolDetached);

                    boolean isSelected = spectrum.getNode().getIsSelected();


                    if (isSelected) {
                        //System.out.println("Spectrum "+spectrum.getTitle()+"gettowait"+spectrum.getToWait());
                        if (true/*spectrum.getToWait()*/) {
                            toBeDownloaded.add(spectrum);
                        } else {
                            //if (this.aioSpecToolDetached.utils.addRunningSpectrum(spectrum)) {
                            //System.out.println("Running asyn "+spectrum.getTitle());

                            //Thread spectrumThread = new Thread(spectrum);
                            //spectrumThread.start();
                           // new Thread(spectrum).start();

                        }
                    }
                }
            }

            // the ones not-downloaded are launched sequentially
            Iterator it = toBeDownloaded.iterator();
            while (it.hasNext() && !stop) {
                spectrum = (Spectrum) it.next();
                spectrum.setAioSpecToolDetached(this.aioSpecToolDetached);
                //if (this.aioSpecToolDetached.utils.addRunningSpectrum(spectrum)) {
                //System.out.println("Running syn "+spectrum.getTitle());
                    /*new Thread(spectrum).start();
                while (spectrum.getToWait()) {
                Thread.sleep(100);
                }*/


                //this.setPriority(Thread.MIN_PRIORITY);
                try{
                    spectrum.run();
                }catch(Throwable t){
                    System.out.println("Exception downloading "+spectrum.getName());
                }



                //}

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        aioSpecToolDetached.displaySpectraColorList();
        aioSpecToolDetached.setDefaultCursor();

    }
}
