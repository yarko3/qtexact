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


public class qtBranching 
{
	
	public static Cloner clone = new Cloner();
	public static int min = Integer.MAX_VALUE;
	
	public static Graph<Integer, String> qtEditConnectedComponents(Graph<Integer, String> G)
	{
		//keep proper degree order as an ArrayList<LinkedList<vertex>>
		ArrayList<LinkedList<Integer>> deg = degSequenceOrder(G);
		
		branchingReturnC goal = branchingCC(new branchingReturnC(G, deg));
		System.out.println("Number of moves: " + goal.getChanges());
		return goal.getG();
		
	}
	public static Graph<Integer, String> qtEditNoHeuristic(Graph<Integer, String> G)
	{
		//keep proper degree order as an ArrayList<LinkedList<vertex>>
		ArrayList<LinkedList<Integer>> deg = degSequenceOrder(G);
		
		branchingReturnC goal = branchingNoHeuristic(new branchingReturnC(G, deg));
		System.out.println("Number of moves: " + goal.getChanges());
		return goal.getG();
		
	}
	
	private static branchingReturnC branchingNoHeuristic(branchingReturnC s)
	{
		Graph<Integer, String> G = s.getG();
		ArrayList<LinkedList<Integer>> deg = s.getDeg();
		LinkedList<String> changes = s.getChanges();
		
		
		//check if graph is QT
		ArrayList<Integer> t = flattenAndReverseDeg(deg);
		
		ArrayList<Integer> lexResult = genericLBFS.qtLexBFS(G, t).getList();
		//qt graph has been found
		if (lexResult.size() == G.getVertexCount() && lexResult.get(4) != Character.getNumericValue('C') && lexResult.get(4) != Character.getNumericValue('P'))
		{
			branchingReturnC rtn = new branchingReturnC(G, deg, changes);
			return rtn;
		}
		//branch on found P4 or C4
		else
		{	
			return branch(G, deg, lexResult, changes);
		}
	}
	
	
	/**
	 * branch using connected components
	 * 
	 * @param s current search state
	 * @return
	 */
	private static branchingReturnC branchingCC(branchingReturnC s)
	{
		Graph<Integer, String> G = s.getG();
		ArrayList<LinkedList<Integer>> deg = s.getDeg();
		LinkedList<String> changes = s.getChanges();
		
		
		//check if graph is QT
		ArrayList<Integer> t = flattenAndReverseDeg(deg);
		
		lexReturnC lexSearch = genericLBFS.qtLexBFSComponents(G, t);
		ArrayList<Integer> lexResult = lexSearch.getList();
		
		//qt graph has been found
		if (lexSearch.isQT())
		{
			branchingReturnC rtn = new branchingReturnC(G, deg, changes);
			return rtn;
		}
		//branch on found P4 or C4
		else
		{	//search yields only one connected component, branch on one component
			if (lexSearch.isConnected())
			{
				return branch(G, deg, lexResult, changes);
			}
			//multiple connected components exist
			else
			{
				//build graphs from connected components
				boolean gWithForbiddenFound = false;
				Graph<Integer, String> gWtihForbidden = null;
				LinkedList<Graph<Integer, String>> cGraphs = new LinkedList<Graph<Integer, String>>();
				LinkedList<branchingReturnC> results = new LinkedList<branchingReturnC>();
				for (HashSet<Integer> l : lexSearch.getcComponents())
				{
					Graph<Integer, String> c = new SparseGraph<Integer, String>();
					for (Integer i : l)
					{
						if (!gWithForbiddenFound && (i == lexResult.get(0) || i == lexResult.get(1) || i == lexResult.get(2) || i == lexResult.get(3)))
						{
							gWithForbiddenFound = true;
							gWtihForbidden = c;
						}
						
						c.addVertex(i);
						
						//neighbourhood of i
						Collection<Integer> hood = G.getNeighbors(i);
						for (Integer n : hood)
						{
							if (!(c.containsEdge("e:" + i + "-" + n) || c.containsEdge("e:" + n + "-" + i)))
							{
								c.addEdge("e:" + i + "-" + n, i, n);
							}
						}
					}
					if (c != gWtihForbidden)
						cGraphs.add(c);
				}
				
				results.add(branch(gWtihForbidden, degSequenceOrder(gWtihForbidden), lexResult, changes));
				//branch on the rest of the graphs
				for (Graph<Integer, String> g : cGraphs)
				{
					//if component is large enough to care
					if (g.getVertexCount() > 3)
					{
						results.add(branchingCC(new branchingReturnC(g,degSequenceOrder(g), changes)));
					}
					else
						results.add(new branchingReturnC(g, degSequenceOrder(g)));
						
				}
				
				//final results return
				LinkedList<String> rChanges = clone.deepClone(changes);
				Graph<Integer, String> rGraph = new SparseGraph<Integer, String>();
				ArrayList<LinkedList<Integer>> rDeg = new ArrayList<LinkedList<Integer>>();
				
				//build graph from connected components
				for (branchingReturnC r : results)
				{
					//update total number of changes made
					rChanges.addAll(r.getChanges());
					
					//add all the edges
					for (Integer v : r.getG().getVertices())
					{
						rGraph.addVertex(v);
					}
					//add all the vertices
					for (String a : r.getG().getEdges())
					{
						rGraph.addEdge(a, Integer.parseInt(a.substring(2, a.indexOf('-'))), Integer.parseInt(a.substring(a.indexOf('-'), a.length())));
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
							rDeg.add(i, new LinkedList<Integer>());
							rDeg.get(i).addAll(r.getDeg().get(i));
						}
					}	
				}
				return new branchingReturnC(rGraph, rDeg, rChanges);
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
	private static branchingReturnC branch(Graph<Integer, String> G, ArrayList<LinkedList<Integer>> deg, ArrayList<Integer>lexResult, LinkedList<String> changes)
	{
		//C4 has been found
		if (lexResult.get(4) == -1)
		{
			//result of adding 2 edges to break C4
			//branchingReturnC c4Add1 = branchingCC(c4AddResult(G, deg, changes, lexResult, lexResult.get(0), lexResult.get(2)));
			//branchingReturnC c4Add2 = branchingCC(c4AddResult(G, deg, changes, lexResult, lexResult.get(1), lexResult.get(3)));
			
			
			//results of removing 2 edges to break C4
			branchingReturnC c4Remove0 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2)));
			branchingReturnC c4Remove1 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3)));
			branchingReturnC c4Remove2 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3)));
			branchingReturnC c4Remove3 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2)));
			branchingReturnC c4Remove4 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3)));
			branchingReturnC c4Remove5 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3)));
			
			
			//add to PriorityQueue to sort
			PriorityQueue<branchingReturnC> pQueue = new PriorityQueue<branchingReturnC>();
