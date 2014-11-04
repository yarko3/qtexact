package greedy;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import abstractClasses.Branch;
import abstractClasses.GreedyEdit;

public class clusterGreedy<V> extends GreedyEdit<V> 
{

	public clusterGreedy(Branch<V> b) {
		super(b);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected int getObstructionCount(Graph<V, Pair<V>> g) 
	{
		int count = 0;
		for (V v0  : g.getVertices())
		{
			for (V v1 : g.getNeighbors(v0))
			{
				for (V v2 : g.getNeighbors(v1))
				{
					if (v0.equals(v2))
						continue;
					if (!g.isNeighbor(v0, v2))
						count++;
					
				}
			}
		}
		
		return count/2;
	}

}
