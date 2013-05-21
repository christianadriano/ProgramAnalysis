package unittest;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import pointsto.ConstraintGraphFactory;
import pointsto.PToAnalysis;
import soot.Body;
import soot.PatchingChain;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Stmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JReturnVoidStmt;

public class PToAnalysisTest {

	PToAnalysis analysis = new PToAnalysis();
	Body body;

	public PToAnalysisTest(Body body) {
		this.body=body;
	}

	public void investigateReturnStmt(){
		PatchingChain<Unit> units = this.body.getUnits();
		Iterator stmtIt = units.snapshotIterator();

		while(stmtIt.hasNext()){
			Stmt stmt = (Stmt)stmtIt.next();
			System.out.println("Stmt:" + stmt);
			System.out.println("Stmt class: ------------ "+ stmt.getClass().getName());

			//if((stmt instanceof soot.jimple.internal.JReturnStmt) || (unit instanceof soot.jimple.internal.JRetStmt)|| (unit instanceof soot.jimple.internal.JReturnVoidStmt)){

			if (stmt instanceof JReturnStmt){
				List<ValueBox> values = ((JReturnStmt)stmt).getOp().getUseBoxes();
				Value value = values.get(0).getValue();
				System.out.println("JReturnStmt, value="+ value);
			}
			else{ 
				if(stmt instanceof JReturnVoidStmt) {

					System.out.println("JReturnVoidStmt, stmt="+ stmt);
				}
			}
		}
	}

	public void testConstraintGraphTraversal(){
		ConstraintGraphFactory factory = new ConstraintGraphFactory();
		factory.setup();

		analysis.constraintGraph = factory.graph;
		analysis.allocationSiteMap = factory.allocationSiteMap;

		Iterator<Integer> iterSites = analysis.allocationSiteMap.keySet().iterator();
		while(iterSites.hasNext()){
			Integer key = iterSites.next();
			String className = analysis.allocationSiteMap.get(key);
			System.out.println("iter key="+key);
			Iterator<Integer> iterFirstInstance = analysis.constraintGraph.nodesMap.get(key).iterator();
			Integer succKey = iterFirstInstance.next();
			analysis.performReachabilityAnalysis(succKey, className);
		}

	}

	protected void investigateOutputs(){
		Iterator<Unit> unitIter=analysis.getUnits();
		while(unitIter.hasNext()){
			Unit unit = (Unit) unitIter.next();

			if(unit!=null){
				List<ValueBox> uses = unit.getUseBoxes();
				System.out.println("---------------------------------------------------------------------------");
				System.out.println("unit: "+unit);

				for(ValueBox use : uses) {
					Value useValue = use.getValue();
					System.out.println("Use value: " + useValue + ", hashCode="+use.hashCode()+" ,type: " + useValue.getClass());
					List<ValueBox> defs = unit.getDefBoxes();
					for(ValueBox def : defs) {
						Value defValue = def.getValue();
						System.out.println("Def value: " + defValue + ", hashCode="+def.hashCode()+" ,type: " + defValue.getClass());
					}
				}
			}
		}
	}

}
