package greedy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import abstractClasses.Branch;
import abstractClasses.Certificate;
import abstractClasses.GreedyEdit;
import abstractClasses.SearchResult;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class diQTGreedy<V> extends GreedyEdit<V> {

	public diQTGreedy(Branch<V> b) {
		super(b);
	}

	@Override
	protected int getObstructionCount(Graph<V, Pair<V>> g) {

		//number of obstruction 10
		int count0 = 0;
		//number of obstruction 11
		int count1 = 0;
		//number of obstruction 12
		int count2 = 0;
		
		//for every vertex v0,
		for (V v0 : g.getVertices())
		{
			//get in and out neighbours
			Collection<V> v0OutNeighbours = new HashSet<V>();
			Collection<V> v0InNeighbours = new HashSet<V>();
			
			
			for (Pair<V> edge : g.getOutEdges(v0))
			{
				v0OutNeighbours.add(edge.getSecond());
			}
			for (Pair<V> edge : g.getInEdges(v0))
			{
				v0InNeighbours.add(edge.getFirst());
			}
			
			//for every child,
			for (V v1 : v0OutNeighbours)
			{
				//get incident vertices on v1, see if they are non-neighbours of v0
				Collection<V> v1OutNeighbours = new HashSet<V>();
				Collection<V> v1InNeighbours = new HashSet<V>();
				
				
				for (Pair<V> edge : g.getOutEdges(v1))
				{
					v1OutNeighbours.add(edge.getSecond());
				}
				for (Pair<V> edge : g.getInEdges(v1))
				{
					v1InNeighbours.add(edge.getFirst());
				}
				
				//for every parent of v1,
				for (V v2 : v1InNeighbours)
				{
					//make sure v0 is not being checked with v0
					if (v0.equals(v2))
						continue;
					//di-QT property is upheld
					if (g.isNeighbor(v0, v2))
						continue;
					
					
					count0++;
				}
				
				//for every grandchild of v0,
				for (V v2 : v1OutNeighbours)
				{
					//breaks transitive property of di-QT graphs
					if (!v0InNeighbours.contains(v2) && !v0OutNeighbours.contains(v2))
					{
						
						count2++;
					}
					
					//don't count multi-directional edges
					if (v0InNeighbours.contains(v2) /*&& !v0OutNeighbours.contains(v2) && !v0InNeighbours.contains(v1) && !v1InNeighbours.contains(v2)*/)
					{
						count1++;
					}
				}
			}
		}
		
		return count0/2 + count1/3 + count2;
	}

}
