/*******************************************************************************
 * Copyright (c) 2014 Gabriel Skantze.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Gabriel Skantze - initial API and implementation
 ******************************************************************************/
package iristk.util;

import java.util.List;

public class StringUtils {
	public static int countMatches(String string, char find) {
		int count = 0;
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == find)
				count++;
		}
		return count;
	}
	
	public static String join(String[] parts, String glue) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			if (i > 0) {
				result.append(glue);
			}
			result.append(parts[i]);
		}
		return result.toString();
	}
	
	public static String join(List parts, String glue) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < parts.size(); i++) {
			if (i > 0) {
				result.append(glue);
			}
			result.append(parts.get(i));
		}
		return result.toString();
	}

	public static String ucFirst(String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}
	
	public static String lcFirst(String string) {
		return string.substring(0, 1).toLowerCase() + string.substring(1);
	}

	public static String enumerate(String finalGlue, Object... parts) { 
		int len = 0;
		for (int i = 0; i < parts.length; i++) {
			if (parts[i] != null && !parts[i].toString().trim().isEmpty()) {
				len++;
			}
		}
		if (len == 0) {
			return "";
		}
		int blen = 0;
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			if (parts[i] != null && !parts[i].toString().trim().isEmpty()) {
				if(len==1){
					//Avoiding adding glue to enumeration on single String.
					return parts[i].toString();
				}
				else if (blen == len - 1) {
					result.append(" " + finalGlue + " ");
				}
				else if (blen > 0) {
					result.append(", ");
				}
				result.append(parts[i]);
				blen++;
			}
		}
		return result.toString();
	}

}
