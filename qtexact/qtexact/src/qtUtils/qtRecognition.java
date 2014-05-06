package qtUtils;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.PriorityQueue;

import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Graph;

public class qtRecognition 
{
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
		Integer[] vertexArray = (Integer[]) vertices.toArray();
		
		//create a hashtable to store visited vertices
		Hashtable<Integer, Boolean> visited = new Hashtable<Integer, Boolean>();
		for (int j = 1; j <= vertexArray.length; j++)
		{
			if (G.getInEdges(vertexArray[j]).size() >= 1)
			{
				//find neighbours of vertexArray[j]
				Collection<Integer> neighbours = G.getNeighbors(vertexArray[j]);
				//create neighbour priority queue based on their in degree
				PriorityQueue<vertexIn<Integer>> pQueue = new PriorityQueue<vertexIn<Integer>>();
				for (int n : neighbours)
				{
					pQueue.add(new vertexIn<Integer>(n, G.inDegree(n)));
				}
				//choose a neighbour that fits criteria
				Iterator<vertexIn<Integer>> iterator = pQueue.iterator();
				while (iterator.hasNext())
				{
					vertexIn<Integer> next = iterator.next();
					//add edge to F
					if ((G.degree(next.getVertex()) < vertexArray[j]) || (G.degree(next.getVertex()) == G.degree(vertexArray[j]) && visited.contains(next.getVertex())))
					{
						F.addEdge("e:" +  next.getVertex() + "-" + vertexArray[j], next.getVertex(), vertexArray[j]);
						break;
					}
				}
			}
			visited.put(vertexArray[j], true);
		}
		
		//check if number of ancestors of each vertex == in degree
		boolean check = true;
		for (int j : vertices)
		{
			if (F.getPredecessorCount(j) != G.inDegree(j)) 
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
