import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

import qtUtils.genericLBFS;

import com.rits.cloning.Cloner;

import edu.uci.ics.jung.graph.Graph;


public class qtBranching 
{
	
	public static Cloner clone = new Cloner();
	
	public static Graph<Integer, String> noHeuristic(Graph<Integer, String> G)
	{
		//keep proper degree order as an ArrayList<LinkedList<vertex>>
		ArrayList<LinkedList<Integer>> deg = degSequenceOrder(G);
		
		branchingReturnType goal = branching(new branchingReturnType(G, deg, 0));
		System.out.println("Number of moves: " + goal.changes);
		return goal.G;
		
	}
	
	private static branchingReturnType branching(branchingReturnType s)
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
				//branchingReturnType c4Add1 = branching(c4AddResult(G, deg, changes, lexResult, lexResult.get(0), lexResult.get(2)));
				//branchingReturnType c4Add2 = branching(c4AddResult(G, deg, changes, lexResult, lexResult.get(1), lexResult.get(3)));
				
				
				//results of removing 2 edges to break C4
				branchingReturnType c4Remove0 = branching(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2)));
				branchingReturnType c4Remove1 = branching(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3)));
				branchingReturnType c4Remove2 = branching(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3)));
				branchingReturnType c4Remove3 = branching(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2)));
				branchingReturnType c4Remove4 = branching(c4Delete2Result(G, deg, changes, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3)));
				branchingReturnType c4Remove5 = branching(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3)));
				
				
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
				branchingReturnType p4Remove0 = branching(p4DeleteResult(G, deg, changes, lexResult.get(0), lexResult.get(1)));
				branchingReturnType p4Remove1 = branching(p4DeleteResult(G, deg, changes, lexResult.get(1), lexResult.get(2)));
				branchingReturnType p4Remove2 = branching(p4DeleteResult(G, deg, changes, lexResult.get(2), lexResult.get(3)));
				
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
