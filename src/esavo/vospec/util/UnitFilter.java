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

/**
 *
 * @author jsalgado
 */
public class UnitFilter {


   public static String filterUnit(String unit) {

        String newUnit = "";

        boolean startedEquation = false;
        boolean insideExponent = false;
        boolean wasLetter = false;

        boolean isE = false;

        for (int j = 0; j < unit.length(); j++) {

            char character = unit.charAt(j);

            if (startedEquation && (Character.isDigit(character) || character == '-')) {

                if (insideExponent) {
                    newUnit = newUnit + character;
                } else {
                    //The following line check if the letter wasn't the exponent e (o E), so that it's not going to be followed by the caret.
                    if (unit.charAt(j - 1) == 'e' || unit.charAt(j - 1) == 'E') {
                        //Also it checks that the characther previous to the "e" is a number, in this case the caret should not be added
                        if (j > 1 && Character.isDigit(unit.charAt(j - 2))) {
                            isE = true;
                        } else {
                            isE = false;
                        }
                    }
                    if (wasLetter && !isE) {
                        newUnit = newUnit + "^(" + character;
                        insideExponent = true;
                    } else {
                        newUnit = newUnit + character;
                        isE = false;
                    }
                }
            } else {
                if (insideExponent) {
                    newUnit = newUnit + ")";
                    insideExponent = false;
                }
                newUnit = newUnit + character;

                if (Character.isLetter(character)) {
                    startedEquation = true;
                }
            }

            if (Character.isLetter(character)) {
                wasLetter = true;
            } else {
                wasLetter = false;
            }

        }

        if (insideExponent) {
            newUnit = newUnit + ")";
        }

        //System.out.println(newUnit);

        return newUnit;
    }


    public static void main(String args[]) {


        try {
            if (args.length < 1) {
                System.out.println("Please insert the unit");
            }else{

                filterUnit(args[ 0 ]);
            }

        }catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }
}
