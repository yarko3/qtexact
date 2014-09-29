package abstractClasses;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public abstract class GreedyEdit<V> extends Dive<V> 
{
	
	/**
	 * edit the given graph to some sort of conclusion using a greedy approach
	 * @param s edit state
	 */
	public void greedyEdit(branchingReturnC<V> s) 
	{
		//edit greedily until 1000 moves
		greedyEdit(s, 1000);
	}
	
	/**
	 * greedily edit graph until bound number of moves are performed
	 * @param s edit state
	 * @param bound
	 */
	public void greedyEdit(branchingReturnC<V> s, int bound) {
		//get original number of obstructions
		int og = 0;
		int newObs = 0;
		int best;
		myEdge<V> move = null;
		
		//add edges in both directions if graph is directed
		boolean isDirected = s.getG() instanceof DirectedGraph;
		
		//number of greedy edits made
		int count = 0;
		
		//for every two vertices, see if adding a non-existing edge or removing an edge decreases the obstruction count
		do
		{
			count++;
			
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
							//try to add an edge in one direction
							bStruct.addResult(s, v0, v1);
							newObs = getObstructionCount(s.getG());
							if (newObs < best)
							{
								best = newObs;
								move = new myEdge<V>(new Pair<V>(v0, v1), true);
							}
							bStruct.revert(s);
							
							//if graph is directed, try to add an edge in the other direction
							if (isDirected)
							{
								bStruct.addResult(s, v1, v0);
								newObs = getObstructionCount(s.getG());
								if (newObs < best)
								{
									best = newObs;
									move = new myEdge<V>(new Pair<V>(v1, v0), true);
								}
								bStruct.revert(s);
							}
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
			System.out.println("Greedy solution: " + s.getChanges().size());
			System.out.println("Number of obstructions left: " + best);
			
		}while (best < og && count < bound);
	}
	
	/**
	 * get obstruction count, depending on problem
	 * @param g graph
	 * @return number of obstructions
	 */
	protected abstract int getObstructionCount(Graph<V, Pair<V>> g);

	/**
	 * constructor
	 * @param b branching structure
	 */
	public GreedyEdit(Branch<V> b)
	{
		super(b);
	}

	
	/**
	 * greedy dive
	 */
	public void dive(branchingReturnC<V> s)
	{
		greedyEdit(s);
	}
	
	/**
	 * greedy dive
	 */
	public void dive(branchingReturnC<V> s, int bound)
	{
		greedyEdit(s, bound);
	}
}
