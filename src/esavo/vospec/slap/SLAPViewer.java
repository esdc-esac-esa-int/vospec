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

/*
 * VOSpecWrapper.java
 *
 * Created on December 17, 2004, 11:56 AM
 */

import esavo.vospec.dataingestion.SSAIngestor;
import esavo.vospec.main.*;
import esavo.vospec.spectrum.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.*;
import javax.swing.tree.*;


/**
 *
 * @author  ibarbarisi
 */
public class SLAPViewer extends javax.swing.JDialog {
    
    public VOSpecDetached aioSpecToolDetached;
    
    public int oldSelectedRow = -1;
    
    public double w1;
    public double w2;
    
    public String wInit;
    public String wEnd;
    
    public SlapServerList 	slapServerList;
    
    
    public Node 		rootNode = new Node("Server Selector");
    public Node 		slapNode = new Node("SLAP Services");
    public Node 		titleNode;
    public Node 		urlNode;
    public String 		valueSelected;
    public JFileChooser 	fileChooser;
    
    public JTree 		serverListTree;
    public TreePath 		path;
    
    public String 		logo = "../esavo/vospec/images/esa.gif";
    
    public int 			number_server;
    public Vector 		listTitle;
    public Vector 		listUrl;
    public Vector 		listTitleSelected;
    public Vector 		listUrlSelected;
    
    public JPanel 		buttonPanel;
    
    public LineSet 		lineSet;
    public Vector               infoWindow;
    public Vector               rowsClicked;
    public SlapInfo             slapInfo;
    
    
    public ExtendedJTable       selectedJTable;
    public JScrollPane          selectedJScrollPane;
    
    
    public File                 wrapperName;
    public String               type;
    public String               wave;
    public String               flux;
    public Vector               data;
    public String               waveUnit;
    public String               fluxUnit;
    public JFileChooser         fileChooserWrapper;
    public JFileChooser         fileChooserSpectrum;
    public JLabel               label;
    public Vector               waveVector;
    public Vector               fluxVector;
    public SpectrumSet          spectrumSet;
    
    public LineSet              lineSetOrdered;
    
    int ct;
    
    public Vector jTableVector     = new Vector();
    public Vector jScrollPaneVector    = new Vector();
    
    
    /** Creates new form VOSpecWrapper */
    public SLAPViewer() {
        initComponents();        
    }
    
    public SLAPViewer(VOSpecDetached aioSpecToolDetached) {
        this();
        this.setSize(520, 700);
        this.setResizable(true);
        this.setTitle("Slap Viewer");
        this.aioSpecToolDetached = aioSpecToolDetached;
        selectedJTable = new ExtendedJTable();
        selectedJTable.setEditable(false);
        
        getTree();
        infoWindow = new Vector();
        rowsClicked = new Vector();
        
    }
    
    public void getTree(){
        
        try {
            slapServerList = SSAIngestor.getSlapServerList();
            
        }catch (Exception e){
            System.out.println("Error getting SlapServerList ");
            e.printStackTrace();
        }
        
        number_server = (slapServerList.getSlapServerList()).size();
        
        String urlServer;
        String title;
        boolean isPresent;
        listTitle = new Vector();
        listUrl = new Vector();
        
        String currentTitle;
        
        for (int z=0;z<number_server;z++) {
            SlapServer server = (SlapServer) slapServerList.getSlapServer(z);
            urlServer = server.getSlapUrl();
            title = server.getSlapName();
            
            listTitle.addElement(title);
            listUrl.addElement(urlServer);
            
        }
        
        rootNode.add(slapNode);
        
        for (int z=0;z<number_server;z++) {
            
            SlapServer server = (SlapServer) slapServerList.getSlapServer(z);
            
            isPresent=false;
            currentTitle = (String)listTitle.elementAt(z);
            
            //Here check if the parent has one or more childs
            if (z<1) {
                titleNode = new Node(currentTitle);
                slapNode.add(titleNode);
                urlNode = new Node(listUrl.elementAt(z));
                titleNode.add(urlNode);
            }else{
                for (int j=0;j<listTitle.size()-1;j++) {
                    if ( j!=z ) {
                        if (currentTitle.equals(listTitle.elementAt(j))) {
                            isPresent=true;
                        }
                    }
                }
                //isPresent means that the parent is been already written once
                if (isPresent) {
                    urlNode = new Node(listUrl.elementAt(z));
                    titleNode.add(urlNode);
                }else{
                    titleNode = new Node(listTitle.elementAt(z));
                    slapNode.add(titleNode);
                    urlNode = new Node(listUrl.elementAt(z));
                    titleNode.add(urlNode);
                }
            }
            
        }
        
        serverListTree = new JTree(rootNode);
        
        serverListTree.setCellRenderer(new CheckRenderer());
        serverListTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION
                );
        serverListTree.putClientProperty("JTree.lineStyle", "Angled");
        serverListTree.addMouseListener(new NodeSelectionListener(serverListTree));
        
        buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new java.awt.Dimension(505, 40));
        buttonPanel.setBackground(new java.awt.Color(244, 241, 239));
        JButton selectButton = new JButton();
        
        //Create the scroll pane and add the tree to it.
        JScrollPane treeView = new JScrollPane(serverListTree);
        treeView.setPreferredSize(new Dimension(505, 180));
        
        //getContentPane().setLayout(new java.awt.FlowLayout());
        
        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        buttonPanel.setBorder(new javax.swing.border.EtchedBorder());
        
        selectButton.setFont(new java.awt.Font("Dialog", 1, 10));
        selectButton.setForeground(new java.awt.Color(102, 102, 102));
        selectButton.setText("Select");
        
        selectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                oldSelectedRow = -1;
                
                setServerSelectedList();
                showResults();
            }
        });
        selectorPanel.add(treeView, java.awt.BorderLayout.NORTH);
        buttonPanel.add(selectButton);
        selectorPanel.add(buttonPanel, java.awt.BorderLayout.SOUTH);
        
    }
        
    
    public class MarkerCellRenderer extends DefaultTableCellRenderer {
        
        JLabel label = null;
        
        public MarkerCellRenderer() {
            super();
            label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Color back = table.getBackground();
            
            if(row == oldSelectedRow) {
                if(isSelected) {
                    this.setBackground(new java.awt.Color(0, 160, 180));
                } else {
                    this.setBackground(new java.awt.Color(255, 255, 150));
                    
                }
            } else {
                if(isSelected) {
                    this.setBackground(table.getSelectionBackground());
                } else {
                    this.setBackground(table.getBackground());
                }
            }
            
            setFont(table.getFont());
            if(value != null) setText(value.toString());
            
            return this;
        }
        public void setToolTip(String toolTip) {
            if (toolTip != null) {
                label.setToolTipText(toolTip);
            }
        }
        
    }
    
    public void setSlapRequest(SlapRequest slapRequest) {
        
        this.w1 = slapRequest.getW1();
        this.w2 = slapRequest.getW2();
        
        wInit   = Double.toString(w1);
        wEnd    = Double.toString(w2);
        
        waveText.setText(wInit);
        waveText2.setText(wEnd);
        
    }
    
    
    public void showResults() {
        
        setWaitCursor();
        
        selectedJTable.addMouseListener(new java.awt.event.MouseAdapter() {
            
            public void mouseClicked(java.awt.event.MouseEvent e) {
                
                boolean exist = false;
                int pointer = 0;
                
                if (e.getClickCount()==2) {
                    
                    int sel = selectedJTable.getSelectedRow();
                    
                    Line line;
                    for(int i = 0; i < lineSet.size(); i++){
                        
                        line = lineSet.getLine(i);
                        if(i==sel){
                            //if it's been clicked on a new row put the other windows in a grey color
                            for (int j=0;j<infoWindow.size();j++){
                                if(j!=i){
                                    SlapInfo oldInfo = (SlapInfo) infoWindow.elementAt(j);
                                    oldInfo.metaDataTextArea.setBackground(new Color(204,204,204));
                                }
                            }
                            
                            // put the boolean exist=true if the row selected is already been selected once
                            for(int h=0;h<rowsClicked.size();h++){
                                String selString = sel + "";
                                if(selString.equals((String) rowsClicked.elementAt(h))){
                                    exist = true;
                                    pointer = h;
                                }
                                
                            }
                            
                            if(exist){
                                SlapInfo oldInfo = (SlapInfo) infoWindow.elementAt(pointer);
                                
                                if(oldInfo.wasDisposed) {
                                    oldInfo = new SlapInfo(line);
                                    infoWindow.add(pointer, oldInfo);
                                    oldInfo.setVisible(true);
                                }
                                
                                oldInfo.metaDataTextArea.setBackground(new Color(252,234,216));
                            }else{
                                slapInfo = new SlapInfo(line);
                                infoWindow.add(slapInfo);
                                rowsClicked.add(sel+"");
                                exist = false;
                                slapInfo.setVisible(true);
                            }
                            
                        }
                    }
                    
                }
            }
            
        });
        /*
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
         * */
        
        
        SlapUtils.setSLAPViewer(this);
        
        
        //TableColumn column = jTable.getColumnModel().getColumn(0);
        //column.setPreferredWidth(10);
        
        for (int i=0;i<slapServerList.getSlapServerList().size();i++) {
            
            SlapServer slapServer = slapServerList.getSlapServer(i);
            
            if(slapServer.getSelected()) {
                
                SlapThread slapThread = new SlapThread(this, slapServer);
                try {
                    new Thread(slapThread).start();
                } catch(Exception e) {
                    System.out.println("Problems launching thread for slapServer:" + slapServer.getSlapName());
                    e.printStackTrace();
                }
            }
        }
        setDefaultCursor();
        
        
    }
    
    public void showLineSet(String serverName, LineSet lineSet) throws Exception {
        
        
        DefaultTableModel   tableModel  = SlapUtils.getTableModel(lineSet);
        ExtendedJTable      jTable      = new ExtendedJTable();
        
        jTable.setEditable(false);
        jTable.setModel(tableModel);
        
        String thisField = "";
        Vector fields = lineSet.getFields();
        for(int k = 0 ; k < fields.size(); k++) {
            
            thisField = (String) fields.elementAt(k);
            
            int columnIndex = getColumn(jTable, thisField);
            
            MarkerCellRenderer mk = new MarkerCellRenderer();
            mk.setToolTipText(lineSet.getPropertiesString(thisField));
            
            jTable.getColumnModel().getColumn(columnIndex).setCellRenderer(mk);
        }
        
        
        jTable.setPreferredSize(null);
        jTable.getTableHeader().setReorderingAllowed(false);
        
        jTable.repaint();
        
        JScrollPane jScrollPane = new javax.swing.JScrollPane(jTable);
        jScrollPane.setPreferredSize(new Dimension(580,70));
        
        
        jTableVector.add(jTable);
        jScrollPaneVector.add(jScrollPane);
        
        jTabbedPane1.add(serverName, jScrollPane);
        
        jTabbedPaneAction();
        
        
        if(this.isVisible()) {
            repaint();
            show();
        }
        setDefaultCursor();
        
    }
    
    public void errorsFromSLAPSearch(String errorMessage) {
        JOptionPane.showMessageDialog(this,errorMessage);
    }
    
    public LineSet orderLineSet(LineSet lineSet) {
        
        SortedMap map       = new TreeMap();
        LineSet lineSetNew  = new LineSet();
        
        lineSetNew.setFields(lineSet.getFields());
        
        for (int i=0;i<lineSet.size();i++){
            
            Line line = lineSet.getLine(i);
            String wave =  line.getValue("ldm:Line.wavelength");
            map.put(wave, line);
        }
        
        Iterator it = map.keySet().iterator();
        String wave = null;
        
        Line lineNew;
        while(it.hasNext()){
            lineNew = (Line)    it.next();
            lineSetNew.addLine(lineNew);
        }
        
        return lineSetNew;
    }
    
    public void setWaitCursor() {
        Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
        setCursor(hourglassCursor);
    }
    
    public void setDefaultCursor() {
        Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        setCursor(normalCursor);
    }
    
    public TreePath findByName(JTree tree, String[] names) {
        TreeNode root = (TreeNode)tree.getModel().getRoot();
        return find2(tree, new TreePath(root), names, 0, true);
    }
    
    private TreePath find2(JTree tree, TreePath parent, Object[] nodes, int depth, boolean byName) {
        TreeNode node = (TreeNode)parent.getLastPathComponent();
        Object o = node;
        
        // If by name, convert node to a string
        if (byName) {
            o = o.toString();
        }
        
        // If equal, go down the branch
        if (o.equals(nodes[depth])) {
            // If at end, return match
            if (depth == nodes.length-1) {
                return parent;
            }
            
            // Traverse children
            if (node.getChildCount() >= 0) {
                for (Enumeration e=node.children(); e.hasMoreElements(); ) {
                    TreeNode n = (TreeNode)e.nextElement();
                    TreePath path = parent.pathByAddingChild(n);
                    TreePath result = find2(tree, path, nodes, depth+1, byName);
                    // Found a match
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }
    
    public void setVisible(boolean setVisible) {
        super.setVisible(setVisible);
        if(this.isVisible())this.repaint();
    }
    
    public void setServerSelectedList() {
        int q=0;
        int g=0;
        Enumeration enum1 = slapNode.breadthFirstEnumeration();
        
        SlapServerList slapServerList = new SlapServerList();
        
        while (enum1.hasMoreElements()) {
            
            Node node = (Node)enum1.nextElement();
            
            if (node.isSelected() && node.isLeaf()) {
                Float wave1;
                Float wave2;
                
                
                try {
                    wave1 	= new Float(w1);
                    wave2 	= new Float(w2);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Slap Server can not be selected for these wavelengths");
                    break;
                }
                
                SlapServer serverTemp = new SlapServer();
                serverTemp.setSelected(true);
                serverTemp.setSlapName(node.getParent().toString());
                
                String urlString = node.toString() + "WAVELENGTH="+w1+"/"+w2;
                serverTemp.setSlapUrl(urlString);
                
                slapServerList.addSlapServer(q,serverTemp);
                q++;
            }
        }
        
        this.slapServerList = slapServerList;
    }
    
    
    public SlapServerList getServerSelectedList() {
        return this.slapServerList;
    }
    
    public void resetAndShow() {
        this.setVisible(true);
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The ctent of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        containerPanel = new javax.swing.JPanel();
        northPanel = new javax.swing.JPanel();
        titlePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        selectorPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        addPanel = new javax.swing.JPanel();
        resetButton = new javax.swing.JButton();
        wavePanel = new javax.swing.JPanel();
        waveLabel1 = new javax.swing.JLabel();
        waveText = new javax.swing.JTextField();
        waveLabel2 = new javax.swing.JLabel();
        waveText2 = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jtablePanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        closePanel = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        containerPanel.setLayout(new java.awt.BorderLayout());

        containerPanel.setBackground(new java.awt.Color(244, 241, 239));
        containerPanel.setMinimumSize(new java.awt.Dimension(514, 700));
        containerPanel.setPreferredSize(new java.awt.Dimension(514, 700));
        northPanel.setLayout(new java.awt.BorderLayout());

        northPanel.setBackground(new java.awt.Color(244, 241, 239));
        northPanel.setMinimumSize(new java.awt.Dimension(510, 390));
        northPanel.setPreferredSize(new java.awt.Dimension(510, 390));
        titlePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        titlePanel.setBackground(new java.awt.Color(222, 220, 220));
        titlePanel.setPreferredSize(new java.awt.Dimension(510, 20));
        jLabel1.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("SLAP Viewer     Copyright ESAC, Spain");
        titlePanel.add(jLabel1);

        northPanel.add(titlePanel, java.awt.BorderLayout.NORTH);

        selectorPanel.setLayout(new java.awt.BorderLayout());

        selectorPanel.setBackground(new java.awt.Color(244, 241, 239));
        selectorPanel.setPreferredSize(new java.awt.Dimension(510, 240));
        northPanel.add(selectorPanel, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.BorderLayout());

        addPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 10));

        addPanel.setBackground(new java.awt.Color(244, 241, 239));
        addPanel.setFont(new java.awt.Font("Dialog", 1, 10));
        addPanel.setPreferredSize(new java.awt.Dimension(510, 40));
        resetButton.setFont(new java.awt.Font("Dialog", 1, 10));
        resetButton.setForeground(new java.awt.Color(102, 102, 102));
        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });
        resetButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resetButtonMouseClicked(evt);
            }
        });

        addPanel.add(resetButton);

        jPanel1.add(addPanel, java.awt.BorderLayout.CENTER);

        wavePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 1, 10));

        wavePanel.setBackground(new java.awt.Color(244, 241, 239));
        wavePanel.setBorder(new javax.swing.border.TitledBorder(null, "Range of Search (m)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10), new java.awt.Color(102, 102, 102)));
        wavePanel.setPreferredSize(new java.awt.Dimension(510, 70));
        waveLabel1.setFont(new java.awt.Font("Dialog", 1, 10));
        waveLabel1.setForeground(new java.awt.Color(102, 102, 102));
        waveLabel1.setText("Wavelength Start ");
        wavePanel.add(waveLabel1);

        waveText.setFont(new java.awt.Font("Dialog", 0, 10));
        waveText.setForeground(new java.awt.Color(102, 102, 102));
        waveText.setPreferredSize(new java.awt.Dimension(120, 17));
        waveText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waveTextActionPerformed(evt);
            }
        });

        wavePanel.add(waveText);

        waveLabel2.setFont(new java.awt.Font("Dialog", 1, 10));
        waveLabel2.setForeground(new java.awt.Color(102, 102, 102));
        waveLabel2.setText("      Wavelength End   ");
        waveLabel2.setPreferredSize(new java.awt.Dimension(120, 13));
        wavePanel.add(waveLabel2);

        waveText2.setFont(new java.awt.Font("Dialog", 0, 10));
        waveText2.setForeground(new java.awt.Color(102, 102, 102));
        waveText2.setPreferredSize(new java.awt.Dimension(120, 17));
        waveText2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waveText2ActionPerformed(evt);
            }
        });

        wavePanel.add(waveText2);

        jPanel1.add(wavePanel, java.awt.BorderLayout.NORTH);

        jSeparator2.setPreferredSize(new java.awt.Dimension(510, 2));
        jPanel1.add(jSeparator2, java.awt.BorderLayout.SOUTH);

        northPanel.add(jPanel1, java.awt.BorderLayout.SOUTH);

        containerPanel.add(northPanel, java.awt.BorderLayout.NORTH);

        jtablePanel.setLayout(new java.awt.BorderLayout());

        jtablePanel.setBackground(new java.awt.Color(244, 241, 239));
        jtablePanel.setBorder(new javax.swing.border.TitledBorder(null, "Slap Services Output", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10), new java.awt.Color(102, 102, 102)));
        jtablePanel.setMinimumSize(new java.awt.Dimension(510, 300));
        jtablePanel.setPreferredSize(new java.awt.Dimension(510, 300));
        jTabbedPane1.setBackground(new java.awt.Color(244, 241, 239));
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(490, 200));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(490, 200));
        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseClicked(evt);
            }
        });

        jtablePanel.add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        closePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        closePanel.setBackground(new java.awt.Color(244, 241, 239));
        closePanel.setMaximumSize(new java.awt.Dimension(490, 36));
        closePanel.setMinimumSize(new java.awt.Dimension(490, 36));
        closePanel.setPreferredSize(new java.awt.Dimension(490, 36));
        closeButton.setFont(new java.awt.Font("Dialog", 1, 10));
        closeButton.setForeground(new java.awt.Color(102, 102, 102));
        closeButton.setText("Close");
        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                closeButtonMouseClicked(evt);
            }
        });

        closePanel.add(closeButton);

        jtablePanel.add(closePanel, java.awt.BorderLayout.SOUTH);

        containerPanel.add(jtablePanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(containerPanel, java.awt.BorderLayout.CENTER);

    }
    // </editor-fold>//GEN-END:initComponents

    private void waveText2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waveText2ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_waveText2ActionPerformed

    private void waveTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waveTextActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_waveTextActionPerformed
    
    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        
        waveText.setText("0.");
        waveText2.setText("0.");
        
        wInit               = "0.";
        wEnd                = "0.";
        
        selectedJTable      = new ExtendedJTable();
        selectedJScrollPane = new JScrollPane();
        
        jTableVector        = new Vector();
        jScrollPaneVector   = new Vector();
        
        jTabbedPane1.removeAll();
        
    }//GEN-LAST:event_resetButtonActionPerformed
    
    private void jTabbedPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseClicked
        jTabbedPaneAction();
        
    }//GEN-LAST:event_jTabbedPane1MouseClicked
    
    public void jTabbedPaneAction() {
        int selected = jTabbedPane1.getSelectedIndex();
        
        selectedJTable      = (ExtendedJTable) jTableVector.elementAt(selected);
        selectedJScrollPane = (JScrollPane) jScrollPaneVector.elementAt(selected);
        
    }
    
    
    
    private void closeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closeButtonMouseClicked
        dispose();
    }//GEN-LAST:event_closeButtonMouseClicked
    
    private void resetButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resetButtonMouseClicked
        
        
    }//GEN-LAST:event_resetButtonMouseClicked
    
    public void createWrapper(){
    }
    
    
    public void setSpectrumSet(int ct){
    }
    
    public void getComponent(){
    }
    
    public int getColumn(JTable jTable, String keyword) {
        
        for(int i=0; i < ((DefaultTableModel) jTable.getModel()).getColumnCount(); i++) {
           if(((DefaultTableModel) jTable.getModel()).getColumnName(i).toUpperCase().equals(keyword.toUpperCase())) return i;
        }
//        int      columnNumber = ((DefaultTableModel) jTable.getModel()).findColumn(keyword);
//        return   columnNumber;
        return -1;
    }
    
    public void markTable(double x) {
        
        int wlColumn 	= getColumn(selectedJTable, "ldm:Line.wavelength");
        int idColumn 	= getColumn(selectedJTable, "ldm:Line.title");
        
        if(wlColumn < 0 || idColumn < 0) return;
        
        double 	value;
        double  valueMin 	= 1.E40;
        
        int	selectedRow 	= -1;
        
        for(int j=0; j < selectedJTable.getRowCount(); j++) {
            
            String thisValueString = (String) selectedJTable.getValueAt(j,wlColumn);
            if(thisValueString == null) continue;
            if(thisValueString.length()==0) continue;
            
            double thisValue = new Double(thisValueString).doubleValue();
            
            value = (thisValue - x) * (thisValue -x);
            
            if(j == 0) {
                valueMin = value;
                selectedRow 	= j;
            }
            
            if(value <= valueMin) {
                valueMin 	= value;
                selectedRow 	= j;
            }
            
        }
        
        if(selectedRow != oldSelectedRow && selectedRow != -1) {
            selectedJTable.scrollRectToVisible(new Rectangle(0,selectedJTable.getRowHeight()*(selectedRow),
                    selectedJTable.getWidth(),
                    selectedJScrollPane.getViewportBorderBounds().height));
            
            
            selectedJTable.repaint();
            
            drawLineOnDisplay(selectedRow, false);
            oldSelectedRow = selectedRow;
            this.repaint();
        }
    }
    
    
    public void drawLineOnDisplay(int selectedRow,boolean definitive) {
        
        int wlColumn 	= getColumn(selectedJTable, "ldm:Line.wavelength");
        int idColumn 	= getColumn(selectedJTable, "ldm:Line.title");
        
        try {
            double newValue 	= new Double((String) selectedJTable.getValueAt(selectedRow,wlColumn)).doubleValue();
            String identification 	= (String) selectedJTable.getValueAt(selectedRow, idColumn);
            SlapUtils.drawLineOnPlot(newValue,identification,definitive);
            
        } catch(Exception e) {}
    }
    
    
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        //System.exit(0);
    }//GEN-LAST:event_exitForm
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel addPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel closePanel;
    private javax.swing.JPanel containerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel jtablePanel;
    private javax.swing.JPanel northPanel;
    private javax.swing.JButton resetButton;
    private javax.swing.JPanel selectorPanel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JLabel waveLabel1;
    private javax.swing.JLabel waveLabel2;
    private javax.swing.JPanel wavePanel;
    private javax.swing.JTextField waveText;
    private javax.swing.JTextField waveText2;
    // End of variables declaration//GEN-END:variables
    
    
    
    public class NodeSelectionListener extends MouseAdapter {
        JTree tree;
        
        NodeSelectionListener(JTree tree) {
            this.tree = tree;
        }
        
        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            int row = tree.getRowForLocation(x, y);
            TreePath path = tree.getPathForRow(row);
            if (path != null) {
                Node node = (Node)path.getLastPathComponent();
                boolean isSelected = ! (node.isSelected());
                
                node.setSelected(isSelected);
                
                if (node.getSelectionMode() == Node.DIG_IN_SELECTION) {
                    if ( isSelected ) {
                        tree.expandPath(path);
                    } else {
                        tree.collapsePath(path);
                    }
                }
                ((DefaultTreeModel)tree.getModel()).nodeChanged(node);
                if (row == 0) {
                    tree.revalidate();
                    tree.repaint();
                }
            }
        }
    }
    
    public class Node extends DefaultMutableTreeNode {
        
        public final static int SINGLE_SELECTION = 0;
        public final static int DIG_IN_SELECTION = 4;
        protected int selectionMode;
        protected boolean isSelected;
        
        
        public Node() {
            this(null);
        }
        
        public Node(Object userObject) {
            this(userObject, true, false);
        }
        
        public Node(Object userObject, boolean allowsChildren, boolean isSelected) {
            super(userObject, allowsChildren);
            this.isSelected = isSelected;
            setSelectionMode(DIG_IN_SELECTION);
        }
        
        public void setSelectionMode(int mode) {
            selectionMode = mode;
        }
        
        public int getSelectionMode() {
            return selectionMode;
        }
        
        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
            
            if ((selectionMode == DIG_IN_SELECTION) && (children != null)) {
                Enumeration enum3 = children.elements();
                while (enum3.hasMoreElements()) {
                    Node node = (Node)enum3.nextElement();
                    //node.setSelected(isSelected);
                    node.setSelected(false);
                    
                }
            }else if((selectionMode == DIG_IN_SELECTION) && (children == null)){
                
            }
        }
        
        public boolean isSelected() {
            return isSelected;
        }
    }
    
    
    public class CheckRenderer extends JPanel implements TreeCellRenderer {
        protected JCheckBox check;
        protected JLabel label;
        
        public CheckRenderer() {
            setLayout(null);
            check = new JCheckBox();
            label = new JLabel();
            check.setBackground(UIManager.getColor("Tree.textBackground"));
            label.setForeground(UIManager.getColor("Tree.textForeground"));
        }
        
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean isSelected, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            String stringValue = tree.convertValueToText(value, isSelected, true, leaf, row, hasFocus);
            setEnabled(tree.isEnabled());
            check.setSelected(((Node)value).isSelected());
            label.setFont(tree.getFont());
            label.setText(stringValue);
            
            if (leaf) {
                add(check);
                add(label);
                check.setVisible(true);
                
            } else if (expanded) {
                check.setVisible(false);
                add(label);
                //label.setIcon(UIManager.getIcon("Tree.openIcon"));
            } else {
                check.setVisible(false);
                add(label);
                //label.setIcon(UIManager.getIcon("Tree.closedIcon"));
            }
            return this;
        }
        
        public Dimension getPreferredSize() {
            Dimension d_check = check.getPreferredSize();
            Dimension d_label = label.getPreferredSize();
            return new Dimension(d_check.width + d_label.width,
                    (d_check.height < d_label.height ?
                        d_label.height : d_check.height));
        }
        
        public void doLayout() {
            Dimension d_check = check.getPreferredSize();
            Dimension d_label = label.getPreferredSize();
            int y_check = 0;
            int y_label = 0;
            if (d_check.height < d_label.height) {
                y_check = (d_label.height - d_check.height)/2;
            } else {
                y_label = (d_check.height - d_label.height)/2;
            }
            check.setLocation(0,y_check);
            check.setBounds(0,y_check,d_check.width,d_check.height);
            label.setLocation(d_check.width,y_label);
            label.setBounds(d_check.width,y_label,d_label.width,d_label.height);
        }
        
        public void setBackground(Color color) {
            if (color instanceof ColorUIResource)
                color = null;
            super.setBackground(color);
        }
        
    }
    
    
    
    
}



