/**
 * Yaroslav Senyuta
 * NSERC USRA Grant (2014)
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import qtUtils.branchingReturnC;
import qtUtils.genericLBFS;
import qtUtils.lexReturnC;
import qtUtils.myEdge;
import qtUtils.qtGenerate;

import com.rits.cloning.Cloner;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.Pair;


public class qtBranching<V>
{
	public genericLBFS<V> search = new genericLBFS<V>();
	
	public static Cloner clone = new Cloner();
	
	/**
	 * Edit graph with no heuristics other than bound
	 * @param G graph to be edited
	 * @return edited QT graph
	 */
	public Graph<V, Pair<V>> qtEditNoHeuristic(Graph<V, Pair<V>> G, int bound)
	{
	
		//keep proper degree order as an ArrayList<LinkedList<vertex>>
		ArrayList<LinkedList<V>> deg = degSequenceOrder(G);
		
		//start with a full minMoves
		branchingReturnC<V> minMoves = new branchingReturnC<V>(G, deg);
		minMoves.setChanges(fillMyEdgeSet(G, bound));
		minMoves.setMinMoves(minMoves);
		branchingReturnC<V> goal = new branchingReturnC<V>(G, deg);
		goal.setMinMoves(minMoves);
	
		//branch on G with degree ordering deg
		goal = branchingNoHeuristic(goal, 0);
		System.out.println("Number of moves: " + goal.getMinMoves().getChanges());
		
		qtGenerate<V> gen = new qtGenerate<V>();
		
		Graph<V, Pair<V>> rtn = gen.applyMoves(goal.getG(), goal.getMinMoves().getChanges());
		
		return rtn;
		
	}

	/**
	 * branch without using any heuristic
	 * @param s contains graph, degree order, current set of edits
	 * @return a modified graph, degree order and set of edits
	 */
	private branchingReturnC<V> branchingNoHeuristic(branchingReturnC<V> s, double percentDone)
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
					branchingReturnC<V> rtn = branchOnNoHeuristic(s, lexSearch, percentDone);
					return rtn;
				}
				//min moves is a better solution
				else
					return minMoves;
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
	private branchingReturnC<V> branchOnNoHeuristic(branchingReturnC<V> s, lexReturnC<V> searchResult, double percentDone)
	{
		branchingReturnC<V> minMoves = s.getMinMoves();
		LinkedList<myEdge<V>> changes = s.getChanges();
		//C4 has been found
		ArrayList<V> lexResult = searchResult.getCertificate().getVertices();
		if (searchResult.getCertificate().getFlag() == -1)
		{
			//result of adding 1 edge to break C4
			//if we did not remove an edge that is about to be added
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false)))
			{
				branchingReturnC<V> c4Add1 = branchingNoHeuristic(c4p4AddResult(s, lexResult, lexResult.get(0), lexResult.get(2)), percentDone);
				//revert changes
				c4p4AddRevert(s, lexResult.get(0), lexResult.get(2));		
				//update percentDone
				percentDone = updatePercent(c4Add1, percentDone, 5);
				if (c4Add1.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = c4Add1.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
			{
				branchingReturnC<V> c4Add2 = branchingNoHeuristic(c4p4AddResult(s, lexResult, lexResult.get(1), lexResult.get(3)), percentDone);
				//revert changes
				c4p4AddRevert(s, lexResult.get(1), lexResult.get(3));
				//update percentDone
				percentDone = updatePercent(c4Add2, percentDone, 5);
				if (c4Add2.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = c4Add2.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			//results of removing 2 edges to break C4
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				branchingReturnC<V> c4Remove0 = branchingNoHeuristic(c4Delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2)), percentDone);
				//update percentDone
				percentDone = updatePercent(c4Remove0, percentDone, 5);
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2));
				if (c4Remove0.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = c4Remove0.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)) && !changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				branchingReturnC<V> c4Remove1 = branchingNoHeuristic(c4Delete2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3)), percentDone);
				//update percentDone
				percentDone = updatePercent(c4Remove1, percentDone, 5);
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3));
				if (c4Remove1.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = c4Remove1.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				branchingReturnC<V> c4Remove2 = branchingNoHeuristic(c4Delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3)), percentDone);
				//update percentDone
				percentDone = updatePercent(c4Remove2, percentDone, 5);
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3));
				if (c4Remove2.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = c4Remove2.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)) && !changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				branchingReturnC<V> c4Remove3 = branchingNoHeuristic(c4Delete2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2)), percentDone);
				//update percentDone
				percentDone = updatePercent(c4Remove3, percentDone, 5);
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2));
				if (c4Remove3.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = c4Remove3.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)) && !changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				branchingReturnC<V> c4Remove4 = branchingNoHeuristic(c4Delete2Result(s, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3)), percentDone);
				//update percentDone
				percentDone = updatePercent(c4Remove4, percentDone, 5);
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3));
				if (c4Remove4.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = c4Remove4.getMinMoves();
				}
			}
			
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)))
			{
				branchingReturnC<V> c4Remove5 = branchingNoHeuristic(c4Delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3)), percentDone);
				//update percentDone
				percentDone = updatePercent(c4Remove5, percentDone, 5);
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3));
				if (c4Remove5.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = c4Remove5.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			return minMoves;
		}
		//P4 has been found
		else
		{
			//add an edge to break P4
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false)))
			{
				branchingReturnC<V> p4Add0 = branchingNoHeuristic(c4p4AddResult(s, lexResult, lexResult.get(0), lexResult.get(2)), percentDone);
				//revert changes
				c4p4AddRevert(s, lexResult.get(0), lexResult.get(2));
				
				//update percentDone
				percentDone = updatePercent(p4Add0, percentDone, 5);
				if (p4Add0.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = p4Add0.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
			{
				branchingReturnC<V> p4Add1 = branchingNoHeuristic(c4p4AddResult(s, lexResult, lexResult.get(1), lexResult.get(3)), percentDone);
				//revert changes
				c4p4AddRevert(s, lexResult.get(1), lexResult.get(3));
				//update percentDone
				percentDone = updatePercent(p4Add1, percentDone, 5);
				if (p4Add1.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = p4Add1.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			//remove an edge to break P4
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)))
			{
				branchingReturnC<V> p4Remove0 = branchingNoHeuristic(p4DeleteResult(s, lexResult.get(0), lexResult.get(1)), percentDone);
				//update percentDone
				percentDone = updatePercent(s, percentDone, 5);
				//revert changes to global graph
				p4DeleteRevert(s,lexResult.get(0), lexResult.get(1));
				if (p4Remove0.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = p4Remove0.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				branchingReturnC<V> p4Remove1 = branchingNoHeuristic(p4DeleteResult(s, lexResult.get(1), lexResult.get(2)), percentDone);
				//update percentDone
				percentDone = updatePercent(s, percentDone, 5);
				//revert changes to global graph
				p4DeleteRevert(s,lexResult.get(1), lexResult.get(2));
				if (p4Remove1.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = p4Remove1.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				branchingReturnC<V> p4Remove2 = branchingNoHeuristic(p4DeleteResult(s, lexResult.get(2), lexResult.get(3)), percentDone);
				//update percentDone
				percentDone = updatePercent(s, percentDone, 5);
				//revert changes to global graph
				p4DeleteRevert(s,lexResult.get(2), lexResult.get(3));
				if (p4Remove2.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = p4Remove2.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			return minMoves;
		}
	}

	/**
	 * Edit a graph by splitting it into connected components with bounding
	 * @param G graph to be edited
	 * @return an edited QT graph
	 */
	public Graph<V, Pair<V>> qtEditConnectedComponents(Graph<V, Pair<V>> G, int bound)
	{
		
		//keep proper degree order as an ArrayList<LinkedList<vertex>>
		ArrayList<LinkedList<V>> deg = degSequenceOrder(G);
		
		
		//start with a full minMoves
		branchingReturnC<V> minMoves = new branchingReturnC<V>(G, deg);
		minMoves.setChanges(fillMyEdgeSet(G, bound));
		minMoves.setMinMoves(minMoves);
		branchingReturnC<V> goal = new branchingReturnC<V>(G, deg);
		goal.setMinMoves(minMoves);
		
		//branch on G with degree ordering deg
		goal = branchingCC(goal);
		System.out.println("Number of moves: " + goal.getMinMoves().getChanges());
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
				branchingReturnC<V> rtn = componentSplit(s, lexSearch);
				return rtn;
			}
			//min moves is a better solution
			else
				return new branchingReturnC<V>(G, deg, minMoves.getChanges(), minMoves);
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
		private branchingReturnC<V> componentSplit(branchingReturnC<V> s, lexReturnC<V> lex)
		{
			Graph<V, Pair<V>> G = s.getG();
			ArrayList<LinkedList<V>> deg = s.getDeg() ;
			LinkedList<myEdge<V>> changes = s.getChanges();
			branchingReturnC<V> minMoves = s.getMinMoves();
			
			
	//		//make copy of search results, so multiple branches can use the same search
	//		lexReturnC<V> lexSearch = clone.deepClone(lex);
			
			lexReturnC<V> lexSearch = lex;
			
			//search yields only one connected component, branch on one component
			if (lexSearch.isConnected())
			{
				return branchOnCC(s, lexSearch);
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
				//bound the search by the best solution so far
				min.setChanges(fillMyEdgeSet(gWtihForbidden, minMoves.getChanges().size() - changes.size()));
				results.add(branchOnCC(new branchingReturnC<V>(gWtihForbidden, degSequenceOrder(gWtihForbidden), min), lexSearch));
				//branch on the rest of the graphs
				for (Graph<V, Pair<V>> g : cGraphs)
				{
					//if component is large enough to care
					if (g.getVertexCount() > 3)
					{
						//fill new minMoves with entire edge set of component
						min = new branchingReturnC<V>(g, deg);
						min.setChanges(fillMyEdgeSet(g, minMoves.getChanges().size() - changes.size()));
	
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

	private branchingReturnC<V> branchOnCC(branchingReturnC<V> s, lexReturnC<V> searchResult)
	{
		
		branchingReturnC<V> minMoves = s.getMinMoves();
		LinkedList<myEdge<V>> changes = s.getChanges();
		//C4 has been found
		ArrayList<V> lexResult = searchResult.getCertificate().getVertices();
		if (searchResult.getCertificate().getFlag() == -1)
		{
			//result of adding 1 edge to break C4
			//if we did not remove an edge that is about to be added
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false)))
			{
				branchingReturnC<V> c4Add1 = branchingCC(c4p4AddResult(s, lexResult, lexResult.get(0), lexResult.get(2)));
				//revert changes
				c4p4AddRevert(s, lexResult.get(0), lexResult.get(2));
				if (c4Add1.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = c4Add1.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
			{
				branchingReturnC<V> c4Add2 = branchingCC(c4p4AddResult(s, lexResult, lexResult.get(1), lexResult.get(3)));
				//revert changes
				c4p4AddRevert(s, lexResult.get(1), lexResult.get(3));
				if (c4Add2.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = c4Add2.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			//results of removing 2 edges to break C4
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				branchingReturnC<V> c4Remove0 = branchingCC(c4Delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2)));
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2));
				if (c4Remove0.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = c4Remove0.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)) && !changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				branchingReturnC<V> c4Remove1 = branchingCC(c4Delete2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3)));
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3));
				if (c4Remove1.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = c4Remove1.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				branchingReturnC<V> c4Remove2 = branchingCC(c4Delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3)));
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3));
				if (c4Remove2.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = c4Remove2.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)) && !changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				branchingReturnC<V> c4Remove3 = branchingCC(c4Delete2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2)));
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2));
				if (c4Remove3.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = c4Remove3.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)) && !changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				branchingReturnC<V> c4Remove4 = branchingCC(c4Delete2Result(s, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3)));
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3));
				if (c4Remove4.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = c4Remove4.getMinMoves();
				}
			}
			
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)))
			{
				branchingReturnC<V> c4Remove5 = branchingCC(c4Delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3)));
				//revert change to global graph
				c4Delete2Revert(s, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3));
				if (c4Remove5.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = c4Remove5.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			return minMoves;
		}
		//P4 has been found
		else
		{
			//add an edge to break P4
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false)))
			{
				branchingReturnC<V> p4Add0 = branchingCC(c4p4AddResult(s, lexResult, lexResult.get(0), lexResult.get(2)));
				//revert changes
				c4p4AddRevert(s, lexResult.get(0), lexResult.get(2));
				if (p4Add0.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = p4Add0.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
			{
				branchingReturnC<V> p4Add1 = branchingCC(c4p4AddResult(s, lexResult, lexResult.get(1), lexResult.get(3)));
				//revert changes
				c4p4AddRevert(s, lexResult.get(1), lexResult.get(3));
				if (p4Add1.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = p4Add1.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			//remove an edge to break P4
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)))
			{
				branchingReturnC<V> p4Remove0 = branchingCC(p4DeleteResult(s, lexResult.get(0), lexResult.get(1)));
				//revert changes to global graph
				p4DeleteRevert(s,lexResult.get(0), lexResult.get(1));
				if (p4Remove0.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = p4Remove0.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
			{
				branchingReturnC<V> p4Remove1 = branchingCC(p4DeleteResult(s, lexResult.get(1), lexResult.get(2)));
				//revert changes to global graph
				p4DeleteRevert(s,lexResult.get(1), lexResult.get(2));
				if (p4Remove1.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = p4Remove1.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			if (!changes.contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
			{
				branchingReturnC<V> p4Remove2 = branchingCC(p4DeleteResult(s, lexResult.get(2), lexResult.get(3)));
				//revert changes to global graph
				p4DeleteRevert(s,lexResult.get(2), lexResult.get(3));
				if (p4Remove2.getMinMoves().getChanges().size() < minMoves.getChanges().size())
				{
					minMoves = p4Remove2.getMinMoves();
					s.setMinMoves(minMoves);
				}
			}
			return minMoves;
		}
	}
	
	/**
	 * 
	 * @param s
	 * @param lexResult
	 * @param v0
	 * @param v1
	 * @return
	 */
	private branchingReturnC<V> c4p4AddResult(branchingReturnC<V> s, ArrayList<V> lexResult, V v0, V v1)
	{
		Graph<V, Pair<V>> g = s.getG();
		ArrayList<LinkedList<V>> deg = s.getDeg();
		LinkedList<myEdge<V>> changes = s.getChanges();
		
		//update degree sequence (first edge)
		addEdge(g, deg, v0, v1);
		
		//add edge to changes 
		changes.addLast(new myEdge<V>(new Pair<V>(v0, v1), true));
		
		return new branchingReturnC<V>(g, deg, changes, s.getMinMoves());
		
	}
	private void c4p4AddRevert(branchingReturnC<V> s, V v0, V v1)
	{
		Graph<V, Pair<V>> g = s.getG();
		ArrayList<LinkedList<V>> deg = s.getDeg();
		LinkedList<myEdge<V>> changes = s.getChanges();
		
		//update degree sequence (first edge)
		removeEdge(g, deg, v0, v1);
		
		changes.removeLast();
	}
	

	private branchingReturnC<V> c4Delete2Result(branchingReturnC<V> s, V v0, V v1, V v2, V v3)
	{
		Graph<V, Pair<V>> g = s.getG();
		ArrayList<LinkedList<V>> deg = s.getDeg() ;
		LinkedList<myEdge<V>> changes = s.getChanges();
		branchingReturnC<V> minMoves = s.getMinMoves();
		
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
	private void c4Delete2Revert(branchingReturnC<V> s, V v0,
			V v1, V v2, V v3) 
	{
		Graph<V, Pair<V>> g = s.getG();
		ArrayList<LinkedList<V>> deg = s.getDeg() ;
		LinkedList<myEdge<V>> changes = s.getChanges();
		
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
	private branchingReturnC<V> p4DeleteResult(branchingReturnC<V> s, V v0, V v1)
	{
		Graph<V, Pair<V>> g = s.getG();
		ArrayList<LinkedList<V>> deg = s.getDeg() ;
		LinkedList<myEdge<V>> changes = s.getChanges();
		branchingReturnC<V> minMoves = s.getMinMoves();
		
		//update degree sequence (first edge)
		removeEdge(g, deg, v0, v1);
		
		changes.addLast(new myEdge<V>(new Pair<V>(v0, v1), false));
		return new branchingReturnC<V>(g, deg, changes, minMoves);
		
	}
	
	private void p4DeleteRevert(branchingReturnC<V> s, V v0, V v1)
	{
		Graph<V, Pair<V>> g = s.getG();
		ArrayList<LinkedList<V>> deg = s.getDeg() ;
		LinkedList<myEdge<V>> changes = s.getChanges();
		
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
		System.out.println("Done: " + percentDone);
		System.out.println("Size of best solution: " + s.getMinMoves().getChanges().size());
		
		return percentDone;
	}
}
