import constantpropagation.MainSootProject2;


/**
 * Code to be analyzed by Soot for Project-2 
 * @see MainSootProject2.java
 * @author Christian Adriano
 *
 */
public class TargetCode2 {

	
	
	public void compute() {
			
		int x = 1; 
		int y = 4*x;
		if (x == 1){
			y = 7; 
			x = 2;
		}
		else 
			y = x + 4; 
		while (x > 0) {
			x=x-1;
		}
		x = 1+Integer.compare(1, 2);
		return;
	}

}