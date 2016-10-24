package iristk.util;

import java.util.ArrayList;
import java.util.List;

public class EditDistance {

	public static enum Operation {SUB, DEL, INS};
	
	private List<Operation> editSeq;
	private float minDistance;
	private int[] mapP2toP1;
	private int[] mapP1toP2;

	public EditDistance(String p1, String p2) {
		this(p1.trim().length() == 0 ? new String[0] : p1.trim().split(" "), 
				p2.trim().length() == 0 ? new String[0] : p2.trim().split(" "));
	}
	
	public EditDistance(String[] p1, String[] p2) {
		List<Operation>[][] edits = new List[p1.length + 1][p2.length + 1];
		float[][] distances = new float[p1.length + 1][p2.length + 1];
		distances[0][0] = 0;
		edits[0][0] = new ArrayList();
		for (int i = 1; i <= p1.length; i++) {
			distances[i][0] = i;
			edits[i][0] = new ArrayList(edits[i-1][0]);
			edits[i][0].add(Operation.DEL);
		}
		for (int j = 1; j <= p2.length; j++) {
			distances[0][j] = j;
			edits[0][j] = new ArrayList(edits[0][j-1]);
			edits[0][j].add(Operation.INS);
		}
		for (int i = 1; i <= p1.length; i++) {
			for (int j = 1; j <= p2.length; j++) {
				float sub = distances[i - 1][j - 1] + subsitutionCost(p1[i - 1], p2[j - 1]);
				float del = distances[i - 1][j] + deletionCost(p1[i-1]);
				float ins = distances[i][j - 1] + insertionCost(p2[j-1]);
				if (sub <= del && sub <= ins) {
					distances[i][j] = sub;
					edits[i][j] = new ArrayList<>(edits[i - 1][j - 1]);
					edits[i][j].add(Operation.SUB);
				} else if (del <= sub && del <= ins) {
					distances[i][j] = del;
					edits[i][j] = new ArrayList<>(edits[i - 1][j]);
					edits[i][j].add(Operation.DEL);
				} else if (ins <= sub && ins <= del) {
					distances[i][j] = ins;
					edits[i][j] = new ArrayList<>(edits[i][j-1]);
					edits[i][j].add(Operation.INS);
				}
			}
		}
		editSeq = edits[p1.length][p2.length];
		minDistance = distances[p1.length][p2.length];
		int i  = -1, j = -1;
		mapP2toP1 = new int[p2.length];
		mapP1toP2 = new int[p1.length];
		for (Operation op : editSeq) {
			if (op == Operation.SUB) {
				j++;
				i++;
			} else if (op == Operation.DEL) {
				i++;
			} else if (op == Operation.INS) {
				j++;
			}
			if (j >= 0)
				mapP2toP1[j] = Math.max(0, i);
			if (i >= 0)
				mapP1toP2[i] = Math.max(0, j);
		}
	}
	
	public int mapP2toP1(int pos) {
		return mapP2toP1[pos];
	}
	
	public int mapP1toP2(int pos) {
		return mapP1toP2[pos];
	}
	
	public List<Operation> getEditSequence() {
		return editSeq;
	}

	public float getMinDistance() {
		return minDistance;
	}

	protected float subsitutionCost(String w1, String w2) {
		if (w1.equalsIgnoreCase(w2))
			return 0;
		else
			return 1.5f;
	}
	
	protected float insertionCost(String w) {
		return 1;
	}
	
	protected float deletionCost(String w) {
		return 1;
	}
	
	public static void main(String[] args) {
		System.out.println(new EditDistance("", "a b c").getEditSequence());
	}
	
}
