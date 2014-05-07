import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JApplet;
import javax.swing.JFrame;

import jimsFiles.IsA;
import qtUtils.genericLBFS;
import qtUtils.qtGenerate;
import qtUtils.qtRecognition;
import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
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
		Graph<Integer, String> exampleQT = new SparseGraph<Integer, String>();
		exampleQT.addEdge("edge1", 1, 2);
		exampleQT.addEdge("edge2", 1, 3);
		exampleQT.addEdge("edge3", 2, 3);
		exampleQT.addEdge("edge4", 1, 4);
		exampleQT.addEdge("edge5", 3, 4);
		exampleQT.addEdge("edge6", 0, 1);
		exampleQT.addEdge("edge7", 0, 3);
		
		exampleQT.addEdge("edge9", 2, 4);

		
		
		ArrayList<Integer> param = new ArrayList<Integer>(exampleQT.getVertexCount());
		param.addAll(exampleQT.getVertices());
		for ( int i = 0; i < 5; i++)
		{
			param = genericLBFS.genericLexBFS(exampleQT,param);
			System.out.println(param);
		}
		
		
	}
	
	
	public void test()
	{
		graph = new SparseGraph<Integer, String>();

		String filename = "datasets/footballEdgeList.tgf";
		fillGraphFromFile(graph, filename);
		
		Graph<Integer, String> exampleQT = new SparseGraph<Integer, String>();
		exampleQT.addEdge("edge1", 1, 2);
		exampleQT.addEdge("edge2", 1, 3);
		exampleQT.addEdge("edge3", 2, 3);
		exampleQT.addEdge("edge4", 1, 4);
		exampleQT.addEdge("edge5", 3, 4);
		exampleQT.addEdge("edge6", 0, 1);
		exampleQT.addEdge("edge7", 0, 3);

		long start = System.currentTimeMillis();
		exampleQT = qtGenerate.qtGraph(500);
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		start = System.currentTimeMillis();
		System.out.println(qtRecognition.qtCheckYan(exampleQT));
		System.out.println((System.currentTimeMillis()-start) / 1000.0);
		
		System.out.println("Is a chordal: " + IsA.Chordal(exampleQT));
		System.out.println("Is a cograph: " + IsA.Cograph(exampleQT));
		
		//findEdgeBetweennessClustering(graph, 1000);

		JFrame jf = new JFrame();
		jf.setSize(1366, 768);

		FRLayout frl = new FRLayout(exampleQT);

		frl.setAttractionMultiplier(1.5);
		frl.lock(true);
		VisualizationViewer vv = new VisualizationViewer(frl, new Dimension(
				1366, 768));

		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		
		jf.getContentPane().add(vv);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);
		
		JFrame jf1 = new JFrame();
		jf1.setSize(1366, 768);
		FRLayout fr2 = new FRLayout(qtRecognition.qtCheckYan(exampleQT));

		fr2.setAttractionMultiplier(1.5);
		fr2.lock(true);
		VisualizationViewer vv1 = new VisualizationViewer(fr2, new Dimension(
				1366, 768));
		// ViewScalingControl scale = new ViewScalingControl();
		// scale.scale(vv, (float) 0.8, new Point2D.Double(1366/2, 768/2));
		// vv.scaleToLayout(scale);
		vv1.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		jf1.getContentPane().add(vv1);
		jf1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf1.pack();
		jf1.setVisible(true);
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
	private static void fillGraphFromFile(Graph<Integer, String> graph,
			String filename) {
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
			graph.addEdge("e:" + a + "-" + b, a, b);
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
