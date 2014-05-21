/**
 * Yaroslav Senyuta
 * NSERC USRA Grant (2014)
 */


package qtUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.Pair;
/**Class used for generating quasi-threshold graphs
 * 
 * @author Yarko Senyuta
 *
 * @param <V> vertex
 * @param 
 */
public class qtGenerate<V>
{
	/**
	 * generate a completely connected graph of number of edges
	 * @param number size of clique
	 * @return graph
	 */
	public SparseGraph<Integer, Pair<Integer>> clique(int number)
	{
		SparseGraph<Integer, Pair<Integer>> graph = new SparseGraph<Integer, Pair<Integer>>();
		if (number > 0)
		{
			graph.addEdge(new Pair<Integer>(1, 2), 1, 2);
			int curEdge = 3;
			while (curEdge <= number)
			{
				Integer[] array = new Integer[graph.getVertexCount()];
				array = graph.getVertices().toArray(array);
				for(int v : array)
				{
					graph.addEdge(new Pair<Integer>(curEdge, v), curEdge, v);
				}
				curEdge++;
			}
		}
		return graph;
	}
	
	/**Generate graph of two cliques of size n1 and n2 that share one vertex (quasi threshold)
	 * 
	 * @param n1 first clique size
	 * @param n2 second clique size
	 * @return graph
	 */
	public static Graph<Integer, Pair<Integer>> cliques(int n1, int n2)
	{
		SparseGraph<Integer, Pair<Integer>> graph = new SparseGraph<Integer, Pair<Integer>>();
		
		int curV = 0;
		graph.addVertex(curV++);
		if (n1 > 0)
		{
			while (curV <= n1)
			{
				Integer[] array = new Integer[graph.getVertexCount()];
				array = graph.getVertices().toArray(array);
				for (int v : array)
				{
					graph.addEdge(new Pair<Integer>(curV, v), curV, v);
				}
				curV++;
			}
		}
		
		
		curV--;
		
		int newCount = 1;
		
		if (n2 > 0)
		{
			while (newCount <= n2)
			{
				for (int i = curV; i < newCount + curV; i++)
				{
					graph.addEdge(new Pair<Integer>(curV + newCount, i), curV + newCount, i);
				}
				newCount++;
			}
		}
		return graph;

		
	}

	
	/**Generate graph of two cliques of vertices n1 and n2 that are connected by one edge (not quasi threshold)
	 * 
	 * @param n1 size of first clique
	 * @param n2 size of second clique
	 * @return graph
	 */
	public static Graph<Integer, Pair<Integer>> cliqueJoin(int n1, int n2)
	{
		SparseGraph<Integer, Pair<Integer>> graph = new SparseGraph<Integer, Pair<Integer>>();
		
		int curV = 0;
		graph.addVertex(curV++);
		if (n1 > 0)
		{
			while (curV <= n1)
			{
				Integer[] array = new Integer[graph.getVertexCount()];
				array = graph.getVertices().toArray(array);
				for (int v : array)
				{
					graph.addEdge(new Pair<Integer>(curV, v), curV, v);
				}
				curV++;
			}
		}
		
		Integer[] f = new Integer[graph.getVertexCount()];
		
		f = graph.getVertices().toArray(f);
		ArrayList<Integer> fHalf = new ArrayList<Integer>();
		for (int i : f)
		{
			fHalf.add(i);
		}
		
		graph.addVertex(curV);
		
		int newCount = 1;
		
		if (n2 > 0)
		{
			while (newCount <= n2)
			{
				Integer[] array = new Integer[graph.getVertexCount()];
				array = graph.getVertices().toArray(array);
				for (int v : array)
				{
					if (!fHalf.contains(v))
						graph.addEdge(new Pair<Integer>(curV, v), curV + newCount, v);
				}
				newCount++;
			}
		}
		
		//final edge to join the two halves 
		graph.addEdge(new Pair<Integer>(0, curV+newCount-1), 0, curV+newCount-1);
		return graph;
		
	}
	
	/**Generate a simple graph with an induced C4
	 * 
	 * @return graph with C4
	 */
	public static Graph<Integer, Pair<Integer>> simpleC4()
	{
		SparseGraph<Integer, Pair<Integer>> G = new SparseGraph<Integer, Pair<Integer>>();
		G.addEdge(new Pair<Integer>(0, 1), 0, 1);
		G.addEdge(new Pair<Integer>(0, 3), 0, 3);
		G.addEdge(new Pair<Integer>(3, 2), 3, 2);
		G.addEdge(new Pair<Integer>(1, 2), 1, 2);
		G.addEdge(new Pair<Integer>(1, 4), 1, 4);
		G.addEdge(new Pair<Integer>(1, 5), 1, 5);
		G.addEdge(new Pair<Integer>(1, 6), 1, 6);

		
		return G;
	}
	
