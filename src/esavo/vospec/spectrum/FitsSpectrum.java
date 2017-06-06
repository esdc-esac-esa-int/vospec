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
package esavo.vospec.spectrum;

import esavo.vospec.util.*;
import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.*;
import nom.tam.fits.*;



public class FitsSpectrum extends Spectrum{



    public String fileName;

    public transient Fits file;

    public transient BasicHDU[] hdus;
    public transient Header hdr;

    public File localFile = null;

    
    public FitsSpectrum() {
        super();
        file = null;
        setToWait(true);
    }

    public FitsSpectrum(Spectrum spectrum) {
        super(spectrum);
        file = null;
        setToWait(true);
    }


    /**
     * Set the Fits File from a File object.
     *
     * @param File f.
     *
     *
     */
    protected void setFitsFile(File f) {

        if (file != null) {
            return;
        }
        try {
            this.file = new Fits(f.getAbsolutePath());
            this.hdus = file.read();
        } catch (Exception e) {
            System.out.println("Error: Problem reading Fits file " + e);
        }
    }



    /**
     * Returns the type of the Extension for a Fits File.
     */


    protected String chooseTypeExtension(BasicHDU hdu) {
        String type = "";

        if ((hdu.getHeader().getStringValue("XTENSION")).equals("TABLE")) {
            type = "ASCII";
        }
        if ((hdu.getHeader().getStringValue("XTENSION")).equals("BINTABLE")) {
            type = "BINTABLE";
        }
        if ((hdu.getHeader().getStringValue("XTENSION")).equals("IMAGE")) {
            type = "IMAGE";
        }
        return type;
    }



    /**
     * Returns the column values from a Fits File.
     */
    protected double[] getColumnValues(String columnName) {

        double[] columnValue = null;

        //setFitsFile(this.localFile);

        if(hdus==null)return null;

        try {

            for (int thisHDU = 1; thisHDU < hdus.length; thisHDU++) {

                BasicHDU athBasic = (BasicHDU) hdus[thisHDU];
                String ext = chooseTypeExtension(athBasic);

                if (ext.equals("BINTABLE")) {

                    BinaryTableHDU ath = (BinaryTableHDU) athBasic;
                    int column = ath.findColumn(columnName);

                    if (column >= 0) {

                        int nRows = ath.getNRows();
                        columnValue = getColumnDataWithTry(ath, nRows, column);
                    }
                }

                if (ext.equals("ASCII")) {

                    AsciiTableHDU asc = (AsciiTableHDU) athBasic;
                    int column = asc.findColumn(columnName);


                    if (column >= 0) {
                        int nRows = asc.getNRows();
                        columnValue = getColumnDataWithTry(asc, nRows, column);
                    }
                }

                if (ext.equals("IMAGE")) {
                    System.out.println("FITS image format not supported");
                    continue;
//                                    System.out.println("Image extension");
//                                    TableHDU th = (TableHDU) athBasic;
//                                    int column = th.findColumn(columnName);
//                                    
//                                    if(column>=0) {
//                                        
//                                        int nRows = th.getNRows();
//                                        columnValue = getColumnDataWithTry(th,nRows,column);
//						
//                                    }
                }

            }

        } catch (Exception e2) {
            System.out.println("Error in process for " + this.getTitle() + "\n" + e2);
        e2.printStackTrace();
        }

        return columnValue;

    }


    /**
     * Call the getColumnData trying to calculate again the double array in case of
     * memory problems
     *
     */
    protected double[] getColumnDataWithTry(TableHDU asc, int nRows, int column) throws Exception {
        double[] columnValue = null;

        try {

            columnValue = getColumnData(asc, nRows, column);

        } catch (OutOfMemoryError e) {

            java.lang.System.gc();

            try {

                columnValue = getColumnData(asc, nRows, column);

            } catch (OutOfMemoryError ex) {

                System.out.println("Out of Memory Error");

            } // second Try
        }
        return columnValue;
    }


    /**
     * Returns a double array containing the column data. A vector could be included inside a
     * single row (in some weird cases)
     *
     */
    protected double[] getColumnData(TableHDU ath, int nRows, int column) throws OutOfMemoryError, FitsException {

        Object val;

        double[] columnValue;
        double element;

        if (nRows == 1) {

            val = ath.getElement(0, column);
            columnValue = getDoubleArrayFromObject(val);

        } else {

            columnValue = new double[nRows];
            for (int i = 0; i < nRows; i++) {
                if(i >= ath.getNRows() || column>= ath.getNCols()) throw new FitsException("Row out of bounds");
                val = ath.getElement(i, column);
                element = getDoubleElement(val);
                columnValue[i] = (double) element;
            }

        }
        return columnValue;
    }



