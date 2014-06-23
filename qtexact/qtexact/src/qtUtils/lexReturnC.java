/**
 * Yaroslav Senyuta
 * NSERC USRA Grant (2014)
 */

package qtUtils;

import java.util.ArrayList;
import java.util.Set;

import abstractClasses.SearchResult;
import certificate.qtCertificateC;

/**
 * return type of lexBFS search
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public class lexReturnC<V> extends SearchResult<V>
{
	//store ArrayList of either a found P4/C4 or the final ordering
	private ArrayList<V> list;
	//QT check
	private boolean isConnected;
	private Set<Set<V>> cComponents;

	
	public lexReturnC(ArrayList<V> l, qtCertificateC<V> cert, boolean qt, boolean c, Set<Set<V>> cComp)
	{
		setList(l);
		setTarget(qt);
		setConnected(c);
		setcComponents(cComp);
		setCertificate(cert);
	}

	public ArrayList<V> getList() {
		return list;
	}

	public void setList(ArrayList<V> l) {
		this.list = l;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public Set<Set<V>> getcComponents() {
		return cComponents;
	}

	public void setcComponents(Set<Set<V>> cComponents) {
		this.cComponents = cComponents;
	}

	
}