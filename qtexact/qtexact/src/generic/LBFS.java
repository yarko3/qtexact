package generic;

import java.util.ArrayList;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public abstract class LBFS<V>
{
	public abstract SearchResult<V> search(Graph<V, Pair<V>> G, ArrayList<V> t);

}
