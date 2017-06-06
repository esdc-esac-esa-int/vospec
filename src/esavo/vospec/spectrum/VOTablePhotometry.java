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

import esavo.vospec.dataingestion.PFSParser;
import esavo.vospec.dataingestion.VOTable;
import esavo.vospec.dataingestion.VOTableEntry;
import esavo.vospec.photometry.PhotometryFilter;
import esavo.vospec.photometry.PhotometryPoint;
import esavo.vospec.photometry.PhysicalQuantity;
import esavo.vospec.util.Cache;
import esavo.vospec.util.Utils;
import java.io.File;
import java.io.Serializable;
import java.util.Vector;


/**
 *
 * @author jgonzale
 */


public class VOTablePhotometry extends Spectrum implements Serializable, Runnable {

    protected Vector<PhotometryPoint> photometryPoints = new Vector();
    private String fileName;
    private File localFile;

    private boolean populated = false;

    public VOTablePhotometry() {
        super();
        setToWait(true);
    }

    public VOTablePhotometry(Spectrum spectrum) {
        super(spectrum);
        setToWait(true);

    }

    /*********************************************************************
    /**
     * NAME: convert(double enterFlux)
     *
     * PURPOSE:
     *
     * Return the converted flux value
     *
     * INPUT PARAMETERS:  value non converted
     *
     * OUTPUT PARAMETERS: None.
     *
     * RETURN VALUE:  none
     *
     *
     */
    private double convert(double photMag, double zeroPoint) {

        return zeroPoint * Math.pow(10., (-1.) * photMag / 2.5);

    }

    /*********************************************************************
    /**
     * NAME: calculateData()
     *
     * PURPOSE:
     *
     * Operations to be done for this class of Spectrum. In this case, we just
     * need to copy the file. This method overrides the Spectrum mother method
     *
     *
     * INPUT PARAMETERS:  none
     *
     *
     *
     * OUTPUT PARAMETERS: None.
     *
     * RETURN VALUE:  none
     *
     *
     */
    public void calculateData() {


        //return the name of the spectrum associated with url

        //System.out.println(url);


        setToWait(!Cache.alreadyLoaded(url));

        this.localFile = Cache.getFile(url);


        //setFluxFactor("1E-26");
        //setDimeQ("MT-2");

        //setToBeNormalized(false);

        if(!populated){

            parsePoints();

            //String[] units = Utils.getDimensionalEquation(photometryPoints.get(0).getMagnitude().getUnit());
            //setFluxFactor(units[0]);
            //setDimeQ(units[1]);

            convertPoints();

            populated=true;

        }

        setToWait(false);

    }




