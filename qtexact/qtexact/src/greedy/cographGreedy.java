package greedy;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import abstractClasses.Branch;
import abstractClasses.GreedyEdit;

public class cographGreedy<V> extends GreedyEdit<V> {

	public cographGreedy(Branch<V> b) {
		super(b);
	}

	@Override
	protected int getObstructionCount(Graph<V, Pair<V>> g) {
		
		int count = 0;
		
		for (V v0 : g.getVertices())
		{
			for (V v1 : g.getNeighbors(v0))
			{
				for (V v2 : g.getNeighbors(v1))
				{
					if (g.isNeighbor(v0, v2) || v0.equals(v2))
						continue;
					
					for (V v3 : g.getNeighbors(v2))
					{
						if (
								g.isNeighbor(v0, v3) ||
								g.isNeighbor(v1, v3) ||
								v1.equals(v3) ||
								v0.equals(v3)
							)
						{
							count++;
						}
					}
				}
			}
		}
		
		return count / 2;
	}

}
