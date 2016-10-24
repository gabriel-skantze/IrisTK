package iristk.util;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WinRegistry {
	
	public static List<String> getRegistryKeys(String path) {
		ArrayList<String> result = new ArrayList<>();
		String[] lines = ProcessReader.readProcess("reg query " + '"' + path + '"').split("\n");
		for (String line : lines) {
			line = line.trim();
			if (line.length() > 0 && line.startsWith(path)) 
				result.add(line.replace(path + "\\", ""));
		}
		return result;
	}
	
	private static Pattern patt = Pattern.compile("(\\S+) +(REG_SZ|REG_DWORD) +(.+)");
	
	public static Map<String,Object> getRegistryValues(String path) {
		Map<String,Object> result = new HashMap<>();
		String[] lines = ProcessReader.readProcess("reg query " + '"' + path + '"').split("\n");
		for (String line : lines) {
			line = line.trim();
			Matcher m = patt.matcher(line);
			if (m.matches()) {
				if (m.group(2).equals("REG_SZ")) {
					result.put(m.group(1), m.group(3));
				} else if (m.group(2).equals("REG_DWORD")) {
					result.put(m.group(1), Integer.decode(m.group(3)));
				}
			}
		}
		return result;
	}
	
	public static Object getRegistryValue(String path, String valueName) {
		Object value = getRegistryValues(path).get(valueName);
		return value;
	}
	
	private static boolean setRegistryValue(String path, String valueName, String type, String value) {
		String result = ProcessReader.readProcess("reg add \"" + path + "\" /v " + valueName + " /t " + type + " /f /d " + value);
		return result.contains("success");
	}
	
	public static boolean setRegistryValue(String path, String valueName, Integer value) {
		return setRegistryValue(path, valueName, "REG_DWORD", value.toString());
	}
	
	public static boolean setRegistryValue(String path, String valueName, String value) {
		return setRegistryValue(path, valueName, "REG_SZ", value);
	}

	public static void main(String[] args) throws Exception {
		//System.out.println(setRegistryValue("HKEY_CURRENT_USER\\Console", "Test", 5));
		System.out.println(getRegistryValue("HKEY_LOCAL_MACHINE\\HARDWARE\\DEVICEMAP\\SERIALCOMM", "\\Device\\Serial0"));
	}
}
