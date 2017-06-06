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

import Jama.*;
import esavo.vospec.spectrum.Spectrum;
import java.util.*;

public class LevenbergMarquardt {

    private double                  norm;
    private int[]                   fixedParam;
    private double[]                beta;
    private double[][]              alpha;
    private Vector                  bestParam, initialParam;
    private Vector                  backwardModels, forwardModels;
    private Spectrum                spectrum;
    private TSAPmodel               initialModel,  modelToTry;
    private SED                     sed;
    private String                  urlString;
    private BestFitEventListener    listener;
    private TSAPmodelUtils          tsapModelUtils = new TSAPmodelUtils(this);//allows us to work with TSAPmodels
    private boolean                 stopBestFit = false;

    public LevenbergMarquardt(Spectrum spectrum, String urlString, Vector initialParam, BestFitEventListener listener) {

        this.spectrum       = spectrum;
        this.urlString      = urlString;
        this.initialParam   = initialParam;
        this.listener       = listener;
    }

    public void stopBestFit(){
        this.stopBestFit=true;
    }
    
    public boolean getStop(){
        return stopBestFit;
    }

    public void evalBestParam() throws Exception {


        /*
         *Looks for an initial model in the case that the input parameters
         *selected by the user don't have related theoretical model
         *
         */

        listener.newMessage("Looking for initial model");

        initializeLMmethod();

        if (this.initialModel == null) {
            listener.newMessage("Suitable initial model not found, please try again");
            throw new Exception();
        }

        listener.newMessage("Initial model found:");

        listener.newFit(initialModel.getParam());

        double lambda = 0.001;

        listener.newMessage("Performing calculations");

        /*Evaluates gradient and hessian*/
        evalMatrix();

        listener.newMessage("Looking for model to evaluate");

        /*Returns model given by LM method*/
        modelToTry(lambda);

        boolean goOn = true;
        int cont     = 0;
        int contt    = 0;
        int MAX_ITER = 100;

        while (goOn == true & cont < MAX_ITER) {
            
            if(stopBestFit)throw new Exception();

            if ((boolean) initialModel.equals(modelToTry) == false) {
                //in this case we eval chi
                TSAPmodel[] models = new TSAPmodel[2];
                models[0] = (TSAPmodel) initialModel;
                models[1] = (TSAPmodel) modelToTry;

                listener.newMessage("Calculating chi square values");

                ChiSquareFitting chiSquareFitting = new ChiSquareFitting(sed, models, this);
                double[] chiValues  = (double[]) chiSquareFitting.getValues();
                double chi          = chiValues[0];
                double chiToTry     = chiValues[1];


                if (chiToTry < chi) {

                    contt = 0;
                    initialModel.setEquals(modelToTry);

                    listener.newMessage("Better Chi-squared model found");

                    System.out.println("Chi=" + chiToTry + " with model " + initialModel.getParam());
                    listener.newFit(chiToTry, modelToTry.getParam());

                    evalMatrix();

                    lambda = lambda / 10;

                } else {
                    lambda = lambda * 10;
                }
            } else {
                contt++;
                if (contt == 3) {

                    modelToTry = (TSAPmodel) tsapModelUtils.betterChiSquaredNeighbour(sed, modelToTry);

                    if ((boolean) initialModel.equals(modelToTry) == false) {
                        contt = 0;

                        initialModel.setEquals(modelToTry);

                        listener.newMessage("Found better model");

                        System.out.println("Chi=" + tsapModelUtils.takenChi + " with model " + modelToTry.getParam());
                        listener.newFit(tsapModelUtils.takenChi, initialModel.getParam());

                        evalMatrix();

                        lambda = 0.001;

                    } else {
                        goOn = false;

                    }


                } else {
                    lambda = lambda * 10;
                }//contt = 3 

            }


            if (goOn == true) {
                modelToTry(lambda);
            }
            
            cont++;
            
        }//while

        if(cont >= MAX_ITER)listener.newMessage("Maximum number of iterations reached, leaving last model found");

        norm        = (double) initialModel.getNorm();
        bestParam   = (Vector) initialModel.getParam();

    }

