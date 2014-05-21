/**
 * Yaroslav Senyuta
 * NSERC USRA Grant (2014)
 */

package qtUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.PriorityQueue;

import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class qtRecognition<V>
{
	public DelegateForest<V, Pair<V>> qtCheckYan(Graph<V, Pair<V>> G)
	{
		//create a rooted forest representation F of G
		DelegateForest<V, Pair<V>> F = new DelegateForest<V, Pair<V>>();
		
		//vertices of G
		Collection<V> vertices = G.getVertices();
		
		//add the G vertex set to F with no edges
		for (V i : vertices)
		{
			F.addVertex(i);
		}
		
		//array list of vertices (used for indexing in algorithm)
		genericLBFS<V> sort = new genericLBFS<V>();
		ArrayList<V> vertexArrayList = sort.orderVerticesNonDecreasingDegree(G);
		
		//initialize inDegree hashtable
		Hashtable<V, Integer> inDegree = new Hashtable<V, Integer>(vertices.size());
		for (int i = 0; i < vertices.size(); i++)
		{
			inDegree.put((V) vertexArrayList.get(i), 0);
		}
		
		//for each edge, increment inDegree
		for (Pair<V> e : G.getEdges())
		{
			Pair<V> endpoints = e;
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
				Collection<V> neighbours = G.getNeighbors((V) vertexArrayList.get(j));
				PriorityQueue<vertexIn<V>> pQueue = new PriorityQueue<vertexIn<V>>();
				for (V n : neighbours)
				{
					pQueue.add(new vertexIn<V>(n, inDegree.get(n)));
				}
				
				//choose a neighbour that fits criteria
				boolean finish = false;
				while (!pQueue.isEmpty() && !finish)
				{
					vertexIn<V> next = pQueue.remove();
					
					
					//add edge to F
					
					if ((G.degree(next.getVertex()) > G.degree(vertexArrayList.get(j))) || 
							((G.degree(next.getVertex()) == G.degree(vertexArrayList.get(j))) && (vertexArrayList.indexOf(next.getVertex()) < j)))
					{
						F.addEdge(new Pair<V>( next.getVertex(), vertexArrayList.get(j)), next.getVertex(), vertexArrayList.get(j));
						finish = true;
					}
				}
			}
		}
		
		//check if number of ancestors of each vertex == in degree
		boolean check = true;
		for (int j = 0; j < vertexArrayList.size(); j++)
		{
			if (F.getDepth(vertexArrayList.get(j)) - 1 != inDegree.get(vertexArrayList.get(j))) 
				check = false;
		}
		if (check == true)
			return F;
		else
			return null;
	}
}
	
class vertexInComparator implements Comparator<vertexIn<Integer>>
{

	public int compare(vertexIn<Integer> arg0, vertexIn<Integer> arg1) 
	{
		if (arg0.getDegree() != arg1.getDegree())
		{
			return Integer.compare(arg0.getDegree(), arg1.getDegree());
		}
		else
		{
			return Integer.compare((Integer) arg0.getVertex(), (Integer) arg1.getVertex());
		}
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
