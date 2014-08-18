package abstractClasses;

import qtUtils.branchingReturnC;
import branch.qtBranch;

public abstract class GreedyEdit<V> extends Dive<V> 
{
	
	/**
	 * edit the given graph to some sort of conclusion using a greedy approach
	 * @param s edit state
	 */
	public abstract void greedyEdit(branchingReturnC<V> s);
	
	/**
	 * greedily edit graph until bound number of moves are performed
	 * @param s edit state
	 * @param bound
	 */
	public abstract void greedyEdit(branchingReturnC<V> s, int bound);
	
	/**
	 * constructor
	 * @param b branching structure
	 */
	public GreedyEdit(qtBranch<V> b)
	{
		super(b);
	}

	
	/**
	 * greedy dive
	 */
	public void dive(branchingReturnC<V> s)
	{
		greedyEdit(s);
	}
	
	/**
	 * greedy dive
	 */
	public void dive(branchingReturnC<V> s, int bound)
	{
		greedyEdit(s, bound);
	}
}
