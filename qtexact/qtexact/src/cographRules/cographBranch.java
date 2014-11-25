package cographRules;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import search.cographSearch;
import abstractClasses.Branch;
import abstractClasses.Certificate;
import abstractClasses.SearchResult;
import controller.Controller;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class cographBranch<V> extends Branch<V> 
{

	public cographBranch(Controller<V> controller) {
		super(controller);

		//set cograph search
		search = new cographSearch<V>();
		
		//output traversal percentages
		output = controller.getOutputFlag();
		
		//graph editing does not care for directionality
		directed = false;
	}

	@Override
	public branchingReturnC<V> setup(Graph<V, Pair<V>> G, int bound) {
		//start with a full minMoves
		branchingReturnC<V> minMoves = new branchingReturnC<V>(G);
		
		minMoves.setChanges(fillMinMoves(minMoves, bound));
		minMoves.setMinMoves(minMoves);
		branchingReturnC<V> goal = new branchingReturnC<V>(G, minMoves);
		
		//output flags
		if (output)
		{
			goal.setPercent(1);
			//controller.setGlobalPercent(0);
		}
				
		return goal;
	}

	@Override
	public branchingReturnC<V> setup(Graph<V, Pair<V>> G) {
		return setup(G, 0);
	}

	@Override
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s,
			SearchResult<V> sResult) {

		
		List<V> obst = sResult.getCertificate().getVertices();
		double oldPercent = s.getPercent();
		
		//a P4 was found
		if (sResult.getCertificate().getFlag() == -2)
		{
			int ruleCount = 6;
			
			// one delete
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(obst.get(0), obst.get(1)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				controller.branch(deleteResult(s, obst.get(0), obst.get(1)));
				//revert changes
				revert(s);		
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			// one delete
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(obst.get(1), obst.get(2)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				controller.branch(deleteResult(s, obst.get(1), obst.get(2)));
				//revert changes
				revert(s);		
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			// one delete
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(obst.get(2), obst.get(3)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				controller.branch(deleteResult(s, obst.get(2), obst.get(3)));
				//revert changes
				revert(s);		
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			// one add
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(obst.get(0), obst.get(2)), false, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				controller.branch(addResult(s, obst.get(0), obst.get(2)));
				//revert changes
				revert(s);		
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			// one add
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(obst.get(1), obst.get(3)), false, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				controller.branch(addResult(s, obst.get(1), obst.get(3)));
				//revert changes
				revert(s);		
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);
			
			// one add
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(obst.get(0), obst.get(3)), false, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				controller.branch(addResult(s, obst.get(0), obst.get(3)));
				//revert changes
				revert(s);		
				if (output)
				{
					//revert percent
					s.setPercent(oldPercent);
				}
			}
			else
				if (output)
					controller.setGlobalPercent(controller.getGlobalPercent() + oldPercent / ruleCount);

		}
		else throw new NullPointerException();
		
		
		
		return s;

		
	}

	@Override
	public branchingReturnC<V> deleteResult(branchingReturnC<V> s, V v0, V v1) {
		
		Pair<V> edge = s.getG().findEdge(v0, v1);
		
		if (edge != null)
		{
			//update moves made
			s.getChanges().add(new myEdge<V>(new Pair<V>(v0, v1), false, directed));
			
			s.getG().removeEdge(edge);
			return s;
		}
		else
		{
			System.out.println("Tried to delete edge " + v0 + " and " + v1 + ". Edge does not exist.");
			throw new NullPointerException();
		}
	}

	@Override
	public branchingReturnC<V> addResult(branchingReturnC<V> s, V v0, V v1) {
		if (s.getG().isNeighbor(v0, v1))
		{
			System.out.println("Tried to add edge " + v0 + " " + v1 + ". Edge already exists. ");
			throw new NullPointerException();
		}
		Pair<V> edge = new Pair<V>(v0, v1);
		
		//update moves made
		s.getChanges().add(new myEdge<V>(new Pair<V>(v0, v1), true, directed));
		
		s.getG().addEdge(edge, v0, v1);
		return s;
	}

	@Override
	public branchingReturnC<V> removeVertex(branchingReturnC<V> s, V v0) {
		s.getG().removeVertex(v0);
		return s;
	}

	@Override
	public void revertEdgeDelete(branchingReturnC<V> s, V v0, V v1) {
		s.getG().addEdge(new Pair<V>(v0, v1), v0, v1);
		
	}

	@Override
	public void revertEdgeAdd(branchingReturnC<V> s, V v0, V v1) {
		if (!s.getG().removeEdge(s.getG().findEdge(v0, v1)))
		{
			System.out.println("Tried to remove edge " + v0 + " " + v1);
		}
		
	}
	
	
	/**
	 * find forbidden structures
	 * 
	 * Flag index:
	 * -1 C4
	 * -2 P4
	 * -3 4 pan
	 * -4 house
	 * -5 P5
	 * -6 fork
	 * -7 3 pan (extra node on handle)
	 * -8 C5
	 * -9 kite
	 * 
	 * @param s edit state
	 * @param searchResult initial search results
	 * @return updated search results
	 */
	public SearchResult<V> findStructures(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		Certificate<V> obstruction = searchResult.getCertificate();
		List<V> vertices = obstruction.getVertices();
		
		HashMap<V, Integer> hash = new HashMap<V, Integer>();
		
		//add one for every common neighbour into hash
		for (V v : vertices)
		{
			for (V n : s.getG().getNeighbors(v))
			{
				int entry = 1;
				if (hash.containsKey(n))
					entry = hash.get(n) + 1;
				hash.put(n, entry);
			}
		}
		
		//remove vertices of structure from hash
		for (V v : vertices)
			hash.remove(v);
		
		

		for (V n : hash.keySet())
		{
			//number of neighbours of n in obstruction
			int nVal = hash.get(n);
			
		
			if (nVal == 1)
			{
				//either a P5 or a fork has been found
				if (s.getG().isNeighbor(vertices.get(0), n) || s.getG().isNeighbor(vertices.get(3), n))
				{
					//a P5 has been found
					if (s.getG().isNeighbor(vertices.get(0), n))
						vertices.add(0, n);
					else
						vertices.add(n);
					obstruction.setFlag(-5);
					
					return searchResult;
				}
				else
				{
					//a fork has been found
					
					if (s.getG().isNeighbor(vertices.get(2), n))
						vertices.add(n);
					else
					{
						//reverse order of P4
						Collections.reverse(vertices);
						vertices.add(n);
					}
					obstruction.setFlag(-6);
					
					return searchResult;
				}
			}
			else if (nVal == 2)
			{
				//look for 4 pan
				if ((s.getG().isNeighbor(vertices.get(0), n) && s.getG().isNeighbor(vertices.get(2), n)) ||
						(s.getG().isNeighbor(vertices.get(1), n) && s.getG().isNeighbor(vertices.get(3), n)))
				{
					searchResult.setCertificate(construct4Pan(s, searchResult, n));
					return searchResult;
				}
				
				//look for a C5
				if ((s.getG().isNeighbor(vertices.get(0), n) && s.getG().isNeighbor(vertices.get(3), n)))
				{
					vertices.add(n);
					obstruction.setFlag(-8);
					return searchResult;
				}
				
				//look for a 3 pan with extra node on handle
				if ((s.getG().isNeighbor(vertices.get(0), n) && s.getG().isNeighbor(vertices.get(1), n)) ||
						(s.getG().isNeighbor(vertices.get(2), n) && s.getG().isNeighbor(vertices.get(3), n)))
				{
					if (s.getG().isNeighbor(vertices.get(0), n))
					{
						Collections.reverse(vertices);
					}
					vertices.add(n);
					obstruction.setFlag(-7);
					return searchResult;
				}
			}
			else if (nVal == 3)
			{
				//look for a kite
				if (!s.getG().isNeighbor(vertices.get(0), n) || !s.getG().isNeighbor(vertices.get(3), n))
				{
					if (!s.getG().isNeighbor(vertices.get(0), n))
					{
						vertices.add(n);
						
						obstruction.setFlag(-9);
						return searchResult;
					}
					else
					{
						Collections.reverse(vertices);
						vertices.add(n);
						obstruction.setFlag(-9);
						return searchResult;
						
					}
				}
				//a house is found
				else
				{
					if (s.getG().isNeighbor(vertices.get(1), n))
					{
						vertices.add(n);
						obstruction.setFlag(-4);
						return searchResult;
					}
					else
					{
						Collections.reverse(vertices);
						vertices.add(n);
						obstruction.setFlag(-4);
						return searchResult;
					}
				}
				
			}
		}
			

		//no better structures found
		return searchResult;
		
	}
	
	/**
	 * construct a 4-pan 
	 * @param s edit state
	 * @param searchResult initial search result
	 * @param n node for handle of 4-pan
	 * @return certificate
	 */
	private Certificate<V> construct4Pan(branchingReturnC<V> s, SearchResult<V> searchResult, V n)
	{
		Certificate<V> obstruction = searchResult.getCertificate();
		List<V> vertices = obstruction.getVertices();
		
		
		//a pan is found from a P4
		//if n is closer to the front of the obstruction
		if (s.getG().isNeighbor(vertices.get(0), n))
		{
			//reverse obstruction order
			Collections.reverse(obstruction.getVertices());
			
		}
	
		obstruction.getVertices().add(4, n);
		obstruction.setFlag(-3);
		return obstruction;
			
	}
	
	

}
