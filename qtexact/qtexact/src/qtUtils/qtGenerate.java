/**
 * Yaroslav Senyuta
 * NSERC USRA Grant (2014)
 */


package qtUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import search.qtLBFSNoHeuristic;

import com.rits.cloning.Cloner;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;



/**Class used for generating quasi-threshold graphs
 * 
 * @author Yarko Senyuta
 *
 * @param <V> vertex
 */
public class qtGenerate<V>
{
	public static Cloner clone = new Cloner();
	/**
	 * generate a completely connected graph of number of edges
	 * @param number size of clique
	 * @return graph
	 */
	public static SparseGraph<Integer, Pair<Integer>> clique(int number)
	{
		SparseGraph<Integer, Pair<Integer>> graph = new SparseGraph<Integer, Pair<Integer>>();
		if (number >= 0)
		{
			graph.addEdge(new Pair<Integer>(0, 1), 0, 1);
			int curEdge = 2;
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
	
	
	public static SparseGraph<Integer, Pair<Integer>> clique(int number, int start)
	{
		SparseGraph<Integer, Pair<Integer>> graph = new SparseGraph<Integer, Pair<Integer>>();
		if (number+ start > start)
		{
			graph.addEdge(new Pair<Integer>(start, start+1), start, start+1);
			int curEdge = start+2;
			while (curEdge <= number+start)
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
		
		Graph<Integer, Pair<Integer>> fHalf = clique(n1);
		Graph<Integer, Pair<Integer>> sHalf = clique(n2, n1+1);
		
		LinkedList<Graph<Integer, Pair<Integer>>> l = new LinkedList<Graph<Integer, Pair<Integer>>>();
		l.add(fHalf);
		l.add(sHalf);
		
		Graph<Integer, Pair<Integer>> graph = graphJoinP(l);
		
		//final edge to join the two halves 
		graph.addEdge(new Pair<Integer>(n1, n1+1), n1, n1+1);
		return graph;
		
	}
	
	private static Graph<Integer, Pair<Integer>> graphJoinP(LinkedList<Graph<Integer, Pair<Integer>>> l) {
		UndirectedSparseGraph<Integer, Pair<Integer>> rGraph = new UndirectedSparseGraph<Integer, Pair<Integer>>();
		//build graph from connected components
		for (Graph<Integer, Pair<Integer>> r : l)
		{
			
			//add all the edges
			for (Integer v : r.getVertices())
			{
				rGraph.addVertex(v);
			}
			//add all the vertices
			for (Pair<Integer> a : r.getEdges())
			{
				rGraph.addEdge(clone.deepClone(a), a.getFirst(), a.getSecond());
			}

		}
		return rGraph;
	}
	
	public SparseGraph<V, Pair<V>> graphJoin(LinkedList<Graph<V, Pair<V>>> l) {
		SparseGraph<V, Pair<V>> rGraph = new SparseGraph<V, Pair<V>>();
		//build graph from connected components
		
		//map to new Integers
		for (Graph<V, Pair<V>> r : l)
		{
			
			//add all the edges
			for (V v : r.getVertices())
			{
				rGraph.addVertex(v);
			}
			//add all the vertices
			for (Pair<V> a : r.getEdges())
			{
				rGraph.addEdge(clone.deepClone(a), a.getFirst(), a.getSecond());
			}

		}
		return rGraph;
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
		Graph<Integer,Pair<Integer>> g = new UndirectedSparseGraph<Integer,Pair<Integer>>();
		
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
	
	public static Graph<Integer, Pair<Integer>> nonQTEx2()
	{
		Graph<Integer,Pair<Integer>> g = new SparseGraph<Integer,Pair<Integer>>();
		
		g.addEdge(new Pair<Integer>(0, 1), 0, 1);
		g.addEdge(new Pair<Integer>(1, 2), 1, 2);
		g.addEdge(new Pair<Integer>(2, 3), 2, 3);
		g.addEdge(new Pair<Integer>(3, 0), 3, 0);
		g.addEdge(new Pair<Integer>(3, 4), 3, 4);
		g.addEdge(new Pair<Integer>(4, 5), 4, 5);
		g.addEdge(new Pair<Integer>(5, 6), 5, 6);
		g.addEdge(new Pair<Integer>(6, 7), 6, 7);
		g.addEdge(new Pair<Integer>(7, 4), 7, 4);
		g.addEdge(new Pair<Integer>(7, 8), 7, 8);
		g.addEdge(new Pair<Integer>(8, 5), 8, 5);

		return g;
	}
	public static Graph<Integer, Pair<Integer>> nonQTEx3()
	{
		Graph<Integer,Pair<Integer>> g = new SparseGraph<Integer,Pair<Integer>>();
		
		g.addEdge(new Pair<Integer>(0, 1), 0, 1);
		g.addEdge(new Pair<Integer>(1, 2), 1, 2);
		g.addEdge(new Pair<Integer>(2, 3), 2, 3);
		g.addEdge(new Pair<Integer>(2, 4), 2, 4);
		g.addEdge(new Pair<Integer>(4, 5), 4, 5);
		g.addEdge(new Pair<Integer>(0, 4), 0, 4);
		
		return g;
	}
	
	public static Graph<Integer, Pair<Integer>> nonQTEx4()
	{
		Graph<Integer,Pair<Integer>> g = new SparseGraph<Integer,Pair<Integer>>();
		
		g.addEdge(new Pair<Integer>(3, 4), 3, 4);
		g.addEdge(new Pair<Integer>(3, 5), 3, 5);
		g.addEdge(new Pair<Integer>(5, 6), 5, 6);
		g.addEdge(new Pair<Integer>(6, 9), 6, 9);
		g.addEdge(new Pair<Integer>(6, 10), 6, 10);
		g.addEdge(new Pair<Integer>(3, 10), 3, 10);
		g.addEdge(new Pair<Integer>(10, 0), 10, 0);
		
		
		return g;
	}
	
	public static Graph<Integer, Pair<Integer>> nonQTEx5()
	{
		Graph<Integer,Pair<Integer>> g = new SparseGraph<Integer,Pair<Integer>>();
		
		g.addEdge(new Pair<Integer>(2, 3), 2, 3);
		g.addEdge(new Pair<Integer>(3, 6), 3, 6);
		g.addEdge(new Pair<Integer>(3, 10), 3, 10);
		g.addEdge(new Pair<Integer>(2, 8), 2, 8);
		g.addEdge(new Pair<Integer>(0, 2), 0, 2);
		g.addEdge(new Pair<Integer>(8, 10), 8, 10);
		g.addEdge(new Pair<Integer>(10, 0), 10, 0);
		g.addEdge(new Pair<Integer>(0, 9), 0, 9);
		g.addEdge(new Pair<Integer>(6, 9), 6, 9);
		g.addEdge(new Pair<Integer>(7, 4), 7, 4);
		g.addEdge(new Pair<Integer>(6, 7), 6, 7);
		g.addEdge(new Pair<Integer>(10, 7), 10, 7);
		g.addEdge(new Pair<Integer>(7, 0), 7, 0);
		
		
		return g;
	}
	
	public static Graph<Integer, Pair<Integer>> nonQTEx6()
	{
		Graph<Integer,Pair<Integer>> g = new SparseGraph<Integer,Pair<Integer>>();
		
		g.addEdge(new Pair<Integer>(11, 22), 11, 22);
		////g.addEdge(new Pair<Integer>(11, 18), 11, 18);
		g.addEdge(new Pair<Integer>(22, 6), 22, 6);
		g.addEdge(new Pair<Integer>(18, 23), 18, 23);
		g.addEdge(new Pair<Integer>(18, 5), 18, 5);
		g.addEdge(new Pair<Integer>(18, 6), 18, 6);
		g.addEdge(new Pair<Integer>(6, 16), 6, 16);
		////g.addEdge(new Pair<Integer>(6, 5), 6, 5);
		////g.addEdge(new Pair<Integer>(6, 4), 6, 4);
		g.addEdge(new Pair<Integer>(6, 3), 6, 3);
		g.addEdge(new Pair<Integer>(5, 4), 5, 4);
		g.addEdge(new Pair<Integer>(4, 3), 4, 3);
		g.addEdge(new Pair<Integer>(3, 10), 3, 10);
		g.addEdge(new Pair<Integer>(5, 2), 5, 2);
		g.addEdge(new Pair<Integer>(2, 4), 2, 4);
		g.addEdge(new Pair<Integer>(2, 8), 2, 8);
		////g.addEdge(new Pair<Integer>(2, 26), 2, 26);
		g.addEdge(new Pair<Integer>(5, 26), 5, 26);
		
		
		return g;
	}
	
	public static Graph<Integer, Pair<Integer>> ER(int vertices, double p, long seed)
	{
		Random rand = new Random(seed);
		
		Graph<Integer, Pair<Integer>> graph = new UndirectedSparseGraph<Integer, Pair<Integer>>();
		
		for (int i = 0; i < vertices; i++)
		{
			graph.addVertex(i);
		}
		for (Integer i : graph.getVertices())
		{
			for (Integer j : graph.getVertices())
			{
				if (rand.nextDouble() < p)
				{
					if (i != j)
						graph.addEdge(new Pair<Integer>(i, j), i, j);
				}
			}
		}
		
		return graph;
	}
	
	
	public static Graph<Integer, Pair<Integer>> ER(int vertices, double p, int start)
	{
		Random rand = new Random();
		
		Graph<Integer, Pair<Integer>> graph = new UndirectedSparseGraph<Integer, Pair<Integer>>();
		
		for (int i = start; i < vertices+start; i++)
		{
			graph.addVertex(i);
		}
		for (Integer i : graph.getVertices())
		{
			for (Integer j : graph.getVertices())
			{
				if (rand.nextDouble() < p)
				{
					if (i != j)
						graph.addEdge(new Pair<Integer>(i, j), i, j);
				}
			}
		}
		
		return graph;
	}
	
	
	
	public Graph<String, Pair<String>> fromBipartiteFile(String filename) throws FileNotFoundException, UnsupportedEncodingException
	{
		Graph<String, Pair<String>> initial = new UndirectedSparseGraph<String, Pair<String>>();
		HashSet<String> leftVertices = new HashSet<String>();
		HashSet<String> rightVertices = new HashSet<String>();
		
		
		FileReader file = null;
		try {
			file = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}
		
		Scanner scan = new Scanner(file);
		
		//get size of left and right sides of bipartite network
//		int left = scan.nextInt();
//		int right = scan.nextInt();
		
		
		
		while (scan.hasNext()) {
			String a = scan.next();
			String b = scan.next();
			
			leftVertices.add(a);
			rightVertices.add(b);
			
			initial.addEdge(new Pair<String>(a, b), a, b);
		}
		try {
			scan.close();
			file.close();
		} catch (IOException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}
		
		Graph<String, Pair<String>> g = new UndirectedSparseGraph<String, Pair<String>>();
		
		//build new graph
		
		
		//remove common vertices from both sides
		HashSet<Integer> all = new HashSet<Integer>();
		HashSet<Integer> common = new HashSet<Integer>();
		//all.addAll(leftVertices);
		
//		for (Integer i : rightVertices)
//		{
//			if (!all.add(i))
//				common.add(i);
//		}
		
//		for (Integer v : common)
//		{
//			initial.removeVertex(v);
//		}
		
		//leftVertices.removeAll(common);
		//rightVertices.removeAll(common);
		
		
		HashSet<String> oldRight = rightVertices;
		
//		rightVertices = new HashSet<String>();
//		
//    	
//    	FileReader wineries = null;
//		try {
//			wineries = new FileReader("datasets/urlLatLong.txt");
//		} catch (FileNotFoundException e) {
//			System.out.println("File " + " could not be found.");
//			e.printStackTrace();
//		}
//
//		scan = new Scanner(wineries);
//
//		while (scan.hasNextLine()) {
//			rightVertices.add(scan.next().toLowerCase());
//			//scan.nextDouble();
//			//scan.nextDouble();
//		}
//		try {
//			scan.close();
//			wineries.close();
//		} catch (IOException e) {
//			System.out.println("File " + " could not be found.");
//			e.printStackTrace();
//		}
		
		
		
		
		PrintWriter writer = new PrintWriter("datasets/bipartiteEdgeSet.txt", "UTF-8");
		
		//must be bipartite for this to work
		
		//k is the number of common neighbours needed to make an edge
		int k = 10;
		
		for (String i : rightVertices)
		{
			if (initial.containsVertex(i))
			{
				Collection<String> neighbours = initial.getNeighbors(i);

				for (String n : neighbours)
				{
					//no self edges
					if (!n.equals(i))
					{
						Collection<String> nn = initial.getNeighbors(n);
						for (String friend : nn)
						{
							if (!i.equals(friend) && !leftVertices.contains(friend))
							{
								int count = 0;
								Collection<String> friendN = initial.getNeighbors(friend);
								
								//check for number of common neighbours
								for (String temp0 : neighbours)
								{
									for (String temp1 : friendN)
									{
										if (temp0.equals(temp1))
										{
											count++;
										}
									}
								}
								if (count > k)
								{
									if (!(g.containsVertex(friend) && g.containsVertex(i) && g.isNeighbor(friend, i)))
									{
										g.addEdge(new Pair<String>(i , friend), i, friend);
										writer.println(i + " " + friend);
									}
								}
							}
						}
					}
				}
			}
			
		}
		
		
		
		writer.close();
		
		
//		for (int i = 1; i <= right; i++)
//		{
//			//get neighbours of each right node
//			Collection<Integer> neighbourhood = initial.getNeighbors(i + left);
//			
//			for (Integer n : neighbourhood)
//			{
//				for (Integer j : neighbourhood)
//				{
//					if (n != j)
//					{
//						g.addEdge(new Pair<Integer>(n, j), n, j);
//					}
//				}
//			}
//		}
		return g;
		
	}
	
	
	public Graph<Integer, Pair<Integer>> manyInducedC4(int n)
	{
		Graph<Integer, Pair<Integer>> g = new SparseGraph<Integer, Pair<Integer>>();
		
		for (int i = 2; i < n + 2; i++)
		{
			g.addEdge(new Pair<Integer>(0, i), 0, i);
			g.addEdge(new Pair<Integer>(1, i), 1, i);
		}
		
		
		return g;
		
	}
	
	public Graph<Integer, Pair<Integer>> houseStruct()
	{
		Graph<Integer, Pair<Integer>> g = new SparseGraph<Integer, Pair<Integer>>();
		
		//house
//		g.addEdge(new Pair<Integer>(0, 1), 0, 1);
//		g.addEdge(new Pair<Integer>(1, 2), 1, 2);
//		g.addEdge(new Pair<Integer>(2, 3), 2, 3);
//		g.addEdge(new Pair<Integer>(3, 4), 3, 4);
//		g.addEdge(new Pair<Integer>(4, 0), 4, 0);
//		g.addEdge(new Pair<Integer>(4, 1), 4, 1);

//		g.addEdge(new Pair<Integer>(0, 2), 0, 2);
//		g.addEdge(new Pair<Integer>(0, 3), 0, 3);
		
		
		//P5
//		g.addEdge(new Pair<Integer>(0, 1), 0, 1);
//		g.addEdge(new Pair<Integer>(1, 2), 1, 2);
//		g.addEdge(new Pair<Integer>(2, 3), 2, 3);
//		g.addEdge(new Pair<Integer>(3, 4), 3, 4);
//		
		
		//fork
//		g.addEdge(new Pair<Integer>(0, 1), 0, 1);
//		g.addEdge(new Pair<Integer>(1, 2), 1, 2);
//		g.addEdge(new Pair<Integer>(2, 3), 2, 3);
//		g.addEdge(new Pair<Integer>(2, 4), 2, 4);
//		g.addEdge(new Pair<Integer>(3, 4), 3, 4);
		
		
		//4 pan
//		g.addEdge(new Pair<Integer>(0, 1), 0, 1);
//		g.addEdge(new Pair<Integer>(1, 2), 1, 2);
//		g.addEdge(new Pair<Integer>(2, 3), 2, 3);
//		g.addEdge(new Pair<Integer>(3, 4), 3, 4);
//		g.addEdge(new Pair<Integer>(4, 1), 4, 1);
//		
//		g.addEdge(new Pair<Integer>(4, 2), 4, 2);
//		
		
		
	
		//double C4
//		g.addEdge(new Pair<Integer>(0, 1), 0, 1);
//		g.addEdge(new Pair<Integer>(1, 2), 1, 2);
//		g.addEdge(new Pair<Integer>(2, 3), 2, 3);
//		g.addEdge(new Pair<Integer>(3, 4), 3, 4);
//		g.addEdge(new Pair<Integer>(4, 1), 4, 1);
//		g.addEdge(new Pair<Integer>(0, 3), 0, 3);
////		
//		g.addEdge(new Pair<Integer>(4, 2), 4, 2);
		
		
		//C5
		g.addEdge(new Pair<Integer>(0, 1), 0, 1);
		g.addEdge(new Pair<Integer>(1, 2), 1, 2);
		g.addEdge(new Pair<Integer>(2, 3), 2, 3);
		g.addEdge(new Pair<Integer>(3, 4), 3, 4);
		g.addEdge(new Pair<Integer>(4, 0), 4, 0);
		
		

		
		return g;
	}
	
	/**
	 * quick and dirty graph equation method
	 * 
	 * @param g0
	 * @param g1
	 * @return
	 */
	public boolean graphEquals(Graph<V, Pair<V>> g0, Graph<V, Pair<V>> g1)
	{
		
		boolean flag = true;
		
		//check vertices
		for (V v0 : g0.getVertices())
		{
			if (!g1.containsVertex(v0))
			{
				System.out.println("\nGraph 1 does not contain vertex " + v0);
				flag = false;
			}
		}
		for (V v1 : g1.getVertices())
		{
			if (!g0.containsVertex(v1))
			{
				System.out.println("\nGraph 0 does not contain vertex " + v1);
				flag = false;
			}
		}
		
		//check edges
		for (Pair<V> e0 : g0.getEdges())
		{
			if (!g1.isNeighbor(e0.getFirst(), e0.getSecond())){
				
				System.out.println("\nGraph 1 does not contain edge " + e0);
				flag = false;
			}
		}
		for (Pair<V> e1 : g1.getEdges())
		{
			if (!g0.isNeighbor(e1.getFirst(), e1.getSecond()))
			{
				System.out.println("\nGraph 0 does not contain edge " + e1);
				flag = false;
			}
		}
		
		return flag;
	}
	
	public Graph<Integer, Pair<Integer>> erJoins(int n0, int n1, int n2, double p0, double p1, double p2)
	{
		//generate graphs
		Graph<Integer, Pair<Integer>> g0 = ER(n0, p0, 0);
		Graph<Integer, Pair<Integer>> g1 = ER(n1, p1, n0);
		Graph<Integer, Pair<Integer>> g2 = ER(n2, p2, n0+n1);
		
		
		g0.addEdge(new Pair<Integer>(0, n0), 0, n0);
		g0.addEdge(new Pair<Integer>(n0-1, n0), n0-1, n0);
		g1.addEdge(new Pair<Integer>(n0, n0+n1), n0, n0+n1);
		g1.addEdge(new Pair<Integer>(n0+n1-1, n0+n1), n0+n1-1, n0+n1);
		g1.addEdge(new Pair<Integer>(n0+n1, 0), n0+n1, 0);
		g1.addEdge(new Pair<Integer>(n0+n1+n2-1, 0), n0+n1+n2-1, 0);
		
		LinkedList<Graph<Integer, Pair<Integer>>> list = new LinkedList<Graph<Integer, Pair<Integer>>>();
		list.add(g0);
		list.add(g1);
		list.add(g2);
		
		return graphJoinP(list);
		
	}
	
	public Graph<Integer, Pair<Integer>> facebookGraph(String filename)
	{
		Graph<Integer, Pair<Integer>> g = new UndirectedSparseGraph<Integer, Pair<Integer>>();
		
		FileReader file = null;
		try {
			file = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}
		
		Scanner scan = new Scanner(file);
		
		scan.useDelimiter("\"");
		
		String name0;
		String name1;
		
		int n = 0;
		
		while (scan.hasNext() /*&& n < 60*/)
		{
			//get rid of initial number
			scan.next();
			//get first name
			name0 = scan.next();
			
			//skip middle bit
			scan.next();
			
			//get second name
			name1 = scan.next();
			
			//add edge
			g.addEdge(new Pair<Integer>(name0.hashCode(), name1.hashCode()), name0.hashCode(), name1.hashCode());
			n++;
		}
		
		scan.close();
		return g;
		
	}
	
	public Graph<Integer, Pair<Integer>> treeRandom(int n, long seed)
	{
		Graph<myVertex, Pair<myVertex>> init = new SparseGraph<myVertex, Pair<myVertex>>();
		Random rand = new Random();
		rand.setSeed(seed);
		
		//generate first vertex
		myVertex root = new myVertex(0, null, 0);
		
		int count = 1;
		int random;
		myVertex parent;
		myVertex next;
		//store vertices based on their data
		HashMap<Integer, myVertex> vertices = new HashMap<Integer, myVertex>();
		vertices.put(0, root);
		
		
		//store vertices at their depths as well
		HashMap<Integer, LinkedList<myVertex>> depthVert = new HashMap<Integer, LinkedList<myVertex>>();
		
		if (depthVert.get(0) == null)
		{
			depthVert.put(0, new LinkedList<myVertex>());
		}
		depthVert.get(0).add(root);
		
		while (count < n)
		{
			//get data of next parent
			random = rand.nextInt(count);
			parent = vertices.get(random);
			
			
			//new vertex to be added
			next = new myVertex(count, parent, parent.depth + 1);
			vertices.put(count, next);
			
			if (depthVert.get(parent.depth + 1) == null)
			{
				depthVert.put(parent.depth + 1, new LinkedList<myVertex>());
			}
			depthVert.get(parent.depth + 1).add(next);
			
			
			while (parent != null && rand.nextInt(2) == 0)
			{
				init.addEdge(new Pair<myVertex>(next, parent), next, parent);
				parent = parent.parent;
			}
			
			count++;
		}
		
		//depth and frequency
//		for (Integer k : depthVert.keySet())
//		{
//			System.out.println(k + "\t" + depthVert.get(k).size());
//		}
		
		
		
		//change depth HashMap to regular integer LinkedLists
		HashMap<Integer, LinkedList<Integer>> list = new HashMap<Integer, LinkedList<Integer>>();
		for (Integer k : depthVert.keySet())
		{
			LinkedList<myVertex> l = depthVert.get(k);
			
			list.put(k, new LinkedList<Integer>());
			
			for (myVertex v : l)
			{
				list.get(k).add(v.data);
			}
		}
		
		
		Graph<Integer, Pair<Integer>> rtn = new UndirectedSparseGraph<Integer, Pair<Integer>>();
		
		//copy all edges and vertices to return graph
		
		for (Pair<myVertex> e: init.getEdges())
		{
			rtn.addEdge(new Pair<Integer>(e.getFirst().data, e.getSecond().data), e.getFirst().data, e.getSecond().data);
		}
		
		qtLBFSNoHeuristic<Integer> search = new qtLBFSNoHeuristic<Integer>();
		
		//search.flattenAndReverseDegPrint(search.degSequenceOrder(rtn));
		
//		//depth 9
//		System.out.println("Depth 9: ");
//		search.flattenAndReverseDegPrint(search.degSequenceOrder(rtn, list.get(9)));
//		
//		//depth 10
//		System.out.println("Depth 10: ");
//		search.flattenAndReverseDegPrint(search.degSequenceOrder(rtn, list.get(10)));
//		
//		//depth 11
//		System.out.println("Depth 11: ");
//		search.flattenAndReverseDegPrint(search.degSequenceOrder(rtn, list.get(11)));
//		
//		//depth 12
//		System.out.println("Depth 12: ");
//		search.flattenAndReverseDegPrint(search.degSequenceOrder(rtn, list.get(12)));
//		
//		
		//rtn.removeVertex(0);

		
		return rtn;
	}
	
	
	//my vertex data for tree random graph
	class myVertex
	{
		myVertex parent;
		Integer data;
		Integer depth;
		
		myVertex(Integer d, myVertex p, Integer dep)
		{
			parent = p;
			data = d;
			depth = dep;
		}
		
		@Override
		public String toString()
		{
			return data.toString();
		}
	}
	
	
	public LinkedList<myEdge<Integer>> karateSolution()
	{
		LinkedList<myEdge<Integer>> l = new LinkedList<myEdge<Integer>>();
		//deletions
		l.add(new myEdge<Integer>(new Pair<Integer>(19, 2), false));
		l.add(new myEdge<Integer>(new Pair<Integer>(2, 4), false));
		l.add(new myEdge<Integer>(new Pair<Integer>(22, 3), false));
		l.add(new myEdge<Integer>(new Pair<Integer>(18, 22), false));
		l.add(new myEdge<Integer>(new Pair<Integer>(11, 18), false));
		l.add(new myEdge<Integer>(new Pair<Integer>(14, 2), false));
		l.add(new myEdge<Integer>(new Pair<Integer>(2, 3), false));
		l.add(new myEdge<Integer>(new Pair<Integer>(12, 10), false));
		l.add(new myEdge<Integer>(new Pair<Integer>(11, 14), false));
		l.add(new myEdge<Integer>(new Pair<Integer>(2, 15), false));
		l.add(new myEdge<Integer>(new Pair<Integer>(17, 23), false));
		l.add(new myEdge<Integer>(new Pair<Integer>(13, 16), false));
		l.add(new myEdge<Integer>(new Pair<Integer>(12, 18), false));
		l.add(new myEdge<Integer>(new Pair<Integer>(20, 32), false));
		l.add(new myEdge<Integer>(new Pair<Integer>(20, 35), false));
		l.add(new myEdge<Integer>(new Pair<Integer>(33, 26), false));
		
		
		//additions
		l.add(new myEdge<Integer>(new Pair<Integer>(19, 15), true));
		l.add(new myEdge<Integer>(new Pair<Integer>(19, 14), true));
		l.add(new myEdge<Integer>(new Pair<Integer>(31, 23), true));
		l.add(new myEdge<Integer>(new Pair<Integer>(33, 22), true));
		l.add(new myEdge<Integer>(new Pair<Integer>(29, 27), true));
		
		return l;
		
	}
	
}
	
