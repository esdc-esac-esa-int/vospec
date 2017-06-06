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
 * Defines a general quantity entity In contains a unit set.
 *
 * @author J. Salgado (ESAC/ESA)
 *
 * @version 1.0
 *
 **/

public class Quantity {

	/**
	*	The unit set object.
	**/
	private Unit		unit;
	
	/**
	* Void constructor.
	*
	* @see #Quantity(Unit)
	*/
	private Quantity() {
		unit = new Unit();	
	}

	/**
	* 	Contructs a quantity object, settting the unit object..
	*
	* @param unit The unit object containing the scaling and the dimensional equation
	*/
	public Quantity(Unit unit) {
		this();
		this.unit = unit;
	}

	/**
	* 	Obtains the dimensional equation Hashtable representation of the unit object for this quantity.
	*
	*	@return A hashtable containing the dimensional equation
	*
	*	@see Unit#getDimeqHashtable()
	*
	*/
	public Hashtable getDimeqHashtable() {
		return this.unit.getDimeqHashtable();
	}
		
	/**
	* 	Obtains the scaling factor of the unit object for this quantity.
	*
	*	@return The scaling factor
	*
	*	@see Unit#getScaling()
	*
	*/
	public double getScaling() {
		return this.unit.getScaling();
	}
	
	/**
	* 	Obtains the value for a quantity in the original units. In a general case, same as getScaling. In other cases it is not so
	* 	simple.
	*
	*	@return The scaling factor
	*
	*	@see Unit#getScaling()
	*	@see Variable#getValue()
	*
	*/
	public double getValue() {
		return this.unit.getScaling();
	}	

}
