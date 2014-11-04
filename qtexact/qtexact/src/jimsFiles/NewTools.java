package jimsFiles;

import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;


import javax.swing.JFrame;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.ListUtils;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
//import edu.uci.ics.jung.graph.decorators.StringLabeller;

import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.samples.VertexLabelPositionDemo;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.PluggableRenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel;


public class NewTools {

	protected static int minSoFar = Integer.MAX_VALUE;
	protected static int minSoFarCograph = 48;
	protected static int minSoFarCographEdit = 20;
	protected static int minSoFarQTEdit = 15;
	//protected static int minSoFarCograph = 56;
	//protected static int minSoFar = 37; // grass_web
	//protected static int minSoFarCographEdit = Integer.MAX_VALUE;
	//protected static int minSoFar = 87; // terrorist_web
	

	
	public static Graph<Integer,String> InducedSubgraph(Graph<Integer,String> g, int[] v){
		// takes a graph g and an array of integers for vertices
		// and returns a graph h which is the induced subgraph of g on the given vertices
		
		// Note: runtime on order of the size of the induced subgraph
		// IF induced subgraphs of g are obtained from removing 1 or 2 vertices
		// could be better to copy g to h and delete the unwanted vertices
		
		Graph<Integer,String> h = new SparseGraph<Integer,String>();
		
		// add the vertex set to h

		for (int i=0; i<v.length; i++) {
			h.addVertex(v[i]);
			System.out.print("added vertex "+ v[i]+"\n");
		}

		// test the edges in g amongst the given vertices, add to h when necessary
		for (int i=0; i<v.length; i++){
			for (int j=i+1; j<v.length; j++) {
				//System.out.print("testing edge " + v[i] + v[j]+"\n");
				if (g.isNeighbor(v[i],v[j]) == true) {
					h.addEdge("edge:" + v[i] + "-" + v[j], v[i],v[j]);
				}
			}
		}
		
		return h;
	}

	public static Graph<Integer,String> Copy(Graph<Integer,String> g){
	// because I can't figure out how to otherwise copy a graph with built-ins
	
		Graph<Integer,String> h = new SparseGraph<Integer,String>();
		
		// copy the vertex set
		Iterator<Integer> a = g.getVertices().iterator();
		while (a.hasNext()) {
			h.addVertex(a.next());
		}

		// copy the edges
		Iterator<Integer> c = g.getVertices().iterator();
		while (c.hasNext()){
			Integer C = c.next();
			Iterator<Integer> b = g.getVertices().iterator();
			while (b.hasNext()) {
				Integer B = b.next();
				if (C < B) { // so it doesn't test duplicates
					if (g.isNeighbor(C, B)) h.addEdge("e"+C+"-"+B, C, B);				
				}
			}
		}
				
		return h;
	} // end copy method
	
	public static Graph<Integer,String> WesternElectricNetwork () {
		/* creates and return the graph of friends of employees, as given
		 * in Freeman's paper "Cliques, Galois lattices, and the structure
		 * of human social groups." 14 vertices are I1,I3, W1-W9, S1,S2,S4 
		 * which I will represent as 11,13,21-29,31,32,34 respectively.
		 */
		
		//I3 = 13 has no neighbours... (?)
		//Neither does S2 = 32 ?
		//So this will return a graph with 12 vertices and 28 edges
		
		Graph<Integer,String> g = new SparseGraph<Integer,String>();
		
		g.addEdge("e1", 11,21);
		g.addEdge("e2", 11,22);
		g.addEdge("e3", 11,23);
		g.addEdge("e4", 11,24);
		g.addEdge("e5", 21,22);
		g.addEdge("e6", 21,23);
		g.addEdge("e7", 21,24);
		g.addEdge("e8", 21,25);
		g.addEdge("e9", 21,31);
		g.addEdge("e10", 22,23);
		g.addEdge("e11", 22,24);
		g.addEdge("e12", 22,31);
		g.addEdge("e13", 23,24);
		g.addEdge("e14", 23,25);
		g.addEdge("e15", 23,31);
		g.addEdge("e16", 24,25);
		g.addEdge("e17", 24,31);
		g.addEdge("e18", 25,27);
		g.addEdge("e19", 25,31);
		g.addEdge("e20", 26,27);
		g.addEdge("e21", 26,28);
		g.addEdge("e22", 26,29);
		g.addEdge("e23", 27,28);
		g.addEdge("e24", 27,29);
		g.addEdge("e25", 27,34);
		g.addEdge("e26", 28,29);
		g.addEdge("e27", 28,34);		
		g.addEdge("e28", 29,34);
		
		return g;
	}
	
	public static Graph<Integer,String> GridGraph(int n, int m) {
		// graph will have vertices 1..n, n+1..2n, 2n+1...3n, ... , mn
		// and two vertices will be adjacent if they are i,i+1 on same row
		// OR kn+i and (k+1)n+i in the same column
		// every vertex will have deg>0, so sufficient to create all edges
		
		Graph<Integer,String> g = new SparseGraph<Integer,String>();
		for (int i=1; i<=n; i++) {
			for (int j=0; j<m; j++) {
				// vertex at (i,j) will be i+jn. Last vertex is n + (m-1)n = mn
				int v = i+j*n;
				if (i>1) g.addEdge("e"+(v-1)+","+v, v-1,v);
				if (j>0) g.addEdge("e"+(v-n)+","+v, v-n,v);				
			}
		}
		return g;		
	}
	
	public static Graph<Integer,String> ErdosRenyi (int n, double p) {
		Graph<Integer,String> g = new SparseGraph<Integer,String>();
		Random generator = new Random();
		
		for (int i=1; i<= n; i++) {
			g.addVertex(i);
		}
		for (int i=1; i<n; i++){
			for (int j=i+1; j<=n; j++) {
				double r = generator.nextDouble();
				if (r < p) {
					g.addEdge("e"+i+","+j, i,j);
				}
			}
		}
		return g;		
	}

	public static Graph<Integer,String> fromFile (String fileName) {
		// assumes that filename is just a list of u v pairs (edge list)
		
		Graph<Integer,String> g = new SparseGraph<Integer,String>();
		FileReader file = null;
		try {
			file = new FileReader(fileName);
		} catch (FileNotFoundException e) {
			System.out.print("File not found: " + fileName +"\n");
			e.printStackTrace();
		}
		Scanner scan = new Scanner(file);

		while (scan.hasNext()){
			Integer a = scan.nextInt();
			Integer b = scan.nextInt();
			g.addEdge("e:"+a+"-"+b, a, b);
		}
		
		return g;
	}

	public static Integer[] findC4 (Graph<Integer,String> g) {
		// returns an array of size four = abcd where abcd is a C4 in cyclic order
		Integer[] cycle = new Integer[4];
		for (Integer a : g.getVertices()) {
			if (IsA.Simplicial(g, a)) continue; // can't be in a C4.
			for (Integer b : g.getNeighbors(a)) {
				for (Integer c : g.getNeighbors(b)) {
					if (c == a || g.isNeighbor(a, c)) continue;
					for (Integer d : g.getNeighbors(c)) {
						if (d==b || g.isNeighbor(b, d)) continue;
						if (g.isNeighbor(a, d)) {
							cycle[0]=a; cycle[1]=b; cycle[2]=c; cycle[3]=d;
							return cycle;
						}
					}
				}
			}
		}
		return null; // if no C4s are left
	}
	
