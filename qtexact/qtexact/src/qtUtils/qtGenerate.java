package qtUtils;

import java.util.ArrayList;
import java.util.Collection;

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
	 * @param number
	 * @return
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

}
