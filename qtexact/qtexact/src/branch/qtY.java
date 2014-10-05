package branch;

import java.util.ArrayList;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import abstractClasses.Certificate;
import abstractClasses.SearchResult;
import controller.Controller;
import edu.uci.ics.jung.graph.util.Pair;

public class qtY<V> extends qtBranchNoHeuristic<V> 
{
	/**
	 * constructor
	 * @param controller
	 */
	public qtY(Controller<V> controller) 
	{
		super(controller);
	}
	
	/**
	 * fork branching rules
	 */
	@Override
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		Certificate<V> certificate = searchResult.getCertificate();
		
		//sSearchResult<V> old = clone.deepClone(searchResult);
		
		this.findStructures(s, searchResult);
		
		
		//check if fork is present
		if (certificate.getFlag() == -6)
		{
			ArrayList<V> lexResult = certificate.getVertices();
			double oldPercent = s.getPercent();
			int rules = 7;
			
			//delete 2 edges first
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / rules);
				}
				
				controller.branch(delete2Result(s, lexResult.get(2), lexResult.get(3), lexResult.get(2), lexResult.get(4)));
				
				//revert changes
				revert2(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / rules);
			
			//add 1, remove 1
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), false, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / rules);
				}
				
				controller.branch(addRemoveResult(s, lexResult.get(1), lexResult.get(4), lexResult.get(2), lexResult.get(3)));
				
				//revert changes
				revert2(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / rules);
			
			
			//add 1, remove 1
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / rules);
				}
				
				controller.branch(addRemoveResult(s, lexResult.get(1), lexResult.get(3), lexResult.get(2), lexResult.get(4)));
				
				//revert changes
				revert2(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / rules);
			
			
			
			//add 2 edges
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), false, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / rules);
				}
				
				controller.branch(add2Result(s, lexResult.get(1), lexResult.get(3), lexResult.get(1), lexResult.get(4)));
				
				//revert changes
				revert2(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / rules);
			
			
			//delete one edge (2 options)
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / rules);
				}
				
				controller.branch(deleteResult(s, lexResult.get(0), lexResult.get(1)));
				
				//revert changes
				revert(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / rules);
			
			//second option
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / rules);
				}
				
				controller.branch(deleteResult(s, lexResult.get(1), lexResult.get(2)));
				
				//revert changes
				revert(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / rules);
			
			
			//add one edge
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / rules);
				}
				
				controller.branch(addResult(s, lexResult.get(0), lexResult.get(2)));
				
				//revert changes
				revert(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / rules);
			
	
			return s;
		}
		else
		{
			return super.branchingRules(s, searchResult);
		}
	}

}
