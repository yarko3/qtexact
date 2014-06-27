package branch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import qtUtils.branchingReturnC;
import qtUtils.lexReturnC;
import qtUtils.myEdge;
import search.qtLBFS;
import search.qtLBFSComponents;
import abstractClasses.SearchResult;
import controller.Controller;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
/**
 * class used for editing to quasi threshold using connected components
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public class qtBranchComponents<V> extends qtPan<V> 
{
	/**
	 * constructor
	 * @param controller
	 */
	public qtBranchComponents(Controller<V> controller) {
		super(controller);
		
		if (controller != null)
		{
			output = controller.getOutputFlag();
		}
		else
			output = false;
		
		//use proper search function
		search = new qtLBFSComponents<V>();
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
	 * branching rules specific to connected components 
	 * first separate into separate graphs and then apply rules
	 */
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult) 
	{
		lexReturnC<V> lex = (lexReturnC<V>) searchResult;
		
		//search yields only one connected component, branch on one component
		if (lex.isConnected())
		{
			return super.branchingRules(s, lex);
		}
		//multiple connected components exist
		else
		{
			
			//System.out.println("Component split.");
			
			
			//bound
			int bound = s.getMinMoves().getChanges().size() - s.getChanges().size();
			
			
			//build graphs from connected components
//			Graph<V, Pair<V>> gWithForbidden = connectedCFromVertexSet(s.getG(), lex.getcComponents().removeLast());
			
			LinkedList<Graph<V, Pair<V>>> cGraphs = new LinkedList<Graph<V, Pair<V>>>();
			LinkedList<branchingReturnC<V>> results = new LinkedList<branchingReturnC<V>>();
			for (Set<V> l : lex.getcComponents())
			{
				cGraphs.add(connectedCFromVertexSet(s.getG(), l));
			}
			//branch on known forbidden structure
			
			//fill new minMoves with entire edge set
			branchingReturnC<V> min = null;
			
//			branchingReturnC<V> min = new branchingReturnC<V>(gWithForbidden, ((qtLBFS<V>) search).degSequenceOrder(gWithForbidden));
//			//bound the search by the best solution so far
//			min.setChanges(fillMinMoves(gWithForbidden, bound));
//			min.setMinMoves(min);
//			results.addFirst(rules(new branchingReturnC<V>(gWithForbidden, min.getDeg(), min), lex));
//			//update bound
//			bound -= results.get(0).getMinMoves().getChanges().size();
//			
			
			branchingReturnC<V> t;
			int count = 0;
			
			//get number of graphs we need to keep track of for percent
			if (output)
			{
				for (Graph<V, Pair<V>> g : cGraphs)
				{
					//if component is large enough to care
					if (g.getVertexCount() > 3)
					{
						count++;
					}
				}
			}
			
			
			
			//branch on the rest of the graphs
			for (Graph<V, Pair<V>> g : cGraphs)
			{
				//if component is large enough to care
				if (g.getVertexCount() > 3)
				{
					//fill new minMoves with bounded edge set of component
					min = new branchingReturnC<V>(g, ((qtLBFS<V>) search).degSequenceOrder(g));
					min.setChanges(fillMinMoves(g, bound + s.getChanges().size()));
					min.setMinMoves(min);
					
					t = new branchingReturnC<V>(g, min.getDeg(), clone.deepClone(s.getChanges()), min);
					//set new percent
					t.setPercent(s.getPercent() / count);
					
					results.addFirst(controller.branch(t));
					//update bound
					bound -= (t.getMinMoves().getChanges().size() - s.getChanges().size());
					
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
			min = new branchingReturnC<V>(s.getG(), s.getDeg());
			min.setMinMoves(min);
			//throw all minMoves into a HashSet, so they don't have duplicates
			HashSet<myEdge<V>> temp = new HashSet<myEdge<V>>();
			for (branchingReturnC<V> r : results)
			{
				temp.addAll(r.getMinMoves().getChanges());
			}
			min.getChanges().addAll(temp);
			min.getChanges().addAll(s.getChanges());		
			
			//if this solution is better than current one
			if (s.getMinMoves().getChanges().size() > min.getChanges().size())
			{
				s.setMinMoves(min);
			}
			
			return s;
		}
	}
}
