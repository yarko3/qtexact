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
	
	
	/**
	 * find the average distance between points in graph, given a lat long map
	 * @param g
	 * @param mapping 
	 * @return
	 */
	public double meanNeighbourDistance(Graph<V, Pair<V>> g, HashMap<V, Pair<Double>> mapping)
	{
		
		GeodeticCalculator geoCalc = new GeodeticCalculator();

		Ellipsoid reference = Ellipsoid.WGS84;  

		
		int count = 0;
		double temp = 0;
		
		for (V v0 : g.getVertices())
		{
			if (!mapping.containsKey(v0))
				continue;
			
			for (V v1 : g.getNeighbors(v0))
			{
				if (v0.equals(v1) || !mapping.containsKey(v1))
					continue;
				
				GlobalPosition pointA = new GlobalPosition(mapping.get(v0).getFirst(), mapping.get(v0).getSecond(), 0.0); // Point A

				GlobalPosition pointB = new GlobalPosition(mapping.get(v1).getFirst(), mapping.get(v1).getSecond(), 0.0); // Point B

				double distance = geoCalc.calculateGeodeticCurve(reference, pointA, pointB).getEllipsoidalDistance();
				
				temp += distance;
				count++;
				
			}
		}
		
		temp /= 2;
		count /= 2;
		
		return (temp / count) / 1000; 
	}
	
	
	public double meanDistance(Graph<V, Pair<V>> g, HashMap<V, Pair<Double>> mapping)
	{
		
		GeodeticCalculator geoCalc = new GeodeticCalculator();

		Ellipsoid reference = Ellipsoid.WGS84;  

		
		int count = 0;
		double temp = 0;
		
		for (V v0 : g.getVertices())
		{
			if (!mapping.containsKey(v0))
				continue;
			
			for (V v1 : g.getVertices())
			{
				if (v0.equals(v1) || !mapping.containsKey(v1))
					continue;
				
				GlobalPosition pointA = new GlobalPosition(mapping.get(v0).getFirst(), mapping.get(v0).getSecond(), 0.0); // Point A

				GlobalPosition pointB = new GlobalPosition(mapping.get(v1).getFirst(), mapping.get(v1).getSecond(), 0.0); // Point B

				double distance = geoCalc.calculateGeodeticCurve(reference, pointA, pointB).getEllipsoidalDistance();
				
				temp += distance;
				count++;
				
			}
		}
		
		temp /= 2;
		count /= 2;
		
		return (temp / count) / 1000; 
	}
	
	public double meanDistance(Set<V> community, HashMap<V, Pair<Double>> mapping)
	{
		
		GeodeticCalculator geoCalc = new GeodeticCalculator();

		Ellipsoid reference = Ellipsoid.WGS84;  

		
		int count = 0;
		double temp = 0;
		
		for (V v0 : community)
		{
			if (!mapping.containsKey(v0))
				continue;
			
			for (V v1 : community)
			{
				if (v0.equals(v1) || !mapping.containsKey(v1))
					continue;
				
				GlobalPosition pointA = new GlobalPosition(mapping.get(v0).getFirst(), mapping.get(v0).getSecond(), 0.0); // Point A

				GlobalPosition pointB = new GlobalPosition(mapping.get(v1).getFirst(), mapping.get(v1).getSecond(), 0.0); // Point B

				double distance = geoCalc.calculateGeodeticCurve(reference, pointA, pointB).getEllipsoidalDistance();
				
				temp += distance;
				count++;
				
			}
		}
		
		temp /= 2;
		count /= 2;
		
		return (temp / count) / 1000; 
	}
	
	public double medianNeighbourDistance(Graph<V, Pair<V>> g, HashMap<V, Pair<Double>> mapping)
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
					continue;
				
				GlobalPosition pointA = new GlobalPosition(mapping.get(v0).getFirst(), mapping.get(v0).getSecond(), 0.0); // Point A

				GlobalPosition pointB = new GlobalPosition(mapping.get(v1).getFirst(), mapping.get(v1).getSecond(), 0.0); // Point B

				double distance = geoCalc.calculateGeodeticCurve(reference, pointA, pointB).getEllipsoidalDistance();
				
				temp.add(distance);
				
				
			}
		}
		
		Collections.sort(temp);
		
		if (temp.isEmpty())
			return Double.NaN;
		
		
		return temp.get(temp.size()/2) / 1000; 
	}
	
	public double medianDistance(Set<V> community, HashMap<V, Pair<Double>> mapping)
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
					continue;
				
				GlobalPosition pointA = new GlobalPosition(mapping.get(v0).getFirst(), mapping.get(v0).getSecond(), 0.0); // Point A

				GlobalPosition pointB = new GlobalPosition(mapping.get(v1).getFirst(), mapping.get(v1).getSecond(), 0.0); // Point B

				double distance = geoCalc.calculateGeodeticCurve(reference, pointA, pointB).getEllipsoidalDistance();
				
				temp.add(distance);
				
				
			}
		}
		
		Collections.sort(temp);
		
		if (temp.isEmpty())
			return Double.NaN;
		
		
		return temp.get(temp.size()/2) / 1000; 
	}
	
	public double medianDistance(Graph<V, Pair<V>> g, HashMap<V, Pair<Double>> mapping)
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
					continue;
				
				GlobalPosition pointA = new GlobalPosition(mapping.get(v0).getFirst(), mapping.get(v0).getSecond(), 0.0); // Point A

				GlobalPosition pointB = new GlobalPosition(mapping.get(v1).getFirst(), mapping.get(v1).getSecond(), 0.0); // Point B

				double distance = geoCalc.calculateGeodeticCurve(reference, pointA, pointB).getEllipsoidalDistance();
				
				temp.add(distance);
				
				
			}
		}
		
		Collections.sort(temp);
		if (temp.isEmpty())
			return Double.NaN;
		
		return temp.get(temp.size()/2) / 1000; 
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
		writer.println("Mean winery-winery distance: \t" + d.meanDistance(g, mapping));
		writer.println("Median winery-winery distance: \t" + d.medianDistance(g, mapping));
		writer.println("Mean edge distance: \t" + d.meanNeighbourDistance(g, mapping));
		writer.println("Median edge distance: \t" + d.medianNeighbourDistance(g, mapping));
		
		
		
		HashMap<Integer, Graph<String, Pair<String>>> components = new HashMap<Integer, Graph<String, Pair<String>>>();
		
		for (int cID : f.communityMap.keySet())
		{
			components.put(cID, stringUtils.inducedFromVertexSet(g, f.communityMap.get(cID)));
		}
	
		
		for (int cID : components.keySet())
		{
			
			
			writer.println("\n\nComponent " + cID + "\tNumber of nodes: " + components.get(cID).getVertexCount());
			
			//output wineries in this community
			for (String winery : components.get(cID).getVertices())
			{
				writer.print(winery + "\t");
			}
			
			writer.println("\nMean winery-winery distance: \t" + d.meanDistance(components.get(cID), mapping));
			writer.println("Median winery-winery distance: \t" + d.medianDistance(components.get(cID), mapping));
			writer.println("Mean edge distance: \t" + d.meanNeighbourDistance(components.get(cID), mapping));
			writer.println("Median edge distance: \t" + d.medianNeighbourDistance(components.get(cID), mapping));
			
		}
		
		writer.close();
	}
	

}
