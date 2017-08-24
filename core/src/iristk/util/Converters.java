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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Converters {
	
	public static Object asType(Object object, Class<?> type) {
		if (object == null)
			return null;
		else if (object.getClass() == type)
			return object;
		else if (type == String.class) 
			return asString(object);
		else if (type == Boolean.class || type == boolean.class)
			return asBoolean(object);
		else if (type == Integer.class || type == int.class)
			return asInteger(object);
		else if (type == Float.class || type == float.class)
			return asFloat(object);
		else if (type == Double.class || type == double.class)
			return asDouble(object);
		else if (type == Long.class || type == long.class)
			return asLong(object);
		else if (type == Record.class)
			return asRecord(object);
		else if (type == List.class)
			return asList(object);
		else if (object instanceof Record && Record.class.isAssignableFrom(type) && !type.isAssignableFrom(object.getClass())) {
			return asDerivedRecord((Record)object, type); 
		} else
			return object;
	}

	private static Record asDerivedRecord(Record object, Class<?> type) {
		try {
			Record record = (Record) type.newInstance();
			record.putAllExceptNull(object);
			return record;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} 
		return null;
	}

	public static String asString(Object object) {
		return asString(object, null);
	}
	/**
	 * Returns object as a String, unless it is null in which case def is returned.
	 * @param object to be stringed
	 * @param def backup String
	 * @return String version of object
	 */
	public static String asString(Object object, String def) {
		return Objects.toString(object, def);
	}	

	public static boolean asBoolean(Object object) {
		if (object == null)
			return false;
		else if (object instanceof Boolean) 
			return (Boolean)object;
		else if (object instanceof Float) 
			return ((Float)object) != 0;
		else if (object instanceof Integer) 
			return ((Integer)object) != 0;
		else if (object instanceof Double) 
			return ((Double)object) != 0;
		else if (object instanceof String) 
			return !((String)object).equalsIgnoreCase("false");
		else if (object instanceof Collection) 
			return ((Collection<?>)object).size() > 0;
			else return true;
	}

	public static boolean asBoolean(Object object, boolean def) {
		if (object == null)
			return def;
		else
			return asBoolean(object);
	}

	public static List<Object> asList(Object object) {
		if (object == null)
			return null;
		else if (object instanceof List) {
			@SuppressWarnings("unchecked")
			final List<Object> result = (List<Object>)object;
			return result;
		} else if (object instanceof Record)
			return new ArrayList<>(((Record)object).getValues());
		else if (object instanceof Collection)
			return new ArrayList<>((Collection<?>)object);
		else 
			return Arrays.asList(object);
	}
	
	public static List<Object> asList(Object... objects) {
		return Arrays.asList(objects);	
	}

	public static Record asRecord(Object object) {
		if (object == null)
			return null;
		else if (object instanceof Record)
			return (Record)object;
		else if (object instanceof Map)
			return new Record((Map<?,?>)object);
		else 
			return null;
	}

	public static Float asFloat(Object object, Float def) {
		Float f = asFloat(object);
		if (f == null)
			return def;
		else
			return f;
	}

	public static Float asFloat(Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof Number) {
			return ((Number)object).floatValue();
		} else if (object instanceof String) {
			try {
				return Float.parseFloat((String)object);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}

	public static Double asDouble(Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof Number) {
			return ((Number)object).doubleValue();
		} else if (object instanceof String) {
			try {
				String s = (String)object;
				s = s.replace(',', '.');
				return Double.parseDouble(s);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}
	
	public static Double asDouble(Object object, Double def) {
		Double i = asDouble(object);
		if (i == null)
			return def;
		else
			return i;
	}
	
	public static Integer asInteger(Object object, Integer def) {
		Integer i = asInteger(object);
		if (i == null)
			return def;
		else
			return i;
	}

	public static Integer asInteger(Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof Number) {
			return ((Number)object).intValue();
		} else if (object instanceof String) {
			try {
				return Integer.parseInt((String)object);
			} catch (NumberFormatException e) {
				try {
					return (int) Float.parseFloat((String)object);
				} catch (NumberFormatException e2) {
					return null;
				}
			}
		}
		return null;
	}
	
	public static Long asLong(Object object, Long def) {
		Long i = asLong(object);
		if (i == null)
			return def;
		else
			return i;
	}

	public static Long asLong(Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof Number) {
			return ((Number)object).longValue();
		} else if (object instanceof String) {
			try {
				return Long.parseLong((String)object);
			} catch (NumberFormatException e) {
				try {
					return new Double(Double.parseDouble((String)object)).longValue();
				} catch (NumberFormatException e2) {
					return null;
				}
			}
		}
		return null;
	}

}
