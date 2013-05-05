package constantpropagation;

import java.util.HashMap;
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
}
