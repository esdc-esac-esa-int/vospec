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
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;


/**
 *
 * @author  ibarbarisi
 */

public class TsaServerParser extends javax.swing.JDialog {
    
    
    public      String              generalDesc;
    public      Vector              paramOptions;

    public      Hashtable           componentsHashtable;
    
    public      SsaServer           ss;
    public      boolean             checkMaxMin = false;
    public      boolean             isBestFit   = false;

    private FittingWindow fittingWindow;
    
    
    /** Creates new form TsaServerParser */
    public TsaServerParser(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }
       
    
    public TsaServerParser(FittingWindow window,SsaServer ss, boolean isBestFit,boolean modal) throws Exception {
        
        this(null,modal);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - this.getWidth() /2, (screenSize.height / 2)- this.getHeight() /2);

        fittingWindow = window;
        paramOptions            = new Vector();
        componentsHashtable     = new Hashtable();
        this.ss     = ss;
        this.isBestFit = isBestFit;
        

        SSAIngestor.populateInputParameters(ss);
        generalDesc=ss.getGeneralDesc();
        paramOptions = ss.getInputParams();

        
        init();
        
        String description = lineWrapper(generalDesc);
        bodyTextArea.append(description);


        
    }
    
    public void setCheckMaxMin() {
        checkMaxMin = true;
    }
    
    
    /** Initializes the applet TheoreticalSpectrumParser */
    public void init() {
        
        this.setSize(630,430);
        this.setResizable(true);
        this.setTitle(ss.getSsaName());
        
        initComponents();
        
        TsaServerParam  tsaServerParam;
        String          name;
        String          ucd;
        Vector          values;
        String          description;
        
        for (int ct=0; ct < paramOptions.size() ; ct++) {
            
            panelSeries = new JPanel();
            panelSeries.setSize(550,40);
            panelSeries.setBackground(new java.awt.Color(255, 255, 255));
            
            tsaServerParam = (TsaServerParam) paramOptions.elementAt(ct);
           
            JLabel      nameLabel           = new JLabel();
            JComboBox   optionComboBox      = new JComboBox();
            JCheckBox   fixCheckBox         = new JCheckBox();
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
            nameLabel.setPreferredSize(new java.awt.Dimension(65, 20));
            
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

                //System.out.println("IS NOT COMBO");

                valueTextField.setFont(new java.awt.Font("SansSerif", 1, 10));
                valueTextField.setForeground(new java.awt.Color(102, 102, 102));
                valueTextField.setPreferredSize(new java.awt.Dimension(70, 24));
                
                String stringValue = "";
                if(values.size() > 0) stringValue = (String) values.elementAt(0);
                valueTextField.setText(stringValue);
                
                valueTextField.setActionCommand(""+ct);
                valueTextField.addActionListener(lText);
                
                panelSeries.add(valueTextField);
                
                componentsHashtable.put(tsaServerParam, valueTextField);

                paramOptions.setElementAt(tsaServerParam,ct);
                
            }    

            // Description Button
            descriptionButton.setFont(new java.awt.Font("SansSerif", 1, 10));
            descriptionButton.setForeground(new java.awt.Color(102, 102, 102));
            descriptionButton.setText("description");
 
            description = tsaServerParam.getDescription();
            descriptionButton.setActionCommand(""+ct);
            descriptionButton.addActionListener(l);
            
            panelSeries.add(descriptionButton); 
           
            // Ucd Label
            ucdLabel.setFont(new java.awt.Font("SansSerif", 1, 10));
            ucdLabel.setForeground(new java.awt.Color(102, 102, 102));
            ucdLabel.setPreferredSize(new java.awt.Dimension(150, 20));
            
            ucd = tsaServerParam.getUcd();
            ucdLabel.setText(ucd);
            
            panelSeries.add(ucdLabel);
            
            if(this.isBestFit){
                //fixCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
                //fixCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
                //fixCheckBox.setToolTipText("Set as fixed value");
                //panelSeries.add(fixCheckBox);
            }
            
            //bodyPanel.add(jSeparator);
            panelContainer.add(panelSeries);
            jScrollPane2.setViewportView(panelContainer);
        }
            
        getContentPane().add(firstPanel, java.awt.BorderLayout.CENTER);
       
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

        firstPanel = new javax.swing.JPanel();
        bodyPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        bodyTextArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        panelContainer = new javax.swing.JPanel();
        panelSeries = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        selectButton = new javax.swing.JButton();
        exitButton = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        firstPanel.setToolTipText("");
        firstPanel.setPreferredSize(new java.awt.Dimension(620, 410));
        firstPanel.setLayout(new java.awt.BorderLayout());

        bodyPanel.setBackground(new java.awt.Color(233, 229, 229));
        bodyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Choose the starting parameters for this model", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 1, 10), new java.awt.Color(102, 102, 102))); // NOI18N
        bodyPanel.setForeground(new java.awt.Color(233, 229, 229));
        bodyPanel.setAutoscrolls(true);
        bodyPanel.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        bodyTextArea.setBackground(new java.awt.Color(233, 229, 229));
        bodyTextArea.setEditable(false);
        bodyTextArea.setFont(new java.awt.Font("SansSerif", 0, 10)); // NOI18N
        bodyTextArea.setForeground(new java.awt.Color(102, 102, 102));
        bodyTextArea.setLineWrap(true);
        bodyTextArea.setMaximumSize(new java.awt.Dimension(180, 80));
        bodyTextArea.setPreferredSize(new java.awt.Dimension(550, 40));
        jScrollPane1.setViewportView(bodyTextArea);

        bodyPanel.add(jScrollPane1, java.awt.BorderLayout.NORTH);

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setPreferredSize(new java.awt.Dimension(540, 250));

        panelContainer.setBackground(new java.awt.Color(255, 255, 255));
        panelContainer.setLayout(new javax.swing.BoxLayout(panelContainer, javax.swing.BoxLayout.Y_AXIS));

        panelSeries.setBackground(new java.awt.Color(255, 255, 255));
        panelContainer.add(panelSeries);

        jScrollPane2.setViewportView(panelContainer);

        bodyPanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        firstPanel.add(bodyPanel, java.awt.BorderLayout.CENTER);
        bodyPanel.getAccessibleContext().setAccessibleName("");

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        selectButton.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        selectButton.setForeground(new java.awt.Color(102, 102, 102));
        selectButton.setText("Start");
        selectButton.setMaximumSize(new java.awt.Dimension(70, 23));
        selectButton.setMinimumSize(new java.awt.Dimension(70, 23));
        selectButton.setPreferredSize(new java.awt.Dimension(70, 23));
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });
        jPanel1.add(selectButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 0, -1, -1));

        exitButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        exitButton.setForeground(new java.awt.Color(112, 111, 111));
        exitButton.setText("Cancel");
        exitButton.setMaximumSize(new java.awt.Dimension(70, 23));
        exitButton.setMinimumSize(new java.awt.Dimension(70, 23));
        exitButton.setPreferredSize(new java.awt.Dimension(70, 23));
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        jPanel1.add(exitButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 0, -1, -1));

        firstPanel.add(jPanel1, java.awt.BorderLayout.SOUTH);

        getContentPane().add(firstPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    
    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        ss = null;
        dispose();
    }//GEN-LAST:event_exitButtonActionPerformed
    
    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectButtonActionPerformed
                
        TsaServerParam tsaServerParam;
        String value;
        
        for (int ct=0; ct < paramOptions.size() ; ct++) {
            tsaServerParam = (TsaServerParam) paramOptions.elementAt(ct);
            
            value = tsaServerParam.getSelectedValue();
            if(!tsaServerParam.getIsCombo()) {
                value = ((JTextField) componentsHashtable.get(tsaServerParam)).getText();
            }
            tsaServerParam.setSelectedValue(value);
            
            if(!tsaServerParam.getMinString().equals("")) {
                try {
                    double minValue = (new Double(tsaServerParam.getMinString())).doubleValue();
                    double valueValue = (new Double(tsaServerParam.getSelectedValue())).doubleValue();
                    if(minValue > valueValue){
                        JOptionPane.showMessageDialog(this, "Parameter " + tsaServerParam.getName() + " should be greater than " + minValue);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            if(!tsaServerParam.getMaxString().equals("")) {
                try {
                    double maxValue = (new Double(tsaServerParam.getMaxString())).doubleValue();
                    double valueValue = (new Double(tsaServerParam.getSelectedValue())).doubleValue();
                    if(maxValue < valueValue){
                        JOptionPane.showMessageDialog(this, "Parameter " + tsaServerParam.getName() + " should be minor than " + maxValue);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
        }

        //getUrl(ss.getSsaUrl(),paramOptions);

        this.fittingWindow.launchBestFit();

        dispose();
        
    }//GEN-LAST:event_selectButtonActionPerformed
    
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
    private javax.swing.JTextArea bodyTextArea;
    private javax.swing.JToggleButton exitButton;
    private javax.swing.JPanel firstPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel panelContainer;
    private javax.swing.JPanel panelSeries;
    private javax.swing.JButton selectButton;
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
