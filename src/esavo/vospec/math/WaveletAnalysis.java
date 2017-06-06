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
package esavo.vospec.math;


import esavo.vospec.spectrum.*;

/**
 *
 * @author alaruelo
 */
public class WaveletAnalysis {
    
    //types of wavelets
    public static int DAUBECHIES 	= 0;
    public static int SYMLETS    	= 1;
    public static int COIFLETS 		= 2;
    
    //thresholding methods
    public static int HARD              = 3;
    public static int SOFT              = 4;
    
    //thresholds
    public static int UNIVERSAL 	= 5;
    //public static int DONOHO     	= 6;
    
    int         ioff;
    int         joff;
    
    double[]    h;//filter coeffs
    double[]    g;
    
    Spectrum    evenlySpacedSpectrum;
    
    
    /**
     * Creates a new instance of Wavelet
     */
    
    //==================================================================================\
    //   The input spectrum must be evenlySpaced sampled at power of 2 sampling points  \
    //==================================================================================\
    public WaveletAnalysis() {
    }
    
    public WaveletAnalysis(Spectrum spectrum, int waveletType, int numberOfCoeffs) {
        initializeSpectrum(spectrum);
        initializeCoeff(waveletType, numberOfCoeffs);
    }
    
    
    public void initializeSpectrum(Spectrum spectrum){
        //1024 == 2^10
        this.evenlySpacedSpectrum = MathUtils.evenlySpacedSpectrum(spectrum, 1024);
    }
    
