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
package esavo.vospec.plot;

import esavo.vospec.main.*;
import esavo.vospec.slap.*;
import esavo.vospec.util.LuminosityViewer;
import esavo.vospec.util.SplineRequest;
import esavo.vospec.util.Utils;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class ExtendedPlot extends ErrorsPlot {

    public VOSpecDetached AIOSPECTOOLDETACHED = null;
    public LuminosityViewer SPLINEVIEWER = null;
    public SlapRegion slapRegion = new SlapRegion();
    public SlapRequest slapRequest = null;
    public SplineRequest splineRequest = null;
    public SplineRegion splineRegion = null;
    public GraphicalLine cursorLine = new GraphicalLine(false);
    public boolean isNorm = false;
    public boolean isSlap = false;
    public boolean isCross = false;
    public boolean isSpline = false;
    public boolean isDragged = false;
    public JPanel displayPanel;
    public Color[] colors = {
        new Color(0xff0000), // red
        new Color(0x0000ff), // blue
        new Color(0x00aaaa), // cyan-ish
        new Color(0x000000), // black
        new Color(0xffa500), // orange
        new Color(0x53868b), // cadetblue4
        new Color(0xff7f50), // coral
        new Color(0x45ab1f), // dark green-ish
        new Color(0x90422d), // sienna-ish
        new Color(0xa0a0a0), // grey-ish
        new Color(0x14ff14), // green-ish
    };
    JLabel jLabel;
    Hashtable dataSet;
    int nextDataSet;

    /** Creates a new instance of ExtendedPlot */
    public ExtendedPlot() {
        super();

        this.setBackground(new Color(235, 235, 235));

        MouseMotionListener[] mouseMotionListenerArray = this.getMouseMotionListeners();
        MouseListener[] mouseListenerArray = this.getMouseListeners();

        for (int i = 0; i < mouseMotionListenerArray.length; i++) {
            removeMouseMotionListener(mouseMotionListenerArray[i]);
        }
        for (int i = 0; i < mouseListenerArray.length; i++) {
            removeMouseListener(mouseListenerArray[i]);
        }


        addMouseMotionListener(new ExtendedDragListener());
        addMouseListener(new ExtendedZoomListener());

        jLabel = new JLabel();
        dataSet = new Hashtable();
        nextDataSet = 0;
    }

    public ExtendedPlot(JLabel jLabel, VOSpecDetached vospec) {
        this();
        this.jLabel = jLabel;
        this.AIOSPECTOOLDETACHED = vospec;
    }

    public void clearPoints() {

        jLabel.setText("");
        dataSet = new Hashtable();
        nextDataSet = 0;

        clear(true);
        repaint();
    }

    public void setDisplayPanel(JPanel displayPanel) {
        this.displayPanel = displayPanel;
    }

    public synchronized Color getDataSetColor(int externalDataSet, boolean realData, boolean isTsap) {

        Color dataSetColor = Color.white;
        try {

            Integer integer = new Integer(externalDataSet);
            PlotDataSet plotDataSet = (PlotDataSet) dataSet.get(integer);

            if (plotDataSet == null) {
                addDataSet(externalDataSet, realData, isTsap);
                dataSetColor = getDataSetColor(externalDataSet, realData, isTsap);
            } else {
                dataSetColor = plotDataSet.getDrawColor();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataSetColor;
    }

    // Get all the point in a certain wavelength range. The result is
    // a Vector of double[2], setting the wavelength in double[0] and
    // the Flux in double[1]
    public Vector getPoints(double minX, double maxX, double minY, double maxY) {

        Vector pointsResult = new Vector();

        // For all the datasets
        for (int dataset = 0; dataset < _points.size(); dataset++) {

            boolean isRealData = (searchPlotDataSet(dataset)).getRealData();

            if (!isRealData) {
                continue;
            }
            // These are the points for this dataset
            Vector points = (Vector) _points.elementAt(dataset);

            for (int index = 0; index < points.size(); index++) {
                ErrorsPlotPoint pt = (ErrorsPlotPoint) points.elementAt(index);

                double xValue = pt.x;
                double yValue = pt.y;
                double xHighValue = pt.xHighEB;
                double xLowValue = pt.xLowEB;
                double yHighValue = pt.yHighEB;
                double yLowValue = pt.yLowEB;

                if (xValue >= minX && xValue <= maxX && yValue >= minY && yValue <= maxY) {

                    double[] point = new double[6];

                    point[0] = xValue;
                    point[1] = yValue;

                    if (pt.xErrorBar) {
                        point[2] = xHighValue;
                        point[3] = xLowValue;
                    } else {
                        point[2] = xValue;
                        point[3] = yValue;
                    }

                    if (pt.yErrorBar) {
                        point[4] = yHighValue;
                        point[5] = yLowValue;
                    } else {
                        point[4] = yValue;
                        point[5] = yValue;
                    }


                    pointsResult.add(point);
                }
            }
        }

        return pointsResult;
    }

    public boolean getXLog() {
        return _xlog;
    }

    public boolean getYLog() {
        return _ylog;
    }

    // Get all the point in a certain wavelength range. The result is
    // two Vectors of double[2], setting the wavelength in double[0] and
    // the Flux in double[1]
    public Hashtable getPointsForSpline(double minX, double maxX, double minY, double maxY) {

        Hashtable pointsVec = new Hashtable();

        Vector realSpectrum = new Vector();
        Vector theoreticalSpectrum = new Vector();

        // For all the datasets
        for (int dataset = 0; dataset < _points.size(); dataset++) {

            boolean isRealData = (searchPlotDataSet(dataset)).getRealData();
            boolean isTsap = (searchPlotDataSet(dataset)).getTsap();

            Vector points = (Vector) _points.elementAt(dataset);

            if (!isRealData) {
                continue;
            }

            for (int index = 0; index < points.size(); index++) {

                ErrorsPlotPoint pt = (ErrorsPlotPoint) points.elementAt(index);

                double xValue = pt.x;
                double yValue = pt.y;

                if (_xlog) {
                    xValue = Math.pow(10., xValue);
                }
                if (_ylog) {
                    yValue = Math.pow(10., yValue);
                }

                if (xValue >= minX && xValue <= maxX && yValue >= minY && yValue <= maxY) {

                    double[] point = new double[2];

                    point[0] = xValue;
                    point[1] = yValue;

                    if (isTsap) {
                        theoreticalSpectrum.add(point);
                    }
                    if (!isTsap) {
                        realSpectrum.add(point);
                    }
                }
            }
        }
        pointsVec.put("0", realSpectrum);
        pointsVec.put("1", theoreticalSpectrum);

        return pointsVec;
    }

    // This returns all the points contained in the current display of the Plot,
    // i.e., if there is Zoom, only the points displayed
    public Vector getPoints() {
        return getPoints(_xMin, _xMax, _yMin, _yMax);
    }

    // This returns all the points contained in a certain wavelenght,
    // i.e., if there is Zoom, only the points displayed
    public Hashtable getPointsForSpline(double xMin, double xMax) {

        double yValueMin = _yMin;
        double yValueMax = _yMax;

        if (_ylog) {
            yValueMin = Math.pow(10., yValueMin);
            yValueMax = Math.pow(10., yValueMax);
        }

        return getPointsForSpline(xMin, xMax, yValueMin, yValueMax);
    }

    public Vector getPointsRealOrTSAP(boolean isTSAP) {

        Vector returnVector = new Vector();
        Hashtable dataTable = getPointsForSpline(getXReal(_xMin), getXReal(_xMax), getYReal(_yMin), getYReal(_yMax));

        if (!isTSAP) {
            returnVector = (Vector) dataTable.get("0");
        }
        if (isTSAP) {
            returnVector = (Vector) dataTable.get("1");
        }

        return returnVector;
    }

    public Vector getPointsLinear() {

        Vector dataVector = getPoints();

        //if((!_xlog) && (!_ylog)) return dataVector;

        Vector newDataVector = new Vector();
        for (int point = 0; point < dataVector.size(); point++) {

            double[] pointArray = (double[]) dataVector.elementAt(point);

            pointArray[0] = getXReal(pointArray[0]);
            pointArray[1] = getYReal(pointArray[1]);
            pointArray[2] = getXReal(pointArray[2]);
            pointArray[3] = getXReal(pointArray[3]);
            pointArray[4] = getYReal(pointArray[4]);
            pointArray[5] = getYReal(pointArray[5]);

            //Convert error points into error values

            pointArray[2] = pointArray[2] - pointArray[0];
            pointArray[3] = pointArray[0] - pointArray[3];
            pointArray[4] = pointArray[4] - pointArray[1];
            pointArray[5] = pointArray[1] - pointArray[5];



            newDataVector.add(pointArray);
        }


        return newDataVector;
    }

    public double getXReal(double xValue) {

        if (_xlog) {
            xValue = Math.pow(10., xValue);
        }

        return xValue;
    }

    public double getYReal(double yValue) {

        if (_ylog) {
            yValue = Math.pow(10., yValue);
        }

        return yValue;
    }

    public double getXMax() {
        return getXReal(_xMax);
    }

    public double getXMin() {
        return getXReal(_xMin);
    }

    public double getYMax() {
        return getYReal(_yMax);
    }

    public double getYMin() {
        return getYReal(_yMin);
    }

   

    public void goTo(double waveMin, double fluxMin, double waveMax, double fluxMax){
        
        if(_xlog){
            waveMin = Math.log10(waveMin);
        }
        
        if(_xlog){
            waveMax = Math.log10(waveMax);
        }
 
        if(_ylog){
            fluxMin = Math.log10(fluxMin);
        }
        
        if(_ylog){
            fluxMax = Math.log10(fluxMax);
        }
        
        super.zoom(waveMin,fluxMin,waveMax,fluxMax);
        
        
    }
     


    public void addDataSet(int dataset, boolean realData, boolean isTsap) {

        //To Be Fixed. This should not be a direct relation (it should be selectable). In this
        // case, the "getFirstColorFree" method should be implemented
        int colorInt = nextDataSet % colors.length;
        Color color = colors[colorInt];

        PlotDataSet plotDataSet = new PlotDataSet(nextDataSet, color, realData, isTsap);
        dataSet.put(new Integer(dataset), plotDataSet);

        nextDataSet++;
    }

    public void addPoint(int dataset, double x, double y, boolean connected, boolean realData, boolean isTsap) {

        if (getDataSetColor(dataset, realData, isTsap) == Color.white) {
            addDataSet(dataset, realData, isTsap);
        }

        int plotDataSet = ((PlotDataSet) dataSet.get(new Integer(dataset))).getPlotDataSet();
        addPoint(plotDataSet, x, y, connected);

    }

    public void addPointWithFluxErrorBars(int dataset, double x, double y,
            double yLowEB, double yHighEB,
            boolean connected, boolean realData, boolean isTsap) {

        if (getDataSetColor(dataset, realData, isTsap) == Color.white) {
            addDataSet(dataset, realData, isTsap);
        }

        int plotDataSet = ((PlotDataSet) dataSet.get(new Integer(dataset))).getPlotDataSet();
        addPointWithFluxErrorBars(plotDataSet, x, y, yLowEB, yHighEB, connected);

    }

    public synchronized void addPointWithFluxErrorBars(int dataset, double x, double y,
            double yLowEB, double yHighEB,
            boolean connected) {

        if (_xlog && x <= 0.) {
            return;
        }
        if (_ylog && y <= 0.) {
            return;
        }

        super.addPointWithFluxErrorBars(dataset, x, y, yLowEB, yHighEB, connected);
        return;
    }

    public void addPointWithWaveErrorBars(int dataset, double x, double y,
            double xLowEB, double xHighEB,
            boolean connected, boolean realData, boolean isTsap) {

        if (getDataSetColor(dataset, realData, isTsap) == Color.white) {
            addDataSet(dataset, realData, isTsap);
        }

        int plotDataSet = ((PlotDataSet) dataSet.get(new Integer(dataset))).getPlotDataSet();
        addPointWithWaveErrorBars(plotDataSet, x, y, xLowEB, xHighEB, connected);

    }

    public synchronized void addPointWithWaveErrorBars(final int dataset, final double x, final double y,
            final double xLowEB, final double xHighEB,
            final boolean connected) {

        if (_xlog && x <= 0.) {
            return;
        }
        if (_ylog && y <= 0.) {
            return;
        }

        super.addPointWithWaveErrorBars(dataset, x, y, xLowEB, xHighEB, connected);
        return;
    }

    public void addPointWithDoubleErrorBars(int dataset, double x, double y,
            double xLowEB, double xHighEB, double yLowEB, double yHighEB,
            boolean connected, boolean realData, boolean isTsap) {

        if (getDataSetColor(dataset, realData, isTsap) == Color.white) {
            addDataSet(dataset, realData, isTsap);
        }

        int plotDataSet = ((PlotDataSet) dataSet.get(new Integer(dataset))).getPlotDataSet();
        addPointWithDoubleErrorBars(plotDataSet, x, y, xLowEB, xHighEB, yLowEB, yHighEB, connected);

    }

    public synchronized void addPointWithDoubleErrorBars(final int dataset,
            final double x, final double y,
            final double xLowEB, final double xHighEB,
            final double yLowEB, final double yHighEB,
            final boolean connected) {

        if (_xlog && x <= 0.) {
            return;
        }
        if (_ylog && y <= 0.) {
            return;
        }

        super.addPointWithDoubleErrorBars(dataset, x, y, yLowEB, yHighEB, xLowEB, xHighEB, connected);
        return;
    }

    public synchronized void addPoint(final int dataset, final double x,
            final double y, final boolean connected) {

        if (_xlog && x <= 0.) {
            return;
        }
        if (_ylog && y <= 0.) {
            return;
        }

        super.addPoint(dataset, x, y, connected);
        return;
    }

    public void setMarks(String marks, boolean realData, int dataset, boolean isTsap) {

        if (getDataSetColor(dataset, realData, isTsap) == Color.white) {
            addDataSet(dataset, realData, isTsap);
        }

        int plotDataSet = ((PlotDataSet) dataSet.get(new Integer(dataset))).getPlotDataSet();
        if (marks.indexOf("Lines") > -1) {
            setMarksStyle("none", plotDataSet);
            setConnected(true, plotDataSet);
        } else {
            setMarksStyle(marks, plotDataSet);
            setConnected(false, plotDataSet);
        }
    }

    public class ExtendedDragListener extends DragListener {

        public void mouseDragged(MouseEvent event) {

            if (isSlap) {

                mouseMoved(event);
                int xImage = event.getX();

                if (xImage > _lrx || xImage < _ulx) {
                    return;
                }

                double x = _xMin + (xImage - _ulx) / _xscale;

                double pixelXSize = Math.abs((_xMax - _xMin) / (_ulx - _lrx));

                if (_xlog) {
                    pixelXSize = Math.abs(Math.pow(10., x) - Math.pow(10., x - 1. / _xscale));
                    x = Math.pow(10., x);
                }

                slapRequest.setWMaxOnImage(xImage);
                slapRequest.setW2(x);

                slapRegion.refresh();

            } else if (isSpline) {

                mouseMoved(event);
                int xImage = event.getX();

                if (xImage > _lrx || xImage < _ulx) {
                    return;
                }

                double x = _xMin + (xImage - _ulx) / _xscale;

                double pixelXSize = Math.abs((_xMax - _xMin) / (_ulx - _lrx));

                if (_xlog) {
                    pixelXSize = Math.abs(Math.pow(10., x) - Math.pow(10., x - 1. / _xscale));
                    x = Math.pow(10., x);
                }

                splineRequest.setWMaxOnImage(xImage);
                splineRequest.setW2(x);

                splineRegion.refresh();

            } else {
                super.mouseDragged(event);
            }
        }

        public void mouseMoved(MouseEvent event) {

            int xImage = event.getX();
            int yImage = event.getY();

            if (yImage > _lry || yImage < _uly || xImage > _lrx || xImage < _ulx) {
                jLabel.setText("");
                return;
            }

            double x = _xMin + (xImage - _ulx) / _xscale;
            double y = _yMin - (yImage - _lry) / _yscale;

            double pixelXSize = Math.abs((_xMax - _xMin) / (_ulx - _lrx));
            double pixelYSize = Math.abs((_yMax - _yMin) / (_uly - _lry));

            if (_xlog) {
                pixelXSize = Math.abs(Math.pow(10., x) - Math.pow(10., x - 1. / _xscale));
                x = Math.pow(10., x);
            }
            if (_ylog) {
                pixelYSize = Math.abs(Math.pow(10., y) - Math.pow(10., y + 1. / _yscale));
                y = Math.pow(10., y);
            }

            double xPrecision = Math.pow(10., Math.round(Math.log(pixelXSize) * _LOG10SCALE - 1));
            double yPrecision = Math.pow(10., Math.round(Math.log(pixelYSize) * _LOG10SCALE - 1));

            int xSignificantDigits = (int) (Math.log(x / xPrecision + 1) * _LOG10SCALE);
            int ySignificantDigits = (int) (Math.log(y / yPrecision + 1) * _LOG10SCALE);

            String xString = Utils.roundDouble(x, xSignificantDigits + 1);
            String yString = Utils.roundDouble(y, ySignificantDigits + 1);

            jLabel.setText("(" + xString + " , " + yString + ")");

            if (isSlap) {
                SlapUtils.markTableOnSlap(x);
            }

            if (isSpline) {
                //Utils.markTableOnSpline(x);
            }
        }
    }

    public class ExtendedZoomListener extends ZoomListener {

        public void mouseClicked(MouseEvent evt) {

            // Calculation real values
            int xImage = evt.getX();
            int yImage = evt.getY();

            double x = _xMin + (xImage - _ulx) / _xscale;
            double y = _yMin - (yImage - _lry) / _yscale;

            double pixelXSize = Math.abs((_xMax - _xMin) / (_ulx - _lrx));
            double pixelYSize = Math.abs((_yMax - _yMin) / (_uly - _lry));

            if (_xlog) {
                pixelXSize = Math.abs(Math.pow(10., x) - Math.pow(10., x - 1. / _xscale));
                x = Math.pow(10., x);
            }

            if (_ylog) {
                pixelYSize = Math.abs(Math.pow(10., y) - Math.pow(10., y + 1. / _yscale));
                y = Math.pow(10., y);
            }


            // Starting actions
            if (isNorm) {

                passValues(x, y);
                isNorm = false;

            } else if ((!isSlap) || (!isSpline)) {
                requestFocus();
            }

            if (isCross) {
                setPoint(x, y);
                setCrossNotActive();
            }

        }

        public void passValues(double x, double y) {
            AIOSPECTOOLDETACHED.setWaitCursor();
            AIOSPECTOOLDETACHED.normalizeSpectra(x, y);

        }

        public void setPoint(double x, double y) {

            String points = String.valueOf(x);
            AIOSPECTOOLDETACHED.setDefaultCursor();
            AIOSPECTOOLDETACHED.waveToVelocityValue.setText(points);

        }

        public void mouseReleased(MouseEvent evt) {

            if (isSlap) {

                SlapUtils.slapViewer(slapRequest);

            } else if (isSpline) {

                //System.out.println("Mouse released");
                splineViewer(splineRequest);

            } else {
                super.mouseReleased(evt);
            }
        }

        public void splineViewer(SplineRequest splineRequest) {

            if (SPLINEVIEWER == null) {
                SPLINEVIEWER = new LuminosityViewer(AIOSPECTOOLDETACHED);
            }
            SPLINEVIEWER.setSplineRequest(splineRequest);
            SPLINEVIEWER.setVisible(true);
            AIOSPECTOOLDETACHED.setDefaultCursor();
        }

        public void mousePressed(MouseEvent evt) {

            if (isSlap) {

                // Calculation real values
                int xImage = evt.getX();

                double x = _xMin + (xImage - _ulx) / _xscale;

                double pixelXSize = Math.abs((_xMax - _xMin) / (_ulx - _lrx));

                if (_xlog) {
                    pixelXSize = Math.abs(Math.pow(10., x) - Math.pow(10., x - 1. / _xscale));
                    x = Math.pow(10., x);
                }


                if (xImage > _lrx || xImage < _ulx) {
                    return;
                }

                slapRequest.setWMinOnImage(xImage);
                slapRequest.setWMaxOnImage(xImage);
                slapRequest.setW1(x);
                slapRequest.setW2(x);

                slapRegion = new SlapRegion();
                repaint();

            } else if (isSpline) {

                // Calculation real values
                int xImage = evt.getX();

                double x = _xMin + (xImage - _ulx) / _xscale;

                double pixelXSize = Math.abs((_xMax - _xMin) / (_ulx - _lrx));

                if (_xlog) {
                    pixelXSize = Math.abs(Math.pow(10., x) - Math.pow(10., x - 1. / _xscale));
                    x = Math.pow(10., x);
                }


                if (xImage > _lrx || xImage < _ulx) {
                    return;
                }

                splineRequest.setWMinOnImage(xImage);
                splineRequest.setWMaxOnImage(xImage);
                splineRequest.setW1(x);
                splineRequest.setW2(x);

                splineRegion = new SplineRegion();
                repaint();

            } else {
                super.mousePressed(evt);
            }
        }
    }

    public PlotDataSet searchPlotDataSet(int externalDataSetNumber) {

        for (Enumeration e = dataSet.elements(); e.hasMoreElements();) {
            PlotDataSet plotDataSet = (PlotDataSet) e.nextElement();
            if (plotDataSet.getPlotDataSet() == externalDataSetNumber) {
                return plotDataSet;
            }
        }
        return (PlotDataSet) null;
    }

    public void setNormActive() {
        isNorm = true;
    }

    public void setNormNotActive() {
        isNorm = false;
    }

    public void setCrossActive() {
        isCross = true;
    }

    public void setCrossNotActive() {
        isCross = false;
    }

    public boolean getNormActive() {
        return isNorm;
    }

    public void setSlapActive() {
        repaint();
        slapRequest = new SlapRequest();
        slapRegion = new SlapRegion();
        cursorLine = new GraphicalLine(false);
        isSlap = true;
    }

    public void setSlapNotActive() {
        isSlap = false;
        splineRegion = new SplineRegion();
        cursorLine = new GraphicalLine(false);
        repaint();
    }

    public boolean getSlapActive() {
        return isSlap;
    }

    public void setSplineActive() {
        repaint();
        splineRequest = new SplineRequest();
        splineRegion = new SplineRegion();
        cursorLine = new GraphicalLine(false);
        isSpline = true;
    }

    public void setSplineNotActive() {
        isSpline = false;
        splineRegion = new SplineRegion();
        cursorLine = new GraphicalLine(false);
        repaint();
    }

    public boolean getSplineActive() {
        return isSpline;
    }

    public void drawLine(double x, String lineText, boolean definitive) {
       
        int lineX = getXOnImage(x);
        if (!definitive) {
            cursorLine.refresh(lineX, lineText);
        }
        repaint();
    }

    public int getXOnImage(double x) {

        int xOnImage;

        if (_xlog) {
            x = Math.log(x) * _LOG10SCALE;
        }
        xOnImage = (int) Math.round((x - _xMin) * _xscale + _ulx);
        return xOnImage;
    }

    public void paint(Graphics g) {

        super.paint(g);

        SpectraViewer sv = new SpectraViewer(this, displayPanel);

        if (slapRegion.boxToDelete && isSlap) {
            slapRegion.draw(g);
        }
        if (cursorLine.lineToDelete && isSlap) {
            cursorLine.draw(g);
        }

        if (splineRegion.boxToDelete && isSpline) {
            splineRegion.draw(g);
        }
        if (cursorLine.lineToDelete && isSpline) {
            cursorLine.draw(g);
        }

    }

    public void fillPlot() {

        slapRegion = new SlapRegion();
        splineRegion = new SplineRegion();

        cursorLine = new GraphicalLine(false);

        super.fillPlot();

    }

    public class GraphicalLine {

        private Color markedLineColor = Color.yellow;
        public boolean definitive = false;
        public boolean lineToDelete = false;
        public int linePosition = 0;
        public String lineText = "";

        public GraphicalLine() {
        }

        public GraphicalLine(boolean definitive) {

            this.definitive = definitive;

            if (definitive) {
                markedLineColor = Color.white;
            } else {
                markedLineColor = Color.yellow;
            }

        }

        public void updateValues(int linePosition, String lineText) {

            this.linePosition = linePosition;
            this.lineText = lineText;
        }

        public void refresh(int linePosition, String lineText) {

            if (lineToDelete) {
                draw(false);
            }
            updateValues(linePosition, lineText);
            draw(true);
        }

        public void draw(boolean lineToDelete) {
            Graphics g = getGraphics();
            draw(g);
            this.lineToDelete = lineToDelete;
        }

        public void draw(Graphics g) {

            if (linePosition > _lrx || linePosition < _ulx) {
                return;
            }

            g.setColor(new Color(9));
            g.setXORMode(markedLineColor);

            if (definitive) {
                g.drawLine(linePosition, 45 * ((_lry - _uly) + _uly) / 100, linePosition, 30 * ((_lry - _uly) + _uly) / 100);
            } else {
                g.drawLine(linePosition, 45 * ((_lry - _uly) + _uly) / 100, linePosition, 30 * ((_lry - _uly) + _uly) / 100);
            }
            g.drawString(lineText, linePosition, 25 * ((_lry - _uly) + _uly) / 100);

            g.setPaintMode();
        }
    }

    public class SlapRegion {

        private Color regionColor = Color.blue;
        public boolean boxToDelete = false;
        public int minx = 0;
        public int maxx = 0;
        public int miny = 0;
        public int maxy = 0;

        public SlapRegion() {
        }

        public void updateValues() {
            // Draw new Region
            minx = slapRequest.wMinOnImage;
            maxx = slapRequest.wMaxOnImage;
            miny = _uly;
            maxy = _lry;
        }

        public void refresh() {
            if (boxToDelete) {
                draw(false);
            }
            updateValues();
            draw(true);
        }

        public void draw(boolean boxToDelete) {
            Graphics g = getGraphics();
            draw(g);
            this.boxToDelete = boxToDelete;
        }

        public void draw(Graphics g) {

            g.setColor(new Color(9));
            g.setXORMode(regionColor);

            g.fillRect(minx, miny, maxx - minx, maxy - miny);

            g.setPaintMode();
        }
    }

    public class SplineRegion {

        private Color regionColor = Color.blue;
        public boolean boxToDelete = false;
        public int minx = 0;
        public int maxx = 0;
        public int miny = 0;
        public int maxy = 0;

        public SplineRegion() {
        }

        public void updateValues() {
            // Draw new Region
            minx = splineRequest.wMinOnImage;
            maxx = splineRequest.wMaxOnImage;
            miny = _uly;
            maxy = _lry;
        }

        public void refresh() {
            if (boxToDelete) {
                draw(false);
            }
            updateValues();
            draw(true);
        }

        public void draw(boolean boxToDelete) {
            Graphics g = getGraphics();
            draw(g);
            this.boxToDelete = boxToDelete;
        }

        public void draw(Graphics g) {

            g.setColor(new Color(9));
            g.setXORMode(regionColor);

            g.fillRect(minx, miny, maxx - minx, maxy - miny);

            g.setPaintMode();
        }
    }
}
