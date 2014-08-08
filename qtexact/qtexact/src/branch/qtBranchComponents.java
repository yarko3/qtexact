package branch;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JFrame;

import qtUtils.branchingReturnC;
import qtUtils.lexReturnC;
import qtUtils.myEdge;
import qtUtils.qtGenerate;
import search.qtLBFS;
import search.qtLBFSComponents;
import abstractClasses.Branch;
import abstractClasses.SearchResult;
import controller.Controller;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;


/**
 * class used for editing to quasi threshold using connected components
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public class qtBranchComponents<V> extends qtAllStruct<V> 
{
	qtGenerate<V> gen = new qtGenerate<V>();
	
	/**
	 * constructor
	 * @param controller
	 */
	public qtBranchComponents(Controller<V> controller) {
		super(controller);
		
		if (controller != null)
		{
			output = controller.getOutputFlag();
		}
		else
			output = false;
		
		//use proper search function
		search = new qtLBFSComponents<V>();
	}


	/**
	 * setup for quasi threshold editing with no heuristic
	 */
	@Override
	public branchingReturnC<V> setup(Graph<V, Pair<V>> G, int bound) {
		
		//keep proper degree order as an ArrayList<LinkedList<vertex>>
		ArrayList<LinkedList<V>> deg = ((qtLBFS<V>) search).degSequenceOrder(G);
		
		//start with a full minMoves
		branchingReturnC<V> minMoves = new branchingReturnC<V>(G, deg);
		minMoves.setChanges(fillMinMoves(minMoves, bound));
		minMoves.setMinMoves(minMoves);
		branchingReturnC<V> goal = new branchingReturnC<V>(G, deg, minMoves);
		//output flags
		if (output)
		{
			goal.setPercent(1);
			controller.setGlobalPercent(0);
		}
		
		return goal;
	}

	/**
	 * branching rules specific to connected components 
	 * first separate into separate graphs and then apply rules
	 */
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult) 
	{
		lexReturnC<V> lex = (lexReturnC<V>) searchResult;
		
		//search yields only one connected component, branch on one component
		if (lex.isConnected())
		{
			return super.branchingRules(s, lex);
		}
		//multiple connected components exist
		else
		{
			//bound
			int bound = s.getMinMoves().getChanges().size() - s.getChanges().size();
			
			
			//build graphs from connected components
			LinkedList<Graph<V, Pair<V>>> cGraphs = new LinkedList<Graph<V, Pair<V>>>();
			LinkedList<branchingReturnC<V>> results = new LinkedList<branchingReturnC<V>>();
			for (Set<V> l : lex.getcComponents())
			{
				cGraphs.add(connectedCFromVertexSet(s.getG(), l));
			}
			
			
			//create future minMoves
			branchingReturnC<V> min = null;
			
			branchingReturnC<V> t;
			//number of components larger than 3 nodes
			int count = 0;
			
			//sort connected components in increasing size
			for (int i = 0; i < cGraphs.size(); i++)
			{
				for (int j = 0; j < i; j++)
				{
					if (cGraphs.get(i).getVertexCount() < cGraphs.get(j).getVertexCount())
					{
						cGraphs.add(j, cGraphs.remove(i));
						continue;
					}
				}
			}
			//remove all trivial components
			while (cGraphs.getFirst().getVertexCount() < 4)
			{
				cGraphs.removeFirst();
			}
			
			//count = cGraphs.size();
			
			//check which components need editing to be qt
			LinkedList<Integer> needEdit = new LinkedList<Integer>();
			
			for (int i  = 0; i < cGraphs.size(); i++)
			{
				Integer temp = lowerBound(cGraphs.get(i));
				
				if (temp != 0)
					count++;
				needEdit.addLast(temp);
			}
			
			
			//branch on the rest of the graphs
			for (int i = 0; i < cGraphs.size(); i++)
			{
				Graph<V, Pair<V>> g = cGraphs.get(i);
				//find the minimum number of moves still required
				int need = 0;
				for (int j = i+1; j < cGraphs.size(); j++)
				{
					need += needEdit.get(j);
				}
				
				
				//does this component need editing and are more moves allowed?
				if (needEdit.get(i) > 0 && bound >= 0 && bound >= need)
				{
					
					//fill new minMoves with bounded edge set of component
					min = new branchingReturnC<V>(g, ((qtLBFS<V>) search).degSequenceOrder(g));
					//construct minMoves from bound and number of moves done - number of moves needed for other components
					min.setChanges(fillMinMoves(min, bound + s.getChanges().size() - need));
					min.setMinMoves(min);
					
					t = new branchingReturnC<V>(g, min.getDeg(), clone.deepClone(s.getChanges()), min);
					//set new percent
					t.setPercent(s.getPercent() / count);
					
					
//						controller.branchStart(g, bound);
//						System.out.println(g);
//						
					
					results.addFirst(controller.branch(t));
					//update bound
					bound -= (t.getMinMoves().getChanges().size() - s.getChanges().size());
				}				
			}
			
			
			//construct new minMoves from all old ones
			min = new branchingReturnC<V>(s.getG(), s.getDeg());
			min.setMinMoves(min);
			//throw all minMoves into a HashSet, so they don't have duplicates
			HashSet<myEdge<V>> temp = new HashSet<myEdge<V>>();
			temp.addAll(s.getChanges());
			for (branchingReturnC<V> r : results)
			{
				r.getMinMoves().getChanges().removeAll(s.getChanges());
				
				temp.addAll(r.getMinMoves().getChanges());
			}
			
			min.getChanges().addAll(temp);
			
			//if new solution is better than current one
			Graph<V, Pair<V>> rtn = gen.applyMoves(Branch.clone.deepClone(s.getG()), min.getChanges());
			
			if (s.getMinMoves().getChanges().size() >= min.getChanges().size() && getSearch().isTarget(rtn))
			{
				s.setMinMoves(min);
			}
			
			//run garbage collection
			//System.gc();
			
			
			return s;
		}
		
	}
	public void visualize(Graph<V, Pair<V>> fb){
		JFrame jf = new JFrame();
		jf.setSize(1900, 1000);

		FRLayout frl = new FRLayout(fb);

		frl.setAttractionMultiplier(3);
		frl.setRepulsionMultiplier(1.1);
		
		frl.setMaxIterations(1000);
		//frl.lock(true);
		VisualizationViewer vv = new VisualizationViewer(frl, new Dimension(
				1900, 1000));
		
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		
		jf.getContentPane().add(vv);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);
	}
	
	/**
	 * returns the minimum number of edits required for a component
	 * @param graph
	 * @return
	 */
	public Integer lowerBound(Graph<V, Pair<V>> graph)
	{
		Graph<V, Pair<V>> temp = clone.deepClone(graph);
		branchingReturnC<V> s = new branchingReturnC<V>(temp, ((qtLBFS<V>) search).degSequenceOrder(temp));
		SearchResult<V> result = search.search(s);
		
		int count = 0;
		
		while (!result.isTarget())
		{
			//at least one more move needs to be done
			count++;
			
			//remove all vertices of current obstruction from graph
			for (V v0 : result.getCertificate().getVertices())
			{
				removeVertex(s, v0);
			}
			//update search result
			result = search.search(s);
		}
		//free memory
		temp = null;
		s = null;
		result = null;
		
		return count;
		
		
	}
}
