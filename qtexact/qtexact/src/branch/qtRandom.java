package branch;

import java.util.List;
import java.util.Random;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import abstractClasses.SearchResult;
import controller.Controller;
import edu.uci.ics.jung.graph.util.Pair;

public class qtRandom<V> extends qtBranchNoHeuristic<V> 
{
	public static Random rand = new Random();
	
	/**
	 * constructor
	 * @param controller
	 */
	public qtRandom(Controller<V> controller) {
		super(controller);
	}
	
	
	/**
	 * Randomly choose a P4 edge to delete
	 */
	@Override
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult) 
	{
		List<V> lexResult = searchResult.getCertificate().getVertices();
		double oldPercent = s.getPercent();
		
		//C4 has been found
		if (searchResult.getCertificate().getFlag() == -1)
		{
			//randomly choose an addition
			
			int r = rand.nextInt(2);
			
			switch (r)
			{
			case 0:
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
				
				break;
			case 1:
				
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
				
			//randomly choose a deletion
			r = rand.nextInt(6);
			
			
			switch (r)
			{
			case 0:
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true, directed)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true, directed)))
				{
					if (output)
					{
						//change progress percent
						s.setPercent(oldPercent / 2);
					}
					
					controller.branch(delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2)));
					
					if (output)
					{
						s.setPercent(oldPercent);
					}
					
					//revert change to global graph
					revert2(s);
				}
				else
					if (output)
						controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 2);
				
				break;
				
			case 1:
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true, directed)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed)))
				{
					if (output)
					{
						//change progress percent
						s.setPercent(oldPercent / 2);
					}
					controller.branch(delete2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3)));
					
					if (output)
					{
						s.setPercent(oldPercent);
					}
					//revert change to global graph
					revert2(s);
				}
				else
					if (output)
						controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 2);
				
				break;
				
			case 2:
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true, directed)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed)))
				{
					if (output)
					{
						//change progress percent
						s.setPercent(oldPercent / 2);
					}
					
					controller.branch(delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3)));
					if (output)
					{
						s.setPercent(oldPercent);
					}
					
					//revert change to global graph
					revert2(s);
				}
				else
					if (output)
						controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 2);
				
				break;
			case 3:
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true, directed)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true, directed)))
				{
					if (output)
					{
						//change progress percent
						s.setPercent(oldPercent / 2);
					}
					
					controller.branch(delete2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2)));
					
					if (output)
						s.setPercent(oldPercent);
					
					//revert change to global graph
					revert2(s);
				}
				else
					if (output)
						controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 2);
				
				break;
				
			case 4:
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true, directed)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed)))
				{
					if (output)
						//change progress percent
						s.setPercent(oldPercent / 2);
					
					controller.branch(delete2Result(s, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3)));
					
					if (output)
						s.setPercent(oldPercent);
					
					//revert change to global graph
					revert2(s);
				}
				else
					if (output)
						controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 2);
				
				break;
			case 5:
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true, directed)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true, directed)))
				{
					if (output)
						//change progress percent
						s.setPercent(oldPercent / 2);
					
					controller.branch(delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3)));
					
					if (output)
						s.setPercent(oldPercent);
					
					//revert change to global graph
					revert2(s);
				}
				else
					if (output)
						controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 2);
			}
			
			return s;
			
		}
		
		//P4 has been found
		else
		{	
			double index = rand.nextDouble();
			
			//remove an edge to break P4
			//remove middle edge first
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / 2);
				}
				
				controller.branch(deleteResult(s, lexResult.get(1), lexResult.get(2)));
				
				
				if (output)
				{
					s.setPercent(oldPercent);
				}
				
				//revert changes to global graph
				revert(s);
				
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 2);
			
			
			if (index < 0.35)
			{
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true, directed)))
				{
					
					if (output)
					{
						//change progress percent
						s.setPercent(oldPercent / 2);
					}
					
					controller.branch(deleteResult(s, lexResult.get(0), lexResult.get(1)));
					
					
					if (output)
					{
						s.setPercent(oldPercent);
					}
					
					//revert changes to global graph
					revert(s);
				
				}
				else
					if (output)
						controller.setGlobalPercent(controller.getGlobalPercent() +oldPercent / 2);
			}
			
			else if (index < .7)
			{
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed)))
				{
					if (output)
					{
						//change progress percent
						s.setPercent(oldPercent / 2);
					}
					
					controller.branch(deleteResult(s, lexResult.get(2), lexResult.get(3)));
					
					if (output)
					{
						s.setPercent(oldPercent);
					}
					
					//revert changes to global graph
					revert(s);
					
				}
				else
					if (output)
						controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 2);
			}
		
			
			else if (index < 0.85)
			{
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false, directed)))
				{
					if (output)
						//change progress percent
						s.setPercent(oldPercent / 2);
					
					controller.branch(addResult(s, lexResult.get(0), lexResult.get(2)));
					if (output)
						s.setPercent(oldPercent);
					
					//revert changes
					revert(s);
				}
				else
					if (output)
						controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 2);
			
			}
				
			else
			{
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false, directed)))
				{
					if (output)
						//change progress percent
						s.setPercent(oldPercent / 2);
					
					controller.branch(addResult(s, lexResult.get(1), lexResult.get(3)));
					
					if (output)
						s.setPercent(oldPercent);
					
					//revert changes
					revert(s);
				}
				else
					if (output)
						controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 2);
			
			}
		}
		
		return s;
	}

}
