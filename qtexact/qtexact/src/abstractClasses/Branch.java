package abstractClasses;

import qtUtils.branchingReturnC;

import com.rits.cloning.Cloner;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public abstract class Branch<V> 
{
	/**
	 * a search class used for identifying a solution or a certificate to branch on 
	 */
	protected Search<V> search;
	/**
	 * a controller used to run the branching
	 */
	protected Controller<V> controller;
	
	public static Cloner clone = new Cloner();
	
	/**
	 * a Branch datatype needs a controller to run recursively in branchingRules(...)
	 * @param controller
	 */
	public Branch(Controller<V> controller, Search<V> search) {
		super();
		this.search = search;
		this.controller = controller;
	}

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

	public Search<V> getSearch() {
		return search;
	}
}
