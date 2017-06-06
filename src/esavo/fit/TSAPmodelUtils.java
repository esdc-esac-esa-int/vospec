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
package esavo.fit;


import java.util.*;



public class TSAPmodelUtils {
    
    LevenbergMarquardt levenberg    = null;
    
    boolean initialModelNull        = false;//true if initial model has related spectrum = null
    
    public TSAPmodelUtils(LevenbergMarquardt levenberg){
        this.levenberg = levenberg;
    }
    
    
    /**Looks for initial model with related spectrum*/
    public TSAPmodel initialModel(TSAPmodel inputModel) throws Exception{
        
        int posInParam          = 0;//position in vector param
        int paramSize           = (int) ((Vector)inputModel.getParam()).size();
        int numberOfEffecParam  = (int) inputModel.numberOfEffecParam();//number of effective parameters
        
        boolean goOn = true;
        
        if(inputModel.getSpectrum() != null) {
            goOn = false;		//No need to look for initial model
        } else {
            initialModelNull = true;
        }
        
        while(goOn == true & posInParam < paramSize) {
            
            if(levenberg.getStop())throw new Exception("Stop operations");
            
            TSAPmodel backwardModel = (TSAPmodel) backwardModel(inputModel, posInParam);
            
            if(backwardModel.getSpectrum() == null) {
                TSAPmodel forwardModel = (TSAPmodel) forwardModel(inputModel, posInParam);
                if(forwardModel.getSpectrum() != null) {
                    inputModel.setEquals(forwardModel);
                    goOn = false;
                }
            } else {
                inputModel.setEquals(backwardModel);
                goOn = false;
            }
            
            String name = (String) inputModel.getName(posInParam).toUpperCase();
            
            if(name.indexOf("MIN") != -1 || name.indexOf("MAX") != -1) posInParam = posInParam + 2;
            else posInParam++;
        }
        
        int iterations = 0;
        if(inputModel.getSpectrum() == null) {
            goOn = true;
            
            int MAX_ITER_FOR_INITIALMODEL = 10;
            
            while(goOn == true & iterations < MAX_ITER_FOR_INITIALMODEL) {
                
                if(levenberg.getStop())throw new Exception("Stop operations");
                
                Random r = new Random();
                double[] directions = new double[numberOfEffecParam];
                for(int i = 0; i < numberOfEffecParam; i++)
                    directions[i] = (double) r.nextInt(3)-1;
                
                TSAPmodel randomModel = (TSAPmodel) modelInDefinedDirections(inputModel, directions);
                
                if(randomModel.getSpectrum() != null) {
                    inputModel.setEquals(randomModel);
                    goOn = false;
                }
                
                iterations++;
            }
        }
        
        
        return inputModel;
    }
    
