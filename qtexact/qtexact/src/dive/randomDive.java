package dive;

import java.util.ArrayList;
import java.util.Random;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import abstractClasses.Branch;
import abstractClasses.Dive;
import abstractClasses.SearchResult;
import edu.uci.ics.jung.graph.util.Pair;

public class randomDive<V> extends Dive<V> {

	private static Random rand = new Random();
	private int noBudge;
	public randomDive(Branch<V> b)
	{
		super(b);
	}
	
	@Override
	public void dive(branchingReturnC<V> s) {

		SearchResult<V>  searchResult = bStruct.getSearch().search(s);
		
		while (!searchResult.isTarget() && noBudge < 10)
		{
			randomChoice(s, searchResult);
			searchResult = bStruct.getSearch().search(s);
		}
		
	}

	@Override
	public void dive(branchingReturnC<V> s, int bound) 
	{
		SearchResult<V>  searchResult = bStruct.getSearch().search(s);
		
		int count = 0;
		while (!searchResult.isTarget() && count < bound && noBudge < 10)
		{
			randomChoice(s, searchResult);
			searchResult = bStruct.getSearch().search(s);
		}
		
	}
	
	private void randomChoice(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		ArrayList<V> lexResult = searchResult.getCertificate().getVertices();
		boolean moveMade = false;
		
		//C4 has been found
		if (searchResult.getCertificate().getFlag() == -1)
		{
			
			//randomly choose an addition
			
			double r = rand.nextDouble();
			
			
			if (r < .2)
			{
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false)))
				{
					
					bStruct.addResult(s, lexResult.get(0), lexResult.get(2));
					moveMade = true;
					
				}
				else
					r+= .2;
			}
				
			if (r < 0.4 && !moveMade)
			{
				
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
				{
					
					bStruct.addResult(s, lexResult.get(1), lexResult.get(3));
					moveMade = true;
				}
				else
					r+= .1;
			}
				
			if (r < .5 && !moveMade)
			{
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
				{
					bStruct.delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2));
					moveMade = true;
				}
				else
					r+= .1;
					
			}
				
				
			if (r < .6 && !moveMade)
			{
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
				{
					bStruct.delete2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3));
					moveMade = true;
				}
				else
					r+= .1;
					
					
			}
				
			if (r < .7 && !moveMade)
			{
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
				{
					bStruct.delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3));
					moveMade = true;
				}
				else
					r+= .1;
				
			}
				
				
			if (r < .8 && !moveMade)
			{
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
				{
					bStruct.delete2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2));
					moveMade = true;
				}
				else
					r+= .1;
				
			}
				
			if (r < 0.9 && !moveMade)
			{
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
				{
					
					bStruct.delete2Result(s, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3));
					moveMade = true;
				}
				else
					r+= .1;
					
			}
			 if (r < 1 && !moveMade)
			{
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)))
				{
					bStruct.delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3));
					moveMade = true;
					
				}
			
			}
		}
		
		//P4 has been found
		else
		{	
			double r = rand.nextDouble();
			
		
			
			if (r < 0.2){
				//remove an edge to break P4
				//remove middle edge first
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
				{
					bStruct.deleteResult(s, lexResult.get(1), lexResult.get(2));
					
				}
				else
					r+= .2;
			}
			
			
			if (r < 0.4 && !moveMade)
			{
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)))
				{
					
					bStruct.deleteResult(s, lexResult.get(0), lexResult.get(1));
					moveMade = true;
					
				}
				else
					r+= .2;
			}
			
			if (r < .6 && !moveMade)
			{
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
				{
					bStruct.deleteResult(s, lexResult.get(2), lexResult.get(3));
					moveMade = true;
					
				}
				else
					r+= .2;
			}
		
			
			if (r < 0.8 && !moveMade)
			{
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false)))
				{
					bStruct.addResult(s, lexResult.get(0), lexResult.get(2));
					moveMade = true;
				}
				else
					r+= .2;
			}
				
			if ( r < 1 && !moveMade)
			{
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
				{
					bStruct.addResult(s, lexResult.get(1), lexResult.get(3));
					moveMade = true;
				}
				
			
			}
		}
		
		if (!moveMade)
		{
			noBudge++;
		}
		else
			noBudge = 0;
		
	}

}
