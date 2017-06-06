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
package esavo.vospec.util;


import esavo.vospec.main.*;
import esavo.vospec.plastic.*;
import esavo.vospec.plot.ExtendedPlot;
import esavo.vospec.spectrum.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;



/**
 *
 * @author  ibarbarisi
 */
public class LuminosityViewer extends javax.swing.JDialog {
    
    
    /** Creates new form LuminosityViewer */
    public LuminosityViewer(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }
    
    /** Creates new form LuminosityViewer */
    public LuminosityViewer(VOSpecDetached aioSpecToolDetached) {
        initComponents();
        this.setSize(320,180);
        this.setResizable(true);
        this.aioSpecToolDetached = aioSpecToolDetached;
        this.plot = aioSpecToolDetached.plot;
        initJTable();
        //votable="";
        //createHeader();
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jtablePanel = new javax.swing.JPanel();
        displayPanel = new javax.swing.JPanel();
        jScrollPane = new javax.swing.JScrollPane();
        buttonPanel = new javax.swing.JPanel();
        diffButton = new javax.swing.JButton();
        topcatButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jtablePanel.setLayout(new java.awt.BorderLayout());

        jtablePanel.setBackground(new java.awt.Color(244, 241, 239));
        jtablePanel.setBorder(new javax.swing.border.TitledBorder(null, "Luminosity Difference", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10), new java.awt.Color(102, 102, 102)));
        jtablePanel.setPreferredSize(new java.awt.Dimension(320, 180));
        displayPanel.setLayout(new java.awt.BorderLayout());

        displayPanel.setBackground(new java.awt.Color(244, 241, 239));
        displayPanel.setFont(new java.awt.Font("Dialog", 1, 10));
        displayPanel.setPreferredSize(new java.awt.Dimension(310, 150));
        jScrollPane.setBackground(new java.awt.Color(223, 218, 218));
        jScrollPane.setPreferredSize(new java.awt.Dimension(290, 105));
        displayPanel.add(jScrollPane, java.awt.BorderLayout.CENTER);

        diffButton.setFont(new java.awt.Font("Dialog", 1, 10));
        diffButton.setForeground(new java.awt.Color(102, 102, 102));
        diffButton.setText("Difference");
        diffButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                diffButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(diffButton);

        topcatButton.setFont(new java.awt.Font("Dialog", 1, 10));
        topcatButton.setForeground(new java.awt.Color(102, 102, 102));
        topcatButton.setText("Send to Topcat");
        topcatButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                topcatButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(topcatButton);

        closeButton.setFont(new java.awt.Font("Dialog", 1, 10));
        closeButton.setForeground(new java.awt.Color(102, 102, 102));
        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(closeButton);

        displayPanel.add(buttonPanel, java.awt.BorderLayout.SOUTH);

        jtablePanel.add(displayPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(jtablePanel, java.awt.BorderLayout.CENTER);

    }
    // </editor-fold>//GEN-END:initComponents
    
    private void topcatButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_topcatButtonActionPerformed
        votable="";
        createHeader();
        
        int rowCount = jTable.getModel().getRowCount();
        
        for (int x = 0; x < rowCount; x++) {
            
            int columnCount = jTable.getModel().getColumnCount();
            votable = votable + "                     <TR>\n";
            for (int y = 0; y < columnCount; y++) {
                Object data = jTable.getValueAt(x, y);
                votable = votable + "                         <TD>"+data+"</TD>\n";
            }
            votable = votable + "                     </TR>\n";
            
        }  
        closeFile();
        