    public void initializeLMmethod() throws Exception {

        initialModel = (TSAPmodel) tsapModelUtils.initialModel(new TSAPmodel(initialParam, urlString, this));
        sed          = new SED(spectrum, initialModel.getUnits());

        initialModel.setNorm(sed);

        //case of selected (by user) initialModel has not related spectrum
        if (tsapModelUtils.initialModelNull() == true) {
            listener.newMessage("Initial model not found, looking for a neighbour model");
            initialModel = (TSAPmodel) tsapModelUtils.betterChiSquaredNeighbour(sed, initialModel);
        }

    }

    /**  Finds the models to evaluate the derivatives.
     *   This method also evaluates fixedParam array that indicates
     *   by nonzero entries those parameters that be held fixed.
     *   Fixes a parameter if backward AND forward models have null related spectra.
     */
    public void modelsToEvaluateDerivatives() throws Exception {

        int posInParam          = 0;
        int effecPosition       = 0;	//effecPosition = position in Vector param considering param_MIN==param_MAX==param
        int paramSize           = (int) ((Vector) initialModel.getParam()).size();
        int numberOfeffecParam  = initialModel.numberOfEffecParam();

        backwardModels = new Vector();
        forwardModels  = new Vector();

        fixedParam = new int[numberOfeffecParam];

        System.out.println("numberOfeffecParam " + numberOfeffecParam);

        while (posInParam < paramSize) {

            TSAPmodel backwardModel = (TSAPmodel) tsapModelUtils.backwardModel(initialModel, posInParam);
            TSAPmodel forwardModel  = (TSAPmodel) tsapModelUtils.forwardModel(initialModel, posInParam);

            /*
             * If one of the necessary models to evaluate any derivative
             * is not  available, we use the backward or forward first order
             * accurate approximation to the first derivative depending on the case
             */
            if (backwardModel.getSpectrum() != null) {
                if (forwardModel.getSpectrum() != null) {
                    fixedParam[effecPosition] = 0;
                } else {
                    /*
                     * dy/dak = [y(ak)-y(ak-)]/[ak - ak-]
                     */
                    forwardModel.setEquals(initialModel);
                    fixedParam[effecPosition] = 0;
                }
            }

            if (backwardModel.getSpectrum() == null) {
                if (forwardModel.getSpectrum() != null) {
                    /*
                     * dy/dak = [y(ak+)-y(ak)]/[ak+ - ak]
                     */
                    backwardModel.setEquals(initialModel);
                    fixedParam[effecPosition] = 0;
                } else {
                    fixedParam[effecPosition] = 1;
                }
            }



            backwardModels.add(backwardModel);
            forwardModels.add(forwardModel);


            String name = (String) initialModel.getName(posInParam).toUpperCase();

//          param = (p1_min, p1_max, p2_min, p2_max, ..., pn_min, pn_max)
            if (name.indexOf("MIN") != -1 || name.indexOf("MAX") != -1) {//for parameters with min/max values
                posInParam = posInParam + 2;
            } else {
                posInParam++;
            }

            effecPosition++;
        }


    }

    /**
     * Evaluates the grided derivatives in each waveValue of the input sampling
     * If any of these models is not evaluated in any point of the sampling,
     * this method considers the linear interpolation between its previous and next points
     */
    public Vector derivatives(SamplingData samplingData) {
        int i                   = 0;
        int posInParam          = 0;
        int effecPosition       = 0;
        int paramSize           = (int) ((Vector) initialModel.getParam()).size();
        int numberOfeffecParam  = initialModel.numberOfEffecParam();
        int samplingLength      = ((double[]) samplingData.getSampling()).length;
        
        Vector derivatives      = new Vector();
        
        while (posInParam < paramSize) {
            double[] dy = new double[samplingLength];
            
            if (fixedParam[effecPosition] == 0) {//not fixed parameter
                //case paramToBeFitted
                TSAPmodel backwardModel     = (TSAPmodel) backwardModels.elementAt(effecPosition);                
                TSAPmodel forwardModel      = (TSAPmodel) forwardModels.elementAt(effecPosition);
                
                double[] backwardFluxValues = backwardModel.getNormalizedFluxValues(sed, samplingData);
                double[] forwardFluxValues  = forwardModel.getNormalizedFluxValues(sed, samplingData);                
                
                String backwardValueString  = (String) backwardModel.getSelectedValue(posInParam);
                String forwardValueString   = (String) forwardModel.getSelectedValue(posInParam);
                
                double backwardValue        = ((Double) new Double(backwardValueString)).doubleValue();
                double forwardValue         = ((Double) new Double(forwardValueString)).doubleValue();
                double den                  = Math.abs(backwardValue - forwardValue);
                //System.out.println("samplingLength "+samplingLength);
                //den != 0 
                for (int r = 0; r < samplingLength; r++) {
                    dy[r] = (forwardFluxValues[r] - backwardFluxValues[r]) / den;
                    //System.out.println("Calculating partial derivative for: " + initialModel.getName(posInParam));
                    //System.out.println("forwardflux "+forwardFluxValues[r]+" backward "+backwardFluxValues[r]+ " den "+den);
                  //System.out.println("dy[r] "+dy[r]+" efecPosition "+effecPosition);

                }
                
                derivatives.add(dy);
            }
            
            String name = (String) initialModel.getName(posInParam).toUpperCase();
            if (name.indexOf("MIN") != -1 || name.indexOf("MAX") != -1) {
                posInParam = posInParam + 2;
            } else {
                posInParam++;
            }
            effecPosition++;
        }
        System.out.println("Derivatives vector size: " + derivatives.size());
        return derivatives;
    }

