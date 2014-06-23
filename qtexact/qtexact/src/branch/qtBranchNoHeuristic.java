package branch;

import java.util.ArrayList;
import java.util.LinkedList;

import controller.Controller;
import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import search.qtLBFS;
import search.qtLBFSNoHeuristic;
import abstractClasses.SearchResult;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * class used for branching into quasi threshold without any heuristics
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public class qtBranchNoHeuristic<V> extends qtBranch<V> 
{
	/**
	 * output flag
	 */
	boolean output;
	
	
	/**
	 * constructor
	 * @param controller
	 */
	public qtBranchNoHeuristic(Controller<V> controller) {
		super(controller);
		if (controller != null)
		{
			output = controller.getOutputFlag();
		}
		else
			output = false;
		//initialize search
		search = new qtLBFSNoHeuristic<V>();
	}

	/**
	 * setup for quasi threshold editing with no heuristic
	 */
	public branchingReturnC<V> setup(Graph<V, Pair<V>> G, int bound) {
		
		//keep proper degree order as an ArrayList<LinkedList<vertex>>
		ArrayList<LinkedList<V>> deg = ((qtLBFS<V>) search).degSequenceOrder(G);
		
		//start with a full minMoves
		branchingReturnC<V> minMoves = new branchingReturnC<V>(G, deg);
		minMoves.setChanges(fillMinMoves(G, bound));
		minMoves.setMinMoves(minMoves);
		branchingReturnC<V> goal = new branchingReturnC<V>(G, deg, minMoves);
		
		//output flags
		if (output)
		{
			goal.setPercent(1);
			controller.setGlobalPercent(0);
		}
		
		return goal;
	}

	/**
	 * branching rules for quasi threshold editing
	 */
	/**
	 * branching rules for quasi threshold editing
	 */
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult) 
	{
		ArrayList<V> lexResult = searchResult.getCertificate().getVertices();
		double oldPercent = s.getPercent();
		
		//C4 has been found
		if (searchResult.getCertificate().getFlag() == -1)
		{
			//result of adding 1 edge to break C4
			//if we did not remove the edge that is about to be added
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / 8);
				}
				
				
				branchingReturnC<V> c4Add1 = controller.branch(addResult(s, lexResult.get(0), lexResult.get(2)));
				//revert changes
				revert(s);		
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
				
				if (c4Add1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Add1.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 8);
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / 8);
				}
				
				branchingReturnC<V> c4Add2 = controller.branch(addResult(s, lexResult.get(1), lexResult.get(3)));
				//revert changes
				if (output)
				{
					s.setPercent(oldPercent);
				}
				
				revert(s);
				//update percentDone
				if (c4Add2.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Add2.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 8);
			
			//results of removing 2 edges to break C4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / 8);
				}
				
				branchingReturnC<V> c4Remove0 = controller.branch(delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2)));
				if (output)
				{
					s.setPercent(oldPercent);
				}
				
				//revert change to global graph
				revert2(s);
				if (c4Remove0.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove0.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 8);
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / 8);
				}
				branchingReturnC<V> c4Remove1 = controller.branch(delete2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3)));
				
				if (output)
				{
					s.setPercent(oldPercent);
				}
				//revert change to global graph
				revert2(s);
				if (c4Remove1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove1.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 8);
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / 8);
				}
				
				branchingReturnC<V> c4Remove2 = controller.branch(delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3)));
				if (output)
				{
					s.setPercent(oldPercent);
				}
				
				//revert change to global graph
				revert2(s);
				if (c4Remove2.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove2.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 8);
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / 8);
				}
				
				branchingReturnC<V> c4Remove3 = controller.branch(delete2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2)));
				
				if (output)
					s.setPercent(oldPercent);
				
				//revert change to global graph
				revert2(s);
				if (c4Remove3.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove3.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 8);
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				if (output)
					//change progress percent
					s.setPercent(oldPercent / 8);
				
				branchingReturnC<V> c4Remove4 = controller.branch(delete2Result(s, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3)));
				
				if (output)
					s.setPercent(oldPercent);
				
				//revert change to global graph
				revert2(s);
				if (c4Remove4.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove4.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 8);
			
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)))
			{
				if (output)
					//change progress percent
					s.setPercent(oldPercent / 8);
				
				branchingReturnC<V> c4Remove5 = controller.branch(delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3)));
				
				if (output)
					s.setPercent(oldPercent);
				
				//revert change to global graph
				revert2(s);
				if (c4Remove5.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove5.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 8);
			
			return s.getMinMoves();
		}
		//P4 has been found
		else
		{
			//add an edge to break P4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false)))
			{
				if (output)
					//change progress percent
					s.setPercent(oldPercent / 5);
				
				branchingReturnC<V> p4Add0 = controller.branch(addResult(s, lexResult.get(0), lexResult.get(2)));
				if (output)
					s.setPercent(oldPercent);
				
				//revert changes
				revert(s);
				
				if (p4Add0.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Add0.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 5);
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
			{
				if (output)
					//change progress percent
					s.setPercent(oldPercent / 5);
				
				branchingReturnC<V> p4Add1 = controller.branch(addResult(s, lexResult.get(1), lexResult.get(3)));
				
				if (output)
					s.setPercent(oldPercent);
				
				//revert changes
				revert(s);
				
				if (p4Add1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Add1.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 5);
			
			//remove an edge to break P4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)))
			{
				if (output)
					//change progress percent
					s.setPercent(oldPercent / 5);
				
				branchingReturnC<V> p4Remove0 = controller.branch(deleteResult(s, lexResult.get(0), lexResult.get(1)));
				
				if (output)
					s.setPercent(oldPercent);
				
				//revert changes to global graph
				revert(s);
				if (p4Remove0.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Remove0.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 5);
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				if (output)
					//change progress percent
					s.setPercent(oldPercent / 5);
				
				branchingReturnC<V> p4Remove1 = controller.branch(deleteResult(s, lexResult.get(1), lexResult.get(2)));
				
				if (output)
					s.setPercent(oldPercent);
				
				//revert changes to global graph
				revert(s);
				if (p4Remove1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Remove1.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 5);
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				if (output)
					//change progress percent
					s.setPercent(oldPercent / 5);
				
				branchingReturnC<V> p4Remove2 = controller.branch(deleteResult(s, lexResult.get(2), lexResult.get(3)));
				
				if (output)
					s.setPercent(oldPercent);
				
				//revert changes to global graph
				revert(s);
				if (p4Remove2.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Remove2.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 5);
			
			return s.getMinMoves();
		}
	}

}
