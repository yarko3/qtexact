package branch;

import java.util.ArrayList;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import abstractClasses.Certificate;
import abstractClasses.SearchResult;
import controller.Controller;
import edu.uci.ics.jung.graph.util.Pair;

public class qtDiamondC4 <V> extends qtBranchNoHeuristic<V> {

	public qtDiamondC4(Controller<V> controller) {
		super(controller);
	}
	
	@Override
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		Certificate<V> certificate = searchResult.getCertificate();
		this.findStructures(s, searchResult);
		
		
		//check if double C4 is present
		if (certificate.getFlag() == -10)
		{
			ArrayList<V> lexResult = certificate.getVertices();
			double oldPercent = s.getPercent();
			
			int ruleCount = 22;
			
			//delete 4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(0)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(1)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(delete4Result(s, 
						lexResult.get(1), lexResult.get(0), 
						lexResult.get(2), lexResult.get(1), 
						lexResult.get(3), lexResult.get(4),
						lexResult.get(2), lexResult.get(4)));
				
				//revert changes
				revert4(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			//delete 4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(0)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(delete4Result(s, 
						lexResult.get(3), lexResult.get(0), 
						lexResult.get(2), lexResult.get(3), 
						lexResult.get(1), lexResult.get(4),
						lexResult.get(2), lexResult.get(4)));
				
				//revert changes
				revert4(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			//delete 4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(0)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(delete4Result(s, 
						lexResult.get(1), lexResult.get(0), 
						lexResult.get(2), lexResult.get(3), 
						lexResult.get(1), lexResult.get(4),
						lexResult.get(2), lexResult.get(4)));
				
				//revert changes
				revert4(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			//delete 4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(delete4Result(s, 
						lexResult.get(1), lexResult.get(2), 
						lexResult.get(2), lexResult.get(3), 
						lexResult.get(1), lexResult.get(4),
						lexResult.get(3), lexResult.get(4)));
				
				//revert changes
				revert4(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			//delete 4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(0)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(1)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(delete4Result(s, 
						lexResult.get(3), lexResult.get(0), 
						lexResult.get(2), lexResult.get(1), 
						lexResult.get(3), lexResult.get(4),
						lexResult.get(2), lexResult.get(4)));
				
				//revert changes
				revert4(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			//delete 3
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(0)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(1)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(delete3Result(s, 
						lexResult.get(1), lexResult.get(0), 
						lexResult.get(2), lexResult.get(1), 
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
			
			//delete 3
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(0)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(delete3Result(s, 
						lexResult.get(3), lexResult.get(0), 
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			//delete 3
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(0)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(1)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(delete3Result(s, 
						lexResult.get(3), lexResult.get(0), 
						lexResult.get(2), lexResult.get(1), 
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
			
			//delete 3
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(0)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(delete3Result(s, 
						lexResult.get(1), lexResult.get(0), 
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
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			//add 1, delete 2
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(0)), false))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemove2Result(s, 
						lexResult.get(2), lexResult.get(0), 
						lexResult.get(0), lexResult.get(3), 
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
			
			//add 1, delete 2
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(0)), false))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(3)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemove2Result(s, 
						lexResult.get(2), lexResult.get(0), 
						lexResult.get(4), lexResult.get(3), 
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
			
			//add 1, delete 2
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(0)), false))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(0)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemove2Result(s, 
						lexResult.get(4), lexResult.get(0), 
						lexResult.get(1), lexResult.get(2), 
						lexResult.get(1), lexResult.get(0)));
				
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
			
			//add 1, delete 2
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(0)), false))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(0)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemove2Result(s, 
						lexResult.get(2), lexResult.get(0), 
						lexResult.get(0), lexResult.get(1), 
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
			
			//add 1, delete 2
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(0)), false))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemove2Result(s, 
						lexResult.get(4), lexResult.get(0), 
						lexResult.get(2), lexResult.get(3), 
						lexResult.get(1), lexResult.get(2)));
				
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
			
			//add 1, delete 2
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(0)), false))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(0)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemove2Result(s, 
						lexResult.get(4), lexResult.get(0), 
						lexResult.get(2), lexResult.get(3), 
						lexResult.get(1), lexResult.get(0)));
				
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
			
			//add 1, delete 2
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(0)), false))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemove2Result(s, 
						lexResult.get(4), lexResult.get(0), 
						lexResult.get(0), lexResult.get(3), 
						lexResult.get(2), lexResult.get(3)));
				
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
			
			//add 1, delete 2
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(0)), false))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(3)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(0)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemove2Result(s, 
						lexResult.get(2), lexResult.get(0), 
						lexResult.get(4), lexResult.get(3), 
						lexResult.get(1), lexResult.get(0)));
				
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
			
			
			//add 1, delete 2
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(0)), false))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemove2Result(s, 
						lexResult.get(2), lexResult.get(0), 
						lexResult.get(0), lexResult.get(3), 
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
			
			//add 1, delete 2
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(0)), false))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemove2Result(s, 
						lexResult.get(4), lexResult.get(0), 
						lexResult.get(0), lexResult.get(3), 
						lexResult.get(1), lexResult.get(2)));
				
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
			
			//delete 2
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(0)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(delete2Result(s, 
						lexResult.get(1), lexResult.get(0), 
						lexResult.get(0), lexResult.get(3)));
				
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
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(0)), false)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(add2Result(s, 
						lexResult.get(2), lexResult.get(0), 
						lexResult.get(0), lexResult.get(4)));
				
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
			
			//add edge
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addResult(s, 
						lexResult.get(1), lexResult.get(3)));
				
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