    /* Creates a TSAPmodel[] array
     * with all the models we need to evaluate the sampling.
     */
    public SamplingData samplingData() throws Exception {

        int length = backwardModels.size() + forwardModels.size() + 1;

        TSAPmodel[] models = new TSAPmodel[length];

        models[0] = (TSAPmodel) initialModel;

        int i = 1;
        
        for (int j = 0; j < backwardModels.size(); j++) {
            models[i] = (TSAPmodel) backwardModels.elementAt(j);
            i++;
        }
        for (int j = 0; j < forwardModels.size(); j++) {
            models[i] = (TSAPmodel) forwardModels.elementAt(j);
            i++;
        }

        SamplingData samplingData = new SamplingData(sed, models, this);

        return samplingData;
    }

    /*Evaluates the derivatives in each point of the input sampling.
      To evaluate these derivatives we use the tsp from vector models:
      dy(xi)/dak = [y(ak+1;xi)-y(ak+1;xi)] / |ak+1-ak-1|
      If one of these models is not evaluated in xi, we do a linear interpolation
     */
    public double[][] alpha(SamplingData samplingData, Vector derivatives) {

        int length          = derivatives.size();
        double[] sampling   = samplingData.getSampling();
        double[][] alpha    = new double[length][length];

        for (int k = 0; k < length; k++) {
            for (int l = k; l < length; l++) {
                alpha[k][l] = 0.0;

                double weight = 1.0;

                for (int j = 0; j < sampling.length; j++) {

                    alpha[k][l] = alpha[k][l] + ((((double[]) (derivatives.elementAt(k))))[j] * (((double[]) (derivatives.elementAt(l))))[j]) / (weight * weight);
System.out.println("ALPHA "+alpha[k][l]+" derivatives "+derivatives.elementAt(k)+" "+derivatives.elementAt(l));
                }//j

            }//l
        }//k


        //alpha is a symmetrical matrix
        for (int k = 1; k < length; k++) {
            for (int l = 0; l < k; l++) {
                alpha[k][l] = alpha[l][k];
            }
        }


        return alpha;
    }

    public double[][] alphaModified(double[][] alpha, double lambda) {

        int length = alpha[0].length;

        double[][] alphaModified = new double[length][length];

        for (int k = 0; k < length; k++) {
            for (int l = 0; l < length; l++) {
                if (k == l) {
                    alphaModified[k][l] = alpha[k][l] * (1 + lambda);
                    //System.out.println("1 "+alpha[k][l] * (1 + lambda));
                } else {
                    alphaModified[k][l] = alpha[k][l];
                    //System.out.println("2 "+alpha[k][l]);
                }
            }
        }


        return alphaModified;

    }

    public double[] beta(SamplingData samplingData, Vector derivatives) {


        int size = derivatives.size();

        double[] tFluxValues    = initialModel.normalizedFluxValues(sed, samplingData);
        double[] fluxValues     = sed.reSampledFluxValues(samplingData);
        double[] sampling       = (double[]) samplingData.getSampling();
        double[] beta           = new double[size];


        for (int k = 0; k < size; k++) {
            beta[k] = 0.0;

            double weight = 1.0;

            for (int j = 0; j < sampling.length; j++) {

                beta[k] = beta[k] + (((double[]) (derivatives.elementAt(k))))[j] * (fluxValues[j] - tFluxValues[j]) / (weight * weight);

            }//j

        }//k

        return beta;
    }

