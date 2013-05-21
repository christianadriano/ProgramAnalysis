package pointsto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.Local;
import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.InvokeExpr;
import soot.jimple.JimpleBody;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JReturnVoidStmt;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;
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
	}
	
	public void setBody(Body body){
		this.body = body;
	}
	
	/** Perform the analysis and print the output in the console*/
	public void run(){
		//this.initializeInstanceVariables_CallSites();
		buildConstraintEdges();
//		this.investigateReturnStmt();
	}
	
	/** Method iterates through the body of a program to find the free variables, 
	 * which are the variables at the left hand side of the statements.
	 * @return array of free variable names as primitive string type.
	 */
	public void initializeInstanceVariables_CallSites(){
		fvSet= new HashSet<Integer>();
		Iterator<Unit> unitIter=getUnits();
		while(unitIter.hasNext()){
			Unit unit = (Unit) unitIter.next();
			System.out.println("--------------------------");
			System.out.println("unit: "+ unit);
			System.out.println("unit class: "+ unit.getClass().getName());
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
							String className = useValue.toString().replace("return", "");
							System.out.println("put className:"+className+ " key:"+use.hashCode());
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
						System.out.println("put method:"+method+" key:"+key);
						//System.out.println("baseBox: "+((soot.jimple.internal.JVirtualInvokeExpr) useValue).getBaseBox());  //Basebox is the object reference on which the method is being called.
						//System.out.println("hashCode instance: "+key+ "className: "+className);  //Basebox is the object reference on which the method is being called.
						//System.out.println("method: ---------------"+method);
						
						
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
	
	
	public void investigateReturnStmt(){
		PatchingChain<Unit> units = this.body.getUnits();
		Iterator stmtIt = units.snapshotIterator();

		while(stmtIt.hasNext()){
			Stmt stmt = (Stmt)stmtIt.next();
			System.out.println("Stmt:" + stmt);
			System.out.println("Stmt class: ------------ "+ stmt.getClass().getName());

			//if((stmt instanceof soot.jimple.internal.JReturnStmt) || (unit instanceof soot.jimple.internal.JRetStmt)|| (unit instanceof soot.jimple.internal.JReturnVoidStmt)){
			
			if (stmt instanceof JReturnStmt){
				List<ValueBox> values = ((JReturnStmt)stmt).getOp().getUseBoxes();
				Value value = values.get(0).getValue();
				System.out.println("JReturnStmt, value="+ value);
			}
			else{ 
				if(stmt instanceof JReturnVoidStmt) {
				
				System.out.println("JReturnVoidStmt, stmt="+ stmt);
				}
			}
		}
	}
	
	
	/** Traverse the units produced by Soot and for each instance variable assignment, create a constraint edge to be
	 * stored at the ConstraintGraph
	 */
	public void buildConstraintEdges(){
		Iterator<Unit> unitIter=getUnits();
		while(unitIter.hasNext()){
			Unit unit = (Unit) unitIter.next();

			if(unit!=null){
				System.out.println("--------------------------");
				System.out.println("unit: "+ unit);
				System.out.println("unit class: "+ unit.getClass().getName());
				List<ValueBox> uses = unit.getUseBoxes();

				if(unit instanceof soot.jimple.internal.JAssignStmt){
					List<ValueBox> defs = unit.getDefBoxes();
					Value defValue = defs.get(0).getValue(); //Have only one def
					System.out.println("defValue: "+ defValue+ ", Class"+defValue.getClass().getName());

					//----------------------  So it is an assignment to an instance variable. -------------
					if((defValue instanceof soot.jimple.internal.JimpleLocal)){ 

						//------------------ So it is a simple assignment or a new Instance from one variable to another -------------------
						if(uses.size()==1){
							Value useValue = uses.get(0).getValue();
							if((useValue instanceof soot.jimple.internal.JNewExpr) || (useValue instanceof soot.jimple.internal.JimpleLocal)){
								Integer successor = new Integer(defValue.hashCode());
								Integer source = new Integer(useValue.hashCode());
								this.constraintGraph.addSuccessor(source, successor);
								System.out.println("put source:"+source+"successor: "+ successor);
							}
							else
								System.out.println("Assignment situation not predicted:"+useValue.getClass().getName() +" Ignoring....");
						}
						else
							//----------------------  It is a method invocation, so there is  return to be treated. -------------
							if((uses.size()>1)&&
									((JAssignStmt) unit).getRightOp() instanceof JVirtualInvokeExpr){
								System.out.println("Return statement not resolved"+((JAssignStmt) unit).getRightOp());
								Integer successor = new Integer(defValue.hashCode()); //The def Value of the caller will be successor
								//Now we have to obtain the name to the internal variable returned by the method.
								Value value = ((JAssignStmt) unit).getRightOp();
								SootMethod method = ((soot.jimple.internal.JVirtualInvokeExpr) value).getMethod();
								Integer source = this.obtainHashCodeReturnFromMethod(method);
								this.constraintGraph.addSuccessor(source, successor);
								InvokeExpr stmt = ((JAssignStmt) unit).getInvokeExpr();
								
								//Now we have to link the parameters and the internal variables of the method.
								
								linkParametersCallerAndCallee(stmt,method);
							}
					}
				}
				
				else
					if((unit instanceof JInvokeStmt)){
						System.out.println("JVirtualInvokeStmt---------------------");
						InvokeExpr stmt = ((JInvokeStmt) unit).getInvokeExpr();
						SootMethod method = stmt.getMethod();
						linkParametersCallerAndCallee(stmt,method);
					}
				}
			}
		
	}
	
	
	protected void linkParametersCallerAndCallee(InvokeExpr stmt, SootMethod method){
		List<ValueBox> invocationList = stmt.getUseBoxes();
		int j=1;
		int i=0;
		while(j<invocationList.size() && i<method.getParameterCount()){
			Value value= ((ValueBox)invocationList.get(j)).getValue();
			System.out.println("------ Local in invokeStmt: "+ value+" class: "+value.getClass().getName());
			j++;
			Local local = method.getActiveBody().getParameterLocal(i);
			System.out.println("------ Local in method="+local.getName()+" class:"+local.getClass().getName());
			i++;
			
			//Only create edge if the parameter is a local instance (i.e., ignore constants)
			if(value instanceof JimpleLocal){
				Integer source = new Integer(value.hashCode());
				Integer successor = new Integer(local.hashCode());
			
				this.constraintGraph.addSuccessor(source, successor);
			}
		}
		
	}
	
	protected Integer obtainHashCodeReturnFromMethod(SootMethod method){
		Integer source=null;
		Iterator<Unit> iterMB = method.getActiveBody().getUnits().iterator();
		while(iterMB.hasNext()){
			Unit unitMB= iterMB.next();
			if(unitMB instanceof JReturnStmt){
				Value value = ((JReturnStmt)unitMB).getOp();
				System.out.println("------ return value: "+ value.hashCode()+" class: "+unitMB.getClass().getName());
				source = new Integer(value.hashCode());
				return source;
			}
		}
		return source;
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
