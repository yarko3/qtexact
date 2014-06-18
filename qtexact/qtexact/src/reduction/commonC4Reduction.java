package reduction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

import qtUtils.branchingReturnC;
import abstractClasses.Reduction;
import branch.qtBranch;

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
		
		int count = 0;
		
		
		//look through first few two non-neighbours
		
		ArrayList<V> vertices = bStruct.getSearch().orderVerticesNonDecreasingDegree(s.getG());
		
		
		
		outer:
		//for (V v0 : s.getG().getVertices())
		for (int i = 1; i < 5; i++)
		{
			V v0 = vertices.get(i);
			
			if (s.getG().getNeighborCount(v0) < s.getMinMoves().getChanges().size() - s.getChanges().size())
				continue;
			
			for (int j = 0; j < i; j++)
			{
				V v1 = vertices.get(j);
				
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
						
						
						if (count == s.getMinMoves().getChanges().size() - s.getChanges().size())
							break outer;
					}
				}
			}
		}
		
		stack.push(count);
		//bStruct.applyMoves(s, toApply);
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
	
	

}

