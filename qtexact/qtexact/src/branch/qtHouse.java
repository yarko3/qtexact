package branch;

import qtUtils.branchingReturnC;
import abstractClasses.Certificate;
import abstractClasses.SearchResult;
import controller.Controller;

public class qtHouse<V> extends qtKite<V> 
{

	public qtHouse(Controller<V> controller) {
		super(controller);
	}
	
	
	@Override
	public branchingReturnC<V> branchingRules(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		//update certificate
		searchResult.setCertificate(hasHouse(s, searchResult));
	}
	
	private Certificate<V> hasHouse(branchingReturnC<V> s, SearchResult<V> searchResult)
	{
		
		Certificate<V> obstruction = searchResult.getCertificate();
		
		
		//if a C4 was found
		
		
	}
	

	
}
