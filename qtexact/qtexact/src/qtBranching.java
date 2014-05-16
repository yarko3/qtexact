import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import qtUtils.genericLBFS;
import qtUtils.lexReturn;

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
		
		branchingReturnType goal = branchingCC(new branchingReturnType(G, deg, 0));
		System.out.println("Number of moves: " + goal.changes);
		return goal.G;
		
	}
	public static Graph<Integer, String> qtEditNoHeuristic(Graph<Integer, String> G)
	{
		//keep proper degree order as an ArrayList<LinkedList<vertex>>
		ArrayList<LinkedList<Integer>> deg = degSequenceOrder(G);
		
		branchingReturnType goal = branchingNoHeuristic(new branchingReturnType(G, deg, 0));
		System.out.println("Number of moves: " + goal.changes);
		return goal.G;
		
	}
	
	private static branchingReturnType branchingNoHeuristic(branchingReturnType s)
	{
		Graph<Integer, String> G = s.G;
		ArrayList<LinkedList<Integer>> deg = s.deg;
		int changes = s.changes;
		
		
		//check if graph is QT
		ArrayList<Integer> t = new ArrayList<Integer>(0);
		
		ArrayList<LinkedList<Integer>> degCopy = clone.deepClone(deg);
		
		for (int i = degCopy.size() - 1; i >=0; i--)
		{
			while (!degCopy.get(i).isEmpty())
			{
				t.add(degCopy.get(i).remove());
			}
		}
		ArrayList<Integer> lexResult = genericLBFS.qtLexBFS(G, t);
		//qt graph has been found
		if (lexResult.size() == G.getVertexCount() && lexResult.get(4) != Character.getNumericValue('C') && lexResult.get(4) != Character.getNumericValue('P'))
		{
			branchingReturnType rtn = new branchingReturnType(G, deg, changes);
			return rtn;
		}
		//branch on found P4 or C4
		else
		{	
			//C4 has been found
			if (lexResult.get(4) == Character.getNumericValue('C'))
			{
				//result of adding 2 edges to break C4
				//branchingReturnType c4Add1 = branching(c4AddResult(G, deg, changes, lexResult, lexResult.get(0), lexResult.get(2)));
				//branchingReturnType c4Add2 = branching(c4AddResult(G, deg, changes, lexResult, lexResult.get(1), lexResult.get(3)));
				
				
				//results of removing 2 edges to break C4
				branchingReturnType c4Remove0 = branchingNoHeuristic(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2)));
				branchingReturnType c4Remove1 = branchingNoHeuristic(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3)));
				branchingReturnType c4Remove2 = branchingNoHeuristic(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3)));
				branchingReturnType c4Remove3 = branchingNoHeuristic(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2)));
				branchingReturnType c4Remove4 = branchingNoHeuristic(c4Delete2Result(G, deg, changes, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3)));
				branchingReturnType c4Remove5 = branchingNoHeuristic(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3)));
				
				
				//add to PriorityQueue to sort
				PriorityQueue<branchingReturnType> pQueue = new PriorityQueue<branchingReturnType>();
