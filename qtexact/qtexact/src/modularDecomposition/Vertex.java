package modularDecomposition;

import java.util.Collection;

import java.util.LinkedList;
import java.util.ListIterator;

/*
 * A vertex in a simple, undirected graph.
 */
public class Vertex<V> {
	
	// The label of the vertex.
	private V label;
	// The default label assigned to vertices.
	private final V DEFAULT_LABEL = null;
	
	// The vertex's neighbours.
	private LinkedList<Vertex<V>> neighbours;

	
	/* The default constructor. */
	public Vertex() {
		label = DEFAULT_LABEL;
		neighbours = new LinkedList<Vertex<V>>();
	}
	
	
	/* Creates a string with the given string label, but with no neighbours. */
	public Vertex(V vertexLabel) {
		this();
		label = vertexLabel;
	}
	
	
	/* Returns this vertex's label. */
	public V getLabel() {
		return label;
	}

	
	/* 
	 * Adds the given vertex as a neighbour of the given vertex.
	 * @param vertex The neighbour to be added.
	 */
	public void addNeighbour(Vertex<V> vertex) {
		neighbours.add(vertex);		
	}
	
	
	/* Returns this vertex's collection of neighbours. */
	public Collection<Vertex<V>> getNeighbours() {
		return neighbours;
	}
	
	
	/* 
	 * Returns a string representation of this vertex.  Enclosed in brackets 
	 * is the vertex's label, followed by a list of its neighbours.
	 */
	public String toString() {		
		
		String result = "(label=" + label;				
		
		result += ", neighbours:";
		
		ListIterator<Vertex<V>> neighboursIt = neighbours.listIterator();
		
		if (neighboursIt.hasNext()) { result += neighboursIt.next().label; }		
		while(neighboursIt.hasNext()) {
			result += "," + neighboursIt.next().getLabel();		
		}
		
		return result += ")";		
	}	
}
