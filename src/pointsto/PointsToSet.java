package pointsto;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Class responsible to keep track of the points to set for each 
 * free variable at each program statement
 * @author Christian Adriano
 *
 */
public class PointsToSet {

	HashSet<String> pointSet = new HashSet<String>();

	public void addNode(String nodeContent) {
		this.pointSet.add(nodeContent);
	}

	public boolean hasNode(String nodeContent) {
		return this.pointSet.contains(nodeContent);
	}

	
	public String toString(){
		String result="{ ";
		Iterator<String> iter = pointSet.iterator();
		while(iter.hasNext()){
			String content = iter.next();
			result = result + content+","; 
		}
		result = result.substring(0, result.length()-1);
		result = result + " }";
		return result;
	}
	
	

}
