package branch;

import java.util.ArrayList;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import abstractClasses.Certificate;
import abstractClasses.SearchResult;
import controller.Controller;
import edu.uci.ics.jung.graph.util.Pair;

public class qtP5<V> extends qtBranchNoHeuristic<V>
{

	public qtP5(Controller<V> controller) {
		super(controller);
	}
	
	/**
	 * branching rules for P5
	 */
	@Override
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		Certificate<V> certificate = searchResult.getCertificate();
		//check if fork is present
		if (certificate.getFlag() == -5)
		{
			ArrayList<V> lexResult = certificate.getVertices();
			double oldPercent = s.getPercent();
			branchingReturnC<V> temp;
			
			int ruleCount = 9;
			
			//delete an edge
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			//delete edge
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				temp = controller.branch(deleteResult(s, lexResult.get(2), lexResult.get(3)));
				
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			
			//add one, delete one
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), false))
					 && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				temp = controller.branch(addRemoveResult(s, lexResult.get(2), lexResult.get(4), lexResult.get(0), lexResult.get(1)));
				
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			//add one, delete one
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false))
					 && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				temp = controller.branch(addRemoveResult(s, lexResult.get(1), lexResult.get(3), lexResult.get(0), lexResult.get(1)));
				
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			//add one, delete one
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false))
					 && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				temp = controller.branch(addRemoveResult(s, lexResult.get(1), lexResult.get(3), lexResult.get(3), lexResult.get(4)));
				
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			//add one, delete one
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false))
					 && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				temp = controller.branch(addRemoveResult(s, lexResult.get(0), lexResult.get(2), lexResult.get(3), lexResult.get(4)));
				
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			//add two
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), false))
					 && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), false)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				temp = controller.branch(add2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3)));
				
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			//add two
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), false))
					 && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				temp = controller.branch(add2Result(s, lexResult.get(1), lexResult.get(4), lexResult.get(1), lexResult.get(3)));
				
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			
			//add two
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false))
					 && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), false)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				temp = controller.branch(add2Result(s, lexResult.get(0), lexResult.get(2), lexResult.get(2), lexResult.get(4)));
				
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			
			return s.getMinMoves();
		}
		
		else
			return super.branchingRules(s, searchResult);
	}

}
