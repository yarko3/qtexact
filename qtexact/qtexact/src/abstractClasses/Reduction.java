package abstractClasses;

import qtUtils.branchingReturnC;

public abstract class Reduction<V> 
{
	/**
	 * method used to reduce the branching factor of an edit state
	 * @param s initial edit state
	 * @return reduced edit state
	 */
	public abstract branchingReturnC<V> reduce(branchingReturnC<V> s);

	/**
	 * revert reduction (for backing up on a traversal)
	 * @param s
	 * @return
	 */
	public abstract branchingReturnC<V> revertReduce(branchingReturnC<V> s);
}
