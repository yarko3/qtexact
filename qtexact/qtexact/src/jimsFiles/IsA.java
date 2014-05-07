package jimsFiles;

//import java.util.Collection;
//import java.util.HashSet;
import java.util.ArrayList;
import java.util.Iterator;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;

// A collection of static recognition methods for various graph classes

public class IsA {

	public static boolean Cograph(Graph<Integer,String> g){

		// Going to pick a vertex = a
		// Find its neighbours = b
		// Find a neighbour of b which is not a neighbour of a = c
		// Find a neighbour of c which is not a neighbour of b or a = d = P4

		Iterator<Integer> a = g.getVertices().iterator();
		while(a.hasNext()){
			Integer A = a.next();
			Iterator<Integer> b = g.getNeighbors(A).iterator();
			while(b.hasNext()){ 
				Integer B = b.next();
				Iterator<Integer> c = g.getNeighbors(B).iterator();
				while (c.hasNext()){
					Integer C = c.next();
					if (g.isNeighbor(C, A) ) continue;
					Iterator<Integer> d = g.getNeighbors(C).iterator();
					while (d.hasNext()){
						Integer D = d.next();
						if (g.isNeighbor(D,A)) continue;
						if (g.isNeighbor(D,B)) continue;
						//otherwise, we have a P4
						//System.out.print("Found P4: "+A+"-"+B+"-"+C+"-"+D+"... not a cograph\n");
						return false; // not a Cograph
					} // end d.hasNext()
				} // end c.hasNext()
			} // end b.hasNext() 
		} // end a.hasNext()
			
	System.out.print("It is a cograph\n");
	return true; // No P4 found - return true
	}

	public static boolean Prime (Graph<Integer,String> g) {
		// takes an input graph G and if it contains a nontrivial module,
		// it will return false. Otherwise, the graph is prime and returns true.
		// Algorithm: starting from every pair of vertices, will find partial
		// closure. If the end result is less than the size of G, return false.
		
		ArrayList<Integer> module;
		
		for (Integer u : g.getVertices()) {
			for (Integer v : g.getVertices()) {
				if (u <= v) continue; // symmetry breaking
				module = new ArrayList<Integer>();
				module.add(u);
				module.add(v);
				System.out.print("Initial pair is:\n"+u+" with neighbours " + g.getNeighbors(u)+"\n"+v+" with neighbours "+ g.getNeighbors(v)+"\n" );
				
				if (PartialClosure(module,g).size() != g.getVertexCount()) {
					System.out.print("The graph is not prime. The partial closure of");
					System.out.print(" "+u+" "+v+" is the module:\n" + PartialClosure(module,g)+"\n");
					return false;
				}				
			}
		}
		return true;
		
	}
	
	private static ArrayList<Integer> PartialClosure (ArrayList<Integer> module, Graph<Integer,String> g) {
		// - Input arrayList is a subset of vertices of g
		// - returns the partial closure of the arrayList in the form of another arrayList
		ArrayList<Integer> closure = new ArrayList<Integer>();
		for (int i=0; i<module.size(); i++) {
			closure.add(module.get(i));
		}
		boolean moreToDo = true;
		while (moreToDo) {
			moreToDo = false;
			for (Integer x : g.getVertices()) {
				if (closure.contains(x)) continue;
				if (isPartial(x,closure,g)) {
					closure.add(x);
					System.out.print("Found "+x+" to be partial on set "+ closure+"\n");
					moreToDo = true;
				}
			}
		}
		
		return closure;
	}
	
	private static boolean isPartial(Integer x, ArrayList<Integer> module,
			Graph<Integer, String> g) {
		// checks if vertex x is partial on the set "module" in the graph "g"
		
		if (module.size() == 0) {
			System.out.print("Critical error! empty module passed to closure\n");
			return false;
		}
		
		if (g.isNeighbor(module.get(0), x)) {
			// is a neighbour of module(0) so...
			// find non-neighbour to return true;
			for (int i=1; i<module.size(); i++) {
				if (!g.isNeighbor(module.get(i), x)) return true;
			}
		}
		else {
			// x is not a neighbour of module(0) so...
			// find a neighbour to return true;
			for (int i=1; i<module.size(); i++) {
				if (g.isNeighbor(module.get(i), x)) return true;
			}
		}
		// x was not found to be partial on the set "module"
		return false;
	}

	public static boolean Simplicial(Graph<Integer,String> g, Integer i){
		// determines whether a vertex is simplicial in graph g
		Iterator<Integer> a = g.getNeighbors(i).iterator();
		while (a.hasNext()){
			Integer A = a.next();
			Iterator<Integer> b = g.getNeighbors(i).iterator();
			while(b.hasNext()){
				Integer B = b.next();
				if (B>A){
					if (!g.isNeighbor(A, B)) return false;
				}
			}
		}
		return true;
	}
	
