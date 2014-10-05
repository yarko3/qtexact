package branch;

import java.util.ArrayList;

import qtUtils.branchingReturnC;
import qtUtils.myEdge;
import search.diQTSearch;
import abstractClasses.Branch;
import abstractClasses.SearchResult;
import controller.Controller;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class diQTBranch<V> extends Branch<V> 
{

	public diQTBranch(Controller<V> controller) {
		super(controller);
		search = new diQTSearch<V>();
		output = controller.getOutputFlag();
		
		//graph must be directed
		directed = true;
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
		
		ArrayList<V> obst = sResult.getCertificate().getVertices();
		double oldPercent = s.getPercent();
		
		//2 in 1 happened
		if (sResult.getCertificate().getFlag() == -10)
		{
			int ruleCount = 4;
			
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
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(obst.get(2), obst.get(1)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				controller.branch(deleteResult(s, obst.get(2), obst.get(1)));
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
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(obst.get(2), obst.get(0)), false, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				controller.branch(addResult(s, obst.get(2), obst.get(0)));
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
		//a directed C3 happened
		else if (sResult.getCertificate().getFlag() == -11)
		{
			int ruleCount = 3;
			
			if (!s.getChanges().contains(new myEdge<V>(new Pair<V>(obst.get(2), obst.get(0)), true, directed)))
			{
				if (output)
				{
					//change progress percent
					s.setPercent(oldPercent / ruleCount);
				}
				controller.branch(deleteResult(s, obst.get(2), obst.get(0)));
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
		//transitive property was broken
		else
		{
			int ruleCount = 3;
			
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
		
		return s;
		
	}



	@Override
	public branchingReturnC<V> deleteResult(branchingReturnC<V> s, V v0, V v1) {
		Pair<V> edge = s.getG().findEdge(v0, v1);
		
		s.getG().removeEdge(edge);
		
		s.getChanges().add(new myEdge<>(new Pair<V>(v0, v1), false, directed));
		
		return s;
	}

	@Override
	public branchingReturnC<V> addResult(branchingReturnC<V> s, V v0, V v1) {
		Pair<V> edge = new Pair<V>(v0, v1);
		s.getG().addEdge(edge, v0, v1);
		
		s.getChanges().add(new myEdge<>(new Pair<V>(v0, v1), true, directed));
		
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
		s.getG().removeEdge(s.getG().findEdge(v0, v1));
		
	}


}
