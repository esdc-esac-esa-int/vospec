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


import java.awt.Dimension;
import java.awt.Toolkit;

/**
 *
 * @author  jsalgado
 */
public class DeReddeningWindow extends javax.swing.JDialog {
    
    VOSpecDetached aiospectooldetached;

    /** Creates new form DeReddeningWindow */
    public DeReddeningWindow(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        aiospectooldetached=(VOSpecDetached)parent;
        initComponents();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - this.getWidth() /2, (screenSize.height / 2)- this.getHeight() /2);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        calzettiPanel = new javax.swing.JPanel();
        E_BV_Calzetti_Label = new javax.swing.JLabel();
        E_BV_Calzetti = new javax.swing.JTextField();
        R_V_Calzetti_Label = new javax.swing.JLabel();
        R_V_Calzetti = new javax.swing.JTextField();
        check_Calzetti = new javax.swing.JCheckBox();
        description_Calzetti = new javax.swing.JTextPane();
        jLabel1 = new javax.swing.JLabel();
        cardelliPanel = new javax.swing.JPanel();
        E_BV_Cardelli_Label = new javax.swing.JLabel();
        E_BV_Cardelli = new javax.swing.JTextField();
        R_V_Cardelli_Label = new javax.swing.JLabel();
        R_V_Cardelli = new javax.swing.JTextField();
        check_Cardelli = new javax.swing.JCheckBox();
        description_Cardelli = new javax.swing.JTextPane();
        jLabel2 = new javax.swing.JLabel();
        LMCPanel = new javax.swing.JPanel();
        E_BV_LMC_Label = new javax.swing.JLabel();
        E_BV_LMC = new javax.swing.JTextField();
        R_V_LMC_Label = new javax.swing.JLabel();
        R_V_LMC = new javax.swing.JTextField();
        check_LMC = new javax.swing.JCheckBox();
        description_LMC = new javax.swing.JTextPane();
        jLabel3 = new javax.swing.JLabel();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jTabbedPane1.setMaximumSize(new java.awt.Dimension(290, 200));
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(290, 200));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(290, 200));

        calzettiPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        E_BV_Calzetti_Label.setText("E BV");
        calzettiPanel.add(E_BV_Calzetti_Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 30, 20));
        calzettiPanel.add(E_BV_Calzetti, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 40, -1));

        R_V_Calzetti_Label.setText("R V");
        calzettiPanel.add(R_V_Calzetti_Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 30, 20));

        R_V_Calzetti.setText("4.05");
        calzettiPanel.add(R_V_Calzetti, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 40, -1));

        check_Calzetti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check_CalzettiActionPerformed(evt);
            }
        });
        calzettiPanel.add(check_Calzetti, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, -1, -1));

        description_Calzetti.setText("Calzetti et al. (2000) [2000ApJ...533..682]\n\nThis reddening law is appropiate for spectra of  galaxies where massive stars dominate the radiation output\n\nValidity range: 912 to 22000 Angstroms\n\n");
        description_Calzetti.setMaximumSize(new java.awt.Dimension(270, 120));
        description_Calzetti.setMinimumSize(new java.awt.Dimension(270, 120));
        description_Calzetti.setPreferredSize(new java.awt.Dimension(270, 120));
        description_Calzetti.setRequestFocusEnabled(false);
        calzettiPanel.add(description_Calzetti, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        jLabel1.setText("Apply");
        calzettiPanel.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 10, -1, -1));

        jTabbedPane1.addTab("Calzetti", calzettiPanel);

        cardelliPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        E_BV_Cardelli_Label.setText("E BV");
        cardelliPanel.add(E_BV_Cardelli_Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 30, 20));
        cardelliPanel.add(E_BV_Cardelli, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 40, -1));

        R_V_Cardelli_Label.setText("R V");
        cardelliPanel.add(R_V_Cardelli_Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 30, 20));

        R_V_Cardelli.setText("3.1");
        cardelliPanel.add(R_V_Cardelli, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 40, -1));

        check_Cardelli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check_CardelliActionPerformed(evt);
            }
        });
        cardelliPanel.add(check_Cardelli, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, -1, -1));

        description_Cardelli.setText("Cardelli et al.  [1989ApJ...345..245C] \nO'Donnell [1994ApJ...422..1580]\n\nCardelli et al. (1989) reddening law, including an update for the near-UV by O'Donnell (1994)\nValidity range: 912 to 35000 Angstroms");
        description_Cardelli.setMaximumSize(new java.awt.Dimension(270, 120));
        description_Cardelli.setMinimumSize(new java.awt.Dimension(270, 120));
        description_Cardelli.setPreferredSize(new java.awt.Dimension(270, 120));
        description_Cardelli.setRequestFocusEnabled(false);
        cardelliPanel.add(description_Cardelli, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        jLabel2.setText("Apply");
        cardelliPanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 10, -1, -1));

        jTabbedPane1.addTab("Cardelli-O'Donnel", cardelliPanel);

        LMCPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        E_BV_LMC_Label.setText("E BV");
        LMCPanel.add(E_BV_LMC_Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 30, 20));
        LMCPanel.add(E_BV_LMC, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 40, -1));

        R_V_LMC_Label.setText("R V");
        LMCPanel.add(R_V_LMC_Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 30, 20));

        R_V_LMC.setText("3.1");
        LMCPanel.add(R_V_LMC, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 40, -1));

        check_LMC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check_LMCActionPerformed(evt);
            }
        });
        LMCPanel.add(check_LMC, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, -1, -1));

        description_LMC.setText("Howarth [1983MNRAS.203...301H]\n\nExtragalactic extinction. Based on star samples of the Large Magellanic Cloud (LMC) by Nandy et al. and Koorneff & Code (1981)\n\nValidity range:  <=47600 Angstroms");
        description_LMC.setMaximumSize(new java.awt.Dimension(270, 120));
        description_LMC.setMinimumSize(new java.awt.Dimension(270, 120));
        description_LMC.setPreferredSize(new java.awt.Dimension(270, 120));
        description_LMC.setRequestFocusEnabled(false);
        LMCPanel.add(description_LMC, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        jLabel3.setText("Apply");
        LMCPanel.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 10, -1, -1));

        jTabbedPane1.addTab("LMC", LMCPanel);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        aiospectooldetached.setDeRedSelected(false);
        aiospectooldetached.repaintSpectra();
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void check_CalzettiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check_CalzettiActionPerformed
        aiospectooldetached.repaintSpectra();
    }//GEN-LAST:event_check_CalzettiActionPerformed

    private void check_CardelliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check_CardelliActionPerformed
        aiospectooldetached.repaintSpectra();
    }//GEN-LAST:event_check_CardelliActionPerformed

    private void check_LMCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check_LMCActionPerformed
        aiospectooldetached.repaintSpectra();
    }//GEN-LAST:event_check_LMCActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new DeReddeningWindow(new javax.swing.JFrame(), true).show();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField E_BV_Calzetti;
    private javax.swing.JLabel E_BV_Calzetti_Label;
    private javax.swing.JTextField E_BV_Cardelli;
    private javax.swing.JLabel E_BV_Cardelli_Label;
    private javax.swing.JTextField E_BV_LMC;
    private javax.swing.JLabel E_BV_LMC_Label;
    private javax.swing.JPanel LMCPanel;
    private javax.swing.JTextField R_V_Calzetti;
    private javax.swing.JLabel R_V_Calzetti_Label;
    private javax.swing.JTextField R_V_Cardelli;
    private javax.swing.JLabel R_V_Cardelli_Label;
    private javax.swing.JTextField R_V_LMC;
    private javax.swing.JLabel R_V_LMC_Label;
    private javax.swing.JPanel calzettiPanel;
    private javax.swing.JPanel cardelliPanel;
    private javax.swing.JCheckBox check_Calzetti;
    private javax.swing.JCheckBox check_Cardelli;
    private javax.swing.JCheckBox check_LMC;
    private javax.swing.JTextPane description_Calzetti;
    private javax.swing.JTextPane description_Cardelli;
    private javax.swing.JTextPane description_LMC;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables

    public boolean getCalzetti_Check() {
        return check_Calzetti.isSelected();     
    }
    public double getCalzetti_EBV() {
        return getDoubleValue(E_BV_Calzetti);
    }
    public double getCalzetti_RV() {
        return getDoubleValue(R_V_Calzetti);
    }

    
    public boolean getCardelli_Check() {
        return check_Cardelli.isSelected();     
    }
    public double getCardelli_EBV() {
        return getDoubleValue(E_BV_Cardelli);
    }
    public double getCardelli_RV() {
        return getDoubleValue(R_V_Cardelli);
    }

    
    
    public boolean getLMC_Check() {
        return check_LMC.isSelected();     
    }
     public double getLMC_EBV() {
        return getDoubleValue(E_BV_LMC);
    }
    public double getLMC_RV() {
        return getDoubleValue(R_V_LMC);
    }
   
    
    
    
    public double getDoubleValue(javax.swing.JTextField textField) {
        double returnValue = 0.;
        try {
            returnValue = (new Double(textField.getText())).doubleValue();
            return returnValue;
        } catch(Exception e) {
            return returnValue;
        }
    }
}
