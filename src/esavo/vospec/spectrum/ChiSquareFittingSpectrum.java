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


public class ChiSquareFittingSpectrum extends FittingSpectrum {
    
    
    public static double	CHI_SQUARE;
    public static boolean	DIVERGE;
    public int                  iter;
    
    
    public static int           DEG_FREEDOM;
    
    public 	int 		NUMBER_OF_POINTS = 100;
    public 	int 		NUMBER_OF_ITERATIONS = 100;
    public  int 		metadataCount = 0;
    
    public 	double[] 	parameter;
    
    public 	boolean 	logX;
    public 	boolean 	logY;
    
    public 	int 		numberOfPoints;
    public 	double[] 	xData;
    public 	double[]	yData;
    
    public 	double		xMin;
    public  double		xMax;
    public 	double		yMin;
    public  double		yMax;
    
    
    public 	double[] 	xDataLinear;
    public 	double[] 	yDataLinear;
    public 	double[] 	xErrorLinear;
    public 	double[] 	yErrorLinear;
    
    
    
    public ChiSquareFittingSpectrum() {
        super();
    }
    
    
    /** Creates a new instance of ChiSquareFittingSpectrum */
    public ChiSquareFittingSpectrum(Vector dataToFit,boolean logX,boolean logY) {
        
        super(dataToFit);
        
        this.setTitle("ChiSquareFittingSpectrum Fitting ");
        
        this.logX = logX;
        this.logY = logY;
        
        setToWait(true);
    }
    
    public ChiSquareFittingSpectrum(Spectrum spectrum,boolean logX,boolean logY) {
        
        super(spectrum);
        
        this.setTitle("ChiSquareFittingSpectrum Fitting ");
        
        this.logX = logX;
        this.logY = logY;
        
        setToWait(true);
    }
    
    public void calculatePoints() {
        
        /** Read the points from dataToFit and setting the xData and yData double arrays **/
        getXDataYData();
        
        /**Linear transformation. The weight of the points depends of the density **/
        getLinearXDataYData();
        
        /** Calculate initial parameters **/
        calculateInitialParameters();
        
        
        /** This is the method that is going to be superseded  **/
        fit();
        
        
        /** Calulate the waveValues and fluxValues using the parameters for representation **/
        getWaveAndFluxValues();
        
        /** Write metadata info **/
        writeMetadata();
        
        /** Clear the unnecessary data from memory nulling it **/
        clearDataFromMemory();
        
        setToWait(false);
    }
    
    public double[] calculateGuess() {
        /** Read the points from dataToFit and setting the xData and yData double arrays **/
        getXDataYData();
        
        /**Linear transformation. The weight of the points depends of the density **/
        getLinearXDataYData();
        
        /** Calculate initial parameters **/
        calculateInitialParameters();
        
        return parameter;
    }
    
    public void calculateInitialParameters() {
        return;
    }
    
    
    
    public void writeMetadata() {
        return;
    }
    
    
    public void clearDataFromMemory() {
        
        dataToFit 	= null;
        
        xData 		= null;
        yData		= null;
        
        xDataLinear	= null;
        yDataLinear	= null;
        xErrorLinear	= null;
        yErrorLinear	= null;
        
    }
    
    
    public void getXDataYData() {
        
        numberOfPoints = dataToFit.size();
        xData = new double[numberOfPoints];
        yData = new double[numberOfPoints];
        
        for(int i=0; i < numberOfPoints; i++) {
            
            double[] element = (double[]) dataToFit.elementAt(i);
            
            
            xData[i] = element[0];
            yData[i] = element[1];
            
            if(i==0) {
                xMin = xData[i];
                xMax = xData[i];
                
                yMin = yData[i];
                yMax = yData[i];
            }
            
            if(xData[i] < xMin) xMin = xData[i];
            if(xData[i] > xMax) xMax = xData[i];
            
            if(yData[i] < yMin) yMin = yData[i];
            if(yData[i] > yMax) yMax = yData[i];
        }
    }
    
