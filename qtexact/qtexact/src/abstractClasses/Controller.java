package abstractClasses;

import qtUtils.branchingReturnC;
import qtUtils.qtGenerate;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public abstract class Controller<V> 
{
	public Controller(Branch<V> bStruct) {
		super();
		this.bStruct = bStruct;
	}

	//branching structure used by the controller
	protected Branch<V> bStruct;
	
	public Branch<V> getbStruct() {
		return bStruct;
	}

	public void setbStruct(Branch<V> bStruct) {
		this.bStruct = bStruct;
	}


	public Graph<V, Pair<V>> branchID(Graph<V, Pair<V>> G, int START, int MAX)
	{
		//bound to iterate down to
		int bound = START + 1;
		Graph<V, Pair<V>> goal = G;
		
		//while graph is not solved and the bound is less than MAX
		while (bound <= MAX + 1)
		{
			goal = branchStart(G, bound);
			//test if current graph is QT
			if (bStruct.search.isTarget(goal))
			{
				return goal;
			}
			else
			{
				bound++;
			}
		}
		return goal;
	}
	

	public Graph<V, Pair<V>> branchStart(Graph<V, Pair<V>> G, int bound)
	{
		//set up branching
		branchingReturnC<V> goal = bStruct.setup(G, bound);
		
		goal = branch(goal);
		
		System.out.println("Completed after moves: " + goal.getMinMoves().getChanges().size());
		
		qtGenerate<V> gen = new qtGenerate<V>();
		
		
		Graph<V, Pair<V>> rtn = gen.applyMoves(Branch.clone.deepClone(goal.getG()), goal.getMinMoves().getChanges());
		
		//return QT graph if edit succeeds
		if (bStruct.search.isTarget(rtn))
		{
			System.out.println("Solution found. ");
			return rtn;
		}
		else
		{
			//otherwise return original graph
			System.out.println("Solution not found. ");
			return goal.getG();
		}
		
	}
	
	public abstract branchingReturnC<V> branch(branchingReturnC<V> s);
	

}
