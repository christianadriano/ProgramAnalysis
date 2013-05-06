package constpropag;

public class Content {

	
	public static final String BOTTOM = "BOTTOM";
	public static final String TOP = "TOP";
	public static final String NUMBER = "NUMBER";
				
	private String type;
	private int number;
	
	public Content(String type) {
		this.type = type;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
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
		this.type = type;
	}
	
	
	public String toString(){
		if(type.compareTo(Content.NUMBER)!=0) 
			return type;
		else
			return new Integer(number).toString();	
	}
	
	

}
