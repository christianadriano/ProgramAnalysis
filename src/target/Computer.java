package target;

public class Computer{
	int init;
	public Computer(int init){
		this.init = init;
	}
	
	public Computer convert(Computer c){
		int y =  c.init * this.init;
		return c;
	}
	
	public Computer computeThis(Computer c, int y){
		//System.out.println("I am in Computer "+init);
		c=c.convert(c);
		return c;
	}
}