package controller;

import java.util.ArrayList;
import java.util.LinkedList;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import search.qtLBFS;
import abstractClasses.Branch;
import abstractClasses.Dive;
import abstractClasses.SearchResult;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * an abstract class responsible for the recursive control of branching
 * a controller is paired with a Branch for editing
 * @author Yarko Senyuta
 *
 * @param <V> vertex
 */
public class Controller<V> 
{
	/**
	 * branching structure used by the controller
	 */
	protected Branch<V> bStruct;
	/**
	 * number of times branching was run
	 */
	protected int timesRun;
	/**
	 * global percent counter (exact)
	 */
	private double globalPercent;
	/**
	 * printed percent counter (approximate)
	 */
	private double percent;
	/**
	 * output progress to console?
	 */
	private boolean output;
	/**
	 * use diving method after search bottoms out?
	 */
	private boolean useDive;
	/**
	 * original graph size (before connected component split)
	 */
	private int ogGraphSize;
	/**
	 * best diving solution
	 */
	private LinkedList<myEdge<V>>bestDiveSol;
	/**
	 * set useDive
	 * @param useDive whether to use diving strategy
	 */
	public void setUseDive(boolean useDive) {
		this.useDive = useDive;
	}
	
	
	ArrayList<LinkedList<myEdge<V>>> solutions = new ArrayList<LinkedList<myEdge<V>>>();
	
	
	
	/**
	 * constructor
	 * @param b Branch object
	 */
	public Controller(Branch<V> b) {
		super();
		bStruct = b;
		globalPercent = 0;
		percent = 0;
		timesRun = 0;
		output = false;
	}
	/**
	 * set output flag with constructor
	 * @param b branching structure
	 * @param o output
	 */
	public Controller(Branch<V> b, boolean o) {
		super();
		bStruct = b;
		globalPercent = 0;
		timesRun = 0;
		output = o;
	}
	
	/**
	 * constructor with dive
	 * @param b branching structure
	 * @param o output flag
	 * @param g diving strategy
	 */
	public Controller(Branch<V> b, boolean o, Dive<V> g)
	{
		super();
		bStruct = b;
		globalPercent = 0;
		timesRun = 0;
		output = o;
	}
	
	/**
	 * return dive flag
	 * @return dive flag
	 */
	public boolean getUseDive()
	{
		return useDive;
	}
	
	/**
	 * set global percent 
	 * @param p global percent
	 */
	public void setGlobalPercent(double p)
	{
		globalPercent = p;
	}
	/**
	 * get global percent
	 * @return global percent
	 */
	public double getGlobalPercent()
	{
		return globalPercent;
	}
	/**
	 * return branching structure
	 * @return branching structure
	 */
	public Branch<V> getbStruct() {
		return bStruct;
	}

	/**
	 * set branching structure
	 * @param bStruct branching structure
	 */
	public void setbStruct(Branch<V> bStruct) {
		this.bStruct = bStruct;
	}
	
