package iristk.flow;

public class StringCreator {
	
	StringBuilder sb = new StringBuilder();
	String lastStr = "";

	public void append(Object item) {
		if (item != null) {
			String str = item.toString().trim();
			if (lastStr.length() > 0 && !lastStr.endsWith(">") && !str.startsWith("<")) {
				sb.append(" ");
			}
			sb.append(str);
			lastStr = str;
		}
	}
	
	@Override
	public String toString() {
		return sb.toString();
	}
	
}