	public static Integer[] findP4SObs (Graph<Integer,String> g){
		// This will look for a a P4-sparse obstruction that is NOT
		// a co-P5=house or a 4-pan. For QT-branching purposes, those
		// can be dealt with via findC4(g).
		//
		// We will find a P4 and then check the 5th vertex.
		for (Integer a : g.getVertices()) {
			for (Integer b: g.getNeighbors(a)) {
				for (Integer c: g.getNeighbors(b)) {
					if (c == a || g.isNeighbor(a, c)) continue;
					for (Integer d : g.getNeighbors(c)) {
						if (d == b || g.isNeighbor(b, d) || g.isNeighbor(a, d)) continue;
						// now a-b-c-d induce a P4. Find a 5th vertex
						for (Integer x : g.getNeighbors(d)) {
							if (x == c) continue;
							if (g.isNeighbor(x,a)) {
								if (!g.isNeighbor(x, b) && !g.isNeighbor(x,c)) {
									// have a C5 = abcdx
									Integer[] cycle = {a,b,c,d,x};
									return cycle;
								}
							}
							else {
								// here, abcd = P4 and x is adj to d and non-adj to a
								// and combination of adjacencies of x to b,c gives obstruction
								Integer[] obstruction = {a,b,c,d,x};
								return obstruction;								
							}
						}
						
					}
				}
			}
		}
		
		return null;
	}
	
	
	public static void DrawIt (Graph<Integer,String> g) {
//		SpringLayout<Integer,String> layout = new SpringLayout<Integer,String>(H1);
		ISOMLayout<Integer,String> layout = new ISOMLayout<Integer,String>(g);
		// The Layout<V, E> is parameterized by the vertex and edge types
		layout.setSize(new Dimension(650,650)); // sets the initial size of the space
		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		BasicVisualizationServer<Integer,String> vv =
		new BasicVisualizationServer<Integer,String>(layout);
		vv.setPreferredSize(new Dimension(650,650)); //Sets the viewing area size
		JFrame frame = new JFrame("Simple Graph View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);

	}
	
	public static Graph<Integer,String> CopyModel (Integer n, double p1, double p2) {
		//starting from an edge, add a new vertex of degree 1 adj to a randomly selected
		//     existing vertex with prob p1, or
		//add a nonadjacent copy of a randomly selected vertex with prob p2, or
		//add an adjacent copy of a randomly selected vertex with prob 1-p1-p2
		
		Graph<Integer,String> g = new SparseGraph<Integer,String>();
		Random generator = new Random();
		Integer k = 2;
		g.addEdge("e12", 1,2); // initial edge

		while (k<n) {
			double r = generator.nextDouble();
			int v = generator.nextInt(k);
			// k = number of existing vertices
			v += 1; // since vertices will be 1...N
			k += 1;
			// k is the new vertex. v is a randomly selected existing vertex
			
			if (r < p1) {
				// generate a new vertex adjacent to 1 randomly selected				
				g.addEdge("e"+k+v, k, v);
			}
			else {
				// generate a copy vertex. Will check r < p2 to decide if adjacent or not
				for (Integer i : g.getNeighbors(v)) {
					g.addEdge("e"+k+i, k,i);				
				}
				if (r < p2) {
					g.addEdge("e"+k+v, k, v);
				}
			}
		}
		
		return g;
	}
	
	public static boolean CographDel (Graph<Integer,String> g, Integer k){
		//THIS METHOD OPERATES ON g. IT WILL MODIFY INPUT GRAPH g TOWARDS A
		//COGRAPH. IF THIS IS NOT THE DESIRED BEHAVIOUR, RUN THIS ON A COPY
		//OF THE WORKING GRAPH.
		/*
		if (IsA.Cograph(g) == true) {
			System.out.print("Found deletion set with " + k + " more deletions available\n");
			return true;
		}
		*/
		if (k==-1) return false;
		
		boolean existsP4 = false;
		
		Iterator<Integer> a = g.getVertices().iterator();
		Iterator<Integer> b;
		Iterator<Integer> c;
		Iterator<Integer> d;
		while(a.hasNext()){
			Integer A = a.next();
			b = g.getNeighbors(A).iterator();
			while(b.hasNext()){
				Integer B = b.next();
				c = g.getNeighbors(B).iterator();
				while (c.hasNext()){
					Integer C = c.next();
					if (g.isNeighbor(C, A) || C==A) continue;
					d = g.getNeighbors(C).iterator();
					while (d.hasNext()){
						Integer D = d.next();
						if (g.isNeighbor(D,A)) continue;
						if (g.isNeighbor(D,B)) continue;
						//otherwise, we have a P4
						existsP4 = true;
						//System.out.print("Found P4: "+A+"-"+B+"-"+C+"-"+D+"... not a cograph\n");
						
						// try deleting each of the 3 edges only if the edge is
						// NOT marked. An edge becomes marked once its removal
						// has been considered.
						
						//if(g.findEdge(A, B).startsWith("marked") == false) {
							g.removeEdge(g.findEdge(A, B));
							if(CographDel(g,k-1) == true) return true;
							g.addEdge("mar-"+A+"-"+B, A,B);
						//}
						
						//g.findEdge(A,B).replace("mar", "marked");
						
						//if(g.findEdge(B,C).startsWith("marked") == false) {
							g.removeEdge(g.findEdge(B, C));
							if(CographDel(g,k-1) == true) return true;
							g.addEdge("mar-"+B+"-"+C, B,C);
						//}

						//g.findEdge(B,C).replace("mar", "marked");

						//if(g.findEdge(C,D).startsWith("marked") == false) {
							g.removeEdge(g.findEdge(C, D));
							if(CographDel(g,k-1) == true) return true;
							g.addEdge("mar-"+C+"-"+D, C,D);
						//}
						
						//g.findEdge(A,B).replace("marked", "mar");
						//g.findEdge(B,C).replace("marked", "mar");
						
						//if (existsP4 == true) {
							return false;
						//}
					} // end d.hasNext()
					
				} // end c.hasNext()
			} // end b.hasNext() 
		} // end a.hasNext()

		// if we reached here, no P4 was found
		if (existsP4 == false) {
			// arrived at a cograph since no P4 was found
			System.out.print("Edge deletion found with " +k+ " left to spare.\n");
			System.out.print("Resulting graph is " + g);
			return true;
		}
		
		// caught at the initial test for isCograph().
		System.out.print("Critical Error. No P_4 found in a non-cograph.\n");
		return false;
	}

	public static Integer[] QTviaLBFS (Graph<Integer,String> g) {

		ArrayList<Integer> LBFS = new ArrayList<Integer>();

		ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();

		ArrayList<Integer> vertexSet = new ArrayList<Integer>(g.getVertices());
		
		ArrayList<Integer> sortedDegree = new ArrayList<Integer>();
		sortedDegree = DegreeSort(g,vertexSet);
		
		list.add(sortedDegree);		

		//System.out.print("step 1\n"+list+"\n");
		
		while (list.size() > 0) {
			ArrayList<Integer> pivotSet = list.get(0);

			if (pivotSet.size() > 0) {
				Integer pivot = pivotSet.get(0);

				//System.out.print("pivot = "+pivot+ " and list =\n"+list+"\n");
				LBFS.add(pivot);
				pivotSet.remove(0);
				//System.out.print("\n LBFS order so far is \n"+LBFS+"\n");
				
				for (int i = 0; i<list.size(); i+=1) {
					// since we found a pivot, we will create a new sublist
					// so we increment by 2 to make sure we don't process it on this pass
					if (list.get(i).size()==0) continue;
					ArrayList<Integer> newPartition = new ArrayList<Integer>();

					for (int j =0; j<list.get(i).size(); j++) {
				
						if (g.isNeighbor(pivot, list.get(i).get(j))) {
							newPartition.add(list.get(i).get(j));
							if (i > 0) {
								// we have a pivot-neighbor not in the first partition
								//System.out.print(" EDGE "+pivot+" "+list.get(i).get(j) + " is an edge in a P4 or C4\n");
								//System.out.print("Certificate is: ");
								for (int w=0; w<LBFS.size();w++) {
									if (LBFS.get(w) == list.get(i).get(j)) continue;
									if (g.isNeighbor(LBFS.get(w),pivot) == true && g.isNeighbor(LBFS.get(w), list.get(i).get(j)) == false) {
										for (Integer z : g.getNeighbors(LBFS.get(w))) {
											if (z == pivot) continue;
											if (g.isNeighbor(z,pivot)==false) {
												/* debugging
												if (g.isNeighbor(z, list.get(i).get(j))) {
													System.out.print("C4 "+LBFS.get(w)+pivot+list.get(i).get(j)+z+"\n");
													System.out.print("pivot = "+pivot+" list = "+list+"\n");
												}
												else {
													System.out.print("P4 "+LBFS.get(w)+pivot+list.get(i).get(j)+z+"\n");
													System.out.print("pivot = "+pivot+" list = "+list+"\n");
												}
												*/
												Integer[] cert = new Integer[4];
												cert[2] = LBFS.get(w); cert[1]=pivot;
												cert[0]=list.get(i).get(j);cert[3]=z;
												return cert;
											}
										}
									}
								}
							}
						}
					}
					list.get(i).removeAll(newPartition);
					list.add(i, newPartition);
					i+=1; // need to do this to skip processing the same set, which has now moved 
				}

				// done creating new partitions for the pivot. Now remove pivot.
			}
			else {
				//here, pivotSet is the first set and is empty
				list.remove(0);
			}
		}
		
		//System.out.print("LBFS order from partition? :\n"+LBFS);
					
		return null;		
	}
	
	public static boolean QTEditingViaCertificate(Graph<Integer,String> g, Integer k) {
	
		if (k < 0) return false;
		
		Integer[] forb = QTviaLBFS(g);
		if (forb == null) {
			//System.out.print("graph is quasi threshold (with "+g.getEdgeCount()+" edges)\n");
			// no certificate = g is quasi-threshold
			return true;
		}
		else {
			/* Certificate verification for debugging
			if (g.isNeighbor(forb[0],forb[1]) == false) {
				System.out.print("Certificate Verification Failed! (code 1)\n");
				System.out.print("Vertices "+forb[0]+" "+forb[1]+" "+forb[2]+" "+forb[3]+"\n");
			}
			if (g.isNeighbor(forb[2],forb[1]) == false) {
				System.out.print("Certificate Verification Failed! (code 2)\n");				
				System.out.print("Vertices "+forb[0]+" "+forb[1]+" "+forb[2]+" "+forb[3]+"\n");
			}
			if (g.isNeighbor(forb[2],forb[3]) == false) {
				System.out.print("Certificate Verification Failed! (code 3)\n");				
				System.out.print("Vertices "+forb[0]+" "+forb[1]+" "+forb[2]+" "+forb[3]+"\n");
			}
			if (g.isNeighbor(forb[0],forb[2])) {
				System.out.print("Certificate Verification Failed! (code 4)\n");				
				System.out.print("Vertices "+forb[0]+" "+forb[1]+" "+forb[2]+" "+forb[3]+"\n");
			}
			if (g.isNeighbor(forb[3],forb[1])) {
				System.out.print("Certificate Verification Failed! (code 5)\n");				
				System.out.print("Vertices "+forb[0]+" "+forb[1]+" "+forb[2]+" "+forb[3]+"\n");
			}
			*/
			
			
			if (k==0) {
				//we have a certificate but also have more to delete
				//System.out.print("graph is not quasi threshold (with "+g.getEdgeCount()+" edges)\n");
				return false;
			}
			if (g.isNeighbor(forb[0],forb[3])) {
				// we have a C4
				// try deleting *any* two edges;
				// or adding one 1 edge
				
				g.removeEdge(g.findEdge(forb[0],forb[1]));
				
				g.removeEdge(g.findEdge(forb[1],forb[2]));
				if (QTEditingViaCertificate(g,k-2) == true) return true;
				g.addEdge(""+forb[1]+","+forb[2], forb[1], forb[2]);

				g.removeEdge(g.findEdge(forb[1],forb[3]));
				if (QTEditingViaCertificate(g,k-2) == true) return true;
				g.addEdge(""+forb[1]+","+forb[3], forb[1], forb[3]);
				
				g.removeEdge(g.findEdge(forb[2],forb[3]));
				if (QTEditingViaCertificate(g,k-2) == true) return true;
				g.addEdge(""+forb[2]+","+forb[3], forb[2], forb[3]);

				g.addEdge(""+forb[0]+","+forb[1], forb[0], forb[1]);
				// have now tried all with forb[0]forb[1] deleted
				
				
				g.removeEdge(g.findEdge(forb[1],forb[2]));

				g.removeEdge(g.findEdge(forb[2],forb[3]));
				if (QTEditingViaCertificate(g,k-2) == true) return true;
				g.addEdge(""+forb[2]+","+forb[3], forb[2], forb[3]);

				g.removeEdge(g.findEdge(forb[0],forb[3]));
				if (QTEditingViaCertificate(g,k-2) == true) return true;

				g.addEdge(""+forb[1]+","+forb[2], forb[1], forb[2]);
				// Have now tried all deletions with either f0-f1 and f1-f2 deleted

				// forb[0]-forb[3] is still deleted at this point in code.
				
				g.removeEdge(g.findEdge(forb[2],forb[3]));
				if (QTEditingViaCertificate(g,k-2) == true) return true;
				g.addEdge(""+forb[2]+","+forb[3], forb[2], forb[3]);
				g.addEdge(""+forb[0]+","+forb[3], forb[0], forb[3]);

				// have now tried all 2 edge deletions. Now to try the two edge-additions
				
				g.addEdge(""+forb[0]+","+forb[2], forb[0], forb[2]);
				if (QTEditingViaCertificate(g,k-1) == true) return true;
				g.removeEdge(g.findEdge(forb[0], forb[2]));
				
				g.addEdge(""+forb[1]+","+forb[3], forb[1], forb[3]);
				if (QTEditingViaCertificate(g,k-1) == true) return true;
				g.removeEdge(g.findEdge(forb[1], forb[3]));
				
			}
			else {
				// we have a P4 = f0 - f1 - f2 - f3
				// delete any one edge or add f0f2 or add the edge f1f3

				g.removeEdge(g.findEdge(forb[0],forb[1]));
				if (QTEditingViaCertificate(g,k-1) == true) return true;
				g.addEdge(""+forb[0]+","+forb[1], forb[0], forb[1]);

				g.removeEdge(g.findEdge(forb[1],forb[2]));
				if (QTEditingViaCertificate(g,k-1) == true) return true;
				g.addEdge(""+forb[1]+","+forb[2], forb[1], forb[2]);

				g.removeEdge(g.findEdge(forb[2],forb[3]));
				if (QTEditingViaCertificate(g,k-1) == true) return true;
				g.addEdge(""+forb[2]+","+forb[3], forb[2], forb[3]);

				// the above are the 3 deletions. Below are the 2 addition options.
				
				g.addEdge(""+forb[0]+","+forb[2], forb[0], forb[2]);
				if (QTEditingViaCertificate(g,k-1) == true) return true;
				g.removeEdge(g.findEdge(forb[0], forb[2]));
				
				g.addEdge(""+forb[1]+","+forb[3], forb[1], forb[3]);
				if (QTEditingViaCertificate(g,k-1) == true) return true;
				g.removeEdge(g.findEdge(forb[1], forb[3]));
				
			}
		}
		return false;
	}
	
	public static ArrayList<Integer> LBFSviaPartition (Graph<Integer,String> g) {
		// Performs a LexBFS starting with vertex partitioning
		// starts with vertices sorted from largest to smallest degree
		// vertex partitioning respects the degree orders.
		
		ArrayList<Integer> LBFS = new ArrayList<Integer>();

		ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();

		ArrayList<Integer> vertexSet = new ArrayList<Integer>(g.getVertices());
		
		ArrayList<Integer> sortedDegree = new ArrayList<Integer>();
		sortedDegree = DegreeSort(g,vertexSet);
		
		list.add(sortedDegree);		

		System.out.print("step 1\n"+list+"\n");
		
		while (list.size() > 0) {
			ArrayList<Integer> pivotSet = list.get(0);
			
			if (pivotSet.size() > 0) {
				Integer pivot = pivotSet.get(0);

				System.out.print("pivot = "+pivot+ "and list=\n"+list+"\n");
				LBFS.add(pivot);
				pivotSet.remove(0);
				System.out.print("\n LBFS order so far is \n"+LBFS+"\n");
				
				for (int i = 0; i<list.size(); i+=1) {
					// since we found a pivot, we will create a new sublist
					// so we increment by 2 to make sure we don't process it on this pass
					if (list.get(i).size()<=1) continue;
					ArrayList<Integer> newPartition = new ArrayList<Integer>();

					for (int j =0; j<list.get(i).size(); j++) {
				
						if (g.isNeighbor(pivot, list.get(i).get(j))) {
							newPartition.add(list.get(i).get(j));
						}
					}
					list.get(i).removeAll(newPartition);
					list.add(i, newPartition);
					i+=1; // need to do this to skip processing the same set, which has now moved 
				}

				// done creating new partitions for the pivot. Now remove pivot.
			}
			else {
				//here, pivotSet is the first set and is empty
				list.remove(0);
			}


		}
		
		//System.out.print("LBFS order from partition? :\n"+LBFS);
					
		return LBFS;		
		
	}
	
	public static boolean QTDeletionViaCertificate(Graph<Integer,String> g, Integer k) {
		
		if (k < 0) return false;
		
		Integer[] forb = QTviaLBFS(g);
		if (forb == null) {
			//System.out.print("graph is quasi threshold (with "+g.getEdgeCount()+" edges)\n");
			// no certificate = g is quasi-threshold
			return true;
		}
		else {
			/* Certificate verification for debugging
			if (g.isNeighbor(forb[0],forb[1]) == false) {
				System.out.print("Certificate Verification Failed! (code 1)\n");
				System.out.print("Vertices "+forb[0]+" "+forb[1]+" "+forb[2]+" "+forb[3]+"\n");
			}
			if (g.isNeighbor(forb[2],forb[1]) == false) {
				System.out.print("Certificate Verification Failed! (code 2)\n");				
				System.out.print("Vertices "+forb[0]+" "+forb[1]+" "+forb[2]+" "+forb[3]+"\n");
			}
			if (g.isNeighbor(forb[2],forb[3]) == false) {
				System.out.print("Certificate Verification Failed! (code 3)\n");				
				System.out.print("Vertices "+forb[0]+" "+forb[1]+" "+forb[2]+" "+forb[3]+"\n");
			}
			if (g.isNeighbor(forb[0],forb[2])) {
				System.out.print("Certificate Verification Failed! (code 4)\n");				
				System.out.print("Vertices "+forb[0]+" "+forb[1]+" "+forb[2]+" "+forb[3]+"\n");
			}
			if (g.isNeighbor(forb[3],forb[1])) {
				System.out.print("Certificate Verification Failed! (code 5)\n");				
				System.out.print("Vertices "+forb[0]+" "+forb[1]+" "+forb[2]+" "+forb[3]+"\n");
			}
			*/
			
			
			if (k==0) {
				//we have a certificate but also have more to delete
				//System.out.print("graph is not quasi threshold (with "+g.getEdgeCount()+" edges)\n");
				return false;
			}
			if (g.isNeighbor(forb[0],forb[3])) {
				// we have a C4
				// try deleting *any* two edges;
				
				g.removeEdge(g.findEdge(forb[0],forb[1]));
				
				g.removeEdge(g.findEdge(forb[1],forb[2]));
				if (QTDeletionViaCertificate(g,k-2) == true) return true;
				g.addEdge(""+forb[1]+","+forb[2], forb[1], forb[2]);

				g.removeEdge(g.findEdge(forb[1],forb[3]));
				if (QTDeletionViaCertificate(g,k-2) == true) return true;
				g.addEdge(""+forb[1]+","+forb[3], forb[1], forb[3]);
				
				g.removeEdge(g.findEdge(forb[2],forb[3]));
				if (QTDeletionViaCertificate(g,k-2) == true) return true;
				g.addEdge(""+forb[2]+","+forb[3], forb[2], forb[3]);

				g.addEdge(""+forb[0]+","+forb[1], forb[0], forb[1]);
				// have now tried all with forb[0]forb[1] deleted
				
				
				g.removeEdge(g.findEdge(forb[1],forb[2]));

				g.removeEdge(g.findEdge(forb[2],forb[3]));
				if (QTDeletionViaCertificate(g,k-2) == true) return true;
				g.addEdge(""+forb[2]+","+forb[3], forb[2], forb[3]);

				g.removeEdge(g.findEdge(forb[0],forb[3]));
				if (QTDeletionViaCertificate(g,k-2) == true) return true;

				g.addEdge(""+forb[1]+","+forb[2], forb[1], forb[2]);
				// Have now tried all deletions with either f0-f1 and f1-f2 deleted

				// forb[0]-forb[3] is still deleted at this point in code.
				
				g.removeEdge(g.findEdge(forb[2],forb[3]));
				if (QTDeletionViaCertificate(g,k-2) == true) return true;
				g.addEdge(""+forb[2]+","+forb[3], forb[2], forb[3]);
				g.addEdge(""+forb[0]+","+forb[3], forb[0], forb[3]);

				// have now tried all 2 edge deletions.				
			}
			else {
				// we have a P4 = f0 - f1 - f2 - f3
				// delete any one edge or add f0f2 or add the edge f1f3

				g.removeEdge(g.findEdge(forb[0],forb[1]));
				if (QTDeletionViaCertificate(g,k-1) == true) return true;
				g.addEdge(""+forb[0]+","+forb[1], forb[0], forb[1]);

				g.removeEdge(g.findEdge(forb[1],forb[2]));
				if (QTDeletionViaCertificate(g,k-1) == true) return true;
				g.addEdge(""+forb[1]+","+forb[2], forb[1], forb[2]);

				g.removeEdge(g.findEdge(forb[2],forb[3]));
				if (QTDeletionViaCertificate(g,k-1) == true) return true;
				g.addEdge(""+forb[2]+","+forb[3], forb[2], forb[3]);

				// the above are the 3 deletions.				
			}
		}
		return false;
	}
	
	public static ArrayList<Integer> DegreeSort(Graph<Integer,String> h, ArrayList<Integer> vertexSet) {

		ArrayList<Integer> sortedList = new ArrayList<Integer>();
		
		int maxIndex = 0;
		int maxValue = -1;
		
		while (vertexSet.size() > 0) {
			maxIndex = -1;
			maxValue = -1;
			for (int i=0; i<vertexSet.size(); i++) {
				if (h.degree(vertexSet.get(i)) > maxValue) {
					maxValue = h.degree(vertexSet.get(i));
					maxIndex = i; 
				}
			}
			sortedList.add(vertexSet.get(maxIndex));
			vertexSet.remove(maxIndex);
		}

		return sortedList;
	}

	public static Integer[] LexBFS(Graph<Integer,String> g) {
		
		//System.out.print("Converting to colored+labeled graph..\n");
		Graph<myVertex,String> h = new SparseGraph<myVertex,String>();
		h = convertToWeighted(g);
		final int N = g.getVertexCount();
		//System.out.print("Done. Old graph: "+N+" vertices. New graph " +h.getVertexCount()+"\n");
		
		//System.out.print("New graph is:\n"+h+"\n");
		myVertex[] queue = new myVertex[N];
		
		Iterator<myVertex> a = h.getVertices().iterator();
		queue[0] = a.next(); // start of BFS search. Now add neighbours.
		int indexCounter = 1;
		int pivot = 0;
		//System.out.print("Initial vertex is: "+queue[0]+" ");
		System.out.print("STATUS REPORT:\n queue = "+queue+"\n  indexCounter=" + indexCounter+"\n  pivot="+pivot+"\n");
		Iterator<myVertex> b = h.getNeighbors(queue[0]).iterator();
		while (b.hasNext()) {
			queue[indexCounter] = b.next();
			queue[indexCounter].label.add(N);
			queue[indexCounter].setColor(1); // 1 = grey = queued
			indexCounter++;
		}
		//System.out.print("with "+(indexCounter-1) +" neighbours\n");
		//System.out.print("STATUS REPORT:\n queue = "+queue+"\n  indexCounter=" + indexCounter+"\n  pivot="+pivot+"\n");
		queue[0].setColor(2); // 2 = black = processed
		// indexCounter counts where the next grey vertex will be enqueued
		// pivot counts where the next grey vertex will be processed and turned black

		pivot = 1;

		//System.out.print("STATUS REPORT:\n queue = "+queue+"\n  indexCounter=" + indexCounter+"\n  pivot="+pivot+"\n");
		while (pivot < indexCounter) {
			// first, find the highest labelled entry in the rest of the queue
			// and move it to the pivot position. This should be improved upon
			// by maintaining sorted order upon adding elements to the queue
			
			//System.out.print("choosing next vertex...\n");
			int max = pivot;
			for (int i = pivot+1; i<indexCounter; i++) {
				//indexCounter is the next available spot, so indexCounter-1 is the last
				//entry i.e. it is last INDEX where queue[INDEX] is non-empty.
				if (queue[i].comesBefore(queue[max])) {
					max = i;
				}
			}
			//System.out.print("STATUS REPORT:\n queue = "+queue+"\n  indexCounter=" + indexCounter+"\n  pivot="+pivot+"\n");
			// at the end of this for-loop, found the index "max" of the element of
			// the queue with the lexicographically largest label. Swap it with pivot.
			myVertex temp = queue[pivot];
			queue[pivot] = queue[max];
			queue[max] = temp;

			//System.out.print("Chose vertex: "+temp+" to visit next\n");
			//System.out.print("STATUS REPORT:\n queue = "+queue+"\n  indexCounter=" + indexCounter+"\n  pivot="+pivot+"\n");
			
			// process the pivot point => find and mark its neighbours, turn it black.
			b = h.getNeighbors(queue[pivot]).iterator();
			while (b.hasNext()) {
				myVertex B = b.next();
				if (B.color == 0) {
					// found a vertex which has not been queued...
					queue[indexCounter] = B;
					B.label.add(N-pivot);
					B.setColor(1);
					indexCounter++;
				}
				else if (B.color == 1) {
					// found a vertex in the queue which has not been processed...
					B.label.add(N-pivot);
				}
				else if (B.color != 2) {
					System.out.print("Critical Error: found a vertex in LexBFS process ");
					System.out.print("which has been visited but is not grey or black.\n");
				}				
				//System.out.print("STATUS REPORT:\n queue = "+queue+"\n  indexCounter=" + indexCounter+"\n  pivot="+pivot+"\n");
			}
			queue[pivot].setColor(2); //done processing current pivot
			pivot ++;
			
		}
		
		//LexBFS done; produce integer array to return;
		//System.out.print("STATUS REPORT:\n queue = "+queue+"\n  indexCounter=" + indexCounter+"\n  pivot="+pivot+"\n");
		Integer[] LBFS = new Integer[N]; // N assumes the graph is connected...
		for (int i = 0; i<N; i++) {
			LBFS[i] = queue[i].id;
		}
		//System.out.print("STATUS REPORT:\n queue = "+queue+"\n  indexCounter=" + indexCounter+"\n  pivot="+pivot+"\n");
		//System.out.print("Returning array: " + LBFS);
		return LBFS;
	}

	public static Graph<myVertex,String> convertToWeighted(Graph<Integer,String> g) {
		Graph<myVertex,String> h = new SparseGraph<myVertex,String>();
		for (Integer v : g.getVertices()) {
			h.addVertex(new myVertex(v));
		}

		for (myVertex u : h.getVertices()) {
			for (myVertex v : h.getVertices()) {
				if(u.id<v.id) { // avoids duplicates
					if (g.isNeighbor(u.id, v.id)) {
						h.addEdge("e"+u+v, u, v);						
					}
				}
			}
		}

		return h;
	}
	
	
	public static int P4Score (Graph<Integer,String> g) {
		// returns the number of P4s in graph g 
		int totalP4s = 0;
		
		for (Integer a: g.getVertices()) {
			for (Integer b: g.getVertices()) {
				if (b <= a) continue;
				for (Integer c: g.getVertices()) {
					if (c <= a) continue;
					if (c <= b) continue;
					for (Integer d: g.getVertices()) {
						if (d<=a) continue;
						if (d<=b) continue;
						if (d<=c) continue;
						totalP4s += IsA.isP4(g, a, b, c, d);
					}
				}
			}
		}		
		return totalP4s;
	}
	
	public static int P3Score (Graph<Integer,String> g) {
		// returns the number of P3s in graph g 
		int totalP3s = 0;
		
		for (Integer a: g.getVertices()) {
			for (Integer b: g.getVertices()) {
				if (b <= a) continue;
				for (Integer c: g.getVertices()) {
					if (c <= a) continue;
					if (c <= b) continue;
					totalP3s += IsA.isP3(g, a, b, c);
				}
			}
		}		
		return totalP3s;
	}
	
	public static int P4C4Score (Graph<Integer,String> g) {
		// returns the number of 4-sets inducing a P4 or C4 in graph g 
		// does not modify g
		
		int totalP4C4s = 0;
		
		//Integer V[] = new Integer[g.getVertexCount()];
		//g.getVertices().toArray(V);
		
		for (Integer a: g.getVertices()) {
			for (Integer b: g.getVertices()) {
				if (b <= a) continue;
				for (Integer c: g.getVertices()) {
					if (c <= a) continue;
					if (c <= b) continue;
					for (Integer d: g.getVertices()) {
						if (d<=a) continue;
						if (d<=b) continue;
						if (d<=c) continue;
						totalP4C4s += IsA.isP4orC4(g, a, b, c, d);
					}
				}
			}
		}		
		return totalP4C4s;
	}
	
	public static int ScorePairP4(Graph<Integer,String> g, Integer u, Integer v) {
		
		int totalScore = 0;
	
		for (Integer a : g.getVertices()) {
			if (a == u | a == v) continue;
			for (Integer b : g.getVertices()) {
				if (b == u || b == v || b <= a) continue;
				totalScore += IsA.isP4(g, a, b, u, v);
			}
		}
		return totalScore;
	}
	
	public static int ScorePairP3(Graph<Integer,String> g, Integer u, Integer v) {
		
		int totalScore = 0;
	
		for (Integer a : g.getVertices()) {
			if (a == u | a == v) continue;
			totalScore += IsA.isP3(g, a, u, v);
		}
		return totalScore;
	}
	
	public static int ScorePairP4C4(Graph<Integer,String> g, Integer u, Integer v) {
		
		int totalScore = 0;
	
		for (Integer a : g.getVertices()) {
			if (a == u | a == v) continue;
			for (Integer b : g.getVertices()) {
				if (b == u || b == v || b <= a) continue;
				totalScore += IsA.isP4orC4(g, a, b, u, v);
			}
		}
		return totalScore;
	}
	
	
	public static int GreedyCographEdit (Graph<Integer,String> g) {

		int currentScore, modifiedScore=0, bestScore;
		Integer uBest=0, vBest=0;
		int numEdits = 0;
		int numP4s = Integer.MAX_VALUE;
		int improvement = -Integer.MAX_VALUE;
		

	    numP4s = P4Score(g);

	    while (numP4s > 0) {

			if (numP4s == 0) {
				System.out.print("Cograph deletion set found via greedy method:\n");
				System.out.print("Resulting graph is:\n"+g+"\n");
				return 0;
			}
			else System.out.print("Number of P4s left is "+numP4s+"\n");

			
			bestScore = -Integer.MAX_VALUE;
			for (Integer u : g.getVertices()) {
				for (Integer v : g.getVertices()) {
					if (v == u) continue;
					currentScore = ScorePairP4(g,u,v);
				
					if (g.isNeighbor(u, v)) {
						g.removeEdge(g.findEdge(u, v));
						modifiedScore = ScorePairP4(g,u,v);
						g.addEdge("e"+u+","+v, u,v);
						improvement = currentScore - modifiedScore;					
					}

					else {
						g.addEdge("e"+u+","+v, u,v);					
						modifiedScore = ScorePairP4(g,u,v);
						g.removeEdge(g.findEdge(u, v));
						improvement = currentScore - modifiedScore;
					}

					if (improvement > bestScore) {
						bestScore = improvement;
						uBest = u;
						vBest = v;
						System.out.print("               found candidate with score "+bestScore+"\n");
					}				
					
				}
			} // end of checking all pairs
			if (g.isNeighbor(uBest, vBest)) {
				System.out.print("Removing edge "+uBest+" "+vBest + " for improvement of "+bestScore+"\n");
				g.removeEdge(g.findEdge(uBest,vBest));
				numEdits ++;
			}
			else {
				System.out.print("Adding edge "+uBest+" "+vBest + " for improvement of "+bestScore+"\n");
				g.addEdge("e"+uBest+","+vBest, uBest,vBest);
				numEdits ++;
			}
			numP4s -= bestScore;
			
		} // end while-loop
		return numEdits;
		
	}//end GreedyCographEdit
	
	public static int GreedyP3Edit (Graph<Integer,String> g) {

		int currentScore, modifiedScore=0, bestScore;
		Integer uBest=0, vBest=0;
		int numEdits = 0;
		int numP3s = Integer.MAX_VALUE;
		int improvement = -Integer.MAX_VALUE;
		

	    numP3s = P3Score(g);

	    while (numP3s > 0) {

			if (numP3s == 0) {
				System.out.print("P3-edit set found via greedy method:\n");
				System.out.print("Resulting graph is:\n"+g+"\n");
				return 0;
			}
			else System.out.print("Number of P3s left is "+numP3s+"\n");

			
			bestScore = -Integer.MAX_VALUE;
			for (Integer u : g.getVertices()) {
				for (Integer v : g.getVertices()) {
					if (v == u) continue;
					currentScore = ScorePairP3(g,u,v);
				
					if (g.isNeighbor(u, v)) {
						g.removeEdge(g.findEdge(u, v));
						modifiedScore = ScorePairP3(g,u,v);
						g.addEdge("e"+u+","+v, u,v);
						improvement = currentScore - modifiedScore;					
					}

					else {
						g.addEdge("e"+u+","+v, u,v);					
						modifiedScore = ScorePairP3(g,u,v);
						g.removeEdge(g.findEdge(u, v));
						improvement = currentScore - modifiedScore;
					}

					if (improvement > bestScore) {
						bestScore = improvement;
						uBest = u;
						vBest = v;
						System.out.print("               found candidate with score "+bestScore+"\n");
					}				
					
				}
			} // end of checking all pairs
			if (g.isNeighbor(uBest, vBest)) {
				System.out.print("Removing edge "+uBest+" "+vBest + " for improvement of "+bestScore+"\n");
				g.removeEdge(g.findEdge(uBest,vBest));
				numEdits ++;
			}
			else {
				System.out.print("Adding edge "+uBest+" "+vBest + " for improvement of "+bestScore+"\n");
				g.addEdge("e"+uBest+","+vBest, uBest,vBest);
				numEdits ++;
			}
			numP3s -= bestScore;
			
		} // end while-loop
		return numEdits;
		
	}//end GreedyP3Edit
	
	public static int GreedyQTEdit (Graph<Integer,String> g, String outputFileName) {

		// Will modify the input graph with greedy choices!
		// Call on a copy() of g if just the bound is desired
		//
		// Finds the edge or non-edge to del/add that maximizes
		// the NET reduction in P4/C4 obstructions in the graph.
		
		int currentScore, modifiedScore=0, bestScore;
		Integer uBest=0, vBest=0;
		int numEdits = 0;
		int numP4C4s = Integer.MAX_VALUE;
		int improvement = -Integer.MAX_VALUE;

	    numP4C4s = P4C4Score(g); // calculates total number of obstructions in original g
	    System.out.println("Total number of P4 and C4 obstructions in the original graph is "+numP4C4s);
	    
	    while (numP4C4s > 0) {

			System.out.print("Number of 4-set obstructions left is "+numP4C4s+"\n");

			
			bestScore = -Integer.MAX_VALUE;
			for (Integer u : g.getVertices()) {
				for (Integer v : g.getVertices()) {
					if (v <= u) continue; // less-than for symmetry-breaking
					currentScore = ScorePairP4C4(g,u,v);
				
					if (g.isNeighbor(u, v)) {
						g.removeEdge(g.findEdge(u, v));
						modifiedScore = ScorePairP4C4(g,u,v);
						g.addEdge("e"+u+","+v, u,v);
						improvement = currentScore - modifiedScore;					
					}

					else {
						g.addEdge("e"+u+","+v, u,v);					
						modifiedScore = ScorePairP4C4(g,u,v);
						g.removeEdge(g.findEdge(u, v));
						improvement = currentScore - modifiedScore;
					}

					if (improvement >= bestScore) {
						if (improvement == bestScore) {
							if (!g.isNeighbor(u, v)) {
								// in the case of tie, overwrite best only if it is
								// an edge-addition (that is, if u,v doesn't exist)
								bestScore = improvement;
								uBest = u;
								vBest = v;
								System.out.print("               found candidate "+u+" "+v+" with score "+bestScore+"\n");
							}
						}
						else {
							bestScore = improvement;
							uBest = u;
							vBest = v;
							System.out.print("               found candidate "+u+" "+v+" with score "+bestScore+"\n");
						}
					}

				}
			} // end of checking all pairs
			if (g.isNeighbor(uBest, vBest)) {
				numEdits ++;
				System.out.print("("+numEdits+") Removing edge "+uBest+" "+vBest + " for improvement of "+bestScore+"\n\n");
				g.removeEdge(g.findEdge(uBest,vBest));
			}
			else {
				numEdits ++;
				System.out.print("("+numEdits+") Adding edge "+uBest+" "+vBest + " for improvement of "+bestScore+"\n\n");
				g.addEdge("e"+uBest+","+vBest, uBest,vBest);
			}
			numP4C4s -= bestScore;
			
		} // end while-loop
	    
	    if (numP4C4s == 0) {
			System.out.print("QT editing set found via greedy method:\n");
			System.out.print("Resulting graph is:\n"+g+"\n");
			printGML(g,outputFileName);
			return 0;
		}
	    
		return numEdits;
	}//end GreedyQTEdit


	public static int QTDelHeuristic2 (Graph<Integer,String> g) {
	
		// This does not yet work properly
		/*
		Integer bestVertex;
		int bestScore = 0;
		int newDeg;
		int heuristicVal, temp;
		
		Iterator<Integer> a = g.getVertices().iterator();
		Integer A = a.next();
		bestVertex = A;
		bestScore = g.degree(A);

		
		while (a.hasNext()) {
			A = a.next();
			newDeg = g.degree(A);
			if (newDeg > bestScore) {
				bestVertex = A;
				bestScore = newDeg;
			}
		}
		
		// at this point, bestVertex has the vertex of largest degree
		// and its degree is bestScore.
		
		heuristicVal = 0;

		// For any two vertices, if they are adjacent, they either end up in the same
		// quasi-threshold tree (one's nbrhd is a subset of the other) or in disjoint subtrees.
		// ...if they are non-adjacent, they can not end up in the same subtree.  
		
		for (Integer v : g.getVertices()) {
			if (g.isNeighbor(v, bestVertex)) {
				temp = Math.min(
						CollectionUtils.subtract(g.getNeighbors(v), g.getNeighbors(bestVertex)).size(),
						1+CollectionUtils.intersection(g.getNeighbors(v),g.getNeighbors(bestVertex)).size()
						);
				if (temp > heuristicVal) {
					heuristicVal = temp;
				}
			}
			else {
				if (v != bestVertex) {
					temp = CollectionUtils.intersection(g.getNeighbors(v), g.getNeighbors(bestVertex)).size();
					if (temp > heuristicVal) {
						heuristicVal = temp;
					}
				}
			}
		}
*/		
		return 0;		
	}
	

	public static int QTDelHeuristic1 (Graph<Integer,String> h) {
		//Graph<Integer,String> h = new SparseGraph<Integer,String>();
		int count = 0;
		boolean isQT = false;
		//h = Copy(g);
		//boolean moreToDo = true;
		
		Iterator<Integer> a;
		Iterator<Integer> b;
		Iterator<Integer> c;
		Iterator<Integer> d;

		while (isQT == false) {
			isQT = true;
			a= h.getVertices().iterator();
			while(a.hasNext()){
				Integer A = a.next();
				//System.out.print(""+A+" ");
				b = h.getNeighbors(A).iterator();
				while(b.hasNext()){
					Integer B = b.next();
					c = h.getNeighbors(B).iterator();
					while (c.hasNext()){
						Integer C = c.next();
						if (h.isNeighbor(C, A) || C==A) continue;
						d = h.getNeighbors(C).iterator();
						while (d.hasNext()){
							Integer D = d.next();
							if (D==B) continue; 
							if (h.isNeighbor(D,B)) continue;
							//otherwise, we have a P4 or a C4

							isQT = false;
							//System.out.print("Found P4: "+A+"-"+B+"-"+C+"-"+D+"... not a cograph\n");

							if (h.isNeighbor(D,A)) {
								// we have a C4 = a-b-c-d-a
								// requires 2 deletions
								count += 2;
								h.removeVertex(A);
								h.removeVertex(B);
								h.removeVertex(C);
								h.removeVertex(D);
								return 2 + QTDelHeuristic1(h);							
							}

							else {
								// this case says:
								// else D is NOT adjacent to A. Then we have P4=abcd
							
								count += 1;
								h.removeVertex(A);
								h.removeVertex(B);
								h.removeVertex(C);
								h.removeVertex(D);
								return 1 + QTDelHeuristic1(h);
							}
						}
							
					} // end d.hasNext()
				} // end c.hasNext()
			} // end b.hasNext() 
		} // end a.hasNext()
		
		return count;		
	}
	
	public static int CographEditHeuristic1 (Graph<Integer,String> h) {
		//Graph<Integer,String> h = new SparseGraph<Integer,String>();
		boolean isCograph = false;
		//h = Copy(g);
		
		Iterator<Integer> a;
		Iterator<Integer> b;
		Iterator<Integer> c;
		Iterator<Integer> d;

		while (isCograph == false) {
			isCograph = true;
			a= h.getVertices().iterator();
			while(a.hasNext()){
				Integer A = a.next();
				//System.out.print(""+A+" ");
				b = h.getNeighbors(A).iterator();
				while(b.hasNext()){
					Integer B = b.next();
					c = h.getNeighbors(B).iterator();
					while (c.hasNext()){
						Integer C = c.next();
						if (h.isNeighbor(C, A) || C==A) continue;
						d = h.getNeighbors(C).iterator();
						while (d.hasNext()){
							Integer D = d.next();
							if (D==B) continue; 
							if (h.isNeighbor(D,B)) continue;
							if (h.isNeighbor(D, A)) continue;
							//otherwise, we have a P4

							isCograph = false;
							//System.out.print("Found P4: "+A+"-"+B+"-"+C+"-"+D+"... not a cograph\n");

							// else D is NOT adjacent to A. Then we have P4=abcd
							
							h.removeVertex(A);
							h.removeVertex(B);
							h.removeVertex(C);
							return 1 + CographEditHeuristic1(h);
							
						}
							
					} // end d.hasNext()
				} // end c.hasNext()
			} // end b.hasNext() 
		} // end a.hasNext()

		// if it reaches here, no P4s exist
		return 0;		
	}

	public static int CographDelHeuristic1 (Graph<Integer,String> h) {
		//Graph<Integer,String> h = new SparseGraph<Integer,String>();
		boolean isCograph = false;
		//h = Copy(g);
		
		Iterator<Integer> a;
		Iterator<Integer> b;
		Iterator<Integer> c;
		Iterator<Integer> d;

		while (isCograph == false) {
			isCograph = true;
			a= h.getVertices().iterator();
			while(a.hasNext()){
				Integer A = a.next();
				//System.out.print(""+A+" ");
				b = h.getNeighbors(A).iterator();
				while(b.hasNext()){
					Integer B = b.next();
					c = h.getNeighbors(B).iterator();
					while (c.hasNext()){
						Integer C = c.next();
						if (h.isNeighbor(C, A) || C==A) continue;
						d = h.getNeighbors(C).iterator();
						while (d.hasNext()){
							Integer D = d.next();
							if (D==B) continue; 
							if (h.isNeighbor(D,B)) continue;
							if (h.isNeighbor(D, A)) continue;
							//otherwise, we have a P4

							isCograph = false;
							//System.out.print("Found P4: "+A+"-"+B+"-"+C+"-"+D+"... not a cograph\n");

							// else D is NOT adjacent to A. Then we have P4=abcd
							
							h.removeVertex(A);
							h.removeVertex(C);
							return 1 + CographDelHeuristic1(h);
							
						}
							
					} // end d.hasNext()
				} // end c.hasNext()
			} // end b.hasNext() 
		} // end a.hasNext()
		
		// If it reaches here, no P4s exist
		return 0;		
	}
	
	public static int CographDelHeuristic2Error (Graph<Integer,String> h) {
		//Graph<Integer,String> h = new SparseGraph<Integer,String>();
		boolean isCograph = false;
		//h = Copy(g);
		
		Iterator<Integer> a;
		Iterator<Integer> b;
		Iterator<Integer> c;
		Iterator<Integer> d;

		while (isCograph == false) {
			isCograph = true;
			a= h.getVertices().iterator();
			while(a.hasNext()){
				Integer A = a.next();
				//System.out.print(""+A+" ");
				b = h.getNeighbors(A).iterator();
				while(b.hasNext()){
					Integer B = b.next();
					c = h.getNeighbors(B).iterator();
					while (c.hasNext()){
						Integer C = c.next();
						if (h.isNeighbor(C, A) || C==A) continue;
						d = h.getNeighbors(C).iterator();
						while (d.hasNext()){
							Integer D = d.next();
							if (D==B) continue; 
							if (h.isNeighbor(D,B)) continue;
							if (h.isNeighbor(D, A)) continue;
							//otherwise, we have a P4

							isCograph = false;
							//System.out.print("Found P4: "+A+"-"+B+"-"+C+"-"+D+"... not a cograph\n");

							// else D is NOT adjacent to A. Then we have P4=abcd
							
							h.removeEdge(h.findEdge(A, B));
							h.removeEdge(h.findEdge(B, C));
							h.removeEdge(h.findEdge(C, D));

							return 1 + CographDelHeuristic2Error(h);
							
						}
							
					} // end d.hasNext()
				} // end c.hasNext()
			} // end b.hasNext() 
		} // end a.hasNext()
		
		// If it reaches here, no P4s exist
		return 0;		
	}

	
	public static void QTdeletionMinimization (Graph<Integer,String> g, int currentSize) {

		// return value is the optimal deletion set size. So if input is Quasi-threshold,
		// we return 0. If not, we make 1 or 2 deletions and return 1 or 2 + recursive call.
		
		boolean existsP4orC4 = false;
		
		//int temp = QTDelHeuristic1(Copy(g));
		//System.out.print("n = "+g.getVertexCount()+" m="+g.getEdgeCount()+" hVal = "+temp +"\n");
		if (currentSize >= minSoFar) return; 
		Iterator<Integer> a = g.getVertices().iterator();
		Iterator<Integer> b;
		Iterator<Integer> c;
		Iterator<Integer> d;
		while(a.hasNext()){
			Integer A = a.next();
			//System.out.print(""+A+" ");
			b = g.getNeighbors(A).iterator();
			while(b.hasNext()){
				Integer B = b.next();
				c = g.getNeighbors(B).iterator();
				while (c.hasNext()){
					Integer C = c.next();
					if (g.isNeighbor(C, A) || C==A) continue;
					d = g.getNeighbors(C).iterator();
					while (d.hasNext()){
						Integer D = d.next();
						if (D==B) continue; 
						if (g.isNeighbor(D,B)) continue;
						//otherwise, we have a P4 or a C4

						existsP4orC4 = true;
						if (currentSize + 1 >= minSoFar) return;
						
						//System.out.print("Found P4: "+A+"-"+B+"-"+C+"-"+D+"... not a cograph\n");
						if (g.isNeighbor(D,A)) {
							// we have a C4 = a-b-c-d-a
							// delete any two edges
							if (currentSize + 2 >= minSoFar) return;

							g.removeEdge(g.findEdge(A, B));

							g.removeEdge(g.findEdge(B, C));
							QTdeletionMinimization(g,2+currentSize);
							g.addEdge("mar-"+B+"-"+C, B,C);

							g.removeEdge(g.findEdge(C, D));
							QTdeletionMinimization(g,2+currentSize);
							g.addEdge("mar-"+C+"-"+D, C,D);


							g.removeEdge(g.findEdge(D, A));
							QTdeletionMinimization(g,2+currentSize);
							//g.addEdge("mar-"+A+"-"+D, A,D);

								
							g.addEdge("mar-"+A+"-"+B, A,B);
							// all cases with AB deleted are done. AD is STILL deleted

							
							g.removeEdge(g.findEdge(B, C));
							QTdeletionMinimization(g,2+currentSize);
							g.addEdge("mar-"+B+"-"+C, B,C);

							g.removeEdge(g.findEdge(C, D));
							QTdeletionMinimization(g,2+currentSize);

							// all cases with AB deleted or AD deleted are done
							// at this point, CD and AD are still deleted
							// only need to try BC and CD together deleted.
							g.addEdge("mar-"+A+"-"+D, A,D);

							
							g.removeEdge(g.findEdge(B, C));
							QTdeletionMinimization(g,2+currentSize);

							g.addEdge("mar-"+B+"-"+C, B,C);
							g.addEdge("mar-"+C+"-"+D, C,D);
																			
						}

						else {
							// this case says:
							// else D is NOT adjacent to A. Then we have P4=abcd
							
							int choice = randomInt(1,4);
							
							if (choice == 1) {
								g.removeEdge(g.findEdge(A, B));
								QTdeletionMinimization(g,1+currentSize);
								g.addEdge("mar-"+A+"-"+B, A,B);
							}					

							else if (choice == 2) {
								g.removeEdge(g.findEdge(C, D));
								QTdeletionMinimization(g,1+currentSize);
								g.addEdge("mar-"+C+"-"+D, C,D);
							}
							
							else {
								g.removeEdge(g.findEdge(B, C));
								QTdeletionMinimization(g,1+currentSize);
								g.addEdge("mar-"+B+"-"+C, B,C);
							}
						}
							
						if (existsP4orC4==true) {
							// it found an obstruction and branched on all its ways.
							// No need to deal with this graph further.
							return;
						}
					} // end d.hasNext()
				} // end c.hasNext()
			} // end b.hasNext() 
		} // end a.hasNext()

		// if we reached here, no P4 was found
		if (existsP4orC4 == false) {
			// arrived at a quasi-threshold graph since no P4 or C4 was found
			System.out.print("\n currentSize = "+currentSize+" and minSoFar = "+minSoFar+"\n");
			System.out.print("Modified graph has "+g.getEdgeCount()+" edges and is:\n");
			minSoFar = currentSize;
			System.out.print(""+g+"\n");
			NewTools.printToFile(currentSize,g,null,"QTDel.txt");
			//NewTools.DrawIt(g);
			//PleaseWait(60000); // one minute
			return;
		}
		
		// ...
		System.out.print("Critical Error. No P_4 or C_4 found in a non-quasi-threshold graph.\n");
		return;

		
	}
	
	public static void PleaseWait (int n){
        
        long t0, t1;

        t0 =  System.currentTimeMillis();

        do{
            t1 = System.currentTimeMillis();
        }
        while (t1 - t0 < n);
    }


	
	private static int randomInt(int i, int j) {
		// returns a random int from i to j inclusive
		Random generator = new Random();
		return i + generator.nextInt(j);
	}

	public static void CographDeletionMinimization (Graph<Integer,String> g, int currentSize) {

		boolean existsP4 = false;

		//int temp = CographDelHeuristic(Copy(g));
		//System.out.print("n = "+g.getVertexCount()+" m="+g.getEdgeCount()+" hVal = "+temp +"\n");

		if (currentSize >= minSoFarCograph) return; 
		Iterator<Integer> a = g.getVertices().iterator();
		Iterator<Integer> b;
		Iterator<Integer> c;
		Iterator<Integer> d;
		while(a.hasNext()){
			Integer A = a.next();
			//System.out.print(""+A+" ");
			b = g.getNeighbors(A).iterator();
			while(b.hasNext()){
				Integer B = b.next();
				c = g.getNeighbors(B).iterator();
				while (c.hasNext()){
					Integer C = c.next();
					if (g.isNeighbor(C, A) || C==A) continue;
					d = g.getNeighbors(C).iterator();
					while (d.hasNext()){
						Integer D = d.next();
						if (D==B) continue; 
						if (g.isNeighbor(D,B)) continue;
						if (g.isNeighbor(D,A)) continue;
						//otherwise, we have a P4
						
						existsP4 = true;
						if (currentSize + 1 >= minSoFarCograph) return;
						
						//System.out.print("Found P4: "+A+"-"+B+"-"+C+"-"+D+"... not a cograph\n");

						g.removeEdge(g.findEdge(A, B));
						CographDeletionMinimization(g,1+currentSize);
						g.addEdge("mar-"+A+"-"+B, A,B);
														
						g.removeEdge(g.findEdge(B, C));
						CographDeletionMinimization(g,1+currentSize);
						g.addEdge("mar-"+B+"-"+C, B,C);

						g.removeEdge(g.findEdge(C, D));
						CographDeletionMinimization(g,1+currentSize);
						g.addEdge("mar-"+C+"-"+D, C,D);

							
						return;
					} // end d.hasNext()
				} // end c.hasNext()
			} // end b.hasNext() 
		} // end a.hasNext()

		// if we reached here, no P4 was found
		if (existsP4 == false) {
			// arrived at a cograph since no P4 was found
			System.out.print("\n currentSize = "+currentSize+" and minSoFar = "+minSoFarCograph+"\n");
			System.out.print("Modified graph has "+g.getEdgeCount()+" edges and is:\n");
			minSoFarCograph = currentSize;
			System.out.print(""+g+"\n");
			//NewTools.printToFile(currentSize,g,null,"CographDel.txt");
			//DrawIt(g);
			return;
		}
		
		// ...
		System.out.print("Critical Error. No P_4 found in a non-cograph.\n");
		return;

		
	}
	
	public static boolean QuasiThresholdEditing (Graph<Integer,String> g, Integer k){
		// given graph g and int k, decides whether there exist k edge edits
		// (that is, any combination of edge deletions and edge additions)
		// that will turn g into a (P4,C4)-free graph. Will modify the input
		// graph, so call it on a copy of a graph if modification is not desired
		
		if (k<0) return false;
		//System.out.print(" "+k+"\n");
		boolean existsP4orC4 = false;
		
		Iterator<Integer> a = g.getVertices().iterator();
		Iterator<Integer> b;
		Iterator<Integer> c;
		Iterator<Integer> d;
		while(a.hasNext()){
			Integer A = a.next();
			//System.out.print(""+A+" ");
			b = g.getNeighbors(A).iterator();
			while(b.hasNext()){
				Integer B = b.next();
				c = g.getNeighbors(B).iterator();
				while (c.hasNext()){
					Integer C = c.next();
					if (g.isNeighbor(C, A) || C==A) continue;
					d = g.getNeighbors(C).iterator();
					while (d.hasNext()){
						Integer D = d.next();
						if (D==B) continue; 
						if (g.isNeighbor(D,B)) continue;
						//otherwise, we have a P4 or a C4

						existsP4orC4 = true;
						//System.out.print("Found P4: "+A+"-"+B+"-"+C+"-"+D+"... not a cograph\n");

						if (g.isNeighbor(D,A)) {
							// we have a C4 = a-b-c-d-a
							// add a diagonal chord or delete any two edges

							g.addEdge("e"+A+","+C, A,C);
							if(QuasiThresholdEditing(g,k-1) == true) return true;
							g.removeEdge(g.findEdge(A, C));

							g.addEdge("e"+B+","+D, B,D);
							if(QuasiThresholdEditing(g,k-1) == true) return true;
							g.removeEdge(g.findEdge(B,D));

							
							g.removeEdge(g.findEdge(A, B));

							g.removeEdge(g.findEdge(B, C));
							if(QuasiThresholdEditing(g,k-2) == true) return true;
							g.addEdge("mar-"+B+"-"+C, B,C);

							g.removeEdge(g.findEdge(C, D));
							if(QuasiThresholdEditing(g,k-2) == true) return true;
							g.addEdge("mar-"+C+"-"+D, C,D);
							
							g.removeEdge(g.findEdge(D, A));
							if(QuasiThresholdEditing(g,k-2) == true) return true;

							g.addEdge("mar-"+A+"-"+B, A,B);
							// all cases with AB deleted are done. AD is still deleted
							
							g.removeEdge(g.findEdge(B, C));
							if(QuasiThresholdEditing(g,k-2) == true) return true;
							g.addEdge("mar-"+B+"-"+C, B,C);
							
							g.removeEdge(g.findEdge(C, D));
							if(QuasiThresholdEditing(g,k-2) == true) return true;

							// all cases with AB deleted or AD deleted are done
							// at this point, CD and AD are still deleted
							// only need to try BC and CD together deleted.
							
							g.addEdge("mar-"+A+"-"+D, A,D);
							g.removeEdge(g.findEdge(B, C));
							if(QuasiThresholdEditing(g,k-2) == true) return true;

							g.addEdge("mar-"+B+"-"+C, B,C);
							g.addEdge("mar-"+C+"-"+D, C,D);
																			
						}

						else {
							// this case says:
							// else D is NOT adjacent to A. Then we have P4=abcd
							// either add ac or bd or else one edge

							g.addEdge("e"+A+C, A, C);
							if(QuasiThresholdEditing(g,k-1)==true) return true;
							g.removeEdge(g.findEdge(A, C));
							
							g.addEdge("e"+B+D, B, D);
							if(QuasiThresholdEditing(g,k-1)==true) return true;
							g.removeEdge(g.findEdge(B, D));
							
							
							g.removeEdge(g.findEdge(A, B));
							if(QuasiThresholdEditing(g,k-1) == true) return true;
							g.addEdge("mar-"+A+"-"+B, A,B);
							
							
							g.removeEdge(g.findEdge(B, C));
							if(QuasiThresholdEditing(g,k-1) == true) return true;
							g.addEdge("mar-"+B+"-"+C, B,C);
	
							g.removeEdge(g.findEdge(C, D));
							if(QuasiThresholdEditing(g,k-1) == true) return true;
							g.addEdge("mar-"+C+"-"+D, C,D);

						}
							
						return false;
					} // end d.hasNext()
				} // end c.hasNext()
			} // end b.hasNext() 
		} // end a.hasNext()

		// if we reached here, no P4 was found
		if (existsP4orC4 == false) {
			// arrived at a quasi-threshold graph since no P4 or C4 was found
			System.out.print("Edge edit set found with " +k+ " left to spare.\n");
			System.out.print("Resulting graph is " + g);
			return true;
		}
		
		// ...
		System.out.print("Critical Error. No P_4 or C_4 found in a non-quasi-threshold graph.\n");
		return false;

		
	}
	

	public static void CographEditingMinimization (Graph<Integer,String> g, int currentSize, EditSet S) {

		boolean existsP4 = false;

		// int temp = CographHeuristic1(Copy(g));
		//System.out.print("n = "+g.getVertexCount()+" m="+g.getEdgeCount()+" hVal = "+temp +"\n");

		if (currentSize >= minSoFarCographEdit) return; 
		Iterator<Integer> a = g.getVertices().iterator();
		Iterator<Integer> b;
		Iterator<Integer> c;
		Iterator<Integer> d;
		while(a.hasNext()){
			Integer A = a.next();
			//System.out.print(""+A+" ");
			b = g.getNeighbors(A).iterator();
			while(b.hasNext()){
				Integer B = b.next();
				c = g.getNeighbors(B).iterator();
				while (c.hasNext()){
					Integer C = c.next();
					if (g.isNeighbor(C, A) || C==A) continue;
					d = g.getNeighbors(C).iterator();
					while (d.hasNext()){
						Integer D = d.next();
						if (D==B) continue; 
						if (g.isNeighbor(D,B)) continue;
						if (g.isNeighbor(D,A)) continue;
						//otherwise, we have a P4

				    	//System.out.print("Edit set of size " +currentSize + " is "+S+"\n");

						existsP4 = true;
						if (currentSize + 1 >= minSoFarCographEdit) return;
						
						//System.out.print("Found P4: "+A+"-"+B+"-"+C+"-"+D+"... not a cograph\n");

						//int choice = randomInt(1,9);
						
						//if (choice <= 3 ) {
						
					    	if (S.isIn(A, B)==-1) {
					    		g.removeEdge(g.findEdge(A, B));
					    		S.add(A,B);
					    		CographEditingMinimization(g,1+currentSize,S);
					    		g.addEdge("mar-"+A+"-"+B, A,B);
					    		S.remove(A,B);
					    	}
						//}
						
						//else if (choice <= 7) {
							
					    	if (S.isIn(B, C)==-1) {
					    		S.add(B,C);
					    		g.removeEdge(g.findEdge(B, C));
					    		CographEditingMinimization(g,1+currentSize,S);
					    		g.addEdge("mar-"+B+"-"+C, B,C);
					    		S.remove(B,C);
					    	}
						//}

						//else if (choice <= 8) {
							
					    	if (S.isIn(C, D)==-1) {
					    		S.add(C,D);
					    		g.removeEdge(g.findEdge(C, D));
					    		CographEditingMinimization(g,1+currentSize,S);
					    		g.addEdge("mar-"+C+"-"+D, C,D);
					    		S.remove(C,D);
					    	}
						//}

						//else {
						
							if (S.isIn(A, C)==-1) {
								S.add(A,C);
								g.addEdge("mar-"+A+"-"+C, A,C);
								CographEditingMinimization(g,1+currentSize,S);
								g.removeEdge(g.findEdge(A, C));
								S.remove(A,C);
							}

							if (S.isIn(B, D)==-1) {
								S.add(B,D);
								g.addEdge("mar-"+B+"-"+D, B,D);
								CographEditingMinimization(g,1+currentSize,S);
								g.removeEdge(g.findEdge(B, D));
								S.remove(B,D);
							}
					    
							if (S.isIn(A, D)==-1) {
								S.add(A,D);
								g.addEdge("mar-"+A+"-"+D, A,D);
								CographEditingMinimization(g,1+currentSize,S);
								g.removeEdge(g.findEdge(A, D));
								S.remove(A,D);
							}
						//}
					   					   					    
						return;
						
					} // end d.hasNext()
				} // end c.hasNext()
			} // end b.hasNext() 
		} // end a.hasNext()

		// if we reached here, no P4 was found
		if (existsP4 == false) {
			// arrived at a cograph since no P4 was found
			System.out.print("\n currentSize = "+currentSize+" and minSoFar = "+minSoFarCographEdit+"\n");
			System.out.print("Modified(Edited) graph has "+g.getEdgeCount()+" edges and is:\n");
			minSoFarCographEdit = currentSize;
			System.out.print(""+g+"\n");
			System.out.print("Edit set is " + S + "\n");
			NewTools.printToFile(currentSize,g,S,"KarateCographEdit.txt");
			NewTools.DrawIt(g);
			PleaseWait(60000); // one minute 
			return;
		}
		
		// ...
		System.out.print("Critical Error. No P_4 found in a non-cograph.\n");
		return;

		
	}

	public static boolean QuasiThresholdDeletion (Graph<Integer,String> g, Integer k){
		// given graph g and int k, decides whether there exist k edge deletions
		// that will turn g into a (P4,C4)-free graph. Will modify the input
		// graph, so call it on a copy of a graph if modification is not desired
		
		if (k<0) return false;
		//System.out.print(" "+k+"\n");
		boolean existsP4orC4 = false;
		
		Iterator<Integer> a = g.getVertices().iterator();
		Iterator<Integer> b;
		Iterator<Integer> c;
		Iterator<Integer> d;
		while(a.hasNext()){
			Integer A = a.next();
			//System.out.print(""+A+" ");
			b = g.getNeighbors(A).iterator();
			while(b.hasNext()){
				Integer B = b.next();
				c = g.getNeighbors(B).iterator();
				while (c.hasNext()){
					Integer C = c.next();
					if (g.isNeighbor(C, A) || C==A) continue;
					d = g.getNeighbors(C).iterator();
					while (d.hasNext()){
						Integer D = d.next();
						if (D==B) continue; 
						if (g.isNeighbor(D,B)) continue;
						//otherwise, we have a P4 or a C4

						existsP4orC4 = true;
						//System.out.print("Found P4: "+A+"-"+B+"-"+C+"-"+D+"... not a cograph\n");

						if (g.isNeighbor(D,A)) {
							// we have a C4 = a-b-c-d-a
							// delete any two edges

							g.removeEdge(g.findEdge(A, B));

							g.removeEdge(g.findEdge(B, C));
							if(QuasiThresholdDeletion(g,k-2) == true) return true;
							g.addEdge("mar-"+B+"-"+C, B,C);

							g.removeEdge(g.findEdge(C, D));
							if(QuasiThresholdDeletion(g,k-2) == true) return true;
							g.addEdge("mar-"+C+"-"+D, C,D);
							
							g.removeEdge(g.findEdge(D, A));
							if(QuasiThresholdDeletion(g,k-2) == true) return true;

							g.addEdge("mar-"+A+"-"+B, A,B);
							// all cases with AB deleted are done. AD is still deleted
							
							g.removeEdge(g.findEdge(B, C));
							if(QuasiThresholdDeletion(g,k-2) == true) return true;
							g.addEdge("mar-"+B+"-"+C, B,C);
							
							g.removeEdge(g.findEdge(C, D));
							if(QuasiThresholdDeletion(g,k-2) == true) return true;

							// all cases with AB deleted or AD deleted are done
							// at this point, CD and AD are still deleted
							// only need to try BC and CD together deleted.
							
							g.addEdge("mar-"+A+"-"+D, A,D);
							g.removeEdge(g.findEdge(B, C));
							if(QuasiThresholdDeletion(g,k-2) == true) return true;

							g.addEdge("mar-"+B+"-"+C, B,C);
							g.addEdge("mar-"+C+"-"+D, C,D);
																			
						}

						else {
							// this case says:
							// else D is NOT adjacent to A. Then we have P4=abcd
							
							g.removeEdge(g.findEdge(A, B));
							if(QuasiThresholdDeletion(g,k-1) == true) return true;
							g.addEdge("mar-"+A+"-"+B, A,B);
							
							
							g.removeEdge(g.findEdge(B, C));
							if(QuasiThresholdDeletion(g,k-1) == true) return true;
							g.addEdge("mar-"+B+"-"+C, B,C);
	
							g.removeEdge(g.findEdge(C, D));
							if(QuasiThresholdDeletion(g,k-1) == true) return true;
							g.addEdge("mar-"+C+"-"+D, C,D);

						}
							
						return false;
					} // end d.hasNext()
				} // end c.hasNext()
			} // end b.hasNext() 
		} // end a.hasNext()

		// if we reached here, no P4 was found
		if (existsP4orC4 == false) {
			// arrived at a quasi-threshold graph since no P4 or C4 was found
			System.out.print("Edge deletion found with " +k+ " left to spare.\n");
			System.out.print("Resulting graph is " + g);
			return true;
		}
		
		// ...
		System.out.print("Critical Error. No P_4 or C_4 found in a non-quasi-threshold graph.\n");
		return false;

		
	}

	public static void QuasiThresholdEditingMinimization (Graph<Integer,String> g, int currentSize, EditSet S){
		// given graph g, uses recursion to find a min QT edge-edit set
		// if input g is the original graph, the currentSize is 0. Pass a new EditSet to
		// create a new computation. Usage:
		// QuasiThresholdEditingMinimization (g, 0, new EditSet())
		
		boolean existsP4orC4 = false;
		if (currentSize >= minSoFarQTEdit) return; 
		
		
		Iterator<Integer> a = g.getVertices().iterator();
		Iterator<Integer> b;
		Iterator<Integer> c;
		Iterator<Integer> d;
		while(a.hasNext()){
			Integer A = a.next();
			//System.out.print(""+A+" ");
			b = g.getNeighbors(A).iterator();
			while(b.hasNext()){
				Integer B = b.next();
				c = g.getNeighbors(B).iterator();
				while (c.hasNext()){
					Integer C = c.next();
					if (g.isNeighbor(C, A) || C==A) continue;
					d = g.getNeighbors(C).iterator();
					while (d.hasNext()){
						Integer D = d.next();
						if (D==B) continue; 
						if (g.isNeighbor(D,B)) continue;
						//otherwise, we have a P4 or a C4

						existsP4orC4 = true;
						//System.out.print("Found P4: "+A+"-"+B+"-"+C+"-"+D+"... not a cograph\n");

						if (g.isNeighbor(D,A)) {
							// we have a C4 = a-b-c-d-a
							// add a diagonal chord or delete any two edges

							if (S.isIn(A, C)==-1) {
								g.addEdge("e"+A+","+C, A,C);
								S.add(A,C);
								QuasiThresholdEditingMinimization(g,1+currentSize,S);
								g.removeEdge(g.findEdge(A, C));
								S.remove(A,C);
							}

							if (S.isIn(B,D) == -1) {
								g.addEdge("e"+B+","+D, B,D);
								S.add(B,D);
								QuasiThresholdEditingMinimization(g,1+currentSize,S);
								g.removeEdge(g.findEdge(B,D));
								S.remove(B,D);
							}
							
							if (S.isIn(A,B) == -1) {
								g.removeEdge(g.findEdge(A, B));
								S.add(A,B);
							
								if (S.isIn(B,C) == -1) {
									g.removeEdge(g.findEdge(B, C));
									S.add(B,C);
								
									QuasiThresholdEditingMinimization(g,2+currentSize,S);
							
									g.addEdge("mar-"+B+"-"+C, B,C);
									S.remove(B,C);
								}
								
								if (S.isIn(C,D) == -1) {
									g.removeEdge(g.findEdge(C, D));
									S.add(C,D);
									
									QuasiThresholdEditingMinimization(g,2+currentSize,S);

									g.addEdge("mar-"+C+"-"+D, C,D);
									S.remove(C,D);
								}
							

								if (S.isIn(D, A) == -1) {
									g.removeEdge(g.findEdge(D, A));
									S.add(D,A);
									
									QuasiThresholdEditingMinimization(g,2+currentSize,S);

									g.addEdge("mar-"+D+"-"+A, D,A);
									S.remove(D,A);
								}
							
								g.addEdge("mar-"+A+"-"+B,A,B);
								S.remove(A, B);
							}
							// all cases with AB deleted are done.
								
							if (S.isIn(D,A) == -1) {
								g.removeEdge(g.findEdge(D,A));
								S.add(D,A);
							
								if (S.isIn(B,C) == -1) {
									g.removeEdge(g.findEdge(B, C));
									S.add(B,C);
									
									QuasiThresholdEditingMinimization(g,currentSize+2,S);

									g.addEdge("mar-"+B+"-"+C, B,C);
									S.remove(B,C);
								}
							
								if (S.isIn(C,D) == -1) {
									g.removeEdge(g.findEdge(C, D));
									S.add(C,D);
									
									QuasiThresholdEditingMinimization(g,currentSize+2,S);

									g.addEdge("mar-"+C+"-"+D,C,D);
									S.remove(C,D);
								}
								
								g.addEdge("mar-"+A+"-"+D, A,D);
								S.remove(D,A);
							}
								
							// all cases with AB deleted or AD deleted are done
							// only need to try BC and CD together deleted.

							if (S.isIn(B,C)== -1 && S.isIn(C, D)== -1) {

								g.removeEdge(g.findEdge(B, C));
								g.removeEdge(g.findEdge(C, D));
								S.add(B,C);
								S.add(C,D);
								
								QuasiThresholdEditingMinimization(g,currentSize+2,S);

								g.addEdge("mar-"+B+"-"+C, B,C);
								g.addEdge("mar-"+C+"-"+D, C,D);
								S.remove(B,C);
								S.remove(C,D);
							}
							
																			
						} // end of C4-handling cases

						else {
							// this case says:
							// else D is NOT adjacent to A. Then we have P4=abcd
							// either add ac or bd or else delete one edge
							
						//int branchCase = randomInt(1,5);	
						//if (branchCase <= 2) {
							if (S.isIn(A, C) == -1) {
								g.addEdge("e"+A+","+C, A, C);
								S.add(A,C);
								
								QuasiThresholdEditingMinimization(g,currentSize+1,S);
								
								g.removeEdge(g.findEdge(A, C));
								S.remove(A,C);
							}

							if (S.isIn(B,D) == -1) {
								g.addEdge("e"+B+","+D, B, D);
								S.add(B,D);
								
								QuasiThresholdEditingMinimization(g,currentSize+1,S);
								
								g.removeEdge(g.findEdge(B, D));
								S.remove(B,D);
							}
						//}
						
						//else if (branchCase == 3) {
							if (S.isIn(A,B)== -1) {
								g.removeEdge(g.findEdge(A, B));
								S.add(A, B);
								
								QuasiThresholdEditingMinimization(g,currentSize+1,S);

								g.addEdge("mar-"+A+"-"+B, A,B);
								S.remove(A,B);
							}
						//}
						
						//else if (branchCase == 4) {
							if (S.isIn(B,C) == -1) {
								g.removeEdge(g.findEdge(B, C));
								S.add(B, C);
								
								QuasiThresholdEditingMinimization(g,currentSize+1,S);
								
								g.addEdge("mar-"+B+"-"+C, B,C);
								S.remove(B,C);
							}
						//}
						
						//else if (branchCase == 5) {
							if (S.isIn(C,D) == -1) {
								g.removeEdge(g.findEdge(C, D));
								S.add(C,D);
								
								QuasiThresholdEditingMinimization(g,currentSize+1,S);
								
								g.addEdge("mar-"+C+"-"+D, C,D);
								S.remove(C,D);
							}
						//}
							
						} // end of P4-handling
							
						if (existsP4orC4 == true) return;
						
					} // end d.hasNext()
				} // end c.hasNext()
			} // end b.hasNext()
		} // end a.hasNext()

		// if we reached here, no P4 was found
		if (existsP4orC4 == false) {
			// arrived at a quasi-threshold graph since no P4 or C4 was found
			System.out.print("\n currentSize = "+currentSize+" and minSoFar = "+minSoFarQTEdit+"\n");
			System.out.print("Modified(Edited) graph has "+g.getEdgeCount()+" edges and is:\n");
			minSoFarQTEdit = currentSize;
			System.out.print(""+g+"\n");
			System.out.print("Edit set is " + S + "\n");
			NewTools.printToFile(currentSize,g,S,"QTEditLesMis.txt");
			return;
		}
		
		// ...
		System.out.print("Critical Error. No P_4 or C_4 found in a non-quasi-threshold graph.\n");
		return;
	}
	

	
	private static void printToFile(int currentSize, Graph<Integer, String> g, EditSet s, String fname) {

		try {
		
			PrintStream out = new PrintStream(new FileOutputStream(fname,true));	      
			out.println("Found solution of size "+currentSize+"\n");
			out.println("Graph is "+g+"\n");
			if (s!=null) {
				out.println("Edit Set is "+s.toString()+"\n");
			}
		    out.close();
		}
		
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public static void printGML(Graph<Integer, String> g, String fname) {

		try {
		
			PrintStream out = new PrintStream(new FileOutputStream(fname,true));	      
			out.println("Creator \"James Nastos\"\n");
			out.println("Version \"1.0\"\n");
			out.println("graph\n[");
			
			for (Integer v : g.getVertices()) {
				out.println("node [ id "+v+"\n");
				out.println("label \""+v+"\"\n");
				out.println("]\n");
			}
			
			for (Integer v : g.getVertices()) {
				for (Integer u : g.getVertices()) {
					if (u<v && g.isNeighbor(u, v)) {
						out.println ("edge [ source "+ u + " target " + v + " ]\n");
					}
				}
			}
			
			out.println("]\n");
			
		    out.close();
		}
		
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	
	public static ArrayList<Integer> MCS (Graph<Integer,String> g) {
		// Takes input graph g and returns an ArrayList of vertices
		// representing a maximum-cardinality search of its vertices,
		// starting arbitrarily at the first vertex in the vertex set
		// See also overloaded methods:
		// MCS (g,v) : starts at a vertex v
		// MCS (g,u,v) : returns the distance between u,v (shortest path)
		// isConnected(g) : uses MCS to return true if connected
		// isConnected(g,u,v) : uses MCS to see if u and v are in the same connected comp
		
		//g = WeightedGraph<Integer,String>(g);
	
		
		ArrayList<Integer> MCSOrder = new ArrayList<Integer>(0);
		Iterator i = g.getVertices().iterator();
		
		return null;
	}
	
	public static boolean CographAddition (Graph<Integer,String> g, Integer k){
		//THIS METHOD OPERATES ON g. IT WILL MODIFY INPUT GRAPH g TOWARDS A
		//COGRAPH. IF THIS IS NOT THE DESIRED BEHAVIOUR, RUN THIS ON A COPY
		//OF THE WORKING GRAPH.
		/*
		if (IsA.Cograph(g) == true) {
			System.out.print("Found deletion set with " + k + " more deletions available\n");
			return true;
		}
		*/
		if (k < 0) return false;
		
		boolean existsP4 = false;
		
		Iterator<Integer> a = g.getVertices().iterator();
		Iterator<Integer> b;
		Iterator<Integer> c;
		Iterator<Integer> d;
		while(a.hasNext()){
			Integer A = a.next();
			b = g.getNeighbors(A).iterator();
			while(b.hasNext()){
				Integer B = b.next();
				c = g.getNeighbors(B).iterator();
				while (c.hasNext()){
					Integer C = c.next();
					if (g.isNeighbor(C, A) || C==A) continue;
					d = g.getNeighbors(C).iterator();
					while (d.hasNext()){
						Integer D = d.next();
						if (g.isNeighbor(D,A)) continue;
						if (g.isNeighbor(D,B)) continue;
						//otherwise, we have a P4
						existsP4 = true;
						//System.out.print("Found P4: "+A+"-"+B+"-"+C+"-"+D+"... not a cograph\n");
						
							g.addEdge("e:"+A+"-"+C, A, C);					
							if(CographAddition(g,k-1) == true) return true;
							g.removeEdge(g.findEdge(A, C));
						

							g.addEdge("e:"+A+"-"+D, A, D);
							if(CographAddition(g,k-1) == true) return true;
							g.removeEdge(g.findEdge(A, D));

							g.addEdge("e:"+B+"-"+D, B, D);
							if(CographAddition(g,k-1) == true) return true;
							g.removeEdge(g.findEdge(B, D));
						
						return false;
					} // end d.hasNext()
				} // end c.hasNext()
			} // end b.hasNext() 
		} // end a.hasNext()

		// if we reached here, no P4 was found
		if (existsP4 == false) {
			// arrived at a cograph since no P4 was found
			System.out.print("Edge addition found with " +k+ " left to spare.\n");
			System.out.print("Resulting graph is " + g + "\n");
			return true;
		}
		
		// caught at the initial test for isCograph().
		System.out.print("Critical Error. No P_4 found in a non-cograph.\n");
		return false;
	}
	
	public static Graph<Integer, String> complement(Graph<Integer,String> g) {

		Graph<Integer,String> h = new SparseGraph<Integer,String>();
		
		
		Iterator<Integer> a = g.getVertices().iterator();
		// build vertices
		while (a.hasNext()) {
			h.addVertex(a.next());
		}
		
		// add edges
		a = g.getVertices().iterator();
		while(a.hasNext()){
			Integer A = a.next();
			Iterator<Integer> b = g.getVertices().iterator();
			while(b.hasNext()){
				Integer B = b.next();
				if (A<B) {
					if (g.isNeighbor(A, B) == false) h.addEdge("e"+A+"-"+B, A, B);
				}
			}
		}
	
		return h;
	}

		
	public static void CheckClasses(Graph<Integer, String> original) {

		Graph<Integer,String> g = new SparseGraph<Integer,String>();
		Graph<Integer,String> coG = new SparseGraph<Integer,String>();
		g = Copy(original);
		coG = complement(g);
		
		boolean isChordal = IsA.Chordal(g);
		boolean isCoChordal = IsA.Chordal(coG);
		
		g = Copy(original);
		
		boolean isCograph = IsA.Cograph(g);
		
		if (isChordal) {

			System.out.print("This graph is Chordal!\n");

			if (isCograph) {
				System.out.print ("This graph is Quasi-Threshold!\n");
			}
			
			if (isCoChordal) {
				System.out.print ("This graph is Split!\n");
			}
		}
		else {
			System.out.print("This graph is not Chordal\n");
		}
		
		if (isCograph) {
			System.out.print("This graph is a Cograph!\n");
		}
		else {
			System.out.print("This graph is not a Cograph.\n");
		}

		g = Copy(original);
		boolean isCPP = IsA.CPP(g);
		if (isCPP) System.out.print("This graph is (C5,P5,coP5)-free!\n");
		else System.out.print("This graph is not (C5,P5,coP5)-free\n");
	}
	
	public static void drawGraph (Graph<Integer,String> g) {
		
		JFrame f = new JFrame();
		ISOMLayout<Integer,String> layout = new ISOMLayout<Integer,String>(g);
		BasicVisualizationServer<Integer,String> vv =
			new BasicVisualizationServer<Integer,String>(layout);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(vv);
        f.pack();
        f.setVisible(true);
	}
}