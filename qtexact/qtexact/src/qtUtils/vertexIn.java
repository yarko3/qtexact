package qtUtils;

/**
 * class containing vertex and degree for PriorityQueue in qtCheckYan
 * @author Yaroslav Senyuta
 *
 * @param <V> vertex
 */
public class vertexIn<V> implements Comparable<vertexIn<V>>
{
	/**
	 * vertex
	 */
	private V vertex;
	/**
	 * degree of vertex
	 */
	private int degree;
	/**
	 * constructor
	 * @param v
	 * @param in
	 */
	public vertexIn(V v, int in)
	{
		vertex = v;
		degree = in;
	}
	
	public V getVertex(){return vertex;};
	public int getDegree(){return degree;};
	public int compareTo(vertexIn<V> v)
	{
		return Integer.compare(v.getDegree(), degree);
	}
}