    public double[] solveSystem(double[][] alpha, double[] beta) throws Exception {

        int size = beta.length;
        double[] daAux = new double[size];
        for (int i = 0; i < size; i++) {
            //System.out.println("loading in daAux "+i+" "+0.0);
            daAux[i] = 0.0;
        }
        Matrix alphaMatrix = new Matrix(alpha);
        CholeskyDecomposition alphaChD = new CholeskyDecomposition(alphaMatrix);
        if (alphaChD.isSPD()) {
            Matrix betaMatrix = new Matrix(beta, size);
            Matrix paramMatrix = new Matrix(1, size);
            try {
                paramMatrix = alphaChD.solve(betaMatrix);
            } catch (Exception e) {
                System.out.println("ChD solve failed: " + e);
                throw new Exception("ChD solve failed: " + e);
            }
            // The inverse provides the covariance matrix.
            Matrix alphaInverse = alphaMatrix.inverse();
            for (int k = 0; k < size; k++) {
                //System.out.println("loading in daAux "+k+" "+paramMatrix.get(k, 0));
                daAux[k] = paramMatrix.get(k, 0);
            }
        } else {
            QRDecomposition alphaQRD = new QRDecomposition(alphaMatrix);
            Matrix betaMatrix = new Matrix(beta, size);
            Matrix paramMatrix = new Matrix(1, size);
            try {
                paramMatrix = alphaQRD.solve(betaMatrix);
            } catch (Exception e) {
                System.out.println("QRD solve failed: " + e);
                throw new Exception("QRD solve failed: " + e);
            }
            // The inverse provides the covariance matrix.
            Matrix alphaInverse = alphaMatrix.inverse();
            for (int k = 0; k < beta.length; k++) {
                //System.out.println("loading in daAux "+k+" "+paramMatrix.get(k, 0));
                daAux[k] = paramMatrix.get(k, 0);
            }
        }                    //complete da with zeros in the positions of fixed param
        double[] da = new double[fixedParam.length];
        int iAux = 0;
        //System.out.println("da length:    " + da.length);
        //System.out.println("daAux length: " + daAux.length);

        for(int i = 0; i < fixedParam.length; i ++) {
           if(fixedParam[i] == 0){
               if(Double.isNaN(daAux[iAux])) {
                   throw new Exception("Problems solving matrix system");
               }else{
                   da[i] = daAux[iAux];
                   iAux++;
               }
           } else da[i] = 0.0;//case of fixed param that is going to be studied later
        }

        return da;
    }

    public void studyingFixedParam() throws Exception {

        int posInParam = 0;
        int effecPosition = 0;
        int paramSize = (int) ((Vector) initialModel.getParam()).size();
        int numberOfeffecParam = initialModel.numberOfEffecParam();

        TSAPmodel[] models = new TSAPmodel[2];

        while (posInParam < paramSize) {
            if (fixedParam[effecPosition] == 1) {

                //we compare modelToTry with modelToTry.backwarModel(m) and modelToTry.forwarModel(m)
                TSAPmodel backwardModel = (TSAPmodel) tsapModelUtils.backwardModel(modelToTry, posInParam);
                if (backwardModel.getSpectrum() != null) {

                    models[0] = (TSAPmodel) modelToTry;
                    models[1] = (TSAPmodel) backwardModel;

                    ChiSquareFitting chiSquareFitting = new ChiSquareFitting(sed, models, this);
                    double[] chiValues = chiSquareFitting.getValues();
                    double chi = chiValues[0];
                    double backwardChi = chiValues[1];

                    if (backwardChi < chi) {
                        modelToTry.setEquals(backwardModel);
                    }

                }

                TSAPmodel forwardModel = (TSAPmodel) tsapModelUtils.forwardModel(modelToTry, posInParam);
                if (forwardModel.getSpectrum() != null) {

                    models[0] = (TSAPmodel) modelToTry;
                    models[1] = (TSAPmodel) forwardModel;

                    ChiSquareFitting chiSquareFitting = new ChiSquareFitting(sed, models, this);
                    double[] chiValues = chiSquareFitting.getValues();
                    double chi = chiValues[0];
                    double forwardChi = chiValues[1];

                    if (forwardChi < chi) {
                        modelToTry.setEquals(forwardModel);
                    }
                }

            }


            String name = (String) modelToTry.getName(posInParam).toUpperCase();

            if (name.indexOf("MIN") != -1 || name.indexOf("MAX") != -1) {
                posInParam = posInParam + 2;
            } else {
                posInParam++;
            }

            effecPosition++;
        }

    }

