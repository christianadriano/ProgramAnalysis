package pointsto;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Builds a constraint graph for the following three situations
 * 
 * (1) A a = new A() => {new A()} ⊑ pts(a)
 * (2) a = b => pts(b) ⊑ pts(a)
 * (3) r = a.f(b), where f`s definition is
 * O f(B p) { ...; return v; } => pts(a) ⊑ pts(this) ∧ pts(b) ⊑ pts(p) ∧ pts(v) ⊑ pts(r)
 * 
 * @see pointsto.ContraintGraph
 */
public class ConstraintGraphFactory {

	
	
	public ConstraintGraph graph;
	
	/** Keeps track of allocation sites indexed by their hash key*/
	public HashMap<Integer,String> allocationSiteMap = new HashMap<Integer,String>(); 
		
	public ConstraintGraph buildGraph(){
		
		//Initialize allocation sites as roots
	
		
		//Obtain the assignment from one instance to the other
		
		//Obtain the method invocation
		
		//Obtain the instance returned by the method
		
		//Obtain the mapping between arguments and the variables inside method.
		
		return null;
	}
	
	public void setup(){
		Integer allocationSite1 = new Integer(1);
		Integer a1 = new Integer(2);
		Integer allocationSite2 = new Integer(3);
		Integer b1 = new Integer(4);
		
		graph = new ConstraintGraph();
		
		graph.addSuccessor(allocationSite1, a1);
		graph.addSuccessor(a1,b1);
		graph.addSuccessor(allocationSite2, b1);
		
	
		this.allocationSiteMap.put(allocationSite1,"target.Computer");
		this.allocationSiteMap.put(allocationSite2,"target.IncrementComputer");
		
	}
	
	
}
