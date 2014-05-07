package qtUtils;

import java.util.ArrayList;
import java.util.Collection;

import edu.uci.ics.jung.graph.Graph;

public class genericLBFS {
	
	public static ArrayList<Integer> genericLexBFS(Graph<Integer, String> G, ArrayList<Integer> t )
	{
		ArrayList<Integer> s = new ArrayList<Integer>(t.size());
		ArrayList<ArrayList<Integer>> L = new ArrayList<ArrayList<Integer>>(0);
		L.add(t);
		int tsize = t.size();
		for (int i = 0; i < tsize; i++)
		{
			while (L.get(0).isEmpty())
			{
				L.remove(0);
			}
			ArrayList<Integer> p1 = L.get(0);
			int x = p1.remove(0);
			if (p1.isEmpty())
			{
				L.remove(0);
			}
			
			s.add(i, x);
			
			Collection<Integer> hood = G.getNeighbors(x);
			int insertedC = 0;
			ArrayList<ArrayList<Integer>> Lcopy = (ArrayList<ArrayList<Integer>>) L.clone();
			int start = 1;
			if (L.size() == 1)
			{
				start = 0;
			}
			for (int j = start; j < L.size(); j++)
			{
				ArrayList<Integer> pp = new ArrayList<Integer>(0);
				
				for (int h : hood)
				{
					if (L.get(j).contains(h))
					{
						pp.add(L.get(j).remove(L.get(j).indexOf(h)));
					}
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
