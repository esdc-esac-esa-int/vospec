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

import esavo.vospec.math.*;
import esavo.vospec.resourcepanel.*;
import esavo.vospec.spectrum.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.*;



/**
 *
 * @author  ibarbarisi
 */
public class VOSpeculator extends JFrame implements DropTargetListener {
    
    
    /** Creates new form VOSpeculator */
    public VOSpeculator() {
        initComponents();
        listElements        = new Vector(); //original Vector
        listElement2        = new Vector();

        DropTarget dt = new DropTarget(operationsArea, this);
        /*operationsArea.setTransferHandler(new TransferHandler(){

            @Override
            public boolean canImport(TransferHandler.TransferSupport support){
               return true;
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport support){

                System.out.println("importing");
                try {
                    

                    if (support.getTransferable().isDataFlavorSupported(new DataFlavor(TreePath.class, "Tree Path"))) {

                        TreePath p = (TreePath) support.getTransferable().getTransferData(new DataFlavor(TreePath.class, "Tree Path"));
                        Node node = (Node) p.getLastPathComponent();


                        Spectrum spectrumToAdd = node.getSpectrum();
                        spectrumToAdd.setRealData(true);
                        listElements.add(spectrumToAdd);
                        operationsArea.append(node.getSpectrum().getTitle() + "\n");
                    
                    } else {

                        JTable table = (JTable) support.getTransferable().getTransferData(new DataFlavor(JTable.class,"JTable"));
                        Node node=((SpectraTableModel)table.getModel()).getNode(table.getSelectedRow());

                        Spectrum spectrumToAdd = node.getSpectrum();
                        spectrumToAdd.setRealData(true);
                        listElements.add(spectrumToAdd);
                        operationsArea.append(node.getSpectrum().getTitle() + "\n");

                    }




                } catch (UnsupportedFlavorException ex) {
                    System.out.println("unsupportedflavor");
                    Logger.getLogger(VOSpeculator.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    System.out.println("ioexception");
                    Logger.getLogger(VOSpeculator.class.getName()).log(Level.SEVERE, null, ex);
                }


                return true;

            }



        });*/

        this.setTitle("VOSpec Spectral Calculator");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - this.getWidth() /2, (screenSize.height / 2)- this.getHeight() /2);
        
    }
   
    public void dragEnter(DropTargetDragEvent dtde) {
        //System.out.println("Drag Enter");
    }

    public void dragExit(DropTargetEvent dte) {
        //System.out.println("Source: " + dte.getSource());
        //System.out.println("Drag Exit");
    }

    public void dragOver(DropTargetDragEvent dtde) {
        //System.out.println("Drag Over");
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
        //System.out.println("Drop Action Changed");
    }

    public void drop(DropTargetDropEvent dtde) {

        try {
            Transferable tr = dtde.getTransferable();
            DataFlavor[] flavors = tr.getTransferDataFlavors();

            for (int i = 0; i < flavors.length; i++) {
                if (tr.isDataFlavorSupported(flavors[i])) {

                    if(flavors[i].equals(TransferableTable.TABLE_FLAVOR)){

                        JOptionPane.showMessageDialog(this, "Dragging from Table not supported - try Tree instead");

                        /*
                        JXTable table = (JXTable) tr.getTransferData(flavors[i]);
                        Node node=((SpectraTableModel)table.getModel()).getNode(table.getSelectedRow());

                        Spectrum spectrumToAdd = node.getSpectrum();
                        spectrumToAdd.setRealData(true);
                        listElements.add(spectrumToAdd);
                        operationsArea.append(node.getSpectrum().getTitle() + "\n");
                         * */

                    }else{

                        dtde.acceptDrop(dtde.getDropAction());

                        //TreePath p = (TreePath) tr.getTransferData(flavors[i]);

                        //Node node = (Node) p.getLastPathComponent();

                        //Spectrum spectrumToAdd = (Spectrum)node.getRelatedObject();

                        Spectrum spectrumToAdd=(Spectrum) tr.getTransferData(flavors[i]);

                        spectrumToAdd.setRealData(true);

                        //listElements.add(node.getSpectrum());
                        listElements.add(spectrumToAdd);

                        operationsArea.append(spectrumToAdd.getTitle()+"\n");

                        dtde.dropComplete(true);
                        return;
                    }
                }
            }
            //dtde.rejectDrop();
        } catch (Exception e) {
            operationsArea.append("Spectrum Section"+"\n");
            e.printStackTrace();
            //dtde.rejectDrop();
        }


    }
        
        
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        operationsArea = new javax.swing.JTextArea();
        additionButton = new javax.swing.JButton();
        subtractionsButton = new javax.swing.JButton();
        productButton = new javax.swing.JButton();
        divisionButton = new javax.swing.JButton();
        equalButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        screenArea = new javax.swing.JTextArea();
        convolutionButton = new javax.swing.JButton();
        constantTextField = new javax.swing.JTextField();
        constantLabel = new javax.swing.JLabel();
        resetButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "VOSpec Spectral Calculator", javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 24), new java.awt.Color(51, 0, 255)));
        jPanel1.setForeground(new java.awt.Color(51, 51, 255));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Drop Spectra here - Operational Area"));
        jScrollPane1.setEnabled(false);
        operationsArea.setColumns(20);
        operationsArea.setEditable(false);
        operationsArea.setForeground(new java.awt.Color(102, 102, 102));
        operationsArea.setRows(5);
        jScrollPane1.setViewportView(operationsArea);

        additionButton.setFont(new java.awt.Font("Dialog", 1, 18));
        additionButton.setToolTipText("Addition");
        additionButton.setLabel("+");
        additionButton.setPreferredSize(new java.awt.Dimension(60, 50));
        additionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                additionButtonActionPerformed(evt);
            }
        });

        subtractionsButton.setFont(new java.awt.Font("Dialog", 1, 18));
        subtractionsButton.setText("-");
        subtractionsButton.setToolTipText("Subtraction ");
        subtractionsButton.setPreferredSize(new java.awt.Dimension(60, 50));
        subtractionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subtractionsButtonActionPerformed(evt);
            }
        });

        productButton.setFont(new java.awt.Font("Dialog", 1, 18));
        productButton.setText("x");
        productButton.setToolTipText("Multiplication");
        productButton.setPreferredSize(new java.awt.Dimension(60, 50));
        productButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productButtonActionPerformed(evt);
            }
        });

        divisionButton.setFont(new java.awt.Font("Dialog", 1, 18));
        divisionButton.setText("/");
        divisionButton.setToolTipText("Division");
        divisionButton.setPreferredSize(new java.awt.Dimension(60, 50));
        divisionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                divisionButtonActionPerformed(evt);
            }
        });

        equalButton.setFont(new java.awt.Font("Dialog", 1, 18));
        equalButton.setForeground(new java.awt.Color(102, 102, 102));
        equalButton.setText("=");
        equalButton.setToolTipText("Result");
        equalButton.setPreferredSize(new java.awt.Dimension(83, 53));
        equalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                equalButtonActionPerformed(evt);
            }
        });

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("History Track"));
        screenArea.setBackground(new java.awt.Color(249, 241, 221));
        screenArea.setColumns(20);
        screenArea.setEditable(false);
        screenArea.setForeground(new java.awt.Color(102, 102, 102));
        screenArea.setRows(5);
        jScrollPane2.setViewportView(screenArea);

        convolutionButton.setFont(new java.awt.Font("Dialog", 1, 18));
        convolutionButton.setText("*");
        convolutionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                convolutionButtonActionPerformed(evt);
            }
        });

        constantTextField.setText("0");

        constantLabel.setFont(new java.awt.Font("Dialog", 1, 10));
        constantLabel.setForeground(new java.awt.Color(102, 102, 102));
        constantLabel.setText("Constant");

        resetButton.setForeground(new java.awt.Color(102, 102, 102));
        resetButton.setText("Reset");
        resetButton.setPreferredSize(new java.awt.Dimension(93, 53));
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Dialog", 1, 10));
        jButton1.setForeground(new java.awt.Color(102, 102, 102));
        jButton1.setText("Enter");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(constantLabel)
                                .add(additionButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(jPanel1Layout.createSequentialGroup()
                                    .add(subtractionsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(productButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(divisionButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(constantTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 157, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jPanel1Layout.createSequentialGroup()
                                    .add(convolutionButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 51, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 47, Short.MAX_VALUE)
                                    .add(equalButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(21, 21, 21))
                                .add(jPanel1Layout.createSequentialGroup()
                                    .add(jButton1)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 127, Short.MAX_VALUE))))
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 459, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 453, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(379, Short.MAX_VALUE)
                .add(resetButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(21, 21, 21))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 232, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(additionButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(subtractionsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(productButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(divisionButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(convolutionButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(equalButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 47, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(38, 38, 38)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(constantLabel)
                    .add(constantTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton1))
                .add(15, 15, 15)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 148, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(resetButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(169, 169, 169))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 607, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
    }// </editor-fold>
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        operationsArea.append(constantTextField.getText()+"\n");
    }                                        
    
    private void convolutionButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        operationsArea.append(" * "+"\n");
        listElements.add("*");
    }                                                 
    
    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {                                            
        reset();
        
    }
    private void reset() {
        operationsArea.setText("");
//      screenArea.setText("");
        constantTextField.setText("0");
        spectraVec          = new Vector();
        listElements        = new Vector();
        listElement2        = new Vector();
        trackVector         = new Vector();
        finalElements       = new Vector();
        operandum           = false;

        this.metadata = new Hashtable();
        this.title = new String();

    }
    
    private void equalButtonActionPerformed(java.awt.event.ActionEvent evt) {                                            
        
        setWaitCursor();
 //       checkIfVelocity();
        checkIfMultipleSpectra();
        performOperations();
        reset();
        setDefaultCursor();
        
        
    }                                           
    
    public void checkIfVelocity(){
        
        spectrumConverter = new SpectrumConverter();
        
        if(this.aioSpecToolDetached.isInVelocitySpace())  spectrumConverter.setWaveToVel(this.aioSpecToolDetached.getRefWaveValue());
        
        System.out.println("In check if velocity of VOSpec Calculator. refWave = "+this.aioSpecToolDetached.getRefWaveValue());
        
    }
    
//    public void setWaitCursor() {
//        Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
//        setCursor(hourglassCursor);
//    }
    
    
//    public void setDefaultCursor() {
//        Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
//        setCursor(normalCursor);
//    }
    
    
    public void setWaitCursor() {
        Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
        setCursor(hourglassCursor);
        this.setCursor(hourglassCursor);
        aioSpecToolDetached.setCursor(hourglassCursor);
    }
    
    public void setDefaultCursor() {
        Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        setCursor(normalCursor);
        this.setCursor(normalCursor);
        aioSpecToolDetached.setCursor(normalCursor);
    }
    
    
    public void checkIfMultipleSpectra(){
        
        checkIfVelocity();
        
        spectraVec          = new Vector();
        operandum           = false;
        trackVector         = new Vector();
        
        //Vector containg spectrum operandum spectrum operandum
        for (int i=0;i<listElements.size();i++){
            
            String element = (String) listElements.elementAt(i).toString();
            
            //check if spectrum or operandum (spectrum=true, operandum=false
            type = chooseTypeElement(element);
            
            if (type){
                Spectrum s = (Spectrum) listElements.elementAt(i);
                spectraVec.add(s);
                
            }else{
                operandum = true;
                //if it has behind one or more spectra when it arrives to operandum it sums them
                if(spectraVec.size()>1) {
                    //sum spectra in spectra Vector
                    sumSpectra(spectraVec);
                }else{
                    trackVector.add("Spectrum");
                    listElement2.add(spectraVec.elementAt(0));
                }
                //re-initialize spectraVec whenever it finds an operandum
                spectraVec = new Vector();
                trackVector.add(element);
                listElement2.add(element);
            }
            
        }
        
        //if there are many spectra at the end
        if(spectraVec.size()>1 ){
            sumSpectra(spectraVec);
        }
        
        
        //If there is one last single spectra and operandum in the Vector
        if(spectraVec.size()==1 && (operandum)){
            listElement2.add(spectraVec.elementAt(0));
            trackVector.add("Spectrum");
        }
        
        //if there are many spectra and no operandum draw sum
        if(spectraVec.size()>1 && (!operandum)){
            sumSpectra(spectraVec);
            Spectrum s = (Spectrum) listElement2.elementAt(0);
            operation = "MULTIPLE SPECTRA";
            title = "Sum Multiple Spectra";
            s.setTitle(title);
            s.getMetaData().clear();
            s.addMetaData(" TITLE ", title);
            
            SpectrumUtils.checkVelocity(this.aioSpecToolDetached, s);
            
            SpectrumSet spectrumSetTmp = new SpectrumSet();
            spectrumSetTmp.addSpectrum(0,s);
            
            //add to the previous spectrumSet a new Spectrum trasformed
            this.aioSpecToolDetached.spectrumSet.addSpectrumSet(spectrumSetTmp);
            s.setRow(this.aioSpecToolDetached.spectrumSet.getSpectrumSet().size() - 1);
            
            this.aioSpecToolDetached.addSpectrum("MULTIPLE SPECTRA", s,(javax.swing.JTextArea) null/*,checkNode*/);
            screenArea.append("SUM Multiple Spectra "+"\n");
            
        }
    }
    
    
     
    public void performOperations(){
        
        try{
            //String out ="";
            //String operation ="";
            int ct=-1;
            finalElements = new Vector();
            
            Spectrum spectrum1 = new Spectrum();
            Spectrum spectrumModified = new Spectrum();
            
            boolean disjointSpectra = false;
            
            checkIfVelocity();
            
            for (int i=0;i<listElement2.size();i++){
                
                
                String element = (String) listElement2.elementAt(i).toString();
                
                boolean type = chooseTypeElement(element);
                
                String text = constantTextField.getText();
                
                // System.out.println("listElements size = " +listElements.size());
                
                //If it's operandum
                if(!type){
                    //check if spectrum or operandum (spectrum=true, operandum=false)
                    String operando = getTypeOperandum(element);
                    
                    
                    if (operando.equals("+")){
                        
                        spectrum1 = new Spectrum();
                        spectrumModified = new Spectrum();
                        operation = " ADD ";
                        
                        
                        if(i==1){
                            spectrum1 = (Spectrum)listElement2.elementAt(i-1);
                        }else{
                            spectrum1 = (Spectrum)finalElements.elementAt(ct);
                        }                        
                        
                        
                       setUnits();
                        
                        
                        new Thread(spectrum1).start();
                        while(spectrum1.getToWait()) Thread.sleep(500);                      
                        
                        //Spectrum spectrumConverted1 = (new SpectrumConverter()).convertSpectrum(spectrum1,this.finalUnits);
//                        checkIfVelocity();
                        
                        Spectrum spectrumConverted1 = spectrumConverter.convertSpectrum(spectrum1,this.finalUnits);                        
                        SpectrumUtils.checkVelocity(this.aioSpecToolDetached,spectrumConverted1);
                        
                        System.out.println("ref wavelength ="+spectrumConverter.refWave);
                        
                        //there is NOT spectrum after the operand --> the operation in between spec and constant
                        if(i+1 > listElement2.size()-1){
                            
                            double constant = Double.valueOf(text).doubleValue();
                            
                            Arithmetics arithmetics = new Arithmetics(spectrumConverted1, constant);
                            spectrumModified = arithmetics.sumConstant();
                            spectrumModified.setRealData(true);
                            
                            title = "SUM( "+spectrum1.getTitle() + ", "+constant+" )";
                            
                            spectrumModified.setTitle(spectrum1.getTitle()+" ADD "+constant);
                            spectrumModified.setUrl(spectrum1.getUrl()+" ADD "+constant);
                            
                        } 
                        //there is a spectrum after the operand --> the operation in between spec and spec
                        else{
                            
//                            checkIfVelocity();
                            
                            Spectrum spectrum2 = (Spectrum)listElement2.elementAt(i+1);
                            spectrum2.setRealData(true);
                            
                            new Thread(spectrum2).start();
                            while(spectrum2.getToWait()) Thread.sleep(500);
                            
                            
                            //Spectrum spectrumConverted2 = (new SpectrumConverter()).convertSpectrum(spectrum2,this.finalUnits);
                            Spectrum spectrumConverted2 = spectrumConverter.convertSpectrum(spectrum2,this.finalUnits);
                            SpectrumUtils.checkVelocity(this.aioSpecToolDetached,spectrumConverted2);
                            
                            Arithmetics arithmetics = new Arithmetics(spectrumConverted1, spectrumConverted2);
                            arithmetics.initializeData();
                            
                            disjointSpectra = arithmetics.disjointSpectra();
                            
                            spectrumModified = arithmetics.sum();
                            spectrumModified.setRealData(true);
                            
                            title = "SUM( "+spectrum1.getTitle() + ", "+spectrum2.getTitle()+" )";
                            
                            spectrumModified.setTitle(spectrum1.getTitle()+" ADD "+spectrum2.getTitle());
                            spectrumModified.setUrl(spectrum1.getUrl()+" ADD "+spectrum2.getUrl());
                            
                            
                        }
                        
                        
                        
                    }//if "+"
                    
                    if (operando.equals("-")){
                        
                        //Spectrum spectrum1 = new Spectrum();
                        spectrum1 = new Spectrum();
                        spectrumModified = new Spectrum();
                        operation = " DIFF ";
                        
                        spectrumModified = new Spectrum();
                        if(i==1){
                            spectrum1 = (Spectrum)listElement2.elementAt(i-1);
                        }else{
                            spectrum1 = (Spectrum)finalElements.elementAt(ct);
                        }
                        
                        
                        setUnits();
                        
                        new Thread(spectrum1).start();
                        while(spectrum1.getToWait()) Thread.sleep(500);
                        
                        
                        //Spectrum spectrumConverted1 = (new SpectrumConverter()).convertSpectrum(spectrum1,this.finalUnits);
                        Spectrum spectrumConverted1 = spectrumConverter.convertSpectrum(spectrum1,this.finalUnits);
                        SpectrumUtils.checkVelocity(this.aioSpecToolDetached,spectrumConverted1);
                        
                        if(i+1 > listElement2.size()-1){
                            
                            double constant = Double.valueOf(text).doubleValue();
                            
                            Arithmetics arithmetics = new Arithmetics(spectrumConverted1, constant);
                            spectrumModified = arithmetics.substractConstant();
                            
                            title = "DIFF( "+spectrum1.getTitle() + ", "+constant+" )";
                            
                            spectrumModified.setTitle(spectrum1.getTitle()+" SUBTRACT "+constant);
                            spectrumModified.setUrl(spectrum1.getUrl()+" SUBTRACT "+constant);
                            
                        }else{
                            
                            Spectrum spectrum2 = (Spectrum)listElement2.elementAt(i+1);
                            
                            new Thread(spectrum2).start();
                            while(spectrum2.getToWait()) Thread.sleep(500);
                            
                            
                            //Spectrum spectrumConverted2 = (new SpectrumConverter()).convertSpectrum(spectrum2,this.finalUnits);
                            Spectrum spectrumConverted2 = spectrumConverter.convertSpectrum(spectrum2,this.finalUnits);
                            SpectrumUtils.checkVelocity(this.aioSpecToolDetached,spectrumConverted2);
                            
                            Arithmetics arithmetics = new Arithmetics(spectrumConverted1, spectrumConverted2);
                            arithmetics.initializeData();
                            
                            disjointSpectra = arithmetics.disjointSpectra();
                            
                            spectrumModified = arithmetics.diff();
                            
                            title = "DIFF( "+spectrum1.getTitle() + ", "+spectrum2.getTitle()+" )";
                            
                            spectrumModified.setTitle(spectrum1.getTitle()+" SUBTRACT "+spectrum2.getTitle());
                            spectrumModified.setUrl(spectrum1.getUrl()+" SUBTRACT "+spectrum2.getUrl());
                            
                            
                        }
                        
                    }//if "-"
                    
                    
                    if (operando.equals("x")){
                        
                        //Spectrum spectrum1 = new Spectrum();
                        spectrum1 = new Spectrum();
                        spectrumModified = new Spectrum();
                        operation = "PROD ";
                        
                        spectrumModified = new Spectrum();
                        if(i==1){
                            spectrum1 = (Spectrum)listElement2.elementAt(i-1);
                        }else{
                            spectrum1 = (Spectrum)finalElements.elementAt(ct);
                        }
                        
                        
                        setUnits();
                        
                        new Thread(spectrum1).start();
                        while(spectrum1.getToWait()) Thread.sleep(500);
                        
                        //SpectrumUtils.checkVelocity(this.aioSpecToolDetached,spectrum1);
                        //Spectrum spectrumConverted1 = (new SpectrumConverter()).convertSpectrum(spectrum1,this.finalUnits);
                        Spectrum spectrumConverted1 = spectrumConverter.convertSpectrum(spectrum1,this.finalUnits);
                        
                        
                        
                        if(i+1 > listElement2.size()-1){
                            
                            double constant = Double.valueOf(text).doubleValue();
                            
                            Arithmetics arithmetics = new Arithmetics(spectrumConverted1, constant);
                            spectrumModified = arithmetics.multConstant();
                            
                            title = "PROD( "+spectrum1.getTitle() + ", "+constant+" )";
                            
                            spectrumModified.setTitle(spectrum1.getTitle()+" MULTIPLY "+constant);
                            spectrumModified.setUrl(spectrum1.getUrl()+" MULTIPLY "+constant);
                            
                        }else{
                            
                            Spectrum spectrum2 = (Spectrum)listElement2.elementAt(i+1);
                            
                            new Thread(spectrum2).start();
                            while(spectrum2.getToWait()) Thread.sleep(500);
                            
                            //SpectrumUtils.checkVelocity(this.aioSpecToolDetached,spectrum2);
                            //Spectrum spectrumConverted2 = (new SpectrumConverter()).convertSpectrum(spectrum2,this.finalUnits);
                            Spectrum spectrumConverted2 =spectrumConverter.convertSpectrum(spectrum2,this.finalUnits);
                            
                            Arithmetics arithmetics = new Arithmetics(spectrumConverted1, spectrumConverted2);
                            arithmetics.initializeData();
                            
                            disjointSpectra = arithmetics.disjointSpectra();
                            
                            spectrumModified = arithmetics.prod();
                            
                            title = "PROD( "+spectrum1.getTitle() + ", "+spectrum2.getTitle()+" )";
                            
                            spectrumModified.setTitle(spectrum1.getTitle()+" MULTIPLY "+spectrum2.getTitle());
                            spectrumModified.setUrl(spectrum1.getUrl()+" MULTIPLY "+spectrum2.getTitle());
                            
                            
                        }
                        
                    }//if "x"
                    
                    
                    if (operando.equals("/")){
                        
                        //Spectrum spectrum1 = new Spectrum();
                        spectrum1 = new Spectrum();
                        spectrumModified = new Spectrum();
                        operation = " DIV ";
                        
                        if(i==1){
                            spectrum1 = (Spectrum)listElement2.elementAt(i-1);
                        }else{
                            spectrum1 = (Spectrum)finalElements.elementAt(ct);
                        }
                        
                        
                        setUnits();
                        
                        new Thread(spectrum1).start();
                        while(spectrum1.getToWait()) Thread.sleep(500);
                        
                        //SpectrumUtils.checkVelocity(this.aioSpecToolDetached,spectrum1);
                        //Spectrum spectrumConverted1 = (new SpectrumConverter()).convertSpectrum(spectrum1,this.finalUnits);
                        Spectrum spectrumConverted1 = spectrumConverter.convertSpectrum(spectrum1,this.finalUnits);
                        
                        
                        if(i+1 > listElement2.size()-1){
                            
                            double constant = Double.valueOf(text).doubleValue();
                            
                            Arithmetics arithmetics = new Arithmetics(spectrumConverted1, constant);
                            try{
                                spectrumModified = arithmetics.divConstant();
                            }catch(Exception e){
                                JOptionPane.showMessageDialog(this,
                                        "Unable to return a suitable result. Please, review input data.",
                                        "Warning",
                                        JOptionPane.WARNING_MESSAGE);
                            }
                            
                            title = "DIV( "+spectrum1.getTitle() + ", "+constant+" )";
                            
                            spectrumModified.setTitle(spectrum1.getTitle()+" DIV "+constant);
                            spectrumModified.setUrl(spectrum1.getUrl()+" DIV "+constant);
                            
                        }else{
                            
                            Spectrum spectrum2 = (Spectrum)listElement2.elementAt(i+1);
                            
                            new Thread(spectrum2).start();
                            while(spectrum2.getToWait()) Thread.sleep(500);
                            
                            //SpectrumUtils.checkVelocity(this.aioSpecToolDetached,spectrum2);
                            //Spectrum spectrumConverted2 = (new SpectrumConverter()).convertSpectrum(spectrum2,this.finalUnits);
                            Spectrum spectrumConverted2 = spectrumConverter.convertSpectrum(spectrum2,this.finalUnits);
                            
                            Arithmetics arithmetics = new Arithmetics(spectrumConverted1, spectrumConverted2);
                            arithmetics.initializeData();
                            
                            disjointSpectra = arithmetics.disjointSpectra();
                            
                            spectrumModified = arithmetics.div();
                            
                            title = "DIV( "+spectrum1.getTitle() + ", "+spectrum2.getTitle()+" )";
                            
                            spectrumModified.setTitle(spectrum1.getTitle()+" DIV "+spectrum2.getTitle());
                            spectrumModified.setUrl(spectrum1.getUrl()+" DIV "+spectrum2.getUrl());
                            
                            
                        }
                        
                    }//if "/"
                    
                    
                    if (operando.equals("**")){
                        
                        operation = " CONV ";

                        spectrumModified = new Spectrum();
                        if(i==1){
                            spectrum1 = (Spectrum)listElement2.elementAt(i-1);
                        }else{
                            spectrum1 = (Spectrum)finalElements.elementAt(ct);
                        }
                        Spectrum spectrum2 = (Spectrum)listElement2.elementAt(i+1);
                        
                        setUnits();
                        
                        new Thread(spectrum1).start();
                        while(spectrum1.getToWait()) Thread.sleep(500);
                        
                        //SpectrumUtils.checkVelocity(this.aioSpecToolDetached,spectrum1);
                        //Spectrum spectrumConverted1 = (new SpectrumConverter()).convertSpectrum(spectrum1,this.finalUnits);
                        Spectrum spectrumConverted1 = spectrumConverter.convertSpectrum(spectrum1,this.finalUnits);
                        
                        new Thread(spectrum2).start();
                        while(spectrum2.getToWait()) Thread.sleep(500);
                        
                        //SpectrumUtils.checkVelocity(this.aioSpecToolDetached,spectrum2);
                        //Spectrum spectrumConverted2 = (new SpectrumConverter()).convertSpectrum(spectrum2,this.finalUnits);
                        Spectrum spectrumConverted2 = spectrumConverter.convertSpectrum(spectrum2,this.finalUnits);
                        
                        DiscreteConvolution discConv = new DiscreteConvolution(spectrumConverted1, spectrumConverted2);
                        spectrumModified = discConv.getConvolution();
                        
                        
//                      DiscreteConvolution discConv = new DiscreteConvolution();
//                      spectrumModified = discConv.runningConvolution(spectrumConverted1, spectrumConverted2);
                        
                        
                        title = "CONV( "+spectrumConverted1.getTitle()+ ", "+spectrumConverted2.getTitle()+" )";
                        
                        spectrumModified.setTitle(spectrum1.getTitle()+" CONV "+spectrum2.getTitle());
                        spectrumModified.setUrl(spectrum1.getUrl()+" CONV "+spectrum2.getUrl());
                        spectrumModified.setFormat(spectrum1.getFormat());
                        spectrumModified.setFluxColumnName(spectrum1.getFluxColumnName());
                        spectrumModified.setWaveLengthColumnName(spectrum1.getWaveLengthColumnName());
                        
                        //spectrumModified.setMetaDataComplete(spectrum1.getMetaData());
                        
                        spectrumModified.setUnits(finalUnits);
                        
                        SpectrumSet spectrumSetTmp = new SpectrumSet();
                        spectrumSetTmp.addSpectrum(0,spectrumModified);
                        
                        finalElements.add(spectrumModified);
                        ct++;
                        
                        //add to the previous spectrumSet a new Spectrum trasformed
                        this.aioSpecToolDetached.spectrumSet.addSpectrumSet(spectrumSetTmp);
                        spectrumModified.setRow(this.aioSpecToolDetached.spectrumSet.getSpectrumSet().size() - 1);
                        
                        this.aioSpecToolDetached.addSpectrum("CONV", spectrumModified,(javax.swing.JTextArea) null/*,checkNode*/);
                        screenArea.append(title+"\n");
                    }//if **
                    
                    if (operando.equals("*")){

                        operation = " CONV ";

                        spectrumModified = new Spectrum();
                        if(i==1){
                            spectrum1 = (Spectrum)listElement2.elementAt(i-1);
                        }else{
                            spectrum1 = (Spectrum)finalElements.elementAt(ct);
                        }
                        Spectrum spectrum2 = (Spectrum)listElement2.elementAt(i+1);
                        
                        setUnits();
                        
                        new Thread(spectrum1).start();
                        while(spectrum1.getToWait()) Thread.sleep(500);
                        //Spectrum spectrumConverted1 = (new SpectrumConverter()).convertSpectrum(spectrum1,this.finalUnits);
                        Spectrum spectrumConverted1 = spectrumConverter.convertSpectrum(spectrum1,this.finalUnits);
                        
                        
                        new Thread(spectrum2).start();
                        while(spectrum2.getToWait()) Thread.sleep(500);
                        
                        //Spectrum spectrumConverted2 = (new SpectrumConverter()).convertSpectrum(spectrum2,this.finalUnits);
                        Spectrum spectrumConverted2 = spectrumConverter.convertSpectrum(spectrum2,this.finalUnits);
                        
                        
//                        DiscreteConvolution discConv = new DiscreteConvolution(spectrumConverted1, spectrumConverted2);
//                        spectrumModified = discConv.getConvolution();
                        
                        
                        DiscreteConvolution discConv = new DiscreteConvolution();
//                      spectrumModified = discConv.runningConvolution(spectrumConverted1, spectrumConverted2);
                        spectrumModified = discConv.convolutionToTest(spectrumConverted1, spectrumConverted2);
                        
                        
                        title = "CONV( "+spectrum1.getTitle()+ ", "+spectrum2.getTitle()+" )";
                        
                        spectrumModified.setTitle(spectrum1.getTitle()+" RUNNING_CONV "+spectrum2.getTitle());
                        spectrumModified.setUrl(spectrum1.getUrl()+" RUNNING_CONV "+spectrum2.getUrl());

                    }//if *
                    
                }//if type
                
            }//for

            if (spectrumModified.getWaveValues() != null) {
                if (spectrumModified.getWaveValues().length != 0) {
                    //common for all operations(, but convolution!)
                    if (disjointSpectra == false) {
                        /*if operation==multiple Spectra the algorithm does NOT reach any "if clause" of other operation(+,-,x,/)
                        So spectrumModified = new Spectrum() without data
                        See checkIfMultipleSpectra() method!!!!*/
                        if (operation != "MULTIPLE SPECTRA") {

                            SpectrumUtils.setParameters(this.aioSpecToolDetached, spectrumModified, title, this.aioSpecToolDetached.mathematicMethodExecution);
                            this.aioSpecToolDetached.mathematicMethodExecution++;
                            this.aioSpecToolDetached.addSpectrum(operation, spectrumModified, (javax.swing.JTextArea) null/*,checkNode*/);
                            screenArea.append(title + "\n");
                        }
                    } else {
                        System.out.println("disjoint true ");
                        //custom title, warning icon
                        JOptionPane.showMessageDialog(this,
                                "Disjoint spectra ranges.",
                                "Warning",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
            
            
        }catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Unable to return a suitable result. Please, review input data.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    
    
    public String getTypeOperandum(String listElement){
        
        String operationSymbol = "";
        
        if (listElement=="+"){
            operationSymbol = "+";
        }else if(listElement=="-"){
            operationSymbol = "-";
        }else if(listElement=="x"){
            operationSymbol = "x";
        }else if(listElement=="/"){
            operationSymbol = "/";
        }else if (listElement=="*"){
            operationSymbol = "*";
            
        }else if (listElement=="**"){
            operationSymbol = "**";
        }else{
            operationSymbol = "false";
        }
        
        return operationSymbol;
        
    }
    
    public boolean chooseTypeElement(String listElement){
        
        if (listElement=="+"){
            type = false;
        }else if(listElement=="-"){
            type = false;
        }else if(listElement=="x"){
            type = false;
        }else if(listElement=="/"){
            type = false;
        }else if (listElement=="*"){
            type = false;
        }else if (listElement=="**"){
            type = false;
        } else{
            type = true;
        }
        
        return type;
        
    }
    
    
    //create new Vector type spectrum - operandum - spectrum - operandum....
    public void sumSpectra(Vector spectraVec){
        try{
            
            String out ="";
            
            setUnits();
            Spectrum sp = new Spectrum();
            Spectrum sp2 = (Spectrum) spectraVec.elementAt(0);
            
            new Thread(sp2).start();
            while(sp2.getToWait()) Thread.sleep(500);
            
            //SpectrumUtils.checkVelocity(this.aioSpecToolDetached,sp2);
            //Spectrum spectrumConverted2 = (new SpectrumConverter()).convertSpectrum(sp2,this.finalUnits);
            Spectrum spectrumConverted2 = spectrumConverter.convertSpectrum(sp2,this.finalUnits);
            
            out = "SUM( "+sp2.getTitle()+ ", ";
            
            for(int j=1;j<spectraVec.size();j++){
                sp = (Spectrum) spectraVec.elementAt(j);
                
                new Thread(sp).start();
                while(sp.getToWait()) Thread.sleep(500);
                
                //SpectrumUtils.checkVelocity(this.aioSpecToolDetached,sp);
                //Spectrum spectrumConverted1 = (new SpectrumConverter()).convertSpectrum(sp,this.finalUnits);
                Spectrum spectrumConverted1 = spectrumConverter.convertSpectrum(sp,this.finalUnits);
                
                Arithmetics arithmetics = new Arithmetics(spectrumConverted1, spectrumConverted2);
                arithmetics.initializeData();
                spectrumConverted2 = arithmetics.sum();
                
                out = out+", "+sp.getTitle();
            }
            
            out = out +" )";
            
            spectrumConverted2.setTitle(sp.getTitle()+" ADD "+sp2.getTitle());
            spectrumConverted2.setFormat(sp.getFormat());
            spectrumConverted2.setFluxColumnName(sp.getFluxColumnName());
            spectrumConverted2.setWaveLengthColumnName(sp.getWaveLengthColumnName());
            spectrumConverted2.setMetaDataComplete(sp.getMetaData());
            spectrumConverted2.setUnits(finalUnits);
            
            
            listElement2.add(spectrumConverted2);
            trackVector.add(out);
            
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    public void drawSpectra(Spectrum spec){
        SpectrumSet spectrumSetTmp = new SpectrumSet();
        spectrumSetTmp.addSpectrum(0,spec);
        
        //add to the previous spectrumSet a new Spectrum trasformed
        this.aioSpecToolDetached.spectrumSet.addSpectrumSet(spectrumSetTmp);
        spec.setRow(this.aioSpecToolDetached.spectrumSet.getSpectrumSet().size() - 1);
        
        this.aioSpecToolDetached.addSpectrum("SUM", spec,(javax.swing.JTextArea) null/*,checkNode*/);
        
    }
    
    
    public void setUnits(){
        this.waveUnits =(String)this.aioSpecToolDetached.waveChoice.getSelectedItem();
        this.fluxUnits =(String)this.aioSpecToolDetached.fluxChoice.getSelectedItem();
        System.out.println("Units "+this.waveUnits+" "+this.fluxUnits);
        this.finalUnits     = new Unit(waveUnits,fluxUnits);
    }
    
    
    private void divisionButtonActionPerformed(java.awt.event.ActionEvent evt) {                                               
        operationsArea.append(" / "+"\n");
        listElements.add("/");
    }                                              
    
    private void productButtonActionPerformed(java.awt.event.ActionEvent evt) {                                              
        operationsArea.append(" x "+"\n");
        listElements.add("x");
    }                                             
    
    private void subtractionsButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        operationsArea.append(" - "+"\n");
        listElements.add("-");
    }                                                  
    
    private void additionButtonActionPerformed(java.awt.event.ActionEvent evt) {                                               
        operationsArea.append(" + "+"\n");
        listElements.add("+");
    }                                              
    
    
    public void setAioSpecToolDetached(VOSpecDetached aioSpecToolDetached) {
        this.aioSpecToolDetached = aioSpecToolDetached;
        
    }
    
    public void addMetadataLine(String ind,String line) {
        metadata.put(ind ,line);
        System.out.println("metadata.put  ="+ line);
        //metadataCount++;
    }
    
    public void writeMetadata() {
        this.metadata = new Hashtable();
        
        // Setting the metadata
        addMetadataLine("TITLE",title);
        System.out.println("TITLE ="+ title);
        addMetadataLine("WAVELENGTH UNITS",waveUnits);
        System.out.println("WAVE UNITS ="+ waveUnits);
        addMetadataLine("FLUX UNITS",fluxUnits);
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VOSpeculator().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify
    private javax.swing.JButton additionButton;
    private javax.swing.JLabel constantLabel;
    private javax.swing.JTextField constantTextField;
    private javax.swing.JButton convolutionButton;
    private javax.swing.JButton divisionButton;
    private javax.swing.JButton equalButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea operationsArea;
    private javax.swing.JButton productButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JTextArea screenArea;
    private javax.swing.JButton subtractionsButton;
    // End of variables declaration
    public Vector listElements;
    public boolean type = false;  // true = Spectrum
    // false = Operandum (+/-/%/x..)
    public boolean operandum =false;
    public Vector listElement2;
    public Vector spectraVec;
    public Vector trackVector;
    
    public VOSpecDetached      aioSpecToolDetached 	= null;
    public String waveUnits = "";
    public String fluxUnits = "";
    public Unit finalUnits = null;
    //public CheckNode checkNode = null;
    public Vector finalElements;
    
    public  Hashtable metadata;
    
    public  String title ="";
    public  String axes = "";
    public String operation="";
    
    
    public SpectrumConverter spectrumConverter = null;
    
    
    
    
}
