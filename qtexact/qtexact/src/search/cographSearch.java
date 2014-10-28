package search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import qtUtils.branchingReturnC;
import qtUtils.lexReturnC;
import abstractClasses.Certificate;

import com.rits.cloning.Cloner;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class cographSearch<V>  extends LBFS<V> 
{
	//cloner used for cloning
	Cloner clone = new Cloner();
	
	@Override
	public lexReturnC<V> search(Graph<V, Pair<V>> G, ArrayList<V> t) {
		
		//t is an arbitrary ordering
		
		//run LexBFS with t
		lexReturnC<V> firstPass = LexBFSplus(G, t);
		
		
		//run LexBFSminus with firstPass ordering
		lexReturnC<V> secondPass = LexBFSminus(G, clone.deepClone(firstPass.getList()));
		
		//do cograph check
		LinkedList<V> NSPplus = NSP(G, firstPass.getList());
		LinkedList<V> NSPminus = NSP(utils.complement(G), secondPass.getList());
		
		//find complement of NSPminus, because it gives a complement P4
		if (NSPminus != null)
		{
			LinkedList<V> temp = new LinkedList<V>();
			temp.add(NSPminus.get(2));
			temp.add(NSPminus.get(0));
			temp.add(NSPminus.get(3));
			temp.add(NSPminus.get(1));
			
			NSPminus = temp;
		}
		
		
		// get connected components
		Set<Set<V>> cComp = cluster.transform(G);
		
		
		//if success, return success
		if (NSPplus == null && NSPminus == null)
		{
			//construct return
			return new lexReturnC<V>(null, null, true, (cComp.size() == 1), cComp);
		}
		else
		{
			if (NSPplus != null)
			{
				Certificate<V> cert = new Certificate<V>(NSPplus, -2);
				return new lexReturnC<V>(null, cert, false, (cComp.size() == 1), cComp);
			}
			else
			{
				Certificate<V> cert = new Certificate<V>(NSPminus, -2);
				return new lexReturnC<V>(null, cert, false, (cComp.size() == 1), cComp);
			}
		}
		
	}

	private ArrayList<V> getArbitraryOrdering(Graph<V, Pair<V>> g)
	{
		//create an ArrayList of vertices in no particular order
		ArrayList<V> ordering = new ArrayList<V>();
		
		Iterator<V> i = g.getVertices().iterator();
		//pull an ordering of vertices
		while (i.hasNext())
		{
			ordering.add(i.next());
		}
		
		return ordering;
	
	}
	
	
	@Override
	public boolean isTarget(Graph<V, Pair<V>> g) {
		
		return search(g, this.getArbitraryOrdering(g)).isTarget();
	}

	@Override
	protected lexReturnC<V> searchPrep(branchingReturnC<V> s) {
		return search(s.getG(), this.getArbitraryOrdering(s.getG()));
	}

	@Override
	public lexReturnC<V> search(branchingReturnC<V> s) {
		return search(s.getG(), this.getArbitraryOrdering(s.getG()));
	}
	
	public lexReturnC<V> search(Graph<V, Pair<V>> g)
	{
		return search(g, this.getArbitraryOrdering(g));
	}
	
	

	/**
	 * check for P4s
	 * @param g graph
	 * @param t ordering
	 * @return search result
	 */
	protected LinkedList<V> NSP(Graph<V, Pair<V>> g, ArrayList<V> t)
	{
		//if an obstruction exists, store it here
		LinkedList<V> obs = null;
		
		//covert the given ordering to a lookup table with the key being the vertex
		//and value being the location in the ordering
		HashMap<V, Integer> orderingMap = new HashMap<V, Integer>();
		for (int i = 0; i < t.size(); i++)
		{
			orderingMap.put(t.get(i), i);
		}
		
		
		//sort the adjacency list of every vertex according to t ordering
		HashMap<V, LinkedList<V>> adjList = new HashMap<V, LinkedList<V>>();
		//initialize adjacency lists
		for (V v : t)
		{
			adjList.put(v, new LinkedList<V>());
		}
		
		
		
		//collection for neighbours
		Collection<V> n;
		
		for (int i = 0; i < t.size(); i++)
		{
			V v = t.get(i);
			
			//get neighbourhood of vertex
			n = g.getNeighbors(v);
			
			//for every element in n, add v as the next element in their adjacency lists
			for (V neighbour : n)
			{
				adjList.get(neighbour).addLast(v);
			}
		}
		
		//print slices
//		for (V v : t)
//		{
//			System.out.println(v + " slice: " + slice(v, t, adjList));
//		}
		
		
		//here comes the NSP check
		for (int k = 0; k < t.size() - 1; k++)
		{
			//vertex being checked
			V v = t.get(k);
			
			LinkedList<V> vSlice = slice(v, t, adjList);
			
			LinkedList<LinkedList<V>> subSlices = nonNeighbourSubSlices(v, t, adjList);
			
			
//			//check if more than 1 vertex in graph 
//			if (t.size() < 3)
//				break;
			
			//internal counter
			int i = 0;
			
			
			//A <- N<(v1) INTERSECT S^A(v)
			
			//N<(v1)
			LinkedList<V> NbeforeVi;
			if (subSlices.isEmpty())
				NbeforeVi = new LinkedList<V>();
			else
				NbeforeVi = nBeforeIndex(subSlices.get(i).getFirst(), 
						vSlice.indexOf(subSlices.get(i).getFirst()), 
								adjList, vSlice);
			
			//LinkedList<V> NbeforeV = nBeforeIndex(t.get(i), i, adjList, t);
			
			//S^A(v)
			Set<V> nINTERSECTSlice = nINTERSECTSlice(v, vSlice, adjList);
			
			
			Set<V> A = new HashSet<V>();
			A.addAll(NbeforeVi);
			
			
			//union the two together
			A.retainAll(nINTERSECTSlice);
			
			//while a next vertex exits after vi
			while (i+1 < subSlices.size())
			{
				//B <- N<(vi+1) INTERSECT S^A(v)
				Set<V> B = new HashSet<V>();
				
				//N<(vi+1)
				
				B.addAll(nBeforeIndex(subSlices.get(i+1).getFirst(), 
						vSlice.indexOf(subSlices.get(i+1).getFirst()), 
						adjList, vSlice));
				
				//intersect them together
				B.retainAll(vSlice);
				
				//if A contains all of B,
				if (A.containsAll(B))
				{
					A = B;
					i++;
				}
				//P4 was found
				else
				{
					//construct P4
					return reportP4(v, i, adjList, t, g);
				}
			}
			
		}
		
		return null;
	}
	
	
	/**
	 * computes N < (vi) of some v
	 * @param v vertex whose neighbourhood is checked
	 * @param i the delimiter vertex index
	 * @param map map of ordered neighbourhoods according to t
	 * @param t ordering
	 * @return list of neighbours of v that appear in t before vi
	 */
	private LinkedList<V> nBeforeIndex(V v, int i, Map<V, LinkedList<V>> map, List<V> t)
	{
		//list to be returned
		LinkedList<V> rtn = new LinkedList<V>();
		//get ordered neighbours of v
		LinkedList<V> N = map.get(v);
		
		//keep track of neighbour vertices checked
		int nIndex = 0;
		
		
		//for every 
		for (int j = 0; j < i; j++)
		{
			V temp = t.get(j);
			
			if (nIndex == N.size())
			{
				//reached end of traversal
				return rtn;
			}
			
			//a neighbour was found, add it to list
			if (temp.equals(N.get(nIndex)))
			{
				rtn.addLast(N.get(nIndex));
				nIndex++;
			}
			
		}
		
		return rtn;
	}

	
	/**
	 * calculate the union of slice and neighbourhood of a vertex
	 * @param v vertex
	 * @param map map of ordered neighbourhoods
	 * @param t ordering
	 * @return a union of slice and neighbourhood
	 */
	private Set<V> nINTERSECTSlice(V v, LinkedList<V> slice, HashMap<V, LinkedList<V>> map)
	{
		//retrieve neighbourhood
		LinkedList<V> N = map.get(v);
		
		
		//convert neighbours to a set (for set operations)
		Set<V> set = new HashSet<V>();
		set.addAll(N);
		
		//union
		set.retainAll(slice);
		
		return set;
		
	}

	
	/**
	 * compute the slice(v) of t
	 * @param v vertex
	 * @param t ordering
	 * @return slice
	 */
	private LinkedList<V> slice(V u, List<V> t, Map<V, LinkedList<V>> mapping)
	{
		//list of the slice to be returned
		LinkedList<V> rtn = new LinkedList<V>();
		
		//u is contained within its own slice
		rtn.add(u);
		
		//get index of u
		int i = t.indexOf(u);
		LinkedList<V> NiI = nBeforeIndex(u, i, mapping, t);
		LinkedList<V> NiJ;
		
		for (int j = i+1; j < t.size(); j++)
		{
			V vertexJ = t.get(j);
			NiJ = nBeforeIndex(vertexJ, i, mapping, t);
			
			if (NiI.containsAll(NiJ) && NiJ.containsAll(NiI))
			{
				rtn.addLast(vertexJ);
			}
			else
				break;
		}	
			
		return rtn;
	}
	
	private LinkedList<V> reportP4(V v, int j, HashMap<V, LinkedList<V>> adjList, ArrayList<V> t, Graph<V, Pair<V>> g)
	{
		LinkedList<V> rtn = new LinkedList<V>();
		
		LinkedList<LinkedList<V>> subSlices = nonNeighbourSubSlices(v, t, adjList);
		
		LinkedList<V> vSlice = slice(v, t, adjList);
		
		
		
		//Choose w is an element of (Nl(vj) \ Nl(vj+1)) UNION SA(v)
		
		//Nl(vj)
		LinkedList<V> nbeforevj = nBeforeIndex(subSlices.get(j).getFirst(), 
				vSlice.indexOf(subSlices.get(j).getFirst()), 
				adjList, vSlice);
		
		LinkedList<V> nbeforevjCOPY = clone.deepClone(nbeforevj);
		
		//Nl(vj+1)
		LinkedList<V> nbeforevjplus1 = nBeforeIndex(subSlices.get(j+1).getFirst(), 
				vSlice.indexOf(subSlices.get(j+1).getFirst()), 
				adjList, vSlice);
		
		
		
		//SA(V)
		Set<V> nUnionSlice = nINTERSECTSlice(v, vSlice, adjList);
		
		//(Nl(vj) \ Nl(vj+1))
		nbeforevj.removeAll(nbeforevjplus1);
		
		//intersect
		nUnionSlice.retainAll(nbeforevj);
		
		//get a w
		if (nUnionSlice.isEmpty())
		{
			System.out.println("Search broke in reportP4.");
			throw new NullPointerException();
		}
		V w = nUnionSlice.iterator().next();
		
		//Choose y to be the rightmost vertex such that y ELEMENT OF  Nl(vj+1) \ Nl(vj)
		nbeforevjplus1.removeAll(nbeforevjCOPY);
		
		//choose a y
		V y = nbeforevjplus1.get(nbeforevjplus1.size() - 1);
		
		//build obstruction
		if (!g.isNeighbor(y, v))
		{
			rtn.add(v);
			rtn.add(w);
			rtn.add(y);
			rtn.add(subSlices.get(j+1).getFirst());
			return rtn;
		}
		else if (g.isNeighbor(w, y))
		{
			rtn.add(subSlices.get(j).getFirst());
			rtn.add(w);
			rtn.add(y);
			rtn.add(subSlices.get(j+1).getFirst());
			return rtn;
		}
		else
		{
			rtn.add(y);
			rtn.add(v);
			rtn.add(w);
			rtn.add(subSlices.get(j).getFirst());
			return rtn;
		}
	}
	
	LinkedList<LinkedList<V>> nonNeighbourSubSlices(V v, ArrayList<V> t, HashMap<V, LinkedList<V>> map)
	{
		LinkedList<LinkedList<V>> rtn = new LinkedList<LinkedList<V>>();
		
		//get slice to work with
		LinkedList<V>  slice = slice(v, t, map);
		
		//remove neighbours of v and v from slice
		slice.removeFirst();
		slice.removeAll(map.get(v));
		
		//the rest of vertices in slice are non-neighbours of v and form their own subslices
		while (!slice.isEmpty())
		{
			//add the subslice into rtn
			LinkedList<V> subSlice = slice(slice.getFirst(), t, map);
			slice.removeAll(subSlice);
			rtn.addLast(subSlice);
		}
		
		return rtn;
	}
	
}
