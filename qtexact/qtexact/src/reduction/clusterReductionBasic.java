package reduction;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import abstractClasses.Branch;
import abstractClasses.Reduction;
import edu.uci.ics.jung.graph.util.Pair;

public class clusterReductionBasic<V> extends Reduction<V> {

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
		
		LinkedList<myEdge<V>> movesToMake = new LinkedList<myEdge<V>>();
		
		for (myEdge<V> move : s.getChanges())
		{
			//an addition was made
			if (move.isFlag() && s.getG().containsVertex(move.getEdge().getFirst()) &&
					s.getG().containsVertex(move.getEdge().getSecond()))
			{
				//add edges between all neighbours of added edge
				HashSet<V> n = new HashSet<V>();
				Collection<V> temp = s.getG().getNeighbors(move.getEdge().getFirst());
				n.addAll(s.getG().getNeighbors(move.getEdge().getFirst()));
				
				n.retainAll(s.getG().getNeighbors(move.getEdge().getSecond()));
				
				HashSet<V> uncommon = new HashSet<V>();
				uncommon.addAll(s.getG().getNeighbors(move.getEdge().getFirst()));
				uncommon.addAll(s.getG().getNeighbors(move.getEdge().getSecond()));
				
				uncommon.removeAll(n);
				
				//now uncommon contains all vertices that are neighbours of one vertex of forced add but not the other
				
				for (V v0 : uncommon)
				{
					V v1;
					if (s.getG().isNeighbor(move.getEdge().getFirst(), v0))
						v1 = move.getEdge().getFirst();
					else
						v1 = move.getEdge().getSecond();
					
					
					//see if addition was already made or deletion was made
					myEdge<V> newMove = new myEdge<V>(new Pair<V>(v0, v1), true, directed);
					
				
					//see if we can add the edge
					if (
							!s.getG().isNeighbor(v0, v1) &&
							!s.getChanges().contains(newMove) && 
							!movesToMake.contains(newMove) && 
							s.getChanges().size() < s.getMinMoves().getChanges().size())
					{
						movesToMake.add(newMove);
					}
						
				}
				
				
				
			}
		}
		int bound = s.getMinMoves().getChanges().size() - ogCount;
		//make moves
		for (myEdge<V> move : movesToMake)
		{
			if (bound <= 0)
				break;
			
			bStruct.addResult(s, move.getEdge().getFirst(), move.getEdge().getSecond());
			
			bound--;
			
		}
		
	
		stack.push(s.getChanges().size() - ogCount);
		
		return s;
		
		
	}

	@Override
	public branchingReturnC<V> revertReduce(branchingReturnC<V> s) {
		//return the number of deletes from stack
		int editCount = stack.pop();
		
		for (int i = 0; i < editCount; i++)
		{
			bStruct.revert(s);
		}
		return s;
	}
	

}
