package constantpropagation;

public class Content {

	
	public static final String BOTTOM = "BOTTOM";
	public static final String TOP = "TOP";
	public static final String NUMBER = "NUMBER";
	private static String INITIALIZE;
	
			
	private String type;
	public boolean isExtreme;		
	private int number;
	
	public Content(boolean isExtreme, String analysisType) {
		this.isExtreme = isExtreme;
		if(analysisType.compareTo(Analysis.MUST)==0)
			this.type= Content.TOP;
		else
			this.type = Content.BOTTOM;

	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		isExtreme=false;
		this.number = number;
		this.type = this.NUMBER;
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
