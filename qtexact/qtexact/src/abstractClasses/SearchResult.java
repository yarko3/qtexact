package abstractClasses;

import java.util.Set;

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
	protected boolean isTarget;
	protected Certificate<V> certificate;
	protected Set<Set<V>> cComponents;
	protected boolean isConnected;
	
	public SearchResult(boolean isT, Certificate<V> cert )
	{
		isTarget = isT;
		certificate = cert;
	}
	
	public SearchResult(boolean isT, Certificate<V> cert, Set<Set<V>> cComp)
	{
		isTarget = isT;
		certificate = cert;
		cComponents = cComp;
		isConnected = (cComp.size() == 1) ? true : false;
	}
	
	/**
	 * @return the cComponents
	 */
	public Set<Set<V>> getcComponents() {
		return cComponents;
	}

	/**
	 * @param cComponents the cComponents to set
	 */
	public void setcComponents(Set<Set<V>> cComponents) {
		this.cComponents = cComponents;
	}

	/**
	 * @return the isConnected
	 */
	public boolean isConnected() {
		return isConnected;
	}

	/**
	 * @param isConnected the isConnected to set
	 */
	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SearchResult [isTarget=" + isTarget + ", certificate="
				+ certificate + "]";
	}
	
	
	
	
	

}
