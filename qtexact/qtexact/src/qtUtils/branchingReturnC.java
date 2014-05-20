package qtUtils;


import java.util.ArrayList;
import java.util.LinkedList;

import edu.uci.ics.jung.graph.Graph;

//a class to keep the return graph and the number of alterations that have been made
public class branchingReturnC implements Comparable<branchingReturnC>
{
	Graph<Integer, String> G;
	ArrayList<LinkedList<Integer>> deg;
	LinkedList<String> changes;
	
	public branchingReturnC(Graph<Integer, String> graph, ArrayList<LinkedList<Integer>> d, LinkedList<String> c)
	{
		G = graph;
		changes = c;
		deg = d;
	}
	public branchingReturnC(Graph<Integer, String> graph, ArrayList<LinkedList<Integer>> d)
	{
		G = graph;
		deg = d;
		changes = new LinkedList<String>();
	}

	@Override
	public int compareTo(branchingReturnC o) {
		return Integer.compare(changes.size(), o.changes.size());
	}
	
	public Graph<Integer, String> getG(){
		return G;
	}
	
	public LinkedList<String> getChanges()
	{
		return changes;
	}
	
	public ArrayList<LinkedList<Integer>> getDeg()
	{
		return deg;
	}
	
	public void setGraph(Graph<Integer, String> g)
	{
		G = g;
	}
	public void setChanges(LinkedList<String> c)
	{
		changes = c;
	}
	public void setDeg(ArrayList<LinkedList<Integer>> d)
	{
		deg = d;
	}
	
}
