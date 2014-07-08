package controller;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
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
	protected int timesRun;
	private double globalPercent;
	private double percent;
	private boolean output;
	private int globalBound;
	
	
	/**
	 * branching structure used by the controller
	 */
	protected Branch<V> bStruct;
	
	/**
	 * constructor
	 * @param bStruct Branch object
	 */
	public Controller(Branch<V> bStruct) {
		super();
		globalPercent = 0;
		percent = 0;
		timesRun = 0;
		output = false;
	}
	/**
	 * set output flag with constructor
	 * @param bStruct
	 * @param o
	 */
	public Controller(Branch<V> bStruct, boolean o) {
		super();
		globalPercent = 0;
		timesRun = 0;
		output = o;
	}
	
	public void setGlobalPercent(double p)
	{
		globalPercent = p;
	}
	
	public double getGlobalPercent()
	{
		return globalPercent;
	}
	
	public Branch<V> getbStruct() {
		return bStruct;
	}

	public void setbStruct(Branch<V> bStruct) {
		this.bStruct = bStruct;
	}
	
	public boolean getOutputFlag()
	{
		return output;
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
			goal = branchStart(G, bound).getG();
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
	public branchingReturnC<V> branchStart(Graph<V, Pair<V>> G, int bound)
	{
		//initialize percent
		globalPercent = 0;
		percent = 0;
		timesRun = 0;
		globalBound = bound;
		
		
		//set up branching
		branchingReturnC<V> goal = bStruct.setup(G, bound);
		
		goal = branch(goal);
		
		System.out.println("Completed after moves: " + goal.getMinMoves().getChanges().size());
		
		qtGenerate<V> gen = new qtGenerate<V>();
		
		
		Graph<V, Pair<V>> rtn = gen.applyMoves(Branch.clone.deepClone(goal.getG()), goal.getMinMoves().getChanges());
		
		System.out.println("Branching run: " + timesRun);
		
		
		
		//return QT graph if edit succeeds
		if (bStruct.getSearch().isTarget(rtn))
		{
			System.out.println("Solution found. ");
			System.out.println(goal.getMinMoves().getChanges());
			goal.setGraph(rtn);
			return goal;
		}
		else
		{
			//otherwise return original graph
			System.out.println("Solution not found. ");
			//goal.setGraph(null);
			return goal;
		}
		
	}
	
	/**
	 * the branching structure 
	 * @param s
	 * @return
	 */
	public branchingReturnC<V> branch(branchingReturnC<V> s)
	{
		//run garbage collection
		//System.gc();
		
		//current number of allowed moves
		int bound = s.getMinMoves().getChanges().size() - s.getChanges().size();
		
		//check if bound allows any more moves (does not matter if current graph state is at target)
		if (bound < 0)
		{
			updatePercent(s);
			return s.getMinMoves();
		}
		
		//increment the number of times this controller has branched
		timesRun++;
		
		
		System.out.println("Times run: " + timesRun);
		System.out.println("Bound: " + bound);
		System.out.println("Moves made: " + s.getChanges().size());
		System.out.println("Min moves: " + s.getMinMoves().getChanges().size());
		System.out.println("Size of graph: " + s.getG().getVertexCount());
		
		
		if (s.getMinMoves().getChanges().size() > globalBound)
		{
			System.out.println("\n\nWhoa: global bound " + globalBound);
			System.out.println("Moves made: " + s.getChanges().size());
			System.out.println();
		}
		
		
		
		//set flag for whether this node has been reduced
		boolean reduced = false;
		
		//if branch has a reduction and bound allows more moves, reduce
		if (bStruct.getReductions() != null && bound > 0)
		{
			//run reduction	
			reduced = true;
			s = bStruct.reduce(s);
			//update bound
			bound = s.getMinMoves().getChanges().size() - s.getChanges().size();
			
			//check if reductions have exceeded parameter
			if (bound < 0)
			{
				updatePercent(s);
				
				if (reduced)
				{
					bStruct.reduceRevert(s);
				}
				
				return s;
			}
		}
		
		//check if graph is target
		SearchResult<V> searchResult =  bStruct.getSearch().search(s);
		
		
		//target graph has been found
		if (searchResult.isTarget())
		{
			updatePercent(s);
			
			//update the minMoves list if this solution is better
			if (bound >= 0)
			{
				//make a new minMoves to store
				branchingReturnC<V> newMin = new branchingReturnC<V>(s.getG(), s.getDeg(), Branch.clone.deepClone(s.getChanges()));
				newMin.setMinMoves(newMin);
				s.setMinMoves(newMin);
			}
			
			if (reduced)
				bStruct.reduceRevert(s);
			
			return s;
		}
		//branch on found obstruction
		else
		{	
			//only branch if current minMoves is longer than current state of search
			if (bound > 0)
			{
				branchingReturnC<V> rtn = bStruct.branchingRules(s, searchResult);
				
				if (reduced)
				{
					bStruct.reduceRevert(s);
				}
				
				
				return rtn;
			}	
			//min moves is a better solution
			else
			{
				updatePercent(s);
				
				if (reduced)
				{
					bStruct.reduceRevert(s);
				}
				
				return s;
			}
		}
	}
	
	private void updatePercent(branchingReturnC<V> s)
	{
		if (output)
		{
			//update global percent
			globalPercent += s.getPercent();
			if (globalPercent - percent > .01)
			{
				percent = globalPercent;
				System.out.println(Math.round(percent*100.0) + "%");
			}
		}
	}

}
