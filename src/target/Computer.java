package target;

public class Computer{
	int init;
	public Computer(int init){
		this.init = init;
	}
	public Computer computeThis(int x, int y){
		System.out.println("I am in Computer "+init);
		return this;

	}
}