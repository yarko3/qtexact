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
 * @param <V> vertex
 */
public class lexReturnC<V> extends SearchResult<V>
{
	//store ArrayList of either a found P4/C4 or the final ordering
	private ArrayList<V> list;
	//QT check
	private boolean isConnected;
	
	/**
	 * constructor of lexReturnC
	 * @param l a lexBFS ordering if graph is at target state
	 * @param cert an obstruction certificate, if an obstruction exists
	 * @param qt is the solution at target state
	 * @param c does the graph contain connected components
	 * @param cComp set of sets of connected component vertices
	 */
	public lexReturnC(ArrayList<V> l, qtCertificateC<V> cert, boolean qt, boolean c, Set<Set<V>> cComp)
	{
		super(qt, cert);
		setList(l);
		setConnected(c);
		setcComponents(cComp);
	}

	/**
	 * get the lexBFS ordering
	 * @return lexBFS ordering
	 */
	public ArrayList<V> getList() {
		return list;
	}

	/**
	 * set the lexBFS ordering
	 * @param l lexBFS ordering
	 */
	public void setList(ArrayList<V> l) {
		this.list = l;
	}

	/**
	 * return true if graph searched is a single connected component
	 * @return true if graph searched is a single connected component, otherwise false
	 */
	public boolean isConnected() {
		return isConnected;
	}

	/**
	 * set boolean value for connectedness
	 * @param isConnected is the graph connected
	 */
	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	/**
	 * returns set of set of vertices of connected components
	 * @return set of sets of vertices
	 */
	public Set<Set<V>> getcComponents() {
		return cComponents;
	}

	/**
	 * set connected components
	 * @param cComponents set of sets of connected component vertices
	 */
	public void setcComponents(Set<Set<V>> cComponents) {
		this.cComponents = cComponents;
	}

	
}