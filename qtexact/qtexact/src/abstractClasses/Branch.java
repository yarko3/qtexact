package abstractClasses;

import com.rits.cloning.Cloner;

import qtUtils.branchingReturnC;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public abstract class Branch<V> 
{
	protected Search<V> search;
	public static Cloner clone = new Cloner();
	
	/**
	 * set up the search environment (minMoves, etc.)
	 * @param G
	 * @param bound
	 * @return
	 */
	public abstract branchingReturnC<V> setup(Graph<V, Pair<V>> G, int bound);
	
	/**
	 * the branching rules, given an obstruction in SearchResult
	 * @param s
	 * @param sResult
	 * @return
	 */
	public abstract branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> sResult);
	
	
	

}
