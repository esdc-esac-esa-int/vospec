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

import Jama.*;
import java.util.*;


/**
 *
 * Mother class to make conversions using dimensional analysis. 
 * <p> 
 * 
 * As an example, to convert a spectral point from (Hz,Jy) to (um,W/cm^2/um) the algorithm steps are
 * the following:
 *
 * Get the dimensional equations for all the units/quantities:<p>
 * <pre>
 * 	Hz = 		1. 		T-1
 * 	Jy = 		1.E-26 		MT-2
 * 	um =		1.E-6		L
 *	W/cm^2/um =	1.E10		ML-1T-3
 *
 *	c =		299792458.	LT-1
 *	h =		6.626068E-34	ML2T-1
 * </pre>
 *
 *
 * Create the conversion table including the original units: 
 *
 *  <pre>
 *		Jy	 Hz	 c	 h
 *    ------------------------------------------
 *	M 	 1	| 0	 0	 1
 *	L 	 0	| 0	 1	 2
 *	T	-2	|-1	-1	-1
 *
 * </pre> 
 *
 * The conversion matrix will be the one for Hz, c and h. The to conversion varibles will be defined by
 * the rest.
 *
 * <pre>
 *		|0	1	-1|		
 *	A = 	|0	1	 2|
 *		|-1	-1	-1|	
 *
 *
 *		| 1|
 *	B =	| 0|
 *		|-2|
 * </pre>
 *
 * Construct the general conversion matrix system E in the form:
 *
 * <pre>
 *		|I	0	|
 		|B	-BA^-1	|
 *
 * </pre>
 *
 * where I i the identity matrix, and -BA^-1 is the negative value of the matrix B multiplied by the inverse of A.
 *
 * finally, the Z vectors should be created, using the dimensional equations for the final units:
 *
 * <pre>
 *
 *			| 0| Flux	
 *	Z_wave =	| 0| M
 *			| 1| L
 *			| 0| T
 * 
 *			| 1| Flux
 *	Z_flux = 	| 1| M
 *			|-1| L
 *			|-3| T 
 * </pre>
 * where the first element in the vector is the proporcionallity in the non-Matrix quantities, in this case the Flux.
 * <p>
 * Finally, the conversion vectors are defined by:
 *
 * <pre>
 * 				| 0|	Jy
 * 	P_wave = E * Z_wave = 	|-1|	Hz
 *				| 1|	 c
 *				| 0|	 h
 *
 * 				| 1|	Jy
 * 	P_flux = E * Z_flux = 	| 2|	Hz
 *				|-1|	 c
 *				| 0|	 h
 *
 * </pre>
 *
 * This algorithm is described in the article: 
 * <p>
 * <b>"Dimensional Analysis applied to Spectrum Handling in Virtual Observatory Context"</b>
 * Pedro Osuna, Jesus Salgado	
 *
 * @author J. Salgado (ESAC/ESA)
 *
 * @version 1.0
 */
 

public class GeneralConverter {

	/**
	* Check if the system is already solved.
	*/
	private boolean		solved = false;
	

	/** 
	* This Vector contains the keys for the quantities that are included in the conversion matrix.
	*/
	private Vector		matrixElements;
	/** 
	* This Vector contains the keys for the quantities that are not included in the conversion matrix.
	*/
	private Vector		nonMatrixElements;
	/** 
	* Contains matrix and nonmatrix quantities keys in the inclusion order.
	*/
	private Vector		allElements;
	
	/**
	* Quantities stored in hashtable using the name as key.
	*/
	private Hashtable 	quantityTable;
	/**
	* Dimensions of the system in a specific order.
	*/
	private TreeSet		dimensionsSet;
	
	
	
