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

import Jama.Matrix;
import java.util.*;

/**
 *
 * Defines a quantity of type variable. In contains a initialValue to be converted, a initial unit set and a
 * final unit set. 
 *
 *
 * @author J. Salgado (ESAC/ESA)
 *
 * @version 1.0
 *
 **/
 

public class Variable extends Quantity {

	/**
   	* The value of this variable in this units.
	**/
	private double		initialValue;
		
	/**
   	* The final set of units for this variable.
	**/
	private Unit		finalUnit;

	/**
   	* The vector conversion. Set by the GeneralConverter algorithm.
	**/
	private Matrix		P;
		
		
	/**
   	* Constructs a Variable, setting the original and final units.
	*
	* @param originalUnit Original units, set in a unit object
	* @param finalUnit Final units, set in a unit object
	**/ 		
	public Variable(Unit originalUnit,Unit finalUnit) {
		
		super(originalUnit);
		this.finalUnit 	= finalUnit;
	}


	/**
   	* Obtain the dimensional hashtable for the final units
	*
	* @return Hashtable containing the dimensional equation exponents
	**/
	public Hashtable getFinalDimeqHashtable() {
		return this.finalUnit.getDimeqHashtable();
	}
		
	/**
   	* Obtain the scaling for the final units
	*
	* @return The scaling factor for the final unit set.
	**/
	public double getFinalScaling() {
		return this.finalUnit.getScaling();
	}	
	
	
	/**
   	* Set the value of this variable in the original units
	*
	* @param initialValue The initial value for this variable in the original units
	**/	
	public void setInitialValue(double initialValue) {
		this.initialValue = initialValue;
	}

	/**
   	* Set the conversion vector
	*
	* @param P The conversion vector calculated in the solve method of GeneralConverter
	**/	
	public void setPMatrix(Matrix P) {
		this.P = P;
	} 	
	
	/**
   	* Get the conversion vector
	*
	* @return The conversion vector calculated in the solve method of GeneralConverter
	**/	
	public Matrix getPMatrix() {
		return this.P;
	} 	

	/**
   	* Get the value in the original units. For a variable the value is the scaling factor of the original units mutiplied
	* per the the initial value
	*
	* @return The conversion vector calculated in the solve method of GeneralConverter
	**/	
	public double getValue() {
	
		double realValue = getScaling() * initialValue;
		return realValue;
	}	

}
