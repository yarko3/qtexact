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
	private int all;
	private int fail;
	private qtCo4Pan<V> co4pan;
	
	public qtAllStruct(Controller<V> controller) {
		super(controller);
		C5 = new qtC5<V>(controller);
		house = new qtHouse<V>(controller);
		kite = new qtKite<V>(controller);
		P5 = new qtP5<V>(controller);
		y = new qtY<V>(controller);
		pan = new qtPan<V>(controller);
		co4pan = new qtCo4Pan<V>(controller);
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
		all++;
		
//		System.out.println("Branch on a " + flag);
//		System.out.println("On " + searchResult.getCertificate().getVertices());
		
		switch (flag)
		{
		//a C4 was found
		case (-1):
			//System.out.println("Found a C4.");
			fail++;
			super.branchingRules(s, searchResult);
			//System.out.println("end of C4.");
			return s;
		//a P4 was found
		case (-2):
			//System.out.println("Found a P4.");
			fail++;
			super.branchingRules(s, searchResult);
			//System.out.println("end of P4.");
			return s;
		//a 4 pan was found
		case (-3):
			//System.out.println("Found a 4 pan.");
			pan.branchingRules(s, searchResult);
			//System.out.println("end of 4 pan.");
			return s;
		//a house was found
		case (-4):
			//System.out.println("Found a house.");
			house.branchingRules(s, searchResult);
			//System.out.println("end of house.");
			return s;
		//a P5 was found
		case (-5):
			//System.out.println("Found a P5.");
			P5.branchingRules(s, searchResult);
			//System.out.println("end of P5.");
			return s;
		//a fork was found 
		case (-6):
			//System.out.println("Found a fork.");
			y.branchingRules(s, searchResult);
			//System.out.println("end of fork.");
			return s;
		//a 3 pan with an extra was found
		case (-7):
			//System.out.println("Found a 3 pan.");
			co4pan.branchingRules(s, searchResult);
			//System.out.println("end of 3 pan.");
			return s;
		//a C5 was found
		case (-8):
			//System.out.println("Found a C5.");
			C5.branchingRules(s, searchResult);
			//System.out.println("end of C5.");
			return s;
		//a kite was found
		case (-9):
			//System.out.println("Found a kite.");
			kite.branchingRules(s, searchResult);
			//System.out.println("end of kite.");
			return s;
		default:
			return super.branchingRules(s, searchResult);
		}
	}
	
}
