package clusterRules;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import qtUtils.branchingReturnC;
import abstractClasses.Certificate;
import abstractClasses.SearchResult;
import controller.Controller;

public class clusterAllStruct<V> extends clusterBranch<V> 
{

	private clusterP4<V> p4;
	private clusterC4<V> c4;
	private clusterClaw<V> claw;
	private clusterPaw<V> paw;
	
	public clusterAllStruct(Controller<V> controller) {
		super(controller);
		
		//initialize all branching structures
		p4 = new clusterP4<V>(controller);
		c4 = new clusterC4<V>(controller);
		claw = new clusterClaw<V>(controller);
		paw = new clusterPaw<V>(controller);
		
		output = controller.getOutputFlag();
	}
	
	
	@Override
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		//update search result
		searchResult = findStructures(s, searchResult);
		
		int flag = searchResult.getCertificate().getFlag();
		
		switch (flag)
		{
		//a C4 was found
		case (-1):
			
			c4.branchingRules(s, searchResult);
			return s;
		//a P4 was found
		case (-2):
			p4.branchingRules(s, searchResult);
			return s;
		//a claw
		case (-14):
			claw.branchingRules(s, searchResult);
			return s;
		//a paw
		case (-15):
			paw.branchingRules(s, searchResult);
			return s;
		default:
			return super.branchingRules(s, searchResult);
		}
	}
	/**
	 * find 4-vertex structures from P3 obstruction
	 * Index:
	 * -14 claw
	 * -15 paw
	 * -1 C4
	 * -2 P4
	 * 
	 * @param s current branching state
	 * @param searchResult known P3 obstruction
	 * @return improved search result or old search result
	 */
	public SearchResult<V> findStructures(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		Certificate<V> obstruction = searchResult.getCertificate();
		List<V> vertices = obstruction.getVertices();
		
		HashMap<V, Integer> hash = new HashMap<V, Integer>();
		
		//add one for every common neighbour into hash
		for (V v : vertices)
		{
			for (V n : s.getG().getNeighbors(v))
			{
				int entry = 1;
				if (hash.containsKey(n))
					entry = hash.get(n) + 1;
				hash.put(n, entry);
			}
		}
		
		//remove vertices of structure from hash
		for (V v : vertices)
			hash.remove(v);
		
		
		for (V n : hash.keySet())
		{
			//number of neighbours of n in obstruction
			int nVal = hash.get(n);
			
			if (nVal == 1)
			{
				
				
				//check for claw
				if (s.getG().isNeighbor(n, vertices.get(1)))
				{
					
					vertices.add(3, n);
					searchResult.getCertificate().setFlag(-14);
					return searchResult;
				}
				//a P4 is present
				else
				{
					if (s.getG().isNeighbor(n, vertices.get(0)))
					{
						vertices.add(0, n);
					}
					else
					{
						vertices.add(n);
					}
					searchResult.getCertificate().setFlag(-2);
					return searchResult;
				}
			}
			else if (nVal == 2)
			{
				//check for paw
				//first check
				if (
						(s.getG().isNeighbor(n, vertices.get(2))
						&& s.getG().isNeighbor(n, vertices.get(1))))
				{
					vertices.add(n);
					searchResult.getCertificate().setFlag(-15);
					return searchResult;
				}
				//second check
				else if ((s.getG().isNeighbor(n, vertices.get(0))
						&& s.getG().isNeighbor(n, vertices.get(1))))
				{
					//must reverse order of vertices
					Collections.reverse(vertices);
					
					vertices.add(n);
					searchResult.getCertificate().setFlag(-15);
					return searchResult;
				}
				//otherwise a C4 was found
				else
				{
					vertices.add(n);
					searchResult.getCertificate().setFlag(-1);
					return searchResult;
				}
				
			}
			
		}
		//no better obstruction was found
		return searchResult;
	}
	
	

}