    public void evalMatrix() throws Exception {

        modelsToEvaluateDerivatives();

        SamplingData samplingData = (SamplingData) samplingData();

        Vector derivatives  = derivatives(samplingData);
        
        alpha               = alpha(samplingData, derivatives);
        beta                = beta(samplingData, derivatives);

    }

    /**
     * Adjusts the step obtained by LM method according to the
     * available grided parameters.
     */
    public double[] adjustStep(double[] step) {

        int effecPosition       = 0;
        int posInParam          = 0;
        int paramSize           = (int) ((Vector) initialModel.getParam()).size();
        int numberOfEffecParam  = initialModel.numberOfEffecParam();

        double[] firstElements  = new double[numberOfEffecParam];
        double[] selectedValues = new double[numberOfEffecParam];
        double[] lastElements   = new double[numberOfEffecParam];


        while (posInParam < paramSize) {

            //method getValues returns an already ordered vector of Doubles
            Vector values = initialModel.getValues(posInParam);

            int size = values.size();

            String selectedValueString = initialModel.getSelectedValue(posInParam);
            
            Double firstElementDouble  = Double.valueOf((String)values.elementAt(0));
            Double lastElementDouble   = Double.valueOf((String) values.elementAt(size - 1));

            int indexOfSelectedValue = TSAPmodelUtils.indexOfSelectedValue(selectedValueString, values);

            Double selectedValueDouble = new Double(selectedValueString);
          
            double selectedValue       = selectedValueDouble.doubleValue();
            double firstElement        = firstElementDouble.doubleValue();
            double lastElement         = lastElementDouble.doubleValue();

            firstElements[effecPosition]  = firstElement;
            selectedValues[effecPosition] = selectedValue;
            lastElements[effecPosition]   = lastElement;

            String name = (String) initialModel.getName(posInParam).toUpperCase();

            if (name.indexOf("MIN") != -1 || name.indexOf("MAX") != -1) {
                posInParam = posInParam + 2;
            } else {
                posInParam++;
            }

            effecPosition++;
        }

        boolean go = true;

        while (go == true) {
            int cont = 0;

            //for (int l = 0; l < numberOfEffecParam; l++) {
            //    System.out.println("da[] ELEMENTS = " + step[l]);
            //}

            for (int j = 0; j < numberOfEffecParam; j++) {
                boolean noOption = false;

                if (selectedValues[j] == firstElements[j] & step[j] <= 0) {
                    noOption = true;
                    cont++;
                }
                if (selectedValues[j] == lastElements[j] & step[j] >= 0) {
                    noOption = true;
                    cont++;
                }
                if (noOption == false) {
                    if (step[j] >= 0) {
                        if (selectedValues[j] + step[j] <= lastElements[j]) {
                            cont = cont + 1;
                        } else {
                            step[j] = step[j] / 2;
                        }
                    }
                    if (step[j] < 0) {
                        if (firstElements[j] <= selectedValues[j] + step[j]) {
                            cont = cont + 1;
                        } else {
                            step[j] = step[j] / 2;
                        }
                    }
                }

            }// j

            if (cont == numberOfEffecParam) {
                go = false;
            }
        }

        return step;
    }

