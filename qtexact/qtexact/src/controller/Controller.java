package controller;

import qtUtils.branchingReturnC;
import qtUtils.qtGenerate;
import abstractClasses.Branch;
import abstractClasses.SearchResult;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * an abstract class responsible for the recursive control of branching
 * a controller is paired with a Branch for editing
 * @author ssd
 *
 * @param <V>
 */
public class Controller<V> 
{
	
	/**
	 * constructor
	 * @param bStruct Branch object
	 */
	public Controller(Branch<V> bStruct) {
		super();
		this.bStruct = bStruct;
	}

	/**
	 * branching structure used by the controller
	 */
	protected Branch<V> bStruct;
	
	public Branch<V> getbStruct() {
		return bStruct;
	}

	public void setbStruct(Branch<V> bStruct) {
		this.bStruct = bStruct;
	}

	/**
	 * Iterative deepening edit
	 * 
	 * @param G graph to be edited
	 * @param START start depth of edits
	 * @param MAX maximum depth of edits
	 * @return an edited graph if solution is found or the original graph
	 */
	public Graph<V, Pair<V>> branchID(Graph<V, Pair<V>> G, int START, int MAX)
	{
		//bound to iterate down to
		int bound = START + 1;
		Graph<V, Pair<V>> goal = G;
		
		//while graph is not solved and the bound is less than MAX
		while (bound <= MAX + 1)
		{
			goal = branchStart(G, bound);
			//test if current graph is QT
			if (bStruct.getSearch().isTarget(goal))
			{
				return goal;
			}
			else
			{
				bound++;
			}
		}
		return goal;
	}
	
	/**
	 * start of edit
	 * @param G graph to be edited
	 * @param bound maximum depth of edits
	 * @return an edited graph, if one exists, or the original graph
	 */
	public Graph<V, Pair<V>> branchStart(Graph<V, Pair<V>> G, int bound)
	{
		//set up branching
		branchingReturnC<V> goal = bStruct.setup(G, bound);
		
		goal = branch(goal);
		
		System.out.println("Completed after moves: " + goal.getMinMoves().getChanges().size());
		
		qtGenerate<V> gen = new qtGenerate<V>();
		
		
		Graph<V, Pair<V>> rtn = gen.applyMoves(Branch.clone.deepClone(goal.getG()), goal.getMinMoves().getChanges());
		
		//return QT graph if edit succeeds
		if (bStruct.getSearch().isTarget(rtn))
		{
			System.out.println("Solution found. ");
			return rtn;
		}
		else
		{
			//otherwise return original graph
			System.out.println("Solution not found. ");
			return goal.getG();
		}
		
	}
	
	/**
	 * the branching structure 
	 * @param s
	 * @return
	 */
	public branchingReturnC<V> branch(branchingReturnC<V> s)
	{
		//run reduction step
		s = bStruct.reduce(s);
		
		
		//check if graph is target
		SearchResult<V> searchResult =  bStruct.getSearch().searchPrep(s);
		
		
		//target graph has been found
		if (searchResult.isTarget())
		{
			
			//update the minMoves list if this solution is better
			if (s.getChanges().size() < s.getMinMoves().getChanges().size())
			{
				//make a new minMoves to store
				branchingReturnC<V> newMin = new branchingReturnC<V>(s.getG(), s.getDeg(), Branch.clone.deepClone(s.getChanges()));
				newMin.setMinMoves(newMin);
				s.setMinMoves(newMin);
			}
			//reverse the reduction
			s = bStruct.reduceRevert(s);
			return s;
		}
		//branch on found obstruction
		else
		{	
			//only branch if current minMoves is longer than current state of search
			if (s.getMinMoves().getChanges().size() > s.getChanges().size())
			{
				branchingReturnC<V> rtn = bStruct.branchingRules(s, searchResult);
				
				branchingReturnC<V> reverted = bStruct.reduceRevert(s);
				s.setChanges(reverted.getChanges());
				s.setDeg(reverted.getDeg());
				s.setGraph(reverted.getG());
				
				return rtn;
			}
			//min moves is a better solution
				else
				{
					branchingReturnC<V> reverted = bStruct.reduceRevert(s);
					s.setChanges(reverted.getChanges());
					s.setDeg(reverted.getDeg());
					s.setGraph(reverted.getG());
					
					return s.getMinMoves();
				}
		}
	}
	

}