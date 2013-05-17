package pointsto;

public class ConstraintGraphFactory {

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
	public ConstraintGraphFactory() {
		
	}

	
	public ConstraintGraph buildGraph(){
		
		//Initialize allocation sites as roots
		
		//Obtain the assignment from one instance to the other
		
		//Obtain the method invocation
		
		//Obtain the instance returned by the method
		
		//Obtain the mapping between arguments and the variables inside method.
		
		return null;
	}
	
}
