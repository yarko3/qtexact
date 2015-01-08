package reduction;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import abstractClasses.Branch;
import abstractClasses.Reduction;

import com.rits.cloning.Cloner;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class cographReduction<V> extends Reduction<V> 
{
	Branch<V> bStruct;
	Stack<Integer> stack;
	boolean directed;
	
	Cloner clone;
	
	public cographReduction(Branch<V> b)
	{
		super();
		this.bStruct = b;
		stack = new Stack<Integer>();
		directed = b.isDirected();
		clone = new Cloner();
	}
	
	@Override
	public branchingReturnC<V> reduce(branchingReturnC<V> s) {
		//initial number of moves allowed to be made (parameter)
		int bound = s.getMinMoves().getChanges().size() - s.getChanges().size();
		int originalBound = bound;
		
		//use clone for edge modifications
		Graph<V, Pair<V>> cln = clone.deepClone(s.getG());
		
		branchingReturnC<V> clnS = new branchingReturnC<V>(cln);
		
		LinkedList<myEdge<V>> toDo = new LinkedList<myEdge<V>>();
		//traverse every edge
		for (Pair<V> edge : s.getG().getEdges())
		{
			V v0 = edge.getFirst();
			V v1 = edge.getSecond();
			if (bound <= 0)
				break;
			
			//are there too many P4s on this edge?
			if (p4Count(v0, v1, cln) > bound)
			{
				//make edge deletion
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(v0, v1), true, directed)))
				{
					
					bStruct.deleteResult(clnS, v0, v1);
					
					
					toDo.add(new myEdge<V>(new Pair<V>(v0, v1), false, directed));
					
					int count = p4Count(v0, v1, cln);
					
					bound--;
				}
				//this branch cannot continue, move must be made but cannot
				else
				{
					s.setContinueEditing(false);
					break;
				}
			}
		}
		
		//apply moves
		applyMoves(s, toDo);
		
		//record the number of moves made
		stack.push(originalBound - bound);
		
		return s;
	}
	
	private int p4Count(V v0, V v1, Graph<V, Pair<V>> g)
	{
		
		//get neighbours of each vertex
		HashSet<V> v0Neighbours = new HashSet<V>();
		HashSet<V> v1Neighbours = new HashSet<V>();
		
		v0Neighbours.addAll(g.getNeighbors(v0));
		v1Neighbours.addAll(g.getNeighbors(v1));
		
		v0Neighbours.removeAll(g.getNeighbors(v1));
		v1Neighbours.removeAll(g.getNeighbors(v0));
		
		v0Neighbours.remove(v1);
		v1Neighbours.remove(v0);
		
		
		int a0 = 0;
		int a1 = 0;
		int b0 = 0;
		int b1 = 0;
		
//		
		for (V v0N : v0Neighbours)
		{
			boolean flag = true;
			
			for (V v1N : v1Neighbours)
			{
				if (g.isNeighbor(v0N, v1N))
				{
					flag = false;
					break;
				}
			}
			
			if (flag)
				b0++;
			else
				a0++;
		}
		
		for (V v1N : v1Neighbours)
		{
			boolean flag = true;
			
			for (V v0N : v0Neighbours)
			{
				if (g.isNeighbor(v0N, v1N))
				{
					flag = false;
					break;
				}
			}
			
			if (flag)
				b1++;
			else
				a1++;
		}
		
		int temp =  Math.min(b0 + Math.min(a0, b1), b1 + Math.min(a1, b0));
//		
//
		return temp;
		
		}

	@Override
	public branchingReturnC<V> revertReduce(branchingReturnC<V> s) {
		//return the number of modifications from stack
		int editCount = stack.pop();
		
		for (int i = 0; i < editCount; i++)
		{
			bStruct.revert(s);
		}
		return s;
	}
	
	/**
	 * apply moves in LinkedList
	 * @param s branching state
	 * @param toBeMade moves to be applied
	 */
	private void applyMoves(branchingReturnC<V> s, LinkedList<myEdge<V>> toBeMade)
	{
		for (myEdge<V> edge : toBeMade)
		{
			//edge addition
			if (edge.isFlag())
				bStruct.addResult(s, edge.getEdge().getFirst(), edge.getEdge().getSecond());
			else
				bStruct.deleteResult(s, edge.getEdge().getFirst(), edge.getEdge().getSecond());
		}
	}
	

}
