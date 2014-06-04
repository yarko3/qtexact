package abstractClasses;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import com.rits.cloning.Cloner;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.Pair;



public abstract class qtBranch<V> extends Branch<V> 
{
	protected qtLBFS<V> search;
	
	
	/**
	 * edit the graph by adding an edge between v0 and v1
	 * @param s search state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @return edited search state
	 */
	protected branchingReturnC<V> c4p4AddResult(branchingReturnC<V> s, V v0, V v1)
	{
		
		//update degree sequence (first edge)
		addEdge(s.getG(), s.getDeg(), v0, v1);
		
		//add edge to changes 
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v0, v1), true));
		
		return s;
		
	}
	/**
	 * revert the changes of adding an edge on v0 and v1
	 * @param s search state
	 * @param v0 vertex
	 * @param v1 vertex
	 */
	protected void c4p4AddRevert(branchingReturnC<V> s, V v0, V v1)
	{
		//update degree sequence (first edge)
		removeEdge(s.getG(), s.getDeg(), v0, v1);
		
		s.getChanges().removeLast();
	}
	

	/**
	 * delete 2 edges to remove a C4
	 * @param s search state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @param v2 vertex
	 * @param v3 vertex
	 * @return search state
	 */
	protected branchingReturnC<V> c4Delete2Result(branchingReturnC<V> s, V v0, V v1, V v2, V v3)
	{	
		//update degree sequence (first edge)
		removeEdge(s.getG(), s.getDeg(), v0, v1);
		
		//update degree sequence (second edge)
		removeEdge(s.getG(), s.getDeg(), v2, v3);
		
		//add edge deletions to changes
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v0, v1), false));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v2, v3), false));
		
		return s;
		
	}
	
	/**
	 * revert changes made by removing 2 edges from a C4
	 * @param s search state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @param v2 vertex
	 * @param v3 vertex
	 */
	protected void c4Delete2Revert(branchingReturnC<V> s, V v0,
			V v1, V v2, V v3) 
	{
		
		//add edges back in
		addEdge(s.getG(), s.getDeg(), v0, v1);
		addEdge(s.getG(), s.getDeg(), v2, v3);
		
		//revert changes
		s.getChanges().removeLast();
		s.getChanges().removeLast();
	
	}
	
	/**
	 * edit a P4 by removing an edge
	 * @param s search state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @return search state
	 */
	protected branchingReturnC<V> p4DeleteResult(branchingReturnC<V> s, V v0, V v1)
	{
		//update degree sequence (first edge)
		removeEdge(s.getG(), s.getDeg(), v0, v1);
		
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v0, v1), false));
		return s;
		
	}
	
	/**
	 * revert changes of removing an edge from a P4
	 * @param s search state
	 * @param v0 vertex
	 * @param v1 vertex
	 */
	protected void p4DeleteRevert(branchingReturnC<V> s, V v0, V v1)
	{
		//update degree sequence (first edge)
		addEdge(s.getG(), s.getDeg(), v0, v1);
		
		s.getChanges().removeLast();
	}
	
	
	/**
	 * Remove edge between v0 and v1 from graph G and update degree order deg
	 * @param G graph
	 * @param deg degree order
	 * @param v0 endpoint of edge to be deleted
	 * @param v1 endpoint of edge to be deleted
	 */
	private void removeEdge(Graph<V, Pair<V>> G, ArrayList<LinkedList<V>> deg, V v0, V v1)
	{
		int v0Deg = G.degree(v0);
		int v1Deg = G.degree(v1);
		deg.get(v0Deg).removeFirstOccurrence(v0);
		deg.get(v0Deg - 1).add(v0);
		if (deg.get(v0Deg).isEmpty() && v0Deg+1 == deg.size())
		{
			deg.remove(v0Deg);
		}
		deg.get(v1Deg).removeFirstOccurrence(v1);
		deg.get(v1Deg - 1).add(v1);
		if (deg.get(v1Deg).isEmpty() && v1Deg+1 == deg.size())
		{
			deg.remove(v1Deg);
		}
		//find the edge to remove
		if (!G.removeEdge(new Pair<V>(v0, v1)))
			G.removeEdge(new Pair<V>(v1, v0));	
	}
	
	/**
	 * Add an edge to graph G between vertices v0 and v1 and update the degree order deg
	 * @param G graph to be modified
	 * @param deg degree order to be updated
	 * @param v0 first endpoint of new edge
	 * @param v1 second endpoint of new edge
	 */
	private void addEdge(Graph<V, Pair<V>> G, ArrayList<LinkedList<V>> deg, V v0, V v1)
	{
		//get current degrees of vertices
		int v0Deg = G.degree(v0);
		int v1Deg = G.degree(v1);
		
		//remove old occurrence of v0 in degree order
		deg.get(v0Deg).remove(v0);
		
		//try to add v0 at new location
		try
		{
			deg.get(v0Deg + 1).add(v0);
		}
		catch (IndexOutOfBoundsException e)
		{
			//make new element for growing degree order
			deg.add(new LinkedList<V>());
			deg.get(v0Deg + 1).add(v0);
		}
		
		
		deg.get(v1Deg).remove(v1);
		
		try
		{
			deg.get(v1Deg + 1).add(v1);
		}
		catch (IndexOutOfBoundsException e)
		{
			deg.add(new LinkedList<V>());
			deg.get(v1Deg + 1).add(v1);
		}
		
		//add edge
		G.addEdge(new Pair<V>(v0, v1), v0, v1);	
	}
	
	/**
	 * get induced subgraph from vertex set
	 * @param G original graph
	 * @param l vertex set
	 * @return induced subgraph
	 */
	protected Graph<V, Pair<V>> graphFromVertexSet(Graph<V, Pair<V>> G, HashSet<V> l)
	{
		Graph<V, Pair<V>> c = new SparseGraph<V, Pair<V>>();
		for (V i : l)
		{
			c.addVertex(i);
			
			//get incident edges
			Collection<Pair<V>> iEdges = c.getIncidentEdges(i);
			//iterate through edges to add to new graph
			for (Pair<V> e : iEdges)
			{
				if (e.getFirst().equals(i) && l.contains(e.getSecond()))
					c.addEdge(e, e.getFirst(), e.getSecond());
				else if (e.getSecond().equals(i) && l.contains(e.getFirst()))
					c.addEdge(e, e.getFirst(), e.getSecond());
			}
			
//			//neighbourhood of i
//			Collection<V> hood = G.getNeighbors(i);
//			for (V n : hood)
//			{
//				if (!(c.containsEdge(new Pair<V>(i, n))))
//					c.addEdge(new Pair<V>(i, n), i, n);
//			}
		}
		return c;
	}
	
	protected Graph<V, Pair<V>> connectedCFromVertexSet(Graph<V, Pair<V>> G, HashSet<V> l)
	{
		Graph<V, Pair<V>> c = new SparseGraph<V, Pair<V>>();
		HashSet<Pair<V>> tempSet = new HashSet<Pair<V>>();
		//throw all edges into hashset, no duplicates
		for (V i : l)
		{
			tempSet.addAll(G.getIncidentEdges(i));
		}
		//add all edges to c
		for (Pair<V> e : tempSet)
		{
			c.addEdge(e, e.getFirst(), e.getSecond());
		}
		return c;
	}
	
	
	/**
	 * Create a graph, the degree order, and changes from connected component graphs
	 * @param rGraph
	 * @param rDeg
	 * @param rChanges
	 * @param tempChanges
	 * @param results
	 */
	protected void graphFromComponentGraphs(Graph<V, Pair<V>> rGraph,
			ArrayList<LinkedList<V>> rDeg, LinkedList<myEdge<V>> rChanges,
			HashSet<myEdge<V>> tempChanges, LinkedList<branchingReturnC<V>> results) {
		//build graph from connected components
		for (branchingReturnC<V> r : results)
		{
			//update total number of changes made
			tempChanges.addAll(r.getChanges());
			
			//add all the edges
			for (V v : r.getG().getVertices())
			{
				rGraph.addVertex(v);
			}
			//add all the vertices
			for (Pair<V> a : r.getG().getEdges())
			{
				rGraph.addEdge(clone.deepClone(a), a.getFirst(), a.getSecond());
			}
			
			//add to degree sequence
			for (int i = 0; i < r.getDeg().size(); i ++)
			{
				try
				{
					rDeg.get(i).addAll(r.getDeg().get(i));
				}
				catch (IndexOutOfBoundsException e)
				{
					rDeg.add(i, new LinkedList<V>());
					rDeg.get(i).addAll(r.getDeg().get(i));
				}
			}	
		}
		rChanges.addAll(tempChanges);
		
	}
	
	/**
	 * fill a linked list with a myEdge LinkedList for the minMoves set
	 * @param G
	 * @return
	 */
	protected LinkedList<myEdge<V>> fillMyEdgeSet(Graph<V, Pair<V>> G, int bound)
	{
		LinkedList<myEdge<V>> l = new LinkedList<myEdge<V>>();
		for (Pair<V> e : G.getEdges())
		{
			//treat each edge in this set as a deletion
			l.add(new myEdge<V>(e, false));
		}
		if (bound > 0)
		{
			while (l.size() > bound)
				l.removeLast();
		}
		return l;
	}
	
	/**
	 * update percent complete and give status of current search
	 * @param s
	 * @param percentDone
	 * @param branching
	 * @return
	 */
	protected double updatePercent(branchingReturnC<V> s, double percentDone, int branching)
	{
		percentDone += Math.pow(((double) 1 / (double) branching), s.getChanges().size());
		//System.out.println("Done: " + percentDone);
		//System.out.println("Size of best solution: " + s.getMinMoves().getChanges().size());
		
		return percentDone;
	}

}