	public static boolean SixTwo(Graph<Integer,String> g) {
		// Tests every set of 6 distinct vertices. If some 6-set contains
		// more than 2 P4s, return false.
		
		int n = g.getVertexCount();
		int totalP4;
		int[] v = new int[n]; // maps the vertices to 0..n-1
		int a,b,c,d,e,f;
		for (a=0; a<n;a++) {
			for (b=a+1; b<n; b++) {
				for (c=b+1;c<n; c++){
					for(d=c+1;d<n;d++){
						for(e=d+1;e<n;e++){
							for(f=e+1;f<n;f++){
								totalP4 = countP4s(g,v[a],v[b],v[c],v[d],v[e],v[f]);
								if (totalP4 > 2) return false;
							}
						}
					}
				}
			}
		}
		return true;
		
	}
	
	private static int countP4s(Graph<Integer, String> g, int a, int b, int c,
			int d, int e, int f) {
		
		int tot = 0;
		tot = isP4(g, a,b,c,d)+
		isP4(g,a,b,c,e)+
		isP4(g,a,b,c,f)+
		isP4(g,a,b,d,e)+
		isP4(g,a,b,d,f)+
		isP4(g,a,b,e,f)+
		isP4(g,a,c,d,e)+
		isP4(g,a,c,d,f)+
		isP4(g,a,c,e,f)+
		isP4(g,a,d,e,f)+
		isP4(g,b,c,d,e)+
		isP4(g,b,c,d,f)+
		isP4(g,b,c,e,f)+
		isP4(g,b,d,e,f)+
		isP4(g,c,d,e,f);
		return tot;
		
	}

	public static int isP4(Graph<Integer, String> g, int a, int b, int c, int d) {
		// tests if a,b,c,d induce a P4 in g.
		// P4 is the only graph with sum(deg)=6 and sum(deg^2)=10;
		
		int dega = 0, degb=0, degc=0, degd=0;
		int m=0; // counts edges
		if (g.isNeighbor(a, b) == true) {
			dega++;
			degb++;
			m++;
		}
		if (g.isNeighbor(a, c) == true) {
			dega++;
			degc++;
			m++;
		}
		if (g.isNeighbor(a, d) == true) {
			dega++;
			degd++;
			m++;
		}
		if (g.isNeighbor(b, c) == true) {
			degb++;
			degc++;
			m++;
		}
		if (g.isNeighbor(b, d) == true) {
			degb++;
			degd++;
			m++;
		}
		if (g.isNeighbor(c, d) == true) {
			degc++;
			degd++;
			m++;
		}
	
		if (m==3 && dega*dega+degb*degb+degc*degc+degd*degd == 10) return 1; // true
		else return 0; // not a P4
	}

	public static int isP4orC4(Graph<Integer, String> g, int a, int b, int c, int d) {
		// tests if a,b,c,d induce a P4 or C4.
		// P4 is the only graph with sum(deg)=6 and sum(deg^2)=10;
		// if the sum(deg) = 8, then check if 4-cycle
		
		int dega = 0, degb=0, degc=0, degd=0;
		int m=0; // counts edges
		if (g.isNeighbor(a, b) == true) {
			dega++;
			degb++;
			m++;
		}
		if (g.isNeighbor(a, c) == true) {
			dega++;
			degc++;
			m++;
		}
		if (g.isNeighbor(a, d) == true) {
			dega++;
			degd++;
			m++;
		}
		if (g.isNeighbor(b, c) == true) {
			degb++;
			degc++;
			m++;
		}
		if (g.isNeighbor(b, d) == true) {
			degb++;
			degd++;
			m++;
		}
		if (g.isNeighbor(c, d) == true) {
			degc++;
			degd++;
			m++;
		}
	
		if (m==3 && dega*dega+degb*degb+degc*degc+degd*degd == 10) return 1; // true
		if (m==4 && dega==2 && degb == 2 && degc == 2 && degd == 2) return 1; 
		else return 0; // not a P4
	}
	
