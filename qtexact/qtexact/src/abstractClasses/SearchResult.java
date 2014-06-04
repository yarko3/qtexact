package abstractClasses;

/**
 * an abstract class for the return type of searches
 * 
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public abstract class SearchResult<V>
{
	//is at goal state
	private boolean isTarget;
	private Certificate<V> certificate;
	
	
	public boolean isTarget() {
		return isTarget;
	}
	public void setTarget(boolean isTarget) {
		this.isTarget = isTarget;
	}
	public Certificate<V> getCertificate() {
		return certificate;
	}
	public void setCertificate(Certificate<V> certificate) {
		this.certificate = certificate;
	}
	
	
	

}