    /**
     * Returns the double value for this column element
     *
     */
    protected double getDoubleElement(Object val) throws OutOfMemoryError, FitsException {

        int arrayLength = Array.getLength(val);
        double elementValue = 0;

        if (val.toString().startsWith("[F")) {
            float fl = Array.getFloat(val, 0);
            elementValue = (double) fl;
        }
        if (val.toString().startsWith("[D")) {
            double fl = Array.getDouble(val, 0);
            elementValue = (double) fl;
        }
        if (val.toString().startsWith("[I")) {
            int in = Array.getInt(val, 0);
            elementValue = (double) in;
        }
        if (val.toString().startsWith("[S")) {
            int in = Array.getShort(val, 0);
            elementValue = (double) in;
        }

        return elementValue;
    }


    /**
     * Returns the double Array in return of a Object reflect Array
     */
    protected double[] getDoubleArrayFromObject(Object val) throws OutOfMemoryError, FitsException {

        int arrayLength = Array.getLength(val);
        double[] elementType = new double[arrayLength];

        if (val.toString().startsWith("[F")) {

            for (int j = 0; j < arrayLength; j++) {
                float fl = Array.getFloat(val, j);
                elementType[j] = (double) fl;
            }
        }
        if (val.toString().startsWith("[D")) {
            for (int j = 0; j < arrayLength; j++) {
                double fl = Array.getDouble(val, j);
                elementType[j] = (double) fl;
            }
        }
        if (val.toString().startsWith("[I")) {
            for (int j = 0; j < arrayLength; j++) {
                int in = Array.getInt(val, j);
                elementType[j] = (double) in;
            }
        }
        if (val.toString().startsWith("[S")) {
            for (int j = 0; j < arrayLength; j++) {
                int in = Array.getShort(val, j);
                elementType[j] = (double) in;
            }
        }
        return elementType;
    }


    /**
     * Implement a Vector with all the fits columns name and Units
     *
     */
    public static Vector getColumnsNameAndUnits(String urlString) throws Exception {


        URL fitsUrl = new URL(urlString);
        Fits fitsFile = new Fits(fitsUrl, false);
        BasicHDU[] hdus = fitsFile.read();

        Vector columnsName = new Vector();
        int tFields;

        //Start from first extension
        for (int hdu = 1; hdu < hdus.length; hdu++) {

            Header hdr = hdus[hdu].getHeader();
            // Determine if the number of columns is not missing.
            if ((tFields = hdr.getIntValue("TFIELDS")) < 0) {
                System.out.println("Header keyword TFIELDS is missing.");
            }

            // Get Name column
            for (int tf = 0; tf < tFields + 1; tf++) {
                String[] column = new String[2];

                column[0] = getName(tf, hdr);
                column[1] = getUnit(tf, hdr);

                if (column[0] != null) {
                    if (column[1] == null) {
                        column[1] = "";
                    }
                    columnsName.addElement(column);
                }
            }

        }
        return (columnsName);
    }



    protected static String getName(int column, Header hd) {
        return hd.getStringValue("TTYPE" + column);
    }



    protected static String getUnit(int column, Header hd) {
        return hd.getStringValue("TUNIT" + column);
    }





    /**
     * Operations to be done for this class of Spectrum. In this case, we just
     * need to copy the FitsFile. This method overrides the Spectrum mother method
     *
     */

    public void calculateData() {

        setToWait(!Cache.alreadyLoaded(url));

        localFile = Cache.getFile(url);

        setFitsFile(localFile);

        this.setWaveValues(getColumnValues(waveLengthColumnName));
        this.setFluxValues(getColumnValues(fluxColumnName));
        if(this.waveErrorsPresent){
            this.setWaveErrorUpper(getColumnValues(this.getWaveErrorUpperColumnName()));
            this.setWaveErrorLower(getColumnValues(this.getWaveErrorLowerColumnName()));
        }
        if(this.fluxErrorsPresent){
            this.setFluxErrorUpper(getColumnValues(this.getFluxErrorUpperColumnName()));
            this.setFluxErrorLower(getColumnValues(this.getFluxErrorLowerColumnName()));
        }

        file = null;
        hdus = null;
        hdr = null;
        
        setToWait(false);
    }





}
