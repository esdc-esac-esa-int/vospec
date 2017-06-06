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


import esavo.vospec.util.Utils;
import java.io.Serializable;
import java.util.Vector;

/**
 *
 *
 *
 * <pre>
 * %full_filespec: Unit.java,4:java:2 %
 * %derived_by: ibarbarisi %
 * %date_created: Mon Oct 15 18:00:44 2007 %
 *
 * </pre>
 *
 * @author ibarbarisi
 * @version %version: 4 %
 */

public class Unit implements Serializable {
    
    
    public String waveUnits;
    public String fluxUnits;
    public Vector waveVector;
    public Vector fluxVector;
    
    public Unit() {
        waveVector = new Vector();
        fluxVector = new Vector();
    }
    
    public Unit(String waveUnits, String fluxUnits) {
        this();
        
        this.waveUnits = waveUnits;
        this.fluxUnits = fluxUnits;
        
        switchUnits();
    }
    
    public String getWaveUnits() {
        return waveUnits;
    }
    
    public String getFluxUnits() {
        return fluxUnits;        
    }    
    
    public Unit(String waveDimEq, String waveScale,String fluxDimeEq,String fluxScale) {
        this();
        waveVector.add(new Double(waveScale));
        waveVector.add(new String(waveDimEq));
        
        fluxVector.add(new Double(fluxScale));
        fluxVector.add(new String(fluxDimeEq));
    }
    
 
    public void switchUnits(){        
        setDimForUnit(waveUnits,waveVector);
        setDimForUnit(fluxUnits,fluxVector);
    }
    
    public void setDimForUnit(String unitsString, Vector unitVector) {
        
        
        if(unitsString.equals("micron")) {           
        
            unitVector.add(new Double(1E-6));
            unitVector.add("L");
        
        } else if (unitsString.equals("m")){
        
            unitVector.add(new Double(1));
            unitVector.add("L");
        
        } else if (unitsString.equals("Angstrom")){
        
            unitVector.add(new Double(1E-10));
            unitVector.add("L");
         
        } else if (unitsString.equals("Kev")){
        
            unitVector.add(new Double(2.41473E+17));
            unitVector.add("T-1");
            
       } else if (unitsString.equals("Hz")){
       
            unitVector.add(new Double(1));
            unitVector.add("T-1");
             
        } else if (unitsString.equals("Jy")){
                   
            unitVector.add(new Double(1E-26));
            unitVector.add("MT-2");
             
        }else if (unitsString.equals("1/cm")){

            unitVector.add(new Double(1E2));
            unitVector.add("L-1");

        } else if (unitsString.equals("W/cm2/um")){
                   
            unitVector.add(new Double(1E+10));
            unitVector.add("ML-1T-3");
             
        } else if (unitsString.equals("erg/cm2/s/Angstrom")){

            System.out.println("Im here1");
            unitVector.add(new Double(1E+7));
            unitVector.add("ML-1T-3");
 
        } else if (unitsString.equals("erg/cm2/s/A")){

            unitVector.add(new Double(1E+7));
            unitVector.add("ML-1T-3");

        } else if (unitsString.equals("Joule")){
                  
            unitVector.add(new Double(1));
            unitVector.add("ML2T-2");

        } else if (unitsString.equals("Counts")){
                             
            unitVector.add(new Double(6.626E-34));
            unitVector.add("ML2T-1");
        
        } else if (unitsString.equals("W/m2")){
                            
            unitVector.add(new Double(1));
            unitVector.add("MT-3");
       
        } else if (unitsString.equals("erg/cm2/s")){
                            
            unitVector.add(new Double(1E-3));
            unitVector.add("MT-3");
       
         } else {
            //System.out.println("Im here2");
            try {
                String[] dimensional = Utils.getDimensionalEquation(unitsString);
                unitVector.add(new Double(dimensional[0]));
                unitVector.add(dimensional[1]);
                
            } catch (Exception e) {
                System.out.println("Problems parsing:" + unitsString);
            }   
             
        }    
    }
    
    public Vector getWaveVector() {
        return this.waveVector;
    }
    
    public Vector getFluxVector() {
        return this.fluxVector;
    }
    
