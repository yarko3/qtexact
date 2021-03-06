package components;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import abstractClasses.Branch;
import abstractClasses.Dive;
import abstractClasses.SearchResult;
import controller.Controller;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class branchComponents<V> extends Branch<V> {

	/**
	 * branching structure to be applied after component split
	 */
	private Branch<V> bStruct;
	
	public branchComponents(Controller<V> controller, Branch<V> b) {
		super(controller);
		bStruct = b;
		
		if (controller != null)
		{
			output = controller.getOutputFlag();
		}
		else
			output = true;
		
		//use proper search function
		search = bStruct.getSearch();
		
		//get reductions
		reductions = bStruct.getReductions();
	}

	@Override
	public branchingReturnC<V> setup(Graph<V, Pair<V>> G, int bound) {
		return bStruct.setup(G, bound);
	}

	@Override
	public branchingReturnC<V> setup(Graph<V, Pair<V>> G) {
		return bStruct.setup(G);
	}

	@Override
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s,
			SearchResult<V> sResult) {

		SearchResult<V> lex = sResult;
		
		//search yields only one connected component, branch on one component
		if (lex.isConnected())
		{
			return bStruct.branchingRules(s, lex);
		}
		//multiple connected components exist
		else
		{
			//bound
			int bound = s.getMinMoves().getChanges().size() - s.getChanges().size();
			
			
			//build graphs from connected components
			LinkedList<Graph<V, Pair<V>>> cGraphs = new LinkedList<Graph<V, Pair<V>>>();
			LinkedList<branchingReturnC<V>> results = new LinkedList<branchingReturnC<V>>();
			
			for (Set<V> l : lex.getcComponents())
			{
				//do we even care about editing this component?
				if (l.size() > 1)
					cGraphs.add(connectedCFromVertexSet(s.getG(), l));
			}
			
			
			//create future minMoves
			branchingReturnC<V> min = null;
			
			branchingReturnC<V> t;
			
			int count = cGraphs.size();
			
			//sort connected components in increasing size
			for (int i = 0; i < cGraphs.size(); i++)
			{
				for (int j = 0; j < i; j++)
				{
					if (cGraphs.get(i).getVertexCount() < cGraphs.get(j).getVertexCount())
					{
						cGraphs.add(j, cGraphs.remove(i));
						continue;
					}
				}
			}
//			//remove all trivial components
//			int count = 0;
//			while (cGraphs.getFirst().getVertexCount() < 4)
//			{
//				cGraphs.removeFirst();
//			}
			
			//count = cGraphs.size();
			
			//check which components need editing to be qt
//			LinkedList<Integer> needEdit = new LinkedList<Integer>();
//			
//			for (int i  = 0; i < cGraphs.size(); i++)
//			{
//				Integer temp = lowerBound(cGraphs.get(i));
//				
//				if (temp != 0)
//					count++;
//				needEdit.addLast(temp);
//			}
			
			
			//branch on graphs
			for (int i = 0; i < cGraphs.size(); i++)
			{
				Graph<V, Pair<V>> g = cGraphs.get(i);
				//find the minimum number of moves still required
//				int need = 0;
//				for (int j = i+1; j < cGraphs.size(); j++)
//				{
//					need += needEdit.get(j);
//				}
				
				//success flag
				boolean flag = true;
				
				//does this component need editing and are more moves allowed?
				if (/*needEdit.get(i) > 0 &&*/ bound >= 0 /*&& bound >= need*/ && g.getVertexCount() > 1)
				{
					
					//fill new minMoves with bounded edge set of component
					min = bStruct.setup(g);
					//construct minMoves from bound and number of moves done - number of moves needed for other components
					min.setChanges(fillMinMoves(min, bound + s.getChanges().size() /*- need*/));
					min.setMinMoves(min);
					
					t = new branchingReturnC<V>(g, min.getDeg(), clone.deepClone(s.getChanges()), min);
					
					//set new percent
					t.setPercent(s.getPercent() / count);
						
					
					results.addFirst(controller.branch(t));
					//update bound
					bound -= (t.getMinMoves().getChanges().size() - s.getChanges().size());
					
					//check if component edited successfully (BREAKS IF SOLUTION FOR COMPONENT NOT FOUND)
//					t.getMinMoves().getChanges().removeAll(s.getChanges());
//					applyMoves(t, t.getMinMoves().getChanges());
//					if (!getSearch().isTarget(g))
//						flag = false;
				}				
			}
			
			
			//construct new minMoves from all old ones
			min = new branchingReturnC<V>(s.getG(), s.getDeg());
			min.setMinMoves(min);
			
			//all moves made to each component
			LinkedList<myEdge<V>> temp = new LinkedList<myEdge<V>>();
			
			//temp is now the best moves from this search
			min.setChanges(temp);
			
			temp.addAll(s.getChanges());
			
			//for every component's results
			for (branchingReturnC<V> r : results)
			{
				//remove the previously made moves from result's best so it now contains only the new edits
				//r.getMinMoves().getChanges().removeAll(s.getChanges());
//				
				for (int i = 0; i < s.getChanges().size(); i++)
				{
					//I don't know why sometimes this doesn't work
					if (r.getMinMoves().getChanges().isEmpty())
						break;
					r.getMinMoves().getChanges().removeFirst();
				}
				
				
				//add all new edits to temp
				temp.addAll(r.getMinMoves().getChanges());
			}
			
			
			//if new solution is better than current one, apply new moves to graph
			Graph<V, Pair<V>> rtn = applyMoves(Branch.clone.deepClone(s.getG()), temp);
			
			//add old edits
//			min.getChanges().addAll(s.getChanges());
			
			//is it at goal state?
			boolean success = getSearch().isTarget(rtn);
			
			if (s.getMinMoves().getChanges().size() >= min.getChanges().size() && success)
			{
				
				
				//solution has been found
				s.setMinMoves(min);
				
				//set flag
				s.setSolutionFound(true);
			}
			else
			{
				//we didn't find a solution, do we dive for one?
				if (controller.getUseDive())
				{
					controller.dive(s);
				}
			}
			
			
			return s;
		}
	}

	@Override
	public void revert(branchingReturnC<V> s) {
		bStruct.revert(s);
		
	}

	@Override
	public branchingReturnC<V> deleteResult(branchingReturnC<V> s, V v0, V v1) {
		return bStruct.deleteResult(s, v0, v1);
	}

	@Override
	public branchingReturnC<V> addResult(branchingReturnC<V> s, V v0, V v1) {
		return bStruct.addResult(s, v0, v1);
	}
	
	/**
	 * connected component from vertex set
	 * @param G graph
	 * @param l vertex set
	 * @return subgraph constructed from vertex set
	 */
	@SuppressWarnings("unchecked")
	protected Graph<V, Pair<V>> connectedCFromVertexSet(Graph<V, Pair<V>> G, Set<V> l)
	{
		Graph<V, Pair<V>> c = null;
		try {
			c = (Graph<V, Pair<V>>) G.getClass().newInstance();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashSet<Pair<V>> tempSet = new HashSet<Pair<V>>();
		//throw all edges into hashset, no duplicates
		for (V i : l)
		{
			tempSet.addAll(G.getIncidentEdges(i));
			c.addVertex(i);
		}
		//add all edges to c
		for (Pair<V> e : tempSet)
		{
			Pair<V> edge = clone.deepClone(e);
			c.addEdge(edge, edge.getFirst(), edge.getSecond());
		}
		return c;
	}
	
	/**
	 * returns the minimum number of edits required for a component
	 * @param graph
	 * @return lower bound for connected component
	 */
	public Integer lowerBound(Graph<V, Pair<V>> graph)
	{
		Graph<V, Pair<V>> temp = clone.deepClone(graph);
		branchingReturnC<V> s = bStruct.setup(temp);
		SearchResult<V> result = search.search(s);
		
		int count = 0;
		
		while (!result.isTarget())
		{
			//at least one more move needs to be done
			count++;
			
			//remove all vertices of current obstruction from graph
			for (V v0 : result.getCertificate().getVertices())
			{
				bStruct.removeVertex(s, v0);
			}
			//update search result
			result = search.search(s);
		}
		//free memory
		temp = null;
		s = null;
		result = null;
		
		return count;
		
		
	}

	@Override
	public branchingReturnC<V> removeVertex(branchingReturnC<V> s, V v0) {
		return bStruct.removeVertex(s, v0);
	}

	@Override
	public void revertEdgeDelete(branchingReturnC<V> s, V v0, V v1) {
		bStruct.revertEdgeDelete(s, v0, v1);
		
	}

	@Override
	public void revertEdgeAdd(branchingReturnC<V> s, V v0, V v1) {
		bStruct.revertEdgeAdd(s, v0, v1);
		
	}
	
	
	@Override
	public void setDive(Dive<V> d)
	{
		bStruct.setDive(d);
	}
	
	@Override
	public Dive<V> getDive()
	{
		return bStruct.getDive();
	}

}
