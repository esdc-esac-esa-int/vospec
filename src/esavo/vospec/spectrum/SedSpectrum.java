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

import cfa.vo.sed.dm.*;
import cfa.vo.sed.io.*;
import cfa.vo.sed.io.util.FileFormat;
import esavo.vospec.util.Cache;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Vector;

/**
 *
 * @author jgonzale
 */
public class SedSpectrum extends Spectrum implements Serializable, Runnable {

    public String fileName;
    public File localFile = null;

    public SedSpectrum(Spectrum spectrum) {
        super(spectrum);
        setToWait(true);
    }

    @Override
    public void calculateData() {


        try {

            //cache the remote file



            setToWait(!Cache.alreadyLoaded(url));

            this.localFile = Cache.getFile(url);


            //setToWait(false);


            //populate wave and flux values


            InputStream fis = null;
            try {
                fis = new FileInputStream(localFile);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            String urlSEDFile = "file:" + localFile.getAbsolutePath();

            //TODO continuar aqui: solo un segmento (espectro) por recurso (fichero)


            //System.out.println("urlSEDfile " + urlSEDFile);

            //if(spectrumSet==null){
            //  spectrumSet = new SpectrumSet();
            //}

            //Fits conversion to SED
            IWrapper wrapper = null;

            Unit finalUnits = null;
            String waveUnit = null;
            String fluxUnit = null;


            SED sed = null;

            IReader reader = new cfa.vo.sed.io.Reader();

            /*
            try {

            wrapper = reader.read(fis, FileFormat.FITS);
            ISEDDeserializer fitsDeserializer = new FitsDeserializer();
            sed = fitsDeserializer.convertToSED(wrapper);

            } catch (Exception e1) {
            try {

            wrapper = reader.read(fis, FileFormat.XML);
            ISEDDeserializer xmlDeserializer = new XMLDeserializer();
            sed = xmlDeserializer.convertToSED(wrapper);


            } catch (Exception e2) {
            try {
             */
            wrapper = reader.read(fis, FileFormat.VOTABLE);
            ISEDDeserializer votableDeserializer = new VOTableDeserializer();
            sed = votableDeserializer.convertToSED(wrapper);
            /*
            } catch (Exception e3) {

            //throw new Exception("SED could not be deserialized");

            }
            }
            }
             */




            //consider only one segment

            int nSegments = 0;

            nSegments = sed.getNSegments();

            //System.out.println(nSegments + " segments");

            /*
            for (int i = 0; i < nSegments; i++) {
             */

            int i = 0;

            SEDSegment sadSegment = null;


            sadSegment = sed.getSegment(i);


            waveValues = new double[sadSegment.getData().getNumDataPoints()];
            fluxValues = new double[sadSegment.getData().getNumDataPoints()];

            waveErrorUpper = new double[sadSegment.getData().getNumDataPoints()];
            waveErrorLower = new double[sadSegment.getData().getNumDataPoints()];
            fluxErrorUpper = new double[sadSegment.getData().getNumDataPoints()];
            fluxErrorLower = new double[sadSegment.getData().getNumDataPoints()];


            for (int j = 0; j < sadSegment.getData().getNumDataPoints(); j++) {
                try {
                    double wave = ((Double) sadSegment.getData().getSpectral().getValue().getDataValue(j)).doubleValue();
                    waveValues[j] = wave;
                } catch (SEDException ex) {
                    ex.printStackTrace();
                }

            }

            for (int j = 0; j < sadSegment.getData().getNumDataPoints(); j++) {
                try {
                    double flux = ((Double) sadSegment.getData().getFlux().getValue().getDataValue(j)).doubleValue();
                    fluxValues[j] = flux;
                } catch (SEDException ex) {
                    ex.printStackTrace();
                }
            }



            setStatisticalErrors(sadSegment);

            addSistematicErrors(sadSegment);

            addBins(sadSegment);



            //set UNITS using SED libraries


            //Try to create from the SI fields (if present)

            //TODO - not correctly implemented in SED library



            //Try to create from data.spectral.unit (optional)
            try {

                waveUnit = sadSegment.getData().getSpectral().getUnits();
                fluxUnit = sadSegment.getData().getFlux().getUnits();
            } catch (Throwable t) {
                //continue
                }


            //Try to create from char.spectral.unit (mandatory)

            if (finalUnits == null) {
                if (waveUnit == null || fluxUnit == null) {
                    waveUnit = sadSegment.getCharacterization().getSpectral().getUnit();
                    fluxUnit = sadSegment.getCharacterization().getFlux().getUnit();
                }
            }



            finalUnits = new Unit(waveUnit, fluxUnit);


            this.setUnits(finalUnits);


            //set Calibration

            //TODO not working because field not correctly implemented in the SED library

            //System.out.println("HERE "+sadSegment.getCharacterization().getFlux().getCalibration().get);
/*
            if(((String)sadSegment.getCharacterization().getFlux().getCalibration().getName()).toLowerCase().contains("uncalibrated") ||
                    ((String)sadSegment.getCharacterization().getFlux().getCalibration().getName()).toLowerCase().contains("calibrated")){
                this.setToBeNormalized(true);
            }
*/


            //set AXES using SED libraries


            String[] axes;
            String[] fluxAxes;
            axes = sadSegment.getSpectralSI().toString().split(" ");
            if (!axes[0].equals("") && !axes[1].equals("")) {
                this.setDimeQWave(axes[1]);
                this.setWaveFactor(axes[0]);
                System.out.println("spectrumSED.setWaveFactor(axes[0]); " + this.getWaveFactor());
            }
            fluxAxes = sadSegment.getFluxSI().toString().split(" ");
            if (!fluxAxes[0].equals("") && !fluxAxes[1].equals("")) {
                this.setDimeQ(fluxAxes[1]);
                this.setFluxFactor(fluxAxes[0]);
            }

            /*
            String[] dimEqWave = getDimensionalEquation(waveUnit);
            String dq1 = dimEqWave[0];
            String dq2 = dimEqWave[1];

            String[] dimEqFlux = getDimensionalEquation(fluxUnit);
            String dq1F = dimEqFlux[0];
            String dq2F = dimEqFlux[1];

            spectrumSED.setDimeQWave(dq1);
            spectrumSED.setWaveFactor(dq2);
            spectrumSED.setDimeQ(dq1F);
            spectrumSED.setFluxFactor(dq2F);

            SpectrumSet spectrumSetTmp = new SpectrumSet();
            spectrumSetTmp.addSpectrum(0, spectrumSED);
            spectrumSet.addSpectrumSet(spectrumSetTmp);
            spectrumSED.setRow(spectrumSet.getSpectrumSet().size() - 1);

            addSpectrum("SED " + i, spectrumSED, (javax.swing.JTextArea) null, checkNode);

             */

            fis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        setToWait(false);

    }

    private void setStatisticalErrors(SEDSegment sadSegment) {


        this.fluxErrorsPresent = false;

        //FLUX Statistical error - simmetrical error

        for (int j = 0; j < sadSegment.getData().getNumDataPoints(); j++) {
            try {
                double fluxError = ((Double) sadSegment.getData().getFlux().getAccuracy().getStatError().getDataValue(j)).doubleValue();
                fluxErrorUpper[j] = fluxError;
                fluxErrorLower[j] = fluxError;
                this.fluxErrorsPresent = true;
            } catch (Exception ex) {
            }
        }

        //FLUX statistical error - separate upper and lower values

        for (int j = 0; j < sadSegment.getData().getNumDataPoints(); j++) {
            try {
                double fluxErrorHigh = ((Double) sadSegment.getData().getFlux().getAccuracy().getStatErrHigh().getDataValue(j)).doubleValue();
                double fluxErrorLow = ((Double) sadSegment.getData().getFlux().getAccuracy().getStatErrLow().getDataValue(j)).doubleValue();
                if (fluxErrorHigh != 0) {
                    fluxErrorUpper[j] = fluxErrorHigh;
                }
                if (fluxErrorLow != 0) {
                    fluxErrorLower[j] = fluxErrorLow;
                }
                this.fluxErrorsPresent = true;
            } catch (Exception ex) {
            }
        }

        this.waveErrorsPresent = false;

        //WAVE Statistical error - simmetrical error

        for (int j = 0; j < sadSegment.getData().getNumDataPoints(); j++) {
            try {
                double waveError = ((Double) sadSegment.getData().getSpectral().getAccuracy().getStatError().getDataValue(j)).doubleValue();
                waveErrorUpper[j] = waveError;
                waveErrorLower[j] = waveError;
                this.waveErrorsPresent = true;
            } catch (Exception ex) {
            }
        }

        //WAVE statistical error - separate upper and lower values

        for (int j = 0; j < sadSegment.getData().getNumDataPoints(); j++) {
            try {
                double waveErrorHigh = ((Double) sadSegment.getData().getSpectral().getAccuracy().getStatErrHigh().getDataValue(j)).doubleValue();
                double waveErrorLow = ((Double) sadSegment.getData().getSpectral().getAccuracy().getStatErrLow().getDataValue(j)).doubleValue();
                if (waveErrorHigh != 0) {
                    waveErrorUpper[j] = waveErrorHigh;
                }
                if (waveErrorLow != 0) {
                    waveErrorLower[j] = waveErrorLow;
                }
                this.waveErrorsPresent = true;
            } catch (Exception ex) {
            }
        }



    }

    private void addSistematicErrors(SEDSegment sadSegment) {



        //FLUX Sistematic error - simmetrical error

        for (int j = 0; j < sadSegment.getData().getNumDataPoints(); j++) {
            try {
                double fluxSysError = ((Double) sadSegment.getData().getFlux().getAccuracy().getSysErr().getDataValue(j)).doubleValue();
                fluxErrorUpper[j] += (fluxValues[j] * fluxSysError) / 2;
                fluxErrorLower[j] += (fluxValues[j] * fluxSysError) / 2;
                this.fluxErrorsPresent = true;
            } catch (Exception ex) {
            }
        }



        //WAVE Statistical error - simmetrical error

        for (int j = 0; j < sadSegment.getData().getNumDataPoints(); j++) {
            try {
                double waveSysError = ((Double) sadSegment.getData().getSpectral().getAccuracy().getSysErr().getDataValue(j)).doubleValue();
                waveErrorUpper[j] += (waveValues[j] * waveSysError) / 2;
                waveErrorLower[j] += (waveValues[j] * waveSysError) / 2;
                this.waveErrorsPresent = true;
            } catch (Exception ex) {
            }
        }




    }

    private void addBins(SEDSegment sadSegment) {



        //FLUX Bins - separate upper and lower values

        for (int j = 0; j < sadSegment.getData().getNumDataPoints(); j++) {
            try {
                double fluxBinHigh = ((Double) sadSegment.getData().getFlux().getAccuracy().getBinHigh().getDataValue(j)).doubleValue();
                double fluxBinLow = ((Double) sadSegment.getData().getFlux().getAccuracy().getBinLow().getDataValue(j)).doubleValue();

                fluxErrorUpper[j] += fluxBinHigh - fluxValues[j];
                fluxErrorLower[j] += fluxValues[j] - fluxBinLow;

                this.fluxErrorsPresent = true;
            } catch (Exception ex) {
            }
        }



        //WAVE Bins - separate upper and lower values

        for (int j = 0; j < sadSegment.getData().getNumDataPoints(); j++) {
            try {
                double waveBinHigh = ((Double) sadSegment.getData().getSpectral().getAccuracy().getBinHigh().getDataValue(j)).doubleValue();
                double waveBinLow = ((Double) sadSegment.getData().getSpectral().getAccuracy().getBinLow().getDataValue(j)).doubleValue();

                waveErrorUpper[j] += waveBinHigh - waveValues[j];
                waveErrorLower[j] += waveValues[j] - waveBinLow;

                this.waveErrorsPresent = true;
            } catch (Exception ex) {
            }
        }


    }

    /*
     * Serialize this spectrum to a local file
     * 
     * */
    public void serializeToVOTable(String file) {


        String flux = this.getUnitsF();
        String wave = this.getUnitsW();

        //System.out.println("flux "+flux);
        //System.out.println("wave "+wave);


        SED sed = new SED();
        SEDSegment segment = new SEDSegment();
        Data data = new Data();


        //Populate DataModel


        segment.setDataModel(new SingleValue("Spectrum-1.0", "", "dataModel", "", IDataTypes.STRING));



        //Populate Curation

        segment.getCuration().setPublisher(new SingleValue("Science Archiving Team - European Space Astronomy Center", "", "publisher", "", IDataTypes.STRING));


        //Populate Target

        Target target = new Target();
        target.setName(new SingleValue("UNKNOWN", IDataTypes.STRING));
        segment.setTarget(target);


        //Populate Characterization

        segment.getCharacterization().getFlux().setUCD(flux);
        segment.getCharacterization().getFlux().setUnit(flux);
        segment.getCharacterization().getSpectral().setUCD(wave);
        segment.getCharacterization().getSpectral().setUnit(wave);

        segment.getCharacterization().getSpatial().setUCD("Spatial");
        segment.getCharacterization().getSpatial().getCoverage().getLocation().setValue(new SingleValue(0, "pos.eq", "spatialLocation", "", "int"));
        segment.getCharacterization().getSpatial().getCoverage().getBounds().setExtent(new SingleValue(0, "instr.fov", "spatialCoverageBoundsExtent", "", "int"));

        segment.getCharacterization().getTime().getCoverage().getLocation().setValue(new SingleValue(0, "time.epoch", "timeLocation", "", "int"));
        segment.getCharacterization().getTime().getCoverage().getBounds().setExtent(new SingleValue(0, "time.duration", "timeCoverageBoundsExtent", "", "int"));

        segment.getCharacterization().getSpectral().getCoverage().getLocation().setValue(new SingleValue(0, "instr.bandpass", "spectralLocation", "", "int"));
        segment.getCharacterization().getSpectral().getCoverage().getBounds().setExtent(new SingleValue(0, "instr.bandwidth", "spectralCoverageBoundsExtent", "", "int"));

        segment.getCharacterization().getSpectral().getCoverage().getBounds().setStart(new SingleValue(0, "stat.min", "spectralCoverageBoundsStart", "", "int"));
        segment.getCharacterization().getSpectral().getCoverage().getBounds().setStop(new SingleValue(0, "stat.max", "spectralCoverageBoundsStop", "", "int"));



        // Populate FLUX axis


        FluxAxis fluxAxis = new FluxAxis();
        fluxAxis.setUnits(flux);
        ArrayValue flux_values = new ArrayValue();
        flux_values.setName("flux");
        flux_values.setUnits(flux);
        for (int i = 0; i < this.fluxValues.length; i++) {
            Double dataValue = new Double(fluxValues[i]);
            flux_values.addDataValue(dataValue, IDataTypes.DOUBLE);
        }
        fluxAxis.setValue(flux_values);



        // Populate SPECTRAL axis

        SpectralAxis spectralAxis = new SpectralAxis();
        spectralAxis.setUnits(wave);
        ArrayValue wave_values = new ArrayValue();
        wave_values.setName("wave");
        wave_values.setUnits(wave);
        for (int i = 0; i < this.waveValues.length; i++) {
            Double dataValue = new Double(waveValues[i]);
            wave_values.addDataValue(dataValue, IDataTypes.DOUBLE);
            //waveValues.setUnits("units");
        }
        spectralAxis.setValue(wave_values);


        // Save axis in the SED

        data.setSpectral(spectralAxis);
        data.setFlux(fluxAxis);
        segment.setData(data);
        sed.addSegment(segment);


        // Serialize SED to file

        ISEDSerializer serializer = new VOTableSerializer();
        serializer.serialize(file, sed);


    }

    /*
     *
     * This method returns the standard column names (SEDWave and SEDFlux)
     * only if the file can be normally read.
     *
     *
     * Method for opening local files.
     *
     * */
    public static Vector getColumnsNameAndUnits(String urlString) throws Exception {


        Vector columnsName = new Vector();



        try {


            File file = Cache.getFile(urlString);


            //populate wave and flux values

            InputStream fis = null;
            try {
                fis = new FileInputStream(file);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            String urlSEDFile = "file:" + file.getAbsolutePath();

            //TODO continuar aqui: solo un segmento (espectro) por recurso (fichero)


            System.out.println("urlSEDfile " + urlSEDFile);



            //conversion to SED
            IWrapper wrapper = null;

            String waveUnit = null;
            String fluxUnit = null;


            SED sed = null;


            IReader reader = new cfa.vo.sed.io.Reader();

            /*
            try {

            wrapper = reader.read(fis, FileFormat.FITS);
            ISEDDeserializer fitsDeserializer = new FitsDeserializer();
            sed = fitsDeserializer.convertToSED(wrapper);

            } catch (Exception e1) {
            try {

            wrapper = reader.read(fis, FileFormat.XML);
            ISEDDeserializer xmlDeserializer = new XMLDeserializer();
            sed = xmlDeserializer.convertToSED(wrapper);


            } catch (Exception e2) {
            try {
             */
            wrapper = reader.read(fis, FileFormat.VOTABLE);
            ISEDDeserializer votableDeserializer = new VOTableDeserializer();
            sed = votableDeserializer.convertToSED(wrapper);
            /*
            } catch (Exception e3) {

            //throw new Exception("SED could not be deserialized");

            }
            }
            }
             */

            //consider only one segment

            int nSegments = 0;


            nSegments = sed.getNSegments();


            //System.out.println(nSegments + " segmentos");

            /*
            for (int i = 0; i < nSegments; i++) {
             */

            int i = 0;

            SEDSegment sadSegment = null;
            sadSegment = sed.getSegment(i);


            //set UNITS using SED libraries
            waveUnit = sadSegment.getData().getSpectral().getUnits();
            fluxUnit = sadSegment.getData().getFlux().getUnits();

            String[] column = new String[2];
            column[0] = "SEDWave";
            column[1] = waveUnit;
            columnsName.addElement(column);

            column = new String[2];
            column[0] = "SEDFlux";
            column[1] = fluxUnit;
            columnsName.addElement(column);


            fis.close();


        } catch (Exception e) {
            //e.printStackTrace();
        }


        return (columnsName);
    }
}
