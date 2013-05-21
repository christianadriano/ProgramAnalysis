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
		//args = new String[1];
		//args[0] = "target.TargetCode3";
		//args[1] = "--w";
		
		try{
			System.out.println("----------------------------------------");
			System.out.println("Content of TargetCode3.java :");
			File file = new File("target.TargetCode3.java");
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
		
		/*PackManager.v().getPack("cg").add(new Transform("cg.myPTAnalysis", 
				new SceneTransformer(){
			
			protected void internalTransform(String phaseName, Map opts){
				System.out.println("Entered internalTransform");
				CHAOptions options = new CHAOptions( opts );
				CallGraphBuilder cg = new CallGraphBuilder();
				cg.build();
				System.out.println("Number of reachable methods: " +Scene.v().getReachableMethods().size() );
				if( options.verbose() ) {
					//G.v().out.println( "Number of reachable methods: " +Scene.v().getReachableMethods().size() );
					
				}
			}}));
		*/
		//Options.v().set_verbose(false);
		//PhaseOptions.v().setPhaseOption("cg", "on");
		
		//soot.Main.main(args);
		
		MyTransformer myTransformer =  new MyTransformer(); 
		PackManager.v().getPack("jtp").add(
			      new Transform("jtp.myTransform",myTransformer));
			      
		Options.v().parse(args);
		Options.v().set_verbose(false);
		Options.v().set_whole_program(true); 
		SootClass c = Scene.v().forceResolve("target.TargetCode3", SootClass.HIERARCHY);
		c.setApplicationClass();
		Scene.v().loadNecessaryClasses();
		SootMethod method = c.getMethodByName("main");
		List entryPoints = new ArrayList();
		entryPoints.add(method);
		Scene.v().setEntryPoints(entryPoints);
		
		PackManager.v().runPacks();

		myTransformer.printOutPut();		
		//CallGraph cg = Scene.v().getCallGraph();
		Hierarchy hier = Scene.v().getActiveHierarchy();
		
		
		
		
    	System.out.println("In internal transform "+ hier);
       // System.out.println(Scene.v().getApplicationClasses());
	
        //Scene.v();
      /*
      Iterator<MethodOrMethodContext> targets = new Targets(
    		  cg.edgesOutOf(method));
        while (targets.hasNext()) {
        	SootMethod tgt = (SootMethod) targets.next();
        	System.out.println(method + " may call " + tgt);
        	Iterator<MethodOrMethodContext> subTargets = new Targets(cg.edgesOutOf(tgt));
        	//System.out.println("subTargets size: "+subTargets.toString());
        	boolean once=true;
        	if(tgt!=null){
        		while (subTargets.hasNext() && once) {
        			SootMethod subTgt = (SootMethod) subTargets.next();
        			System.out.println(tgt+ " may calll " + subTgt);
        			//once=false;
        		}
        	}
        }
		*/
	}




/** Builds an invoke graph using Class Hierarchy Analysis. 
class CHATransformer extends SceneTransformer{

	public CHATransformer( Singletons.Global g ) {}

	public static CHATransformer v() { return G.v().soot_jimple_toolkits_callgraph_CHATransformer(); }

	protected void internalTransform(String phaseName, Map opts){
		CHAOptions options = new CHAOptions( opts );
		CallGraphBuilder cg = new CallGraphBuilder( (PointsToAnalysis) TargetCode3.main() );
		cg.build();
		if( options.verbose() ) {
			G.v().out.println( "Number of reachable methods: " +Scene.v().getReachableMethods().size() );
		}
	}
	*/
	
}
	
