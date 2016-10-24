package iristk.util;

public class TextMatcher {

	public boolean matches(Object text, Object pattern) {
		if (text == null || pattern == null)
			return false;
		return text.toString().toUpperCase().contains(pattern.toString().toUpperCase());
	}
	
}
