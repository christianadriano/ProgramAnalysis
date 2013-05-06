package constpropag;

import java.util.Iterator;

/**
 * Performs kill operation by means of computing the expression on the right hand side 
 * of the statement
 * @author Christian Adriano
 *
 */
public class KillCompute {
	
	private String expression;
	private Lattice lattice;


	public boolean setupKill(String expression, Lattice newLattice){
		if((expression==null) || (newLattice==null))
			return false;
		else{
			this.expression = expression;
			this.lattice = newLattice;
			return true;
		}
	}
	
	
	/**
	 * 
	 * @return the new value for the left side of the expression, if cannot compute (because there is no left side), then return null 
	 */
	public Content compute(){
		
		Iterator<String> iter = this.lattice.fvMap.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			//System.out.println("key: "+key+", indexOf= "+expression.indexOf(key));
			if(expression.indexOf(key)>=0){//Means the expression contains the free variable
				Content fvContent = this.lattice.getFreeVariableValue(key);
				
				if(fvContent.getType().compareTo(Content.NUMBER)==0){
					int keyValue = fvContent.getNumber();
					//Search the freeVariable in the expression and substitute it for the keyValue
					String keyValueStr = new Integer(keyValue).toString();
					expression = expression.replace(key, keyValueStr);
				}
				else//Contains the freeVariable, but the its value is TOP or BOTTOM
					return null;
			}
			//Does not contain the freeVariable, so keep expression the way it is	 
		}
		
		if(expression!=null){
			ExpressionCalculator calculator = new ExpressionCalculator();
			int value = calculator.evaluate(expression);
			//System.out.println("value="+ value);
			Content content = new Content(Content.NUMBER); 
			content.setNumber(value);
			return content;
		}
		else
			return null;
	}
			
}
