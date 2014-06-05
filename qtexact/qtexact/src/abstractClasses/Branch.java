package abstractClasses;

import qtUtils.branchingReturnC;

import com.rits.cloning.Cloner;

import controller.Controller;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Class for holding branching rules and search class associated with a particular graph edit
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
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
	public Branch(Controller<V> controller) {
		super();
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
