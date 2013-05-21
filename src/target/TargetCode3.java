package target;



/**
 * Code to be analyzed by Soot for Project-3
 * @see MainSootProject.java
 * @author Christian Adriano
 *
 */
public class TargetCode3{
	
	
	
	public Computer main(String[] args){
		//TargetCode3 code = new TargetCode3();
		//int x=1;
		//int y=3;
		//int z=0;
	
		Computer calc1 = new Computer(1);
		Computer calc2 = new Computer(2);
		Computer calc3;
		Computer calc4;
		//DecrementComputer decCalc = new DecrementComputer(3);
		//IncrementComputer incCalc = new IncrementComputer(4);
		
		//x = calc1.compute(x,y);
		//y = decCalc.compute(x,y);
		//z = incCalc.compute(x, y);
		//z=z+1;
		//calc2 = incCalc;
		
		//incCalc= (IncrementComputer) calc2;
		calc1 =calc2;
		//calc1.computeThis(calc2, 1);
		calc3 = this.calculate(calc1);
				
		//calc1=incCalc; //Calc receives a new variable name.
		//x = x+ calc1.compute(x, y);
		
		//return this.caller(calc, (DecrementComputer) calc ,incCalc);//Issue of explicit cast, otherwise it does not compile.
//		this.caller(calc1,  decCalc ,incCalc);//Issue of explicit cast, otherwise it does not compile.
		return calc1;
	}

	public Computer calculate(Computer c){
		c = c.convert(c);
		return c;
	}
	
/*	public void methodComplex(){
		int x=1;
		//int y=3;
		//int z=0;
	
	
		Computer calc1 = new Computer(1);
		Computer calc2 = new Computer(2);
		//DecrementComputer decCalc = new DecrementComputer(3);
		//IncrementComputer incCalc = new IncrementComputer(4);
		
		//x = calc1.compute(x,y);
		//y = decCalc.compute(x,y);
		//z = incCalc.compute(x, y);
		//z=z+1;
		//calc2 = incCalc;
		
		//incCalc= (IncrementComputer) calc2;
		calc1=calc2.computeThis(1, 1);
		//calc1=incCalc; //Calc receives a new variable name.
		//x = x+ calc1.compute(x, y);
		
		//return this.caller(calc, (DecrementComputer) calc ,incCalc);//Issue of explicit cast, otherwise it does not compile.
//		this.caller(calc1,  decCalc ,incCalc);//Issue of explicit cast, otherwise it does not compile.

	}
*/
	
/**	public int caller(Computer com, DecrementComputer dec, IncrementComputer inc){
		
		int x= com.compute(1, 1);
		int y= dec.compute(1, 2);
		int z= inc.compute(2,2);
		return x+y+z;
	}
	*/
	
}