    public void initializeCoeff(int type, int numberOfCoeffs){
        double sqrt_2   = Math.sqrt( 2 );
        double sqrt_3   = Math.sqrt( 3 );
        double sqrt_10  = Math.sqrt( 10 );
        double sqrt     = Math.sqrt( 5 + 2 * Math.sqrt( 10 ) );
        double denom    = 4 * Math.sqrt( 2 );
        
        this.h = new double[numberOfCoeffs];
        this.g = new double[numberOfCoeffs];
        
        
        if(numberOfCoeffs == 2){
            
            h[0]    =   0.7071067811865475;
            h[1]    =   0.7071067811865475;
            
        } else if(numberOfCoeffs == 4){
/*            if(type ==DAUBECHIES){
                h[0] = (1 + sqrt_3)/denom;
                h[1] = (3 + sqrt_3)/denom;
                h[2] = (3 - sqrt_3)/denom;
                h[3] = (1 - sqrt_3)/denom;
            } else if(type == SYMLETS){
 */
            h[0]    =   0.482962913144;
            h[1]    =   0.836516303737;
            h[2]    =   0.224143868042;
            h[3]    =  -0.129409522551;
/*
            //normalized coeffs(sum = 2)
            h[0]    =   0.6830127;
            h[1]    =   1.183027;
            h[2]    =   0.31169873;
            h[3]    =  -0.1830127;
             }
 */      }else if(numberOfCoeffs==5){
            h[0] = 1/16;
            h[1] = 1/4;
            h[2] = 3/8;
            h[3] = 1/4;
            h[4] = 1/16;
            
 } else if(numberOfCoeffs == 6){
            if(type == DAUBECHIES){
/*                h[0] = sqrt_2 * (1 + sqrt_10 + sqrt) / 32;
                h[1] = sqrt_2 * (10 - 2*sqrt_10 + 2*sqrt) / 32;
                h[2] = sqrt_2 * (5 + sqrt_10 - 3*sqrt) / 32;
                h[3] = sqrt_2 * (5 + sqrt_10 + 3*sqrt) / 32;
                h[4] = sqrt_2 * (10 - 2*sqrt_10 - 2*sqrt) / 32;
                h[5] = sqrt_2 * (1 + sqrt_10 - sqrt) / 32;
            } else if(type == SYMLETS){
 */
                h[0]    =   0.332670552950;
                h[1]    =   0.806891509311;
                h[2]    =   0.459877502118;
                h[3]    =  -0.135011020010;
                h[4]    =  -0.085441273882;
                h[5]    =   0.035226291885;
            }else if(type == COIFLETS){
                h[0]    =  -0.072732619513;
                h[1]    =   0.337897662458;
                h[2]    =   0.852572020212;
                h[3]    =   0.384864846864;
                h[4]    =   h[0];
                h[5]    =  -0.015655728135;
            }
 } else if(numberOfCoeffs == 8){
            if(type == DAUBECHIES){
                h[0]    =   0.230377813308;
                h[1]    =   0.714846570552;
                h[2]    =   0.630880767929;
                h[3]    =  -0.027983769416;
                h[4]    =  -0.187034811719;
                h[5]    =   0.030841381835;
                h[6]    =   0.032883011666;
                h[7]    =  -0.010597401785;
            } else if(type == SYMLETS){
                h[0]    =   0.032223100604;
                h[1]    =  -0.012603967262;
                h[2]    =  -0.099219543577;
                h[3]    =   0.297857795606;
                h[4]    =   0.803738751807;
                h[5]    =   0.497618667633;
                h[6]    =  -0.029635527646;
                h[7]    =  -0.075765714789;
            }
 } else if(numberOfCoeffs == 10){
            if(type == DAUBECHIES){
                h[0]    =   0.160102397974;
                h[1]    =   0.603829269797;
                h[2]    =   0.724308528437;
                h[3]    =   0.138428145901;
                h[4]    =  -0.242294887066;
                h[5]    =  -0.032244869584;
                h[6]    =   0.077571493840;
                h[7]    =  -0.006241490212;
                h[8]    =  -0.012580751999;
                h[9]    =  0.0033357252854;
            } else if(type == SYMLETS){
                h[0]    =   0.019538882735;
                h[1]    =  -0.021101834025;
                h[2]    =  -0.175328089908;
                h[3]    =   0.016602105765;
                h[4]    =   0.633978963458;
                h[5]    =   0.723407690402;
                h[6]    =   0.199397533977;
                h[7]    =  -0.039134249302;
                h[8]    =   0.029519490926;
                h[9]    =   0.027333068345;
            }
 } else if(numberOfCoeffs == 12){
            if(type == DAUBECHIES){
                h[0]    =   0.111540743350;
                h[1]    =   0.494623890398;
                h[2]    =   0.751133908021;
                h[3]    =   0.315250351709;
                h[4]    =  -0.226264693965;
                h[5]    =  -0.129766867567;
                h[6]    =   0.097501605587;
                h[7]    =   0.027522865530;
                h[8]    =  -0.031582039318;
                h[9]    =   0.000553842201;
                h[10]   =   0.004777257511;
                h[11]   =  -0.001077301085;
            } else if(type == SYMLETS){
                h[0]    =  -0.007800708325;
                h[1]    =   0.001767711864;
                h[2]    =   0.044724901771;
                h[3]    =  -0.021060292512;
                h[4]    =  -0.072637522786;
                h[5]    =   0.337929421728;
                h[6]    =   0.787641141030;
                h[7]    =   0.491055941927;
                h[8]    =  -0.048311742586;
                h[9]    =  -0.117990111148;
                h[10]   =   0.003490712084;
                h[11]   =   0.015404109327;
            } else if(type == COIFLETS){
                h[0]    =   0.016387336464;
                h[1]    =  -0.041464936782;
                h[2]    =  -0.067372554722;
                h[3]    =   0.386110066823;
                h[4]    =   0.812723635450;
                h[5]    =   0.417005184424;
                h[6]    =  -0.076488599079;
                h[7]    =  -0.059434418647;
                h[8]    =   0.023680171946;
                h[9]    =   0.005611434819;
                h[10]   =  -0.001823208871;
                h[11]   =  -0.000720549445;
            }
 }else if(numberOfCoeffs == 14){
            if(type == DAUBECHIES){
                h[0]    =   0.077852054085;
                h[1]    =   0.396539319482;
                h[2]    =   0.729132090846;
                h[3]    =   0.469782287405;
                h[4]    =  -0.143906003929;
                h[5]    =  -0.224036184994;
                h[6]    =   0.071309219267;
                h[7]    =   0.080612609151;
                h[8]    =  -0.038029936935;
                h[9]    =  -0.016574541631;
                h[10]   =   0.012550998556;
                h[11]   =   0.000429577973;
                h[12]   =  -0.001801640704;
                h[13]   =   0.000353713800;
            } else if(type == SYMLETS){
                h[0]    =   0.010268176709;
                h[1]    =   0.004010244872;
                h[2]    =  -0.107808237704;
                h[3]    =  -0.140047240443;
                h[4]    =   0.288629631752;
                h[5]    =   0.767764317003;
                h[6]    =   0.536101917092;
                h[7]    =   0.017441255087;
                h[8]    =  -0.049552834937;
                h[9]    =   0.067892693501;
                h[10]   =   0.030515513166;
                h[11]   =  -0.012636303403;
                h[12]   =  -0.001047384889;
                h[13]   =   0.002681814568;
            }
 }else if(numberOfCoeffs == 18){
            if(type == COIFLETS){
                h[0]    =  -0.003793512864;
                h[1]    =   0.007782596427;
                h[2]    =   0.023452696142;
                h[3]    =  -0.065771911282;
                h[4]    =  -0.061123390003;
                h[5]    =   0.405176902410;
                h[6]    =   0.793777222626;
                h[7]    =   0.428483476378;
                h[8]    =  -0.071799821619;
                h[9]    =  -0.082301927107;
                h[10]   =   0.034555027573;
                h[11]   =   0.015880544864;
                h[12]   =  -0.009007976137;
                h[13]   =  -0.002574517689;
                h[14]   =   0.001117518771;
                h[15]   =   0.000466216960;
                h[16]   =  -0.000070983303;
                h[17]   =  -0.000034599773;

            }
 }
        for(int k = 0; k < numberOfCoeffs; k++){
            g[numberOfCoeffs - 1 - k] = h[k]*Math.pow(-1, k);
        }
        
        ioff = -numberOfCoeffs/2;
        joff = -numberOfCoeffs/2;
    }
    
    
    public double[] transformStep( double a[],int numberOfCoeffs, int n ) {
        
        int nmod  = numberOfCoeffs*n;
        int n1 = n-1;
        int nh = n/2;
        
        if (n >= 4) {
            int ii, i;
            
            double tmp[] = new double[n];
            
            ii = 0;
            
            for (i = 0; i < n; i = i + 2) {
                
                int ni = i + nmod + ioff;
                int nj = i + nmod + joff;
                
                for(int k = 0; k < numberOfCoeffs; k++){
                    
                    int jf = n1&(ni+k);
                    int jr = n1&(nj+k);
                    
                    tmp[ii] = tmp[ii] + h[k]*a[jf];
                    tmp[ii+nh] = tmp[ii+nh] + g[k]*a[jr];
                }
                
                ii++;
            }
            
            for (i = 0; i < n; i++) {
                a[i] = tmp[i];
            }
        }
        
        return a;
    } // transformStep
    
