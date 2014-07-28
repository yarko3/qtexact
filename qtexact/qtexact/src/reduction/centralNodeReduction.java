package reduction;

import java.util.LinkedList;
import java.util.Stack;

import qtUtils.branchingReturnC;
import abstractClasses.Reduction;
import branch.qtBranch;

public class centralNodeReduction<V> extends Reduction<V> 
{
	private qtBranch<V> bStruct;
	private Stack<LinkedList<V>> removedVertices;
	
	public centralNodeReduction(qtBranch<V> b)
	{
		removedVertices = new Stack<LinkedList<V>>();
		bStruct = b;
	}
	
	/**
	 * find central vertices and remove them to continue traversal
	 */
	@Override
	public branchingReturnC<V> reduce(branchingReturnC<V> s) 
	{
		//nodes to be deleted and placed on stack for retrieval later
		LinkedList<V> removed = new LinkedList<V>();
		
		//check if there are any vertices with max degree
		if (s.getDeg().size() == s.getG().getVertexCount())
		{
			//store all vertices to be removed
			removed.addAll(s.getDeg().get(s.getG().getVertexCount() - 1));
			
			//delete nodes
			for (V d : removed)
			{
				bStruct.removeVertex(s, d);
			}
		}
		removedVertices.push(removed);
		return s;
	}

	@Override
	public branchingReturnC<V> revertReduce(branchingReturnC<V> s) {
		//last entered LinkedList
		LinkedList<V> removed = removedVertices.pop();
		
		//if some node was removed
		if (!removed.isEmpty())
		{
			for (V v0 : removed)
			{
				//add vertex
				bStruct.addVertex(s.getG(), s.getDeg(), v0);
				
				//add all edges
				for (V v1 : s.getG().getVertices())
				{
					//do not add a self-edge
					if (v0 != v1)
					{
						bStruct.addEdge(s.getG(),s.getDeg(), v0, v1);
					}
				}
				
			}
		}
		
		return s;
	}
	

}
