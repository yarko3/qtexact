import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

import qtUtils.genericLBFS;

import com.rits.cloning.Cloner;

import edu.uci.ics.jung.graph.Graph;


public class qtBranching 
{
	
	public static Cloner clone = new Cloner();
	
	public static void noHeuristic(Graph<Integer, String> G)
	{
		//keep proper degree order as an ArrayList<LinkedList<vertex>>
		ArrayList<LinkedList<Integer>> deg = degSequenceOrder(G);
		
		
		G = branching(G, deg, 0).G;
		
		
		
		
	}
	
	private static branchingReturnType branching(Graph<Integer, String> G, ArrayList<LinkedList<Integer>> deg, int changes)
	{
		//check if graph is QT
		ArrayList<Integer> t = new ArrayList<Integer>(0);
		
		for (int i = deg.size() - 1; i >= 0; i--)
		{
			while (!deg.get(i).isEmpty())
				t.add(deg.get(i).remove());
		}
		ArrayList<Integer> lexResult = genericLBFS.genericLexBFS(G, t);
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
				branchingReturnType c4Add = c4AddResult(G, deg, changes, lexResult);
				
				//results of removing 2 edges to break C4
				branchingReturnType c4Remove0 = c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2));
				branchingReturnType c4Remove1 = c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3));
				branchingReturnType c4Remove2 = c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3));
				branchingReturnType c4Remove3 = c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2));
				branchingReturnType c4Remove4 = c4Delete2Result(G, deg, changes, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3));
				branchingReturnType c4Remove5 = c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3));
				
				
				//add to PriorityQueue to sort
				PriorityQueue<branchingReturnType> pQueue = new PriorityQueue<branchingReturnType>();
				pQueue.add(c4Add);
				pQueue.add(c4Remove0);
				pQueue.add(c4Remove1);
				pQueue.add(c4Remove2);
				pQueue.add(c4Remove3);
				pQueue.add(c4Remove4);
				pQueue.add(c4Remove5);
				
				return pQueue.remove();
				
				
			}
			//P4 has been found
			else
			{
				branchingReturnType c4Remove0 = c4DeleteResult(G, deg, changes, lexResult.get(0), lexResult.get(1));
				branchingReturnType c4Remove1 = c4DeleteResult(G, deg, changes, lexResult.get(1), lexResult.get(2));
				branchingReturnType c4Remove2 = c4DeleteResult(G, deg, changes, lexResult.get(2), lexResult.get(3));
				
				//add to PriorityQueue to sort
				PriorityQueue<branchingReturnType> pQueue = new PriorityQueue<branchingReturnType>();
				pQueue.add(c4Remove0);
				pQueue.add(c4Remove1);
				pQueue.add(c4Remove2);
				
				return pQueue.remove();
			}
				
			
		}
	}
	
	
	//result of adding 2 edges to break C4
	private static branchingReturnType c4AddResult(Graph<Integer, String> G, ArrayList<LinkedList<Integer>> deg, int changes, ArrayList<Integer> lexResult)
	{
		//make copy of graph and degree sequence
		Graph<Integer, String> graphCopy = clone.deepClone(G);
		ArrayList<LinkedList<Integer>> degCopy = clone.deepClone(deg);
		
		//update degree sequence (first edge)
		addEdge(graphCopy, degCopy, lexResult.get(0), lexResult.get(2));
		
		//update degree sequence (second edge)
		addEdge(graphCopy, degCopy, lexResult.get(1), lexResult.get(3));
		
		
		return branching(graphCopy, degCopy, changes + 2);
		
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
		
		
		return branching(graphCopy, degCopy, changes + 2);
		
	}
	
	private static branchingReturnType c4DeleteResult(Graph<Integer, String> G, ArrayList<LinkedList<Integer>> deg, int changes, Integer v0, Integer v1)
	{
		//make copy of graph and degree sequence
		Graph<Integer, String> graphCopy = clone.deepClone(G);
		ArrayList<LinkedList<Integer>> degCopy = clone.deepClone(deg);
		
		//update degree sequence (first edge)
		removeEdge(graphCopy, degCopy, v0, v1);
		
		return branching(graphCopy, degCopy, changes + 1);
		
	}
	
	
	private static void removeEdge(Graph<Integer, String> G, ArrayList<LinkedList<Integer>> deg, Integer v0, Integer v1)
	{
		int v0Deg = G.degree(v0);
		int v1Deg = G.degree(v1);
		
		deg.get(v0Deg).remove(v0);
		if (deg.get(v0Deg - 1) == null)
		{
			deg.add(v0Deg - 1, new LinkedList<Integer>());
		}
		deg.get(v0Deg - 1).add(v0);
		
		deg.get(v1Deg).remove(v1);
		if (deg.get(v1Deg - 1) == null)
		{
			deg.add(v1Deg - 1, new LinkedList<Integer>());
		}
		deg.get(v1Deg - 1).add(v1);
		
		//find the edge to remove
		if (!G.removeEdge("e:" + v0 + "-" + v1))
			G.removeEdge("e:" + v1 + "-" + v0);
		
		
	}
	private static void addEdge(Graph<Integer, String> G, ArrayList<LinkedList<Integer>> deg, Integer v0, Integer v1)
	{
		int v0Deg = G.degree(v0);
		int v1Deg = G.degree(v1);
		
		deg.get(v0Deg).remove(v0);
		if (deg.get(v0Deg + 1) == null)
		{
			deg.add(v0Deg + 1, new LinkedList<Integer>());
		}
		deg.get(v0Deg + 1).add(v0);
		
		deg.get(v1Deg).remove(v1);
		if (deg.get(v1Deg + 1) == null)
		{
			deg.add(v1Deg + 1, new LinkedList<Integer>());
		}
		deg.get(v1Deg + 1).add(v1);
		
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
