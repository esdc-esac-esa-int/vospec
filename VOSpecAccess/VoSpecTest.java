/*
 * VoSpecTest.java
 *
 * Created on November 11, 2004, 2:49 PM
 */

package esavo.vospec.standalone;


import esavo.vospec.dataingestion.SsaServer;
import esavo.vospec.dataingestion.SsaServerList;
import esavo.vospec.spectrum.Spectrum;
import esavo.vospec.spectrum.SpectrumSet;
import esavo.vospec.spectrum.Unit;


/**
 *
 * @author  jsalgado
 */
public class VoSpecTest extends javax.swing.JFrame {

    public VoSpec voSpec;

    /** Creates new form VoSpecTest */
    public VoSpecTest() {
        initComponents();
         voSpec = new VoSpec();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        addPositionButton = new javax.swing.JButton();
        addSpectraButton = new javax.swing.JButton();
        toFrontButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        showButton = new javax.swing.JButton();
        hideButton = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.FlowLayout());

        addPositionButton.setText("Add Position/Size/SSA ServerList");
        addPositionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPositionButtonActionPerformed(evt);
            }
        });

        getContentPane().add(addPositionButton);

        addSpectraButton.setText("Add spectra directly");
        addSpectraButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSpectraButtonActionPerformed(evt);
            }
        });

        getContentPane().add(addSpectraButton);

        toFrontButton.setText("ToFront");
        toFrontButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toFrontButtonActionPerformed(evt);
            }
        });

        getContentPane().add(toFrontButton);

        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        getContentPane().add(resetButton);

        showButton.setText("Show");
        showButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showButtonActionPerformed(evt);
            }
        });

        getContentPane().add(showButton);

        hideButton.setText("Hide");
        hideButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hideButtonActionPerformed(evt);
            }
        });

        getContentPane().add(hideButton);

    }//GEN-END:initComponents

    private void hideButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hideButtonActionPerformed
        voSpec.hide();
    }//GEN-LAST:event_hideButtonActionPerformed

    private void showButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showButtonActionPerformed
        voSpec.show();
    }//GEN-LAST:event_showButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        voSpec.reset();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void addSpectraButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSpectraButtonActionPerformed

 // Next needed if you do not conserve a static reference
//      VoSpec voSpec = new VoSpec();


//This object will contain the spectra to be displayed in VOSpec
        SpectrumSet spectrumSet = new SpectrumSet();


        Spectrum spectrum1 = new Spectrum();
        spectrum1.setUrl("http://pma.iso.vilspa.esa.es:8080/aio/jsp/product.jsp?obsno=40001501&protocol=HTTP&name=swaa&level=Custom");
        spectrum1.setWaveLengthColumnName("SWAAWAVE");
        spectrum1.setFluxColumnName("SWAAFLUX");
        spectrum1.setUnits(new Unit("L","1.E-6","MT-2","1.E-26"));
        spectrum1.setTitle("ISO SWS01  Spectrum Target: M31_BULGE");
        spectrum1.setRa("10.691809995");
        spectrum1.setDec("41.27003");
	spectrum1.setFormat("spectrum/fits");


        Spectrum spectrum2 = new Spectrum();
        spectrum2.setUrl("http://pma.iso.vilspa.esa.es:8080/aio/jsp/product.jsp?obsno=57702107&protocol=HTTP&name=swaa&level=Custom");
        spectrum2.setWaveLengthColumnName("SWAAWAVE");
        spectrum2.setFluxColumnName("SWAAFLUX");
        spectrum2.setUnits(new Unit("L","1.E-6","MT-2","1.E-26"));
        spectrum2.setTitle("ISO SWS01  Spectrum Target: EG AND");
        spectrum2.setRa("11.15409");
        spectrum2.setDec("40.67945");
	spectrum2.setFormat("spectrum/fits");


        spectrumSet.addSpectrum(0,spectrum1);
        spectrumSet.addSpectrum(1,spectrum2);

// This particular case will contain a spectrum with only the url and the title
        Spectrum spectrum3 = new Spectrum();
        spectrum3.setTitle("IUE spectrum");
        spectrum3.setUrl("http://sdc.laeff.esa.es:80/cgi-ines/SingleDownload?filename=SWP27041LL.FITS");
        spectrumSet.addSpectrum(2,spectrum3);

	voSpec.loadSpectrumSet("",spectrumSet);
	voSpec.show();

    }//GEN-LAST:event_addSpectraButtonActionPerformed

    private void toFrontButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toFrontButtonActionPerformed
        voSpec.toFront();
    }//GEN-LAST:event_toFrontButtonActionPerformed

    private void addPositionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPositionButtonActionPerformed

// Next needed if you do not conserve a static reference
//      VoSpec voSpec = new VoSpec();


// Setting the coordinates and size
        voSpec.setRa("10");
	voSpec.setDec("41");
	voSpec.setSize("1.");

// This VOSpec object will contain the Ssa Servers
	SsaServerList ssaServerList = new SsaServerList();

	SsaServer ssaServer1 = new SsaServer();
	ssaServer1.setSsaName("ISO");
	ssaServer1.setSsaUrl("http://pma.iso.vilspa.esa.es:8080/aio/jsp/siap.jsp?imageType=spectrum");
	ssaServerList.addSsaServer(0,ssaServer1);

        SsaServer ssaServer2 = new SsaServer();
	ssaServer2.setSsaName("INES");
	ssaServer2.setSsaUrl("http://sdc.laeff.esa.es/ines/jsp/siap.jsp?");
	ssaServerList.addSsaServer(1,ssaServer2);

	voSpec.setSsaServerList(ssaServerList);

	voSpec.show();

    }//GEN-LAST:event_addPositionButtonActionPerformed


   public static void main (String[] args) {
        VoSpecTest voSpecTest = new VoSpecTest();
        voSpecTest.setSize(162*2,200);
        voSpecTest.setVisible(true);
   }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addPositionButton;
    private javax.swing.JButton hideButton;
    private javax.swing.JButton addSpectraButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton showButton;
    private javax.swing.JButton toFrontButton;
    // End of variables declaration//GEN-END:variables

}
