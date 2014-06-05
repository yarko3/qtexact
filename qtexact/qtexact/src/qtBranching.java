///**
// * Yaroslav Senyuta
// * NSERC USRA Grant (2014)
// */
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashSet;
//import java.util.LinkedList;
//
//import qtUtils.branchingReturnC;
//import qtUtils.lexReturnC;
//import qtUtils.myEdge;
//import qtUtils.qtGenerate;
//import search.qtLBFS;
//
//import com.rits.cloning.Cloner;
//
//import edu.uci.ics.jung.graph.Graph;
//import edu.uci.ics.jung.graph.SparseGraph;
//import edu.uci.ics.jung.graph.util.Pair;
//
//
//public class qtBranching<V>
//{
//	/**
//	 * edit graph using BFS (no heuristic)
//	 * @param G graph to be edited
//	 * @param MAX maximum depth permitted
//	 * @return return an edited graph if one exists, otherwise return original graph 
//	 */
//	public Graph<V, Pair<V>> qtEditIDBound(Graph<V, Pair<V>> G, int START, int MAX)
//	{
//		//bound to iterate down to
//		int bound = START + 1;
//		Graph<V, Pair<V>> goal = G;
//		
//		//while graph is not solved and the bound is less than MAX
//		while (bound <= MAX + 1)
//		{
//			goal = qtEditNoHeuristic(G, bound);
//			//test if current graph is QT
//			if (search.isQT(goal))
//			{
//				return goal;
//			}
//			else
//			{
//				bound++;
//			}
//		}
//		return goal;
//	}
//
//	/**
//	 * edit graph into quasi threshold with given bound of edits
//	 * @param G graph to be edited
//	 * @param bound maximum number of edits
//	 * @return an edited graph if one is found; original graph if no qt graph exists
//	 */
//	public Graph<V, Pair<V>> qtEditNoHeuristic(Graph<V, Pair<V>> G, int bound)
//	{
//	
//		//keep proper degree order as an ArrayList<LinkedList<vertex>>
//		ArrayList<LinkedList<V>> deg = search.degSequenceOrder(G);
//		
//		//start with a full minMoves
//		branchingReturnC<V> minMoves = new branchingReturnC<V>(G, deg);
//		minMoves.setChanges(fillMyEdgeSet(G, bound));
//		minMoves.setMinMoves(minMoves);
//		branchingReturnC<V> goal = new branchingReturnC<V>(G, deg, minMoves);
//	
//		//branch on G with degree ordering deg
//		goal = branchingNoHeuristic(goal, 0);
//		System.out.println("Number of moves: " + goal.getMinMoves().getChanges());
//		
//		qtGenerate<V> gen = new qtGenerate<V>();
//		
//		
//		Graph<V, Pair<V>> rtn = gen.applyMoves(clone.deepClone(goal.getG()), goal.getMinMoves().getChanges());
//		
//		//return QT graph if edit succeeds
//		if (search.isQT(rtn))
//			return rtn;
//		else
//			//otherwise return original graph
//			return goal.getG();
//		
//		
//	}
//
//	/**
//	 * recursive function for qt editing with no heuristics
//	 * graph is checked for qt; minMoves is updated if found solution is better
//	 * branch further if allowed by minMoves
//	 * @param s the state of the search
//	 * @param percentDone used for keeping track of progress (initialize to zero)
//	 * @return a state of the search
//	 */
//	private branchingReturnC<V> branchingNoHeuristic(branchingReturnC<V> s, double percentDone)
//	{	
//		//check if graph is QT
//		ArrayList<V> t = search.flattenAndReverseDeg(s.getDeg());
//		lexReturnC<V> lexSearch = search.qtLexBFS(s.getG(), t);
//		//qt graph has been found
//		
//		if (lexSearch.isTarget())
//		{
//			//update the minMoves list if this solution is better
//			if (s.getChanges().size() < s.getMinMoves().getChanges().size())
//			{
//				//make a new minMoves to store
//				branchingReturnC<V> newMin = new branchingReturnC<V>(s.getG(), s.getDeg(), clone.deepClone(s.getChanges()));
//				newMin.setMinMoves(newMin);
//				s.setMinMoves(newMin);
//			}
//			return s;
//		}
//			//branch on found P4 or C4
//			else
//			{	
//				//only branch if current minMoves is longer than current state of search
//				if (s.getMinMoves().getChanges().size() > s.getChanges().size())
//				{
//					branchingReturnC<V> rtn = branchOnNoHeuristic(s, lexSearch, percentDone);
//					return rtn;
//				}
//				//min moves is a better solution
//				else
//					return s.getMinMoves();
//			}
//	}
//
//
//	/**
//	 * Edit a graph by splitting it into connected components with bounding
//	 * @param G graph to be edited
//	 * @param bound maximum number of edits permitted
//	 * @return edited graph if qt found; original graph if no solution is found
//	 */
//	public Graph<V, Pair<V>> qtEditConnectedComponents(Graph<V, Pair<V>> G, int bound)
//	{
//		
//		//keep proper degree order as an ArrayList<LinkedList<vertex>>
//		ArrayList<LinkedList<V>> deg = search.degSequenceOrder(G);
//		
//		
//		//start with a full minMoves
//		branchingReturnC<V> minMoves = new branchingReturnC<V>(G, deg);
//		minMoves.setChanges(fillMyEdgeSet(G, bound));
//		minMoves.setMinMoves(minMoves);
//		branchingReturnC<V> goal = new branchingReturnC<V>(G, deg, minMoves);
//		
//		//branch on G with degree ordering deg
//		goal = branchingCC(goal);
//		System.out.println("Number of moves: " + goal.getMinMoves().getChanges());
//		
//		qtGenerate<V> gen = new qtGenerate<V>();
//		
//		Graph<V, Pair<V>> rtn = gen.applyMoves(clone.deepClone(goal.getG()), goal.getMinMoves().getChanges());
//
//		//return QT graph if edit succeeds
//		if (search.isQT(rtn))
//			return rtn;
//		else
//			//otherwise return original graph
//			return goal.getG();
//		
//	}
//	
//	/**
//	 * branch using connected components
//	 * 
//	 * @param s current search state
//	 * @return a branching state containing the graph, degree order and edited edge set
//	 */
//	private branchingReturnC<V> branchingCC(branchingReturnC<V> s)
//	{
////		Graph<V, Pair<V>> G = s.getG();
////		ArrayList<LinkedList<V>> deg = s.getDeg();
////		LinkedList<myEdge<V>> changes = s.getChanges();
////		branchingReturnC<V> minMoves = s.getMinMoves();
//		
//		
//		//check if graph is QT
//		ArrayList<V> t = search.flattenAndReverseDeg(s.getDeg());
//		
//		lexReturnC<V> lexSearch = search.qtLexBFSComponents(s.getG(), t);
//		
//		//qt graph has been found
//		if (lexSearch.isTarget())
//		{
//			//update the minMoves list
//			if (s.getChanges().size() < s.getMinMoves().getChanges().size())
//			{
//				//make a new minMoves to store
//				branchingReturnC<V> newMin = new branchingReturnC<V>(s.getG(), s.getDeg(), clone.deepClone(s.getChanges()));
//				newMin.setMinMoves(newMin);
//				s.setMinMoves(newMin);
//			}
//
//			return s;
//		}
//		//branch on found P4 or C4
//		else
//		{	
//			//check if minMoves is a better choice than current state of search
//			if (s.getMinMoves().getChanges().size() > s.getChanges().size())
//			{
//				branchingReturnC<V> rtn = componentSplit(s, lexSearch);
//				return rtn;
//			}
//			//min moves is a better solution
//			else
//				return s.getMinMoves();
//		}
//	}
//	
//	/**
//	 * split graph into components and edit each individual component
//	 * @param s search state
//	 * @param lexSearch result of lexBFS search
//	 * @return search state
//	 */
//	private branchingReturnC<V> componentSplit(branchingReturnC<V> s, lexReturnC<V> lexSearch)
//	{				
//		//search yields only one connected component, branch on one component
//		if (lexSearch.isConnected())
//		{
//			return branchOnCC(s, lexSearch);
//		}
//		//multiple connected components exist
//		else
//		{
//			//build graphs from connected components
//			Graph<V, Pair<V>> gWtihForbidden = connectedCFromVertexSet(s.getG(), lexSearch.getcComponents().removeLast());
//			
//			LinkedList<Graph<V, Pair<V>>> cGraphs = new LinkedList<Graph<V, Pair<V>>>();
//			LinkedList<branchingReturnC<V>> results = new LinkedList<branchingReturnC<V>>();
//			for (HashSet<V> l : lexSearch.getcComponents())
//			{
//				cGraphs.add(connectedCFromVertexSet(s.getG(), l));
//			}
//			//branch on known forbidden structure
//			
//			//fill new minMoves with entire edge set
//			branchingReturnC<V> min = new branchingReturnC<V>(gWtihForbidden, s.getDeg());
//			//bound the search by the best solution so far
//			min.setChanges(fillMyEdgeSet(gWtihForbidden, s.getMinMoves().getChanges().size() - s.getChanges().size()));
//			min.setMinMoves(min);
//			results.add(branchOnCC(new branchingReturnC<V>(gWtihForbidden, search.degSequenceOrder(gWtihForbidden), min), lexSearch));
//			//branch on the rest of the graphs
//			for (Graph<V, Pair<V>> g : cGraphs)
//			{
//				//if component is large enough to care
//				if (g.getVertexCount() > 3)
//				{
//					//fill new minMoves with bounded edge set of component
//					min = new branchingReturnC<V>(g, s.getDeg());
//					min.setChanges(fillMyEdgeSet(g, s.getMinMoves().getChanges().size() - s.getChanges().size()));
//					min.setMinMoves(min);
//					results.add(branchingCC(new branchingReturnC<V>(g,search.degSequenceOrder(g), new LinkedList<myEdge<V>>(), min)));
//				}
//				//don't care about branching on this but still need it to build up the solution later
//				else
//				{
//					//empty minMoves 
//					min = new branchingReturnC<V>(g, s.getDeg());
//					min.setMinMoves(min);
//					results.add(new branchingReturnC<V>(g, search.degSequenceOrder(g), min));
//				}		
//			}
//			
//			//construct new minMoves from all old ones
//			min = new branchingReturnC<V>(s.getG(), s.getDeg(), min);
//			//throw all minMoves into a HashSet, so they don't have duplicates
//			HashSet<myEdge<V>> temp = new HashSet<myEdge<V>>();
//			for (branchingReturnC<V> r : results)
//			{
//				temp.addAll(r.getMinMoves().getChanges());
//			}
//			min.getChanges().addAll(temp);
//			min.getChanges().addAll(s.getChanges());		
//			
//			return min;
//		}
//	}
//
//	/**
//	 * edit out a P4/C4 by adding or removing edges and branch further
//	 * @param s search state
//	 * @param searchResult lexBFS result
//	 * @return search state
//	 */
//	private branchingReturnC<V> branchOnCC(branchingReturnC<V> s, lexReturnC<V> searchResult)
//	{
//		
//		//C4 has been found
//		ArrayList<V> lexResult = searchResult.getCertificate().getVertices();
//		if (searchResult.getCertificate().getFlag() == -1)
//		{
//			//result of adding 1 edge to break C4
//			//if we did not remove an edge that is about to be added
//			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false)))
//			{
//				branchingReturnC<V> c4Add1 = branchingCC(c4p4AddResult(s, lexResult.get(0), lexResult.get(2)));
//				//revert changes
//				c4p4AddRevert(s, lexResult.get(0), lexResult.get(2));
//				if (c4Add1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
//				{
//					s.setMinMoves(c4Add1.getMinMoves());
//				}
//			}
//			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
//			{
//				branchingReturnC<V> c4Add2 = branchingCC(c4p4AddResult(s, lexResult.get(1), lexResult.get(3)));
//				//revert changes
//				c4p4AddRevert(s, lexResult.get(1), lexResult.get(3));
//				if (c4Add2.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
//				{
//					s.setMinMoves(c4Add2.getMinMoves());
//				}
//			}
//			//results of removing 2 edges to break C4
//			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
//			{
//				branchingReturnC<V> c4Remove0 = branchingCC(c4Delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2)));
//				//revert change to global graph
//				c4Delete2Revert(s, lexResult.get(0), lexResult.get(1), lexResult.get(1), lexResult.get(2));
//				if (c4Remove0.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
//				{
//					s.setMinMoves(c4Remove0.getMinMoves());
//				}
//			}
//			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
//			{
//				branchingReturnC<V> c4Remove1 = branchingCC(c4Delete2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3)));
//				//revert change to global graph
//				c4Delete2Revert(s, lexResult.get(0), lexResult.get(3), lexResult.get(2), lexResult.get(3));
//				if (c4Remove1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
//				{
//					s.setMinMoves(c4Remove1.getMinMoves());
//				}
//			}
//			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
//			{
//				branchingReturnC<V> c4Remove2 = branchingCC(c4Delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3)));
//				//revert change to global graph
//				c4Delete2Revert(s, lexResult.get(0), lexResult.get(1), lexResult.get(2), lexResult.get(3));
//				if (c4Remove2.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
//				{
//					s.setMinMoves(c4Remove2.getMinMoves());
//				}
//			}
//			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
//			{
//				branchingReturnC<V> c4Remove3 = branchingCC(c4Delete2Result(s, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2)));
//				//revert change to global graph
//				c4Delete2Revert(s, lexResult.get(0), lexResult.get(3), lexResult.get(1), lexResult.get(2));
//				if (c4Remove3.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
//				{
//					s.setMinMoves(c4Remove3.getMinMoves());
//				}
//			}
//			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
//			{
//				branchingReturnC<V> c4Remove4 = branchingCC(c4Delete2Result(s, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3)));
//				//revert change to global graph
//				c4Delete2Revert(s, lexResult.get(1), lexResult.get(2), lexResult.get(2), lexResult.get(3));
//				if (c4Remove4.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
//				{
//					s.setMinMoves(c4Remove4.getMinMoves());
//				}
//			}
//			
//			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)) && !s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(3)), true)))
//			{
//				branchingReturnC<V> c4Remove5 = branchingCC(c4Delete2Result(s, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3)));
//				//revert change to global graph
//				c4Delete2Revert(s, lexResult.get(0), lexResult.get(1), lexResult.get(0), lexResult.get(3));
//				if (c4Remove5.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
//				{
//					s.setMinMoves(c4Remove5.getMinMoves());
//				}
//			}
//			return s.getMinMoves();
//		}
//		//P4 has been found
//		else
//		{
//			//see if any vertices of P4 have a deg of 1 (if so, branch only on removing those ones)
//			int deg0 = s.getG().degree(lexResult.get(0));
//			int deg3 = s.getG().degree(lexResult.get(3));
//			
//			if (deg0 == 1 || deg3 == 1)
//			{
//				//branch only on the deletion of removing the first element of P4
//				if (deg0 == 1)
//				{
//					if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)))
//					{
//						branchingReturnC<V> p4Remove0 = branchingCC(p4DeleteResult(s, lexResult.get(0), lexResult.get(1)));
//						//revert changes to global graph
//						p4DeleteRevert(s,lexResult.get(0), lexResult.get(1));
//						if (p4Remove0.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
//						{
//							s.setMinMoves(p4Remove0.getMinMoves());
//						}
//						return s.getMinMoves();
//					}
//				}
//				
//				if (deg3 == 1)
//				{
//					if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
//					{
//						branchingReturnC<V> p4Remove2 = branchingCC(p4DeleteResult(s, lexResult.get(2), lexResult.get(3)));
//						//revert changes to global graph
//						p4DeleteRevert(s,lexResult.get(2), lexResult.get(3));
//						if (p4Remove2.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
//						{
//							s.setMinMoves(p4Remove2.getMinMoves());
//						}
//					}
//					return s.getMinMoves();
//				}
//			}
//			
//			//------------------------------------------------------------------------------------------------------------
//			//add an edge to break P4
//			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(2)), false)))
//			{
//				branchingReturnC<V> p4Add0 = branchingCC(c4p4AddResult(s, lexResult.get(0), lexResult.get(2)));
//				//revert changes
//				c4p4AddRevert(s, lexResult.get(0), lexResult.get(2));
//				if (p4Add0.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
//				{
//					s.setMinMoves(p4Add0.getMinMoves());
//				}
//			}
//			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(3)), false)))
//			{
//				branchingReturnC<V> p4Add1 = branchingCC(c4p4AddResult(s, lexResult.get(1), lexResult.get(3)));
//				//revert changes
//				c4p4AddRevert(s, lexResult.get(1), lexResult.get(3));
//				if (p4Add1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
//				{
//					s.setMinMoves(p4Add1.getMinMoves());
//				}
//			}
//			//remove an edge to break P4
//			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(0), lexResult.get(1)), true)))
//			{
//				branchingReturnC<V> p4Remove0 = branchingCC(p4DeleteResult(s, lexResult.get(0), lexResult.get(1)));
//				//revert changes to global graph
//				p4DeleteRevert(s,lexResult.get(0), lexResult.get(1));
//				if (p4Remove0.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
//				{
//					s.setMinMoves(p4Remove0.getMinMoves());
//				}
//			}
//			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(1), lexResult.get(2)), true)))
//			{
//				branchingReturnC<V> p4Remove1 = branchingCC(p4DeleteResult(s, lexResult.get(1), lexResult.get(2)));
//				//revert changes to global graph
//				p4DeleteRevert(s,lexResult.get(1), lexResult.get(2));
//				if (p4Remove1.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
//				{
//					s.setMinMoves(p4Remove1.getMinMoves());
//				}
//			}
//			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(lexResult.get(2), lexResult.get(3)), true)))
//			{
//				branchingReturnC<V> p4Remove2 = branchingCC(p4DeleteResult(s, lexResult.get(2), lexResult.get(3)));
//				//revert changes to global graph
//				p4DeleteRevert(s,lexResult.get(2), lexResult.get(3));
//				if (p4Remove2.getMinMoves().getChanges().size() < s.getMinMoves().getChanges().size())
//				{
//					s.setMinMoves(p4Remove2.getMinMoves());
//				}
//			}
//			return s.getMinMoves();
//		}
//	}
//	
//}
