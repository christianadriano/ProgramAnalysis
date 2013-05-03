package constantpropagation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * Implements a FIFO for unique elements.
 * @author Christian Adriano
 *
 */
public class WorkList {

	/** Set of unique elements. It serves to help keep the linked list free from clones */
	HashMap<String,Edge> edgeMap = new HashMap<String,Edge>();
	
	/** Order list of elements. It works as First In First Out list, 
	 * in other words, we remove always the head (first element) and add at tail (last element)
	 */
	LinkedList<Edge> edgeList = new LinkedList<Edge>();
	
	/**
	 * 
	 * @return an edge if it is not empty, otherwise null
	 */
	public Edge extract(){
		if(!edgeMap.isEmpty()){
			Edge edge = edgeList.getFirst();
			edgeMap.remove(edge.getKey());
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
		if(edgeMap.containsKey(edge.getKey()))
			return false;
		else{
			edgeMap.put(edge.getKey(),edge);
			edgeList.add(edge);
			//System.out.println("Insert:"+edge);
			return true;
		}
	}
	
	
	/** Better format to display results in the console */
	public String toString(){
		String result="";
		Iterator<Edge> iter = this.edgeList.iterator();
		while(iter.hasNext()){
			Edge edge = (Edge) iter.next();
			result = result+"\n"+edge.getKey();
		}
		result.substring(0, result.length()-1);
		return result;
		
	}
}
