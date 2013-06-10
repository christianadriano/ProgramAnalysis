import target.Computer;
import target.DecrementComputer;
import target.IncrementComputer;


/**
 * Code to be analyzed by Soot for Project-4 (1-CFA)
 * 
 * The code below creates a situation in which a method is called 
 * many times and in a way that the 0-CFA analysis cannot decided precisely the 
 * callsites involved.
 *  
 * @see MainSootProject.java
 * @author Christian Adriano
 *
 */
public class TargetCode4{
	
	
	
	public Computer main(String[] args){
	
		Computer calc1 = new Computer(1);
		Computer calc2 = new IncrementComputer(2);
		Computer calc5 = new DecrementComputer(5);
		Computer calc3, calc4=null;
		
		calc3=calc1.computeThis(calc1, 1);
		if(calc3.getInit()<0){
			calc3=calc2.computeThis(calc2, 2);
		}
		calc4=calc3.computeThis(calc5, 3);
		calc5=calc4.computeThis(calc1, 4);
		
		return calc5;
	}
}