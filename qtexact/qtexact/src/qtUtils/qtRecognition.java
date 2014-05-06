package qtUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.PriorityQueue;

import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class qtRecognition 
{
	/**
	 * 
	 * @param Graph G
	 * @return DelegateForest
	 */
	public static DelegateForest<Integer, String> qtCheckYan(Graph<Integer, String> G)
	{
		//create a rooted forest representation F of G
		DelegateForest<Integer, String> F = new DelegateForest<Integer, String>();
		
		Collection<Integer> vertices = G.getVertices();
		
		//add the G vertex set to F with no edges
		for (int i : vertices)
		{
			F.addVertex(i);
		}
		
		//array of vertices
		ArrayList<Integer> vertexArrayList = new ArrayList<Integer>();
		vertexArrayList.addAll(vertices);
		
		//initialize inDegree hashtable
		Hashtable<Integer, Integer> inDegree = new Hashtable<Integer, Integer>();
		for (int i : vertices)
		{
			inDegree.put(i, 0);
		}
		
		//for each edge, increment inDegree
		for (String e : G.getEdges())
		{
			Pair<Integer> endpoints = G.getEndpoints(e);
			if ((G.degree(endpoints.getFirst()) > G.degree(endpoints.getSecond())) || ((G.degree(endpoints.getFirst()) == G.degree(endpoints.getSecond())) && (vertexArrayList.indexOf(endpoints.getFirst()) < vertexArrayList.indexOf(endpoints.getSecond()))))
				inDegree.put(endpoints.getSecond(), inDegree.get(endpoints.getSecond()) + 1);
			else
				inDegree.put(endpoints.getFirst(), inDegree.get(endpoints.getFirst()) + 1);
		}

		for (int j = 0; j < vertexArrayList.size(); j++)
		{
			if (inDegree.get(vertexArrayList.get(j)) >= 1)
			{
				//find neighbours of vertexArray[j]
				Collection<Integer> neighbours = G.getNeighbors(vertexArrayList.get(j));
				//create neighbour priority queue based on their in degree
				PriorityQueue<vertexIn<Integer>> pQueue = new PriorityQueue<vertexIn<Integer>>();
				for (int n : neighbours)
				{
					pQueue.add(new vertexIn<Integer>(n, inDegree.get(n)));
				}
				//choose a neighbour that fits criteria
				Iterator<vertexIn<Integer>> iterator = pQueue.iterator();
				boolean finish = false;
				while (iterator.hasNext() && !finish)
				{
					vertexIn<Integer> next = iterator.next();
					//add edge to F
					if ((G.degree(next.getVertex()) > vertexArrayList.get(j)) || 
							((G.degree(next.getVertex()) == G.degree(vertexArrayList.get(j))) && (vertexArrayList.indexOf(next.getVertex()) < j)))
					{
						F.addEdge("e:" +  next.getVertex() + "-" + vertexArrayList.get(j), next.getVertex(), vertexArrayList.get(j));
						finish = true;
					}
				}
			}
		}
		
		//check if number of ancestors of each vertex == in degree
		boolean check = true;
		for (int j : vertices)
		{
			int predecessorCount = F.getPredecessorCount(j);
			if (predecessorCount != inDegree.get(j)) 
				check = false;
		}
		if (check == true)
			return F;
		else
			return null;
		
	}
}
	
//class containing vertex and inDegree for PriorityQueue in qtCheckYan
class vertexIn<V> implements Comparable<vertexIn<V>>
{
	private V vertex;
	private int inDegree;
	vertexIn(V v, int in)
	{
		vertex = v;
		inDegree = in;
	}
	
	public V getVertex(){return vertex;};
	public int getInDegree(){return inDegree;};
	public int compareTo(vertexIn<V> v)
	{
		return Integer.compare(inDegree, v.getInDegree());
	}
}