	public Graph<Integer, Pair<Integer>> randomQT(int n)
	{
		SparseGraph<Integer, Pair<Integer>> G = new SparseGraph<Integer, Pair<Integer>>();
		Random rand = new Random();
		
		int vCount = 0;

		Collection<Integer> v;
		Integer[] array;
		
		while (n - vCount > 0)
		{
			if (n - vCount < 3)
			{
				v = G.getVertices();
				array = new Integer[v.size()];
				array = v.toArray(array);
				for (int i : array)
				{
					G.addEdge(new Pair<Integer>(i, vCount), i, vCount);
				}
				vCount++;	
			}
			else
			{
				double r = rand.nextDouble();
				switch ((int)(r * 3)){
				//create a TP 3 vetex and TP 2 vertex
					case (0):
						if (n - vCount > 5)
						{
							//create TP 3
							G.addEdge(new Pair<Integer>(vCount, vCount+1), vCount, ++vCount);
							vCount++;
							G.addEdge(new Pair<Integer>(vCount, (vCount + 1)), vCount, ++vCount);
							vCount++;
							
							//create TP 2
							G.addEdge(new Pair<Integer>(vCount, (vCount + 1)), vCount, ++vCount);
							vCount++;
							
							//connect everything to one vertex
							v = G.getVertices();
							array = new Integer[v.size()];
							array = v.toArray(array);
							for (int i : array)
							{
								G.addEdge(new Pair<Integer>(i, vCount), i, vCount);
							}
							vCount++;
							break;
						}
					case (1):
						if (n - vCount > 3)
						{
							//create TP 3
							G.addEdge(new Pair<Integer>(vCount, (vCount + 1)), vCount, ++vCount);
							vCount++;
							G.addEdge(new Pair<Integer>(vCount, (vCount + 1)), vCount, ++vCount);
							vCount++;
							
							//connect everything to one vertex
							v = G.getVertices();
							array = new Integer[v.size()];
							array = v.toArray(array);
							for (int i : array)
							{
								G.addEdge(new Pair<Integer>(i, vCount), i, vCount);
							}
							vCount++;
							break;
						}
					case (2):
						if (n - vCount > 2)
						{
							//create TP 2
							G.addEdge(new Pair<Integer>(vCount, (vCount + 1)), vCount, ++vCount);
							vCount++;
							
							//connect everything to one vertex
							v = G.getVertices();
							array = new Integer[v.size()];
							array = v.toArray(array);
							for (int i : array)
							{
								G.addEdge(new Pair<Integer>(i, vCount), i, vCount);
							}
							vCount++;
							break;
						}
				}
						
			}
		}
		return G;
	}
	
	public static Graph<Integer, Pair<Integer>> westernElectricNetwork()
	{
		Graph<Integer,Pair<Integer>> g = new SparseGraph<Integer,Pair<Integer>>();
		
		g.addEdge(new Pair<Integer>(11, 21), 11,21);
		g.addEdge(new Pair<Integer>(11, 22), 11,22);
		g.addEdge(new Pair<Integer>(11, 23), 11,23);
		g.addEdge(new Pair<Integer>(11, 24), 11,24);
		g.addEdge(new Pair<Integer>(21, 22), 21,22);
		g.addEdge(new Pair<Integer>(21, 23), 21,23);
		g.addEdge(new Pair<Integer>(21, 24), 21,24);
		g.addEdge(new Pair<Integer>(21, 25), 21,25);
		g.addEdge(new Pair<Integer>(21, 31), 21,31);
		g.addEdge(new Pair<Integer>(22, 23), 22,23);
		g.addEdge(new Pair<Integer>(22, 24), 22,24);
		g.addEdge(new Pair<Integer>(22, 31), 22,31);
		g.addEdge(new Pair<Integer>(23, 24), 23,24);
		g.addEdge(new Pair<Integer>(23, 25), 23,25);
		g.addEdge(new Pair<Integer>(23, 31), 23,31);
		g.addEdge(new Pair<Integer>(24, 25), 24,25);
		g.addEdge(new Pair<Integer>(24, 31), 24,31);
		g.addEdge(new Pair<Integer>(25, 27), 25,27);
		g.addEdge(new Pair<Integer>(25, 31), 25,31);
		g.addEdge(new Pair<Integer>(26, 27), 26,27);
		g.addEdge(new Pair<Integer>(26, 28), 26,28);
		g.addEdge(new Pair<Integer>(26, 29), 26,29);
		g.addEdge(new Pair<Integer>(27, 28), 27,28);
		g.addEdge(new Pair<Integer>(27, 29), 27,29);
		g.addEdge(new Pair<Integer>(27, 34), 27,34);
		g.addEdge(new Pair<Integer>(28, 29), 28,29);
		g.addEdge(new Pair<Integer>(28, 34), 28,34);		
		g.addEdge(new Pair<Integer>(29, 34), 29,34);
		
		return g;
	}
	
	public static Graph<Integer, Pair<Integer>> nonQTEx1()
	{
		Graph<Integer,Pair<Integer>> g = new SparseGraph<Integer,Pair<Integer>>();
		
		g.addEdge(new Pair<Integer>(0, 1), 0, 1);
		g.addEdge(new Pair<Integer>(0, 2), 0, 2);
		g.addEdge(new Pair<Integer>(0, 4), 0, 4);
		g.addEdge(new Pair<Integer>(1, 2), 1, 2);
		g.addEdge(new Pair<Integer>(1, 4), 1, 4);
		g.addEdge(new Pair<Integer>(1, 5), 1, 5);
		g.addEdge(new Pair<Integer>(2, 3), 2, 3);
		g.addEdge(new Pair<Integer>(3, 4), 3, 4);
		g.addEdge(new Pair<Integer>(4, 5), 4, 5);
		g.addEdge(new Pair<Integer>(5, 6), 5, 6);
		g.addEdge(new Pair<Integer>(6, 7), 6, 7);
		g.addEdge(new Pair<Integer>(7, 8), 7, 8);
		g.addEdge(new Pair<Integer>(6, 9), 6, 9);
		g.addEdge(new Pair<Integer>(7, 9), 7, 9);
		g.addEdge(new Pair<Integer>(9, 10), 9,10);

		return g;
	}

}
