import heapstack.MainSootProject1;


/**
 * Code to be analyzed by Soot for Project-1 
 * @see MainSootProject1.java
 * @author Christian Adriano
 *
 */
public class TargetCode1 {

	static String staticProperty;
	
	String instanceProperty;
	
	public TargetCode1() {
		String[] wordsStrArray = new String[5];  //Heap Write
		wordsStrArray[0]="zero"; //Heap write
		String zeroVar= new String("9"); //No heap access
		zeroVar = wordsStrArray[0]; //Heap read
		String oneVar= "one"; //No heap access
		wordsStrArray[1]= oneVar; //Heap write
		
		TargetCode1.staticProperty="value"; //Heap write
		oneVar = TargetCode1.staticProperty; //Heap read
		
		this.instanceProperty ="value"; //Heap write
		oneVar=this.instanceProperty; //Heap read
		
	}

}
