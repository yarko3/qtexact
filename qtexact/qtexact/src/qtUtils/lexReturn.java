package qtUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * lexBFS search return type
 * @author Yarko Senyuta
 *
 */
public class lexReturn
{
	//store ArrayList of either a found P4 or the final ordering
	private ArrayList<Integer> list;
	//QT check
	private boolean isQT;
	private boolean isConnected;
	private LinkedList<HashSet<Integer>> cComponents;
	
	public lexReturn(ArrayList<Integer> l, boolean qt, boolean c, LinkedList<HashSet<Integer>> cComp)
	{
		setList(l);
		setQT(qt);
		setConnected(c);
		setcComponents(cComp);
	}

	public ArrayList<Integer> getList() {
		return list;
	}

	public void setList(ArrayList<Integer> list) {
		this.list = list;
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

	public LinkedList<HashSet<Integer>> getcComponents() {
		return cComponents;
	}

	public void setcComponents(LinkedList<HashSet<Integer>> cComponents) {
		this.cComponents = cComponents;
	}
	
	
}