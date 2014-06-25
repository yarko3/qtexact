package abstractClasses;

import java.util.LinkedList;
import java.util.Stack;

import qtUtils.branchingReturnC;

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
	 * a search class used for identifying a solution or a certificate to branch on 
	 */
	protected Search<V> search;
	/**
	 * a controller used to run the branching
	 */
	protected Controller<V> controller;
	
	public static Cloner clone = new Cloner();
	
	
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
	 * @param G
	 * @param bound
	 * @return
	 */
	public abstract branchingReturnC<V> setup(Graph<V, Pair<V>> G, int bound);
	
	/**
	 * the branching rules, given an obstruction in SearchResult
	 * @param s
	 * @param sResult
	 * @return
	 */
	public abstract branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> sResult);

	public Search<V> getSearch() {
		return search;
	}
	
	/**
	 * reduction step prior to performing a search
	 * @param s initial edit state
	 * @return reduced edit state
	 */
	public branchingReturnC<V> reduce(branchingReturnC<V> s)
	{
		//get number of moves done. Run reductions until no further changes are made.
		//push number of changes onto stack.
		int bound = s.getMinMoves().getChanges().size() - s.getChanges().size();
		int oldMoves = s.getChanges().size();
		int newMoves = 0;
		int count = 0;
		
		if (reductions != null)
		{
			do
			{
				oldMoves = newMoves;
				count++;
				for (int i = 0; i < reductions.size(); i++)
				{
					s = reductions.get(i).reduce(s);
				}
				newMoves = s.getChanges().size();
				
				bound = s.getMinMoves().getChanges().size() - s.getChanges().size();
				
			} while(newMoves != oldMoves && bound > 0);
			
			reductionStack.push(count);
		}
		return s;
	}
	
	public branchingReturnC<V> reduceRevert(branchingReturnC<V> s)
	{
		if (reductions != null)
		{
			//pop the number of reduction loops ran during reduction
			int count = reductionStack.pop();
			for (int j = 0; j < count; j++)
			{
				for (int i = reductions.size() - 1; i >= 0; i--)
				{
					s = reductions.get(i).revertReduce(s);
				}
			}
		}
		return s;
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
	
	public LinkedList<Reduction<V>> getReductions()
	{
		return reductions;
	}
}
