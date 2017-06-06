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
package esavo.vospec.resourcepanel;


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import org.jdesktop.swingx.JXTable;


public class TransferableTable implements Transferable {


    public static DataFlavor TABLE_FLAVOR = new DataFlavor(JXTable.class,
      "JXTable");

    DataFlavor flavors[] = { TABLE_FLAVOR };

    private JXTable table;

    TransferableTable(JXTable table){
        this.table=table;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (flavor.getRepresentationClass() == JXTable.class);
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor)) {
            System.out.println(table.getRowCount());
            return (Object) table;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

//  public static DataFlavor TABLE_FLAVOR = new DataFlavor(JTable.class,
//      "Table");
//
//  DataFlavor flavors[] = { TABLE_FLAVOR };
//
//  JTable table;
//
//  public TransferableTable(JTable table) {
//    this.table = table;
//  }
//
//  public synchronized DataFlavor[] getTransferDataFlavors() {
//    return flavors;
//  }
//
//  public boolean isDataFlavorSupported(DataFlavor flavor) {
//    return (flavor.getRepresentationClass() == JTable.class);
//  }
//
//  public synchronized Object getTransferData(DataFlavor flavor)
//      throws UnsupportedFlavorException, IOException {
//    if (isDataFlavorSupported(flavor)) {
//        System.out.println(table.getName());
//      return (Object) table;
//    } else {
//      throw new UnsupportedFlavorException(flavor);
//    }
//  }
}

