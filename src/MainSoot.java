
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
import constpropag.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;


/**
 * Entry point for the Constant Propagation Analysis
 * @author Christian Adriano
 *
 */
public class MainSoot {

	public MainSoot() {}

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
		        BlockGraph blockGraph = new ExceptionalBlockGraph(body);
		        Analysis analysis1= new Analysis(blockGraph, body, Analysis.MUST);
		        if(analysis1.hasFreeVariables()){
		        	analysis1.run();
		        	analysis1.printResult();
		        }
		        Analysis analysis2= new Analysis(blockGraph, body, Analysis.MAY);
		        if(analysis2.hasFreeVariables()){
		        	analysis2.run();
		        	analysis2.printResult();
		        }
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