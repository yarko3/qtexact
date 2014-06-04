package search;

import java.util.ArrayList;
import java.util.LinkedList;

import qtUtils.lexReturnC;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class qtLBFSNoHeuristic<V> extends qtLBFS<V> 
{
	
	public lexReturnC<V> search(Graph<V, Pair<V>> G, ArrayList<V> t)
	{
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
					return new lexReturnC<V>(null, qtCertificate(G, x, pp.get(0), s), false, false, null);
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
		return new lexReturnC<V>(s, null, true, false, null);
	}

	
	public boolean isQT(Graph<V, Pair<V>> G) {
		ArrayList<LinkedList<V>> deg = degSequenceOrder(G);
		
		lexReturnC<V> search = search(G, flattenAndReverseDeg(deg));
		
		if (search.isTarget())
			return true;
		else
			return false;
	}


	@Override
	public boolean isTarget(Graph<V, Pair<V>> g) {
		return isQT(g);
	}

}
