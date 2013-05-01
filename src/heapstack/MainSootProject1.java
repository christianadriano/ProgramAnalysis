package heapstack;
import soot.Body;
import soot.BodyTransformer;
import soot.PackManager;
import soot.PhaseOptions;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.Value;
import soot.jimple.JimpleBody;
import soot.jimple.internal.JAssignStmt;
import soot.options.Options;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * Uses Soot to evaluate whether each statement is a Heap read or a Heap write
 * @author Christian Adriano
 *
 */
public class MainSootProject1 {

	public MainSootProject1() {}

	public static void main(String[] args){
		args = new String[1];
		args[0] = "TargetCode1";
		
		try{
			System.out.println("----------------------------------------");
			System.out.println("Content of TargetCode1.java :");
			File file = new File("TargetCode1.java");
			FileInputStream fis = new FileInputStream(file);
			int oneByte;
			while ((oneByte = fis.read()) != -1) {
				System.out.write(oneByte);
			}
			System.out.println("----------------------------------------");
			}
			catch(Exception ex){
				System.out.println("! Please to see the Java File content, put the file TargetCode1.java in the same folder of the JarFile");
				System.out.println();
			}
		
		PackManager.v().getPack("jap").add(new Transform("jap.profiler", 
				new BodyTransformer(){
			protected void internalTransform(Body body, String phase, Map options) {
				System.out.println("Jimple internalTransform result ---------------------------");
				
				SootMethod m = (SootMethod)body.getMethod();
				if(m.isConcrete()){
					JimpleBody jbody = (JimpleBody) m.retrieveActiveBody();
					Iterator unitIt = jbody.getUnits().iterator();
					while(unitIt.hasNext()){
						Unit unit = (Unit) unitIt.next();
						if(unit instanceof soot.jimple.internal.JAssignStmt){
							//System.out.println("JAssignStatement = "+unit.toString());
							checkRightSideToHeapOperation((JAssignStmt)unit);
						}
						//else
						//	System.out.println("Just Unit = "+unit.toString());
					}
				}
				//for (Unit u : body.getUnits()) {
				//	System.out.println(u.getTags());
				//}
			}
		}));
		Options.v().set_verbose(false);
		PhaseOptions.v().setPhaseOption("jap.npc", "on");
		soot.Main.main(args);
	}

	protected static void checkRightSideToHeapOperation(JAssignStmt stmt){

		Value rightBox = stmt.rightBox.getValue();
		Value leftBox = stmt.leftBox.getValue();
		String heapAccessType="no heap access";
		
		//If the LHS/RHS of an assignment is an InstanceFieldRef,
		if(rightBox instanceof soot.jimple.InstanceFieldRef)
			//b = a.f (heap read);
			heapAccessType="heap READ";
		else
			if(leftBox instanceof soot.jimple.InstanceFieldRef)
				//a.f = b(heap write)
				heapAccessType="heap WRITE";

		//If the LHS/RHS of an assignment is a StaticFieldRef, 
			else
				if(rightBox instanceof soot.jimple.StaticFieldRef)
					//b=A.f (heap read);
					heapAccessType="heap READ";
				else
					if(leftBox instanceof soot.jimple.StaticFieldRef)
						//A.f = b(heap write)
						heapAccessType="heap WRITE";

					else

						//If the LHS/RHS of an assignment is an ArrayRef, 
						if(rightBox instanceof soot.jimple.ArrayRef)
							//b=a[i] (heap read);
							heapAccessType="heap READ";
						else
							if(leftBox instanceof soot.jimple.ArrayRef)
								//a[i] = b (heap write)
								heapAccessType="heap WRITE";

		System.out.println(stmt.toString()+" "+ heapAccessType);
	}

}
;