package abstractClasses;

import qtUtils.branchingReturnC;
import branch.qtBranch;

public abstract class GreedyEdit<V> 
{
	protected qtBranch<V> bStruct;
	
	/**
	 * edit the given graph to some sort of conclusion using a greedy approach
	 * @param s
	 */
	public abstract void greedyEdit(branchingReturnC<V> s);
	
	public GreedyEdit(qtBranch<V> b)
	{
		bStruct = b;
	}
}
