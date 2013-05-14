package pointsto;

import java.util.HashSet;

public class Edge {

	public HashSet<String> constraintSet;
	
	public Edge(){
		this.constraintSet = new HashSet<String>();
	}
	
	
	public void addConstraint(String constraint){
		this.constraintSet.add(constraint);
	}
}
