package abstractClasses;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Abstract class for graph scorer
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public abstract class Scorer<V> {
	
	/**
	 * graph to be scored
	 */
	protected Graph<V, Pair<V>> graph;
	
	/**
	 * constructor for scorer
	 * @param g graph to be scored
	 */
	public Scorer(Graph<V, Pair<V>> g)
	{
		graph = g;
	}
	/**
	 * Score vertex
	 * @param vertex vertex to be scored
	 * @return score
	 */
	public abstract double getVertexScore(V vertex);

}
