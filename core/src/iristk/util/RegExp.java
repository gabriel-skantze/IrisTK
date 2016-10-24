package iristk.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExp {

	public static String getGroup(String string, String pattern, int group) {
		Matcher m = Pattern.compile(pattern).matcher(string);
		if (m.find() && m.groupCount() >= group) {
			return m.group(group);
		}
		return null;
	}
	
}
