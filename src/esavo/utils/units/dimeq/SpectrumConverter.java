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
package esavo.utils.units.dimeq;

//Import from java classes

//Import from esavo packages
import esavo.utils.units.parser.*;


/**
 *
 * General class to convert spectral points to different units using dimensional
 * analysis. The mother class GeneralConverter is flexible to construct other kind of
 * extensions/conversion types, but to make conversions in a Spectral displayer, this class should be
 * enough.
 * <p> 
 * The only steps to be followed for an application should be:
 * <p>
 * 1. Construct a Spectrum converter object, setting the dimensional equations for the
 * 	wave and flux original and final units, e.g.:
 * <p>
 * 	SpectrumConverter sc = new SpectrumConverter(1.,"T-1",1.E-26,"MT-2",1.E-6,"L",1.E10,"ML-1T-3");
 * <p>
 * where:
 * <ul> 
 * <li>		wave original units are: 	<pre>1. 	T-1 	(Hz)</pre>
 * <li>		flux original units are: 	<pre>1.E-26 	MT-2 	(Jy)</pre>
 * <li>		wave final units are: 		<pre>1.E-6 	L 	(um)</pre>
 * <li>		flux final units are: 		<pre>1.E10 	ML-1T-3 (W/m^2/um)</pre>
 * </ul>
 *
 *
 * 2. Call the getConvertedPoint method:
 * <pre>
 *	double[] value = sc.getConvertedPoint(double wave,double flux);
 * </pre>
 *
 * 	the double array output contains the spectral point in the new units, where:
 *
 * <pre>
 * 	value[0] = convertedWave;
 * 	value[1] = convertedFlux;
 * </pre>
 *
 *
 * See reference: 
 * <p>
 * <b>"Dimensional Analysis applied to Spectrum Handling in Virtual Observatory Context"</b>
 * Pedro Osuna, Jesus Salgado	
 *
 * @author J. Salgado (ESAC/ESA)
 *
 * @version 1.0
 *
 **/


public class SpectrumConverter extends GeneralConverter {

			
	/**
   	* Void Constructor. Constructs a SpectrumConverter object. Even when this constructor can be invoked, 
	* the complex constructor is recommended. 
     	*
	* @see #SpectrumConverter(double,String,double,String,double,String,double,String)
   	*/
	
	public SpectrumConverter() {
		
		super();
			
		Constant c = new Constant(new Unit(299792458.,"LT-1"));
		Constant h = new Constant(new Unit(6.626068E-34,"ML2T-1"));	
		
		addQuantity("c",c);
		addQuantity("h",h);
	}
	

	/**
	* Constructs a SpectrumConverter object. After invoke the void constructor, the setWaveInitialUnits and setFluxInitialUnits is called.
	* The conversion system is solved afterwards.
	* Constructor recommended
   	*
	* @param waveScalingInitial 	Scaling value for the wave's initial unit dimensional equation
	* @param waveDimeqInitial 	Dimensional equation for the wave's initial unit
	* @param fluxScalingInitial 	Scaling value for the flux's initial unit dimensional equation
	* @param fluxDimeqInitial 	Dimensional equation for the flux's initial unit
	* @param waveScalingFinal 	Scaling value for the wave's final unit dimensional equation
	* @param waveDimeqFinal 	Dimensional equation for the wave's final unit
	* @param fluxScalingFinal 	Scaling value for the flux's final unit dimensional equation
	* @param fluxDimeqFinal 	Dimensional equation for the flux's final unit
   	*
	*
   	*/
	
	public SpectrumConverter(double waveScalingInitial, String waveDimeqInitial, double fluxScalingInitial, String fluxDimeqInitial, double waveScalingFinal, String waveDimeqFinal, double fluxScalingFinal, String fluxDimeqFinal) {
		
		this();
		
		setWaveInitialUnits(waveScalingInitial, waveDimeqInitial, waveScalingFinal, waveDimeqFinal);
		setFluxInitialUnits(fluxScalingInitial, fluxDimeqInitial, fluxScalingFinal, fluxDimeqFinal);
		
		solve();
	}


