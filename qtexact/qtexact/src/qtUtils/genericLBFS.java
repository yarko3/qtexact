package qtUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

import edu.uci.ics.jung.graph.Graph;

public class genericLBFS {
	
	/** LexBFS search 
	 * 
	 * @param G graph to be tested
	 * @param t initial ordering of vertices
	 * @return final LexBFS ordering of vertices
	 */
	public static ArrayList<Integer> genericLexBFS(Graph<Integer, String> G)
	{
		ArrayList<Integer> t = orderVerticesNonDecreasingDegree(G);
		
		//new ordering
		ArrayList<Integer> s = new ArrayList<Integer>(t.size());
		//list of partitions (each partition has a common label)
		ArrayList<ArrayList<Integer>> L = new ArrayList<ArrayList<Integer>>(0);
		
		
		
		//initial element of L has all vertices
		L.add(t);
		int tsize = t.size();
		//for every vertex, ordered by t
		for (int i = 0; i < tsize; i++)
		{
			while (L.get(0).isEmpty())
			{
				L.remove(0);
			}
			//get first element x of first partition
			ArrayList<Integer> p1 = L.get(0);
			int x = p1.remove(0);
			//if first partition is empty, remove partition from L (haha, nope)
			/*if (p1.isEmpty())
			{
				L.remove(0);
			}*/
			
			//add x to s at i
			s.add(i, x);
			
			//neighbours of x
			
			ArrayList<Integer> hood = orderNeighbour(G, x);
			
			//usually start j from 1, unless 1 element in L
			int j = 1;
			if (L.size() == 1)
			{
				j = 0;
			}
			
			while (j < L.size())
			{
				//new partition to be inserted into L
				ArrayList<Integer> pp = new ArrayList<Integer>(0);
				ArrayList<Integer> pj = L.get(j);
				
				
				for (int k = 0; k < hood.size(); k++)
				{
					int h = hood.get(k);
					if (L.get(j).contains(h))
					{
						//remove element from L and add to pp
						pp.add(L.get(j).remove(L.get(j).indexOf(h)));
					}
				}
				//quasi-threshold check (should return C4 or P4)
				if (j != 0 && !pp.isEmpty())
				{
					return TPCertificate(G, x, pp.get(0), s);
				}
				if (!pp.isEmpty())
				{
					L.add(j, pp);
					j++;
				}
				j++;
					
			}
		}
		
		return s;
	}
	
	
	private static ArrayList<Integer> orderNeighbour(Graph<Integer, String> G, int neighbour)
	{
		//return variable
		ArrayList<Integer> ordered = new ArrayList<Integer>(0);
		
		//throw all vertices into priority queue then get the order back out
		PriorityQueue<vertexIn<Integer>> pQueue = new PriorityQueue<vertexIn<Integer>>();
		for (int n : G.getNeighbors(neighbour))
		{
			pQueue.add(new vertexIn<Integer>(n, G.degree(n)));
		}
		while (!pQueue.isEmpty())
		{
			ordered.add(pQueue.remove().getVertex());
		}
		
		return ordered;
	}
	
	/**Order the vertices in non-decreasing degrees for LexBFS
	 * 
	 * @param G graph
	 * @return ordered ArrayList<Integer> of vertices
	 */
	private static ArrayList<Integer> orderVerticesNonDecreasingDegree(Graph<Integer, String> G)
	{
		//return variable
		ArrayList<Integer> ordered = new ArrayList<Integer>(0);
		
		//throw all vertices into priority queue then get the order back out
		PriorityQueue<vertexIn<Integer>> pQueue = new PriorityQueue<vertexIn<Integer>>();
		for (int n : G.getVertices())
		{
			pQueue.add(new vertexIn<Integer>(n, G.degree(n)));
		}
		while (!pQueue.isEmpty())
		{
			ordered.add(pQueue.remove().getVertex());
		}
		
		System.out.println("Ordered list: " + ordered);
		return ordered;

	}
	/**get C4 or P4 from graph
	 * 
	 * @param G graph
	 * @param x first element of partition retrieved/last element of s
	 * @param y first element of P' 
	 * @param s ordering until the C4 or P4 was found
	 * @return
	 */
	private static ArrayList<Integer> TPCertificate(Graph<Integer, String> G, int x, int y, ArrayList<Integer> s)
	{
		ArrayList<Integer> S = new ArrayList<Integer>(0);
		for (int v : s)
		{
			if (s.indexOf(v) < s.indexOf(x) && G.findEdge(v, x) != null && (G.findEdge(v, y) == null))
			{
				S.add(v);
			}
			Collection<Integer> vertices = G.getVertices();
			for (int w : S)
			{
				for (int z : vertices)
				{
					if (G.findEdge(z, w) != null && G.findEdge(z, x) == null && z != x && z != y & z != w)
					{
						if (G.findEdge(z, y) != null)
						{
							System.out.println("Found C4: " + z + "-" + w + "-" + x + "-" + y);
							ArrayList<Integer> rtn = new ArrayList<Integer>(0);
							rtn.add(z);
							rtn.add(w);
							rtn.add(x);
							rtn.add(y);
							return rtn;
						}
						else
						{
							System.out.println("Found P4: " + z + "-" + w + "-" + x + "-" + y);
							ArrayList<Integer> rtn = new ArrayList<Integer>(0);
							rtn.add(z);
							rtn.add(w);
							rtn.add(x);
							rtn.add(y);
							return rtn;
						}
					}
				}
			}
		}
		
		return null;
		
	}

}