	/**
	 * get output flag
	 * @return output flag
	 */
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
	public branchingReturnC<V> branchID(Graph<V, Pair<V>> G, int START, int MAX)
	{
		//bound to iterate down to
		int bound = START + 1;
		branchingReturnC<V> goal = null;
		
		//while graph is not solved and the bound is less than MAX
		while (bound <= MAX)
		{
			goal = branchStart(G, bound);
			//test if current graph is QT
			if (bStruct.getSearch().isTarget(goal.getG()))
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
	 * edit strategy where a dive is done first and when a solution is found, exact solution tries to improve it
	 * @param G graph
	 * @param diveDepth maximum depth to dive to 
	 * @return edit state
	 */
	public branchingReturnC<V> diveAtStartEdit(Graph<V, Pair<V>> G, int diveDepth)
	{
		//setup the branchingReturnC with an empty MinMoves
		branchingReturnC<V> s = bStruct.setup(G);
		
		//run dive approach
		bStruct.getDive().dive(s);
		
		System.out.println("Dive terminated after " + s.getChanges().size() + " moves.");
		boolean success = bStruct.getSearch().isTarget(s.getG());
		System.out.println("Dive solution was " + ((success) ? "" : " not ") + "successful");
		
		//initialize old solution count & new solution count
		int oldC = s.getMinMoves().getChanges().size();
		int newC = 0;
		
		//save old moves as best solution so far
		s.getMinMoves().setChanges(Branch.clone.deepClone(s.getChanges()));
		
		
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
			int numRevert = 10;
			if (success)
			{
				if (s.getChanges().size() - 10 >= 0)
					numRevert = 10;
				else
				{
					numRevert = s.getChanges().size();
				}
				
				//revert the number of moves to be tried by exact algorithm
				bStruct.revert(s, numRevert);
			}
			//greedy did not solve
			else
			{
				s.getMinMoves().setChanges(bStruct.fillMinMoves(s, s.getChanges().size() + numRevert));
				oldC = s.getMinMoves().getChanges().size();
			}
			
			
			//branch with exact algorithm with a bound of numRevert
			branch(s);
			
			//new solution size
			newC = s.getMinMoves().getChanges().size();
			
			System.out.println("Current best solution: " + s.getMinMoves().getChanges().size());
			
			//if needed, apply new moves to graph
			if (oldC > newC)
			{
				bStruct.revertAll(s);
				bStruct.applyMoves(s, s.getMinMoves().getChanges());
				success = true;
			}
			
			
			
			//repeat until exact algorithm does not provide a better answer
		}while (oldC > newC || (!success));
		
		//undo all moves
		bStruct.revertAll(s);
		
		//check for solution correctness
		Graph<V, Pair<V>> rtn = bStruct.applyMoves(Branch.clone.deepClone(s.getG()), s.getMinMoves().getChanges());
		
		
		
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
		
		ogGraphSize = G.getVertexCount();
		
		
		//set up branching
		branchingReturnC<V> goal = bStruct.setup(G, bound);
		
		goal = branch(goal);
		
		
//		System.out.println("Solutions: ");
//		for (LinkedList<myEdge<V>> l : solutions)
//			System.out.println(l);
		
		//FOR RULE GENERATION
//		outputRules(solutions);
		
		
		System.out.println("Completed after moves: " + goal.getMinMoves().getChanges().size());
		
		
		
		Graph<V, Pair<V>> rtn = bStruct.applyMoves(Branch.clone.deepClone(goal.getG()), goal.getMinMoves().getChanges());
		
		
		
		System.out.println("Branching run: " + timesRun);
		
		
		//see if a greedy solution is available
		if (useDive && bestDiveSol != null)
		{
			System.out.println("Best greedy solution found: " + bestDiveSol.size());
		}
		
		
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
	 * @param s edit state
	 * @return edit state
	 */
	public branchingReturnC<V> branch(branchingReturnC<V> s)
	{
		//current number of allowed moves
		int bound = s.getMinMoves().getChanges().size() - s.getChanges().size();
		
		//check if bound allows any more moves (does not matter if current graph state is at target)
		//COMMENT THIS OUT FOR RULE GENERATION
		if (bound < 0)
		{
			if (useDive)
			{
				dive(s);
			}
			updatePercent(s);
			return s;
		}
		
		//increment the number of times this controller has branched
		timesRun++;
		
		
		//set flag for whether this node has been reduced
		boolean reduced = false;
		
		//if bStruct has a reduction and bound allows more moves, reduce
		if (bStruct.getReductions() != null && !bStruct.getReductions().isEmpty() && timesRun != 1)
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
				
				if (useDive)
				{
					dive(s);
				}
				
				
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
			//COMMENT OUT THIS LINE FOR RULE GENERATION
			if (bound > 0 && s.isContinueEditing())
			{
				branchingReturnC<V> rtn = bStruct.branchingRules(s, searchResult);
				
				if (reduced)
				{
					bStruct.reduceRevert(s);
				}
				
				
				return rtn;
			}	
			//bound does not permit more moves
			//COMMENT OUT FOR RULE GENERATION
			else
			{
				//use greedy algorithm to finish editing, if allowed
				if (useDive)
				{
					dive(s);
				}
				
				updatePercent(s);
				
				if (reduced)
				{
					bStruct.reduceRevert(s);
					s.setContinueEditing(true);
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
	
	/**
	 * after search bottoms out, dive and find a solution
	 * @param s edit state
	 */
	public void dive(branchingReturnC<V> s)
	{
		if (ogGraphSize == s.getG().getVertexCount())
		{
			int branchDepth = s.getChanges().size();
			if (bestDiveSol == null)
				bStruct.getDive().dive(s);
			else
				bStruct.getDive().dive(s, bestDiveSol.size());
			
			//update minMoves if solution found is good
			if (getbStruct().getSearch().isTarget(s.getG()))
			{
				if (bestDiveSol != null && bestDiveSol.size() > s.getChanges().size())
				{
					bestDiveSol = Branch.clone.deepClone(s.getChanges());
					System.out.println("Best greedy so far: " + bestDiveSol.size());
				}
				else if (bestDiveSol == null)
				{
					bestDiveSol = Branch.clone.deepClone(s.getChanges());
					System.out.println("Best dive so far: " + bestDiveSol.size());
				}
				
			}
			
			//revert greedy moves
			bStruct.revert(s, s.getChanges().size() - branchDepth);
			
		}
		
	}
	
	/**
	 * print the branching rule code for editing
	 * @param solutions
	 */
	private void outputRules(ArrayList<LinkedList<myEdge<V>>> solutions)
	{
		System.out.println("//RULES GENERATED BY EDITOR");
		System.out.println("int ruleCount = " + solutions.size() + ";");
		
		
		for (LinkedList<myEdge<V>> solution : solutions)
		{
			LinkedList<myEdge<V>> adds = new LinkedList<myEdge<V>>();
			LinkedList<myEdge<V>> deletes = new LinkedList<myEdge<V>>();
			
			//separate
			for (myEdge<V> edit : solution)
			{
				if (edit.isFlag())
					adds.add(edit);
				
				else
					deletes.add(edit);
			}
			
			System.out.println("//" + adds.size() + " additions and " + deletes.size() + " deletions");
			
			//write if condition
			String condition = "";
			for (myEdge<V> edit : solution)
			{
				if (!condition.equals(""))
					condition += "\n\t&& ";
				
				if (edit.isFlag())
				{
					condition += "!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(" + edit.getEdge().getFirst() + "), lexResult.get(" + edit.getEdge().getSecond() + ")), false, directed))";
				}
				else
					condition += "!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(" + edit.getEdge().getFirst() + "), lexResult.get(" + edit.getEdge().getSecond() + ")), true, directed))";
			}
			
			System.out.println("if (" + condition + ")\n{");
			
			System.out.println("\tif (output)");
			System.out.println("\t{");
			System.out.println("\t\t//change progress percent");
			System.out.println("\t\ts.setPercent(oldPercent / ruleCount);");
			System.out.println("\t}");
			for (myEdge<V> edit : solution)
			{
				//add edge
				if (edit.isFlag())
					System.out.println("\taddResult(s, lexResult.get(" + edit.getEdge().getFirst() + "), lexResult.get(" + edit.getEdge().getSecond() + "));");
				//delete edge
				else
					System.out.println("\tdeleteResult(s, lexResult.get(" + edit.getEdge().getFirst() + "), lexResult.get(" + edit.getEdge().getSecond() + "));");
			}
			
			System.out.println("\tcontroller.branch(s);");
			
			System.out.println("\trevert(s, " + solution.size() + ");");
			System.out.println("\tif (output)");
			System.out.println("\t{");
			System.out.println("\t\t//revert percent");
			System.out.println("\t\ts.setPercent(oldPercent);");
			System.out.println("\t}");
			System.out.println("}");
			
			System.out.println("else");
			
			System.out.println("\tif (output)");
			System.out.println("\t\tcontroller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);");
			System.out.println();
			
		}
		
	}
}