//			pQueue.add(c4Add1);
//			pQueue.add(c4Add2);
			pQueue.add(c4Remove0);
			pQueue.add(c4Remove1);
			pQueue.add(c4Remove2);
			pQueue.add(c4Remove3);
			pQueue.add(c4Remove4);
			pQueue.add(c4Remove5);
			
			branchingReturnC r = pQueue.remove();
			
			return r;
			
		}
		//P4 has been found
		else
		{
			branchingReturnC p4Remove0 = branchingCC(p4DeleteResult(G, deg, changes, lexResult.get(0), lexResult.get(1)));
			branchingReturnC p4Remove1 = branchingCC(p4DeleteResult(G, deg, changes, lexResult.get(1), lexResult.get(2)));
			branchingReturnC p4Remove2 = branchingCC(p4DeleteResult(G, deg, changes, lexResult.get(2), lexResult.get(3)));
			
			//add to PriorityQueue to sort
			PriorityQueue<branchingReturnC> pQueue = new PriorityQueue<branchingReturnC>();
			pQueue.add(p4Remove0);
			pQueue.add(p4Remove1);
			pQueue.add(p4Remove2);
			
			branchingReturnC r = pQueue.remove();
			
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
	private static branchingReturnC c4AddResult(Graph<Integer, String> G, ArrayList<LinkedList<Integer>> deg, LinkedList<String> changes, ArrayList<Integer> lexResult, int v0, int v1)
	{
		//make copy of graph and degree sequence
		Graph<Integer, String> graphCopy = clone.deepClone(G);
		ArrayList<LinkedList<Integer>> degCopy = clone.deepClone(deg);
		LinkedList<String> cCopy = clone.deepClone(changes);
		//update degree sequence (first edge)
		addEdge(graphCopy, degCopy, v0, v1);
		
		//add edge to changes 
		cCopy.add("a:" + v0 + "-" + v1);
		return new branchingReturnC(graphCopy, degCopy, cCopy);
		
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
	private static branchingReturnC c4Delete2Result(Graph<Integer, String> G, ArrayList<LinkedList<Integer>> deg, LinkedList<String> changes, Integer v0, Integer v1, Integer v2, Integer v3)
	{
		//make copy of graph and degree sequence
		Graph<Integer, String> graphCopy = clone.deepClone(G);
		ArrayList<LinkedList<Integer>> degCopy = clone.deepClone(deg);
		LinkedList<String> cCopy = clone.deepClone(changes);
		//update degree sequence (first edge)
		removeEdge(graphCopy, degCopy, v0, v1);
		
		//update degree sequence (second edge)
		removeEdge(graphCopy, degCopy, v2, v3);
		
		cCopy.add("d:" + v0 + "-" + v1);
		cCopy.add("d:" + v2 + "-" + v3);
		return new branchingReturnC(graphCopy, degCopy, cCopy);
		
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
	private static branchingReturnC p4DeleteResult(Graph<Integer, String> G, ArrayList<LinkedList<Integer>> deg, LinkedList<String> changes, Integer v0, Integer v1)
	{
		//make copy of graph and degree sequence
		Graph<Integer, String> graphCopy = clone.deepClone(G);
		ArrayList<LinkedList<Integer>> degCopy = clone.deepClone(deg);
		LinkedList<String> cCopy = clone.deepClone(changes);
		//update degree sequence (first edge)
		removeEdge(graphCopy, degCopy, v0, v1);
		
		cCopy.add("d:" + v0 + "-" + v1);
		return new branchingReturnC(graphCopy, degCopy, cCopy);
		
	}
	
	/**
	 * Remove edge between v0 and v1 from graph G and update degree order deg
	 * @param G graph
	 * @param deg degree order
	 * @param v0 endpoint of edge to be deleted
	 * @param v1 endpoint of edge to be deleted
	 */
	private static void removeEdge(Graph<Integer, String> G, ArrayList<LinkedList<Integer>> deg, Integer v0, Integer v1)
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
		if (!G.removeEdge("e:" + v0 + "-" + v1))
			G.removeEdge("e:" + v1 + "-" + v0);	
	}
	
	/**
	 * Add an edge to graph G between vertices v0 and v1 and update the degree order deg
	 * @param G graph to be modified
	 * @param deg degree order to be updated
	 * @param v0 first endpoint of new edge
	 * @param v1 second endpoint of new edge
	 */
	private static void addEdge(Graph<Integer, String> G, ArrayList<LinkedList<Integer>> deg, Integer v0, Integer v1)
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
			deg.add(new LinkedList<Integer>());
			deg.get(v0Deg + 1).add(v0);
		}
		
		
		deg.get(v1Deg).remove(v1);
		
		try
		{
			deg.get(v1Deg + 1).add(v1);
		}
		catch (IndexOutOfBoundsException e)
		{
			deg.add(new LinkedList<Integer>());
			deg.get(v1Deg + 1).add(v1);
		}
		
		//add edge
		G.addEdge("e:" + v0 + "-" + v1, v0, v1);	
	}
	
	/**
	 * Compute an ArrayList where every index holds a LinkedList of vertices with degrees of index
	 * @param G graph
	 * @return degree set
	 */
	public static ArrayList<LinkedList<Integer>> degSequenceOrder(Graph<Integer, String> G)
	{
		//store vertices of same degree in LinkedList<Integer> at the index of their degree in ArrayList
		ArrayList<LinkedList<Integer>> deg = new ArrayList<LinkedList<Integer>>();
		int max = 0;
		for (int i : G.getVertices())
		{
			if (G.degree(i) > max)
				max = G.degree(i);
		}
		
		for (int i = 0; i <= max; i++)
		{
			deg.add(new LinkedList<Integer>());
		}
		
		//for every vertex, add it to the appropriate LinkedList
		for (Integer i : G.getVertices())
		{
			int iDeg = G.degree(i);
			if (deg.get(iDeg) == null)
			{
				deg.add(iDeg, new LinkedList<Integer>());
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
	private static ArrayList<Integer> flattenAndReverseDeg(ArrayList<LinkedList<Integer>> deg)
	{
		ArrayList<Integer> t = new ArrayList<Integer>(0);
		
		ArrayList<LinkedList<Integer>> degCopy = clone.deepClone(deg);
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
