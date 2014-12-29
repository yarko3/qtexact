package cographRules;

import qtUtils.branchingReturnC;
import abstractClasses.SearchResult;
import controller.Controller;

public class cographAllStruct<V> extends cographBranch<V> {

	//all structures to be used
	cograph4Pan<V> pan;
	cographC5<V> c5;
	cographCo4Pan<V> co4pan;
	cographHouse<V> house;
	cographKite<V> kite;
	cographY<V> y;
	cographP5<V> p5;
	
	
	public cographAllStruct(Controller<V> controller) {
		super(controller);

		//initialize branching structures
		pan = new cograph4Pan<V>(controller);
		c5 = new cographC5<V>(controller);
		co4pan = new cographCo4Pan<V>(controller);
		house = new cographHouse<V>(controller);
		kite = new cographKite<V>(controller);
		y = new cographY<V>(controller);
		p5 = new cographP5<V>(controller);
		
		output = controller.getOutputFlag();
	}
	
	@Override
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		//update search result
		searchResult = findStructures(s, searchResult);
		
		int flag = searchResult.getCertificate().getFlag();
		
		switch (flag)
		{
		//a C5 was found
		case (-8):
			
			c5.branchingRules(s, searchResult);
			return s;
		//a P5 was found
		case (-5):
			p5.branchingRules(s, searchResult);
			return s;
		//a 4-pan was found
		case (-3):
			pan.branchingRules(s, searchResult);
			return s;
		//a house was found
		case (-4):
			house.branchingRules(s, searchResult);
			return s;
		//a kite was found
		case (-9):
			kite.branchingRules(s, searchResult);
			return s;
		//a y was found
		case (-6):
			y.branchingRules(s, searchResult);
			return s;
		//a co-4-pan was found
		case (-7):
			co4pan.branchingRules(s, searchResult);
			return s;
		
		default:
			return super.branchingRules(s, searchResult);
		}
	}
	

}