	/**
   	* Void Constructor. Constructs a GeneralConverter object. 
  	**/
	public GeneralConverter() {
		matrixElements		= new Vector();
		nonMatrixElements	= new Vector();
		allElements		= new Vector();

		quantityTable 		= new Hashtable();
		dimensionsSet		= new TreeSet();
	}
	
	
	/**
   	* Add a quantity to the quantity Hashtable. Same as addQuantity(String,Quantity,true)
	*
	* @param quantityDescription Quantity search Key. Usually the variable/constant name.
	* @param quantity Quantity object. It could be a Variable or a Constant.
	* 
	* @see #addQuantity(String,Quantity,boolean)
	*
  	**/
	public void addQuantity(String quantityDescription, Quantity quantity) {
		addQuantity(quantityDescription,quantity,true);
	}
	
	
	/**
   	* Add a quantity to the quantity Hashtable.
	*
	* @param quantityDescription Quantity search Key. Usually the variable/constant name.
	* @param quantity Quantity object. It could be a Variable or a Constant.
	* @param inConversionMatrix This Quantity to be included or not in the conversion matrix
	* 
	*
  	**/	
	public void addQuantity(String quantityDescription, Quantity quantity, boolean inConversionMatrix) {
		
		quantityTable.put(quantityDescription,quantity);
		
		if(inConversionMatrix) {
			matrixElements.addElement(quantityDescription);
		} else {
			nonMatrixElements.addElement(quantityDescription);
		}	
		
		allElements.addElement(quantityDescription);
	}
	

	/**
   	* Solve the conversion system.
	*
	**/
	public void solve() {
		
		// Extract the MLT.... from the matrixElements and create the dimensions vector		
		String 		thisKey;
		String		element;
		Quantity 	quantity;
		Set 		thisDimensionsSet;
		Iterator 	it;
			
		for (Enumeration e = quantityTable.keys() ; e.hasMoreElements() ;) {        		 
			
			thisKey 		= (String) 		e.nextElement();
			quantity		= (Quantity) 		quantityTable.get(thisKey);
			
			thisDimensionsSet 	= (Set) 		((Hashtable) quantity.getDimeqHashtable()).keySet();
			
			it			= (Iterator) 		thisDimensionsSet.iterator();
			while(it.hasNext()) dimensionsSet.add((String) it.next());			
     		}
		
		
		
		
		// Build the matrix A: Conversion matrix to create the conversion vector 
		//
		Hashtable	dimeqHashtable;
		int 		nDimensions 	= dimensionsSet.size();	
		double[][] 	arrayForA 	= new double[nDimensions][nDimensions];
		
		for(int i=0; i < matrixElements.size(); i++) {
			
			element = (String) matrixElements.elementAt(i);
			
			quantity 	= (Quantity) 	quantityTable.get(element);		
			dimeqHashtable 	= (Hashtable) 	quantity.getDimeqHashtable();
			
			int k=0;
			it		= (Iterator)	dimensionsSet.iterator();
			while(it.hasNext()) { 
				
				String nextElement 	= (String) it.next();
				Double value 		= (Double) dimeqHashtable.get(nextElement);
				if(value == null) 	value = new Double(0.);
				
				arrayForA[k++][i] = ((Double) value).doubleValue();			
			}
		}			
		Matrix A = new Matrix(arrayForA);
		//A.print(10,10);

		// Invert A
		Matrix AInverse = A.inverse();
		
		
		// Calculate matrix B
		int nonMatrixElementsNumber = nonMatrixElements.size();
		
		double[][] 	arrayForB = new double[nDimensions][nonMatrixElementsNumber];
		for(int i=0; i < nonMatrixElementsNumber; i++) {
		
			element 	= (String) 	nonMatrixElements.elementAt(i);
			
			quantity 	= (Quantity) 	quantityTable.get(element);
			dimeqHashtable 	= (Hashtable) 	quantity.getDimeqHashtable();
			
			int k=0;
			it		= (Iterator)	dimensionsSet.iterator();
			while(it.hasNext()) { 
				
				String nextElement 	= (String) it.next();
				Double value 		= (Double) dimeqHashtable.get(nextElement);
				if(value == null) 	value = new Double(0.);
				
				arrayForB[k++][i] = ((Double) value).doubleValue();
			}				
		}
		
		Matrix B 		= new Matrix(arrayForB);
		//B.print(10,10);
		
		Matrix MinusAInversB 	= (AInverse.times(B)).times(-1.);
		//MinusAInversB.print(10,10);
		
		// calculate matrix E
		int 		mAIBColumns 	=  MinusAInversB.getColumnDimension();
		int 		AInvColumns 	=  AInverse.getColumnDimension();
		
		double[][] 	arrayForE 	= new double[mAIBColumns+AInvColumns][mAIBColumns+AInvColumns];
		
		double[][] 	arrayForMinusAInversB 	= MinusAInversB.getArray();
		double[][] 	arrayForAInvColumns 	= AInverse.getArray();
		
		for(int i=0; i < mAIBColumns+AInvColumns; i++) {
			for(int k=0; k < mAIBColumns+AInvColumns; k++) {
				
				if(i < mAIBColumns && k < mAIBColumns) {
					if(i==k) 	arrayForE[i][k] = 1.;
					else		arrayForE[i][k] = 0.;
				}
				
				if(i < mAIBColumns && k >= mAIBColumns) {
					arrayForE[i][k] = 0.;
				}
				
				if(i >= mAIBColumns && k < mAIBColumns) {
					arrayForE[i][k] = arrayForMinusAInversB[i - mAIBColumns][k];
				}
				
				if(i >= mAIBColumns && k >= mAIBColumns) {
					arrayForE[i][k] = arrayForAInvColumns[i - mAIBColumns][k - mAIBColumns];
				}
				
				
			}
		}
		Matrix E = new Matrix(arrayForE);
		//E.print(10,10);
		
		// foreach variable in the quantity table, calculate the vector P
		double[][] 	arrayForZ 	= new double[mAIBColumns+AInvColumns][1]; 
		Hashtable 	tmpHashtable 	= new Hashtable();

		
		for(int i=0; i < allElements.size(); i++) {        		 
			
			thisKey 		= (String) allElements.elementAt(i);
			quantity 		= (Quantity) quantityTable.get(thisKey);
			
			if(quantity instanceof Variable) {
				
				dimeqHashtable 	=  (Hashtable) 	((Variable) quantity).getFinalDimeqHashtable();	
				
				int kIndex = 0;
				
				if(nonMatrixElements.contains(thisKey)) {
					arrayForZ[kIndex][0]	= 1.;			
				} else {
					arrayForZ[kIndex][0]	= 0.;			
				}
				
				kIndex++; 
				 
				it		= (Iterator)	dimensionsSet.iterator();
				while(it.hasNext()) { 
				
					String nextElement 	= (String) it.next();
					Double value 		= (Double) dimeqHashtable.get(nextElement);
					if(value == null) 	value = new Double(0.);
					
					
					arrayForZ[kIndex++][0] = ((Double) value).doubleValue();	
				}
				
				Matrix Z = new Matrix(arrayForZ);
				//Z.print(10,10);
				
				Matrix P = E.times(Z);				
				//P.print(10,10);
			
				((Variable) quantity).setPMatrix(P);
			}		
		
			tmpHashtable.put(thisKey,quantity);
		}	
		
		quantityTable = tmpHashtable;
		
		solved = true;
				
	}
	
	
	/**
	*	Set the initial values to one variable
	*
	*	@param 	variableDescription Key for this variable
	*	@param	variableInitialValue Value for this variable in the original units
	*/
	public void setInitialValue(String variableDescription, double variableInitialValue) {
		
		Variable variable = (Variable) quantityTable.get(variableDescription);
		variable.setInitialValue(variableInitialValue);
		
		quantityTable.put(variableDescription,variable);
		
	}
	
