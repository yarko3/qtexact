package qtUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * lexBFS search return type
 * @author Yarko Senyuta
 *
 */
public class lexReturnC<V>
{
	//store ArrayList of either a found P4 or the final ordering
	private ArrayList<V> list;
	//QT check
	private boolean isQT;
	private boolean isConnected;
	private LinkedList<HashSet<V>> cComponents;
	private tpCertificateC<V> certificate;
	
	public lexReturnC(ArrayList<V> l, tpCertificateC<V> cert, boolean qt, boolean c, LinkedList<HashSet<V>> cComp)
	{
		setList(l);
		setQT(qt);
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

	public boolean isQT() {
		return isQT;
	}

	public void setQT(boolean isQT) {
		this.isQT = isQT;
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

	public tpCertificateC<V> getCertificate() {
		return certificate;
	}

	public void setCertificate(tpCertificateC<V> certificate) {
		this.certificate = certificate;
	}
	
	
}