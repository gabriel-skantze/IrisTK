package iristk.util;

import java.util.LinkedList;
import java.util.List;

public class MedianFilter {

	private List<Integer> added = new LinkedList<Integer>();
	private List<Integer> sorted = new LinkedList<Integer>();
	private int length;

	public MedianFilter(int length) {
		this.length = length;
	}

	public Integer getMedian() {
		return sorted.get(sorted.size() / 2);
	}

	public void add(Integer value) {
		INSERT: {
			for (int i = 0; i < sorted.size(); i++) {
				if (value <= sorted.get(i)) {
					sorted.add(i, value);
					break INSERT;
				}
			}
			sorted.add(value);
		}
		added.add(value);
		if (added.size() > length) {
			sorted.remove(added.remove(0));
		}
	}
	
	public void clear() {
		sorted.clear();
		added.clear();
	}

}
