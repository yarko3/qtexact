package search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import qtUtils.branchingReturnC;
import abstractClasses.Certificate;
import abstractClasses.Search;
import abstractClasses.SearchResult;
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class clusterSearch<V> extends Search<V> {

	//for testing connectivity
	private WeakComponentClusterer<V, Pair<V>> cluster =  new WeakComponentClusterer<V, Pair<V>>();
	
	
	@Override
	public boolean isTarget(Graph<V, Pair<V>> g) {
		return search(g).isTarget();
	}

	@Override
	protected SearchResult<V> searchPrep(branchingReturnC<V> s) {
		return search(s.getG());
	}


	public SearchResult<V> search(Graph<V, Pair<V>> graph) {
		
		
		//separate graph into connected components (if graph not connected)
		Set<Set<V>> components = cluster.transform(graph);
		if (components.size() > 1)
		{
			return new SearchResult<V>(false, null, components);
		}
		
		
		//set of traversed vertices that have an edge to the rest of the edgeset
		HashSet<V> traversed = new HashSet<V>();
		
		//for every vertex in graph, look to make sure it has an edge to every other vertex
		for (V v0 : graph.getVertices())
		{	
			for (V v1 : graph.getVertices())
			{
				if (v0.equals(v1))
					continue;
				
				//not a clique
				if (!graph.isNeighbor(v0, v1))
				{
					//generate certificate
					//use a known maximally connected node
					if (!traversed.isEmpty())
					{
						ArrayList<V> obst = new ArrayList<V>();
						obst.add(v0);
						obst.add(traversed.iterator().next());
						obst.add(v1);
						
						//return obstruction
						return new SearchResult<V>(false, new Certificate<V>(obst, -13));
					}
					else
					{
						//get a vertex that is a common neighbour of both vertex and n
						Collection<V> v0n = graph.getNeighbors(v0);
						Collection<V> v1n = graph.getNeighbors(v1);
						
						HashSet<V> all = new HashSet<V>();
						HashSet<V> common = new HashSet<V>();
						all.addAll(v0n);
						
						for (V temp : v1n)
						{
							if (!all.add(temp))
							{
								common.add(temp);
								break;
							}
						}
						
						if (!common.isEmpty())
						{
							ArrayList<V> obst = new ArrayList<V>();
							obst.add(v0);
							obst.add(common.iterator().next());
							obst.add(v1);
							
							//return obstruction
							return new SearchResult<V>(false, new Certificate<V>(obst, -13));
						}
					}
				}
			}
		}
		
		//graph passed
		return new SearchResult<V>(true, null);
		
	}

	@Override
	public SearchResult<V> search(branchingReturnC<V> s) {
		return search(s.getG());
	}

}