	/**
	*	Get the value for this variable in the final units
	*
	*	@param 	variableDescription Key for this variable
	*	@return Value of the variable in the final units	
	*/
	public double getConvertedValue(String variableDescription) {
	
		if(!solved) solve();
		solved = true;
		
		double   convertedValue = 1.;
		Variable variable	= (Variable) quantityTable.get(variableDescription);
		
		Matrix P = variable.getPMatrix();
		
		Quantity quantity;
		for(int i=0; i < nonMatrixElements.size(); i++) {
		
			quantity = (Quantity) quantityTable.get((String) nonMatrixElements.elementAt(i));
			convertedValue = convertedValue * Math.pow(quantity.getValue(), P.get(i,0));
			
		}
		for(int i=0; i < matrixElements.size(); i++) {
		
			quantity = (Quantity) quantityTable.get((String) matrixElements.elementAt(i));
			convertedValue = convertedValue * Math.pow(quantity.getValue(),P.get(i + nonMatrixElements.size(),0));
			
		}


		convertedValue = convertedValue/variable.getFinalScaling();
		
		// using P vector, calculate conversion
		return convertedValue;
	}
	
	/**
	*	Overrides toString() method
	*	
	*	@return String representation of the conversion
	*/
	public String toString() {
		if(!solved) return super.toString();
		
		String 		conversionString 	= "";
		String 		conversionStringTmp 	= "";
		String 		thisKey;
		
		
		for(int k=0; k < allElements.size(); k++) {        		 
			
			thisKey 		= (String) allElements.elementAt(k);
			conversionStringTmp 	= toString(thisKey);
			
			if(! conversionStringTmp.equals("")) {
				conversionString = conversionString + "(" + thisKey + "] = " + toString(thisKey);			
				conversionString = conversionString + "\n";
			}
		}
		
		return conversionString;		
	}
	