//				pQueue.add(c4Add1);
//				pQueue.add(c4Add2);
				pQueue.add(c4Remove0);
				pQueue.add(c4Remove1);
				pQueue.add(c4Remove2);
				pQueue.add(c4Remove3);
				pQueue.add(c4Remove4);
				pQueue.add(c4Remove5);
				
				branchingReturnType r = pQueue.remove();
				
				return r;
				
			}
			//P4 has been found
			else
			{
				branchingReturnType p4Remove0 = branchingNoHeuristic(p4DeleteResult(G, deg, changes, lexResult.get(0), lexResult.get(1)));
				branchingReturnType p4Remove1 = branchingNoHeuristic(p4DeleteResult(G, deg, changes, lexResult.get(1), lexResult.get(2)));
				branchingReturnType p4Remove2 = branchingNoHeuristic(p4DeleteResult(G, deg, changes, lexResult.get(2), lexResult.get(3)));
				
				//add to PriorityQueue to sort
				PriorityQueue<branchingReturnType> pQueue = new PriorityQueue<branchingReturnType>();
				pQueue.add(p4Remove0);
				pQueue.add(p4Remove1);
				pQueue.add(p4Remove2);
				
				branchingReturnType r = pQueue.remove();
				
				return r;
				
			}
				
			
		}
	}
	
	
	/**
	 * branch using connected components
	 * 
	 * @param s current search state
	 * @return
	 */
	private static branchingReturnType branchingCC(branchingReturnType s)
	{
		Graph<Integer, String> G = s.G;
		ArrayList<LinkedList<Integer>> deg = s.deg;
		int changes = s.changes;
		
		
		//check if graph is QT
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
		lexReturn lexSearch = genericLBFS.qtLexBFSComponents(G, t);
		ArrayList<Integer> lexResult = lexSearch.getList();
		
		//qt graph has been found
		if (lexSearch.isQT())
		{
			branchingReturnType rtn = new branchingReturnType(G, deg, changes);
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
				LinkedList<branchingReturnType> results = new LinkedList<branchingReturnType>();
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
							if (!(c.containsEdge("e:" + i + ":" + n) || c.containsEdge("e:" + n + ":" + i)))
							{
								c.addEdge("e:" + i + "-" + n, i, n);
							}
					
						}
					}
					if (c != gWtihForbidden)
						cGraphs.add(c);
				}
				
				results.add(branch(gWtihForbidden, degSequenceOrder(gWtihForbidden), lexResult, 0));
				//branch on the rest of the graphs
				for (Graph<Integer, String> g : cGraphs)
				{
					//if component is large enough to care
					if (g.getVertexCount() > 3)
					{
						results.add(branchingCC(new branchingReturnType(g,degSequenceOrder(g), 0)));
					}
					else
						results.add(new branchingReturnType(g, degSequenceOrder(g), 0));
						
				}
				
				//final results return
				int rChanges = changes;
				Graph<Integer, String> rGraph = new SparseGraph<Integer, String>();
				ArrayList<LinkedList<Integer>> rDeg = new ArrayList<LinkedList<Integer>>();
				for (branchingReturnType r : results)
				{
					rChanges += r.changes;
					
					//add all the edges
					for (Integer v : r.G.getVertices())
					{
						rGraph.addVertex(v);
					}
					//add all the vertices
					for (String a : r.G.getEdges())
					{
						rGraph.addEdge(a, r.G.getEndpoints(a).getFirst(), r.G.getEndpoints(a).getSecond());
					}
					
					//add to degree sequence
					for (int i = 0; i < r.deg.size(); i ++)
					{
						try
						{
							rDeg.get(i).addAll(r.deg.get(i));
						}
						catch (IndexOutOfBoundsException e)
						{
							rDeg.add(i, new LinkedList<Integer>());
							rDeg.get(i).addAll(r.deg.get(i));
						}
					}
					
				}
				
				return new branchingReturnType(rGraph, rDeg, rChanges);
			}
			
		}
	}
	
	private static branchingReturnType branch(Graph<Integer, String> G, ArrayList<LinkedList<Integer>> deg, ArrayList<Integer>lexResult, int changes)
	{
		//C4 has been found
		if (lexResult.get(4) == -1)
		{
			//result of adding 2 edges to break C4
			//branchingReturnType c4Add1 = branchingCC(c4AddResult(G, deg, changes, lexResult, lexResult.get(0), lexResult.get(2)));
			//branchingReturnType c4Add2 = branchingCC(c4AddResult(G, deg, changes, lexResult, lexResult.get(1), lexResult.get(3)));
			
			
			//results of removing 2 edges to break C4
			branchingReturnType c4Remove0 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2)));
			branchingReturnType c4Remove1 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3)));
			branchingReturnType c4Remove2 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3)));
			branchingReturnType c4Remove3 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2)));
			branchingReturnType c4Remove4 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3)));
			branchingReturnType c4Remove5 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3)));
			
			
			//add to PriorityQueue to sort
			PriorityQueue<branchingReturnType> pQueue = new PriorityQueue<branchingReturnType>();
