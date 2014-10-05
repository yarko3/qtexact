package branch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import abstractClasses.Certificate;
import abstractClasses.SearchResult;
import controller.Controller;
import edu.uci.ics.jung.graph.util.Pair;

public class qtHouse<V> extends qtBranchNoHeuristic<V> 
{

	/**
	 * constructor
	 * @param controller
	 */
	public qtHouse(Controller<V> controller) {
		super(controller);
	}
	
	
	@Override
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		
		//if a house was found, apply house rules
		if (searchResult.getCertificate().getFlag() == -4)
		{
			ArrayList<V> lexResult = searchResult.getCertificate().getVertices();
			double oldPercent = s.getPercent();
			
			int ruleCount = 21;
			
			//add 1, delete 3
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), false, directed)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(1)), true, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), true, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(0)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemove3Result(s, 
						lexResult.get(0), lexResult.get(3), 
						lexResult.get(2), lexResult.get(1), 
						lexResult.get(1), lexResult.get(4),
						lexResult.get(1), lexResult.get(0)));
				
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
			
			
			//add 1, delete 3
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false, directed)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(0)), true, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), true, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(3)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemove3Result(s, 
						lexResult.get(0), lexResult.get(2), 
						lexResult.get(4), lexResult.get(0), 
						lexResult.get(1), lexResult.get(4),
						lexResult.get(4), lexResult.get(3)));
				
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
			
			
			//add 2, delete 2
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false, directed)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(0)), false, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), true, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(add2Remove2Result(s, 
						lexResult.get(0), lexResult.get(2), 
						lexResult.get(3), lexResult.get(0), 
						lexResult.get(1), lexResult.get(4),
						lexResult.get(1), lexResult.get(2)));
				
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
			
			//add 2, delete 2
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false, directed)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(0)), false, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), true, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(2)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(add2Remove2Result(s, 
						lexResult.get(0), lexResult.get(2), 
						lexResult.get(3), lexResult.get(0), 
						lexResult.get(1), lexResult.get(4),
						lexResult.get(3), lexResult.get(2)));
				
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
			
			
			//add 2, delete 2
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false, directed)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(0)), false, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(add2Remove2Result(s, 
						lexResult.get(0), lexResult.get(2), 
						lexResult.get(3), lexResult.get(0), 
						lexResult.get(3), lexResult.get(4),
						lexResult.get(1), lexResult.get(4)));
				
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
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true, directed)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(delete3Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3), lexResult.get(1), lexResult.get(4)));
				
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
			

			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(4)), true, directed)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(delete3Result(s, lexResult.get(0), lexResult.get(4), lexResult.get(2), lexResult.get(3), lexResult.get(1), lexResult.get(4)));
				
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
			

			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true, directed)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(delete3Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(3), lexResult.get(4), lexResult.get(1), lexResult.get(4)));
				
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
			
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(4)), true, directed)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true, directed))
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(delete3Result(s, lexResult.get(0), lexResult.get(4), lexResult.get(1), lexResult.get(2), lexResult.get(1), lexResult.get(4)));
				
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
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true, directed)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				controller.branch(delete2Result(s, lexResult.get(1), lexResult.get(2), lexResult.get(3), lexResult.get(4)));
				
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
			
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true, directed)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(delete2Result(s, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3)));
				
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
			
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(delete2Result(s, lexResult.get(2), lexResult.get(3), lexResult.get(3), lexResult.get(4)));
				
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
			
			
			//add one, remove one
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false, directed)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemoveResult(s, lexResult.get(1), lexResult.get(3), lexResult.get(3), lexResult.get(4)));
				
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
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false, directed)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(4)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemoveResult(s, lexResult.get(1), lexResult.get(3), lexResult.get(0), lexResult.get(4)));
				
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
			
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false, directed)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemoveResult(s, lexResult.get(1), lexResult.get(3), lexResult.get(2), lexResult.get(3)));
				
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
			
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), false, directed)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemoveResult(s, lexResult.get(2), lexResult.get(4), lexResult.get(1), lexResult.get(2)));
				
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
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), false, directed)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemoveResult(s, lexResult.get(2), lexResult.get(4), lexResult.get(2), lexResult.get(3)));
				
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
			
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), false, directed)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(addRemoveResult(s, lexResult.get(2), lexResult.get(4), lexResult.get(0), lexResult.get(1)));
				
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
			
			
			
			//add 2 edges
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false, directed)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), false, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(add2Result(s, lexResult.get(1), lexResult.get(3), lexResult.get(2), lexResult.get(4)));
				
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
			
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false, directed)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(0)), false, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(add2Result(s, lexResult.get(1), lexResult.get(3), lexResult.get(3), lexResult.get(0)));
				
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
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), false, directed)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				
				controller.branch(add2Result(s, lexResult.get(2), lexResult.get(4), lexResult.get(0), lexResult.get(2)));
				
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
			
			
			return s;
			
		}
		else
			return super.branchingRules(s, searchResult);
		
		
	}
	
	private Certificate<V> hasHouse(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		
		Certificate<V> obstruction = searchResult.getCertificate();
		ArrayList<V> oVert = obstruction.getVertices();
		
		//if a C4 was found
		if (obstruction.getFlag() == -1)
		{
			//look through every edge in C4
			for (int i = 0; i < 4; i++)
			{
				//index of other vertex
				int v1num = i+1;
				if (v1num ==4)
					v1num = 0;
				
				V v0 = oVert.get(i);
				V v1 = oVert.get(v1num);
				
				
				//look for common neighbours that fit house constraint
				HashSet<V> all = commonNeighbours(s, v0, v1);
				
				//check for no edges to the rest of c4 from common neighbours
				HashSet<V> common = new HashSet<V>();
				
				for (V n : all)
				{
					boolean flag = true;
					
					for (V temp : oVert)
					{
						if (temp == v0 || temp == v1)
							continue;
						
						if (s.getG().isNeighbor(temp, n))
							flag = false;
					}
					if (flag)
						common.add(n);
				}
				
				//now common contains all the possible nodes for the peak of the house
				if (!common.isEmpty())
				{
					V n = common.iterator().next();
					
					//build new obstruction structure
					ArrayList<V> newObst = new ArrayList<V>();
					
					newObst.add(n);
					newObst.add(v1);
					
					if (++v1num > 3)
						v1num = 0;
					while (newObst.size() < 5)
					{
						newObst.add(oVert.get(v1num));
						if (++v1num > 3)
							v1num = 0;
					}
					
					obstruction.setFlag(-4);
					obstruction.setVertices(newObst);
					return obstruction;
				}
			}
		}
		//house from P4
		else
		{
			//three options from a P4
			//look for house
			Certificate<V> order = checkP4(s, searchResult);
			
			//has a house been found
			if (order.getFlag() == -4)
				return order;
			
			//reverse order of obstruction and try again
			Collections.reverse(searchResult.getCertificate().getVertices());
			
			Certificate<V> reversed = checkP4(s, searchResult);
			
			if (order.getFlag() == -4)
				return reversed;
		}
		return obstruction;
		
	}
	
	private Certificate<V> checkP4(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		ArrayList<V> oVert = searchResult.getCertificate().getVertices();
		Certificate<V> obstruction = searchResult.getCertificate();
		
		
		HashSet<V> common = commonNeighbours(s, oVert.get(0), oVert.get(1), oVert.get(3));
		
		for (V n : common)
		{
			if (!s.getG().isNeighbor(n, oVert.get(2)))
			{
				//a house has been found
				oVert.add(n);
				obstruction.setFlag(-4);
				obstruction.setVertices(oVert);
				return obstruction;
			}
		}
		
		//no house was found
		return obstruction;
	}
	
}
