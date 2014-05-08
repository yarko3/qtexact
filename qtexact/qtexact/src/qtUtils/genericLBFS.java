package qtUtils;

import java.util.ArrayList;
import java.util.Collection;

import edu.uci.ics.jung.graph.Graph;

public class genericLBFS {
	
	/** LexBFS search 
	 * 
	 * @param G graph to be tested
	 * @param t initial ordering of vertices
	 * @return final LexBFS ordering of vertices
	 */
	public static ArrayList<Integer> genericLexBFS(Graph<Integer, String> G, ArrayList<Integer> t )
	{
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
			//if first partition is empty, remove partition from L
			if (p1.isEmpty())
			{
				L.remove(0);
			}
			//add x to s at i
			s.add(i, x);
			
			//neighbours of x
			Collection<Integer> hood = G.getNeighbors(x);
			//number of new partitions inserted into L
			int insertedC = 0;
			//deep copy of L
			ArrayList<ArrayList<Integer>> Lcopy = new ArrayList<ArrayList<Integer>>(L.size());
			for (int k = 0; k < L.size(); k++)
			{
				Lcopy.add(k, (ArrayList<Integer>) L.get(k).clone());
			}
			//usually start j from 1, unless 1 or less elements in L
			int start = 1;
			if (L.size() == 1)
			{
				start = 0;
			}
			for (int j = start; j < L.size(); j++)
			{
				//new partition to be inserted into L
				ArrayList<Integer> pp = new ArrayList<Integer>(0);
				
				for (int h : hood)
				{
					if (L.get(j).contains(h))
					{
						//remove element from L and add to pp
						pp.add(L.get(j).remove(L.get(j).indexOf(h)));
						//remove element from Lcopy
						Lcopy.get(j + insertedC).remove(Lcopy.get(j + insertedC).indexOf(h));
						if (L.get(j).isEmpty())
						{
							//L.remove(j);
							Lcopy.remove(j + insertedC);
						}
					}
				}
				//quasi-threshold check (should return C4 or P4)
				if (j != 0 && !pp.isEmpty())
				{
					return null;
				}
				if (!pp.isEmpty())
				{
					Lcopy.add(j + insertedC++, pp);
				}
					
			}
			
			L = Lcopy;
			
		}
		
		return s;
	}

}