    public double[] invTransformStep( double a[], int numberOfCoeffs, int n ) {
        
        int nmod  = numberOfCoeffs*n;
        int n1 = n-1;
        int nh = n/2;
        
        if (n >= 4) {
            int ii, i;
            
            double tmp[] = new double[n];
            
            ii = 0;
            
            for (i = 0; i < n; i = i + 2) {
                
                double ai = a[ii];
                double ai1 = a[ii+nh];
                
                int ni = i + nmod + ioff;
                int nj = i + nmod + joff;
                
                for(int k = 0; k < numberOfCoeffs; k++){
                    
                    int jf = n1&(ni+k);
                    int jr = n1&(nj+k);
                    
                    tmp[jf] = tmp[jf] + h[k]*ai;
                    tmp[jr] = tmp[jr] + g[k]*ai1;
                }
                
                ii++;
            }
            
            for (i = 0; i < n; i++) {
                a[i] = tmp[i];
            }
            
        }
        return a;
    }
    
    public double[] waveletCoeffs(Spectrum spec, int type, int numberOfCoeffs){
        
        initializeCoeff(type, numberOfCoeffs);
        
        double[] waveletCoeffs = spec.getFluxValues();
        
        final int N = waveletCoeffs.length;
        int n;
        for (n = N; n >= 4; n >>= 1) {
            //for (n = N; n >= 4; n=n/2) {
            waveletCoeffs = transformStep(waveletCoeffs, numberOfCoeffs, n);
        }
        
        return waveletCoeffs;
        
    }
    
