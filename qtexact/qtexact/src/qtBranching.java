/**
 * Yaroslav Senyuta
 * NSERC USRA Grant (2014)
 */


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import qtUtils.genericLBFS;
import qtUtils.lexReturnC;
import qtUtils.branchingReturnC;

import com.rits.cloning.Cloner;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.Pair;


public class qtBranching<V>
{
	public genericLBFS<V> search = new genericLBFS<V>();
	
	public static Cloner clone = new Cloner();
	public static int min = Integer.MAX_VALUE;
	
	public Graph<V, Pair<V>> qtEditConnectedComponents(Graph<V, Pair<V>> G)
	{
		//keep proper degree order as an ArrayList<LinkedList<vertex>>
		ArrayList<LinkedList<V>> deg = degSequenceOrder(G);
		
		branchingReturnC<V> goal = branchingCC(new branchingReturnC<V>(G, deg));
		System.out.println("Number of moves: " + goal.getChanges());
		return goal.getG();
		
	}
	public Graph<V, Pair<V>> qtEditNoHeuristic(Graph<V, Pair<V>> G)
	{
		//keep proper degree order as an ArrayList<LinkedList<vertex>>
		ArrayList<LinkedList<V>> deg = degSequenceOrder(G);
		
		branchingReturnC<V> goal = branchingNoHeuristic(new branchingReturnC<V>(G, deg));
		System.out.println("Number of moves: " + goal.getChanges());
		return goal.getG();
		
	}
	
