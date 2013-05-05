package constantpropagation;

import java.util.Iterator;
import java.util.List;

import soot.Unit;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;

/** Creates the lists used in the WorkList and the EdgeList
 *  
 * @author Christian Adriano
 *
 */
public class WorkListFactory {

	private BlockGraph blockGraph=null;
	
	private WorkList workList=null;
	
	private EdgeList edgeList=null;;
	
	public void setupFactory(BlockGraph graph, WorkList workList, EdgeList edgeList){
		this.blockGraph = graph;
		this.workList = workList;
		this.edgeList = edgeList;
	}
	
	public boolean buildLists(){
		
		if((workList==null) || (edgeList==null) || (blockGraph ==null)) 
			return false;
		
		for (Block block : blockGraph.getBlocks()) {
			List<Block >predBlockList = block.getPreds();
			List<Block >succBlockList = block.getSuccs();
			//System.out.println("-------------------------------------------------");
			System.out.println("Block:  "+block);
			Iterator<Unit> unitIt =  block.iterator();
			Edge edge;
			while(unitIt.hasNext()){
				Unit unit = (Unit) unitIt.next();
				Unit unitSucc= block.getSuccOf(unit);
				Unit unitPred= block.getPredOf(unit);
				if((unitPred==null)&&(!predBlockList.isEmpty())){//Means unit is the first node in the block
					linkHeadUnitToPedecessors(unit,predBlockList);
				}
				else if((unitPred==null)&&(predBlockList.isEmpty())){ //Means unit is the first node in the graph
					edge=new Edge(null, unit);
					workList.insert(edge);
					edgeList.insert(edge);
				}
				else if ((unitSucc == null)&&(!succBlockList.isEmpty())){ //Means unit is the last node in the block
						linkHeadUnitToSuccessors(unit,succBlockList);
					}
					else if ((unitSucc == null)&&(succBlockList.isEmpty())){ //Means unit is the last node in the graph
						edge=new Edge(unit,null);
						workList.insert(edge);
						edgeList.insert(edge);
					}else
						if((unitPred!=null)&&(unitSucc!=null)){//Means the node in the middle of a block
							edge=new Edge(unitPred,unit);
							workList.insert(edge);
							edgeList.insert(edge);
							edge = new Edge(unit,unitSucc);
							workList.insert(edge);
							edgeList.insert(edge);
							
						}
						else
							System.out.println("ERROR in Worklist construction, unit="+unit+", pred="+unitPred+", succ="+unitSucc);//
			}
		}
		return true;
	}
	
	/** @param unit for a given unit that is the last unit in a block, link it the unit that is a tail in 
	 * each of the predecessors blocks
	 *  @param predBlockList the list of all predecessor blocks for the block the current unit belongs
	 *  Updates the workList and the EdgeList with new inserted elements representing edges
	 * */
	private void linkHeadUnitToPedecessors(Unit unit,List<Block>predBlockList){
		for(Block block : predBlockList){
			Unit unitTail=block.getTail();
			Edge edge = new Edge(unitTail,unit);
			workList.insert(edge);
			edgeList.insert(edge);
		}
	}
	
	/** @param unit for a given unit that is the last unit in a block, link it the unit that is a tail in 
	 * each of the predecessors blocks
	 *  @param predBlockList the list of all predecessor blocks for the block the current unit belongs
	 *  Updates workList and the EgdeList with new inserted elements representing edges
	 * */
	private void linkHeadUnitToSuccessors(Unit unit,List<Block>succBlockList){
		for(Block block : succBlockList){
			Unit unitTail=block.getTail();
			Edge edge = new Edge(unitTail,unit);
			workList.insert(edge);
			edgeList.insert(edge);
		}
	}
	

	public EdgeList getEdgeList(){
		return this.edgeList;
	}
	
	public WorkList getWorkList(){
		return this.workList;
	}
}
