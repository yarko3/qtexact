package abstractClasses;

import qtUtils.branchingReturnC;

public abstract class Dive<V> 
{
	
	
	public Dive(Branch<V> b)
	{
		bStruct = b;
	}
	
	
	protected Branch<V> bStruct;
	
	
	public Branch<V> getbStruct() {
		return bStruct;
	}

	public void setbStruct(Branch<V> bStruct2) {
		this.bStruct = bStruct2;
	}
	
	/**
	 * Apply a diving strategy to the current edit state
	 * the resulting graph may or may not be at target state
	 * @param s
	 */
	public abstract void dive(branchingReturnC<V> s);

	/**
	 * Apply a diving strategy to the current edit state with a bound of allowed moves
	 * the resulting graph may or may not be at target state
	 * @param s
	 * @param bound
	 */
	public abstract void dive(branchingReturnC<V> s, int bound);

}
