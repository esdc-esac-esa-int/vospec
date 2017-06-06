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
package esavo.vospec.resourcepanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;


/**
 *
 * @author  jgonzale
 */
public class TreeRebuilder extends javax.swing.JDialog implements ActionListener {
    


    public Vector<JComboBox> optionLists;

    public JTree serverListTree;
    public Node node;
    

    
    public TreeRebuilder(java.awt.Frame parent, boolean modal) {

        super(parent, modal);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - this.getWidth() /2, (screenSize.height / 2)- this.getHeight() /2);

    }


    public TreeRebuilder(Node node, JTree serverListTree) {

        this.node=node;
        this.serverListTree=serverListTree;

        initComponents();
        setSize(370,385);

        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - this.getWidth() /2, (screenSize.height / 2)- this.getHeight() /2);



        metaPanel.add(metaDataScrollPanel);
        metaDataPanel.add(metaPanel);



        metaDataScrollPanel.repaint();

        optionLists=new Vector();


        //add first options field

        Vector firstOptions = new Vector();
        firstOptions.add("NONE");
        Hashtable metadata = ((Node)node.getFirstLeaf()).getMetadata();
        Vector metadataIdentifiers = ((Node)node.getFirstLeaf()).getMetadata_identifiers();
        Vector metadataIdentifiersFiltered = new Vector();

        for(int i=0;i<metadataIdentifiers.size();i++){
                metadataIdentifiersFiltered.add(metadataIdentifiers.get(i));
        }

        firstOptions.addAll(metadataIdentifiersFiltered);

        JComboBox list = new JComboBox(firstOptions);

        list.addActionListener(this);

        optionLists.add(list);

        this.setSize((list.getPreferredSize().width+(new JLabel("Level "+(1)+" criterion ")).getPreferredSize().width+18), this.getPreferredSize().height);

        printOptionLists();

	

    }


    /*
     * Builds elements for selection according to optionLists
     *
     * */

    private void printOptionLists(){

        JPanel panel = new JPanel(new GridBagLayout());

        
        for(int i=0;i<optionLists.size();i++){

            GridBagConstraints c = new GridBagConstraints();

            c.anchor=GridBagConstraints.LINE_START;
            c.gridx = 0;
            c.gridy = i;
            panel.add(new JLabel("Level "+(i+1)+" criterion "), c);

            c.anchor=GridBagConstraints.LINE_END;
            c.gridx = 1;
            c.gridy = i;
            panel.add(optionLists.elementAt(i), c);



        }

        metaDataScrollPanel.setViewportView(panel);
        metaDataScrollPanel.getViewport().setViewPosition(new Point(0,optionLists.size()*optionLists.elementAt(0).getHeight()));
        
        metaDataScrollPanel.repaint();

        


    }





    /*
     *
     * CheckBoxes changing events listener
     *
     *
     * */

    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox)e.getSource();

        if (!((String) cb.getSelectedItem()).equals("NONE")) {

            //search for the selected combobox
            int index = 0;
            for (int i = 0; i < optionLists.size(); i++) {
                if (optionLists.elementAt(i).equals(cb)) {
                    index = i;
                }
            }

            //remove all the latter selected comboboxes
            Vector remaining = new Vector();
            for (int i = 0; i <= index; i++) {
                remaining.add(optionLists.get(i));
            }
            optionLists = remaining;

            //generate the options of the new one
            Vector<String> options = new Vector();
            for (int i = 0; i < cb.getItemCount(); i++) {
                if (!((String) cb.getSelectedItem()).equals((String) cb.getItemAt(i))) {
                    options.add((String) cb.getItemAt(i));
                }
            }

            //add the new combobox
            JComboBox newBox = new JComboBox(options);
            newBox.addActionListener(this);
            optionLists.add(newBox);

            //refresh the display
            printOptionLists();


        }else{

            //remove next options
            boolean starting_remove=false;

            Vector resultingOptions = new Vector();

            for (int i = 0; i < optionLists.size(); i++) {
                if(!starting_remove){
                    resultingOptions.add(optionLists.elementAt(i));
                }
                if (optionLists.elementAt(i).equals(cb)) {
                    starting_remove=true;
                }
            }

            optionLists=resultingOptions;

            //refresh the display
            printOptionLists();
        }


    }




    public void reBuildTree(){


        //for each level
        for(int i=0;i<optionLists.size();i++){

            String level_value=((String)optionLists.elementAt(i).getSelectedItem());

            if(!level_value.equals("NONE")){
             

                //take all the spectrum nodes (leafs) for this server

                Vector childrenVector = new Vector();
                Node leaf = (Node) node.getFirstLeaf();

                while (true) {
                    childrenVector.add(leaf);
                    if(leaf==node.getLastLeaf())break;
                    leaf = (Node) leaf.getNextLeaf();
                }



                //for each leaf of this server

                for(int j=0;j<childrenVector.size();j++){

                    Node thischild = (Node)childrenVector.get(j);

                    

                    //take his "brothers" and guess if any of them is the new criterium

                    Collection possibleValues = getPossibleValues(i, (Node)thischild.getParent());

                    Enumeration brothers = thischild.getParent().children();


                    boolean found_brother = false;

                    while(brothers.hasMoreElements()) {
                        Node brother = (Node)brothers.nextElement();

                        if(brother.getName().equals(thischild.getMetadata().get(level_value).toString())&&(brother.getMetadata().isEmpty())){
                            //if one of his "brothers" is the value of this
                            //metadata field for this child we
                            //move it to there as a child

                            ((Node)thischild.getParent()).remove(thischild);
                            brother.add(thischild);
                            found_brother = true;

                        }

                    }

                    //if there was not a brother with his value
                    //of the metadata field, we add all the possible values

                    if(!found_brother&&thischild.getMetadata().get(level_value)!=null){

                        //System.out.println("there was not a brother with value "+thischild.getMetadata().get(level_value));


                        Vector possibleValuesVector = new Vector();
                        possibleValuesVector.addAll(possibleValues);

                        //System.out.println("so we add "+possibleValuesVector);


                        //sorting of the new nodes

                        Collections.sort(possibleValuesVector);


                        //adding all the new nodes with the sorted possible values

                        for (int k=0;k<possibleValuesVector.size();k++){
                            Node newNode = new Node(possibleValuesVector.get(k).toString());
                            ((Node) thischild.getParent()).add(newNode);
                            newNode.setParent((Node) thischild.getParent());
                        }


                        //and now we act as if there was a brother with his value

                        brothers = thischild.getParent().children();
                        while (brothers.hasMoreElements()) {
                            Node brother = (Node) brothers.nextElement();
                            if (brother.getName().equals(thischild.getMetadata().get(level_value).toString()) && (brother.getMetadata().isEmpty())) {

                                //System.out.println("adding to "+brother.getName());
                                ((Node) thischild.getParent()).remove(thischild);
                                brother.add(thischild);


                            }

                        }




                    }





                }



            }



        }



    }




    /*
     * Returns the possible values for a server regarding a metadata field
     * 
     * @param level     Number of the criteria checkbox in the optionLists
     * @param fromNode  Node of origin of the server
     * 
     * */



    private Collection getPossibleValues(int level, Node fromNode){

        //System.out.println("getting possible values for level "+level+" starting node "+fromNode.getName());

        Hashtable possiblevalues = new Hashtable();


        String metadata_field= (String) optionLists.get(level).getSelectedItem();


        //take all the children leafs for this server

        Vector childrenVector = new Vector();
        Node leaf = (Node) fromNode.getFirstLeaf();

        while (true) {
            childrenVector.add(leaf);
            //System.out.println("adding "+leaf.getName());
            if (leaf == fromNode.getLastLeaf()) {
                break;
            }
            leaf = (Node) leaf.getNextLeaf();
        }



        //generate a list with all the found values

        for (int j = 0; j < childrenVector.size(); j++) {

            Node thisNode = (Node) childrenVector.get(j);

            //System.out.println("node "+thisNode.getName());
            if (!thisNode.getMetadata().isEmpty()) {
                if (possiblevalues.get(thisNode.getMetadata().get(metadata_field)) == null) {
                    possiblevalues.put(thisNode.getMetadata().get(metadata_field),
                            thisNode.getMetadata().get(metadata_field));
                }
            }
        }

        return possiblevalues.values();

    }



    /*
     * Generates a new tree for the server's node without hierarchy
     *
     * */

    private void setTreePlain(){


        //save leafs in a vector
        Node leaf = (Node) node.getFirstLeaf();
        Vector leafVector = new Vector();
        while(true){
            leafVector.add(leaf);
            if(leaf==node.getLastLeaf())break;
            leaf=(Node)leaf.getNextLeaf();
        }


        node.removeAllChildren();

        //add saved leafs
        for(int i=0;i<leafVector.size();i++){
            node.add((Node)leafVector.get(i));
        }




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
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tree Organizer");
        setAlwaysOnTop(true);

        metaDataPanel.setBackground(new java.awt.Color(240, 236, 236));
        metaDataPanel.setPreferredSize(new java.awt.Dimension(310, 340));
        metaDataPanel.setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(240, 236, 236));
        jPanel1.setPreferredSize(new java.awt.Dimension(300, 40));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        saveButton.setText("Organize");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jPanel1.add(saveButton);

        exitButton.setText("Cancel");
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
        metaDataScrollPanel.setFont(new java.awt.Font("Dialog", 0, 11));
        metaDataScrollPanel.setPreferredSize(null);
        metaPanel.add(metaDataScrollPanel, java.awt.BorderLayout.CENTER);

        metaDataPanel.add(metaPanel, java.awt.BorderLayout.CENTER);

        jLabel1.setText("Select the Tree ordering criteria:");
        jPanel2.add(jLabel1);

        metaDataPanel.add(jPanel2, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(metaDataPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed

        setTreePlain();
        reBuildTree();
        ((DefaultTreeModel) serverListTree.getModel()).reload();
        serverListTree.repaint();
        this.dispose();

                          
    }//GEN-LAST:event_saveButtonActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
         dispose();
    }//GEN-LAST:event_exitButtonActionPerformed
    
    public void dispose() {
        super.dispose();

    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new TreeRebuilder(new javax.swing.JFrame(), true).show();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton exitButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel metaDataPanel;
    private javax.swing.JScrollPane metaDataScrollPanel;
    private javax.swing.JPanel metaPanel;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables
    
}
