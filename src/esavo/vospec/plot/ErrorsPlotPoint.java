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
package esavo.vospec.plot;

import java.io.Serializable;

/**
 *
 * @author jgonzale
 */
public class ErrorsPlotPoint implements Serializable {
    ///////////////////////////////////////////////////////////////////
    ////                         public variables                  ////

    /** True if this point is connected to the previous point by a line. */
    public boolean connected = false;

    /** True if the yLowEB and yHighEB fields are valid. */
    public boolean yErrorBar = false;

    /** True if the xLowEB and xHighEB fields are valid. */
    public boolean xErrorBar = false;

    /** Original value of x before wrapping. */
    public double originalx;

    /** X value after wrapping (if any). */
    public double x;

    /** Y value. */
    public double y;

    /** Error bar Y low value. */
    public double yLowEB;

    /** Error bar Y low value. */
    public double yHighEB;

    /** Error bar X low value. */
    public double xLowEB;

    /** Error bar X low value. */
    public double xHighEB;



}
