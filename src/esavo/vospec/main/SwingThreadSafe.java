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

import esavo.vospec.resourcepanel.*;
import esavo.vospec.spectrum.SpectrumSet;
import java.util.Vector;

public class SwingThreadSafe implements Runnable{

	private String 			method = "";
        private VOSpecDetached 	aioSpecToolDetached;

        private Node 		node;
	
        private String                  nodeName;
        private SpectrumSet             sv;
        private boolean                 isTSA;
        private boolean                 createNodes;

       private  Vector                  nodesVector;

       private TreePanel treePanel;


	public SwingThreadSafe(String method, VOSpecDetached aioSpecToolDetached) {
         	
	        this.aioSpecToolDetached 	= aioSpecToolDetached;		
		this.method 			= method;
        }
        /*
        public SwingThreadSafe(String method, AioSpecToolDetached aioSpecToolDetached, Node checkNode) {
         	
                this(method,aioSpecToolDetached);
                
                this.checkNode 			= checkNode;
        }*/


	public SwingThreadSafe(String method, VOSpecDetached aioSpecToolDetached, String nodeName, SpectrumSet sv, boolean isTSA, boolean createNodes) {
         	
               this(method,aioSpecToolDetached);
               
               this.nodeName    = nodeName;
               this.sv          = sv;
               this.isTSA       = isTSA;
               this.createNodes = createNodes;
        }

	public SwingThreadSafe(String method, VOSpecDetached aioSpecToolDetached, String nodeName, Vector nodesVector) {
         	
               this(method,aioSpecToolDetached);
               
               this.nodeName    = nodeName;
               this.nodesVector = nodesVector;
        }

    SwingThreadSafe(String method, TreePanel treePanel) {
        this.treePanel=treePanel;
        this.method=method;
    }

    SwingThreadSafe(String method, TreePanel treePanel, Node node) {
        this(method, treePanel);
        this.node=node;
    }


         

	
	 public void run() {
	 
	 	//if(method.equals("expandNode_NT"))                          this.treePanel.expandNode_NT(node);
	 	//if(method.equals("collapseNode_NT"))                        this.aioSpecToolDetached.collapseNode_NT(checkNode);
	 	if(method.equals("addLocalData_NT"))                        this.aioSpecToolDetached.addLocalData_NT();
	 	//if(method.equals("refreshJTree_NT"))                        this.aioSpecToolDetached.refreshJTree_NT();
//	 	if(method.equals("removeComponentFromColorContainer_NT"))   this.aioSpecToolDetached.removeComponentFromColorContainer_NT();
//	 	if(method.equals("displaySpectraColorList_NT"))             this.aioSpecToolDetached.displaySpectraColorList_NT();
        if(method.equals("repaintServerListTree_NT"))               this.treePanel.repaintServerListTree_NT();
                
//	 	if(method.equals("addSpectraToNode_NT")) this.aioSpecToolDetached.addSpectraToNode_NT(nodeName, sv, isTSA, createNodes);
//	 	if(method.equals("addNodes_NT")) this.aioSpecToolDetached.addNodes_NT(nodeName, nodesVector);
	}
	
	
	
}		
