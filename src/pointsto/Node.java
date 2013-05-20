package pointsto;


/**
 * Represents a variable
 * 
 * @author Christian Adriano
 *
 */
public class Node {

	public static String ALLOCATION_SITE= "ALLOCATION_SITE";
	public static String ASSIGNMENT= "ASSIGNMETN";
	public static String CALL_SITET= "CALL_SITE";
	
	String className=null; //Only set if the type if Allocation Site
	String type;
	Integer key;
	
	public Node(String type, Integer key,  String className){
		if(type.compareTo(Node.ALLOCATION_SITE)==0){
			this.className=className;
		}
		this.type=type;
		this.key=key;
	}
	
	
	
}