    public double[] invWaveletTrans(double[] waveletCoeffs, int type, int numberOfCoeffs){
        
        initializeCoeff(type, numberOfCoeffs);
        
        final int N = waveletCoeffs.length;
        int n;
        
        for (n = 4; n <= N; n <<= 1) {
            waveletCoeffs = invTransformStep(waveletCoeffs, numberOfCoeffs, n);
        }
        
        //at this time waveletCoeffs == already inversed wavelet coeffs
        return waveletCoeffs;
        
    }
    
    
    public Spectrum waveletTransform(Spectrum spec, int type, int numberOfCoeffs){
        
        
        initializeCoeff(type, numberOfCoeffs);
        
        double[] s = spec.getFluxValues();
        
        final int N = s.length;
        int n;
        for (n = N; n >= 4; n >>= 1) {
            //for (n = N; n >= 4; n=n/2) {
            s = transformStep(s, numberOfCoeffs, n);
        }
        
        spec.setFluxValues(s);
        
        return spec;
        
    }
    
    /**
     * Inverse wavelet transform
     */
    public Spectrum invWaveletTrans(Spectrum spec, int type, int numberOfCoeffs){
        
        initializeCoeff(type, numberOfCoeffs);
        
        double[] s = spec.getFluxValues();
        
        final int N = s.length;
        int n;
        for (n = 4; n <= N; n <<= 1) {
            s = invTransformStep(s, numberOfCoeffs, n);
        }
        
        spec.setFluxValues(s);
        
        return spec;
    }
    
    
    public double[] hardThresholding(double[] w, double thresholdPercent){
        
        double universalThreshold = Math.sqrt(2*Math.log(w.length));
        
        for(int k = 0; k < w.length; k++){
            if(Math.abs(w[k]) <= universalThreshold*thresholdPercent/100){
                w[k] = 0.;
            }
        }
        
        return w;
    }
    
    public Spectrum hardThresholding(Spectrum spec, int type, int numberOfCoeff, double thresholdPercent){
        
        Spectrum waveletTransformSpectrum = waveletTransform(spec, type, numberOfCoeff);
        
        System.out.println("waveletTransformSpectrum wavevalues length = "+waveletTransformSpectrum.getWaveValues().length);
        System.out.println("waveletTransformSpectrum fluxvalues length = "+waveletTransformSpectrum.getFluxValues().length);
        
        double[] w = waveletTransformSpectrum.getFluxValues();
        
        //double universalThreshold = Math.sqrt(2*Math.log(w.length))*noiseEstimation;
        double universalThreshold = Math.sqrt(2*Math.log(w.length)/w.length);
        
        double[] w2 = new double[w.length];
        
        //setting equal to zero all wavelet coeffs. le than threshold percent
        for(int k = 0; k < w.length; k++){
            
            double u = universalThreshold*thresholdPercent/100;
            
            //System.out.println("w["+k+"] = "+w[k] + "    universalthreshold = "+u);
            
            if(Math.abs(w[k]) <= universalThreshold*thresholdPercent/100){
                //System.out.println("If sentence");
                
                w2[k] = 0.0;
                
                //w[k] = 0.;
                
            }else{
                w2[k] = w[k];
            }
        }
        
        //waveletTransformSpectrum.setFluxValues(w);
        
        
        //double[] thresholdedWaveValues =
        
        Spectrum spectrum = new Spectrum();
        
        spectrum.setWaveValues(waveletTransformSpectrum.getWaveValues());
        spectrum.setFluxValues(w2);
        
        //System.out.println("w2 length = "+w2.length + "returnSpectrum fluxValues length = "+spectrum.getFluxValues().length);
        
        for(int k = 0; k < w.length; k ++){
            
            // System.out.println("returnSpec fluxValues = "+(spectrum.getFluxValues())[k]+"w2["+k+"] = "+w2[k] );
        }
        
        //Spectrum filteredSpectrum = invDaubTrans(spectrum, numberOfCoeff);
        
        //return filteredSpectrum;
        return spectrum;
    }
    
