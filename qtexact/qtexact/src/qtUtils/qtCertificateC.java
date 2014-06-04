/**
 * Yaroslav Senyuta
 * NSERC USRA Grant (2014)
 */

package qtUtils;

import java.util.ArrayList;

public class qtCertificateC<V> {
	
	private ArrayList<V> vertices;
	//flag for C4 or P4 found
	private int flag;
	public qtCertificateC(ArrayList<V> v, int f)
	{
		setVertices(v);
		setFlag(f);
	}
	public ArrayList<V> getVertices() {
		return vertices;
	}
	public void setVertices(ArrayList<V> vertices) {
		this.vertices = vertices;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}

}
