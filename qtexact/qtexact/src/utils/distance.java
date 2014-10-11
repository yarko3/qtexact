package utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalPosition;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class distance<V> 
{
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
		
		return temp.get(temp.size()/2) / 1000; 
	}
	
	public HashMap<String, Pair<Double>> getLatLongFromFile(String filename)
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
	
	

}
