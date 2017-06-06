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
package esavo.vospec.dataingestion;

import java.io.*;
import java.util.*;

/**
 *
 * @author ibarbarisi
 * @version %version: 3 %
 */
public class TsaServer extends SsaServer implements Serializable {

    public String param;
    public String desc;
    public Vector values;
    public String selectedValue;
    public boolean selected = false;
    public int row;

    public TsaServer() {
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getParam() {
        return param;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setValues(Vector values) {
        this.values = values;
    }

    public Vector getValues() {
        return values;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getRow() {
        return row;
    }

    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
    }

    public String getSelectedValue() {
        return selectedValue;
    }
}
