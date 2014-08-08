package greedy;

import java.util.ArrayList;
import java.util.HashSet;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import abstractClasses.Certificate;
import abstractClasses.GreedyEdit;
import branch.qtBranch;
import certificate.qtCertificateC;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class maxObsGreedy<V> extends GreedyEdit<V> 
{

	public maxObsGreedy(qtBranch<V> bStruct) {
		super(bStruct);
	}

	/**
	 * Greedily edit the graph until no more benefitial moves can be made
	 */
	@Override
	public void greedyEdit(branchingReturnC<V> s) 
	{

		//get original number of obstructions
		int og = 0;
		int newObs = 0;
		int best = Integer.MAX_VALUE;
		myEdge<V> move = null;
		
		//for every two vertices, see if adding a non-existing edge or removing an edge decreases the obstruction count
		do
		{
			og = getObstructionCount(s.getG());
			best = og;
			for (V v0 : s.getG().getVertices())
			{
				for (V v1 : s.getG().getVertices())
				{
					if (v0 == v1)
						continue;
					
					//if an edge between v0 and v1 exists, remove it and count the number of obstructions
					if (s.getG().isNeighbor(v0, v1))
					{
						if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(v0, v1), true)))
						{
							bStruct.deleteResult(s, v0, v1);
							newObs = getObstructionCount(s.getG());
							if (newObs < best)
							{
								best = newObs;
								move = new myEdge<V>(new Pair<V>(v0, v1), false);
							}
							bStruct.revert(s);
						}
							
					}
					//add an edge
					else
					{
						if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(v0, v1), false)))
						{
							bStruct.addResult(s, v0, v1);
							newObs = getObstructionCount(s.getG());
							if (newObs < best)
							{
								best = newObs;
								move = new myEdge<V>(new Pair<V>(v0, v1), true);
							}
							bStruct.revert(s);
						}
					}
				}
			}
			
			//apply move if it is good
			if (move != null && move.isFlag() && best < og)
			{
				bStruct.addResult(s, move.getEdge().getFirst(), move.getEdge().getSecond());
			}
			else if (move != null && !move.isFlag() && best < og)
			{
				bStruct.deleteResult(s, move.getEdge().getFirst(), move.getEdge().getSecond());
			}
		}while (best < og);
		
		
		
		
	}
	
	public int getObstructionCount(Graph<V, Pair<V>> g)
	{
		HashSet<Certificate<V>> cert = new HashSet<Certificate<V>>();
		
		for (V v0 : g.getVertices())
		{
			for (V v1 : g.getNeighbors(v0))
			{
				for (V v2 : g.getNeighbors(v1))
				{
					if (v2.equals(v0) || g.isNeighbor(v0, v2))
						continue;
					
					for (V v3 : g.getNeighbors(v2))
					{
						if (v3.equals(v0) || v3.equals(v1) || g.isNeighbor(v3, v1))
						{
							continue;
						}
						ArrayList<V> obstruction = new ArrayList<V>();
						obstruction.add(v0);
						obstruction.add(v1);
						obstruction.add(v2);
						obstruction.add(v3);
						
						
						if (g.isNeighbor(v0, v3))
						{
							
							cert.add(new qtCertificateC<V>(obstruction, -1));
						}
						else
						{
							cert.add(new qtCertificateC<V>(obstruction, -2));
						}
					}
				}
			}
		}
		
		return cert.size();
	}

}