	private branchingReturnC<V> branchingNoHeuristic(branchingReturnC<V> s)
	{
		Graph<V, Pair<V>> G = s.getG();
		ArrayList<LinkedList<V>> deg = s.getDeg();
		LinkedList<Pair<V>> changes = s.getChanges();
		
		
		//check if graph is QT
		ArrayList<V> t = flattenAndReverseDeg(deg);
		lexReturnC<V> lexSearch = search.qtLexBFS(G, t);
		ArrayList<V> forbidden = lexSearch.getList();
		//qt graph has been found
		try
		{
			if (forbidden.size() == G.getVertexCount() && lexSearch.isQT())
			{
				branchingReturnC<V> rtn = new branchingReturnC<V>(G, deg, changes);
				return rtn;
			}
			//branch on found P4 or C4
			else
			{	
				return branch(G, deg, lexSearch, changes);
			}
		}
		catch (NullPointerException e)
		{
			return branch(G, deg, lexSearch, changes);
		}
	}
	
	
	/**
	 * branch using connected components
	 * 
	 * @param s current search state
	 * @return
	 */
	private branchingReturnC<V> branchingCC(branchingReturnC<V> s)
	{
		Graph<V, Pair<V>> G = s.getG();
		ArrayList<LinkedList<V>> deg = s.getDeg();
		LinkedList<Pair<V>> changes = s.getChanges();
		
		
		//check if graph is QT
		ArrayList<V> t = flattenAndReverseDeg(deg);
		
		lexReturnC<V> lexSearch = search.qtLexBFSComponents(G, t);
		ArrayList<V> forbidden;
		
		//qt graph has been found
		if (lexSearch.isQT())
		{
			branchingReturnC<V> rtn = new branchingReturnC<V>(G, deg, changes);
			return rtn;
		}
		//branch on found P4 or C4
		else
		{	
			forbidden = lexSearch.getCertificate().getVertices();
			//search yields only one connected component, branch on one component
			if (lexSearch.isConnected())
			{
				return branch(G, deg,lexSearch, changes);
			}
			//multiple connected components exist
			else
			{
				//build graphs from connected components
				boolean gWithForbiddenFound = false;
				Graph<V, Pair<V>> gWtihForbidden = null;
				LinkedList<Graph<V, Pair<V>>> cGraphs = new LinkedList<Graph<V, Pair<V>>>();
				LinkedList<branchingReturnC<V>> results = new LinkedList<branchingReturnC<V>>();
				for (HashSet<V> l : lexSearch.getcComponents())
				{
					Graph<V, Pair<V>> c = new SparseGraph<V, Pair<V>>();
					for (V i : l)
					{
						if (!gWithForbiddenFound && (i.equals(forbidden.get(0)) || i.equals(forbidden.get(1)) || i.equals(forbidden.get(2)) || i.equals(forbidden.get(3))))
						{
							gWithForbiddenFound = true;
							gWtihForbidden = c;
						}
						
						c.addVertex(i);
						
						//neighbourhood of i
						Collection<V> hood = G.getNeighbors(i);
						for (V n : hood)
						{
							
							if (!(c.containsEdge(new Pair<V>(i, n))))
								c.addEdge(new Pair<V>(i, n), i, n);
						}
					}
					if (c != gWtihForbidden)
						cGraphs.add(c);
				}
				
				results.add(branch(gWtihForbidden, degSequenceOrder(gWtihForbidden), lexSearch, new LinkedList<Pair<V>>()));
				//branch on the rest of the graphs
				for (Graph<V, Pair<V>> g : cGraphs)
				{
					//if component is large enough to care
					if (g.getVertexCount() > 3)
					{
						results.add(branchingCC(new branchingReturnC<V>(g,degSequenceOrder(g), new LinkedList<Pair<V>>())));
					}
					else
						results.add(new branchingReturnC<V>(g, degSequenceOrder(g)));
						
				}
				
				//final results return
				LinkedList<Pair<V>> rChanges = clone.deepClone(changes);
				Graph<V, Pair<V>> rGraph = new SparseGraph<V, Pair<V>>();
				ArrayList<LinkedList<V>> rDeg = new ArrayList<LinkedList<V>>();
				
				//build graph from connected components
				for (branchingReturnC<V> r : results)
				{
					//update total number of changes made
					rChanges.addAll(r.getChanges());
					
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
				return new branchingReturnC<V>(rGraph, rDeg, rChanges);
			}
			
		}
	}
	
	/**
	 * branch on all options available
	 * @param G graph to be modified
	 * @param deg degree order
	 * @param lexResult result of lexBFS search
	 * @param changes changes made 
	 * @return result of most efficient branching
	 */
	private branchingReturnC<V> branch(Graph<V, Pair<V>> G, ArrayList<LinkedList<V>> deg, lexReturnC<V> searchResult, LinkedList<Pair<V>> changes)
	{
		//C4 has been found
		ArrayList<V> lexResult = searchResult.getCertificate().getVertices();
		if (searchResult.getCertificate().getFlag() == -1)
		{
			//result of adding 2 edges to break C4
			//branchingReturnC c4Add1 = branchingCC(c4AddResult(G, deg, changes, lexResult, lexResult.get(0), lexResult.get(2)));
			//branchingReturnC c4Add2 = branchingCC(c4AddResult(G, deg, changes, lexResult, lexResult.get(1), lexResult.get(3)));
			
			
			//results of removing 2 edges to break C4
			branchingReturnC<V> c4Remove0 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2)));
			branchingReturnC<V> c4Remove1 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3)));
			branchingReturnC<V> c4Remove2 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3)));
			branchingReturnC<V> c4Remove3 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2)));
			branchingReturnC<V> c4Remove4 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3)));
			branchingReturnC<V> c4Remove5 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3)));
			
			
			//add to PriorityQueue to sort
			PriorityQueue<branchingReturnC<V>> pQueue = new PriorityQueue<branchingReturnC<V>>();