//				pQueue.add(c4Add1);
//				pQueue.add(c4Add2);
			pQueue.add(c4Remove0);
			pQueue.add(c4Remove1);
			pQueue.add(c4Remove2);
			pQueue.add(c4Remove3);
			pQueue.add(c4Remove4);
			pQueue.add(c4Remove5);
			
			branchingReturnType r = pQueue.remove();
			
			return r;
			
		}
		//P4 has been found
		else
		{
			branchingReturnType p4Remove0 = branchingCC(p4DeleteResult(G, deg, changes, lexResult.get(0), lexResult.get(1)));
			branchingReturnType p4Remove1 = branchingCC(p4DeleteResult(G, deg, changes, lexResult.get(1), lexResult.get(2)));
			branchingReturnType p4Remove2 = branchingCC(p4DeleteResult(G, deg, changes, lexResult.get(2), lexResult.get(3)));
			
			//add to PriorityQueue to sort
			PriorityQueue<branchingReturnType> pQueue = new PriorityQueue<branchingReturnType>();
			pQueue.add(p4Remove0);
			pQueue.add(p4Remove1);
			pQueue.add(p4Remove2);
			
			branchingReturnType r = pQueue.remove();
			
			return r;
			
		}
	}
	
	
	//result of adding an edge to break C4
	private static branchingReturnType c4AddResult(Graph<Integer, String> G, ArrayList<LinkedList<Integer>> deg, int changes, ArrayList<Integer> lexResult, int v0, int v1)
	{
		//make copy of graph and degree sequence
		Graph<Integer, String> graphCopy = clone.deepClone(G);
		ArrayList<LinkedList<Integer>> degCopy = clone.deepClone(deg);
		
		//update degree sequence (first edge)
		addEdge(graphCopy, degCopy, v0, v1);
		
		return new branchingReturnType(graphCopy, degCopy, changes + 1);
		
	}
	
	private static branchingReturnType c4Delete2Result(Graph<Integer, String> G, ArrayList<LinkedList<Integer>> deg, int changes, Integer v0, Integer v1, Integer v2, Integer v3)
	{
		//make copy of graph and degree sequence
		Graph<Integer, String> graphCopy = clone.deepClone(G);
		ArrayList<LinkedList<Integer>> degCopy = clone.deepClone(deg);
		
		//update degree sequence (first edge)
		removeEdge(graphCopy, degCopy, v0, v1);
		
		//update degree sequence (second edge)
		removeEdge(graphCopy, degCopy, v2, v3);
		
		
		return new branchingReturnType(graphCopy, degCopy, changes + 2);
		
	}
	
	private static branchingReturnType p4DeleteResult(Graph<Integer, String> G, ArrayList<LinkedList<Integer>> deg, int changes, Integer v0, Integer v1)
	{
		//make copy of graph and degree sequence
		Graph<Integer, String> graphCopy = clone.deepClone(G);
		ArrayList<LinkedList<Integer>> degCopy = clone.deepClone(deg);
		
		//update degree sequence (first edge)
		removeEdge(graphCopy, degCopy, v0, v1);
		
		return new branchingReturnType(graphCopy, degCopy, changes + 1);
		
	}
	
	
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
	private static void addEdge(Graph<Integer, String> G, ArrayList<LinkedList<Integer>> deg, Integer v0, Integer v1)
	{
		int v0Deg = G.degree(v0);
		int v1Deg = G.degree(v1);
		
		deg.get(v0Deg).remove(v0);
		
		try
		{
			deg.get(v0Deg + 1).add(v0);
		}
		catch (IndexOutOfBoundsException e)
		{
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

}

//a class to keep the return graph and the number of alterations that have been made
class branchingReturnType implements Comparable<branchingReturnType>
{
	Graph<Integer, String> G;
	int changes;
	ArrayList<LinkedList<Integer>> deg;
	
	branchingReturnType(Graph<Integer, String> graph, ArrayList<LinkedList<Integer>> d, int c)
	{
		G = graph;
		changes = c;
		deg = d;
	}

	@Override
	public int compareTo(branchingReturnType o) {
		return Integer.compare(changes, o.changes);
	}
}
