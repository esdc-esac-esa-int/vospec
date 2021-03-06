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

import esavo.vospec.spectrum.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.*;
import java.lang.*;
import java.net.*;
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.table.*;


/**
 *
 * @author  ibarbarisi
 */
public class VOSpecInfo extends javax.swing.JDialog {
    
    public Spectrum spectrum;
    public String metadata;
    public javax.swing.JTextArea metaDataTextArea;
    
    public boolean wasDisposed;

    private Frame parent;
    
    /** Creates new form AioSpecInfo */
    public VOSpecInfo(java.awt.Frame parent, boolean modal) {

        super(parent, modal);
        wasDisposed = false;
        this.parent = parent;
        
        metaDataScrollPanel.setViewportView(metadataJTable);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - this.getWidth() /2, (screenSize.height / 2)- this.getHeight() /2);

    }
    
    public VOSpecInfo(Hashtable metadata, Spectrum spectrum) {


        initComponents();
        setSize(310,370);

        metadataJTable.setModel(new DefaultTableModel(new String[] { "Keyword","Value"},0));
    
        wasDisposed = false;
        
        metaDataTextArea = new javax.swing.JTextArea();
        metaDataTextArea.setBackground(new java.awt.Color(252,234,216));
        metaDataTextArea.setFont(new java.awt.Font("Dialog", 1, 11));
        metaDataScrollPanel.setViewportView(metaDataTextArea);

        metaPanel.add(metaDataScrollPanel);
        metaDataPanel.add(metaPanel);

        this.spectrum = spectrum;
	
        Hashtable   ht      = metadata;
        Vector vm           = spectrum.getMetadata_identifiers();
 
        //Enumeration keys    = ht.keys();
        
        String thisKey          = "";
        Object thisKeyValue;
             
        
        DefaultTableModel dfTM = (DefaultTableModel) metadataJTable.getModel();
      
        //while(keys.hasMoreElements()) {
        for(int i=0;i<vm.size();i++){
        
            //thisKey         	= (String)keys.nextElement();
            thisKey             = (String) vm.get(i);
            thisKeyValue    	= ht.get(thisKey);
            
            Vector rowVector = new Vector();
            rowVector.add(thisKey);
            rowVector.add(thisKeyValue);
            
            dfTM.addRow(rowVector);            
        }

        metadataJTable.setModel(dfTM);
        metaDataScrollPanel.setViewportView(metadataJTable);
        metadataJTable.repaint();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - this.getWidth() /2, (screenSize.height / 2)- this.getHeight() /2);
    }
 
    public void setPrincipal(boolean isPrincipal) {
        if(isPrincipal) metadataJTable.setBackground(new Color(255,255,255));
        else            metadataJTable.setBackground(new Color(204,204,204));
    } 
     
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        metaDataPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        exitButton = new javax.swing.JToggleButton();
        metaPanel = new javax.swing.JPanel();
        metaDataScrollPanel = new javax.swing.JScrollPane();
        metadataJTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Info");
        setAlwaysOnTop(true);

        metaDataPanel.setBackground(new java.awt.Color(240, 236, 236));
        metaDataPanel.setPreferredSize(new java.awt.Dimension(310, 340));
        metaDataPanel.setLayout(new java.awt.BorderLayout(5, 5));

        jPanel1.setBackground(new java.awt.Color(240, 236, 236));
        jPanel1.setPreferredSize(new java.awt.Dimension(300, 40));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jPanel1.add(saveButton);

        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        jPanel1.add(exitButton);

        metaDataPanel.add(jPanel1, java.awt.BorderLayout.SOUTH);

        metaPanel.setBackground(new java.awt.Color(240, 236, 236));
        metaPanel.setPreferredSize(new java.awt.Dimension(300, 290));
        metaPanel.setLayout(new java.awt.BorderLayout());

        metaDataScrollPanel.setBackground(new java.awt.Color(246, 244, 244));
        metaDataScrollPanel.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        metaDataScrollPanel.setPreferredSize(new java.awt.Dimension(290, 280));

        metadataJTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        metaDataScrollPanel.setViewportView(metadataJTable);

        metaPanel.add(metaDataScrollPanel, java.awt.BorderLayout.CENTER);

        metaDataPanel.add(metaPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(metaDataPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        
        saveSpectrum();
                          
    }//GEN-LAST:event_saveButtonActionPerformed

    public void saveSpectrum(){
        
        if(!spectrum.getNode().getDownloaded()){
            spectrum.run();
        }

        

        (new Thread() {

            public void run() {
                try {

                    BufferedInputStream di = null;
                    BufferedOutputStream fo = null;
                    byte[] b = new byte[1024];

                    String url = spectrum.getUrl();

                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Saving "+spectrum.getTitle());
                    int returnValue = fileChooser.showSaveDialog(parent);
                    File localFile = fileChooser.getSelectedFile();

                    if (returnValue != JFileChooser.APPROVE_OPTION) {
                        return;
                    }

                    URL urlT = new URL(url);
                    URLConnection urlConnection = urlT.openConnection();
                    urlConnection.connect();
                    di = new BufferedInputStream(urlConnection.getInputStream());
                    fo = new BufferedOutputStream(new FileOutputStream(localFile));

                    // copy data
                    while (-1 != di.read(b, 0, 1)) {
                        fo.write(b, 0, 1);
                    }

                    di.close();
                    fo.close();

                } catch (MalformedURLException e) {
                    System.err.println(e.toString());
                } catch (IOException e) {
                    System.err.println(e.toString());
                }
            }
        }).start();
        
    }


    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
         dispose();
    }//GEN-LAST:event_exitButtonActionPerformed
    
    public void dispose() {
        super.dispose();
        wasDisposed = true;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new VOSpecInfo(new javax.swing.JFrame(), true).show();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton exitButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel metaDataPanel;
    private javax.swing.JScrollPane metaDataScrollPanel;
    private javax.swing.JPanel metaPanel;
    private javax.swing.JTable metadataJTable;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables
    
}