	public static boolean isC5(Graph<Integer,String> g,int a, int b, int c, int d, int e){
		// counts the degrees of each vertex in the induced subgraph on abcde
		// if all degrees are 2, then we must have a C5 (for simple graphs.)
		int[] deg = {0,0,0,0,0};
		
		if (g.isNeighbor(a, b)) {
			deg[0]++;
			deg[1]++;
		}
		if (g.isNeighbor(a, c)) {
			deg[0]++;
			deg[2]++;
		}
		if (g.isNeighbor(a, d)) {
			deg[0]++;
			deg[3]++;
		}
		if (g.isNeighbor(a, e)) {
			deg[0]++;
			deg[4]++;
		}
		if (g.isNeighbor(b, c)) {
			deg[1]++;
			deg[2]++;
		}
		if (g.isNeighbor(b, d)) {
			deg[1]++;
			deg[3]++;
		}
		if (g.isNeighbor(b, e)) {
			deg[1]++;
			deg[4]++;
		}
		if (g.isNeighbor(c, d)) {
			deg[2]++;
			deg[3]++;
		}
		if (g.isNeighbor(c, e)) {
			deg[2]++;
			deg[4]++;
		}
		if (g.isNeighbor(d, e)) {
			deg[3]++;
			deg[4]++;
		}
	
		for (int i=0; i<5; i++) {
			if (deg[i] != 2) return false;
		}
		return true;
	}

	public static boolean isP5(Graph<Integer,String> g,int a, int b, int c, int d, int e){
		// counts the degrees of each vertex in the induced subgraph on abcde
		// if three degrees are 2, and two are 1, then we either have
		// a P5 or a K3+e. We verify that the two vertices of deg1 are nonadj to
		// confirm that it is a P5.

		int[] deg = {0,0,0,0,0};
		int first, second; // the vertices of deg 1
		
		if (g.isNeighbor(a, b)) {
			deg[0]++;
			deg[1]++;
		}
		if (g.isNeighbor(a, c)) {
			deg[0]++;
			deg[2]++;
		}
		if (g.isNeighbor(a, d)) {
			deg[0]++;
			deg[3]++;
		}
		if (g.isNeighbor(a, e)) {
			deg[0]++;
			deg[4]++;
		}
		if (g.isNeighbor(b, c)) {
			deg[1]++;
			deg[2]++;
		}
		if (g.isNeighbor(b, d)) {
			deg[1]++;
			deg[3]++;
		}
		if (g.isNeighbor(b, e)) {
			deg[1]++;
			deg[4]++;
		}
		if (g.isNeighbor(c, d)) {
			deg[2]++;
			deg[3]++;
		}
		if (g.isNeighbor(c, e)) {
			deg[2]++;
			deg[4]++;
		}
		if (g.isNeighbor(d, e)) {
			deg[3]++;
			deg[4]++;
		}
	
		int numDegTwo = 0;
		first = -1;
		second = -1;
		for (int i=0; i<5; i++) {
			if (deg[i] == 2) numDegTwo += 1;
			if (deg[i] == 1) {
				if (first == -1) first = i;
				else second = i;
			}
		}
		if (first == 0) first = a;
		if (first == 1) first = b;
		if (first == 2) first = c;
		if (first == 3) first = d;
		if (first == 4) first = e;
		if (second == 0) second = a;
		if (second == 1) second = b;
		if (second == 2) second = c;
		if (second == 3) second = d;
		if (second == 4) second = e;
		
		if (numDegTwo != 3) return false;
		// at this point, we know we have 3 vertices of deg 2
		
		if (second == -1) return false;
		// at this point, we additionally know we have at least two vertices of deg 1
		// verify that the two vertices of deg1 are not adjacent
		//System.out.print("f s = "+first+" "+second+"\n");
		if (g.isNeighbor(first,second)) return false;
			
		return true;
	}

	public static boolean isHouse(Graph<Integer,String> g,int a, int b, int c, int d, int e){
		// counts the degrees of each vertex in the induced subgraph on abcde
		// needs 2 vertices of degree 3 and 3 vertices of deg 2, and the two vertices
		// of degree 3 must be adjacent to each other
		
		int[] deg = {0,0,0,0,0};
		int first, second; // the vertices of deg 1
		
		if (g.isNeighbor(a, b)) {
			deg[0]++;
			deg[1]++;
		}
		if (g.isNeighbor(a, c)) {
			deg[0]++;
			deg[2]++;
		}
		if (g.isNeighbor(a, d)) {
			deg[0]++;
			deg[3]++;
		}
		if (g.isNeighbor(a, e)) {
			deg[0]++;
			deg[4]++;
		}
		if (g.isNeighbor(b, c)) {
			deg[1]++;
			deg[2]++;
		}
		if (g.isNeighbor(b, d)) {
			deg[1]++;
			deg[3]++;
		}
		if (g.isNeighbor(b, e)) {
			deg[1]++;
			deg[4]++;
		}
		if (g.isNeighbor(c, d)) {
			deg[2]++;
			deg[3]++;
		}
		if (g.isNeighbor(c, e)) {
			deg[2]++;
			deg[4]++;
		}
		if (g.isNeighbor(d, e)) {
			deg[3]++;
			deg[4]++;
		}
	
		int numDegTwo = 0;
		first = -1;
		second = -1;

		for (int i=0; i<5; i++) {
			if (deg[i] == 2) numDegTwo += 1;
			if (deg[i] == 3) {
				if (first == -1) first = i;
				else second = i;
			}
		}

		if (first == 0) first = a;
		if (first == 1) first = b;
		if (first == 2) first = c;
		if (first == 3) first = d;
		if (first == 4) first = e;
		if (second == 0) second = a;
		if (second == 1) second = b;
		if (second == 2) second = c;
		if (second == 3) second = d;
		if (second == 4) second = e;

		
		if (numDegTwo != 3) return false;
		// at this point in the code, we know we have 3 vertices of deg 2
		
		if (second == -1) return false;
		// at this point, we additionally know we have at least two vertices of deg 3
		// verify that the two vertices of deg3 are adjacent
		
		if (!g.isNeighbor(first,second)) return false;
			
		return true;
	}

