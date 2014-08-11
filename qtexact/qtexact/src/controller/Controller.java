package controller;

import java.util.LinkedList;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import qtUtils.qtGenerate;
import search.qtLBFS;
import abstractClasses.Branch;
import abstractClasses.SearchResult;
import branch.qtBranch;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import greedy.maxObsGreedy;

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
	qtGenerate<V> gen = new qtGenerate<V>();
	
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
	
	LinkedList<LinkedList<myEdge<V>>> solutions = new LinkedList<LinkedList<myEdge<V>>>();
	
	public branchingReturnC<V> greedyEdit(Graph<V, Pair<V>> G)
	{
		//initialize greedy class
		maxObsGreedy<V> greedy = new maxObsGreedy<V>((qtBranch<V>) bStruct);
		//setup the branchingReturnC with an empty MinMoves
		branchingReturnC<V> s = bStruct.setup(G);
		//run greedy approach
		greedy.greedyEdit(s);
		
		System.out.println("Greedy terminated after " + s.getChanges().size() + " moves.");
		System.out.println("Greedy solution was " + ((bStruct.getSearch().isTarget(s.getG())) ? "" : " not ") + "successful");
		
		//initialize old solution count & new solution count
		int oldC = s.getMinMoves().getChanges().size();
		int newC = 0;
		
		//save old moves as best solution so far
		s.getMinMoves().setChanges(bStruct.clone.deepClone(s.getChanges()));
		
		
		do
		{
			//reset controller variables
			globalPercent = 0;
			percent = 0;
			timesRun = 0;
			
			//clear bad edges
			s.getKnownBadEdges().clear();
			
			//store old solution count
			oldC = s.getMinMoves().getChanges().size();
			
			//how many moves to undo
			int numRevert;
			if (s.getChanges().size() - 16 >= 0)
				numRevert = 16;
			else
			{
				numRevert = s.getChanges().size();
			}
			
			//revert the number of moves to be tried by exact algorithm
			((qtBranch<V>) bStruct).revert(s, numRevert);
			
			//branch with exact algorithm with a bound of numRevert
			branch(s);
			
			//new solution size
			newC = s.getMinMoves().getChanges().size();
			
			System.out.println("Current best solution: " + s.getMinMoves().getChanges().size());
			
			//if needed, apply new moves to graph
			if (oldC > newC)
			{
				((qtBranch<V>) bStruct).revertAll(s);
				((qtBranch<V>) bStruct).applyMoves(s, s.getMinMoves().getChanges());
			}
			
			
			//repeat until exact algorithm does not provide a better answer
		}while (oldC > newC);
		
		//undo all moves
		((qtBranch<V>) bStruct).revertAll(s);
		
		//check for solution correctness
		Graph<V, Pair<V>> rtn = gen.applyMoves(Branch.clone.deepClone(s.getG()), s.getMinMoves().getChanges());
		
		
		
		System.out.println("Branching run: " + timesRun);
		
		
		
		//return QT graph if edit succeeds
		if (bStruct.getSearch().isTarget(rtn))
		{
			System.out.println("Solution found. ");
			System.out.println(s.getMinMoves().getChanges());
			s.setGraph(rtn);
			return s;
		}
		else
		{
			//otherwise return original graph
			System.out.println("Solution not found. ");
			//goal.setGraph(null);
			return s;
		}
		
		
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

		
		//set up branching
		branchingReturnC<V> goal = bStruct.setup(G, bound);
		
		goal = branch(goal);
		
		
//		System.out.println("Solutions: ");
//		for (LinkedList<myEdge<V>> l : solutions)
//			System.out.println(l);
		
		
		System.out.println("Completed after moves: " + goal.getMinMoves().getChanges().size());
		
		
		
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
		//current number of allowed moves
		int bound = s.getMinMoves().getChanges().size() - s.getChanges().size();
		
		//check if bound allows any more moves (does not matter if current graph state is at target)
		if (bound < 0)
		{
			updatePercent(s);
			return s;
		}
		
		//increment the number of times this controller has branched
		timesRun++;
		
//		if (timesRun == 127)
//		{
//			@SuppressWarnings("unused")
//			int i = 0;
//		}
//		
//		if (s.getChanges().toString().equals("[Delete: <38, 1>]"))
//			System.out.println();
		
//		System.out.println(s.getChanges());
//		System.out.println("Times run: " + timesRun);
//		System.out.println("Bound: " + bound);
//		System.out.println("Moves made: " + s.getChanges().size());
//		System.out.println("Min moves: " + s.getMinMoves().getChanges().size());
//		System.out.println("Size of graph: " + s.getG().getVertexCount());

		
//		if (s.getMinMoves().getChanges().size() > globalBound)
//		{
//			System.out.println("\n\nWhoa: global bound " + globalBound);
//			System.out.println("Moves made: " + s.getChanges().size());
//			System.out.println();
//		}
//		
		
//		
		
		//set flag for whether this node has been reduced
		boolean reduced = false;
		
		//if branch has a reduction and bound allows more moves, reduce
		if (bStruct.getReductions() != null)
		{
			//run reduction	
			reduced = true;
			bStruct.reduce(s);
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
		
		SearchResult<V> searchResult = null;
		if (!s.getKnownBadEdges().isEmpty())
		{
			//find an obstruction on a bad edge and do connected component check
			while (searchResult == null && !s.getKnownBadEdges().isEmpty())
			{
				//get a bad edge from HashSet
				Pair<V> badEdge = s.getKnownBadEdges().iterator().next();
				//remove bad edge from HashSet
				s.getKnownBadEdges().remove(badEdge);
				//get search results from badEdge
				searchResult = ((qtLBFS<V>) (bStruct.getSearch())).searchResultFromBadEdge(s, badEdge);
			}
			
			//s.getKnownBadEdges().clear();
			
			if (searchResult == null)
			{
				searchResult = bStruct.getSearch().search(s);
			}
		}
		else
		{
			//check if graph is target
			searchResult =  bStruct.getSearch().search(s);
		}
		
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
			
			//revert reduction
			if (reduced)
			{
				bStruct.reduceRevert(s);
			}
			
			
			//USED FOR GETTING BRANCHING RULES
//			boolean flag = false;
//			
//			for (int i = 0; i < solutions.size(); i++)
//			{
//				LinkedList<myEdge<V>> l = solutions.get(i);
//				
//				//solution has already been found
//				if (s.getChanges().containsAll(l))
//				{
//					flag = true;
//					break;
//				}
//				
//				if (l.containsAll(s.getChanges()))
//				{
//					solutions.remove(i);
//					if (flag == false)
//					{
//						flag = true;
//						solutions.add(i, bStruct.clone.deepClone(s.getChanges()));
//					}
//				}
//			}
//			
//			
//			
//			
//			if (solutions.isEmpty() || flag == false)
//			{
//				solutions.add(bStruct.clone.deepClone(s.getChanges()));
//			}
//			
//			
			
			
			
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
