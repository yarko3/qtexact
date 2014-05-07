package jimsFiles;

import java.util.ArrayList;

import edu.uci.ics.jung.graph.Graph;


public class CoTree {

	private node root;
	private int numVertices;
	
	public CoTree () {
		setRoot(new node());
		numVertices = 0;
	}

	public void setRoot(node root) {
		this.root = root;
	}

	public node getRoot() {
		return root;
	}

	public void add(Graph<Integer,String> g,Integer v){
		if (numVertices == 0) setRoot(new node(v));
		else {
			for (Integer nbr : g.getNeighbors(v)) {
				mark(nbr);
			}
		}
	}
	
	
	private void mark(Integer nbr) {
		// TODO Auto-generated method stub
		
	}


	protected class node {

		private ArrayList<node> children;
		private node parent;
		private int operator; // 0 for disjoint union, 1 for complete join
		private int vertexValue; // vertex label in the case that it is a leaf
		private boolean isLeaf;
		public int numChildren;
		public int numMarked;
		public boolean isMarked;
		
		public node (Integer v) {
			children = new ArrayList<node>(0);
			setParent(null);
			setOperator(-1);
			setValue(0);
			isLeaf=false;
			numChildren=0;
			numMarked=0;
			isMarked=false;
			vertexValue = v;
		}

		public node () {
			children = new ArrayList<node>(0);
			setParent(null);
			setOperator(-1);
			setValue(0);
			isLeaf=false;
			numChildren=0;
			numMarked=0;
			isMarked=false;
		}
		
		
		public void setParent(node parent) {
			this.parent = parent;
		}

		public node getParent() {
			return parent;
		}

		public void setOperator(int operator) {
			this.operator = operator;
		}

		public int getOperator() {
			return operator;
		}

		public void setValue(int v) {
			this.vertexValue = v;
		}

		public int getValue() {
			return vertexValue;
		}

		public void addChild(node child) {
			this.children.add(child);
		}

		public ArrayList<node> getChildren() {
			return children;
		}
	
		public void mark() {
			isMarked = true;
		}
		
		public void unMark() {
			isMarked = false;
		}
	}

}