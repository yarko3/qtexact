package utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalPosition;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class distance<V> 
{
	
	static graphUtils<String> utils = new graphUtils<String>();
	
	public static double distanceBetween(String v0, String v1, HashMap<String, Pair<Double>> mapping)
	{
		GeodeticCalculator geoCalc = new GeodeticCalculator();

		Ellipsoid reference = Ellipsoid.WGS84;  
		
		GlobalPosition pointA = new GlobalPosition(mapping.get(v0).getFirst(), mapping.get(v0).getSecond(), 0.0); // Point A

		
		GlobalPosition pointB = new GlobalPosition(mapping.get(v1).getFirst(), mapping.get(v1).getSecond(), 0.0); // Point B

		return geoCalc.calculateGeodeticCurve(reference, pointA, pointB).getEllipsoidalDistance() / 1000;
		
	}
	
	
	/**
	 * find the average distance between points in graph, given a lat long map
	 * @param g
	 * @param mapping 
	 * @return
	 */
	public double[] meanNeighbourDistance(Graph<V, Pair<V>> g, HashMap<V, Pair<Double>> mapping)
	{
		
		GeodeticCalculator geoCalc = new GeodeticCalculator();

		Ellipsoid reference = Ellipsoid.WGS84;  

		List<Double> distances = new LinkedList<Double>();
		
		int count = 0;
		double temp = 0;
		
		for (V v0 : g.getVertices())
		{
			if (!mapping.containsKey(v0))
			{
				System.out.println("Winery not found in distance file: " + v0);
				throw new NullPointerException();
			}
			
			for (V v1 : g.getNeighbors(v0))
			{
				if (v0.equals(v1) || !mapping.containsKey(v1))
				{
					if (!mapping.containsKey(v1))
					{
						System.out.println("Winery not found in distance file: " + v1);
						throw new NullPointerException();
					}
					continue;
				}
				
				GlobalPosition pointA = new GlobalPosition(mapping.get(v0).getFirst(), mapping.get(v0).getSecond(), 0.0); // Point A

				GlobalPosition pointB = new GlobalPosition(mapping.get(v1).getFirst(), mapping.get(v1).getSecond(), 0.0); // Point B

				double distance = geoCalc.calculateGeodeticCurve(reference, pointA, pointB).getEllipsoidalDistance();
				
				temp += distance;
				count++;
				
				distances.add(distance / 1000);
				
			}
		}
		
		temp /= 2;
		count /= 2;
		
		double mean = (temp / count) / 1000; 
		
		return standardDeviation(distances, mean);
	}
	
	
	public double[] meanDistance(Graph<V, Pair<V>> g, HashMap<V, Pair<Double>> mapping)
	{
		
		GeodeticCalculator geoCalc = new GeodeticCalculator();

		Ellipsoid reference = Ellipsoid.WGS84;  

		LinkedList<Double> distances = new LinkedList<Double>();
		
		int count = 0;
		double temp = 0;
		
		for (V v0 : g.getVertices())
		{
			if (!mapping.containsKey(v0))
			{
				System.out.println("Winery not found in distance file: " + v0);
				throw new NullPointerException();
			}
			
			for (V v1 : g.getVertices())
			{
				if (v0.equals(v1) || !mapping.containsKey(v1))
				{
					if (!mapping.containsKey(v1))
					{
						System.out.println("Winery not found in distance file: " + v1);
						throw new NullPointerException();
					}
					continue;
				}
				GlobalPosition pointA = new GlobalPosition(mapping.get(v0).getFirst(), mapping.get(v0).getSecond(), 0.0); // Point A

				GlobalPosition pointB = new GlobalPosition(mapping.get(v1).getFirst(), mapping.get(v1).getSecond(), 0.0); // Point B

				double distance = geoCalc.calculateGeodeticCurve(reference, pointA, pointB).getEllipsoidalDistance();
				
				temp += distance;
				
				//add to list for standard deviation later
				distances.add(distance / 1000);
				count++;
				
			}
		}
		
		temp /= 2;
		count /= 2;
		
		double mean = (temp / count) / 1000; 
		
		return standardDeviation(distances, mean);
		
	}
	
	private double[] standardDeviation(List<Double> distances, double mean)
	{
		//calculate standard deviation
		double sum = 0;
		for (Double d : distances)
		{
			sum += (mean - d) * (mean - d); 
		}
		
		sum = sum / distances.size();
		
		double sd = Math.sqrt(sum);
		
		double[] rtn = new double[2];
		rtn[0] = mean;
		rtn[1] = sd;
		return rtn;
	}
	
	public double[] meanDistance(Set<V> community, HashMap<V, Pair<Double>> mapping)
	{
		
		GeodeticCalculator geoCalc = new GeodeticCalculator();

		Ellipsoid reference = Ellipsoid.WGS84;  

		LinkedList<Double> distances = new LinkedList<Double>();
		
		int count = 0;
		double temp = 0;
		
		for (V v0 : community)
		{
			if (!mapping.containsKey(v0))
			{
				System.out.println("Winery not found in distance file: " + v0);
				throw new NullPointerException();
			}
			
			for (V v1 : community)
			{
				if (v0.equals(v1) || !mapping.containsKey(v1))
					continue;
				
				GlobalPosition pointA = new GlobalPosition(mapping.get(v0).getFirst(), mapping.get(v0).getSecond(), 0.0); // Point A

				GlobalPosition pointB = new GlobalPosition(mapping.get(v1).getFirst(), mapping.get(v1).getSecond(), 0.0); // Point B

				double distance = geoCalc.calculateGeodeticCurve(reference, pointA, pointB).getEllipsoidalDistance();
				
				temp += distance;
				count++;
				
				distances.add(distance / 1000);
			}
		}
		
		temp /= 2;
		count /= 2;
		
		double mean = (temp / count) / 1000;
		
		return standardDeviation(distances, mean);
	}
	
	public double[] medianNeighbourDistance(Graph<V, Pair<V>> g, HashMap<V, Pair<Double>> mapping)
	{
		
		GeodeticCalculator geoCalc = new GeodeticCalculator();

		Ellipsoid reference = Ellipsoid.WGS84;  

		
		ArrayList<Double> temp = new ArrayList<Double>();
		
		for (V v0 : g.getVertices())
		{
			if (!mapping.containsKey(v0))
				continue;
			
			for (V v1 : g.getNeighbors(v0))
			{
				if (v0.equals(v1) || !mapping.containsKey(v1))
				{
					if (!mapping.containsKey(v1))
					{
						System.out.println("Winery not found in distance file: " + v1);
						throw new NullPointerException();
					}
					continue;
				}
				
				GlobalPosition pointA = new GlobalPosition(mapping.get(v0).getFirst(), mapping.get(v0).getSecond(), 0.0); // Point A

				GlobalPosition pointB = new GlobalPosition(mapping.get(v1).getFirst(), mapping.get(v1).getSecond(), 0.0); // Point B

				double distance = geoCalc.calculateGeodeticCurve(reference, pointA, pointB).getEllipsoidalDistance();
				
				temp.add(distance / 1000);
				
				
			}
		}
		
		Collections.sort(temp);
		
		
		double median =  temp.get(temp.size()/2); 
		
		return standardDeviation(temp, median);
	}
	
	public double[] medianDistance(Set<V> community, HashMap<V, Pair<Double>> mapping)
	{
		
		GeodeticCalculator geoCalc = new GeodeticCalculator();

		Ellipsoid reference = Ellipsoid.WGS84;  

		
		ArrayList<Double> temp = new ArrayList<Double>();
		
		for (V v0 : community)
		{
			if (!mapping.containsKey(v0))
				continue;
			
			for (V v1 : community)
			{
				if (v0.equals(v1) || !mapping.containsKey(v1))
				{
					if (!mapping.containsKey(v1))
					{
						System.out.println("Winery not found in distance file: " + v1);
						throw new NullPointerException();
					}
					continue;
				}
				
				GlobalPosition pointA = new GlobalPosition(mapping.get(v0).getFirst(), mapping.get(v0).getSecond(), 0.0); // Point A

				GlobalPosition pointB = new GlobalPosition(mapping.get(v1).getFirst(), mapping.get(v1).getSecond(), 0.0); // Point B

				double distance = geoCalc.calculateGeodeticCurve(reference, pointA, pointB).getEllipsoidalDistance();
				
				temp.add(distance / 1000);
				
				
			}
		}
		
		Collections.sort(temp);
		
		
		double median = temp.get(temp.size()/2);
		
		return standardDeviation(temp, median);
	}
	
	public double[] medianDistance(Graph<V, Pair<V>> g, HashMap<V, Pair<Double>> mapping)
	{
		
		GeodeticCalculator geoCalc = new GeodeticCalculator();

		Ellipsoid reference = Ellipsoid.WGS84;  

		
		ArrayList<Double> temp = new ArrayList<Double>();
		
		for (V v0 : g.getVertices())
		{
			if (!mapping.containsKey(v0))
				continue;
			
			for (V v1 : g.getVertices())
			{
				if (v0.equals(v1) || !mapping.containsKey(v1))
				{
					if (!mapping.containsKey(v1))
					{
						System.out.println("Winery not found in distance file: " + v1);
						throw new NullPointerException();
					}
					continue;
				}
				
				GlobalPosition pointA = new GlobalPosition(mapping.get(v0).getFirst(), mapping.get(v0).getSecond(), 0.0); // Point A

				GlobalPosition pointB = new GlobalPosition(mapping.get(v1).getFirst(), mapping.get(v1).getSecond(), 0.0); // Point B

				double distance = geoCalc.calculateGeodeticCurve(reference, pointA, pointB).getEllipsoidalDistance();
				
				temp.add(distance / 1000);
				
				
			}
		}
		
		Collections.sort(temp);
		
		double median = temp.get(temp.size()/2);
		
		return standardDeviation(temp, median);
	}
	
	public static HashMap<String, Pair<Double>> getLatLongFromFile(String filename)
	{
		HashMap<String, Pair<Double>> mapping = new HashMap<String, Pair<Double>>();
		FileReader file = null;
		try {
			file = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}

		Scanner scan = new Scanner(file);

		while (scan.hasNext()) {
			
			String v = scan.next();
			
			Double lat = scan.nextDouble();
			Double lo = scan.nextDouble();
			
			mapping.put(v, new Pair<Double>(lat, lo));
			
			
		}
		
		scan.close();
		
		return mapping;
	}
	
	public static void provinceSpecificExternalsEdgeList(String inputFile, String inputAddress, String province, String outputFile)
	{
		Graph<String, Pair<String>> original = graphUtils.graphFromFile(inputFile);
		
		HashMap<String, String> addressMap = getAddressMapFromFile(inputAddress);
		
		
		Graph<String, Pair<String>> rtn = new UndirectedSparseGraph<String, Pair<String>>();
		
		for (Pair<String> edge : original.getEdges())
		{
			//if edge target found in province, add edge
			if (addressMap.containsKey(edge.getSecond()) && addressMap.get(edge.getSecond()).contains(", " + province))
			{
				rtn.addEdge(new Pair<String>(edge.getFirst(), edge.getSecond()), edge.getFirst(), edge.getSecond());
			}
		}
		
		utils.printEdgeSet(rtn, outputFile);
		
	}
	
	private static HashMap<String, String> getAddressMapFromFile(String filename)
	{
		HashMap<String, String> mapping = new HashMap<String, String>();
		
		FileReader file = null;
		try {
			file = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}

		Scanner scan = new Scanner(file).useDelimiter("\\t|\\r|\\n");

		while (scan.hasNext()) {
			
			String v = scan.next();
			
			if (!v.contains("www"))
			{
				System.out.println("address import breaks at " + v);
			}
			//name
			scan.next();
			//address
			String address = scan.next();
			
			//scan through the rest of line
			String temp = scan.next();
			temp = scan.next();
			temp = scan.next();
			if (scan.hasNext())
				temp = scan.next();
			
			mapping.put(v, address);
			
			
		}
		
		try {
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		scan.close();
		
		return mapping;
	}
	
	
	/**
	 * NOT FINISHED
	 * get a mapping of our categories for externals
	 * @param filename
	 * @return mapping
	 */
	public HashMap<String, String> getCategoriesFromFile(String filename)
	{
		
		HashMap<String, String> mapping = new HashMap<String, String>();
		
		FileReader file = null;
		try {
			file = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}

		Scanner scan = new Scanner(file).useDelimiter("\\t|\\r|\\n");

		while (scan.hasNext()) {
			
			String v = scan.next();
			
			if (!v.contains("www"))
			{
				System.out.println("category import breaks at " + v);
			}
			//name
			scan.next();
			//address
			scan.next();
			
			//scan through the rest of line
			String cats = scan.next();
			scan.next();
			scan.next();
			if (scan.hasNext())
				scan.next();
			
			mapping.put(v, cats);
			
			
		}
		
		try {
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		scan.close();
		
		
		
		
		
		//map our categories
		for (String key : mapping.keySet())
		{
			String old = mapping.get(key);
			
			String[] oldCats = old.split(", ");
			
			Set<String> list = new HashSet<String>();
			Collections.addAll(list, oldCats);
			
			
			if (list.contains("liquor_store"))
				mapping.put(key, "Liquor Store");
			else if (list.contains("travel_agency"))
				mapping.put(key, "Travel Agency");
			
			
			
		}
		
		return mapping;
	}
	
	public static void outputDistanceMeasurements(String editFile, String distanceFile, String outputFile)
	{
		
		graphFromEdgeSetWithCommunities f = new graphFromEdgeSetWithCommunities(editFile);
		
		
		distance<String> d = new distance<String>();
		graphUtils<String> stringUtils = new graphUtils<String>();
		
		
		HashMap<String, Pair<Double>> mapping = distance.getLatLongFromFile(distanceFile);
		
		
		Graph<String, Pair<String>> g = f.g;
		
		
		//print network to file
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(outputFile, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		writer.println("Overall: ");
		writer.println("Mean winery-winery distance: \t" + d.meanDistance(g, mapping)[0] + ", " +d.meanDistance(g, mapping)[1]);
		writer.println("Median winery-winery distance: \t" + d.medianDistance(g, mapping)[0] + ", " + d.medianDistance(g, mapping)[1]);
		writer.println("Mean edge distance: \t" + d.meanNeighbourDistance(g, mapping)[0] + ", " + d.meanNeighbourDistance(g, mapping)[1]);
		writer.println("Median edge distance: \t" + d.medianNeighbourDistance(g, mapping)[0] + ", " + d.medianNeighbourDistance(g, mapping)[1]);
		
		
		
		HashMap<Integer, Graph<String, Pair<String>>> components = new HashMap<Integer, Graph<String, Pair<String>>>();
		
		for (int cID : f.communityMap.keySet())
		{
			components.put(cID, stringUtils.inducedFromVertexSet(g, f.communityMap.get(cID)));
		}
	
		writer.println("Mean community distance: \t" + d.meanCommunityDistance(components, mapping)[0] + ", " + d.meanCommunityDistance(components, mapping)[1]);
		writer.println("Median community distance: \t" + d.medianCommunityDistance(components, mapping)[0] + ", " + d.medianCommunityDistance(components, mapping)[1]);
		
		
		for (int cID : components.keySet())
		{
			
			
			writer.println("\n\nComponent " + cID + "\tNumber of nodes: " + components.get(cID).getVertexCount());
			
			//output wineries in this community
			for (String winery : components.get(cID).getVertices())
			{
				writer.print(winery + "\t");
			}
			writer.println();
			writer.println("\n\nMean winery-winery distance: \t" + d.meanDistance(components.get(cID), mapping)[0] + ", " + d.meanDistance(components.get(cID), mapping)[1]);
			writer.println("Median winery-winery distance: \t" + d.medianDistance(components.get(cID), mapping)[0] + ", " + d.medianDistance(components.get(cID), mapping)[1]);
			writer.println("Mean edge distance: \t" + d.meanNeighbourDistance(components.get(cID), mapping)[0] + ", " + d.meanNeighbourDistance(components.get(cID), mapping)[1]);
			writer.println("Median edge distance: \t" + d.medianNeighbourDistance(components.get(cID), mapping)[0] + ", " + d.medianNeighbourDistance(components.get(cID), mapping)[1]);
			
			writer.println();
		}
		
		writer.close();
	}
	/**
	 * Get the mean of all community member distances
	 * @param hashmap
	 * @param mapping
	 * @return
	 */
	private double[] meanCommunityDistance(HashMap<Integer, Graph<String, Pair<String>>> components, HashMap<String, Pair<Double>> mapping)
	{
		GeodeticCalculator geoCalc = new GeodeticCalculator();

		Ellipsoid reference = Ellipsoid.WGS84;  
		
		double sum = 0;
		int count = 0;
		
		LinkedList<Double> distances = new LinkedList<Double>();
		
		for (int cID : components.keySet())
		{
			ArrayList<String> vertices = new ArrayList<String>();
			vertices.addAll(components.get(cID).getVertices());
			
			for (int i = 0; i < vertices.size(); i++)
			{
				for (int j = i+1; j < vertices.size(); j++)
				{
					GlobalPosition pointA = new GlobalPosition(mapping.get(vertices.get(i)).getFirst(), mapping.get(vertices.get(i)).getSecond(), 0.0); // Point A

					GlobalPosition pointB = new GlobalPosition(mapping.get(vertices.get(j)).getFirst(), mapping.get(vertices.get(j)).getSecond(), 0.0); // Point B

					double distance = geoCalc.calculateGeodeticCurve(reference, pointA, pointB).getEllipsoidalDistance();
					
					sum += distance;
					count++;
					
					distances.add(distance / 1000);
				}
			}
		}
		
		
		double mean = (sum/count);
		
		return standardDeviation(distances, mean);
	}
	
	private double[] medianCommunityDistance(HashMap<Integer, Graph<String, Pair<String>>> components, HashMap<String, Pair<Double>> mapping)
	{
		GeodeticCalculator geoCalc = new GeodeticCalculator();

		Ellipsoid reference = Ellipsoid.WGS84;  
		
		ArrayList<Double> list = new ArrayList<Double>();
		
		for (int cID : components.keySet())
		{
			ArrayList<String> vertices = new ArrayList<String>();
			vertices.addAll(components.get(cID).getVertices());
			
			for (int i = 0; i < vertices.size(); i++)
			{
				for (int j = i+1; j < vertices.size(); j++)
				{
					GlobalPosition pointA = new GlobalPosition(mapping.get(vertices.get(i)).getFirst(), mapping.get(vertices.get(i)).getSecond(), 0.0); // Point A

					GlobalPosition pointB = new GlobalPosition(mapping.get(vertices.get(j)).getFirst(), mapping.get(vertices.get(j)).getSecond(), 0.0); // Point B

					double distance = geoCalc.calculateGeodeticCurve(reference, pointA, pointB).getEllipsoidalDistance();
					
					list.add(distance / 1000);
				}
			}
		}
		
		Collections.sort(list);
		double median = (list.get(list.size()/2));
		
		return standardDeviation(list, median);
	}
	
	public static void outputAllDistances(Graph<String, Pair<String>> graph, String outputFile, String distanceFile) throws FileNotFoundException, UnsupportedEncodingException
	{
		PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
		GeodeticCalculator geoCalc = new GeodeticCalculator();
		Ellipsoid reference = Ellipsoid.WGS84;  
		
		HashMap<String, Pair<Double>> mapping = distance.getLatLongFromFile(distanceFile);
		
		writer.println("Edge Distances: (Distance v1 v2)");
		//for every edge, output distance measure
		for (Pair<String> edge : graph.getEdges())
		{
			GlobalPosition pointA = new GlobalPosition(mapping.get(edge.getFirst()).getFirst(), mapping.get(edge.getFirst()).getSecond(), 0.0); // Point A

			GlobalPosition pointB = new GlobalPosition(mapping.get(edge.getSecond()).getFirst(), mapping.get(edge.getSecond()).getSecond(), 0.0); // Point B

			double distance = geoCalc.calculateGeodeticCurve(reference, pointA, pointB).getEllipsoidalDistance() / 1000;
			
			writer.println(distance + " " + edge.getFirst() + " " + edge.getSecond());
		}
		
		
		writer.println("Pair Distances: (Distance v1 v2)");
		//for every edge, output distance measure
		ArrayList<String> vertices = new ArrayList<String>();
		vertices.addAll(graph.getVertices());
		
		for (int i = 0; i < vertices.size(); i++)
		{
			for (int j = i+1; j < vertices.size(); j++)
			{
				GlobalPosition pointA = new GlobalPosition(mapping.get(vertices.get(i)).getFirst(), mapping.get(vertices.get(i)).getSecond(), 0.0); // Point A

				GlobalPosition pointB = new GlobalPosition(mapping.get(vertices.get(j)).getFirst(), mapping.get(vertices.get(j)).getSecond(), 0.0); // Point B

				double distance = geoCalc.calculateGeodeticCurve(reference, pointA, pointB).getEllipsoidalDistance() / 1000;
				
				
				writer.println(distance + " " + vertices.get(i) + " " + vertices.get(j));
			}
		}
		
		writer.close();
	}
	

}
