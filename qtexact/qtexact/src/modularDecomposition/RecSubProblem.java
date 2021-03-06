package modularDecomposition;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/* 
 * A node in the recursion tree used to compute the modular decomposition tree
 * of a graph.
 */
class RecSubProblem<V> extends RootedTreeNode<V> {

	// Is the recursive subproblem connected by edges to its sibling subproblems.
	private boolean connected;
	
	// Is the recursive subproblem currently being solved.
	private boolean active;
	
	// The pivot associated with this subproblem.
	private MDTreeLeafNode<V> pivot;
	
	// Used to denote module boundaries in a factorizing permutation.
	private static final int MODULE_BOUNDARY = -1;

	
	/* The default constructor. */
	private RecSubProblem() {
		super();
		connected = false;
		active = false;
		pivot = null;
	}
	
	
	/* 
	 * The copy-constructor.  The newly created subproblem gets the same 
	 * field values as the supplied problem but not any of its children.
	 * @param copy The subproblem whose field values are to be copied from.
	 */
	private RecSubProblem(RecSubProblem<V> copy) {
		this();
		connected = copy.connected;
		active = copy.active;
		pivot = copy.pivot;
	}
	
	
	/* 
	 * Creates a recursive subproblem for the graph consisting of the single
	 * vertex represented by the supplied leaf node.
	 * @param leaf A single leaf node representing the graph that defines this
	 * subproblem.  
	 */
	private RecSubProblem(MDTreeLeafNode<V> leaf) {
		this();	
		addChild(leaf);			
	}

	
	/* 
	 * Creates a recursive subproblem defined by the supplied graph.
	 * @param graph The graph defining this subproblem.
	 */
	RecSubProblem(myGraph<V> graph) {
		
		this();

		Hashtable<Vertex<V>,MDTreeLeafNode<V>> leaves = new Hashtable<Vertex<V>,MDTreeLeafNode<V>>();
		
		// Create leaves for each vertex.
		Iterator<Vertex<V>> vertexIt = graph.getVertices().iterator();
		while (vertexIt.hasNext()) {
			Vertex<V> current = vertexIt.next();
			MDTreeLeafNode<V> currentLeaf = new MDTreeLeafNode<V>(current);
			leaves.put(current, currentLeaf);
		}
		
		// Convert the vertex neighbours (in the graph) of the vertices into leaf 
		// neighbours (in the recursion tree). 
		Iterator<MDTreeLeafNode<V>> leafIt = leaves.values().iterator();
		while (leafIt.hasNext()) {
			MDTreeLeafNode<V> current = leafIt.next();
			Iterator<Vertex<V>> neighboursIt = current.getVertex().getNeighbours().iterator();
			while (neighboursIt.hasNext()) {
				current.addNeighbour(leaves.get(neighboursIt.next()));
			}
		}		
			
		// Now populate the subproblem with the leaf nodes.
		leafIt = leaves.values().iterator();
		while (leafIt.hasNext()) {			
			MDTreeLeafNode<V> current = leafIt.next();
			addChild(current);
		}
				
	}	
	
	
	/* Resets this subproblem's attributes to their default values. */
	private void clearAttributes() {
		active = false;
		connected = false;
		pivot = null;
	}
	
	
	/* 
	 * Computes the MD tree for this subproblem.  The root of the 
	 * MD tree becomes the sole child of this subproblem.  
	 * @return The root of the constructed MD tree.
	 */
	protected MDTreeNode<V> solve() {		
				
		// We are currently solving this subproblem.
		active = true;
		
		// The subproblem (and thus the MD tree) contains a single node; nothing 
		// to do, simply must process the pivot to refine the subproblems in the
		// rest of the recursion tree.
		if (hasOnlyOneChild()) {			
			MDTreeLeafNode<V> pivot = (MDTreeLeafNode<V>)getFirstChild();
						
			processNeighbours(pivot);
			pivot.setVisited();
						
			return (MDTreeNode<V>) this.getFirstChild(); 
		}
									
		// Pivot this subproblem and refine the subproblems in the rest of 
		// the recursion tree.  See note of 'pivot' for the required introduction
		// of 'thisProblem'.
		RecSubProblem<V> thisProblem = pivot();
		
		
		// Solve the subproblems defined by the layers.
		RecSubProblem<V> currentSubProblem = 
			(RecSubProblem<V>) thisProblem.getFirstChild();
		while (currentSubProblem != null) {				
				MDTreeNode<V> solvedRoot = currentSubProblem.solve();					
				currentSubProblem = 
					(RecSubProblem<V>) solvedRoot.getParent().getRightSibling();
		}
													
		// The MD tree of all but the first component of this subproblem's graph
		// has already been computed.  Remove it for now; we'll merge the two
		// MD trees later.
		MDTreeNode<V> extraComponents = 
			thisProblem.removeExtraComponents();

		// Replace the layers by their solutions.
		thisProblem.removeLayers();
		
		thisProblem.completeAlphaLists();
		thisProblem.numberByComp();
		thisProblem.numberByTree();
		
		// Get the factorizing permutation.
		thisProblem.refinement();				
		thisProblem.promotion();
				
		// Use the factorizing permutation to build the tree.
		thisProblem.delineation();							
		thisProblem.assembleTree();
		thisProblem.removeDegenerateDuplicates();
		
		// Incorporate extra components.
		thisProblem.mergeComponents(extraComponents);

		// Must reset fields to have recursion continue to work.  We do not
		// reset 'visited' field since we must continue to know which nodes
		// have already been pivots for calculation of alpha-lists 
		// (see 'processNeighbours').
		thisProblem.clearAllButVisited();
				
		return (MDTreeNode<V>) thisProblem.getFirstChild();			
	}
	
	
	/* 
	 * For all nodes in this subproblem's MD tree, clears all fields
	 * except 'visited'.
	 */
	private void clearAllButVisited() {
		((MDTreeNode<V>) getFirstChild()).clearAll();
	}

	
	/* 
	 * Removes consecutively appearing degenerate nodes of the same type
	 * in this subproblem's MD tree.
	 */
	private void removeDegenerateDuplicates() {
		((MDTreeNode<V>) getFirstChild()).removeDegenerateDuplicatesFromSubtree();			
	}

	
	/* 
	 * Takes the MD tree for this subproblem and merges it with the
	 * MD tree rooted at the supplied node.  If the roots of both
	 * trees are parallel, then the former's children are made children
	 * of the latter.  Otherwise, a new root is created with its children
	 * being the roots of the two trees in question.  The tree resulting
	 * from this merge becomes the MD tree of this subproblem.
	 * @param The root of the MD tree to be incorporated into this 
	 * subproblem's MD tree.  
	 */
	private void mergeComponents(MDTreeNode<V> newComponents) {
		
		MDTreeNode<V> firstComponent = (MDTreeNode<V>) getFirstChild();
		
		if (newComponents == null) { 
			return;
		}			
		else if (newComponents.getType() == MDNodeType.PARALLEL) {
			if (firstComponent.getType() == MDNodeType.PARALLEL) {
				newComponents.addChildrenFrom(firstComponent);
			}
			else {
				newComponents.addChild(firstComponent);
			}
			addChild(newComponents);
		}
		else {
			MDTreeNode<V> newRoot = new MDTreeNode<V>(MDNodeType.PARALLEL);
			newRoot.addChild(firstComponent);
			newRoot.addChild(newComponents);
			addChild(newRoot);
		}			
	}

	
	/* 
	 * Takes the factorizing permutation with the strong modules containing x
	 * properly delineated and assembles the MD tree.  Creates a spine of new
	 * modules for each strong module containing x, and affixes to 
	 * these the subtrees forming the permutation, based on the position of 
	 * each subtree relative to the nested strong modules containing x.
	 * Replaces the factorizing permutation within the current subproblem with
	 * the MD tree assembled.  That is, this subproblem is made to have one 
	 * child, namely the root of the MD tree constructed.
	 */
	private void assembleTree() {
		
		FactPermElement<V> left = (FactPermElement<V>) pivot.getParent().getLeftSibling();
		FactPermElement<V> right = (FactPermElement<V>) pivot.getParent().getRightSibling();
		
		// Smallest strong module containing x is x itself.
		MDTreeNode<V> lastModule = pivot;
		
		while (left != null || right != null) {
			
			// Creates the spine one new module at a time.
			MDTreeNode<V> newModule = new MDTreeNode<V>();
			newModule.addChild(lastModule);
			
			boolean addedPivotNeighbours = false;
			boolean addedPivotNonNeighbours = false;
			
			// Add the subtrees of the new module from N(x).
			while (left.getIndex() != MODULE_BOUNDARY) {										
				newModule.addChildrenFrom(left);										
				FactPermElement<V> oldLeft = left;					
				left = (FactPermElement<V>) left.getLeftSibling();
				oldLeft.remove();
				addedPivotNeighbours = true;
			}
			
			// Add the subtrees of the new module from \overline{N(x)}.
			while (right.getIndex() != MODULE_BOUNDARY) {					
				newModule.addChildrenFrom(right);					
				FactPermElement<V> oldRight = right;
				right = (FactPermElement<V>) right.getRightSibling();
				oldRight.remove();
				addedPivotNonNeighbours = true;
			}
			
			if (addedPivotNeighbours && addedPivotNonNeighbours) {
				newModule.setType(MDNodeType.PRIME);
			}
			else if (addedPivotNeighbours) {
				newModule.setType(MDNodeType.SERIES);
			}
			else {
				newModule.setType(MDNodeType.PARALLEL);
			}
						
			left = (FactPermElement<V>) left.getLeftSibling();
			right = (FactPermElement<V>) right.getRightSibling();
			lastModule = newModule;					
		}
		
		replaceChildrenWith(lastModule);
	}
	
	
	/* 
	 * Uses the factorizing permutation resulting from promotion, the 
	 * active edges between the vertices, and the (co-)components 
	 * calculated recursively to identify and delineate the strong modules 
	 * containing x.  See 'delineate' for details on how the strong 
	 * modules are marked out.
	 */
	private void delineation() {						
		buildPermutation();	
		determineLeftCoCompFragments();
		determineRightCompFragments();	
		determineRightLayerNeighbour();
		computeFactPermEdges();			
		computeMu();			
		delineate();			
	}

	
	/* 
	 * Computes the mu-value for each factorizing permutation element.
	 * See paper for definition of mu.
	 */
	private void computeMu() {
		
		FactPermElement<V> firstElement = (FactPermElement<V>) getFirstChild();
		FactPermElement<V> current = firstElement;
		FactPermElement<V> pivotElement = 
			(FactPermElement<V>) pivot.getParent();			
		
		// Initialize mu-values for those left of pivot; this is 
		// their default value.
		while (current != pivotElement) {
			current.setMu(pivotElement);
			current = (FactPermElement<V>) current.getRightSibling();				
		}
		
		// Initialize mu-values for those right of pivot; this is their
		// default value.
		while (current != null) {
			current.setMu(firstElement);
			current = (FactPermElement<V>) current.getRightSibling();				
		}
		
		// mu-values determined only by looking at elements to the left of pivot.			
		current = (FactPermElement<V>) getFirstChild();
		while (current != pivotElement) {
			
			FactPermElement<V> next = 
				(FactPermElement<V>) current.getRightSibling();
			
			Iterator<FactPermElement<V>> neighIt = 
				current.getNeighbours().iterator();				
			
			while (neighIt.hasNext()) {
				
				FactPermElement<V> currentNeighbour = neighIt.next();
				
				// Neighbour to left of pivot is universal to all up to current,
				// and also adjacent to current, so mu gets updated to next.
				if (currentNeighbour.getMu().getIndex() == current.getIndex()) {
					currentNeighbour.setMu(next);
				}
				
				// Current has an edge past previous farthest edge, so must 
				// update mu.
				if (currentNeighbour.getIndex() > current.getMu().getIndex() ) {
					current.setMu(currentNeighbour);
				}										
			}
			
			current = next;
		}									
	}

	
	/* 
	 * Builds an array containing the factorizing permutation elements
	 * in order.  Thus, the index of each factorizing permutation element
	 * is their index in the array.
	 * @return The array containing the factorizing permutation.
	 */
	private FactPermElement<V>[] buildFactPermArray() {
		FactPermElement<V> temp = new FactPermElement<V>();
		
		FactPermElement<V>[] factPerm = (FactPermElement<V>[]) Array.newInstance(temp.getClass(), getNumChildren());
		FactPermElement<V> currentElement = 
			(FactPermElement<V>)getFirstChild();
		for (int i = 0; currentElement != null; i++) {			
			factPerm[i] = currentElement;
			currentElement = (FactPermElement<V>)currentElement.getRightSibling();
		}
		return factPerm;			
	}		

	
	/* 
	 * Determines the edges between factorizing permutation elements on
	 * either side of the pivot and explicitly adds these edges as 
	 * adjacencies of the factorizing permutation elements in question.  
	 * Two factorizing permutation elements are considered adjacent if 
	 * there is a join between the leaves/vertices in the trees forming them.
	 */
	private void computeFactPermEdges() {			
		
		// Change the compNum of each vertex to the index of the factorizing
		// permutation element to which it belongs.
		FactPermElement<V> currentElement = (FactPermElement<V>) getFirstChild();
		while (currentElement != null) {
			
			Iterator<RootedTreeNode<V>> leavesIt =
				currentElement.getLeaves().iterator();
			while (leavesIt.hasNext()) {
				((MDTreeLeafNode<V>)leavesIt.next()).setCompNumber(currentElement.getIndex());
			}								
			
			currentElement = (FactPermElement<V>) currentElement.getRightSibling();				
		}
		
		// Determine the size of each factorizing permutation element.
		FactPermElement<V>[] factPermArray = buildFactPermArray();
		int[] elementSizes = new int[getNumChildren()];			
		for (int i = 0; i < factPermArray.length; i++) {			
			elementSizes[i] = factPermArray[i].getLeaves().size();				
		}
		
		// Add a neighbour every time there is an edge between factorizing permutation 
		// elements on either side of the pivot.
		currentElement = (FactPermElement<V>) getFirstChild();
		while (currentElement != null ) { 
			
			Iterator<RootedTreeNode<V>> leavesIt = 
				currentElement.getLeaves().iterator();
			
			while (leavesIt.hasNext()) {					
				Iterator<MDTreeLeafNode<V>> alphaIt = 
					((MDTreeLeafNode<V>)leavesIt.next()).getAlpha().iterator();
				
				while (alphaIt.hasNext()) {
					currentElement.addNeighbour(factPermArray[alphaIt.next().getCompNumber()]);
				}					
			}
			
			currentElement = (FactPermElement<V>) currentElement.getRightSibling();
		}						
		
		// Replace the edges added above with edges iff a join exists.
		currentElement = (FactPermElement<V>) getFirstChild();
		while (currentElement != null) { 								
			
			// Count the edges added above and remove duplicates.
			Iterator<FactPermElement<V>> neighboursIt = currentElement.getNeighbours().iterator();
			while (neighboursIt.hasNext()) {
				FactPermElement<V> currentNeighbour = neighboursIt.next();
				if (currentNeighbour.isMarked()) {
					neighboursIt.remove();
				}
				currentNeighbour.addMark();										
			}
					
			// Add the edge iff a join is found to exist.
			LinkedList<FactPermElement<V>> newNeighbours = new LinkedList<FactPermElement<V>>();
			neighboursIt = currentElement.getNeighbours().iterator();
			while (neighboursIt.hasNext()) {
				
				FactPermElement<V> currentNeighbour = neighboursIt.next();
				
				int mySize = elementSizes[currentElement.getIndex()];
				int neighbourSize = elementSizes[currentNeighbour.getIndex()];
				
				// There is a join.
				if ((mySize * neighbourSize) == currentNeighbour.getNumMarks()) {
					newNeighbours.add(currentNeighbour);
				}
				
				currentNeighbour.clearMarks();					
			}
			
			currentElement.replaceNeighbours(newNeighbours);
			
			currentElement = (FactPermElement<V>) currentElement.getRightSibling();								
		}		
	}

	
	 /*
	  * For each strong module containing x, inserts a pair of markers to 
	  * delineate the module; one marker is inserted immediately to the left
	  * of the module's left boundary, and another immediately to the right 
	  * of the module's right boundary.  Markers are FactPermElements whose
	  * index is MODULE_BOUNDARY.
	  */
	private void delineate() {
	
		FactPermElement<V> pivotElement = (FactPermElement<V>) pivot.getParent();
		
		// Find the last element in the permutation.
		FactPermElement<V> lastElement = pivotElement;
		while (lastElement.getRightSibling() != null) {
			lastElement = (FactPermElement<V>) lastElement.getRightSibling();
		}
		
		FactPermElement<V> firstElement = (FactPermElement<V>) getFirstChild();
		
		// Current boundaries of module currently being formed.
		FactPermElement<V> left = (FactPermElement<V>) pivotElement.getLeftSibling();
		FactPermElement<V> right = (FactPermElement<V>) pivotElement.getRightSibling();
		
		// The boundaries of the last module created.
		FactPermElement<V> leftLastIn = pivotElement;
		FactPermElement<V> rightLastIn = pivotElement;
			
		// Delineates the modules one at a time.
		while (left != null || right != null) {
			
			boolean seriesModuleFormed = false;
			
			// If a series module is possible, greedily adds the 
			// elements composing it.
			while(left != null && 
					left.getMu().getIndex() <= rightLastIn.getIndex() && 
					!left.hasLeftCoCompFragment()) {				
				seriesModuleFormed = true;
				leftLastIn = left;
				left = (FactPermElement<V>) left.getLeftSibling();			
			}
			
			boolean parallelModuleFormed = false;
				
			// If a parallel module is possible (and a series module has not
			// already been formed), greedily adds the elements composing it.
			while (!seriesModuleFormed && right != null && 
					right.getMu().getIndex() >= leftLastIn.getIndex() && 
					!right.hasRightCompFragment() && !right.hasRightLayerNeighbour()) {			
				parallelModuleFormed = true;
				rightLastIn = right;
				right = (FactPermElement<V>) right.getRightSibling();				
			}	
														
			// Neither a series nor a parallel module could be formed, must
			// then form a prime module (which means neither left nor right will be null),
			// which must contain the first co-component to the left of the pivot.
			LinkedList<FactPermElement<V>> leftQueue = new LinkedList<FactPermElement<V>>();
			if (!seriesModuleFormed && !parallelModuleFormed) {								
				do {
					leftQueue.addLast(left);
					leftLastIn = left;
					left = (FactPermElement<V>) left.getLeftSibling();
				} while (leftLastIn.hasLeftCoCompFragment());									
			}

			LinkedList<FactPermElement<V>> rightQueue = new LinkedList<FactPermElement<V>>();
			boolean hasRightEdge = false;
			
			// Add elements to a prime module one at a time using a forcing
			//  rule (see inner loops' conditional as well as paper).
			while (leftQueue.size() != 0 || rightQueue.size() != 0) {

				// Add elements from the left of the pivot.
				while (leftQueue.size() != 0) {						
					
					FactPermElement<V> currentLeft = leftQueue.remove();					
					
					// Must add all elements up to mu once currentLeft is included
					// in the module.					
					while (currentLeft.getMu().getIndex() > rightLastIn.getIndex()) {										
						
						// Once part of a component is added, all of it must be added.
						do {	
							rightQueue.addLast(right);
							rightLastIn = right;
							right = (FactPermElement<V>) right.getRightSibling();

							if (rightLastIn.hasRightLayerNeighbour()) { hasRightEdge = true; }

						} while (rightLastIn.hasRightCompFragment());						
					}
				}
				
				// Add elements to the right of the pivot.
				while (rightQueue.size() != 0) {
					
					FactPermElement<V> currentRight = rightQueue.remove();
					
					// Must add all elements up to mu once currentRight is included
					// in the module.
					while ( currentRight.getMu().getIndex() < leftLastIn.getIndex()) {

						// Once part of a co-component is added, all of it must be added.
						do {
							leftQueue.addLast(left);
							leftLastIn = left;
							left = (FactPermElement<V>) left.getLeftSibling();
						} while (leftLastIn.hasLeftCoCompFragment());						
					}	
				}
			}
			
			// Added to the module an element to the right of x with an edge to a 
			// layer to its right, so the module must be the entire graph in this case.
			if (hasRightEdge) {
				leftLastIn = firstElement;
				rightLastIn = lastElement;
				left = null;
				right = null;
			}

			// Delineate the module just found.	
			FactPermElement<V> leftBoundary = new FactPermElement<V>(MODULE_BOUNDARY);
			FactPermElement<V> rightBoundary = new FactPermElement<V>(MODULE_BOUNDARY);
			leftBoundary.insertBefore(leftLastIn);
			rightBoundary.insertAfter(rightLastIn);													
		}				
	}
	
	
	/* 
	 * Replaces each tree in this subproblem's forest with a 
	 * FactPermElement object, making the root of the tree the 
	 * child of the FactPermElement.  The new FactPermElements
	 * are numbered from left to right starting at 0, and these
	 * numbers are used as their index. 
	 */
	private void buildPermutation() {
		
		MDTreeNode<V> current = (MDTreeNode<V>) getFirstChild();
		int numFactPermElements = 0;
		
		while (current != null) {
			MDTreeNode<V> next = (MDTreeNode<V>) current.getRightSibling();
			FactPermElement<V> newElement = new FactPermElement<V>(numFactPermElements);
			newElement.insertBefore(current);
			newElement.addChild(current);
			numFactPermElements++;
			current = next;
		}
	}
	
	
	/* 
	 * For each co-component of G[N(X)], determines if some portion of it
	 * appears as part of a factorizing permutation element to its left.
	 */
	private void determineLeftCoCompFragments() {
		
		// We take advantage of the fact that co-components of G[N(x)]
		// appear consecutively and all nodes within them are numbered
		// according to their membership in these co-components.  
		
		FactPermElement<V> current = (FactPermElement<V>) getFirstChild();			
		int lastCompNum = MDTreeNode.DEF_COMP_NUM;
		
		while (current.getFirstChild() != pivot) {
			int currentCompNum = ((MDTreeNode<V>)current.getFirstChild()).getCompNumber();
			if (lastCompNum != MDTreeNode.DEF_COMP_NUM && lastCompNum == currentCompNum) {
				current.setLeftCoCompFragment();
			}
			lastCompNum = currentCompNum;
			current = (FactPermElement<V>) current.getRightSibling();
		}						
	}
	
	
	/* 
	 * For the components of G[N_2] (the vertices distance 2 from x), determines 
	 * if some portion of it appears as part of a factorizing permutation element to 
	 * its right.
	 */
	private void determineRightCompFragments() {
		
		
		// We use an approach similar to that applied in 'determineLeftCoCompFragments'.
		FactPermElement<V> current = (FactPermElement<V>) pivot.getParent().getRightSibling();
		FactPermElement<V> last = null;
		int lastCompNum = MDTreeNode.DEF_COMP_NUM;

		while (current != null) {
			int currentCompNum = ((MDTreeNode<V>)current.getFirstChild()).getCompNumber();
			if (lastCompNum != MDTreeNode.DEF_COMP_NUM && lastCompNum == currentCompNum) {
				last.setRightCompFragment();
			}
			last = current;
			lastCompNum = currentCompNum;
			current = (FactPermElement<V>) current.getRightSibling();
		}			
	}

	
	/* 
	 * For the factorizing permutation elements of G[N_2] (the vertices distance 2 
	 * from x), determines if each has an edge to N_3 (the vertices distance 3 from x).
	 */
	private void determineRightLayerNeighbour() {
		
		FactPermElement<V> current = (FactPermElement<V>) pivot.getParent().getRightSibling();
		while (current != null) {
			
			MDTreeNode<V> currentTree = (MDTreeNode<V>) current.getFirstChild(); 
			int currentTreeNum = currentTree.getTreeNumber();
			
			Iterator<RootedTreeNode<V>> currentLeavesIt = currentTree.getLeaves().iterator();				
			while (currentLeavesIt.hasNext()) {
				
				Iterator<MDTreeLeafNode<V>> alphaIt = ((MDTreeLeafNode<V>) currentLeavesIt.next()).getAlpha().iterator();
				while (alphaIt.hasNext()) {
					if (alphaIt.next().getTreeNumber() > currentTreeNum) {
						current.setHasRightLayerNeighbour();
					}
				}
			}
			
			current = (FactPermElement<V>) current.getRightSibling();
		}
	}
	

