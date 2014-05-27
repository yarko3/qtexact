/**
 * Yaroslav Senyuta
 * NSERC USRA Grant (2014)
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import qtUtils.genericLBFS;
import qtUtils.lexReturnC;
import qtUtils.branchingReturnC;
import qtUtils.myEdge;

import com.rits.cloning.Cloner;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.Pair;


public class qtBranching<V>
{
	public genericLBFS<V> search = new genericLBFS<V>();
	
	public static Cloner clone = new Cloner();
	
	/**
	 * Edit a graph by splitting it into connected components with bounding
	 * @param G graph to be edited
	 * @return an edited QT graph
	 */
	public Graph<V, Pair<V>> qtEditConnectedComponents(Graph<V, Pair<V>> G)
	{
		
		//keep proper degree order as an ArrayList<LinkedList<vertex>>
		ArrayList<LinkedList<V>> deg = degSequenceOrder(G);
		
		
		//start with a full minMoves
		branchingReturnC<V> minMoves = new branchingReturnC<V>(G, deg);
		minMoves.setChanges(fillMyEdgeSet(G));
		branchingReturnC<V> goal = new branchingReturnC<V>(G, deg);
		goal.setMinMoves(minMoves);
		
		//branch on G with degree ordering deg
		goal = branchingCC(goal);
		System.out.println("Number of moves: " + goal.getMinMoves().getChanges());
		return goal.getG();
		
	}
	/**
	 * Edit graph with no heuristics other than bound
	 * @param G graph to be edited
	 * @return edited QT graph
	 */
	public Graph<V, Pair<V>> qtEditNoHeuristic(Graph<V, Pair<V>> G)
	{

		//keep proper degree order as an ArrayList<LinkedList<vertex>>
		ArrayList<LinkedList<V>> deg = degSequenceOrder(G);
		
		//start with a full minMoves
		branchingReturnC<V> minMoves = new branchingReturnC<V>(G, deg);
		minMoves.setChanges(fillMyEdgeSet(G));
		branchingReturnC<V> goal = new branchingReturnC<V>(G, deg);
		goal.setMinMoves(minMoves);

		//branch on G with degree ordering deg
		goal = branchingNoHeuristic(goal);
		System.out.println("Number of moves: " + goal.getMinMoves().getChanges());
		return goal.getG();
		
	}
	
	/**
	 * branch without using any heuristic but bounded
	 * @param s contains graph, degree order, current set of edits
	 * @return a modified graph, degree order and set of edits
	 */
	private branchingReturnC<V> branchingNoHeuristic(branchingReturnC<V> s)
	{
		Graph<V, Pair<V>> G = s.getG();
		ArrayList<LinkedList<V>> deg = s.getDeg();
		LinkedList<myEdge<V>> changes = s.getChanges();
		branchingReturnC<V> minMoves = s.getMinMoves();
		
		//check if graph is QT
		ArrayList<V> t = flattenAndReverseDeg(deg);
		lexReturnC<V> lexSearch = search.qtLexBFS(G, t);
		//qt graph has been found
		
		if (lexSearch.isQT())
		{
			branchingReturnC<V> rtn = new branchingReturnC<V>(G, deg, changes, minMoves);
			//update the minMoves list if this solution is better
			if (rtn.getChanges().size() < minMoves.getChanges().size())
			{
				//make a new minMoves to store
				branchingReturnC<V> newMin = new branchingReturnC<V>(G, deg, clone.deepClone(changes));
				newMin.setMinMoves(newMin);
				s.setMinMoves(newMin);
			}
			return s;
		}
			//branch on found P4 or C4
			else
			{	
				//only branch if current minMoves is longer than current state of search
				if (minMoves.getChanges().size() > changes.size())
				{
					branchingReturnC<V> rtn = branchOnNoHeuristic(G, deg, lexSearch, changes, minMoves);
					return rtn;
				}
				//min moves is a better solution
				else
					return new branchingReturnC<V>(G, deg, minMoves.getChanges(), minMoves);
			}
	}
	
	
	/**
	 * branch using connected components
	 * 
	 * @param s current search state
	 * @return a branching state containing the graph, degree order and edited edge set
	 */
	private branchingReturnC<V> branchingCC(branchingReturnC<V> s)
	{
		Graph<V, Pair<V>> G = s.getG();
		ArrayList<LinkedList<V>> deg = s.getDeg();
		LinkedList<myEdge<V>> changes = s.getChanges();
		branchingReturnC<V> minMoves = s.getMinMoves();
		
		
		//check if graph is QT
		ArrayList<V> t = flattenAndReverseDeg(deg);
		
		lexReturnC<V> lexSearch = search.qtLexBFSComponents(G, t);
		
		//qt graph has been found
		if (lexSearch.isQT())
		{
			//update the minMoves list
			if (s.getChanges().size() < minMoves.getChanges().size())
			{
				//make a new minMoves to store
				branchingReturnC<V> newMin = new branchingReturnC<V>(G, deg, clone.deepClone(changes));
				newMin.setMinMoves(newMin);
				s.setMinMoves(newMin);
			}

			return s;
		}
		//branch on found P4 or C4
		else
		{	
			//check if minMoves is a better choice than current state of search
			if (minMoves.getChanges().size() > changes.size())
			{
				branchingReturnC<V> rtn = componentSplit(G, deg, changes, lexSearch, minMoves);
				return rtn;
			}
			//min moves is a better solution
			else
				return new branchingReturnC<V>(G, deg, minMoves.getChanges(), minMoves);
		}
	}
	
	/**
	 * branch on all options available
	 * @param G graph to be modified
	 * @param deg degree order
	 * @param lexResult result of lexBFS search
	 * @param changes changes made 
	 * @return result of most efficient branching
	 */
	private branchingReturnC<V> branchOnNoHeuristic(Graph<V, Pair<V>> G, ArrayList<LinkedList<V>> deg, lexReturnC<V> searchResult, LinkedList<myEdge<V>> changes, branchingReturnC<V> minMoves)
	{
		//C4 has been found
		ArrayList<V> lexResult = searchResult.getCertificate().getVertices();
		if (searchResult.getCertificate().getFlag() == -1)
		{
			
			//result of adding 2 edges to break C4
			//branchingReturnC c4Add1 = branchingCC(c4AddResult(G, deg, changes, lexResult, lexResult.get(0), lexResult.get(2)));
			//branchingReturnC c4Add2 = branchingCC(c4AddResult(G, deg, changes, lexResult, lexResult.get(1), lexResult.get(3)));
			
			
			//results of removing 2 edges to break C4
			branchingReturnC<V> c4Remove0 = branchingNoHeuristic(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2), minMoves));
			
			//revert change to global graph
			c4Delete2Revert(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2), minMoves);
			
			if (c4Remove0.getMinMoves().getChanges().size() < minMoves.getChanges().size())
			{
				minMoves = c4Remove0.getMinMoves();
			}
			
			branchingReturnC<V> c4Remove1 = branchingNoHeuristic(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3), minMoves));
			//revert change to global graph
			c4Delete2Revert(G, deg, changes, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3), minMoves);
			
			if (c4Remove1.getMinMoves().getChanges().size() < minMoves.getChanges().size())
			{
				minMoves = c4Remove1.getMinMoves();
			}
			
			branchingReturnC<V> c4Remove2 = branchingNoHeuristic(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3), minMoves));
			//revert change to global graph
			c4Delete2Revert(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3), minMoves);
			if (c4Remove2.getMinMoves().getChanges().size() < minMoves.getChanges().size())
			{
				minMoves = c4Remove2.getMinMoves();
			}
			
			branchingReturnC<V> c4Remove3 = branchingNoHeuristic(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2), minMoves));
			//revert change to global graph
			c4Delete2Revert(G, deg, changes, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2), minMoves);
			if (c4Remove3.getMinMoves().getChanges().size() < minMoves.getChanges().size())
			{
				minMoves = c4Remove3.getMinMoves();
			}
			
			branchingReturnC<V> c4Remove4 = branchingNoHeuristic(c4Delete2Result(G, deg, changes, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3), minMoves));
			//revert change to global graph
			c4Delete2Revert(G, deg, changes, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3), minMoves);
			if (c4Remove4.getMinMoves().getChanges().size() < minMoves.getChanges().size())
			{
				minMoves = c4Remove4.getMinMoves();
			}
			
			branchingReturnC<V> c4Remove5 = branchingNoHeuristic(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3), minMoves));
			//revert change to global graph
			c4Delete2Revert(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3), minMoves);
			if (c4Remove5.getMinMoves().getChanges().size() < minMoves.getChanges().size())
			{
				minMoves = c4Remove5.getMinMoves();
			}
			
			//add to PriorityQueue to sort
			PriorityQueue<branchingReturnC<V>> pQueue = new PriorityQueue<branchingReturnC<V>>();
