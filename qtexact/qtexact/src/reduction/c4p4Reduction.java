package reduction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import branch.qtBranch;
import abstractClasses.Reduction;

public class c4p4Reduction<V> extends Reduction<V> 
{
	qtBranch<V> bStruct;
	Stack<Integer> stack;
	
	
	public c4p4Reduction(qtBranch<V> b)
	{
		super();
		bStruct = b;
		stack = new Stack<Integer>();
	}
	
	@Override
	/**
	 * look for two non-neighbours who make many induced C4s and add an edge there
	 */
	public branchingReturnC<V> reduce(branchingReturnC<V> s) 
	{
		
		int count = 0;
		int bound = s.getMinMoves().getChanges().size() - s.getChanges().size();
		
		//look through first few two non-neighbours
		
		ArrayList<V> vertices = bStruct.getSearch().orderVerticesNonDecreasingDegree(s.getG());
		//number of nodes to be checked
		int max = s.getG().getVertexCount() / 5;
		
		max = s.getG().getVertexCount() / bound;
		
		//for each most connected vertex, check other most connected vertices
		outer:
		for (int i = 1; i < max; i++)
		{
			V v0 = vertices.get(i);
			
			//if at a node with harmless degree, cannot be reduced further
			if (s.getG().getNeighborCount(v0) < s.getMinMoves().getChanges().size() - s.getChanges().size())
				break outer;
			
			for (int j = 0; j < i; j++)
			{
				V v1 = vertices.get(j);
				
				//use C4 reduction
				if (!s.getG().isNeighbor(v0, v1))
				{
					//get all common neighbours
					HashSet<V> all = new HashSet<V>();
					ArrayList<V> common = new ArrayList<V>();
					
					all.addAll(s.getG().getNeighbors(v0));
					
					//get all the duplicates in common, while all neighbours are stored in all
					for (V v : s.getG().getNeighbors(v1))
					{
						if (!all.add(v))
							common.add(v);
					}
					
					//look for independent sets in common neighbours
					for (int k = 0; k < common.size(); k++)
					{
						V temp = common.remove(k);
						boolean flag = true;
						for (V n : common)
						{
							if (!s.getG().isNeighbor(temp, n))
								flag = false;
						}
						
						if (flag == false)
						{
							common.add(k, temp);
						}
						else
							k--;
					}
					
					//if number of induced C4s is greater than the allowed number of moves, add an edge
					if (common.size() > s.getMinMoves().getChanges().size() - s.getChanges().size())
					{
						
						//toApply.addLast(new myEdge<V>(new Pair<V>(v0, v1), true));
						
						s = bStruct.addResult(s, v0, v1);
						count++;
						
						if (count >= bound)
							break outer;
					}
				}
				
				//use P4 reduction
				else
				{
					//edge to be checked
					Pair<V> e = new Pair<V>(v0, v1);
					//if leaving the edge is out of bounds, remove this edge
					if (!s.getChanges().contains(new myEdge<V>(e, true)) && getObstructionCount(e, s.getG()) > s.getMinMoves().getChanges().size() - s.getChanges().size())
					{
						//remove edge
						s = bStruct.deleteResult(s, v0, v1);
						count++;
						
						if (count >= bound)
							break outer;
						
					}
				}
			}
		}
		
	
		
		stack.push(count);
		//bStruct.applyMoves(s, toApply);
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
		int editCount = stack.pop();
		
		for (int i = 0; i < editCount; i++)
		{
			bStruct.revert(s);
		}
		return s;
	}
	

}
