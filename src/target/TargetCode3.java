package target;



/**
 * Code to be analyzed by Soot for Project-3
 * @see MainSootProject.java
 * @author Christian Adriano
 *
 */
public class TargetCode3{
	
	
	
	public static void main(){
		TargetCode3 code = new TargetCode3();
		code.methodSimple();
		code.methodComplex();
	}
	
	public int caller(Computer com, DecrementComputer dec, IncrementComputer inc){
		int x= com.compute(1, 1);
		int y= dec.compute(1, 2);
		int z= inc.compute(2,2);
		return x+y+z;
	}
	
	public int methodSimple(){
		int x=1;
		int y=3;
		int z=0;
		Computer calc = new Computer();
		DecrementComputer decCalc = new DecrementComputer();
		IncrementComputer incCalc = new IncrementComputer();
		
		x = calc.compute(x,y);
		y = decCalc.compute(x,y);
		z = incCalc.compute(x, y);
		
		
		caller(calc,decCalc,incCalc);

	
		return x;
	}

	
	public int methodComplex(){
		int x=1;
		int y=3;
		int z=0;
		Computer calc = new Computer();
		DecrementComputer decCalc = new DecrementComputer();
		IncrementComputer incCalc = new IncrementComputer();
		
		x = calc.compute(x,y);
		y = decCalc.compute(x,y);
		z = incCalc.compute(x, y);
		
		calc=decCalc;
		caller(incCalc, decCalc,incCalc);

	
		return x;
	}

	
}