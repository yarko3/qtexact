package bipartiteConvergence;

import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.JFrame;

import qtUtils.branchingReturnC;
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
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException
	{
//		//generate lots of test cases and run them
//		int leftMax = 16;
//		int rightMax = 20;
//		
//		leftLoop:
//		for (int left = 15; left < leftMax; left++)
//		{
//			rightLoop:
//			for (int right = 15; right < rightMax; right++)
//			{
//				percentLoop:
//				for (double percent = .2; percent < 0.9; percent+=0.2)
//				{
//					for (double innerPercent = 0.01; innerPercent < 0.05; innerPercent+=0.01)
//					{
//						seedLoop:
//						for (int seed = 0; seed < 1; seed++)
//						{
//							//generate the random graph
//							Generate.randomBipartiteGraph(left, right, percent, innerPercent, seed);
//							kLoop:
//							for(int k = 2; k < 15; k++)
//							{
//								//if the result is empty, stop looking
//								if (!yongConvergence("datasets/bipartite/random.txt", "datasets/bipartite/qt/qtL"+left+"R"+right+"P"+String.format("%.1f", percent)+"I"+String.format("%.2f", innerPercent)+"S"+seed+"K"+k+".txt",
//										k, 2, false))
//									break kLoop;
//	
//								
//							}
//						}
//					}
//				}
//			}
//		}
//		Generate.randomBipartiteGraph(100, 200, 0.8, 0.2, 0);
		
		yongConvergence("datasets/bipartite/random.txt", "datasets/bipartite/test.txt",
				93, -1, false);
	}
	
	/**
	 * Given a bipartite edge set in file, try Yong convergence on it
	 * @param inputFileName path to input file
	 * @param outputFileName path to output file
	 * @param k projection parameter
	 * @param method 0 for Cluster Editing, 1 for Cogrpah Editing, 2 for QT Editing
	 * @param side false for projection on left side, true for projection on right side
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static boolean yongConvergence(String inputFileName, String outputFileName, int k, int method, boolean side) throws FileNotFoundException, UnsupportedEncodingException
	{
		Generate<String> gen = new Generate<String>();
		
		//only works when editing succeeds
		try
		{
		
			//number of iterations
			int count = 1;
			
			//set up writer
			PrintWriter writer = new PrintWriter(outputFileName, "UTF-8");
			
			//writer column titles
			writer.println("Iteration\tVertices\tEdges\tComponents");
			
			//get initial projection graph
			Graph<String, Pair<String>> bipartiteProj = Generate.bipartiteProjection(inputFileName, k, side);
			
			//original projection components
			int originalProjComponentCount = cluster.transform(bipartiteProj).size();
			
			//visualize original graph
			//visualize(clone.deepClone(bipartiteProj));
			
			//vertices from both sides
			Hashtable<String, HashSet<String>> left = Generate.leftBipartiteVertices(inputFileName);
			Hashtable<String, HashSet<String>> right = Generate.rightBipartiteVertices(inputFileName);
			
			
			//edit initial projection graph
			Graph<String, Pair<String>> editedProj = graphEdit(bipartiteProj, method);
			
			//original projection components
			int editedProjComponentCount = cluster.transform(editedProj).size();
					
			
			//construct the clique-superposition graph of other side
			Graph<String, Pair<String>> cliqueSuper = cliqueSuper(editedProj, left, right, k, side);
			
			//edit superposition graph
			Graph<String, Pair<String>> editedSuper = graphEdit(cliqueSuper, method);
			
			//construct the new projection graph
			Graph<String, Pair<String>> newProj = cliqueSuper(editedSuper, left, right, k, !side);
			
			//edit new projection graph
			Graph<String, Pair<String>> editNewProj = graphEdit(newProj, method);
			
			//how close is this new projection graph to the old one?
			System.out.println("Is the new projection the same as the old? " + gen.graphEquals(editedProj, editNewProj));
			
			System.out.println("Old projection vertices: " + editedProj.getVertexCount());
			System.out.println("New projection vertices: " + editNewProj.getVertexCount());
			
			System.out.println("Old projection edges: " + editedProj.getEdgeCount());
			System.out.println("New projection edges: " + editNewProj.getEdgeCount());
			
			//write stuff
			writer.println("0\t" + editedProj.getVertexCount()+ "\t" + editedProj.getEdgeCount() + "\t" + editedProjComponentCount );
			writer.println("1\t" + editNewProj.getVertexCount()+ "\t" + editNewProj.getEdgeCount() + "\t" + cluster.transform(editNewProj).size());
			
			
			//keep running until convergence
			while (!gen.graphEquals(editedProj, editNewProj))
			{
				count++;
				
				//new becomes old
				editedProj = editNewProj;
				
				//construct new super
				cliqueSuper = cliqueSuper(editedProj, left, right, k, side);
				
				//edit super
				editedSuper = graphEdit(cliqueSuper, method);
				
				//new projection
				newProj = cliqueSuper(editedSuper, left, right, k, !side);
				
				//edit new projection
				editNewProj = graphEdit(newProj, method);
				
				
				System.out.println("Is the new projection the same as the old? " + gen.graphEquals(editedProj, editNewProj));
				
				System.out.println("Old projection vertices: " + editedProj.getVertexCount());
				System.out.println("New projection vertices: " + editNewProj.getVertexCount());
				
				System.out.println("Old projection edges: " + editedProj.getEdgeCount());
				System.out.println("New projection edges: " + editNewProj.getEdgeCount());
				
				writer.println(count + "\t" + editNewProj.getVertexCount()+ "\t" + editNewProj.getEdgeCount() + "\t" + cluster.transform(editNewProj).size());
				
			}
			
			System.out.println("\nIterations to convergence: " + count);
			System.out.println("Start number of projection components: " + originalProjComponentCount);
			System.out.println("Start edited number of projection components: " + editedProjComponentCount);
			System.out.println("End number of projection components: " + cluster.transform(editNewProj).size());
			
			//visualize(editedProj);
			
			writer.close();
			
			//delete file if it yielded nothing interesting
			try {
				if (count == 1 && editNewProj.getEdgeCount() ==0)
					Files.delete(Paths.get(outputFileName));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//return true if result was not an empty graph
			return editNewProj.getEdgeCount() > 0;
		}
		catch(SolutionNotFoundException solException)
		{
			//delete file if one was created
			try {
				Files.deleteIfExists(Paths.get(outputFileName));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
	}
	
	private static Graph<String, Pair<String>> graphEdit(Graph<String, Pair<String>> graph, int method) throws SolutionNotFoundException
	{
		
		//if we're actually editing
		if (method >= 0)
		{
			//set method to be used
			c.setbStruct(bStruct.get(method));
			
			//try greedy editing
			branchingReturnC<String> rtn = c.diveAtStartEdit(graph, 10);
			
			//if solution was not found, throw error
			if (!rtn.isSolutionFound())
				throw new SolutionNotFoundException();
			
			return rtn.getG();
		}
		else
			return clone.deepClone(graph);
		
	}
	
	private static Graph<String, Pair<String>> cliqueSuper(Graph<String, Pair<String>> edited, Hashtable<String, HashSet<String>> left, Hashtable<String, HashSet<String>> right, int k, boolean side)
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
			
			//build a hashtable for clique super graph
			Hashtable<String, Integer> occurrenceTable = new Hashtable<String, Integer>();
			
			//for every member of community, count how many times each vertex on opposite side is encountered
			for (String v : component) 
			{
				for (String temp : tempLeft.get(v))
				{
					//haven't encountered this one before
					if (!occurrenceTable.containsKey(temp))
					{
						occurrenceTable.put(temp, 0);
					}
					//increment
					occurrenceTable.put(temp, occurrenceTable.get(temp)+1);
				}
			}
			
			//add vertices to cliqueVertices
			for (String temp : occurrenceTable.keySet())
			{
				//if we've seen this opposite side vertex in k or more members of community, add it
				if (occurrenceTable.get(temp) >= k)
					cliqueVertices.add(temp);
			}
			
//			//look through the members of each component in original graph
//			for (String v : component)
//			{
//				for (String v2 : component)
//				{
//					if (v.equals(v2))
//						continue;
//					
//					HashSet<String> intersection = new HashSet<String>(tempLeft.get(v));
//					intersection.retainAll(tempLeft.get(v2));
//					
//					cliqueVertices.addAll(intersection);
//				}
//				
////				cliqueVertices.addAll(tempLeft.get(v));
//			}
			
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
