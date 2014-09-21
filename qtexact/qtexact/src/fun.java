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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JApplet;
import javax.swing.JFrame;

import qtUtils.branchingReturnC;
import qtUtils.qtGenerate;
import reduction.biconnectedReduction;
import reduction.c4p4Reduction;
import reduction.centralNodeReduction;
import reduction.commonC4Reduction;
import reduction.edgeBoundReduction;
import scorer.familialGroupCentrality;
import search.YanSearch;
import search.clusterSearch;
import search.diQTSearch;
import search.qtLBFSNoHeuristic;
import abstractClasses.Branch;
import abstractClasses.Dive;
import abstractClasses.Reduction;
import branch.diQTBranch;
import branch.qtAllStruct;
import branch.qtBranch;
import branch.qtBranchComponents;
import branch.qtBranchNoHeuristic;

import com.rits.cloning.Cloner;
import components.branchComponents;

import controller.Controller;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.scoring.ClosenessCentrality;
import edu.uci.ics.jung.algorithms.scoring.EigenvectorCentrality;
import edu.uci.ics.jung.algorithms.scoring.HITS;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import greedy.maxObsGreedy;

@SuppressWarnings("serial")
public class fun<V> extends JApplet {

	/**
	 * graph
	 */
	static Graph<Integer, String> graph;
	static Cloner clone = new Cloner();
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException
	{
		//fbTest();
		//editTest();
		//comparisonTest();
		//wineTest();
		//userInterface();
		diGraphWineryTest();
		//clusterSearchTest();
		//scoreWineryGraph();
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
		qtGenerate<Integer> gen = new qtGenerate<Integer>();
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
		
		
		exampleQT = gen.randomTreeGraph(50, 9, 5);
		
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
		
		c.setbStruct(comp);
		System.out.println("\nConnected component (new components): ");
		start = System.currentTimeMillis();
		System.out.println(yan.search(c.branchStart(exampleQT, 9).getG()));
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
		
		
		
		c.setbStruct(branchC);
		System.out.println("\nConnected component: ");
		start = System.currentTimeMillis();
		System.out.println(yan.search(c.branchStart(exampleQT, 9).getG()));
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
////	
		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
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
		
		
//		c.setbStruct(branchC);
//		branchC.setDive(dive);
//		System.out.println("\nGreedy Edit: ");
//		start = System.currentTimeMillis();
//		System.out.println(yan.search(c.diveAtStartEdit(exampleQT, 50).getG()));
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		
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
//		c.setbStruct(branchC);
//		branchC.setDive(dive);
//		System.out.println("\nGreedy Edit: ");
//		start = System.currentTimeMillis();
//		rtn = c.diveAtStartEdit(wine, 4);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		//regular edit
		c.setbStruct(branchC);
		System.out.println("\nConnected component: ");
		start = System.currentTimeMillis();
		rtn = c.branchStart(wine, 8);
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
		System.out.println("\nGraph same? " + gen.graphEquals(cln, wine));
		
		
		if (branchC.getSearch().isTarget(rtn.getG()))
		{
			
			System.out.println("Solution has " + rtn.getG().getVertexCount() + " nodes and " + rtn.getG().getEdgeCount() + " edges.");
			
			//print network to file
			PrintWriter writer = new PrintWriter("datasets/wine/wineSolutionEdgeSet.tgf", "UTF-8");
			
			writer.println("#");
			for (Pair<String> edge : rtn.getG().getEdges())
			{
				writer.println(edge.getFirst() + " " + edge.getSecond());
			}
			
			writer.close();
		}
		
	}
	
	
	public static void exactVsGreedy()
	{
		
	}
	
	public static void diGraphWineryTest() throws FileNotFoundException, UnsupportedEncodingException
	{
		DirectedGraph<String, Pair<String>> g = fillDiGraphFromFileWithStrings("datasets/wine/ON/wineryEdgeSet.txt");
		
		//reverse edges and add to new graph
		DirectedGraph<String, Pair<String>> reversed = new DirectedSparseGraph<String, Pair<String>>();
		for (String v : g.getVertices())
		{
			reversed.addVertex(v);
		}
		for (Pair<String> edge : g.getEdges())
		{
			reversed.addEdge(new Pair<String>(edge.getSecond(), edge.getFirst()), edge.getSecond(), edge.getFirst());
		}
		g = reversed;
//		
		
		diQTSearch<String> search = new diQTSearch<String>();
		
		//visualize(g);
		
		System.out.println(search.search(g));
		
		Controller<String> c = new Controller<String>(null, true);
		diQTBranch<String> bStruct = new diQTBranch<String>(c);
		
		branchComponents<String> b = new branchComponents<String>(c, bStruct);
		
		c.setbStruct(b);
		
		System.out.println("\nConnected component: ");
		long start = System.currentTimeMillis();
		branchingReturnC<String> rtn = c.branchStart(g, 20);
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		System.out.println(search.isTarget(rtn.getG()));
		
		visualizeString(rtn.getG());
		
		
		//print network to file
		PrintWriter writer = new PrintWriter("datasets/wine/ONWineDiSolutionEdgeSetREVERSED.tgf", "UTF-8");
		
		writer.println("#");
		for (Pair<String> edge : rtn.getG().getEdges())
		{
			writer.println(edge.getFirst() + " " + edge.getSecond());
		}
		
		writer.close();
		
	}
	
	public static void clusterSearchTest()
	{
		clusterSearch<Integer> search = new clusterSearch<Integer>();
		
		
		Graph<Integer, Pair<Integer>> exampleQT;
		qtGenerate<Integer> gen = new qtGenerate<Integer>();
		exampleQT = gen.ER(10, .8, (long) 2);
		
		visualize(exampleQT);
		
		System.out.println(search.search(exampleQT));
	}
	
	public static void scoreWineryGraph() throws FileNotFoundException, UnsupportedEncodingException
	{
		DirectedGraph<String, Pair<String>> graph = fillDiGraphFromFileWithStrings("datasets/wine/ONWineDiSolutionEdgeSet.tgf");
		//print network to file
		PrintWriter writer = new PrintWriter("datasets/wine/scoring/ON DiQT Winery-Winery Scoring.txt", "UTF-8");
		BetweennessCentrality<String, Pair<String>> betweenness = new BetweennessCentrality<String, Pair<String>>(graph);
		ClosenessCentrality<String, Pair<String>> closeness = new ClosenessCentrality<String, Pair<String>>(graph);
		EigenvectorCentrality<String, Pair<String>> eigen = new EigenvectorCentrality<String, Pair<String>>(graph);
		HITS<String, Pair<String>> hits = new HITS<String, Pair<String>>(graph);
		familialGroupCentrality<String>	familial = new familialGroupCentrality<String>(graph);
		
		writer.println("Vertex\tBetweenness Centrality\tCloseness Centrality\tEigenvector Centrality\tHITS\tFamilial Group Centrality");
		
		for (String v : graph.getVertices())
		{
			writer.println(v + "\t" + betweenness.getVertexScore(v) + "\t" + closeness.getVertexScore(v) + "\t" + eigen.getVertexScore(v) + "\t" + hits.getVertexScore(v) + "\t" + familial.getVertexScore(v));
		}
		
		
		writer.close();
		
		
	}
	
}
