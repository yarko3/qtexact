package reduction;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import abstractClasses.Branch;
import abstractClasses.Reduction;
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.Graph;
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
		int movesMade = 0;
		
		//reduction rule only works on a connected component
		if (componentClusterer.transform(s.getG()).size() > 1 || bound <= 0)
		{
			stack.push(0);
			return s;
		}
		
		//store moves to be made
		LinkedList<myEdge<V>> toBeMade = new LinkedList<myEdge<V>>();

		//store vertices as a linked list to iterate easily
		LinkedList<V> vertices = orderVerticesByDegree(s.getG());
		
		
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
				//if we cannot do any more moves, stop reduction rule
				if (bound == 0)
					break outer;
				
				//second vertex
				V v1 = vertices.get(j);
				//this should never happen but ok
				if (v0.equals(v1))
					continue;
				
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
				
				//remove the vertices we are dealing with
				tempCombined.remove(v0);
				tempCombined.remove(v1);
				
				boolean okToAdd = false;
				boolean okToRemove = false;
				
				if (tempCombined.size() > bound && s.getG().isNeighbor(v0, v1))
				{
//					okToRemove = true;
//					//see if this edge is a forced addition
//					if (s.getChanges().contains(new myEdge<V>(new Pair<V>(v0, v1), true, directed)))
//					{
//						//stop editing here
//						s.setContinueEditing(false);
//						break outer;
//					}
//					//make this edge deletion
//					if (s.getG().isNeighbor(v0, v1))
//					{
						bStruct.deleteResult(s, v0, v1);
//						toBeMade.add(new myEdge<V>(new Pair<V>(v0, v1), false, directed));
						bound--;
						movesMade++;
//					}
				}
				
				//see if we need to force this edge
				if (tempRetain.size() > bound && !s.getG().isNeighbor(v0, v1))
				{
//					okToAdd = true;
//					//see if this edge is a forced deletion
//					if (s.getChanges().contains(new myEdge<V>(new Pair<V>(v0, v1), false, directed)))
//					{
//						//stop editing here
//						s.setContinueEditing(false);
//						break outer;
//					}
					//make this edge addition
//					if (!s.getG().isNeighbor(v0, v1))
//					{
						bStruct.addResult(s, v0, v1);
						
//						toBeMade.add(new myEdge<V>(new Pair<V>(v0, v1), true, directed));
						bound--;
						movesMade++;
//					}
				}
//				//edge must exist and not exist for optimal solution
//				if (okToAdd && okToRemove)
//				{
//					s.setContinueEditing(false);
//					break outer;
//				}
//				
			}
		}
		
//		//if the best solution cannot be reached from here, stop editing
//		if (!s.isContinueEditing())
//		{
//			stack.push(0);
//			return s;
//		}
//		
//		//apply moves
//		applyMoves(s, toBeMade);
		
		//push how many modifications reduction rule made
		stack.push(movesMade);
		
		return s;
		
		
	}

	/**
	 * order vertices in non-increasing degree
	 * @param g graph
	 * @return ordered list of vertices
	 */
	private LinkedList<V> orderVerticesByDegree(Graph<V, Pair<V>> g) {
		
		LinkedList<V> vertices = new LinkedList<V>();
		
		//throw all into hashtable based on degree key
		
		Hashtable<Integer, LinkedList<V>> hash = new Hashtable<Integer, LinkedList<V>>();
		
		for (V v : g.getVertices())
		{
			int deg = g.degree(v);
			if (!hash.containsKey(deg))
			{
				hash.put(deg, new LinkedList<V>());
			}
			
			hash.get(deg).add(v);
		}
		
		LinkedList<Integer> keys = new LinkedList<Integer>();
		keys.addAll(hash.keySet());
		
		Collections.sort(keys);
		
		for (int i = 0; i < keys.size(); i++)
		{
			vertices.addAll(hash.get(keys.get(i)));
		}
		
		return vertices;
	}

	@Override
	public branchingReturnC<V> revertReduce(branchingReturnC<V> s) {
		//return the number of modifications from stack
		int editCount = stack.pop();
		
		bStruct.revert(s, editCount);
		
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
