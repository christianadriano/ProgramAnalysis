package constpropag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/** Keeps track of all edges of the program
 *  This list is created once and never changes.
 *  It also provide an indexing of edges by entry labels, 
 *  to facilitate the retrieval of edges impacted by a change in on label.
 *  
 * @author Christian Adriano
 *
 */
public class EdgeList{

	/** Set of unique elements. It serves to help keep the linked list free from clones */
	protected HashMap<String,Edge> edgeMap = new HashMap<String,Edge>();
	
	/** Order list of elements. It works as First In First Out list, 
	 * in other words, we remove always the head (first element) and add at tail (last element)
	 */
	protected LinkedList<Edge> edgeList = new LinkedList<Edge>();
	
	/** Map of start labels to Edges */
	HashMap<String,HashMap<String,Edge>> startlabelToEdgeMap = new HashMap<String,HashMap<String,Edge>>();
	
	/** Map of end labels to Edges */
	HashMap<String,HashMap<String,Edge>> endlabelToEdgeMap = new HashMap<String,HashMap<String,Edge>>();

	
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
			
			insertStartToEdgeMap(edge);
			insertEndToEdgeMap(edge);
			//System.out.println("Insert:"+edge);
			return true;
		}
	}

	
	/** Inserts the edge under the map of edges dependant of the startLabel */ 
	protected void insertStartToEdgeMap(Edge edge){
		HashMap<String,Edge> map = this.startlabelToEdgeMap.get(edge.startLabel);
		if(map==null)
			map =new HashMap<String,Edge>();
		map.put(edge.getKey(),edge);
		startlabelToEdgeMap.put(edge.startLabel,map);
	}
	
	protected void insertEndToEdgeMap(Edge edge){
		HashMap<String,Edge> map = this.endlabelToEdgeMap.get(edge.endLabel);
		if(map==null)
			map =new HashMap<String,Edge>();
		map.put(edge.getKey(),edge);
		endlabelToEdgeMap.put(edge.endLabel,map);
	}
	
	
	/** 
	 * 
	 * @param label a label representing a program statement
	 * @return the map of edges that has this label as entry point
	 */
	public HashMap<String,Edge> getDependantEdges(String label){
		return this.startlabelToEdgeMap.get(label);
	}
	
	/** 
	 * 
	 * @param label a label representing a program statement
	 * @return the map of edges which arrive at this label
	 */
	public HashMap<String,Edge> getArrivingEdges(String label) {
		return this.endlabelToEdgeMap.get(label);
	}
	
	public String toString(){
		
		String result="Edge List for the Program -------------------------------";
		Iterator<Edge> iter = this.edgeList.iterator();
		while(iter.hasNext()){
			Edge edge = (Edge) iter.next();
			result = result+"\n"+edge.getKey();
		}
		result.substring(0, result.length()-1);
		
		result = result.concat("\nEdges dependant of labels ---------------------------------");
		Iterator<String> startKeys = this.startlabelToEdgeMap.keySet().iterator();		
		while(startKeys.hasNext()){
			String key = startKeys.next();
			HashMap<String, Edge> map = this.startlabelToEdgeMap.get(key);
			result = result.concat("Entry Label: "+key+" of Edges:"+map.keySet()+"\n");
		}
		
		result = result.concat("\nEdges arriving at labels ---------------------------------");
		Iterator<String>endKeys = this.endlabelToEdgeMap.keySet().iterator();		
		while(endKeys.hasNext()){
			String key = endKeys.next();
			HashMap<String, Edge> map = this.endlabelToEdgeMap.get(key);
			result = result.concat("Exit Label: "+key+" of Edges:"+map.keySet()+"\n");
		}

		
		result = result.concat("\n");
		return result;
	}
	
}

