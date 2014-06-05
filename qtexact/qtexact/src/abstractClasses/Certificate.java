package abstractClasses;

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
	/**
	 * vertices which induce obstruction
	 */
	private ArrayList<V> vertices;
	/**
	 * flag of obstruction found
	 */
	private int flag;
	
	/**
	 * constructor
	 * @param v vertices
	 * @param f flag
	 */
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
