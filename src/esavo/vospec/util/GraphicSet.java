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
package esavo.vospec.util;

import java.io.*;
import java.util.*;

/**
 *
 * @author ibarbarisi
 * @version %version: 2 %
 */

public class GraphicSet implements Serializable{

    public Hashtable graphicSet; 
        
    public GraphicSet() {
    	//constructor
	graphicSet = new Hashtable();
    }
    
    public void addGraphic(int ct,Graphic graphic) {
    	graphicSet.put(""+ct,graphic);
    }
    
    public Hashtable getGraphicSet() {
    	return graphicSet;
    }
        
    public Graphic getGraphic(int ct) {
	return (Graphic) graphicSet.get(""+ct);
    }
    
    public void addGraphicSet(GraphicSet graphicSet) {
          int thisSize = this.graphicSet.size();
          
          for(int j = 0;j<graphicSet.getGraphicSet().size();j++) {
                int position = thisSize + j;
          	Graphic graphic = graphicSet.getGraphic(j);
                addGraphic(position, graphic);
          }      
    }
    
   
    
}
