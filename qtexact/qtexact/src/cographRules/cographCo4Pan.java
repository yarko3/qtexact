package cographRules;

import java.util.List;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import abstractClasses.Certificate;
import abstractClasses.SearchResult;
import controller.Controller;
import edu.uci.ics.jung.graph.util.Pair;


public class cographCo4Pan<V> extends cographBranch<V> {

	public cographCo4Pan(Controller<V> controller) {
		super(controller);
		
	}
	
	@Override
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		Certificate<V> certificate = searchResult.getCertificate();
		
		
		//check if co-4-pan is present
		if (certificate.getFlag() == -7)
		{
			List<V> lexResult = certificate.getVertices();
			double oldPercent = s.getPercent();
			
			//RULES GENERATED BY EDITOR
			int ruleCount = 14;
			//0 additions and 1 deletions
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				deleteResult(s, lexResult.get(0), lexResult.get(1));
				controller.branch(s);
				revert(s, 1);
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);

			//0 additions and 1 deletions
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				deleteResult(s, lexResult.get(1), lexResult.get(2));
				controller.branch(s);
				revert(s, 1);
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);

			//0 additions and 2 deletions
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed))
				&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				deleteResult(s, lexResult.get(2), lexResult.get(3));
				deleteResult(s, lexResult.get(2), lexResult.get(4));
				controller.branch(s);
				revert(s, 2);
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);

			//1 additions and 0 deletions
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				addResult(s, lexResult.get(0), lexResult.get(2));
				controller.branch(s);
				revert(s, 1);
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);

			//2 additions and 1 deletions
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed))
				&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), false, directed))
				&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(4)), false, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				deleteResult(s, lexResult.get(2), lexResult.get(3));
				addResult(s, lexResult.get(1), lexResult.get(4));
				addResult(s, lexResult.get(0), lexResult.get(4));
				controller.branch(s);
				revert(s, 3);
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);

			//2 additions and 0 deletions
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false, directed))
				&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(4)), false, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				addResult(s, lexResult.get(1), lexResult.get(3));
				addResult(s, lexResult.get(0), lexResult.get(4));
				controller.branch(s);
				revert(s, 2);
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);

			//2 additions and 0 deletions
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), false, directed))
				&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), false, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				addResult(s, lexResult.get(0), lexResult.get(3));
				addResult(s, lexResult.get(1), lexResult.get(4));
				controller.branch(s);
				revert(s, 2);
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);

			//1 additions and 2 deletions
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed))
				&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), false, directed))
				&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(3)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				deleteResult(s, lexResult.get(2), lexResult.get(3));
				addResult(s, lexResult.get(1), lexResult.get(4));
				deleteResult(s, lexResult.get(4), lexResult.get(3));
				controller.branch(s);
				revert(s, 3);
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);

			//2 additions and 0 deletions
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false, directed))
				&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), false, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				addResult(s, lexResult.get(1), lexResult.get(3));
				addResult(s, lexResult.get(1), lexResult.get(4));
				controller.branch(s);
				revert(s, 2);
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);

			//1 additions and 2 deletions
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed))
				&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(4)), false, directed))
				&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(3)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				deleteResult(s, lexResult.get(2), lexResult.get(3));
				addResult(s, lexResult.get(0), lexResult.get(4));
				deleteResult(s, lexResult.get(4), lexResult.get(3));
				controller.branch(s);
				revert(s, 3);
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);

			//1 additions and 2 deletions
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false, directed))
				&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true, directed))
				&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				addResult(s, lexResult.get(1), lexResult.get(3));
				deleteResult(s, lexResult.get(3), lexResult.get(4));
				deleteResult(s, lexResult.get(2), lexResult.get(4));
				controller.branch(s);
				revert(s, 3);
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);

			//2 additions and 1 deletions
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false, directed))
				&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), false, directed))
				&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				addResult(s, lexResult.get(1), lexResult.get(3));
				addResult(s, lexResult.get(0), lexResult.get(3));
				deleteResult(s, lexResult.get(2), lexResult.get(4));
				controller.branch(s);
				revert(s, 3);
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);

			//1 additions and 2 deletions
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), false, directed))
				&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), true, directed))
				&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				addResult(s, lexResult.get(0), lexResult.get(3));
				deleteResult(s, lexResult.get(2), lexResult.get(4));
				deleteResult(s, lexResult.get(3), lexResult.get(4));
				controller.branch(s);
				revert(s, 3);
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);

			//2 additions and 0 deletions
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), false, directed))
				&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(4)), false, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				addResult(s, lexResult.get(0), lexResult.get(3));
				addResult(s, lexResult.get(0), lexResult.get(4));
				controller.branch(s);
				revert(s, 2);
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);

			
		}
		else throw new NullPointerException();
		
		return s;
		
	}

}
