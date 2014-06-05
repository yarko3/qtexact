package search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import qtUtils.lexReturnC;
import certificate.qtCertificateC;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * A lexBFS search for quasi threshold graphs while also finding connected components
 * 
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public class qtLBFSComponents<V> extends qtLBFS<V> 
{
	/** LexBFS search 
	 * 
	 * @param G graph to be tested
	 * @param t initial ordering of vertices
	 * @return final LexBFS ordering of vertices
	 */
	public lexReturnC<V> search(Graph<V, Pair<V>> G, ArrayList<V> t)
	{
		//new ordering
		ArrayList<V> s = new ArrayList<V>(t.size());
		//list of partitions (each partition has a common label)
		ArrayList<ArrayList<V>> L = new ArrayList<ArrayList<V>>(0);
		
		
		
		//initial element of L has all vertices
		L.add(t);
		int tsize = t.size();
		
		//get connected components
		LinkedList<HashSet<V>> cComponents = new LinkedList<HashSet<V>>();
		int compIndex = 0;
		HashSet<V> component = new HashSet<V>();
		
		//flag for whether the graph is QT and a forbidden P4 or C4
		boolean isQT = true;
		qtCertificateC<V> forbidden = null;
		
		
		
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
			
			component.add(x);
			
			//add x to s at i
			s.add(i, x);
			
			//neighbours of x
			
			ArrayList<V> hood = orderNeighbour(G, x);
			
			
			//flag to keep track if x is the last element of connected component
			boolean compEnd = true;
			
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
						V temp = L.get(j).remove(L.get(j).indexOf(h));
						pp.add(temp);
						component.add(temp);
						compEnd = false;
					}
				}
				//quasi-threshold check (should return C4 or P4)
				if (isQT && j != 0 && !pp.isEmpty())
				{
					isQT = false;
					forbidden = qtCertificate(G, x, pp.get(0), s);
				}
				if (!pp.isEmpty())
				{
					L.add(j, pp);
					j++;
				}
				j++;
					
			}
			
			//x is the last element of connected component, add all preceding ones to list
			if (compEnd && isQT)
			{
				HashSet<V> tempComp = new HashSet<V>();
				//add elements of connected component
				for (int k = compIndex; k <= i; k++)
				{
					tempComp.add(s.get(k));
				}
				if (tempComp.containsAll(component))
				{
					//update the end of last connected component
					compIndex = i+1;
					//add new connected component to list
					cComponents.add(component);
					component = new HashSet<V>();
				}
			}
			
			//if the graph is not QT, finish up connected component list
			if (!isQT)
			{
				//add elements of forbidden component
				for (int k = compIndex; k <= i; k++)
				{
					component.add(s.get(k));
				}
				for (ArrayList<V> v : L)
				{
					component.addAll(v);
				}
				//add forbidden component to list
				cComponents.add(component);
				
				//return
				return new lexReturnC<V>(null, forbidden, false, cComponents.size() == 1, cComponents);
			}
		}
		//return search results
		
		return new lexReturnC<V>(s, null, true, cComponents.size() == 1, cComponents);
		
	}

}
