package pointsto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class NodeCopiesMap {

	
	/** Map indexed by the String representation of the key of a node */
	public HashMap<Integer,HashSet<Integer>> nodesMap = new HashMap<Integer,HashSet<Integer>>();
	
	/** Used to avoid insert duplicated nodes */
	public HashSet<String> nodeHash = new HashSet<String>();

	/**
	 * Adds a new copy related to a key. 
	 * @param original node whose copy is being associated
	 * @param successor the node being associated to the key
	 * @return true if the key already had a set of copies, otherwise false. In both cases adds the copy to the original node.
	 */
	public boolean addCopy(Integer original, Integer copy){
		String keyPair = original.toString()+copy.toString();
		if(!nodeHash.contains(keyPair)){
			nodeHash.add(keyPair);
			HashSet<Integer> set = nodesMap.get(original);
			if(set==null){
				set = new HashSet<Integer>();
				set.add(copy);
				nodesMap.put(original, set);
				return false;
			}
			else{
				set.add(copy);
				nodesMap.put(original, set);
				return true;
			}
		}
		else//Does not insert the new node, because it already exists.
			return false;
	}

	public boolean hasPair(Integer original, Integer copy) {
		String keyPair = original.toString()+copy.toString();
		return this.nodeHash.contains(keyPair);
	}

	public boolean removeSourceSuccessorsSet(Integer original){
		HashSet<Integer> copySet = this.nodesMap.get(original);
		if(copySet==null) return false;
		Iterator<Integer> copyIter = copySet.iterator();
		while(copyIter.hasNext()){
			Integer successor = copyIter.next();
			String keyPair = original.toString()+successor.toString();
			this.nodeHash.remove(keyPair);
		}
		this.nodesMap.remove(original);
		return true;
	}
	
	public HashSet<Integer> get(Integer original) {
		return this.nodesMap.get(original);
	}

	public Iterator<Integer> keyIterator() {
		return this.nodesMap.keySet().iterator();
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
