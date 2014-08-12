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
	
	public String toString()
	{
		return "Vertices: " + vertices + ", flag: " + flag;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		Certificate<V> other = (Certificate<V>) obj;
		if (flag != other.flag)
			return false;
		if (vertices == null) {
			if (other.vertices != null)
				return false;
		} else if (!vertices.containsAll(other.vertices) && !other.vertices.containsAll(vertices))
			return false;
		return true;
	}

}
