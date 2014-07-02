package branch;

import qtUtils.branchingReturnC;
import abstractClasses.SearchResult;
import controller.Controller;

public class qtAllStruct<V> extends qtBranchNoHeuristic<V> 
{

	private qtC5<V> C5;
	private qtHouse<V> house;
	private qtKite<V> kite;
	private qtP5<V> P5;
	private qtPan<V> pan;
	private qtY<V> y;
	
	public qtAllStruct(Controller<V> controller) {
		super(controller);
		C5 = new qtC5<V>(controller);
		house = new qtHouse<V>(controller);
		kite = new qtKite<V>(controller);
		P5 = new qtP5<V>(controller);
		y = new qtY<V>(controller);
		pan = new qtPan<V>(controller);
	}
	
	/**
	 * look for all induced forbidden structures on 5 vertices 
	 */
	@Override
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		//update search result
		searchResult = findStructures(s, searchResult);
		
		int flag = searchResult.getCertificate().getFlag();
		
		switch (flag)
		{
		//a C4 was found
		case (-1):
			//System.out.println("Found a C4.");
			return super.branchingRules(s, searchResult);
		//a P4 was found
		case (-2):
			//System.out.println("Found a P4.");
			return super.branchingRules(s, searchResult);
		//a 4 pan was found
		case (-3):
			//System.out.println("Found a 4 pan.");
			return pan.branchingRules(s, searchResult);
		//a house was found
		case (-4):
			//System.out.println("Found a house.");
			return house.branchingRules(s, searchResult);
		//a P5 was found
		case (-5):
			//System.out.println("Found a P5.");
			return P5.branchingRules(s, searchResult);
		//a fork was found 
		case (-6):
			//System.out.println("Found a fork.");
			return y.branchingRules(s, searchResult);
		//a 3 pan with an extra was found
		case (-7):
			//System.out.println("Found a 3 pan.");
			return y.branchingRules(s, searchResult);
		//a C5 was found
		case (-8):
			//System.out.println("Found a C5.");
			return C5.branchingRules(s, searchResult);
		//a kite was found
		case (-9):
			//System.out.println("Found a kite.");
			return kite.branchingRules(s, searchResult);
		default:
			return super.branchingRules(s, searchResult);
		}
	}
	
}
