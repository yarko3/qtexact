package qtUtils;

import edu.uci.ics.jung.graph.util.Pair;

public class moveC<V> 
{
	//edge
	private Pair<V> move;
	//false for delete, true for addition
	private boolean flag;
	
	public moveC(Pair<V> m, boolean f)
	{
		setMove(m);
		setFlag(f);
	}

	public Pair<V> getMove() {
		return move;
	}

	public void setMove(Pair<V> move) {
		this.move = move;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
}
