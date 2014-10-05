package search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import qtUtils.branchingReturnC;
import abstractClasses.Certificate;
import abstractClasses.Search;
import abstractClasses.SearchResult;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class diQTSearch<V> extends Search<V> {

	@Override
	public boolean isTarget(Graph<V, Pair<V>> g) {
		
		return search(g).isTarget();
	}

	@Override
	protected SearchResult<V> searchPrep(branchingReturnC<V> s) {
		return search(s);
	}

	
	public SearchResult<V> search(Graph<V, Pair<V>> graph) {
		
		//check that graph is directed
		if (!(graph instanceof DirectedGraph))
		{
			System.out.println("Attempting to search a non-directed graph.");
			throw new NullPointerException();
		}

		
		//cast as a directed graph
		DirectedGraph<V, Pair<V>> g = (DirectedGraph<V, Pair<V>>) graph;
		
		//get connected components
		Set<Set<V>> components = cluster.transform(g);
		
		
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
					
					//obstruction found, 2-into-1
					ArrayList<V> obstruction = new ArrayList<V>();
					obstruction.add(v0);
					obstruction.add(v1);
					obstruction.add(v2);
					
					return new SearchResult<V>(false, new Certificate<V>(obstruction, -10), components);
				}
				
				//for every grandchild of v0,
				for (V v2 : v1OutNeighbours)
				{
					//breaks transitive property of di-QT graphs
					if (!v0InNeighbours.contains(v2) && !v0OutNeighbours.contains(v2))
					{
						ArrayList<V> obstruction = new ArrayList<V>();
						obstruction.add(v0);
						obstruction.add(v1);
						obstruction.add(v2);
						
						return new SearchResult<V>(false, new Certificate<V>(obstruction, -12), components);
					}
					
					//don't count multi-directional edges
					if (v0InNeighbours.contains(v2) /*&& !v0OutNeighbours.contains(v2) && !v0InNeighbours.contains(v1) && !v1InNeighbours.contains(v2)*/)
					{
						ArrayList<V> obstruction = new ArrayList<V>();
						obstruction.add(v0);
						obstruction.add(v1);
						obstruction.add(v2);
						
						return new SearchResult<V>(false, new Certificate<V>(obstruction, -11), components);
					}
				}
			}
		}
		
		
		//OLD IMPLEMENTATION
		
//		//build a forest with the given graph
//		DirectedSparseGraph<V, Pair<V>> forest = new DirectedSparseGraph<V, Pair<V>>();
//		
//		//get vertex iterator
//		Iterator<V> vertices = dig.getVertices().iterator();
		
		//pull vertices and do a BFS
//		while (vertices.hasNext())
//		{
//			V nVertex = vertices.next();
//			if (!forest.containsVertex(nVertex))
//				forest.addVertex(nVertex);
//			
//			//get all incident edges on nVertex
//			Collection<Pair<V>> inEdges = dig.getInEdges(nVertex);
//			
//			//check each incident edge for violations of diQT
//			//get parents
//			HashSet<V> parents = new HashSet<V>();
//			
//			for (Pair<V> edge : inEdges)
//			{
//				if (edge.getFirst().equals(nVertex))
//				{
//					parents.add(edge.getSecond());
//				}
//				else
//					parents.add(edge.getFirst());
//			}
//			
//			//all parents should have an edge between them (clique)
//			for (V p0 : parents)
//			{
//				for (V p1 : parents)
//				{
//					if (p0.equals(p1))
//						continue;
//					
//					//violation of diQT
//					if (!dig.isNeighbor(p0, p1))
//					{
//						ArrayList<V> obstruction = new ArrayList<V>();
//						obstruction.add(p0);
//						obstruction.add(nVertex);
//						obstruction.add(p1);
//						
//						return new SearchResult<V>(false, new Certificate<V>(obstruction, -10), components);
//					}
//				}
//			}
//			
//			//add parents to forest
//			for (V p : parents)
//			{
//				
//				forest.addEdge(new Pair<V>(p, nVertex), p, nVertex);
//				
//			}
//			
//			//get out-edges from nVertex
//			Collection<Pair<V>> childEdges = dig.getOutEdges(nVertex);
//			HashSet<V> children = new HashSet<V>();
//			//get children
//			for (Pair<V> edge : childEdges)
//			{
//				if (edge.getFirst().equals(nVertex))
//				{
//					children.add(edge.getSecond());
//				}
//				else
//					children.add(edge.getFirst());
//			}
//			
//			
//			//check for directed 3-cycles
//			for (V p : parents)
//			{
//				//get parents of parent
//				Collection<Pair<V>> gParentEdges = forest.getInEdges(p);
//				HashSet<V> gParents = new HashSet<V>();
//				
//				for (Pair<V> edge : gParentEdges)
//				{
//					if (edge.getFirst().equals(p))
//					{
//						gParents.add(edge.getSecond());
//					}
//					else
//						gParents.add(edge.getFirst());
//				}
//				
//				//see if there is any overlap between gParents and children
//				
//				for (V c : children)
//				{
//					if (!gParents.add(c))
//					{
//						//a directed 3-cycle has been found
//						ArrayList<V> obstruction = new ArrayList<V>();
//						obstruction.add(p);
//						obstruction.add(nVertex);
//						obstruction.add(c);
//						
//						return new SearchResult<V>(false, new Certificate<V>(obstruction, -11), components);
//					}
//				}
//			}
//			
//			//add children to forest
//			for (V c : children)
//			{
//				forest.addEdge(new Pair<V>(nVertex, c), nVertex, c);
//			}
//			
//		}
		
		
		
		
		return new SearchResult<V>(true, null, components);
	}

	@Override
	public SearchResult<V> search(branchingReturnC<V> s) {
		return search(s.getG());
	}

}