	public String toString(String thisKey) {
	
		if(!solved) return "";
		
		String 		conversionString = "";
		Matrix		P;
		Quantity 	quantity;
		Quantity 	quantityTmp;
		String 		thisElement;

		quantity	= (Quantity) quantityTable.get(thisKey);
		
		if(quantity instanceof Variable) {
			
			P = ((Variable) quantity).getPMatrix();
			for(int i=0; i < nonMatrixElements.size(); i++) {
					
				thisElement = (String) nonMatrixElements.elementAt(i);
				quantityTmp = (Quantity) quantityTable.get((String) nonMatrixElements.elementAt(i));
				if(quantityTmp instanceof Variable) conversionString = conversionString + "(" + thisElement + "]^" + P.get(i,0);
			}
			for(int i=0; i < matrixElements.size(); i++) {
					
				thisElement = (String) matrixElements.elementAt(i);
				quantityTmp = (Quantity) quantityTable.get((String) matrixElements.elementAt(i));
				if(quantityTmp instanceof Variable) conversionString = conversionString + "(" + thisElement + "]^" + P.get(i + nonMatrixElements.size(),0);
			}
			
		}
		
		return conversionString;		
	}	


	public String toLongString(String thisKey) {
	
		if(!solved) return "";
		
		String 		conversionString = "";
		Matrix		P;
		Quantity 	quantity;
		Quantity 	quantityTmp;
		String 		thisElement;

		quantity	= (Quantity) quantityTable.get(thisKey);
		
		if(quantity instanceof Variable) {
			
			P = ((Variable) quantity).getPMatrix();
			for(int i=0; i < nonMatrixElements.size(); i++) {
					
				thisElement = (String) nonMatrixElements.elementAt(i);
				quantityTmp = (Quantity) quantityTable.get((String) nonMatrixElements.elementAt(i));
				if(quantityTmp instanceof Variable) { 
					
					long exponent = Math.round(P.get(i,0));
					
					if(exponent != 0) {
						if(exponent < 0) {
							exponent = exponent * (-1);
							if(exponent != 1) {
								conversionString = conversionString + "/" + "(" + thisElement + ")" + "^" + exponent;
							} else {							
								conversionString = conversionString + "/" + "(" + thisElement + ")";
							}	
						} else {
							if(exponent != 1) {
								conversionString = conversionString + " " + "(" + thisElement + ")" + "^" + exponent;
							} else {							
								conversionString = conversionString + " " + "(" + thisElement + ")";
							}
						}
					}	
						
				}
			}
			for(int i=0; i < matrixElements.size(); i++) {
					
				thisElement = (String) matrixElements.elementAt(i);
				quantityTmp = (Quantity) quantityTable.get((String) matrixElements.elementAt(i));
				if(quantityTmp instanceof Variable) { 
					
					long exponent = Math.round(P.get(i + nonMatrixElements.size(),0));
					
					if(exponent != 0) {
						if(exponent < 0) {
							exponent = exponent * (-1);
							if(exponent != 1) {
								conversionString = conversionString + "/" + "(" + thisElement + ")" + "^" + exponent;
							} else {							
								conversionString = conversionString + "/" + "(" + thisElement + ")";
							}	
						} else {
							if(exponent != 1) {
								conversionString = conversionString + " " + "(" + thisElement + ")" + "^" + exponent;
							} else {							
								conversionString = conversionString + " " + "(" + thisElement + ")";
							}	
						}
					}	
						
				}
			}
			
		}
		
		return conversionString;		
	}	

	
}	
