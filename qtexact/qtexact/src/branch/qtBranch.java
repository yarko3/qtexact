package branch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import search.qtLBFS;
import abstractClasses.Branch;
import abstractClasses.Certificate;
import abstractClasses.SearchResult;
import controller.Controller;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * class used to hold many helper functions required for quasi threshold editing
 * @author Yaroslav Senyuta
 *
 * @param <V> vertex
 */
public abstract class qtBranch<V> extends Branch<V> 
{
	
	/**
	 * output flag
	 */
	boolean output;
	
	
	/**
	 * constructor
	 * @param controller controller for qt branching
	 */
	public qtBranch(Controller<V> controller) {
		super(controller);
	}

	
	@Override
	public qtLBFS<V> getSearch() {
		return (qtLBFS<V>) search;
	}
	public void setSearch(qtLBFS<V> search) {
		this.search = search;
	}
	
	
	/**
	 * setup for quasi threshold editing with no heuristic
	 */
	public branchingReturnC<V> setup(Graph<V, Pair<V>> G, int bound) {
		
		//keep proper degree order as an ArrayList<LinkedList<vertex>>
		ArrayList<LinkedList<V>> deg = ((qtLBFS<V>) search).degSequenceOrder(G);
		
		//start with a full minMoves
		branchingReturnC<V> minMoves = new branchingReturnC<V>(G, deg);
		minMoves.setChanges(fillMinMoves(minMoves, bound));
		minMoves.setMinMoves(minMoves);
		branchingReturnC<V> goal = new branchingReturnC<V>(G, deg, minMoves);
		
		//output flags
		if (output)
		{
			goal.setPercent(1);
			controller.setGlobalPercent(0);
		}
		
		return goal;
	}
	
	/**
	 * setup for qusi threshold editing with no heuristic and no bound
	 */
	public branchingReturnC<V> setup(Graph<V, Pair<V>> G) {
		
		//keep proper degree order as an ArrayList<LinkedList<vertex>>
		ArrayList<LinkedList<V>> deg = ((qtLBFS<V>) search).degSequenceOrder(G);
		
		//start with a full minMoves
		branchingReturnC<V> minMoves = new branchingReturnC<V>(G, deg);
		minMoves.setChanges(fillMinMoves(minMoves, 0));
		minMoves.setMinMoves(minMoves);
		branchingReturnC<V> goal = new branchingReturnC<V>(G, deg, minMoves);
		
		//output flags
		if (output)
		{
			goal.setPercent(1);
			controller.setGlobalPercent(0);
		}
		
		return goal;
	}
	
	
	
	
	/**
	 * edit a P4 by removing an edge
	 * @param s search state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @return edit state
	 */
	public branchingReturnC<V> deleteResult(branchingReturnC<V> s, V v0, V v1)
	{
		//use original edge in edit set
		s.getChanges().addLast(new myEdge<V>(s.getG().findEdge(v0, v1), false));
		
		
		//update degree sequence (first edge)
		removeEdge(s.getG(), s.getDeg(), v0, v1);
		
		return s;
	}
	
