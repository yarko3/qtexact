package reduction;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import abstractClasses.Branch;
import abstractClasses.Reduction;
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.util.Pair;

public class clusterReductionBasic<V> extends Reduction<V> {

	private WeakComponentClusterer<V, Pair<V>> componentClusterer;
	
	/**
	 * constructor
	 * @param b
	 */
	public clusterReductionBasic(Branch<V> b)
	{
		super();
		bStruct = b;
		stack = new Stack<Integer>();
		directed = b.isDirected();
		componentClusterer = new WeakComponentClusterer<V, Pair<V>>();
	}
	
	Branch<V> bStruct;
	/**
	 * stack for storing moves made
	 */
	Stack<Integer> stack;
	/**
	 * look through every forced addition and add all neighbours
	 * look through each deletion and remove all neighbours
	 */
	@Override
	public branchingReturnC<V> reduce(branchingReturnC<V> s) {
		
		//store original move count
		int ogCount = s.getChanges().size();
		int bound = s.getMinMoves().getChanges().size() - ogCount;
		
		//reduction rule only works on a connected component
		if (componentClusterer.transform(s.getG()).size() > 1 || bound <= 0)
		{
			stack.push(0);
			return s;
		}
		
		//store moves to be made
		LinkedList<myEdge<V>> toBeMade = new LinkedList<myEdge<V>>();

		//store vertices as a linked list to iterate easily
		//TODO sort in decreasing degree
		LinkedList<V> vertices = new LinkedList<V>();
		vertices.addAll(s.getG().getVertices());
		
		outer:
		for (int i = 0; i < vertices.size(); i++)
		{
			//first vertex
			V v0 = vertices.get(i);
			//neighbours of first vertex
			Collection<V> v0Neighbours = s.getG().getNeighbors(v0);
			
			//look through all vertices after the first vertex
			for (int j = i+1; j < vertices.size(); j++)
			{
				//second vertex
				V v1 = vertices.get(j);
				//this should never happen but ok
				if (v0.equals(v1))
					continue;
				//if we cannot do any more moves, stop reduction rule
				if (bound == 0)
					break outer;
				
				//check for number of common neighbours (if # common neighbours > bound)
				Collection<V> v1Neighbours = s.getG().getNeighbors(v1);
				
				Set<V> tempRetain = new HashSet<V>();
				Set<V> tempCombined = new HashSet<V>();
				tempRetain.addAll(v0Neighbours);
				tempCombined.addAll(v0Neighbours);
				
				//tempRetain now contains all intersecting neighbours
				tempRetain.retainAll(v1Neighbours);
				//tempCombined now contains union of neighbours
				tempCombined.addAll(v1Neighbours);
				
				//tempCombined now contains exclusive or neighbours
				tempCombined.removeAll(tempRetain);
				
				boolean okToAdd = false;
				boolean okToRemove = false;
				
				//see if we need to force this edge
				if (tempRetain.size() > bound)
				{
					okToAdd = true;
					//see if this edge is a forced deletion
					if (s.getChanges().contains(new myEdge<V>(new Pair<V>(v0, v1), false, directed)))
					{
						//stop editing here
						s.setContinueEditing(false);
						break outer;
					}
					//make this edge addition
					if (!s.getG().isNeighbor(v0, v1))
					{
						//bStruct.addResult(s, v0, v1);
						
						toBeMade.add(new myEdge<V>(new Pair<V>(v0, v1), true, directed));
						bound--;
					}
				}
				if (tempCombined.size() > bound)
				{
					okToRemove = true;
					//see if this edge is a forced addition
					if (s.getChanges().contains(new myEdge<V>(new Pair<V>(v0, v1), true, directed)))
					{
						//stop editing here
						s.setContinueEditing(false);
						break outer;
					}
					//make this edge deletion
					if (s.getG().isNeighbor(v0, v1))
					{
						//bStruct.deleteResult(s, v0, v1);
						toBeMade.add(new myEdge<V>(new Pair<V>(v0, v1), false, directed));
						bound--;
					}
				}
				//edge must exist and not exist for optimal solution
				if (okToAdd && okToRemove)
				{
					s.setContinueEditing(false);
					break outer;
				}
				
			}
		}
		
		//if the best solution cannot be reached from here, stop editing
		if (!s.isContinueEditing())
		{
			stack.push(0);
			return s;
		}
		
		//apply moves
		applyMoves(s, toBeMade);
		
		//push how many modifications reduction rule made
		stack.push(toBeMade.size());
		
		return s;
		
		
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
