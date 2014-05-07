package qtUtils;

import java.util.Collection;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;

public class qtGenerate<V, E>
{
	public static Graph<Integer, String> qtGraph(int number)
	{
		SparseGraph<Integer, String> graph = new SparseGraph<Integer, String>();
		if (number > 0)
		{
			graph.addEdge("e:" + 1 + "-" + 2, 1, 2);
			int curEdge = 3;
			int level = 1;
			while (level <= number)
			{
				Integer[] array = new Integer[graph.getVertexCount()];
				array = graph.getVertices().toArray(array);
				for(int v : array)
				{
					graph.addEdge("e:" + curEdge + "-" + v, curEdge, v);
				}
				curEdge++;
				level++;
			}
		}
		return graph;
		
		
		
	}
	

}
