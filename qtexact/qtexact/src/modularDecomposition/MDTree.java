package modularDecomposition;

/*
 * A modular decomposition tree of a simple, undirected graph.
 */
public class MDTree<V> extends RootedTree<V> {
		
	/*
	 * Creates the modular decomposition tree for the supplied graph.
	 */
	protected MDTree(myGraph<V> g) {
		super();
		setRoot(buildMDTree(g));			
	}
	
	
	/*
	 * Builds the modular decomposition tree for the supplied graph.
	 * @return The root of the constructed modular decomposition tree.
	 */
	private MDTreeNode<V> buildMDTree(myGraph<V> g) {
		
		if (g.getNumVertices() == 0) { return null; }
		
		RecSubProblem<V> entireProblem = new RecSubProblem<V>(g);	
		
		MDTreeNode<V> root = entireProblem.solve();		
		root.clearVisited();
		return root;			
	}
}
