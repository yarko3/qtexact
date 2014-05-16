package qtUtils;


import java.util.ArrayList;
import java.util.LinkedList;

import edu.uci.ics.jung.graph.Graph;

//a class to keep the return graph and the number of alterations that have been made
public class branchingReturnC implements Comparable<branchingReturnC>
{
	Graph<Integer, String> G;
	int changes;
	ArrayList<LinkedList<Integer>> deg;
	
	public branchingReturnC(Graph<Integer, String> graph, ArrayList<LinkedList<Integer>> d, int c)
	{
		G = graph;
		changes = c;
		deg = d;
	}

	@Override
	public int compareTo(branchingReturnC o) {
		return Integer.compare(changes, o.changes);
	}
	
	public Graph<Integer, String> getG(){
		return G;
	}
	
	public int getChanges()
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
	public void setChanges(int c)
	{
		changes = c;
	}
	public void setDeg(ArrayList<LinkedList<Integer>> d)
	{
		deg = d;
	}
	
}