    private void parsePoints(){

        VOTable table = null;

        Vector<VOTableEntry> entries = new Vector();

        try {

            table = new VOTable("file:"+localFile.getAbsolutePath(), 10000);
            entries = table.getEntries();

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        for(int i=0 ; i<entries.size(); i++){

            PhotometryPoint point = new PhotometryPoint();

            for(int j=0 ; j<entries.get(i).components.size() ; j++){

                String utype = entries.get(i).components.get(j).getUtype();
                String value = entries.get(i).components.get(j).getValue();
                String unit = entries.get(i).components.get(j).getUnit();

                if (utype.toUpperCase().contains("PHOTOMETRYFILTER.UNIQUEIDENTIFIER")) {
                    point.setID(value);
                }
                if (utype.toUpperCase().contains("SPECTRALAXIS.VALUE")          ||
                    utype.toUpperCase().contains("PHOTOMETRYFILTER.MEANFREQUENCY")) {
                    PhysicalQuantity wavelength = new PhysicalQuantity();
                    wavelength.setValue(new Double(value));
                    wavelength.setUnit(unit);
                    point.setWavelength(wavelength);
                }
                if (utype.toUpperCase().contains("PHOTOMETRYPOINT.VALUE")   ||
                    utype.toUpperCase().contains("FLUXAXIS.VALUE")) {
                    
                    PhysicalQuantity magnitude = new PhysicalQuantity();
                    
                    magnitude.setValue(new Double(value));
                    magnitude.setUnit(unit);
                    point.setMagnitude(magnitude);
                }

            }

            photometryPoints.add(point);


        }


    }



    private void convertPoints() {

        this.waveValues = new double[photometryPoints.size()];
        this.fluxValues = new double[photometryPoints.size()];


        for (int i = 0; i < photometryPoints.size(); i++) {

            PhotometryFilter filter = PFSParser.getFullPhotometryFilter(photometryPoints.get(i).getID());

            photometryPoints.get(i).setFilter(filter);

            Number zeroPoint = filter.getZeroPoint().getValue();


            //System.out.println("zeropoint= "+zeroPoint.doubleValue());
            //System.out.println("photometrypoint= "+photometryPoints.get(i).getMagnitude().getValue().doubleValue());
            //waveValues[i] = photometryPoints.get(i).getWavelength().getValue().doubleValue();
            waveValues[i] = filter.getWavelengthMean().getValue().doubleValue();
            fluxValues[i] = convert(photometryPoints.get(i).getMagnitude().getValue().doubleValue(), zeroPoint.doubleValue());

//            // transform points from the original Units to predefined Units
////            double[] values = (new SpectrumConverter()).convertPoint(waveValues[i], fluxValues[i], photUnit, mksUnits);
////            waveValues[i] = values[0];
////            fluxValues[i] = values[1];
            String[] units = Utils.getDimensionalEquation(photometryPoints.get(i).getFilter().getZeroPoint().getUnit());

            setFluxFactor(units[0]);
            setDimeQ(units[1]);

            units = Utils.getDimensionalEquation(photometryPoints.get(i).getFilter().getWavelengthMean().getUnit());

            setWaveFactor(units[0]);
            this.setDimeQWave(units[1]);

        }

          //FIXME: currently all the units are taken from the first phot. point
//        String[] units = Utils.getDimensionalEquation(photometryPoints.get(0).getFilter().getZeroPoint().getUnit());
//        setFluxFactor(units[0]);
//        setDimeQ(units[1]);


    }


 
   
//    private void convertUnits() {
//
//
//        Unit mksUnits = new Unit("L", "1.", "ML-1T-3", "1.");
//
//        double waveScalingMKS = ((Double) mksUnits.getWaveVector().elementAt(0)).doubleValue();
//        String waveDimeqMKS = (String) mksUnits.getWaveVector().elementAt(1);
//
//        double fluxScalingMKS = ((Double) mksUnits.getFluxVector().elementAt(0)).doubleValue();
//        String fluxDimeqMKS = (String) mksUnits.getFluxVector().elementAt(1);
//
//
//        for (int i = 0; i < photometryPoints.size(); i++) {
//
//            String[] originalUnits = Utils.getDimensionalEquation(photometryPoints.get(i).getFilter().getZeroPoint().getUnit());
//
//
//            SpectrumConverter converter = new SpectrumConverter(
//                    this.waveF, this.dimQWave,Double.valueOf(originalUnits[0]),
//                    originalUnits[1],waveScalin SgMKS, waveDimeqMKS,fluxScalingMKS, fluxDimeqMKS);
//
//        }
//
//
//
//
//    }





//    private void convertPhotoToFlux() {
//
//        this.waveValues = new double[photometryPoints.size()];
//        this.fluxValues = new double[photometryPoints.size()];
//
//        Unit mksUnits = new Unit("L", "1.", "ML-1T-3", "1.");
//
//        double waveScalingMKS = ((Double) mksUnits.getWaveVector().elementAt(0)).doubleValue();
//        String waveDimeqMKS = (String) mksUnits.getWaveVector().elementAt(1);
//
//        double fluxScalingMKS = ((Double) mksUnits.getFluxVector().elementAt(0)).doubleValue();
//        String fluxDimeqMKS = (String) mksUnits.getFluxVector().elementAt(1);
//
//
//
//        for (int i = 0; i < photometryPoints.size(); i++) {
//
//            PhotometryFilter filter = PFSParser.getPhotometryFilter(photometryPoints.get(i).getID());
//
//            photometryPoints.get(i).setFilter(filter);
//
//            Number zeroPoint = filter.getZeroPoint().getValue();
//
//            waveValues[i] = photometryPoints.get(i).getWavelength().getValue().doubleValue();
//            fluxValues[i] = convert(photometryPoints.get(i).getMagnitude().getValue().doubleValue(), zeroPoint.doubleValue());
//
//        }
//
//
//
//        for (int i = 0; i < photometryPoints.size(); i++) {
//
//            String[] originalUnits = Utils.getDimensionalEquation(photometryPoints.get(i).getFilter().getZeroPoint().getUnit());
//
//            SpectrumConverter converter = new esavo.utils.units.dimeq.SpectrumConverter(
//                    this.waveF, this.dimQWave,
//                    originalUnits[0], originalUnits[1],
//                    waveScalingMKS, waveDimeqMKS,
//                    fluxScalingMKS, fluxDimeqMKS);
//
//        }
//
//        setUnits(mksUnits);
//
//    //String[] units = Utils.getDimensionalEquation(photometryPoints.get(0).getFilter().getZeroPoint().getUnit());
//    //setFluxFactor(units[0]);
//    //setDimeQ(units[1]);
//
//
//    }



     
}
