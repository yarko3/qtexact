package search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
		LinkedList<V> NSPminus = NSP(utils.complement(G), firstPass.getList());
		
		
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
		}
		
		
		return null;
	}

	@Override
	public boolean isTarget(Graph<V, Pair<V>> g) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected lexReturnC<V> searchPrep(branchingReturnC<V> s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public lexReturnC<V> search(branchingReturnC<V> s) {
		// TODO Auto-generated method stub
		return null;
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
		
		
		//here comes the NSP check
		for (int k = 0; k < t.size(); k++)
		{
			//vertex being checked
			V v = t.get(k);
			
			//check if more than 1 vertex in graph 
			if (t.size() < 2)
				continue;
			
			//internal counter
			int i = 0;
			
			
			//A <- N<(v1) UNION S^A(v)
			
			//N<(v1)
			LinkedList<V> NbeforeV = nBeforeV(v, t.get(i), adjList, t);
			
			//S^A(v)
			Set<V> nUnionSlice = nUnionSlice(v, adjList, t);
			
			
			Set<V> A = new HashSet<V>();
			A.addAll(NbeforeV);
			
			
			//union the two together
			A.retainAll(nUnionSlice);
			
			//while a next vertex exits after vi
			while (i+1 < t.size())
			{
				//B <- N<(vi+1) UNION S^A(v)
				Set<V> B = new HashSet<V>();
				
				//N<(vi+1)
				B.addAll(nBeforeV(v, t.get(i+1), adjList, t));
				
				//union them together
				B.retainAll(nUnionSlice);
				
				//if A contains all of B,
				if (A.retainAll(B))
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
	 * @param vi the delimiter vertex
	 * @param map map of ordered neighbourhoods according to t
	 * @param t ordering
	 * @return list of neighbours of v that appear in t before vi
	 */
	private LinkedList<V> nBeforeV(V v, V vi, Map<V, LinkedList<V>> map, ArrayList<V> t)
	{
		//list to be returned
		LinkedList<V> rtn = new LinkedList<V>();
		//get ordered neighbours of v
		LinkedList<V> N = map.get(v);
		
		//keep track of neighbour vertices checked
		int nIndex = 0;
		
		
		//for every 
		for (int i = 0; i < t.size(); i++)
		{
			V temp = t.get(i);
			
			if (temp.equals(vi) || nIndex + 1 == N.size())
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
	private Set<V> nUnionSlice(V v, HashMap<V, LinkedList<V>> map, ArrayList<V> t)
	{
		//retrieve neighbourhood
		LinkedList<V> N = map.get(v);
		
		//get slice
		LinkedList<V> slice = slice(v, t);
		
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
	private LinkedList<V> slice(V v, ArrayList<V> t)
	{
		//list of the slice to be returned
		LinkedList<V> rtn = new LinkedList<V>();
		
		for (int i = 0; i < t.size(); i++)
		{
			V temp = t.get(i);
			
			//end of slice has been reached
			if (temp.equals(v))
			{
				return rtn;
			}
			
			rtn.addLast(temp);
		}
		
		return rtn;
	}
	
	private LinkedList<V> reportP4(V v, int j, HashMap<V, LinkedList<V>> adjList, ArrayList<V> t, Graph<V, Pair<V>> g)
	{
		LinkedList<V> rtn = new LinkedList<V>();
		
		
		//Choose w is an element of (Nl(vj) \ Nl(vj+1)) UNION SA(v)
		
		//Nl(vj)
		LinkedList<V> nbeforevj = nBeforeV(v, t.get(j), adjList, t);
		
		LinkedList<V> nbeforevjCOPY = clone.deepClone(nbeforevj);
		
		//Nl(vj+1)
		LinkedList<V> nbeforevjplus1 = nBeforeV(v, t.get(j+1), adjList, t);
		
		
		
		//SA(V)
		Set<V> nUnionSlice = nUnionSlice(v, adjList, t);
		
		//(Nl(vj) \ Nl(vj+1))
		nbeforevj.removeAll(nbeforevjplus1);
		
		//intersect
		nUnionSlice.retainAll(nbeforevj);
		
		//get a w
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
			rtn.add(t.get(j+1));
			return rtn;
		}
		else if (g.isNeighbor(w, y))
		{
			rtn.add(t.get(j));
			rtn.add(w);
			rtn.add(y);
			rtn.add(t.get(j+1));
			return rtn;
		}
		else
		{
			rtn.add(y);
			rtn.add(v);
			rtn.add(w);
			rtn.add(t.get(j));
			return rtn;
		}
	}
	
}
