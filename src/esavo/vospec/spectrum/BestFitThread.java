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

import esavo.fit.BestFitEventListener;
import esavo.fit.LevenbergMarquardt;
import esavo.vospec.dataingestion.*;
import esavo.vospec.main.*;
import java.awt.Cursor;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JTextArea;

//Note for the future: extending Thread is considered poor design choice, better implemetn Runnable class
public class BestFitThread extends Thread implements BestFitEventListener {

    private String method = "";
    private VOSpecDetached aioSpecToolDetached;
    private FittingWindow fittingWindow;
    private LevenbergMarquardt lm;
    //private CheckNode                   checkNode;
    private String nodeName;
    private SpectrumSet sv;
    private boolean isTSA;
    private boolean createNodes;
    private Vector nodesVector;
    private Spectrum spectrum;
    private SsaServer finalssaServer;
    private Vector paramOptions;
    private TsaServerParser tsp;
    private SsaServer ssaServer;
    private double norm;

    public BestFitThread(FittingWindow fw, String method, VOSpecDetached aioSpecToolDetached) {

        this.fittingWindow = fw;
        this.aioSpecToolDetached = aioSpecToolDetached;
        this.method = method;

    }
    //Runnable bestFitThreads = new BestFitThread("",this,spectrum,finalssaServer,paramOptions,tsp,ssaServer);

    public BestFitThread(FittingWindow fw, String method, VOSpecDetached aioSpecToolDetached, Spectrum spectrum, SsaServer finalssaServer, Vector paramOptions, TsaServerParser tsp, SsaServer ssaServer) {

        this.fittingWindow = fw;
        this.aioSpecToolDetached = aioSpecToolDetached;
        this.method = method;
        this.spectrum = spectrum;
        this.finalssaServer = finalssaServer;
        this.paramOptions = paramOptions;
        this.tsp = tsp;
        this.ssaServer = ssaServer;
    }
    /*
    public BestFitThread(FittingWindow fw, String method, AioSpecToolDetached aioSpecToolDetached, CheckNode checkNode) {

    this(fw, method,aioSpecToolDetached);

    //this.checkNode 			= checkNode;
    }*/

    public BestFitThread(FittingWindow fw, String method, VOSpecDetached aioSpecToolDetached, String nodeName, SpectrumSet sv, boolean isTSA, boolean createNodes) {

        this(fw, method, aioSpecToolDetached);

        this.nodeName = nodeName;
        this.sv = sv;
        this.isTSA = isTSA;
        this.createNodes = createNodes;
    }

    public BestFitThread(FittingWindow fw, String method, VOSpecDetached aioSpecToolDetached, String nodeName, Vector nodesVector) {

        this(fw, method, aioSpecToolDetached);

        this.nodeName = nodeName;
        this.nodesVector = nodesVector;
    }

    public void run() {

        fittingWindow.tsapButton.setEnabled(false);
        Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
        fittingWindow.setCursor(hourglassCursor);
        fittingWindow.jProgressBar1.setIndeterminate(true);
        fittingWindow.jProgressBar1.setStringPainted(true);
        fittingWindow.jProgressBar1.setString("Fitting in progress...");
        fittingWindow.cancelButton.setEnabled(true);
        fittingWindow.jTextArea1 = new JTextArea();
        fittingWindow.tsapScrollPane.setViewportView(fittingWindow.jTextArea1);
        fittingWindow.jTextArea1.setEnabled(true);
        fittingWindow.jTextArea1.repaint();

        try {
            lm = new LevenbergMarquardt(spectrum, finalssaServer.getSsaUrl(), paramOptions, this);
            ///////////////////
            lm.evalBestParam();
            ///////////////////
            paramOptions = lm.getBestParam();
            norm = lm.getNorm();

            //Fitting process finished successfully

            Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
            fittingWindow.setCursor(normalCursor);

            fittingWindow.jProgressBar1.setIndeterminate(false);
            fittingWindow.jProgressBar1.setString("final " + fittingWindow.jProgressBar1.getString());
            fittingWindow.cancelButton.setEnabled(false);
            fittingWindow.toFront();

            BestFitConfirmDialog dialog = new BestFitConfirmDialog(this.fittingWindow, false, this);
            dialog.setVisible(true);

        } catch (Exception e) {

            //Fitting process cancelled / error produced
            e.printStackTrace();
            Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
            fittingWindow.setCursor(normalCursor);
            fittingWindow.jProgressBar1.setIndeterminate(false);
            fittingWindow.jProgressBar1.setString("Fitting cancelled");
            fittingWindow.cancelButton.setEnabled(false);
            this.newMessage(e.getLocalizedMessage());
            fittingWindow.bestFitRunning = false;
            fittingWindow.tsapButton.setEnabled(true);
        }

        fittingWindow.tsapButton.setEnabled(true);
        fittingWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fittingWindow.bestFitRunning = false;
        fittingWindow.tsapButton.setText("Restart");
        fittingWindow.restart = true;
    }

