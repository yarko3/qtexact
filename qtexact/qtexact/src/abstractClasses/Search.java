package abstractClasses;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public abstract class Search<V> 
{
	public abstract boolean isTarget(Graph<V, Pair<V>> g);
	

}
