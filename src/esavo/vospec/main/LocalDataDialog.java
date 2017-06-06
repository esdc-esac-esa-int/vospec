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

import esavo.vospec.dataingestion.SSAIngestor;
import esavo.vospec.spectrum.*;
import esavo.vospec.util.VOTableUtils;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.*;

/**
 *
 * @author  jsalgado
 */
public class LocalDataDialog extends javax.swing.JDialog {
    
    File                    currentDirectory    	= null;
    AddNewDialog            addNewDialog        	= null;
    Hashtable               spectraTable        	= new Hashtable();
    Vector                  spectraVector       	= new Vector();
    VOSpecDetached     aioSpecToolDetached 	= null;
    
    boolean 		    addLocalDataRequested 	= false;
    
    
    protected DefaultMutableTreeNode        parentNode  = new DefaultMutableTreeNode("Local Spectra");;
    protected DefaultTreeModel              treeModel;
    
    /** Creates new form LocalDataDialog */
    public LocalDataDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        
        this.aioSpecToolDetached = (VOSpecDetached) parent;
        
        initComponents();
        this.setSize(500,400);
        this.setTitle("Local Data Dialog");
        populateTree();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - this.getWidth() /2, (screenSize.height / 2)- this.getHeight() /2);

        AddLocalDataThread addLocalDataThread = new AddLocalDataThread(this);
        new Thread(addLocalDataThread).start();

    }
    
    public void populateTree() {
        parentNode = new DefaultMutableTreeNode("Local Spectra");
        treeModel = new DefaultTreeModel(parentNode);
        jTree = new JTree(treeModel);
        jTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTreeMouseClicked(evt);
            }
        });
        jTree.setCellRenderer(new CheckRenderer());
        jScrollForJTree.setViewportView(jTree);
    }
    
    public synchronized void addSpectrum(Spectrum spectrum) {
        
        if(alreadyLoaded(spectrum)) return;
        
        String name                         = spectrum.getTitle();
        DefaultMutableTreeNode childNode    = new DefaultMutableTreeNode(name);
        treeModel.insertNodeInto(childNode, parentNode, 
                                 parentNode.getChildCount());
                                 
        jTree.scrollPathToVisible(new TreePath(childNode.getPath()));
        
        spectraTable.put(childNode,spectrum);
        spectraVector.add(childNode);
    }    
    /*
        public boolean deleteSpectrum(Spectrum spectrum) {

        Spectrum tmpSpectrum;

        for (int i=0; i < spectraVector.size(); i++) {

            Object  key = spectraVector.elementAt(i);
            tmpSpectrum = (Spectrum) spectraTable.get(key);

            if(tmpSpectrum.getUrl().equals(spectrum.getUrl())) {
                
                DefaultMutableTreeNode currentNode = tmpSpectrum.getNode();
System.out.println("test "+tmpSpectrum.getUrl());
                Node parent = (Node)(currentNode.getParent());
                if (parent != null) {
                    treeModel.removeNodeFromParent(currentNode);
                    spectraVector.removeElement(currentNode);
                    spectraTable.remove(currentNode);
                }
            }

        }

        return false;
    }
    */
    public void addLocalData() {        
        this.addLocalDataRequested = true;
    }
    
    public void addLocalDataExecute() {
        this.aioSpecToolDetached.addLocalData();
    }
    
    public boolean alreadyLoaded(Spectrum spectrum) {
        
        Spectrum tmpSpectrum;
        
        for (int i=0; i < spectraVector.size(); i++) {
            
            Object  key = spectraVector.elementAt(i);            
            tmpSpectrum = (Spectrum) spectraTable.get(key);
            
            if(tmpSpectrum.getUrl().equals(spectrum.getUrl())) {
                spectraTable.put(key, spectrum);
                return true;
            }
 
        }    
        
        return false;
    }    
    
    public void removeSpectrum() {
        
       TreePath[] currentSelectionArray = jTree.getSelectionPaths();
       
       for(int i=0; i < currentSelectionArray.length; i++) {
       
            TreePath currentSelection = currentSelectionArray[i];
            
            if (currentSelection != null) {
            
                DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                                                        (currentSelection.getLastPathComponent());
                MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
                if (parent != null) {
                    treeModel.removeNodeFromParent(currentNode);    
                    spectraVector.removeElement(currentNode);
                    spectraTable.remove(currentNode);
                }
            }
       }    
    }     
    
    public void setSelectedForAll(boolean isSelected) {
        
       
        Spectrum    tmpSpectrum     = null;
        Hashtable   tmpHashtable    = new Hashtable();

        for (int i=0; i < spectraVector.size(); i++) {
            
            Object  key = spectraVector.elementAt(i);            
            tmpSpectrum = (Spectrum) spectraTable.get(key);
            tmpSpectrum.setSelected(isSelected);
            tmpSpectrum.setToWait(isSelected);
            
            tmpHashtable.put(key, tmpSpectrum);
        }
        
        spectraTable = tmpHashtable;
    }     
    
    public void modifySpectrum() {
      
      if(jTree.getSelectionPaths().length != 1)  {
          JOptionPane.showMessageDialog(this,"Please select one and only one Spectrum to be modified");
          return;
      }
          
      TreePath currentSelection = jTree.getSelectionPath();
      if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                         (currentSelection.getLastPathComponent());  
            
            if(currentNode == parentNode) return;
            
            if(addNewDialog == null) addNewDialog = new AddNewDialog(this.aioSpecToolDetached, true);
        
            
            Spectrum spectrum = (Spectrum) spectraTable.get(currentNode);
            addNewDialog.beanSpectrum(spectrum);
            Spectrum outputSpectrum = addNewDialog.outputSpectrum;
        
          if(outputSpectrum != null) {
              spectraTable.put(currentNode,outputSpectrum);
              currentNode.setUserObject(outputSpectrum.getTitle());
              treeModel.nodeChanged(currentNode);
         }      
       } 
    }

    public void deleteAll() {
        parentNode.removeAllChildren();
        treeModel.reload();
        spectraTable.clear();
        spectraVector.clear();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanelForButtons = new javax.swing.JPanel();
        titlePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanelForSpectrumButtons = new javax.swing.JPanel();
        addNewButton = new javax.swing.JButton();
        modifyButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        deleteAllButton = new javax.swing.JButton();
        jScrollForJTree = new javax.swing.JScrollPane();
        jTree = new javax.swing.JTree();
        bottomPanel = new javax.swing.JPanel();
        closePanel = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        displayButton = new javax.swing.JButton();
        jPanelForGeneralButtons = new javax.swing.JPanel();
        openButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();

        setTitle("Local Data");
        setBackground(new java.awt.Color(244, 241, 239));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanelForButtons.setLayout(new java.awt.BorderLayout());

        jPanelForButtons.setBackground(new java.awt.Color(244, 241, 239));
        jPanelForButtons.setMaximumSize(new java.awt.Dimension(380, 60));
        jPanelForButtons.setMinimumSize(new java.awt.Dimension(380, 60));
        jPanelForButtons.setPreferredSize(null);
        titlePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        titlePanel.setBackground(new java.awt.Color(244, 241, 239));
        titlePanel.setFont(new java.awt.Font("Dialog", 1, 10));
        titlePanel.setMaximumSize(new java.awt.Dimension(400, 20));
        titlePanel.setMinimumSize(new java.awt.Dimension(400, 20));
        titlePanel.setPreferredSize(new java.awt.Dimension(400, 20));
        jLabel1.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("Local Data Editor 3.0");
        titlePanel.add(jLabel1);

        jPanelForButtons.add(titlePanel, java.awt.BorderLayout.NORTH);

        jPanelForSpectrumButtons.setBackground(new java.awt.Color(244, 241, 239));
        jPanelForSpectrumButtons.setMaximumSize(new java.awt.Dimension(400, 40));
        jPanelForSpectrumButtons.setMinimumSize(new java.awt.Dimension(400, 40));
        jPanelForSpectrumButtons.setPreferredSize(new java.awt.Dimension(400, 40));
        addNewButton.setFont(new java.awt.Font("Dialog", 1, 11));
        addNewButton.setForeground(new java.awt.Color(102, 102, 102));
        addNewButton.setText("Add New Spectrum");
        addNewButton.setToolTipText("Add Spectrum File");
        addNewButton.setMaximumSize(new java.awt.Dimension(89, 24));
        addNewButton.setMinimumSize(new java.awt.Dimension(89, 24));
        addNewButton.setPreferredSize(new java.awt.Dimension(150, 24));
        addNewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewButtonActionPerformed(evt);
            }
        });

        jPanelForSpectrumButtons.add(addNewButton);

        modifyButton.setFont(new java.awt.Font("Dialog", 1, 11));
        modifyButton.setForeground(new java.awt.Color(102, 102, 102));
        modifyButton.setText("Modify");
        modifyButton.setToolTipText("Modify Spectrum Information");
        modifyButton.setMaximumSize(new java.awt.Dimension(89, 24));
        modifyButton.setMinimumSize(new java.awt.Dimension(89, 24));
        modifyButton.setPreferredSize(new java.awt.Dimension(89, 24));
        modifyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyButtonActionPerformed(evt);
            }
        });

        jPanelForSpectrumButtons.add(modifyButton);

        deleteButton.setFont(new java.awt.Font("Dialog", 1, 11));
        deleteButton.setForeground(new java.awt.Color(102, 102, 102));
        deleteButton.setText("Delete");
        deleteButton.setToolTipText("Delete Selected Spectrum");
        deleteButton.setMaximumSize(new java.awt.Dimension(89, 24));
        deleteButton.setMinimumSize(new java.awt.Dimension(89, 24));
        deleteButton.setPreferredSize(new java.awt.Dimension(89, 24));
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        jPanelForSpectrumButtons.add(deleteButton);

        deleteAllButton.setFont(new java.awt.Font("Dialog", 1, 11));
        deleteAllButton.setForeground(new java.awt.Color(102, 102, 102));
        deleteAllButton.setText("Delete All");
        deleteAllButton.setToolTipText("Delete All Spectra");
        deleteAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAllButtonActionPerformed(evt);
            }
        });

        jPanelForSpectrumButtons.add(deleteAllButton);

        jPanelForButtons.add(jPanelForSpectrumButtons, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanelForButtons, java.awt.BorderLayout.NORTH);

        jScrollForJTree.setBackground(new java.awt.Color(244, 241, 239));
        jScrollForJTree.setMinimumSize(new java.awt.Dimension(82, 363));
        jScrollForJTree.setPreferredSize(null);
        jTree.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jTree.setMaximumSize(new java.awt.Dimension(327670, 327670));
        jTree.setPreferredSize(new java.awt.Dimension(400, 80));
        jTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTreeMouseClicked(evt);
            }
        });

        jScrollForJTree.setViewportView(jTree);

        getContentPane().add(jScrollForJTree, java.awt.BorderLayout.CENTER);

        bottomPanel.setLayout(new java.awt.BorderLayout());

        bottomPanel.setBackground(new java.awt.Color(244, 241, 239));
        bottomPanel.setMaximumSize(new java.awt.Dimension(365, 80));
        bottomPanel.setMinimumSize(new java.awt.Dimension(365, 80));
        bottomPanel.setPreferredSize(new java.awt.Dimension(365, 80));
        closePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        closePanel.setBackground(new java.awt.Color(244, 241, 239));
        closeButton.setFont(new java.awt.Font("Dialog", 1, 11));
        closeButton.setForeground(new java.awt.Color(102, 102, 102));
        closeButton.setText("Close");
        closeButton.setToolTipText("Close Dialog");
        closeButton.setMaximumSize(new java.awt.Dimension(89, 24));
        closeButton.setMinimumSize(new java.awt.Dimension(89, 24));
        closeButton.setPreferredSize(new java.awt.Dimension(89, 24));
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        closePanel.add(closeButton);

        displayButton.setFont(new java.awt.Font("Dialog", 1, 11));
        displayButton.setForeground(new java.awt.Color(102, 102, 102));
        displayButton.setText("Display");
        displayButton.setToolTipText("Display Local Spectra in VOSpec");
        displayButton.setMaximumSize(new java.awt.Dimension(89, 24));
        displayButton.setMinimumSize(new java.awt.Dimension(89, 24));
        displayButton.setPreferredSize(new java.awt.Dimension(89, 24));
        displayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                displayButtonActionPerformed(evt);
            }
        });

        closePanel.add(displayButton);

        bottomPanel.add(closePanel, java.awt.BorderLayout.SOUTH);

        jPanelForGeneralButtons.setBackground(new java.awt.Color(244, 241, 239));
        jPanelForGeneralButtons.setMaximumSize(new java.awt.Dimension(400, 20));
        jPanelForGeneralButtons.setMinimumSize(new java.awt.Dimension(400, 20));
        jPanelForGeneralButtons.setPreferredSize(new java.awt.Dimension(400, 20));
        openButton.setFont(new java.awt.Font("Dialog", 1, 11));
        openButton.setForeground(new java.awt.Color(102, 102, 102));
        openButton.setText("Add from SSAP VOTable");
        openButton.setToolTipText("Add Spectra from a local SSAP Wrapper File");
        openButton.setMaximumSize(new java.awt.Dimension(189, 25));
        openButton.setMinimumSize(new java.awt.Dimension(0, 0));
        openButton.setPreferredSize(new java.awt.Dimension(189, 25));
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });

        jPanelForGeneralButtons.add(openButton);

        saveButton.setFont(new java.awt.Font("Dialog", 1, 11));
        saveButton.setForeground(new java.awt.Color(102, 102, 102));
        saveButton.setText("Save in SSAP VOTable");
        saveButton.setToolTipText("Save the local Spectra in a SSAP Wrapper File");
        saveButton.setMaximumSize(new java.awt.Dimension(189, 25));
        saveButton.setMinimumSize(new java.awt.Dimension(0, 0));
        saveButton.setPreferredSize(new java.awt.Dimension(189, 25));
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        jPanelForGeneralButtons.add(saveButton);

        bottomPanel.add(jPanelForGeneralButtons, java.awt.BorderLayout.CENTER);

        getContentPane().add(bottomPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.hide();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void displayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayButtonActionPerformed
        displayButtonAction();
    }//GEN-LAST:event_displayButtonActionPerformed

    public void displayButtonAction() {
        this.hide();
        setSelectedForAll(true);
        addLocalData();

        /*
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                aioSpecToolDetached.displayButtonActionPerformed();
            }
        });*/

        
   
    }    
        
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        JFileChooser fileChooser;
        if(currentDirectory != null) {
            fileChooser = new JFileChooser(currentDirectory);
        } else {
            fileChooser = new JFileChooser();
        }
        
        int returnVal   = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File localFile  = fileChooser.getSelectedFile();
            
            SpectrumSet spectrumSet = getSpectrumSet();
            
            try {
                VOTableUtils.saveSpectrumSetInVOTable(spectrumSet,localFile);
                JOptionPane.showMessageDialog(this,"Wrapper file: " + localFile.getAbsolutePath() + " correctly saved");
            } catch(Exception e) {
                 JOptionPane.showMessageDialog(this,"Problems saving wrapper file: " + e.toString());
                 return;
            }    
        }
        
    }//GEN-LAST:event_saveButtonActionPerformed

    public SpectrumSet getSpectrumSet() {
        
        SpectrumSet outputSpectrumSet = new SpectrumSet();
        Spectrum tmpSpectrum;
        
        for (int i=0; i < spectraVector.size(); i++) {
            Object  key = spectraVector.elementAt(i);            
            tmpSpectrum = (Spectrum) spectraTable.get(key);
            outputSpectrumSet.addSpectrum(i, tmpSpectrum);
        }
        
        return outputSpectrumSet;
    }    
    
    private void jTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTreeMouseClicked
        if (evt.getClickCount() == 2) modifySpectrum();
    }//GEN-LAST:event_jTreeMouseClicked

    private void modifyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyButtonActionPerformed
        modifySpectrum();
    }//GEN-LAST:event_modifyButtonActionPerformed

    private void deleteAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAllButtonActionPerformed
        this.deleteAll();
    }//GEN-LAST:event_deleteAllButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        removeSpectrum();
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void addNewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewButtonActionPerformed
        try {
            addNewAction(true, null, null);
        } catch (Exception ex) {
            Logger.getLogger(LocalDataDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_addNewButtonActionPerformed
   
    public void addNewAction(boolean toDisplay, String urlString, String labelString) throws Exception {

        if (urlString != null) {

            AddNewDialog dialogAux = new AddNewDialog(this.aioSpecToolDetached, true);
            dialogAux.setFileName(urlString, true);
            dialogAux.setVisible(true);
            //dialogs.add(dialogAux);

            //System.out.println("setFileName " + urlString);

            Spectrum spectrum = dialogAux.outputSpectrum;


            if (spectrum != null) {
                if (!this.alreadyLoaded(spectrum)) {
                    spectrum.setSelected(toDisplay);
                    addSpectrum(spectrum);
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Spectrum already loaded, open Local Data Editor and modify it");
                }
            //}else{
            //  throw new Exception();
            }
            dialogAux.dispose();


        } else {

            JFileChooser fileChooser = null;

            if (currentDirectory != null) {
                fileChooser = new JFileChooser(currentDirectory);
            } else {
                fileChooser = new JFileChooser();
            }
            fileChooser.setMultiSelectionEnabled(true);

            int returnVal = fileChooser.showOpenDialog(this);
            File[] localFile = fileChooser.getSelectedFiles();

            if(returnVal != JFileChooser.APPROVE_OPTION) return;

            if (localFile == null) {
                return;
            }

            //Vector<AddNewDialog> dialogs = new Vector();
            AddNewDialog dialogAux;

            for (int i = 0; i < localFile.length; i++) {

                currentDirectory = localFile[i].getParentFile();
                urlString = "file:" + localFile[i].getAbsolutePath();
                dialogAux = new AddNewDialog(this.aioSpecToolDetached, true);
                dialogAux.setFileName(urlString, true);
                dialogAux.setVisible(true);
                //dialogs.add(dialogAux);

                //System.out.println("setFileName " + urlString);

                Spectrum spectrum = dialogAux.outputSpectrum;


                if (spectrum != null) {
                    if (!this.alreadyLoaded(spectrum)) {
                        spectrum.setSelected(toDisplay);
                        addSpectrum(spectrum);
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(this, "Spectrum already loaded, open Local Data Editor and modify it");
                    }
                //}else{
                //  throw new Exception();
                }
                dialogAux.dispose();

            }

        /*if(addNewDialog == null) addNewDialog = new AddNewDialog(new javax.swing.JFrame(), true);
        
        addNewDialog.fileSelectorAction(urlString,labelString);
        Spectrum spectrum = addNewDialog.outputSpectrum;

        if(spectrum != null) {
        spectrum.setSelected(toDisplay);
        addSpectrum(spectrum);
        }else{
        throw new Exception();
        }
         * */
        }
    }

    public void addNewDirectly() {
        addNewDirectly((String) null,(String) null);
    }
    
    public void addNewDirectly(String urlString,String labelString) {
        try {
            addNewAction(true, urlString, labelString);
        } catch (Exception e) {
            //System.out.println("catched");
            return;
        }
        addLocalData();
       
    }    
    
    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
        addFromSSAPWrapper();
    }//GEN-LAST:event_openButtonActionPerformed
    
   public void addFromSSAPWrapper() {
    
        try {
            JFileChooser fileChooser;
            if(currentDirectory != null) {
                    fileChooser = new JFileChooser(currentDirectory);    
            } else {
                fileChooser = new JFileChooser();            
            }    
            
            int returnVal   = fileChooser.showOpenDialog(this);
            File localFile  = fileChooser.getSelectedFile();
            
            if(returnVal != JFileChooser.APPROVE_OPTION) return;

            if(localFile != null) {
                currentDirectory = localFile.getParentFile();
                
                String ssapWrapperURLString = "file:" + localFile.getAbsolutePath();
                addSpectrumSetFromURL(ssapWrapperURLString);  
            } 
        } catch(Exception e) {
                JOptionPane.showMessageDialog(this,"Problems parsing SSAP Wrapper File. Reason: " + e.toString());
                e.printStackTrace();
        }    
    }    
    
    public void addSpectrumSetFromURL(String ssapWrapperURLString) throws Exception {
                SpectrumSet spectrumSet  = SSAIngestor.getSpectra(ssapWrapperURLString);
                for(int i=0; i < spectrumSet.getSpectrumSet().size(); i++) {
                    Spectrum spectrum = spectrumSet.getSpectrum(i);
                    addSpectrum(spectrum);
                }     
    }
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new LocalDataDialog(new javax.swing.JFrame(), true).show();
    }
    
    
    
       public class CheckRenderer extends JPanel implements TreeCellRenderer {
 
        protected JLabel label;

        public CheckRenderer() {
            setLayout(null);
            label = new JLabel();
            label.setForeground(UIManager.getColor("Tree.textForeground"));
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                boolean isSelected, boolean expanded,
                                boolean leaf, int row, boolean hasFocus) {
                                    
            String stringValue = tree.convertValueToText(value, isSelected, true, leaf, row, hasFocus);
            setEnabled(tree.isEnabled());
            
            label.setFont(tree.getFont());
            label.setText(stringValue);
            label.setForeground(UIManager.getColor("Tree.textForeground"));           

            if(isSelected && ((DefaultMutableTreeNode) value).getParent() != null) label.setForeground(new Color(50,120,200));
            add(label);
            return this;
        }

        public Dimension getPreferredSize() {
           return label.getPreferredSize();
        }
        
         public void doLayout() {
            Dimension d_label = label.getPreferredSize();
            label.setLocation(0,0);
            label.setBounds(0,0,d_label.width,d_label.height);
        }       
         
        public void setBackground(Color color) {
            if (color instanceof ColorUIResource)
            color = null;
            super.setBackground(color);
        }
        
   }
 
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addNewButton;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel closePanel;
    private javax.swing.JButton deleteAllButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton displayButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanelForButtons;
    private javax.swing.JPanel jPanelForGeneralButtons;
    private javax.swing.JPanel jPanelForSpectrumButtons;
    private javax.swing.JScrollPane jScrollForJTree;
    private javax.swing.JTree jTree;
    private javax.swing.JButton modifyButton;
    private javax.swing.JButton openButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
    
}
