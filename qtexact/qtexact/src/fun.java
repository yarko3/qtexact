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

import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;

@SuppressWarnings("serial")
public class fun extends JApplet {

	/**
	 * graph
	 */
	static Graph<Integer, String> graph;

	public static void main(String[] args) {
		graph = new SparseGraph<Integer, String>();

		String filename = "datasets/disease.net";
		fillGraphFromFile(graph, filename);

		findEdgeBetweennessClustering(graph, 1000);

		JFrame jf = new JFrame();
		jf.setSize(1366, 768);

		FRLayout frl = new FRLayout(graph);

		frl.setAttractionMultiplier(1.5);
		frl.lock(true);
		VisualizationViewer vv = new VisualizationViewer(frl, new Dimension(
				1366, 768));
		// ViewScalingControl scale = new ViewScalingControl();
		// scale.scale(vv, (float) 0.8, new Point2D.Double(1366/2, 768/2));
		// vv.scaleToLayout(scale);

		jf.getContentPane().add(vv);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);
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
			Integer weight = scan.nextInt();

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
