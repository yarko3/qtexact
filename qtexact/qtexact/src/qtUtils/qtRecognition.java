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
	 * Use Yan algorithm for QT recognition (1994)
	 * @author Yarko Senyuta
	 * @param Graph G
	 * @return DelegateForest
	 */
	public static DelegateForest<Integer, String> qtCheckYan(Graph<Integer, String> G)
	{
		//create a rooted forest representation F of G
		DelegateForest<Integer, String> F = new DelegateForest<Integer, String>();
		
		//vertices of G
		Collection<Integer> vertices = G.getVertices();
		
		//add the G vertex set to F with no edges
		for (int i : vertices)
		{
			F.addVertex(i);
		}
		
		//array list of vertices (used for indexing in algorithm)
		ArrayList<Integer> vertexArrayList = new ArrayList<Integer>(vertices.size());
		vertexArrayList.addAll(vertices);
		
		//initialize inDegree hashtable
		Hashtable<Integer, Integer> inDegree = new Hashtable<Integer, Integer>(vertices.size());
		for (int i = 0; i < vertices.size(); i++)
		{
			inDegree.put(vertexArrayList.get(i), 0);
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
		
		//for each vertex j
		for (int j = 0; j < vertexArrayList.size(); j++)
		{
			if (inDegree.get(vertexArrayList.get(j)) >= 1)
			{
				//find neighbours of vertexArray[j]
				Collection<Integer> neighbours = G.getNeighbors(vertexArrayList.get(j));
				//create neighbour priority queue based on their inDegree
				PriorityQueue<vertexIn<Integer>> pQueue = new PriorityQueue<vertexIn<Integer>>();
				for (int n : neighbours)
				{
					pQueue.add(new vertexIn<Integer>(n, inDegree.get(n)));
				}
				//choose a neighbour that fits criteria
				boolean finish = false;
				while (!pQueue.isEmpty() && !finish)
				{
					vertexIn<Integer> next = pQueue.remove();
					//add edge to F
					if ((G.degree(next.getVertex()) > G.degree(vertexArrayList.get(j))) || 
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
		for (int j = 0; j < vertexArrayList.size(); j++)
		{
			int depth = F.getDepth(vertexArrayList.get(j));
			int inDegreeval = inDegree.get(vertexArrayList.get(j));
			if (F.getDepth(vertexArrayList.get(j)) - 1 != inDegree.get(vertexArrayList.get(j))) 
				check = false;
		}
		if (check == true)
			return F;
		else
			return null;
	}
}
	
/**
 * class containing vertex and Degree for PriorityQueue in qtCheckYan
 * @author Yarko Senyuta
 *
 * @param <V>
 */
class vertexIn<V> implements Comparable<vertexIn<V>>
{
	private V vertex;
	private int degree;
	vertexIn(V v, int in)
	{
		vertex = v;
		degree = in;
	}
	
	public V getVertex(){return vertex;};
	public int getDegree(){return degree;};
	public int compareTo(vertexIn<V> v)
	{
		return Integer.compare(v.getDegree(), degree);
	}
}