	public static boolean CPP(Graph<Integer,String> g) {
		for (int a : g.getVertices()) {
			for (int b : g.getVertices()) {
				if (b<=a) continue;
				for (int c : g.getVertices()) {
					if (c<=a) continue;
					if (c<=b) continue;				
					for (int d : g.getVertices()) {
						if (d<=a) continue;
						if (d<=b) continue;
						if (d<=c) continue;
						for (int e : g.getVertices()) {
							if (e<=a) continue;
							if (e<=b) continue;
							if (e<=c) continue;
							if (e<=d) continue;
							if (isP5(g,a,b,c,d,e)) return false;
							if (isC5(g,a,b,c,d,e)) return false;
							if (isHouse(g,a,b,c,d,e)) return false;							
						} //close e
					} //close d
				} //close c
			} //close b
		} //close a
		
		// No forbidden subgraph found.
		return true;
	}// close isA.CPP()
	
	public static boolean EliminationOrder(Graph<Integer,String> h) {
		//brute-force method: find a simplicial vertex. Remove it. Recurse.		

		//System.out.print("Test1\n");
		if (h.getVertexCount() < 4) return true;
		
		Iterator<Integer> v = h.getVertices().iterator();		
		Integer V = 0;
		
		while (v.hasNext()){
			V = v.next();
			//System.out.print("Testing simplicial for " + V+" in h where\n");
			//System.out.print("h is the graph: \n" + h + "\n");
			if (Simplicial(h,V) == true) {
				//System.out.print(V+" is simplicial in h\n");
				h.removeVertex(V);
				return EliminationOrder(h);
			}
		}
		
		System.out.print("Not chordal: contains this subgraph without simplicial vertex: " + h +"\n");
		return false; // if no simplicial vertex was found		

	}
	
	public static boolean Chordal(Graph<Integer,String> g) {
		// tests if a given graph is chordal
		// for every pair u,v of nonadjacent vertices, they consider a common nbr
		// (forming a P3) then see if private nbrs(u) are connected to private nbrs(v)
		// If so, Cycle is found.		
		if (g.getVertexCount() < 4) return true;

		/*
		Iterator<Integer> v = g.getVertices().iterator();
		Iterator<Integer> u = g.getVertices().iterator();


		Collection<Integer> common = new HashSet<Integer>(); // common neighbours
		Collection<Integer> space = new HashSet<Integer>(); // space to search for connectivity

		while (v.hasNext()){
			Integer V = v.next();
			while (u.hasNext()){
				Integer U = u.next();
				if (U>V) continue;
				if (g.isNeighbor(U, V)) continue;
				common = g.getNeighbors(U);
				common.retainAll(g.getNeighbors(V)); // common nbrhd
				Iterator<Integer> c = common.iterator();
				while (c.hasNext()){
					Graph<Integer, String> h = new SparseGraph<Integer, String>();
					
					Integer C = c.next();
					
					//want to test connectivity here
				}
			}	
		}
	*/
				
		Graph<Integer,String> h = new SparseGraph<Integer,String>();
		h = NewTools.Copy(g);
		return EliminationOrder(h); // this method modifies h, so we don't call it on g
		
	}

	public static boolean QuasiThreshold(Graph<Integer,String> g) {
		if (Chordal(g) && Cograph(g))
			return true;
		return false;
	}
	
	public static boolean Split(Graph<Integer,String> g) {
		if (Chordal(g) && Chordal(NewTools.complement(g)) == true)
			return true;
		return false;
	}

	public static int isP3(Graph<Integer, String> g, Integer a, Integer b,
			Integer c) {
		// returns 1 if a,b,c induce a P3 in g
		// return 0 otherwise
		int numEdges = 0;
		if (g.isNeighbor(a, b)) numEdges ++;
		if (g.isNeighbor(a, c)) numEdges ++;
		if (g.isNeighbor(c, b)) numEdges ++;
		if (numEdges == 2) return 1;
		return 0;
	}
	
	
}
