package qtUtils;

/**
 * class containing vertex and degree for PriorityQueue in qtCheckYan
 * @author Yarko Senyuta
 *
 * @param <V>
 */
public class vertexIn<V> implements Comparable<vertexIn<V>>
{
	private V vertex;
	private int degree;
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

