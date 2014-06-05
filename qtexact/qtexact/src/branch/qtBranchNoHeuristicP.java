package branch;

import java.util.ArrayList;
import java.util.LinkedList;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import search.qtLBFS;
import search.qtLBFSNoHeuristic;
import abstractClasses.SearchResult;
import controller.ControllerP;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * class used for branching into quasi threshold without any heuristics
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public class qtBranchNoHeuristicP<V> extends qtBranch<V> 
{
	/**
	 * constructor
	 * @param controller
	 */
	public qtBranchNoHeuristicP(ControllerP<V> controller) {
		super(controller);
	}

	/**
	 * setup for quasi threshold editing with no heuristic
	 */
	public branchingReturnC<V> setup(Graph<V, Pair<V>> G, int bound) {
		
		search = new qtLBFSNoHeuristic<V>();
		//keep proper degree order as an ArrayList<LinkedList<vertex>>
		ArrayList<LinkedList<V>> deg = ((qtLBFS<V>) search).degSequenceOrder(G);
		
		//start with a full minMoves
		branchingReturnC<V> minMoves = new branchingReturnC<V>(G, deg);
		minMoves.setChanges(fillMyEdgeSet(G, bound));
		minMoves.setMinMoves(minMoves);
		branchingReturnC<V> goal = new branchingReturnC<V>(G, deg, minMoves);
		
		goal.setPercent(1);
		((ControllerP<V>) controller).setGlobalPercent(0);
		return goal;
	}

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
				//change progress percent
				s.setPercent(oldPercent / 8);
				
				branchingReturnC<V> c4Add1 = controller.branch(c4p4AddResult(s, lexResult.get(0), lexResult.get(2)));
				//revert changes
				c4p4AddRevert(s, lexResult.get(0), lexResult.get(2));		
				//revert percent
				s.setPercent(oldPercent);
				if (c4Add1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Add1.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
			{
				//change progress percent
				s.setPercent(oldPercent / 8);
				
				branchingReturnC<V> c4Add2 = controller.branch(c4p4AddResult(s, lexResult.get(1), lexResult.get(3)));
				//revert changes
				
				s.setPercent(oldPercent);
				
				c4p4AddRevert(s, lexResult.get(1), lexResult.get(3));
				//update percentDone
				if (c4Add2.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Add2.getMinMoves());
				}
			}
			//results of removing 2 edges to break C4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				//change progress percent
				s.setPercent(oldPercent / 8);
				
				branchingReturnC<V> c4Remove0 = controller.branch(c4Delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2)));
				
				s.setPercent(oldPercent);
				
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2));
				if (c4Remove0.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove0.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				//change progress percent
				s.setPercent(oldPercent / 8);
				
				branchingReturnC<V> c4Remove1 = controller.branch(c4Delete2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3)));
				
				s.setPercent(oldPercent);
				
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3));
				if (c4Remove1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove1.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				//change progress percent
				s.setPercent(oldPercent / 8);
				
				branchingReturnC<V> c4Remove2 = controller.branch(c4Delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3)));
				
				s.setPercent(oldPercent);
				
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3));
				if (c4Remove2.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove2.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				//change progress percent
				s.setPercent(oldPercent / 8);
				
				branchingReturnC<V> c4Remove3 = controller.branch(c4Delete2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2)));
				
				s.setPercent(oldPercent);
				
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2));
				if (c4Remove3.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove3.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				//change progress percent
				s.setPercent(oldPercent / 8);
				
				branchingReturnC<V> c4Remove4 = controller.branch(c4Delete2Result(s, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3)));
				
				s.setPercent(oldPercent);
				
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3));
				if (c4Remove4.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove4.getMinMoves());
				}
			}
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)))
			{
				//change progress percent
				s.setPercent(oldPercent / 8);
				
				branchingReturnC<V> c4Remove5 = controller.branch(c4Delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3)));
				
				s.setPercent(oldPercent);
				
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3));
				if (c4Remove5.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove5.getMinMoves());
				}
			}
			return s.getMinMoves();
		}
		//P4 has been found
		else
		{
			//add an edge to break P4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false)))
			{
				//change progress percent
				s.setPercent(oldPercent / 5);
				
				branchingReturnC<V> p4Add0 = controller.branch(c4p4AddResult(s, lexResult.get(0), lexResult.get(2)));
				
				s.setPercent(oldPercent);
				
				//revert changes
				c4p4AddRevert(s, lexResult.get(0), lexResult.get(2));
				
				if (p4Add0.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Add0.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
			{
				//change progress percent
				s.setPercent(oldPercent / 5);
				
				branchingReturnC<V> p4Add1 = controller.branch(c4p4AddResult(s, lexResult.get(1), lexResult.get(3)));
				
				s.setPercent(oldPercent);
				
				//revert changes
				c4p4AddRevert(s, lexResult.get(1), lexResult.get(3));
				
				if (p4Add1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Add1.getMinMoves());
				}
			}
			//remove an edge to break P4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)))
			{
				//change progress percent
				s.setPercent(oldPercent / 5);
				
				branchingReturnC<V> p4Remove0 = controller.branch(p4DeleteResult(s, lexResult.get(0), lexResult.get(1)));
				
				s.setPercent(oldPercent);
				
				//revert changes to global graph
				p4DeleteRevert(s,lexResult.get(0), lexResult.get(1));
				if (p4Remove0.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Remove0.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				//change progress percent
				s.setPercent(oldPercent / 5);
				
				branchingReturnC<V> p4Remove1 = controller.branch(p4DeleteResult(s, lexResult.get(1), lexResult.get(2)));
				
				s.setPercent(oldPercent);
				
				//revert changes to global graph
				p4DeleteRevert(s,lexResult.get(1), lexResult.get(2));
				if (p4Remove1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Remove1.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				//change progress percent
				s.setPercent(oldPercent / 5);
				
				branchingReturnC<V> p4Remove2 = controller.branch(p4DeleteResult(s, lexResult.get(2), lexResult.get(3)));
				
				s.setPercent(oldPercent);
				
				//revert changes to global graph
				p4DeleteRevert(s,lexResult.get(2), lexResult.get(3));
				if (p4Remove2.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Remove2.getMinMoves());
				}
			}
			return s.getMinMoves();
		}
	}

}
