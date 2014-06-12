package branch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import controller.Controller;
import qtUtils.branchingReturnC;
import qtUtils.lexReturnC;
import qtUtils.myEdge;
import search.qtLBFS;
import search.qtLBFSComponents;
import abstractClasses.SearchResult;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
/**
 * class used for editing to quasi threshold using connected components
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public class qtBranchComponents<V> extends qtBranch<V> 
{
	/**
	 * constructor
	 * @param controller
	 */
	public qtBranchComponents(Controller<V> controller) {
		super(controller);
	}


	/**
	 * setup for quasi threshold editing with no heuristic
	 */
	public branchingReturnC<V> setup(Graph<V, Pair<V>> G, int bound) {
		//use proper search function
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

	/**
	 * branching rules specific to connected components 
	 * first separate into separate graphs and then apply rules
	 */
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult) 
	{
		lexReturnC<V> lex = (lexReturnC<V>) searchResult;
		
		//search yields only one connected component, branch on one component
		if (lex.isConnected())
		{
			return rules(s, lex);
		}
		//multiple connected components exist
		else
		{
			//build graphs from connected components
			Graph<V, Pair<V>> gWtihForbidden = connectedCFromVertexSet(s.getG(), lex.getcComponents().removeLast());
			
			LinkedList<Graph<V, Pair<V>>> cGraphs = new LinkedList<Graph<V, Pair<V>>>();
			LinkedList<branchingReturnC<V>> results = new LinkedList<branchingReturnC<V>>();
			for (HashSet<V> l : lex.getcComponents())
			{
				cGraphs.add(connectedCFromVertexSet(s.getG(), l));
			}
			//branch on known forbidden structure
			
			//fill new minMoves with entire edge set
			branchingReturnC<V> min = new branchingReturnC<V>(gWtihForbidden, s.getDeg());
			//bound the search by the best solution so far
			min.setChanges(fillMyEdgeSet(gWtihForbidden, s.getMinMoves().getChanges().size() - s.getChanges().size()));
			min.setMinMoves(min);
			results.add(rules(new branchingReturnC<V>(gWtihForbidden, ((qtLBFS<V>) search).degSequenceOrder(gWtihForbidden), min), lex));
			//branch on the rest of the graphs
			for (Graph<V, Pair<V>> g : cGraphs)
			{
				//if component is large enough to care
				if (g.getVertexCount() > 3)
				{
					//fill new minMoves with bounded edge set of component
					min = new branchingReturnC<V>(g, s.getDeg());
					min.setChanges(fillMyEdgeSet(g, s.getMinMoves().getChanges().size() - s.getChanges().size()));
					min.setMinMoves(min);
					results.add(controller.branch(new branchingReturnC<V>(g, ((qtLBFS<V>)search).degSequenceOrder(g), new LinkedList<myEdge<V>>(), min)));
				}
				//don't care about branching on this but still need it to build up the solution later
				else
				{
					//empty minMoves 
					min = new branchingReturnC<V>(g, s.getDeg());
					min.setMinMoves(min);
					results.add(new branchingReturnC<V>(g, ((qtLBFS<V>)search).degSequenceOrder(g), min));
				}		
			}
			
			//construct new minMoves from all old ones
			min = new branchingReturnC<V>(s.getG(), s.getDeg(), min);
			//throw all minMoves into a HashSet, so they don't have duplicates
			HashSet<myEdge<V>> temp = new HashSet<myEdge<V>>();
			for (branchingReturnC<V> r : results)
			{
				temp.addAll(r.getMinMoves().getChanges());
			}
			min.getChanges().addAll(temp);
			min.getChanges().addAll(s.getChanges());		
			
			return min;
		}
	}
	
	/**
	 * branching rules for connected component search
	 * @param s current state of edit
	 * @param searchResult search result
	 * @return an exhausted edit state
	 */
	private branchingReturnC<V> rules(branchingReturnC<V> s, lexReturnC<V> searchResult)
	{
		
		//C4 has been found
		ArrayList<V> lexResult = searchResult.getCertificate().getVertices();
		if (searchResult.getCertificate().getFlag() == -1)
		{
			//result of adding 1 edge to break C4
			//if we did not remove an edge that is about to be added
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false)))
			{
				branchingReturnC<V> c4Add1 = controller.branch(addResult(s, lexResult.get(0), lexResult.get(2)));
				//revert changes
				revert(s);
				if (c4Add1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Add1.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
			{
				branchingReturnC<V> c4Add2 = controller.branch(addResult(s, lexResult.get(1), lexResult.get(3)));
				//revert changes
				revert(s);
				if (c4Add2.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Add2.getMinMoves());
				}
			}
			//results of removing 2 edges to break C4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				branchingReturnC<V> c4Remove0 = controller.branch(delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2)));
				//revert change to global graph
				revert2(s);
				if (c4Remove0.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove0.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				branchingReturnC<V> c4Remove1 = controller.branch(delete2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3)));
				//revert change to global graph
				revert2(s);
				if (c4Remove1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove1.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				branchingReturnC<V> c4Remove2 = controller.branch(delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3)));
				//revert change to global graph
				revert2(s);
				if (c4Remove2.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove2.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				branchingReturnC<V> c4Remove3 = controller.branch(delete2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2)));
				//revert change to global graph
				revert2(s);
				if (c4Remove3.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove3.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				branchingReturnC<V> c4Remove4 = controller.branch(delete2Result(s, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3)));
				//revert change to global graph
				revert2(s);
				if (c4Remove4.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove4.getMinMoves());
				}
			}
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)))
			{
				branchingReturnC<V> c4Remove5 = controller.branch(delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3)));
				//revert change to global graph
				revert2(s);
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
			
			//------------------------------------------------------------------------------------------------------------
			//add an edge to break P4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false)))
			{
				branchingReturnC<V> p4Add0 = controller.branch(addResult(s, lexResult.get(0), lexResult.get(2)));
				//revert changes
				revert(s);
				if (p4Add0.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Add0.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
			{
				branchingReturnC<V> p4Add1 = controller.branch(addResult(s, lexResult.get(1), lexResult.get(3)));
				//revert changes
				revert(s);
				if (p4Add1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Add1.getMinMoves());
				}
			}
			//remove an edge to break P4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)))
			{
				branchingReturnC<V> p4Remove0 = controller.branch(deleteResult(s, lexResult.get(0), lexResult.get(1)));
				//revert changes to global graph
				revert(s);
				if (p4Remove0.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Remove0.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				branchingReturnC<V> p4Remove1 = controller.branch(deleteResult(s, lexResult.get(1), lexResult.get(2)));
				//revert changes to global graph
				revert(s);
				if (p4Remove1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Remove1.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				branchingReturnC<V> p4Remove2 = controller.branch(deleteResult(s, lexResult.get(2), lexResult.get(3)));
				//revert changes to global graph
				revert(s);
				if (p4Remove2.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Remove2.getMinMoves());
				}
			}
			return s.getMinMoves();
		}
	}
}
