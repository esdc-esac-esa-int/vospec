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
import java.lang.*;
import java.net.*;

/**
 *
 * @author  ibarbarisi
 */
public class VOSpecHowTo extends javax.swing.JDialog {
    
    public VOSpecDetached aioSpecToolDetached;

    
    /** Creates new form AioSpecHowTo */
    public VOSpecHowTo(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
     
    
    public VOSpecHowTo(VOSpecDetached aioSpecToolDetached) {
        this.setSize(310,350);
        this.aioSpecToolDetached = aioSpecToolDetached;
        initComponents();
        this.setResizable(false); 
        this.setTitle("VOSpec Manual");
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

        howToPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        aboutContainerPanel = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        exitButton = new javax.swing.JToggleButton();
        exitButton1 = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        howToPanel.setBackground(new java.awt.Color(240, 236, 236));
        howToPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        howToPanel.setPreferredSize(new java.awt.Dimension(400, 160));
        howToPanel.setRequestFocusEnabled(false);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logoAbout.jpg"))); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(390, 50));
        howToPanel.add(jLabel1);

        aboutContainerPanel.setBackground(new java.awt.Color(240, 236, 236));
        aboutContainerPanel.setPreferredSize(new java.awt.Dimension(390, 110));
        aboutContainerPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 10));

        jSeparator1.setPreferredSize(new java.awt.Dimension(380, 2));
        aboutContainerPanel.add(jSeparator1);

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(102, 102, 102));
        jLabel7.setText("The VOSpec Manual can be accessed at:");
        jLabel7.setPreferredSize(new java.awt.Dimension(245, 13));
        aboutContainerPanel.add(jLabel7);

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(102, 102, 102));
        jLabel3.setText("http://esavo.esa.int/VOSpecManual");
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        aboutContainerPanel.add(jLabel3);

        exitButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        exitButton.setForeground(new java.awt.Color(112, 111, 111));
        exitButton.setText("Close");
        exitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitButtonMouseClicked(evt);
            }
        });
        aboutContainerPanel.add(exitButton);

        exitButton1.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        exitButton1.setForeground(new java.awt.Color(112, 111, 111));
        exitButton1.setText("GO");
        exitButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButton1ActionPerformed(evt);
            }
        });
        exitButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitButton1MouseClicked(evt);
            }
        });
        aboutContainerPanel.add(exitButton1);

        howToPanel.add(aboutContainerPanel);

        getContentPane().add(howToPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButton1ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_exitButton1ActionPerformed

    private void exitButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitButton1MouseClicked
// TODO add your handling code here:
    }//GEN-LAST:event_exitButton1MouseClicked

    private void exitButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitButtonMouseClicked
        dispose();
    }//GEN-LAST:event_exitButtonMouseClicked

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        showURL("howTo","http://esavo.esac.esa.int/VOSpecManual");
    }//GEN-LAST:event_jLabel3MouseClicked
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new VOSpecHowTo(new javax.swing.JFrame(), true).show();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel aboutContainerPanel;
    private javax.swing.JToggleButton exitButton;
    private javax.swing.JToggleButton exitButton1;
    private javax.swing.JPanel howToPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
    //============================================================================
  /**
   * NAME: showURL
   *
   * PURPOSE:
   *
   * Method which actually displays an URL on a browser window, if 
   * the applet is not running under Netscape or Explorer it 
   * tries to reuse a running netscape and if it fails it starts
   * one.
   *
   * INPUT PARAMETERS:
   *
   * @param url the URL to show
   * @param browserID the ID of the browser window to use for the display
   *
   * OUTPUT PARAMETERS: None.
   *
   * RETURN VALUE: None.
   *
   */
  public void showURL(String browserID,String urlString) {
  	try{
		URL url=new URL(urlString);
		
		System.out.println("URL is: "+ url.toString());

     		aioSpecToolDetached.parentAppletContext.showDocument(url, browserID);
		
	} catch (Exception e) {
		System.out.println("Malformed URL: "+e);
		e.printStackTrace();
	}
  }
  //==========================================================================
    
}
