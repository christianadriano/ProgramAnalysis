package pointsto;

import java.util.HashMap;
import java.util.HashSet;

/** 
 * Graph in which each node is a variable in the program and the edges is a subset relationship.
 * 
 * For example. Node a has a subset relationship of {classA,subclassA, subsubclassA} 
 * Which means variable a is connected to all theses classes.
 * 
 * @author Christian Adriano
 *
 */
public class ConstraintGraph {

	
	/** Map indexed by the String representation of the key of a node */
	public HashMap<Integer,HashSet<Integer>> nodesMap = new HashMap<Integer,HashSet<Integer>>();
	

	/**
	 * Adds a new successor to a key. 
	 * @param key the node whose successor
	 * @param successor the node being associated to the key
	 * @return true if the key already had a set of successors, otherwise false. In both cases adds the successor to the key.
	 */
	public boolean addSuccessor(Integer key, Integer successor){
		HashSet<Integer> set = nodesMap.get(key);
		if(set==null){
			set = new HashSet<Integer>();
			set.add(successor);
			nodesMap.put(key, set);
			return false;
		}
		else{
			set.add(successor);
			nodesMap.put(key, set);
			return true;
		}
	}
	
	
}
