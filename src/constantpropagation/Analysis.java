package constantpropagation;

import java.util.*;
import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.JimpleBody;
import soot.jimple.internal.JAssignStmt;
import soot.tagkit.CodeAttribute;
import soot.tagkit.Tag;
import soot.toolkits.graph.BlockGraph;

/**
 * Implements the worklist algorithm to execute a constant propagation analysis.
 * 
 * @author Christian Adriano
 */
public class Analysis {

	static final String MUST = "MUST";
	static final String MAY = "MAY";
	private String type; 
	
	/** Initial list of all freeVariables */
	HashMap<String,Value> fvList= new HashMap<String,Value>();
	
	/** Keeps the list of edges to be processed*/
	WorkList workList = new WorkList();
	
	/** Keeps the results for each statement */
	HashMap<Unit,HashMap<String,Value>> analysisMap = new HashMap<Unit,HashMap<String,Value>>();
	
	/** Keeps the list of statements in order of occurence so we can display the results in the same order
	 * they appear in the source code listing */
	ArrayList<Unit> analysisList = new ArrayList<Unit>();
	
	/**Blocks are the entities in Soot that effectively keep track of succession and 
	 * predecession (both at block as well as at unit level*/ 
	BlockGraph graph; //this is initialized by the constructor
	
	/** It is used to keep the statements of the program in a simple the format (a list 
	 * of Soot Units) */
	private Body body;
	
	 
	
	public Analysis(BlockGraph graph, Body body, String analysisType) {
		this.graph=graph;
		this.body=body;
		this.type = analysisType;	
		tagUnits();
		this.fvList = this.initializeFreeVariablesLattice();
	}
	
	public void run(){
		initializeAnalysisList(this.fvList);
		WorkListFactory factory = new WorkListFactory(this.graph);
		factory.buildList(this.workList);
	}

	/** Define a unique Tag for each Unit so we might identify them uniquely without
	 * needing to rely on Object equals comparisons.
	 */
	public void tagUnits(){
		int i=0;
		Iterator<Unit> unitIter=getUnits();
		while(unitIter.hasNext()){
			Unit unit = (Unit) unitIter.next();
			String label= new Integer(i).toString();
			CodeAttribute labelTag = new CodeAttribute(label);
			unit.addTag(labelTag);
			i++;
		}
	}
	
	
	/** Method iterates through the body of a program to find the free variables, 
	 * which are the variables at the left hand side of the statements.
	 * @return array of free variable names as primitive string type.
	 */
	public HashMap<String,Value> initializeFreeVariablesLattice(){
		fvList= new HashMap<String,Value>();
		Iterator<Unit> unitIter=getUnits();
		while(unitIter.hasNext()){
			Unit unit = (Unit) unitIter.next();
			if(unit instanceof soot.jimple.internal.JAssignStmt){
				fvList.put(((JAssignStmt)unit).leftBox.getValue().toString(),new Value(true,this.type));
			}
		}
		return fvList;
	}
	
	/** Iterates through the graph and obtains each program statement (which is a Unit in Soot).
	 * For each associates the Initial Lattice with it.
	 */
	public void initializeAnalysisList(HashMap<String,Value> initialValues){
		Iterator<Unit> unitIter = getUnits();
		while(unitIter.hasNext()){
			Unit unit = (Unit) unitIter.next();
			this.analysisMap.put(unit,fvList);
			this.analysisList.add(unit);
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
	
	public void printResult(){
		System.out.println("-----------------------------------------------------------------");
		System.out.println("Constant Propagation Results <Statement : Free Variable Values>");
		System.out.println("Analysis Type: "+ this.type);
		System.out.println("-----------------------------------------------------------------");
		for(Unit unit : analysisList){
			HashMap<String, Value> lattice = analysisMap.get(unit);
			System.out.println("< Unit:"+unit.toString()+" = "+ lattice+">");
		}
		System.out.println("-----------------------------------------------------------------");
	}

	public boolean hasFreeVariables() {
		if(this.fvList.isEmpty())
			return false;
		else{
			System.out.println("Lattice initialized to:"+ fvList);
			return !(this.fvList.isEmpty());
		}
	}
	
}