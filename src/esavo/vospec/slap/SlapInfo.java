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
package esavo.vospec.slap;


/**
 *
 * @author  ibarbarisi
 */
public class SlapInfo extends javax.swing.JDialog {
    
    public Line line;
    public String metadata;
    public javax.swing.JTextArea metaDataTextArea;
    
    public boolean wasDisposed;

    
    /** Creates new form SlapInfo */
    public SlapInfo(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        wasDisposed = false;   
    }
    
     public SlapInfo(Line line) {
        initComponents();
        setSize(310,370);
        setResizable(true);

        wasDisposed = false;
        
        metaDataTextArea = new javax.swing.JTextArea();
        metaDataTextArea.setBackground(new java.awt.Color(252,234,216));
        metaDataTextArea.setFont(new java.awt.Font("Dialog", 1, 11));
        metaDataScrollPanel.setViewportView(metaDataTextArea);

        metaPanel.add(metaDataScrollPanel);
        metaDataPanel.add(metaPanel);
           
        this.line = line; 
        metadata = decode(line.getString());
        
        /*String[] metaForm = metadata.split("\n");
        for (int i=0;i<metaForm.length;i++){
            String[] metaDiv = metaForm[i].split(":");
            if (metaDiv[0].equals("ads_code")){
                String newAds = metaDiv[1];  
            } 
        }
         
        */
         metaDataTextArea.setText(this.metadata);
    }
    
     public String decode(String value) {
        value = value.replaceAll("&lt;", "<");
        value = value.replaceAll("&gt;", ">");
        value = value.replaceAll("&apos;", "'");
        value = value.replaceAll("&quot;", "\"");
        value = value.replaceAll("&amp;", "&");

        return value;
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        metaDataPanel = new javax.swing.JPanel();
        metaPanel = new javax.swing.JPanel();
        metaDataScrollPanel = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        exitButton = new javax.swing.JToggleButton();

        getContentPane().setLayout(new java.awt.GridLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Info");
        metaDataPanel.setLayout(new java.awt.BorderLayout(5, 5));

        metaDataPanel.setBackground(new java.awt.Color(240, 236, 236));
        metaPanel.setLayout(new java.awt.GridLayout());

        metaPanel.setBackground(new java.awt.Color(240, 236, 236));
        metaDataScrollPanel.setBackground(new java.awt.Color(246, 244, 244));
        metaDataScrollPanel.setFont(new java.awt.Font("Dialog", 0, 11));
        metaDataScrollPanel.setPreferredSize(new java.awt.Dimension(290, 280));
        metaPanel.add(metaDataScrollPanel);

        metaDataPanel.add(metaPanel, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        jPanel1.setBackground(new java.awt.Color(240, 236, 236));
        jPanel1.setPreferredSize(null);
        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        jPanel1.add(exitButton);

        metaDataPanel.add(jPanel1, java.awt.BorderLayout.SOUTH);

        getContentPane().add(metaDataPanel);

    }//GEN-END:initComponents

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
        new SlapInfo(new javax.swing.JFrame(), true).show();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton exitButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel metaDataPanel;
    private javax.swing.JScrollPane metaDataScrollPanel;
    private javax.swing.JPanel metaPanel;
    // End of variables declaration//GEN-END:variables
    
}
