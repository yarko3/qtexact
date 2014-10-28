package utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;

import com.rits.cloning.Cloner;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class graphUtils<V> 
{
	WeakComponentClusterer<V, Pair<V>> cluster = new WeakComponentClusterer<V, Pair<V>>();
	
	static Cloner clone = new Cloner();
	
	/**
	 * generate complement graph 
	 * @param g input graph
	 * @return undirected complement graph
	 */
	public Graph<V, Pair<V>> complement(Graph<V, Pair<V>> g)
	{
		Graph<V, Pair<V>> comp = new UndirectedSparseGraph<V, Pair<V>>();
		
		//add all vertices
		for (V v : g.getVertices())
		{
			comp.addVertex(v);
		}
		
		for (V v0 : g.getVertices())
		{
			for (V v1 : g.getVertices())
			{
				if (v0.equals(v1))
					continue;
				
				if (!g.isNeighbor(v0, v1))
				{
					comp.addEdge(new Pair<V>(v0, v1), v0, v1);
				}
			}
		}
		
		return comp;
	}
	
	/**
	 * output graph to a file specified (untouched edges have a weight of 2, edge deletions have a weight of 1 and edge additions have a weight of 3
	 * @param s finished graph traversal to be outputed
	 * @param filename name of file to be saved to
	 */
	public void printGMLWithEdits(branchingReturnC<V> s, String filename)
	{
		//get vertex ids
		HashMap<V, Integer> ids = new HashMap<V, Integer>();
		
		Iterator<V> iterator = s.getG().getVertices().iterator();
		
		int i = 0;
		while (iterator.hasNext())
		{
			ids.put(iterator.next(), i);
			i++;
		}
		
		//print network to file
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(filename, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// write intro with weights
		writer.println("graph [");
		
		
		if (s.getG() instanceof DirectedGraph)
			writer.println("\tdirected 1");
		else
			writer.println("\tdirected 0");
		writer.println("\tweighted 1");
		
		
		//TODO remember to close graph tag
		
		//print vertices
		for (V v : s.getG().getVertices())
		{
			writer.println("\tnode [");
			writer.println("\t\tid " + ids.get(v));
			writer.println("\t\tlabel \"" + v + "\"");
			writer.println("\t]");
		}
		
		//print edges
		for (Pair<V> e : s.getG().getEdges())
		{
			int weight = 1;
			//change weight if edge was added later
			if (s.getMinMoves().getChanges().contains(new myEdge<V>(e, true, s.getG() instanceof DirectedGraph)))
			{
				weight = 2;
			}
			writer.println("\tedge [");
			writer.println("\t\tsource " + ids.get(e.getFirst()));
			writer.println("\t\ttarget " + ids.get(e.getSecond()));
			writer.println("\t\tgraphics [");
			if (weight == 1)
				writer.println("\t\t\twidth 1");
			else
				writer.println("\t\t\twidth 3");
			writer.println("\t\t]");
			
			writer.println("\t\tweight " + weight);
			writer.println("\t]");
			
		}
		
		for (myEdge<V> e : s.getMinMoves().getChanges())
		{
			int weight = -1;
			//write edge deletions
			if (!e.isFlag())
			{
				writer.println("\tedge [");
				writer.println("\t\tsource " + ids.get(e.getEdge().getFirst()));
				writer.println("\t\ttarget " + ids.get(e.getEdge().getSecond()));
				writer.println("\t\tgraphics [");
				
				writer.println("\t\tstyle \"dashed\"");
				writer.println("\t\t]");
				
				writer.println("\t\tweight " + weight);
				writer.println("\t]");
			}
		}
		
		writer.println("]");
		
		writer.close();
	}
	
	
	public void printGMLWithAdditions(branchingReturnC<V> s, String filename)
	{
		//get vertex ids
		HashMap<V, Integer> ids = new HashMap<V, Integer>();
		
		Iterator<V> iterator = s.getG().getVertices().iterator();
		
		int i = 0;
		while (iterator.hasNext())
		{
			ids.put(iterator.next(), i);
			i++;
		}
		
		//print network to file
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(filename, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// write intro with weights
		writer.println("graph [");
		
		
		if (s.getG() instanceof DirectedGraph)
			writer.println("\tdirected 1");
		else
			writer.println("\tdirected 0");
		writer.println("\tweighted 1");
		
		
		//TODO remember to close graph tag
		
		//print vertices
		for (V v : s.getG().getVertices())
		{
			writer.println("\tnode [");
			writer.println("\t\tid " + ids.get(v));
			writer.println("\t\tlabel \"" + v + "\"");
			writer.println("\t]");
		}
		
		//print edges
		for (Pair<V> e : s.getG().getEdges())
		{
			int weight = 1;
			//change weight if edge was added later
			if (s.getMinMoves().getChanges().contains(new myEdge<V>(e, true, s.getG() instanceof DirectedGraph)))
			{
				weight = 2;
			}
			writer.println("\tedge [");
			writer.println("\t\tsource " + ids.get(e.getFirst()));
			writer.println("\t\ttarget " + ids.get(e.getSecond()));
			writer.println("\t\tgraphics [");
			if (weight == 1)
				writer.println("\t\t\twidth 1");
			else
				writer.println("\t\t\twidth 3");
			writer.println("\t\t]");
			
			writer.println("\t\tweight " + weight);
			writer.println("\t]");
			
		}
		
		writer.println("]");
		
		writer.close();
	}
	
	public Graph<V, Pair<V>> inducedFromVertexSet(Graph<V, Pair<V>> G, Set<V> l)
	{
		Graph<V, Pair<V>> c = null;
		try {
			c = (Graph<V, Pair<V>>) G.getClass().newInstance();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
	
	public void printSolutionEdgeSetWithWeights(branchingReturnC<V> s, String filename)
	{
		//print network to file
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(filename, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (Pair<V> e : s.getG().getEdges())
		{
			int weight = 1;
			//change weight if edge was added later
			if (s.getMinMoves().getChanges().contains(new myEdge<V>(e, true, s.getG() instanceof DirectedGraph)))
			{
				weight = 2;
			}
			writer.println(e.getFirst() + " " + e.getSecond() + " " + weight);
			
		}
		
		for (myEdge<V> e : s.getMinMoves().getChanges())
		{
			int weight = -1;
			//write edge deletions
			if (!e.isFlag())
			{
				writer.println(e.getEdge().getFirst() + " " + e.getEdge().getSecond() + " " + weight);
			}
		}
		
		writer.close();
		
		
	}
	
	public void printSolutionEdgeSetWithWeightsComponents(branchingReturnC<V> s, String filename)
	{
		Set<Set<V>> sets = cluster.transform(s.getG());
		
		int componentCount = 0;
		HashMap<V, Integer> componentLabels = new HashMap<V, Integer>();
		
		
		for (Set<V> c : sets)
		{
			for (V v : c)
			{
				componentLabels.put(v, componentCount);
			}
			
			componentCount++;
		}
		
		//print network to file
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(filename, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (Pair<V> e : s.getG().getEdges())
		{
			int weight = 1;
			//change weight if edge was added later
			if (s.getMinMoves() != null && s.getMinMoves().getChanges().contains(new myEdge<V>(e, true, s.getG() instanceof DirectedGraph)))
			{
				weight = 2;
			}
			writer.println(e.getFirst() + " " + e.getSecond() + " " + weight + " " + componentLabels.get(e.getFirst()));
			
		}
		
		if (s.getMinMoves() != null)
		{
			for (myEdge<V> e : s.getMinMoves().getChanges())
			{
				int weight = -1;
				//write edge deletions
				if (!e.isFlag())
				{
					writer.println(e.getEdge().getFirst() + " " + e.getEdge().getSecond() + " " + weight + " " + componentLabels.get(e.getEdge().getFirst()));
				}
			}
		}
		
		writer.close();
		
		
	}
	
	public static Graph<String, Pair<String>> graphFromFile(
			String filename) 
	{
		
		Graph<String, Pair<String>> graph = new UndirectedSparseGraph<String, Pair<String>>();
		FileReader file = null;
		try {
			file = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}

		Scanner scan = new Scanner(file);

		while (scan.hasNext()) {
			
			
			String a = scan.next();
			
			
			//for .tgf edge sets
			if (a.equals("#"))
			{
				continue;
			}
			
			String b = scan.next();
			
			
			graph.addEdge(new Pair<String>(a, b), a, b);
			
		}
		try {
			scan.close();
			file.close();
		} catch (IOException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}
		
		return graph;
	}
	
	public void printEdgeSet(Graph<V, Pair<V>> g, String filename)
	{
		
		//print network to file
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(filename, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (Pair<V> e :g.getEdges())
		{
			
			writer.println(e.getFirst() + "\t" + e.getSecond());
			
		}
		
		
		writer.close();
		
		
	}
	
	public static PriorityQueue<Pair<Set<String>>> wineriesWithKExternals(String filename, int k)
	{
		FileReader file = null;
		try {
			file = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}

		//store externals here, based on key
		HashMap<String, HashSet<String>> edges = new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> wineryEdges = new HashMap<String, HashSet<String>>();
		Scanner scan = new Scanner(file);
		String winery;
		String external;
		while (scan.hasNext())
		{
			winery = scan.next();
			external = scan.next();
			
			if (!edges.containsKey(external))
			{
				edges.put(external, new HashSet<String>());
			}
			
			if (!wineryEdges.containsKey(winery))
				wineryEdges.put(winery, new HashSet<String>());
			
			wineryEdges.get(winery).add(external);
			
			edges.get(external).add(winery);	
		}
	
		scan.close();
		
		//remove all wineries with less than k externals
		for (String key : wineryEdges.keySet())
		{
			if (wineryEdges.get(key).size() < k)
			{
				for (String ex : wineryEdges.get(key))
				{
					edges.get(ex).remove(key);
					if (edges.get(ex).isEmpty())
						edges.remove(ex);
				}
			}
		}
		
		
		//remove all externals with less than 2 wineries
		LinkedList<String> toDelete = new LinkedList<String>();

		for (String key : edges.keySet())
		{
			if (edges.get(key).size() < 2)
				toDelete.add(key);
		}
		
		for (String key : toDelete)
		{
			edges.remove(key);
		}
	
		HashMap<Set<String>, Set<String>> kMap = new HashMap<Set<String>, Set<String>>();
		Set<String> temp;
		Set<String> c;
		
		for (int i = 0; i < k; i++)
		{
			System.out.println("At k" + i);
			if (i == 0)
			{
				for (String key : edges.keySet())
				{
					HashSet<String> keySet = new HashSet<String>();
					keySet.add(key);
					
					kMap.put(keySet, clone.deepClone(edges.get(key)));
				}
			}
			else
			{
				//make new kMap to replace with
				HashMap<Set<String>, Set<String>> newKMap = new HashMap<Set<String>, Set<String>>();
				
				int max = 2;
				
				for (Set<String> next : kMap.keySet())
				{
					//get common wineries to this set of keys
					temp = null;
					
					for (String s0 : next)
					{
						if (temp == null)
						{
							temp = new HashSet<String>();
							temp.addAll(edges.get(s0));
						}
						else
							temp.retainAll(edges.get(s0));
					}
					
					if (temp.isEmpty())
					{
						System.out.println("This set of externals has no wineries: \n" + next);
					}
					
					
					for (String key : edges.keySet())
					{
						if (next.contains(key))
							continue;
						
						Set<String> newKey = new HashSet<String>();
						newKey.addAll(next);
						newKey.add(key);
						
						if (newKMap.containsKey(newKey))
							continue;
						
						
						c = clone.deepClone(temp);
						c.retainAll(edges.get(key));
						
						if (c.size() >= max)
						{
							if (c.size() > max && i == k-1)
							{
								//trim all those of size < c.size()
								max = c.size();
								
								HashSet<Set<String>> tempDelete = new HashSet<Set<String>>();
								for (Set<String> tempKey : newKMap.keySet())
								{
									if (newKMap.get(tempKey).size() < max)
									{
										tempDelete.add(tempKey);
									}
								}
								
								for (Set<String> tempKey : tempDelete)
								{
									newKMap.remove(tempKey);
								}
							}
							
							
							//add this key to newKMap
							
							newKMap.put(newKey, c);
							//System.out.println(newKey + "\t" + c);
						}
					}
				}
				
				kMap = newKMap;
			}
		}
		
		HashMap<Set<String>, Set<String>> permMap = kMap;
		
		
		
		
		
//		PrintWriter writer = null;
//		try {
//			writer = new PrintWriter("datasets/wine/externalk"+k+"Projection.txt", "UTF-8");
//		} catch (FileNotFoundException | UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		
		//order them by non-increasing number of wineries
		PriorityQueue<Pair<Set<String>>> pq = new PriorityQueue<Pair<Set<String>>>(new pairComparator());
				
		
		for (Set<String> s : permMap.keySet())
		{
//			for (String t : s)
//				writer.print(t + " ");
//			writer.print("\t");
//			for (String t : permMap.get(s))
//				writer.print(t + " ");
//			
//			writer.print("\t" + permMap.get(s).size() + "\n");
			
			pq.add(new Pair<Set<String>>(s, permMap.get(s)));
		}
		
//		writer.close();
		
		
		//System.out.println(pq.remove().getSecond().size());
		
		return pq;

		
	}
	
	
	
	
	static class pairComparator implements Comparator<Pair<Set<String>>>
	{

		@Override
		public int compare(Pair<Set<String>> arg0, Pair<Set<String>> arg1) {
			if (arg0.getSecond().size() > arg1.getSecond().size())
				return -1;
			else if (arg0.getSecond().size() == arg1.getSecond().size())
				return 0;
			else
				return 1;
		}


		
		
	}

}
