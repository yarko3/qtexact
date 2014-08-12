package abstractClasses;

import qtUtils.branchingReturnC;
import branch.qtBranch;

public abstract class GreedyEdit<V> 
{
	protected Branch<V> bStruct;
	
	/**
	 * edit the given graph to some sort of conclusion using a greedy approach
	 * @param s
	 */
	public abstract void greedyEdit(branchingReturnC<V> s);
	
	public GreedyEdit(qtBranch<V> b)
	{
		bStruct = b;
	}

	public Branch<V> getbStruct() {
		return bStruct;
	}

	public void setbStruct(Branch<V> bStruct2) {
		this.bStruct = bStruct2;
	}
	
}
