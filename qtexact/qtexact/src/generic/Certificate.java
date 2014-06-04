package generic;

import java.util.ArrayList;

/**
 * an abstract class for the certificate of an obstruction given by a search
 * 
 * @author Yaroslav Senyuta
 *
 * @param <V>
 */
public abstract class Certificate<V> 
{
	private ArrayList<V> vertices;
	private int flag;
	
	public Certificate(ArrayList<V> v, int f)
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
