package abstractClasses;

/**
 * an abstract class for the return type of searches
 * 
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public class SearchResult<V>
{
	//is at goal state
	private boolean isTarget;
	private Certificate<V> certificate;
	
	public SearchResult(boolean isT, Certificate<V> cert )
	{
		isTarget = isT;
		certificate = cert;
	}
	public boolean isTarget() {
		return isTarget;
	}
	public void setTarget(boolean b)
	{
		isTarget = b;
	}
	
	public Certificate<V> getCertificate() {
		return certificate;
	}
	public void setCertificate(Certificate<V> certificate) {
		this.certificate = certificate;
	}
	
	
	

}