        try{
            
            if(!aioSpecToolDetached.putil.plasticActive) {
                aioSpecToolDetached.putil.plastic = new Plastic(aioSpecToolDetached);
                aioSpecToolDetached.plasticButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/hub.gif")));
                aioSpecToolDetached.plasticButton.setToolTipText("Unregister with the Palstic Hub");
                aioSpecToolDetached.putil.plasticActive = true;
                sendToTopcat();
                
            }else { 
                sendToTopcat(); 
            }
        }catch(Exception e){
            System.out.println("Plastic Hub problems");
            
        }
    }//GEN-LAST:event_topcatButtonActionPerformed
    
    public void sendToTopcat(){
        System.out.println(votable);
        java.util.List subset =  aioSpecToolDetached.putil.plastic.getRegisteredIds(true);
        if(subset.size()<1){
            JOptionPane.showMessageDialog(this.aioSpecToolDetached,"Please open Topcat first");  
        }
        for(int i=0;i<subset.size();i++){
            System.out.println("Client registered "+subset.get(i));
        }
        aioSpecToolDetached.putil.plastic.sendVOTableToTopcat(subset,votable,true);
    }
    
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeButtonActionPerformed
    
    private void diffButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_diffButtonActionPerformed
        setWaitCursor();
        getSplineDiff();
        addRowJTable();
        setDefaultCursor();
        
    }//GEN-LAST:event_diffButtonActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new LuminosityViewer(new javax.swing.JFrame(), true).show();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton diffButton;
    private javax.swing.JPanel displayPanel;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JPanel jtablePanel;
    private javax.swing.JButton topcatButton;
    // End of variables declaration//GEN-END:variables
    
    public VOSpecDetached aioSpecToolDetached;
    
    public ExtendedJTable       jTable;
    public DefaultTableModel	tableModel;
    
    public double w1;
    public double w2;
    
    public String target;
    public String ra;
    public String dec;
    
    public String wInit;
    public String wEnd;
    
    public double diff;
    
    public ExtendedPlot plot;
    public File temp;
    
    public static int NUMBER_OF_POINTS = 100;
    public static int NUMBER_OF_CLOSER = 3;
    
    public String votable = "";
    
    
    //return the difference between real and theoretical spectra
    public double getSplineDiff() {
        
        Hashtable points = plot.getPointsForSpline(w1, w2);
        
        Vector realSpectrum         = (Vector) points.get("0");
        Vector theoreticalSpectrum  = (Vector) points.get("1");
        
        diff = calculateDiff(realSpectrum,theoreticalSpectrum);
        
        //return diff;
        return diff;
    }
    
    public void addRowJTable() {
        //String wave = wInit + " " + wEnd;
        String diff = String.valueOf(getSplineDiff());
        tableModel.addRow(new Object[]{this.target,this.ra,this.dec,wInit,wEnd,diff});
        jTable.setModel(tableModel);
        jTable.validate();
        //fillData();
    }
    
    public void initJTable() {
        
        tableModel = new DefaultTableModel(
                new Object [][] {
        },
                new String [] {
            "Target","Ra","Dec","W_min"," W_max","Diff"
        }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class, java.lang.String.class
            };
            
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        };
        
        jTable = new ExtendedJTable();
        jTable.setEditable(true);
        jTable.setModel(tableModel);
        jTable.validate();
        
        jScrollPane.setViewportView(jTable);
        repaint();
        show();
    }
    
    public void setSplineRequest(SplineRequest splineRequest) {
        
        this.w1 = splineRequest.getW1();
        this.w2 = splineRequest.getW2();
        
        //this.target = splineRequest.getTarget();
        this.target = aioSpecToolDetached.targetField.getText();
        
        //this.ra = splineRequest.getRA();
        this.ra = aioSpecToolDetached.raField.getText();
        //this.dec = splineRequest.getDEC();
        this.dec = aioSpecToolDetached.decField.getText();
        
        System.out.println("ra "+ra);
        System.out.println("dec "+dec);
        
        //wInit = Double.toString(w1);
        double wInit2 = Math.rint(w1*Math.pow(10,4))/Math.pow(10,4);
        wInit = Double.toString(wInit2);
        
        double wEnd2 = Math.rint(w2*Math.pow(10,4))/Math.pow(10,4);
        wEnd = Double.toString(wEnd2);
        
    }
    
    public double calculateDiff(Vector realSpectrumVector, Vector theoreticalSpectrumVector) {
        
        int ORDER = 5;
        
        double yReal = 0;
        int kIndex,iIndex;
        double diff = 0;
        double[] realPoints = null;
        double waveRealPoint = 0;
        double fluxRealPoint = 0;
        int numberOfRealElements = realSpectrumVector.size();
        
        int[] closerElements = new int[numberOfRealElements];
        
        
        PolynomialFitting pr = new PolynomialFitting(realSpectrumVector,true,true,ORDER);
        PolynomialFitting pt = new PolynomialFitting(theoreticalSpectrumVector,true,true,ORDER);
        
        pr.setRow(-1);
        try {
            new Thread(pr).start();
            while(pr.getToWait()){
                Thread.sleep(500);
            }
        } catch(Exception epf) {
            epf.printStackTrace();
        }
        
        pt.setRow(-1);
        try {
            new Thread(pt).start();
            while(pt.getToWait()){
                Thread.sleep(500);
            }
        } catch(Exception epf) {
            epf.printStackTrace();
        }
        
        double[] parameterPolPr = pr.getParameters();
        double[] parameterPolPt = pt.getParameters();
        
        
        return getIntegral(parameterPolPr,parameterPolPt,w1,w2,ORDER+1);
        
        //        Vector linearRealSpectrum   = getLinearVector(realSpectrum);
        //        Vector linearTheoSpectrum   = getLinearVector(theoreticalSpectrum);
        //        System.out.println("calculatediff2");
        //
        //        return getIntegral(linearRealSpectrum,linearTheoSpectrum,w1,w2);
        
    }
    
    public double getIntegral(double[] paramPr ,double[] paramPt, double wMin, double wMax, int coefficients) {
        
        double interval = (wMax-wMin)/NUMBER_OF_POINTS;
        
        double thisWave;
        double thisFluxDiff;
        
        double integral = 0.;
        
        for(int i = 0; i < NUMBER_OF_POINTS; i++) {
            
            thisWave         = wMin + (i + .5) * interval;
            thisFluxDiff     = getPolynomial(thisWave,paramPr,coefficients) - getPolynomial(thisWave,paramPt,coefficients);
            
            integral = integral + thisFluxDiff * interval;
        }
        
        
        return integral;
    }
    
    public double getPolynomial(double x, double[] param,int coefficients) {
        
        double returnValue = 0.;
        
        for(int i =0; i < coefficients; i++) {
            returnValue = returnValue + param[i] * Math.pow(x, i);
        }
        
        return returnValue;
    }

    public Vector getLinearVector(Vector initialVector) {
        
        Vector linearVector = new Vector();
        
        double  xMax = 0;
        double  xMin = 0;
        double  yMax = 0;
        double  yMin = 0;
        int     numberOfPoints = initialVector.size();
        
        double[] xData = new double[numberOfPoints];
        double[] yData = new double[numberOfPoints];
        
        for(int i=0; i < numberOfPoints; i++) {
            
            double[] element = (double[]) initialVector.elementAt(i);
            
            xData[i] = element[0];
            yData[i] = element[1];
            
            if(i==0) {
                xMin = xData[i];
                xMax = xData[i];
                
                yMin = yData[i];
                yMax = yData[i];
            }
            
            if(xData[i] < xMin) xMin = xData[i];
            if(xData[i] > xMax) xMax = xData[i];
            
            if(yData[i] < yMin) yMin = yData[i];
            if(yData[i] > yMax) yMax = yData[i];
        }
        
        int linearPoints = 0;
        Vector orphanVector = new Vector();
        
        for(int k=0; k < 10 * NUMBER_OF_POINTS; k++) {
            
            double waveMedium = k * (xMax - xMin)/(10 * NUMBER_OF_POINTS-1) + xMin;
            double waveDoubleMax = (1.* k+.5) * (xMax - xMin)/(10 * NUMBER_OF_POINTS-1) + xMin;
            double waveDoubleMin = (1.* k-.5) * (xMax - xMin)/(10 * NUMBER_OF_POINTS-1) + xMin;
            
            double fluxMedium = 0;
            int pointsInThisRange = 0;
            
            for(int i=0; i < numberOfPoints; i++) {
                
                if(xData[i]<waveDoubleMax && xData[i]>waveDoubleMin) {
                    fluxMedium = fluxMedium + yData[i];
                    pointsInThisRange++;
                }
            }
            
            if(pointsInThisRange > 0) {
                
                double error = 0;
                for(int i=0; i < numberOfPoints; i++) {
                    if(xData[i]<waveDoubleMax && xData[i]>waveDoubleMin) {
                        error += Math.abs(yData[i] - fluxMedium/pointsInThisRange);
                    }
                }
                
                double[] point = new double[2];
                point[0] = waveMedium;
                point[1] = fluxMedium / pointsInThisRange;
                linearVector.add(point);
                
                linearPoints++;
            } else {
                orphanVector.add(new Double(waveMedium));
            }
        }
        
        double thisWave;
        double thisFlux;
        for(int i=0; i< orphanVector.size(); i++) {
            thisWave = ((Double) orphanVector.elementAt(i)).doubleValue();
            thisFlux = getPolynomialInterpolation(thisWave,linearVector);
            
            double[] point  = new double[2];
            point[0]        = thisWave;
            point[1]        = thisFlux;
            linearVector.add(point);
            
        }
        
        linearVector = getOrderedVector(linearVector);
        
        return linearVector;
        
    }
    
    public Vector getOrderedVector(Vector inputVector) {
        
//        Vector outputVector = new Vector();
//
//        Vector tmpVevtor = new Vector();
//        double[] point = new double[2];
//        double pointToCheck = 0;
//
//        for(int i=0;i<inputVector.size();i++){
//            boolean isMin = true;
//
//            point = (double[])inputVector.elementAt(i);
//            pointToCheck = point[0];
//
//            for (int j=0;j<inputVector.size();j++){
//                double[] pointRecursed = (double[])inputVector.elementAt(i);
//                double pointToRecurse = point[0];
//                if (pointToCheck > pointToRecurse){
//                    isMin = false;
//                }
//            }
//            if(isMin){
//                outputVector.add(point);
//            }
//        }
        
        SpectralPointsComparator comparator = new SpectralPointsComparator();
        Collections.sort(inputVector, comparator);
        return inputVector;
//        return outputVector;
    }
    
    public double getIntegral(Vector firstVector, Vector secondVector, double initialWave, double finalWave) {
        
        double integralValue = 0;
        
        double[] firstPoint;
        double[] secondPoint;
        
        double  wave;
        double  firstFlux;
        double  secondFlux;
        
        double waveMin, waveMax;
        
        for(int i=0; i < firstVector.size(); i++) {
            
            firstPoint = (double[]) firstVector.elementAt(i);
            secondPoint = (double[]) secondVector.elementAt(i);
            
            wave = firstPoint[0];
            firstFlux = firstPoint[1];
            secondFlux = secondPoint[1];
            
            if(i == 0) {
                waveMin = initialWave;
            } else {
                waveMin = (((double[]) firstVector.elementAt(i-1))[0] + wave)/2.;
            }
            
            if(i == firstVector.size() - 1) {
                waveMax = finalWave;
            } else {
                waveMax = (((double[]) firstVector.elementAt(i+1))[0] + wave)/2.;
            }
            
            integralValue = integralValue + (waveMax-waveMin) * (firstFlux - secondFlux);
            
        }
        
        return integralValue;
        
    }
    
    
    public double distance(double wave1, double wave2) {
        return (wave1-wave2)*(wave1-wave2);
    }
    
    public double getPolynomialInterpolation(double wave, Vector initialVector) {
        
        Vector closerElements = new Vector();
        
        double returnFlux = 0;
        
        double[] point = null;
        double thisWave = 0;
        double thisFlux = 0;
        
        for(int i=0 ; i < initialVector.size(); i++) {
            
            point       = (double[]) initialVector.elementAt(i);
            thisWave    = point[0];
            
            if(closerElements.size() < NUMBER_OF_CLOSER ) {
                closerElements.add(point);
            } else {
                
                double closerWave;
                for(int k=0; k< closerElements.size(); k++) {
                    closerWave = ((double[]) closerElements.elementAt(k))[0];
                    if(distance(thisWave,wave) < distance(closerWave,wave)) {
                        closerElements.add(point);
                        break;
                    }
                }
            }
        }
        
        
        double topPart      = 1.;
        double bottomPart   = 1.;
        
        double thisWave_i = 0;
        double thisFlux_i = 0;
        double thisWave_k = 0;
        double thisFlux_k = 0;
        for(int i=0; i < closerElements.size(); i++) {
            
            point       = (double[]) closerElements.elementAt(i);
            
            thisWave_i    = point[0];
            thisFlux_i    = point[1];
            
            for(int k=0; k < closerElements.size(); k++) {
                
                point       = (double[]) closerElements.elementAt(i);
                
                thisWave_k    = point[0];
                thisFlux_k    = point[1];
                
                if(i != k) topPart    = topPart     * (wave-thisWave_k);
                if(i != k) bottomPart = bottomPart  * (thisWave_i - thisFlux_k);
            }
            
            if(Math.abs(bottomPart) > 1.E-40) returnFlux = returnFlux + thisFlux_i * topPart / bottomPart;
        }
        
        return returnFlux;
        
    }
    
    public void createHeader() {
        
        try {
            votable = votable + "<?xml version=\"1.0\"?>\n";
            votable = votable + "<!DOCTYPE VOTABLE SYSTEM \"http://us-vo.org/xml/VOTable.dtd\">\n";
            votable = votable + "  <VOTABLE>\n";
            votable = votable + "       <DESCRIPTION>\n";
            votable = votable + "          European Science Astronomy Centre - Created by VOSpec: http://esavo.esa.int/vospec\n";
            votable = votable + "       </DESCRIPTION>\n";
            votable = votable + "       <RESOURCE>\n";
            votable = votable + "          <TABLE name=\"Luminosity Difference Table\"  >\n";
            votable = votable + "            <FIELD datatype=\"deg\"  precision=\"6\"  width=\"10\"  name=\"TARGET\"  />\n";
            votable = votable + "             <FIELD unit=\"deg\"  datatype=\"float\"  precision=\"8\"  width=\"20\"  name=\"RA\"  />\n";
            votable = votable + "             <FIELD unit=\"charArray\"  datatype=\"float\"  precision=\"8\"  width=\"20\"  name=\"DEC\"  />\n";
            votable = votable + "              <FIELD unit=\""+this.aioSpecToolDetached.waveChoice.getSelectedItem()+"*"+this.aioSpecToolDetached.fluxChoice.getSelectedItem()+"\"  datatype=\"float\"  precision=\"6\"  width=\"10\"  name=\"DIFF\"  />\n";
            votable = votable + "               <FIELD unit=\""+this.aioSpecToolDetached.waveChoice.getSelectedItem()+"\"  datatype=\"float\"  precision=\"8\"  width=\"20\"  name=\"W1\"  />\n";
            votable = votable + "                <FIELD unit=\""+this.aioSpecToolDetached.waveChoice.getSelectedItem()+"\"  datatype=\"float\"  precision=\"8\"  width=\"20\"  name=\"W2\"  />\n";
            
            votable = votable + "               <DATA>\n";
            votable = votable + "                <TABLEDATA>\n";
            
            
        }catch (Exception e){
            System.out.println(e);
        }
    }
    
