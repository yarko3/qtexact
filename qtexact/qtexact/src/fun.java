/**
 * Yaroslav Senyuta
 * NSERC USRA Grant (2014)
 */

import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JApplet;
import javax.swing.JFrame;

import modularDecomposition.myGraph;
import qtUtils.branchingReturnC;
import qtUtils.qtGenerate;
import reduction.biconnectedReduction;
import reduction.c4p4Reduction;
import reduction.centralNodeReduction;
import reduction.clusterReductionBasic;
import reduction.commonC4Reduction;
import reduction.edgeBoundReduction;
import scorer.familialGroupCentrality;
import search.YanSearch;
import search.clusterSearch;
import search.cographSearch;
import search.diQTSearch;
import search.qtLBFSNoHeuristic;
import utils.distance;
import utils.graphFromEdgeSetWithCommunities;
import utils.graphUtils;
import abstractClasses.Branch;
import abstractClasses.Dive;
import abstractClasses.GreedyEdit;
import abstractClasses.Reduction;
import abstractClasses.SearchResult;
import branch.diQTBranch;
import branch.qtAllStruct;
import branch.qtBranch;
import branch.qtBranchComponents;
import branch.qtBranchNoHeuristic;
import clusterRules.clusterAllStruct;
import clusterRules.clusterBranch;
import cographRules.cographAllStruct;
import cographRules.cographBranch;

import com.rits.cloning.Cloner;
import components.branchComponents;

import controller.Controller;
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.scoring.ClosenessCentrality;
import edu.uci.ics.jung.algorithms.scoring.EigenvectorCentrality;
import edu.uci.ics.jung.algorithms.scoring.HITS;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import greedy.clusterGreedy;
import greedy.cographGreedy;
import greedy.diQTGreedy;
import greedy.maxObsGreedy;

@SuppressWarnings("serial")
public class fun<V> extends JApplet {

	/**
	 * graph
	 */
	static Graph<Integer, String> graph;
	static Cloner clone = new Cloner();
	static qtGenerate<Integer> gen = new qtGenerate<Integer>();
	static qtGenerate<String> genString = new qtGenerate<String>();
	
	static graphUtils<String> stringUtils = new graphUtils<String>();
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException
	{
		//fbTest();
		//editTest();
		//comparisonTest();
		//wineTest();
		//userInterface();
		//diGraphWineryTest();
		//clusterTest();
		//scoreWineryGraph();
		//distanceTest();
		//cographTest();
		//wineryProjectionTest();
		//getProvinceSpecificExternalsEdgeList();
		//projectionAnalysis();
		
		//winerykExternalProjections();
		//externalProjectionsClique();
		//outputWeightedProjection();
		//projectionClusterAnalysis();
		
		//mdTest();
		//getRules();
		
		//clusterComparisonTest();
	
		//clusterCommonExternals();
		
		honoursTest();
	}
	
	

