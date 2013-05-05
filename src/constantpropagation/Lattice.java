package constantpropagation;

import java.util.HashMap;

/** Keeps track of the values of the variables at each program statement (label).
 * 
 * @author Christian Adriano
 *
 */
public class Lattice {

	/** The label to which this lattice represents */
	String label =new String();
	
	/** The values for the free variables */
	HashMap<String,Value> fvMap = new HashMap<String,Value>();
	
	public Lattice(String label, HashMap<String,Value>freeVariables){
		this.label=label;
		this.fvMap=freeVariables;
	}
	
	/** Sets the value of a free variable in the lattice
	 * @param freeVariable the variable that needs to be changed
	 * @param value the new value 
	 * @return true if the variable is in the lattice, false otherwise
	 */
	public boolean setFreeVariableValue(String freeVariable, Value value){
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
	public Value getFreeVariableValue(String freeVariable){
		if(this.fvMap.containsKey(freeVariable))
			return this.fvMap.get(freeVariable);
		else return null;
	}
}
