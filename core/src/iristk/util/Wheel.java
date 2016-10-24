package iristk.util;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Arrays;

public class Wheel<T> {

	private List<T> list;
	private int pos;
	
	public Wheel(T... values) {
		list = new ArrayList<T>(Arrays.asList(values));
		pos = 0;
	}

	public T next() {
		if (pos >= list.size())
			pos = 0;
		return list.get(pos++);
	}

}
