package branch;

import java.util.List;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import abstractClasses.SearchResult;
import controller.Controller;
import edu.uci.ics.jung.graph.util.Pair;

public class qtSimple<V> extends qtBranchNoHeuristic<V> 
{

	/**
	 * constructor
	 * @param controller
	 */
	public qtSimple(Controller<V> controller) {
		super(controller);
	}
	/**
	 * only add on C4
	 * only remove on P4
	 */
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult) 
	{
		List<V> lexResult = searchResult.getCertificate().getVertices();
		double oldPercent = s.getPercent();
		
		//C4 has been found
		if (searchResult.getCertificate().getFlag() == -1)
		{
			//result of adding 1 edge to break C4
			//if we did not remove the edge that is about to be added
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / 2);
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 2);
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / 2);
				}
				
				controller.branch(addResult(s, lexResult.get(1), lexResult.get(3)));
				//revert changes
				if (output)
				{
					s.setPercent(oldPercent);
				}
				
				revert(s);
				//update percentDone
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 2);
			
			
			

		}
		//P4 has been found
		else
		{	
			//remove an edge to break P4
			//remove middle edge first
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true, directed)))
			{
				if (output)
					//change progress percent
					s.setPercent(oldPercent / 3);
				
				controller.branch(deleteResult(s, lexResult.get(1), lexResult.get(2)));
				
				if (output)
					s.setPercent(oldPercent);
				
				//revert changes to global graph
				revert(s);
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 3);
			
			
			//try other edges later
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true, directed)))
			{
				if (output)
					//change progress percent
					s.setPercent(oldPercent / 3);
				
				controller.branch(deleteResult(s, lexResult.get(0), lexResult.get(1)));
				
				if (output)
					s.setPercent(oldPercent);
				
				//revert changes to global graph
				revert(s);
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 3);
			
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed)))
			{
				if (output)
					//change progress percent
					s.setPercent(oldPercent / 3);
				
				controller.branch(deleteResult(s, lexResult.get(2), lexResult.get(3)));
				
				if (output)
					s.setPercent(oldPercent);
				
				//revert changes to global graph
				revert(s);
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 3);
			
		}
		
		return s;
	}
	

}
