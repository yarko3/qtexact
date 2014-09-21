package scorer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import abstractClasses.Scorer;

public class familialGroupCentrality<V> extends Scorer<V> 
{

	public familialGroupCentrality(Graph<V, Pair<V>> g) {
		super(g);
	}

	@Override
	public double getVertexScore(V vertex) {
		
		double score = 0;
		int deg = graph.degree(vertex);
		
		for (V n : graph.getNeighbors(vertex))
		{
			int nDeg = graph.degree(n);
			if (deg > nDeg)
			{
				score++;
			}
			if (deg == nDeg)
			{
				score += .5;
			}
			
		}
		
		return score;
	}

}
