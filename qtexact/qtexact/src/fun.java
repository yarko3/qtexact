/**
 * Yaroslav Senyuta
 * NSERC USRA Grant (2014)
 */

import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JApplet;
import javax.swing.JFrame;

import qtUtils.qtGenerate;
import reduction.commonC4Reduction;
import reduction.edgeBoundReduction;
import search.YanSearch;
import abstractClasses.Reduction;
import branch.qtBranchComponents;
import branch.qtBranchNoHeuristic;
import branch.qtHouse;
import branch.qtPan;

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
		
		//exampleQT = qtGenerate.nonQTEx3();
		
		//random graph join
		exampleQT = gen.erJoins(20, 10, 5, .86, .86, 1);
		
		//exampleQT = gen.ER(11, 0.2);
		
		//exampleQT = new SparseGraph<Integer, Pair<Integer>>();
		//fillGraphFromFile(exampleQT, "datasets/karate.txt");
		
		//exampleQT = gen.fromBipartiteFile("datasets/southernwomen");
	
		//exampleQT = gen.manyInducedC4(6);
		
		//exampleQT = gen.houseStruct();
		
		
		exampleQT = new SparseGraph<Integer, Pair<Integer>>();
		fillGraphFromFile(exampleQT, "datasets/grass_web.pairs");
		
		
		Graph<String, Pair<String>> fb = gen.facebookGraph("datasets/fbFriends.txt");
		
		
		//visualize(exampleQT);
		
		
		Controller<String> c = new Controller<String>(null, true);
		//qtBranchNoHeuristic<Integer> branchNoHP = new qtBranchNoHeuristic<Integer>(c);
		//c.setbStruct(branchNoHP);
		
		
		//qtHouse<Integer> house = new qtHouse<Integer>(c);
		//c.setbStruct(house);
		
		qtPan<String> pan = new qtPan<String>(c);
		
		
		qtBranchComponents<String> branchC = new qtBranchComponents<String>(c);
		//c.setbStruct(branchC);
		
		
		
//		Reduction<Integer> rHouse = new edgeBoundReduction<Integer>(house);
//		house.addReduction(rHouse);
//		Reduction<Integer> r2House = new commonC4Reduction<Integer>(house);
//		house.addReduction(r2House);
//		
		Reduction<String> rpan = new edgeBoundReduction<String>(pan);
		pan.addReduction(rpan);
		Reduction<String> r2pan = new commonC4Reduction<String>(pan);
		pan.addReduction(r2pan);
		
		
		Reduction<String> rC = new edgeBoundReduction<String>(branchC);
		branchC.addReduction(rC);
		Reduction<String> r2C = new commonC4Reduction<String>(branchC);
		branchC.addReduction(r2C);
//		
//		Reduction<Integer> rNo = new edgeBoundReduction<Integer>(branchNoHP);
//		branchNoHP.addReduction(rNo);
//		Reduction<Integer> r2No = new commonC4Reduction<Integer>(branchNoHP);
//		branchNoHP.addReduction(r2No);
//		
		
		YanSearch<Integer> yan = new YanSearch<Integer>();
		
		long start = System.currentTimeMillis();
		System.out.println(yan.search(exampleQT));
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		c.setbStruct(branchC);
		System.out.println("\nConnected component with no reductions: ");
		start = System.currentTimeMillis();
		c.branchStart(fb, 22);
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
//		
//		c.setbStruct(branchNoHP);
//		System.out.println("\nNo heuristic without reductions: ");
//		start = System.currentTimeMillis();
//		c.branchStart(exampleQT, 7);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
//		c.setbStruct(pan);
//		System.out.println("\npan: ");
//		start = System.currentTimeMillis();
//		c.branchStart(fb, 10);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
//		c.setbStruct(house);
//		System.out.println("\nHouse: ");
//		start = System.currentTimeMillis();
//		exampleQT = c.branchStart(exampleQT, 7);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		start = System.currentTimeMillis();
		System.out.println(yan.search(exampleQT));
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		visualize(fb);
	}
	
	public static void visualize(Graph<String, Pair<String>> g){
		JFrame jf = new JFrame();
		jf.setSize(1366, 768);

		FRLayout frl = new FRLayout(g);

		frl.setAttractionMultiplier(1.5);
		//frl.lock(true);
		VisualizationViewer vv = new VisualizationViewer(frl, new Dimension(
				1366, 768));
		
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