	/**
	 * edit the graph by adding an edge between v0 and v1
	 * @param s search state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @return edit state
	 */
	public branchingReturnC<V> addResult(branchingReturnC<V> s, V v0, V v1)
	{
		
		//update degree sequence (first edge)
		addEdge(s.getG(), s.getDeg(), v0, v1);
		
		//add edge to changes 
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v0, v1), true));
		
		return s;
	}
	
	/**
	 * given a move list, apply moves to graph
	 * @param s search state
	 * @param list list of myEdge objects that provide an addition or deletion flag
	 * @return edit state
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
	 * add 2 edges to graph
	 * @param s edit state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @param v2 vertex
	 * @param v3 vertex
	 * @return modified edit state
	 */
	public branchingReturnC<V> add2Result(branchingReturnC<V> s, V v0, V v1, V v2, V v3)
	{
		
		//update degree sequence 
		addEdge(s.getG(), s.getDeg(), v0, v1);
		addEdge(s.getG(), s.getDeg(), v2, v3);
		
		
		
		//add edge to changes 
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v0, v1), true));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v2, v3), true));
		
		return s;
	}
	
	/**
	 * add 3 edges to graph
	 * @param s initial edit state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @param v2 vertex
	 * @param v3 vertex
	 * @param v4 vertex
	 * @param v5 vertex
	 * @return modified edit state
	 */
	public branchingReturnC<V> add3Result(branchingReturnC<V> s, V v0, V v1, V v2, V v3, V v4, V v5)
	{
		
		//update degree sequence 
		addEdge(s.getG(), s.getDeg(), v0, v1);
		addEdge(s.getG(), s.getDeg(), v2, v3);
		addEdge(s.getG(), s.getDeg(), v4, v5);
		
		
		
		//add edge to changes 
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v0, v1), true));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v2, v3), true));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v4, v5), true));
		
		return s;
	}
	
	/**
	 * add an edge and remove an edge
	 * @param s initial edit state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @param v2 vertex
	 * @param v3 vertex
	 * @return modified edit state
	 */
	public branchingReturnC<V> addRemoveResult(branchingReturnC<V> s, V v0, V v1, V v2, V v3)
	{
		
		//update degree sequence 
		addEdge(s.getG(), s.getDeg(), v0, v1);
		removeEdge(s.getG(), s.getDeg(), v2, v3);
		
		
		
		//add edge to changes 
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v0, v1), true));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v2, v3), false));
		
		return s;
	}
	
	/**
	 * add an edge and remove 2 edges
	 * @param s initial edit state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @param v2 vertex
	 * @param v3 vertex
	 * @param v4 vertex
	 * @param v5 vertex
	 * @return modified edit state
	 */
	public branchingReturnC<V> addRemove2Result(branchingReturnC<V> s, V v0, V v1, V v2, V v3, V v4, V v5)
	{
		
		//update degree sequence 
		addEdge(s.getG(), s.getDeg(), v0, v1);
		removeEdge(s.getG(), s.getDeg(), v2, v3);
		removeEdge(s.getG(), s.getDeg(), v4, v5);
		
		
		
		//add edge to changes 
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v0, v1), true));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v2, v3), false));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v4, v5), false));
		
		return s;
	}
	
	/**
	 * add an edge and remove 2 edges
	 * @param s initial edit state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @param v2 vertex
	 * @param v3 vertex
	 * @param v4 vertex
	 * @param v5 vertex
	 * @param v6 vertex
	 * @param v7 vertex
	 * @return modified edit state
	 */
	public branchingReturnC<V> addRemove3Result(branchingReturnC<V> s, V v0, V v1, V v2, V v3, V v4, V v5, V v6, V v7)
	{
		
		//update degree sequence 
		addEdge(s.getG(), s.getDeg(), v0, v1);
		removeEdge(s.getG(), s.getDeg(), v2, v3);
		removeEdge(s.getG(), s.getDeg(), v4, v5);
		removeEdge(s.getG(), s.getDeg(), v6, v7);
		
		
		
		//add edge to changes 
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v0, v1), true));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v2, v3), false));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v4, v5), false));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v6, v7), false));
		
		return s;
	}
	
	/**
	 * add 2 edges and remove one edge
	 * @param s initial edit state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @param v2 vertex
	 * @param v3 vertex
	 * @param v4 vertex
	 * @param v5 vertex
	 * @return modified edit state
	 */
	public branchingReturnC<V> add2RemoveResult(branchingReturnC<V> s, V v0, V v1, V v2, V v3, V v4, V v5)
	{
		
		//update degree sequence 
		addEdge(s.getG(), s.getDeg(), v0, v1);
		addEdge(s.getG(), s.getDeg(), v2, v3);
		removeEdge(s.getG(), s.getDeg(), v4, v5);
		
		
		
		//add edge to changes 
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v0, v1), true));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v2, v3), true));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v4, v5), false));
		
		return s;
	}
	
	/**
	 * add 2 edges and remove 2 edges
	 * @param s initial edit state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @param v2 vertex
	 * @param v3 vertex
	 * @param v4 vertex
	 * @param v5 vertex
	 * @param v6 vertex
	 * @param v7 vertex
	 * @return modified edit state
	 */
	public branchingReturnC<V> add2Remove2Result(branchingReturnC<V> s, V v0, V v1, V v2, V v3, V v4, V v5, V v6, V v7)
	{
		
		//update degree sequence 
		addEdge(s.getG(), s.getDeg(), v0, v1);
		addEdge(s.getG(), s.getDeg(), v2, v3);
		removeEdge(s.getG(), s.getDeg(), v4, v5);
		removeEdge(s.getG(), s.getDeg(), v6, v7);
		
		
		
		//add edge to changes 
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v0, v1), true));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v2, v3), true));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v4, v5), false));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v6, v7), false));
		
		return s;
	}
	
	
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
	 * delete 3 edges
	 * @param s initial edit state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @param v2 vertex
	 * @param v3 vertex
	 * @param v4 vertex
	 * @param v5 vertex
	 * @return modified edit state
	 */
	protected branchingReturnC<V> delete3Result(branchingReturnC<V> s, V v0, V v1, V v2, V v3, V v4, V v5)
	{	
		//update degree sequence (first edge)
		removeEdge(s.getG(), s.getDeg(), v0, v1);
		
		//update degree sequence (second edge)
		removeEdge(s.getG(), s.getDeg(), v2, v3);
		
		//update degree sequence (third edge)
		removeEdge(s.getG(), s.getDeg(), v4, v5);
		
		//add edge deletions to changes
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v0, v1), false));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v2, v3), false));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v4, v5), false));
		
		return s;
		
	}
	
	/**
	 * delete 4 edges
	 * @param s initial edit state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @param v2 vertex
	 * @param v3 vertex
	 * @param v4 vertex
	 * @param v5 vertex
	 * @param v6 vertex
	 * @param v7 vertex
	 * @return modified edit state
	 */
	protected branchingReturnC<V> delete4Result(branchingReturnC<V> s, V v0, V v1, V v2, V v3, V v4, V v5, V v6, V v7)
	{	
		//update degree sequence (first edge)
		removeEdge(s.getG(), s.getDeg(), v0, v1);
		
		//update degree sequence (second edge)
		removeEdge(s.getG(), s.getDeg(), v2, v3);
		
		//update degree sequence (third edge)
		removeEdge(s.getG(), s.getDeg(), v4, v5);
		//last edge
		removeEdge(s.getG(), s.getDeg(), v6, v7);
		
		//add edge deletions to changes
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v0, v1), false));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v2, v3), false));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v4, v5), false));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v6, v7), false));
		
		return s;
		
	}
	
	
	/**
	 * remove one vertex and all edges connected to it
	 * @param s edit state
	 * @param v0 vertex to be deleted
	 * @return modified edit state
	 */
	public branchingReturnC<V> removeVertex(branchingReturnC<V> s, V v0)
	{
		//get all edges connecting v0
		Collection<Pair<V>> edges = clone.deepClone(s.getG().getIncidentEdges(v0));
		//delete all incident edges
		for (Pair<V> e : edges)
		{
			removeEdge(s.getG(), s.getDeg(), e.getFirst(), e.getSecond());
		}
		
		//remove vertex from graph and degree sequence
		removeVertex(s.getG(), s.getDeg(), v0);
		
		return s;
	}
	
	/**
	 * roll back one edit
	 * 
	 * @param s edit state
	 */
	public void revert(branchingReturnC<V> s)
	{
		//check to make sure an edit to undo exists
		if (!s.getChanges().isEmpty())
		{
			myEdge<V> edited = s.getChanges().removeLast();
			
			V v0 = edited.getEdge().getFirst();
			V v1 = edited.getEdge().getSecond();
			
			//update degree sequence
			if (edited.isFlag() == false)
				addEdge(s.getG(), s.getDeg(), v0, v1);
			else
				removeEdge(s.getG(), s.getDeg(), v0, v1);
		}
		else
		{
			System.out.println("No moves to revert.");
		}
	}
	
	
	/**
	 * roll back two moves
	 * @param s edit state
	 */
	protected void revert2(branchingReturnC<V> s)
	{
		revert(s);
		revert(s);
	}
	
	/**
	 * roll back 3 moves
	 * @param s edit state
	 */
	protected void revert3(branchingReturnC<V> s)
	{
		revert2(s);
		revert(s);
	}
	
	/**
	 * roll back 4 moves
	 * @param s edit state
	 */
	protected void revert4(branchingReturnC<V> s)
	{
		revert2(s);
		revert2(s);
	}
	
	/**
	 * remove vertex and update degree array
	 * @param G graph
	 * @param deg degree array
	 * @param v0 vertex to be deleted
	 */
	public void removeVertex(Graph<V, Pair<V>> G, ArrayList<LinkedList<V>> deg, V v0)
	{
		if (G.containsVertex(v0))
		{
			int v0Deg = G.degree(v0);
			deg.get(v0Deg).removeFirstOccurrence(v0);
			
			if (deg.get(v0Deg).isEmpty() && v0Deg+1 == deg.size())
			{
				deg.remove(v0Deg);
			}
			
			G.removeVertex(v0);
		}
		else
		{
			System.out.println("Tried to delete vertex "  + v0 + " but does not exist.");
		}
	}
	
	
	
	/**
	 * Remove edge between v0 and v1 from graph G and update degree order deg
	 * @param G graph
	 * @param deg degree order
	 * @param v0 endpoint of edge to be deleted
	 * @param v1 endpoint of edge to be deleted
	 */
	public void removeEdge(Graph<V, Pair<V>> G, ArrayList<LinkedList<V>> deg, V v0, V v1)
	{
		
		if (G.isNeighbor(v0, v1))
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
			
			G.removeEdge(G.findEdge(v0, v1));
		}
		else
		{
			System.out.println("Tried to delete edge between " + v0 + " and " + v1 + ". No such edge");
			throw new NullPointerException();
		}
	}
	
	/**
	 * add a vertex to graph and degree sequence
	 * @param G graph
	 * @param deg degree sequence
	 * @param v0 vertex
	 */
	public void addVertex(Graph<V, Pair<V>> G, ArrayList<LinkedList<V>> deg, V v0)
	{
		if (G.containsVertex(v0))
		{
			System.out.println("Tried to add vertex " + v0 + ", already exists");
			throw new NullPointerException();
		}
		else
		{
			//add vertex to graph
			G.addVertex(v0);
			
			//add vertex to deg
			
			//if index does not exist in deg
			if (deg.size() < 1)
				deg.add(0, new LinkedList<V>());
			
			deg.get(0).add(v0);
		}
	}
	
	
	/**
	 * Add an edge to graph G between vertices v0 and v1 and update the degree order deg
	 * @param G graph to be modified
	 * @param deg degree order to be updated
	 * @param v0 first endpoint of new edge
	 * @param v1 second endpoint of new edge
	 */
	public void addEdge(Graph<V, Pair<V>> G, ArrayList<LinkedList<V>> deg, V v0, V v1)
	{
		//if the edge already doesn't exist
		if (!G.isNeighbor(v0, v1))
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
		else
		{
			System.out.println("Tried to add edge between " + v0 + " and " + v1 + ". Edge already exists.");
			throw new NullPointerException();
		}
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
	
	/**
	 * connected component from vertex set
	 * @param G graph
	 * @param l vertex set
	 * @return subgraph constructed from vertex set
	 */
	protected Graph<V, Pair<V>> connectedCFromVertexSet(Graph<V, Pair<V>> G, Set<V> l)
	{
		Graph<V, Pair<V>> c = new UndirectedSparseGraph<V, Pair<V>>();
		HashSet<Pair<V>> tempSet = new HashSet<Pair<V>>();
		//throw all edges into hashset, no duplicates
		for (V i : l)
		{
			tempSet.addAll(G.getIncidentEdges(i));
			c.addVertex(i);
		}
		//add all edges to c
		for (Pair<V> e : tempSet)
		{
			Pair<V> edge = clone.deepClone(e);
			c.addEdge(edge, edge.getFirst(), edge.getSecond());
		}
		return c;
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
		
//		
		
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
	
	/**
	 * update percent complete and give status of current search
	 * @param s edit state
	 * @param percentDone
	 * @param branching
	 * @return updated percent
	 */
	protected double updatePercent(branchingReturnC<V> s, double percentDone, int branching)
	{
		percentDone += Math.pow(((double) 1 / (double) branching), s.getChanges().size());
		//System.out.println("Done: " + percentDone);
		//System.out.println("Size of best solution: " + s.getMinMoves().getChanges().size());
		
		return percentDone;
	}
	
	/**
	 * return a HashSet of common neighbours to both vertices given
	 * @param s edit state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @return hashset of common neighbours
	 */
	public HashSet<V> commonNeighbours(branchingReturnC<V> s, V v0, V v1)
	{
		HashSet<V> all = new HashSet<V>();
		HashSet<V> common = new HashSet<V>();
		
		all.addAll(s.getG().getNeighbors(v0));
		
		for (V n : s.getG().getNeighbors(v1))
		{
			if (!all.add(n))
				common.add(n);
		}
		
		return common;
	}
	
	/**
	 * find common neighbours to three vertices
	 * @param s edit state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @param v2 vertex
	 * @return set of common neighbours
	 */
	public HashSet<V> commonNeighbours(branchingReturnC<V> s, V v0, V v1, V v2)
	{
		HashSet<V> all = commonNeighbours(s, v0, v1);
		HashSet<V> common = new HashSet<V>();
		
		
		for (V n : s.getG().getNeighbors(v2))
		{
			if (!all.add(n))
				common.add(n);
		}
		
		return common;
		
	}
	
	/**
	 * find forbidden structures
	 * 
	 * Flag index:
	 * -1 C4
	 * -2 P4
	 * -3 4 pan
	 * -4 house
	 * -5 P5
	 * -6 fork
	 * -7 3 pan (extra node on handle)
	 * -8 C5
	 * -9 kite
	 * 
	 * @param s edit state
	 * @param searchResult initial search results
	 * @return updated search results
	 */
	public SearchResult<V> findStructures(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		Certificate<V> obstruction = searchResult.getCertificate();
		ArrayList<V> vertices = obstruction.getVertices();
		
		HashMap<V, Integer> hash = new HashMap<V, Integer>();
		
		//add one for every common neighbour into hash
		for (V v : vertices)
		{
			for (V n : s.getG().getNeighbors(v))
			{
				int entry = 1;
				if (hash.containsKey(n))
					entry = hash.get(n) + 1;
				hash.put(n, entry);
			}
		}
		
		//remove vertices of structure from hash
		for (V v : vertices)
			hash.remove(v);
		
		
//		//order vertices in decreasing degree
//		HashMap<Integer, LinkedList<V>> degHash = new HashMap<Integer, LinkedList<V>>();
//		
//		for (V n : hash.keySet())
//		{
//			int degree = hash.get(n);
//			
//			if (!degHash.containsKey(degree))
//    		{
//    			degHash.put(degree, new LinkedList<V>());
//    		}
//			
//			degHash.get(degree).add(n);
//		}
//		
//		PriorityQueue<Integer> pQueue = new PriorityQueue<Integer>(Collections.reverseOrder());
//		pQueue.addAll(degHash.keySet());
//		
//		
//		
//		//get the most connected vertices first
//		while (!pQueue.isEmpty())
//		{
//			int degree = pQueue.remove();
//			
//			if (degree == 4)
//				continue;
//			
//			LinkedList<V> nextList = degHash.get(degree);
//			
//			for (V n : nextList)
			for (V n : hash.keySet())
			{
				//number of neighbours of n in obstruction
				int nVal = hash.get(n);
				
				//if C4 was found
				if (obstruction.getFlag() == -1)
				{	
					if (nVal == 1)
					{
						//a 4 pan has been found
						searchResult.setCertificate(construct4Pan(s, searchResult, n));
						return searchResult;
					}
					
					
					else if (nVal == 2)
					{
						//look for a house
						for (int i = 0; i < vertices.size(); i++)
						{
							V v = vertices.get(i);
							if (s.getG().isNeighbor(v, n))
							{
								V next = vertices.get((i + 1) % 4);
								//check next vertex for adjacency
								if (s.getG().isNeighbor(n, next))
								{
									//a house has been found, rotate it into shape
									vertices.add(0, n);
									while (vertices.get(1) != next)
									{
										vertices.add(1, obstruction.getVertices().remove(4));
									}
									
									obstruction.setFlag(-4);
									return searchResult;
									
								}
							}
						}
						
						//a house has not been found, so a double C4 is present
						V nonNeighbour = null;
						for (V v : vertices)
						{
							if (!s.getG().getNeighbors(n).contains(v))
							{
								nonNeighbour = v;
								break;
							}
						}
						//rotate into shape
						while (!vertices.get(0).equals(nonNeighbour))
							vertices.add(0, vertices.remove(3));
						
						//add 5th vertex
						vertices.add(n);
						obstruction.setFlag(-10);
						return searchResult;
						
					}
					else if (nVal == 3)
					{
						//find diamond C4
						V nonNeighbour = null;
						for (V v : vertices)
						{
							if (!s.getG().getNeighbors(n).contains(v))
							{
								nonNeighbour = v;
								break;
							}
						}
						//rotate into shape
						while (!vertices.get(0).equals(nonNeighbour))
							vertices.add(0, vertices.remove(3));
						
						//add 5th vertex
						vertices.add(n);
						obstruction.setFlag(-11);
						return searchResult;
					}
				}
				
				//if P4 was found
				if (obstruction.getFlag() == -2)
				{
					if (nVal == 1)
					{
						//either a P5 or a fork has been found
						if (s.getG().isNeighbor(vertices.get(0), n) || s.getG().isNeighbor(vertices.get(3), n))
						{
							//a P5 has been found
							if (s.getG().isNeighbor(vertices.get(0), n))
								vertices.add(0, n);
							else
								vertices.add(n);
							obstruction.setFlag(-5);
							
							return searchResult;
						}
						else
						{
							//a fork has been found
							
							if (s.getG().isNeighbor(vertices.get(2), n))
								vertices.add(n);
							else
							{
								//reverse order of P4
								Collections.reverse(vertices);
								vertices.add(n);
							}
							obstruction.setFlag(-6);
							
							return searchResult;
						}
					}
					else if (nVal == 2)
					{
						//look for 4 pan
						if ((s.getG().isNeighbor(vertices.get(0), n) && s.getG().isNeighbor(vertices.get(2), n)) ||
								(s.getG().isNeighbor(vertices.get(1), n) && s.getG().isNeighbor(vertices.get(3), n)))
						{
							searchResult.setCertificate(construct4Pan(s, searchResult, n));
							return searchResult;
						}
						
						//look for a C5
						if ((s.getG().isNeighbor(vertices.get(0), n) && s.getG().isNeighbor(vertices.get(3), n)))
						{
							vertices.add(n);
							obstruction.setFlag(-8);
							return searchResult;
						}
						
						//look for a 3 pan with extra node on handle
						if ((s.getG().isNeighbor(vertices.get(0), n) && s.getG().isNeighbor(vertices.get(1), n)) ||
								(s.getG().isNeighbor(vertices.get(2), n) && s.getG().isNeighbor(vertices.get(3), n)))
						{
							if (s.getG().isNeighbor(vertices.get(0), n))
							{
								Collections.reverse(vertices);
							}
							vertices.add(n);
							obstruction.setFlag(-7);
							return searchResult;
						}
					}
					else if (nVal == 3)
					{
						//look for a kite
						if (!s.getG().isNeighbor(vertices.get(0), n) || !s.getG().isNeighbor(vertices.get(3), n))
						{
							if (!s.getG().isNeighbor(vertices.get(0), n))
							{
								vertices.add(n);
								
								obstruction.setFlag(-9);
								return searchResult;
							}
							else
							{
								Collections.reverse(vertices);
								vertices.add(n);
								obstruction.setFlag(-9);
								return searchResult;
								
							}
						}
						//a house is found
						else
						{
							if (s.getG().isNeighbor(vertices.get(1), n))
							{
								vertices.add(n);
								obstruction.setFlag(-4);
								return searchResult;
							}
							else
							{
								Collections.reverse(vertices);
								vertices.add(n);
								obstruction.setFlag(-4);
								return searchResult;
							}
						}
						
					}
				}
			}
			
//		}
		//no better structures found
		return searchResult;
		
	}
	
	/**
	 * construct a 4-pan 
	 * @param s edit state
	 * @param searchResult initial search result
	 * @param n node for handle of 4-pan
	 * @return certificate
	 */
	private Certificate<V> construct4Pan(branchingReturnC<V> s, SearchResult<V> searchResult, V n)
	{
		Certificate<V> obstruction = searchResult.getCertificate();
		ArrayList<V> vertices = obstruction.getVertices();
		
		//a 4 pan found from a C4
		if (obstruction.getFlag() == -1)
		{
			V common = null;
			int cIndex;
			
			//find the vertex adjacent in structure
			for (cIndex = 0; cIndex < vertices.size(); cIndex++)
			{
				V v = vertices.get(cIndex);
				if (s.getG().isNeighbor(n, v))
				{
					common = v;
					break;
				}
			}
			
			//update obstruction
			obstruction.getVertices().add(0, n);
			
			//rotate C4 into proper shape
			while (obstruction.getVertices().get(1) != common)
			{
				obstruction.getVertices().add(1, obstruction.getVertices().remove(4));
			}
			
			obstruction.setFlag(-3);
			
			return obstruction;
		}
		//a pan is found from a P4
		else
		{
			//if n is closer to the front of the obstruction
			if (s.getG().isNeighbor(vertices.get(0), n))
			{
				//reverse obstruction order
				Collections.reverse(obstruction.getVertices());
				
				obstruction.getVertices().add(4, n);
				obstruction.setFlag(-3);
				return obstruction;
			}
			else
			{
				obstruction.getVertices().add(4, n);
				obstruction.setFlag(-3);
				return obstruction;
			}
		}
			
	}

}
