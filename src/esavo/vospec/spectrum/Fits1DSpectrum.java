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


import java.net.*;
import java.util.*;
import nom.tam.fits.*;



/**
 *
 * @author jgonzale
 */
public class Fits1DSpectrum extends FitsSpectrum {



    public Fits1DSpectrum() {
        super();
        file = null;
        setToWait(true);
    }

    public Fits1DSpectrum(Spectrum spectrum) {
        super(spectrum);
        file = null;
        setToWait(true);
    }


    /*******************************************************
    /**
     * NAME: getColumnsNameAndUnits()
     *
     * PURPOSE:
     *
     * Implement a Vector with all the fits columns name and Units
     *
     * INPUT PARAMETERS: None
     *
     * OUTPUT PARAMETERS: None.
     *
     * RETURN VALUE: Vector columnsName
     *
     */
    public static Vector getColumnsNameAndUnits(String urlString) throws Exception {


        URL fitsUrl = new URL(urlString);
        Fits fitsFile = new Fits(fitsUrl, false);
        BasicHDU[] hdus = fitsFile.read();

        Vector columnsName = new Vector();

        //process the primary HDU containing "image"
        //to see if there is a 1D spectrum

        Header head = hdus[0].getHeader();

        String waveUnits = "";
        String fluxUnits = "";

        if(head.getStringValue("CTYPE1")!=null)waveUnits=head.getStringValue("CTYPE1");
        if(head.getStringValue("CUNIT1")!=null)waveUnits=head.getStringValue("CUNIT1");

        if(head.getStringValue("BUNIT")!=null)fluxUnits=head.getStringValue("BUNIT");

        if(head.getIntValue("NAXIS")==1){
            String [] wave = {"WAVE-1D",waveUnits};
            columnsName.addElement(wave);
            String [] flux = {"FLUX-1D",fluxUnits};
            columnsName.addElement(flux);
        }


        return (columnsName);
    }


    /*******************************************************
    /**
     * NAME: getColumnValues()
     *
     * PURPOSE:
     *
     * Returns the column values from a Fits File.
     *
     * INPUT PARAMETERS: name of the column
     *
     * OUTPUT PARAMETERS: None.
     *
     * RETURN VALUE: double
     *
     *
     *
     */
    @Override
    public double[] getColumnValues(String columnName) {

        double[] columnValue = null;

        setFitsFile(this.localFile);

        if (columnName.equalsIgnoreCase("FLUX-1D")) {

            try {

                Header head = hdus[0].getHeader();

                float bScale = 1;
                float bZero = 0;

                bScale = head.getFloatValue("BSCALE");
                bZero = head.getFloatValue("BZERO");

                if (bScale == 0) {
                    bScale = 1;
                }

                ImageHDU image = (ImageHDU) file.getHDU(0);


                //We assume we have a unidimensional array of numeric values,
                //these are the possible ones:


                //Doubles copied directly
                if(image.getKernel().toString().startsWith("[D")){
                    double[] matrix = (double[]) image.getKernel();
                    columnValue = matrix;
                    for(int i=0;i<columnValue.length;i++){
                        columnValue[i]=columnValue[i]*bScale+bZero;
                    }

                //Floats parsed to double
                }else if(image.getKernel().toString().startsWith("[F")){
                    float[] matrix = (float[]) image.getKernel();
                    columnValue = new double[matrix.length];
                    for(int i=0;i<matrix.length;i++){
                        columnValue[i]=(new Double(matrix[i])).doubleValue();
                        columnValue[i]=columnValue[i]*bScale+bZero;
                    }

                //Integers parsed to double
                }else if(image.getKernel().toString().startsWith("[I")){
                    int[] matrix = (int[]) image.getKernel();
                    columnValue = new double[matrix.length];
                    for(int i=0;i<matrix.length;i++){
                        columnValue[i]=(new Double(matrix[i])).doubleValue();
                        columnValue[i]=columnValue[i]*bScale+bZero;
                    }

                //Shorts parsed to double
                }else if(image.getKernel().toString().startsWith("[S")){
                    short[] matrix = (short[]) image.getKernel();
                    columnValue = new double[matrix.length];
                    for(int i=0;i<matrix.length;i++){
                        columnValue[i]=(new Double(matrix[i])).doubleValue();
                        columnValue[i]=columnValue[i]*bScale+bZero;
                    }
                }



                //System.out.println("flux "+columnValue[0]+" "+columnValue[1]);
            } catch (Exception e2) {
                System.out.println("Error in process for " + this.getTitle() + "\n" + e2);
                e2.printStackTrace();
            }

        } else {

            try{

                Header head = hdus[0].getHeader();

                float refPix = head.getFloatValue("CRPIX1");
                float refVal = head.getFloatValue("CRVAL1");
                float delta = head.getFloatValue("CDELT1");

                int nPoints = head.getIntValue("NAXIS1");

                columnValue = new double[nPoints];

                double startValue = refVal - refPix * delta;
                columnValue[0] = startValue;

                for (int i = 1; i < nPoints; i++) {
                    columnValue[i] = columnValue[i - 1] + delta;
                }

                for (int i = 1; i < nPoints; i++) {
                    if(columnValue[i]==0)columnValue[i]=i+1;
                }

                //System.out.println("wave "+columnValue[0]+" "+columnValue[1]);

            } catch (Exception e2) {
                System.out.println("Error in process for " + this.getTitle() + "\n" + e2);
                e2.printStackTrace();
            }





        }

        return columnValue;

    }


}
