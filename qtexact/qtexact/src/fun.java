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
import reduction.edgeBoundReduction;
import search.YanSearch;
import abstractClasses.Reduction;
import branch.qtBranchComponents;
import branch.qtBranchNoHeuristic;
import branch.qtBranchNoHeuristicP;
import controller.Controller;
import controller.ControllerP;
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
	public static void main(String[] args)
	{
		
		editTest();
	}
	
	public static void editTest() 
	{
		Graph<Integer, Pair<Integer>> exampleQT;
		qtGenerate<Integer> gen = new qtGenerate<Integer>();
		exampleQT = gen.randomQT(100);
		//may break it
		exampleQT.addEdge(new Pair<Integer>(0, 6), 0, 6);
		exampleQT.addEdge(new Pair<Integer>(8, 1), 8, 1);
		exampleQT.addEdge(new Pair<Integer>(8, 5), 8, 5);
		
		//exampleQT = gen.cliqueJoin(20, 30);
		
		//exampleQT = qtGenerate.simpleC4();
		
		//exampleQT = qtGenerate.westernElectricNetwork();
		
		//exampleQT = qtGenerate.nonQTEx3();
		
		//exampleQT = gen.ER(15, 0.53);
		
		exampleQT = new SparseGraph<Integer, Pair<Integer>>();
		fillGraphFromFile(exampleQT, "datasets/karate.txt");
		
		//exampleQT = gen.fromBipartiteFile("datasets/southernwomen");
		
		Controller<Integer> cNoHeuristic = new Controller<Integer>(null);
		qtBranchNoHeuristic<Integer> b = new qtBranchNoHeuristic<Integer>(cNoHeuristic);
		cNoHeuristic.setbStruct(b);
		
		Controller<Integer> cConnected = new Controller<Integer>(null);
		qtBranchComponents<Integer> bConnected = new qtBranchComponents<Integer>(cConnected);
		cConnected.setbStruct(bConnected);
		
		ControllerP<Integer> cNoHeuristicP = new ControllerP<Integer>(null);
		qtBranchNoHeuristicP<Integer> branchNoHP = new qtBranchNoHeuristicP<Integer>(cNoHeuristicP);
		cNoHeuristicP.setbStruct(branchNoHP);
		
		Reduction<Integer> r = new edgeBoundReduction<Integer>(branchNoHP);
		branchNoHP.addReduction(r);
		
		
		//visualize(exampleQT);
		
		YanSearch<Integer> yan = new YanSearch<Integer>();
		
		long start = System.currentTimeMillis();
		System.out.println(yan.search(exampleQT));
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		/*System.out.println("\nIterative Deepening connected components: ");
		start = System.currentTimeMillis();
		cConnected.branchID(exampleQT, 1, 10);
		System.out.println((System.currentTimeMillis()-start) / 1000.0);*/
		
		System.out.println("\nIterative Deepening no heuristic: ");
		start = System.currentTimeMillis();
		cNoHeuristicP.branchID(exampleQT, 1, 30);
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		/*System.out.println("\nConnected components: ");
		start = System.currentTimeMillis();
		cConnected.branchStart(exampleQT, 10);
		System.out.println((System.currentTimeMillis()-start) / 1000.0);*/
		
//		System.out.println("\nNo heuristic: ");
//		start = System.currentTimeMillis();
//		exampleQT = cNoHeuristicP.branchStart(exampleQT, 30);
//		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		start = System.currentTimeMillis();
		System.out.println(yan.search(exampleQT));
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		visualize(exampleQT);
	}
	
	public static void visualize(Graph<Integer, Pair<Integer>> g){
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