	public static void userInterface() throws FileNotFoundException
	{
		Controller<Integer> c = new Controller<Integer>(null, true);
		
		System.out.println(
				"Enter graph to edit to be quasi-threshold:\n"
				+ "1 - From file\n"
				+ "2 - Zachary's Karate Club\n"
				+ "3 - Grass Web\n"
				+ "4 - Erdos Renyi Graph\n"
				+ "5 - Random QT Graph from Tree\n");
		
		Scanner scan = new Scanner(System.in);
		
		int g = scan.nextInt();
		
		Graph<Integer, Pair<Integer>> graph = null;
		qtGenerate<Integer> gen = new qtGenerate<Integer>();
		int nodes;
		long seed;
		switch (g)
		{
		case 1: 
			System.out.println("\nEnter path of graph file (integer edge list):");
			String path = scan.next();
			graph = fillGraphFromFile(path);
			break;
		case 2:
			graph = fillGraphFromFile("datasets/zachary.txt");
			break;
		case 3:
			graph = fillGraphFromFile("datasets/grass_web.pairs");
			break;
		case 4:
			System.out.println("\nEnter number of nodes in graph: ");
			nodes = scan.nextInt();
			System.out.println("\nEnter probability: ");
			double probability = scan.nextDouble();
			System.out.println("\nSet random seed: ");
			seed = scan.nextLong();
			
			graph = qtGenerate.ER(nodes, probability, seed);
			break;
		case 5:
			System.out.println("\nEnter number of nodes in graph: ");
			nodes = scan.nextInt();
			System.out.println("\nSet random seed: ");
			seed = scan.nextLong();
			
			graph = gen.treeRandom(nodes, seed);
			break;
		}
		
		System.out.println("\nGraph generated successfully. Visualize graph? (true/false) ");
		boolean v = scan.nextBoolean();
		
		if (v)
			visualize(clone.deepClone(graph));
		
		
		System.out.println("\nSelect branching strategy\n"
				+ "1 - branch on P4/C4 only\n"
				+ "2 - branch on all 5 vertex obstructions\n"
				+ "3 - (fastest) branch on all 5 vertex obstructions with connected components\n");
		
		int branch = scan.nextInt();
		
		qtBranch<Integer> bStruct = null;
		
		switch (branch)
		{
		case 1:
			bStruct = new qtBranchNoHeuristic<Integer>(c);
			break;
		case 2:
			bStruct = new qtAllStruct<Integer>(c);
			break;
		case 3:
			bStruct = new qtBranchComponents<Integer>(c);
			break;
		}
		
		System.out.println("\nAdd C4/P4 reduction (recommended)? (true/false) ");
		boolean temp = scan.nextBoolean();
		
		if (temp)
			bStruct.addReduction(new c4p4Reduction<Integer>(bStruct));
		
		System.out.println("\nAdd biconnected reduction? (true/false) ");
		temp = scan.nextBoolean();
		
		if (temp)
			bStruct.addReduction(new biconnectedReduction<Integer>(bStruct));
		
		System.out.println("\nAdd common node reduction? (true/false) ");
		temp = scan.nextBoolean();
		
		if (temp)
			bStruct.addReduction(new centralNodeReduction<Integer>(bStruct));
		
		
		System.out.println("\nEnter allowed branching depth: ");
		int depth = scan.nextInt();
		
		c.setbStruct(bStruct);
		System.out.println("\nStart:");
		long start = System.currentTimeMillis();
		c.branchStart(graph, depth).getG();
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
	}
	
	
	public static void editTest() throws FileNotFoundException, UnsupportedEncodingException 
	{
		Graph<Integer, Pair<Integer>> exampleQT;
		exampleQT = gen.randomQT(50);
		//may break it
		exampleQT.addEdge(new Pair<Integer>(0, 6), 0, 6);
		exampleQT.addEdge(new Pair<Integer>(8, 1), 8, 1);
		exampleQT.addEdge(new Pair<Integer>(8, 5), 8, 5);
		
		exampleQT = gen.cliqueJoin(10, 5);
		
		//exampleQT = qtGenerate.simpleC4();
		
		//exampleQT = qtGenerate.westernElectricNetwork();
		
		//exampleQT = qtGenerate.nonQTEx6();
		
		//random graph join
		//exampleQT = gen.erJoins(8, 8, 5, .86, .86, .9);
		
		exampleQT = gen.ER(16, 0.1, (long) 3);
		
		exampleQT = fillGraphFromFile("datasets/zachary.txt");
		
		
		exampleQT = gen.randomTreeGraph(40, 15, 6);
		
		
		//Graph<String, Pair<String>>wine = fillGraphFromFile("datasets/wineryEdgeSet.txt");
		
		
		//Graph<String, Pair<String>> wine = gen.fromBipartiteFile("datasets/edgeSet.txt");
		
		//exampleQT = gen.fromBipartiteFile("datasets/southernwomen");
	
		//exampleQT = gen.manyInducedC4(6);
		
		//exampleQT = gen.houseStruct();
		
		
		
		//exampleQT = fillGraphFromFile("datasets/grass_web.pairs");

		
		//exampleQT = gen.treeRandom(150, 5);
		
		//exampleQT = gen.houseStruct();
		

		
		//exampleQT = gen.facebookGraph("datasets/fbFriends.txt");
		
		Graph<Integer, Pair<Integer>> cln = clone.deepClone(exampleQT);


		
		visualize(cln);
		
		Controller<Integer> c = new Controller<Integer>(null, true);
		
		
		
		//dive = new randomDive<Integer>(null);

//		
		qtAllStruct<Integer> all = new qtAllStruct<Integer>(c);
//		
		qtAllStruct<Integer> all2 = new qtAllStruct<Integer>(c);
		
		
		qtBranchComponents<Integer> branchC = new qtBranchComponents<Integer>(c);
		
		
		
		//qtBranchComponents<Integer> branchC2 = new qtBranchComponents<Integer>(c);
		
//		qtSimple<Integer> simple = new qtSimple<Integer>(c);
//		
//		qtRandom<Integer> random = new qtRandom<Integer>(c);
		
		
		qtBranchNoHeuristic<Integer> nothing = new qtBranchNoHeuristic<Integer>(c);
		
		Reduction<Integer> rC;
		
		
		rC = new c4p4Reduction<Integer>(branchC);
		branchC.addReduction(rC);
//		
//		rC = new biconnectedReduction<Integer>(branchC);
//		branchC.addReduction(rC);
		
//		rC = new c4p4Reduction<Integer>(nothing);
//		nothing.addReduction(rC);
		
		rC = new biconnectedReduction<Integer>(nothing);
		nothing.addReduction(rC);
		

		
//		Reduction<Integer> rC = new biconnectedReduction<Integer>(branchC);
//		branchC.addReduction(rC);
////		
//		rC = new centralNodeReduction<Integer>(branchC);
//		branchC.addReduction(rC);
		
		
//		rC = new c4p4Reduction<Integer>(branchC2);
//		branchC2.addReduction(rC);
//		rC = new biconnectedReduction<Integer>(branchC2);
//		branchC2.addReduction(rC);
//		rC = new centralNodeReduction<Integer>(branchC2);
//		branchC2.addReduction(rC);
//		
//		
//		rC = new edgeBoundReduction<Integer>(all);
//		all.addReduction(rC);
//		rC = new commonC4Reduction<Integer>(all);
//		all.addReduction(rC);
		
		
//		rC = new c4p4Reduction<Integer>(all);
//		all.addReduction(rC);
		
		rC = new c4p4Reduction<Integer>(all2);
		all2.addReduction(rC);
//		rC = new biconnectedReduction<Integer>(all2);
//		all2.addReduction(rC);
		
//		Reduction<Integer> rNo = new edgeBoundReduction<Integer>(branchNoHP);
//		branchNoHP.addReduction(rNo);
//		Reduction<Integer> r2No = new commonC4Reduction<Integer>(branchNoHP);
//		branchNoHP.addReduction(r2No);
		
		
//		
		
		YanSearch<Integer> yan = new YanSearch<Integer>();
		
		long start = System.currentTimeMillis();
		System.out.println(yan.search(exampleQT));
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		
		
//		rC = new edgeBoundReduction<Integer>(simple);
//		simple.addReduction(rC);
//		r2C = new commonC4Reduction<Integer>(simple);
//		simple.addReduction(r2C);
//		
//		rC = new edgeBoundReduction<Integer>(random);
//		random.addReduction(rC);
//		r2C = new commonC4Reduction<Integer>(random);
//		random.addReduction(r2C);
		
		
//		
		
//		c.setbStruct(simple);
//		System.out.println("\nSimple: ");
//		start = System.currentTimeMillis();
//		c.branchStart(exampleQT, 21);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
//		
//		c.setbStruct(random);
//		System.out.println("\nRandom: ");
//		start = System.currentTimeMillis();
//		
//		int hit = 0;
//		LinkedList<myEdge<Integer>> min = null;
//		
//		while (hit < 1)
//		{
//			branchingReturnC<Integer> rtn = c.branchStart(exampleQT, 30);
//			if (yan.search(rtn.getG()))
//			{
//				if (min == null)
//					min = rtn.getMinMoves().getChanges();
//				
//				else if (min.size() > rtn.getMinMoves().getChanges().size())
//					min = rtn.getMinMoves().getChanges();
//				
//				exampleQT = rtn.getG();
//					
//				hit++;
//			}
//		}
//		
//		System.out.println("Number of min moves: " + min.size());
//		System.out.println("Min moves: " + min);
//		
//		
//		
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
		
//		
//		
		branchComponents<Integer> comp = new branchComponents<Integer>(c, all2);
		
//		c.setbStruct(comp);
//		System.out.println("\nConnected component (new components): ");
//		start = System.currentTimeMillis();
//		System.out.println(yan.search(c.branchStart(exampleQT, 15).getG()));
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
//		
//		
//		
//		c.setbStruct(branchC);
//		System.out.println("\nConnected component: ");
//		start = System.currentTimeMillis();
//		System.out.println(yan.search(c.branchStart(exampleQT, 15).getG()));
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//////	
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
////		
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
//		
//		
//		c.setbStruct(all);
//		System.out.println("\nAll structures (old reductions): ");
//		start = System.currentTimeMillis();
//		c.branchStart(exampleQT, 14);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
////		
//		c.setbStruct(all2);
//		System.out.println("\nAll structures (new reductions): ");
//		start = System.currentTimeMillis();
//		System.out.println(yan.search(c.branchStart(exampleQT, 12).getG()));
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
		
//		Graph<Integer, Pair<Integer>> cln9 = clone.deepClone(exampleQT);
//		visualize(cln9);
		
		
//		
		
//		c.setbStruct(nothing);
//		System.out.println("\nNo Heuristic: ");
//		start = System.currentTimeMillis();
//		c.branchStart(exampleQT, 15);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
		
//		
//		Graph<Integer, Pair<Integer>> cln3 = clone.deepClone(exampleQT);
//		visualize(cln3);
//		
//
//		/
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
//		
//		
//		c.setbStruct(nothing);
//	
//		for (int i = 5; i <= 15; i++)
//		{
//			System.out.println("Bound: " + i);
//			start = System.currentTimeMillis();
//			System.out.println(yan.search(c.branchStart(exampleQT, i).getG()));
//			System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		}
		
		
		Dive<Integer> dive = new maxObsGreedy<Integer>(branchC);
		
		
//		c.setbStruct(branchC);
//		dive.setbStruct(branchC);
//		c.setDive(dive);
//		c.setUseGreedy(true);
//		System.out.println("\nConnected component with greedy diving: ");
//		start = System.currentTimeMillis();
//		System.out.println(yan.search(c.branchStart(exampleQT, 10).getG()));
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
		
		
		c.setbStruct(branchC);
		branchC.setDive(dive);
		System.out.println("\nGreedy Edit: ");
		start = System.currentTimeMillis();
		System.out.println(yan.search(c.diveAtStartEdit(exampleQT, 50).getG()));
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
//		
		

		
		start = System.currentTimeMillis();
		System.out.println(yan.search(exampleQT));
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		
		//visualize(exampleQT);
	}

	
	public static void visualize(Graph<Integer, Pair<Integer>> exampleQT){
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
	
	public static void visualizeString(Graph<String, Pair<String>> exampleQT){
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
	


	/**
	 * fill graph with given file
	 * 
	 * @param Graph graph
	 * @param String filename
	 * @return 
	 */
	private static Graph<Integer, Pair<Integer>> fillGraphFromFile(
			String filename) 
	{
		
		Graph<Integer, Pair<Integer>> graph = new UndirectedSparseGraph<Integer, Pair<Integer>>();
		FileReader file = null;
		try {
			file = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}

		Scanner scan = new Scanner(file);

		while (scan.hasNext()) {
			Integer a = scan.nextInt();
			Integer b = scan.nextInt();
			//Integer weight = scan.nextInt();

			// for (int i = 0; i < weight; i++)
			// {
			graph.addEdge(new Pair<Integer>(a, b), a, b);
			// }
		}
		try {
			scan.close();
			file.close();
		} catch (IOException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}
		
		return graph;
	}
	
	private static Graph<String, Pair<String>> fillGraphFromFileWithStrings(
			String filename) 
	{
		
		Graph<String, Pair<String>> graph = new UndirectedSparseGraph<String, Pair<String>>();
		FileReader file = null;
		try {
			file = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}

		Scanner scan = new Scanner(file);

		while (scan.hasNext()) {
			
			
			String a = scan.next();
			
			
			//for .tgf edge sets
			if (a.equals("#"))
			{
				continue;
			}
			
			String b = scan.next();
			
			
			graph.addEdge(new Pair<String>(a, b), a, b);
			
		}
		try {
			scan.close();
			file.close();
		} catch (IOException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}
		
		return graph;
	}
	
	private static DirectedGraph<String, Pair<String>> fillDiGraphFromFileWithStrings(
			String filename) 
	{
		
		DirectedSparseGraph<String, Pair<String>> graph = new DirectedSparseGraph<String, Pair<String>>();
		FileReader file = null;
		try {
			file = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}

		Scanner scan = new Scanner(file);

		while (scan.hasNext()) {
			
			
			String a = scan.next();
			
			//for .tgf edge sets
			if (a.equals("#"))
			{
				continue;
			}
			
			String b = scan.next();
			
			
			graph.addEdge(new Pair<String>(a, b), a, b);
			
		}
		try {
			scan.close();
			file.close();
		} catch (IOException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}
		
		return graph;
	}
	
	
	private static void comparisonTest()
	{
		//run the same graph as a test over multiple traversal methods
		qtLBFSNoHeuristic<Integer> search = new qtLBFSNoHeuristic<Integer>();
		qtGenerate<Integer> gen = new qtGenerate<Integer>();
		Cloner clone = new Cloner();
		
		//load all test branching methods
		Controller<Integer> c = new Controller<Integer>(null, false);
		qtAllStruct<Integer> all = new qtAllStruct<Integer>(c);
		qtAllStruct<Integer> all2 = new qtAllStruct<Integer>(c);
		qtBranchComponents<Integer> branchC = new qtBranchComponents<Integer>(c);
		
		Reduction<Integer> rC = new edgeBoundReduction<Integer>(all);
		all.addReduction(rC);
		rC = new commonC4Reduction<Integer>(all);
		all.addReduction(rC);
		
		rC = new c4p4Reduction<Integer>(all2);
		all2.addReduction(rC);
		rC = new biconnectedReduction<Integer>(all2);
		all2.addReduction(rC);
		
		rC = new c4p4Reduction<Integer>(branchC);
		branchC.addReduction(rC);
		rC = new biconnectedReduction<Integer>(branchC);
		branchC.addReduction(rC);
		
		
		//store branching types
		LinkedList<Branch<Integer>> b = new LinkedList<Branch<Integer>>();
		HashSet<Integer> moves;
		HashSet<Boolean> success;
		
		Graph<Integer, Pair<Integer>> graph;
		
		b.add(all);
		b.add(all2);
		b.add(branchC);
		
		int size = 2;
		
		outer:
		while (size < 66)
		{
			int seed = 0;
			seedloop:
			while (seed < 20)
			{
				//create graph
				graph = gen.treeRandom(size, seed);
				Graph<Integer, Pair<Integer>> og = clone.deepClone(graph);
				int bound = 0;
				
				boundloop:
				while (bound < 17)
				{
					moves = new HashSet<Integer>();
					success = new HashSet<Boolean>();
					for (Branch<Integer> temp : b)
					{
						c.setbStruct(temp);
						branchingReturnC<Integer> ans = c.branchStart(graph, bound);
						boolean s = search.isQT(ans.getG());
						
						if (!gen.graphEquals(og, graph))
						{
							System.out.println("Graph modified at size " + size + ", seed " + seed + ", bound " + bound);
							break outer;
						}
						
						
						if (!moves.isEmpty() && moves.add(ans.getMinMoves().getChanges().size()))
						{
							System.out.println("Different solutions at size " + size + ", seed " + seed + ", bound " + bound);
							break outer;
						}
						else if (moves.isEmpty())
							moves.add(ans.getMinMoves().getChanges().size());
						
						if (!success.isEmpty() && success.add(search.isQT(ans.getG())))
						{
							System.out.println("Different success in solving at size " + size + ", seed " + seed + ", bound " + bound);
							break outer;
						}
						else if (success.isEmpty())
							success.add(search.isQT(ans.getG()));
						
					}
					if (success.iterator().next())
					{
						break boundloop;
					}

					bound++;
				}
				seed++;
			}
			size++;
		}
	}
	
	private static void wineTest() throws FileNotFoundException, UnsupportedEncodingException
	{
		
		Graph<String, Pair<String>> wine = fillGraphFromFileWithStrings("datasets/wine/ON/wineryEdgeSet.txt");
		
//		int k = 9;
//		wine = genString.fromBipartiteFile("datasets/edgeSet.txt", k);
		
		
		Graph<String, Pair<String>> cln = clone.deepClone(wine);
		
		Controller<String> c = new Controller<String>(null, true);
		
		qtGenerate<String> gen = new qtGenerate<String>();
		
		
		//test size of graph
		System.out.println("Graph has " + wine.getVertexCount() + " nodes and " + wine.getEdgeCount() + " edges.");
		
		qtBranchComponents<String> branchC = new qtBranchComponents<String>(c);
		
		
		Reduction<String> rC = new c4p4Reduction<String>(branchC);
		branchC.addReduction(rC);
		
//		rC = new biconnectedReduction<String>(branchC);
//		branchC.addReduction(rC);
		
		visualizeString(wine);
		long start;
		
		Dive<String> dive = new maxObsGreedy<String>(branchC);
		branchingReturnC<String> rtn;
		
		//greedy edit
		c.setbStruct(branchC);
		branchC.setDive(dive);
		System.out.println("\nGreedy Edit: ");
		start = System.currentTimeMillis();
		rtn = c.diveAtStartEdit(wine, 4);
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
//		//regular edit
//		c.setbStruct(branchC);
//		System.out.println("\nConnected component: ");
//		start = System.currentTimeMillis();
//		rtn = c.branchStart(wine, 15);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
////		
		System.out.println("\nGraph same? " + gen.graphEquals(cln, wine));
		
		
		if (branchC.getSearch().isTarget(rtn.getG()))
		{
			
			System.out.println("Solution has " + rtn.getG().getVertexCount() + " nodes and " + rtn.getG().getEdgeCount() + " edges.");
			
			
			stringUtils.printSolutionEdgeSetWithWeightsComponents(rtn, "datasets/wine/weightedResults/ON/QT Solution-GREEDY-EDGE SET.txt");
			
			
//			//print network to file
//			PrintWriter writer = new PrintWriter("datasets/wine/BC-k"+ k + "-SolutionEdgeSetGREEDY.tgf", "UTF-8");
//			
//			writer.println("#");
//			for (Pair<String> edge : rtn.getG().getEdges())
//			{
//				writer.println(edge.getFirst() + " " + edge.getSecond());
//			}
//			
//			writer.close();
		}
		
	}
	
	
	public static void diGraphWineryTest() throws FileNotFoundException, UnsupportedEncodingException
	{
		DirectedGraph<String, Pair<String>> g = fillDiGraphFromFileWithStrings("datasets/wine/BC/wineryEdgeSet.txt");
		
		//reverse edges and add to new graph
//		DirectedGraph<String, Pair<String>> reversed = new DirectedSparseGraph<String, Pair<String>>();
//		for (String v : g.getVertices())
//		{
//			reversed.addVertex(v);
//		}
//		for (Pair<String> edge : g.getEdges())
//		{
//			reversed.addEdge(new Pair<String>(edge.getSecond(), edge.getFirst()), edge.getSecond(), edge.getFirst());
//		}
//		g = reversed;
////		
		Graph<String, Pair<String>> cGraph = clone.deepClone(g);
		
		
		diQTSearch<String> search = new diQTSearch<String>();
		
		//visualize(g);
		
		System.out.println(search.search(g));
		
		Controller<String> c = new Controller<String>(null, true);
		diQTBranch<String> bStruct = new diQTBranch<String>(c);
		
		branchComponents<String> b = new branchComponents<String>(c, bStruct);
		
		c.setbStruct(b);
		
		b.setDive(new diQTGreedy<String>(b));
		
		visualizeString(g);
		
		
		
//		System.out.println("\nGreedy Edit: ");
//		long start = System.currentTimeMillis();
//		branchingReturnC<String> rtn = c.diveAtStartEdit(g, 50);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
////		
		
//		System.out.println("\nConnected component: ");
//		long start = System.currentTimeMillis();
//		//branchingReturnC<String> rtn = c.branchStart(g, 20);
//		branchingReturnC<String> rtn = c.branchID(g, 2, 22);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		branchingReturnC<String> rtn = new branchingReturnC<String>(g);
		
		System.out.println(search.isTarget(rtn.getG()));
		
		visualizeString(rtn.getG());
		
		System.out.println("\nGraph same? " + genString.graphEquals(cGraph, g));
		
		String filename = "datasets/wine/BC/weightedResults/BCunedited";
		
		stringUtils.printSolutionEdgeSetWithWeightsComponents(rtn, filename + ".txt");
		
//		//print network to file
//		PrintWriter writer = new PrintWriter(filename +".tgf", "UTF-8");
//		
//		
//		writer.println("#");
//		for (Pair<String> edge : rtn.getG().getEdges())
//		{
//			writer.println(edge.getFirst() + " " + edge.getSecond());
//		}
//		
//		writer.close();
		
	}
	
	public static void clusterTest()
	{
		clusterSearch<Integer> search = new clusterSearch<Integer>();
		
		Graph<Integer, Pair<Integer>> exampleQT;
		qtGenerate<Integer> gen = new qtGenerate<Integer>();
		exampleQT = gen.ER(20, .8, (long) 3);
		//exampleQT = gen.randomTreeGraph(20, 0, 10);
		exampleQT = gen.treeRandom(25, 6);
		
		
		
		visualize(exampleQT);
		
		System.out.println(search.search(exampleQT));
		
		Controller<Integer> c = new Controller<Integer>(null, true);
		clusterBranch<Integer> bStruct = new clusterBranch<Integer>(c);
		clusterAllStruct<Integer> all = new clusterAllStruct<Integer>(c);
		
		bStruct.addReduction(new clusterReductionBasic<Integer>(bStruct));
//		
		all.addReduction(new clusterReductionBasic<Integer>(all));
		
		branchComponents<Integer> b = new branchComponents<Integer>(c, bStruct);

		branchComponents<Integer> bAll = new branchComponents<Integer>(c, all);
		
		branchingReturnC<Integer> rtn;
		long start;
		
		
		c.setbStruct(b);
		
		System.out.println("\nConnected component (P3 rules): ");
		start = System.currentTimeMillis();
		rtn = c.branchStart(exampleQT, 20);
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		System.out.println(search.isTarget(rtn.getG()));
//		
		
		c.setbStruct(bAll);
		System.out.println("\nConnected component (all rules): ");
		start = System.currentTimeMillis();
		rtn = c.branchStart(exampleQT, 20);
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		System.out.println(search.isTarget(rtn.getG()));
		
		
		visualize(rtn.getG());
	}
	
	public static void scoreWineryGraph() throws FileNotFoundException, UnsupportedEncodingException
	{
		
		String file = "datasets/wine/BCWineDiSolutionEdgeSetREVERSED-GREEDY.tgf";
		DirectedGraph<String, Pair<String>> diGraph =  fillDiGraphFromFileWithStrings(file);
		UndirectedGraph<String, Pair<String>> graph = (UndirectedGraph<String, Pair<String>>) fillGraphFromFileWithStrings(file);
		
//		DirectedGraph<String, Pair<String>> diGraph = readGeneratedDiTGF(file);
//		UndirectedGraph<String, Pair<String>> graph = (UndirectedGraph<String, Pair<String>>) readGeneratedTGF(file);
		
		
		//reverse edges and add to new graph
		DirectedGraph<String, Pair<String>> reversed = new DirectedSparseGraph<String, Pair<String>>();
		for (String v : diGraph.getVertices())
		{
			reversed.addVertex(v);
		}
		for (Pair<String> edge : diGraph.getEdges())
		{
			reversed.addEdge(new Pair<String>(edge.getSecond(), edge.getFirst()), edge.getSecond(), edge.getFirst());
		}
		diGraph = reversed;
		
		
		
		//print network to file
		PrintWriter writer = new PrintWriter("datasets/wine/scoring/Reversed BC Winery-Winery.txt", "UTF-8");
		BetweennessCentrality<String, Pair<String>> betweenness = new BetweennessCentrality<String, Pair<String>>(graph);
		ClosenessCentrality<String, Pair<String>> closeness = new ClosenessCentrality<String, Pair<String>>(graph);
		EigenvectorCentrality<String, Pair<String>> eigen = new EigenvectorCentrality<String, Pair<String>>(diGraph);
		HITS<String, Pair<String>> hits = new HITS<String, Pair<String>>(diGraph);
		familialGroupCentrality<String>	familial = new familialGroupCentrality<String>(graph);
		PageRank<String, Pair<String>> pageRank = new PageRank<String, Pair<String>>(diGraph, 0.15);
		
		
		eigen.acceptDisconnectedGraph(true);
		
		pageRank.setMaxIterations(10000);
		pageRank.initialize();
		pageRank.evaluate();
		
		hits.setMaxIterations(10000);
		hits.initialize();
		hits.evaluate();
		

		eigen.setMaxIterations(10000);
		eigen.initialize();
		eigen.evaluate();
	
		
		System.out.println("is disconnected graph ok? \nhits: " + eigen.isDisconnectedGraphOK());
		System.out.println("hits iterations: " + hits.getIterations());
		System.out.println("eigen iterations: " + eigen.getIterations());
		System.out.println("pageRank iterations: " + pageRank.getIterations());
		
		
		writer.println("Vertex\tDegree\tIn-Degree\tOut-Degree\tBetweenness Centrality\tCloseness Centrality\tEigenvector Centrality\tHITS\tFamilial Group Centrality\tPage Rank");
		
		
		
		for (String v : graph.getVertices())
		{
			writer.println(v + "\t" +diGraph.degree(v) + "\t" +diGraph.inDegree(v) + "\t" + diGraph.outDegree(v) + "\t"+ betweenness.getVertexScore(v) + "\t" + closeness.getVertexScore(v) + "\t" + eigen.getVertexScore(v) + "\t" + hits.getVertexScore(v) + "\t" + familial.getVertexScore(v) + "\t" + pageRank.getVertexScore(v));
			
		}
		
		
		writer.close();
		
		
	}
	
	
	static Graph<String, Pair<String>> readGeneratedTGF(String filename)
	{
		Graph<String, Pair<String>> graph = new UndirectedSparseGraph<String, Pair<String>>();
		
		String[] vert = new String[500];
		
		FileReader file = null;
		try {
			file = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}

		Scanner scan = new Scanner(file);

		boolean past = false;
		
		while (scan.hasNext()) {
			
			String next = scan.next();
			
			if (next.equals("#"))
			{
				past = true;
				continue;
			}
			
			if (past)
			{
				String next2 = scan.next();
				
				int first = Integer.parseInt(next);
				int second = Integer.parseInt(next2);
				
				graph.addEdge(new Pair<String>(vert[first], vert[second]), vert[first], vert[second]);
				
			}
			else
			{
				String next2 = scan.next();
				
				vert[Integer.parseInt(next)]= next2;
			}
			
		}
		scan.close();
		
		return graph;
		
		
	}
	
	static DirectedGraph<String, Pair<String>> readGeneratedDiTGF(String filename)
	{
		DirectedGraph<String, Pair<String>> graph = new DirectedSparseGraph<String, Pair<String>>();
		
		String[] vert = new String[500];
		
		FileReader file = null;
		try {
			file = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}

		Scanner scan = new Scanner(file);

		boolean past = false;
		
		while (scan.hasNext()) {
			
			String next = scan.next();
			
			if (next.equals("#"))
			{
				past = true;
				continue;
			}
			
			if (past)
			{
				String next2 = scan.next();
				
				int first = Integer.parseInt(next);
				int second = Integer.parseInt(next2);
				
				graph.addEdge(new Pair<String>(vert[first], vert[second]), vert[first], vert[second]);
				
			}
			else
			{
				String next2 = scan.next();
				
				vert[Integer.parseInt(next)]= next2;
			}
			
		}
		scan.close();
		
		return graph;
		
		
	}
	
	private static void distanceTest()
	{
		distance<String> d = new distance<String>();
		graphFromEdgeSetWithCommunities f = new graphFromEdgeSetWithCommunities("datasets/wine/QC/projections/province/k10PROVINCEedgeSet.txt");
		
		HashMap<String, Pair<Double>> mapping = d.getLatLongFromFile("datasets/wine/Distance/QC/QCDistance.txt");
		
		
		Graph<String, Pair<String>> g = f.g;
		
		System.out.println("Overall: ");
		System.out.println("Mean winery-winery distance: \t" + d.meanDistance(g, mapping));
		System.out.println("Median winery-winery distance: \t" + d.medianDistance(g, mapping));
		System.out.println("Mean edge distance: \t" + d.meanNeighbourDistance(g, mapping));
		System.out.println("Median edge distance: \t" + d.medianNeighbourDistance(g, mapping));
		
		
		
		HashMap<Integer, Graph<String, Pair<String>>> components = new HashMap<Integer, Graph<String, Pair<String>>>();
		
		for (int cID : f.communityMap.keySet())
		{
			components.put(cID, stringUtils.inducedFromVertexSet(g, f.communityMap.get(cID)));
		}
	
		
		for (int cID : components.keySet())
		{
			
			
			System.out.println("\n\nComponent " + cID + "\tNumber of nodes: " + components.get(cID).getVertexCount());
			
			//output wineries in this community
			for (String winery : components.get(cID).getVertices())
			{
				System.out.print(winery + "\t");
			}
			
			System.out.println("\nMean winery-winery distance: \t" + d.meanDistance(components.get(cID), mapping));
			System.out.println("Median winery-winery distance: \t" + d.medianDistance(components.get(cID), mapping));
			System.out.println("Mean edge distance: \t" + d.meanNeighbourDistance(components.get(cID), mapping));
			System.out.println("Median edge distance: \t" + d.medianNeighbourDistance(components.get(cID), mapping));
			
		}
		
	}
	
	private static void cographSearchTest()
	{
		
		Graph<Integer, Pair<Integer>> g = new UndirectedSparseGraph<Integer, Pair<Integer>>(); // = gen.houseStruct();
		
		g.addEdge(new Pair<Integer>(0, 1), 0, 1);
		g.addEdge(new Pair<Integer>(2, 1), 2, 1);
		//g.addEdge(new Pair<Integer>(2, 3), 2, 3);
		g.addEdge(new Pair<Integer>(0, 3), 0, 3);
		
		g = gen.ER(7, 0.67, (long) 0);
		
		

		
		//visualize(g);
		
		cographSearch<Integer> search = new cographSearch<Integer>();
		
		int count = 0;
		int brokenCount = 0;
		
		
		//run tests up to 100 vertices
		for (int k = 3; k < 100; k++)
		{
			for (long l = 0; l < 10; l++)
			{
				
				count++;
				
				g = gen.ER(k, 0.95, l);
				
				SearchResult<Integer> result = search.search(g);
				LinkedList<Integer> obs = new LinkedList<Integer>();
				boolean bruteFlag = true;
				outer:
				for (Integer v0 : g.getVertices())
				{
					for (Integer v1 : g.getNeighbors(v0))
					{
						for (Integer v2 : g.getNeighbors(v1))
						{
							if (v2.equals(v0) || g.findEdge(v0, v2) != null)
								continue;
							
							for (Integer v3 : g.getNeighbors(v2))
							{
								if (v3.equals(v1) || v3.equals(v0) || g.findEdge(v0, v3) != null || g.findEdge(v1, v3) != null)
									continue;
								
								
								if (obs.isEmpty())
								{
									obs.add(v0);
									obs.add(v1);
									obs.add(v2);
									obs.add(v3);
								}
								bruteFlag = false;
								break outer;
								
							}
						}
					}
				}
				
				if (result.isTarget() != bruteFlag)
				{
					System.out.println("Test failed at k=" + k + ", l = "+ l );
					System.out.println("Brute force got obstruction: " + obs);
					visualize(g);
					
					//run test again when debugging
					search.search(g);
					
					brokenCount++;
					throw new NullPointerException();
					
				}
				System.out.println(count);
				if (result.isTarget())
					System.out.println("Is cograph: " + result.isTarget());
				
			}
		}
		
		
		System.out.println("Out of " + count + ", " + brokenCount + " were broken.");
		
		SearchResult<Integer> result = search.search(g);
		
		
		
		
		if (result.isTarget())
			System.out.println("Is Cograph");
		else
			System.out.println("Certificate " + result.getCertificate());
		
		//System.out.println("Is cograph: " + search.isTarget(g));
	}
	
	
	public static void cographTest()
	{
		cographSearch<Integer> search = new cographSearch<Integer>();
		
		Graph<Integer, Pair<Integer>> exampleQT;
		qtGenerate<Integer> gen = new qtGenerate<Integer>();
		exampleQT = gen.randomTreeGraph(50, 7, 3);
		
		//exampleQT = gen.treeRandom(50, 2);
		
//		exampleQT = new UndirectedSparseGraph<Integer, Pair<Integer>>();
//		
//		exampleQT.addEdge(new Pair<Integer>(0, 1), 0, 1);
//		exampleQT.addEdge(new Pair<Integer>(1, 2), 1, 2);
//		exampleQT.addEdge(new Pair<Integer>(2, 3), 2, 3);
//		exampleQT.addEdge(new Pair<Integer>(3, 4), 3, 4);
//		exampleQT.addEdge(new Pair<Integer>(4, 2), 4, 2);
		//exampleQT.addEdge(new Pair<Integer>(4, 1), 4, 1);
		
		
		
		visualize(exampleQT);
		
		System.out.println(search.search(exampleQT));
		
		Controller<Integer> c = new Controller<Integer>(null, true);
		cographBranch<Integer> bStruct = new cographBranch<Integer>(c);
		cographAllStruct<Integer> all = new cographAllStruct<Integer>(c);
		
		
		branchComponents<Integer> bStructComp = new branchComponents<Integer>(c, bStruct);
		branchComponents<Integer> allComp = new branchComponents<Integer>(c, all);
		
		
		
		
		c.setbStruct(allComp);
		
		System.out.println("\nAll structures cograph: ");
		long start = System.currentTimeMillis();
		branchingReturnC<Integer> rtn = c.branchStart(exampleQT, 7);
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		System.out.println(search.isTarget(rtn.getG()));
		
		c.setbStruct(bStructComp);
		
		System.out.println("\nRegular cograph: ");
		start = System.currentTimeMillis();
		rtn = c.branchStart(exampleQT, 7);
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		System.out.println(search.isTarget(rtn.getG()));
		
//		c.setbStruct(b);
//		
//		System.out.println("\nConnected component: ");
//		start = System.currentTimeMillis();
//		rtn = c.branchStart(exampleQT, 10);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
//		System.out.println(search.isTarget(rtn.getG()));
		
		visualize(rtn.getG());
	}
	
	
	public static void wineryProjectionTest() throws FileNotFoundException, UnsupportedEncodingException
	{
		String province = "QC";
		
		Graph<String, Pair<String>> wine = null;
		
		
		
//		int k = 9;
		
		for (int k = 2; k < 11; k++)
		{
			wine = genString.fromBipartiteFile("datasets/wine/" + province + "/ProvinceSpecificEdgeList.txt", k);
			
			
			Graph<String, Pair<String>> cln = clone.deepClone(wine);
			
			Controller<String> c = new Controller<String>(null, true);
			
			qtGenerate<String> gen = new qtGenerate<String>();
			
			
			//test size of graph
			System.out.println("Graph has " + wine.getVertexCount() + " nodes and " + wine.getEdgeCount() + " edges.");
			
			qtBranchComponents<String> branchC = new qtBranchComponents<String>(c);
			
			
			Reduction<String> rC = new c4p4Reduction<String>(branchC);
			branchC.addReduction(rC);
			
	//		rC = new biconnectedReduction<String>(branchC);
	//		branchC.addReduction(rC);
			
			visualizeString(wine);
			long start;
			
			Dive<String> dive = new maxObsGreedy<String>(branchC);
			branchingReturnC<String> rtn;
			
//			//greedy edit
//			c.setbStruct(branchC);
//			branchC.setDive(dive);
//			System.out.println("\nGreedy Edit: ");
//			start = System.currentTimeMillis();
//			rtn = c.diveAtStartEdit(wine, 4);
//			System.out.println((System.currentTimeMillis()-start) / 1000.0);
			
	//		//regular edit
	//		c.setbStruct(branchC);
	//		System.out.println("\nConnected component: ");
	//		start = System.currentTimeMillis();
	//		rtn = c.branchStart(wine, 15);
	//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
	////		
			System.out.println("\nGraph same? " + gen.graphEquals(cln, wine));
			
			stringUtils.printSolutionEdgeSetWithWeightsComponents(new branchingReturnC<String>(cln), "datasets/wine/" + province + "/projections/province/k" + k + "PROVINCEedgeSet.txt");
			
			
//			if (branchC.getSearch().isTarget(rtn.getG()))
//			{
//				
//				System.out.println("Solution has " + rtn.getG().getVertexCount() + " nodes and " + rtn.getG().getEdgeCount() + " edges.");
//				
//				
//				stringUtils.printSolutionEdgeSetWithWeightsComponents(rtn, "datasets/wine/" + province +"/weightedResults/"+ province + "-k"+k+"-Solution-GREEDY-EDGE SET.txt");
//				
//				
//			}
		}
		
	}
	
	private static void getProvinceSpecificExternalsEdgeList() {
		
		String province = "ON";
		distance.provinceSpecificExternalsEdgeList("datasets/wine/"+province+ "/edgeSet.txt", "datasets/wine/Distance/"+province+"/"+province +"ExternalAddress.txt", province, "datasets/wine/"+province+"/ProvinceSpecificEdgeList.txt");
		
	}
	
	private static void projectionAnalysis()
	{
		//String province = "BC";
		LinkedList<String> provinces = new LinkedList<String>();
		provinces.add("ON");
		provinces.add("QC");
		
		for (String province : provinces)
		{
			String distanceFile = "datasets/wine/Distance/"+province +"/"+province+"Distance.txt";
			
			
			Graph<String, Pair<String>> wine = null;
			
			
			Controller<String> c = new Controller<String>(null, true);
			
			qtGenerate<String> gen = new qtGenerate<String>();
			
			qtBranchComponents<String> branchC = new qtBranchComponents<String>(c);
			Dive<String> dive = new maxObsGreedy<String>(branchC);
			
			Reduction<String> rC = new c4p4Reduction<String>(branchC);
			branchC.addReduction(rC);
			
			branchingReturnC<String> rtn;
			
			for (int k = 1; k < 11; k++)
			{
			
				try {
					wine = genString.fromBipartiteFile("datasets/wine/" + province + "/ProvinceSpecificEdgeList.txt", k);
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				//dump original graph to file
				String originalFile = "datasets/wine/"+province+"/k"+k+"-unedited.txt";
				stringUtils.printSolutionEdgeSetWithWeightsComponents(new branchingReturnC<String>(wine), originalFile);
				
				distance.outputDistanceMeasurements(originalFile, distanceFile, "datasets/wine/"+province+"/k"+k+"-uneditedDISTANCES.txt");
				
				//edit graph
				//greedy edit
				c.setbStruct(branchC);
				branchC.setDive(dive);
				System.out.println("\nGreedy Edit: ");
				rtn = c.diveAtStartEdit(wine, 4);
				
				//dump edited graph to file
				String editedFile = "datasets/wine/"+province+"/k"+k+"-edited.txt";
				stringUtils.printSolutionEdgeSetWithWeightsComponents(rtn, editedFile);
				
				//output distance measurements for each
				distance.outputDistanceMeasurements(editedFile, distanceFile, "datasets/wine/"+province+"/k"+k+"-editedDISTANCES.txt");
				
			}
		}
	}
	
	private static void projectionClusterAnalysis()
	{
		//String province = "BC";
		LinkedList<String> provinces = new LinkedList<String>();
		provinces.add("BC");
		provinces.add("ON");
		provinces.add("QC");
		
		for (String province : provinces)
		{
			String distanceFile = "datasets/wine/Distance/"+province +"/"+province+"Distance.txt";
			
			
			Graph<String, Pair<String>> wine = null;
			
			
			Controller<String> c = new Controller<String>(null, true);
			
			clusterBranch<String> b = new clusterBranch<String>(c);
			//add basic reduction
			b.addReduction(new clusterReductionBasic<String>(b));
			
			branchComponents<String> bStruct = new branchComponents<String>(c, b);
			GreedyEdit<String> greedy = new clusterGreedy<String>(bStruct);
			
			branchingReturnC<String> rtn;
			
			for (int k = 1; k < 11; k++)
			{
			
				try {
					wine = genString.fromBipartiteFile("datasets/wine/" + province + "/ProvinceSpecificEdgeList.txt", k);
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				//edit graph
				//greedy edit
				c.setbStruct(bStruct);
				//bStruct.setDive(greedy);
				
				System.out.println("\nExact Edit: " + province + " k"+k );
				rtn = c.branchStart(wine, 40);
				
				//dump edited graph to file
				if (b.getSearch().search(rtn).isTarget())
				{
					String editedFile = "datasets/wine/"+province+"/clusterEdit/k"+k+"-clusterEdit.txt";
					stringUtils.printSolutionEdgeSetWithWeightsComponents(rtn, editedFile);
					
					//output distance measurements for each
					distance.outputDistanceMeasurements(editedFile, distanceFile, "datasets/wine/"+province+"/clusterEdit/k"+k+"-clusterEditDISTANCES.txt");
				}
			}
		}
	}
	
	public static void winerykExternalProjections()
	{
		//String province = "BC";
		LinkedList<String> provinces = new LinkedList<String>();
		provinces.add("BC");
		provinces.add("ON");
		provinces.add("QC");
		
		distance<String> d = new distance<String>();
		PriorityQueue<Pair<Set<String>>> pq;
		
		
		for (String province : provinces)
		{
			String distanceFile = "datasets/wine/Distance/"+province +"/"+province+"Distance.txt";
			HashMap<String, Pair<Double>> mapping = distance.getLatLongFromFile(distanceFile);
			
			
			for (int k = 1; k < 6; k++)
			{
				pq = graphUtils.wineriesWithKExternals("datasets/wine/"+province+"/ProvinceSpecificEdgeList.txt", k);
				
				PrintWriter writer = null;
				try {
					writer = new PrintWriter("datasets/wine/externalProjections/"+province+"/externalk"+k+"ProjectionDISTANCES.txt", "UTF-8");
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//write the total number of combinations
				writer.println("Total number of external sets with MAX winereies: " + pq.size());
				
				
				for (int i = 0; i < 10; i++)
				{
					if (pq.isEmpty())
						break;
					
					
					Pair<Set<String>> next = pq.remove();
					
					
					//output distances
					writer.println("Externals: (" + next.getFirst().size() + ")");
					writer.println(next.getFirst());
//					writer.println("Mean external distance: \t" + d.meanDistance(next.getFirst(), mapping));
//					writer.println("Median external distance: \t" + d.medianDistance(next.getFirst(), mapping));
					writer.println("Wineries: (" + next.getSecond().size() + ")");
					writer.println(next.getSecond());
					writer.println("Mean winery distance: \t" + d.meanDistance(next.getSecond(), mapping));
					writer.println("Median winery distance: \t" + d.medianDistance(next.getSecond(), mapping));
					
					writer.println();
					
				}
				
				writer.close();
				
				pq = null;
				
			}
		}
		
		
		
	}
	
	
	public static void externalProjectionsClique()
	{
		//String province = "BC";
		LinkedList<String> provinces = new LinkedList<String>();
		distance<String> d = new distance<String>();
		
		
		provinces.add("BC");
		provinces.add("ON");
		provinces.add("QC");
		
		for (String province : provinces)
		{
			//make a general graph for lookup
			Graph<String, Pair<String>> overall = fillGraphFromFileWithStrings("datasets/wine/" + province + "/ProvinceSpecificEdgeList.txt");
			
			
			String distanceFile = "datasets/wine/Distance/"+province +"/"+province+"Distance.txt";
			HashMap<String, Pair<Double>> mapping = distance.getLatLongFromFile(distanceFile);
			
			UndirectedGraph<String, Pair<String>> wine = null;
			
			
			for (int k = 1; k < 23; k++)
			{
			
				try {
					wine = genString.fromBipartiteFile("datasets/wine/" + province + "/ProvinceSpecificEdgeList.txt", k);
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				//get maximal clique
				Collection<Set<String>> cliques = stringUtils.maximalClique(wine);
				
				
				PrintWriter writer = null;
				try {
					writer = new PrintWriter("datasets/wine/externalProjections/"+province+"/clique/externalk"+k+"ProjectionDISTANCES.txt", "UTF-8");
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				//order sets in decreasing size
				LinkedList<Set<String>> orderedSets = new LinkedList<Set<String>>();
				
				for (Set<String> set : cliques)
				{
					if (orderedSets.isEmpty())
						orderedSets.add(set);
					else
					{
						for (int i = 0; i < orderedSets.size(); i++)
						{
							Set<String> inList = orderedSets.get(i);
							
							if (set.size() > inList.size())
							{
								orderedSets.add(i, set);
								break;
							}
						}
					}
				}
				
				
				for (int i = 0; i < orderedSets.size(); i++)
				{
					Set<String> set = orderedSets.get(i);
					//get externals from these cliques
					Set<String> externals = new HashSet<String>();
					
					boolean flag = true;
					
					for (String w : set)
					{
						if (flag)
						{
							externals.addAll(overall.getNeighbors(w));
							flag = false;
						}
						else
						{
							externals.retainAll(overall.getNeighbors(w));
						}
							
					}
					
					//do we care about this clique?
					if (externals.size() >= k)
					{
						//yes we do
						
						//do distance analysis
						//output distances
						writer.println("Externals: (" + externals.size() + ")");
						
						writer.println(externals);
//						writer.println("Mean external distance: \t" + d.meanDistance(next.getFirst(), mapping));
//						writer.println("Median external distance: \t" + d.medianDistance(next.getFirst(), mapping));
						writer.println("Wineries: (" + set.size() + ")");
						writer.println(set);
						writer.println("Mean winery distance: \t" + d.meanDistance(set, mapping));
						writer.println("Median winery distance: \t" + d.medianDistance(set, mapping));
						
						writer.println();
						
					}
					
				}
				
				writer.close();
					
			}
		}
	}
	
	
	//get an edge weight based on number of overlapping externals and output to file
	public static void outputWeightedProjection()
	{
		LinkedList<String> provinces = new LinkedList<String>();
		provinces.add("BC");
		provinces.add("ON");
		provinces.add("QC");
		
		distance<String> d = new distance<String>();
		PriorityQueue<Pair<Set<String>>> pq;
		
		
		for (String province : provinces)
		{
			String distanceFile = "datasets/wine/Distance/"+province +"/"+province+"Distance.txt";
			HashMap<String, Pair<Double>> mapping = distance.getLatLongFromFile(distanceFile);
			
			String filename = "datasets/wine/"+province+"/provinceSpecificEdgeList.txt";
			
			HashMap<String, HashSet<String>> wineries = new HashMap<String, HashSet<String>>();
			
			//get list of wineries
			FileReader file = null;
			try {
				file = new FileReader(filename);
			} catch (FileNotFoundException e) {
				System.out.println("File " + filename + " could not be found.");
				e.printStackTrace();
			}

			Scanner scan = new Scanner(file);

			while (scan.hasNext()) {
				String a = scan.next();
				String b = scan.next();
				
				if (!wineries.containsKey(a))
					wineries.put(a, new HashSet<String>());
				
				wineries.get(a).add(b);
				
			}
			try {
				scan.close();
				file.close();
			} catch (IOException e) {
				System.out.println("File " + filename + " could not be found.");
				e.printStackTrace();
			}
			
			//get a keyset to be traversed
			LinkedList<String> keys = new LinkedList<String>();
			keys.addAll(wineries.keySet());
			
			//output file for generated edge list
			String writeFilename = "datasets/wine/"+province+"/" + province + "weightedProjection.txt";
			PrintWriter writer = null;
			try {
				writer = new PrintWriter(writeFilename, "UTF-8");
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for (int i = 0; i < keys.size(); i++)
			{
				String w1 = keys.get(i);
				for (int j = i+1; j < wineries.size(); j++)
				{
					String w2 = keys.get(j);
					
					HashSet<String> w1Externals = new HashSet<String>();
					w1Externals.addAll(wineries.get(w1));
					
					HashSet<String> w2Externals = new HashSet<String>();
					w2Externals.addAll(wineries.get(w2));
					
					//remove all wineries from external sets
					w1Externals.removeAll(keys);
					w2Externals.removeAll(keys);
					
					
					w1Externals.retainAll(w2Externals);
					
					int card = w1Externals.size();
					
					if (card > 0)
						writer.println(w1 + "\t" + w2 + "\t" + card);
					
				}
			}
			
			writer.close();
			
			
		}
	}
	
	public static void mdTest()
	{
		Graph<Integer, Pair<Integer>> g = gen.clique(10);
		
		myGraph<Integer> mdGraph = new myGraph<Integer>(g);
		
		System.out.println(mdGraph.getMDTree());
	}
	
	public static void getRules()
	{
		Graph<Integer, Pair<Integer>> g = new UndirectedSparseGraph<Integer, Pair<Integer>>();
		
		g.addEdge(new Pair<Integer>(0, 1), 0, 1);
		g.addEdge(new Pair<Integer>(1, 2), 1, 2);
		g.addEdge(new Pair<Integer>(1, 3), 1, 3);
		g.addEdge(new Pair<Integer>(2, 3), 2, 3);
		
		Controller<Integer> c = new Controller<Integer>(null, true);
		
		clusterBranch<Integer> b = new clusterBranch<Integer>(c);
		
		c.setbStruct(b);
		
		c.branchStart(g, 10);
		
		
		
	}
	
	private static void clusterComparisonTest()
	{
		//run the same graph as a test over multiple traversal methods
		clusterSearch<Integer> search = new clusterSearch<Integer>();
		qtGenerate<Integer> gen = new qtGenerate<Integer>();
		Cloner clone = new Cloner();
		
		//load all test branching methods
		Controller<Integer> c = new Controller<Integer>(null, false);
		clusterAllStruct<Integer> allOriginal = new clusterAllStruct<Integer>(c);
		clusterBranch<Integer> branchCOriginal = new clusterBranch<Integer>(c);
		
		
		branchComponents<Integer> all = new branchComponents<Integer>(c, allOriginal);
		branchComponents<Integer> branchC = new branchComponents<Integer>(c, branchCOriginal);
		
		
		
		//store branching types
		LinkedList<Branch<Integer>> b = new LinkedList<Branch<Integer>>();
		HashSet<Integer> moves;
		HashSet<Boolean> success;
		
		Graph<Integer, Pair<Integer>> graph;
		
		b.add(all);
		b.add(branchC);
		
		int size = 30;
		
		outer:
		while (size < 66)
		{
			int seed = 0;
			seedloop:
			while (seed < 20)
			{
				//create graph
				graph = gen.treeRandom(size, seed);
				Graph<Integer, Pair<Integer>> og = clone.deepClone(graph);
				int bound = 0;
				
				visualize(og);
				
				boundloop:
				while (bound < 17)
				{
					moves = new HashSet<Integer>();
					success = new HashSet<Boolean>();
					
					branchingReturnC<Integer> ans = null;
					
					for (Branch<Integer> temp : b)
					{
						c.setbStruct(temp);
						ans = c.branchStart(graph, bound);
						
						if (!gen.graphEquals(og, graph))
						{
							System.out.println("Graph modified at size " + size + ", seed " + seed + ", bound " + bound);
							break outer;
						}
						
						
						if (!moves.isEmpty() && moves.add(ans.getMinMoves().getChanges().size()))
						{
							System.out.println("Different solutions at size " + size + ", seed " + seed + ", bound " + bound);
							break outer;
						}
						else if (moves.isEmpty())
							moves.add(ans.getMinMoves().getChanges().size());
						
						if (!success.isEmpty() && success.add(search.isTarget(ans.getG())))
						{
							System.out.println("Different success in solving at size " + size + ", seed " + seed + ", bound " + bound);
							break outer;
						}
						else if (success.isEmpty())
							success.add(search.isTarget(ans.getG()));
						
					}
					if (success.iterator().next())
					{
						break boundloop;
					}

					bound++;
				}
				seed++;
			}
			size++;
		}
	}
	
	
	/*
	 * output common externals to cliques found by editing
	 */
	public static void clusterCommonExternals()
	{
		//String province = "BC";
		LinkedList<String> provinces = new LinkedList<String>();
		distance<String> d = new distance<String>();
		
		//get edited cliques
		Controller<String> c = new Controller<String>(null, true);
		clusterAllStruct<String> bStruct = new clusterAllStruct<String>(c);
		
		c.setbStruct(bStruct);
		bStruct.setDive(new clusterGreedy<String>(bStruct));
		
		provinces.add("BC");
		provinces.add("ON");
		provinces.add("QC");
		
		for (String province : provinces)
		{
			//make a general graph for lookup
			Graph<String, Pair<String>> overall = fillGraphFromFileWithStrings("datasets/wine/" + province + "/ProvinceSpecificEdgeList.txt");
			
			
			String distanceFile = "datasets/wine/Distance/"+province +"/"+province+"Distance.txt";
			HashMap<String, Pair<Double>> mapping = distance.getLatLongFromFile(distanceFile);
			
			UndirectedGraph<String, Pair<String>> wine = null;
			
			
			for (int k = 2; k < 6; k++)
			{
			
				try {
					wine = genString.fromBipartiteFile("datasets/wine/" + province + "/ProvinceSpecificEdgeList.txt", k);
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				//run editing
				branchingReturnC<String> result = c.diveAtStartEdit(wine, 30);
				
				if (!bStruct.getSearch().isTarget(result.getG()))
					continue;
				
				
				//find connected components in results (aka cliques)
				WeakComponentClusterer<String, Pair<String>> components = new WeakComponentClusterer<String, Pair<String>>();
				Set<Set<String>> cliques = components.transform(result.getG());
				
				//nothing left of graph
				if (cliques.size() == 0)
					break;
				
				PrintWriter writer = null;
				try {
					writer = new PrintWriter("datasets/wine/"+province+"/clusterEdit/externalk"+k+"ProjectionWithCommonExternals.txt", "UTF-8");
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				//order sets in decreasing size
				LinkedList<Set<String>> orderedSets = new LinkedList<Set<String>>();
				
				for (Set<String> set : cliques)
				{
					if (orderedSets.isEmpty())
						orderedSets.add(set);
					else
					{
						for (int i = 0; i < orderedSets.size(); i++)
						{
							Set<String> inList = orderedSets.get(i);
							
							if (set.size() > inList.size())
							{
								orderedSets.add(i, set);
								break;
							}
						}
					}
				}
				
				
				for (int i = 0; i < orderedSets.size(); i++)
				{
					Set<String> set = orderedSets.get(i);
					//get externals from these cliques
					Set<String> externals = new HashSet<String>();
					
					boolean flag = true;
					
					for (String w : set)
					{
						if (flag)
						{
							externals.addAll(overall.getNeighbors(w));
							flag = false;
						}
						else
						{
							externals.retainAll(overall.getNeighbors(w));
						}
							
					}
					
					//do we care about this clique?
					if (set.size() > 1)
					{
						//yes we do
						
						//do distance analysis
						//output distances
						writer.println("Externals: (" + externals.size() + ")");
						
						writer.println(externals);
//						writer.println("Mean external distance: \t" + d.meanDistance(next.getFirst(), mapping));
//						writer.println("Median external distance: \t" + d.medianDistance(next.getFirst(), mapping));
						writer.println("Wineries: (" + set.size() + ")");
						writer.println(set);
						writer.println("Mean winery distance: \t" + d.meanDistance(set, mapping));
						writer.println("Median winery distance: \t" + d.medianDistance(set, mapping));
						
						writer.println();
						
					}
					
				}
				
				writer.close();
					
			}
		}
	}
	
	public static void test()
	{
		Graph<Integer, Pair<Integer>> graph = gen.treeRandom(30, 1);
		
		Controller<Integer> c = new Controller<Integer>(null, true);
		
		Branch<Integer> bStruct = new clusterAllStruct<Integer>(c);
		
		c.setbStruct(bStruct);
		
		bStruct.setDive(new clusterGreedy<Integer>(bStruct));
		visualize(clone.deepClone(graph));
		
		branchingReturnC<Integer> rtn = c.branchStart(graph, 19);
		
		visualize(rtn.getG());
		
	}
	
	public static void honoursTest()
	{
		//----------------------------------------------------
		//initialize distance measurements
		distance<String> d = new distance<String>();
		String distanceFile = "datasets/wine/Distance/BC/BCDistance.txt";
		HashMap<String, Pair<Double>> mapping = distance.getLatLongFromFile(distanceFile);
		
		
		//----------------------------------------------------
		//initialize editor
		Controller<String> c = new Controller<String>(null, true);
		
		ArrayList<Branch<String>> bStructs = new ArrayList<Branch<String>>(3);
		
		clusterAllStruct<String> cluster = new clusterAllStruct<String>(c);
		cluster.addReduction(new clusterReductionBasic<String>(cluster));
		
		cluster.setDive(new clusterGreedy<String>(cluster));
		
		bStructs.add(0, new branchComponents<String>(c, cluster));
		
		
		cographAllStruct<String> cograph = new cographAllStruct<String>(c);
		
		cograph.setDive(new cographGreedy<String>(cograph));
	
		bStructs.add(1, new branchComponents<String>(c, cograph));
		
		//set up qt editor with reduction rule
		qtBranch<String> temp = new qtAllStruct<String>(c);
		
		temp.addReduction(new c4p4Reduction<String>(temp));
		
		temp.setDive(new maxObsGreedy<String>(temp));
		
		bStructs.add(2, new branchComponents<String>(c, temp));
		
		
		//----------------------------------------------------
		//set up data
		
		Graph<String, Pair<String>> wine = fillGraphFromFileWithStrings("datasets/wine/BC/wineryEdgeSet.txt");
		
		Graph<String, Pair<String>> originalWineClone = clone.deepClone(wine);
		
		
		
		
		//----------------------------------------------------
		//perform editing
		
		branchingReturnC<String> goal = null;
		
		for (int i = 0; i < 3; i++)
		{
			Branch<String> bStruct = bStructs.get(i);
			
			c.setbStruct(bStruct);
			
			//try approximate edit
			goal = c.diveAtStartEdit(wine, 4);
			
			//try iterative deepening
			//goal = c.branchID(wine, 2, 50);
		
			if (bStruct.getSearch().isTarget(goal.getG()))
			{
				//print results
				String path = null;
				if (i == 0)
					path = "datasets/wine/Thesis/Cluster/Approximate/";
				else if (i == 1)
					path = "datasets/wine/Thesis/Cograph/Approximate/";
				else
					path = "datasets/wine/Thesis/QT/Approximate/";
			
				stringUtils.printSolutionEdgeSetWithWeightsComponents(goal, path+"wineSolution.txt");
				d.outputDistanceMeasurements(path+"wineSolution.txt", distanceFile, path+"wineDistances.txt");
			}
			
			
			
		}
		
		
		
		
	}
}
