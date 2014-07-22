/**
 * Yaroslav Senyuta
 * NSERC USRA Grant (2014)
 */

package search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.PriorityQueue;

import qtUtils.branchingReturnC;
import qtUtils.lexReturnC;
import qtUtils.vertexIn;
import certificate.qtCertificateC;

import com.rits.cloning.Cloner;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * an abstract class that provides helper methods for qtLBFS searches
 * 
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public abstract class qtLBFS<V> extends LBFS<V> 
{
	public static Cloner clone = new Cloner();

	@Override
	public boolean isTarget(Graph<V, Pair<V>> g) {
		return isQT(g);
	}
	
	public lexReturnC<V> searchPrep(branchingReturnC<V> s)
	{
		ArrayList<V> t = flattenAndReverseDeg(s.getDeg());
		return search(s.getG(), t);
	}
	
	public lexReturnC<V> search(branchingReturnC<V> s)
	{
		return searchPrep(s);
	}
	
	protected ArrayList<V> orderNeighbour(Graph<V, Pair<V>> G, V neighbour)
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
		
		
		//return flattenAndReverseDeg(degSequenceOrder(G, G.getNeighbors(neighbour)));
		
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
	protected qtCertificateC<V> qtCertificate(Graph<V, Pair<V>> G, V x, V y, ArrayList<V> s)
	{
		ArrayList<V> S = new ArrayList<V>(0);
		for (V v : s)
		{
			if (s.indexOf(v) < s.indexOf(x) && G.isNeighbor(v, x) && !G.isNeighbor(v, y))
			{
				S.add(v);
			}
			
			
			Collection<V> vertices = G.getVertices();
			
			//Collection<V> vertices = flattenAndReverseDeg(degSequenceOrder(G));
			for (V w : S)
			{
				for (V z : vertices)
				{
					if (G.isNeighbor(z, w) && !G.isNeighbor(z, x) && !z.equals(x) && !z.equals(y) && !z.equals(w))
					{
						if (G.isNeighbor(z, y))
						{
							//System.out.println("Found C4: " + z + "-" + w + "-" + x + "-" + y);
							ArrayList<V> rtn = new ArrayList<V>(0);
							rtn.add(z);
							rtn.add(w);
							rtn.add(x);
							rtn.add(y);
							return new qtCertificateC<V>(rtn, -1);
						}
						else
						{
							//System.out.println("Found P4: " + z + "-" + w + "-" + x + "-" + y);
							ArrayList<V> rtn = new ArrayList<V>(0);
							rtn.add(z);
							rtn.add(w);
							rtn.add(x);
							rtn.add(y);
							return new qtCertificateC<V>(rtn, -2);
						}
					}
				}
			}
		}
		System.out.println("Certificate failed ");
		return null;
		
	}
	/**
	 * Compute an ArrayList where every index holds a LinkedList of vertices with degrees of index
	 * @param G graph
	 * @return degree set
	 */
	public ArrayList<LinkedList<V>> degSequenceOrder(Graph<V, Pair<V>> G)
	{
		//store vertices of same degree in LinkedList<V> at the index of their degree in ArrayList
		ArrayList<LinkedList<V>> deg = new ArrayList<LinkedList<V>>();
		int max = 0;
		for (V i : G.getVertices())
		{
			if (G.degree(i) > max)
				max = G.degree(i);
		}
		
		//initialize LinkedList for each degree
		for (int i = 0; i <= max; i++)
		{
			deg.add(new LinkedList<V>());
		}
		
		//for every vertex, add it to the appropriate LinkedList
		for (V i : G.getVertices())
		{
			int iDeg = G.degree(i);
			deg.get(iDeg).add(i);
		}
		
		//deg.trimToSize();
		return deg;
	}
	/**
	 * Flatten and reverse degree order
	 * @param deg degree sequence
	 * @return vertex set in non-increasing degree order
	 */
	public ArrayList<V> flattenAndReverseDeg(ArrayList<LinkedList<V>> deg)
	{
		ArrayList<V> t = new ArrayList<V>(0);
		
		ArrayList<LinkedList<V>> degCopy = clone.deepClone(deg);
		//reverse the order of deg and flatten it for lexBFS
		for (int i = degCopy.size() - 1; i >=0; i--)
		{
			while (!degCopy.get(i).isEmpty())
			{
				t.add(degCopy.get(i).remove());
			}
		}
		return t;
	}
	
	
	
	public ArrayList<LinkedList<V>> degSequenceOrder(Graph<V, Pair<V>> G, Collection<V> vertexList)
	{
		//store vertices of same degree in LinkedList<V> at the index of their degree in ArrayList
		ArrayList<LinkedList<V>> deg = new ArrayList<LinkedList<V>>();
		int max = 0;
		for (V i : vertexList)
		{
			if (G.degree(i) > max)
				max = G.degree(i);
		}
		
		for (int i = 0; i <= max; i++)
		{
			deg.add(new LinkedList<V>());
		}
		
		//for every vertex, add it to the appropriate LinkedList
		for (V i : vertexList)
		{
			int iDeg = G.degree(i);
//			if (deg.get(iDeg) == null)
//			{
//				deg.add(iDeg, new LinkedList<V>());
//			}
			
			deg.get(iDeg).add(i);
		}
		
		
		deg.trimToSize();
		return deg;
	}
	
	public void flattenAndReverseDegPrint(ArrayList<LinkedList<V>> deg)
	{
		
		ArrayList<LinkedList<V>> degCopy = clone.deepClone(deg);
		//reverse the order of deg and flatten it for lexBFS
		for (int i = degCopy.size() - 1; i >=0; i--)
		{
			if (!degCopy.get(i).isEmpty())
				System.out.println(i + "\t" + degCopy.get(i).size());

		}
	}
	
	/**
	 * checks if graph is quasi threshold
	 * @param G
	 * @return true if graph is qt, otherwise false
	 */
	public boolean isQT(Graph<V, Pair<V>> G) {
		ArrayList<LinkedList<V>> deg = degSequenceOrder(G);
		
		lexReturnC<V> search = search(G, flattenAndReverseDeg(deg));
		
		if (search.isTarget())
			return true;
		else
			return false;
	}
	
}