//			pQueue.add(c4Add1);
//			pQueue.add(c4Add2);
			pQueue.add(c4Remove0);
			pQueue.add(c4Remove1);
			pQueue.add(c4Remove2);
			pQueue.add(c4Remove3);
			pQueue.add(c4Remove4);
			pQueue.add(c4Remove5);
			
			branchingReturnC<V> r = pQueue.remove();
			
			return r;
			
		}
		//P4 has been found
		else
		{
			branchingReturnC<V> p4Remove0 = branchingCC(p4DeleteResult(G, deg, changes, lexResult.get(0), lexResult.get(1)));
			branchingReturnC<V> p4Remove1 = branchingCC(p4DeleteResult(G, deg, changes, lexResult.get(1), lexResult.get(2)));
			branchingReturnC<V> p4Remove2 = branchingCC(p4DeleteResult(G, deg, changes, lexResult.get(2), lexResult.get(3)));
			
			//add to PriorityQueue to sort
			PriorityQueue<branchingReturnC<V>> pQueue = new PriorityQueue<branchingReturnC<V>>();
			pQueue.add(p4Remove0);
			pQueue.add(p4Remove1);
			pQueue.add(p4Remove2);
			
			branchingReturnC<V> r = pQueue.remove();
			
			return r;
			
		}
	}
	
	
	/**
	 * result of adding an edge to break C4
	 * @param G graph
	 * @param deg degree
	 * @param changes changes made to the original graph
	 * @param lexResult result of the lexBFS search for quasi-thresholdness
	 * @param v0 vertex of edge to be added
	 * @param v1 vertex of edge to be added
	 * @return an object storing the new graph, updated degree order, changes
	 */
	private branchingReturnC<V> c4AddResult(Graph<V, Pair<V>> G, ArrayList<LinkedList<V>> deg, LinkedList<Pair<V>> changes, ArrayList<V> lexResult, V v0, V v1)
	{
		//make copy of graph and degree sequence
		Graph<V, Pair<V>> graphCopy = clone.deepClone(G);
		ArrayList<LinkedList<V>> degCopy = clone.deepClone(deg);
		LinkedList<Pair<V>> cCopy = clone.deepClone(changes);
		//update degree sequence (first edge)
		addEdge(graphCopy, degCopy, v0, v1);
		
		//add edge to changes 
		cCopy.add(new Pair<V>(v0, v1));
		return new branchingReturnC<V>(graphCopy, degCopy, cCopy);
		
	}
	/**
	 * Return the result of deleting two edges from an identified C4
	 * @param G graph
	 * @param deg degree
	 * @param changes changes made to the original graph
	 * @param v0 endpoint of first edge to be deleted
	 * @param v1 endpoint of first edge to be deleted
	 * @param v2 endpoint of second edge to be deleted
	 * @param v3 endpoint of second edge to be deleted
	 * @return graph, degree order and changes after deletion
	 */
	private branchingReturnC<V> c4Delete2Result(Graph<V, Pair<V>> G, ArrayList<LinkedList<V>> deg, LinkedList<Pair<V>> changes, V v0, V v1, V v2, V v3)
	{
		//make copy of graph and degree sequence
		Graph<V, Pair<V>> graphCopy = clone.deepClone(G);
		ArrayList<LinkedList<V>> degCopy = clone.deepClone(deg);
		LinkedList<Pair<V>> cCopy = clone.deepClone(changes);
		//update degree sequence (first edge)
		removeEdge(graphCopy, degCopy, v0, v1);
		
		//update degree sequence (second edge)
		removeEdge(graphCopy, degCopy, v2, v3);
		
		cCopy.add(new Pair<V>(v0, v1));
		cCopy.add(new Pair<V>(v2, v3));
		return new branchingReturnC<V>(graphCopy, degCopy, cCopy);
		
	}
	
	/**
	 * Delete an edge from a P4 and return a branchingReturnC with new graph and degree order
	 * @param G graph
	 * @param deg degree order
	 * @param changes number of changes made to the initial graph
	 * @param v0 endpoint of edge to be deleted
	 * @param v1 endpoint of edge to be deleted
	 * @return
	 */
	private branchingReturnC<V> p4DeleteResult(Graph<V, Pair<V>> G, ArrayList<LinkedList<V>> deg, LinkedList<Pair<V>> changes, V v0, V v1)
	{
		//make copy of graph and degree sequence
		Graph<V, Pair<V>> graphCopy = clone.deepClone(G);
		ArrayList<LinkedList<V>> degCopy = clone.deepClone(deg);
		LinkedList<Pair<V>> cCopy = clone.deepClone(changes);
		//update degree sequence (first edge)
		removeEdge(graphCopy, degCopy, v0, v1);
		
		cCopy.add(new Pair<V>(v0, v1));
		return new branchingReturnC<V>(graphCopy, degCopy, cCopy);
		
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
		
		for (int i = 0; i <= max; i++)
		{
			deg.add(new LinkedList<V>());
		}
		
		//for every vertex, add it to the appropriate LinkedList
		for (V i : G.getVertices())
		{
			int iDeg = G.degree(i);
			if (deg.get(iDeg) == null)
			{
				deg.add(iDeg, new LinkedList<V>());
			}
			
			deg.get(iDeg).add(i);
		}
		
		deg.trimToSize();
		return deg;
	}
	
	/**
	 * Flatten and reverse degree order
	 * @param deg degree sequence
	 * @return vertex set in non-increasing degree order
	 */
	private ArrayList<V> flattenAndReverseDeg(ArrayList<LinkedList<V>> deg)
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
