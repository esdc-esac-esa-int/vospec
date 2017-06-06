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
package esavo.vospec.math;

import esavo.vospec.main.*;
import esavo.vospec.plot.ExtendedPlot;
import esavo.vospec.spectrum.*;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;


/**
 *
 * @author  alaruelo
 */
public class FilteringWindow extends javax.swing.JFrame implements java.awt.event.ActionListener{
    
    private VOSpecDetached AIOSPECTOOLDETACHED;
    
//    private HelpSet hs;
//    private HelpBroker hb;
//    private URL hsURL;
    
    
    public ExtendedPlot plot;
    public SpectrumSet spectrumSet;
    public String order;
    //public DefaultTreeModel model;
    
    public static int AVERAGING 	= 0;
    public static int KERNEL            = 1;
    public static int ADAPTIVE 		= 2;
    public static int WAVELET 		= 3;
    
    //public CheckNode fittingNode;
    
    public Hashtable nodeHashtable      = new Hashtable();
    
    private double xMinOriginal;
    private double xMaxOriginal;
   
    /**
     * Creates new form AnalysisWindow
     */
    public FilteringWindow() {
        initComponents();
        initializeButtonGroups();
    }
    
    /** Creates new form FittingWindow */
   // public FilteringWindow(java.awt.Frame parent, boolean modal) {
     //   super(parent, modal);
    //}
    
    public FilteringWindow(VOSpecDetached aiospecToolDetached) {
        this();
        this.AIOSPECTOOLDETACHED = aiospecToolDetached;
    }
    
