package constantpropagation;

import java.util.*;

import soot.toolkits.graph.BlockGraph;

/**
 * Implements the worklist algorithm to execute a constant propagation analysis.
 * 
 * @author Christian Adriano
 *
 */
public class Analysis {

	HashMap latticeMap= new HashMap();
	ArrayList workList = new ArrayList();
	BlockGraph graph; //this is initialized by the constructor
	
	public Analysis(BlockGraph graph) {
		this.graph=graph;
	}

	//How to navigate the callGraph? Maybe I can use the UnitIterator itself. Check what the UnitIterator does 
	//in the presence of loops and ifs.
	
	public void obtainPredecessors(){
		//check this code below, because it uses graph.Block, which has methods to retrieve predecessors of a unit
		//http://stackoverflow.com/questions/6792305/identify-loops-in-java-byte-code
	}
	
	public void initializeWorkList(){}
	
	
	/** Method iterates through the body of a program to find the free variables, 
	 * which are the variables at the left hand side of the statements.
	 * @return array of free variable names as primitive string type.
	 */
	public ArrayList obtainFreeVariables(){
		
		ArrayList<String> fvList = new ArrayList<String>();
		
		return fvList;
	}
	
	public ArrayList meet(){
		return null;
	}
	
}
