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

/**
 * Branching on a kite formation
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public class qtKite<V> extends qtBranchNoHeuristic<V> 
{

	public qtKite(Controller<V> controller) {
		super(controller);
	}
	
	
	@Override
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		//update certificate
		searchResult.setCertificate(hasKite(s, searchResult));
		
		//if a kite has been found, use new branching rules
		if (searchResult.getCertificate().getFlag() == -3)
		{
			ArrayList<V> lexResult = searchResult.getCertificate().getVertices();
			double oldPercent = s.getPercent();
			
			//kite branching
			//add one edge on C4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / 6.0);
				}
				branchingReturnC<V> kAdd = controller.branch(addResult(s, lexResult.get(1), lexResult.get(3)));
				
				//revert changes
				revert(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
				
				if (kAdd.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(kAdd.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 6);
			
			//delete 2 edges
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / 6.0);
				}
				branchingReturnC<V> kDelete20 = controller.branch(delete2Result(s, lexResult.get(2), lexResult.get(3), lexResult.get(3), lexResult.get(4)));
				
				//revert changes
				revert2(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
				
				if (kDelete20.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(kDelete20.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 6);
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(1)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / 6.0);
				}
				branchingReturnC<V> kDelete21 = controller.branch(delete2Result(s, lexResult.get(2), lexResult.get(3), lexResult.get(4), lexResult.get(1)));
				
				//revert changes
				revert2(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
				
				if (kDelete21.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(kDelete21.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 6);
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(3), lexResult.get(4)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / 6.0);
				}
				branchingReturnC<V> kDelete22 = controller.branch(delete2Result(s, lexResult.get(1), lexResult.get(2), lexResult.get(3), lexResult.get(4)));
				
				//revert changes
				revert2(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
				
				if (kDelete22.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(kDelete22.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 6);
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(4), lexResult.get(1)), true)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / 6.0);
				}
				branchingReturnC<V> kDelete23 = controller.branch(delete2Result(s, lexResult.get(1), lexResult.get(2), lexResult.get(4), lexResult.get(1)));
				
				//revert changes
				revert2(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
				
				if (kDelete23.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(kDelete23.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 6);
			
			//add one, remove one
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) 
					&& !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(4)), false)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / 6.0);
				}
				
				
				branchingReturnC<V> kAddDelete = deleteResult(s, lexResult.get(0), lexResult.get(1));
				kAddDelete = controller.branch(addResult(kAddDelete, lexResult.get(2), lexResult.get(4)));
				
				//revert changes
				revert2(s);		
				
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
				
				if (kAddDelete.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(kAddDelete.getMinMoves());
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / 6);
			
			return s.getMinMoves();
		}
		//otherwise use old branching rules
		else
		{
			return super.branchingRules(s, searchResult);
		}
		
	}
	
	
	/**
	 * attempt to find kite formation from obstruction
	 * 
	 * @param s search state
	 * @param searchResult search result
	 * @return kite certificate or original certificate
	 */
	private Certificate<V> hasKite(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		
		Certificate<V> obstruction = searchResult.getCertificate();
		//if obstruction is a C4
		if (obstruction.getFlag() == -1)
		{
			for (int i = 0; i < obstruction.getVertices().size(); i++)
			{
				V v = obstruction.getVertices().get(i);
				
				HashSet<V> all = new HashSet<V>();
				//get all common neighbours
				for (V temp : obstruction.getVertices())
				{
					all.addAll(s.getG().getNeighbors(temp));
				}
				//remove all neighbours other than those of v
				for (V temp : obstruction.getVertices())
				{
					if (temp != v)
						all.removeAll(s.getG().getNeighbors(temp));
				}
				//a kite has been found
				if (!all.isEmpty())
				{
					//change certificate
					obstruction.getVertices().add(0, all.iterator().next());
					
					//rotate C4 into proper shape
					while (obstruction.getVertices().get(1) != v)
					{
						obstruction.getVertices().add(1, obstruction.getVertices().remove(4));
					}
					
					obstruction.setFlag(-3);
					return obstruction;
				}
			}	
		}
		//a P4 was found
		else
		{
			//look through common neighbours of vertices 0 & 2 and 1 & 3
			HashSet<V> all = new HashSet<V>();
			HashSet<V> common = new HashSet<V>();
			all.addAll(s.getG().getNeighbors(obstruction.getVertices().get(0)));
			
			//get common ones
			for (V n : s.getG().getNeighbors(obstruction.getVertices().get(2)))
			{
				if (!all.add(n))
					common.add(n);
			}
			
			all = common;
			
			all.removeAll(s.getG().getNeighbors(obstruction.getVertices().get(1)));
			all.removeAll(s.getG().getNeighbors(obstruction.getVertices().get(3)));
			
			all.removeAll(obstruction.getVertices());
			
			
			//a kite has been found
			if (!all.isEmpty())
			{
				//reverse obstruction order
				Collections.reverse(obstruction.getVertices());
				
				obstruction.getVertices().add(4, all.iterator().next());
				obstruction.setFlag(-3);
				return obstruction;
			}
			
			//look through other two vertices
			all = new HashSet<V>();
			all.addAll(s.getG().getNeighbors(obstruction.getVertices().get(1)));

			//get common ones
			for (V n : s.getG().getNeighbors(obstruction.getVertices().get(3)))
			{
				if (!all.add(n))
					common.add(n);
			}
			all = common;
			
			
			all.removeAll(s.getG().getNeighbors(obstruction.getVertices().get(0)));
			all.removeAll(s.getG().getNeighbors(obstruction.getVertices().get(2)));
			
			all.removeAll(obstruction.getVertices());
			
			//a kite has been found
			if (!all.isEmpty())
			{
				obstruction.getVertices().add(4, all.iterator().next());
				obstruction.setFlag(-3);
				return obstruction;
			}
			
		}
		
		return obstruction;
	}
	
}
