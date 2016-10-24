package iristk.flow;

import java.util.List;

public class RepeatHandler {

	private List list = null;
	private int length = -1;
	private int position = 0;
	private Object item;

	public RepeatHandler() {
	}
	
	public RepeatHandler(int length) {
		this.length = length;
	}

	public RepeatHandler(List list) {
		this.list = list;
		this.length = list.size();
		if (length > 0)
			item = list.get(0);
	}

	public void next() {
		position++;
		if (list != null && length > position)
			item = list.get(position);
	}
	
	public int getPosition() {
		return position;
	}

	public int getLength() {
		return length;
	}

	public Object getItem() {
		return item;
	}

	public boolean isLast() {
		return position == length-1;
	}
	
	public boolean isFirst() {
		return position == 0;
	}
	
}
