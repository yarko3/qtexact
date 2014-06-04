/**
 * Yaroslav Senyuta
 * NSERC USRA Grant (2014)
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import qtUtils.branchingReturnC;
import qtUtils.lexReturnC;
import qtUtils.myEdge;
import qtUtils.qtGenerate;
import qtUtils.qtLBFS;

import com.rits.cloning.Cloner;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.Pair;


public class qtBranching<V>
{
	public qtLBFS<V> search = new qtLBFS<V>();
	public static Cloner clone = new Cloner();
	
	/**
	 * edit graph using BFS (no heuristic)
	 * @param G graph to be edited
	 * @param MAX maximum depth permitted
	 * @return return an edited graph if one exists, otherwise return original graph 
	 */
	public Graph<V, Pair<V>> qtEditIDBound(Graph<V, Pair<V>> G, int START, int MAX)
	{
		//bound to iterate down to
		int bound = START + 1;
		Graph<V, Pair<V>> goal = G;
		
		//while graph is not solved and the bound is less than MAX
		while (bound <= MAX + 1)
		{
			goal = qtEditNoHeuristic(G, bound);
			//test if current graph is QT
			if (search.isQT(goal))
			{
				return goal;
			}
			else
			{
				bound++;
			}
		}
		return goal;
	}

	/**
	 * edit graph into quasi threshold with given bound of edits
	 * @param G graph to be edited
	 * @param bound maximum number of edits
	 * @return an edited graph if one is found; original graph if no qt graph exists
	 */
	public Graph<V, Pair<V>> qtEditNoHeuristic(Graph<V, Pair<V>> G, int bound)
	{
	
		//keep proper degree order as an ArrayList<LinkedList<vertex>>
		ArrayList<LinkedList<V>> deg = search.degSequenceOrder(G);
		
		//start with a full minMoves
		branchingReturnC<V> minMoves = new branchingReturnC<V>(G, deg);
		minMoves.setChanges(fillMyEdgeSet(G, bound));
		minMoves.setMinMoves(minMoves);
		branchingReturnC<V> goal = new branchingReturnC<V>(G, deg, minMoves);
	
		//branch on G with degree ordering deg
		goal = branchingNoHeuristic(goal, 0);
		System.out.println("Number of moves: " + goal.getMinMoves().getChanges());
		
		qtGenerate<V> gen = new qtGenerate<V>();
		
		
		Graph<V, Pair<V>> rtn = gen.applyMoves(clone.deepClone(goal.getG()), goal.getMinMoves().getChanges());
		
		//return QT graph if edit succeeds
		if (search.isQT(rtn))
			return rtn;
		else
			//otherwise return original graph
			return goal.getG();
		
		
	}

	/**
	 * recursive function for qt editing with no heuristics
	 * graph is checked for qt; minMoves is updated if found solution is better
	 * branch further if allowed by minMoves
	 * @param s the state of the search
	 * @param percentDone used for keeping track of progress (initialize to zero)
	 * @return a state of the search
	 */
	private branchingReturnC<V> branchingNoHeuristic(branchingReturnC<V> s, double percentDone)
	{	
		//check if graph is QT
		ArrayList<V> t = search.flattenAndReverseDeg(s.getDeg());
		lexReturnC<V> lexSearch = search.qtLexBFS(s.getG(), t);
		//qt graph has been found
		
		if (lexSearch.isQT())
		{
			//update the minMoves list if this solution is better
			if (s.getChanges().size() < s.getMinMoves().getChanges().size())
			{
				//make a new minMoves to store
				branchingReturnC<V> newMin = new branchingReturnC<V>(s.getG(), s.getDeg(), clone.deepClone(s.getChanges()));
				newMin.setMinMoves(newMin);
				s.setMinMoves(newMin);
			}
			return s;
		}
			//branch on found P4 or C4
			else
			{	
				//only branch if current minMoves is longer than current state of search
				if (s.getMinMoves().getChanges().size() > s.getChanges().size())
				{
					branchingReturnC<V> rtn = branchOnNoHeuristic(s, lexSearch, percentDone);
					return rtn;
				}
				//min moves is a better solution
				else
					return s.getMinMoves();
			}
	}

	/**
	 * remove P4/C4 when one is found
	 * @param s state of search
	 * @param searchResult lexBFS result
	 * @param percentDone progress through search
	 * @return state of search
	 */
	private branchingReturnC<V> branchOnNoHeuristic(branchingReturnC<V> s, lexReturnC<V> searchResult, double percentDone)
	{
//		branchingReturnC<V> minMoves = s.getMinMoves();
//		LinkedList<myEdge<V>> changes = s.getChanges();
		ArrayList<V> lexResult = searchResult.getCertificate().getVertices();
		//C4 has been found
		if (searchResult.getCertificate().getFlag() == -1)
		{
			//result of adding 1 edge to break C4
			//if we did not remove the edge that is about to be added
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false)))
			{
				branchingReturnC<V> c4Add1 = branchingNoHeuristic(c4p4AddResult(s, lexResult.get(0), lexResult.get(2)), percentDone);
				//revert changes
				c4p4AddRevert(s, lexResult.get(0), lexResult.get(2));		
				//update percentDone
				percentDone = updatePercent(c4Add1, percentDone, 5);
				if (c4Add1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Add1.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
			{
				branchingReturnC<V> c4Add2 = branchingNoHeuristic(c4p4AddResult(s, lexResult.get(1), lexResult.get(3)), percentDone);
				//revert changes
				c4p4AddRevert(s, lexResult.get(1), lexResult.get(3));
				//update percentDone
				percentDone = updatePercent(c4Add2, percentDone, 5);
				if (c4Add2.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Add2.getMinMoves());
				}
			}
			//results of removing 2 edges to break C4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				branchingReturnC<V> c4Remove0 = branchingNoHeuristic(c4Delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2)), percentDone);
				//update percentDone
				percentDone = updatePercent(c4Remove0, percentDone, 5);
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2));
				if (c4Remove0.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove0.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				branchingReturnC<V> c4Remove1 = branchingNoHeuristic(c4Delete2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3)), percentDone);
				//update percentDone
				percentDone = updatePercent(c4Remove1, percentDone, 5);
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3));
				if (c4Remove1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove1.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				branchingReturnC<V> c4Remove2 = branchingNoHeuristic(c4Delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3)), percentDone);
				//update percentDone
				percentDone = updatePercent(c4Remove2, percentDone, 5);
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3));
				if (c4Remove2.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove2.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				branchingReturnC<V> c4Remove3 = branchingNoHeuristic(c4Delete2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2)), percentDone);
				//update percentDone
				percentDone = updatePercent(c4Remove3, percentDone, 5);
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2));
				if (c4Remove3.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove3.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				branchingReturnC<V> c4Remove4 = branchingNoHeuristic(c4Delete2Result(s, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3)), percentDone);
				//update percentDone
				percentDone = updatePercent(c4Remove4, percentDone, 5);
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3));
				if (c4Remove4.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove4.getMinMoves());
				}
			}
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)))
			{
				branchingReturnC<V> c4Remove5 = branchingNoHeuristic(c4Delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3)), percentDone);
				//update percentDone
				percentDone = updatePercent(c4Remove5, percentDone, 5);
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3));
				if (c4Remove5.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove5.getMinMoves());
				}
			}
			return s.getMinMoves();
		}
		//P4 has been found
		else
		{
			//add an edge to break P4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false)))
			{
				branchingReturnC<V> p4Add0 = branchingNoHeuristic(c4p4AddResult(s, lexResult.get(0), lexResult.get(2)), percentDone);
				//revert changes
				c4p4AddRevert(s, lexResult.get(0), lexResult.get(2));
				
				//update percentDone
				percentDone = updatePercent(p4Add0, percentDone, 5);
				if (p4Add0.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Add0.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
			{
				branchingReturnC<V> p4Add1 = branchingNoHeuristic(c4p4AddResult(s, lexResult.get(1), lexResult.get(3)), percentDone);
				//revert changes
				c4p4AddRevert(s, lexResult.get(1), lexResult.get(3));
				//update percentDone
				percentDone = updatePercent(p4Add1, percentDone, 5);
				if (p4Add1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Add1.getMinMoves());
				}
			}
			//remove an edge to break P4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)))
			{
				branchingReturnC<V> p4Remove0 = branchingNoHeuristic(p4DeleteResult(s, lexResult.get(0), lexResult.get(1)), percentDone);
				//update percentDone
				percentDone = updatePercent(s, percentDone, 5);
				//revert changes to global graph
				p4DeleteRevert(s,lexResult.get(0), lexResult.get(1));
				if (p4Remove0.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Remove0.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				branchingReturnC<V> p4Remove1 = branchingNoHeuristic(p4DeleteResult(s, lexResult.get(1), lexResult.get(2)), percentDone);
				//update percentDone
				percentDone = updatePercent(s, percentDone, 5);
				//revert changes to global graph
				p4DeleteRevert(s,lexResult.get(1), lexResult.get(2));
				if (p4Remove1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Remove1.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				branchingReturnC<V> p4Remove2 = branchingNoHeuristic(p4DeleteResult(s, lexResult.get(2), lexResult.get(3)), percentDone);
				//update percentDone
				percentDone = updatePercent(s, percentDone, 5);
				//revert changes to global graph
				p4DeleteRevert(s,lexResult.get(2), lexResult.get(3));
				if (p4Remove2.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Remove2.getMinMoves());
				}
			}
			return s.getMinMoves();
		}
	}

	/**
	 * Edit a graph by splitting it into connected components with bounding
	 * @param G graph to be edited
	 * @param bound maximum number of edits permitted
	 * @return edited graph if qt found; original graph if no solution is found
	 */
	public Graph<V, Pair<V>> qtEditConnectedComponents(Graph<V, Pair<V>> G, int bound)
	{
		
		//keep proper degree order as an ArrayList<LinkedList<vertex>>
		ArrayList<LinkedList<V>> deg = search.degSequenceOrder(G);
		
		
		//start with a full minMoves
		branchingReturnC<V> minMoves = new branchingReturnC<V>(G, deg);
		minMoves.setChanges(fillMyEdgeSet(G, bound));
		minMoves.setMinMoves(minMoves);
		branchingReturnC<V> goal = new branchingReturnC<V>(G, deg, minMoves);
		
		//branch on G with degree ordering deg
		goal = branchingCC(goal);
		System.out.println("Number of moves: " + goal.getMinMoves().getChanges());
		
		qtGenerate<V> gen = new qtGenerate<V>();
		
		Graph<V, Pair<V>> rtn = gen.applyMoves(clone.deepClone(goal.getG()), goal.getMinMoves().getChanges());

		//return QT graph if edit succeeds
		if (search.isQT(rtn))
			return rtn;
		else
			//otherwise return original graph
			return goal.getG();
		
	}
	
	/**
	 * branch using connected components
	 * 
	 * @param s current search state
	 * @return a branching state containing the graph, degree order and edited edge set
	 */
	private branchingReturnC<V> branchingCC(branchingReturnC<V> s)
	{
//		Graph<V, Pair<V>> G = s.getG();
//		ArrayList<LinkedList<V>> deg = s.getDeg();
//		LinkedList<myEdge<V>> changes = s.getChanges();
//		branchingReturnC<V> minMoves = s.getMinMoves();
		
		
		//check if graph is QT
		ArrayList<V> t = search.flattenAndReverseDeg(s.getDeg());
		
		lexReturnC<V> lexSearch = search.qtLexBFSComponents(s.getG(), t);
		
		//qt graph has been found
		if (lexSearch.isQT())
		{
			//update the minMoves list
			if (s.getChanges().size() < s.getMinMoves().getChanges().size())
			{
				//make a new minMoves to store
				branchingReturnC<V> newMin = new branchingReturnC<V>(s.getG(), s.getDeg(), clone.deepClone(s.getChanges()));
				newMin.setMinMoves(newMin);
				s.setMinMoves(newMin);
			}

			return s;
		}
		//branch on found P4 or C4
		else
		{	
			//check if minMoves is a better choice than current state of search
			if (s.getMinMoves().getChanges().size() > s.getChanges().size())
			{
				branchingReturnC<V> rtn = componentSplit(s, lexSearch);
				return rtn;
			}
			//min moves is a better solution
			else
				return s.getMinMoves();
		}
	}
	
	/**
	 * split graph into components and edit each individual component
	 * @param s search state
	 * @param lexSearch result of lexBFS search
	 * @return search state
	 */
	private branchingReturnC<V> componentSplit(branchingReturnC<V> s, lexReturnC<V> lexSearch)
	{				
		//search yields only one connected component, branch on one component
		if (lexSearch.isConnected())
		{
			return branchOnCC(s, lexSearch);
		}
		//multiple connected components exist
		else
		{
			//build graphs from connected components
			Graph<V, Pair<V>> gWtihForbidden = connectedCFromVertexSet(s.getG(), lexSearch.getcComponents().removeLast());
			
			LinkedList<Graph<V, Pair<V>>> cGraphs = new LinkedList<Graph<V, Pair<V>>>();
			LinkedList<branchingReturnC<V>> results = new LinkedList<branchingReturnC<V>>();
			for (HashSet<V> l : lexSearch.getcComponents())
			{
				cGraphs.add(connectedCFromVertexSet(s.getG(), l));
			}
			//branch on known forbidden structure
			
			//fill new minMoves with entire edge set
			branchingReturnC<V> min = new branchingReturnC<V>(gWtihForbidden, s.getDeg());
			//bound the search by the best solution so far
			min.setChanges(fillMyEdgeSet(gWtihForbidden, s.getMinMoves().getChanges().size() - s.getChanges().size()));
			min.setMinMoves(min);
			results.add(branchOnCC(new branchingReturnC<V>(gWtihForbidden, search.degSequenceOrder(gWtihForbidden), min), lexSearch));
			//branch on the rest of the graphs
			for (Graph<V, Pair<V>> g : cGraphs)
			{
				//if component is large enough to care
				if (g.getVertexCount() > 3)
				{
					//fill new minMoves with bounded edge set of component
					min = new branchingReturnC<V>(g, s.getDeg());
					min.setChanges(fillMyEdgeSet(g, s.getMinMoves().getChanges().size() - s.getChanges().size()));
					min.setMinMoves(min);
					results.add(branchingCC(new branchingReturnC<V>(g,search.degSequenceOrder(g), new LinkedList<myEdge<V>>(), min)));
				}
				//don't care about branching on this but still need it to build up the solution later
				else
				{
					//empty minMoves 
					min = new branchingReturnC<V>(g, s.getDeg());
					min.setMinMoves(min);
					results.add(new branchingReturnC<V>(g, search.degSequenceOrder(g), min));
				}		
			}
			
			//construct new minMoves from all old ones
			min = new branchingReturnC<V>(s.getG(), s.getDeg(), min);
			//throw all minMoves into a HashSet, so they don't have duplicates
			HashSet<myEdge<V>> temp = new HashSet<myEdge<V>>();
			for (branchingReturnC<V> r : results)
			{
				temp.addAll(r.getMinMoves().getChanges());
			}
			min.getChanges().addAll(temp);
			min.getChanges().addAll(s.getChanges());		
			
			return min;
		}
	}

	/**
	 * edit out a P4/C4 by adding or removing edges and branch further
	 * @param s search state
	 * @param searchResult lexBFS result
	 * @return search state
	 */
	private branchingReturnC<V> branchOnCC(branchingReturnC<V> s, lexReturnC<V> searchResult)
	{
		
		//C4 has been found
		ArrayList<V> lexResult = searchResult.getCertificate().getVertices();
		if (searchResult.getCertificate().getFlag() == -1)
		{
			//result of adding 1 edge to break C4
			//if we did not remove an edge that is about to be added
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false)))
			{
				branchingReturnC<V> c4Add1 = branchingCC(c4p4AddResult(s, lexResult.get(0), lexResult.get(2)));
				//revert changes
				c4p4AddRevert(s, lexResult.get(0), lexResult.get(2));
				if (c4Add1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Add1.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
			{
				branchingReturnC<V> c4Add2 = branchingCC(c4p4AddResult(s, lexResult.get(1), lexResult.get(3)));
				//revert changes
				c4p4AddRevert(s, lexResult.get(1), lexResult.get(3));
				if (c4Add2.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Add2.getMinMoves());
				}
			}
			//results of removing 2 edges to break C4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				branchingReturnC<V> c4Remove0 = branchingCC(c4Delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2)));
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2));
				if (c4Remove0.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove0.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				branchingReturnC<V> c4Remove1 = branchingCC(c4Delete2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3)));
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3));
				if (c4Remove1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove1.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				branchingReturnC<V> c4Remove2 = branchingCC(c4Delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3)));
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3));
				if (c4Remove2.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove2.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				branchingReturnC<V> c4Remove3 = branchingCC(c4Delete2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2)));
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2));
				if (c4Remove3.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove3.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				branchingReturnC<V> c4Remove4 = branchingCC(c4Delete2Result(s, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3)));
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3));
				if (c4Remove4.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove4.getMinMoves());
				}
			}
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)))
			{
				branchingReturnC<V> c4Remove5 = branchingCC(c4Delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3)));
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3));
				if (c4Remove5.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(c4Remove5.getMinMoves());
				}
			}
			return s.getMinMoves();
		}
		//P4 has been found
		else
		{
			//see if any vertices of P4 have a deg of 1 (if so, branch only on removing those ones)
			int deg0 = s.getG().degree(lexResult.get(0));
			int deg3 = s.getG().degree(lexResult.get(3));
			
			if (deg0 == 1 || deg3 == 1)
			{
				//branch only on the deletion of removing the first element of P4
				if (deg0 == 1)
				{
					if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)))
					{
						branchingReturnC<V> p4Remove0 = branchingCC(p4DeleteResult(s, lexResult.get(0), lexResult.get(1)));
						//revert changes to global graph
						p4DeleteRevert(s,lexResult.get(0), lexResult.get(1));
						if (p4Remove0.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
						{
							s.setMinMoves(p4Remove0.getMinMoves());
						}
						return s.getMinMoves();
					}
				}
				
				if (deg3 == 1)
				{
					if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
					{
						branchingReturnC<V> p4Remove2 = branchingCC(p4DeleteResult(s, lexResult.get(2), lexResult.get(3)));
						//revert changes to global graph
						p4DeleteRevert(s,lexResult.get(2), lexResult.get(3));
						if (p4Remove2.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
						{
							s.setMinMoves(p4Remove2.getMinMoves());
						}
					}
					return s.getMinMoves();
				}
			}
			
			//------------------------------------------------------------------------------------------------------------
			//add an edge to break P4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false)))
			{
				branchingReturnC<V> p4Add0 = branchingCC(c4p4AddResult(s, lexResult.get(0), lexResult.get(2)));
				//revert changes
				c4p4AddRevert(s, lexResult.get(0), lexResult.get(2));
				if (p4Add0.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Add0.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
			{
				branchingReturnC<V> p4Add1 = branchingCC(c4p4AddResult(s, lexResult.get(1), lexResult.get(3)));
				//revert changes
				c4p4AddRevert(s, lexResult.get(1), lexResult.get(3));
				if (p4Add1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Add1.getMinMoves());
				}
			}
			//remove an edge to break P4
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)))
			{
				branchingReturnC<V> p4Remove0 = branchingCC(p4DeleteResult(s, lexResult.get(0), lexResult.get(1)));
				//revert changes to global graph
				p4DeleteRevert(s,lexResult.get(0), lexResult.get(1));
				if (p4Remove0.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Remove0.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				branchingReturnC<V> p4Remove1 = branchingCC(p4DeleteResult(s, lexResult.get(1), lexResult.get(2)));
				//revert changes to global graph
				p4DeleteRevert(s,lexResult.get(1), lexResult.get(2));
				if (p4Remove1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Remove1.getMinMoves());
				}
			}
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				branchingReturnC<V> p4Remove2 = branchingCC(p4DeleteResult(s, lexResult.get(2), lexResult.get(3)));
				//revert changes to global graph
				p4DeleteRevert(s,lexResult.get(2), lexResult.get(3));
				if (p4Remove2.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
				{
					s.setMinMoves(p4Remove2.getMinMoves());
				}
			}
			return s.getMinMoves();
		}
	}
	
	/**
	 * edit the graph by adding an edge between v0 and v1
	 * @param s search state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @return edited search state
	 */
	private branchingReturnC<V> c4p4AddResult(branchingReturnC<V> s, V v0, V v1)
	{
		
		//update degree sequence (first edge)
		addEdge(s.getG(), s.getDeg(), v0, v1);
		
		//add edge to changes 
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v0, v1), true));
		
		return s;
		
	}
	/**
	 * revert the changes of adding an edge on v0 and v1
	 * @param s search state
	 * @param v0 vertex
	 * @param v1 vertex
	 */
	private void c4p4AddRevert(branchingReturnC<V> s, V v0, V v1)
	{
		//update degree sequence (first edge)
		removeEdge(s.getG(), s.getDeg(), v0, v1);
		
		s.getChanges().removeLast();
	}
	

	/**
	 * delete 2 edges to remove a C4
	 * @param s search state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @param v2 vertex
	 * @param v3 vertex
	 * @return search state
	 */
	private branchingReturnC<V> c4Delete2Result(branchingReturnC<V> s, V v0, V v1, V v2, V v3)
	{	
		//update degree sequence (first edge)
		removeEdge(s.getG(), s.getDeg(), v0, v1);
		
		//update degree sequence (second edge)
		removeEdge(s.getG(), s.getDeg(), v2, v3);
		
		//add edge deletions to changes
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v0, v1), false));
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v2, v3), false));
		
		return s;
		
	}
	
	/**
	 * revert changes made by removing 2 edges from a C4
	 * @param s search state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @param v2 vertex
	 * @param v3 vertex
	 */
	private void c4Delete2Revert(branchingReturnC<V> s, V v0,
			V v1, V v2, V v3) 
	{
		
		//add edges back in
		addEdge(s.getG(), s.getDeg(), v0, v1);
		addEdge(s.getG(), s.getDeg(), v2, v3);
		
		//revert changes
		s.getChanges().removeLast();
		s.getChanges().removeLast();
	
	}
	
	/**
	 * edit a P4 by removing an edge
	 * @param s search state
	 * @param v0 vertex
	 * @param v1 vertex
	 * @return search state
	 */
	private branchingReturnC<V> p4DeleteResult(branchingReturnC<V> s, V v0, V v1)
	{
		//update degree sequence (first edge)
		removeEdge(s.getG(), s.getDeg(), v0, v1);
		
		s.getChanges().addLast(new myEdge<V>(new Pair<V>(v0, v1), false));
		return s;
		
	}
	
	/**
	 * revert changes of removing an edge from a P4
	 * @param s search state
	 * @param v0 vertex
	 * @param v1 vertex
	 */
	private void p4DeleteRevert(branchingReturnC<V> s, V v0, V v1)
	{
		//update degree sequence (first edge)
		addEdge(s.getG(), s.getDeg(), v0, v1);
		
		s.getChanges().removeLast();
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
	private LinkedList<myEdge<V>> fillMyEdgeSet(Graph<V, Pair<V>> G, int bound)
	{
		LinkedList<myEdge<V>> l = new LinkedList<myEdge<V>>();
		for (Pair<V> e : G.getEdges())
		{
			//treat each edge in this set as a deletion
			l.add(new myEdge<V>(e, false));
		}
		if (bound > 0)
		{
			while (l.size() > bound)
				l.removeLast();
		}
		return l;
	}
	
	/**
	 * update percent complete and give status of current search
	 * @param s
	 * @param percentDone
	 * @param branching
	 * @return
	 */
	private double updatePercent(branchingReturnC<V> s, double percentDone, int branching)
	{
		percentDone += Math.pow(((double) 1 / (double) branching), s.getChanges().size());
		//System.out.println("Done: " + percentDone);
		//System.out.println("Size of best solution: " + s.getMinMoves().getChanges().size());
		
		return percentDone;
	}
}
