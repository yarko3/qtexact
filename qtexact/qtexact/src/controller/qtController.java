package controller;

import branch.qtBranch;
import qtUtils.branchingReturnC;
import abstractClasses.Branch;
import abstractClasses.Controller;
import abstractClasses.SearchResult;
import search.qtLBFS;

public class qtController<V> extends Controller<V> 
{
	public qtController(Branch<V> bStruct) {
		super(bStruct);
	}

	protected qtBranch<V> bStruct;

	public branchingReturnC<V> branch(branchingReturnC<V> s) 
	{
		//check if graph is target
		SearchResult<V> searchResult = ((qtLBFS<V>) bStruct.getSearch()).searchPrep(s);
		
		
		//target graph has been found
		if (searchResult.isTarget())
		{
			//update the minMoves list if this solution is better
			if (s.getChanges().size() < s.getMinMoves().getChanges().size())
			{
				//make a new minMoves to store
				branchingReturnC<V> newMin = new branchingReturnC<V>(s.getG(), s.getDeg(), Branch.clone.deepClone(s.getChanges()));
				newMin.setMinMoves(newMin);
				s.setMinMoves(newMin);
			}
			return s;
		}
		//branch on found obstruction
		else
		{	
			//only branch if current minMoves is longer than current state of search
			if (s.getMinMoves().getChanges().size() > s.getChanges().size())
			{
				branchingReturnC<V> rtn = bStruct.branchingRules(s, searchResult);
				return rtn;
			}
			//min moves is a better solution
			else
				return s.getMinMoves();
		}
	}
	

}
