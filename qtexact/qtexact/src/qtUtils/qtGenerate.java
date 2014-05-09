package qtUtils;

import java.util.ArrayList;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
/**Class used for generating quasi-threshold graphs
 * 
 * @author Yarko Senyuta
 *
 * @param <V> vertex
 * @param <E> edge
 */
public class qtGenerate<V, E>
{
	/**
	 * generate a completely connected graph of number of edges
	 * @param number size of clique
	 * @return graph
	 */
	public static Graph<Integer, String> qtGraph(int number)
	{
		SparseGraph<Integer, String> graph = new SparseGraph<Integer, String>();
		if (number > 0)
		{
			graph.addEdge("e:" + 1 + "-" + 2, 1, 2);
			int curEdge = 3;
			while (curEdge <= number)
			{
				Integer[] array = new Integer[graph.getVertexCount()];
				array = graph.getVertices().toArray(array);
				for(int v : array)
				{
					graph.addEdge("e:" + curEdge + "-" + v, curEdge, v);
				}
				curEdge++;
			}
		}
		return graph;
	}
	
	/**Generate graph of two cliques of size n1 and n2 that share one vertex (quasi threshold)
	 * 
	 * @param n1 first clique size
	 * @param n2 second clique size
	 * @return graph
	 */
	public static Graph<Integer, String> qtGraph(int n1, int n2)
	{
		SparseGraph<Integer, String> graph = new SparseGraph<Integer, String>();
		
		int curV = 0;
		graph.addVertex(curV++);
		if (n1 > 0)
		{
			while (curV <= n1)
			{
				Integer[] array = new Integer[graph.getVertexCount()];
				array = graph.getVertices().toArray(array);
				for (int v : array)
				{
					graph.addEdge("e:" + curV + "-" + v, curV, v);
				}
				curV++;
			}
		}
		
		
		curV--;
		
		int newCount = 1;
		
		if (n2 > 0)
		{
			while (newCount <= n2)
			{
				for (int i = curV; i < newCount + curV; i++)
				{
					graph.addEdge("e:" + (curV + newCount) + "-" + i, curV + newCount, i);
				}
				newCount++;
			}
		}
		return graph;

		
	}

	
	/**Generate graph of two cliques of vertices n1 and n2 that are connected by one edge (not quasi threshold)
	 * 
	 * @param n1 size of first clique
	 * @param n2 size of second clique
	 * @return graph
	 */
	public static Graph<Integer, String> cliqueJoin(int n1, int n2)
	{
		SparseGraph<Integer, String> graph = new SparseGraph<Integer, String>();
		
		int curV = 0;
		graph.addVertex(curV++);
		if (n1 > 0)
		{
			while (curV <= n1)
			{
				Integer[] array = new Integer[graph.getVertexCount()];
				array = graph.getVertices().toArray(array);
				for (int v : array)
				{
					graph.addEdge("e:" + curV + "-" + v, curV, v);
				}
				curV++;
			}
		}
		
		Integer[] f = new Integer[graph.getVertexCount()];
		
		f = graph.getVertices().toArray(f);
		ArrayList<Integer> fHalf = new ArrayList<Integer>();
		for (int i : f)
		{
			fHalf.add(i);
		}
		
		graph.addVertex(curV);
		
		int newCount = 1;
		
		if (n2 > 0)
		{
			while (newCount <= n2)
			{
				Integer[] array = new Integer[graph.getVertexCount()];
				array = graph.getVertices().toArray(array);
				for (int v : array)
				{
					if (!fHalf.contains(v))
						graph.addEdge("e:" + (curV + newCount) + "-" + v, curV + newCount, v);
				}
				newCount++;
			}
		}
		
		//final edge to join the two halves 
		graph.addEdge("e:" + 0 + "-" + (curV - 1), 0, curV+newCount-1);
		return graph;
		
	}
	
	/**Generate a simple graph with an induced C4
	 * 
	 * @return graph with C4
	 */
	public static Graph<Integer, String> simpleC4()
	{
		SparseGraph<Integer, String> G = new SparseGraph<Integer, String>();
		G.addEdge("e:" + 0 + "-" + 1, 0, 1);
		G.addEdge("e:" + 0 + "-" + 3, 0, 3);
		G.addEdge("e:" + 3 + "-" + 2, 3, 2);
		G.addEdge("e:" + 1 + "-" + 2, 1, 2);
		G.addEdge("e:" + 1 + "-" + 4, 1, 4);
		G.addEdge("e:" + 1 + "-" + 5, 1, 5);
		G.addEdge("e:" + 1 + "-" + 6, 1, 6);

		
		return G;
	}
	

}