	/* 
	 * All nodes labelled by one of the two split marks are promoted
	 * to depth-0 in this subproblem's forest.  First the nodes marked 
	 * by left splits are promoted, then those marked by right splits.  
	 * Nodes without children or only a single child are deleted, and in 
	 * the latter instance replaced by their lone child. 
	 * Precondition: if a node 'n' has a split mark of type 'x', then
	 * all its ancestors in the forest also have a split mark of type 'x'.
	 */
	private void promotion() {
		promoteOneDirection(SplitDirection.LEFT);
		promoteOneDirection(SplitDirection.RIGHT);			
		clearSplitMarks();
	}

	
	/* 
	 * All nodes labelled by the supplied split mark are promoted
	 * to depth-0 in this subproblem's forest.
	 * Nodes without children or only a single child are deleted, and in 
	 * the latter instance replaced by their lone child.
	 * @param splitType the type of split mark nodes must have to be
	 * promoted.  
	 * Precondition: If a node is marked by the supplied type, then all its
	 * ancestors must also be marked by this type.
	 */
	private void promoteOneDirection(SplitDirection splitType) {
		MDTreeNode<V> current = (MDTreeNode<V>) getFirstChild();
		while (current != null) {
			MDTreeNode<V> next = (MDTreeNode<V>) current.getRightSibling();
			current.promote(splitType);
			current = next;
		}
	}
	
	
	/* 
	 * Removes all the split marks from the nodes in this 
	 * subproblem's forest.
	 */
	private void clearSplitMarks() {
		MDTreeNode<V> current = (MDTreeNode<V>) getFirstChild();
		while (current != null) {
			MDTreeNode<V> next = (MDTreeNode<V>) current.getRightSibling();
			current.clearSplitMarksForSubtree();
			current = next;
		}			
	}

	
	/* Every vertex in this subproblem uses its active edges to refine
	 * the recursively computed MD trees other than its own.
	 */
	private void refinement() {
		Iterator<RootedTreeNode<V>> leafIt = getLeaves().iterator();
		while(leafIt.hasNext()) { 
			refineWith((MDTreeLeafNode<V>) leafIt.next()); 
		}		
	}

	
	/* 
	 * Effects the changes that result from a single vertex refining
	 * with its active edges.
	 */
	private void refineWith(MDTreeLeafNode<V> refiner) {
				
		Collection<MDTreeNode<V>> subtreeRoots = 
			getMaxSubtrees(refiner.getAlpha());
				
		Collection<MDTreeNode<V>> siblingGroups = groupSiblingNodes(subtreeRoots);
				
		// Remove roots of trees.
		Iterator<MDTreeNode<V>> sibGroupsIt = siblingGroups.iterator();
		while (sibGroupsIt.hasNext()) {
			if (sibGroupsIt.next().isRoot()) {
				sibGroupsIt.remove();
			}
		}

		
		// Split trees when sibling groups are children of the root, and split
		// nodes when not.  In the latter case, mark the two nodes resulting from
		// the split, plus all their ancestors as having been marked, also mark the
		// children of all prime ancestors.
		sibGroupsIt = siblingGroups.iterator();			
		while (sibGroupsIt.hasNext()) {
						
			MDTreeNode<V> current = sibGroupsIt.next();

			// Determine the split type.
			int pivotTreeNumber = pivot.getTreeNumber();
			int refinerTreeNumber = refiner.getTreeNumber();				
			int currentTreeNumber = current.getTreeNumber();				
			SplitDirection splitType;
			if (currentTreeNumber < pivotTreeNumber ||
					refinerTreeNumber < currentTreeNumber) {
				splitType = SplitDirection.LEFT;
			}
			else {
				splitType = SplitDirection.RIGHT;
			}				
															
			MDTreeNode<V> currentParent = (MDTreeNode<V>) current.getParent();			
			MDTreeNode<V> newSibling;
			
			// Parent is a root, must split the tree.
			if (currentParent.isRoot()) {
				if (splitType == SplitDirection.LEFT) {
					current.insertBefore(currentParent);						
				}
				else {
					current.insertAfter(currentParent);						
				}
				
				newSibling = currentParent; 
				
				if (currentParent.hasOnlyOneChild()) {
					currentParent.replaceThisByItsChildren();
				}	
				if (currentParent.hasNoChildren()) {
					currentParent.remove();
				}
			}
			// Parent is not a root, must split the node.
			else {					
																		
				current.remove();				
									
				if (currentParent.hasOnlyOneChild()) { 
					newSibling = (MDTreeNode<V>)currentParent.getFirstChild();
					currentParent.addChild(current);						
				}
				else {
					
					// To achieve linear time, must reuse the parent node to 
					// represent the non-neighbour partition.  See pivot() for another
					// example of this trick.
					MDTreeNode<V> replacement = new MDTreeNode<V>(currentParent);
					currentParent.replaceWith(replacement);												
					replacement.addChild(current);
					replacement.addChild(currentParent);
					newSibling = currentParent; 
				}							
			}	
			
			current.addSplitMark(splitType);
			newSibling.addSplitMark(splitType);			
			current.markAncestorsBySplit(splitType);
			newSibling.markAncestorsBySplit(splitType);						
		}	
	}

	
	/* 
	 * Takes the collection of supplied nodes and makes those that are siblings
	 * in one of this subproblem's recursively computed MD trees
	 * the children of a new node inserted in their place.  New nodes inserted
	 * have the same attributes as their parents.  Nodes in the collection
	 * without siblings are left unchanged.
	 * @param nodes The collection of nodes to be grouped as siblings.
	 * @return A collection consisting of the supplied nodes without siblings
	 * and the new nodes inserted in place of siblings.
	 */
	private Collection<MDTreeNode<V>> groupSiblingNodes(
			Collection<MDTreeNode<V>> nodes) {

		// Moves non-root nodes to front of parent's child list.  Marks each node
		// and marks their parents.  Parents are marked once for each child node.
		LinkedList<MDTreeNode<V>> parents = new LinkedList<MDTreeNode<V>>();
		Iterator<MDTreeNode<V>> nodesIt = nodes.iterator();
		while (nodesIt.hasNext()) {
			
			MDTreeNode<V> current = (MDTreeNode<V>) nodesIt.next();
			current.addMark();
			
			if (!current.isRoot()) {

				current.makeFirstChild();
				
				MDTreeNode<V> currentParent = (MDTreeNode<V>) current.getParent();												
				
				if (!currentParent.isMarked()) {
					parents.add(currentParent);												
				}
				
				currentParent.addMark();
			}
		}	

		// Collects the sibling groups formed.
		LinkedList<MDTreeNode<V>> siblingGroups = new LinkedList<MDTreeNode<V>>();

		// First, trivial case of nodes without siblings, meaning...
		// ...the roots of trees...
		nodesIt = nodes.iterator();
		while (nodesIt.hasNext()) {
			
			MDTreeNode<V> current = nodesIt.next();
			
			if (current.isRoot()) {
				
				//This line is new
				current.clearMarks();
				
				siblingGroups.add(current);				
			}
		}						
		//...and the non-root nodes without siblings.
		Iterator<MDTreeNode<V>> parentsIt = parents.iterator();		
		while (parentsIt.hasNext()) {				
			
			MDTreeNode<V> current = parentsIt.next();
			
			if (current.getNumMarks() == 1) {																					
				parentsIt.remove();
				current.clearMarks();				
				((MDTreeNode<V>)current.getFirstChild()).clearMarks();
				siblingGroups.add((MDTreeNode<V>)current.getFirstChild());
			}
		}						
		
		// Next, group sibling nodes as children of a new node inserted
		// in their place.
		parentsIt = parents.iterator();
		while(parentsIt.hasNext()) {								
											
			MDTreeNode<V> currentParent = parentsIt.next();
			
			currentParent.clearMarks();

			MDTreeNode<V> groupedChildren = new MDTreeNode<V>(currentParent);				
			MDTreeNode<V> currentChild = 
				(MDTreeNode<V>) currentParent.getFirstChild();								
			while (currentChild != null && currentChild.isMarked()) {

				MDTreeNode<V> nextChild = 
					(MDTreeNode<V>) currentChild.getRightSibling();
				
				currentChild.clearMarks();
			
				groupedChildren.addChild(currentChild);
				
				currentChild = nextChild;															
			}
						
			currentParent.addChild(groupedChildren);
			siblingGroups.add(groupedChildren);
		}
						
		return siblingGroups;
	}

	
	/* 
	 * Finds the set of maximal subtrees of this subproblem's recursively
	 * computed forest of MD trees where the leaves of each subtree are
	 * members of the supplied collection of vertices.
	 * @param leaves The collection of vertices defining the maximal subtrees.
	 * @return A collection of the roots of each maximal subtree.
	 */
	private Collection<MDTreeNode<V>> getMaxSubtrees(Collection<MDTreeLeafNode<V>> leaves) {
		
		LinkedList<MDTreeNode<V>> active =
			new LinkedList<MDTreeNode<V>>(leaves);
		ListIterator<MDTreeNode<V>> activeIt = active.listIterator();
		
		LinkedList<MDTreeNode<V>> discharged = new LinkedList<MDTreeNode<V>>();
		
		// Marking process: all nodes in maximal subtrees fully marked;
		// the only other marked nodes are parents of roots of maximal
		// subtrees, and these are partially marked.
		while (activeIt.hasNext()) {
			while(activeIt.hasNext()) {			

				MDTreeNode<V> current = activeIt.next();
				activeIt.remove();
				
				if (!current.isRoot()) {
					MDTreeNode<V> currentParent = (MDTreeNode<V>) current.getParent();
					
					currentParent.addMark();
					
					if (currentParent.isFullyMarked()) {						
						activeIt.add(currentParent);
					}
				}

				discharged.add(current);
			}
			activeIt = active.listIterator();
		}		
		
		// Removes marks on all nodes; leaves discharged list so that it
		// only holds roots of maximal subtrees.
		ListIterator<MDTreeNode<V>> dischargedIt = discharged.listIterator();
		while(dischargedIt.hasNext()) {
		
			MDTreeNode<V> current = dischargedIt.next();
			current.clearMarks();
			
			if (!current.isRoot()) {
				MDTreeNode<V> currentParent = (MDTreeNode<V>) current.getParent();
				
				if (currentParent.isFullyMarked()) {
					dischargedIt.remove();
				}
				else {
					currentParent.clearMarks();
				}						
			}				
		}
			
		return discharged;
	}
	
	
	/* 
	 * Replaces the subproblems of this subproblem with their 
	 * recursively computed solutions (i.e. replaces the layers with
	 * their MD Trees). 
	 */
	private void removeLayers() {
		RecSubProblem<V> currentLayer = (RecSubProblem<V>)getFirstChild();
		while (currentLayer != null) {
			RecSubProblem<V> nextLayer = 
				(RecSubProblem<V>)currentLayer.getRightSibling();
			currentLayer.replaceWith(currentLayer.getFirstChild());
			currentLayer = nextLayer;
		}	
	}

	
	/* 
	 * This subproblem's recursively computed MD trees are numbered 
	 * one by one, starting at 0 for the tree to the left of x; every
	 * node in the tree is assigned that tree's number.
	 */
	private void numberByTree() {
		int treeNumber = 0;
		MDTreeNode<V> currentRoot = (MDTreeNode<V>)getFirstChild();
		while (currentRoot != null) {
			currentRoot.setTreeNumForSubtree(treeNumber);
			currentRoot = (MDTreeNode<V>) currentRoot.getRightSibling();
			treeNumber++;
		}			
	}

	
	/* 
	 * Numbers the nodes in the recursively computed MD trees for this subproblem.
	 * Nodes in the tree to the left of x are numbered by co-component and those
	 * in trees to the right of x are numbered by component.  The numbering starts
	 * at 0, and the tree to the left of x is considered first.  All nodes in a
	 * particular (co-)component receive the same number, which is one more than
	 * the previoue (co-)component.  The roots of trees are therefore left 
	 * unnumbered sometimes.
	 */
	private void numberByComp() {

		int compNumber = 0;
		boolean afterPivot = false;
		
		MDTreeNode<V> currentRoot = (MDTreeNode<V>) getFirstChild();
		while (currentRoot != null) {
			
			if (currentRoot == pivot) { afterPivot = true; }
						
			if (afterPivot) { 
				compNumber += currentRoot.numberByComp(compNumber);
			}
			else {
				compNumber += currentRoot.numberByCoComp(compNumber);
			}
			
			currentRoot = (MDTreeNode<V>) currentRoot.getRightSibling();
		}			
	}
	