    /**
     * evaluates model to try using information given by LM method
     */
    public void modelToTry(double lambda) throws Exception {
        /*
         *looks for a model to try for LM method among
         * model(adjustedStep)
         * model(adjustedStep/2)
         * model(adjustedStep*2)
         * model(directionsGivenByStep)
         */

        //System.out.println("printing alpha ");

        //for(int i=0;i<alpha.length;i++){
        //    for(int j=0;j<alpha[i].length;j++){
        //        System.out.println("element "+i+" "+j+" = "+alpha[i][j]);
        //    }
        //}


        double[][] alphaModified    = alphaModified(alpha, lambda);
        //zero entries in array step for fixed parameters
        double[] step               = solveSystem(alphaModified, beta);
        double[] adjustedStep       = adjustStep(step);

        modelToTry = (TSAPmodel) tsapModelUtils.mostApproxModel(initialModel, adjustedStep);

        if (modelToTry.getSpectrum() == null) {
            modelToTry = (TSAPmodel) tsapModelUtils.modelInDefinedDirections(modelToTry, step);
        }
        //if(modelToTry.getSpectrum()==null) modelToTry.setEquals(initialModel);
        //if(modelToTry.getSpectrum()!=null) length++;

        double[] halfStep = new double[step.length];

        for (int i = 0; i < step.length; i++) {
            halfStep[i] = adjustedStep[i] / 2;
        }

        double[] doubleStep = new double[step.length];

        for (int i = 0; i < step.length; i++) {
            doubleStep[i] = adjustedStep[i] * 2;
        }


        TSAPmodel modelToTry1 = (TSAPmodel) tsapModelUtils.mostApproxModel(initialModel, halfStep);

        //if(modelToTry1.getSpectrum()==null) modelToTry1 = (TSAPmodel) tsapModelUtils.modelInDefinedDirections(modelToTry1, step);
        //if(modelToTry1.getSpectrum()==null) modelToTry1.setEquals(modelToTry);

        TSAPmodel modelToTry2 = (TSAPmodel) tsapModelUtils.mostApproxModel(initialModel, doubleStep);

        //if(modelToTry2.getSpectrum()==null) modelToTry2 = (TSAPmodel) tsapModelUtils.modelInDefinedDirections(modelToTry2, step);
        //if(modelToTry2.getSpectrum()==null) modelToTry2.setEquals(modelToTry);
        //if(modelToTry2.getSpectrum()!=null) length++;

        TSAPmodel modelToTry3 = (TSAPmodel) tsapModelUtils.modelInDefinedDirections(initialModel, step);
        //if(modelToTry3.getSpectrum()==null) modelToTry3.setEquals(modelToTry);
        //if(modelToTry3.getSpectrum()!=null) length++;

        TSAPmodel[] models = new TSAPmodel[4];
        models[0] = (TSAPmodel) modelToTry;
        models[1] = (TSAPmodel) modelToTry1;
        models[2] = (TSAPmodel) modelToTry2;
        models[3] = (TSAPmodel) modelToTry3;


        ChiSquareFitting chiSquareFitting   = new ChiSquareFitting(sed, models, this);
        double[] chiValues                  = chiSquareFitting.getValues();


        double chi = 0;
        int i = 0;
        int index = 0; //index of the smaller chi

        //initializes chi
        while (i < chiValues.length) {
            if (chiValues[i] != -1) {
                chi = chiValues[i];
                index = i;
                i = chiValues.length; //to stop the loop
            } else {
                i++;
            }
        }


        for (int j = 1; j < chiValues.length; j++) {
            if (chiValues[j] != -1 && chiValues[j] < chi) {
                chi = chiValues[j];
                index = j;

            }
        }

        //chi(models[index]) < chi(modelToTry)
        if (index != 0) {
            //index==0 means that chi(models)>=chi(modelToTry) or that all models have null spectrum
            TSAPmodel model = (TSAPmodel) models[index];
            modelToTry.setEquals(model);
        }

        if (modelToTry.getSpectrum() == null) {
            modelToTry.setEquals(initialModel);
        }

    //Now we should study fixed parameters
    //studyingFixedParam();
    }

    

    public Vector orderVectorOfStrings(Vector vector) {
        for (int i = 0; i < vector.size(); i++) {
            String elementString1 = (String) vector.elementAt(i);
            Double elementDouble1 = new Double(elementString1);
            double element1 = elementDouble1.doubleValue();

            for (int j = i + 1; j < vector.size(); j++) {
                String elementString2 = (String) vector.elementAt(i);
                Double elementDouble2 = new Double(elementString2);
                double element2 = elementDouble2.doubleValue();

                if (element2 < element1) {
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

    public Vector getBestParam() {
        return bestParam;
    }

    public double getNorm() {
        return norm;
    }
}
