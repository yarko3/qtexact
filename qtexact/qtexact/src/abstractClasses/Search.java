package abstractClasses;

import qtUtils.branchingReturnC;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * class used to hold a search
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public abstract class Search<V> 
{
	/**
	 * is the current graph at target state
	 * @param g graph
	 * @return true if at target state, false otherwise
	 */
	public abstract boolean isTarget(Graph<V, Pair<V>> g);
	/**
	 * if some steps are required to prepare graph data for search, it is done here
	 * @param s edit state
	 * @return search result
	 */
	protected abstract SearchResult<V> searchPrep(branchingReturnC<V> s);
	
	/**
	 * search graph
	 * @param s edit state
	 * @return search result
	 */
	public abstract SearchResult<V> search(branchingReturnC<V> s);

}
