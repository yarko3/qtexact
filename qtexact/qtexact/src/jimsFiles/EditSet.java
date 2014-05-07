package jimsFiles;

import java.util.ArrayList;
import java.util.Arrays;


public class EditSet {
	
	ArrayList<Integer[]> C;
	
	public EditSet() {
		C = new ArrayList<Integer[]>();
	}

	public int isIn(Integer a, Integer b) {
		// returns -1 if it is not in, and returns its index if it is in.
		
		Integer[] edge = {Math.min(a, b), Math.max(a, b)};
		for (int i=0; i<C.size(); i++) {
			if (Arrays.equals(C.get(i),edge)) return i;
		}
		return -1;
	}

	public void add (Integer a, Integer b) {
		if (isIn(a,b) >= 0) {
			System.out.print ("Critical Error: edit set already contains "+a+", "+b+"\n");
		}
		Integer[] edge = {Math.min(a, b), Math.max(a, b)};
		C.add(edge);
		//System.out.print("Added "+a+","+b+"\n"+toString()+"\n");
	}

	public void remove (Integer a, Integer b) {
		int index = isIn(a,b);
		if (index < 0) {
			System.out.print("Critical Error: trying to remove "+ a+", "+b+" from edit set but it does not exist\n"
					+"Edit Set is: " + toString() + "\n");
		}
		C.remove(index);
	}
	
	public String toString () {
		String output = "";
		for (int i=0; i<C.size(); i++) {
			output += " {" + C.get(i)[0] + ", " + C.get(i)[1]+"}" ; 
		}
		return output;
	}
	
}