package constpropag;

import java.util.*;

import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.JimpleBody;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.tagkit.CodeAttribute;
import soot.tagkit.Tag;
import soot.toolkits.graph.BlockGraph;

/**
 * Implements the worklist algorithm to execute a constant propagation analysis.
 * 
 * @author Christian Adriano
 */
public class Analysis {

	public static final String MUST = "MUST";
	public static final String MAY = "MAY";
	private String type; 
	
	/** Initial list of all freeVariables. <Variable name, Value of the Variable>*/
	HashMap<String,Content> fvList= new HashMap<String,Content>();
	
	/** Keeps the list of edges to be processed*/
	WorkList workList = new WorkList();
	
	/** Keeps the list of edges of the program. This list is created once and never changes.*/
	EdgeList edgeList = new EdgeList();
	
	/** Keeps the results for each statement */
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
	
	 
	
	public Analysis(BlockGraph graph, Body body, String analysisType) {
		this.graph=graph;
		this.body=body;
		this.type = analysisType;	
		tagUnits();
		this.fvList = this.initializeFreeVariablesLattice();
	}
	
	public void run(){
		
		initializeAnalysisList();
		WorkListFactory factory = new WorkListFactory();
		factory.setupFactory(this.graph, this.workList, this.edgeList);
		factory.buildLists();
		this.workList=factory.getWorkList();
		this.edgeList=factory.getEdgeList();
		//System.out.println("WorkList: "+workList);
		Join join = new Join();

		while(workList.hasElements()){
			Edge edge = workList.extract();
			String label=edge.startLabel;
			Lattice entryLattice = this.analysisMap.get(label);
			//System.out.println("---------------------------------------------------------------");
			//System.out.println("Edge: "+edge.getKey()+", Label: "+label+" lattice: "+entryLattice);
			
			HashMap<String,Edge> arrivingEdges = edgeList.getArrivingEdges(label);
			//System.out.println("Arriving Edges: "+arrivingEdges);
			
			//PERFORM THE MEET OPERATION
			Lattice newEntryLattice=null;
			if(label!=null){//Ignore null labels.
				join.setupAnalysis(this.type, entryLattice, arrivingEdges,analysisMap);
				newEntryLattice = join.join();
				//System.out.println("After JOIN = Edge: "+edge.getKey()+", Label: "+label+" OldLattice: "+entryLattice+", NewLattice: "+newEntryLattice);
				//System.out.println("-----------------------------------------------------------------------------------------------------------------");
				this.analysisMap.put(label, newEntryLattice);//Stores the value for the label
			}
			
			//PERFORM THE KILL OPERATION
			Lattice exitLattice = newEntryLattice;
			Content computedContent;
			KillCompute killCompute = new KillCompute();
			Unit unit = edge.startUnit;
			if(unit instanceof soot.jimple.internal.JAssignStmt){
				String expression = ((JAssignStmt)unit).getRightOp().toString();
				String freeVariable = ((JAssignStmt)unit).getLeftOp().toString();
				
				if(expression.indexOf("invoke")>0){//Means it contains a method call, so considered TOP for left side
					computedContent = new Content(Content.TOP);
				}
				else{
					killCompute.setupKill(expression, exitLattice);
					computedContent = killCompute.compute();
				}

				//Now with the new computed value
				if (computedContent!=null){
					//Replace the new Content in the newLattice
					exitLattice.setFreeVariableValue(freeVariable, computedContent);
					this.analysisMap.put(freeVariable, exitLattice); //Stores the new lattice for the label
				}
				else{//Compute returned null, because there are freevariables with TOP or BOTTOM
					//Try to kill nothing, keep variable with initialized values
					if(this.type.compareTo(Analysis.MUST)==0)
						computedContent = new Content(Content.TOP); 
					else
						if (this.type.compareTo(Analysis.MAY)==0)
							computedContent =  new Content(Content.BOTTOM);
					
					exitLattice.setFreeVariableValue(freeVariable, computedContent);
					this.analysisMap.put(freeVariable, exitLattice); //Stores the new lattice for the label
				}
				
			}
			else{ //Keep the same lattice from the MEET OPERATION	
			}
			
			
			if (!(entryLattice.equals(exitLattice)))
				workList.insertAllEdgesWithEntryLabel(label,this.edgeList); //Restore to worklist all edges with the label whose lattice changed
			
		}
		
		//System.out.println("WorkList------------------------\n"+workList);
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
	public HashMap<String,Content> initializeFreeVariablesLattice(){
		fvList= new HashMap<String,Content>();
		Iterator<Unit> unitIter=getUnits();
		while(unitIter.hasNext()){
			Unit unit = (Unit) unitIter.next();
			if(unit instanceof soot.jimple.internal.JAssignStmt){
				String cellType;
				if(this.type.compareTo(Analysis.MUST)==0)
					cellType= Content.TOP;
				else
					cellType = Content.BOTTOM;
				fvList.put(((JAssignStmt)unit).leftBox.getValue().toString(),new Content(cellType));
				//System.out.println("Unit: "+ unit+", Oper: "+((JAssignStmt)unit).getRightOp());
			}
			//ELSE System.out.println("Non Assignment Statement: "+unit);
		}
		return fvList;
	}
	
	/** Iterates through the graph and obtains each program statement (which is a Unit in Soot).
	 * For each associates the Initial Lattice with it.
	 */
	public void initializeAnalysisList(){
		Iterator<Unit> unitIter = getUnits();
		while(unitIter.hasNext()){
			Unit unit = (Unit) unitIter.next();
			Lattice lattice  = new Lattice(unit,this.fvList);
			List<Tag> list = unit.getTags();
			String label = ((CodeAttribute) list.get(0)).getName();
			this.analysisMap.put(label,lattice);
			this.analysisList.add(label);
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
		for(String label : analysisList){
			Lattice lattice = analysisMap.get(label);
			System.out.println("<"+lattice.unit.toString()+">  ==  "+ lattice.fvMap);
		}
		System.out.println("-----------------------------------------------------------------");
	}

	public boolean hasFreeVariables() {
		if(this.fvList.isEmpty())
			return false;
		else{
			//System.out.println("Lattice initialized to:"+ fvList);
			return !(this.fvList.isEmpty());
		}
	}
	
}