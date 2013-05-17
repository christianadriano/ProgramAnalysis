package pointsto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.JimpleBody;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JNewExpr;
import soot.toolkits.graph.BlockGraph;

import constpropag.Lattice;


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

	
	/** Initial set of all freeVariables. <Variable hashcode, which is unique>*/
	HashSet<Integer> fvSet= new HashSet<Integer>();
	
	/** Initial set of all freeVariables. <Variable hashcode, which is unique>*/
	HashSet<Integer> callSitesSet= new HashSet<Integer>();
	
	/** Keeps the results for each function call statement */
	HashMap<String,Lattice> analysisMap = new HashMap<String,Lattice>();
	
	/** Keeps the list of labels corresponding to the statements in order of occurence so 
	 * we can display the results in the same order they appear in the source code listing */
	ArrayList<String> analysisList = new ArrayList<String>();
	
	/**Blocks are the entities in Soot that effectively keep track of succession and 
	 * predecession (both at block as well as at unit level*/ 
	BlockGraph graph; //this is initialized by the constructor
	
	/** It is used to keep the statements of the program in a simple the format (a list 
	 * of Soot Units) */
	private Body body;
	
	public PToAnalysis(Body body) {
		//build constraint graph
		this.body = body;
		ConstraintGraphFactory factory = new ConstraintGraphFactory();
		ConstraintGraph graph = factory.buildGraph();
		this.initializeFreeVariablesLattice();
	}
	
	
	
	/** Method iterates through the body of a program to find the free variables, 
	 * which are the variables at the left hand side of the statements.
	 * @return array of free variable names as primitive string type.
	 */
	public void initializeFreeVariablesLattice(){
		fvSet= new HashSet<Integer>();
		Iterator<Unit> unitIter=getUnits();
		while(unitIter.hasNext()){
			Unit unit = (Unit) unitIter.next();
			
			if(unit!=null){
				List<ValueBox> uses = unit.getUseBoxes();
				//System.out.println("uses: "+uses);
				for(ValueBox use : uses) {
					Value useValue = use.getValue();
					System.out.println("Use value: " + useValue + ", hashCode="+use.hashCode()+" ,type: " + useValue.getClass());
					//It is an object instantiation -------------------------------------------
					if(useValue instanceof soot.jimple.internal.JNewExpr){
						List<ValueBox> defs = unit.getDefBoxes();
						for(ValueBox def : defs) {
							Value defValue = def.getValue();
							System.out.println("Def value: " + defValue + ", hashCode="+def.hashCode()+" ,type: " + defValue.getClass());
							//Node node = new Node(use.hashCode());
							//PointsToSet set = new PointsToSet(defValue.hashCode());
							//Edge edge = new Edge(use.hashCode(),set));
							fvSet.add(new Integer(use.hashCode())); //FreeVariable must have a unique entry in the map
							//edgeList.add(edge,edge);
						}
					}
					
					//It is a method invocation ------------------------------------------------
					else if (useValue instanceof soot.jimple.internal.JVirtualInvokeExpr){
						//Obtain variable and method name
						System.out.println("method: "+((soot.jimple.internal.JVirtualInvokeExpr) useValue).getMethod());
						System.out.println("baseBox: "+((soot.jimple.internal.JVirtualInvokeExpr) useValue).getBaseBox());  //Basebox is the object reference on which the method is being called.
					}
				}
			}
			
			//if(unit instanceof soot.jimple.internal.JAssignStmt){
			//	fvList.put(((JAssignStmt)unit).leftBox.getValue().toString(),new PointsToSet());
			//	System.out.println("Unit: "+ unit); 
			//}
			//ELSE System.out.println("Non Assignment Statement: "+unit);
		}
	}
	
	
	
	protected Iterator<Unit> getUnits(){
		SootMethod m = (SootMethod)body.getMethod();
		if(m.isConcrete()){
			JimpleBody jbody = (JimpleBody) m.retrieveActiveBody();
			Iterator<Unit> unitIter = jbody.getUnits().iterator();
			return unitIter;
		}
		else
			return null;
	}
}
