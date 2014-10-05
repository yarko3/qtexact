package branch;

import java.util.ArrayList;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import abstractClasses.Certificate;
import abstractClasses.SearchResult;
import controller.Controller;
import edu.uci.ics.jung.graph.util.Pair;

public class qtCo4Pan<V> extends qtBranchNoHeuristic<V> 
{

	/**
	 * constructor
	 * @param controller
	 */
	public qtCo4Pan(Controller<V> controller) {
		super(controller);
	}
	
	@Override
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		Certificate<V> certificate = searchResult.getCertificate();
		
		//SearchResult<V> old = clone.deepClone(searchResult);
		
		this.findStructures(s, searchResult);
		
		
		//check if co-4-pan is present
		if (certificate.getFlag() == -7)
		{
			
			ArrayList<V> lexResult = certificate.getVertices();
			double oldPercent = s.getPercent();
			int rules = 10;
			
			
			//add 1, delete 2
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), false, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / rules);
				}
				
				controller.branch(addRemove2Result(s, 
						lexResult.get(1), lexResult.get(4), 
						lexResult.get(2), lexResult.get(3),
						lexResult.get(3), lexResult.get(4)));
				
				//revert changes
				revert3(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / rules);
			
			
			//add 1, delete 2
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), true, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / rules);
				}
				
				controller.branch(addRemove2Result(s, 
						lexResult.get(1), lexResult.get(3), 
						lexResult.get(2), lexResult.get(4),
						lexResult.get(3), lexResult.get(4)));
				
				//revert changes
				revert3(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / rules);
			
			//add 2, delete 1
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), false, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(0)), false, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(2)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / rules);
				}
				
				controller.branch(add2RemoveResult(s, 
						lexResult.get(1), lexResult.get(4), 
						lexResult.get(4), lexResult.get(0),
						lexResult.get(3), lexResult.get(2)));
				
				//revert changes
				revert3(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / rules);
			
			
			//add 2, delete 1
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(0)), false, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(2)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / rules);
				}
				
				controller.branch(add2RemoveResult(s, 
						lexResult.get(1), lexResult.get(3), 
						lexResult.get(3), lexResult.get(0),
						lexResult.get(4), lexResult.get(2)));
				
				//revert changes
				revert3(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / rules);
			
			
			//add 3
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(0)), false, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(0)), false, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / rules);
				}
				
				controller.branch(add3Result(s, 
						lexResult.get(1), lexResult.get(3), 
						lexResult.get(2), lexResult.get(0),
						lexResult.get(3), lexResult.get(0)));
				
				//revert changes
				revert3(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / rules);
			
			
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
