package abstractClasses;

import qtUtils.branchingReturnC;

/**
 * a diving strategy after the search bottoms out
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public abstract class Dive<V> 
{
	/**
	 * 
	 * @param b branching structure
	 */
	public Dive(Branch<V> b)
	{
		bStruct = b;
		directed = b.isDirected();
	}
	
	/**
	 * is the graph edited directed?
	 */
	protected boolean directed;
	
	/**
	 * branching structure
	 */
	protected Branch<V> bStruct;
	
	/**
	 * 
	 * @return branching structure
	 */
	public Branch<V> getbStruct() {
		return bStruct;
	}

	/**
	 * set branching structure
	 * @param bStruct2 branching struct
	 */
	public void setbStruct(Branch<V> bStruct2) {
		this.bStruct = bStruct2;
	}
	
	/**
	 * Apply a diving strategy to the current edit state
	 * the resulting graph may or may not be at target state
	 * @param s edit state
	 */
	public abstract void dive(branchingReturnC<V> s);

	/**
	 * Apply a diving strategy to the current edit state with a bound of allowed moves
	 * (the resulting graph may or may not be at target state)
	 * @param s edit state
	 * @param bound allowed moves
	 */
	public abstract void dive(branchingReturnC<V> s, int bound);

}
