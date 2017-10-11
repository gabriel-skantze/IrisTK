package iristk.util;

import java.util.*;

public class EnumMap<A,B> extends HashMap<A,ArrayList<B>> {

	public void add(A key, B value) {
		if (!containsKey(key)) {
			put(key, new ArrayList<B>());
		}
		get(key).add(value);
	}
	
	public void removeItem(A key, B value) {
		if (containsKey(key)) {
			List<B> list = get(key);
			list.remove(value);
			if (list.size() == 0)
				remove(key);
		}
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
