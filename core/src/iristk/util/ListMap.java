package iristk.util;

import java.util.*;

public class ListMap<A,B> extends HashMap<A,ArrayList<B>> {

	public void add(A key, B value) {
		if (!containsKey(key)) {
			put(key, new ArrayList<B>());
		}
		get(key).add(value);
	}
	
	public void addUnique(A key, B value) {
		if (!containsKey(key)) {
			put(key, new ArrayList<B>());
		}
		List<B> list = get(key);
		if (!list.contains(value))
			list.add(value);
	}

}
