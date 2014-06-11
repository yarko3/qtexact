package branch;

import java.util.HashSet;

import qtUtils.branchingReturnC;
import abstractClasses.Certificate;
import abstractClasses.SearchResult;
import controller.Controller;

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
			all.addAll(s.getG().getNeighbors(obstruction.getVertices().get(0)));
			all.addAll(s.getG().getNeighbors(obstruction.getVertices().get(2)));
			
			all.removeAll(s.getG().getNeighbors(obstruction.getVertices().get(1)));
			all.removeAll(s.getG().getNeighbors(obstruction.getVertices().get(3)));
			
			//a kite has been found
			if (!all.isEmpty())
			{
				obstruction.getVertices().add(obstruction.getVertices().remove(3));
				obstruction.getVertices().add(4, all.iterator().next());
				obstruction.setFlag(-3);
				return obstruction;
			}
			
			//look through other two vertices
			all = new HashSet<V>();
			all.addAll(s.getG().getNeighbors(obstruction.getVertices().get(1)));
			all.addAll(s.getG().getNeighbors(obstruction.getVertices().get(3)));
			
			all.removeAll(s.getG().getNeighbors(obstruction.getVertices().get(0)));
			all.removeAll(s.getG().getNeighbors(obstruction.getVertices().get(2)));
			
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
