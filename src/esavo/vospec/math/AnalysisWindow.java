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
public class AnalysisWindow extends javax.swing.JFrame implements java.awt.event.ActionListener{
    
    private VOSpecDetached AIOSPECTOOLDETACHED;
    
    public ExtendedPlot plot;
    public SpectrumSet spectrumSet;
    public String order;
    //public DefaultTreeModel model;
    
    public static int MIRRORING 	= 0;
    public static int FILTERING 	= 1;
    public static int EW 		= 2;
    public static int INT_FLUX 		= 3;
    public static int W_TO_V 		= 4;
    public static int TUNING            = 5;
    public static int WAV_TRANS         = 6;
    public static int WAV_INV           = 7;
    
    //public CheckNode fittingNode;
    
    public Hashtable nodeHashtable      = new Hashtable();
    
    private double xMinOriginal;
    private double xMaxOriginal;


    public AnalysisWindow(ExtendedPlot plot,SpectrumSet spectrumSet/*,DefaultTreeModel model*/) {
        initComponents();
        initializeButtonGroups();
        this.plot = plot;
        this.spectrumSet = spectrumSet;
        this.setTitle("Analysis Window");
        //this.serverListTree = serverListTree;
        //this.model = model;
        setSize(540,400);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - this.getWidth() /2, (screenSize.height / 2)- this.getHeight() /2);

    }
    
    
    private void initializeButtonGroups(){
        lineMirroringRadioButton.addActionListener(this);
        spectrumMirroringRadioButton.addActionListener(this);
        spectrumMirroringAxisRadioButton.addActionListener(this);
        
        //Group the mirroring radio buttons.
        ButtonGroup mirroringGroup = new ButtonGroup();
        mirroringGroup.add(lineMirroringRadioButton);
        mirroringGroup.add(spectrumMirroringRadioButton);
        mirroringGroup.add(spectrumMirroringAxisRadioButton);
        
        //Don't forget hidden button
        lineFluxRadioButton.addActionListener(this);
        intFluxRadioButton.addActionListener(this);
        fluxHiddenRadioButton.addActionListener(this);
        
        //Group the flux radio buttons.
        ButtonGroup fluxGroup = new ButtonGroup();
        fluxGroup.add(lineFluxRadioButton);
        fluxGroup.add(intFluxRadioButton);
        fluxGroup.add(fluxHiddenRadioButton);
        
        singleValuatorRadioButton.addActionListener(this);
        rebinningRadioButton.addActionListener(this);
        rejectRadioButton.addActionListener(this);
        tuningHiddenRadioButton.addActionListener(this);
        
        //Group the tuning radio buttons.
        ButtonGroup tuningGroup = new ButtonGroup();
        tuningGroup.add(singleValuatorRadioButton);
        tuningGroup.add(rebinningRadioButton);
        tuningGroup.add(rejectRadioButton);
        tuningGroup.add(tuningHiddenRadioButton);
        
        meanRadioButton.addActionListener(this);
        medianRadioButton.addActionListener(this);
        standardDevRadioButton.addActionListener(this);
        varianceRadioButton.addActionListener(this);
        madRadioButton.addActionListener(this);
        rangeRadioButton.addActionListener(this);
        minimumRadioButton.addActionListener(this);
        maximumRadioButton.addActionListener(this);
        statisticsHiddenRadioButton.addActionListener(this);
        
        //Group the statistics radio buttons
        ButtonGroup statisticsGroup = new ButtonGroup();
        statisticsGroup.add(meanRadioButton);
        statisticsGroup.add(medianRadioButton);
        statisticsGroup.add(standardDevRadioButton);
        statisticsGroup.add(varianceRadioButton);
        statisticsGroup.add(madRadioButton);
        statisticsGroup.add(rangeRadioButton);
        statisticsGroup.add(minimumRadioButton);
        statisticsGroup.add(maximumRadioButton);
        statisticsGroup.add(statisticsHiddenRadioButton);
        
        
        daubechiesRadioButton.addActionListener(this);
        symletsRadioButton.addActionListener(this);
        coifletsRadioButton.addActionListener(this);
        
        //Group the wavelet filters radio buttons.
        ButtonGroup waveletGroup = new ButtonGroup();
        waveletGroup.add(daubechiesRadioButton);
        waveletGroup.add(symletsRadioButton);
        waveletGroup.add(coifletsRadioButton);
        
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jTabbedPane1 = new javax.swing.JTabbedPane();
        equivalentWidthPanel = new javax.swing.JPanel();
        eqWidthButton = new javax.swing.JButton();
        eqWidthScrollPane = new javax.swing.JScrollPane();
        eqWidthTextArea = new javax.swing.JTextArea();
        fluxPanel = new javax.swing.JPanel();
        intFluxValueButton = new javax.swing.JButton();
        fluxPane = new javax.swing.JPanel();
        intFluxRadioButton = new javax.swing.JRadioButton();
        fluxHiddenRadioButton = new javax.swing.JRadioButton();
        lineFluxRadioButton = new javax.swing.JRadioButton();
        intFluxTextArea = new javax.swing.JTextArea();
        waveletAnalysisPanel = new javax.swing.JPanel();
        transformButton = new javax.swing.JButton();
        inverseButton = new javax.swing.JButton();
        waveletFunctionsPanel = new javax.swing.JPanel();
        daubechiesRadioButton = new javax.swing.JRadioButton();
        symletsRadioButton = new javax.swing.JRadioButton();
        coifletsRadioButton = new javax.swing.JRadioButton();
        daubechiesComboBox = new javax.swing.JComboBox();
        symletsComboBox = new javax.swing.JComboBox();
        coifletsComboBox = new javax.swing.JComboBox();
        statisticsPanel = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        statisticsTextArea = new javax.swing.JTextArea();
        statisticsButton = new javax.swing.JButton();
        centralTendencyPanel = new javax.swing.JPanel();
        meanRadioButton = new javax.swing.JRadioButton();
        medianRadioButton = new javax.swing.JRadioButton();
        dispersionPanel = new javax.swing.JPanel();
        varianceRadioButton = new javax.swing.JRadioButton();
        standardDevRadioButton = new javax.swing.JRadioButton();
        madRadioButton = new javax.swing.JRadioButton();
        rangeRadioButton = new javax.swing.JRadioButton();
        extremePanel = new javax.swing.JPanel();
        minimumRadioButton = new javax.swing.JRadioButton();
        maximumRadioButton = new javax.swing.JRadioButton();
        statisticsHiddenRadioButton = new javax.swing.JRadioButton();
        mirroringPanel = new javax.swing.JPanel();
        mirroringButton = new javax.swing.JButton();
        mirroringMethodsPanel = new javax.swing.JPanel();
        lineMirroringRadioButton = new javax.swing.JRadioButton();
        spectrumMirroringRadioButton = new javax.swing.JRadioButton();
        spectrumMirroringAxisRadioButton = new javax.swing.JRadioButton();
        axisTextField = new javax.swing.JTextField();
        tuningPanel = new javax.swing.JPanel();
        tuningButton = new javax.swing.JButton();
        tuningHiddenRadioButton = new javax.swing.JRadioButton();
        multivaluedPanel = new javax.swing.JPanel();
        singleValuatorRadioButton = new javax.swing.JRadioButton();
        rebinningPanel = new javax.swing.JPanel();
        rebinningRadioButton = new javax.swing.JRadioButton();
        rebinningTextField = new javax.swing.JTextField();
        rejectPanel = new javax.swing.JPanel();
        rejectRadioButton = new javax.swing.JRadioButton();
        bisectorPanel = new javax.swing.JPanel();
        bisectorButton = new javax.swing.JButton();
        bisectorOptionsPanel = new javax.swing.JPanel();
        automatedCheckBox = new javax.swing.JCheckBox();
        bisectorPointsTextField = new javax.swing.JTextField();
        bisectorParametersPanel = new javax.swing.JPanel();
        velocitySpanCheckBox = new javax.swing.JCheckBox();
        curvatureCheckBox = new javax.swing.JCheckBox();
        velocitySpanScrollPane = new javax.swing.JScrollPane();
        velocitySpanTextArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        curvatureTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        jTabbedPane1.setMaximumSize(new java.awt.Dimension(540, 360));
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(540, 360));
        jTabbedPane1.setName("VoSpec Fitting Window");
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(540, 360));
        jTabbedPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jTabbedPane1ComponentShown(evt);
            }
        });

        equivalentWidthPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        eqWidthButton.setText("Calculate");
        eqWidthButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eqWidthButtongenerateButtonActionPerformed3(evt);
            }
        });

        equivalentWidthPanel.add(eqWidthButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 240, -1, -1));

        eqWidthTextArea.setColumns(20);
        eqWidthTextArea.setEditable(false);
        eqWidthTextArea.setRows(5);
        eqWidthTextArea.setBorder(javax.swing.BorderFactory.createTitledBorder("Equivalent width value"));
        eqWidthScrollPane.setViewportView(eqWidthTextArea);

        equivalentWidthPanel.add(eqWidthScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 70, 330, 140));

        jTabbedPane1.addTab("Equivalent Width", equivalentWidthPanel);

        fluxPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        intFluxValueButton.setText("Calculate");
        intFluxValueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                intFluxValueButtongenerateButtonActionPerformed3(evt);
            }
        });

        fluxPanel.add(intFluxValueButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 250, -1, -1));

        fluxPane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        intFluxRadioButton.setText("Integrated Flux");
        intFluxRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        intFluxRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        fluxPane.add(intFluxRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, -1, -1));

        fluxHiddenRadioButton.setForeground(new java.awt.Color(153, 153, 153));
        fluxHiddenRadioButton.setSelected(true);
        fluxHiddenRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        fluxHiddenRadioButton.setContentAreaFilled(false);
        fluxHiddenRadioButton.setEnabled(false);
        fluxHiddenRadioButton.setFocusPainted(false);
        fluxHiddenRadioButton.setFocusable(false);
        fluxHiddenRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        fluxHiddenRadioButton.setMaximumSize(new java.awt.Dimension(0, 0));
        fluxHiddenRadioButton.setMinimumSize(new java.awt.Dimension(0, 0));
        fluxHiddenRadioButton.setRequestFocusEnabled(false);
        fluxHiddenRadioButton.setRolloverEnabled(false);
        fluxHiddenRadioButton.setVerifyInputWhenFocusTarget(false);
        fluxHiddenRadioButton.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                fluxHiddenRadioButtonComponentHidden(evt);
            }
        });

        fluxPane.add(fluxHiddenRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 0, 0));

        lineFluxRadioButton.setText("Line Flux");
        lineFluxRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        lineFluxRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        fluxPane.add(lineFluxRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, -1, -1));

        fluxPanel.add(fluxPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        intFluxTextArea.setEditable(false);
        intFluxTextArea.setTabSize(9);
        intFluxTextArea.setBorder(javax.swing.BorderFactory.createTitledBorder("Result"));
        intFluxTextArea.setMinimumSize(new java.awt.Dimension(79, 72));
        intFluxTextArea.setPreferredSize(new java.awt.Dimension(79, 72));
        fluxPanel.add(intFluxTextArea, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 130, 400, 90));

        jTabbedPane1.addTab("Flux", fluxPanel);

        transformButton.setText("Wavelet Transform");
        transformButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transformButtonActionPerformed(evt);
            }
        });

        inverseButton.setText("Inverse Wavelet Transform");
        inverseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inverseButtonActionPerformed(evt);
            }
        });

        waveletFunctionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Wavelet Functions"));
        daubechiesRadioButton.setSelected(true);
        daubechiesRadioButton.setText("Daubechies");
        daubechiesRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        daubechiesRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        symletsRadioButton.setText("Symlets");
        symletsRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        symletsRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        coifletsRadioButton.setText("Coiflets");
        coifletsRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        coifletsRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        daubechiesComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2", "4", "6", "8", "10", "12" }));

        symletsComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2", "4", "8" }));

        coifletsComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "6", "12", "18" }));

        org.jdesktop.layout.GroupLayout waveletFunctionsPanelLayout = new org.jdesktop.layout.GroupLayout(waveletFunctionsPanel);
        waveletFunctionsPanel.setLayout(waveletFunctionsPanelLayout);
        waveletFunctionsPanelLayout.setHorizontalGroup(
            waveletFunctionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(waveletFunctionsPanelLayout.createSequentialGroup()
                .addContainerGap(37, Short.MAX_VALUE)
                .add(waveletFunctionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(symletsRadioButton)
                    .add(daubechiesRadioButton)
                    .add(coifletsRadioButton))
                .add(80, 80, 80)
                .add(waveletFunctionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(symletsComboBox, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, daubechiesComboBox, 0, 53, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, coifletsComboBox, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        waveletFunctionsPanelLayout.setVerticalGroup(
            waveletFunctionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(waveletFunctionsPanelLayout.createSequentialGroup()
                .add(31, 31, 31)
                .add(waveletFunctionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(daubechiesRadioButton)
                    .add(daubechiesComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(25, 25, 25)
                .add(waveletFunctionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(symletsRadioButton)
                    .add(symletsComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(25, 25, 25)
                .add(waveletFunctionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(coifletsRadioButton)
                    .add(coifletsComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(35, 35, 35))
        );

        org.jdesktop.layout.GroupLayout waveletAnalysisPanelLayout = new org.jdesktop.layout.GroupLayout(waveletAnalysisPanel);
        waveletAnalysisPanel.setLayout(waveletAnalysisPanelLayout);
        waveletAnalysisPanelLayout.setHorizontalGroup(
            waveletAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(waveletAnalysisPanelLayout.createSequentialGroup()
                .add(waveletAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(waveletAnalysisPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(transformButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 226, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(19, 19, 19)
                        .add(inverseButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 226, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(waveletAnalysisPanelLayout.createSequentialGroup()
                        .add(78, 78, 78)
                        .add(waveletFunctionsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(52, Short.MAX_VALUE))
        );
        waveletAnalysisPanelLayout.setVerticalGroup(
            waveletAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(waveletAnalysisPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(waveletFunctionsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 207, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(27, 27, 27)
                .add(waveletAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(transformButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(inverseButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(54, Short.MAX_VALUE))
        );
        jTabbedPane1.addTab("Wavelet Analysis", waveletAnalysisPanel);

        statisticsTextArea.setColumns(20);
        statisticsTextArea.setEditable(false);
        statisticsTextArea.setRows(5);
        statisticsTextArea.setBorder(javax.swing.BorderFactory.createTitledBorder("Result"));
        jScrollPane8.setViewportView(statisticsTextArea);

        statisticsButton.setText("Calculate");
        statisticsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                statisticsButtonActionPerformed(evt);
            }
        });

        centralTendencyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Measures of central tendency"));
        meanRadioButton.setText("mean");
        meanRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        meanRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        medianRadioButton.setText("median");
        medianRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        medianRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout centralTendencyPanelLayout = new org.jdesktop.layout.GroupLayout(centralTendencyPanel);
        centralTendencyPanel.setLayout(centralTendencyPanelLayout);
        centralTendencyPanelLayout.setHorizontalGroup(
            centralTendencyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(centralTendencyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(centralTendencyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(meanRadioButton)
                    .add(medianRadioButton))
                .addContainerGap(129, Short.MAX_VALUE))
        );
        centralTendencyPanelLayout.setVerticalGroup(
            centralTendencyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(centralTendencyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(meanRadioButton)
                .add(26, 26, 26)
                .add(medianRadioButton)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        dispersionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Measures of dispersion"));
        varianceRadioButton.setText("variance");
        varianceRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        varianceRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        standardDevRadioButton.setText("standard deviation");
        standardDevRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        standardDevRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        madRadioButton.setText("median absolute deviation");
        madRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        madRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        rangeRadioButton.setText("range");
        rangeRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rangeRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout dispersionPanelLayout = new org.jdesktop.layout.GroupLayout(dispersionPanel);
        dispersionPanel.setLayout(dispersionPanelLayout);
        dispersionPanelLayout.setHorizontalGroup(
            dispersionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dispersionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(dispersionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(varianceRadioButton)
                    .add(standardDevRadioButton)
                    .add(rangeRadioButton)
                    .add(madRadioButton))
                .addContainerGap(63, Short.MAX_VALUE))
        );
        dispersionPanelLayout.setVerticalGroup(
            dispersionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dispersionPanelLayout.createSequentialGroup()
                .add(standardDevRadioButton)
                .add(17, 17, 17)
                .add(varianceRadioButton)
                .add(25, 25, 25)
                .add(madRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 23, Short.MAX_VALUE)
                .add(rangeRadioButton)
                .addContainerGap())
        );

        extremePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Extreme values"));
        minimumRadioButton.setText("minimum flux value");
        minimumRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        minimumRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        maximumRadioButton.setText("maximum flux value");
        maximumRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        maximumRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout extremePanelLayout = new org.jdesktop.layout.GroupLayout(extremePanel);
        extremePanel.setLayout(extremePanelLayout);
        extremePanelLayout.setHorizontalGroup(
            extremePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(extremePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(extremePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(maximumRadioButton)
                    .add(minimumRadioButton))
                .addContainerGap(71, Short.MAX_VALUE))
        );
        extremePanelLayout.setVerticalGroup(
            extremePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, extremePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(minimumRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 37, Short.MAX_VALUE)
                .add(maximumRadioButton)
                .add(26, 26, 26))
        );

        statisticsHiddenRadioButton.setSelected(true);
        statisticsHiddenRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        statisticsHiddenRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout statisticsPanelLayout = new org.jdesktop.layout.GroupLayout(statisticsPanel);
        statisticsPanel.setLayout(statisticsPanelLayout);
        statisticsPanelLayout.setHorizontalGroup(
            statisticsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statisticsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(statisticsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(statisticsPanelLayout.createSequentialGroup()
                        .add(statisticsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(statisticsPanelLayout.createSequentialGroup()
                                .add(statisticsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(statisticsPanelLayout.createSequentialGroup()
                                        .add(statisticsHiddenRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(27, 27, 27))
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, statisticsPanelLayout.createSequentialGroup()
                                        .add(centralTendencyPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                                .add(29, 29, 29))
                            .add(statisticsPanelLayout.createSequentialGroup()
                                .add(extremePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                        .add(statisticsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jScrollPane8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                            .add(dispersionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(statisticsButton))
                .addContainerGap())
        );
        statisticsPanelLayout.setVerticalGroup(
            statisticsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statisticsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(statisticsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(statisticsPanelLayout.createSequentialGroup()
                        .add(centralTendencyPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(extremePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(statisticsPanelLayout.createSequentialGroup()
                        .add(dispersionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(22, 22, 22)
                        .add(jScrollPane8, 0, 0, Short.MAX_VALUE)))
                .add(15, 15, 15)
                .add(statisticsButton)
                .add(13, 13, 13)
                .add(statisticsHiddenRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(90, 90, 90))
        );
        jTabbedPane1.addTab("Statistics", statisticsPanel);

        mirroringPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        mirroringButton.setText("Generate");
        mirroringButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mirroringButtongenerateButtonActionPerformed0(evt);
            }
        });

        mirroringPanel.add(mirroringButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 270, -1, -1));

        mirroringMethodsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Mirroring methods"));
        lineMirroringRadioButton.setSelected(true);
        lineMirroringRadioButton.setText("Line Mirroring");
        lineMirroringRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        lineMirroringRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        spectrumMirroringRadioButton.setText("Spectrum Mirroring");
        spectrumMirroringRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        spectrumMirroringRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        spectrumMirroringAxisRadioButton.setText("Spectrum Mirroring Input Axis");
        spectrumMirroringAxisRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        spectrumMirroringAxisRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        axisTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        axisTextField.setBorder(javax.swing.BorderFactory.createTitledBorder("Axis position"));
        axisTextField.setPreferredSize(new java.awt.Dimension(60, 20));

        org.jdesktop.layout.GroupLayout mirroringMethodsPanelLayout = new org.jdesktop.layout.GroupLayout(mirroringMethodsPanel);
        mirroringMethodsPanel.setLayout(mirroringMethodsPanelLayout);
        mirroringMethodsPanelLayout.setHorizontalGroup(
            mirroringMethodsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mirroringMethodsPanelLayout.createSequentialGroup()
                .add(mirroringMethodsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lineMirroringRadioButton)
                    .add(mirroringMethodsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(spectrumMirroringAxisRadioButton)
                        .add(6, 6, 6)
                        .add(axisTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                    .add(spectrumMirroringRadioButton))
                .addContainerGap())
        );
        mirroringMethodsPanelLayout.setVerticalGroup(
            mirroringMethodsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mirroringMethodsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mirroringMethodsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, mirroringMethodsPanelLayout.createSequentialGroup()
                        .add(lineMirroringRadioButton)
                        .add(52, 52, 52)
                        .add(spectrumMirroringRadioButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 44, Short.MAX_VALUE)
                        .add(spectrumMirroringAxisRadioButton)
                        .add(33, 33, 33))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, mirroringMethodsPanelLayout.createSequentialGroup()
                        .add(axisTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(22, 22, 22))))
        );
        mirroringPanel.add(mirroringMethodsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 410, 210));

        jTabbedPane1.addTab("Mirroring", mirroringPanel);

        tuningButton.setText("Calculate");
        tuningButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tuningButtonActionPerformed(evt);
            }
        });

        tuningHiddenRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        tuningHiddenRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        multivaluedPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Average multivalued flux values"));
        singleValuatorRadioButton.setText("average ");
        singleValuatorRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        singleValuatorRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout multivaluedPanelLayout = new org.jdesktop.layout.GroupLayout(multivaluedPanel);
        multivaluedPanel.setLayout(multivaluedPanelLayout);
        multivaluedPanelLayout.setHorizontalGroup(
            multivaluedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(multivaluedPanelLayout.createSequentialGroup()
                .add(23, 23, 23)
                .add(singleValuatorRadioButton)
                .addContainerGap(263, Short.MAX_VALUE))
        );
        multivaluedPanelLayout.setVerticalGroup(
            multivaluedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, multivaluedPanelLayout.createSequentialGroup()
                .add(singleValuatorRadioButton)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        rebinningPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Increase/decrease spectrum resolution"));
        rebinningRadioButton.setText("re-binning");
        rebinningRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rebinningRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        rebinningTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        rebinningTextField.setText("1000");
        rebinningTextField.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Number of wavelength positions", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        org.jdesktop.layout.GroupLayout rebinningPanelLayout = new org.jdesktop.layout.GroupLayout(rebinningPanel);
        rebinningPanel.setLayout(rebinningPanelLayout);
        rebinningPanelLayout.setHorizontalGroup(
            rebinningPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(rebinningPanelLayout.createSequentialGroup()
                .add(rebinningPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(rebinningPanelLayout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(rebinningRadioButton))
                    .add(rebinningPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(rebinningTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 288, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        rebinningPanelLayout.setVerticalGroup(
            rebinningPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(rebinningPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(rebinningRadioButton)
                .add(25, 25, 25)
                .add(rebinningTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        rejectPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Reject zero and negative flux values"));
        rejectRadioButton.setText("reject ");
        rejectRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rejectRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout rejectPanelLayout = new org.jdesktop.layout.GroupLayout(rejectPanel);
        rejectPanel.setLayout(rejectPanelLayout);
        rejectPanelLayout.setHorizontalGroup(
            rejectPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(rejectPanelLayout.createSequentialGroup()
                .add(26, 26, 26)
                .add(rejectRadioButton)
                .addContainerGap(274, Short.MAX_VALUE))
        );
        rejectPanelLayout.setVerticalGroup(
            rejectPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, rejectPanelLayout.createSequentialGroup()
                .add(rejectRadioButton)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout tuningPanelLayout = new org.jdesktop.layout.GroupLayout(tuningPanel);
        tuningPanel.setLayout(tuningPanelLayout);
        tuningPanelLayout.setHorizontalGroup(
            tuningPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tuningPanelLayout.createSequentialGroup()
                .add(tuningPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(tuningPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(tuningHiddenRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(tuningPanelLayout.createSequentialGroup()
                        .add(44, 44, 44)
                        .add(tuningPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(multivaluedPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(rebinningPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(rejectPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(tuningButton))))
                .addContainerGap())
        );
        tuningPanelLayout.setVerticalGroup(
            tuningPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tuningPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(multivaluedPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rebinningPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rejectPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(13, 13, 13)
                .add(tuningButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tuningHiddenRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(45, 45, 45))
        );
        jTabbedPane1.addTab("Tuning", tuningPanel);

        bisectorPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        bisectorButton.setText("Calculate");
        bisectorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bisectorButtongenerateButtonActionPerformed3(evt);
            }
        });

        bisectorPanel.add(bisectorButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, -1, -1));

        bisectorOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Bisector method"));
        automatedCheckBox.setText("Previous smoothing");
        automatedCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        automatedCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        bisectorPointsTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        bisectorPointsTextField.setText("10");
        bisectorPointsTextField.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Number of bisector points", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10)));

        org.jdesktop.layout.GroupLayout bisectorOptionsPanelLayout = new org.jdesktop.layout.GroupLayout(bisectorOptionsPanel);
        bisectorOptionsPanel.setLayout(bisectorOptionsPanelLayout);
        bisectorOptionsPanelLayout.setHorizontalGroup(
            bisectorOptionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(bisectorOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(bisectorOptionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(bisectorPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 144, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(automatedCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 160, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        bisectorOptionsPanelLayout.setVerticalGroup(
            bisectorOptionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(bisectorOptionsPanelLayout.createSequentialGroup()
                .add(21, 21, 21)
                .add(automatedCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(36, 36, 36)
                .add(bisectorPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(54, Short.MAX_VALUE))
        );
        bisectorPanel.add(bisectorOptionsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 200, 200));

        bisectorParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Bisector parameters"));
        velocitySpanCheckBox.setText("velocity span");
        velocitySpanCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        velocitySpanCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        curvatureCheckBox.setText("curvature");
        curvatureCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        curvatureCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        velocitySpanScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        velocitySpanScrollPane.setAutoscrolls(true);
        velocitySpanScrollPane.setMaximumSize(new java.awt.Dimension(220, 75));
        velocitySpanScrollPane.setMinimumSize(new java.awt.Dimension(220, 75));
        velocitySpanScrollPane.setPreferredSize(new java.awt.Dimension(220, 75));
        velocitySpanTextArea.setColumns(20);
        velocitySpanTextArea.setEditable(false);
        velocitySpanTextArea.setRows(5);
        velocitySpanTextArea.setMaximumSize(new java.awt.Dimension(220, 75));
        velocitySpanTextArea.setMinimumSize(new java.awt.Dimension(220, 75));
        velocitySpanScrollPane.setViewportView(velocitySpanTextArea);

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane2.setMaximumSize(new java.awt.Dimension(220, 75));
        jScrollPane2.setMinimumSize(new java.awt.Dimension(220, 75));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(220, 75));
        curvatureTextArea.setColumns(20);
        curvatureTextArea.setRows(5);
        curvatureTextArea.setMaximumSize(new java.awt.Dimension(220, 75));
        curvatureTextArea.setMinimumSize(new java.awt.Dimension(220, 75));
        jScrollPane2.setViewportView(curvatureTextArea);

        org.jdesktop.layout.GroupLayout bisectorParametersPanelLayout = new org.jdesktop.layout.GroupLayout(bisectorParametersPanel);
        bisectorParametersPanel.setLayout(bisectorParametersPanelLayout);
        bisectorParametersPanelLayout.setHorizontalGroup(
            bisectorParametersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(bisectorParametersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(bisectorParametersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                    .add(velocitySpanCheckBox)
                    .add(curvatureCheckBox)
                    .add(velocitySpanScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE))
                .addContainerGap())
        );
        bisectorParametersPanelLayout.setVerticalGroup(
            bisectorParametersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(bisectorParametersPanelLayout.createSequentialGroup()
                .add(velocitySpanCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(velocitySpanScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 32, Short.MAX_VALUE)
                .add(curvatureCheckBox)
                .add(15, 15, 15)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        bisectorPanel.add(bisectorParametersPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 280, 200));

        jTabbedPane1.addTab("Bisector method", bisectorPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 540, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 375, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void jTabbedPane1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTabbedPane1ComponentShown
// TODO add your handling code here:
    }//GEN-LAST:event_jTabbedPane1ComponentShown
    
    private void fluxHiddenRadioButtonComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_fluxHiddenRadioButtonComponentHidden
// TODO add your handling code here:
    }//GEN-LAST:event_fluxHiddenRadioButtonComponentHidden
    
    
    
    //Necessary for button groups
    public void actionPerformed(ActionEvent e) {
    }
    private void inverseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inverseButtonActionPerformed
        generateAction(WAV_INV, null);
    }//GEN-LAST:event_inverseButtonActionPerformed
    
    private void transformButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transformButtonActionPerformed
        generateAction(WAV_TRANS, null);
    }//GEN-LAST:event_transformButtonActionPerformed
    
    private void mirroringButtongenerateButtonActionPerformed0(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mirroringButtongenerateButtonActionPerformed0
        generateAction(MIRRORING, null);
    }//GEN-LAST:event_mirroringButtongenerateButtonActionPerformed0
    
    private void eqWidthButtongenerateButtonActionPerformed3(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eqWidthButtongenerateButtonActionPerformed3
        setWaitCursor();
        Spectrum spectrum1 = getXDataYData();
        try{
            IntegralTools intTool = new IntegralTools(spectrum1);
            double intF = intTool.getEquivWidth();
            String waveChoice = AIOSPECTOOLDETACHED.getWaveChoiceToPrint();
            eqWidthTextArea.setText(""+intF+" "+waveChoice);
        }catch(Exception e){
            
            //custom title, warning icon
            JOptionPane.showMessageDialog(this,
                    "Unable to return a suitable result. Please, review input data.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
        setDefaultCursor();
    }//GEN-LAST:event_eqWidthButtongenerateButtonActionPerformed3
    
    private void intFluxValueButtongenerateButtonActionPerformed3(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_intFluxValueButtongenerateButtonActionPerformed3
        setWaitCursor();
        Spectrum spectrum1 = getXDataYData();
        try{
            IntegralTools intTool = new IntegralTools(spectrum1);
            
            double flux = 0.0;
            
            if(intFluxRadioButton.isSelected()){
                flux = intTool.getIntegratedFlux();
            } else if(lineFluxRadioButton.isSelected()){
                flux = intTool.getLineFlux();
            }
            
            String waveChoice = AIOSPECTOOLDETACHED.getWaveChoiceToPrint();
            String fluxChoice = AIOSPECTOOLDETACHED.getFluxChoice();
            intFluxTextArea.setText(""+flux+" "+waveChoice+"  "+fluxChoice);
        }catch(Exception e){
            
            //custom title, warning icon
            JOptionPane.showMessageDialog(this,
                    "Unable to return a suitable result. Please, review input data.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
        setDefaultCursor();
    }//GEN-LAST:event_intFluxValueButtongenerateButtonActionPerformed3
    
    private void bisectorButtongenerateButtonActionPerformed3(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bisectorButtongenerateButtonActionPerformed3
        generateBisectorAction(null);
    }//GEN-LAST:event_bisectorButtongenerateButtonActionPerformed3
    
    private void tuningButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tuningButtonActionPerformed
        generateAction(TUNING,null);
    }//GEN-LAST:event_tuningButtonActionPerformed
    
    private void statisticsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statisticsButtonActionPerformed
        setWaitCursor();
        
        Spectrum spectrum = getXDataYData();
        
        double[] fluxValues = spectrum.getFluxValues();
        
        
        String title = " ";
        
        double statisticValue = 0.0;
        
        if(meanRadioButton.isSelected()){
            statisticValue = MathUtils.average(fluxValues);
            title = "mean";
        } else if(medianRadioButton.isSelected()){
            statisticValue = MathUtils.median(fluxValues);
            title = "median";
        } else if(varianceRadioButton.isSelected()){
            statisticValue = MathUtils.variance(fluxValues);
            title = "variance";
        } else if(standardDevRadioButton.isSelected()){
            statisticValue = MathUtils.standardDeviation(fluxValues);
            title = "standard deviation";
        } else if(rangeRadioButton.isSelected()){
            statisticValue = MathUtils.range(fluxValues);
            title = "range";
        } else if(madRadioButton.isSelected()){
            statisticValue = MathUtils.mad(fluxValues);
            title = "median abs. deviation";
        } else if(minimumRadioButton.isSelected()){
            statisticValue = MathUtils.minValue(fluxValues);
            title = "minimum flux value";
        } else if(maximumRadioButton.isSelected()){
            statisticValue = MathUtils.maxValue(fluxValues);
            title = "maximum flux value";
        }
        
//      String fluxChoice = AIOSPECTOOLDETACHED.getFluxChoice();
        statisticsTextArea.setText(""+ title +"  " + statisticValue+" ");
        
        setDefaultCursor();
    }//GEN-LAST:event_statisticsButtonActionPerformed
    
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
    
    public Spectrum getLinearXDataYData() {
        
        Vector dataToFit = AIOSPECTOOLDETACHED.plot.getPoints();
        int numberOfPoints = dataToFit.size();
        
        double[] xData = new double[numberOfPoints];
        double[] yData = new double[numberOfPoints];
        
        boolean logX = AIOSPECTOOLDETACHED.plot.getXLog();
        boolean logY = AIOSPECTOOLDETACHED.plot.getYLog();
        
        for(int i=0; i < numberOfPoints; i++) {
            
            double[] element = (double[]) dataToFit.elementAt(i);
            
            xData[i] = element[0];
            yData[i] = element[1];
            
            
            if(logX)    xData[i] = Math.log(xData[i])/Math.log(10);
            if(logY)    yData[i] = Math.log(yData[i])/Math.log(10);
            
            
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
    
    

    
    private void generateBisectorAction(javax.swing.JTextArea jTextArea) {
        
        setWaitCursor();
        this.AIOSPECTOOLDETACHED.createNewSpectraViewer();
        
        Spectrum spectrum = null;
        try {
            
            spectrum = generateBisector();
            
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        this.AIOSPECTOOLDETACHED.addSpectrum("Analysis Tools",spectrum,jTextArea/*,fittingNode*/);
        
        setDefaultCursor();
    }
    
    
    public Spectrum generateBisector(){
        setWaitCursor();
        
        Spectrum spectrum = new Spectrum();
        String title = "";
        
        try{
            int dataLength = getXDataYData().getWaveValues().length;
            int numBisectorPoints = 0;
            
            if(dataLength > 0){
                
                String numBisectorPointsString = bisectorPointsTextField.getText();
                numBisectorPoints = Integer.valueOf(numBisectorPointsString).intValue();
                
                Bisector bis = new Bisector(getXDataYData());
                
                if(automatedCheckBox.isSelected())  {
                    spectrum = bis.getPointsAutomatedForm(numBisectorPoints);
                    spectrum.addMetaData(" AUTOMATED PREVIOUS SMOOTHING: ", " TRUE " );
                } else{
                    spectrum = bis.getPointsManualForm(numBisectorPoints);
                    spectrum.addMetaData(" AUTOMATED PREVIOUS SMOOTHING: ", " FALSE " );
                }
                
                if(velocitySpanCheckBox.isSelected()){
                    double velocitySpan = bis.getVelocitySpan();
                    velocitySpanTextArea.setText(""+velocitySpan+ this.AIOSPECTOOLDETACHED.getWaveChoiceToPrint());
                }
                
                if(curvatureCheckBox.isSelected()){
                    double curvature = bis.getCurvature();
                    curvatureTextArea.setText(""+curvature+this.AIOSPECTOOLDETACHED.getWaveChoiceToPrint());
                }
                
                
            }else{
                //custom title, warning icon
                JOptionPane.showMessageDialog(this,
                        "Unable to evaluate bisector. Please, review input data",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
            
            title = "Bisector";
            
        }catch(Exception e){
            //custom title, warning icon
            JOptionPane.showMessageDialog(this,
                    "Unable to evaluate bisector. Please, review input data",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
        
        SpectrumUtils.setParameters(this.AIOSPECTOOLDETACHED, spectrum, title, this.AIOSPECTOOLDETACHED.mathematicMethodExecution);
        this.AIOSPECTOOLDETACHED.mathematicMethodExecution++;
        setDefaultCursor();
        
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
        
        this.AIOSPECTOOLDETACHED.addSpectrum("Analysis Tools",spectrum,jTextArea/*,fittingNode*/);
        
    }
    
    
    
    public Spectrum defineAction(int type) throws Exception {
        
//        Vector dataVector = plot.getPoints();
//        //Set the values selected on the AIOSPECTOOLDETACHED
//        String waveChoice = AIOSPECTOOLDETACHED.getWaveChoice();
//        String fluxChoice = AIOSPECTOOLDETACHED.getFluxChoice();
//
//        Unit unit = new Unit(waveChoice,fluxChoice);
        
        Spectrum spectrum = new Spectrum();
        String title = "";
        
        try{
            
            if (type == MIRRORING) {
                
                Mirroring mirroring = new Mirroring(getXDataYData());
                
                if(lineMirroringRadioButton.isSelected()){
                    
                    spectrum = mirroring.getMirroredLine();
                    
                    Double axis = new Double(mirroring.getAxis());
                    
                    spectrum.addMetaData(" MIRRORED LINE ", "");
                    spectrum.addMetaData(" AXIS :", axis.toString());
                    
                } else if(spectrumMirroringRadioButton.isSelected()){
                    
                    spectrum = mirroring.getMirroredSpectrum();
                    
                    Double axis = new Double(mirroring.getAxis());
                    
                    spectrum.addMetaData( " MIRRORED SPECTRUM ","");
                    spectrum.addMetaData(" AXIS :", axis.toString());
                    
                } else if(spectrumMirroringAxisRadioButton.isSelected()){
                    String axisString = axisTextField.getText();
                    double axis = Double.valueOf(axisString).doubleValue();
                    
                    spectrum = mirroring.getMirroredSpectrum(axis);
                    
                    spectrum.addMetaData( " MIRRORED SPECTRUM ","");
                    spectrum.addMetaData(" AXIS :", axisString);
                }
                
                
                title = "Mirroring";
                
                
            }else if (type == TUNING){
                
                
                if(singleValuatorRadioButton.isSelected()){
                    
                    spectrum = (new Smoothing()).multivaluedSpectrumSmoothing(getXDataYData());
                    
                    title = "Multivalued Spectra averaging ";
                    
                    
                } else if(rebinningRadioButton.isSelected()){
                    String numPointsString = rebinningTextField.getText();
                    int numPoints= (int) Double.valueOf(numPointsString).doubleValue();
                    
                    spectrum = MathUtils.evenlySpacedSpectrum(getXDataYData(), numPoints);
                    
                    title = "Evenly Spaced Spectrum "+numPointsString+" sampling points";
                    
                    
                } else if(rejectRadioButton.isSelected()){
                    spectrum = MathUtils.rejectZeros(getXDataYData());
                    
                    title = "Rejected zero/negative values ";
                    
                    
                }
                
            } else if(type == WAV_TRANS){
                int DAUBECHIES  =   0;
                int SYMLETS     =   1;
                int COIFLETS    =   2;
                
//              String numCoeffsString = (String) daubechiesComboBox.getSelectedItem();
//                int numCoeffs = (int) Double.valueOf(numCoeffsString).doubleValue();
                
                Spectrum evenlySpacedSpectrum = MathUtils.evenlySpacedSpectrum(getXDataYData(), 1024);
//            Spectrum evenlySpacedSpectrum = MathUtils.evenlySpacedSpectrum(getLinearXDataYData(), 1024);
                
                if (daubechiesRadioButton.isSelected()){
                    String numCoeffsString = (String) daubechiesComboBox.getSelectedItem();
                    int numCoeffs = (int) Double.valueOf(numCoeffsString).doubleValue();
                    //spectrum = (new Daub4()).daubTrans(evenlySpacedSpectrum);
                    spectrum = (new WaveletAnalysis()).waveletTransform(evenlySpacedSpectrum,DAUBECHIES, numCoeffs);
                    
                    title = "Daubechies transform " + numCoeffs;
                    
                }else if (symletsRadioButton.isSelected()){
                    String numCoeffsString = (String) symletsComboBox.getSelectedItem();
                    int numCoeffs = (int) Double.valueOf(numCoeffsString).doubleValue();
                    //spectrum = (new Daub4()).daubTrans(evenlySpacedSpectrum);
                    spectrum = (new WaveletAnalysis()).waveletTransform(evenlySpacedSpectrum,SYMLETS, numCoeffs);
                    
                    title = "Symlets transform " + numCoeffs;
                    
                } else if (coifletsRadioButton.isSelected()){
                    String numCoeffsString = (String) coifletsComboBox.getSelectedItem();
                    int numCoeffs = (int) Double.valueOf(numCoeffsString).doubleValue();
                    //spectrum = (new Daub4()).daubTrans(evenlySpacedSpectrum);
                    spectrum = (new WaveletAnalysis()).waveletTransform(evenlySpacedSpectrum,COIFLETS, numCoeffs);
                    
                    title = "Coiflets transform " + numCoeffs;
                    
                }
            }else if(type == WAV_INV){
                int DAUBECHIES  =   0;
                int SYMLETS  =   1;
                int COIFLETS  =   2;
                
//              String numCoeffsString = (String) daubechiesComboBox.getSelectedItem();
//              int numCoeffs = (int) Double.valueOf(numCoeffsString).doubleValue();
                
//        Spectrum evenlySpacedSpectrum = MathUtils.evenlySpacedSpectrum(getXDataYData(), 1024);
                Spectrum evenlySpacedSpectrum = MathUtils.evenlySpacedSpectrum(getXDataYData(), 1024);
                
                if (daubechiesRadioButton.isSelected()){
                    String numCoeffsString = (String) daubechiesComboBox.getSelectedItem();
                    int numCoeffs = (int) Double.valueOf(numCoeffsString).doubleValue();
                    //spectrum = (new Daub4()).daubTrans(evenlySpacedSpectrum);
                    spectrum = (new WaveletAnalysis()).invWaveletTrans(evenlySpacedSpectrum,DAUBECHIES, numCoeffs);
                    
                    
                    title = "Inverse Daubechies transform " + numCoeffs;
                    
                }else if (symletsRadioButton.isSelected()){
                    String numCoeffsString = (String) symletsComboBox.getSelectedItem();
                    int numCoeffs = (int) Double.valueOf(numCoeffsString).doubleValue();
                    spectrum = (new WaveletAnalysis()).invWaveletTrans(evenlySpacedSpectrum,SYMLETS, numCoeffs);
                    
                    
                    title = "Inverse Symlets transform " + numCoeffs;
                    
                } else if (coifletsRadioButton.isSelected()){
                    String numCoeffsString = (String) coifletsComboBox.getSelectedItem();
                    int numCoeffs = (int) Double.valueOf(numCoeffsString).doubleValue();
                    spectrum = (new WaveletAnalysis()).invWaveletTrans(evenlySpacedSpectrum,COIFLETS, numCoeffs);
                    
                    
                    title = "Inverse Coiflets transform " + numCoeffs;
                }
                
                spectrum.addMetaData(" TITLE ", title);
                
            }//if mirroring, filtering, ...
        }catch(Exception e){
            e.printStackTrace();
            //custom title, warning icon
            JOptionPane.showMessageDialog(this,
                    "Unable to return a suitable result.Please, review input data.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            
        }
        
//        spectrum.setUnits(unit);
//        //spectrum.setTitle(getXDataYData().getTitle()+ title);
//        spectrum.setTitle(title);
//
//        spectrum.setRedShift(AIOSPECTOOLDETACHED.getRedShift());
//        // to be displayed with the others
//        spectrum.setSelected(true);
//        spectrum.setFormat("spectrum/spectrum");
//
//
//        SpectrumSet sv = new SpectrumSet();
//        sv.addSpectrum(0, spectrum);
//        //AIOSPECTOOLDETACHED.tableModel = sv.refreshTableModel(title);
//
//
//        //add to the previous spectrumSet a new Spectrum trasformed
//        this.AIOSPECTOOLDETACHED.spectrumSet.addSpectrumSet(sv);
//
//        if(this.AIOSPECTOOLDETACHED.remoteSpectrumSet == null) this.AIOSPECTOOLDETACHED.remoteSpectrumSet = new SpectrumSet();
//        this.AIOSPECTOOLDETACHED.remoteSpectrumSet.addSpectrumSet(sv);
//
//        spectrum.setRow(this.AIOSPECTOOLDETACHED.spectrumSet.getSpectrumSet().size() - 1);
        
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
    
    public void setInitialConditions(){
        velocitySpanCheckBox.setSelected(false);
        curvatureCheckBox.setSelected(false);
        automatedCheckBox.setSelected(false);
        
        eqWidthTextArea.setText("");
        intFluxTextArea.setText("");
        velocitySpanTextArea.setText("");
        curvatureTextArea.setText("");
        axisTextField.setText("");
        statisticsTextArea.setText("");
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox automatedCheckBox;
    private javax.swing.JTextField axisTextField;
    private javax.swing.JButton bisectorButton;
    private javax.swing.JPanel bisectorOptionsPanel;
    public javax.swing.JPanel bisectorPanel;
    private javax.swing.JPanel bisectorParametersPanel;
    private javax.swing.JTextField bisectorPointsTextField;
    private javax.swing.JPanel centralTendencyPanel;
    private javax.swing.JComboBox coifletsComboBox;
    private javax.swing.JRadioButton coifletsRadioButton;
    private javax.swing.JCheckBox curvatureCheckBox;
    private javax.swing.JTextArea curvatureTextArea;
    private javax.swing.JComboBox daubechiesComboBox;
    private javax.swing.JRadioButton daubechiesRadioButton;
    private javax.swing.JPanel dispersionPanel;
    private javax.swing.JButton eqWidthButton;
    private javax.swing.JScrollPane eqWidthScrollPane;
    private javax.swing.JTextArea eqWidthTextArea;
    public javax.swing.JPanel equivalentWidthPanel;
    private javax.swing.JPanel extremePanel;
    private javax.swing.JRadioButton fluxHiddenRadioButton;
    private javax.swing.JPanel fluxPane;
    public javax.swing.JPanel fluxPanel;
    private javax.swing.JRadioButton intFluxRadioButton;
    private javax.swing.JTextArea intFluxTextArea;
    private javax.swing.JButton intFluxValueButton;
    private javax.swing.JButton inverseButton;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane8;
    public javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JRadioButton lineFluxRadioButton;
    private javax.swing.JRadioButton lineMirroringRadioButton;
    private javax.swing.JRadioButton madRadioButton;
    private javax.swing.JRadioButton maximumRadioButton;
    private javax.swing.JRadioButton meanRadioButton;
    private javax.swing.JRadioButton medianRadioButton;
    private javax.swing.JRadioButton minimumRadioButton;
    private javax.swing.JButton mirroringButton;
    private javax.swing.JPanel mirroringMethodsPanel;
    public javax.swing.JPanel mirroringPanel;
    private javax.swing.JPanel multivaluedPanel;
    private javax.swing.JRadioButton rangeRadioButton;
    private javax.swing.JPanel rebinningPanel;
    private javax.swing.JRadioButton rebinningRadioButton;
    private javax.swing.JTextField rebinningTextField;
    private javax.swing.JPanel rejectPanel;
    private javax.swing.JRadioButton rejectRadioButton;
    private javax.swing.JRadioButton singleValuatorRadioButton;
    private javax.swing.JRadioButton spectrumMirroringAxisRadioButton;
    private javax.swing.JRadioButton spectrumMirroringRadioButton;
    private javax.swing.JRadioButton standardDevRadioButton;
    private javax.swing.JButton statisticsButton;
    private javax.swing.JRadioButton statisticsHiddenRadioButton;
    private javax.swing.JPanel statisticsPanel;
    private javax.swing.JTextArea statisticsTextArea;
    private javax.swing.JComboBox symletsComboBox;
    private javax.swing.JRadioButton symletsRadioButton;
    private javax.swing.JButton transformButton;
    private javax.swing.JButton tuningButton;
    private javax.swing.JRadioButton tuningHiddenRadioButton;
    private javax.swing.JPanel tuningPanel;
    private javax.swing.JRadioButton varianceRadioButton;
    private javax.swing.JCheckBox velocitySpanCheckBox;
    private javax.swing.JScrollPane velocitySpanScrollPane;
    private javax.swing.JTextArea velocitySpanTextArea;
    private javax.swing.JPanel waveletAnalysisPanel;
    private javax.swing.JPanel waveletFunctionsPanel;
    // End of variables declaration//GEN-END:variables
    
    
    
    
}
