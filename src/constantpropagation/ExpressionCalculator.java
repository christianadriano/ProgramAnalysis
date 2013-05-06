
package constantpropagation;

import java.util.*; 

/** Computes the value of an expression having only the following operators:
 * add (+)
 * subtract (-)
 * multiply (*)
 * divide (/)
 * 
 * accepts only integers and outputs integers
 * 
 * @author Christian Adriano
 *
 */
public class ExpressionCalculator {

	private StringTokenizer tokenizer; 
	private String token; 

	/**
	 * Simply insert spaces between numbers and operators
	 * @param line
	 * @return
	 */
	protected String insertSpaces(String line){
		String processedLine=new String();
		StringBuffer buffer = new StringBuffer(line);
		for(int i=0;i<buffer.length();i++){
			String item = buffer.substring(i,i+1);
			processedLine = processedLine +item +" ";
		}
		return processedLine;
	}
	
	public int evaluate(String line) 
	{ 
		if(line==null)
			return 0;
		else{
			line = insertSpaces(line);
			tokenizer = new StringTokenizer(line); 
			token = tokenizer.nextToken();
			double result = expression();
			int intResult = (int) Math.round(result);
			return intResult;
		}
	} 

	private double primary() 
	{ 
		double result; 

		if(token.equals("(")) 
				{ 
			token = tokenizer.nextToken(); 
			result = expression(); 
				} 
		else 
		{ 
			//System.out.println("token: "+token);
			result = Double.valueOf(token).doubleValue(); 
		} 
		
		if(tokenizer.hasMoreTokens()){
			token = tokenizer.nextToken();
		}
		return result; 
	} 

	private double term() 
	{ 
		double nextValue; 
		double result; 

		result = primary(); 

		while(token.equals("*")) 
		{ 
			token = tokenizer.nextToken(); 
			nextValue = primary(); 
			result *= nextValue; 
		} 

		while(token.equals("/")){ 
			token = tokenizer.nextToken();
			nextValue = primary();
			result /= nextValue;
		}
		return result;
	} 

	private double expression(){
		double nextValue;
		double result;

		result = term();

		while(token.equals("+")){
			token = tokenizer.nextToken();
			nextValue = term();
			result += nextValue;
		} 

		while(token.equals("-")){ 
			token = tokenizer.nextToken(); 
			nextValue = term(); 
			result -= nextValue; 
		} 

		return result; 
	} 

	public static void main (String[] args)
	{ 
		Scanner input = new Scanner(System.in);
		input.useDelimiter("\n");
		String line; 

		String choice = "y";
		while(choice.equalsIgnoreCase("y")) 
		{ 
			System.out.print("Enter an expression: "); 
			line = input.next(); 

			if(line.length() == 0) 
			{
				break; 
			}
			if(choice.equalsIgnoreCase("n"))
			{
				System.out.println("Bye.");
				System.exit(0);
			}

			ExpressionCalculator expn = new ExpressionCalculator();
			String expression= "i0+2/i0";
			
			expression = expression.replaceAll("i0", "1");
			System.out.println(expression);
			
			System.out.println("Result is " +expn.evaluate(line)); 
		} 
	} 
} 
