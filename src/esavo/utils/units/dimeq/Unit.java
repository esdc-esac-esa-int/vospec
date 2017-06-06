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
package esavo.utils.units.dimeq;

import java.util.*;

/**
 *
 * Unit class to set scalings and dimensional equations 
 *
 * @author J. Salgado (ESAC/ESA)
 *
 * @version 1.0
 */
 
public class Unit {

	/**
	* Scaling factor for this units
	*/
	private double 		scaling;
	/**
	* Hashtable containing the dimensional equation for this unit
	*/
	private Hashtable	dimeqHashtable;
	
	/**
   	* Void Constructor. Constructs a Unit object. 
	*
	* @see #Unit(double,String)
  	**/
	public Unit() {
		dimeqHashtable = new Hashtable();		
	}
	
	/**
   	* Constructs a Unit object. It will call internally to createDimeqHashtable to create a Hashtable representation of the
	* dimensional equation
	*
	* @param scaling Scaling factor for this unit
	* @param dimeq String containig the dimensional equation for this unit
	*
	* @see #createDimeqHashtable(String)
	*
  	**/
	public Unit(double scaling, String dimeq) {
		setScaling(scaling);
		setDimeq(dimeq);
	} 
	
	/**	
	*	Set the scaling factor
	*
	*	@param scaling Scaling factor
	*
	**/
	public void setScaling(double scaling) {
		this.scaling = scaling;
	}
	
	/**	
	*	Set the dimeq hashtable from the string representation
	*
	*	@param dimeq String representation of the dimensional equation
	**/
	public void setDimeq(String dimeq) {
		this.dimeqHashtable = createDimeqHashtable(dimeq);
	}
	
	/**
	*	Returns the scaling factor for this unit
	*	@return Scaling factor
	**/
	public double getScaling() {
		return scaling;
	}
	
	/**
	*	Returns a Hashtable containing the dimensional equation for this unit
	*	@return Dimensional equation Hashtable
	**/
	public Hashtable getDimeqHashtable() {
		return dimeqHashtable;
	}
    
  	/**
	*	Creates a Hashtable representation of the
	* 	dimensional equation
	*
	*	@param	dimeq The string representation of the dimensional equation
	*	@return A Hashtable representation of the dimensional equation
	*
	**/
    
       	private Hashtable createDimeqHashtable(String dimeq) {
      
       		String 	   dimEQ 		= dimeq.toUpperCase();
      		Hashtable  dimeqHashtable 	= new Hashtable();
      
      		char c; 
      		double exponent = 0.;
		for(int i=0; i < dimEQ.length(); i++) {
			c = dimEQ.charAt(i);
			if(Character.isLetter(c)) {
				exponent = getExponentForCoefficient(dimEQ,c+"");
				dimeqHashtable.put(c+"", new Double(exponent));
			}
		
		}
 
          	return dimeqHashtable;
   	}
   
        /**
	*	Extracts the exponent from a string representation of a dimensional equation for
	*	a certain dimensional coordinate (M,L,T,...). It is a double to cover unusual exponents
	*
	*	@param dimEq String representation of a dimensional equation
	*	@param coefficient String name of the dimensional coordinate
	*
	*	@return The exponent
	*
	**/
   	private double getExponentForCoefficient(String dimEq, String coefficient) {
       
       		int index=0;
       
       		if (dimEq.indexOf(coefficient)<0){
           		return 0;
       		} else {
           		index= dimEq.indexOf(coefficient);
       		}
       
       		index++;
       
       		long sign = 1;
       		if(index == dimEq.length()) return 1;
       
       		if(dimEq.substring(index,index+1).equals("-")) {
           		sign = -1;
           		index++;
       		} else if(dimEq.substring(index,index+1).equals("+")) {
           		sign = 1;
           		index++;           
       		}
       
       		String numberString = "";
       		while(index < dimEq.length()) {
           		if(!Character.isDigit(dimEq.charAt(index)))break;
           		numberString = numberString + dimEq.substring(index,index+1);
           		index++;
       		}
       
      		 if(numberString.equals("")||numberString.equals("/"))numberString="1";
       
       		double number = sign * (new Double(numberString)).doubleValue();
       		return number;
   	}
   
}
