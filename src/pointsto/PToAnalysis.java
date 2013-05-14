package pointsto;

/** 
 *  * Sets up and runs the points to analysis
 * 
 * Basic Algorithm:
 * 1- Construct nodes and edges for top-level variables,
 * statements, and expressions (e.g., main)
 * 2- Propagate classes through flow graph starting with main 
 * and top-level new expressions
 * 3- When call encountered, add edge to target and construct 
 * flow graph for target method (if not already done)
 * 4- If method not reachable, it will be pruned
 * 
 * @author Christian Adriano
 *
 */
public class PToAnalysis {

	public PToAnalysis() {
		//build constraint graph
		ConstraintGraphFactory factory = new ConstraintGraphFactory();
		ConstraintGraph graph = factory.buildGraph();
	}
}