    public double takenChi = 0;
    /**Looks for a better chi-squared "neighbour" model in any directions*/
    public TSAPmodel betterChiSquaredNeighbour(SED sed, TSAPmodel inputModel) throws Exception {
        
        int effecPosition   = 0;
        int posInParam      = 0;
        int paramSize       = (int) ((Vector)inputModel.getParam()).size();
        
        TSAPmodel tsapModel         = (TSAPmodel) new TSAPmodel(inputModel, levenberg);
        TSAPmodel fixedTsapModel    = (TSAPmodel) new TSAPmodel(inputModel, levenberg);
        
        while(posInParam < paramSize) {
            
            TSAPmodel backwardModel = (TSAPmodel) backwardModel(fixedTsapModel, posInParam);
            
            
            if(backwardModel.getSpectrum()!=null) {
                
                TSAPmodel[] models = new TSAPmodel[2];
                models[0] = (TSAPmodel) fixedTsapModel;
                models[1] = (TSAPmodel) backwardModel;
                
                //WARNING: fixedTsapModel must have a related spectrum
                double[] chiValues = (double[]) (new ChiSquareFitting(sed, models, levenberg)).getValues();
                
                double currentChi = chiValues[0];
                double backwardChi = chiValues[1];
                
                //Updating TSAPmodel
                if(backwardChi < currentChi){
                    tsapModel.setEquals(backwardModel);
                    takenChi = backwardChi;
                }
            }
            
            TSAPmodel forwardModel = (TSAPmodel) forwardModel(fixedTsapModel, posInParam);
            
            if(forwardModel.getSpectrum()!=null) {
                
                TSAPmodel[] models = new TSAPmodel[2];
                models[0] = (TSAPmodel) fixedTsapModel;
                models[1] = (TSAPmodel) forwardModel;
                
                double[] chiValues = (double[]) (new ChiSquareFitting(sed, models, levenberg)).getValues();
                
                double currentChi = chiValues[0];
                double forwardChi = chiValues[1];
                
                //updating this.TSAPmodel
                if(forwardChi < currentChi){
                    tsapModel.setEquals(forwardModel);
                    takenChi = forwardChi;
                }
                
            }
            
            String name = (String) tsapModel.getName(posInParam).toUpperCase();
            
            if(name.indexOf("MIN") != -1 || name.indexOf("MAX") != -1) posInParam = posInParam + 2;
            else posInParam++;
            
            fixedTsapModel.setEquals(tsapModel);
            
            effecPosition++;
        }
        return tsapModel;
    }
    
    
    /**
     * it looks for the model related to the most approx param to
     * param of the input model plus input step
     * WARNING: The returned model can have related null spectrum
     */
    public TSAPmodel mostApproxModel(TSAPmodel inputModel, double[]da) throws Exception {
        int effecPosition = 0;	//position in vector param considering param_MIN = param_MAX = param
        int posInParam = 0;	//position in vector param
        int paramSize = (int) ((Vector)inputModel.getParam()).size();
        
        double[] paramValueToTry = new double[da.length];
        
        TSAPmodel tsapModel = (TSAPmodel) new TSAPmodel(inputModel, levenberg);
        
        while(posInParam < paramSize) {
            
            String selectedValueString = tsapModel.getSelectedValue(posInParam);
            
            double selectedValue = (new Double(selectedValueString)).doubleValue();
            double theoreticalValueToTry = selectedValue + da[effecPosition];
            
            Vector values = (Vector)tsapModel.getValues(posInParam);
            
            String auxString = (String) values.elementAt(0);
            Double auxDouble = new Double(auxString);
            
            paramValueToTry[effecPosition] = (double) auxDouble.doubleValue();
            
            double aux = Math.abs(paramValueToTry[effecPosition]-theoreticalValueToTry);
            
            for(int j = 1; j < values.size(); j++) {
                String auxString2 = (String) values.elementAt(j);
                Double auxDouble2 = new Double(auxString2);
                if(Math.abs(auxDouble2.doubleValue()-theoreticalValueToTry) < aux) {
                    paramValueToTry[effecPosition] = (double) auxDouble2.doubleValue();
                    aux = Math.abs(paramValueToTry[effecPosition]-theoreticalValueToTry);
                }
                
            }
            
            Double auxTry = new Double(paramValueToTry[effecPosition]);
            String auxString2 = (String) auxTry.toString();
            
            tsapModel.setSelectedValue(auxString2, posInParam);
            
            String name = (String) tsapModel.getName(posInParam).toUpperCase();
            
            if(name.indexOf("MIN") != -1 || name.indexOf("MAX") != -1) posInParam = posInParam + 2;
            else posInParam++;
            
            effecPosition++;
        }
        //Returns tsapModel althought it has related null spectrum
        return tsapModel;
    }
    
    
    /**Looks, starting from input model and in the directions given by da,
     * for a model WITH RELATED SPECTRUM NOT NULL
     */
    public TSAPmodel modelInDefinedDirections(TSAPmodel inputModel, double[] da) throws Exception {
        
        int lastOption = 0;
        int effecPosition= 0;
        int posInParam= 0;
        int paramSize = (int) ((Vector)inputModel.getParam()).size();
        
        boolean goOn = true;
        boolean[] lastValue = new boolean[da.length];
        
        TSAPmodel tsapModel = new TSAPmodel(inputModel, levenberg);
        
        
        while (goOn==true) {
            lastOption = 0;
            posInParam = 0;
            effecPosition = 0;
            
            while(posInParam < paramSize) {
                
                String aux1 = tsapModel.getSelectedValue(posInParam);
                String aux2 = " ";
                
                Vector values = (Vector) tsapModel.getValues(posInParam);//already ordered vector of Doubles
                
                int indexOfSelectedValue = indexOfSelectedValue(aux1, values);
                
                if(da[effecPosition]==0) lastValue[effecPosition] = true;
                
                if(da[effecPosition]>0) {
                    
                    int size = (int) ((Vector)values).size();
                    
                    if(indexOfSelectedValue != size-1) {
                        aux2 =(String)((Vector) values).elementAt(indexOfSelectedValue+1);
                        tsapModel.setSelectedValue(aux2, posInParam);
                    } else lastValue[effecPosition] = true;
                }
                
                if(da[effecPosition]<0) {
                    
                    if(indexOfSelectedValue > 0) {
                        aux2 =(String)((Vector)values).elementAt(indexOfSelectedValue-1);
                        tsapModel.setSelectedValue(aux2, posInParam);
                        
                    }else lastValue[effecPosition] = true;
                }//if da[i] < 0
                
                String name = (String) tsapModel.getName(posInParam).toUpperCase();
                
                if(name.indexOf("MIN") != -1 || name.indexOf("MAX") != -1) posInParam = posInParam + 2;
                else posInParam++;
                
                effecPosition++;
                
            }//while posInParam
            
            for(int j = 0; j < tsapModel.numberOfEffecParam(); j++)
                if(lastValue[j] == true) lastOption++;
            
            if(lastOption == tsapModel.numberOfEffecParam()) {
                goOn=false;
            }else {
                if(tsapModel.getSpectrum()!=null) goOn=false;
            }
        }
        //Returns tsapModel althought it has related null spectrum
        return tsapModel;
    }
    
    
    
