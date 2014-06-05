package search;

import java.util.ArrayList;

import qtUtils.lexReturnC;
import abstractClasses.Search;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
/**
 * an abstract class used as the backbone of further LBFS searches
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public abstract class LBFS<V> extends Search<V>
{
	/**
	 * a search which returns a SearchResult object
	 * @param G
	 * @param t
	 * @return the result of search
	 */
	public abstract lexReturnC<V> search(Graph<V, Pair<V>> G, ArrayList<V> t);
	

}
