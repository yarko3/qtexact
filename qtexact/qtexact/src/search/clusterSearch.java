package search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import qtUtils.branchingReturnC;
import abstractClasses.Certificate;
import abstractClasses.Search;
import abstractClasses.SearchResult;

import com.rits.cloning.Cloner;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class clusterSearch<V> extends Search<V> {

	protected static Cloner clone = new Cloner();
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
			//split each component and run test
			Set<Graph<V, Pair<V>>> cGraphs = new HashSet<Graph<V, Pair<V>>>();
			
			//make connected component graphs
			for (Set<V> s : components)
			{
				if (s.size() > 2)
					cGraphs.add(this.connectedCFromVertexSet(graph, s));
			}
			

			//test each component separately
			for (Graph<V, Pair<V>> g : cGraphs)
			{
				SearchResult<V> temp = this.search(g);
				//if one component is not a clique, stop search and return obstruction
				if (temp.isTarget() == false)
				{
					return new SearchResult<V>(false, temp.getCertificate(), components);
				}
			}
			//search passed
			return new SearchResult<V>(true, null, components);
		}
		
		
		//set of traversed vertices that have an edge to the rest of the vertex-set
		HashSet<V> traversed = new HashSet<V>();
		
		//for every vertex in graph, look to make sure it has an edge to every other vertex
		for (V v0 : graph.getVertices())
		{	
			//if v0 breaks clique
			if (graph.degree(v0) < graph.getVertexCount() - 1)
			{
				//find the missing edge
				for (V v1 : graph.getVertices())
				{
					if (v0.equals(v1))
						continue;
					
					//not a clique
					if (!graph.isNeighbor(v0, v1))
					{
						//generate certificate
						//use a known maximally connected node to create a P3
						if (!traversed.isEmpty())
						{
							ArrayList<V> obst = new ArrayList<V>();
							obst.add(v0);
							obst.add(traversed.iterator().next());
							obst.add(v1);
							
							//return obstruction
							return new SearchResult<V>(false, new Certificate<V>(obst, -13), components);
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
							
							//if a common neighbour exists, use it
							if (!common.isEmpty())
							{
								ArrayList<V> obst = new ArrayList<V>();
								obst.add(v0);
								obst.add(common.iterator().next());
								obst.add(v1);
								
								//return obstruction
								return new SearchResult<V>(false, new Certificate<V>(obst, -13),  components);
							}
						}
					}
				}
			}
			else
			{
				//traversed contains maximally connected vertices
				traversed.add(v0);
			}
		}
		
		//graph passed
		return new SearchResult<V>(true, null, components);
		
	}

	@Override
	public SearchResult<V> search(branchingReturnC<V> s) {
		return search(s.getG());
	}
	
	/**
	 * connected component from vertex set
	 * @param G graph
	 * @param l vertex set
	 * @return subgraph constructed from vertex set
	 */
	@SuppressWarnings("unchecked")
	protected Graph<V, Pair<V>> connectedCFromVertexSet(Graph<V, Pair<V>> G, Set<V> l)
	{
		Graph<V, Pair<V>> c = null;
		try {
			c = (Graph<V, Pair<V>>) G.getClass().newInstance();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashSet<Pair<V>> tempSet = new HashSet<Pair<V>>();
		//throw all edges into hashset, no duplicates
		for (V i : l)
		{
			tempSet.addAll(G.getIncidentEdges(i));
			c.addVertex(i);
		}
		//add all edges to c
		for (Pair<V> e : tempSet)
		{
			Pair<V> edge = clone.deepClone(e);
			c.addEdge(edge, edge.getFirst(), edge.getSecond());
		}
		return c;
	}

}
