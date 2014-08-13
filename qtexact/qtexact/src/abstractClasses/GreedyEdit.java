package abstractClasses;

import qtUtils.branchingReturnC;
import branch.qtBranch;

public abstract class GreedyEdit<V> extends Dive<V> 
{
	
	/**
	 * edit the given graph to some sort of conclusion using a greedy approach
	 * @param s
	 */
	public abstract void greedyEdit(branchingReturnC<V> s);
	
	public abstract void greedyEdit(branchingReturnC<V> s, int bound);
	
	public GreedyEdit(qtBranch<V> b)
	{
		super(b);
	}

	
	
	public void dive(branchingReturnC<V> s)
	{
		greedyEdit(s);
	}
	
	public void dive(branchingReturnC<V> s, int bound)
	{
		greedyEdit(s, bound);
	}
}
