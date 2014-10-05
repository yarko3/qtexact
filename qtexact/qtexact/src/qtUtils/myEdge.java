package qtUtils;

import edu.uci.ics.jung.graph.util.Pair;
/**
 * class used for storing an edge of an edit set
 * @author Yaroslav Senyuta
 *
 * @param <V> vertex
 */
public class myEdge<V> implements Comparable<myEdge<V>>
{
	//edge stored
	private Pair<V> edge;
	//change flag (false for deletion; true for addition)
	private boolean flag;
	
	/**
	 * is the graph directed?
	 */
	private boolean directed;
	
	/**
	 * constructor
	 * @param e edge
	 * @param f flag
	 */
	public myEdge(Pair<V> e, boolean f, boolean d)
	{
		setEdge(e);
		setFlag(f);
		setDirected(d);
	}

	/**
	 * @return the directed
	 */
	public boolean isDirected() {
		return directed;
	}

	/**
	 * @param directed the directed to set
	 */
	public void setDirected(boolean directed) {
		this.directed = directed;
	}

	/**
	 * get the stored edge
	 * @return edge
	 */
	public Pair<V> getEdge() {
		return edge;
	}

	/**
	 * set the edge stored
	 * @param edge stored edge
	 */
	public void setEdge(Pair<V> edge) {
		this.edge = edge;
	}

	/**
	 * returns false for deletion, true for addition
	 * @return false for deletion, true for addition
	 */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * set flag of this edit
	 * @param flag set true for addition, false for deletion
	 */
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
		if (!directed)
		{
			if ((arg0.getEdge().getFirst().equals(edge.getFirst()) && arg0.getEdge().getSecond().equals(edge.getSecond())) || (arg0.getEdge().getSecond().equals(edge.getFirst()) && arg0.getEdge().getFirst().equals(edge.getSecond())))
			{
				if (arg0.isFlag() == flag)
					return 0;
				else
					return -1;
			}
		}
		//check as a directed graph
		else
		{
			if ((arg0.getEdge().getFirst().equals(edge.getFirst()) && arg0.getEdge().getSecond().equals(edge.getSecond())))
			{
				if (arg0.isFlag() == flag)
					return 0;
				else
					return -1;
			}
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
