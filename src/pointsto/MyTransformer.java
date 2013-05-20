package pointsto;

import java.util.Map;

import soot.Body;
import soot.BodyTransformer;

public class MyTransformer extends BodyTransformer {
	
	PToAnalysis analysis = new PToAnalysis();
	
	protected void internalTransform(Body body, String phaseName, Map options) {
		analysis.setBody(body);
		analysis.run();
	  }
	  
	  public void printOutPut(){
		  analysis.printResults();
	  }

}
