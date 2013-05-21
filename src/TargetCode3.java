

import target.Computer;
import target.DecrementComputer;
import target.IncrementComputer;



/**
 * Code to be analyzed by Soot for Project-3
 * @see MainSootProject.java
 * @author Christian Adriano
 *
 */
public class TargetCode3{
	
	
	
	public Computer main(String[] args){
	
		Computer calc1 = new Computer(1);
		Computer calc2 = new IncrementComputer(2);
		Computer calc5 = new DecrementComputer(5);
		Computer calc3, calc4, calc6;
		
		calc1 =calc2;
		calc3=calc1.computeThis(calc1, 1);
		calc4 = calc2.convert(calc2);
		calc5 = caller(calc2, (DecrementComputer) calc2 ,(IncrementComputer) calc2);//Issue of explicit cast, otherwise it does not compile.
		calc6 = copy(calc5); 
		
		Computer calc7 = new Computer(calc6.init);
		
		return calc7;
	}

	public Computer copy(Computer c){
		Computer c6= new DecrementComputer(c.init+1);
		return c6;
	}
	

	
	public Computer caller(Computer com, DecrementComputer dec, IncrementComputer inc){
		Computer C9 = new Computer(com.init + dec.init + inc.init);
		return C9;
	}
		
}