	/**
	* Constructs a SpectrumConverter object. The scaling factors and the dimensional equations will be extracted from the
	* unit strings using the ESAVO units parser. This units parser package covers most of the units string standards, but we
	* recommend the use of the precedent constructor using scaling/dimeq objective data, as the correct parsing for all the
	* units conventions cannot be guaranteed 
   	*
	* @param waveInitialUnit 	Units string for the initial wave value
	* @param fluxInitialUnit 	Units string for the initial flux value
	* @param waveFinalUnit 		Units string for the final wave value
	* @param fluxFinalUnit 		Units string for the final flux value
   	*
	* @see #SpectrumConverter(double,String,double,String,double,String,double,String)
    	*/

	public SpectrumConverter(String waveInitialUnit, String fluxInitialUnit, String waveFinalUnit, String fluxFinalUnit) {
		
		this();

		UnitEquation equation;
		UnitEquationFactory factory 	= new UnitEquationFactory();
			
		equation 			= (UnitEquation) factory.resolveEquation(waveInitialUnit);
		double waveScalingInitial	= (new Double(equation.getScaleEq())).doubleValue();
		String waveDimeqInitial 	= equation.getDimeEq();
			
		equation 			= (UnitEquation) factory.resolveEquation(waveFinalUnit);
		double waveScalingFinal		= (new Double(equation.getScaleEq())).doubleValue();
		String waveDimeqFinal 		= equation.getDimeEq();
						
		equation 			= (UnitEquation) factory.resolveEquation(fluxInitialUnit);
		double fluxScalingInitial	= (new Double(equation.getScaleEq())).doubleValue();
		String fluxDimeqInitial 	= equation.getDimeEq();
			
		equation 			= (UnitEquation) factory.resolveEquation(fluxFinalUnit);
		double fluxScalingFinal		= (new Double(equation.getScaleEq())).doubleValue();
		String fluxDimeqFinal 		= equation.getDimeEq();
		       
		setWaveInitialUnits(waveScalingInitial, waveDimeqInitial, waveScalingFinal, waveDimeqFinal);
		setFluxInitialUnits(fluxScalingInitial, fluxDimeqInitial, fluxScalingFinal, fluxDimeqFinal);
		
		solve();
	}


	/**
   	* Convert the a spectral point in the original units to the final units
   	*
	* @param waveValue Wave value in the original units
	* @param fluxValue Flux value in the original units
   	*
   	* @return A double array containing the wave in value [0] and the flux in value [1] in the final units
	*
   	*/
	public double[] getConvertedPoint(double waveValue, double fluxValue) {
			
		setInitialValue("SpectralCoordinate", waveValue);
	 	setInitialValue("FluxCoordinate", fluxValue);
				
		double[] convertedPoint = new double[2];
		convertedPoint[0] 	= getConvertedValue("SpectralCoordinate"); 
		convertedPoint[1] 	= getConvertedValue("FluxCoordinate"); 
		
		return convertedPoint;
	}


	/**
   	* Set externally the initial and final wave units in case the void constructor was used.
   	*
	* @param waveScalingInitial 	Scaling value for the wave's initial unit dimensional equation
	* @param waveDimeqInitial 	Dimensional equation for the wave's initial unit
	* @param waveScalingFinal 	Scaling value for the wave's final unit dimensional equation
	* @param waveDimeqFinal 	Dimensional equation for the wave's final unit
   	*
	*
   	*/
	public void setWaveInitialUnits(double waveScalingInitial, String waveDimeqInitial,double waveScalingFinal, String waveDimeqFinal) {

		Unit waveInitialUnit 	= new Unit(waveScalingInitial,waveDimeqInitial);
		Unit waveFinalUnit 	= new Unit(waveScalingFinal,waveDimeqFinal);

		Variable wave 		= new Variable(waveInitialUnit, waveFinalUnit);

		addQuantity("SpectralCoordinate",wave,true);			
	}	