    public void stopBestFit() {
        lm.stopBestFit();
        fittingWindow.jProgressBar1.setString("STOPPING fitting...");
       
    }

    public void displayResults() throws Exception {

        tsp.getUrl(ssaServer.getSsaUrl(), paramOptions);
        finalssaServer = tsp.getTsa();

        if (finalssaServer != null) {
            String url = finalssaServer.getSsaUrl();

            SpectrumSet sv = new SpectrumSet();

            if (!finalssaServer.getLocal()) {
                sv = SSAIngestor.getSpectra(url);
            } else {
                sv = SSAIngestor.getSpectra(url);
            }

            int execution = this.aioSpecToolDetached.tsapFittingExecution;

            if (sv.getSpectrumSet().size() != 0) {

                Spectrum spectrumTmp;
                for (int i = 0; i < sv.spectrumSet.size(); i++) {
                    spectrumTmp = sv.getSpectrum(i);
                    spectrumTmp.setToBeNormalized(true);
                    spectrumTmp.setNorm(norm);
                    spectrumTmp.setTitle("fitting execution " + execution + " " + spectrumTmp.getTitle());
                    spectrumTmp.setAioSpecToolDetached(aioSpecToolDetached);
                    SpectrumSet spectrumSetTmp = new SpectrumSet();
                    spectrumSetTmp.addSpectrum(0, spectrumTmp);

                    //add to the previous spectrumSet a new Spectrum with the fitted model
                    this.aioSpecToolDetached.spectrumSet.addSpectrumSet(spectrumSetTmp);
                    //this.aioSpecToolDetached.remoteSpectrumSet.addSpectrumSet(spectrumSetTmp);

                    spectrumTmp.setRow(this.aioSpecToolDetached.spectrumSet.getSpectrumSet().size() - 1);
                    this.aioSpecToolDetached.addSpectrum("TSAP Best Fit", spectrumTmp, (javax.swing.JTextArea) null);

                }

            }

            //Add the original spectrum we are fitting to
            //add to the previous spectrumSet a new Spectrum with the fitted model
            spectrum.setName("this");
            spectrum.setTitle("fitting execution " + execution + ", fitted spectrum");
            spectrum.setUrl("fitting execution " + execution + ", fitted spectrum");
            spectrum.setAioSpecToolDetached(aioSpecToolDetached);
            this.aioSpecToolDetached.tsapFittingExecution++;

            SpectrumSet set = new SpectrumSet();
            set.addSpectrum(0, spectrum);
            this.aioSpecToolDetached.spectrumSet.addSpectrumSet(set);
            //this.aioSpecToolDetached.remoteSpectrumSet.addSpectrumSet(set);

            spectrum.setRow(this.aioSpecToolDetached.spectrumSet.getSpectrumSet().size() - 1);
            this.aioSpecToolDetached.addSpectrum("TSAP Best Fit", spectrum, (javax.swing.JTextArea) null);

        }
    }

    public synchronized void newFit(double chi, Vector param) {


        fittingWindow.jProgressBar1.setString("chi = " + String.valueOf(chi));


        fittingWindow.jTextArea1.append("chi = " + String.valueOf(chi) + " parameters:");

        for (int i = 0; i < param.size(); i++) {
            fittingWindow.jTextArea1.append(" " + (((TsaServerParam) param.get(i)).getName()) + " = " + (((TsaServerParam) param.get(i)).getSelectedValue()));
        }


        fittingWindow.jTextArea1.append(System.getProperty("line.separator"));
        fittingWindow.jTextArea1.repaint();

    }

    public synchronized void newFit(Vector param) {


        fittingWindow.tsapScrollPane.setViewportView(fittingWindow.jTextArea1);
        fittingWindow.jTextArea1.setEnabled(true);
        fittingWindow.jTextArea1.append("parameters:");

        for (int i = 0; i < param.size(); i++) {
            fittingWindow.jTextArea1.append(" " + (((TsaServerParam) param.get(i)).getName()) + " = " + (((TsaServerParam) param.get(i)).getSelectedValue()));
        }


        fittingWindow.jTextArea1.append(System.getProperty("line.separator"));
        fittingWindow.jTextArea1.repaint();

    }

    public synchronized void newMessage(String message) {

        fittingWindow.tsapScrollPane.setViewportView(fittingWindow.jTextArea1);
        fittingWindow.jTextArea1.setEnabled(true);
        fittingWindow.jTextArea1.append(message);
        fittingWindow.jTextArea1.append(System.getProperty("line.separator"));
        fittingWindow.jTextArea1.repaint();

    }
}







