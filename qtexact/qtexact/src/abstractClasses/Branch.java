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
	 * an output flag
	 */
	protected boolean output;
	
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
	 * given a move list, apply moves to graph
	 * @param s search state
	 * @param list list of myEdge objects that provide an addition or deletion flag
	 */
	public void applyMoves(branchingReturnC<V> s, LinkedList<myEdge<V>> list)
	{
		for (myEdge<V> edit : list)
		{
			if (edit.isFlag())
			{
				//add edge
				addResult(s, edit.getEdge().getFirst(), edit.getEdge().getSecond());
			}
			else
				//remove edge
				deleteResult(s, edit.getEdge().getFirst(), edit.getEdge().getSecond());
		}
	}

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
	/**
	 * delete 2 edges to remove a C4
	 * @param s search state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @param v2 vertex
	 * @param v3 vertex
	 * @return modified edit state
	 */
	public branchingReturnC<V> delete2Result(branchingReturnC<V> s, V v0, V v1, V v2, V v3)
	{	
		deleteResult(s, v0, v1);
		deleteResult(s, v2, v3);
		
		return s;
	}
	
	/**
	 * apply moves to graph
	 * @param graph graph
	 * @param list moves to be applied to graph
	 * @return modified graph
	 */
	public Graph<V, Pair<V>> applyMoves(Graph<V, Pair<V>> graph, LinkedList<myEdge<V>> list)
	{
		for (myEdge<V> m : list)
		{
			if (m.isFlag())
				graph.addEdge(m.getEdge(), m.getEdge().getFirst(), m.getEdge().getSecond());
			else
				if (!graph.removeEdge(m.getEdge()))
					graph.removeEdge(new Pair<V>(m.getEdge().getSecond(), m.getEdge().getFirst()));
		}
		
		return graph;
	}
	
	
	/**
	 * fill the current changes list with random deletions up to the minMoves bound
	 * @param s current search state
	 */
	public void fillChangeListAndApplyMoves(branchingReturnC<V> s)
	{
		//current number of moves
		int count = s.getChanges().size();
		//maximum number of moves allowed
		int bound = s.getMinMoves().getChanges().size();
		
		//cannot concurrently modify graph, so must create a move list to apply later
		LinkedList<myEdge<V>> toApply = new LinkedList<myEdge<V>>();
		
		//current moves list
		LinkedList<myEdge<V>> l = s.getChanges();
		forLoop:
		for (Pair<V> e : s.getG().getEdges())
		{
			//treat each edge in this set as a deletion
			if ((!l.contains(new myEdge<V>(e, false)) || !l.contains(new myEdge<V>(e, true))) && count < bound)
			{	
				//make random deletions to fill move list
				toApply.add(new myEdge<V>(e, false));
				count++;
				if (count >= bound)
					break forLoop;
			}
		}
		
		//apply moves
		applyMoves(s, toApply);
	}
	
	/**
	 * fill a linked list with a myEdge LinkedList for the minMoves set
	 * @param G graph
	 * @return minimum move set
	 */
	protected LinkedList<myEdge<V>> fillMinMoves(branchingReturnC<V> s, int bound)
	{
		LinkedList<myEdge<V>> l = new LinkedList<myEdge<V>>();
		int count = 0;
		if (bound > 0)
		{
			l.addAll(s.getChanges());
			count += s.getChanges().size();
			
			if (count < bound)
				for (Pair<V> e : s.getG().getEdges())
				{
					//treat each edge in this set as a deletion
					if (!l.contains(new myEdge<V>(e, false)) || !l.contains(new myEdge<V>(e, true)))
					{	
						l.add(new myEdge<V>(e, false));
						count++;
						if (count == bound)
							break;
					}
				}
		}
		

		if (count < bound && s.getG().getVertexCount() > 0)
		{
			V v0 = s.getG().getVertices().iterator().next();
			while (count < bound)
			{
				//add a self edge
				l.add(new myEdge<V>(new Pair<V>(v0, v0), false));
				count++;
			}
		}
		return l;
	}
	
}
