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
import esavo.vospec.plot.ExtendedPlot;
import esavo.vospec.resourcepanel.*;
import esavo.vospec.spectrum.*;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;


/**
 *
 * @author  ibarbarisi
 */
public class FittingWindow extends javax.swing.JFrame {
    
    private VOSpecDetached AIOSPECTOOLDETACHED;
    
    public ExtendedPlot plot;
    public SpectrumSet spectrumSet;
    public String order;
    //public DefaultTreeModel model;
    
    public static int POLYNOMIAL 	= 0;
    public static int BLACK_BODY 	= 1;
    public static int GAUSSIAN 		= 2;
    public static int EQ 		= 3;
    public static int INT_FLUX 		= 4;
    public static int LORENTZIAN 	= 5;
    public static int PVOIGHT    	= 6;
    public static int BESTFIT    	= 7;
    
    //public Node fittingNode;
    
    public Hashtable nodeHashtable      = new Hashtable();
    
    private double xMinOriginal;
    private double xMaxOriginal;

    public BestFitThread bestFitThread;
    
    public boolean bestFitRunning = false;

    public boolean restart = false;
    
     
    /** Creates new form FittingWindow */
    //public FittingWindow(java.awt.Frame parent, boolean modal) {
      //  super(parent, modal);
    //}
    
    public FittingWindow(ExtendedPlot plot,SpectrumSet spectrumSet, VOSpecDetached aiospectooldetached/*,DefaultTreeModel model*/) {
        //this(null,false);
        this.AIOSPECTOOLDETACHED=aiospectooldetached;
        initComponents();
        initBestFitPanel();
        this.plot = plot;
        this.spectrumSet = spectrumSet;
        this.setTitle("Fitting Window");
        //this.serverListTree = serverListTree;
        //this.model = model;
        //fittingNode = new Node("Fitting Utilities");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - this.getWidth() /2, (screenSize.height / 2)- this.getHeight() /2);
        
    }
    
