package pointsto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Class responsible to keep track of the points to set for each 
 * free variable at each program statement
 * @author Christian Adriano
 *
 */
public class PointsToSet {

	/** Stores the class names associated to a instance free variable */
	HashMap<Integer, HashSet<String>> pointSetMap = new HashMap<Integer,HashSet<String>>();
	
	public boolean addNode(Integer key, String className) {
		HashSet<String> set = pointSetMap.get(key);
		if(set==null){
			set = new HashSet<String>();
			set.add(className);
			this.pointSetMap.put(key,set);
			return false;
		}
		else{
			set.add(className);
			this.pointSetMap.put(key,set);
			return true;
		}
	}

	public boolean hasNode(Integer key) {
		return this.pointSetMap.keySet().contains(key);
	}

	
	public String toString(){
		
		String result=new String();
		Iterator<Integer> iter = pointSetMap.keySet().iterator();
		while(iter.hasNext()){
			Integer key = iter.next();
			Iterator<String> set = pointSetMap.get(key).iterator();
			result = result+key + " = {";
			while(set.hasNext()){
				String className =set.next();
				result = result + className+",";
			}
			result = result.substring(0, result.length()-1);
			result = result + "}\n";
		}
		return result;
	}
	
	

}