//    public void fillData(){
//        
//        try {
//            
//            votable = votable + "                     <TR>\n";
//            votable = votable + "                         <TD>"+target+"</TD>\n";
//            votable = votable + "                         <TD>"+ra+"</TD>\n";
//            votable = votable + "                         <TD>"+dec+"</TD>\n";
//            votable = votable + "                         <TD>"+diff+"</TD>\n";
//            votable = votable + "                         <TD>"+w1+"</TD>\n";
//            votable = votable + "                         <TD>"+w2+"</TD>\n";
//            votable = votable + "                     </TR>\n";
//            
//        } catch (Exception e){
//            
//            System.out.println(e);
//        }
//    }
    
    public void closeFile() {
        try {
            votable = votable + "                  </TABLEDATA>\n";
            votable = votable + "                 </DATA>\n";
            votable = votable + "              </TABLE>\n";
            votable = votable + "         </RESOURCE>\n";
            votable = votable + "   </VOTABLE>\n";
                        
        }catch (Exception e){
            System.out.println(e);
        }
    }
    
    public void setWaitCursor() {
        Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
        setCursor(hourglassCursor);
    }
    
    public void setDefaultCursor() {
        Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        setCursor(normalCursor);
    }
    
//    public double getAverageWaveTheoretical(Vector theoreticalPoints) {
//
//        double waveAverage = 0;
//
//        //add to Vector allWaves all the waves of the theoretical spectrum
//        Vector allWaves = new Vector();
//        for (int i=0;i<theoreticalPoints.size();i++){
//            double[] point = (double[])theoreticalPoints.elementAt(i);
//            Double wave = Double.valueOf(String.valueOf(point[0]));
//            allWaves.add(wave);
//        }
//
//        int numberIntervals = 20;
//        double delta = (w2-w1)/numberIntervals;
//        double wavesInDelta = 0;
//        double tmpTotalAverage = 0;
//
//        for (int j=0;j<numberIntervals;j++){
//            w1 = w1 + delta*numberIntervals;
//            Vector tmp = aioSpecToolDetached.plot.getPointsForSpline(w1,w1+delta);
//            Vector theoreticalVector = (Vector)tmp.elementAt(1);
//            int numberPointsInDelta = theoreticalVector.size();
//
//            do {
//                if(numberPointsInDelta>0){
//                    for(int k=0;k<numberPointsInDelta;k++){
//                        double[] points = (double[]) theoreticalVector.elementAt(k);
//                        wavesInDelta = wavesInDelta + points[0];
//                    }
//                    double tmpWav = wavesInDelta/numberPointsInDelta;
//                    tmpTotalAverage = tmpTotalAverage + tmpWav;
//                }else{
//                    delta = delta*2;
//                    w1 = w1 + delta*numberIntervals;
//                    tmp = plot.getPoints(w1,w1+delta);
//                    theoreticalVector = (Vector)tmp.elementAt(1);
//                    numberPointsInDelta = theoreticalVector.size();
//                }
//            }while(numberPointsInDelta>0);//funciona?
//        }
//
//        waveAverage = tmpTotalAverage/numberIntervals;
//        return waveAverage;
//
//    }
    
    class SpectralPointsComparator implements Comparator {
        
        /** Creates a new instance of SpectralPointsComparator */
        public SpectralPointsComparator() {
        }
        public int compare(Object o1, Object o2) {
            try {
                double wv1 = ((double[])o1)[0];
                double wv2 = ((double[])o2)[0];
                return Double.compare(wv1, wv2);
            } catch (ClassCastException e) {
                return -1;
            }
        }
    }
    
}
