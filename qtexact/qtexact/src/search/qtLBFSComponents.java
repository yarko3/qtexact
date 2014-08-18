package search;

import java.util.ArrayList;
import java.util.Set;

import qtUtils.branchingReturnC;
import qtUtils.lexReturnC;
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * A lexBFS search for quasi threshold graphs while also finding connected components
 * 
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public class qtLBFSComponents<V> extends qtLBFSNoHeuristic<V> 
{
	
	private WeakComponentClusterer<V, Pair<V>> cluster =  new WeakComponentClusterer<V, Pair<V>>();
	
	/** LexBFS search 
	 * 
	 * @param G graph to be tested
	 * @param t initial ordering of vertices
	 * @return final LexBFS ordering of vertices
	 */
	public lexReturnC<V> search(Graph<V, Pair<V>> G, ArrayList<V> t)
	{
		return super.search(G, t);
		
	}
	
	//look for connected components only when last edit was a deletion
	@Override
	public lexReturnC<V> search(branchingReturnC<V> s)
	{
		//look for connected components
		
		//split into connected components
		Set<Set<V>> cComponents = cluster.transform(s.getG());
		
		lexReturnC<V> rtn = searchPrep(s);
		rtn.setcComponents(cComponents);
		rtn.setConnected(cComponents.size() == 1);
		return rtn;
		
	}

}
