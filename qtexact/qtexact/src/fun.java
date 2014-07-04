/**
 * Yaroslav Senyuta
 * NSERC USRA Grant (2014)
 */

import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JApplet;
import javax.swing.JFrame;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import qtUtils.qtGenerate;
import reduction.commonC4Reduction;
import reduction.edgeBoundReduction;
import search.YanSearch;
import abstractClasses.Reduction;
import branch.qtAllStruct;
import branch.qtBranchComponents;
import branch.qtBranchNoHeuristic;
import branch.qtC5;
import branch.qtHouse;
import branch.qtKite;
import branch.qtP5;
import branch.qtPan;
import branch.qtRandom;
import branch.qtSimple;
import branch.qtY;

import com.rits.cloning.Cloner;

import controller.Controller;
import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

@SuppressWarnings("serial")
public class fun extends JApplet {

	/**
	 * graph
	 */
	static Graph<Integer, String> graph;
	static Cloner clone = new Cloner();
	
	public static void main(String[] args)
	{
		
		editTest();
	}
	
	public static void editTest() 
	{
		Graph<Integer, Pair<Integer>> exampleQT;
		qtGenerate<Integer> gen = new qtGenerate<Integer>();
		exampleQT = gen.randomQT(50);
		//may break it
		exampleQT.addEdge(new Pair<Integer>(0, 6), 0, 6);
		exampleQT.addEdge(new Pair<Integer>(8, 1), 8, 1);
		exampleQT.addEdge(new Pair<Integer>(8, 5), 8, 5);
		
		exampleQT = gen.cliqueJoin(5, 5);
		
		//exampleQT = qtGenerate.simpleC4();
		
		//exampleQT = qtGenerate.westernElectricNetwork();
		
		exampleQT = qtGenerate.nonQTEx5();
		
		//random graph join
		exampleQT = gen.erJoins(8, 8, 5, .86, .86, .9);
		
		//exampleQT = gen.ER(17, 0.7);
		
		exampleQT = new SparseGraph<Integer, Pair<Integer>>();
		fillGraphFromFile(exampleQT, "datasets/karate.txt");
		
		//exampleQT = gen.fromBipartiteFile("datasets/southernwomen");
	
		//exampleQT = gen.manyInducedC4(6);
		
		//exampleQT = gen.houseStruct();
		
		
		//exampleQT = new SparseGraph<Integer, Pair<Integer>>();
		//fillGraphFromFile(exampleQT, "datasets/grass_web.pairs");
		
		
		//Graph<String, Pair<String>> fb = gen.facebookGraph("datasets/fbFriends.txt");
		
		Graph<Integer, Pair<Integer>> cln = clone.deepClone(exampleQT);
		visualize(cln);
		
		
		Controller<Integer> c = new Controller<Integer>(null, false);
		
		
		qtBranchNoHeuristic<Integer> branchNoHP = new qtBranchNoHeuristic<Integer>(c);
		//c.setbStruct(branchNoHP);
		
		
		//qtHouse<Integer> house = new qtHouse<Integer>(c);
		//c.setbStruct(house);
		
		qtPan<Integer> pan = new qtPan<Integer>(c);
		
		qtAllStruct<Integer> all = new qtAllStruct<Integer>(c);
		
		
		qtBranchComponents<Integer> branchC = new qtBranchComponents<Integer>(c);
		//c.setbStruct(branchC);
		
		qtC5<Integer> C5 = new qtC5<Integer>(c);
		
		qtHouse<Integer> house = new qtHouse<Integer>(c);
		
		qtKite<Integer> kite = new qtKite<Integer>(c);
		
		qtP5<Integer> P5 = new qtP5<Integer>(c);
		
		qtY<Integer> y = new qtY<Integer>(c);
		
		
		qtSimple<Integer> simple = new qtSimple<Integer>(c);
		
		qtRandom<Integer> random = new qtRandom<Integer>(c);
		
		
//		Reduction<Integer> rHouse = new edgeBoundReduction<Integer>(house);
//		house.addReduction(rHouse);
//		Reduction<Integer> r2House = new commonC4Reduction<Integer>(house);
//		house.addReduction(r2House);
//		
		Reduction<Integer> rpan = new edgeBoundReduction<Integer>(pan);
		pan.addReduction(rpan);
		Reduction<Integer> r2pan = new commonC4Reduction<Integer>(pan);
		pan.addReduction(r2pan);
		
		
		Reduction<Integer> rC = new edgeBoundReduction<Integer>(branchC);
		branchC.addReduction(rC);
		Reduction<Integer> r2C = new commonC4Reduction<Integer>(branchC);
		branchC.addReduction(r2C);
		
		
		rC = new edgeBoundReduction<Integer>(all);
		all.addReduction(rC);
		r2C = new commonC4Reduction<Integer>(all);
		all.addReduction(r2C);
//		
		Reduction<Integer> rNo = new edgeBoundReduction<Integer>(branchNoHP);
		branchNoHP.addReduction(rNo);
		Reduction<Integer> r2No = new commonC4Reduction<Integer>(branchNoHP);
		branchNoHP.addReduction(r2No);
//		
		
		YanSearch<Integer> yan = new YanSearch<Integer>();
		
		long start = System.currentTimeMillis();
		System.out.println(yan.search(exampleQT));
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		
		rC = new edgeBoundReduction<Integer>(C5);
		C5.addReduction(rC);
		r2C = new commonC4Reduction<Integer>(C5);
		C5.addReduction(r2C);
		
		rC = new edgeBoundReduction<Integer>(house);
		house.addReduction(rC);
		r2C = new commonC4Reduction<Integer>(house);
		house.addReduction(r2C);
		
		
		rC = new edgeBoundReduction<Integer>(kite);
		kite.addReduction(rC);
		r2C = new commonC4Reduction<Integer>(kite);
		kite.addReduction(r2C);
		
		rC = new edgeBoundReduction<Integer>(P5);
		P5.addReduction(rC);
		r2C = new commonC4Reduction<Integer>(P5);
		P5.addReduction(r2C);
		
		rC = new edgeBoundReduction<Integer>(y);
		y.addReduction(rC);
		r2C = new commonC4Reduction<Integer>(y);
		y.addReduction(r2C);
		
		
		rC = new edgeBoundReduction<Integer>(simple);
		simple.addReduction(rC);
		r2C = new commonC4Reduction<Integer>(simple);
		simple.addReduction(r2C);
		
		rC = new edgeBoundReduction<Integer>(random);
		random.addReduction(rC);
		r2C = new commonC4Reduction<Integer>(random);
		random.addReduction(r2C);
		
		
//		
		
//		c.setbStruct(simple);
//		System.out.println("\nSimple: ");
//		start = System.currentTimeMillis();
//		c.branchStart(exampleQT, 21);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
		
		c.setbStruct(random);
		System.out.println("\nRandom: ");
		start = System.currentTimeMillis();
		
		int hit = 0;
		LinkedList<myEdge<Integer>> min = null;
		
		while (hit < 10)
		{
			branchingReturnC<Integer> rtn = c.branchStart(exampleQT, 34);
			if (rtn.getG() != null)
			{
				if (min == null)
					min = rtn.getMinMoves().getChanges();
				
				else if (min.size() > rtn.getMinMoves().getChanges().size())
					min = rtn.getMinMoves().getChanges();
					
				hit++;
			}
		}
		
		System.out.println("Number of min moves: " + min.size());
		System.out.println("Min moves: " + min);
		
		
		
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
		
//		c.setbStruct(C5);
//		System.out.println("\nC5: ");
//		start = System.currentTimeMillis();
//		c.branchStart(exampleQT, 10);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
////		
//		Graph<Integer, Pair<Integer>> cln4 = clone.deepClone(exampleQT);
//		visualize(cln4);
		
//		c.setbStruct(house);
//		System.out.println("\nHouse: ");
//		start = System.currentTimeMillis();
//		c.branchStart(exampleQT, 10);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
//		
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
		
		
//		Graph<Integer, Pair<Integer>> cln5 = clone.deepClone(exampleQT);
//		visualize(cln5);
		
//		c.setbStruct(kite);
//		System.out.println("\nKite: ");
//		start = System.currentTimeMillis();
//		c.branchStart(exampleQT, 10);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
//		
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
//		
//		Graph<Integer, Pair<Integer>> cln6 = clone.deepClone(exampleQT);
//		visualize(cln6);
		
//		c.setbStruct(P5);
//		System.out.println("\nP5: ");
//		start = System.currentTimeMillis();
//		c.branchStart(exampleQT, 10);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
		
//		Graph<Integer, Pair<Integer>> cln7 = clone.deepClone(exampleQT);
//		visualize(cln7);
		
//		c.setbStruct(y);
//		System.out.println("\nY: ");
//		start = System.currentTimeMillis();
//		c.branchStart(exampleQT, 10);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
//		
//		Graph<Integer, Pair<Integer>> cln8 = clone.deepClone(exampleQT);
//		visualize(cln8);
		
		
//		c.setbStruct(all);
//		System.out.println("\nAll structures: ");
//		start = System.currentTimeMillis();
//		c.branchStart(exampleQT, 16);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
		
		
//		Graph<Integer, Pair<Integer>> cln9 = clone.deepClone(exampleQT);
//		visualize(cln9);
		
		
//		
		
//		c.setbStruct(branchNoHP);
//		System.out.println("\nNo Heuristic: ");
//		start = System.currentTimeMillis();
//		c.branchStart(exampleQT, 10);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
//		
//		
//		Graph<Integer, Pair<Integer>> cln3 = clone.deepClone(exampleQT);
//		visualize(cln3);
//		
//		c.setbStruct(pan);
//		System.out.println("\npan: ");
//		start = System.currentTimeMillis();
//		c.branchStart(exampleQT, 10);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
//		
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
////		
//		Graph<Integer, Pair<Integer>> cln2 = clone.deepClone(exampleQT);
//		visualize(cln2);
//	
//		c.setbStruct(branchC);
//		System.out.println("\nConnected component: ");
//		start = System.currentTimeMillis();
//		c.branchStart(exampleQT, 17);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
//		
//		System.out.println("\nGraph same? " + gen.graphEquals(cln, exampleQT));
//		
		
		
		start = System.currentTimeMillis();
		System.out.println(yan.search(exampleQT));
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		
		visualize(exampleQT);
	}
	
