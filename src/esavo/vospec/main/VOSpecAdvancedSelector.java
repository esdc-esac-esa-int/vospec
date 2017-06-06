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

import cds.savot.model.*;
import cds.savot.pull.*;
import esavo.vospec.dataingestion.*;  
import esavo.vospec.resourcepanel.*;
import esavo.vospec.util.CheckRendererExtended;
import esavo.vospec.util.EnvironmentDefs;
import esavo.vospec.util.NodeSelectionListener;
import esavo.vospec.util.ParamsTree;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*; 
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.tree.*;

/**
 *
 * @author  ibarbarisi/jgonzalez
 */
public class VOSpecAdvancedSelector extends javax.swing.JFrame {

    /** Creates new form VOSpecAdvancedSelector */
    public VOSpecAdvancedSelector(VOSpecDetached tool) {
        this.aioSpecToolDetached = tool;
        initComponents();
        initComponent2();
        installBasicToolBar();
        initJList();
        this.setTitle("Server Selector");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - this.getWidth() / 2, (screenSize.height / 2) - this.getHeight() / 2);
    }

    public void setTrees() {
        try {
            serverListTree = new JTree(rootNode);
            paramTree = new JTree();
            this.refreshParamTree();
            this.refreshServerListTree();
            setServerList();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBasicParamsToBeSelected() {
        if (ssaRequest != null) {
            if (!(ssaRequest.getTarget()).equals("")) {
                addValueToParamTree("TARGET.NAME", ssaRequest.getTarget());
                findAndSelectByName(paramTree, "TARGET.NAME", true);
            }

            if (!(ssaRequest.getPos()).equals("")) {
                couple.put("POS", ssaRequest.getPos());
                paramSelectedVector.put("POS", new Node("POS"));
                addParamValueToServices("POS", ssaRequest.getPos());
                addValueToParamTree("POS", ssaRequest.getPos());
                findAndSelectByName(paramTree, "POS", true);
                //set green all the services supporting param node.toString()
                checkOtherServicesSelectedIfTheyHaveSameParam(new Node("POS"), true);
            }

            if (!(ssaRequest.getSize()).equals("")) {
                couple.put("SIZE", ssaRequest.getSize());
                paramSelectedVector.put("SIZE", new Node("SIZE"));
                addParamValueToServices("SIZE", ssaRequest.getSize());
                addValueToParamTree("SIZE", ssaRequest.getSize());
                findAndSelectByName(paramTree, "SIZE", true);
                //set green all the services supporting param node.toString()
                checkOtherServicesSelectedIfTheyHaveSameParam(new Node("SIZE"), true);
            }
        }
    }

    public void setBasicParamsToBeSelected(SsaRequest ssaRequest) {
        this.ssaRequest = ssaRequest;

    }

    public void installBasicToolBar() {
        JToolBar bar = new JToolBar();
        bar.setBackground(new Color(235, 235, 235));
        bar.add(jPanel1);
        bar.setFloatable(true);
        bar.setUI(new javax.swing.plaf.metal.MetalToolBarUI() {

            @Override
            protected RootPaneContainer createFloatingWindow(JToolBar tb) {
                JDialog dialog = (JDialog) super.createFloatingWindow(tb);
                dialog.setResizable(true);
                return dialog;
            }
        });

        panelContainer.add(bar, java.awt.BorderLayout.SOUTH);
    }

    public void initJList() {
        listModel = new DefaultListModel();
        outlook = new JList();
        outlook.setBackground(new java.awt.Color(255, 246, 218));
        outlook.setFont(new java.awt.Font("Dialog", 1, 10));
        outlook.setForeground(new java.awt.Color(153, 153, 153));
        outlook.setModel(listModel);
    }

    public void initJTree() {

        try {
            //server selected by user
            serverSelected = new Vector();
            serverListTree = new JTree(rootNode);

            serverListModel = new DefaultTreeModel(rootNode);

            serverListModel.addTreeModelListener(new TreeModelListener() {

                public void treeNodesChanged(TreeModelEvent e) {

                    Node node = (Node) serverListTree.getLastSelectedPathComponent();
                    if (node == null) {
                        return;
                    }
                    if (node.isLeaf()) {
                        setWaitCursor();
                        if (node.getIsSelected()) {
                            addServiceToParamTree(node);
                            serverSelected.add(node.toString());
                            //traverse paramTree and select all nodes with the same name
                            //if one service is selected it checks all params with value and add to this service this param/value if supported
                            if (couple.size() > 0) {
                                Enumeration keys = paramSelectedVector.keys();
                                while (keys.hasMoreElements()) {

                                    String n = (String) keys.nextElement();
                                    Node node2 = (Node) paramSelectedVector.get(n);
                                    if (couple.get(node2.toString()) != null) {
                                        if (!node2.isParamTsa()) {
                                            //check all services selected and add param/value
                                            addParamValueToServices(node2.toString(), (String) couple.get(node2.toString()));
                                            //refresh paramTree with new param/values
                                            addValueToParamTree(node2.toString(), (String) couple.get(node2.toString()));
                                        } else {
                                            tsaCounter++;
                                            addParamValueToServicesTSA(node2, node2.toString(), (String) couple.get(node2.toString()));
                                            //refresh paramTree with new param/values
                                            addValueToParamTreeTSA((TreePath) getPath(node2), node2.toString(), (String) couple.get(node2.toString()));

                                        }
                                    }
                                }
                            }

                            if (couple.size() > 0) {
                                Enumeration keys = couple.keys();
                                while (keys.hasMoreElements()) {
                                    String name = (String) keys.nextElement();
                                    String value = (String) couple.get(name);
                                    Node node2 = (Node) paramSelectedVector.get(name);
                                    if (node2 != null) {
                                        if (node2.getIsSelected()) {
                                            if (!node2.isParamTsa()) {
                                                //check all services selected and add param/value
                                                addParamValueToServices(name, value);
                                                //refresh paramTree with new param/values
                                                addValueToParamTree(name, value);
                                            } else {
                                                addParamValueToServicesTSA(node2, node2.toString(), (String) couple.get(node2.toString()));
                                                //refresh paramTree with new param/values
                                                addValueToParamTreeTSA((TreePath) getPath(node2), node2.toString(), (String) couple.get(node2.toString()));

                                            }
                                        } else {
                                            if (!node2.isParamTsa()) {
                                                //refresh paramTree with new param/values
                                                addValueToParamTree(name, value);
                                                findAndSelectByName(paramTree, name, false);
                                            } else {
                                                addParamValueToServicesTSA(node2, node2.toString(), (String) couple.get(node2.toString()));
                                                //refresh paramTree with new param/values
                                                addValueToParamTreeTSA((TreePath) getPath(node2), node2.toString(), (String) couple.get(node2.toString()));
                                            }
                                        }
                                    }
                                }
                            }
                            ///////////////////////////////////////////////////////////////////////////////////////////
                            //this line create a problems when selecting the same tsa param and adding different values
                            //checkOtherParamToSelect();

                            buildQuery();

                        } else {
                            if (!node.isParamTsa()) {
                                serverSelected.remove(node.toString());
                                //reset params/value service node
                                removeParamValueToService(node);
                                removeURLFromOutlook(node);
                                removeServiceFromParamTree(node);
                            } else {
                                tsaCounter--;
                                serverSelected.remove(node.toString());
                                //reset params/value service node
                                removeParamValueToServiceTSA(node, node.toString());
                                removeURLFromOutlook(node);
                                removeServiceFromParamTree(node);
                            }
                        }
                        setDefaultCursor();
                    }
                }

                public void treeNodesInserted(TreeModelEvent e) {
                }

                public void treeNodesRemoved(TreeModelEvent e) {
                }

                public void treeStructureChanged(TreeModelEvent e) {
                }
            });

            serverListTree.setModel(serverListModel);
            serverListTree.setBackground(new Color(255, 255, 255));
            serverListTree.setCellRenderer(new CheckRendererExtended());
            serverListTree.getSelectionModel().setSelectionMode(
                    TreeSelectionModel.SINGLE_TREE_SELECTION);
            serverListTree.addMouseListener(new NodeSelectionListener(serverListTree));
            serverListTree.putClientProperty("JTree.lineStyle", "Angled");
            resetJTree();
            listPanel.setViewportView(serverListTree);

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    //traverse paramTree and select all nodes with the same name
    public void checkOtherParamToSelect() {

        Enumeration keys = paramSelectedVector.keys();
        while (keys.hasMoreElements()) {
            String n = (String) keys.nextElement();
            Node node = (Node) paramSelectedVector.get(n);
            findAndSelectByName(paramTree, node.toString(), true);
        }

    }

    public void initParamTree() {

        paramTree = new JTree();
        paramTree.setToolTipText("");
        rootQueryNode = new Node("Query");
        targetQueryNode = new Node("TARGET.NAME");
        simpleQueryNode = new Node("Simple Query");
        advancedQueryNode = new Node("Advanced Query");
        serviceSpecificNode = new Node("Service Specific Query");
        rootQueryNode.add(targetQueryNode);
        rootQueryNode.add(simpleQueryNode);
        rootQueryNode.add(advancedQueryNode);
        rootQueryNode.add(serviceSpecificNode);

        Hashtable paramSSAP = new Hashtable();
        paramSSAP = parseSSAPVOTable();

        Enumeration keys = paramSSAP.keys();
        while (keys.hasMoreElements()) {

            String n = (String) keys.nextElement();
            Node node = new Node(n);

            node.setToolTipTextParam((String) paramSSAP.get(n));
            if (n.equals("POS") || n.equals("SIZE")) {
                simpleQueryNode.add(node);
            } else {
                advancedQueryNode.add(node);
            }
        }

        treeModelParam = new DefaultTreeModel(rootQueryNode);

        treeModelParam.addTreeModelListener(new TreeModelListener() {

            public void treeNodesChanged(TreeModelEvent e) {
                //Node node = (Node) (e.getTreePath().getLastPathComponent());
                Node node = (Node) paramTree.getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }
                if (!node.isParamTsa()) {
                    if (node.isLeaf()) {
                        if (node.getIsSelected()) {
                            addParamToInsertPanel(node);
                            if (!node.toString().equals("TARGET.NAME")) {
                                paramSelectedVector.put(node.toString(), node);
                            }
                            //select same params in paramTree if selected
                            TreePath path = findAndSelectByName(paramTree, node.toString(), true);
                            //set green all the services supporting param node.toString()
                            checkOtherServicesSelectedIfTheyHaveSameParam(node, true);
                        } else {
                            addParamToInsertPanel(node);
                            //select same params in paramTree if selected
                            String nodeToRemove = node.toString();
                            TreePath path = findAndSelectByName(paramTree, nodeToRemove, false);
                            refreshParamTree();
                            removeParamValueToService(node.toString());
                            paramSelectedVector.remove(node.toString());
                            //set green all the services supporting param node.toString()
                            checkOtherServicesSelectedIfTheyHaveSameParam(node, false);
                            intersectionServices();
                        }
                    }
                } else {
                    //TSA CASE
                    if (node.isLeaf()) {
                        if (node.getIsSelected()) {
                            //setParam(node.getTsaServerParam());
                            addParamToInsertPanel(node);
                            //select param in ParamTree
                            node.setIsSelected(true);
                        } else {
                            addParamToInsertPanel(node);
                            TreePath path = getPath(node);
                            findAndSelectByNameTSA(path, node.toString(), false);
                            refreshParamTree();
                            removeParamValueToServiceTSA(node, node.toString());
                            paramSelectedVector.remove(node.toString());
                            tsaHasBeenSet = true;
                        }
                    }
                }
            }

            public void treeNodesInserted(TreeModelEvent e) {
            }

            public void treeNodesRemoved(TreeModelEvent e) {
            }

            public void treeStructureChanged(TreeModelEvent e) {
            }
        });

        paramTree.setModel(treeModelParam);
        paramTree.setCellRenderer(new ParamsTree());
        paramTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        paramTree.putClientProperty("JTree.lineStyle", "Angled");
        paramTree.addMouseListener(new NodeSelectionListener(paramTree));
        paramPanel.setViewportView(paramTree);

        TreePath pathToExpand = paramTree.getNextMatch(simpleQueryNode.toString(), 0, Position.Bias.Forward);
        paramTree.expandPath(pathToExpand);
    }

//    public void setParam(TsaServerParam tsaParam){
//        this.tsaParam = tsaParam;
//    }
    public Hashtable parseSSAPVOTable() {

        Hashtable paramSSAP = new Hashtable();

        try {
            HttpURLConnection.setFollowRedirects(true);
            HttpURLConnection con = (HttpURLConnection) new URL(EnvironmentDefs.getSSAPURL()).openConnection();

            SavotPullParser sb = new SavotPullParser(con.getInputStream(), 0, "UTF8");
            SavotVOTable sv = sb.getVOTable();
            if (sb.getResourceCount() == 0) {
                throw new Exception("Incorrect Format");
            }
            // for each resource
            for (int l = 0; l < sb.getResourceCount(); l++) {
                SavotResource currentResource = (SavotResource) (sv.getResources().getItemAt(l));

                if (currentResource != null) {
                    // get all the params of the table
                    ParamSet ps = currentResource.getParams();
                    //get the number of params
                    int number_field = ps.getItemCount();

                    //for each param
                    for (int g = 0; g < number_field; g++) {
                        SavotParam currentParam = (SavotParam) (currentResource.getParams().getItemAt(g));
                        if (currentParam.getName().indexOf("INPUT") > -1) {
                            String[] param1 = currentParam.getName().split(":");
                            String param = param1[1];
                            String description = currentParam.getDescription();
                            paramSSAP.put(param, description);
                        }
                    }
                }//end if

            }//end for l
            return paramSSAP;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //when service is selected add this service to paramTree with all its params
    public void addServiceToParamTree(Node node) {
        Node serv = null;

        for (int i = 0; i < number_server; i++) {
            if ((node.getUserObject().toString()).equals(ssaServerList.getSsaServer(i).getSsaName())) {
                Vector params = new Vector();

                params = (ssaServerList.getSsaServer(i)).getInputParams();

                if (params.isEmpty() && ssaServerList.getSsaServer(i).getType() != SsaServer.TSAP) {
                    String[] paramDesc2 = new String[2];
                    paramDesc2[0] = "POS";
                    paramDesc2[1] = "Position";
                    params.add(paramDesc2);
                    String[] paramDesc3 = new String[2];
                    paramDesc3[0] = "SIZE";
                    paramDesc3[1] = "Size";
                    params.add(paramDesc3);
                    String[] paramDesc4 = new String[2];
                    paramDesc4[0] = "BAND";
                    paramDesc4[1] = "Band (meters)";
                    params.add(paramDesc4);
                    String[] paramDesc5 = new String[2];
                    paramDesc5[0] = "TIME";
                    paramDesc5[1] = "Time (ISO 8601 UTC)";
                    params.add(paramDesc5);
                    String[] paramDesc6 = new String[2];
                    paramDesc6[0] = "FORMAT";
                    paramDesc6[1] = "Format (ex. votable)";
                    params.add(paramDesc6);
                }

                serv = new Node(ssaServerList.getSsaServer(i).getSsaName());
                int startRow = 0;
                String prefix = ssaServerList.getSsaServer(i).getSsaName();

                //if node doesn't exists add Node
                if (paramTree.getNextMatch(prefix, startRow, Position.Bias.Forward) == null) {
                    treeModelParam.insertNodeInto(serv, serviceSpecificNode, serviceSpecificNode.getChildCount());
                    for (int g = 0; g < params.size(); g++) {
                        if ((ssaServerList.getSsaServer(i)).getType() == SsaServer.TSAP) {
                            TsaServerParam tsaServerParam = (TsaServerParam) params.elementAt(g);
                            Node nodeTsa = new Node(tsaServerParam.getName());
                            nodeTsa.setParamTsa(true);
                            nodeTsa.setIsSelected(true);
                            String toolTip = tsaServerParam.getDescription();
                            nodeTsa.setToolTipTextParam(toolTip);
                            nodeTsa.setTsaServerParam(tsaServerParam);
                            //Removed if not wante behaviour : when select tsa server add all params/value to Tsa
                            couple.put(tsaServerParam.getName(), getTsaParamValue(tsaServerParam));
                            paramSelectedVector.put(tsaServerParam.getName(), nodeTsa);
                            serv.add(nodeTsa);
                        } else {
                            String[] paramDesc = (String[]) params.elementAt(g);
                            Node nodeSsa = new Node(paramDesc[0].toString());
                            String toolTip = paramDesc[1].toString();
                            nodeSsa.setToolTipTextParam(toolTip);
                            nodeSsa.setParamTsa(false);
                            serv.add(nodeSsa);
                        }
                        refreshParamTree();
                    }
                } else {
                    //System.out.println("Node already exists " + ssaServerList.getSsaServer(i).getSsaName());
                }
            }
        }
        TreePath pathToExpand = paramTree.getNextMatch(serviceSpecificNode.toString(), 0, Position.Bias.Forward);
        paramTree.expandPath(pathToExpand);
    }

    public String getTsaParamValue(TsaServerParam tsaServerParam) {
        String paramValue = "";
        if (tsaServerParam.getSelectedValue() == null) {
            Vector values = tsaServerParam.getValues();
            if (tsaServerParam.getValues().size() > 0) {
                paramValue = (String) values.elementAt(0);
            }
        } else {
            paramValue = tsaServerParam.getSelectedValue();
        }
        return paramValue;
    }

    public void removeServiceFromParamTree(Node nodeName) {

        try {
            //expand ServiceSpecificNode in case is collapsed
            TreePath pathToExpand = paramTree.getNextMatch(serviceSpecificNode.toString(), 0, Position.Bias.Forward);
            paramTree.expandPath(pathToExpand);
            String prefix = nodeName.toString();
            TreePath path = paramTree.getNextMatch(prefix, 0, Position.Bias.Forward);
            paramTree.collapsePath(path);
            Node nodeToBeRemoved = (Node) path.getLastPathComponent();
            treeModelParam.removeNodeFromParent(nodeToBeRemoved);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addParamToInsertPanel(Node node) {

        try {
            if (node.isParamTsa()) {
                TsaServerParam tsaServerParam = (TsaServerParam) node.getTsaServerParam();
                if (tsaServerParam.getIsCombo()) {
                    panelComponent.removeAll();
                    initComboBox(node, tsaServerParam);
                } else {
                    panelComponent.removeAll();
                    ///FICME: should add node here as above
                    initTextField(node, tsaServerParam);
                }
            } else {
                panelComponent.removeAll();
                initComponent2();
                paramText.setText(node.toString());
                paramText.setToolTipText(node.toString());
                panelComponent.repaint();
                panelComponent.revalidate();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //set green all the services supporting param node.toString()
    public void checkOtherServicesSelectedIfTheyHaveSameParam(Node node, boolean select) {
        try {
            //ignore TARGET.NAME
            if (!node.toString().equals("TARGET.NAME")) {
                highLightServicesSupportingThisParam(serverListTree, node.toString(), select);
                intersectionServices();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetJTree() {
        try {
            Enumeration enume = rootNode.breadthFirstEnumeration();

            //check ssa nodes and set green services compatible with param
            while (enume.hasMoreElements()) {
                Node node = (Node) enume.nextElement();
                if (node.isLeaf()) {
                    node.setDownloaded(false);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void highLightServicesSupportingThisParam(JTree serverListTree, String param, boolean select) {

        try {
            Enumeration enume = rootNode.breadthFirstEnumeration();

            //check ssa nodes and set green services compatible with param
            while (enume.hasMoreElements()) {
                Node node = (Node) enume.nextElement();
                if (node.isLeaf()) {
                    for (int i = 0; i < number_server; i++) {
                        SsaServer ssaServer = ssaServerList.getSsaServer(i);
                        if (node.toString().equals(ssaServer.getSsaName())) {
                            if (serverHasThisParam(ssaServer, param)) {
                                //node.setDownloaded(select);
                                listPanel.setViewportView(serverListTree);
                                serverListTree.repaint();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void intersectionServices() {
        try {
            boolean paramSupported = true;
            if (paramSelectedVector.size() > 0) {
                for (int i = 0; i < number_server; i++) {
                    paramSupported = true;
                    SsaServer ssaServer = ssaServerList.getSsaServer(i);
                    Enumeration keys = paramSelectedVector.keys();
                    while (keys.hasMoreElements()) {
                        String n = (String) keys.nextElement();
                        Node node = (Node) paramSelectedVector.get(n);
                        if (!serverHasThisParam(ssaServer, node.toString())) {
                            paramSupported = false;
                        }
                    }

                    Enumeration enume = rootNode.breadthFirstEnumeration();

                    //check ssa nodes and set green services compatible with param
                    while (enume.hasMoreElements()) {
                        Node node = (Node) enume.nextElement();
                        if (node.isLeaf()) {
                            for (int j = 0; j < number_server; j++) {
                                if (node.toString().equals(ssaServer.getSsaName())) {
                                    //node.setDownloaded(paramSupported);
                                    listPanel.setViewportView(serverListTree);
                                    serverListTree.repaint();
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Finds the path in tree as specified by the array of names. The names array is a
    // sequence of names where names[0] is the root and names[i] is a child of names[i-1].
    // Comparison is done using String.equals(). Returns null if not found.
    public TreePath findAndSelectByName(JTree paramTree, String node, boolean select) {
        TreeNode root = (TreeNode) paramTree.getModel().getRoot();
        return find2(paramTree, new TreePath(root), node, 0, true, select);
    }

    public TreePath find2(JTree tree, TreePath parent, String nodeTofind, int depth, boolean byName, boolean select) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        Object o = node;

        // If by name, convert node to a string
        if (byName) {
            o = o.toString();
        }

        // If equal, go down the branch
        if (o.equals(nodeTofind)) {
            Node nodeToBeSelected = (Node) parent.getLastPathComponent();
            nodeToBeSelected.setIsSelected(select);
            if (!select) {
                nodeToBeSelected.isValueSelected(false);
            }
        }

        // If at end, return match
        if (depth == tree.getVisibleRowCount() - 1) {
            return parent;
        }

        // Traverse children
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                Node nody = (Node) parent.getLastPathComponent();
                TreePath result = find2(tree, path, nodeTofind, depth + 1, byName, select);
            }
        }
        // No match at this branch
        return null;
    }

    // Finds the path in tree as specified by the array of names. The names array is a
    // sequence of names where names[0] is the root and names[i] is a child of names[i-1].
    // Comparison is done using String.equals(). Returns null if not found.
    public TreePath findAndSelectByNameTSA(TreePath path, String node, boolean select) {
        return find3(paramTree, path, node, 0, true, select);
    }

    public TreePath find3(JTree tree, TreePath path, String nodeTofind, int depth, boolean byName, boolean select) {
        TreeNode node = (TreeNode) path.getLastPathComponent();
        Object o = node;

        // If by name, convert node to a string
        if (byName) {
            o = o.toString();
        }

        // If equal, go down the branch
        if (o.equals(nodeTofind)) {
            Node nodeToBeSelected = (Node) path.getLastPathComponent();
            nodeToBeSelected.setIsSelected(select);
            if (!select) {
                nodeToBeSelected.isValueSelected(false);
            }
        }

        // If at end, return match
        if (depth == tree.getVisibleRowCount() - 1) {
            return path;
        }

        // Traverse children
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath pathy = path.pathByAddingChild(n);
                Node nody = (Node) pathy.getLastPathComponent();
                TreePath result = find2(tree, pathy, nodeTofind, depth + 1, byName, select);
            }
        }
        // No match at this branch
        return null;
    }

    // Returns a TreePath containing the specified node.
    public TreePath getPath(TreeNode node) {
        List list = new ArrayList();

        // Add all nodes to list
        while (node != null) {
            list.add(node);
            node = node.getParent();
        }
        Collections.reverse(list);

        // Convert array of nodes to TreePath
        return new TreePath(list.toArray());
    }

    public void removeURLFromOutlook(Node node) {

        listModel.removeElement(node.getUserObject().toString());
        outlook.setModel(listModel);
        queryOutlook.setViewportView(outlook);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        panelContainer = new javax.swing.JPanel();
        listPanel = new javax.swing.JScrollPane();
        serverListTree = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        selectAllCheckBox = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        queryOutlook = new javax.swing.JScrollPane();
        outlook = new javax.swing.JList();
        jProgressBar1 = new javax.swing.JProgressBar();
        paramsQueryPanel = new javax.swing.JPanel();
        queryParams = new javax.swing.JTabbedPane();
        paramPanel = new javax.swing.JScrollPane();
        paramTree = new javax.swing.JTree();
        insertPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        panelComponent = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        queryButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(235, 235, 235));

        jSplitPane1.setBackground(new java.awt.Color(235, 235, 235));
        jSplitPane1.setDividerLocation(370);
        jSplitPane1.setPreferredSize(new java.awt.Dimension(777, 602));

        panelContainer.setBackground(new java.awt.Color(235, 235, 235));
        panelContainer.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Query by Service", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11), new java.awt.Color(102, 102, 102))); // NOI18N
        panelContainer.setMinimumSize(new java.awt.Dimension(20, 600));
        panelContainer.setPreferredSize(new java.awt.Dimension(315, 600));
        panelContainer.setLayout(new java.awt.BorderLayout());

        listPanel.setMinimumSize(new java.awt.Dimension(321, 302));

        serverListTree.setFont(new java.awt.Font("Dialog", 0, 10));
        serverListTree.setMaximumSize(new java.awt.Dimension(1800, 1800));
        serverListTree.setMinimumSize(new java.awt.Dimension(1800, 1800));
        serverListTree.setPreferredSize(new java.awt.Dimension(300, 350));
        listPanel.setViewportView(serverListTree);

        panelContainer.add(listPanel, java.awt.BorderLayout.CENTER);

        jPanel1.setBackground(new java.awt.Color(255, 246, 218));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Query Outlook", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11), new java.awt.Color(102, 102, 102))); // NOI18N
        jPanel1.setMaximumSize(new java.awt.Dimension(3000, 2000));
        jPanel1.setMinimumSize(new java.awt.Dimension(250, 200));
        jPanel1.setPreferredSize(new java.awt.Dimension(300, 200));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel4.setBackground(new java.awt.Color(255, 246, 218));
        jPanel4.setMaximumSize(new java.awt.Dimension(297, 40));
        jPanel4.setMinimumSize(new java.awt.Dimension(297, 40));
        jPanel4.setPreferredSize(new java.awt.Dimension(297, 40));
        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jButton3.setFont(new java.awt.Font("Luxi Sans", 0, 10));
        jButton3.setText("Refresh");
        jButton3.setToolTipText("Refresh Servers list from Registry");
        jButton3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton3);

        jButton2.setFont(new java.awt.Font("Luxi Sans", 0, 10));
        jButton2.setText("Add SSA/TSA");
        jButton2.setToolTipText("Add locally an SSA/TSA server");
        jButton2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton2);

        selectAllCheckBox.setBackground(new java.awt.Color(255, 246, 218));
        selectAllCheckBox.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        selectAllCheckBox.setForeground(new java.awt.Color(51, 153, 255));
        selectAllCheckBox.setText("Select All Observational");
        selectAllCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        selectAllCheckBox.setEnabled(false);
        jPanel4.add(selectAllCheckBox);

        jPanel1.add(jPanel4, java.awt.BorderLayout.NORTH);

        jPanel5.setBackground(new java.awt.Color(255, 246, 218));
        jPanel5.setPreferredSize(new java.awt.Dimension(309, 155));
        jPanel5.setLayout(new java.awt.BorderLayout());

        queryOutlook.setBackground(new java.awt.Color(255, 246, 218));
        queryOutlook.setForeground(new java.awt.Color(102, 102, 102));
        queryOutlook.setMinimumSize(new java.awt.Dimension(250, 250));

        outlook.setBackground(new java.awt.Color(255, 246, 218));
        outlook.setFont(new java.awt.Font("Dialog", 1, 10));
        outlook.setForeground(new java.awt.Color(153, 153, 153));
        outlook.setMaximumSize(new java.awt.Dimension(32767, 32767));
        outlook.setPreferredSize(null);
        queryOutlook.setViewportView(outlook);

        jPanel5.add(queryOutlook, java.awt.BorderLayout.CENTER);

        jProgressBar1.setStringPainted(true);
        jPanel5.add(jProgressBar1, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel5, java.awt.BorderLayout.CENTER);

        panelContainer.add(jPanel1, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setLeftComponent(panelContainer);

        paramsQueryPanel.setBackground(new java.awt.Color(204, 222, 238));
        paramsQueryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Query by params", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11), new java.awt.Color(0, 153, 255))); // NOI18N
        paramsQueryPanel.setForeground(new java.awt.Color(0, 153, 255));
        paramsQueryPanel.setMinimumSize(new java.awt.Dimension(20, 600));
        paramsQueryPanel.setPreferredSize(new java.awt.Dimension(350, 600));
        paramsQueryPanel.setLayout(new java.awt.BorderLayout());

        queryParams.setBackground(new java.awt.Color(255, 255, 255));
        queryParams.setFont(new java.awt.Font("Dialog", 1, 10));
        queryParams.setMinimumSize(new java.awt.Dimension(1800, 1800));

        paramPanel.setBackground(new java.awt.Color(255, 255, 255));
        paramPanel.setPreferredSize(new java.awt.Dimension(800, 750));

        paramTree.setForeground(new java.awt.Color(51, 51, 51));
        paramTree.setMaximumSize(new java.awt.Dimension(1800, 1800));
        paramTree.setMinimumSize(new java.awt.Dimension(1800, 1800));
        paramPanel.setViewportView(paramTree);

        queryParams.addTab("Tree", paramPanel);

        paramsQueryPanel.add(queryParams, java.awt.BorderLayout.CENTER);

        insertPanel.setBackground(new java.awt.Color(235, 235, 235));
        insertPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Insert Param Value", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 10), new java.awt.Color(0, 153, 255))); // NOI18N
        insertPanel.setPreferredSize(new java.awt.Dimension(350, 150));
        insertPanel.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(235, 235, 235));
        jPanel2.setForeground(new java.awt.Color(51, 0, 255));
        jPanel2.setFont(new java.awt.Font("Dialog", 1, 10));
        jPanel2.setPreferredSize(new java.awt.Dimension(345, 25));

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 10));
        jLabel1.setForeground(new java.awt.Color(153, 153, 153));
        jLabel1.setText("Point mouse on param label to see description");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addContainerGap(86, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jLabel1)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        insertPanel.add(jPanel2, java.awt.BorderLayout.NORTH);

        panelComponent.setBackground(new java.awt.Color(235, 235, 235));
        panelComponent.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 15));
        insertPanel.add(panelComponent, java.awt.BorderLayout.CENTER);

        jPanel3.setBackground(new java.awt.Color(235, 235, 235));
        jPanel3.setPreferredSize(new java.awt.Dimension(345, 33));

        queryButton.setFont(new java.awt.Font("Dialog", 1, 10));
        queryButton.setForeground(new java.awt.Color(255, 0, 0));
        queryButton.setText("Query");
        queryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                queryButtonActionPerformed(evt);
            }
        });
        jPanel3.add(queryButton);

        jButton1.setFont(new java.awt.Font("Dialog", 1, 10));
        jButton1.setForeground(new java.awt.Color(102, 102, 102));
        jButton1.setText("Reset");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetActionPerformed(evt);
            }
        });
        jPanel3.add(jButton1);

        insertPanel.add(jPanel3, java.awt.BorderLayout.SOUTH);

        paramsQueryPanel.add(insertPanel, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setRightComponent(paramsQueryPanel);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void resetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetActionPerformed
        resetAll();

    }//GEN-LAST:event_resetActionPerformed

    public void resetAll() {

        tsaHasBeenSet = false;
        serverSelected = new Vector();
        paramSelectedVector = new Hashtable();
        couple = new Hashtable();
        try {
            Node root = (Node) serverListTree.getModel().getRoot();
            Enumeration enume = root.breadthFirstEnumeration();
            //check all nodes and set gray all the rows
            while (enume.hasMoreElements()) {
                Node node = (Node) enume.nextElement();
                if (node.isLeaf()) {
                    node.setIsSelected(false);
                }
                if (!node.isLeaf()) {
                    node.setDownloaded(false);
                }
            }

            removeParamValueToService();

        } catch (Exception e) {
        }

        initJList();
        initJTree();
        initParamTree();
        queryOutlook.setViewportView(outlook);
        paramText.setText("Text Param");
        paramField.setText("");
        setTrees();
    }

    private void queryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_queryButtonActionPerformed

        if (!this.aioSpecToolDetached.finishedQuery) {
            JOptionPane.showMessageDialog(this, " Please wait until previous query finishes");
            return;
        }

        if (serverSelected.size() == 0) {
            JOptionPane.showMessageDialog(this, " Please select one/more servers");
            return;
        }


        if (!tsaHasBeenSet && tsaCounter > 0) {
            Object[] options = {"Yes", "No"};
            int n = JOptionPane.showOptionDialog(this,
                    "Parameters for a TSA service have not been set. Would you like to continue with default parameters?",
                    "Warning",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (n == 1) {
                return;
            }

        }

        String pos = (String) couple.get("POS");
        String target = ssaRequest.getTarget();
        String size = (String) couple.get("SIZE");

        if (pos != null) {
            String[] raDec;
            pos = parsePos(pos);
            raDec = (String[]) pos.split(",");

            aioSpecToolDetached.raField.setText(raDec[0].trim());
            aioSpecToolDetached.decField.setText(raDec[1].trim());

        } else {
            aioSpecToolDetached.raField.setText("");
            aioSpecToolDetached.decField.setText("");
        }

        if (target != null) {
            aioSpecToolDetached.targetField.setText(target);
        } else {
            aioSpecToolDetached.targetField.setText("");
        }

        if (size != null) {
            aioSpecToolDetached.sizeField.setText(size);
        } else {
            aioSpecToolDetached.sizeField.setText("");
        }

        aioSpecToolDetached.addLocalData();

        buildQuery();
        sendQuery();

        aioSpecToolDetached.setVisible(true);
        aioSpecToolDetached.toFront();
        this.toBack();


    }//GEN-LAST:event_queryButtonActionPerformed

    private String parsePos(String pos) {

        if (pos.indexOf(",") == -1) {
            pos = pos.replaceFirst(" ", ",");
        }
        return pos.trim();
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        localSSADialog = new LocalSSADialog(this, false);
        localSSADialog.setVisible(true);

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        this.jProgressBar1.setVisible(true);
        this.aioSpecToolDetached.serverList = null;
        this.setTrees();

    }//GEN-LAST:event_jButton3ActionPerformed

    public String getUrl(String url) {

        String[] url2 = url.split("FORMAT");
        String newUrl = url2[0];
        return newUrl;
    }

    public void buildQuery() {

        ssaServerListQuery = new SsaServerList();
        ssaServerListToQuery = new SsaServerList();
        boolean isTsa = false;
        boolean isLocal = false;

        for (int i = 0; i < number_server; i++) {
            SsaServer ssaServer = new SsaServer();
            ssaServer = ssaServerList.getSsaServer(i);
            //isTsa = ssaServer.getTsa();
            isLocal = ssaServer.getLocal();

            String name = null;

            name = ssaServer.getSsaName();
            String url = null;

            for (int j = 0; j < serverSelected.size(); j++) {
                String serverSel = (String) serverSelected.elementAt(j);
                if (serverSel.equals(name)) {
                    String query = "";
                    Hashtable paramQuery = (Hashtable) ssaServer.getParams();
                    if (isTsa) {
                        url = getUrl(ssaServer.getSsaUrl());
                        query = url;
                    } else {
                        query = ssaServer.getSsaUrl();
                    }
                    Enumeration keys = paramQuery.keys();
                    while (keys.hasMoreElements()) {
                        String param = (String) keys.nextElement();
                        String value = (String) paramQuery.get(param);


                        //ConeSearch Photometry arrangements

                        // Convert size to arcminutes
                        if (param.equals("SIZE") && ssaServer.getType() == SsaServer.CONE_SEARCH) {
                            if (ssaRequest.getTarget().length() > 0) {
                                continue;
                            } else {
                                value = String.valueOf(Double.valueOf(value) * 60);
                            }

                        }

                        // If there is a target, instead of POS use the target name
                        if (ssaServer.getType() == SsaServer.CONE_SEARCH &&
                                ssaRequest.getTarget().length() > 0) {

                            if (param.equals("POS")) {
                                try {
                                    query = query + "&" + "-c=" + URLEncoder.encode(ssaRequest.getTarget(), "UTF-8");
                                } catch (UnsupportedEncodingException ex) {
                                    Logger.getLogger(VOSpecAdvancedSelector.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                continue;
                            }

                        }
                        //End of ConeSearch photometry arrangements


                        //SSA Photometry arrangements
                        if (ssaServer.getType() == SsaServer.SSA_PHOTO) {

                            if (param.equals("POS")) {
                                try {
                                    query = query + "&TARGETNAME=" + URLEncoder.encode(ssaRequest.getTarget(), "UTF-8");
                                } catch (UnsupportedEncodingException ex) {
                                    Logger.getLogger(VOSpecAdvancedSelector.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                continue;
                            }


                        }

                        //End of SSA Photometry arrangements


                        if (param.equals("POS")) {
                            value = this.parsePos(value);
                        }



                        query = query + "&" + param + "=" + value;

                    }

                    System.out.println("QUERY "+query);

                    SsaServer ssaServerQuery = new SsaServer();
                    ssaServerQuery.setSsaName(ssaServer.getSsaName());
                    ssaServerQuery.setSsaUrl(query);
                    ssaServerQuery.setSelected(true);
                    //ssaServerQuery.setTsa(isTsa);
                    ssaServerQuery.setLocal(isLocal);
                    ssaServerQuery.setType(ssaServer.getType());

                    listModel.addElement(query);
                    outlook.setModel(listModel);
                    queryOutlook.setViewportView(outlook);
                    ssaServerListQuery.addSsaServer(j, ssaServerQuery);
                }
            }
        }
        ssaServerListToQuery.addSsaServerList(ssaServerListQuery);
        listModel.addElement("-----------");
        outlook.setModel(listModel);
        queryOutlook.setViewportView(outlook);
    }

    public void sendQuery() {
        aioSpecToolDetached.ssaServerList = this.ssaServerListToQuery;
        aioSpecToolDetached.localDataDialog.setSelectedForAll(false);
        aioSpecToolDetached.showResults();
    }

    public void addParamValue(String paramName, String paramValue) {

        if (!paramValue.equals("")) {
            addValueToParamTree(paramName, paramValue);
            //if (!paramName.equals("TARGET.NAME")) {
            couple.put(paramName, paramValue);
            paramSelectedVector.put(paramName, new Node(paramName));
            //check all services selected and add param/value
            addParamValueToServices(paramName, paramValue);
            intersectionServices();
            //}
            buildQuery();
        } else {
            JOptionPane.showMessageDialog(this, " Insert value for this param ");
        }

    }

    public void addParamValueForCombo(Node node, String paramName, String paramValue) {
        if (!paramValue.equals("")) {
            couple.put(paramName, paramValue);
            paramSelectedVector.put(paramName, new Node(paramName));
            //check all services selected and add param/value
            TreePath path = getPath(node);
            addParamValueToServicesTSA(node, paramName, paramValue);
            addValueToParamTreeTSA(path, paramName, paramValue);
            findAndSelectByNameTSA(path, paramName, true);
            intersectionServices();
            buildQuery();
        } else {
            JOptionPane.showMessageDialog(this, " Insert value for this param ");
        }
    }

    public void addValueToParamTree(String paramName, String paramValue) {
        findAndAddValuetoParam(paramTree, paramName, paramValue, true);
        refreshParamTree();
    }

    public void addValueToParamTreeTSA(TreePath path, String paramName, String paramValue) {
        findAndAddValuetoParamTSA(path, paramName, paramValue, true);
        refreshParamTree();
    }

    public void refreshParamTree() {
        //to see what's obsolete
        paramTree.setModel(treeModelParam);
        paramTree.setCellRenderer(new ParamsTree());
        paramPanel.setViewportView(paramTree);
    }

    public void refreshServerListTree() {
        //to see what's obsolete
        serverListTree.setModel(serverListModel);
        serverListTree.setCellRenderer(new CheckRendererExtended());
        serverListTree.setBackground(new Color(255, 255, 255));
        listPanel.setViewportView(serverListTree);
    }

    //When value is added to param set node isValueSelected (shows param value)
    public TreePath findAndAddValuetoParam(JTree paramTree, String param, String value, boolean select) {
        TreeNode root = (TreeNode) paramTree.getModel().getRoot();
        return findAndAddValuetoParam2(paramTree, new TreePath(root), param, value, 0, true, select);
    }

    public TreePath findAndAddValuetoParam2(JTree tree, TreePath parent, String paramToFind, String value, int depth, boolean byName, boolean select) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        Object o = node;
        // If by name, convert node to a string
        if (byName) {
            o = o.toString();
        }

        Node nodeToBeSelected = (Node) parent.getLastPathComponent();
        // If equal, go down the branch
        if (o.equals(paramToFind)) {
            nodeToBeSelected.setValueSelected(value);
            nodeToBeSelected.isValueSelected(true);
        }

        // If at end, return match
        if (depth == tree.getVisibleRowCount() - 1) {
            return parent;
        }

        // Traverse children
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                findAndAddValuetoParam2(tree, path, paramToFind, value, depth + 1, byName, select);
            }
        }

        // No match at this branch
        return null;
    }

    //When value is added to param set node isValueSelected (shows param value)
    public TreePath findAndAddValuetoParamTSA(TreePath path, String param, String value, boolean select) {
        return findAndAddValuetoParam3(paramTree, path, param, value, 0, true, select);
    }

    public TreePath findAndAddValuetoParam3(JTree tree, TreePath path, String paramToFind, String value, int depth, boolean byName, boolean select) {
        TreeNode node = (TreeNode) path.getLastPathComponent();
        Object o = node;
        // If by name, convert node to a string
        if (byName) {
            o = o.toString();
        }

        Node nodeToBeSelected = (Node) path.getLastPathComponent();
        // If equal, go down the branch
        if (o.equals(paramToFind)) {
            nodeToBeSelected.setValueSelected(value);
            nodeToBeSelected.isValueSelected(true);
        }

        // If at end, return match
        if (depth == tree.getVisibleRowCount() - 1) {
            return path;
        }

        // Traverse children
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath pathy = path.pathByAddingChild(n);
                findAndAddValuetoParam2(tree, pathy, paramToFind, value, depth + 1, byName, select);
            }
        }

        // No match at this branch
        return null;
    }

    public boolean checkIfValueIsIncludedInMinMax(String value, TsaServerParam tsaP) {
        boolean isIncluded = false;
        try {
            double valueDouble = new Double(value).doubleValue();
            double min = new Double(tsaP.getMinString()).doubleValue();
            double max = new Double(tsaP.getMaxString()).doubleValue();
            if (valueDouble >= min && valueDouble <= max) {
                isIncluded = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isIncluded;
    }

    //check all services selected and add paramName/paramValue
    public void addParamValueToServices(String paramName, String paramValue) {

        for (int i = 0; i < number_server; i++) {

            SsaServer ssaServer = ssaServerList.getSsaServer(i);
            String name = ssaServer.getSsaName();

            for (int j = 0; j < serverSelected.size(); j++) {
                String serverName = (String) serverSelected.elementAt(j);

                if (name.equals(serverName)) {
                    if (serverHasThisParam(ssaServer, paramName)) {
                        ssaServer.addParam(paramName, paramValue);
                    }
                }
            }
        }
    }

    public void addParamValueToServicesTSA(Node node, String paramName, String paramValue) {


        TreeNode parent = (TreeNode) node.getParent();

        for (int i = 0; i < number_server; i++) {

            SsaServer ssaServer = ssaServerList.getSsaServer(i);
            String name = ssaServer.getSsaName();

            if (name.equals(parent.toString())) {
                if (serverHasThisParam(ssaServer, paramName)) {
                    ssaServer.addParam(paramName, paramValue);
                }
            }
        }
    }

    public void removeParamValueToService(String param) {

        for (int i = 0; i < number_server; i++) {

            SsaServer ssaServer = ssaServerList.getSsaServer(i);
            String name = ssaServer.getSsaName();

            for (int j = 0; j < serverSelected.size(); j++) {
                String serverName = (String) serverSelected.elementAt(j);

                if (name.equals(serverName)) {
                    if (serverHasThisParam(ssaServer, param)) {
                        ssaServer.removeParam(param);
                    }
                }
            }
        }

    }

    public void removeParamValueToServiceTSA(Node node, String param) {

        TreeNode parent = (TreeNode) node.getParent();

        for (int i = 0; i < number_server; i++) {

            SsaServer ssaServer = ssaServerList.getSsaServer(i);
            String name = ssaServer.getSsaName();

            if (name.equals(parent.toString())) {
                if (serverHasThisParam(ssaServer, param)) {
                    ssaServer.removeParam(param);
                }
            }
        }
    }

    //in case deselect server containing params/values
    public void removeParamValueToService(Node node) {

        for (int i = 0; i < number_server; i++) {

            //there is just one server selected supporting param included in server that has been deselected
            boolean thereIsOne = true;

            SsaServer ssaServer = ssaServerList.getSsaServer(i);
            String name = ssaServer.getSsaName();

            if (name.equals(node.toString())) {

                Enumeration keys = ssaServer.getParams().keys();
                while (keys.hasMoreElements()) {

                    String param = (String) keys.nextElement();
                    //check all servers selected if they have other param/value  
                    for (int z = 0; z < serverSelected.size(); z++) {
                        String ssaServerTmp = (String) serverSelected.elementAt(z);
                        for (int r = 0; r < number_server; r++) {
                            SsaServer ssa = ssaServerList.getSsaServer(r);
                            String n = ssa.getSsaName();
                            if (n.equals(ssaServerTmp)) {
                                if (serverHasThisParam(ssa, param) && !ssa.getSsaName().equals(name)) {
                                    thereIsOne = false;
                                }
                            }
                        }
                    }
                    //if there are no more servers having that param, it is removed
                    if (thereIsOne && !param.equals("POS") && !param.equals("TARGETNAME") && !param.equals("SIZE")) {
                        paramSelectedVector.remove(param);
                        findAndSelectByName(paramTree, param, false);
                        //else it stays
                    } else {
                        //System.out.println("There are more, param stays "+param);
                    }

                }
                ssaServer.resetParams();
                node.setIsSelected(false);
                refreshServerListTree();
            }
        }
    }

    //used in resetAll
    public void removeParamValueToService() {

        for (int i = 0; i < number_server; i++) {
            SsaServer ssaServer = ssaServerList.getSsaServer(i);
            ssaServer.resetParams();
        }

    }

    public boolean serverHasThisParam(SsaServer server, String paramName) {
        boolean hasThisParam = false;
        Vector paramsSupported = new Vector();
        paramsSupported = (Vector) server.getInputParams();

        for (int g = 0; g < paramsSupported.size(); g++) {
            if (server.getType() == SsaServer.TSAP) {
                TsaServerParam tsaServerParam = (TsaServerParam) paramsSupported.elementAt(g);
                if ((tsaServerParam.getName()).equals(paramName)) {
                    hasThisParam = true;
                }
            } else {
                String[] paramToCompare = (String[]) paramsSupported.elementAt(g);
                if (paramToCompare[0].equals(paramName)) {
                    hasThisParam = true;
                }
            }
        }
        return hasThisParam;
    }

    public void initComponent2() {
        paramText = new javax.swing.JLabel();
        paramField = new javax.swing.JTextField();

        paramText.setFont(new java.awt.Font("Dialog", 1, 10));
        paramText.setForeground(new java.awt.Color(102, 102, 102));
        paramText.setText("Text Param");
        paramText.setToolTipText("Choose Param from Tree Window");
        paramText.setPreferredSize(new java.awt.Dimension(100, 13));

        paramField = new javax.swing.JTextField();
        paramField.setColumns(15);
        paramField.setText("");

        panelComponent2 = new JPanel();
        panelComponent2.setBackground(new java.awt.Color(235, 235, 235));
        panelComponent2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        panelComponent2.setPreferredSize(new java.awt.Dimension(400, 30));
        panelComponent2.add(paramText);
        panelComponent2.add(paramField);
        setButton();
        addButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonPerform();
            }
        });
        paramField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent evt) {
                returnPressed(evt);
            }
        });
        panelComponent2.add(addButton);
        panelComponent.add(panelComponent2);
        panelComponent.repaint();
        panelComponent.revalidate();

        selectAllCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    Enumeration enume = rootNode.breadthFirstEnumeration();
                    setWaitCursor();
                    //check ssa nodes and set green services compatible with param
                    while (enume.hasMoreElements()) {
                        Node node = (Node) enume.nextElement();
                        for (int i = 0; i < number_server; i++) {
                            String ss = ssaServerList.getSsaServer(i).getSsaName();
                            if ((node.toString()).equals(ss) && ssaServerList.getSsaServer(i).getType() != SsaServer.TSAP /*&& node.getDownloaded()*/) {
                                if (selectAllCheckBox.isSelected() && !node.getIsSelected()) {
                                    node.setIsSelected(true);
                                    addServiceToParamTree(node);
                                    serverSelected.add(node.toString());
                                }
                                if (!selectAllCheckBox.isSelected() && node.getIsSelected()) {
                                    node.setIsSelected(false);
                                    serverSelected.remove(node.toString());
                                    //reset params/value service node
                                    removeParamValueToService(node);
                                    removeServiceFromParamTree(node);
                                }
                            }
                        }
                    }
                    //traverse paramTree and select all nodes with the same name
                    //if one service is selected it checks all params with value and add to this service this param/value if supported
                    ////////////////////////////////////////////////////////////////
                    if (couple.size() > 0) {
                        Enumeration keys = paramSelectedVector.keys();
                        while (keys.hasMoreElements()) {
                            String n = (String) keys.nextElement();
                            Node node2 = (Node) paramSelectedVector.get(n);
                            if (couple.get(node2.toString()) != null) {
                                if (!node2.isParamTsa()) {
                                    //check all services selected and add param/value
                                    addParamValueToServices(node2.toString(), (String) couple.get(node2.toString()));
                                    //refresh paramTree with new param/values
                                    addValueToParamTree(node2.toString(), (String) couple.get(node2.toString()));
                                }
                            }
                        }
                    }
                    ////////////////////////////////////////////////////////////////
                    checkOtherParamToSelect();
                    setDefaultCursor();
                    buildQuery();
                    intersectionServices();

                } catch (Exception ev) {
                    ev.printStackTrace();
                }
            }
        });
    }

    public void setWaitCursor() {
        Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
        setCursor(hourglassCursor);
    }

    public void setDefaultCursor() {
        Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        setCursor(normalCursor);
    }

    public void returnPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            addButtonPerform();
        }
    }

    public void addButtonPerform() {

        String paramName = paramText.getText();
        String paramValue = paramField.getText();

        if (paramName.equals("Text Param")) {
            JOptionPane.showMessageDialog(this, "No match");
            return;
        }

        if (paramName.equals("TARGET.NAME")) {
            boolean success = callSesame(paramValue);
            removeParamValueFromParamTree("TARGETNAME");
            if (!success) {
                return;
            }
        }

        if (paramName.equals("TARGETNAME")) {
            boolean success = callSesame(paramValue);
            removeParamValueFromParamTree("TARGET.NAME");
            if (!success) {
                return;
            }
        }

        if (paramName.equals("POS")) {
            removeParamValueFromParamTree("TARGETNAME");
            removeParamValueFromParamTree("TARGET.NAME");
        }


        try {
            addParamValue(paramName, paramValue);

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

    public void removeParamValueFromParamTree(String param) {
        TreePath path2 = findAndSelectByName(paramTree, param, false);
        removeParamValueToService(param);
        paramSelectedVector.remove(param);
        //set green all the services supporting param node.toString()
        checkOtherServicesSelectedIfTheyHaveSameParam(new Node(param), false);
        refreshParamTree();
    }

    public boolean callSesame(String paramValue) {
        try {
            String as[] = Sesame.getNameResolved(paramValue);
            ssaRequest.setTarget(paramValue);
            if (as != null) {
                String pos = as[0] + "," + as[1];

                couple.put("POS", pos);
                paramSelectedVector.put("POS", new Node("POS"));
                addParamValueToServices("POS", pos);
                addValueToParamTree("POS", pos);
                findAndSelectByName(paramTree, "POS", true);
                //set green all the services supporting POS
                checkOtherServicesSelectedIfTheyHaveSameParam(new Node("POS"), true);

            } else {
                JOptionPane.showMessageDialog(this, "No match");
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No connection to Name Resolver");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void initTextField(Node node, TsaServerParam tsaServerParam) {

        initComponent2();

        paramText.setText(tsaServerParam.getName());
        paramText.setToolTipText(tsaServerParam.getDescription());

        Vector values = tsaServerParam.getValues();
        String stringValue = "";

        if (values.size() > 0) {
            stringValue = (String) values.elementAt(0);
        }
        paramField.setText(node.getValueSelected());

        panelComponent.repaint();
        panelComponent.revalidate();

    }

    public void initComboBox(Node node, TsaServerParam tsaServerParam) {

        this.specificNode = node;
        paramText = new javax.swing.JLabel();

        paramText.setFont(new java.awt.Font("Dialog", 1, 10));
        paramText.setForeground(new java.awt.Color(102, 102, 102));
        paramText.setText(tsaServerParam.getName());
        paramText.setToolTipText(tsaServerParam.getDescription());
        paramText.setPreferredSize(new java.awt.Dimension(100, 13));

        optionComboBox = new JComboBox();
        optionComboBox.setFont(new java.awt.Font("SansSerif", 1, 10));
        optionComboBox.setForeground(new java.awt.Color(102, 102, 102));
        optionComboBox.setPreferredSize(new java.awt.Dimension(150, 24));

        Vector values = tsaServerParam.getValues();

        for (int ct2 = 0; ct2 < values.size(); ct2++) {
            optionComboBox.addItem(values.elementAt(ct2));
        }


        /** default choice in hashtable and in TsaServer bean**/
        String label;
        if (tsaServerParam.getSelectedValue() != null) {
            label = tsaServerParam.getSelectedValue();
        } else {
            label = (String) optionComboBox.getItemAt(0);
        }

        optionComboBox.setSelectedItem(node.getValueSelected());

        optionComboBox.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String paramName = paramText.getText();
                String paramValue = (String) optionComboBox.getSelectedItem();
                //add para/value to a specific node (Tsa case)
                addParamValueForCombo(specificNode, paramName, paramValue);
                tsaHasBeenSet = true;
            }
        });
        tsaServerParam.setSelectedValue(label);

        panelComponent2 = new JPanel();
        panelComponent2.setBackground(new java.awt.Color(235, 235, 235));
        panelComponent2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));
        panelComponent2.setPreferredSize(new java.awt.Dimension(400, 30));
        panelComponent2.add(paramText);
        panelComponent2.add(optionComboBox);

        setButton();
        addButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String paramName = paramText.getText();
                String paramValue = (String) optionComboBox.getSelectedItem();
                //add para/value to a specific node (Tsa case)
                addParamValueForCombo(specificNode, paramName, paramValue);
                tsaHasBeenSet = true;
            }
        });
        panelComponent2.add(addButton);
        panelComponent.add(panelComponent2);
        panelComponent.repaint();
        panelComponent.revalidate();

    }

    public void setButton() {
        addButton = new JButton();
        addButton.setFont(new java.awt.Font("Dialog", 1, 10));
        addButton.setForeground(new java.awt.Color(102, 102, 102));
        addButton.setText("Add");
    }

    public void resetAndShow() {
        resetAll();
        this.setVisible(true);
    }
    int threads = 0;
    int threads_ready = 0;

    public void setServerList() {

        if (aioSpecToolDetached.serverList == null) {

            ssaServerList = RegistryIngester.getSsaServerList();
            threads = ssaServerList.ssaServerList.size();
            threads_ready = 0;

            this.selectAllCheckBox.setEnabled(false);
            setEnabled(false);
            setWaitCursor();

            for (int i = 0; i < ssaServerList.ssaServerList.size(); i++) {
                FormatMetadataThread formatMetadataThread = new FormatMetadataThread(this, ssaServerList.getSsaServer(i));
                formatMetadataThread.start();
            }

            aioSpecToolDetached.serverList = ssaServerList;

        } else {

            this.selectAllCheckBox.setEnabled(true);
            ssaServerList = aioSpecToolDetached.serverList;
            this.jProgressBar1.setVisible(false);
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    createServiceTree();
                    initJTree();
                    initParamTree();
                    getBasicParamsToBeSelected();
                }
            });
        }
    }

    public void threadReady() {
        threads_ready++;
        setFormatMetadataProgress(threads_ready, threads);
        if (threads_ready >= threads) {

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    createServiceTree();
                    initJTree();
                    initParamTree();
                    getBasicParamsToBeSelected();
                    setEnabled(true);
                    setDefaultCursor();
                }
            });


        }

    }

    private void setFormatMetadataProgress(int completed, int total) {
        int progress = (int) Math.floor(new Float(completed) / new Float(total) * 100);
        this.jProgressBar1.setValue(progress);
        if (completed < total) {
            this.jProgressBar1.setString("Gathering parameters: " + completed + "/" + total);
            this.jProgressBar1.repaint();
        } else {
            this.jProgressBar1.setString("Ready");
            this.jProgressBar1.repaint();
            this.selectAllCheckBox.setEnabled(true);
        }
    }

    public boolean exists(String URLName) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            // note : you may also need
            //HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void createServiceTree() {

        rootNode = new Node("Server Selector");
        Node ssaNode = new Node("Observational Spectra Services");
        Node csNode = new Node("Photometry Services");
        Node tsaNode = new Node("Theoretical Spectra Services");


        String currentTitle;

        //Sort ssaServerList
        Vector serverVector = new Vector(ssaServerList.getSsaServerList().values());
        Collections.sort(serverVector);
        Hashtable resultServers = new Hashtable();
        Hashtable resultNames = new Hashtable();
        for (int i = 0; i < serverVector.size(); i++) {
            if (resultNames.get(((SsaServer) serverVector.get(i)).getSsaName()) != null) {
                ((SsaServer) serverVector.get(i)).setSsaName(((SsaServer) serverVector.get(i)).getSsaName() + "(2)");
            }
            resultServers.put("" + i, (SsaServer) serverVector.get(i));
            resultNames.put(((SsaServer) serverVector.get(i)).getSsaName(), i);
        }
        ssaServerList.ssaServerList = resultServers;
        number_server = (ssaServerList.getSsaServerList()).size();
        rootNode.add(ssaNode);
        rootNode.add(csNode);
        rootNode.add(tsaNode);

        for (int z = 0; z < number_server; z++) {
            try {
                SsaServer server = (SsaServer) ssaServerList.getSsaServer(z);

                currentTitle = server.getSsaName();
                titleNode = new Node(currentTitle);
                /*
                if (server.getTsa()) {
                tsaNode.add(titleNode);
                } else {
                ssaNode.add(titleNode);
                }
                 */
                //System.out.println(server.getSsaName() + " " + server.getType());

                if (server.getType() == SsaServer.TSAP) {
                    tsaNode.add(titleNode);
                } else if (server.getType() == SsaServer.SSAP ||
                        server.getType() == SsaServer.PSAP) {
                    ssaNode.add(titleNode);
                } else if (server.getType() == SsaServer.CONE_SEARCH ||
                        server.getType() == SsaServer.SSA_PHOTO ) {
                    csNode.add(titleNode);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /*
     * Adds a service manually to the service tree, based on his URL
     *
     * Allows user-added SSA/TSA servers, searching the query parameters
     * in the fly with SSAParamQuery.populateInputParameters
     *
     * */
    public void addServiceToServiceTree(String url, boolean isTsa, String name) {

        SsaServer ssaServer = new SsaServer();

        url = url.trim();

        if (url.indexOf("?") < 0) {
            url = url + "?";
        }

        ssaServer.setSsaUrl(url);
        ssaServer.setSsaName(name);
        ssaServer.setSelected(true);
        ssaServer.setLocal(false);

        if (isTsa) {
            ssaServer.setType(SsaServer.TSAP);
        } else {
            ssaServer.setType(SsaServer.SSAP);
        }
        //ssaServer.setTsa(isTsa);

        //ssaServer.setInputParams(this.getParamOptionsFromSavot(url + "&FORMAT=METADATA", false));

        ssaServer = SSAIngestor.populateInputParameters(ssaServer);

        /*
        if(!isTsa){
        ssaServer.setSsaUrl(ssaServer.getSsaUrl()+"&POS="+ra+","+dec+"&SIZE="+size);
        }else{
        ssaServer.setSsaUrl(ssaServer.getSsaUrl());
        }
         */

        ssaServerList.addSsaServer(ssaServerList.ssaServerList.size(), ssaServer);
        createServiceTree();
        initJTree();
        initParamTree();
        getBasicParamsToBeSelected();

        this.serverListTree.repaint();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new VOSpecAdvancedSelector(true).setVisible(true);
//            }
//        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel insertPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JScrollPane listPanel;
    private javax.swing.JList outlook;
    private javax.swing.JPanel panelComponent;
    private javax.swing.JPanel panelContainer;
    private javax.swing.JScrollPane paramPanel;
    private javax.swing.JTree paramTree;
    private javax.swing.JPanel paramsQueryPanel;
    private javax.swing.JButton queryButton;
    private javax.swing.JScrollPane queryOutlook;
    private javax.swing.JTabbedPane queryParams;
    private javax.swing.JCheckBox selectAllCheckBox;
    private javax.swing.JTree serverListTree;
    // End of variables declaration//GEN-END:variables
    public VOSpecDetached aioSpecToolDetached;
    public Node rootNode = new Node("Server Selector");
    public Node titleNode;
    public Node urlNode;
    public Node rootQueryNode;
    public Node targetQueryNode;
    public Node simpleQueryNode;
    public Node advancedQueryNode;
    public Node serviceSpecificNode;
    public SsaServerList ssaServerList = new SsaServerList();
    public SsaServerList ssaServerListQuery = new SsaServerList();
    public SsaServerList ssaServerListToQuery = new SsaServerList();
    public int number_server = 0;
    public Vector listTitle = null;
    public Vector listUrl = null;
    public Vector listTitleSelected = null;
    public Vector listUrlSelected = null;
    public DefaultListModel listModel = null;
    public DefaultTreeModel serverListModel = null;
    public DefaultTreeModel treeModelParam = null;
    public Vector serverSelected = null;
    public Hashtable paramSelectedVector = new Hashtable();
    public Hashtable couple = new Hashtable();
    public JComboBox optionComboBox = null;
    public JTextField paramField = null;
    public JLabel paramText = null;
    public JPanel panelComponent2 = null;
    public JButton addButton = null;
    public SsaRequest ssaRequest = null;
    public LocalSSADialog localSSADialog = null;
    public Node specificNode = null;
    //public TsaServerParam tsaParam = null;
    boolean tsaHasBeenSet = false;
    int tsaCounter = 0;
    public TsaServerParam TSAPARAM = null;
}
