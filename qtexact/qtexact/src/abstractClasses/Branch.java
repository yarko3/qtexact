package abstractClasses;

import java.util.LinkedList;
import java.util.Stack;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;

import com.rits.cloning.Cloner;

import controller.Controller;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Class for holding branching rules and search class associated with a particular graph edit
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public abstract class Branch<V> 
{
	/**
	 * a linked list which holds all of the reductions applicable to this Branch object
	 */
	protected LinkedList<Reduction<V>> reductions;
	
	/**
	 * a diving strategy for branching
	 */
	protected Dive<V> dive;
	
	/**
	 * @return the dive
	 */
	public Dive<V> getDive() {
		return dive;
	}

	/**
	 * @param dive the dive to set
	 */
	public void setDive(Dive<V> dive) {
		this.dive = dive;
	}

	/**
	 * a search class used for identifying a solution or a certificate to branch on 
	 */
	protected Search<V> search;
	/**
	 * a controller used to run the branching
	 */
	protected Controller<V> controller;
	
	/**
	 * cloner
	 */
	public static Cloner clone = new Cloner();
	
	/**
	 * use stack to store number of times reduction loop was run
	 */
	private Stack<Integer> reductionStack;
	
	/**
	 * a Branch datatype needs a controller to run recursively in branchingRules(...)
	 * @param controller
	 */
	public Branch(Controller<V> controller) {
		super();
		this.controller = controller;
		reductionStack = new Stack<Integer>();
	}

	/**
	 * set up the search environment (minMoves, etc.)
	 * @param G graph
	 * @param bound allowed moves
	 * @return branching return structure
	 */
	public abstract branchingReturnC<V> setup(Graph<V, Pair<V>> G, int bound);
	
	/**
	 * set up search environment without bound
	 * @param G graph
	 * @return branching return structure
	 */
	public abstract branchingReturnC<V> setup(Graph<V, Pair<V>> G);
	
	
	/**
	 * the branching rules, given an obstruction in SearchResult
	 * @param s edit state
	 * @param sResult
	 * @return traversed edit state
	 */
	public abstract branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> sResult);

	/**
	 * revert a move
	 * @param s
	 */
	public abstract void revert(branchingReturnC<V> s);

	/**
	 * delete an edge and return the edit state
	 * @param s edit state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @return modified edit state
	 */
	public abstract branchingReturnC<V> deleteResult(branchingReturnC<V> s, V v0, V v1);

	/**
	 * add an edge and return the edit state
	 * @param s edit state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @return modified edit state
	 */
	public abstract branchingReturnC<V> addResult(branchingReturnC<V> s, V v0, V v1);

	/**
	 * apply moves from list to edit state
	 * @param s edit state
	 * @param moves
	 */
	public abstract void applyMoves(branchingReturnC<V> s, LinkedList<myEdge<V>> moves);

	/**
	 * search tied to this branching strategy
	 * @return search 
	 */
	public Search<V> getSearch() {
		return search;
	}
	
	/**
	 * reduction step prior to performing a search
	 * @param s initial edit state
	 */
	public void reduce(branchingReturnC<V> s)
	{
		//get number of moves done. Run reductions until no further changes are made.
		//push number of changes onto stack.
		int bound = s.getMinMoves().getChanges().size() - s.getChanges().size();
		int oldMoves = s.getChanges().size();
		int newMoves = oldMoves;
		int count = 0;
		
		if (reductions != null)
		{
			do
			{
				oldMoves = newMoves;
				count++;
				//try to reduce with every reduction available
				for (int i = 0; i < reductions.size(); i++)
				{
					s = reductions.get(i).reduce(s);
				}
				newMoves = s.getChanges().size();
				
				bound = s.getMinMoves().getChanges().size() - s.getChanges().size();
				
			} while(newMoves != oldMoves && bound > 0);
			
			reductionStack.push(count);
		}
	}
	
	/**
	 * revert the reduction moved done
	 * @param s reverted edit state
	 */
	public void reduceRevert(branchingReturnC<V> s)
	{
		if (reductions != null)
		{
			//pop the number of reduction loops ran during reduction
			int count = reductionStack.pop();
			for (int j = 0; j < count; j++)
			{
				//every reduction rule must revert its changes
				for (int i = reductions.size() - 1; i >= 0; i--)
				{
					s = reductions.get(i).revertReduce(s);
				}
			}
		}
	}
	
	/**
	 * add a reduction to this branch object
	 * @param r reduction
	 */
	public void addReduction(Reduction<V> r)
	{
		if (reductions == null)
		{
			reductions = new LinkedList<Reduction<V>>();
		}
		reductions.add(r);
	}
	
	/**
	 * return reductions stored at this branching type
	 * @return reduction list
	 */
	public LinkedList<Reduction<V>> getReductions()
	{
		return reductions;
	}
	
	/**
	 * revert n number of changes from edit state
	 * @param s edit state
	 * @param n number of moves to revert
	 */
	public void revert(branchingReturnC<V> s, int n)
	{
		for (int i = 0; i < n; i++)
		{
			revert(s);
		}
	}
	
	/**
	 * revert all moves in edit state
	 * @param s edit state
	 */
	public void revertAll(branchingReturnC<V> s)
	{
		while (!s.getChanges().isEmpty())
		{
			revert(s);
		}
	}
	
	/**
	 * delete 2 edges at the same time 
	 * @param s edit state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @param v2 vertex
	 * @param v3 vertex
	 * @return modified edit state
	 */
	public abstract branchingReturnC<V> delete2Result(branchingReturnC<V> s, V v0, V v1, V v2, V v3);
	
	
}
