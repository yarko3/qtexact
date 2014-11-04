package jimsFiles;


public class CographCertificate {

	private boolean isCograph;
	private CoTree T;
	private int[] P4; // will be [a,b,c,d] with edges ab, bc, cd
	
	public CographCertificate(){
		isCograph = false;
		T = new CoTree();
		P4 = new int[4];
	}

	public boolean isCograph() {
		return isCograph;
	}

	public void setCograph(boolean isCograph) {
		this.isCograph = isCograph;
	}

	public CoTree getT() {
		return T;
	}

	public void setT(CoTree t) {
		T = t;
	}

	public int[] getP4() {
		return P4;
	}

	public void setP4(int[] p4) {
		P4 = p4;
	}
}
