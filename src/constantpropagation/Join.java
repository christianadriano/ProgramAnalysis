package constantpropagation;

import java.util.HashMap;
import java.util.Iterator;

/** Executes a Join for a MUST or MAY analysis */
public class Join {

	private String type;
	
	private Lattice entryLattice;
	
	private HashMap<String,Edge> arrivingEdges;
	
	private HashMap<String,HashMap<String,String>> mustTable;
	
	private HashMap<String,HashMap<String,String>> mayTable;
	
	public Join(){
		initMustTable();
		initMayTable();
	}
	
	public void setupAnalysis(String type, Lattice entrylattice, HashMap arrivingEdges){
		this.type = type;
		this.entryLattice = entryLattice;
		this.arrivingEdges = arrivingEdges;
	}
	
	public Lattice join(){
		if((type==null)||(entryLattice==null)||(arrivingEdges==null)) 
			return null;
		
		if(type.compareTo(Analysis.MAY)==0)
			return mayJoin();
		else
			if(type.compareTo(Analysis.MUST)==0)
				return mustJoin();
			else return null;
					
	}
	
	protected Lattice mustJoin(){
		Lattice resultLattice=null;
		return resultLattice;
	}
	
	
	protected Lattice mayJoin(){
		Lattice resultLattice=null;
		return resultLattice;
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
