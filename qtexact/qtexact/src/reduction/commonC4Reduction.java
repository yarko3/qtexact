package reduction;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import abstractClasses.Reduction;
import branch.qtBranch;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Reduction for removing induced C4s
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public class commonC4Reduction<V> extends Reduction<V> 
{
	qtBranch<V> bStruct;
	Stack<Integer> stack;
	
	
	public commonC4Reduction(qtBranch<V> b)
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
		//store the moves to be applied
		LinkedList<myEdge<V>> toApply = new LinkedList<myEdge<V>>();
		
		//look through every two non-neighbours
		outer:
		for (V v0 : s.getG().getVertices())
		{
			for (V v1 : s.getG().getVertices())
			{
				if (v0 != v1 && !s.getG().isNeighbor(v0, v1))
				{
					//get all common neighbours
					HashSet<V> all = new HashSet<V>();
					HashSet<V> common = new HashSet<V>();
					
					all.addAll(s.getG().getNeighbors(v0));
					
					//get all the duplicates in common, while all neighbours are stored in all
					for (V v : s.getG().getNeighbors(v1))
					{
						if (!all.add(v))
							common.add(v);
					}
					
					//if number of induced C4s is greater than the allowed number of moves, add an edge
					if (binomial(common.size(), 2) > s.getMinMoves().getChanges().size() - s.getChanges().size())
					{
						toApply.addLast(new myEdge<V>(new Pair<V>(v0, v1), true));
						
						if (toApply.size() > s.getMinMoves().getChanges().size() - s.getChanges().size())
							break outer;
					}
				}
			}
		}
		
		stack.push(toApply.size());
		bStruct.applyMoves(s, toApply);
		return s;
		
	}

	@Override
	public branchingReturnC<V> revertReduce(branchingReturnC<V> s) {
		//return the number of deletes from stack
		int addCount = stack.pop();
		
		for (int i = 0; i < addCount; i++)
		{
			bStruct.revert(s);
		}
		return s;
	}
	
	/** 
	 * Calculates n choose k (using dynamic programming)
	 *   @param n the total number to choose from (n > 0)
	 *   @param k the number to choose (0 <= k <= n)
	 *   @return n choose k (the binomial coefficient)
	 */
	 private static int binomial(int n, int k) 
	 { 
	    if (n < 2) {
	        return 1;
	    }
	    else {
	        int bin[][] = new int[n+1][n+1];   

	        for (int r = 0; r <= n; r++) {      
	            for (int c = 0; c <= r && c <= k; c++) {
	                if (c == 0 || c == r) {
	                    bin[r][c] = 1;        
	                }
	                else {                      
	                    bin[r][c] = bin[r-1][c-1] + bin[r-1][c];
	                }
	            }
	        }
	        return bin[n][k];
	    }
	 }

}

