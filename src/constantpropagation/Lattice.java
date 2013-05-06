package constantpropagation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import soot.Unit;
import soot.tagkit.CodeAttribute;
import soot.tagkit.Tag;

/** Keeps track of the values of the variables at each program statement (label).
 * 
 * @author Christian Adriano
 *
 */
public class Lattice {

	/** The label to which this lattice represents */
	String label =new String();
	
	/** The values for the free variables */
	HashMap<String,Content> fvMap = new HashMap<String,Content>();
	
	/** The program statement */
	Unit unit;
	
	public Lattice(Unit unit, HashMap<String,Content>freeVariables){
		List<Tag> list = unit.getTags();
		this.label = ((CodeAttribute) list.get(0)).getName();
		this.fvMap=freeVariables;
		this.unit = unit;
	}
	
	/** Sets the value of a free variable in the lattice
	 * @param freeVariable the variable that needs to be changed
	 * @param value the new value 
	 * @return true if the variable is in the lattice, false otherwise
	 */
	public boolean setFreeVariableValue(String freeVariable, Content value){
		if(this.fvMap.containsKey(freeVariable)){
				this.fvMap.put(freeVariable,value);
				return true;
		}
		else return false;
	}
	
	
	/**
	 * @param freeVariable
	 * @return the value of the variable. If no variable with the provided name exists, returns null.
	 */
	public Content getFreeVariableValue(String freeVariable){
		if(this.fvMap.containsKey(freeVariable))
			return this.fvMap.get(freeVariable);
		else return null;
	}
	
	
	/**
	 * 
	 * @param target the Lattice which content is to be compared against the current Lattice
	 * @return true if all the values of the freeVariables are the same in the both Lattices, false otherwise.
	 */
	public boolean equals(Lattice target){
		if(target==null)
			return false;
		else{
			Iterator<String> iter = this.fvMap.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				Content fvContent = this.fvMap.get(key);
				Content targetContent= target.getFreeVariableValue(key);
				if(fvContent.getType().compareTo(targetContent.getType())!=0) //Two situations might imply in different Lattice. Different types
					return false;
				else // Or different number values
					if((fvContent.getType().compareTo(Content.NUMBER)==0) && (fvContent.getNumber()!=targetContent.getNumber()))
						return false;
			}
			return true;
		}
	}
	
	public String toString(){
		String result="Lattice of label "+label+"={";
		Iterator<String> iter = this.fvMap.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			Content content = this.fvMap.get(key);
			result=result+" "+key+"="+content+",";
		}
		
		result = result.substring(0,result.length()-1);
		result = result+" }";
		return result;
		
	}
}
