package utils;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class graphUtils<V> 
{
	/**
	 * generate complement graph 
	 * @param g input graph
	 * @return undirected complement graph
	 */
	public Graph<V, Pair<V>> complement(Graph<V, Pair<V>> g)
	{
		Graph<V, Pair<V>> comp = new UndirectedSparseGraph<V, Pair<V>>();
		
		//add all vertices
		for (V v : g.getVertices())
		{
			comp.addVertex(v);
		}
		
		for (V v0 : g.getVertices())
		{
			for (V v1 : g.getVertices())
			{
				if (v0.equals(v1))
					continue;
				
				if (!g.isNeighbor(v0, v1))
				{
					comp.addEdge(new Pair<V>(v0, v1), v0, v1);
				}
			}
		}
		
		return comp;
	}

}
