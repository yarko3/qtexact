/**
 * Yaroslav Senyuta
 * NSERC USRA Grant (2014)
 */

package qtUtils;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * a class to keep the state of the graph and the number of alterations that have been made
 * @author Yaroslav Senyuta
 *
 * @param <V> vertex
 */
public class branchingReturnC<V> implements Comparable<branchingReturnC<V>>
{
	/**
	 * graph
	 */
	Graph<V, Pair<V>> G;
	/**
	 * degree sequence of graph
	 */
	ArrayList<LinkedList<V>> deg;
	/**
	 * changes made to the original graph
	 */
	LinkedList<myEdge<V>> changes;
	/**
	 * best solution so far (or a solution that bounds the search space)
	 */
	branchingReturnC<V>	minMoves;
	/**
	 * a set of moves assumed to cause obstructions
	 */
	HashSet<Pair<V>> knownBadEdges;
	/**
	 * percent progress made at current node of editing
	 */
	double percent;
	
	public branchingReturnC(Graph<V, Pair<V>> graph, ArrayList<LinkedList<V>> d, LinkedList<myEdge<V>> c, branchingReturnC<V> m)
	{
		G = graph;
		changes = c;
		deg = d;
		minMoves = m;
		knownBadEdges = new HashSet<Pair<V>>();
	}
	public branchingReturnC(Graph<V, Pair<V>> graph, ArrayList<LinkedList<V>> d, LinkedList<myEdge<V>> c)
	{
		G = graph;
		changes = c;
		deg = d;
		knownBadEdges = new HashSet<Pair<V>>();
	}
	public branchingReturnC(Graph<V, Pair<V>> graph, ArrayList<LinkedList<V>> d, branchingReturnC<V> m)
	{
		G = graph;
		deg = d;
		changes = new LinkedList<myEdge<V>>();
		minMoves = m;
		knownBadEdges = new HashSet<Pair<V>>();
	}
	public branchingReturnC(Graph<V, Pair<V>> graph, ArrayList<LinkedList<V>> d)
	{
		G = graph;
		deg = d;
		changes = new LinkedList<myEdge<V>>();
		knownBadEdges = new HashSet<Pair<V>>();
	}

	
	@Override
	public int compareTo(branchingReturnC<V> o) {
		return Integer.compare(minMoves.changes.size(), o.getMinMoves().changes.size());
	}
	
	/**
	 * returns stored graph
	 * @return graph
	 */
	public Graph<V, Pair<V>> getG(){
		return G;
	}
	
	/**
	 * returns changes made to original graph
	 * @return changes made
	 */
	public LinkedList<myEdge<V>> getChanges()
	{
		return changes;
	}
	
	/**
	 * returns degree sequence of graph
	 * @return degree sequence
	 */
	public ArrayList<LinkedList<V>> getDeg()
	{
		return deg;
	}
	
	/**
	 * sets the graph
	 * @param g graph to set
	 */
	public void setGraph(Graph<V, Pair<V>> g)
	{
		G = g;
	}
	
	/**
	 * sets changes made to graph
	 * @param c changes made
	 */
	public void setChanges(LinkedList<myEdge<V>> c)
	{
		changes = c;
	}
	
	/**
	 * sets degree sequence of graph
	 * @param d degree sequence
	 */
	public void setDeg(ArrayList<LinkedList<V>> d)
	{
		deg = d;
	}
	
	/**
	 * sets minMoves of traversal
	 * @param m minMoves
	 */
	public void setMinMoves(branchingReturnC<V> m)
	{
		minMoves = m;
	}
	
	/**
	 * returns minMoves of traversal
	 * @return minMoves
	 */
	public branchingReturnC<V> getMinMoves()
	{
		return minMoves;
	}
	
	/**
	 * set percentage of traversal
	 * @param p
	 */
	public void setPercent(double p)
	{
		percent = p;
	}
	
	/**
	 * return percentage of traversal
	 * @return
	 */
	public double getPercent()
	{
		return percent;
	}
	
	/**
	 * does current state of search have known bad edges
	 * @return true if bad edges are known
	 */
	public boolean hasBadEdges()
	{
		return !knownBadEdges.isEmpty();
	}
	
	/**
	 * return known bad edges 
	 * @return bad edge set
	 */
	public HashSet<Pair<V>> getKnownBadEdges() {
		return knownBadEdges;
	}
	
	/**
	 * set known bad edges
	 * @param knownBadEdges a set of edges believed to induce obstructions
	 */
	public void setKnownBadEdges(HashSet<Pair<V>> knownBadEdges) {
		this.knownBadEdges = knownBadEdges;
	}
	
}
