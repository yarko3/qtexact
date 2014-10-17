package utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class graphFromEdgeSetWithCommunities
{
	public Graph<String, Pair<String>> g;
	public HashMap<Integer, HashSet<String>> communityMap;
	
	public graphFromEdgeSetWithCommunities(String filename)
	{
		g = new DirectedSparseGraph<String, Pair<String>>();
		communityMap = new HashMap<Integer, HashSet<String>>();
		
		FileReader file = null;
		try {
			file = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}

		Scanner scan = new Scanner(file);

		while (scan.hasNext()) {
			
			//source
			String a = scan.next();
			//target
			String b = scan.next();
			
			//weight
			int weight = scan.nextInt();
			
			if (weight == -1)
			{
				scan.next();
				continue;
			}
			
			//community ID
			int cID = scan.nextInt();
			
			g.addEdge(new Pair<String>(a, b), a, b);
			
			//add community mapping
			if (!communityMap.containsKey(cID))
			{
				communityMap.put(cID, new HashSet<String>());
			}
			communityMap.get(cID).add(a);
			communityMap.get(cID).add(b);
			
			
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