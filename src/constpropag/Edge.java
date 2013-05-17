package constpropag;

import java.util.List;

import javax.swing.text.html.HTMLDocument.Iterator;

import soot.Unit;
import soot.tagkit.CodeAttribute;
import soot.tagkit.Tag;

/**
 * Represent an edge of a callgraph
 * @author Christian Adriano
 *
 */
public class Edge {

	public Unit startUnit; //source label
	public Unit endUnit; //destination label

	public String startLabel;
	public String endLabel;

	public Edge(Unit start,Unit end) {
		this.startUnit = start;
		this.endUnit = end;

		List<Tag> list;
		CodeAttribute tag;

		if(startUnit!=null){
			list = startUnit.getTags();
			Object object = list.get(0);
			tag =null;
			if(object instanceof CodeAttribute){
				tag = ((CodeAttribute) object);
				this.startLabel = tag.getName();
			}
			else{
				this.startLabel = "null";
			}
		}

		if(endUnit!=null){
			list = endUnit.getTags();
			Object object = list.get(0);
			tag =null;
			if(object instanceof CodeAttribute){
				tag = ((CodeAttribute) object);
				this.endLabel = tag.getName();
			}
			else{
				this.endLabel = "null";
			}
		}
	}

	/** format the edge to print it */
	public String toString(){
		return(getKey());
	}

	public String getKey(){
		return("("+startLabel+","+endLabel+")");
	}

	public String getContentKey(){
	//	return("("+startLabel+","+endLabel+")"+": ("+startUnit+")");//+endUnit+")");
		return("("+startLabel+"="+startUnit+")");//+endUnit+")");
	}
}
