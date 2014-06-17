package reduction;

import java.util.ArrayList;
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
		ArrayList<V> vertices = new ArrayList<V>();
		vertices.addAll(s.getG().getVertices());
		
		
		outer:
		for (int i = 0; i < vertices.size(); i++)
		{
			V v0 = vertices.get(i);
			
			//if this node does not have enough neighbours to satisfy reduction, skip
			if (s.getG().getNeighborCount(v0) < s.getMinMoves().getChanges().size() - s.getChanges().size())
				continue;
			
			for (V n0 : s.getG().getNeighbors(v0))
			{
				for (V v1 : s.getG().getNeighbors(n0))
				{
					if (!s.getG().isNeighbor(v0, v1) && v0 != v1)
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
						if (common.size() > s.getMinMoves().getChanges().size() - s.getChanges().size() && !toApply.contains(new myEdge<V>(new Pair<V>(v0, v1), true)))
						{
							
							toApply.addLast(new myEdge<V>(new Pair<V>(v0, v1), true));
							
							if (toApply.size() == s.getMinMoves().getChanges().size() - s.getChanges().size())
								break outer;
						}
					}
				}
				
				
			}
			
			
			
//			for (int j = i+1; j < vertices.size(); j++)
//			{
//				V v1 = vertices.get(j);
//				
//				if (!s.getG().isNeighbor(v0, v1))
//				{
//					//get all common neighbours
//					HashSet<V> all = new HashSet<V>();
//					ArrayList<V> common = new ArrayList<V>();
//					
//					all.addAll(s.getG().getNeighbors(v0));
//					
//					//get all the duplicates in common, while all neighbours are stored in all
//					for (V v : s.getG().getNeighbors(v1))
//					{
//						if (!all.add(v))
//							common.add(v);
//					}
//					
//					
//					for (int k = 0; k < common.size(); k++)
//					{
//						V temp = common.remove(k);
//						boolean flag = true;
//						for (V n : common)
//						{
//							if (!s.getG().isNeighbor(temp, n))
//								flag = false;
//						}
//						
//						if (flag == false)
//						{
//							common.add(k, temp);
//						}
//						else
//							k--;
//					}
//					
//					
//					//if number of induced C4s is greater than the allowed number of moves, add an edge
//					if (common.size() > s.getMinMoves().getChanges().size() - s.getChanges().size() && !toApply.contains(new myEdge<V>(new Pair<V>(v0, v1), true)))
//					{
//						
//						toApply.addLast(new myEdge<V>(new Pair<V>(v0, v1), true));
//						
//						if (toApply.size() == s.getMinMoves().getChanges().size() - s.getChanges().size())
//							break outer;
//					}
//				}
//			}
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

