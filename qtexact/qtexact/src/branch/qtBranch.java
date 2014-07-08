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
import edu.uci.ics.jung.graph.util.Pair;

/**
 * class used to hold many helper functions required for quasi threshold editing
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public abstract class qtBranch<V> extends Branch<V> 
{
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
	 * edit the graph by adding an edge between v0 and v1
	 * @param s search state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @return edited search state
	 */
	public branchingReturnC<V> addResult(branchingReturnC<V> s, V v0, V v1)
	{
		
		//update degree sequence (first edge)
		addEdge(s.getG(), s.getDeg(), v0, v1);
		
		//add edge to changes 
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v0, v1), true));
		
		return s;
	}
	
	
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
	 * delete 2 edges to remove a C4
	 * @param s search state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @param v2 vertex
	 * @param v3 vertex
	 * @return search state
	 */
	protected branchingReturnC<V> delete2Result(branchingReturnC<V> s, V v0, V v1, V v2, V v3)
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
	 * edit a P4 by removing an edge
	 * @param s search state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @return search state
	 */
	public branchingReturnC<V> deleteResult(branchingReturnC<V> s, V v0, V v1)
	{
		//update degree sequence (first edge)
		removeEdge(s.getG(), s.getDeg(), v0, v1);
		
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v0, v1), false));
		return s;
		
	}
	
	
	/**
	 * roll back one edit
	 * 
	 * @param s search state
	 */
	public void revert(branchingReturnC<V> s)
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
	
	/**
	 * roll back two moves
	 * @param s search state
	 */
	protected void revert2(branchingReturnC<V> s)
	{
		revert(s);
		revert(s);
	}
	
	protected void revert3(branchingReturnC<V> s)
	{
		revert2(s);
		revert(s);
	}
	
	
	/**
	 * Remove edge between v0 and v1 from graph G and update degree order deg
	 * @param G graph
	 * @param deg degree order
	 * @param v0 endpoint of edge to be deleted
	 * @param v1 endpoint of edge to be deleted
	 */
	protected void removeEdge(Graph<V, Pair<V>> G, ArrayList<LinkedList<V>> deg, V v0, V v1)
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
			//find the edge to remove
//			if (!G.removeEdge(new Pair<V>(v0, v1)))
//				G.removeEdge(new Pair<V>(v1, v0));	
			
			G.removeEdge(G.findEdge(v0, v1));
		}
		else
		{
			System.out.println("Tried to delete edge between " + v0 + " and " + v1 + ". No such edge");
		}
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
	
	protected Graph<V, Pair<V>> connectedCFromVertexSet(Graph<V, Pair<V>> G, Set<V> l)
	{
		Graph<V, Pair<V>> c = new SparseGraph<V, Pair<V>>();
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
					l.add(new myEdge<V>(e, false));
					count++;
					if (count >= bound)
						break;
				}
		}
//		
//		if (count < bound)
//			while (count < bound)
//			{
//				l.add(l.getLast());
//				count++;
//			}
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
	
	/**
	 * return a HashSet of common neighbours to both vertices given
	 * @param s
	 * @param v0
	 * @param v1
	 * @return
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
	 * @param s
	 * @param searchResult
	 * @return
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
						
						//TODO check where to add on extra node
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
						//this structure has the same branching rules as a fork
						obstruction.setFlag(-6);
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
		//no better structures found
		return searchResult;
		
	}
	
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
