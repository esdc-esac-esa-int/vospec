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


import esavo.vospec.dataingestion.*;
import esavo.vospec.math.IntegralTools;
import esavo.vospec.photometry.NDDataSet;
import esavo.vospec.photometry.PhotometryFilter;
import esavo.vospec.resourcepanel.Node;
import esavo.vospec.resourcepanel.ResourcesPanelManager;
import esavo.vospec.spectrum.Spectrum;
import esavo.vospec.spectrum.SpectrumConverter;
import esavo.vospec.spectrum.SpectrumUtils;
import esavo.vospec.spectrum.Unit;
import esavo.vospec.util.Utils;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;



public class SyntheticPhotoDialog extends javax.swing.JDialog {
    
    
    public      String              generalDesc;
    public      Vector              paramOptions;

    public      Hashtable           componentsHashtable;
    
    public      SsaServer           ss;
    public      boolean             checkMaxMin = false;

    private ResourcesPanelManager resourcesPanelManager;

    private Spectrum spectrum = new Spectrum();

    Vector<PhotometryFilter> retrievedFilters;

    VOSpecDetached aiospectooldetached;
    
    

    public SyntheticPhotoDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }
       
    
    public SyntheticPhotoDialog(boolean modal, Spectrum spectrum, VOSpecDetached aiospectooldetached) throws Exception {

        this(null,modal);

        this.aiospectooldetached=aiospectooldetached;
        this.spectrum=spectrum;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - this.getWidth() /2, (screenSize.height / 2)- this.getHeight() /2);

        paramOptions            = new Vector();
        componentsHashtable     = new Hashtable();


        ss = new SsaServer();

        ss.setSsaUrl(PFSParser.fpsUrl);
        ss.setType(SsaServer.TSAP);
        ss.setSelected(true);
        ss.setLocal(false);
        //ss.setTsa(true);


        ss = SSAIngestor.populateInputParameters(ss);
        paramOptions = ss.getInputParams();

        
        init();

        resourcesPanelManager = new ResourcesPanelManager(jToolBar1, ResourcesPanelManager.PHOTOMETRY);

        
    }

    
    
    /** Initializes the applet TheoreticalSpectrumParser */
    public void init() throws Exception {
        
        this.setSize(630,630);
        //this.setResizable(true);
        this.setTitle("Spanish VO (SVO) Filter Profile Service");
        
        initComponents();
        
        TsaServerParam  tsaServerParam;
        String          name;
        String          ucd;
        Vector          values;
        
        for (int ct=0; ct < paramOptions.size() ; ct++) {
            
            panelSeries = new JPanel();
            panelSeries.setSize(550,40);
            panelSeries.setBackground(new java.awt.Color(255, 255, 255));
            
            tsaServerParam = (TsaServerParam) paramOptions.elementAt(ct);
           
            JLabel      nameLabel           = new JLabel();
            JComboBox   optionComboBox      = new JComboBox();
            JTextField  valueTextField      = new JTextField();
            JButton     descriptionButton   = new JButton();
            JLabel      ucdLabel            = new JLabel();
            
            JSeparator jSeparator = new JSeparator();
            jSeparator.setPreferredSize(new java.awt.Dimension(580, 2));
            jSeparator.setBackground(new java.awt.Color(233, 229, 229));
            jSeparator.setForeground(new java.awt.Color(233, 229, 229));
            
            
            // Name Label
            nameLabel.setFont(new java.awt.Font("SansSerif", 1, 10));
            nameLabel.setForeground(new java.awt.Color(102, 102, 102));
            nameLabel.setPreferredSize(new java.awt.Dimension(100, 20));
            
            name = tsaServerParam.getName();
            nameLabel.setText(name);
            
            panelSeries.add(nameLabel);
             
            // Vector combo
            values = tsaServerParam.getValues();
            if(tsaServerParam.getIsCombo()) {
            
                optionComboBox.setFont(new java.awt.Font("SansSerif", 1, 10));
                optionComboBox.setForeground(new java.awt.Color(102, 102, 102));
                optionComboBox.setPreferredSize(new java.awt.Dimension(70, 24));
 
                values = tsaServerParam.getValues();
                for (int ct2=0; ct2<values.size(); ct2++){
                    optionComboBox.addItem(values.elementAt(ct2));
                }
                
                /** default choice in hashtable and in TsaServer bean**/
                String label; 
                if(tsaServerParam.getSelectedValue() != null) {
                    label = tsaServerParam.getSelectedValue();
                } else {    
                    label = (String)optionComboBox.getItemAt(0);
                }    
                 
                optionComboBox.setSelectedItem(label);
                optionComboBox.setActionCommand(""+ct);
                optionComboBox.addActionListener(combo);

                tsaServerParam.setSelectedValue(label);
                
                componentsHashtable.put(tsaServerParam, optionComboBox);

                paramOptions.setElementAt(tsaServerParam,ct);

                 
                panelSeries.add(optionComboBox);
 
            } else {
                valueTextField.setFont(new java.awt.Font("SansSerif", 1, 10));
                valueTextField.setForeground(new java.awt.Color(102, 102, 102));
                valueTextField.setPreferredSize(new java.awt.Dimension(70, 24));
                
                String stringValue = "";
                if(values.size() > 0) stringValue = (String) values.elementAt(0);
                valueTextField.setText(stringValue);
                
                valueTextField.setActionCommand(""+ct);
                valueTextField.addActionListener(lText);
                
                if(tsaServerParam.getName().toLowerCase().contains("max")&&
                        tsaServerParam.getName().toLowerCase().contains("wav")){

                    Spectrum testSpectrum = new Spectrum(spectrum);
                    String [] unitsWfinal = Utils.getDimensionalEquation(tsaServerParam.getUnit());
                    
                    Unit finalUnit = new Unit(unitsWfinal[1],unitsWfinal[0],(String)testSpectrum.getUnits().getFluxVector().get(1),((Double)testSpectrum.getUnits().getFluxVector().get(0)).toString());
                    testSpectrum=(new SpectrumConverter()).convertSpectrum(testSpectrum, finalUnit);
                    
                    double [] waveValues = testSpectrum.getWaveValues();

                    double max = waveValues[0];
                    for(int i=0;i<waveValues.length;i++){
                        if(waveValues[i]>max){
                            max=waveValues[i];
                        }
                    }

                    valueTextField.setText(new Double(max).toString());
                    
                }
                if(tsaServerParam.getName().toLowerCase().contains("min")&&
                        tsaServerParam.getName().toLowerCase().contains("wav")){

                    Spectrum testSpectrum = new Spectrum(spectrum);
                    String [] unitsWfinal = Utils.getDimensionalEquation(tsaServerParam.getUnit());
                    
                    Unit finalUnit =  new Unit(unitsWfinal[1],unitsWfinal[0],(String)testSpectrum.getUnits().getFluxVector().get(1),((Double)testSpectrum.getUnits().getFluxVector().get(0)).toString());
                    testSpectrum=(new SpectrumConverter()).convertSpectrum(testSpectrum,finalUnit);
                    
                    double [] waveValues = testSpectrum.getWaveValues();
                    
                    double min = waveValues[0];
                    for(int i=0;i<waveValues.length;i++){
                        if(waveValues[i]<min){
                            min=waveValues[i];
                        }
                    }
                   
                    valueTextField.setText(new Double(min).toString());
                    

                }


                panelSeries.add(valueTextField);
                
                componentsHashtable.put(tsaServerParam, valueTextField);

                paramOptions.setElementAt(tsaServerParam,ct);
                
            }    

            // Description Button
            descriptionButton.setFont(new java.awt.Font("SansSerif", 1, 10));
            descriptionButton.setForeground(new java.awt.Color(102, 102, 102));
            descriptionButton.setText("description");
 
            descriptionButton.setActionCommand(""+ct);
            descriptionButton.addActionListener(l);
            
            panelSeries.add(descriptionButton); 
           
            // Ucd Label
            ucdLabel.setFont(new java.awt.Font("SansSerif", 1, 10));
            ucdLabel.setForeground(new java.awt.Color(102, 102, 102));
            ucdLabel.setPreferredSize(new java.awt.Dimension(250, 20));

            if(!tsaServerParam.getUtype().equals("")){
                ucd = tsaServerParam.getUtype();
                ucdLabel.setText(ucd);
            }else{
                ucd = tsaServerParam.getUcd();
                ucdLabel.setText(ucd);
            }
            
            panelSeries.add(ucdLabel);
            
            
            //bodyPanel.add(jSeparator);
            panelContainer.add(panelSeries);
            jScrollPane2.setViewportView(panelContainer);
        }
            
        //getContentPane().add(firstPanel, java.awt.BorderLayout.CENTER);
       
    }
    
    ActionListener l = new ActionListener() {
        public void actionPerformed(ActionEvent evt){   
            
            int ct = new Integer(evt.getActionCommand()).intValue();
            TsaServerParam tsaServerParam = (TsaServerParam) paramOptions.elementAt(ct);
            VOSpecTheoreticalDescription astd = new VOSpecTheoreticalDescription(tsaServerParam.getDescription());
            astd.setVisible(true);
            
        }
    };
    
    ActionListener combo = new ActionListener() {
        public void actionPerformed(ActionEvent evt){
            
            int ct = new Integer(evt.getActionCommand()).intValue();
            TsaServerParam tsaServerParam = (TsaServerParam) paramOptions.elementAt(ct);
            
            JComboBox cb = (JComboBox)evt.getSource();
            Object newItem = cb.getSelectedItem();
            String label = newItem.toString();
            
            tsaServerParam.setSelectedValue(label);
            paramOptions.setElementAt(tsaServerParam,ct);
            
            if(checkMaxMin) {
                if(tsaServerParam.getName().toUpperCase().indexOf("_MIN") != -1 ||
                   tsaServerParam.getName().toUpperCase().indexOf("_MAX") != -1) {                    
                    
                    
                    boolean isMin = false;
                    boolean isMax = false;
                    
                    String thisTsaName = tsaServerParam.getName().toUpperCase();
                    if(thisTsaName.indexOf("_MIN") != -1) isMin = true;
                    if(thisTsaName.indexOf("_MAX") != -1) isMax = true;
                    
                    TsaServerParam tsaServerParamTmp;
                    
                    
                    for(int i=0; i < paramOptions.size(); i++) {
                        tsaServerParamTmp       = (TsaServerParam) paramOptions.elementAt(i);
                        String thisTsaNameTmp   = tsaServerParamTmp.getName().toUpperCase();
                        if(thisTsaNameTmp.equals(thisTsaName)) continue;
                        
                        String expectedName = "";
                        if(isMin) expectedName = thisTsaName.substring(0, thisTsaName.lastIndexOf("_MIN")) + "_MAX";
                        if(isMax) expectedName = thisTsaName.substring(0, thisTsaName.lastIndexOf("_MAX")) + "_MIN";
                        
                        
                        if(thisTsaNameTmp.equals(expectedName)) {
                            if(tsaServerParamTmp.getIsCombo()) ((JComboBox) componentsHashtable.get(tsaServerParamTmp)).setSelectedItem(label);
                            tsaServerParamTmp.setSelectedValue(label);
                            paramOptions.setElementAt(tsaServerParamTmp,i);
                            
                        }
                            
                    }
                    
                }            
            }
            
        }
    };

    ActionListener lText = new ActionListener() {
        public void actionPerformed(ActionEvent evt){
            
            int ct = new Integer(evt.getActionCommand()).intValue();
            TsaServerParam tsaServerParam = (TsaServerParam) paramOptions.elementAt(ct);
            
            JTextField jt = (JTextField)evt.getSource();
            String label = jt.getText();
            
            tsaServerParam.setSelectedValue(label);
            paramOptions.setElementAt(tsaServerParam,ct);
        }
    };
    
    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    
    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        bodyPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        panelContainer = new javax.swing.JPanel();
        panelSeries = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        retrieveButton = new javax.swing.JButton();
        viewButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jPanel4 = new javax.swing.JPanel();
        generateButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(422, 422));

        jSplitPane1.setDividerLocation(300);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(422, 422));

        jPanel2.setMinimumSize(new java.awt.Dimension(422, 200));

        bodyPanel.setBackground(new java.awt.Color(233, 229, 229));
        bodyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Choose the starting parameters for this model", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 1, 10), new java.awt.Color(102, 102, 102))); // NOI18N
        bodyPanel.setForeground(new java.awt.Color(233, 229, 229));
        bodyPanel.setAutoscrolls(true);
        bodyPanel.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setPreferredSize(new java.awt.Dimension(540, 250));

        panelContainer.setBackground(new java.awt.Color(255, 255, 255));
        panelContainer.setPreferredSize(null);
        panelContainer.setLayout(new javax.swing.BoxLayout(panelContainer, javax.swing.BoxLayout.Y_AXIS));

        panelSeries.setBackground(new java.awt.Color(255, 255, 255));
        panelSeries.setPreferredSize(null);
        panelContainer.add(panelSeries);

        jScrollPane2.setViewportView(panelContainer);

        bodyPanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        retrieveButton.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        retrieveButton.setForeground(new java.awt.Color(102, 102, 102));
        retrieveButton.setText("Retrieve filters");
        retrieveButton.setMaximumSize(new java.awt.Dimension(150, 23));
        retrieveButton.setMinimumSize(new java.awt.Dimension(150, 23));
        retrieveButton.setPreferredSize(new java.awt.Dimension(150, 23));
        retrieveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                retrieveButtonActionPerformed(evt);
            }
        });

        viewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/table.gif"))); // NOI18N
        viewButton.setToolTipText("Tree/Table view");
        viewButton.setMaximumSize(new java.awt.Dimension(40, 40));
        viewButton.setMinimumSize(new java.awt.Dimension(40, 40));
        viewButton.setPreferredSize(new java.awt.Dimension(40, 40));
        viewButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        viewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(viewButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 365, Short.MAX_VALUE)
                .add(retrieveButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(viewButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(retrieveButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(bodyPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(12, 12, 12))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .add(bodyPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        bodyPanel.getAccessibleContext().setAccessibleName("");

        jSplitPane1.setLeftComponent(jPanel2);

        jToolBar1.setRollover(true);
        jToolBar1.setPreferredSize(null);

        jPanel4.setLayout(new java.awt.BorderLayout());

        generateButton.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        generateButton.setForeground(new java.awt.Color(102, 102, 102));
        generateButton.setText("Generate Synthetic Photometry");
        generateButton.setMaximumSize(new java.awt.Dimension(220, 23));
        generateButton.setMinimumSize(new java.awt.Dimension(220, 23));
        generateButton.setPreferredSize(new java.awt.Dimension(220, 23));
        generateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(generateButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(generateButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jSplitPane1.setRightComponent(jPanel3);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
        
    private void retrieveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_retrieveButtonActionPerformed
                
        TsaServerParam tsaServerParam;
        String value;
        
        for (int ct=0; ct < paramOptions.size() ; ct++) {
            tsaServerParam = (TsaServerParam) paramOptions.elementAt(ct);
            
            value = tsaServerParam.getSelectedValue();
            if(!tsaServerParam.getIsCombo()) {
                value = ((JTextField) componentsHashtable.get(tsaServerParam)).getText();
            }
            tsaServerParam.setSelectedValue(value);
            
            if(!tsaServerParam.getMinString().equals("")&&!tsaServerParam.getSelectedValue().equals("")) {
                try {
                    double minValue = (new Double(tsaServerParam.getMinString())).doubleValue();
                    double valueValue = (new Double(tsaServerParam.getSelectedValue())).doubleValue();
                    if(minValue > valueValue){
                        JOptionPane.showMessageDialog(this, "Parameter " + tsaServerParam.getName() + " should be greater than " + minValue + ", overwriting with minimum value");
                        tsaServerParam.setSelectedValue(tsaServerParam.getMinString());
                        //return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            if(!tsaServerParam.getMaxString().equals("")&&!tsaServerParam.getSelectedValue().equals("")) {
                try {
                    double maxValue = (new Double(tsaServerParam.getMaxString())).doubleValue();
                    double valueValue = (new Double(tsaServerParam.getSelectedValue())).doubleValue();
                    if(maxValue < valueValue){
                        JOptionPane.showMessageDialog(this, "Parameter " + tsaServerParam.getName() + " should be minor than " + maxValue+ ", overwriting with maximum value");
                        tsaServerParam.setSelectedValue(tsaServerParam.getMaxString());
                        //return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
        }


        resourcesPanelManager.deleteSSATables();


        retrievedFilters = PFSParser.getPhotometryFilters(paramOptions);
        Vector<Node> nodes = new Vector();

        for(int i=0;i<retrievedFilters.size();i++){
            Node node = new Node(retrievedFilters.get(i));
            node.setName(retrievedFilters.get(i).getId());
            node.setRelatedObject(retrievedFilters.get(i));
            
            Hashtable metadata = new Hashtable();
            Vector identifiers = new Vector();
            
            metadata.put("ID", retrievedFilters.get(i).getId());
            identifiers.add("ID");

            metadata.put("Facility", retrievedFilters.get(i).getFacility());
            identifiers.add("Facility");

            metadata.put("Name", retrievedFilters.get(i).getFilterName());
            identifiers.add("Name");

            metadata.put("Instrument", retrievedFilters.get(i).getInstrument());
            identifiers.add("Instrument");

            metadata.put("Photometric System", retrievedFilters.get(i).getPhotSystem());
            identifiers.add("Photometric System");
            
            metadata.put("Zero Point ("+retrievedFilters.get(i).getZeroPoint().getUnit().toString()+")", retrievedFilters.get(i).getZeroPoint().getValue().doubleValue());
            identifiers.add("Zero Point ("+retrievedFilters.get(i).getZeroPoint().getUnit().toString()+")");

            metadata.put("Wavelength min ("+retrievedFilters.get(i).getWavelengthMin().getUnit().toString()+")", retrievedFilters.get(i).getWavelengthMin().getValue().doubleValue());
            identifiers.add("Wavelength min ("+retrievedFilters.get(i).getWavelengthMin().getUnit().toString()+")");

            metadata.put("Wavelength mean ("+retrievedFilters.get(i).getWavelengthMean().getUnit().toString()+")", retrievedFilters.get(i).getWavelengthMean().getValue().doubleValue());
            identifiers.add("Wavelength mean ("+retrievedFilters.get(i).getWavelengthMean().getUnit().toString()+")");

            metadata.put("Wavelength max ("+retrievedFilters.get(i).getWavelengthMax().getUnit().toString()+")", retrievedFilters.get(i).getWavelengthMax().getValue().doubleValue());
            identifiers.add("Wavelength max ("+retrievedFilters.get(i).getWavelengthMax().getUnit().toString()+")");

            node.setMetadata(metadata);
            node.setMetadata_identifiers(identifiers);

            nodes.add(node);
        }

        resourcesPanelManager.addSSATable("Photometric filters", "Photometric filters"+nodes.toString(), nodes, "");
        //resourcesPanelManager.add

        //System.out.println(filters);
        
    }//GEN-LAST:event_retrieveButtonActionPerformed




    private void generateSyntheticPhotometry(){
        
        Vector<Node> nodes = this.resourcesPanelManager.getSelectedNodes();


        double [] outputWave = new double[nodes.size()];
        double []  outputFlux = new double[nodes.size()];

        double []  outputWaveUpperError = new double[nodes.size()];
        double []  outputWaveLowerError = new double[nodes.size()];
        double []  outputFluxUpperError = new double[nodes.size()];
        double []  outputFluxLowerError = new double[nodes.size()];

        Hashtable metadata = new Hashtable();
        Vector metadata_identifiers = new Vector();

        for(int i=0;i<nodes.size();i++){

            try {

                PhotometryFilter filter = (PhotometryFilter) nodes.get(i).getRelatedObject();

                NDDataSet curve = PFSParser.getTransmissionCurve(filter.getFilterTransmissionCurveUrl());

                Vector<Number> transmission = curve.getTransmissionValues();
                Vector<Number> wave = curve.getWaveValues();

                Spectrum fakeSpectrum = new Spectrum();

                double[] fluxValues = new double[wave.size()];
                double[] waveValues = new double[wave.size()];


                for (int j = 0; j < wave.size(); j++) {
                    fluxValues[j] = transmission.get(j).doubleValue();
                    waveValues[j] = wave.get(j).doubleValue();
                }

                fakeSpectrum.setWaveValues(waveValues);
                fakeSpectrum.setFluxValues(fluxValues);

                //Convert filter to the UNITS that are now in the display
                String [] unitsW = Utils.getDimensionalEquation(curve.getUnitsW());
                fakeSpectrum.setUnits(new Unit(unitsW[1], unitsW[0],"M","1"));
                fakeSpectrum=(new SpectrumConverter()).convertSpectrum(fakeSpectrum, new Unit((String)spectrum.getUnits().getWaveVector().get(1),((Double)spectrum.getUnits().getWaveVector().get(0)).toString(),(String)fakeSpectrum.getUnits().getFluxVector().get(1),((Double)fakeSpectrum.getUnits().getFluxVector().get(0)).toString()));

                IntegralTools integralTools = new IntegralTools(fakeSpectrum, spectrum);
                double result = integralTools.getTransmissionFunction()/integralTools.getIntegratedFlux();


                Spectrum testSpectrum = new Spectrum(spectrum);
                testSpectrum.setFluxValues(testSpectrum.getFluxErrorLower());
                integralTools = new IntegralTools(fakeSpectrum, testSpectrum);
                double resultLower = integralTools.getTransmissionFunction()/integralTools.getIntegratedFlux();


                testSpectrum.setFluxValues(testSpectrum.getFluxErrorUpper());
                integralTools = new IntegralTools(fakeSpectrum, testSpectrum);
                double resultUpper = integralTools.getTransmissionFunction()/integralTools.getIntegratedFlux();


                outputFlux[i] = result;
                outputWave[i] = filter.getWavelengthMean().getValue().doubleValue();
                outputWaveLowerError[i] = (filter.getWidthEff().getValue()).doubleValue()/2;
                outputWaveUpperError[i] = (filter.getWidthEff().getValue()).doubleValue()/2;
                outputFluxLowerError[i] = resultLower;
                outputFluxUpperError[i] = resultUpper;

                metadata.put(filter.getId()+"_WAVE ("+filter.getWavelengthMean().getUnit()+")", filter.getWavelengthMean().getValue().doubleValue());
                metadata.put(filter.getId()+"_FLUX ("+spectrum.getUnitsF()+")", result);
                metadata_identifiers.add(filter.getId()+"_WAVE ("+filter.getWavelengthMean().getUnit()+")");
                metadata_identifiers.add(filter.getId()+"_FLUX ("+spectrum.getUnitsF()+")");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        //create output spectrum

        Spectrum outputSpectrum = new Spectrum();
        Random random = new Random();
        String name = "Synthetic photometry "+random.nextInt(10000);
        outputSpectrum.setTitle(name);
        outputSpectrum.setUrl(name);
        outputSpectrum.setFormat("spectrum/photometry");
        outputSpectrum.setUnitsF(spectrum.getUnitsF());
        outputSpectrum.setUnitsW(((PhotometryFilter) nodes.get(0).getRelatedObject()).getWavelengthMean().getUnit());
        outputSpectrum.setUnits(new Unit(outputSpectrum.getUnitsW(), outputSpectrum.getUnitsF()));

        //set output spectrum metadata

        outputSpectrum.setMetaDataComplete(metadata);
        outputSpectrum.setMetadata_identifiers(metadata_identifiers);

        //populate output spectrum

        outputSpectrum.setWaveValues(outputWave);
        outputSpectrum.setFluxValues(outputFlux);
        outputSpectrum.setWaveErrorsPresent(true);
        outputSpectrum.setWaveErrorLower(outputWaveLowerError);
        outputSpectrum.setWaveErrorUpper(outputWaveUpperError);
        outputSpectrum.setFluxErrorsPresent(true);
        outputSpectrum.setFluxErrorUpper(outputFluxUpperError);
        outputSpectrum.setFluxErrorLower(outputFluxLowerError);

        //add to the previous spectrumSet a new Spectrum trasformed
        
        SpectrumUtils.setParameters(aiospectooldetached, outputSpectrum, name, 0);
        aiospectooldetached.addSpectrum("Synthetic photometry", outputSpectrum,(javax.swing.JTextArea) null/*,checkNode*/);

        
    }





    private void generateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateButtonActionPerformed



            (new Thread() {
                public void run() {

                    setCursor(new Cursor(Cursor.WAIT_CURSOR));

                    generateSyntheticPhotometry();

                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

                }
            }).start();
        
        



    }//GEN-LAST:event_generateButtonActionPerformed

    private boolean table = false;

    private void viewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewButtonActionPerformed

        if (table) {

            viewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/table.gif")));
            resourcesPanelManager.viewTree();
            table = false;

        } else {
            viewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/tree.gif")));
            resourcesPanelManager.viewTable();
            table = true;
        }
    }//GEN-LAST:event_viewButtonActionPerformed
    
    public void getUrl(String url, Vector thisParamOptions) {
         
       /** Create the Url withall the selected parameters and values*/
        String[] url2 = url.split("FORMAT");
        
        String newUrl = url2[0];
         
         TsaServerParam tsaServerParam;
         for (int ct=0; ct < thisParamOptions.size() ; ct++) {
            tsaServerParam = (TsaServerParam) thisParamOptions.elementAt(ct);
            newUrl = newUrl + "&" + tsaServerParam.getName() + "=" + tsaServerParam.getSelectedValue();
         }
         
         ss.setSsaUrl(newUrl);
    }
    
    public SsaServer getTsa() {
        return ss;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bodyPanel;
    private javax.swing.JButton generateButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel panelContainer;
    private javax.swing.JPanel panelSeries;
    private javax.swing.JButton retrieveButton;
    private javax.swing.JButton viewButton;
    // End of variables declaration//GEN-END:variables
    




    public static String lineWrapper(String longLine) {
        
        int maxChar		= 0;
        int count 		= 0;
        
        String lineWrapped 	= "";
        
        if(longLine == null) return lineWrapped;
        if(longLine.equals("")) return lineWrapped;
        
        longLine = subsCharacter(longLine,'>',"&gt;");
        longLine = subsCharacter(longLine,'<',"&lt;");
        
        while(count<longLine.length()-1) {
            maxChar = count + 100;
            
            int endLine = longLine.indexOf("\n",count);
            if (endLine == -1 || (endLine-count) >= 100) {
                if (maxChar<longLine.length()) {
                    while(isSeparator(longLine.charAt(maxChar)) == 0) {
                        maxChar = maxChar-1;
                        
                        if(maxChar == 0) {
                            maxChar = 100;
                            break;
                        }
                    }
                }
            } else {
                maxChar = endLine;
            }
            
            if (maxChar >= longLine.length()) maxChar = longLine.length()-1;
            
            if(isSeparator(longLine.charAt(maxChar)) == 2) {
                lineWrapped = lineWrapped + longLine.substring(count,maxChar) + "\n";
            } else {
                lineWrapped = lineWrapped + longLine.substring(count,maxChar+1) + "\n";
            }
            count = maxChar+1;
        }
        
        return lineWrapped;
    }
    
    public static String subsCharacter(String line,char charToChange,String stringSubs) {
        String newLine = "";
        
        for(int charInt = 0; charInt <line.length(); charInt ++) {
            char thisChar = line.charAt(charInt);
            String thisCharString = (new Character(thisChar)).toString();
            
            if(thisChar == charToChange) {
                newLine = newLine + stringSubs;
            } else {
                newLine = newLine + thisCharString;
            }
        }
        
        return newLine;
    }
    
    public static int isSeparator(char c) {
        if(c == ',' || c == '.' || c == '-' || c == '?' || c == ';' || c == ':' || c == ')') {
            return 1;
        } else if (c == ' ' || c == '\n' || c == '\t' ) {
            return 2;
            
        } else {
            return 0;
        }
    }
    
}