	/* 
	 * For each vertex x in this subproblem, looks at alpha(x), and if 
	 * y \in alpha(x), adds x to alpha(y).
	 * Postcondition: no alpha-list contains duplicate entries.  
	 */
	private void completeAlphaLists() {
		
		// Completes the list (possibly creating duplicate entries within them).
		Iterator<RootedTreeNode<V>> leavesIt = getLeaves().iterator();
		while (leavesIt.hasNext()) {
			MDTreeLeafNode<V> currentLeaf = (MDTreeLeafNode<V>) leavesIt.next();
			Iterator<MDTreeLeafNode<V>> alphaIt = currentLeaf.getAlpha().iterator(); 
			while (alphaIt.hasNext()) {
				alphaIt.next().addToAlpha(currentLeaf);
			}
		}
		
		// Removes duplicate entries in the lists.
		leavesIt = getLeaves().iterator();
		while (leavesIt.hasNext()) {
			MDTreeLeafNode<V> currentLeaf = (MDTreeLeafNode<V>) leavesIt.next();
			Iterator<MDTreeLeafNode<V>> alphaIt = currentLeaf.getAlpha().iterator();
			while (alphaIt.hasNext()) {
				MDTreeLeafNode<V> currentAlphaNeighbour = alphaIt.next();
				if (currentAlphaNeighbour.isMarked()) {
					alphaIt.remove();
				}
				else {
					currentAlphaNeighbour.addMark();
				}
			}
			
			alphaIt = currentLeaf.getAlpha().iterator();
			while (alphaIt.hasNext()) {
				alphaIt.next().clearMarks();
			}
		}
	}

	
	/* 
	 * Determines if this subproblem's graph has more than one component and removes
	 * them if it does.
	 * @ return If more than one component exists, returns the root of the recursively 
	 * computed MD tree for the graph consisting of all but the first component.  
	 * Returns null otherwise.
	 */
	private MDTreeNode<V> removeExtraComponents() {			
		RecSubProblem<V> currentSubProblem = (RecSubProblem<V>) getFirstChild();
		
		while (currentSubProblem != null && 
				currentSubProblem.isConnected()) {
			currentSubProblem = 
				(RecSubProblem<V>) currentSubProblem.getRightSibling();
		}
		if (currentSubProblem != null) {
			currentSubProblem.remove(); 
			MDTreeNode<V> root = (MDTreeNode<V>)currentSubProblem.getFirstChild();
			root.remove();
			return root;
		}
		else { return null; }
	}

	
	/* 
	 * Selects a pivot vertex from this recursive subproblem and partitions
	 * the recursion tree according to its neighbours.
	 * Has the side effect that this recursive subproblem will no longer
	 * be the current recursive subproblem, which is necessary to achieve 
	 * linear time; returns the new current recursive subproblem.
	 * @return The current recursive subproblem.
	 */
	private RecSubProblem<V> pivot() {
					
		MDTreeLeafNode<V> pivot = (MDTreeLeafNode<V>) getFirstChild();
		pivot.setVisited();
	 
		RecSubProblem<V> neighbourProblem = processNeighbours(pivot);
						
		pivot.remove();
		RecSubProblem<V> pivotProblem = new RecSubProblem<V>(pivot);
		
		// Pivot forms part of first connected component of the
		// current subproblem's graph.
		pivotProblem.connected = true;
		
		// Replace the current recursive subproblem with something new,
		// but sharing the same attributes.  See comment below regarding
		// reuse of current recursive subproblem.
		RecSubProblem<V> replacement = new RecSubProblem<V>(this);		
		replaceWith(replacement);
		replacement.pivot = pivot;
		
		// Must reuse the current recursive subproblem to act as non-neighbour
		// partition of the current recursive subproblem in order to achieve
		// linear-time.		
		if (!this.hasNoChildren()) { 
			this.clearAttributes();
			replacement.addChild(this); 
		}
		
		replacement.addChild(pivotProblem);
		
		if (!neighbourProblem.hasNoChildren()) { 
			
			// Neighbours connected to pivot and so also part of first
			// connected component of current subproblem's graph.
			neighbourProblem.connected = true;
			
			replacement.addChild(neighbourProblem);
		}
			
		return replacement;
	}

	
	/* 
	 * Refines the subproblems of the recursion tree according to the
	 * neighbourhood of a pivot.  If a neighbour has already been visited,
	 * adds the pivot to that neighbour's alpha-list.  
	 * @param pivot The vertex whose neighbours dictate the partitioning.
	 * @return A subproblem consisting of the neighbours of pivot in the 
	 * same subproblem as pivot.
	 */
	private RecSubProblem<V> processNeighbours(MDTreeLeafNode<V> pivot) {
					
		Iterator<MDTreeLeafNode<V>> pivNeighsIt = pivot.getNeighbours().iterator();
		
		RecSubProblem<V> neighbourProblem = new RecSubProblem<V>();
							
		while(pivNeighsIt.hasNext()) {
			
			MDTreeLeafNode<V> currentNeighbour = pivNeighsIt.next();
			
			if (currentNeighbour.isVisited()) {
				currentNeighbour.addToAlpha(pivot);					
			}
			else if (currentNeighbour.getParent() == pivot.getParent()) {
				neighbourProblem.addChild(currentNeighbour);
			}
			else {
				pullForward(currentNeighbour);
			}
		}
		return neighbourProblem;
	}

	
	/* 
	 * Determines which of the following three cases applies:
	 * (1) the given vertex must be moved forward from its current
	 * current subproblem to the immediately preceding subproblem (i.e. it
	 * is found to occupy the previous layer);
	 * (2) a new subproblem must be formed consisting of the given vertex
	 * and placed immediately before its current subproblem (i.e. a new layer
	 * must be formed initially consisting of only this vertex);
	 * (3) the recursion tree remains unchanged.
	 * In the first two cases it effects the necessary changes.
	 * @param The vertex specified in cases (1) and (2) above.
	 */
	private void pullForward(MDTreeLeafNode<V> leaf) {
		
		RecSubProblem<V> currentLayer = (RecSubProblem<V>) leaf.getParent();
		
		if (currentLayer != null && currentLayer.isConnected()) { return; }					
		RecSubProblem<V> prevLayer = (RecSubProblem<V>) currentLayer.getLeftSibling();
		
		// A new layer must be formed.
		if (prevLayer != null && 
				(prevLayer.isActive() || prevLayer.isPivotLayer())) {
			prevLayer = new RecSubProblem<V>();
			prevLayer.insertBefore(currentLayer);
			
			// The new layer is connected to the first component in its
			// subproblem through the pivot.
			prevLayer.connected = true;
		}
		
		if (prevLayer != null && prevLayer.isConnected()) {
			prevLayer.addChild(leaf);	
		}		
		
		if (currentLayer.hasNoChildren()) {
			currentLayer.remove();
		}
	}

	
	/* Is this subproblem the one containing its parent subproblem's pivot? */
	private boolean isPivotLayer() {
		return (((RecSubProblem<V>)getParent()).pivot == getFirstChild());
	}

	
	/* Is this subproblem currently being solved recursively? */
	private boolean isActive() {
		return active;
	}
	
	
	/* 
	 * Is the current subproblem part of its parent subproblem's first
	 * connected component?
	 */ 
	private boolean isConnected() {
		return connected;
	}

	
	/* 
	 * A string representation of this subproblem.  The children subtrees of this
	 * subproblem are converted to a string and separated by square brackets.  
	 * The pivot subproblem is signalled.
	 */
	public String toString() {
		
		String result = "[";
		
		RootedTreeNode<V> current = getFirstChild();
		if (current != null) {
			if (current.getFirstChild() == pivot) { result += ", PIVOT="; }
			result += current; 
			current  = current.getRightSibling(); 
		}
		while (current != null) {
			if (current.getFirstChild() == pivot) { result += ", PIVOT=" + current; }
			else { result += "," + current; }
			current = current.getRightSibling();
		}
		return result + "]";
	}
}
