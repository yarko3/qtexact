package branch;

import java.util.ArrayList;
import java.util.Random;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import abstractClasses.SearchResult;
import controller.Controller;
import edu.uci.ics.jung.graph.util.Pair;

public class qtRandom<V> extends qtBranchNoHeuristic<V> 
{
	public static Random rand = new Random();
	
	public qtRandom(Controller<V> controller) {
		super(controller);
	}
	
	
	/**
	 * Randomly choose a P4 edge to delete
	 */
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult) 
	{
		ArrayList<V> lexResult = searchResult.getCertificate().getVertices();
		double oldPercent = s.getPercent();
		
		//C4 has been found
		if (searchResult.getCertificate().getFlag() == -1)
		{
			//result of adding 1 edge to break C4
			//if we did not remove the edge that is about to be added
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false)))
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
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
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
		//P4 has been found
		else
		{	
			int index = rand.nextInt(3);
			
			
			switch (index)
			{
			case (0):
				//remove an edge to break P4
				//remove middle edge first
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
				{
					
					controller.branch(deleteResult(s, lexResult.get(1), lexResult.get(2)));
					
					//revert changes to global graph
					revert(s);
					
					return s;
				}
			
			case (1):
				//try other edges later
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)))
				{
					
					controller.branch(deleteResult(s, lexResult.get(0), lexResult.get(1)));
					
					//revert changes to global graph
					revert(s);
					
					return s;
				}
			
			case (2):
			
				if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
				{
					
					controller.branch(deleteResult(s, lexResult.get(2), lexResult.get(3)));
					
					//revert changes to global graph
					revert(s);
					
					return s;
				}
			
			}
		}
		
		return s;
	}

}
