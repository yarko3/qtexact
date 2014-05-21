/**
 * Yaroslav Senyuta
 * NSERC USRA Grant (2014)
 */

package qtUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class genericLBFS<V> {
	
	/** LexBFS search 
	 * 
	 * @param G graph to be tested
	 * @param t initial ordering of vertices
	 * @return final LexBFS ordering of vertices
	 */
	public lexReturnC<V> qtLexBFSComponents(Graph<V, Pair<V>> G, ArrayList<V> t)
	{
		/*ArrayList<V> t = orderVerticesNonDecreasingDegree(G); */
		
		
		//new ordering
		ArrayList<V> s = new ArrayList<V>(t.size());
		//list of partitions (each partition has a common label)
		ArrayList<ArrayList<V>> L = new ArrayList<ArrayList<V>>(0);
		
		
		
		//initial element of L has all vertices
		L.add(t);
		int tsize = t.size();
		
		//get connected components
		LinkedList<HashSet<V>> cComponents = new LinkedList<HashSet<V>>();
		
		
		//flag for whether the graph is QT and a forbidden P4 or C4
		boolean isQT = true;
		tpCertificateC<V> forbidden = null;
		
		//for every vertex, ordered by t
		for (int i = 0; i < tsize; i++)
		{
			//clean up L
			while (L.get(0).isEmpty())
			{
				L.remove(0);
			}
			
			//get first element x of first partition
			ArrayList<V> p1 = L.get(0);
			V x = p1.remove(0);
			
			
			//if first partition is empty, remove partition from L (haha, nope)
			/*if (p1.isEmpty())
			{
				L.remove(0);
			}*/
			
			
			//add x to s at i
			s.add(i, x);
			
			//neighbours of x
			
			ArrayList<V> hood = orderNeighbour(G, x);
			
			
			//find connected components
			boolean found = false;
			search:
			for (HashSet<V> j : cComponents)
			{
				for (V n : hood)
				{
					//if a neighbour of x or x is already in a set, add the rest
					if (j.contains(n) || j.contains(x))
					{
						j.addAll(hood);
						j.add(x);
						found = true;
						break search;
					}
				}
			}
			//elements were not found in current set of connected components, make new one
			if (!found)
			{
				cComponents.add(new HashSet<V>());
				cComponents.getLast().addAll(hood);
				cComponents.getLast().add(x);
			}
			
			
			
			//usually start j from 1, unless 1 element in L
			int j = 0;
			
			while (j < L.size())
			{
				//new partition to be inserted into L
				ArrayList<V> pp = new ArrayList<V>(0);
				for (int k = 0; k < hood.size(); k++)
				{
					V h = hood.get(k);
					if (L.get(j).contains(h))
					{
						//remove element from L and add to pp
						pp.add(L.get(j).remove(L.get(j).indexOf(h)));
					}
				}
				//quasi-threshold check (should return C4 or P4)
				if (isQT && j != 0 && !pp.isEmpty())
				{
					isQT = false;
					forbidden = TPCertificate(G, x, pp.get(0), s);
				}
				if (!pp.isEmpty())
				{
					L.add(j, pp);
					j++;
				}
				j++;
					
			}
		}
		//return search results
		if (isQT)
		{
			return new lexReturnC<V>(s, null, true, cComponents.size() == 1, cComponents);
		}
		else
		{
			return new lexReturnC<V>(null, forbidden, false, cComponents.size() == 1, cComponents);
		}
	}
	
	public lexReturnC<V> qtLexBFS(Graph<V, Pair<V>> G, ArrayList<V> t)
	{
		/*ArrayList<V> t = orderVerticesNonDecreasingDegree(G); */
		
		
		//new ordering
		ArrayList<V> s = new ArrayList<V>(t.size());
		//list of partitions (each partition has a common label)
		ArrayList<ArrayList<V>> L = new ArrayList<ArrayList<V>>(0);
		
		
		
		//initial element of L has all vertices
		L.add(t);
		int tsize = t.size();
		
		
		
		//for every vertex, ordered by t
		for (int i = 0; i < tsize; i++)
		{
			//clean up L
			while (L.get(0).isEmpty())
			{
				L.remove(0);
			}
			
			//get first element x of first partition
			ArrayList<V> p1 = L.get(0);
			V x = p1.remove(0);
			
			
			//if first partition is empty, remove partition from L (haha, nope)
			/*if (p1.isEmpty())
			{
				L.remove(0);
			}*/
			
			
			//add x to s at i
			s.add(i, x);
			
			//neighbours of x
			
			ArrayList<V> hood = orderNeighbour(G, x);
			
			
		
			int j = 0;
			
			while (j < L.size())
			{
				//new partition to be inserted into L
				ArrayList<V> pp = new ArrayList<V>(0);
				for (int k = 0; k < hood.size(); k++)
				{
					V h = hood.get(k);
					if (L.get(j).contains(h))
					{
						//remove element from L and add to pp
						pp.add(L.get(j).remove(L.get(j).indexOf(h)));
					}
				}
				//quasi-threshold check (should return C4 or P4)
				if (j != 0 && !pp.isEmpty())
				{
					
					return new lexReturnC<V>(null, TPCertificate(G, x, pp.get(0), s), false, false, null);
				}
				if (!pp.isEmpty())
				{
					L.add(j, pp);
					j++;
				}
				j++;
					
			}
		}
		//return search results
		return new lexReturnC<V>(s, null, false, false, null);
	}
	
	
	private ArrayList<V> orderNeighbour(Graph<V, Pair<V>> G, V neighbour)
	{
		//return variable
		ArrayList<V> ordered = new ArrayList<V>(0);
		
		//throw all vertices into priority queue then get the order back out
		PriorityQueue<vertexIn<V>> pQueue = new PriorityQueue<vertexIn<V>>();
		for (V n : G.getNeighbors(neighbour))
		{
			pQueue.add(new vertexIn<V>(n, G.degree(n)));
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
	 * @return ordered ArrayList<V> of vertices
	 */
	public ArrayList<V> orderVerticesNonDecreasingDegree(Graph<V, Pair<V>> G)
	{
		//return variable
		ArrayList<V> ordered = new ArrayList<V>(0);
		
		//throw all vertices into priority queue then get the order back out
		PriorityQueue<vertexIn<V>> pQueue = new PriorityQueue<vertexIn<V>>();
		for (V n : G.getVertices())
		{
			pQueue.add(new vertexIn<V>(n, G.degree(n)));
		}
		while (!pQueue.isEmpty())
		{
			ordered.add(pQueue.remove().getVertex());
		}
		
		//System.out.println("Ordered list: " + ordered);
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
	private tpCertificateC<V> TPCertificate(Graph<V, Pair<V>> G, V x, V y, ArrayList<V> s)
	{
		ArrayList<V> S = new ArrayList<V>(0);
		for (V v : s)
		{
			if (s.indexOf(v) < s.indexOf(x) && G.findEdge(v, x) != null && (G.findEdge(v, y) == null))
			{
				S.add(v);
			}
			Collection<V> vertices = G.getVertices();
			for (V w : S)
			{
				for (V z : vertices)
				{
					if (G.findEdge(z, w) != null && G.findEdge(z, x) == null && z != x && z != y & z != w)
					{
						if (G.findEdge(z, y) != null)
						{
							//System.out.println("Found C4: " + z + "-" + w + "-" + x + "-" + y);
							ArrayList<V> rtn = new ArrayList<V>(0);
							rtn.add(z);
							rtn.add(w);
							rtn.add(x);
							rtn.add(y);
							return new tpCertificateC<V>(rtn, -1);
						}
						else
						{
							//System.out.println("Found P4: " + z + "-" + w + "-" + x + "-" + y);
							ArrayList<V> rtn = new ArrayList<V>(0);
							rtn.add(z);
							rtn.add(w);
							rtn.add(x);
							rtn.add(y);
							return new tpCertificateC<V>(rtn, -2);
						}
					}
				}
			}
		}
		System.out.println("Certificate failed ");
		return null;
		
	}

}

