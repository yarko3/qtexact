package modularDecomposition;

import java.util.Collection;
import java.util.Hashtable;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/* 
 * A simple, undirected graph.
 */
public class myGraph<V> {
	
	
	// The graph's vertices, keyed by their label.
	private Hashtable<V,Vertex<V>> vertices;
	
	
	/* 
	 * Constructs a graph from the file whose name is that specified.  The 
	 * format of the file must be as follows:
	 * - each line in the file specifies a vertex;
	 * - each vertex is specified by its label, followed by VERTEX_DELIM, 
	 * followed by a list of its neighbours separated by NEIGH_DELIM (without
	 * spaces between these tokens);
	 * - the file must correctly specify a graph in that each vertex appearing
	 * as a neighbour is specified by a line in the file, and the neighbourhood
	 * lists are symmetric in that an entry for x as a neighbour of y implies 
	 * an entry for y as a neighbour of x.
	 * @param file The name of the input file specifying the graph.
	 */
	
	
	public myGraph(Graph<V, Pair<V>> g) {		
		vertices = buildFromJung(g);
	}

	/*
	 * Does the work of reading the file and populating the graph with 
	 * vertices according to the contents of the file.  See 'Graph(String )'
	 * for the required input file format.
	 * @param file The name of the input file specifying the graph. 
	 */
	private Hashtable<V,Vertex<V>> buildFromJung(Graph<V, Pair<V>> g) {
		
		Hashtable<V,Vertex<V>> vertices = new Hashtable<V,Vertex<V>>();
		
        for (V v : g.getVertices())
        {
                            	
            // Determine the current vertex's label.
        
            V vertexLabel = v;
            
              
          
            Vertex<V> vertex;
            if (vertices.containsKey(vertexLabel)) {
            	vertex = vertices.get(vertexLabel);
            }
            else {
            	vertex = new Vertex<V>(vertexLabel);
            	vertices.put(vertexLabel,vertex);
            }
                   
            // Create vertices for each of its neighbours (if they haven't already
            // been created) and add them as neigbhours of this vertex.
            for (V n : g.getNeighbors(v)) {
            	if (vertices.containsKey(n)) {
            		vertex.addNeighbour(vertices.get(n)); 
            	}
            	else {
            		Vertex<V> unseenNeighbour = new Vertex<V>(n);
            		vertices.put(n,unseenNeighbour);
            		vertex.addNeighbour(unseenNeighbour);
            	}
            } 

        } 
        
        return vertices;
	}
	

	/* Returns this graph's vertices. */
	public Collection<Vertex<V>> getVertices() {
		return vertices.values();	
	}

	
	/* Returns the number of this graph's vertices. */
	public int getNumVertices() {		
		return vertices.values().size();
	}

	
	/* Returns the modular decomposition tree for this graph. */
	public MDTree<V> getMDTree() {
		return new MDTree<V>(this);
	}
	
	
	/* 
	 * Returns a string representation of this graph.  The representation
	 * is a list of the graph's vertices.
	 * @return A string representation of the graph.
	 */
	public String toString() {
		return vertices.values().toString();
	}
}
