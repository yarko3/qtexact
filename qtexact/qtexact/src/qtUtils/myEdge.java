package qtUtils;

import edu.uci.ics.jung.graph.util.Pair;
/**
 * class used for storing an edge of an edit set
 * @author ssd
 *
 * @param <V>
 */
public class myEdge<V> 
{
	//edge stored
	private Pair<V> edge;
	//change flag (false for deletion; true for addition)
	private boolean flag;
	
	public myEdge(Pair<V> e, boolean f)
	{
		setEdge(e);
		setFlag(f);
	}

	public Pair<V> getEdge() {
		return edge;
	}

	public void setEdge(Pair<V> edge) {
		this.edge = edge;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	public String toString()
	{
		if (flag == false)
			return "Delete: " + edge;
		else
			return "Add: " + edge;
	}
	

}
