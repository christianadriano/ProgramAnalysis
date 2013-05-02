import constantpropagation.MainSootProject2;


/**
 * Code to be analyzed by Soot for Project-2 
 * @see MainSootProject2.java
 * @author Christian Adriano
 *
 */
public class TargetCode2 {

	
	/** Simple method to be used as call site
	 * @return always 1
	 * */
	/*public int subFunction(){
		return 1;
	}
	*/
	
	public void compute() {
				
		//int z = 3; 
		int x = 1; 
		int y = 4;
		if (x == 1)
			y = 7; 
		else 
			y = x + 4; 
		while (x > 0) {
			x=x-1;
		}
		//z= subFunction();
		x = 3; 
		//System.out.println("final values of [x,y,z] = " +x+","+y+","+z);
	}

}