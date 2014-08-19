/**
 * Yaroslav Senyuta
 * NSERC USRA Grant (2014)
 */

package search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.PriorityQueue;

import qtUtils.branchingReturnC;
import qtUtils.vertexIn;
import abstractClasses.Search;
import abstractClasses.SearchResult;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * a class containing the search for quasi threshold graphs 
 * Yan 1995
 * @author Yaroslav Senyuta
 *
 * @param <V> vertex
 */
public class YanSearch<V> extends Search<V>
{
	/**
	 * returns true if graph given is quasi threshold, false otherwise
	 * @param G graph
	 * @return true if QT
	 */
	public boolean search(Graph<V, Pair<V>> G)
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
		qtLBFS<V> sort = new qtLBFSNoHeuristic<V>();
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
			return true;
		else
			return false;
	}

	@Override
	public boolean isTarget(Graph<V, Pair<V>> g) {
		return search(g);
	}

	@Override
	protected SearchResult<V> searchPrep(branchingReturnC<V> s) {
		return search(s);
	}

	@Override
	public SearchResult<V> search(branchingReturnC<V> s) {
		return new SearchResult<V>(search(s.getG()), null);
	}
}
	
