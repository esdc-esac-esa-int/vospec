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

import esavo.vospec.plot.ExtendedPlot;
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author  ibarbarisi
 */
public class SpectraViewer extends JPanel {

    public ExtendedPlot plot;
    public double fluxFactor;
    public double waveFactor;
    public boolean connected;
    public Color foreground = null;
    public Color background = null;
    private boolean waveErrors = true,  fluxErrors = true;
    public boolean waveToVel = false;

    public SpectraViewer() {
        super.setBackground(new Color(235, 235, 235));
    }

    public SpectraViewer(ExtendedPlot plot, JPanel displayPanel) {
        this();
        this.plot = plot;
        if (background != null) {
            plot.setBackground(getBg());
        } else {
            background = new Color(235, 235, 235);
        }
        if (foreground != null) {
            plot.setForeground(getFg());
        } else {
            foreground = new Color(102, 102, 102);
        }

        if (!this.plot.getTitle().equals("VOSpec Spectral Analysis Tool")) {
            this.plot.setTitle("VOSpec Spectral Analysis Tool");
        }

        plot.setDisplayPanel(displayPanel);

        if (displayPanel.getWidth() > 0 && displayPanel.getHeight() > 0) {
            plot.setSize(displayPanel.getWidth() - 20, displayPanel.getHeight() - 30);
        }
    }

    public SpectraViewer(ExtendedPlot plot) {
        this();
        this.plot = plot;
        this.background = new Color(235, 235, 235);
        this.foreground = new Color(102, 102, 102);
        Dimension d = new Dimension(570, 390);

        if (!this.plot.getBackground().equals(getBg())) {
            this.plot.setBackground(getBg());
        }
        if (!this.plot.getForeground().equals(getFg())) {
            this.plot.setForeground(getFg());
        }

        if (!this.plot.getTitle().equals("VOSpec Spectral Analysis Tool")) {
            this.plot.setTitle("VOSpec Spectral Analysis Tool");
        }

        if (this.plot.getSize().height < 0 || this.plot.getSize().width < 0) {
            this.plot.setSize(d);
        }

    }

    public SpectraViewer(ExtendedPlot plot, Color background, Color foreground, String title, String xAxis, String yAxis) {
        this.plot = plot;
        this.foreground = foreground;
        this.background = background;
        super.setBackground(new Color(235, 235, 235));
        if (background != null) {
            plot.setBackground(getBg());
        } else {
            background = new Color(235, 235, 235);
        }

        if (!this.plot.getTitle().equals("VOSpec Spectral Analysis Tool")) {
            this.plot.setTitle("VOSpec Spectral Analysis Tool");
        }

        plot.setForeground(foreground);
        plot.setTitle(title);
        plot.setXLabel(xAxis);
        plot.setYLabel(yAxis);
    }

    public SpectraViewer(ExtendedPlot plot, boolean w, boolean f, JPanel displayPanel) {
        this(plot);
        //setMarksStyle("Points");
        setPlot(false, w, f);
    }

    public void setWaveToVelSelected(boolean selected) {
        this.waveToVel = selected;
    }

    public void setMarksStyle(String m) {
        plot.setMarksStyle(m);
    }

    public void setPlot(boolean con, boolean w, boolean f) {
        plot.setConnected(con);
        plot.setXLog(w);
        plot.setYLog(f);
    }

    public void setMarksStyle(String marks, boolean realData, int dataset, boolean isTsap) {
        plot.setMarks(marks, realData, dataset, isTsap);
    }

    public void setErrors(boolean waveErrors, boolean fluxErrors) {

        this.waveErrors = waveErrors;
        this.fluxErrors = fluxErrors;

    }


    /*
     * Drawing an spectrum that does not have errors
     * */
    public void drawSpectrumConverted(int ct, double wavd, double fluxd, boolean c, boolean realData, boolean isTsap) {

        plot.addPoint(ct, wavd, fluxd, c, realData, isTsap);

    }

    /*
     * Drawing an spectrum that has errors in the FLUX axis
     * */
    public void drawSpectrumConvertedErrorFlux(int ct, double wavd, double fluxd,
            double fluxErrorLower, double fluxErrorUpper,
            boolean c, boolean realData, boolean isTsap) {

        if (fluxErrors) {
            plot.addPointWithFluxErrorBars(ct, wavd, fluxd, fluxd - fluxErrorLower, fluxd + fluxErrorUpper, c, realData, isTsap);

        } else {

            plot.addPoint(ct, wavd, fluxd, c, realData, isTsap);

        }

    }

    /*
     * Drawing an spectrum that has errors in the WAVE axis
     * */
    public void drawSpectrumConvertedErrorWave(int ct, double wavd, double fluxd,
            double waveErrorLower, double waveErrorUpper,
            boolean c, boolean realData, boolean isTsap) {

        if (waveErrors) {

            plot.addPointWithWaveErrorBars(ct, wavd, fluxd, wavd - waveErrorLower, wavd + waveErrorUpper, c, realData, isTsap);

        } else {

            plot.addPoint(ct, wavd, fluxd, c, realData, isTsap);

        }

    }


    /*
     * Drawing an spectrum that has errors in BOTH axis
     * */
    public void drawSpectrumConvertedErrorDouble(int ct, double wavd, double fluxd,
            double wavErrorLower, double wavErrorUpper, double fluxErrorLower, double fluxErrorUpper,
            boolean c, boolean realData, boolean isTsap) {

        if (waveErrors && fluxErrors) {

            plot.addPointWithDoubleErrorBars(ct, wavd, fluxd, wavd - wavErrorLower, wavd + wavErrorUpper, fluxd - fluxErrorLower, fluxd + fluxErrorUpper, c, realData, isTsap);

        } else if (waveErrors) {

            plot.addPointWithWaveErrorBars(ct, wavd, fluxd, wavd - wavErrorLower, wavd + wavErrorUpper, c, realData, isTsap);

        } else if (fluxErrors) {

            plot.addPointWithFluxErrorBars(ct, wavd, fluxd, fluxd - fluxErrorLower, fluxd + fluxErrorUpper, c, realData, isTsap);
        } else {

            plot.addPoint(ct, wavd, fluxd, c, realData, isTsap);

        }

    }

    public void updateViewer(String waveUnit, String fluxUnit) {
        String xLog;
        String yLog;
        if (plot.getXLog()) {
            xLog = "logarithmic";
        } else {
            xLog = "linear";
        }
        if (plot.getYLog()) {
            yLog = "logarithmic";
        } else {
            yLog = "linear";
        }
        if (!waveToVel) {
            plot.setXLabel("Wavelength " + "(" + waveUnit + ";" + xLog + ")");
        }
        if (waveToVel) {
            plot.setXLabel("Velocity " + "(Km/s)");
        }

        plot.setYLabel("Flux " + "(" + fluxUnit + ";" + yLog + ")");
        plot.fillPlot();
        add(plot);
    }

//     public void drawSpectrumNormalized(int ct,double wavd,double fluxd, boolean c,boolean realData,boolean isTsap){          
//         plot.addPoint(ct,wavd,fluxd,c,realData,isTsap);                   
//     }
    public void setBackgroundAndForeground(Color bg, Color fg) {
        this.foreground = fg;
        this.background = bg;

    }

    public Color getBg() {
        return background;
    }

    public Color getFg() {
        return foreground;
    }
}