    public void initBestFitPanel(){
        
        try{
            
            File infoParamTemp = null;
            SsaServerList ssaServerList = new SsaServerList();
            int i=0;
            boolean isTsa = false;
            
            // Create temp file.
            try {
                infoParamTemp = File.createTempFile("infoParamTmp", ".xml");
                infoParamTemp.deleteOnExit();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Node tsaNode = new Node("TSAP Services");
            serverListTree = new JTree(tsaNode);
            //serverListTree.setCellRenderer(new CheckRenderer());
            //serverListTree.addMouseListener(new NodeSelectionListener(serverListTree));
            tsapScrollPane.setViewportView(serverListTree);

            String currentTitle,currentURL;
            Node titleNode,urlNode;

            //Get servers from registry and use only the TSAP ones
            SsaServerList listMixed;

            if(this.AIOSPECTOOLDETACHED.serverList!=null){
                listMixed=this.AIOSPECTOOLDETACHED.serverList;
            }else{
                listMixed = RegistryIngester.getSsaServerList();
            }

            int k = 0;
            for (int j = 0; j < listMixed.ssaServerList.size(); j++) {
                if (listMixed.getSsaServer(j).getType() == SsaServer.TSAP) {
                    ssaServerList.addSsaServer(k, listMixed.getSsaServer(j));
                    k++;
                }
            }

            //Add servers to the tree
            for (int z=0;z< ssaServerList.getSsaServerList().size();z++) {

                    SsaServer server = (SsaServer) ssaServerList.getSsaServer(z);

                    if(server.getType() == SsaServer.TSAP){
                        currentTitle = (String) server.getSsaName();
                        titleNode = new Node(currentTitle);
                        tsaNode.add(titleNode);
                        nodeHashtable.put(titleNode, server);
                    }
             }

            serverListTree.getSelectionModel().setSelectionMode(
                    TreeSelectionModel.SINGLE_TREE_SELECTION
                    );
            ((DefaultTreeModel) serverListTree.getModel()).reload();
            tsapScrollPane.setViewportView(serverListTree);
            
            
        }catch(Exception e){
            e.printStackTrace();
        }
                 
    }
    
    public boolean exists(String URLName){
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    SsaServer finalssaServer;
    TsaServerParser tsp;
    SsaServer ssaServer;

    public void addButtonBestFit(){

        if(bestFitRunning){
            JOptionPane.showMessageDialog(this, "Please open another fitting window to perform simultaneous queries");
            return;
        }

        if(restart){
            tsapButton.setText("Initiate");
            initBestFitPanel();
            restart=false;
            return;
        }
            
        
        Node node  = (Node) serverListTree.getLastSelectedPathComponent();
        ssaServer  = (SsaServer) nodeHashtable.get(node);

        if(ssaServer==null){
            JOptionPane.showMessageDialog(this, "Please choose a TSA server to perform the fitting");
            return;
        }

        
        finalssaServer = new SsaServer(ssaServer);
        
        try {

            if(tsp!=null)tsp.dispose();
            
            tsp = new TsaServerParser(this,finalssaServer,true,false);
            
            tsp.setCheckMaxMin();
            tsp.setVisible(true);
                        
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("Url exception");
        }
    }

    public void launchBestFit(){

            finalssaServer      = tsp.getTsa();
            Vector paramOptions = tsp.paramOptions;

            Vector dataVector   = plot.getPointsRealOrTSAP(false);
            Spectrum spectrum   = SpectrumUtils.getSpectrum(dataVector);
            spectrum.setFormat("application/fits");
            spectrum.setUnits(new Unit(this.AIOSPECTOOLDETACHED.waveChoice.getSelectedItem().toString(),this.AIOSPECTOOLDETACHED.fluxChoice.getSelectedItem().toString()));

            SpectrumUtils.checkVelocity(this.AIOSPECTOOLDETACHED,spectrum);

            String waveChoice   = AIOSPECTOOLDETACHED.getWaveChoice();
            String fluxChoice   = AIOSPECTOOLDETACHED.getFluxChoice();
            Unit   unit         = new Unit(waveChoice,fluxChoice);

            spectrum.setUnits(unit);

            //Launch Threads BestFit
            bestFitThread = new BestFitThread(this,"",this.AIOSPECTOOLDETACHED,spectrum,finalssaServer,paramOptions,tsp,ssaServer);
            //SwingUtilities.invokeLater(bestFitThread);
            this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            
            bestFitRunning = true;
            
            bestFitThread.start();
            

    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        tsapPanel = new javax.swing.JPanel();
        tsapScrollPane = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        tsapButton = new javax.swing.JButton();
        tsapLabel = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        cancelButton = new javax.swing.JButton();
        polynomialPanel = new javax.swing.JPanel();
        polynomialOrderLabel = new javax.swing.JLabel();
        orderTextField = new javax.swing.JTextField();
        infoFittingScrollPanel = new javax.swing.JScrollPane();
        JTextArea0 = new javax.swing.JTextArea();
        JButton0 = new javax.swing.JButton();
        gaussianPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        JTextArea3 = new javax.swing.JTextArea();
        jButton3 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        pVoightPanel = new javax.swing.JPanel();
        pVoightButton = new javax.swing.JButton();
        pVoightScrollPane = new javax.swing.JScrollPane();
        pVoightTextArea = new javax.swing.JTextArea();
        pVoightLabel = new javax.swing.JLabel();
        lorentzianPanel1 = new javax.swing.JPanel();
        lorentzianScrollPane = new javax.swing.JScrollPane();
        lorentzianTextArea = new javax.swing.JTextArea();
        lorentzianButton = new javax.swing.JButton();
        lorentzianLabel = new javax.swing.JLabel();
        blackBoxPanel = new javax.swing.JPanel();
        blackInfoFittinScrollPanel = new javax.swing.JScrollPane();
        JTextArea1 = new javax.swing.JTextArea();
        JButton1 = new javax.swing.JButton();
        blackFitLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        temperatureField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Fitting Window");
        setMinimumSize(new java.awt.Dimension(422, 301));

        jTabbedPane1.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(422, 301));
        jTabbedPane1.setName("VoSpec Fitting Window"); // NOI18N
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(418, 266));
        jTabbedPane1.setRequestFocusEnabled(false);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setEnabled(false);
        tsapScrollPane.setViewportView(jTextArea1);

        tsapButton.setText("Initiate");
        tsapButton.setPreferredSize(new java.awt.Dimension(66, 24));
        tsapButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tsapButtongenerateButtonActionPerformed2(evt);
            }
        });

        tsapLabel.setText("Fit to TSAP Service");

        jProgressBar1.setFont(new java.awt.Font("Luxi Sans", 0, 10));

        cancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/esavo/vospec/images/close_cross.gif"))); // NOI18N
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout tsapPanelLayout = new org.jdesktop.layout.GroupLayout(tsapPanel);
        tsapPanel.setLayout(tsapPanelLayout);
        tsapPanelLayout.setHorizontalGroup(
            tsapPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, tsapPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(tsapButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jProgressBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 249, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(cancelButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .add(tsapPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(tsapLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(282, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, tsapScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
        );
        tsapPanelLayout.setVerticalGroup(
            tsapPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tsapPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(tsapLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tsapScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tsapPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(cancelButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jProgressBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(tsapButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("TSAP Best Fit", tsapPanel);

        polynomialPanel.setPreferredSize(new java.awt.Dimension(419, 266));

        polynomialOrderLabel.setText("Polynomial order");

        orderTextField.setText("2");
        orderTextField.setPreferredSize(new java.awt.Dimension(60, 20));

        infoFittingScrollPanel.setPreferredSize(new java.awt.Dimension(222, 72));

        JTextArea0.setEditable(false);
        JTextArea0.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        JTextArea0.setPreferredSize(null);
        infoFittingScrollPanel.setViewportView(JTextArea0);

        JButton0.setText("Generate");
        JButton0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateButtonActionPerformed0(evt);
            }
        });

        org.jdesktop.layout.GroupLayout polynomialPanelLayout = new org.jdesktop.layout.GroupLayout(polynomialPanel);
        polynomialPanel.setLayout(polynomialPanelLayout);
        polynomialPanelLayout.setHorizontalGroup(
            polynomialPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(polynomialPanelLayout.createSequentialGroup()
                .add(polynomialPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(polynomialPanelLayout.createSequentialGroup()
                        .add(26, 26, 26)
                        .add(polynomialOrderLabel)
                        .add(18, 18, 18)
                        .add(orderTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(polynomialPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(JButton0)))
                .addContainerGap())
            .add(infoFittingScrollPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
        );
        polynomialPanelLayout.setVerticalGroup(
            polynomialPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(polynomialPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(polynomialPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(polynomialOrderLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(orderTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(infoFittingScrollPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(JButton0)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Polynomial", polynomialPanel);

        jScrollPane2.setViewportView(JTextArea3);

        jButton3.setText("Generate");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3generateButtonActionPerformed2(evt);
            }
        });

        jLabel4.setText("Gaussian");

        org.jdesktop.layout.GroupLayout gaussianPanelLayout = new org.jdesktop.layout.GroupLayout(gaussianPanel);
        gaussianPanel.setLayout(gaussianPanelLayout);
        gaussianPanelLayout.setHorizontalGroup(
            gaussianPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gaussianPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jButton3)
                .addContainerGap(330, Short.MAX_VALUE))
            .add(gaussianPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(282, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
        );
        gaussianPanelLayout.setVerticalGroup(
            gaussianPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gaussianPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton3)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Gaussian", gaussianPanel);

        pVoightPanel.setPreferredSize(new java.awt.Dimension(418, 255));

        pVoightButton.setText("Generate");
        pVoightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pVoightButtongenerateButtonActionPerformed2(evt);
            }
        });

        pVoightTextArea.setEditable(false);
        pVoightTextArea.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        pVoightScrollPane.setViewportView(pVoightTextArea);

        pVoightLabel.setText("pVoight");

        org.jdesktop.layout.GroupLayout pVoightPanelLayout = new org.jdesktop.layout.GroupLayout(pVoightPanel);
        pVoightPanel.setLayout(pVoightPanelLayout);
        pVoightPanelLayout.setHorizontalGroup(
            pVoightPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pVoightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(pVoightLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(pVoightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(pVoightButton)
                .addContainerGap(330, Short.MAX_VALUE))
            .add(pVoightScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
        );
        pVoightPanelLayout.setVerticalGroup(
            pVoightPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pVoightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(pVoightLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pVoightScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pVoightButton)
                .addContainerGap())
        );

        jTabbedPane1.addTab("pVoight", pVoightPanel);

        lorentzianTextArea.setEditable(false);
        lorentzianTextArea.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        lorentzianScrollPane.setViewportView(lorentzianTextArea);

        lorentzianButton.setText("Generate");
        lorentzianButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lorentzianButtongenerateButtonActionPerformed2(evt);
            }
        });

        lorentzianLabel.setText("Lorentzian");

        org.jdesktop.layout.GroupLayout lorentzianPanel1Layout = new org.jdesktop.layout.GroupLayout(lorentzianPanel1);
        lorentzianPanel1.setLayout(lorentzianPanel1Layout);
        lorentzianPanel1Layout.setHorizontalGroup(
            lorentzianPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lorentzianPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(lorentzianLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(282, Short.MAX_VALUE))
            .add(lorentzianPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(lorentzianButton)
                .addContainerGap(330, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, lorentzianScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
        );
        lorentzianPanel1Layout.setVerticalGroup(
            lorentzianPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lorentzianPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(lorentzianLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lorentzianScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lorentzianButton)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Lorentzian", lorentzianPanel1);

        blackBoxPanel.setPreferredSize(new java.awt.Dimension(419, 468));

        JTextArea1.setEditable(false);
        JTextArea1.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        blackInfoFittinScrollPanel.setViewportView(JTextArea1);

        JButton1.setText("Generate");
        JButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateButtonActionPerformed1(evt);
            }
        });

        blackFitLabel.setText("Black Body Fitting");

        jLabel2.setText("Temperature:");

        jButton1.setText("Guess Temperature");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setText("K");

        org.jdesktop.layout.GroupLayout blackBoxPanelLayout = new org.jdesktop.layout.GroupLayout(blackBoxPanel);
        blackBoxPanel.setLayout(blackBoxPanelLayout);
        blackBoxPanelLayout.setHorizontalGroup(
            blackBoxPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(blackBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(1, 1, 1)
                .add(temperatureField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 208, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .add(blackBoxPanelLayout.createSequentialGroup()
                .add(22, 22, 22)
                .add(blackFitLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(272, Short.MAX_VALUE))
            .add(blackBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(JButton1)
                .addContainerGap(330, Short.MAX_VALUE))
            .add(blackInfoFittinScrollPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
        );
        blackBoxPanelLayout.setVerticalGroup(
            blackBoxPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(blackBoxPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(blackFitLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(blackBoxPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(temperatureField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(blackInfoFittinScrollPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(JButton1)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Black Body", blackBoxPanel);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void pVoightButtongenerateButtonActionPerformed2(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pVoightButtongenerateButtonActionPerformed2
// TODO add your handling code here:
        generateAction(PVOIGHT,pVoightTextArea);
    }//GEN-LAST:event_pVoightButtongenerateButtonActionPerformed2
    
    private void lorentzianButtongenerateButtonActionPerformed2(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lorentzianButtongenerateButtonActionPerformed2
// TODO add your handling code here: 
        generateAction(LORENTZIAN,lorentzianTextArea);
    }//GEN-LAST:event_lorentzianButtongenerateButtonActionPerformed2
    
    public Spectrum getXDataYData() {
        
        
        Vector dataToFit = AIOSPECTOOLDETACHED.plot.getPoints();
        int numberOfPoints = dataToFit.size();
        double[] xData = new double[numberOfPoints];
        double[] yData = new double[numberOfPoints];
        
        boolean logX = true;
        boolean logY = true;
        
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
        
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Vector dataVector = plot.getPoints();
        //Set the values selected on the AioSpecToolDetached
        String waveChoice = AIOSPECTOOLDETACHED.getWaveChoice();
        String fluxChoice = AIOSPECTOOLDETACHED.getFluxChoice();
        
        Unit unit = new Unit(waveChoice,fluxChoice);
        
        BlackBodyFitting spectrum = new BlackBodyFitting(dataVector,unit,plot.getXLog(),plot.getYLog());
        spectrum.setRedShift(AIOSPECTOOLDETACHED.getRedShift());
        spectrum.setRow(-1);
        
        double[] parameter = spectrum.calculateGuess();
        
        DecimalFormat 	decimalFormat 	= new DecimalFormat("#.##");
        String  	tempString 	= decimalFormat.format(parameter[0]);
        temperatureField.setText(tempString);
        
        // Add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed
        
    private void generateButtonActionPerformed0(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateButtonActionPerformed0
        generateAction(POLYNOMIAL,JTextArea0);
    }//GEN-LAST:event_generateButtonActionPerformed0
    
    private void generateButtonActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateButtonActionPerformed1
        generateAction(BLACK_BODY,JTextArea1);
    }//GEN-LAST:event_generateButtonActionPerformed1

    private void tsapButtongenerateButtonActionPerformed2(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tsapButtongenerateButtonActionPerformed2
        this.addButtonBestFit();
}//GEN-LAST:event_tsapButtongenerateButtonActionPerformed2

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.bestFitThread.stopBestFit();
        this.bestFitThread.interrupt();
}//GEN-LAST:event_cancelButtonActionPerformed

    private void jButton3generateButtonActionPerformed2(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3generateButtonActionPerformed2
        generateAction(GAUSSIAN,JTextArea3);
}//GEN-LAST:event_jButton3generateButtonActionPerformed2
    
    
    /*
    public void setAioSpecToolDetached(AioSpecToolDetached aioSpecToolDetached) {
        this.AIOSPECTOOLDETACHED = aioSpecToolDetached;
    }
     * */
    
    
    private void generateAction(int type,javax.swing.JTextArea jTextArea) {
        
        setWaitCursor();
        
        this.AIOSPECTOOLDETACHED.createNewSpectraViewer();
        
        Spectrum spectrum = null;
        try {
            spectrum = fitting(type);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        this.AIOSPECTOOLDETACHED.addSpectrum("Fitting Utilities",spectrum,jTextArea/*,fittingNode*/);
        
        setDefaultCursor();
        
    }
  
    public Spectrum fitting(int type) throws Exception {


        Vector dataVector = plot.getPoints();
        //Set the values selected on the AIOSPECTOOLDETACHED
        String waveChoice = AIOSPECTOOLDETACHED.getWaveChoice();
        String fluxChoice = AIOSPECTOOLDETACHED.getFluxChoice();
//        
        Unit unit = new Unit(waveChoice, fluxChoice);

        Spectrum spectrum = new Spectrum();
        String title = "";

        try {
            if (type == POLYNOMIAL) {

                order = orderTextField.getText();
                int orderInt = Integer.parseInt(order);

                spectrum = new PolynomialFitting(dataVector, plot.getXLog(), plot.getYLog(), orderInt);
                title = "PolynomialFitting, order " + order;

                spectrum.setUnits(unit);

            } else if (type == BLACK_BODY) {

                title = "BlackBodyFitting";

                spectrum = new BlackBodyFitting(dataVector, unit, plot.getXLog(), plot.getYLog());

                spectrum.setBB(true);

                if (!temperatureField.getText().equals("")) {
                    double temp = 0;
                    try {
                        temp = (new Double(temperatureField.getText())).doubleValue();
                        ((BlackBodyFitting) spectrum).setTemperature(temp);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "The temperature will not be used as it was not a correct number");
                    }
                }

            } else if (type == GAUSSIAN) {

                spectrum = new GaussianFitting(dataVector, unit, plot.getXLog(), plot.getYLog());

                title = "GaussianFitting";

            } else if (type == LORENTZIAN) {

                spectrum = new LorentzianFitting(dataVector, unit, plot.getXLog(), plot.getYLog());
                //spectrum = new LorentzianFitting(getXDataYData(),unit,plot.getXLog(),plot.getYLog());

                title = "LorentzianFitting";

            } else if (type == PVOIGHT) {

                spectrum = new PVoightFitting(dataVector, unit, plot.getXLog(), plot.getYLog());

                title = "pVoightFitting";
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        SpectrumUtils.setParameters(this.AIOSPECTOOLDETACHED, spectrum, title, this.AIOSPECTOOLDETACHED.mathematicMethodExecution);
        this.AIOSPECTOOLDETACHED.mathematicMethodExecution++;

//        spectrum.setUnits(unit);
//        //spectrum.setTitle(getXDataYData().getTitle()+ title);
//        spectrum.setTitle(title);
//        spectrum.setRedShift(AIOSPECTOOLDETACHED.getRedShift());
//        // to be displayed with the others
//        spectrum.setSelected(true);
//        
//        SpectrumSet sv = new SpectrumSet();
//        sv.addSpectrum(0, spectrum);
//        //aioSpecToolDetached.tableModel = sv.refreshTableModel(title);
//        
//        
//        //add to the previous spectrumSet a new Spectrum trasformed
//        this.AIOSPECTOOLDETACHED.spectrumSet.addSpectrumSet(sv);
//        
//        if(this.AIOSPECTOOLDETACHED.remoteSpectrumSet == null) this.AIOSPECTOOLDETACHED.remoteSpectrumSet = new SpectrumSet();
//        this.AIOSPECTOOLDETACHED.remoteSpectrumSet.addSpectrumSet(sv);
//        
//        spectrum.setRow(this.AIOSPECTOOLDETACHED.spectrumSet.getSpectrumSet().size() - 1);
//                
        return spectrum;
    }

    public void setWaitCursor() {
        Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
        setCursor(hourglassCursor);
        AIOSPECTOOLDETACHED.setCursor(hourglassCursor);
    }

    public void setDefaultCursor() {
        Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        setCursor(normalCursor);
        AIOSPECTOOLDETACHED.setCursor(normalCursor);
    }
    
    
    /**
     * @param args the command line arguments
     */
    //public static void main(String args[]) {
      //  new FittingWindow(new javax.swing.JFrame(), true).show();
    //}
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton JButton0;
    public javax.swing.JButton JButton1;
    public javax.swing.JTextArea JTextArea0;
    public javax.swing.JTextArea JTextArea1;
    public javax.swing.JTextArea JTextArea3;
    public javax.swing.JPanel blackBoxPanel;
    public javax.swing.JLabel blackFitLabel;
    public javax.swing.JScrollPane blackInfoFittinScrollPanel;
    public javax.swing.JButton cancelButton;
    public javax.swing.JPanel gaussianPanel;
    public javax.swing.JScrollPane infoFittingScrollPanel;
    public javax.swing.JButton jButton1;
    public javax.swing.JButton jButton3;
    public javax.swing.JLabel jLabel2;
    public javax.swing.JLabel jLabel3;
    public javax.swing.JLabel jLabel4;
    public javax.swing.JProgressBar jProgressBar1;
    public javax.swing.JScrollPane jScrollPane2;
    public javax.swing.JTabbedPane jTabbedPane1;
    public javax.swing.JTextArea jTextArea1;
    public javax.swing.JButton lorentzianButton;
    public javax.swing.JLabel lorentzianLabel;
    public javax.swing.JPanel lorentzianPanel1;
    public javax.swing.JScrollPane lorentzianScrollPane;
    public javax.swing.JTextArea lorentzianTextArea;
    public javax.swing.JTextField orderTextField;
    public javax.swing.JButton pVoightButton;
    public javax.swing.JLabel pVoightLabel;
    public javax.swing.JPanel pVoightPanel;
    public javax.swing.JScrollPane pVoightScrollPane;
    public javax.swing.JTextArea pVoightTextArea;
    public javax.swing.JLabel polynomialOrderLabel;
    public javax.swing.JPanel polynomialPanel;
    public javax.swing.JTextField temperatureField;
    public javax.swing.JButton tsapButton;
    public javax.swing.JLabel tsapLabel;
    public javax.swing.JPanel tsapPanel;
    public javax.swing.JScrollPane tsapScrollPane;
    // End of variables declaration//GEN-END:variables
    public javax.swing.JTree serverListTree = new javax.swing.JTree();
    //public JScrollPane jScrollPane3 = new javax.swing.JScrollPane();
    
    
}