    public void getLinearXDataYData() {
        
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
                
                double[] point = new double[4];
                point[0] = waveMedium;
                point[1] = fluxMedium / pointsInThisRange;
                point[2] = 1.* k;
                point[3] = error/pointsInThisRange;
                linearVector.add(point);
                linearPoints++;
            }
        }
        
        
        xDataLinear = new double[linearPoints];
        yDataLinear = new double[linearPoints];
        xErrorLinear = new double[linearPoints];
        yErrorLinear = new double[linearPoints];
        
        for(int kk=0; kk < linearVector.size() ; kk++) {
            double[] point = (double[]) linearVector.elementAt(kk);
            xDataLinear[kk] = point[0];
            yDataLinear[kk] = point[1];
            double error = point[3];
            
            xErrorLinear[kk] = 0.;
            yErrorLinear[kk] = error;
            
            if(kk > 0 && kk < linearVector.size() - 1) {
                double[] pointPrevious = (double[]) linearVector.elementAt(kk - 1);
                double[] pointNext = (double[]) linearVector.elementAt(kk + 1);
                yErrorLinear[kk] = 1. + error * 2./(pointNext[2] - pointPrevious[2]);
//                System.out.println("yError = "+yErrorLinear[kk]);
            }
            
            if(kk == 0) {
                double[] thisPoint = (double[]) linearVector.elementAt(kk);
                double[] pointNext = (double[]) linearVector.elementAt(kk + 1);
                yErrorLinear[kk] = 1. + error * 1./(pointNext[2] - thisPoint[2]);
//                System.out.println("yError = "+yErrorLinear[kk]);
            }
            
            if(kk == (linearVector.size() - 1)) {
                double[] pointPrevious = (double[]) linearVector.elementAt(kk-1);
                double[] thisPoint = (double[]) linearVector.elementAt(kk);
                yErrorLinear[kk] = 1. + error * 1./(thisPoint[2] - pointPrevious[2]);
                
//                System.out.println("yError = "+yErrorLinear[kk]);
            }
        }
        
    }
    
    
    public void getWaveAndFluxValues() {
        // XY Arrray generations (for representation)
        waveValues = new double[NUMBER_OF_POINTS];
        fluxValues = new double[NUMBER_OF_POINTS];
        
        for(int k=0; k<NUMBER_OF_POINTS; k++) {
            
            double waveDouble = k * (xMax - xMin)/(NUMBER_OF_POINTS-1) + xMin;
            double  fluxDouble = fittedFunction(waveDouble);
            
            waveValues[k] = waveDouble;
            fluxValues[k] = fluxDouble;
            
            if(logX)waveValues[k]=Math.pow(10.,waveValues[k]);
            if(logY)fluxValues[k]=Math.pow(10.,fluxValues[k]);
            
        }
        
    }
    
    
    public void fit() {
        
        int numberOfPoints 	= xDataLinear.length;
        int numberOfParameters 	= parameter.length;
        
        //DEG_FREEDOM = numberOfPoints - numberOfParameters;
        DEG_FREEDOM = numberOfPoints;
        
        
        //double[] s 	= new double[numberOfPoints];
        //for(int i=0; i < numberOfPoints; i++) s[i] = 1.;
        
        boolean[] vary = new boolean[parameter.length];
        for( int i = 0; i < parameter.length; i++ ) vary[i] = true;
        
        CHI_SQUARE = chiSquared(xDataLinear,yDataLinear,yErrorLinear);
        
        double newChiSquare = CHI_SQUARE;
        try {
            newChiSquare = solve( xDataLinear, yDataLinear, yErrorLinear, vary, NUMBER_OF_ITERATIONS, 0);
            DIVERGE = false;
        } catch(Exception ex) {
            DIVERGE = true;
        }
        
        if(newChiSquare >= CHI_SQUARE || DIVERGE) {
            calculateInitialParameters();
            DIVERGE = true;
        } else {
            CHI_SQUARE = newChiSquare;
        }
        
        calculateTotalChiSquare();
    }
    
    public void calculateTotalChiSquare() {
        
        int numberOfPoints 	= xData.length;
        double[] s = new double[numberOfPoints];
    
        CHI_SQUARE = chiSquared(xDataLinear,yDataLinear,yErrorLinear);
        
    }
    
    public double fittedFunction(double x) {
        return fittedFunction(x,parameter);
    }
    
    
    public double fittedFunction(double x,double[] tempParameter) {
        return 0;
    }
    
    
    public double functionDerivate(double x, int index) {
        return 0;
    }
    
    
    
    /**
     * calculate the current sum-squared-error
     * (Chi-squared is the distribution of squared Gaussian errors,
     * thus the name)
     */
    public double chiSquared(double[] x, double[] y, double[] s) {
        
        return chiSquared(x,y,s,parameter);
        
    } //chiSquared
    
    
    public double chiSquared(double[] x, double[] y, double[] s,double[] tmpParameter) {
        int npts = y.length;
        double sum = 0.;
        double d;
        
        System.out.println("numPoints segun JS = "+npts);
                       
        
        for( int i = 0; i < npts; i++ ) {
            
//            double d = y[i] - fittedFunction(x[i],tmpParameter);
//            if(y[i] != 0){
//                d = (y[i] - fittedFunction(x[i],tmpParameter))/y[i];
//            }else{
           
            d = y[i] - fittedFunction(x[i],tmpParameter);
//            }
            
//            System.out.println("yi - f(i) = "+d);
//            System.out.println("s["+i+"]"+" = "+s[i]);
            
//            s[i] = 1.0;
            
//            d = d/s[i];
            
            if(y[i] != 0) sum = sum+ d*d/Math.abs(y[i]);
            else          sum = sum + d*d;
        }
        
        return sum;
    } //chiSquared
    
    
    
    /**
     * Minimize E = sum {(y[k] - f(x[k],a)) / s[k]}^2
     * The individual errors are optionally scaled by s[k].
     * Note that LMfunc implements the value and gradient of f(x,a),
     * NOT the value and gradient of E with respect to a!
     *
     * @param x array of domain points
     * @param y corresponding array of values
     * @param a the parameters/state of the model
     * @param vary false to indicate the corresponding a[k] is to be held fixed
     * @param s2 sigma^2 for point i
     * @param lambda blend between steepest descent (lambda high) and
     *	jump to bottom of quadratic (lambda zero).
     * 	Start with 0.001.
     * @param termepsilon termination accuracy (0.01)
     * @param maxiter	stop and return after this many iterations if not done
     * @param verbose	set to zero (no prints), 1, 2
     *
     * @return the new lambda for future iterations.
     *  Can use this and maxiter to interleave the LM descent with some other
     *  task, setting maxiter to something small.
     */
    public double solve(double[] x, double[] y, double[] s,
            boolean[] vary, int maxiter,
            int verbose) throws Exception {
        
        double lambda = .001;
        
        int npts = y.length;
        int nparm = parameter.length;
        //assert s.length == npts;
        //assert x.length == npts;
        
        double e0 = chiSquared(x, y, s);
        //double lambda = 0.001;
        boolean done = false;
        
        // g = gradient, H = hessian, d = step to minimum
        // H d = -g, solve for d
        double[][] H = new double[nparm][nparm];
        double[] g = new double[nparm];
        //double[] d = new double[nparm];
        
        double[] oos2 = new double[s.length];
        for( int i = 0; i < npts; i++ )  {
            oos2[i] = 1./(s[i]*s[i]);
        }
        
        //int iter = 0;
        int term = 0;	// termination count test
        
        
        boolean wasBetter = false;
        
        do {
            
            ++iter;
            
            System.out.println("iter = "+iter);
            
            
            // hessian approximation
            for( int r = 0; r < nparm; r++ ) {
                for( int c = 0; c < nparm; c++ ) {
                    for( int i = 0; i < npts; i++ ) {
                        if (i == 0) H[r][c] = 0.;
                        double xi = x[i];
                        H[r][c] += (hessianElement(x[i], y[i], s[i], r, c));
                    }  //npts
                } //c
            } //r
            
            // boost diagonal towards gradient descent and set diagonal to one
            for( int r = 0; r < nparm; r++ )
                H[r][r] *= (1. + lambda);
            
            // gradient
            for( int r = 0; r < nparm; r++ ) {
                for( int i = 0; i < npts; i++ ) {
                    if (i == 0) g[r] = 0.;
                    double xi = x[i];
                    g[r] += (gradientElement(x[i], y[i], s[i], r));
                }
            } //npts
            
            // scale (for consistency with NR, not necessary)
            if (false) {
                for( int r = 0; r < nparm; r++ ) {
                    g[r] = -0.5 * g[r];
                    for( int c = 0; c < nparm; c++ ) {
                        H[r][c] *= 0.5;
                    }
                }
            }
            
            // solve H d = -g, evaluate error at new location
            //double[] d = DoubleMatrix.solve(H, g);
            double[] d = (new Matrix(H)).lu().solve(new Matrix(g, nparm)).getRowPackedCopy();
            //double[] na = DoubleVector.add(a, d);
            double[] na = (new Matrix(parameter, nparm)).plus(new Matrix(d, nparm)).getRowPackedCopy();
            double e1  = chiSquared(x, y, s, na);
            
            if (verbose > 0) {
                System.out.println("\n\niteration "+iter+" lambda = "+lambda);
                System.out.print("a = ");
                (new Matrix(parameter, nparm)).print(10, 2);
                if (verbose > 1) {
                    System.out.print("H = ");
                    (new Matrix(H)).print(10, 2);
                    System.out.print("g = ");
                    (new Matrix(g, nparm)).print(10, 2);
                    System.out.print("d = ");
                    (new Matrix(d, nparm)).print(10, 2);
                }
                System.out.print("e0 = " + e0 + ": ");
                System.out.print("moved from ");
                (new Matrix(parameter, nparm)).print(10, 2);
                System.out.print("e1 = " + e1 + ": ");
                if (e1 < e0) {
                    System.out.print("to ");
                    (new Matrix(na, nparm)).print(10, 2);
                } else {
                    System.out.println("move rejected");
                }
            }
            
            
            // in the C++ version, found that changing this to e1 >= e0
            // was not a good idea.  See comment there.
            //
            if (e1 > e0) {	// new location worse than before
                
                lambda *= 10.;
            } else {		// new location better, accept new parameters
                
                lambda /= 10.;
                
                e0 = e1;
                
                // simply assigning a = na will not get results copied back to caller
                for( int i = 0; i < nparm; i++ ) parameter[i] = na[i];
            }
            
            if (iter >= maxiter) done = true;
            
        } while(!done);
        
        
        return e0;
    } //solve
    
    
    public void addMetadataLine(String line) {
        metadata.put(metadataCount + "",line);
        metadataCount++;
    }
    
    public double hessianElement(double x, double y, double error,int r, int c) {
        
//	double hElement = functionDerivate(x, r) * functionDerivate(x, c);
        double hElement = functionDerivate(x, r) * functionDerivate(x, c) / error / error;
//	double hElement = .5 * doublePartialLorenztian((fittedFunction(x) - y)/error)
//				* functionDerivate(x, r) * functionDerivate(x, c) / error/error;
        
        return hElement;
        
        
    }
    
    public double gradientElement(double x, double y, double error,int r) {
        
//  	double gElement = (y- fittedFunction(x)) * functionDerivate(x, r);
        double gElement = (y- fittedFunction(x)) * functionDerivate(x, r) /error /error;
//  	double gElement = .5 * partialLorenztian((y- fittedFunction(x))/error) * functionDerivate(x, r)/error;
        
        return gElement;
    }
    
    public double partialLorenztian(double x) {
        return x/(1. + .5 * x * x);
    }
    
    public double doublePartialLorenztian(double x) {
        return (1. - .5 * x * x) / (1. + .5 * x * x);
    }
    
}
