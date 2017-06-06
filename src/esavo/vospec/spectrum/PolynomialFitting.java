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

/**
 *
 * @author  jsalgado
 */

import Jama.*;
import java.util.*;



public class PolynomialFitting extends FittingSpectrum {
    
    
    private int NUMBER_OF_POINTS = 100;
    
    private int numberOfCoefficients;
    
    private double xMin;
    private double xMax;
    private double yMax;
    private double yMin;
    
    private boolean logX;
    private boolean logY;
    
    private double[] parameters;
    
    public PolynomialFitting() {
        super();
    }
    
    /** Creates a new instance of PolynomialFitting */
    public PolynomialFitting(Vector dataToFit,boolean logX,boolean logY,int order) {
        
        super(dataToFit);
        
        this.setTitle("Polynomial Fitting of " + order + " order");
        
        this.numberOfCoefficients = order + 1;
        
        this.logX = logX;
        this.logY = logY;
        
        setToWait(true);
    }
    
    public PolynomialFitting(Spectrum spectrum,boolean logX,boolean logY,int order) {
        
        super(spectrum);
        
        this.setTitle("Polynomial Fitting of " + order + " order");
        
        this.numberOfCoefficients = order + 1;
        
        this.logX = logX;
        this.logY = logY;
        
        setToWait(true);
    }
    
