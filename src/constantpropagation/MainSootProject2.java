package constantpropagation;
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
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.options.Options;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.ExceptionalBlockGraph;
import soot.toolkits.graph.LoopNestTree;
import constantpropagation.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;


/**
 * Entry point for the Constant Propagation Analysis
 * @author Christian Adriano
 *
 */
public class MainSootProject2 {

	public MainSootProject2() {}

	public static void main(String[] args){
		args = new String[1];
		args[0] = "TargetCode2";
		
		try{
			System.out.println("----------------------------------------");
			System.out.println("Content of TargetCode2.java :");
			File file = new File("TargetCode2.java");
			FileInputStream fis = new FileInputStream(file);
			int oneByte;
			while ((oneByte = fis.read()) != -1) {
				System.out.write(oneByte);
			}
			System.out.println("----------------------------------------");
			}
			catch(Exception ex){
				System.out.println("! Please to see the Java File content, put the file TargetCode.java in the same folder of the JarFile");
				System.out.println();
			}
		
		PackManager.v().getPack("jap").add(new Transform("jap.profiler", 
				new BodyTransformer(){
			protected void internalTransform(Body body, String phase, Map options) {
				System.out.println("Constant Propagation result ---------------------------");
				
				System.out.println("**** Blocks ****");
		        BlockGraph blockGraph = new ExceptionalBlockGraph(body);
		        Analysis analysis= new Analysis(blockGraph);
		        
		        for (Block block : blockGraph.getBlocks()) {
		            System.out.println("-------------------------------------------------");
		        	System.out.println("Block:  "+block);
		            Iterator<Unit> unitIt =  block.iterator();
		            while(unitIt.hasNext()){
						Unit unit = (Unit) unitIt.next();
						System.out.println("Unit:  "+unit.toString());
		            }
		            System.out.println("-------------------------------------------------");
		        }
		        System.out.println();
				
		       /** LoopNestTree loopNestTree = new LoopNestTree(body);
		        for (Loop loop : loopNestTree) {
		            System.out.println("Found a loop with head: " + loop.getHead());
		        }
		        
		        System.out.println("**** Body ****");
		        System.out.println(body);
		        System.out.println();
		        */
/*				SootMethod m = (SootMethod)body.getMethod();
				if(m.isConcrete()){
					JimpleBody jbody = (JimpleBody) m.retrieveActiveBody();
					Iterator unitIt = jbody.getUnits().iterator();
					while(unitIt.hasNext()){
						Unit unit = (Unit) unitIt.next();
						System.out.println("Unit:"+unit.toString());
						
						if(unit instanceof soot.jimple.internal.JAssignStmt){
							//System.out.println("JAssignStatement = "+unit.toString());
							//performConstantPropagationAnalysis((JAssignStmt)unit);
						}
						
						//else
						//	System.out.println("Just Unit = "+unit.toString());
					}
				}
				//for (Unit u : body.getUnits()) {
				//	System.out.println(u.getTags());
				//}
			}
			*/
		}}));
		Options.v().set_verbose(false);
		PhaseOptions.v().setPhaseOption("jap.npc", "on");
		soot.Main.main(args);
	}

	protected static void performConstantPropagationAnalysis(JAssignStmt stmt){

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