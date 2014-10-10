package branch;

import java.util.List;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import search.clusterSearch;
import abstractClasses.Branch;
import abstractClasses.SearchResult;
import controller.Controller;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class clusterBranch<V> extends Branch<V> {

	public clusterBranch(Controller<V> controller) {
		super(controller);
		//set proper search
		search = new clusterSearch<V>();
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
		
		//a P3 was found
		if (sResult.getCertificate().getFlag() == -13)
		{
			int ruleCount = 3;
			
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
			
			
			//one delete
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
			
			//one delete
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
			
		}
		else
		{
			System.out.println("Tried to branch on P3, no P3 in certificate.");
			throw new NullPointerException();
		}
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

}