//					pQueue.add(c4Add1);
//					pQueue.add(c4Add2);
			pQueue.add(c4Remove0);
			pQueue.add(c4Remove1);
			pQueue.add(c4Remove2);
			pQueue.add(c4Remove3);
			pQueue.add(c4Remove4);
			pQueue.add(c4Remove5);
			
			branchingReturnC<V> r = pQueue.remove();
			
			return r;
			
		}
		//P4 has been found
		else
		{
			branchingReturnC<V> p4Remove0 = branchingNoHeuristic(p4DeleteResult(G, deg, changes, lexResult.get(0), lexResult.get(1), minMoves));
			//revert changes to global graph
			p4DeleteRevert(G, deg, changes,lexResult.get(0), lexResult.get(1), minMoves);
			if (p4Remove0.getMinMoves().getChanges().size() < minMoves.getChanges().size())
			{
				minMoves = p4Remove0.getMinMoves();
			}
			
			branchingReturnC<V> p4Remove1 = branchingNoHeuristic(p4DeleteResult(G, deg, changes, lexResult.get(1), lexResult.get(2), minMoves));
			//revert changes to global graph
			p4DeleteRevert(G, deg, changes,lexResult.get(1), lexResult.get(2), minMoves);
			if (p4Remove1.getMinMoves().getChanges().size() < minMoves.getChanges().size())
			{
				minMoves = p4Remove1.getMinMoves();
			}
			
			branchingReturnC<V> p4Remove2 = branchingNoHeuristic(p4DeleteResult(G, deg, changes, lexResult.get(2), lexResult.get(3), minMoves));
			//revert changes to global graph
			p4DeleteRevert(G, deg, changes,lexResult.get(2), lexResult.get(3), minMoves);
			if (p4Remove2.getMinMoves().getChanges().size() < minMoves.getChanges().size())
			{
				minMoves = p4Remove2.getMinMoves();
			}
			
			//add to PriorityQueue to sort
			PriorityQueue<branchingReturnC<V>> pQueue = new PriorityQueue<branchingReturnC<V>>();
			pQueue.add(p4Remove0);
			pQueue.add(p4Remove1);
			pQueue.add(p4Remove2);
			
			branchingReturnC<V> r = pQueue.remove();
			
			return r;
			
		}
	}
	
	private branchingReturnC<V> branchOnCC(Graph<V, Pair<V>> G, ArrayList<LinkedList<V>> deg, lexReturnC<V> searchResult, LinkedList<myEdge<V>> changes, branchingReturnC<V> minMoves)
	{
		//C4 has been found
		ArrayList<V> lexResult = searchResult.getCertificate().getVertices();
		if (searchResult.getCertificate().getFlag() == -1)
		{
			
			//result of adding 2 edges to break C4
			//branchingReturnC c4Add1 = branchingCC(c4AddResult(G, deg, changes, lexResult, lexResult.get(0), lexResult.get(2)));
			//branchingReturnC c4Add2 = branchingCC(c4AddResult(G, deg, changes, lexResult, lexResult.get(1), lexResult.get(3)));
			
			
			//results of removing 2 edges to break C4
			branchingReturnC<V> c4Remove0 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2), minMoves));
			
			//revert change to global graph
			c4Delete2Revert(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2), minMoves);
			
			if (c4Remove0.getMinMoves().getChanges().size() < minMoves.getChanges().size())
			{
				minMoves = c4Remove0.getMinMoves();
			}
			
			branchingReturnC<V> c4Remove1 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3), minMoves));
			//revert change to global graph
			c4Delete2Revert(G, deg, changes, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3), minMoves);
			
			if (c4Remove1.getMinMoves().getChanges().size() < minMoves.getChanges().size())
			{
				minMoves = c4Remove1.getMinMoves();
			}
			
			branchingReturnC<V> c4Remove2 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3), minMoves));
			//revert change to global graph
			c4Delete2Revert(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3), minMoves);
			if (c4Remove2.getMinMoves().getChanges().size() < minMoves.getChanges().size())
			{
				minMoves = c4Remove2.getMinMoves();
			}
			
			branchingReturnC<V> c4Remove3 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2), minMoves));
			//revert change to global graph
			c4Delete2Revert(G, deg, changes, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2), minMoves);
			if (c4Remove3.getMinMoves().getChanges().size() < minMoves.getChanges().size())
			{
				minMoves = c4Remove3.getMinMoves();
			}
			
			branchingReturnC<V> c4Remove4 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3), minMoves));
			//revert change to global graph
			c4Delete2Revert(G, deg, changes, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3), minMoves);
			if (c4Remove4.getMinMoves().getChanges().size() < minMoves.getChanges().size())
			{
				minMoves = c4Remove4.getMinMoves();
			}
			
			branchingReturnC<V> c4Remove5 = branchingCC(c4Delete2Result(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3), minMoves));
			//revert change to global graph
			c4Delete2Revert(G, deg, changes, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3), minMoves);
			if (c4Remove5.getMinMoves().getChanges().size() < minMoves.getChanges().size())
			{
				minMoves = c4Remove5.getMinMoves();
			}
			
			//add to PriorityQueue to sort
			PriorityQueue<branchingReturnC<V>> pQueue = new PriorityQueue<branchingReturnC<V>>();