    public double[] getFluxCoefficients(Unit originalUnits) {

        Vector originalVector = originalUnits.getFluxVector();
        Vector finalVector = fluxVector;
        return getCoefficients(originalVector,finalVector);
    
    }    

    public double[] getWaveCoefficients(Unit originalUnits) {
    
        Vector originalVector = originalUnits.getWaveVector();        
        Vector finalVector = waveVector;
        return getCoefficients(originalVector,finalVector);
    
    }
    
    public double getFluxFactor(Unit originalUnits) {
        Vector originalVector = originalUnits.getFluxVector();        
        Vector finalVector = fluxVector;
        return getFactor(originalVector,finalVector);        
    }    
    
    
    public double getWaveFactor(Unit originalUnits) {
        Vector originalVector = originalUnits.getWaveVector();        
        Vector finalVector = waveVector;
        return getFactor(originalVector,finalVector);        
    }    

    
    public double getFactor(Vector originalVector,Vector finalVector) {
        double factor = ((Double) originalVector.elementAt(0)).doubleValue() / 
                        ((Double) finalVector.elementAt(0)).doubleValue();
        
        return factor;
    }     
    
    public double[] getCoefficients(Vector originalVector,Vector finalVector) {
        
        // Calculation for the Flux dimensional equation division
        long[] originalUnits    = getLongUnitsArray((String) originalVector.elementAt(1));
        long[] finalUnits       = getLongUnitsArray((String) finalVector.elementAt(1));
        long[] divisionUnits    = getDivUnits(finalUnits,originalUnits);

        if (divisionUnits[0] !=0) {
            System.out.println("Can't solve Units Conversion");
            System.exit(0);
        }
        
        double nIndex = divisionUnits[1] + divisionUnits[2];
        double mIndex = - divisionUnits[2];
        
        double[] returnArray = new double[3];
        returnArray[0] = 0.;
        returnArray[1] = mIndex;
        returnArray[2] = nIndex;
        
        return returnArray;
    }
    
    public long[] getWaveLongUnitsArray() {
        return getLongUnitsArray((String) waveVector.elementAt(1));
    }    

    public long[] getFluxLongUnitsArray() {
       return getLongUnitsArray((String) fluxVector.elementAt(1));        
    }    
     
    
       public long[] getLongUnitsArray(String dimEq) {
      
       long[] outputLongArray=null;
       outputLongArray = new long[3];
       outputLongArray[0] = getExponentForCoefficient(dimEq,"M");
       outputLongArray[1] = getExponentForCoefficient(dimEq,"L");
       outputLongArray[2] = getExponentForCoefficient(dimEq,"T");
       return outputLongArray;
   }
   
   
   public long[] getDivUnits(long[] finalUnits,long[] originalUnits) {
       
       long[] outputLongArray=null;
       outputLongArray = new long[3];
       outputLongArray[0] = finalUnits[0] - originalUnits[0];
       outputLongArray[1] = finalUnits[1] - originalUnits[1];
       outputLongArray[2] = finalUnits[2] - originalUnits[2];      
       return outputLongArray; 
   }
     
   public long getExponentForCoefficient(String dimEq, String coefficient) {
       
       int index=0;
       
       if (dimEq.indexOf(coefficient)<0){
           return 0;
       }else{
           index= dimEq.indexOf(coefficient);
       }
       
       index++;
       
       long sign = 1;
       if(index == dimEq.length()) return 1;
       
       if(dimEq.substring(index,index+1).equals("-")) {
           sign = -1;
           index++;
       } else if(dimEq.substring(index,index+1).equals("+")) {
           sign = 1;
           index++;           
       }
       
       String numberString = "";
       while(index < dimEq.length()) {
           if(!Character.isDigit(dimEq.charAt(index)))break;
           numberString = numberString + dimEq.substring(index,index+1);
           index++;
       }
       
       if(numberString.equals("")||numberString.equals("/"))numberString="1";
       
       long numberLong = sign * (new Integer(numberString)).intValue();
       return numberLong;
   }
   
   public String toString() {
        return  ((Double) waveVector.elementAt(0)).toString() + "/" + (String) waveVector.elementAt(1) + "/|/" +  
                ((Double) fluxVector.elementAt(0)).toString() + "/" + (String) fluxVector.elementAt(1);
   }    

}