    public FilteringWindow(ExtendedPlot plot,SpectrumSet spectrumSet/*,DefaultTreeModel model*/) {
        setSize(510,350);
        initComponents();
        initializeButtonGroups();
        this.setTitle("Filtering Window");
        this.plot = plot;
        this.spectrumSet = spectrumSet;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - this.getWidth() /2, (screenSize.height / 2)- this.getHeight() /2);

        
    }
    
    private void initializeButtonGroups(){
        pointsMeanRadioButton.addActionListener(this);
        pointsMedianRadioButton.addActionListener(this);
        intMeanRadioButton.addActionListener(this);
        intMedianRadioButton.addActionListener(this);
        
        //Group the averaging filters radio buttons.
        ButtonGroup averageGroup = new ButtonGroup();
        averageGroup.add(pointsMeanRadioButton);
        averageGroup.add(pointsMedianRadioButton);
        averageGroup.add(intMeanRadioButton);
        averageGroup.add(intMedianRadioButton);
        
        gaussianRadioButton.addActionListener(this);
        lorentzianRadioButton.addActionListener(this);
        voightRadioButton.addActionListener(this);
        
        //Group the kernel filters radio buttons.
        ButtonGroup kernelGroup = new ButtonGroup();
        kernelGroup.add(gaussianRadioButton);
        kernelGroup.add(lorentzianRadioButton);
        kernelGroup.add(voightRadioButton);
        
        adaptGaussianRadioButton.addActionListener(this);
        adaptLorentzianRadioButton.addActionListener(this);
        adaptVoightRadioButton.addActionListener(this);
        
        //Group the adaptive filters radio buttons.
        ButtonGroup adaptiveGroup = new ButtonGroup();
        adaptiveGroup.add(adaptGaussianRadioButton);
        adaptiveGroup.add(adaptLorentzianRadioButton);
        adaptiveGroup.add(adaptVoightRadioButton);
        
        daubechiesRadioButton.addActionListener(this);
        symletsRadioButton.addActionListener(this);
        coifletsRadioButton.addActionListener(this);
        
        //Group the wavelet filters radio buttons.
        ButtonGroup waveletGroup = new ButtonGroup();
        waveletGroup.add(daubechiesRadioButton);
        waveletGroup.add(symletsRadioButton);
        waveletGroup.add(coifletsRadioButton);
        
        softRadioButton.addActionListener(this);
        hardRadioButton.addActionListener(this);
        
        //Group the threshold radio buttons.
        ButtonGroup thresholdGroup = new ButtonGroup();
        thresholdGroup.add(softRadioButton);
        thresholdGroup.add(hardRadioButton);
        
        
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        averagingPanel = new javax.swing.JPanel();
        pointsPanel = new javax.swing.JPanel();
        pointsMedianRadioButton = new javax.swing.JRadioButton();
        pointsMeanRadioButton = new javax.swing.JRadioButton();
        numPointsTextField = new javax.swing.JTextField();
        intervalsPanel = new javax.swing.JPanel();
        intMeanRadioButton = new javax.swing.JRadioButton();
        intMedianRadioButton = new javax.swing.JRadioButton();
        numIntervalsTextField = new javax.swing.JTextField();
        averagingButton = new javax.swing.JButton();
        kernelPanel = new javax.swing.JPanel();
        functionsPanel = new javax.swing.JPanel();
        gaussianRadioButton = new javax.swing.JRadioButton();
        lorentzianRadioButton = new javax.swing.JRadioButton();
        voightRadioButton = new javax.swing.JRadioButton();
        nuTextField = new javax.swing.JTextField();
        widthPanel = new javax.swing.JPanel();
        sigmaFittedToRangeCheckBox = new javax.swing.JCheckBox();
        sigmaTextField = new javax.swing.JTextField();
        kernelButton = new javax.swing.JButton();
        adaptivePanel = new javax.swing.JPanel();
        adaptiveButton = new javax.swing.JButton();
        functionsPanel1 = new javax.swing.JPanel();
        adaptGaussianRadioButton = new javax.swing.JRadioButton();
        adaptLorentzianRadioButton = new javax.swing.JRadioButton();
        adaptVoightRadioButton = new javax.swing.JRadioButton();
        nuTextField1 = new javax.swing.JTextField();
        waveletPanel = new javax.swing.JPanel();
        waveletFunctionsPanel = new javax.swing.JPanel();
        daubechiesRadioButton = new javax.swing.JRadioButton();
        symletsRadioButton = new javax.swing.JRadioButton();
        coifletsRadioButton = new javax.swing.JRadioButton();
        daubechiesComboBox1 = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        symletsComboBox1 = new javax.swing.JComboBox();
        coifletsComboBox1 = new javax.swing.JComboBox();
        thresholdPanel = new javax.swing.JPanel();
        thresholdTextField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        softRadioButton = new javax.swing.JRadioButton();
        hardRadioButton = new javax.swing.JRadioButton();
        waveletButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        averagingPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        averagingPanel.setPreferredSize(new java.awt.Dimension(540, 450));

        pointsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Points Filters"));

        pointsMedianRadioButton.setText("Median");
        pointsMedianRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        pointsMeanRadioButton.setSelected(true);
        pointsMeanRadioButton.setText("Mean");
        pointsMeanRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        numPointsTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        numPointsTextField.setText("3");
        numPointsTextField.setBorder(javax.swing.BorderFactory.createTitledBorder("Number of points"));

        org.jdesktop.layout.GroupLayout pointsPanelLayout = new org.jdesktop.layout.GroupLayout(pointsPanel);
        pointsPanel.setLayout(pointsPanelLayout);
        pointsPanelLayout.setHorizontalGroup(
            pointsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pointsPanelLayout.createSequentialGroup()
                .add(pointsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pointsMedianRadioButton)
                    .add(pointsMeanRadioButton)
                    .add(pointsPanelLayout.createSequentialGroup()
                        .add(32, 32, 32)
                        .add(numPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 119, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        pointsPanelLayout.setVerticalGroup(
            pointsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pointsPanelLayout.createSequentialGroup()
                .add(21, 21, 21)
                .add(pointsMeanRadioButton)
                .add(28, 28, 28)
                .add(pointsMedianRadioButton)
                .add(56, 56, 56)
                .add(numPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        intervalsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Intervals FIlters"));

        intMeanRadioButton.setText("Mean");
        intMeanRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        intMedianRadioButton.setText("Median");
        intMedianRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        numIntervalsTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        numIntervalsTextField.setText("1000");
        numIntervalsTextField.setBorder(javax.swing.BorderFactory.createTitledBorder("Number of intervals"));

        org.jdesktop.layout.GroupLayout intervalsPanelLayout = new org.jdesktop.layout.GroupLayout(intervalsPanel);
        intervalsPanel.setLayout(intervalsPanelLayout);
        intervalsPanelLayout.setHorizontalGroup(
            intervalsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(intervalsPanelLayout.createSequentialGroup()
                .add(intervalsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(intMeanRadioButton)
                    .add(intMedianRadioButton)
                    .add(intervalsPanelLayout.createSequentialGroup()
                        .add(23, 23, 23)
                        .add(numIntervalsTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)))
                .addContainerGap())
        );
        intervalsPanelLayout.setVerticalGroup(
            intervalsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(intervalsPanelLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(intMeanRadioButton)
                .add(26, 26, 26)
                .add(intMedianRadioButton)
                .add(59, 59, 59)
                .add(numIntervalsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        averagingButton.setText("Filter");
        averagingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                averagingButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout averagingPanelLayout = new org.jdesktop.layout.GroupLayout(averagingPanel);
        averagingPanel.setLayout(averagingPanelLayout);
        averagingPanelLayout.setHorizontalGroup(
            averagingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(averagingPanelLayout.createSequentialGroup()
                .add(averagingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(averagingPanelLayout.createSequentialGroup()
                        .add(34, 34, 34)
                        .add(pointsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(49, 49, 49)
                        .add(intervalsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(averagingPanelLayout.createSequentialGroup()
                        .add(184, 184, 184)
                        .add(averagingButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 88, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(106, Short.MAX_VALUE))
        );
        averagingPanelLayout.setVerticalGroup(
            averagingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(averagingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(averagingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, intervalsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pointsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(averagingButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Averaging Filters", averagingPanel);

        functionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Kernel Functions"));

        gaussianRadioButton.setSelected(true);
        gaussianRadioButton.setText("Gaussian");
        gaussianRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        lorentzianRadioButton.setText("Lorentzian");
        lorentzianRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        voightRadioButton.setText("pseudo Voight");
        voightRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        nuTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        nuTextField.setText("0.5");
        nuTextField.setBorder(javax.swing.BorderFactory.createTitledBorder("Lorentzian content"));

        org.jdesktop.layout.GroupLayout functionsPanelLayout = new org.jdesktop.layout.GroupLayout(functionsPanel);
        functionsPanel.setLayout(functionsPanelLayout);
        functionsPanelLayout.setHorizontalGroup(
            functionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(functionsPanelLayout.createSequentialGroup()
                .add(functionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(voightRadioButton)
                    .add(lorentzianRadioButton))
                .addContainerGap(101, Short.MAX_VALUE))
            .add(functionsPanelLayout.createSequentialGroup()
                .add(gaussianRadioButton)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, functionsPanelLayout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .add(nuTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 145, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(48, 48, 48))
        );
        functionsPanelLayout.setVerticalGroup(
            functionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(functionsPanelLayout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .add(gaussianRadioButton)
                .add(25, 25, 25)
                .add(lorentzianRadioButton)
                .add(29, 29, 29)
                .add(voightRadioButton)
                .add(21, 21, 21)
                .add(nuTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        widthPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Functions width"));

        sigmaFittedToRangeCheckBox.setSelected(true);
        sigmaFittedToRangeCheckBox.setText("width fitted to spectrum range");
        sigmaFittedToRangeCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        sigmaTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sigmaTextField.setText("1");
        sigmaTextField.setBorder(javax.swing.BorderFactory.createTitledBorder("Width (sigma)"));

        org.jdesktop.layout.GroupLayout widthPanelLayout = new org.jdesktop.layout.GroupLayout(widthPanel);
        widthPanel.setLayout(widthPanelLayout);
        widthPanelLayout.setHorizontalGroup(
            widthPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(widthPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(widthPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(sigmaTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, sigmaFittedToRangeCheckBox))
                .addContainerGap())
        );
        widthPanelLayout.setVerticalGroup(
            widthPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(widthPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(sigmaTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(39, 39, 39)
                .add(sigmaFittedToRangeCheckBox)
                .add(23, 23, 23))
        );

        kernelButton.setText("Filter");
        kernelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kernelButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout kernelPanelLayout = new org.jdesktop.layout.GroupLayout(kernelPanel);
        kernelPanel.setLayout(kernelPanelLayout);
        kernelPanelLayout.setHorizontalGroup(
            kernelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(kernelPanelLayout.createSequentialGroup()
                .add(25, 25, 25)
                .add(functionsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(widthPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(53, 53, 53))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, kernelPanelLayout.createSequentialGroup()
                .addContainerGap(254, Short.MAX_VALUE)
                .add(kernelButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(222, 222, 222))
        );
        kernelPanelLayout.setVerticalGroup(
            kernelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, kernelPanelLayout.createSequentialGroup()
                .add(kernelPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(kernelPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(functionsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(kernelPanelLayout.createSequentialGroup()
                        .add(40, 40, 40)
                        .add(widthPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 153, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(26, 26, 26)
                .add(kernelButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(88, 88, 88))
        );

        jTabbedPane1.addTab("Kernel Filters", kernelPanel);

        adaptivePanel.setMinimumSize(new java.awt.Dimension(0, 0));

        adaptiveButton.setText("Filter");
        adaptiveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adaptiveButtonActionPerformed(evt);
            }
        });

        functionsPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Kernel Functions"));

        adaptGaussianRadioButton.setSelected(true);
        adaptGaussianRadioButton.setText("Gaussian");
        adaptGaussianRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        adaptLorentzianRadioButton.setText("Lorentzian");
        adaptLorentzianRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        adaptVoightRadioButton.setText("pseudo Voight");
        adaptVoightRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        nuTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        nuTextField1.setText("0.5");
        nuTextField1.setBorder(javax.swing.BorderFactory.createTitledBorder("Lorentzian content"));
        nuTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuTextField1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout functionsPanel1Layout = new org.jdesktop.layout.GroupLayout(functionsPanel1);
        functionsPanel1.setLayout(functionsPanel1Layout);
        functionsPanel1Layout.setHorizontalGroup(
            functionsPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(functionsPanel1Layout.createSequentialGroup()
                .add(62, 62, 62)
                .add(functionsPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(functionsPanel1Layout.createSequentialGroup()
                        .add(adaptVoightRadioButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 49, Short.MAX_VALUE)
                        .add(nuTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 134, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(adaptLorentzianRadioButton)
                    .add(adaptGaussianRadioButton))
                .addContainerGap())
        );
        functionsPanel1Layout.setVerticalGroup(
            functionsPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(functionsPanel1Layout.createSequentialGroup()
                .add(32, 32, 32)
                .add(adaptGaussianRadioButton)
                .add(41, 41, 41)
                .add(adaptLorentzianRadioButton)
                .add(37, 37, 37)
                .add(adaptVoightRadioButton)
                .addContainerGap(19, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, functionsPanel1Layout.createSequentialGroup()
                .addContainerGap(137, Short.MAX_VALUE)
                .add(nuTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout adaptivePanelLayout = new org.jdesktop.layout.GroupLayout(adaptivePanel);
        adaptivePanel.setLayout(adaptivePanelLayout);
        adaptivePanelLayout.setHorizontalGroup(
            adaptivePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(adaptivePanelLayout.createSequentialGroup()
                .add(adaptivePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(adaptivePanelLayout.createSequentialGroup()
                        .add(52, 52, 52)
                        .add(functionsPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(adaptivePanelLayout.createSequentialGroup()
                        .add(93, 93, 93)
                        .add(adaptiveButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(127, Short.MAX_VALUE))
        );
        adaptivePanelLayout.setVerticalGroup(
            adaptivePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(adaptivePanelLayout.createSequentialGroup()
                .add(23, 23, 23)
                .add(functionsPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(32, 32, 32)
                .add(adaptiveButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Adaptive Kernel Filters", adaptivePanel);

        waveletFunctionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Wavelet Functions"));

        daubechiesRadioButton.setSelected(true);
        daubechiesRadioButton.setText("Daubechies");
        daubechiesRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        symletsRadioButton.setText("Symlets");
        symletsRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        coifletsRadioButton.setText("Coiflets");
        coifletsRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        daubechiesComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2", "4", "6", "8", "10", "12" }));

        jLabel1.setText("number of Coeffs");

        symletsComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2", "4", "8" }));

        coifletsComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "6", "12", "18" }));

        org.jdesktop.layout.GroupLayout waveletFunctionsPanelLayout = new org.jdesktop.layout.GroupLayout(waveletFunctionsPanel);
        waveletFunctionsPanel.setLayout(waveletFunctionsPanelLayout);
        waveletFunctionsPanelLayout.setHorizontalGroup(
            waveletFunctionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(waveletFunctionsPanelLayout.createSequentialGroup()
                .add(waveletFunctionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(waveletFunctionsPanelLayout.createSequentialGroup()
                        .add(108, 108, 108)
                        .add(jLabel1))
                    .add(waveletFunctionsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(waveletFunctionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(daubechiesRadioButton)
                            .add(symletsRadioButton)
                            .add(coifletsRadioButton))
                        .add(32, 32, 32)
                        .add(waveletFunctionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(coifletsComboBox1, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(symletsComboBox1, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(daubechiesComboBox1, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        waveletFunctionsPanelLayout.setVerticalGroup(
            waveletFunctionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(waveletFunctionsPanelLayout.createSequentialGroup()
                .add(jLabel1)
                .add(18, 18, 18)
                .add(waveletFunctionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(daubechiesRadioButton)
                    .add(daubechiesComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(17, 17, 17)
                .add(waveletFunctionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(symletsRadioButton)
                    .add(symletsComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 20, Short.MAX_VALUE)
                .add(waveletFunctionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(coifletsRadioButton)
                    .add(coifletsComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        thresholdPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Threshold  "));

        thresholdTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        thresholdTextField.setText("10");
        thresholdTextField.setBorder(javax.swing.BorderFactory.createTitledBorder("Threshold percent"));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Thresholding type"));

        softRadioButton.setSelected(true);
        softRadioButton.setText("soft");
        softRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        hardRadioButton.setText("hard");
        hardRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(softRadioButton)
                    .add(hardRadioButton))
                .addContainerGap(76, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(softRadioButton)
                .add(18, 18, 18)
                .add(hardRadioButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout thresholdPanelLayout = new org.jdesktop.layout.GroupLayout(thresholdPanel);
        thresholdPanel.setLayout(thresholdPanelLayout);
        thresholdPanelLayout.setHorizontalGroup(
            thresholdPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, thresholdPanelLayout.createSequentialGroup()
                .add(21, 21, 21)
                .add(thresholdPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, thresholdTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                .add(22, 22, 22))
        );
        thresholdPanelLayout.setVerticalGroup(
            thresholdPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(thresholdPanelLayout.createSequentialGroup()
                .add(33, 33, 33)
                .add(thresholdTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        waveletButton.setText("Filter");
        waveletButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waveletButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout waveletPanelLayout = new org.jdesktop.layout.GroupLayout(waveletPanel);
        waveletPanel.setLayout(waveletPanelLayout);
        waveletPanelLayout.setHorizontalGroup(
            waveletPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(waveletPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(waveletPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(waveletPanelLayout.createSequentialGroup()
                        .add(waveletFunctionsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(14, 14, 14)
                        .add(thresholdPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(waveletButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 95, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(72, Short.MAX_VALUE))
        );
        waveletPanelLayout.setVerticalGroup(
            waveletPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, waveletPanelLayout.createSequentialGroup()
                .add(waveletPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, waveletPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(thresholdPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE))
                    .add(waveletPanelLayout.createSequentialGroup()
                        .add(34, 34, 34)
                        .add(waveletFunctionsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(17, 17, 17)
                .add(waveletButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(52, 52, 52))
        );

        jTabbedPane1.addTab("Wavelet Filter", waveletPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 369, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nuTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuTextField1ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_nuTextField1ActionPerformed
    
    private void waveletButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waveletButtonActionPerformed
        generateAction(WAVELET, null);
    }//GEN-LAST:event_waveletButtonActionPerformed
    
    private void averagingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_averagingButtonActionPerformed
        generateAction(AVERAGING, null);
    }//GEN-LAST:event_averagingButtonActionPerformed
    
    private void adaptiveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adaptiveButtonActionPerformed
        generateAction(ADAPTIVE, null);
    }//GEN-LAST:event_adaptiveButtonActionPerformed
    
    private void kernelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kernelButtonActionPerformed
        generateAction(KERNEL, null);
    }//GEN-LAST:event_kernelButtonActionPerformed
    

    //necessary for button groups
    public void actionPerformed(ActionEvent e) {
        
    }
    
    public Spectrum getXDataYData() {
        
        
        Vector dataToFit = AIOSPECTOOLDETACHED.plot.getPoints();
        int numberOfPoints = dataToFit.size();
        
        double[] xData = new double[numberOfPoints];
        double[] yData = new double[numberOfPoints];
        
        boolean logX = AIOSPECTOOLDETACHED.plot.getXLog();
        boolean logY = AIOSPECTOOLDETACHED.plot.getYLog();
        
        System.out.println("logX = "+logX);
        System.out.println("logY = "+logY);
        
        for(int i=0; i < numberOfPoints; i++) {
            
            double[] element = (double[]) dataToFit.elementAt(i);
            
            xData[i] = element[0];
            yData[i] = element[1];
            
            
            if(logX)    xData[i] = Math.pow(10. , xData[i]);
            if(logY)    yData[i] = Math.pow(10. , yData[i]);
            
            if(i==0) {
                xMinOriginal = xData[i];
                xMaxOriginal = xData[i];
            }
            if(xData[i] < xMinOriginal)	xMinOriginal	= xData[i];
            if(xData[i] > xMaxOriginal)	xMaxOriginal 	= xData[i];
            
        }
        
        Spectrum spectrum = new Spectrum();
        spectrum.setWaveValues(xData);
        spectrum.setFluxValues(yData);
        
        return spectrum;
        
    }
    
    
    private void generateAction(int type, javax.swing.JTextArea jTextArea) {
        
        setWaitCursor();
        this.AIOSPECTOOLDETACHED.createNewSpectraViewer();
        
        Spectrum spectrum = null;
        try {
            spectrum = defineAction(type);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        this.AIOSPECTOOLDETACHED.addSpectrum("Filtering Tools",spectrum,jTextArea/*,fittingNode*/);
        
    }
    
    
    
    public Spectrum defineAction(int type) throws Exception {
                
        Spectrum spectrum = new Spectrum();
        String title = "";
               
        try{
            if (type == AVERAGING) {
                
                if(intMeanRadioButton.isSelected()){
                    
                    String numIntervalsString = numIntervalsTextField.getText();
                    int numIntervals = Integer.valueOf(numIntervalsTextField.getText()).intValue();
                                        
                    spectrum = (new Smoothing(getXDataYData())).mean(numIntervals);
                    title = "Intervals Mean Filtering, number of intervals = "+numIntervals ;
                    
                } else if(pointsMeanRadioButton.isSelected()){
                    
                    String numPointsString = numPointsTextField.getText();
                    int numPoints = Integer.valueOf(numPointsString).intValue();
                    
                    spectrum = (new Smoothing(getXDataYData())).nPointsAverage(numPoints);
                    title = "Points Mean Filtering, number of points " + numPointsString;
                    
                } else if(intMedianRadioButton.isSelected()){
                    String numIntervalsString = numIntervalsTextField.getText();
                    int numIntervals = Integer.valueOf(numIntervalsTextField.getText()).intValue();
                    
                    spectrum = (new Smoothing(getXDataYData())).medianFilter(numIntervals);
                    title = "Intervals Median Filtering, number of intervals = "+numIntervals ;
                    
                } else if(pointsMedianRadioButton.isSelected()){
                    
                    String numPointsString = numPointsTextField.getText();
                    int numPoints = Integer.valueOf(numPointsString).intValue();
                    
                    spectrum = (new Smoothing(getXDataYData())).nPointsMedianFilter(numPoints);
                    title = "Points Median Filtering, number of points " + numPointsString;
                    
                }
            }//AVERAGING
            else if (type == KERNEL) {
                if(gaussianRadioButton.isSelected()){
                    
                    String sigmaString = sigmaTextField.getText();
                    double sigma = Double.valueOf(sigmaString).doubleValue();
                    boolean sigmaFitted = false;
                                        
                    if(sigmaFittedToRangeCheckBox.isSelected()) sigmaFitted = true;
                    
                    spectrum = (new Smoothing(getXDataYData())).gaussianFiltering(sigma, sigmaFitted);
                    title = "Gaussian Filtering sigma " + sigmaString;
                    
                } else if(lorentzianRadioButton.isSelected()){
                    
                    String sigmaString = sigmaTextField.getText();
                    double sigma = Double.valueOf(sigmaString).doubleValue();
                    boolean sigmaFitted = false;
                    
                    if(sigmaFittedToRangeCheckBox.isSelected()) sigmaFitted = true;
                    
                    spectrum = (new Smoothing(getXDataYData())).lorentzianFiltering(sigma, sigmaFitted);
                    title = "Lorentzian Filtering sigma " + sigmaString;
                    
                }else if(voightRadioButton.isSelected()){
                    
                    String sigmaString = sigmaTextField.getText();
                    double sigma = Double.valueOf(sigmaString).doubleValue();
                    
                    
                    String nuString = nuTextField.getText();
                    double nu = Double.valueOf(nuString).doubleValue();
                    boolean sigmaFitted = false;
                    
                    if(sigmaFittedToRangeCheckBox.isSelected()) sigmaFitted = true;
                    
                    spectrum = (new Smoothing(getXDataYData())).pVoightFiltering(nu, sigma, sigmaFitted);
                    title = "Voight Filtering sigma " + sigmaString;
                    
                }
            }//KERNEL
            else if (type == ADAPTIVE) {
                
                if(adaptGaussianRadioButton.isSelected()){
                    
                    Spectrum evenlySpacedSpectrum = MathUtils.evenlySpacedSpectrum(getXDataYData(), 1000);
                    spectrum = (new Smoothing(evenlySpacedSpectrum)).adaptiveIDSGaussian();
                    title = "Adaptive Gaussian Filtering";
                    
                } else if(adaptVoightRadioButton.isSelected()){
                    
                    Spectrum evenlySpacedSpectrum = MathUtils.evenlySpacedSpectrum(getXDataYData(), 1000);
                    String nuString = nuTextField1.getText();
                    double nu = Double.valueOf(nuString).doubleValue();
                    
                    spectrum = (new Smoothing(evenlySpacedSpectrum)).adaptiveIDSPVoight(nu);
                    
                    title = "Adaptive pseudo Voight Filtering nu = "+nuString;
                    
                }else if(adaptLorentzianRadioButton.isSelected()){
                    
                    Spectrum evenlySpacedSpectrum = MathUtils.evenlySpacedSpectrum(getXDataYData(), 1000);
                    spectrum = (new Smoothing(evenlySpacedSpectrum)).adaptiveIDSLorentzian();
                    title = "Adaptive Lorentzian Filtering";
                    
                }
            }//ADAPTIVE
            else if(type == WAVELET){
                if (daubechiesRadioButton.isSelected()){
                    
                    int DAUBECHIES  =   0;
                    
                    
                    String waveletCoeffsString = (String) daubechiesComboBox1.getSelectedItem();
                    int waveletCoeffs = (int) Double.valueOf(waveletCoeffsString).doubleValue();
                    
                    String thresholdPercentString = thresholdTextField.getText();
                    double thresholdPercent =  Double.valueOf(thresholdPercentString).doubleValue();
                    
                    WaveletAnalysis wavelet = new WaveletAnalysis();
                    Spectrum evenlySpacedSpectrum = MathUtils.evenlySpacedSpectrum(getXDataYData(), 1024);
                    
                    if(hardRadioButton.isSelected()){
                        spectrum = wavelet.filter(evenlySpacedSpectrum, wavelet.DAUBECHIES, waveletCoeffs, wavelet.HARD, wavelet.UNIVERSAL, thresholdPercent);
                        title = "Wavelet (Daub) Filter " + "numCoeff = "+waveletCoeffsString+"hard tresholding, thresholdPercent = "+thresholdPercentString;
                    } else{
                        spectrum = wavelet.filter(evenlySpacedSpectrum, wavelet.DAUBECHIES, waveletCoeffs, wavelet.SOFT, wavelet.UNIVERSAL, thresholdPercent);
                        title = "Wavelet (Daub) Filter " + "numCoeff = "+waveletCoeffsString+"soft thresholding, thresholdPercent = "+thresholdPercentString;
                    }
                                
                } else if (symletsRadioButton.isSelected()){
                    
                    int SYMLETS  =   1;
                    
                    String waveletCoeffsString = (String) symletsComboBox1.getSelectedItem();
                    int waveletCoeffs = (int) Double.valueOf(waveletCoeffsString).doubleValue();
                    
                    String thresholdPercentString = thresholdTextField.getText();
                    double thresholdPercent =  Double.valueOf(thresholdPercentString).doubleValue();
                    
                    WaveletAnalysis wavelet = new WaveletAnalysis();
                    
                    Spectrum evenlySpacedSpectrum = MathUtils.evenlySpacedSpectrum(getXDataYData(), 1024);
                    
                    if(hardRadioButton.isSelected()){
                        spectrum = wavelet.filter(evenlySpacedSpectrum, wavelet.SYMLETS, waveletCoeffs, wavelet.HARD, wavelet.UNIVERSAL, thresholdPercent);
                        title = "Wavelet (Symlets) Filter " + "numCoeff = "+waveletCoeffsString+"hard tresholding, thresholdPercent = "+thresholdPercentString;
                    } else{
                        spectrum = wavelet.filter(evenlySpacedSpectrum, wavelet.SYMLETS, waveletCoeffs, wavelet.SOFT, wavelet.UNIVERSAL, thresholdPercent);
                        title = "Wavelet (Symlets) Filter " + "numCoeff = "+waveletCoeffsString+"soft thresholding, thresholdPercent = "+thresholdPercentString;
                    }
                    
                } else if (coifletsRadioButton.isSelected()){
                   
                    int COIFLETS  =   2;
                    
                    String waveletCoeffsString = (String) coifletsComboBox1.getSelectedItem();
                    int waveletCoeffs = (int) Double.valueOf(waveletCoeffsString).doubleValue();
                    
                    String thresholdPercentString = thresholdTextField.getText();
                    double thresholdPercent =  Double.valueOf(thresholdPercentString).doubleValue();
                    
                    WaveletAnalysis wavelet = new WaveletAnalysis();
                    
                    Spectrum evenlySpacedSpectrum = MathUtils.evenlySpacedSpectrum(getXDataYData(), 1024);
                    
                    if(hardRadioButton.isSelected()){
                       
                        spectrum = wavelet.filter(evenlySpacedSpectrum, wavelet.COIFLETS, waveletCoeffs, wavelet.HARD, wavelet.UNIVERSAL, thresholdPercent);
                        title = "Wavelet (Coiflets) Filter " + "numCoeff = "+waveletCoeffsString+"hard tresholding, thresholdPercent = "+thresholdPercentString;
                    } else{
                        
                        spectrum = wavelet.filter(evenlySpacedSpectrum, wavelet.COIFLETS, waveletCoeffs, wavelet.SOFT, wavelet.UNIVERSAL, thresholdPercent);
                        title = "Wavelet (Coiflets) Filter " + "numCoeff = "+waveletCoeffsString+"soft thresholding, thresholdPercent = "+thresholdPercentString;
                    }
                    
                }
                
            }//if type
        }catch(Exception e){
            
            //custom title, warning icon
            JOptionPane.showMessageDialog(this,
                    "Unable to return a suitable result. Please, review input data.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            
        }
         

        SpectrumUtils.setParameters(this.AIOSPECTOOLDETACHED, spectrum, title, this.AIOSPECTOOLDETACHED.mathematicMethodExecution);
        this.AIOSPECTOOLDETACHED.mathematicMethodExecution++;
        setDefaultCursor();
        
        return spectrum;
    }
    
    public void setWaitCursor() {
        Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
        setCursor(hourglassCursor);
        
    }
    public void setDefaultCursor() {
        Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        setCursor(normalCursor);
        
    }
    
    public void setAioSpecToolDetached(VOSpecDetached aioSpecToolDetached) {
        this.AIOSPECTOOLDETACHED = aioSpecToolDetached;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton adaptGaussianRadioButton;
    private javax.swing.JRadioButton adaptLorentzianRadioButton;
    private javax.swing.JRadioButton adaptVoightRadioButton;
    private javax.swing.JButton adaptiveButton;
    private javax.swing.JPanel adaptivePanel;
    private javax.swing.JButton averagingButton;
    private javax.swing.JPanel averagingPanel;
    private javax.swing.JComboBox coifletsComboBox1;
    private javax.swing.JRadioButton coifletsRadioButton;
    private javax.swing.JComboBox daubechiesComboBox1;
    private javax.swing.JRadioButton daubechiesRadioButton;
    private javax.swing.JPanel functionsPanel;
    private javax.swing.JPanel functionsPanel1;
    private javax.swing.JRadioButton gaussianRadioButton;
    private javax.swing.JRadioButton hardRadioButton;
    private javax.swing.JRadioButton intMeanRadioButton;
    private javax.swing.JRadioButton intMedianRadioButton;
    private javax.swing.JPanel intervalsPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton kernelButton;
    private javax.swing.JPanel kernelPanel;
    private javax.swing.JRadioButton lorentzianRadioButton;
    private javax.swing.JTextField nuTextField;
    private javax.swing.JTextField nuTextField1;
    private javax.swing.JTextField numIntervalsTextField;
    private javax.swing.JTextField numPointsTextField;
    private javax.swing.JRadioButton pointsMeanRadioButton;
    private javax.swing.JRadioButton pointsMedianRadioButton;
    private javax.swing.JPanel pointsPanel;
    private javax.swing.JCheckBox sigmaFittedToRangeCheckBox;
    private javax.swing.JTextField sigmaTextField;
    private javax.swing.JRadioButton softRadioButton;
    private javax.swing.JComboBox symletsComboBox1;
    private javax.swing.JRadioButton symletsRadioButton;
    private javax.swing.JPanel thresholdPanel;
    private javax.swing.JTextField thresholdTextField;
    private javax.swing.JRadioButton voightRadioButton;
    private javax.swing.JButton waveletButton;
    private javax.swing.JPanel waveletFunctionsPanel;
    private javax.swing.JPanel waveletPanel;
    private javax.swing.JPanel widthPanel;
    // End of variables declaration//GEN-END:variables
    
}