    public void calculatePoints() {
        
        int numberOfPoints = dataToFit.size();
        double[] xData = new double[numberOfPoints];
        double[] yData = new double[numberOfPoints];
        
        double[] xError = new double[numberOfPoints];
        double[] yError = new double[numberOfPoints];
        
        
        for(int i=0; i < numberOfPoints; i++) {
            
            double[] element = (double[]) dataToFit.elementAt(i);
            
            
            xData[i] = element[0];
            yData[i] = element[1];
            
            if(i==0) {
                xMin = xData[i];
                xMax = xData[i];
                yMax = yData[i];
                yMin = yData[i];
            }
            
            if(xData[i] < xMin) xMin = xData[i];
            if(xData[i] > xMax) xMax = xData[i];
            if(yData[i] < yMin) yMin = yData[i];
            if(yData[i] > yMax) yMax = yData[i];
            
            xError[i] = 0.;
            yError[i] = 0.;
            
            
        }
        
        int linearPoints = 0;
        Vector linearVector = new Vector();
        for(int k=0; k < 10 * NUMBER_OF_POINTS; k++) {
            
            double waveMedium = k * (xMax - xMin)/(10 * NUMBER_OF_POINTS-1) + xMin;
            double waveDoubleMax = (1.* k+.5) * (xMax - xMin)/(10 * NUMBER_OF_POINTS-1) + xMin;
            double waveDoubleMin = (1.* k-.5) * (xMax - xMin)/(10 * NUMBER_OF_POINTS-1) + xMin;
            
            double fluxMedium = 0;
            int pointsInThisRange = 0;
            
            for(int i=0; i < numberOfPoints; i++) {
                
                
                if(xData[i]<waveDoubleMax && xData[i]>waveDoubleMin) {
                    
                    fluxMedium = fluxMedium + yData[i];
                    pointsInThisRange++;
                    
                }
            }
            
            if(pointsInThisRange > 0) {
                double error = 0;
                for(int i=0; i < numberOfPoints; i++) {
                    if(xData[i]<waveDoubleMax && xData[i]>waveDoubleMin) {
                        error += Math.abs(yData[i] - fluxMedium/pointsInThisRange);
                    }
                }
                
                double[] point = new double[3];
                point[0] = waveMedium;
                point[1] = fluxMedium / pointsInThisRange;
                point[2] = error/pointsInThisRange;
                linearVector.add(point);
                linearPoints++;
            }
        }
        
        double[] xDataLinear = new double[linearPoints];
        double[] yDataLinear = new double[linearPoints];
        double[] xErrorLinear = new double[linearPoints];
        double[] yErrorLinear = new double[linearPoints];
        
        
        
        for(int kk=0; kk < linearVector.size() ; kk++) {
            double[] point = (double[]) linearVector.elementAt(kk);
            xDataLinear[kk] = point[0];
            yDataLinear[kk] = point[1];
            xErrorLinear[kk] = 0.;
            yErrorLinear[kk] = point[2];
            
            if(kk > 0 && kk < linearVector.size() - 1) {
                double[] pointPrevious = (double[]) linearVector.elementAt(kk - 1);
                double[] pointNext = (double[]) linearVector.elementAt(kk + 1);
                yErrorLinear[kk] = 1. + yErrorLinear[kk]  * 2./(pointNext[0] - pointPrevious[0]);
            }
            
            if(kk == 0) {
                double[] thisPoint = (double[]) linearVector.elementAt(kk);
                double[] pointNext = (double[]) linearVector.elementAt(kk + 1);
                yErrorLinear[kk] = 1. + yErrorLinear[kk]  * 1./(pointNext[0] - thisPoint[0]);
            }
            
            if(kk == (linearVector.size() - 1)) {
                double[] pointPrevious = (double[]) linearVector.elementAt(kk-1);
                double[] thisPoint = (double[]) linearVector.elementAt(kk);
                yErrorLinear[kk] = 1. + yErrorLinear[kk] * 1./(thisPoint[0] - pointPrevious[0]);
            }
        }
        
        //FIX
        double[] xDataLinearRectified = new double[linearPoints];
        double[] yDataLinearRectified = new double[linearPoints];
        
        int numPoints = linearVector.size();
        int order = this.numberOfCoefficients-1;
        
        double xMedium = (xMin+xMax)/2;
        double yMedium = (yMin+yMax)/2;
        
        for(int kk=0; kk < linearPoints ; kk++) {
//            xDataLinearRectified[kk] = linearVector.size()*order*(xDataLinear[kk] -xMin) / (xMax - xMin);
            //xDataLinearRectified[kk] = (xDataLinear[kk] -xMin) / (xMax - xMin);
            //yDataLinearRectified[kk] = yDataLinear[kk]/Math.abs(yMax);
            
            
            xDataLinearRectified[kk]=xDataLinear[kk] -xMedium;
            yDataLinearRectified[kk]=yDataLinear[kk] -yMedium;
            
            
            //System.out.println(kk + "pol x:" + xDataLinear[kk] + " x_rec:" + xDataLinearRectified[kk] + " y:" + yDataLinear[kk]);
        }
        
        
       //parameters = this.fit(this.numberOfCoefficients, xDataLinearRectified, yDataLinearRectified, xErrorLinear, yErrorLinear);
      //parameters = this.fit(this.numberOfCoefficients, xDataLinearRectified, yDataLinear, xErrorLinear, yErrorLinear);        
      parameters = this.fit(this.numberOfCoefficients, xDataLinear, yDataLinear, xErrorLinear, yErrorLinear);
        
//        for(int i=0; i < parameters.length/2; i++) {
//            parameters[i] = parameters[i] / Math.pow((xMax - xMin),i);
//        }
       
        //parameters = new double[tempParameters.length];

        //System.out.println("step2 " + parameters.length/2);
        
        //for(int kk = 0; kk < parameters.length/2; kk++){
        //    System.out.println("parameters previous["+kk+"] = "+parameters[kk]);
        //}
        
/*        double[] sum = new double[parameters.length/2];
        
        for(int kk = 0; kk < parameters.length/2; kk++){
            //parameters[kk] = 0;
            sum[kk] = 0;
            for(int l = kk; l < parameters.length/2; l++){
                int exp1= l+kk;
                int exp2 = l-kk;
//                sum[kk] = sum[kk]+Math.pow(-1, exp1)*parameters[l]*combi(l, kk)*Math.pow(xMin, exp2)*Math.pow(numPoints*order, l)/Math.pow((xMax-xMin), l);
                //sum[kk] = sum[kk]+Math.pow(-1, exp1)*parameters[l]*combi(l, kk)*Math.pow(xMin, exp2)/Math.pow((xMax-xMin), l);
                sum[kk] = sum[kk]+Math.pow(-1, exp1)*parameters[l]*combi(l, kk)*Math.pow(xMedium, exp2);
            }
            //parameters[kk] = Math.abs(yMax)*sum[kk];
            parameters[kk] = sum[kk];
           
            //System.out.println("parameters later["+kk+"] = "+parameters[kk]);
        
        }
        
*/        
        //for(int kk=0; kk < parameters.length/2; kk++) System.out.println(parameters[kk]);
        
        //System.out.println("step3 " + parameters.length/2);
        
        //parameters[0] = parameters[0] - parameters[1] * xMin / (xMax-xMin) + parameters[2] * Math.pow(xMin / (xMax-xMin),2);
        //parameters[1] = parameters[1]/(xMax-xMin) - 2.* parameters[2] * xMin/Math.pow((xMax-xMin),2);
        //parameters[2] = parameters[2]/Math.pow((xMax-xMin),2);
        
        
        // XY Arrray generations
        
        
        waveValues = new double[10*NUMBER_OF_POINTS];
        fluxValues = new double[10*NUMBER_OF_POINTS];
      
        //waveValues = new double[6000];
        //fluxValues = new double[6000];
        
        //int cont = 0;
       
        for(int k=0; k<10*NUMBER_OF_POINTS; k++) {
            //for(int k=-3000; k<3000; k++) {
            double waveDouble = k * (xMax - xMin)/(10*NUMBER_OF_POINTS-1) + xMin;
            //double waveDouble = k;
            
            double  fluxDouble = 0;
            int     powerIndex = parameters.length/2;
            for(int kIndex=0; kIndex<parameters.length/2; kIndex++) {
                fluxDouble += parameters[kIndex] * Math.pow(waveDouble,1.*kIndex);
            }
            
            waveValues[k] = waveDouble;
            //fluxValues[k] = fluxDouble * Math.abs(yMax);
            fluxValues[k] = fluxDouble;
            //fluxValues[k] = fluxDouble + yMedium;
                        
            
            //System.out.println("despues x:" + waveDouble + " y:" + fluxDouble);
            
            
          if(logX)waveValues[k]=Math.pow(10.,waveValues[k]);
          if(logY)fluxValues[k]=Math.pow(10.,fluxValues[k]);
        }
        // Setting the metadata
        int metadataInt = 1;
        String equationString = "y=";
        
        for(int kk=0; kk < parameters.length/2; kk++) {
            super.metadata.put(metadataInt+"","A"+ kk + " : "+parameters[kk]);
            
            // Setting the string of the equation
            equationString = equationString + "A" + kk;
            if(kk>0) equationString = equationString + " x^" + kk;
            if(kk< parameters.length/2.-1) equationString = equationString + "+";
            
            metadataInt++;
        }
        super.metadata.put("0",equationString);
        
        if(logX) {
            super.metadata.put(metadataInt + "","X axis fitted in logarithm scale");
            metadataInt++;
        }
        if(logY) {
            super.metadata.put(metadataInt + "","Y axis fitted in logarithm scale");
            metadataInt++;
        }
        setToWait(false);
        
    }
    
