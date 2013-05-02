package constantpropagation;

import java.util.HashSet;
import java.util.LinkedList;


/**
 * Implements a FIFO for unique elements.
 * @author Christian Adriano
 *
 */
public class WorkList {

	HashSet<Edge> edgeSet = new HashSet<Edge>();
	
	LinkedList<Edge> edgeList = new LinkedList<Edge>();
	
	/**
	 * 
	 * @return an edge if it is not empty, otherwise null
	 */
	public Edge extract(){
		if(!edgeSet.isEmpty()){
			Edge edge = edgeList.getFirst();
			edgeSet.remove(edge);
			edgeList.removeFirst();
			return edge;
		}
		else return null;
	}
	
	/** 
	 * 
	 * @param edge
	 * @return true if the edge is new to the list, otherwise false
	 */
	public boolean insert(Edge edge){
		if(edgeSet.contains(edge))
			return false;
		else{
			edgeSet.add(edge);
			edgeList.add(edge);
			return true;
		}
	}
	
	

}
