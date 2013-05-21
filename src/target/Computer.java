package target;

public class Computer{
	public int init;
	public Computer(int init){
		this.init = init;
	}
	
	public Computer convert(Computer c){
		int y =  c.init * this.init;
		return c;
	}
	
	public Computer computeThis(Computer c, int y){
		c.convert(c);
		return c;
	}
}