    public double[]  getParameters() {
        return this.parameters;
    }
    
    
    /**
     *  Use the Least Squares fit method for fitting a
     *  polynomial to 2-D data for measurements
     *  y[i] vs. dependent variable x[i]. This fit assumes
     *  there are errors only on the y measuresments as
     *  given by the sigmaY array.<br><br>
     *
     *  See, e.g. Press et al., "Numerical Recipes..." for details
     *  of the algorithm. <br><br>
     *
     *  The solution to the LSQ fit uses the open source JAMA -
     *  "A Java Matrix Package" classes. See http://math.nist.gov/javanumerics/jama/
     *  for description.<br><br>
     *
     *  @param parameters - first half of the array holds the coefficients for the polynomial.
     *  The second half holds the errors on the coefficients.
     *  @param x - independent variable
     *  @param y - vertical dependent variable
     *  @param sigmaX - std. dev. error on each x value
     *  @param sigmaY - std. dev. error on each y value
     *  @param numPoints - number of points to fit. Less than or equal to the
     *  dimension of the x array.
     */
    public double[] fit(int numberOfCoefficients, double [] x, double [] y,
            double [] sigmaX, double [] sigmaY){
        
        // numParams = num coeff + error on each coeff.
        int         numPoints  = x.length;
        double[]    parameters = new double[numberOfCoefficients * 2];
        
        
        int nk = parameters.length/2;
        
        double [][] alpha  = new double[nk][nk];
        double [] beta = new double[nk];
        double term = 0;
        
        for(int k=0; k < nk; k++){
            
            // Only need to calculate diagonal and upper half
            // of symmetric matrix.
            for(int j=k; j < nk; j++){
                
                // Calc terms over the data points
                term = 0.0;
                alpha[k][j] = 0.0;
                for(int i=0; i < numPoints; i++){
                    
                    double prod1 = 1.0;
                    // Calculate x^k
                    if( k > 0) for( int m=0; m < k; m++) prod1 *= x[i];
                    
                    double prod2 = 1.0;
                    // Calculate x^j
                    if( j > 0) for( int m=0; m < j; m++) prod2 *= x[i];
                    
                    // Calculate x^k * x^j
                    term = (prod1*prod2);
                    
                    if( sigmaY != null && sigmaY[i] != 0.0)
                        term /= (sigmaY[i]*sigmaY[i]);
                    alpha[k][j] += term;
                                      
                }
                
                System.out.println("alpha element["+k+"]["+j+"] = " + alpha[k][j]);
                alpha[j][k] = alpha[k][j];// C will need to be inverted.
            }
            
            for(int i=0; i < numPoints; i++){
                double prod1 = 1.0;
                if( k > 0) for( int m=0; m < k; m++) prod1 *= x[i];
                term = (y[i] * prod1);
                if( sigmaY != null  && sigmaY[i] != 0.0)
                    term /= (sigmaY[i]*sigmaY[i]);
                beta[k] +=term;
            }
        }
        
        double [][] A   = new double[numPoints][nk];
        double [] b     = new double[numPoints];
        
        for(int i = 0; i < numPoints; i++){
            for(int j = 0 ; j < nk; j++){
                //if(sigmaX[i] != 0) A[i][j] = Math.pow(x[i], j)/sigmaX[i];
                //else A[i][j] = Math.pow(x[i], j);
                A[i][j] = Math.pow(x[i], j);
            }
        }
        for(int i = 0; i < numPoints; i++){
            //if(sigmaY[i] != 0) b[i] = y[i]/sigmaY[i];
            //else b[i] = y[i];
            b[i] = y[i];
        }
        
        // Use the Jama QR Decomposition classes to solve for
        // the parameters.
        Matrix AMatrix = new Matrix(A);
        
        //QRDecomposition alphaQRD = new QRDecomposition(alphaMatrix);
        SingularValueDecomposition svdA = new SingularValueDecomposition(AMatrix);
        
        Matrix U = svdA.getU();
        Matrix V = svdA.getV();
        Matrix S = svdA.getS();
        
        Matrix invS = new Matrix(nk, nk);
        
        for(int i = 0; i < nk; i++){
            double diagElement = S.get(i, i);
            if( diagElement != 0){
                invS.set(i, i,1/diagElement);
            }
        }
        
        Matrix bMatrix = new Matrix(b,numPoints);
        Matrix paramMatrix;
        
        Matrix prod = V.times(invS);
        prod = prod.times(U.transpose());
        
        Matrix id = AMatrix.times(prod);
        
        for(int k = 0; k < nk; k++){
            for(int l = 0; l < nk; l++){
                System.out.println("alpha*prod["+k+"]["+l+"] = "+id.get(k,l));
            }
        }
        
        
        paramMatrix = prod.times(bMatrix);
        
        
/*        try{
            //paramMatrix = alphaQRD.solve(betaMatrix);
            
        }catch( Exception e){
            System.out.println("QRD solve failed: "+ e);
            return (double[]) null;
        }
        
 */     // The inverse provides the covariance matrix.
        Matrix c = AMatrix.inverse();
        
        for(int k=0; k < nk; k++){
            
            parameters[k] = paramMatrix.get(k,0);
            
            // Diagonal elements of the covariance matrix provide
            // the square of the parameter errors. Put in top half
            // of the parametes array.
            parameters[k+nk] = Math.sqrt(c.get(k,k));
        }
        
        
        return parameters;
    }
    
    
    public double fittedFunction(double x) {
        
        double fluxDouble = 0;
        
        int	powerIndex = parameters.length/2;
        for(int kIndex=0; kIndex<parameters.length/2; kIndex++) {
            fluxDouble += parameters[kIndex] * Math.pow(x,1.*kIndex);
        }
        
        return fluxDouble;
    }
    
    public int combi(int n, int m){
        
        int combi;
        int num = 1;
        int den = 1;
        
        if(m == 0){
            combi = 1;
            
        }else{
            for(int k = 0; k < m; k++){
                num = num*(n-k);
                den = den*(m-k);
            }
            
            if (den != 0){
                combi = num/den;
            }else{
                combi = 1;
            }
        }
        
          System.out.println("combi "+n+" sobre "+m+ " = "+combi);  
        
        return combi;
    }
    
}
