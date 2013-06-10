package pointsto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
	private HashMap<Integer,HashSet<InvokeExpr>> callSitesMap= new HashMap<Integer,HashSet<InvokeExpr>>();

	/** Keeps track of allocation sites indexed by their hash key*/
	public HashMap<Integer,String> allocationSiteMap = new HashMap<Integer,String>();

	/** Keeps track of the constraint graph */
	public ConstraintGraph constraintGraph = new ConstraintGraph();

	/** Keeps the results for each free variable and the possible (MAY analysis) allocation sites reaching this variable */
	private PointsToSet pointsToSet = new PointsToSet();

	/** It is used to keep the statements of the program in a simple the format (a list 
	 * of Soot Units) */
	private Body body;

	/** Maps the copies of keys used to arguments and parameters in method calls. 
	 * The idea is to the new links between these two groups everytime a method is called. 
	 * So, the solution is to have new parameters references, but keep track of the original ones. 
	 */
	private NodeCopiesMap nodeCopiesMap = new NodeCopiesMap();

	public PToAnalysis() {
		//build constraint graph
		//ConstraintGraphFactory factory = new ConstraintGraphFactory();
		//ConstraintGraph graph = factory.buildGraph();
	}

	public void setBody(Body body){
		this.body = body;
	}

	/** Perform the analysis and print the output in the console*/
	public void run(){
		
		this.initializeInstanceVariables_CallSites();
		this.buildConstraintEdges();
		System.out.println();
		System.out.println("allocationSiteMap.size: "+allocationSiteMap.size());
		System.out.println("constraintMap.size: "+this.constraintGraph.nodesMap.size());

	}

	/** Method iterates through the body of a program to find the free variables, 
	 * which are the variables at the left hand side of the statements.
	 * @return array of free variable names as primitive string type.
	 */
	public void initializeInstanceVariables_CallSites(){
		System.out.println();
		System.out.println("Initializing Variables and Call Sites ------------------------------------------");
		Iterator<Unit> unitIter=getUnits();
		while(unitIter.hasNext()){
			Unit unit = (Unit) unitIter.next();
			System.out.println("--------------------------");
			System.out.println("unit: "+ unit);
			System.out.println("unit class: "+ unit.getClass().getName());
			if(unit!=null){
				List<ValueBox> uses = unit.getUseBoxes();
				//System.out.println("uses: "+uses);

				if(unit instanceof soot.jimple.internal.JIdentityStmt){
					Value useValue = ((soot.jimple.internal.JIdentityStmt)unit).getRightOp();
					Value defValue = ((soot.jimple.internal.JIdentityStmt)unit).getLeftOp();
					int colonPosition = useValue.toString().lastIndexOf(":");
					String className = useValue.toString().substring(colonPosition+1);
					
					if(!(className.contains("java")) && !(className.contains("soot"))&&!(className.contains("pointsto"))){
						this.allocationSiteMap.put(new Integer(defValue.equivHashCode()), className);
						System.out.println("put IDENTITY defValue: "+ defValue+ ", className:"+className);
					}
				}
				else
					if(unit instanceof soot.jimple.internal.JAssignStmt){
						List<ValueBox> defs = unit.getDefBoxes();
						Value defValue = defs.get(0).getValue(); //Have only one def
						System.out.println("defValue: "+ defValue+ ", Class"+defValue.getClass().getName());

						//----------------------  So it is an assignment to an instance variable. -------------
						if((defValue instanceof soot.jimple.internal.JimpleLocal)){ 
							//------------------ So it is a simple assignment or a new Instance from one variable to another -------------------
							if(uses.size()==1){
								Value useValue = uses.get(0).getValue();
								if((useValue instanceof soot.jimple.internal.JNewExpr)){
									String className = useValue.toString().replaceAll("new", "");

									if(!(className.contains("java")) && !(className.contains("soot"))){
										this.allocationSiteMap.put(new Integer(useValue.equivHashCode()), className);
										System.out.println("put INSTANCE key:"+useValue.equivHashCode()+", className:"+className);
									}
								}
							}
							else//----------------------  It is a method invocation with an Assignment --------------------------------
								if((uses.size()>1)&&
										((JAssignStmt) unit).getRightOp() instanceof JVirtualInvokeExpr){
									InvokeExpr stmt = ((JAssignStmt) unit).getInvokeExpr();
									SootMethod method = stmt.getMethod();
									Integer key = new Integer(method.equivHashCode());
									HashSet<InvokeExpr> set; //used to store the statements (call sites related to the same method)
									if(!(method.getSignature().contains("java")) 
											&& !(method.getSignature().contains("soot"))
											&& !(method.getSignature().contains("pointsto"))){
										if(callSitesMap.containsKey(key)){
											set = callSitesMap.get(key);
										}
										else{
											set = new HashSet<InvokeExpr>();
										}
										set.add(stmt);
											this.callSitesMap.put(key,set);
											System.out.println("put METHOD key:"+ key+", method:"+method.getSignature());
									}
								}
						}
					}
					else//It is simply a method invocation, without assignments
						if((unit instanceof JInvokeStmt)){
							System.out.println("JVirtualInvokeStmt---------------------");
							InvokeExpr stmt = ((JInvokeStmt) unit).getInvokeExpr();
							SootMethod method = stmt.getMethod();
							Integer key = new Integer(method.equivHashCode());
							HashSet<InvokeExpr> set; //used to store the statements (call sites related to the same method)
							if(!(method.getSubSignature().contains("java")) && !(method.getSubSignature().contains("soot"))&& !(method.getSubSignature().contains("pointsto"))){
								if(callSitesMap.containsKey(key)){
									set = callSitesMap.get(key);
								}
								else{
									set = new HashSet<InvokeExpr>();
								}
								set.add(stmt);
								this.callSitesMap.put(key,set);
								System.out.println("put METHOD key:"+ key+", method:"+method.getSubSignature());
							}
						}
			}
		}
	}


	/** Traverse the units produced by Soot and for each instance variable assignment, create a constraint edge to be
	 * stored at the ConstraintGraph
	 */
	public void buildConstraintEdges(){
		Iterator<Unit> unitIter=getUnits();
		System.out.println();
		System.out.println("-------------------------------------------------------------------");
		System.out.println("Building Constraint Graph------------------------------------------");
		while(unitIter.hasNext()){
			Unit unit = (Unit) unitIter.next();

			if(unit!=null){
				System.out.println("------------------------------------------");
				System.out.println("unit: "+ unit);
				System.out.println("unit class: "+ unit.getClass().getName());
				List<ValueBox> uses = unit.getUseBoxes();

				if(unit instanceof soot.jimple.internal.JIdentityStmt){
					Value useValue = ((soot.jimple.internal.JIdentityStmt)unit).getRightOp();
					Value defValue = ((soot.jimple.internal.JIdentityStmt)unit).getLeftOp();
					Integer source = new Integer(useValue.equivHashCode());
					Integer successor = new Integer(defValue.equivHashCode());
					System.out.println("put IDENTITY STMT source: "+ source+ ", successor:"+successor);
					this.constraintGraph.addSuccessor(source, successor);
				}
				else
					if(unit instanceof soot.jimple.internal.JAssignStmt){
						List<ValueBox> defs = unit.getDefBoxes();
						Value defValue = defs.get(0).getValue(); //Have only one def
						System.out.println(" ASSIGN STMT, leftOp/defValue: "+ defValue+  ", key: "+defValue.equivHashCode()+", Class"+defValue.getClass().getName());

						//----------------------  So it is an assignment to an instance variable. -------------
						if((defValue instanceof soot.jimple.internal.JimpleLocal)){ 

							//------------------ So it is a simple assignment or a new Instance from one variable to another -------------------
							if(uses.size()==1){
								Value useValue = uses.get(0).getValue();
								if((useValue instanceof soot.jimple.internal.JNewExpr) || (useValue instanceof soot.jimple.internal.JimpleLocal)){
									Integer successor = new Integer(defValue.equivHashCode());
									Integer source = new Integer(useValue.equivHashCode());
									
									this.constraintGraph.addSuccessor(source, successor);
									System.out.println("put source: "+source+", successor: "+ successor);
								}
								else
									System.out.println("Assignment situation not predicted:"+useValue.getClass().getName() +" Ignoring....");
							}
							else
								//----------------------  It is a method invocation, so there is  return to be treated. -------------
								if((uses.size()>1)&&
										((JAssignStmt) unit).getRightOp() instanceof JVirtualInvokeExpr){
									System.out.println("Resolving return statement: "+((JAssignStmt) unit).getRightOp());
									

									Integer successor = new Integer(defValue.equivHashCode()); //The def Value of the caller will be successor

									//Now we have to obtain the name to the internal variable returned by the method.
									Value value = ((JAssignStmt) unit).getRightOp();
									SootMethod method = ((soot.jimple.internal.JVirtualInvokeExpr) value).getMethod();
									
									System.out.println("Successor = "+defValue+"/"+successor+ ", method has ActiveBody "+method.hasActiveBody());
									if(method.hasActiveBody()){
										//Now we have to link the parameters and the internal variables of the method.
										InvokeExpr stmt = ((JAssignStmt) unit).getInvokeExpr();	
										NodeCopiesMap copiesMap = linkParametersCallerAndCallee(stmt,method);
										

										//Link the return with the variable receiving the result
										linkCodeReturnFromMethod(method,successor);
										this.constraintGraph.cloneNodes(copiesMap);

										
									}
								}
						}
					}

					else
						if((unit instanceof JInvokeStmt)){
							System.out.println("JVirtualInvokeStmt---------------------");
							InvokeExpr stmt = ((JInvokeStmt) unit).getInvokeExpr();
							SootMethod method = stmt.getMethod();
							if(method.hasActiveBody()){
								NodeCopiesMap copiesMap =linkParametersCallerAndCallee(stmt,method);
								//Replicate nodes which are copies for the 1-CFA algorithm
								this.constraintGraph.cloneNodes(copiesMap);
							}
						}
			}
		}

	}


	protected NodeCopiesMap linkParametersCallerAndCallee(InvokeExpr stmt, SootMethod method){
		System.out.println("--- Linking Caller-Callee ----- -------------");
		List<ValueBox> invocationList = stmt.getUseBoxes();
		int j=1;
		int i=0;
		
		//This random value is to create a node hashcode that is distinct from the one provided by soot.
		//This enables to have distinct method parameter nodes even in face of calling the same 
		//method multiple times. This is what is needed to have 1-CFA class of analysis instead of zero-CFA.
		Random rand = new Random(); 
		int randValue = rand.nextInt(10000); 
		System.out.println("randValue: "+randValue);
		
		NodeCopiesMap localNodesCopies = new NodeCopiesMap();

		
		while(j<invocationList.size() && i<method.getParameterCount()){
			Value value= ((ValueBox)invocationList.get(j)).getValue();
			if((value instanceof JimpleLocal)&&(method.hasActiveBody())){//Only create edge if the parameter is a local instance (i.e., ignore constants)
				System.out.println("------ Local in invokeStmt: "+ value+" class: "+value.getClass().getName());

				Local local = method.getActiveBody().getParameterLocal(i);
				System.out.println("------ Local in method="+local.getName()+" class:"+local.getClass().getName());

				Integer source = new Integer(value.equivHashCode());
				Integer successor = new Integer(local.equivHashCode());
				System.out.println("InvokeExpr Caller-Callee source: "+value+ "/"+ source+", successor: "+ local+"/"+ successor);
				
				//Need to create copies to obtain the 1-CFA solution	
				//See also the method cloneNodes(this.constraintGraph,nodeCopiesMap);
				Integer copyNode=new Integer(randValue+successor.intValue());
				this.nodeCopiesMap.addCopy(successor, copyNode);
				localNodesCopies.addCopy(successor, copyNode);
				System.out.println("put copyMap successor: "+local+ "/"+ successor+", successorCopy: "+ local+"/"+ copyNode);
				this.constraintGraph.addSuccessor(source, copyNode);
				System.out.println("InvokeExpr put source: "+value+ "/"+ source+", successorCopy: "+ local+"/"+ copyNode);
				
			}
			else{
				System.out.println("missed If in linkParametersCallerAndCallee:" +stmt);
			}
			i++;
			j++;
		}
		return localNodesCopies;
	}

	protected void linkCodeReturnFromMethod(SootMethod method, Integer successor){
		System.out.println("--- Linking Return calls -------------");
		Integer source=null;
		if(method.hasActiveBody()){
			Iterator<Unit> iterMB = method.getActiveBody().getUnits().iterator();
			while(iterMB.hasNext()){
				Unit unitMB= iterMB.next();
				if(unitMB instanceof JReturnStmt){
					Value value = ((JReturnStmt)unitMB).getOp();
					System.out.println("------ return value: "+unitMB+"/"+value.equivHashCode());
					source = new Integer(value.equivHashCode());
				}
			}
		}
		
		this.constraintGraph.addSuccessor(source, successor);
		System.out.println("---- Return put source: "+source+", successor: "+ successor);
	}


	/** Run the reachability analysis by traversing the constraint graph and making unions at 
	 * every path intersection (paths coming from different allocation sites)
	 * The result of the processing is ready in the datastructure pointsToSet of this class.
	 */
	public void runReachability(){	
		System.out.print("Running Reachability - traversing the graph...");
				
		Iterator<Integer> iterSites = this.allocationSiteMap.keySet().iterator();
		while(iterSites.hasNext()){
			Integer key = iterSites.next();
			//Obtains the className from the allocation site. This will be propagated to all child nodes (descendents)
			String className = this.allocationSiteMap.get(key);
			this.pointsToSet.addNode(key, className);
			if(this.constraintGraph.nodesMap.get(key)!=null){
				Iterator<Integer> iterFirstInstance = this.constraintGraph.nodesMap.get(key).iterator();
				Integer succKey = iterFirstInstance.next();
				this.performReachabilityAnalysis(succKey, className);
			}
		}
		System.out.println("... Done !");
	}



	/** Traverses the graph from the root (Allocation Site) to the leaves. 
	 * At each node proceeds an Union operation
	 * @param graph
	 */
	public void performReachabilityAnalysis(Integer key, String className){
		//System.out.println("iter key="+key+" className: "+className);
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
		System.out.println();
		System.out.println("Points to Set obtained------------- ");
		System.out.println(this.pointsToSet);
		System.out.println();
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("Possible classes for methods calls from (variable,hashcode)------------------");
		Iterator<Integer> mKeys = this.callSitesMap.keySet().iterator();
		while(mKeys.hasNext()){
			HashSet<InvokeExpr> set = this.callSitesMap.get(mKeys.next()); //used to store the statements (call sites related to the same method)
			Iterator<InvokeExpr> iter = set.iterator(); 
			while(iter.hasNext()){
				InvokeExpr expr = (InvokeExpr) iter.next(); 
				List<ValueBox> invocationList =  expr.getUseBoxes();
				if((invocationList!=null)&&(invocationList.size()>0)){
					Value value= ((ValueBox)invocationList.get(0)).getValue();
					Integer objectKey = new Integer(value.equivHashCode());
					if(this.pointsToSet.hasNode(objectKey)){
						String classes = this.pointsToSet.getStringSet(objectKey);
						System.out.println("Variable ("+ value+","+objectKey+") CALLS "+expr.getMethod().getSubSignature()+" ON "+ classes);
					}
				}
			}
		}
	}
}


