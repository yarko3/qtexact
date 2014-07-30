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
	 * @param g
	 * @return true if at target state, false otherwise
	 */
	public abstract boolean isTarget(Graph<V, Pair<V>> g);
	/**
	 * if some steps are required to prepare graph data for search, it is done here
	 * @param s
	 * @return
	 */
	protected abstract SearchResult<V> searchPrep(branchingReturnC<V> s);
	
	public abstract SearchResult<V> search(branchingReturnC<V> s);

}
