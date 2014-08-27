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
import search.YanSearch;
import search.qtLBFSNoHeuristic;
import abstractClasses.Branch;
import abstractClasses.Dive;
import abstractClasses.Reduction;
import branch.qtAllStruct;
import branch.qtBranch;
import branch.qtBranchComponents;
import branch.qtBranchNoHeuristic;

import com.rits.cloning.Cloner;

import controller.Controller;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
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
		userInterface();
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
		
		exampleQT = gen.ER(16, 0.5, (long) 3);
		
		//exampleQT = fillGraphFromFile("datasets/zachary.txt");
		
		//Graph<String, Pair<String>>wine = fillGraphFromFile("datasets/wineryEdgeSet.txt");
		
		
		//Graph<String, Pair<String>> wine = gen.fromBipartiteFile("datasets/edgeSet.txt");
		
		//exampleQT = gen.fromBipartiteFile("datasets/southernwomen");
	
		//exampleQT = gen.manyInducedC4(6);
		
		//exampleQT = gen.houseStruct();
		
		
		
		//exampleQT = fillGraphFromFile("datasets/grass_web.pairs");

		
		exampleQT = gen.treeRandom(150, 5);
		
		//exampleQT = gen.houseStruct();
		

		
		//exampleQT = gen.facebookGraph("datasets/fbFriends.txt");
		
		Graph<Integer, Pair<Integer>> cln = clone.deepClone(exampleQT);


		
		//visualize(exampleQT);
		
		Controller<Integer> c = new Controller<Integer>(null, false);
		
		
		
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
		
		
//		rC = new c4p4Reduction<Integer>(branchC);
//		branchC.addReduction(rC);
//		
		rC = new biconnectedReduction<Integer>(branchC);
		branchC.addReduction(rC);
		
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
		rC = new biconnectedReduction<Integer>(all2);
		all2.addReduction(rC);
		
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
//		c.setbStruct(branchC);
//		System.out.println("\nConnected component: ");
//		start = System.currentTimeMillis();
//		System.out.println(yan.search(c.branchStart(exampleQT, 15).getG()));
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
////		
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
//		System.out.println(yan.search(c.branchStart(exampleQT, 16).getG()));
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
//		c.setbStruct(branchC);
//		System.out.println("\nConnected component: ");
//		start = System.currentTimeMillis();
//		System.out.println(yan.search(c.branchStart(exampleQT, 14).getG()));
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
//		
//		
		c.setbStruct(branchC);
	
		for (int i = 5; i <= 30; i++)
		{
			System.out.println("Bound: " + i);
			start = System.currentTimeMillis();
			System.out.println(yan.search(c.branchStart(exampleQT, i).getG()));
			System.out.println((System.currentTimeMillis()-start) / 1000.0);
		}
		
		
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
//		System.out.println(yan.search(c.diveAtStartEdit(exampleQT).getG()));
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
		jf.setSize(1900, 1000);

		FRLayout frl = new FRLayout(exampleQT);

		frl.setAttractionMultiplier(3);
		frl.setRepulsionMultiplier(1.1);
		
		frl.setMaxIterations(1000);
		//frl.lock(true);
		VisualizationViewer vv = new VisualizationViewer(frl, new Dimension(
				1800, 900));
		
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		
		jf.getContentPane().add(vv);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);
	}
	
	public static void visualizeString(Graph<String, Pair<String>> exampleQT){
		JFrame jf = new JFrame();
		jf.setSize(1900, 1000);

		FRLayout frl = new FRLayout(exampleQT);

		frl.setAttractionMultiplier(3);
		frl.setRepulsionMultiplier(1.1);
		
		frl.setMaxIterations(1000);
	
		//frl.lock(true);
		VisualizationViewer vv = new VisualizationViewer(frl, new Dimension(
				1800, 900));
		
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
		
		Graph<String, Pair<String>> wine = fillGraphFromFileWithStrings("datasets/wine/wineryEdgeSet.txt");
		
		
		Controller<String> c = new Controller<String>(null, true);
		
		
		qtBranchComponents<String> branchC = new qtBranchComponents<String>(c);
		
		Reduction<String> rC = new c4p4Reduction<String>(branchC);
		branchC.addReduction(rC);
		
		rC = new biconnectedReduction<String>(branchC);
		branchC.addReduction(rC);
		
		visualizeString(wine);
		
		c.setbStruct(branchC);
		System.out.println("\nConnected component: ");
		long start = System.currentTimeMillis();
		branchingReturnC<String> rtn = c.branchStart(wine, 15);
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		
		if (branchC.getSearch().search(rtn).isTarget())
		{
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
}
