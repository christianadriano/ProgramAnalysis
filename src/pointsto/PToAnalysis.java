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
	private HashSet<Integer> fvSet= new HashSet<Integer>();
	
	/** Initial set of all freeVariables. <Variable hashcode, which is unique>*/
	private HashMap<Integer,SootMethod> callSitesMap= new HashMap<Integer,SootMethod>();

	/** Keeps track of allocation sites indexed by their hash key*/
	public HashMap<Integer,String> allocationSiteMap = new HashMap<Integer,String>();
	
	/** Keeps track of the constraint graph */
	public ConstraintGraph constraintGraph = new ConstraintGraph();
	
	/** Keeps the results for each free variable and the possible (MAY analysis) allocation sites reaching this variable */
	private PointsToSet pointsToSet = new PointsToSet();
	
	/** It is used to keep the statements of the program in a simple the format (a list 
	 * of Soot Units) */
	private Body body;
	
	public PToAnalysis() {
		//build constraint graph
		
		ConstraintGraphFactory factory = new ConstraintGraphFactory();
		ConstraintGraph graph = factory.buildGraph();
		//this.investigateOutputs();
		//this.initializeFreeVariablesLattice();
	}
	
	public void setBody(Body body){
		this.body = body;
	}
	
	/** Method iterates through the body of a program to find the free variables, 
	 * which are the variables at the left hand side of the statements.
	 * @return array of free variable names as primitive string type.
	 */
	public void initializeFreeVariable_CallSites(){
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
					List<ValueBox> defs = unit.getDefBoxes();
					//It is an object instantiation -------------------------------------------
					if(useValue instanceof soot.jimple.internal.JNewExpr){
					
						for(ValueBox def : defs) {
							Value defValue = def.getValue();
							System.out.println("Def value: " + defValue + ", hashCode="+def.hashCode()+" ,type: " + defValue.getClass());
							System.out.println("put:"+use.hashCode()+" className:"+useValue.toString());
							this.allocationSiteMap.put(new Integer(use.hashCode()), useValue.toString());
						}
					}
					
					//It is a method invocation ------------------------------------------------
					else if (useValue instanceof soot.jimple.internal.JVirtualInvokeExpr){
						//Obtain variable and method name
						Integer key = new Integer(((soot.jimple.internal.JVirtualInvokeExpr) useValue).getBaseBox().hashCode());
						String className = this.allocationSiteMap.get(key);
						SootMethod method = ((soot.jimple.internal.JVirtualInvokeExpr) useValue).getMethod();
						this.callSitesMap.put(key,method);
						
						System.out.println("baseBox: "+((soot.jimple.internal.JVirtualInvokeExpr) useValue).getBaseBox());  //Basebox is the object reference on which the method is being called.
						System.out.println("hashCode instance: "+key+ "className: "+className);  //Basebox is the object reference on which the method is being called.
						System.out.println("method: "+method);
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
	
	
	
	
	/** Perform the analysis and print the output in the console*/
	public void run(){
		this.initializeFreeVariable_CallSites();
	}
	


	
	/** Traverses the graph from the root (Allocation Site) to the leaves. 
	 * At each node proceeds an Union operation
	 * @param graph
	 */
	public void performReachabilityAnalysis(Integer key, String className){
		System.out.println("iter key="+key+" className: "+className);
		this.pointsToSet.addNode(key, className); //add the class name to the points to set of key	
		if(this.constraintGraph.nodesMap.containsKey(key)){
			Iterator<Integer> iterSucc = this.constraintGraph.nodesMap.get(key).iterator();
			//Propagates the className for all successor nodes.
			while(iterSucc.hasNext()){
				Integer succKey = iterSucc.next();
				performReachabilityAnalysis(succKey,className);
			}
		}
	}
	
	
	public Iterator<Unit> getUnits(){
		SootMethod m = (SootMethod)body.getMethod();
		
		if(m.isConcrete()){
			JimpleBody jbody = (JimpleBody) m.retrieveActiveBody();
			Iterator<Unit> unitIter = jbody.getUnits().iterator();
			return unitIter;
		}
		else
			return null;
	}
	
	public void printResults(){
		System.out.println("Points to Set ------------- ");
		System.out.println(this.pointsToSet);
	}
}
