package abstractClasses;

import java.util.ArrayList;
import java.util.LinkedList;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public abstract class LBFS<V> extends Search<V>
{
	public abstract SearchResult<V> search(Graph<V, Pair<V>> G, ArrayList<V> t);
	
	public abstract SearchResult<V> searchPrep(Graph<V, Pair<V>> G, ArrayList<LinkedList<V>> l);

}
