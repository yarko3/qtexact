package qtUtils;

import java.util.Collection;

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
		for (int j = 1; j <= vertexArray.length; j++)
		{
			if (G.getInEdges(vertexArray[j]).size() >= 1)
			{
				//find neighbours of vertexArray[j]
				Collection<Integer> neighbours = G.getNeighbors(vertexArray[j]);
				
				
			}
		}
	}
	
	//class containing vertex and inDegree for PriorityQueue in qtCheckYan
	private class vertexIn<V>
	{
		private V vertex;
		private int inDegree;
		private vertexIn(V v, int in)
		{
			vertex = v;
			inDegree = in;
		}
		
		private V getVertex(){return vertex;};
		private int getInDegree(){return inDegree;};
	}
}
