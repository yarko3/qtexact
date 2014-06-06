package reduction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import abstractClasses.Certificate;
import abstractClasses.Reduction;
import branch.qtBranch;
import certificate.qtCertificateC;

import com.rits.cloning.Cloner;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class edgeBoundReduction<V> extends Reduction<V> 
{
	Cloner clone = new Cloner();
	qtBranch<V> bStruct;
	Stack<Integer> stack;
	
	
	public edgeBoundReduction(qtBranch<V> b)
	{
		super();
		bStruct = b;
		stack = new Stack<Integer>();
	}
	
	@Override
	public branchingReturnC<V> reduce(branchingReturnC<V> s) 
	{
		//make a copy to be edited
		//TODO may not be the worth it if cloning is too expensive
		branchingReturnC<V> rtn = clone.deepClone(s);
		
		int delCount = 0;
		//for every edge, find C4s and P4s
		for (Pair<V> e : s.getG().getEdges())
		{
			//if leaving the edge is out of bounds, remove this edge
			if (getObstructionCount(e, s.getG()) > s.getMinMoves().getChanges().size() - s.getChanges().size() && !s.getChanges().contains(new myEdge<V>(e, false)))
			{
				bStruct.p4DeleteResult(rtn, e.getFirst(), e.getSecond());
				delCount++;
			}
		}
		stack.push(delCount);
		return rtn;
		
	}
	
	private int getObstructionCount(Pair<V> e, Graph<V, Pair<V>> g)
	{
		//endpoints
		V v0 = e.getFirst();
		V v1 = e.getSecond();
		
		//neighbours of endpoints
		
		LinkedList<V> v0Neighbours = new LinkedList<V>();
		v0Neighbours.addAll(g.getNeighbors(v0));
		LinkedList<V> v1Neighbours = new LinkedList<V>();
		v1Neighbours.addAll(g.getNeighbors(v1));
		
		//remove common neighbours
		HashSet<V> all = new HashSet<V>();
		HashSet<V> common = new HashSet<V>();
		
		all.addAll(v0Neighbours);
		
		//get all the duplicates in common, while all neighbours are stored in all
		for (V v : v1Neighbours)
		{
			if (!all.add(v))
				common.add(v);
		}
		//remove duplicates
		
		v0Neighbours.removeAll(common);
		v1Neighbours.removeAll(common);
		
		
		//get all C4s and P4s
		HashSet<Certificate<V>> c4 = new HashSet<Certificate<V>>();
		HashSet<Certificate<V>> p4 = new HashSet<Certificate<V>>();
		
		//find C4 by looking for an edge between any two nodes from opposite neighbours
		for (V n0 : v0Neighbours)
		{
			for (V n1 : v1Neighbours)
			{
				if (n0 != v0 && n0 != v1 && n1 != v0 && n1 != v1)
				{
					
					if (g.containsEdge(new Pair<V>(n0, n1)) || g.containsEdge(new Pair<V>(n1, n0)))
					{
						ArrayList<V> obstruction = new ArrayList<V>();
						obstruction.add(n0);
						obstruction.add(v0);
						obstruction.add(v1);
						obstruction.add(n1);
						
						Certificate<V> cert = new qtCertificateC<V>(obstruction, -1);
						c4.add(cert);
						
					}
					//a P4 was found instead
					else
					{
						ArrayList<V> obstruction = new ArrayList<V>();
						obstruction.add(n0);
						obstruction.add(v0);
						obstruction.add(v1);
						obstruction.add(n1);
						
						Certificate<V> cert = new qtCertificateC<V>(obstruction, -2);
						p4.add(cert);
					}
				}
					
			}
		}
		//TODO can use the c4 and p4 information later
		return c4.size() + p4.size();
	}

	@Override
	public branchingReturnC<V> revertReduce(branchingReturnC<V> s) 
	{
		int delCount = stack.pop();
		
		for (int i = 0; i < delCount; i++)
		{
			bStruct.p4DeleteRevert(s);
		}
		return s;
	}

}
