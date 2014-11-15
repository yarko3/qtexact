package abstractClasses;

import java.util.ArrayList;
import java.util.LinkedList;

import com.rits.cloning.Cloner;

import qtUtils.branchingReturnC;
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * class used to hold a search
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public abstract class Search<V> 
{
	/**
	 * cloner
	 */
	public static Cloner clone = new Cloner();
	
	//for testing connectivity
	protected WeakComponentClusterer<V, Pair<V>> cluster =  new WeakComponentClusterer<V, Pair<V>>();
		
	/**
	 * is the current graph at target state
	 * @param g graph
	 * @return true if at target state, false otherwise
	 */
	public abstract boolean isTarget(Graph<V, Pair<V>> g);
	/**
	 * if some steps are required to prepare graph data for search, it is done here
	 * @param s edit state
	 * @return search result
	 */
	protected abstract SearchResult<V> searchPrep(branchingReturnC<V> s);
	
	/**
	 * search graph
	 * @param s edit state
	 * @return search result
	 */
	public abstract SearchResult<V> search(branchingReturnC<V> s);
	
	/**
	 * Compute an ArrayList where every index holds a LinkedList of vertices with degrees of index
	 * @param G graph
	 * @return degree set
	 */
	public ArrayList<LinkedList<V>> degSequenceOrder(Graph<V, Pair<V>> G)
	{
		//store vertices of same degree in LinkedList<V> at the index of their degree in ArrayList
		ArrayList<LinkedList<V>> deg = new ArrayList<LinkedList<V>>();
		int max = 0;
		for (V i : G.getVertices())
		{
			if (G.degree(i) > max)
				max = G.degree(i);
		}
		
		//initialize LinkedList for each degree
		for (int i = 0; i <= max; i++)
		{
			deg.add(new LinkedList<V>());
		}
		
		//for every vertex, add it to the appropriate LinkedList
		for (V i : G.getVertices())
		{
			int iDeg = G.degree(i);
			deg.get(iDeg).add(i);
		}
		
		//deg.trimToSize();
		return deg;
	}
	/**
	 * Flatten and reverse degree order
	 * @param deg degree sequence
	 * @return vertex set in non-increasing degree order
	 */
	public ArrayList<V> flattenAndReverseDeg(ArrayList<LinkedList<V>> deg)
	{
		ArrayList<V> t = new ArrayList<V>(0);
		
		ArrayList<LinkedList<V>> degCopy = clone.deepClone(deg);
		//reverse the order of deg and flatten it for lexBFS
		for (int i = degCopy.size() - 1; i >=0; i--)
		{
			while (!degCopy.get(i).isEmpty())
			{
				t.add(degCopy.get(i).remove());
			}
		}
		return t;
	}

}
