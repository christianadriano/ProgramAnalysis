package target;

public class DecrementComputer extends Computer{
	
	public DecrementComputer(int init) {
		super(init);
	}

	public int compute(int x, int y){
		System.out.println("I am in Decrement");
		return y;
	}
}
