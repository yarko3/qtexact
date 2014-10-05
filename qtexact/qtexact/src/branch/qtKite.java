package branch;

import java.util.ArrayList;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import abstractClasses.Certificate;
import abstractClasses.SearchResult;
import controller.Controller;
import edu.uci.ics.jung.graph.util.Pair;

public class qtKite<V> extends  qtBranchNoHeuristic<V>
{

	/**
	 * constructor
	 * @param controller
	 */
	public qtKite(Controller<V> controller) {
		super(controller);
	}
	
	@Override
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		Certificate<V> certificate = searchResult.getCertificate();
		
		
		SearchResult<V> old = clone.deepClone(searchResult);
		
		
		if (searchResult.getCertificate().getFlag() != -9)
			searchResult = old;
		
		
		
		//check if kite is present
		if (certificate.getFlag() == -9)
		{
			ArrayList<V> lexResult = certificate.getVertices();
			double oldPercent = s.getPercent();
			
			int ruleCount = 11;
			
			//delete 3 edges
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true, directed)) &&
					!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), true, directed)) &&
					!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(delete3Result(s, 
						lexResult.get(1), lexResult.get(2), 
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			
			//delete 3 edges
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(2)), true, directed)) &&
					!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed)) &&
					!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(delete3Result(s, 
						lexResult.get(4), lexResult.get(2), 
						lexResult.get(2), lexResult.get(3), 
						lexResult.get(1), lexResult.get(4)));
				
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			//delete 2 edges
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true, directed)) &&
					!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(delete2Result(s, lexResult.get(1), lexResult.get(2), lexResult.get(1), lexResult.get(4)));
				
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			
			//delete 2 edges
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed)) &&
					!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(delete2Result(s, lexResult.get(3), lexResult.get(2), lexResult.get(3), lexResult.get(4)));
				
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			
			//add 1, delete 1
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(0)), false, directed)) &&
					!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(1)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemoveResult(s, 
						lexResult.get(4), lexResult.get(0), 
						lexResult.get(2), lexResult.get(1)));
				
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			
			//add 1, delete 1
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(0)), false, directed)) &&
					!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemoveResult(s, 
						lexResult.get(4), lexResult.get(0), 
						lexResult.get(2), lexResult.get(3)));
				
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			
			//add 1, delete 1
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(0)), false, directed)) &&
					!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(1)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemoveResult(s, 
						lexResult.get(2), lexResult.get(0), 
						lexResult.get(4), lexResult.get(1)));
				
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			
			//add 1, delete 1
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(0)), false, directed)) &&
					!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(3)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemoveResult(s, 
						lexResult.get(2), lexResult.get(0), 
						lexResult.get(4), lexResult.get(3)));
				
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			
			
			//add 2
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(0)), false, directed)) &&
					!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(0)), false, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(add2Result(s, 
						lexResult.get(2), lexResult.get(0), 
						lexResult.get(4), lexResult.get(0)));
				
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			//delete an edge
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			//add edge
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addResult(s, lexResult.get(1), lexResult.get(3)));
				
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			
			
			
			return s;
		}
		else
			return super.branchingRules(s, searchResult);
	}
	

}