	/**
  	* Set externally the initial and final flux units in case the void constructor was used.
    	*
	* @param fluxScalingInitial 	Scaling value for the flux's initial unit dimensional equation
	* @param fluxDimeqInitial 	Dimensional equation for the flux's initial unit
	* @param fluxScalingFinal 	Scaling value for the flux's final unit dimensional equation
	* @param fluxDimeqFinal 	Dimensional equation for the flux's final unit
   	*
	*
   	*/
	public void setFluxInitialUnits(double fluxScalingInitial, String fluxDimeqInitial,double fluxScalingFinal, String fluxDimeqFinal) {

		Unit fluxInitialUnit 	= new Unit(fluxScalingInitial,fluxDimeqInitial);
		Unit fluxFinalUnit 	= new Unit(fluxScalingFinal,fluxDimeqFinal);

		Variable flux 		= new Variable(fluxInitialUnit, fluxFinalUnit);

		addQuantity("FluxCoordinate",flux,false);			
	}	

        
/*
	
	public static void main(String[] args) {
	
		double waveScalingInitial	= 1.E-10;
		String waveDimeqInitial 	= "L";
		double fluxScalingInitial	= 1.E-26;
		String fluxDimeqInitial		= "MT-2";
		double waveScalingFinal 	= 1.;
		String waveDimeqFinal		= "T-1";
		double fluxScalingFinal		= 1.E7;
		String fluxDimeqFinal		= "ML-1T-3";	
	
		
		if(args.length > 3){
		
			UnitEquation equation;
			UnitEquationFactory factory 	= new UnitEquationFactory();
			
			equation 			= (UnitEquation) factory.resolveEquation(args[0].trim());
			waveScalingInitial		= (new Double(equation.getScaleEq())).doubleValue();
			waveDimeqInitial 		= equation.getDimeEq();
			System.out.println(args[0] + " = " + waveScalingInitial + " " + waveDimeqInitial);
			
			equation 			= (UnitEquation) factory.resolveEquation(args[1].trim());
			fluxScalingInitial		= (new Double(equation.getScaleEq())).doubleValue();
			fluxDimeqInitial 		= equation.getDimeEq();
			System.out.println(args[1] + " = " + fluxScalingInitial + " " + fluxDimeqInitial);
						
			equation 			= (UnitEquation) factory.resolveEquation(args[2].trim());
			waveScalingFinal		= (new Double(equation.getScaleEq())).doubleValue();
			waveDimeqFinal 			= equation.getDimeEq();
			System.out.println(args[2] + " = " + waveScalingFinal + " " + waveDimeqFinal);
			
			equation 			= (UnitEquation) factory.resolveEquation(args[3].trim());
			fluxScalingFinal		= (new Double(equation.getScaleEq())).doubleValue();
			fluxDimeqFinal 			= equation.getDimeEq();
			System.out.println(args[3] + " = " + fluxScalingFinal + " " + fluxDimeqFinal);
		}
			
		SpectrumConverter sc;
		sc = new SpectrumConverter(	waveScalingInitial,waveDimeqInitial,
						fluxScalingInitial,fluxDimeqInitial,
						waveScalingFinal,waveDimeqFinal,
						fluxScalingFinal,fluxDimeqFinal);
		
		
		System.out.println(sc.toString());
		
		double [] value;
		value = sc.getConvertedPoint(1.,1.);
		
		if(args.length > 3){
			System.out.println(1. + " " + args[0] + " = " + 
					value[0] + " " + sc.toLongString("SpectralCoordinate") + " " + args[2]);
			System.out.println(1. + " " + args[1] + " = " + 
					value[1] + " " + sc.toLongString("FluxCoordinate") + " " + args[3]);
		} else {
			System.out.println(1. + " " + waveScalingInitial + "*" + waveDimeqInitial + " = " + 
					value[0] + sc.toLongString("SpectralCoordinate") + " " + waveScalingFinal + "*" + waveDimeqFinal);
			System.out.println(1.  + " " + fluxScalingInitial + "*" + fluxDimeqInitial + " = " + 
					value[1] + sc.toLongString("FluxCoordinate") + " " + fluxScalingFinal + "*" + fluxDimeqFinal);		
		
		}
	}

        */
	
	
	
}	
