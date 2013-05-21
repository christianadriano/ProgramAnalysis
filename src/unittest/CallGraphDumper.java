package unittest;

import soot.*;

import java.util.*;
import java.io.*;
import soot.jimple.*;
import soot.util.queue.*;
import soot.jimple.toolkits.callgraph.*;
import soot.options.Options;

public class CallGraphDumper extends SceneTransformer {
    public static void main(String[] args) 
    {
	PackManager.v().getPack("wjop").add(
	    new Transform("wjop.dumpcg", new CallGraphDumper()));

	args = new String[1];
	args[0] = "target.TargetCode3";
	
	Options.v().set_verbose(false);
	PhaseOptions.v().setPhaseOption("wjop", "enabled:true");
	soot.Main.main(args);
    }

    protected void internalTransform(String phaseName, Map options)
    {
    	System.out.println("In internal transform");
        CallGraph cg = Scene.v().getCallGraph();

        Iterator it = cg.listener();
        while( it.hasNext() ) {
            soot.jimple.toolkits.callgraph.Edge e =
                (soot.jimple.toolkits.callgraph.Edge) it.next();
            System.out.println(""+e.src()+e.srcStmt()+" ="+e.kind()+"=> "+e.tgt());
        }
    }
}

