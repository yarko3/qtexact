/**
 * Yaroslav Senyuta
 * NSERC USRA Grant (2014)
 */

package qtUtils;


import java.util.ArrayList;
import java.util.LinkedList;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

//a class to keep the return graph and the number of alterations that have been made
public class branchingReturnC<V> implements Comparable<branchingReturnC<V>>
{
	Graph<V, Pair<V>> G;
	ArrayList<LinkedList<V>> deg;
	LinkedList<myEdge<V>> changes;
	branchingReturnC<V>	minMoves;
	
	public branchingReturnC(Graph<V, Pair<V>> graph, ArrayList<LinkedList<V>> d, LinkedList<myEdge<V>> c, branchingReturnC<V> m)
	{
		G = graph;
		changes = c;
		deg = d;
		minMoves = m;
	}
	public branchingReturnC(Graph<V, Pair<V>> graph, ArrayList<LinkedList<V>> d, LinkedList<myEdge<V>> c)
	{
		G = graph;
		changes = c;
		deg = d;
	}
	public branchingReturnC(Graph<V, Pair<V>> graph, ArrayList<LinkedList<V>> d, branchingReturnC<V> m)
	{
		G = graph;
		deg = d;
		changes = new LinkedList<myEdge<V>>();
		minMoves = m;
	}
	public branchingReturnC(Graph<V, Pair<V>> graph, ArrayList<LinkedList<V>> d)
	{
		G = graph;
		deg = d;
		changes = new LinkedList<myEdge<V>>();
	}

	@Override
	public int compareTo(branchingReturnC<V> o) {
		return Integer.compare(changes.size(), o.changes.size());
	}
	
	public Graph<V, Pair<V>> getG(){
		return G;
	}
	
	public LinkedList<myEdge<V>> getChanges()
	{
		return changes;
	}
	
	public ArrayList<LinkedList<V>> getDeg()
	{
		return deg;
	}
	
	public void setGraph(Graph<V, Pair<V>> g)
	{
		G = g;
	}
	public void setChanges(LinkedList<myEdge<V>> c)
	{
		changes = c;
	}
	public void setDeg(ArrayList<LinkedList<V>> d)
	{
		deg = d;
	}
	public void setMinMoves(branchingReturnC<V> m)
	{
		minMoves = m;
	}
	public branchingReturnC<V> getMinMoves()
	{
		return minMoves;
	}
	
}
