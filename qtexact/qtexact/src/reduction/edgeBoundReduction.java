package reduction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import qtUtils.qtGenerate;
import abstractClasses.Reduction;
import branch.qtBranch;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class edgeBoundReduction<V> extends Reduction<V> 
{
	qtBranch<V> bStruct;
	Stack<Integer> stack;
	
	qtGenerate<V> gen = new qtGenerate<V>();
	
	public edgeBoundReduction(qtBranch<V> b)
	{
		super();
		bStruct = b;
		stack = new Stack<Integer>();
	}
	
	@Override
	public branchingReturnC<V> reduce(branchingReturnC<V> s) 
	{
		LinkedList<myEdge<V>> toDo = new LinkedList<myEdge<V>>();
		
		//for every edge, find C4s and P4s
		ArrayList<Pair<V>> edges = new ArrayList<Pair<V>>();
		edges.addAll(s.getG().getEdges());
		
		for (Pair<V> e : s.getG().getEdges())
		{
			
			//if leaving the edge is out of bounds, remove this edge
			if (!s.getChanges().contains(new myEdge<V>(e, true)) && getObstructionCount(e, s.getG()) > s.getMinMoves().getChanges().size() - s.getChanges().size())
			{
				
				//remove edge
				toDo.add(new myEdge<V>(e, false));
				
				
				//if no more reduction steps are allowed
				if (toDo.size() >= s.getMinMoves().getChanges().size() - s.getChanges().size())
					break;
			}
		}
		
		
		//store the number of last reduction deletions
		stack.push(toDo.size());
		bStruct.applyMoves(s, toDo);
		
		
		return s;
		
	}
	/**
	 * enumerate the number of obstructions on a given edge
	 * 
	 * @param e edge
	 * @param g graph
	 * @return number of C4/P4
	 */
	private int getObstructionCount(Pair<V> e, Graph<V, Pair<V>> g)
	{
		//endpoints
		V v0 = e.getFirst();
		V v1 = e.getSecond();
		
		//neighbours of endpoints
		
		LinkedList<V> v0Neighbours = new LinkedList<V>();
		v0Neighbours.addAll(g.getNeighbors(v0));
		LinkedList<V> v1Neighbours = new LinkedList<V>();
		v1Neighbours.addAll(g.getNeighbors(v1));
		
		v0Neighbours.remove(v1);
		v1Neighbours.remove(v0);
		
		
		//remove common neighbours
		HashSet<V> all = new HashSet<V>();
		HashSet<V> common = new HashSet<V>();
		
		all.addAll(v0Neighbours);
		
		//get all the duplicates in common, while all neighbours are stored in all
		for (V v : v1Neighbours)
		{
			if (!all.add(v))
				common.add(v);
		}
		//remove duplicates
		
		v0Neighbours.removeAll(common);
		v1Neighbours.removeAll(common);
		
		
		//find number of C4s
		HashSet<V> y0 = new HashSet<V>();
		HashSet<V> y1 = new HashSet<V>();
		
		for (V v : v0Neighbours)
		{
			for (V opposite : v1Neighbours)
			{
				if (g.isNeighbor(v, opposite))
				{
					y0.add(v);
					y1.add(opposite);
					break;
				}
			}
		}
		
		int x0 = v0Neighbours.size() - y0.size();
		int x1 = v1Neighbours.size();
		
		v1Neighbours.removeAll(y1);
		
		for (V v : v1Neighbours)
		{
			for (V opposite : y0)
			{
				if (g.isNeighbor(v, opposite))
				{
					y1.add(v);
					break;
				}
			}
		}
		
		x1 -= y1.size();
		
		
		
		
		return Math.max(Math.min(x0 + y0.size(), x1), Math.min(x1 + y1.size(), x0));
	}

	@Override
	/**
	 * undo the reduction
	 */
	public branchingReturnC<V> revertReduce(branchingReturnC<V> s) 
	{
		//return the number of deletes from stack
		int delCount = stack.pop();
		
		for (int i = 0; i < delCount; i++)
		{
			bStruct.revert(s);
		}
		return s;
	}

}
