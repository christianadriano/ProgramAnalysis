package pointsto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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
	
	/** Used to avoid insert duplicated nodes */
	public HashSet<String> nodeHash = new HashSet<String>();

	/**
	 * Adds a new successor to a key. 
	 * @param key the node whose successor
	 * @param successor the node being associated to the key
	 * @return true if the key already had a set of successors, otherwise false. In both cases adds the successor to the key.
	 */
	public boolean addSuccessor(Integer key, Integer successor){
		String keyPair = key.toString()+successor.toString();
		if(!nodeHash.contains(keyPair)){
			nodeHash.add(keyPair);
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
		else
			return false;
	}

	public boolean removeSourceSuccessorsSet(Integer source){
		HashSet<Integer> sucSet = this.nodesMap.get(source);
		if(sucSet==null) return false;
		Iterator<Integer> sucIter = sucSet.iterator();
		while(sucIter.hasNext()){
			Integer successor = sucIter.next();
			String keyPair = source.toString()+successor.toString();
			this.nodeHash.remove(keyPair);
		}
		this.nodesMap.remove(source);
		return true;
	}
	
	public boolean hasPair(Integer source, Integer successor) {
		String keyPair = source.toString()+successor.toString();
		return this.nodeHash.contains(keyPair);
	}
	
	/** This method is called to generate the 1-CFA version of the algorithm 
	 * @param nodeCopies a map of copies of node and the original nodes.
	 * This method replaces all original nodes by the copies. Since there are
	 * multiple copies for the same node, the result is the creation of copies of the branches.
	 * 
	 * This way it is possible to traverse the graph more precisely for the case
	 * of multiple calls to the same method. Even with multiple calls, there will 
	 * be one set nodes (the copies) for each call. The nodes connect the 
	 * the passing arguments with the parameters declared in the function signature.
	 * The copies consist of those parameters, because they are always the same even in
	 * face of different calls.
	 * 
	 * */
	public void cloneNodes(NodeCopiesMap nodeCopies){
		Iterator<Integer> iter = nodeCopies.keyIterator();
		while(iter.hasNext()){
			Integer original = iter.next();
			HashSet<Integer> copySet = nodeCopies.get(original);
			replaceAll(original,copySet);
		}

		//Now replace also all occurrences of original node in the successors
		iter = nodeCopies.keyIterator();
		while(iter.hasNext()){
			Integer original = iter.next();
			HashSet<Integer> copySet = nodeCopies.get(original);
			Iterator<Integer> iterSources =  this.nodesMap.keySet().iterator();
			boolean foundIt=false;
			Integer source=null;
			HashSet<Integer> newSet= new HashSet<Integer>();
			while(iterSources.hasNext() && !foundIt){//Iterate to find the successor node that matches the original
				source = iterSources.next();
				HashSet<Integer> sucSet = this.nodesMap.get(source);
				if(sucSet.contains(original)){
					foundIt=true;
					newSet = (HashSet<Integer>) sucSet.clone();
					newSet.remove(original);		
				}
			}
			if(foundIt){
				removeSourceSuccessorsSet(source);//remove the old node from the main datastructure
				addSuccessors(source,copySet,newSet);
			}
		}
	}
	
	
	/** Auxiliary method to aid in the cloning of nodes 
	 * @see cloneNodes method
	 * @param node the node to be substituted in the nodesMap
	 * @param copy the copy that will substitute the occurrences of node
	 */
	protected void replaceAll(Integer original, HashSet<Integer> copySet){
		HashSet<Integer> originalSuccessorSet = this.nodesMap.get(original);
		if(this.nodesMap.containsKey(original)){
			removeSourceSuccessorsSet(original); //remove the old node from the main datastructure
			addCopies(copySet,originalSuccessorSet);//create new entries with copy node as key and originalSuccessorSet as value 
		}
	}

	
	protected void addCopies(HashSet<Integer> copySet, HashSet<Integer> successorSet){
		Iterator<Integer> copyIter = copySet.iterator();
		while(copyIter.hasNext()){
			Integer copy = copyIter.next();
			Iterator<Integer> sucIter = successorSet.iterator();
			while(sucIter.hasNext()){
				Integer successor = sucIter.next();
				this.addSuccessor(copy, successor);
			}
		}
	}
	
	
	protected void addSuccessors(Integer source,HashSet<Integer> copySet, HashSet<Integer> successorSet){
		Iterator<Integer> copyIter = copySet.iterator();
		while(copyIter.hasNext()){
			Integer copy = copyIter.next();
			HashSet<Integer> copySuccSet = (HashSet<Integer>) successorSet.clone();
			copySuccSet.add(copy);
			Iterator<Integer> sucIter = copySuccSet.iterator();
			while(sucIter.hasNext()){
				Integer successor = sucIter.next();
				this.addSuccessor(source, successor);
			}
		}
	}
	
	public String toString(){
		Iterator<Integer> iter = this.nodesMap.keySet().iterator();
		String result="{";
		while(iter.hasNext()){
			String cellStr=new String("[");
			Integer key = iter.next();
			cellStr = cellStr+key.toString()+":";
			Iterator<Integer> iterSet = this.nodesMap.get(key).iterator();
			while(iterSet.hasNext()){
				Integer node = iterSet.next();
				cellStr = cellStr+ node.toString()+",";
			}
			cellStr = cellStr.substring(0, cellStr.length()-1)+"]"; //removes the last comma
			result = result+cellStr+";";
		}
		result = result.substring(0, result.length()-1)+"}"; //removes the last semicolon;
		return result;
	}
}
