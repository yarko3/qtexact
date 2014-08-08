package reduction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import qtUtils.branchingReturnC;
import abstractClasses.Reduction;
import branch.qtBranch;
import edu.uci.ics.jung.algorithms.cluster.BicomponentClusterer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class biconnectedReduction<V> extends Reduction<V> {
	
	qtBranch<V> bStruct;
	Stack<Integer> stack;
	
	public biconnectedReduction(qtBranch<V> b)
	{
		super();
		bStruct = b;
		stack = new Stack<Integer>();
	}
	
	
	@Override
	public branchingReturnC<V> reduce(branchingReturnC<V> s) {
		BicomponentClusterer<V, Pair<V>> cluster = new BicomponentClusterer<V, Pair<V>>();
		
		//get the clusters
		Set<Set<V>> components = cluster.transform((UndirectedGraph<V, Pair<V>>) s.getG());
			
		//number of edges deleted as a result of the reduction
		int count = 0;
		//number of allowed moves
		int bound = s.getMinMoves().getChanges().size() - s.getChanges().size();
		
		
		//get cut edges
		List<Pair<V>> cutEdges = new LinkedList<Pair<V>>();
		for (Set<V> c : components)
		{
			//if biconnected component is of size 2, should contain cut edge
			if (c.size() == 2)
			{
				Iterator<V> iterator = c.iterator();
				cutEdges.add(s.getG().findEdge(iterator.next(), iterator.next()));
			}
		}
		
		//organize cut edges in order of smallest component size difference
		
		
		
		
		//find P4 with cut edges
		for (Pair<V> edge : cutEdges)
		{
			//if the edge endpoints contain more than one vertex, a P4 exists
			if (s.getG().getNeighborCount(edge.getFirst()) > (bound-count)+1 && s.getG().getNeighborCount(edge.getSecond()) > (bound-count)+1)
			{
				//remove edge
				s = bStruct.deleteResult(s, edge.getFirst(), edge.getSecond());
				count++;
				
				//if all the moves allowed have been made, break
				if (count > bound)
				{
					break;
				}
			}
			//store edge to be edited later
			else
			{
				if (s.getG().getNeighborCount(edge.getFirst()) > 1 && s.getG().getNeighborCount(edge.getSecond()) > 1)
					s.getKnownBadEdges().add(edge);
			}
		}
		
		stack.push(count);
		
		return s;
	}

	@Override
	public branchingReturnC<V> revertReduce(branchingReturnC<V> s) 
	{
		//return the number of deletes from stack
		int editCount = stack.pop();
		
		for (int i = 0; i < editCount; i++)
		{
			bStruct.revert(s);
		}
		return s;
	}
	
	
	private Set<Graph<V, Pair<V>>> graphsFromSets(branchingReturnC<V> s, Set<Set<V>> components)
	{
		Set<Graph<V, Pair<V>>> graphs = new HashSet<Graph<V, Pair<V>>>();
		
		for (Set<V> set : components)
		{
			SparseGraph<V, Pair<V>>	g = new SparseGraph<V, Pair<V>>();
			
			for (V v1 : set)
			{
				for (V v2 : set)
				{
					if (s.getG().isNeighbor(v1, v2))
					{
						g.addEdge(new Pair<V>(v1, v2), v1, v2);
					}
				}
			}
			
			graphs.add(g);
		}
		
		return graphs;
	}
	

}
