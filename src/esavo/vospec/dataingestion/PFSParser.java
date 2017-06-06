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

import esavo.vospec.photometry.*;
import java.util.Vector;


/**
 * Parser for implementing the Photometry Filter Service access
 *
 *
 * @author jgonzale
 */
public class PFSParser {

    public static String fpsUrl = "http://svo.cab.inta-csic.es/theory/fr/fps.php?";

    public PFSParser() {
    }

    public static Vector<PhotometryFilter> getPhotometryFilters(Vector<TsaServerParam> params){

        String url = fpsUrl;

        for(int i=0;i<params.size();i++){
            url=url+"&"+params.get(i).getName()+"="+params.get(i).getSelectedValue();
        }

        System.out.println("Retrieving photometry filters: "+url);

        Vector<PhotometryFilter> filters = new Vector();


        VOTable table = null;

        try {
            table = new VOTable(url, 10000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Vector<VOTableEntry> entries = new Vector();

        try {
            entries = table.getEntries();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        for (int i = 0; i < entries.size(); i++) {

            PhotometryFilter pf = new PhotometryFilter();

            for (int j = 0; j < entries.get(i).components.size(); j++) {


                String name = entries.get(i).components.get(j).getName();
                String utype = entries.get(i).components.get(j).getUtype();
                String ucd = entries.get(i).components.get(j).getUcd();
                String unit = entries.get(i).components.get(j).getUnit();
                String value = entries.get(i).components.get(j).getValue();

                //System.out.println(utype+" "+name);

                if (utype.toUpperCase().indexOf("PHOTOMETRYFILTER.UNIQUEIDENTIFIER") > -1) {
                    pf.setId(value);
                }

                if (utype.toUpperCase().indexOf("PHOTOMETRYFILTER.FILTERNAME") > -1) {
                    pf.setFilterName(value);
                }

                if (utype.toUpperCase().indexOf("PHOTOMETRYFILTER.FILTERDESCRIPTION") > -1) {
                    pf.setDescription(value);
                }

                if (utype.toUpperCase().indexOf("PHOTOMETRYFILTER.PHOTOMETRICSYSTEM") > -1) {
                    pf.setPhotSystem(value);
                }

                //FIXME: tochange when we know the utye
                if (name.toUpperCase().indexOf("INSTRUMENT") > -1) {
                    pf.setInstrument(value);
                }

                //FIXME: tochange when we know the utye
                if (name.toUpperCase().indexOf("FACILITY") > -1) {
                    pf.setFacility(value);
                }

                if (utype.toUpperCase().indexOf("PHOTOMETRYFILTER.MEANWAVELENGTH") > -1) {
                    PhysicalQuantity meanW = new PhysicalQuantity();
                    meanW.setUcd(ucd);
                    meanW.setUnit(unit);
                    meanW.setValue(new Double(value));
                    pf.setWavelengthMean(meanW);
                }

                if ((utype.toUpperCase().indexOf("PHOTOMETRYFILTER.ZEROPOINT") > -1) || (name.toUpperCase().indexOf("ZEROPOINT") > -1)) {
                    PhysicalQuantity zeroPoint = new PhysicalQuantity();
                    zeroPoint.setUcd(ucd);
                    zeroPoint.setUnit(unit);
                    zeroPoint.setValue(new Double(value));
                    pf.setZeroPoint(zeroPoint);
                }

                if (utype.toUpperCase().indexOf("PHOTOMETRYFILTER.MINWAVELENGTH") > -1) {
                    PhysicalQuantity minW = new PhysicalQuantity();
                    minW.setUcd(ucd);
                    minW.setUnit(unit);
                    minW.setValue(new Double(value));
                    pf.setWavelengthMin(minW);
                }

                if (utype.toUpperCase().indexOf("PHOTOMETRYFILTER.MAXWAVELENGTH") > -1) {
                    PhysicalQuantity maxW = new PhysicalQuantity();
                    maxW.setUcd(ucd);
                    maxW.setUnit(unit);
                    maxW.setValue(new Double(value));
                    pf.setWavelengthMax(maxW);
                }

                if (utype.toUpperCase().indexOf("PHOTOMETRYFILTER.EFFECTIVEWIDTH") > -1) {
                    pf.setWidthEff(new PhysicalQuantity(new Double(value), unit, ucd));
                }

                if (utype.toUpperCase().indexOf("PHOTOMETRYFILTER.FILTERTRANSMISSIONCURVE") > -1) {
                    //TrasmissionCurveParser tcp = new TrasmissionCurveParser();
                    //NDDataSet ndds = tcp.getReference(value);
                    //pf.setFilterTrasmissionCurve(ndds);
                    pf.setFilterTransmissionCurveUrl(value);
                }

            }

            filters.add(pf);

        }


        return filters;


    }


    
    public static NDDataSet getTransmissionCurve(String url){
        
        VOTable table = null;

        try {
            table = new VOTable(url, 10000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Vector<VOTableEntry> entries = new Vector();

        try {
            entries = table.getEntries();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        NDDataSet curve = new NDDataSet();

        for (int i = 0; i < entries.size(); i++) {

            Double wave = new Double(0);
            Double transmission = new Double(0);

            for (int j = 0; j < entries.get(i).components.size(); j++) {
                String value = entries.get(i).components.get(j).getValue();
                String ucd = entries.get(i).components.get(j).getUcd();
                String units = entries.get(i).components.get(j).getUnit();

                if (ucd.toUpperCase().equals("EM.WL")) {
                    curve.setUnitsW(units);
                    try{
                        wave = Double.valueOf(value);
                    }catch(Exception e){
                        //do nothing
                    }
                }

                if (ucd.toUpperCase().equals("PHYS.TRANSMISSION")) {
                    try{
                        transmission = Double.valueOf(value);
                    }catch(Exception e){
                        //do nothing
                    }
                }
                
            }

            curve.addPoint(wave, transmission);

        }

        return curve;

        
    }



    public static PhotometryFilter getFullPhotometryFilter(String ID) {

        String url = fpsUrl + ID;

        PhotometryFilter pf = new PhotometryFilter();

        VOTable table = null;

        try {
            table = new VOTable(url, 10000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Vector<VOTableEntry> entries = new Vector();

        try {
            entries = table.getEntries();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        for (int i = 0; i < entries.size(); i++) {

            for (int j = 0; j < entries.get(i).components.size(); j++) {


                String name = entries.get(i).components.get(j).getName();
                String utype = entries.get(i).components.get(j).getUtype();
                String ucd = entries.get(i).components.get(j).getUcd();
                String unit = entries.get(i).components.get(j).getUnit();
                String value = entries.get(i).components.get(j).getValue();

                //System.out.println(utype+" "+name);

                if (utype.toUpperCase().indexOf("PHOTOMETRYFILTER.UNIQUEIDENTIFIER") > -1) {
                    pf.setId(value);
                }

                if (utype.toUpperCase().indexOf("PHOTOMETRYFILTER.FILTERNAME") > -1) {
                    pf.setFilterName(value);
                }

                if (utype.toUpperCase().indexOf("PHOTOMETRYFILTER.FILTERDESCRIPTION") > -1) {
                    pf.setDescription(value);
                }

                if (utype.toUpperCase().indexOf("PHOTOMETRYFILTER.PHOTOMETRICSYSTEM") > -1) {
                    pf.setPhotSystem(value);
                }

                //FIXME: tochange when we know the utye
                if (name.toUpperCase().indexOf("INSTRUMENT") > -1) {
                    pf.setInstrument(value);
                }

                //FIXME: tochange when we know the utye
                if (name.toUpperCase().indexOf("FACILITY") > -1) {
                    pf.setFacility(value);
                }

                if (utype.toUpperCase().indexOf("PHOTOMETRYFILTER.MEANWAVELENGTH") > -1) {
                    PhysicalQuantity meanW = new PhysicalQuantity();
                    meanW.setUcd(ucd);
                    meanW.setUnit(unit);
                    meanW.setValue(new Double(value));
                    pf.setWavelengthMean(meanW);
                }

                if ((utype.toUpperCase().indexOf("PHOTOMETRYFILTER.ZEROPOINT") > -1) || (name.toUpperCase().indexOf("ZEROPOINT") > -1)) {
                    PhysicalQuantity zeroPoint = new PhysicalQuantity();
                    zeroPoint.setUcd(ucd);
                    zeroPoint.setUnit(unit);
                    zeroPoint.setValue(new Double(value));
                    pf.setZeroPoint(zeroPoint);
                }

                if (utype.toUpperCase().indexOf("PHOTOMETRYFILTER.MINWAVELENGTH") > -1) {
                    PhysicalQuantity minW = new PhysicalQuantity();
                    minW.setUcd(ucd);
                    minW.setUnit(unit);
                    minW.setValue(new Double(value));
                    pf.setWavelengthMin(minW);
                }

                if (utype.toUpperCase().indexOf("PHOTOMETRYFILTER.MAXWAVELENGTH") > -1) {
                    PhysicalQuantity maxW = new PhysicalQuantity();
                    maxW.setUcd(ucd);
                    maxW.setUnit(unit);
                    maxW.setValue(new Double(value));
                    pf.setWavelengthMax(maxW);
                }

                if (utype.toUpperCase().indexOf("PHOTOMETRYFILTER.EFFECTIVEWIDTH") > -1) {
                    pf.setWidthEff(new PhysicalQuantity(new Double(value), unit, ucd));
                }

                if (utype.toUpperCase().indexOf("PHOTOMETRYFILTER.FILTERTRANSMISSIONCURVE") > -1) {
                    //TrasmissionCurveParser tcp = new TrasmissionCurveParser();
                    //NDDataSet ndds = tcp.getReference(value);
                    pf.setFilterTransmissionCurveUrl(value);
                }

            }

        }


        NDDataSet curve = new NDDataSet();

        for (int i = 0; i < entries.size(); i++) {

            Double wave = new Double(0);
            Double transmission = new Double(0);

            for (int j = 0; j < entries.get(i).components.size(); j++) {

                String value = entries.get(i).components.get(j).getValue();
                String ucd = entries.get(i).components.get(j).getUcd();

                if (ucd.toUpperCase().equals("EM.WL")) {
                    try{
                        wave = Double.valueOf(value);
                    }catch(Exception e){
                        //do nothing
                    }
                }

                if (ucd.toUpperCase().equals("PHYS.TRANSMISSION")) {
                    try{
                        transmission = Double.valueOf(value);
                    }catch(Exception e){
                        //do nothing
                    }
                }
                
            }

            curve.addPoint(wave, transmission);

        }

        pf.setFilterTransmissionCurve(curve);


        return pf;


    }


}

