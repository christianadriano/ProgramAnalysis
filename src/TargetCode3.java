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
		
		calc3=calc1.computeThis(calc1, 1);
		if(calc3.getInit()<0){
			calc3=calc2.computeThis(calc2, 2);
		}
		calc4=calc3.computeThis(calc1, 3);
		calc4 = calc2.convert(calc2);
		calc5 = caller(calc2, (DecrementComputer) calc2 ,(IncrementComputer) calc2);//Issue of explicit cast, otherwise it does not compile.
		calc6 = copy(calc5); 
		
		
		Computer calc7 = new Computer(calc6.init);
		
		return calc7;
	}

	public Computer copy(Computer c){
		int k = c.getInit();
		Computer c6= new DecrementComputer(k+1);
		return c6;
	}
	

	
	public Computer caller(Computer com, DecrementComputer dec, IncrementComputer inc){
		int i = com.getInit();
		int j = dec.getInit();
		int k = inc.getInit();
		Computer C9 = new Computer(i + j + k);
		return C9;
	}
		
}