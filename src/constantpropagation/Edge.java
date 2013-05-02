package constantpropagation;

/**
 * Represent an edge of a callgraph
 * @author Christian Adriano
 *
 */
public class Edge {

	public int start; //source label
	public int end; //destination label
	
	public Edge(int start,int end) {
		this.start = start;
		this.end = end;
	}

}
