package utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class graphUtils<V> 
{
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
	public void printGMLWithWeights(branchingReturnC<V> s, String filename)
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
		
//		for (myEdge<V> e : s.getMinMoves().getChanges())
//		{
//			int weight = 1;
//			//write edge deletions
//			if (!e.isFlag())
//			{
//				writer.println("\tedge [");
//				writer.println("\t\tsource " + ids.get(e.getEdge().getFirst()));
//				writer.println("\t\ttarget " + ids.get(e.getEdge().getSecond()));
//				writer.println("\t\tgraphics [");
//				
//				writer.println("\t\tstyle \"dashed\"");
//				writer.println("\t\t]");
//				
//				writer.println("\t\tweight " + weight);
//				writer.println("\t]");
//			}
//		}
		
		writer.println("]");
		
		writer.close();
	}

}
