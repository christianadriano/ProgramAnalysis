import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pointsto.MyTransformer;
import pointsto.PToAnalysis;

import constpropag.Analysis;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Hierarchy;
import soot.Pack;
import soot.PackManager;
import soot.PhaseOptions;
import soot.PointsToAnalysis;
import soot.Scene;
import soot.SceneTransformer;
import soot.Singletons;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.CallGraphBuilder;
import soot.jimple.toolkits.callgraph.Targets;
import soot.MethodOrMethodContext;
import soot.options.CHAOptions;
import soot.options.Options;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.ExceptionalBlockGraph;


/**
 * Entry point for the Points to Analysis
 * @author Christian Adriano
 *
 */
public class MainSoot {

	public MainSoot() {}

	public static void main(String[] args){

		//try{
			System.out.println("----------------------------------------");
			System.out.println("For content of TargetCode3.java, see the file in the current folder");
			/*File file = new File("TargetCode3.java");
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
		*/
		
		System.out.println(" PLEASE WAIT, PROCESSING ");
		MyTransformer myTransformer =  new MyTransformer(); 
		PackManager.v().getPack("jtp").add(
			      new Transform("jtp.myTransform",myTransformer));
			      
		Options.v().parse(args);
		Options.v().set_verbose(false);
		Options.v().set_whole_program(false); 
		SootClass c = Scene.v().forceResolve("TargetCode3", SootClass.HIERARCHY);
		c.setApplicationClass();
		Scene.v().loadNecessaryClasses();
		SootMethod method = c.getMethodByName("main");
		List entryPoints = new ArrayList();
		entryPoints.add(method);
		Scene.v().setEntryPoints(entryPoints);
		
		PackManager.v().runPacks();

		
		myTransformer.runReachability();
		myTransformer.printOutPut();
		//CallGraph cg = Scene.v().getCallGraph();
		//Hierarchy hier = Scene.v().getActiveHierarchy();
	}
	
}
	
