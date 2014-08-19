package search;

import java.util.ArrayList;
import java.util.Collection;

import qtUtils.lexReturnC;
import abstractClasses.Search;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
/**
 * an abstract class used as the backbone of further LBFS searches
 * @author Yaroslav Senyuta
 *
 * @param <V> vertex
 */
public abstract class LBFS<V> extends Search<V>
{
	/**
	 * a search which returns a SearchResult object
	 * @param G
	 * @param t
	 * @return the result of search
	 */
	public abstract lexReturnC<V> search(Graph<V, Pair<V>> G, ArrayList<V> t);
	
	
	/**
	 * lexBFS+ traversal
	 * @param G graph
	 * @param t initial vertex ordering
	 * @return lexBFS result
	 */
	public lexReturnC<V> LexBFSplus(Graph<V, Pair<V>> G, ArrayList<V> t)
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
			
			Collection<V> hood = G.getNeighbors(x);
			
			int j = 0;
			
			while (j < L.size())
			{
				//new partition to be inserted into L
				ArrayList<V> pp = new ArrayList<V>(0);
				
				
				//fill pp with neighbours in the original order given
				for (int k = 0; k < L.get(j).size(); k++)
				{
					V h = L.get(j).get(k);
					if (hood.contains(h))
					{
						pp.add(L.get(j).remove(k));
						k--;
					}
					
				}
				
				if (!pp.isEmpty())
				{
					L.add(j, pp);
					j++;
				}
				j++;
			}
		}
		//return ordering
		return new lexReturnC<V>(s, null, true, true, null);
	}
	
	/**
	 * lexBFS- traversal
	 * @param G graph
	 * @param t initial vertex ordering
	 * @return search result
	 */
	public lexReturnC<V> LexBFSminus(Graph<V, Pair<V>> G, ArrayList<V> t)
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
			
			Collection<V> hood = G.getNeighbors(x);
			
			int j = 0;
			
			while (j < L.size())
			{
				//new partition to be inserted into L
				ArrayList<V> pp = new ArrayList<V>(0);
				
				
				//fill pp with neighbours in the original order given
				for (int k = 0; k < L.get(j).size(); k++)
				{
					V h = L.get(j).get(k);
					if (hood.contains(h))
					{
						pp.add(L.get(j).remove(k));
						k--;
					}
					
				}
				
				if (!pp.isEmpty())
				{
					L.add(j+1, pp);
					j++;
				}
				j++;
			}
		}
		//return ordering
		return new lexReturnC<V>(s, null, true, true, null);
	}

}
