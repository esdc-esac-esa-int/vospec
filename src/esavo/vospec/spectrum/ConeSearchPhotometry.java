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

import esavo.vospec.photometry.PhotometryFilter;
import esavo.vospec.photometry.PhotometryPoint;
import esavo.vospec.util.Cache;
import esavo.vospec.util.Utils;
import java.io.Serializable;
import java.util.Vector;

/**
 *
 * @author jgonzale
 */
public class ConeSearchPhotometry extends Spectrum implements Serializable, Runnable {

    protected Vector<PhotometryPoint> photometryPoints = new Vector();


    private boolean populated = false;

    public ConeSearchPhotometry() {
        super();
        setToWait(true);
    }

    public ConeSearchPhotometry(Spectrum spectrum) {
        super(spectrum);
        setToWait(true);

    }


    public void setPoints(Vector<PhotometryPoint> points){

        this.photometryPoints = points;

    }



    private double convert(double photMag, double zeroPoint) {

        return zeroPoint * Math.pow(10., (-1.) * photMag / 2.5);

    }


    public void calculateData() {


        setToWait(!Cache.alreadyLoaded(url));


        if(!populated){

            convertPoints();

            populated=true;

        }

        setToWait(false);

    }





    private void convertPoints() {

        this.waveValues = new double[photometryPoints.size()];
        this.waveErrorLower = new double[photometryPoints.size()];
        this.waveErrorUpper = new double[photometryPoints.size()];
        this.fluxValues = new double[photometryPoints.size()];


        for (int i = 0; i < photometryPoints.size(); i++) {

            //PhotometryFilter filter = PFSParser.getFullPhotometryFilter(photometryPoints.get(i).getID());

            //photometryPoints.get(i).setFilter(filter);

            PhotometryFilter filter = photometryPoints.get(i).getFilter();

            Number zeroPoint = filter.getZeroPoint().getValue();


            //System.out.println("zeropoint= "+zeroPoint.doubleValue());
            //System.out.println("photometrypoint= "+photometryPoints.get(i).getMagnitude().getValue().doubleValue());
            //waveValues[i] = photometryPoints.get(i).getWavelength().getValue().doubleValue();

            //TRY TO TAKE WAVELENGTH FIRST FROM THE POINT ITSELF

            double waveMean = 0;

            if(photometryPoints.get(i).getWavelength()!=null){
                waveMean = photometryPoints.get(i).getWavelength().getValue().doubleValue();
            }else{
                waveMean = filter.getWavelengthMean().getValue().doubleValue();
            }
            
            waveValues[i] = waveMean;



            if(filter.getWavelengthMean().getValue()!=null && filter.getWavelengthMin().getValue()!=null){

                double waveMax = filter.getWavelengthMax().getValue().doubleValue();
                double waveMin = filter.getWavelengthMin().getValue().doubleValue();

                waveErrorLower[i] = waveMean - waveMin;
                waveErrorUpper[i] = waveMax - waveMean;

                this.waveErrorsPresent = true;

            }else if(filter.getWidthEff()!=null && filter.getWidthEff().getValue()!=null){
                
                double width = filter.getWidthEff().getValue().doubleValue();
                
                waveErrorLower[i] = width/2;
                waveErrorUpper[i] = width/2;
                
                this.waveErrorsPresent = true;

            }else{
                this.waveErrorsPresent = false;
            }

            
            


            if (photometryPoints.get(i).getFlux()!=null){
                fluxValues[i] = photometryPoints.get(i).getFlux().getValue().doubleValue();
            }else{
                fluxValues[i] = convert(photometryPoints.get(i).getMagnitude().getValue().doubleValue(), zeroPoint.doubleValue());
            }

//            // transform points from the original Units to predefined Units
////            double[] values = (new SpectrumConverter()).convertPoint(waveValues[i], fluxValues[i], photUnit, mksUnits);
////            waveValues[i] = values[0];
////            fluxValues[i] = values[1];

            String[] units;

            if (photometryPoints.get(i).getFlux()!=null){
                units = Utils.getDimensionalEquation(photometryPoints.get(i).getFlux().getUnit());
            }else{
                units = Utils.getDimensionalEquation(photometryPoints.get(i).getFilter().getZeroPoint().getUnit());
            }

            setFluxFactor(units[0]);
            setDimeQ(units[1]);

            if(photometryPoints.get(i).getWavelength()!=null){
                units = Utils.getDimensionalEquation(photometryPoints.get(i).getWavelength().getUnit());
            }else{
                units = Utils.getDimensionalEquation(photometryPoints.get(i).getFilter().getWavelengthMean().getUnit());
            }
            
            setWaveFactor(units[0]);
            this.setDimeQWave(units[1]);

        }

          //FIXME: currently all the units are taken from the first phot. point
//        String[] units = Utils.getDimensionalEquation(photometryPoints.get(0).getFilter().getZeroPoint().getUnit());
//        setFluxFactor(units[0]);
//        setDimeQ(units[1]);


    }





}
