package jimsFiles;

import java.util.ArrayList;


public class myVertex {

	Integer id;
	ArrayList<Integer> label;
	int color;
	double weight;

	public myVertex(Integer id) {
        this.id = id;
        label = new ArrayList<Integer>(0);
        weight = 0;
        color = 0;
	}

	public void addLabel(int s) {
		label.add(s);
	}
		
	public String toString() { 
        return "V"+id+"("+label+")";
    }

	public void setColor(int i) {
		this.color = i;		
	}
	
	public boolean comesBefore(myVertex v) {
		int i=0;
		while (i < v.label.size()) {
			if (i == this.label.size()) return false; // happens when "this" is shorter than "v"
			if (this.label.get(i) > v.label.get(i)) return true;
			if (this.label.get(i) < v.label.get(i)) return false;
			i++;
		}
		return true;
	}

	
}