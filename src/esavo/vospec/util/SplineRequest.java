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
 * @author ibarbarisi
 * @version %version: 2 %
 */
public class SplineRequest {

    public double w1;
    public double w2;
    public double pressedW;
    public String target;
    public String ra;
    public String dec;
    public int wMinOnImage;
    public int wMaxOnImage;
    public int pressedWOnImage;

    public SplineRequest() {
    }

    public void setW1(double wi) {
        w1 = wi;
        pressedW = wi;
    }

    public void setW2(double we) {

        w2 = we;
        w1 = pressedW;

        if (w2 < pressedW) {
            w2 = pressedW;
            w1 = we;
        }
    }

    public void setRA(String thisRa) {
        ra = thisRa;
    }

    public void setDEC(String thisDec) {
        dec = thisDec;
    }

    public void setTarget(String thisTarget) {
        target = thisTarget;
    }

    public void setWMinOnImage(int wOnImage) {
        wMinOnImage = wOnImage;
        pressedWOnImage = wOnImage;
    }

    public void setWMaxOnImage(int wOnImage) {

        wMaxOnImage = wOnImage;
        wMinOnImage = pressedWOnImage;

        if (wMaxOnImage < pressedWOnImage) {
            wMaxOnImage = pressedWOnImage;
            wMinOnImage = wOnImage;
        }
    }

    public double getW1() {
        return w1;
    }

    public double getW2() {
        return w2;
    }

    public String getTarget() {
        return target;
    }

    public String getRA() {
        return ra;
    }

    public String getDEC() {
        return dec;
    }
} // end class
