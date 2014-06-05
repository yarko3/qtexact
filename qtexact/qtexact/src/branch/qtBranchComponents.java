package branch;

import java.util.ArrayList;
import java.util.LinkedList;

import controller.qtController;
import qtUtils.branchingReturnC;
import search.qtLBFS;
import search.qtLBFSComponents;
import abstractClasses.SearchResult;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class qtBranchComponents<V> extends qtBranch<V> 
{
	public qtBranchComponents(qtController<V> controller, qtLBFS<V> search) {
		super(controller, search);
	}


	/**
	 * setup for quasi threshold editing with no heuristic
	 */
	public branchingReturnC<V> setup(Graph<V, Pair<V>> G, int bound) {
		
		search = new qtLBFSComponents<V>();
		//keep proper degree order as an ArrayList<LinkedList<vertex>>
		ArrayList<LinkedList<V>> deg = ((qtLBFS<V>) search).degSequenceOrder(G);
		
		//start with a full minMoves
		branchingReturnC<V> minMoves = new branchingReturnC<V>(G, deg);
		minMoves.setChanges(fillMyEdgeSet(G, bound));
		minMoves.setMinMoves(minMoves);
		branchingReturnC<V> goal = new branchingReturnC<V>(G, deg, minMoves);
		return goal;
	}

	
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult) 
	{
		
	}
}