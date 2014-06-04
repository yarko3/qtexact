/**
 * Yaroslav Senyuta
 * NSERC USRA Grant (2014)
 */

package qtUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import certificate.qtCertificateC;
import abstractClasses.SearchResult;


public class lexReturnC<V> extends SearchResult<V>
{
	//store ArrayList of either a found P4/C4 or the final ordering
	private ArrayList<V> list;
	//QT check
	private boolean isConnected;
	private LinkedList<HashSet<V>> cComponents;

	
	public lexReturnC(ArrayList<V> l, qtCertificateC<V> cert, boolean qt, boolean c, LinkedList<HashSet<V>> cComp)
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

	public LinkedList<HashSet<V>> getcComponents() {
		return cComponents;
	}

	public void setcComponents(LinkedList<HashSet<V>> cComponents) {
		this.cComponents = cComponents;
	}

	
}