package pointsto;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.ValueBox;
import soot.jimple.InvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JVirtualInvokeExpr;

public class MyTransformer extends BodyTransformer {
	
	PToAnalysis analysis= new PToAnalysis();
	Body body;
	
	protected void internalTransform(Body body, String phaseName, Map options) {
		this.body=body;
		//testInternalTransform();
		analysis.setBody(body);
		analysis.run();
	  }
	  
	  public void printOutPut(){
		analysis.printResults();
	  }

	  public void runReachability(){
		  analysis.runReachability();
	  }
	  
	  public void testInternalTransform(){
		  Iterator<Unit> iter = this.body.getUnits().iterator();
			while(iter.hasNext()){
				Unit unit = (Unit)iter.next();
				System.out.println("UNIT: "+ unit+", class:"+unit.getClass());
				
				if((unit instanceof JInvokeStmt)){
					System.out.println("--- Not Assignment, but is JVirtualInvokeExpr");
					InvokeExpr stmt = ((JInvokeStmt) unit).getInvokeExpr();
					System.out.println("--- stmt :" + stmt);
					
					List<ValueBox> localList = stmt.getUseBoxes();
					for(int j=1;j<localList.size();j++){
						Value value= ((ValueBox)localList.get(j)).getValue();
						System.out.println("------ Local in invokeStmt: "+ value+" class: "+value.getClass().getName());
					}
					
					SootMethod method = stmt.getMethod();
					
					System.out.println("----- method :"+method);
					int max = method.getParameterCount();
					for(int i=0;i<max;i++){
						Local local = method.getActiveBody().getParameterLocal(i);
						System.out.println("------ Local in method="+local.getName()+" class:"+local.getClass().getName());
					}
					
					
					
				}
				
				if(unit instanceof JAssignStmt){
					if(((JAssignStmt) unit).getRightOp() instanceof JVirtualInvokeExpr){
						System.out.println("--- unit right Op is JVirtualInvokeExpr");
						Value value = ((JAssignStmt) unit).getRightOp();
						SootMethod method = ((soot.jimple.internal.JVirtualInvokeExpr) value).getMethod();
						Iterator<Unit> iterMB = method.getActiveBody().getUnits().iterator();
						while(iterMB.hasNext()){
							Unit unitMB= iterMB.next();
							System.out.println("------ Unit: "+ unitMB+" class: "+unitMB.getClass().getName());
						}
						InvokeExpr stmt = ((JAssignStmt) unit).getInvokeExpr();
						
						List<ValueBox> localList = stmt.getUseBoxes();
						for(int j=1;j<localList.size();j++){
							Value value1= ((ValueBox)localList.get(j)).getValue();
							System.out.println("------ Local in invokeStmt: "+ value1+" class: "+value1.getClass().getName());
						}
						
					}
					Iterator<ValueBox> iterBox= unit.getUseBoxes().iterator();
					while(iterBox.hasNext()){
						ValueBox box = iterBox.next();
						Value value = box.getValue();
						System.out.println("Use: "+value+"class: "+value.getClass().getName());
					}
					iterBox= unit.getDefBoxes().iterator();
					while(iterBox.hasNext()){
						ValueBox box = iterBox.next();
						Value value = box.getValue();
						System.out.println("Def: "+value+"class: "+value.getClass().getName());
					}
					
				}
			}
	  }
	  
}
