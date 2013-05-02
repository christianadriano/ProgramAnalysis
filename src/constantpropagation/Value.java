package constantpropagation;

public class Value {

	
	public static final String BOTTOM = "BOTTOM";
	public static final String TOP = "TOP";
	private static String INITIALIZE;
			
	private String type;
	public boolean isExtreme;		
	private int number;
	
	public Value(boolean isExtreme, String analysisType) {
		this.isExtreme = isExtreme;
		if(analysisType.compareTo(Analysis.MUST)==0)
			this.type= Value.TOP;
		else
			this.type = Value.BOTTOM;

	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		isExtreme=false;
		this.number = number;
	}

	public String getType() {
		return type;
	}

	/** 
	 * 	 * @param type should be of type Value.TOP or Value.BOTTOM
	 */
	public void setType(String type) {
		isExtreme=true;
		this.type = type;
	}
	
	
	public String toString(){
		if(isExtreme) 
			return type;
		else
			return new Integer(number).toString();	
	}
	
	

}
