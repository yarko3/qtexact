import java.util.ArrayList;
import java.util.LinkedList;

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
		
		
		
		
		
		
		
	}
	
	private Graph<Integer, String> branching(Graph<Integer, String> G, ArrayList<LinkedList<Integer>> deg, int changes)
	{
		//check if graph is QT
		ArrayList<Integer> lexResult = genericLBFS.genericLexBFS(G);
		//qt graph has been found
		if (lexResult.size() == G.getVertexCount() && lexResult.get(4) != Character.getNumericValue('C') && lexResult.get(4) != Character.getNumericValue('P'))
		{
			return G;
		}
		//branch on found P4 or C4
		else
		{	
			//C4 has been found, add 2 edges to remove C4
			if (lexResult.get(4) == Character.getNumericValue('C'))
			{
				//make copy of graph to change
				Graph<Integer, String> copy = clone.deepClone(G);
				
				//update degree sequence (first edge)
				deg.get(G.degree(lexResult.get(0))).remove(lexResult.get(0));
				if (deg.get(G.degree(lexResult.get(0)) + 1) == null)
				{
					deg.add(G.degree(lexResult.get(0)) + 1, new LinkedList<Integer>());
					deg.get(G.degree(lexResult.get(0)) + 1).add(lexResult.get(0));
				}
				deg.get(G.degree(lexResult.get(0))).remove(lexResult.get(2));
				if (deg.get(G.degree(lexResult.get(2)) + 1) == null)
				{
					deg.add(G.degree(lexResult.get(2)) + 1, new LinkedList<Integer>());
					deg.get(G.degree(lexResult.get(2)) + 1).add(lexResult.get(2));
				}
				
				//update degree sequence (second edge)
				deg.get(G.degree(lexResult.get(1))).remove(lexResult.get(0));
				if (deg.get(G.degree(lexResult.get(1)) + 1) == null)
				{
					deg.add(G.degree(lexResult.get(1)) + 1, new LinkedList<Integer>());
					deg.get(G.degree(lexResult.get(1)) + 1).add(lexResult.get(1));
				}
				deg.get(G.degree(lexResult.get(3))).remove(lexResult.get(3));
				if (deg.get(G.degree(lexResult.get(3)) + 1) == null)
				{
					deg.add(G.degree(lexResult.get(3)) + 1, new LinkedList<Integer>());
					deg.get(G.degree(lexResult.get(3)) + 1).add(lexResult.get(3));
				}
				
				//add edges
				copy.addEdge("e:" + lexResult.get(0) + "-" + lexResult.get(2), lexResult.get(0), lexResult.get(2));
				copy.addEdge("e:" + lexResult.get(1) + "-" + lexResult.get(3), lexResult.get(1), lexResult.get(3));
				
				
				
			}
			
		}
	}
	
	
	//a class to keep the return graph and the number of alterations that have been made
	class branchingReturnType
	{
		Graph<Integer, String> G;
		int changes;
		branchingReturnType(Graph<Integer, String> graph, int c)
		{
			G = graph;
			changes = c;
		}
	}
	
	public static ArrayList<LinkedList<Integer>> degSequenceOrder(Graph<Integer, String> G)
	{
		//store vertices of same degree in LinkedList<Integer> at the index of their degree in ArrayList
		ArrayList<LinkedList<Integer>> deg = new ArrayList<LinkedList<Integer>>();
		
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
		
		return deg;
	}

}
