package target;


public class IncrementComputer extends Computer {

	public IncrementComputer(int init) {
		super(init);
	}

	public int compute(int x, int y){
		System.out.println("I am in Increment");
		return x;
	}
	
}