    public TSAPmodel backwardModel(TSAPmodel inputModel, int posInParam) throws Exception {
        
        boolean goOn = true;
        
        TSAPmodel tsapModel = new TSAPmodel(inputModel, levenberg);
        
        Vector values = tsapModel.getValues(posInParam);
        
        while (goOn==true) {
            
            String aux1 = tsapModel.getSelectedValue(posInParam);
            
            int indexOfSelectedValue = indexOfSelectedValue(aux1, values);
            
            if(indexOfSelectedValue > 0) {
                String aux2 =(String)((Vector)values).elementAt(indexOfSelectedValue-1);
                tsapModel.setSelectedValue(aux2, posInParam);
            }else goOn = false;
            
            //if(goOn==true)
            if(tsapModel.getSpectrum()!=null) goOn=false;
        }
        
        //Returns backwardModel althought it has related null spectrum
        return tsapModel;
    }
    
    
    public TSAPmodel forwardModel(TSAPmodel inputModel, int posInParam) throws Exception {
        
        boolean goOn = true;
        
        TSAPmodel tsapModel = new TSAPmodel(inputModel, levenberg);
        
        Vector values = tsapModel.getValues(posInParam);
        
        while (goOn==true) {
            
            String aux1 = tsapModel.getSelectedValue(posInParam);
            
            int indexOfSelectedValue = indexOfSelectedValue(aux1, values);
            
            if(indexOfSelectedValue < values.size()-1) {
                String aux2 =(String)((Vector)values).elementAt(indexOfSelectedValue+1);
                tsapModel.setSelectedValue(aux2, posInParam);
            }else goOn = false;
            
            if(goOn==true)
                if(tsapModel.getSpectrum()!=null) goOn=false;
        }
        
        //Returns forwardModel althought it has related null spectrum
        return tsapModel;
    }
    
    
    //Note: orderedVector = ordered Vector of Double (comming from getValues(posInParam) method)
    public static int indexOfSelectedValue(String selectedValueString, Vector orderedVectorOfStrings) {
        
        //	Vector orderedVector = (Vector) orderVectorOfStrings(vector);
        
        Double selectedValueDouble  = new Double(selectedValueString);        
        int indexOfSelectedValue    = orderedVectorOfStrings.indexOf(selectedValueDouble);
        
        if(indexOfSelectedValue < 0) { 
            
            String minString = (String) orderedVectorOfStrings.elementAt(0);
            String maxString = (String) orderedVectorOfStrings.elementAt(orderedVectorOfStrings.size()-1);
            
            double minDouble = Double.valueOf(minString);
            double maxDouble = Double.valueOf(maxString);
            
            double minimumGap = (maxDouble-minDouble)/(2*(TSAPmodel.griddingFactor -1));                
            
            for(int m = 0; m < orderedVectorOfStrings.size(); m++){
               
                double selectedValue        = selectedValueDouble.doubleValue();       
                
                Double vectorElementDouble  = Double.valueOf((String)orderedVectorOfStrings.elementAt(m));
                
                if(Math.abs(vectorElementDouble-selectedValue) <= minimumGap)  indexOfSelectedValue = m;
                
            }            
        
        }
        
        return indexOfSelectedValue;
    }
    
    /* This method orders from lower to higher values an input Vector of Strings.
     *Returns an ordered Vector of Doubles.
     **/
    public Vector orderVectorOfStrings(Vector vector) {
        for(int i = 0; i < vector.size(); i++) {
            String elementString1   = (String) vector.elementAt(i);
            Double elementDouble1   = new Double(elementString1);
            double element1         = elementDouble1.doubleValue();
            
            for(int j = i+1; j < vector.size(); j++) {
                String elementString2 = (String) vector.elementAt(i);
                Double elementDouble2 = new Double(elementString2);
                double element2 = elementDouble2.doubleValue();
                
                if(element2 < element1 ) {
                    Double aux1Double = new Double(element1);
                    Double aux2Double = new Double(element2);
                    vector.set(i, aux2Double);
                    vector.set(j, aux1Double);
                    element1 = element2;
                }
            }
        }
        return vector;
    }
    
    
    public boolean initialModelNull() {
        return initialModelNull;
    }
    
    
}
