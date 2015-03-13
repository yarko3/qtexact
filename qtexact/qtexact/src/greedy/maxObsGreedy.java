package greedy;

import abstractClasses.Branch;
import abstractClasses.GreedyEdit;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class maxObsGreedy<V> extends GreedyEdit<V> 
{

	/**
	 * constructor
	 * @param bStruct branching structure
	 */
	public maxObsGreedy(Branch<V> bStruct) {
		super(bStruct);
	}

	
	/**
	 * number of p4s and c4s in the given graph
	 * @param g graph
	 * @return number of obstructions
	 */
	@Override
	protected int getObstructionCount(Graph<V, Pair<V>> g)
	{
		//p4 count
		int count0 = 0;
		//c4 count
		int count1 = 0;
		
		for (V v0 : g.getVertices())
		{
			for (V v1 : g.getNeighbors(v0))
			{
				for (V v2 : g.getNeighbors(v1))
				{
					if (v2.equals(v0) || g.isNeighbor(v0, v2))
						continue;
					
					for (V v3 : g.getNeighbors(v2))
					{
						if (v3.equals(v0) || v3.equals(v1) || g.isNeighbor(v3, v1))
						{
							continue;
						}
						
						if (g.isNeighbor(v0, v3))
							count1++;
						else
							count0++;
						
//						ArrayList<V> obstruction = new ArrayList<V>();
//						obstruction.add(v0);
//						obstruction.add(v1);
//						obstruction.add(v2);
//						obstruction.add(v3);
//						
//						
//						if (g.isNeighbor(v0, v3))
//						{
//							
//							cert.add(new qtCertificateC<V>(obstruction, -1));
//						}
//						else
//						{
//							cert.add(new qtCertificateC<V>(obstruction, -2));
//						}
					}
				}
			}
		}
		
		//return cert.size();
		return count0 / 2 + count1/4;
	}

				
}
