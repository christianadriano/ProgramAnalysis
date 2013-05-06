package constpropag;

import java.util.HashMap;
import java.util.Iterator;

 

/**
 * Executes a Join for a MUST or MAY analysis 
 * @author Christian Adriano
 *
 */
public class Join {

	private String type;
	
	private Lattice entryLattice;
	
	private HashMap<String,Edge> arrivingEdges;
	
	private HashMap<String,HashMap<String,String>> mustTable;
	
	private HashMap<String,HashMap<String,String>> mayTable;

	private HashMap<String, Lattice> analysisMap;
	
	public Join(){
		initMustTable();
		initMayTable();
	}
	
	public void setupAnalysis(String type, Lattice entryLattice, HashMap<String,Edge> arrivingEdges, HashMap<String,Lattice> analysisMap){
		this.type = type;
		this.entryLattice = entryLattice;
		this.arrivingEdges = arrivingEdges;
		this.analysisMap = analysisMap;
	}
	
	protected Lattice join(){
		if((type==null)||(entryLattice==null)) //Failure to initialize Join analysis.
			return null;
		else if (arrivingEdges==null) //it is the first node in the graph or it is a desconneted node.
			return this.entryLattice; // does not change the initial lattice by the join operation.
		else{
			Lattice resultLattice=this.entryLattice;
			Iterator<String> iter = arrivingEdges.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				Edge predEdge = arrivingEdges.get(key);
				String predLabel= predEdge.startLabel;
				Lattice predLabelLattice = this.analysisMap.get(predLabel);
				
				Iterator<String> iterFV = predLabelLattice.fvMap.keySet().iterator();
				//Traverse both lattices performing MEET operations (which can be INTERSECTION or UNION)
				while(iterFV.hasNext()){
					String fvKey = iterFV.next();
					Content resultContent = resultLattice.getFreeVariableValue(fvKey);
					Content predContent = predLabelLattice.getFreeVariableValue(fvKey);
				
					if(this.type.compareTo(Analysis.MUST)==0){
						resultContent = intersect(resultContent,predContent);
					}
					else
						if (this.type.compareTo(Analysis.MAY)==0){
							resultContent = union(resultContent,predContent);
						}
					resultLattice.setFreeVariableValue(fvKey, resultContent);
				}
			}
			return resultLattice;
		}
	}
	
	
	public Content intersect(Content entry, Content pred){
		
		String typeEntry = entry.getType();
		String typePred = pred.getType();
		
		HashMap<String,String> cell = this.mustTable.get(typeEntry);
		String cellValue = cell.get(typePred);
		
		Content result;
		
		if(cellValue==null){//Situation 
			if(entry.getNumber() == pred.getNumber()){
				result= new Content(Content.NUMBER);
				result.setNumber(entry.getNumber());
				return result;
			}
			else{
				result = new Content(Content.BOTTOM);
				return result;
			}
		}
		else{
			if(cellValue.compareTo(Content.NUMBER)==0){
				result = new Content(Content.NUMBER);
				if(entry.getType().compareTo(Content.NUMBER)==0)
					result.setNumber(entry.getNumber());
				else
					result.setNumber(pred.getNumber());
				return result;
			}
			else{
				result= new Content(cellValue);
				return result;
			}
		}
	}
	
	protected Lattice mayJoin(){
		Lattice resultLattice=null;
		return resultLattice;
	}
	
	public Content union(Content entry, Content pred){
		
		String typeEntry = entry.getType();
		String typePred = pred.getType();
		
		HashMap<String,String> cell = this.mayTable.get(typeEntry);
		String cellValue = cell.get(typePred);
		
		Content result;
		
		if(cellValue==null){//Situation 
			if(entry.getNumber() == pred.getNumber()){
				result= new Content(Content.NUMBER);
				result.setNumber(entry.getNumber());
				return result;
			}
			else{
				result = new Content(Content.TOP);
				return result;
			}
		}
		else{
			if(cellValue.compareTo(Content.NUMBER)==0){
				result = new Content(Content.NUMBER);
				if(entry.getType().compareTo(Content.NUMBER)==0)
					result.setNumber(entry.getNumber());
				else
					result.setNumber(pred.getNumber());
				return result;
			}
			else{
				result= new Content(cellValue);
				return result;
			}
		}
	}
	
	
	protected void initMustTable(){
		this.mustTable = new HashMap<String,HashMap<String,String>>();
		HashMap<String,String> TOPLineColumnValues= new HashMap<String,String>();
		TOPLineColumnValues.put(Content.TOP, Content.TOP);
		TOPLineColumnValues.put(Content.BOTTOM, Content.BOTTOM);
		TOPLineColumnValues.put(Content.NUMBER, Content.NUMBER);
		this.mustTable.put(Content.TOP, TOPLineColumnValues);
		
		HashMap<String,String> BOTTOMLineColumnValues= new HashMap<String,String>();
		BOTTOMLineColumnValues.put(Content.TOP, Content.BOTTOM);
		BOTTOMLineColumnValues.put(Content.BOTTOM, Content.BOTTOM);
		BOTTOMLineColumnValues.put(Content.NUMBER, Content.BOTTOM);
		this.mustTable.put(Content.BOTTOM, BOTTOMLineColumnValues);
		
		HashMap<String,String> NUMBERLineColumnValues= new HashMap<String,String>();
		NUMBERLineColumnValues.put(Content.TOP, Content.NUMBER);
		NUMBERLineColumnValues.put(Content.BOTTOM, Content.BOTTOM);
		NUMBERLineColumnValues.put(Content.NUMBER, null);
		this.mustTable.put(Content.NUMBER, NUMBERLineColumnValues);
	}
	

	protected void initMayTable(){
		this.mayTable = new HashMap<String,HashMap<String,String>>();
		HashMap<String,String> TOPLineColumnValues= new HashMap<String,String>();
		TOPLineColumnValues.put(Content.TOP, Content.TOP);
		TOPLineColumnValues.put(Content.BOTTOM, Content.TOP);
		TOPLineColumnValues.put(Content.NUMBER, Content.TOP);
		this.mayTable.put(Content.TOP, TOPLineColumnValues);
		
		HashMap<String,String> BOTTOMLineColumnValues= new HashMap<String,String>();
		BOTTOMLineColumnValues.put(Content.TOP, Content.TOP);
		BOTTOMLineColumnValues.put(Content.BOTTOM, Content.BOTTOM);
		BOTTOMLineColumnValues.put(Content.NUMBER, Content.NUMBER);
		this.mayTable.put(Content.BOTTOM, BOTTOMLineColumnValues);
		
		HashMap<String,String> NUMBERLineColumnValues= new HashMap<String,String>();
		NUMBERLineColumnValues.put(Content.TOP, Content.TOP);
		NUMBERLineColumnValues.put(Content.BOTTOM, Content.NUMBER);
		NUMBERLineColumnValues.put(Content.NUMBER, null);
		this.mayTable.put(Content.NUMBER, NUMBERLineColumnValues);
	}
	
}
