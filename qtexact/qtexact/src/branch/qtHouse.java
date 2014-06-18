package branch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

import qtUtils.branchingReturnC;
import abstractClasses.Certificate;
import abstractClasses.SearchResult;
import controller.Controller;

public class qtHouse<V> extends qtKite<V> 
{

	public qtHouse(Controller<V> controller) {
		super(controller);
	}
	
	
	@Override
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		//update certificate
		searchResult.setCertificate(hasHouse(s, searchResult));
		
		//if a house was found, apply house rules
		if (searchResult.getCertificate().getFlag() == -4)
		{
			
		}
		
		
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
				HashSet<V> all = commonNeighbours(s, v0, v1);;
				
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
		
		
		if (s.getG().isNeighbor(oVert.get(0), oVert.get(2)))
		{
			//find corner of the house
			
			//look through common neighbours of first and last node of P4 as well as 2nd and last
			
			//option 1
			V v0 = oVert.get(0);
			V v1 = oVert.get(3);
			
			HashSet<V> common = commonNeighbours(s, v0, v1);
			
			for (V n : common)
			{
				if (!s.getG().isNeighbor(n, oVert.get(2)) && !s.getG().isNeighbor(n, oVert.get(1)))
				{
					//a house has been found
					oVert.add(n);
					oVert.add(oVert.remove(0));
					obstruction.setFlag(-4);
					obstruction.setVertices(oVert);
					return obstruction;
				}
			}
			
			
			//option 2
			v0 = oVert.get(1);
			
			common = commonNeighbours(s, v0, v1);
			
			for (V n : common)
			{
				if (!s.getG().isNeighbor(n, oVert.get(2)) && !s.getG().isNeighbor(n, oVert.get(0)))
				{
					//a house has been found
					oVert.add(2, n);
					oVert.add(oVert.remove(3));
					obstruction.setFlag(-4);
					obstruction.setVertices(oVert);
					return obstruction;
				}
			}
			
		}
		
		//option 3
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