//			pQueue.add(c4Add1);
//			pQueue.add(c4Add2);
			pQueue.add(c4Remove0);
			pQueue.add(c4Remove1);
			pQueue.add(c4Remove2);
			pQueue.add(c4Remove3);
			pQueue.add(c4Remove4);
			pQueue.add(c4Remove5);
			
			branchingReturnC<V> r = pQueue.remove();
			
			return r;
			
		}
		//P4 has been found
		else
		{
			branchingReturnC<V> p4Remove0 = branchingCC(p4DeleteResult(G, deg, changes, lexResult.get(0), lexResult.get(1), minMoves));
			//revert changes to global graph
			p4DeleteRevert(G, deg, changes,lexResult.get(0), lexResult.get(1), minMoves);
			if (p4Remove0.getMinMoves().getChanges().size() < minMoves.getChanges().size())
			{
				minMoves = p4Remove0.getMinMoves();
			}
			
			branchingReturnC<V> p4Remove1 = branchingCC(p4DeleteResult(G, deg, changes, lexResult.get(1), lexResult.get(2), minMoves));
			//revert changes to global graph
			p4DeleteRevert(G, deg, changes,lexResult.get(1), lexResult.get(2), minMoves);
			if (p4Remove1.getMinMoves().getChanges().size() < minMoves.getChanges().size())
			{
				minMoves = p4Remove1.getMinMoves();
			}
			
			branchingReturnC<V> p4Remove2 = branchingCC(p4DeleteResult(G, deg, changes, lexResult.get(2), lexResult.get(3), minMoves));
			//revert changes to global graph
			p4DeleteRevert(G, deg, changes,lexResult.get(2), lexResult.get(3), minMoves);
			if (p4Remove2.getMinMoves().getChanges().size() < minMoves.getChanges().size())
			{
				minMoves = p4Remove2.getMinMoves();
			}
			
			//add to PriorityQueue to sort
			PriorityQueue<branchingReturnC<V>> pQueue = new PriorityQueue<branchingReturnC<V>>();
			pQueue.add(p4Remove0);
			pQueue.add(p4Remove1);
			pQueue.add(p4Remove2);
			
			branchingReturnC<V> r = pQueue.remove();
			
			return r;
			
		}
	}
	
	/**
	 * branch on connected components if they are available
	 * @param G
	 * @param deg
	 * @param changes
	 * @param lex
	 * @param minMoves
	 * @return
	 */
	private branchingReturnC<V> componentSplit(Graph<V, Pair<V>> G, ArrayList<LinkedList<V>> deg, LinkedList<myEdge<V>> changes, lexReturnC<V> lex, branchingReturnC<V> minMoves)
	{
//		//make copy of search results, so multiple branches can use the same search
//		lexReturnC<V> lexSearch = clone.deepClone(lex);
		
		lexReturnC<V> lexSearch = lex;
		
		//search yields only one connected component, branch on one component
		if (lexSearch.isConnected())
		{
			return branchOnCC(G, deg,lexSearch, changes, minMoves);
		}
		//multiple connected components exist
		else
		{
			//build graphs from connected components
			Graph<V, Pair<V>> gWtihForbidden = connectedCFromVertexSet(G, lexSearch.getcComponents().removeLast());
			
			LinkedList<Graph<V, Pair<V>>> cGraphs = new LinkedList<Graph<V, Pair<V>>>();
			LinkedList<branchingReturnC<V>> results = new LinkedList<branchingReturnC<V>>();
			for (HashSet<V> l : lexSearch.getcComponents())
			{
				cGraphs.add(connectedCFromVertexSet(G, l));
			}
			//branch on known forbidden structure
			
			//fill new minMoves with entire edge set
			branchingReturnC<V> min = new branchingReturnC<V>(gWtihForbidden, deg);
			min.setChanges(fillMyEdgeSet(gWtihForbidden));
			results.add(branchOnCC(gWtihForbidden, degSequenceOrder(gWtihForbidden), lexSearch, new LinkedList<myEdge<V>>(), min));
			//branch on the rest of the graphs
			for (Graph<V, Pair<V>> g : cGraphs)
			{
				//if component is large enough to care
				if (g.getVertexCount() > 3)
				{
					//fill new minMoves with entire edge set of component
					min = new branchingReturnC<V>(g, deg);
					min.setChanges(fillMyEdgeSet(g));

					results.add(branchingCC(new branchingReturnC<V>(g,degSequenceOrder(g), new LinkedList<myEdge<V>>(), min)));
				}
				//don't care about branching on this but still need it to build up the solution later
				else
				{
					//empty minMoves 
					min = new branchingReturnC<V>(g, deg);
					results.add(new branchingReturnC<V>(g, degSequenceOrder(g), min));
				}
					
					
			}
			
//			//final results return
//			LinkedList<myEdge<V>> rChanges = new LinkedList<myEdge<V>>();
//			HashSet<myEdge<V>> tempChanges = new HashSet<myEdge<V>>();
//			Graph<V, Pair<V>> rGraph = new SparseGraph<V, Pair<V>>();
//			ArrayList<LinkedList<V>> rDeg = new ArrayList<LinkedList<V>>();
//			
//			//construct graph, degree sequence, changes from components
//			graphFromComponentGraphs(rGraph, rDeg, rChanges, tempChanges, results);
//			
//			//keep old moves list
//			rChanges.addAll(changes);
			
			//construct new minMoves from all old ones
			min = new branchingReturnC<V>(G, deg);
			//throw all minMoves into a HashSet, so they don't have duplicates
			HashSet<myEdge<V>> temp = new HashSet<myEdge<V>>();
			for (branchingReturnC<V> r : results)
			{
				temp.addAll(r.getMinMoves().getChanges());
			}
			min.getChanges().addAll(temp);
			min.getChanges().addAll(changes);
			
			branchingReturnC<V> rtn = new branchingReturnC<V>(G, deg, changes, min);
			
			return rtn;
		}
	}
	
	/**
	 * result of adding an edge to break C4
	 * @param G graph
	 * @param deg degree
	 * @param changes changes made to the original graph
	 * @param lexResult result of the lexBFS search for quasi-thresholdness
	 * @param v0 vertex of edge to be added
	 * @param v1 vertex of edge to be added
	 * @return an object storing the new graph, updated degree order, changes
	 */
	private branchingReturnC<V> c4AddResult(Graph<V, Pair<V>> G, ArrayList<LinkedList<V>> deg, LinkedList<myEdge<V>> changes, ArrayList<V> lexResult, V v0, V v1)
	{
		//make copy of graph and degree sequence
		Graph<V, Pair<V>> graphCopy = clone.deepClone(G);
		ArrayList<LinkedList<V>> degCopy = clone.deepClone(deg);
		LinkedList<myEdge<V>> cCopy = clone.deepClone(changes);
		//update degree sequence (first edge)
		addEdge(graphCopy, degCopy, v0, v1);
		
		//add edge to changes 
		cCopy.add(new myEdge<V>(new Pair<V>(v0, v1), true));
		return new branchingReturnC<V>(graphCopy, degCopy, cCopy);
		
	}
	/**
	 * Return the result of deleting two edges from an identified C4
	 * @param G graph
	 * @param deg degree
	 * @param changes changes made to the original graph
	 * @param v0 endpoint of first edge to be deleted
	 * @param v1 endpoint of first edge to be deleted
	 * @param v2 endpoint of second edge to be deleted
	 * @param v3 endpoint of second edge to be deleted
	 * @return graph, degree order and changes after deletion
	 */
	private branchingReturnC<V> c4Delete2Result(Graph<V, Pair<V>> g, ArrayList<LinkedList<V>> deg, LinkedList<myEdge<V>> changes, V v0, V v1, V v2, V v3, branchingReturnC<V> minMoves)
	{
		
		//update degree sequence (first edge)
		removeEdge(g, deg, v0, v1);
		
		//update degree sequence (second edge)
		removeEdge(g, deg, v2, v3);
		
		//add edge deletions to changes
		changes.addLast(new myEdge<V>(new Pair<V>(v0, v1), false));
		changes.addLast(new myEdge<V>(new Pair<V>(v2, v3), false));
		
		return new branchingReturnC<V>(g, deg, changes, minMoves);
		
	}
	
	/**
	 * revert changes made by removing 2 edges from a C4
	 * @param g graph pointer
	 * @param deg degree sequence pointer
	 * @param changes changes log
	 * @param v0
	 * @param v1
	 * @param v2
	 * @param v3
	 * @param minMoves
	 */
	private void c4Delete2Revert(Graph<V, Pair<V>> g,
			ArrayList<LinkedList<V>> deg, LinkedList<myEdge<V>> changes, V v0,
			V v1, V v2, V v3, branchingReturnC<V> minMoves) 
	{
		
		//add edges back in
		addEdge(g, deg, v0, v1);
		addEdge(g, deg, v2, v3);
		
		//revert changes
		changes.removeLast();
		changes.removeLast();
	
	}
	/**
	 * Delete an edge from a P4 and return a branchingReturnC with new graph and degree order
	 * @param G graph
	 * @param deg degree order
	 * @param changes number of changes made to the initial graph
	 * @param v0 endpoint of edge to be deleted
	 * @param v1 endpoint of edge to be deleted
	 * @return
	 */
	private branchingReturnC<V> p4DeleteResult(Graph<V, Pair<V>> g, ArrayList<LinkedList<V>> deg, LinkedList<myEdge<V>> changes, V v0, V v1, branchingReturnC<V> minMoves)
	{
		
		//update degree sequence (first edge)
		removeEdge(g, deg, v0, v1);
		
		changes.addLast(new myEdge<V>(new Pair<V>(v0, v1), false));
		return new branchingReturnC<V>(g, deg, changes, minMoves);
		
	}
	
	private void p4DeleteRevert(Graph<V, Pair<V>> g, ArrayList<LinkedList<V>> deg, LinkedList<myEdge<V>> changes, V v0, V v1, branchingReturnC<V> minMoves)
	{
		//update degree sequence (first edge)
		addEdge(g, deg, v0, v1);
		
		changes.removeLast();
		
		
	}
	
	
	/**
	 * Remove edge between v0 and v1 from graph G and update degree order deg
	 * @param G graph
	 * @param deg degree order
	 * @param v0 endpoint of edge to be deleted
	 * @param v1 endpoint of edge to be deleted
	 */
	private void removeEdge(Graph<V, Pair<V>> G, ArrayList<LinkedList<V>> deg, V v0, V v1)
	{
		int v0Deg = G.degree(v0);
		int v1Deg = G.degree(v1);
		
		deg.get(v0Deg).removeFirstOccurrence(v0);
		
		
		deg.get(v0Deg - 1).add(v0);
		
		if (deg.get(v0Deg).isEmpty() && v0Deg+1 == deg.size())
		{
			deg.remove(v0Deg);
		}
		
		
		deg.get(v1Deg).removeFirstOccurrence(v1);
		
		deg.get(v1Deg - 1).add(v1);
		if (deg.get(v1Deg).isEmpty() && v1Deg+1 == deg.size())
		{
			deg.remove(v1Deg);
		}
		
		
		//find the edge to remove
		if (!G.removeEdge(new Pair<V>(v0, v1)))
			G.removeEdge(new Pair<V>(v1, v0));	
	}
	
	/**
	 * Add an edge to graph G between vertices v0 and v1 and update the degree order deg
	 * @param G graph to be modified
	 * @param deg degree order to be updated
	 * @param v0 first endpoint of new edge
	 * @param v1 second endpoint of new edge
	 */
	private void addEdge(Graph<V, Pair<V>> G, ArrayList<LinkedList<V>> deg, V v0, V v1)
	{
		//get current degrees of vertices
		int v0Deg = G.degree(v0);
		int v1Deg = G.degree(v1);
		
		//remove old occurrence of v0 in degree order
		deg.get(v0Deg).remove(v0);
		
		//try to add v0 at new location
		try
		{
			deg.get(v0Deg + 1).add(v0);
		}
		catch (IndexOutOfBoundsException e)
		{
			//make new element for growing degree order
			deg.add(new LinkedList<V>());
			deg.get(v0Deg + 1).add(v0);
		}
		
		
		deg.get(v1Deg).remove(v1);
		
		try
		{
			deg.get(v1Deg + 1).add(v1);
		}
		catch (IndexOutOfBoundsException e)
		{
			deg.add(new LinkedList<V>());
			deg.get(v1Deg + 1).add(v1);
		}
		
		//add edge
		G.addEdge(new Pair<V>(v0, v1), v0, v1);	
	}
	
	/**
	 * Compute an ArrayList where every index holds a LinkedList of vertices with degrees of index
	 * @param G graph
	 * @return degree set
	 */
	public ArrayList<LinkedList<V>> degSequenceOrder(Graph<V, Pair<V>> G)
	{
		//store vertices of same degree in LinkedList<V> at the index of their degree in ArrayList
		ArrayList<LinkedList<V>> deg = new ArrayList<LinkedList<V>>();
		int max = 0;
		for (V i : G.getVertices())
		{
			if (G.degree(i) > max)
				max = G.degree(i);
		}
		
		for (int i = 0; i <= max; i++)
		{
			deg.add(new LinkedList<V>());
		}
		
		//for every vertex, add it to the appropriate LinkedList
		for (V i : G.getVertices())
		{
			int iDeg = G.degree(i);
			if (deg.get(iDeg) == null)
			{
				deg.add(iDeg, new LinkedList<V>());
			}
			
			deg.get(iDeg).add(i);
		}
		
		deg.trimToSize();
		return deg;
	}
	
	/**
	 * Flatten and reverse degree order
	 * @param deg degree sequence
	 * @return vertex set in non-increasing degree order
	 */
	public ArrayList<V> flattenAndReverseDeg(ArrayList<LinkedList<V>> deg)
	{
		ArrayList<V> t = new ArrayList<V>(0);
		
		ArrayList<LinkedList<V>> degCopy = clone.deepClone(deg);
		//reverse the order of deg and flatten it for lexBFS
		for (int i = degCopy.size() - 1; i >=0; i--)
		{
			while (!degCopy.get(i).isEmpty())
			{
				t.add(degCopy.get(i).remove());
			}
		}
		return t;
	}
	
	/**
	 * get induced subgraph from vertex set
	 * @param G original graph
	 * @param l vertex set
	 * @return induced subgraph
	 */
	private Graph<V, Pair<V>> graphFromVertexSet(Graph<V, Pair<V>> G, HashSet<V> l)
	{
		Graph<V, Pair<V>> c = new SparseGraph<V, Pair<V>>();
		for (V i : l)
		{
			c.addVertex(i);
			
			//get incident edges
			Collection<Pair<V>> iEdges = c.getIncidentEdges(i);
			//iterate through edges to add to new graph
			for (Pair<V> e : iEdges)
			{
				if (e.getFirst().equals(i) && l.contains(e.getSecond()))
					c.addEdge(e, e.getFirst(), e.getSecond());
				else if (e.getSecond().equals(i) && l.contains(e.getFirst()))
					c.addEdge(e, e.getFirst(), e.getSecond());
			}
			
//			//neighbourhood of i
//			Collection<V> hood = G.getNeighbors(i);
//			for (V n : hood)
//			{
//				if (!(c.containsEdge(new Pair<V>(i, n))))
//					c.addEdge(new Pair<V>(i, n), i, n);
//			}
		}
		return c;
	}
	
	private Graph<V, Pair<V>> connectedCFromVertexSet(Graph<V, Pair<V>> G, HashSet<V> l)
	{
		Graph<V, Pair<V>> c = new SparseGraph<V, Pair<V>>();
		HashSet<Pair<V>> tempSet = new HashSet<Pair<V>>();
		//throw all edges into hashset, no douplicates
		for (V i : l)
		{
			tempSet.addAll(G.getIncidentEdges(i));
		}
		
		//add all edges to c
		for (Pair<V> e : tempSet)
		{
			c.addEdge(e, e.getFirst(), e.getSecond());
		}
		return c;
	}
	
	
	/**
	 * Create a graph, the degree order, and changes from connected component graphs
	 * @param rGraph
	 * @param rDeg
	 * @param rChanges
	 * @param tempChanges
	 * @param results
	 */
	private void graphFromComponentGraphs(Graph<V, Pair<V>> rGraph,
			ArrayList<LinkedList<V>> rDeg, LinkedList<myEdge<V>> rChanges,
			HashSet<myEdge<V>> tempChanges, LinkedList<branchingReturnC<V>> results) {
		//build graph from connected components
		for (branchingReturnC<V> r : results)
		{
			//update total number of changes made
			tempChanges.addAll(r.getChanges());
			
			//add all the edges
			for (V v : r.getG().getVertices())
			{
				rGraph.addVertex(v);
			}
			//add all the vertices
			for (Pair<V> a : r.getG().getEdges())
			{
				rGraph.addEdge(clone.deepClone(a), a.getFirst(), a.getSecond());
			}
			
			//add to degree sequence
			for (int i = 0; i < r.getDeg().size(); i ++)
			{
				try
				{
					rDeg.get(i).addAll(r.getDeg().get(i));
				}
				catch (IndexOutOfBoundsException e)
				{
					rDeg.add(i, new LinkedList<V>());
					rDeg.get(i).addAll(r.getDeg().get(i));
				}
			}	
		}
		rChanges.addAll(tempChanges);
		
	}
	
	/**
	 * fill a linked list with a myEdge LinkedList for the minMoves set
	 * @param G
	 * @return
	 */
	private LinkedList<myEdge<V>> fillMyEdgeSet(Graph<V, Pair<V>> G)
	{
		LinkedList<myEdge<V>> l = new LinkedList<myEdge<V>>();
		for (Pair<V> e : G.getEdges())
		{
			//treat each edge in this set as a deletion
			l.add(new myEdge<V>(e, false));
		}
		return l;
	}
}
