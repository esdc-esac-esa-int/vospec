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

import java.awt.Color;

public class PlotDataSet {

	private int 	plotDataSet;
	private Color 	drawColor;	
	private boolean realData;
	private boolean isTsap;

	public PlotDataSet() {
		drawColor 	= Color.white;
		plotDataSet 	= 0;
	}
	
	public PlotDataSet(int plotDataSet, Color drawColor, boolean realData, boolean isTsap) {
		this();
		setPlotDataSet(plotDataSet);
		setDrawColor(drawColor);
		setRealData(realData);
                setTsap(isTsap);
	}
	
	
	public void setPlotDataSet(int plotDataSet) {
		this.plotDataSet = plotDataSet;
	}
	public void setDrawColor(Color drawColor) {
		this.drawColor = drawColor;
	}
	public void setRealData(boolean realData) {
		this.realData = realData;
	}
	public void setTsap(boolean isTsap) {
		this.isTsap = isTsap;
	}
	
	
	public int  getPlotDataSet() {
		return this.plotDataSet;
	}
	public Color getDrawColor() {
		return this.drawColor;
	}
	public boolean getRealData() {
		return this.realData;
	}
	public boolean getTsap() {
		return this.isTsap;
	}
	
}