	public static void visualize(Graph<Integer, Pair<Integer>> fb){
		JFrame jf = new JFrame();
		jf.setSize(1900, 1000);

		FRLayout frl = new FRLayout(fb);

		frl.setAttractionMultiplier(3);
		frl.setRepulsionMultiplier(1.1);
		
		frl.setMaxIterations(1000);
		//frl.lock(true);
		VisualizationViewer vv = new VisualizationViewer(frl, new Dimension(
				1900, 1000));
		
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		
		jf.getContentPane().add(vv);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);
	}
	
	public void test()
	{
//		graph = new SparseGraph<Integer, String>();
//
//		String filename = "datasets/footballEdgeList.tgf";
//		fillGraphFromFile(graph, filename);
//		
//		Graph<Integer, String> exampleQT = new SparseGraph<Integer, String>();
//		exampleQT.addEdge("edge1", 1, 2);
//		exampleQT.addEdge("edge2", 1, 3);
//		exampleQT.addEdge("edge3", 2, 3);
//		exampleQT.addEdge("edge4", 1, 4);
//		exampleQT.addEdge("edge5", 3, 4);
//		exampleQT.addEdge("edge6", 0, 1);
//		exampleQT.addEdge("edge7", 0, 3);
//
//		long start = System.currentTimeMillis();
//		exampleQT = qtGenerate.clique(500);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
//		start = System.currentTimeMillis();
//		System.out.println(qtRecognition.qtCheckYan(exampleQT));
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
//		System.out.println("Is a chordal: " + IsA.Chordal(exampleQT));
//		System.out.println("Is a cograph: " + IsA.Cograph(exampleQT));
//		
//		//findEdgeBetweennessClustering(graph, 1000);
//
//		JFrame jf = new JFrame();
//		jf.setSize(1366, 768);
//
//		FRLayout frl = new FRLayout(exampleQT);
//
//		frl.setAttractionMultiplier(1.5);
//		frl.lock(true);
//		VisualizationViewer vv = new VisualizationViewer(frl, new Dimension(
//				1366, 768));
//
//		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
//		
//		jf.getContentPane().add(vv);
//		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		jf.pack();
//		jf.setVisible(true);
//		
//		JFrame jf1 = new JFrame();
//		jf1.setSize(1366, 768);
//		FRLayout fr2 = new FRLayout(qtRecognition.qtCheckYan(exampleQT));
//
//		fr2.setAttractionMultiplier(1.5);
//		fr2.lock(true);
//		VisualizationViewer vv1 = new VisualizationViewer(fr2, new Dimension(
//				1366, 768));
//		// ViewScalingControl scale = new ViewScalingControl();
//		// scale.scale(vv, (float) 0.8, new Point2D.Double(1366/2, 768/2));
//		// vv.scaleToLayout(scale);
//		vv1.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
//		jf1.getContentPane().add(vv1);
//		jf1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		jf1.pack();
//		jf1.setVisible(true);
	}

	/**
	 * find edge betweenness clustering of graph with num edge deletions
	 * 
	 * @param Graph graph
	 * @param Integer num
	 */
	private static void findEdgeBetweennessClustering(
			Graph<Integer, String> graph, int num) {
		EdgeBetweennessClusterer<Integer, String> EBC = new EdgeBetweennessClusterer<Integer, String>(
				num);

		Set<Set<Integer>> edgeBetweennessCluster = EBC.transform(graph);
		List<String> listDeletedEdges = EBC.getEdgesRemoved();
		Iterator<Set<Integer>> iterator = edgeBetweennessCluster.iterator();
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}
		// remove deleted edges from graph
		for (String s : listDeletedEdges) {
			graph.removeEdge(s);
		}
	}

	/**
	 * fill graph with given file
	 * 
	 * @param Graph graph
	 * @param String filename
	 */
	private static void fillGraphFromFile(Graph<Integer, Pair<Integer>> graph,
			String filename) 
	{
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
	}
}
