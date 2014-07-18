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
			
			//get number of graphs we need to keep track of for percent
			if (output)
			{
				for (Graph<V, Pair<V>> g : cGraphs)
				{
					//if component is large enough to care
					if (g.getVertexCount() > 3)
					{
						count++;
					}
				}
			}
			
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
			
			
			//branch on the rest of the graphs
			for (Graph<V, Pair<V>> g : cGraphs)
			{
				//if component is large enough to care
				if (g.getVertexCount() > 3)
				{
					//if more moves are allowed
					if (bound >= 0)
					{
						//visualize(g);
						
						//fill new minMoves with bounded edge set of component
						min = new branchingReturnC<V>(g, ((qtLBFS<V>) search).degSequenceOrder(g));
						min.setChanges(fillMinMoves(s, bound + s.getChanges().size()));
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
//				//don't care about branching on this but still need it to build up the solution later
//				else
//				{
//					//empty minMoves 
//					min = new branchingReturnC<V>(g, s.getDeg());
//					min.setMinMoves(min);
//					results.add(new branchingReturnC<V>(g, ((qtLBFS<V>)search).degSequenceOrder(g), min));
//				}		
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
			
			//if this solution is better than current one
			Graph<V, Pair<V>> rtn = gen.applyMoves(Branch.clone.deepClone(s.getG()), min.getChanges());
			
			if (s.getMinMoves().getChanges().size() >= min.getChanges().size() && getSearch().isTarget(rtn))
			{
				s.setMinMoves(min);
			}
			
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
}
