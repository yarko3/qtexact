package bipartiteConvergence;

import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.JFrame;

import reduction.c4p4Reduction;
import reduction.clusterReductionBasic;
import reduction.cographReduction;
import utils.Generate;
import abstractClasses.Branch;
import branch.qtAllStruct;
import clusterRules.clusterAllStruct;
import cographRules.cographAllStruct;

import com.rits.cloning.Cloner;
import components.branchComponents;

import controller.Controller;
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import greedy.clusterGreedy;
import greedy.cographGreedy;
import greedy.maxObsGreedy;

public class YongConvergence<V>
{
	/**
	 * Used for finding connected components (communities)
	 */
	private static WeakComponentClusterer<String, Pair<String>> cluster = new WeakComponentClusterer<String, Pair<String>>();
	/**
	 * All the graph editing means
	 * Index:
	 * 0 - Cluster Editing
	 * 1 - Cograph Editing
	 * 3 - QT Editing
	 */
	private static ArrayList<Branch<String>> bStruct;
	/**
	 * controller used for graph editing
	 */
	private static Controller<String> c = new Controller<String>(null, true);
	
	/**
	 * cloner for testing
	 */
	private static Cloner clone = new Cloner();
	
	/*
	 * Initialize graph editing stuff
	 */
	static
	{
		bStruct = new ArrayList<Branch<String>>();
		//create new cluster editing method
		Branch<String> temp = new branchComponents<String>(c, new clusterAllStruct<String>(c));
		//add reduction rules to cluster editor
		temp.addReduction(new clusterReductionBasic<String>(temp));
		//set greedy for cluster
		temp.setDive(new clusterGreedy<String>(temp));
		//add to ArrayList
		bStruct.add(temp);
		
		//create new cograph editing method
		temp = new branchComponents<String>(c, new cographAllStruct<String>(c));
		//add reduction rules to cograph editor
		temp.addReduction(new cographReduction<String>(temp));
		//set greedy for cograph
		temp.setDive(new cographGreedy<String>(temp));
		//add to ArrayList
		bStruct.add(temp);
		
		//create new qt editing method
		temp = new branchComponents<String>(c, new qtAllStruct<String>(c));
		//add reduction rules to qt editor
		temp.addReduction(new c4p4Reduction<String>(temp));
		//set greedy for qt
		temp.setDive(new maxObsGreedy<String>(temp));
		//add to ArrayList
		bStruct.add(temp);
	}
	
	
	/**
	 * performs the bipartite convergence
	 * @param args
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException
	{
		Generate<String> gen = new Generate<String>();
		//editing method used
		int method = 0;
		//side to project (false = left, true = right)
		boolean side = false;
		
		Generate.randomBipartiteGraph(10, 20, .35, 0.02, 7);
		
		
		//Generate.randomBipartiteGraph(20, 30, .15, 6);
		
		String filename = "datasets/bipartite/random.txt";
		
		
		//get initial projection graph
		Graph<String, Pair<String>> bipartiteProj = Generate.bipartiteProjection(filename, 4, side);
		
		//original projection components
		int originalProjComponentCount = cluster.transform(bipartiteProj).size();
		
		//visualize original graph
		visualize(clone.deepClone(bipartiteProj));
		
		//vertices from both sides
		Hashtable<String, HashSet<String>> left = Generate.leftBipartiteVertices(filename);
		Hashtable<String, HashSet<String>> right = Generate.rightBipartiteVertices(filename);
		
		
		//edit initial projection graph
		Graph<String, Pair<String>> editedProj = graphEdit(bipartiteProj, method);
		
		//original projection components
		int editedProjComponentCount = cluster.transform(editedProj).size();
				
		
		//construct the clique-superposition graph of other side
		Graph<String, Pair<String>> cliqueSuper = cliqueSuper(editedProj, left, right, side);
		
		//edit superposition graph
		Graph<String, Pair<String>> editedSuper = graphEdit(cliqueSuper, method);
		
		//construct the new projection graph
		Graph<String, Pair<String>> newProj = cliqueSuper(editedSuper, left, right, !side);
		
		//edit new projection graph
		Graph<String, Pair<String>> editNewProj = graphEdit(newProj, method);
		
		//how close is this new projection graph to the old one?
		System.out.println("Is the new projection the same as the old? " + gen.graphEquals(editedProj, editNewProj));
		
		System.out.println("Old projection vertices: " + editedProj.getVertexCount());
		System.out.println("New projection vertices: " + editNewProj.getVertexCount());
		
		System.out.println("Old projection edges: " + editedProj.getEdgeCount());
		System.out.println("New projection edges: " + editNewProj.getEdgeCount());
		
		
		int count = 1;
		
		//keep running until convergence
		while (!gen.graphEquals(editedProj, editNewProj))
		{
			//new becomes old
			editedProj = editNewProj;
			
			//construct new super
			cliqueSuper = cliqueSuper(editedProj, left, right, side);
			
			//edit super
			editedSuper = graphEdit(cliqueSuper, method);
			
			//new projection
			newProj = cliqueSuper(editedSuper, left, right, !side);
			
			//edit new projection
			editNewProj = graphEdit(newProj, method);
			
			
			System.out.println("Is the new projection the same as the old? " + gen.graphEquals(editedProj, editNewProj));
			
			System.out.println("Old projection vertices: " + editedProj.getVertexCount());
			System.out.println("New projection vertices: " + editNewProj.getVertexCount());
			
			System.out.println("Old projection edges: " + editedProj.getEdgeCount());
			System.out.println("New projection edges: " + editNewProj.getEdgeCount());
			
			count++;
		}
		
		
		System.out.println("\nIterations to convergence: " + count);
		System.out.println("Start number of projection components: " + originalProjComponentCount);
		System.out.println("Start edited number of projection components: " + editedProjComponentCount);
		System.out.println("End number of projection components: " + cluster.transform(editNewProj).size());
		
		visualize(editedProj);
		
	}
	
	private static Graph<String, Pair<String>> graphEdit(Graph<String, Pair<String>> graph, int method)
	{
		//set method to be used
		c.setbStruct(bStruct.get(method));
		
		//try greedy editing
		return c.diveAtStartEdit(graph, 11).getG();
	}
	
	private static Graph<String, Pair<String>> cliqueSuper(Graph<String, Pair<String>> edited, Hashtable<String, HashSet<String>> left, Hashtable<String, HashSet<String>> right, boolean side)
	{
		
		//set the right side (lol)
		Hashtable<String, HashSet<String>> tempLeft;
		
		//use right side as left side
		if (side)
		{
			tempLeft = right;
		}
		else
		{
			tempLeft = left;
		}
		
		Graph<String, Pair<String>> rtn = new UndirectedSparseGraph<String, Pair<String>>();
		
		//for every community in edited graph, construct a clique in new graph on right side
		Set<Set<String>> components = cluster.transform(edited);
		 
		//for every component, construct a clique
		for (Set<String> component : components)
		{
			//vertices to make a clique
			HashSet<String> cliqueVertices = new HashSet<String>();
			
			//look through the members of each component in original graph
			for (String v : component)
			{
				for (String v2 : component)
				{
					if (v.equals(v2))
						continue;
					
					HashSet<String> intersection = new HashSet<String>(tempLeft.get(v));
					intersection.retainAll(tempLeft.get(v2));
					
					cliqueVertices.addAll(intersection);
				}
				
//				cliqueVertices.addAll(tempLeft.get(v));
			}
			
			//build clique in return graph
			for (String v1 : cliqueVertices)
				for (String v2 : cliqueVertices)
				{
					if (v1.equals(v2))
						continue;
					
					rtn.addEdge(new Pair<String>(v1, v2), v1, v2);
				}
		}
		return rtn;
	}
	
	
	public static void visualize(Graph<String, Pair<String>> exampleQT){
		JFrame jf = new JFrame();
		jf.setSize(1200, 500);

		FRLayout frl = new FRLayout(exampleQT);

		frl.setAttractionMultiplier(3);
		frl.setRepulsionMultiplier(1.1);
		
		frl.setMaxIterations(1000);
	
		//frl.lock(true);
		VisualizationViewer vv = new VisualizationViewer(frl, new Dimension(
				1200, 500));
		
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		
		jf.getContentPane().add(vv);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);
		
	}

}
