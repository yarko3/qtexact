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
	public qtY(Controller<V> controller) 
	{
		super(controller);
	}
	
	@Override
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		Certificate<V> certificate = searchResult.getCertificate();
		//check if fork is present
		if (certificate.getFlag() == -6)
		{
			ArrayList<V> lexResult = certificate.getVertices();
			double oldPercent = s.getPercent();
			branchingReturnC<V> temp;
			
			//delete 2 edges first
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / 4);
				}
				
				temp = controller.branch(delete2Result(s, lexResult.get(2), lexResult.get(3), lexResult.get(2), lexResult.get(4)));
				
				//revert changes
				revert2(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
				
				if (temp.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(temp.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 4);
			
			
			//delete one edge (2 options)
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / 4);
				}
				
				temp = controller.branch(deleteResult(s, lexResult.get(0), lexResult.get(1)));
				
				//revert changes
				revert(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
				
				if (temp.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(temp.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 4);
			
			//second option
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / 4);
				}
				
				temp = controller.branch(deleteResult(s, lexResult.get(1), lexResult.get(2)));
				
				//revert changes
				revert(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
				
				if (temp.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(temp.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 4);
			
			
			//add one edge
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / 4);
				}
				
				temp = controller.branch(addResult(s, lexResult.get(0), lexResult.get(2)));
				
				//revert changes
				revert(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
				
				if (temp.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(temp.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 4);
			
	
			return s.getMinMoves();
		}
		else
		{
			return super.branchingRules(s, searchResult);
		}
	}

}