    public double threshold(double[] waveletCoeff, int thresholdType){
        
        int n = waveletCoeff.length;
        
        double threshold = 0;
        
        /*highFWaveletCoeff == highest frequency wavelet coeffs*/
        double[] highFWaveletCoeff = new double[n/2];
        
        if(thresholdType == UNIVERSAL){
            
            for(int k = n/2; k < n; k++){
                highFWaveletCoeff[k-n/2] = waveletCoeff[k];
            }
            
            //Donoho's threshold
            //threshold = MathUtils.mad(highFWaveletCoeff)/0.6745;
            
            double sigma = MathUtils.mad(highFWaveletCoeff)/0.6745;
            
            threshold = sigma*Math.sqrt(2*Math.log(n));
        }
        
        return threshold;
    }
    
    public double[] thresholding(double[] waveletCoeff, int thresholdingType, int thresholdType, double thresholdPercent){
        
        double threshold = threshold(waveletCoeff, thresholdType);
        double finalThreshold = threshold*thresholdPercent/100;
        
        double[] thresholdedCoeff = new double[waveletCoeff.length];
        
        System.out.println("threshold = "+ finalThreshold);
        System.out.println("=============================");
        
        //setting equal to zero all wavelet coeffs. le than threshold percent
        for(int k = 0; k < waveletCoeff.length; k++){
            
            if(Math.abs(waveletCoeff[k]) <= finalThreshold){
                
                thresholdedCoeff[k] = 0.0;
                
            }else{
                
                if(thresholdingType == HARD){
                    
                    thresholdedCoeff[k] = waveletCoeff[k];
                    
                }//HARD
                else if(thresholdingType == SOFT){
                    //problems with signum method(since java 1.5)
                    //thresholdedCoeff[k] = Math.signum(waveletCoeff[k])*(Math.abs(waveletCoeff[k]) - finalThreshold);
                    if(waveletCoeff[k] < 0 ){
                        thresholdedCoeff[k] = -(Math.abs(waveletCoeff[k]) - finalThreshold);
                    } else{
                        thresholdedCoeff[k] = (Math.abs(waveletCoeff[k]) - finalThreshold);
                    }
                }//SOFT
                
            }//thresholding process
            
        }
        
        return thresholdedCoeff;
        
    }
    
    
    public Spectrum filter(Spectrum inputSpec, int waveletType, int numberOfCoeffs, int thresholdingType, int thresholdType, double thresholdPercent){
        
        Spectrum filteredSpectrum = new Spectrum();
        
        double[] waveletCoeffs = waveletCoeffs(inputSpec, waveletType, numberOfCoeffs);
        double[] thresholdedCoeffs = thresholding(waveletCoeffs, thresholdingType, thresholdType, thresholdPercent);
        double[] inverseWaveletCoeffs = invWaveletTrans(thresholdedCoeffs, waveletType, numberOfCoeffs);
        
        //waveValues are NOT modified
        filteredSpectrum.setWaveValues(inputSpec.getWaveValues());
        filteredSpectrum.setFluxValues(inverseWaveletCoeffs);
        
        return filteredSpectrum;
    }
    
    
    
}


