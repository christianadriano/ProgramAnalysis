import constantpropagation.MainSootProject2;


/**
 * Code to be analyzed by Soot for Project-2 
 * @see MainSootProject2.java
 * @author Christian Adriano
 *
 */
public class TargetCode2 {
	
	public int method(){
		int x=1;
		int y=3;
		while(x>0){
			x=1+y;
		}
		if(x==0)
			x=y+1/2;
		else 
			y=y*3;
		x=x+y-1;
		x = 5 * Integer.bitCount(y);
		return x;
	}

}