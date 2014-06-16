package qtUtils;

import edu.uci.ics.jung.graph.util.Pair;
/**
 * class used for storing an edge of an edit set
 * @author ssd
 *
 * @param <V>
 */
public class myEdge<V> implements Comparable<myEdge<V>>
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

	@Override
	public int compareTo(myEdge<V> arg0) 
	{
		if ((arg0.getEdge().getFirst().equals(edge.getFirst()) && arg0.getEdge().getSecond().equals(edge.getSecond())) || (arg0.getEdge().getSecond().equals(edge.getFirst()) && arg0.getEdge().getFirst().equals(edge.getSecond())))
		{
			if (arg0.isFlag() == flag)
				return 0;
			else
				return -1;
		}
		
		return arg0.getEdge().hashCode() - this.getEdge().hashCode();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object arg0) 
	{
		if (compareTo((myEdge<V>) arg0) == 0)
			return true;
		else
			return false;
	}
	

}
