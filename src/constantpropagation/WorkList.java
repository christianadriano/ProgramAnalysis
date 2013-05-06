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
public class WorkList{

	/** Set of unique elements. It serves to help keep the linked list free from clones */
	HashMap<String,Edge> edgeMap = new HashMap<String,Edge>();
	
	/** Order list of elements. It works as First In First Out list, 
	 * in other words, we remove always the head (first element) and add at tail (last element)
	 */
	LinkedList<Edge> edgeWorkList = new LinkedList<Edge>();
	
	/**
	 * 
	 * @return an edge if it is not empty, otherwise null
	 */
	public Edge extract(){
		if(!edgeMap.isEmpty()){
			Edge edge = edgeWorkList.getFirst();
			edgeMap.remove(edge.getKey());
			edgeWorkList.removeFirst();
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
			edgeWorkList.add(edge);
			//System.out.println("Insert:"+edge);
			return true;
		}
	}
	

	public boolean hasElements(){
		return ! this.edgeWorkList.isEmpty();
	}

	/** Better format to display results in the console */
	public String toString(){
		
		if(!hasElements())
			return "WorkList is EMPTY!";
		else{
			String result="";
			Iterator<Edge> iter = this.edgeWorkList.iterator();
			while(iter.hasNext()){
				Edge edge = (Edge) iter.next();
				if(edge!=null)
					result = result+"\n"+edge.getKey();
				else
					result = result+"\n"+"null";
			}

			if(result.length()>0)
				result.substring(0, result.length()-1);
			return result;
		}
	}

	
	/** Update the worklist with all edges which have as entry the provided label
	 * @param label entry node
	 * @param programEdgeList the list of all edges in the program
	 */
	public void insertAllEdgesWithEntryLabel(String label, EdgeList programEdgeList) {
		if(label!=null){
			HashMap<String,Edge>successorMap = programEdgeList.getDependantEdges(label);
			Iterator<String> keyIter = successorMap.keySet().iterator();
			while(keyIter.hasNext()){
				String key = keyIter.next();
				Edge edge = successorMap.get(key);
				this.edgeWorkList.add(edge);
				this.edgeMap.put(edge.getKey(), edge);
				//System.out.println("re-inserted: "+edge.getKey());
			}
		}
	}
	
}
