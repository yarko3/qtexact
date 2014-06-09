package controller;

import qtUtils.branchingReturnC;
import abstractClasses.Branch;
import abstractClasses.SearchResult;

public class ControllerP<V> extends Controller<V>
{
	double globalPercent;
	
	
	public ControllerP(Branch<V> bStruct) {
		super(bStruct);
		globalPercent = 0;
	}
	public void setGlobalPercent(double p)
	{
		globalPercent = p;
	}
	
	
	@Override
	public branchingReturnC<V> branch(branchingReturnC<V> s)
	{
		timesRun++;
		
		boolean reduced = false;
		
		//run reduction step
		if (true || s.getChanges().size() % 4 == 0 || timesRun == 1)
		{	
			//System.out.println("Percent done: " + globalPercent);
			reduced = true;
			s = bStruct.reduce(s);
		}
		//check if graph is target
		SearchResult<V> searchResult =  bStruct.getSearch().searchPrep(s);
		
		
		//target graph has been found
		if (searchResult.isTarget())
		{
			//update global percent
			globalPercent += s.getPercent();
			//System.out.println("Percent done: " + globalPercent);
			
			//update the minMoves list if this solution is better
			if (s.getChanges().size() < s.getMinMoves().getChanges().size())
			{
				//make a new minMoves to store
				branchingReturnC<V> newMin = new branchingReturnC<V>(s.getG(), s.getDeg(), Branch.clone.deepClone(s.getChanges()));
				newMin.setMinMoves(newMin);
				s.setMinMoves(newMin);
			}
			
			if (reduced)
				s = bStruct.reduceRevert(s);
			
			return s;
		}
		//branch on found obstruction
		else
		{	
			//only branch if current minMoves is longer than current state of search
			if (s.getMinMoves().getChanges().size() > s.getChanges().size())
			{
				branchingReturnC<V> rtn = bStruct.branchingRules(s, searchResult);
				
				if (reduced)
				{
					branchingReturnC<V> reverted = bStruct.reduceRevert(s);
					s.setChanges(reverted.getChanges());
					s.setDeg(reverted.getDeg());
					s.setGraph(reverted.getG());
				}
				
				
				return rtn;
			}
			//min moves is a better solution
				else
				{
					//update global percent
					globalPercent += s.getPercent();
					//System.out.println("Percent done: " + globalPercent);
					
					if (reduced)
					{
						branchingReturnC<V> reverted = bStruct.reduceRevert(s);
						s.setChanges(reverted.getChanges());
						s.setDeg(reverted.getDeg());
						s.setGraph(reverted.getG());
					}
					
					return s.getMinMoves();
				}
		}
	}

}
