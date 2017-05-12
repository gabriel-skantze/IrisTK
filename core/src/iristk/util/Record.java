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

import static iristk.util.Converters.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;

import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;

/**
 * A Record is essentially a map of key-value pairs (much like any java {@link java.util.Map}). However, it supports a convenient way of adding and accessing values deep in the hierarchy by using colon (:) notation.
 * <p> For example you can add a value like this: {@code myRecord.put("foo:bar", 5)}. If the key "foo" does not already contain a nested Record, it will create one. It will then put the value 5 under the key "bar". 
 * <p> You can also access values like this  {@code myRecord.get("foo:bar")}. If it is not possible to traverse "foo" and then "bar" in the nested hierarchy, the method will return null.
 * <p> There are also convenience functions for getting a value of the right type. For example, {@code myRecord.getInt("foo:bar")} will try to convert the value to an Integer. If this fails the method will return null.
 * <p> Using kleen stars (*), it is also possible to search the hierarchy using the {@code has()} method. Thus, {@code myRecord.has("*:bar")} will return true.   
 *  */

public class Record {
	
	private static HashMap<Class<?>,RecordInfo> recordInfo = new HashMap<>();

	private final HashMap<String, Object> dynamicFields = new HashMap<String,Object>();
	
	public Record() {
		synchronized (Record.class) {
			if (!recordInfo.containsKey(getClass())) {
				recordInfo.put(getClass(), new RecordInfo(getClass()));
			}
		}
	}
	
	private RecordInfo getRecordInfo() {
		return recordInfo.get(getClass());
	}
	
	private static class RecordInfo {
		private final HashMap<String,Field> classFields = new HashMap<>();
		private final HashMap<String,Method> getMethodFields = new HashMap<>();
		private final HashMap<String,Method> setMethodFields = new HashMap<>();
		private final List<String> orderedFields;
		
		public RecordInfo(Class<? extends Record> clazz) {
			final HashMap<String,Integer> order = new HashMap<>();
			setupFields(clazz, order);
			orderedFields = new ArrayList<String>(getMethodFields.keySet());
			orderedFields.addAll(classFields.keySet());
			Collections.sort(orderedFields, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return order.get(o1) - order.get(o2);
				}
			});
		}
		
		private void setupFields(Class<? extends Record> clazz, HashMap<String, Integer> order) {
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(RecordField.class)) {
					field.setAccessible(true);
					RecordField fieldAnnot = field.getAnnotation(RecordField.class);
					String name = fieldAnnot.name().equals("DEFAULT") ? field.getName() : fieldAnnot.name();
					classFields.put(name, field);
					order.put(name, fieldAnnot.order());
				}
			}
			for (Method method : clazz.getDeclaredMethods()) {
				if (method.isAnnotationPresent(RecordField.class)) {
					method.setAccessible(true);
					RecordField fieldAnnot = method.getAnnotation(RecordField.class);
					String name = fieldAnnot.name().equals("DEFAULT") ? method.getName() : fieldAnnot.name();
					if (method.getParameterTypes().length == 1) {
						setMethodFields.put(name, method);
					} else if (method.getParameterTypes().length == 0) {
						order.put(name, fieldAnnot.order());
						getMethodFields.put(name, method);
					}
				}
			}
			Class s = clazz.getSuperclass();
			if (s != null)
				setupFields(s, order);
		}

	}

	public Record(Map map) {
		this();
		putAll(map);
	}

	public Record(Object... init) {
		this();
		if (init.length % 2 == 1)
			throw new IllegalArgumentException("Can only initialize Record with even number of arguments");
		for (int i = 0; i < init.length; i += 2) {
			put(init[i].toString(), init[i+1]);
		}
	}

	public Record(Record initRecord, Object... init) {
		this();
		if (init.length % 2 == 1)
			throw new IllegalArgumentException("Can only initialize Record with even number of arguments");
		putAllExceptNull(initRecord);
		for (int i = 0; i < init.length; i += 2) {
			put(init[i].toString(), init[i+1]);
		}
	}

	public void putAllExceptNull(Record record) {
		for (String field : record.getFields()) {
			if (record.get(field) != null) {
				put(field, record.get(field));
			}
		}
	}

	public void putAll(Record record) {
		for (String field : record.getFields()) {
			put(field, record.get(field));
		}
	}

	public void putAll(Map map) {
		for (Object key : map.keySet()) {
			put(key.toString(), map.get(key));
		}
	}

	public synchronized Class<?> getFieldClass(String fieldName) {
		RecordInfo info = getRecordInfo();
		if (info.classFields.containsKey(fieldName)) {
			return info.classFields.get(fieldName).getType();
		}
		if (info.setMethodFields.containsKey(fieldName)) {
			return info.setMethodFields.get(fieldName).getParameterTypes()[0];
		}
		if (info.getMethodFields.containsKey(fieldName)) {
			return info.getMethodFields.get(fieldName).getReturnType();
		}
		Object val = dynamicFields.get(fieldName);
		if (val != null)
			return val.getClass();
		return null;
	}

	public static Object get(Object obj, String field) {
		if (obj == null) {
			return null;
		} else if (obj instanceof Record) {
			return ((Record)obj).get(field);
		} else if (obj instanceof List) {
			List list = (List)obj;
			try {
				if (field.contains(":")) {
					int i = field.indexOf(":");
					String subf = field.substring(0, i);
					String rest = field.substring(i + 1);
					int li = Integer.parseInt(subf);
					if (li < list.size()) {
						Object sub = list.get(li);
						return get(sub, rest);
					}
				} else {
					int li = Integer.parseInt(field);
					if (li < list.size()) {
						return list.get(li);
					}
				}
			} catch (NumberFormatException e) {
			}
		}
		return null;
	}

	public synchronized Object get(String field) {
		if (field == null)
			return null;
		//if (field.contains(".")) {
		//	System.err.println("Warning: use of dots when accessing record fields is deprecated: " + field);
		//	field = field.replace(".", ":");
		//}
		if (field.contains(":")) {
			int i = field.indexOf(":");
			String subf = field.substring(0, i);
			String rest = field.substring(i + 1);
			Object sub = get(subf);
			return get(sub, rest);
		} else {
			RecordInfo info = getRecordInfo();
			Method getMethod = info.getMethodFields.get(field);
			if (getMethod != null) {
				try {
					return getMethod.invoke(this);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			Field getField = info.classFields.get(field);
			if (getField != null) {
				try {
					return getField.get(this);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} 
			}
			if (dynamicFields.containsKey(field)) 
				return dynamicFields.get(field);
			try {
				int i = Integer.parseInt(field);
				if (i < dynamicFields.size()) {
					return dynamicFields.values().toArray()[i];
				}
			} catch (NumberFormatException e) {
			}
			return null;
		}
	}

	public synchronized void putIfNotNull(String field, Object value) {
		if (value != null)
			put(field, value);
	}

	public synchronized void put(String field, Object value) {
		if (field != null) {
			//if (field.contains(".")) {
			//	System.err.println("Warning: use of dots when accessing record fields is deprecated: " + field);
			//	field = field.replace(".", ":");
			//}
			if (field.contains(":")) {
				int i = field.indexOf(":");
				String fn = field.substring(0, i);
				String rest = field.substring(i + 1);
				String[] subFields;
				if (fn.equals("*")) {
					subFields = dynamicFields.keySet().toArray(new String[0]);
				} else {
					subFields = new String[]{fn};
				}
				for (String f : subFields) {
					Record subRec = getRecord(f);
					if (subRec != null) {
						subRec.put(rest, value);
					} else {
						Record record = new Record();
						dynamicFields.put(f, record);
						record.put(rest, value);
					}
				}
			} else if (field.equals("*")) {
				for (String f : dynamicFields.keySet()) {
					dynamicFields.put(f, value);
				}
			} else {
				RecordInfo info = getRecordInfo();
				Method setMethod = info.setMethodFields.get(field);
				if (setMethod != null) {
					try {
						setMethod.invoke(this, asType(value, setMethod.getParameterTypes()[0]));
						return;
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
				Field setField = info.classFields.get(field);
				if (setField != null) {
					try {
						if(setField.getType().equals(java.time.LocalDate.class)) {
							setField.set(this, new LocalDateStringConverter().fromString((String) value));
						} else if(setField.getType().equals(java.time.LocalDateTime.class)) {
							setField.set(this, new LocalDateTimeStringConverter().fromString((String) value));
						} else {
							setField.set(this, asType(value, setField.getType()));
						}
						return;
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} 
				}
				dynamicFields.put(field, value);
			}
		}
	}

	public synchronized boolean has(String field) {
		//if (field.contains(".")) {
		//	System.err.println("Warning: use of dots when accessing record fields is deprecated: " + field);
		//	field = field.replace(".", ":");
		//}
		if (field.contains(":")) {
			int i = field.indexOf(":");
			String subField = field.substring(0, i);
			String rest = field.substring(i + 1);
			if (subField.equals("*")) {
				for (String key : getFields()) {
					Object sub = get(key);
					if (sub instanceof Record) {
						if (((Record) sub).has(rest)) return true;
					}
				}
				return false;
			} else {
				Object sub = get(subField);
				if (sub instanceof Record) {
					return ((Record) sub).has(rest);
				} else {
					return false;
				}
			}
		} else {
			RecordInfo info = getRecordInfo();
			return info.getMethodFields.containsKey(field) || info.classFields.containsKey(field) || field.equals("*") || dynamicFields.containsKey(field);
		}
	}

	public Object get(String field, Object def) {
		Object value = get(field);
		if (value != null)
			return value;
		else
			return def;
	}

	public String getString(String field, String def) {
		return asString(get(field), def);
	}

	public String getString(String field) {
		return asString(get(field));
	}

	public Float getFloat(String field, Float def) {
		return asFloat(get(field), def);
	}

	public Float getFloat(String field) {
		return asFloat(get(field));
	}

	public Double getDouble(String field) {
		return asDouble(get(field));
	}

	public Double getDouble(String field, Double def) {
		return asDouble(get(field), def);
	}
	
	public Boolean getBoolean(String field, Boolean def) {
		return asBoolean(get(field), def);
	}

	public Boolean getBoolean(String field) {
		return asBoolean(get(field));
	}

	public Integer getInteger(String field, Integer def) {
		return asInteger(get(field), def);
	}

	public Integer getInteger(String field) {
		return asInteger(get(field));
	}

	/**
	 * Increments the Integer under @field by @incr and returns the incremented value. If there is not such @field, it is set to @incr.
	 */
	public Integer incrInteger(String field, int incr) {
		int i = getInteger(field, 0) + incr;
		put(field, i);
		return i;
	}

	/**
	 * Increments the Integer under field by 1 and returns the incremented value. If there is not such @field, it is set to 1.
	 */
	public Integer incrInteger(String field) {
		return incrInteger(field, 1);
	}

	public Record getRecord(String field) {
		return asRecord(get(field));
	}

	public List getList(String field) {
		return asList(get(field));
	}

	public synchronized List<String> getFieldsOrdered() {
		List<String> fields = new ArrayList<>();
		RecordInfo info = getRecordInfo();
		fields.addAll(info.orderedFields);
		fields.addAll(dynamicFields.keySet());
		return fields;
	}

	public synchronized Set<String> getFields() {
		HashSet<String> fields = new HashSet<>();
		fields.addAll(dynamicFields.keySet());
		RecordInfo info = getRecordInfo();
		fields.addAll(info.classFields.keySet());
		fields.addAll(info.getMethodFields.keySet());
		return fields;
	}

	public synchronized Set<String> getPersistentFields() {
		HashSet<String> fields = new HashSet<>();
		fields.addAll(dynamicFields.keySet());
		RecordInfo info = getRecordInfo();
		fields.addAll(info.classFields.keySet());
		for (String f : info.getMethodFields.keySet()) {
			if (info.setMethodFields.containsKey(f))
				fields.add(f);
		}
		return fields;
	}

	public int size() {
		return getFields().size();
	}

	public boolean empty() {
		return size() == 0;
	}

	//TODO: should support nested keys
	public synchronized void remove(String key) {
		dynamicFields.remove(key);
	}

	@Override
	public String toString() {
		Map map = toMap();
		for (Object key : new ArrayList<String>(map.keySet())) {
			Object val = map.get(key);
			if (val instanceof Double || val instanceof Float) {
				map.put(key, String.format(Locale.US, "%.2f", val));
			}
			if (map.get(key) == null)
				map.remove(key);
		}
		return getClass().getSimpleName() + map.toString(); 
	}

	public Map toMap() {
		HashMap<String,Object> map = new HashMap<String,Object>(size());
		for (String field : getFields()) {
			map.put(field, get(field));
		}
		return map;
	}

	/*
	public Map toMapDeep() {
		HashMap<String,Object> map = new HashMap<String,Object>(size());
		for (String field : getFields()) {
			Object value = get(field);
			if (value != null && value instanceof Record)
				value = ((Record)value).toMapDeep();
			map.put(field, value);
		}
		return map;
	}
	 */

	/**
	 * Converts the Record to a JSONObject
	 * @return JSONObject
	 */
	public JsonObject toJSON() {
		JsonObject json = new JsonObject();
		if (this.getClass() != Record.class) {
			json.add("class", this.getClass().getName());
		}
		for (String key : getPersistentFields()) {
			Object val = get(key);
			if (val != null) {
				if (key.equals("class")) {
					System.err.println("Warning: fields with the name 'class' are not allowed");
				} else if (val instanceof Float) {
					json.add(key, (Float)val);
				} else if (val instanceof Double) {
					json.add(key, (Double)val);
				} else if (val instanceof Integer) {
					json.add(key, (Integer)val);
				} else if (val instanceof Long) {
					json.add(key, (Long)val);
				} else if (val instanceof Boolean) {
					json.add(key, (Boolean)val);
				} else if (val instanceof String) {
					json.add(key, (String)val);
				} else if (val instanceof Record) {
					json.add(key, ((Record)val).toJSON());
				} else if (val instanceof List) {
					json.add(key, toJsonArray((List)val));
				} else if (val instanceof LocalDateTime) {
					json.add(key, new LocalDateTimeStringConverter().toString((LocalDateTime) val));
				} else if (val instanceof LocalDate) {
					json.add(key, new LocalDateStringConverter().toString((LocalDate) val));
				} else {
					System.err.println("Warning: could not convert " + val.getClass() + " to JSON");
				}
			}
		}
		return json;
	}
	

	
	/**
	 * Saves the record in JSON format to a file. If the file already exists, the content in the file is overwritten with the new record data.
	 * 
	 * @param inputJSONfile The file the record data are stored in
	 * @throws IOException
	 */
	public void toJSON (File inputJSONfile) throws IOException {
		//Without the if the method will throw a NullPointer when no parent is directly specified when creating the new file
		if (inputJSONfile.getParent() != null) {
			if (!inputJSONfile.getParentFile().exists()) {
				inputJSONfile.getParentFile().mkdirs();
			}
		}
		OutputStream out = new FileOutputStream(inputJSONfile);
		final PrintStream printStream = new PrintStream(out);
		printStream.print(toJSON().toString());
		printStream.close();
	}

	private static JsonArray toJsonArray(List list) {
		JsonArray arr = new JsonArray();
		for (Object val : list) {
			if (val instanceof Float) {
				arr.add((Float)val);
			} else if (val instanceof Double) {
				arr.add((Double)val);
			} else if (val instanceof Integer) {
				arr.add((Integer)val);
			} else if (val instanceof Long) {
				arr.add((Long)val);
			} else if (val instanceof Boolean) {
				arr.add((Boolean)val);
			} else if (val instanceof String) {
				arr.add((String)val);
			} else if (val instanceof Record) {
				arr.add(((Record)val).toJSON());
			} else if (val instanceof List) {
				arr.add(toJsonArray((List)val));
			} else if (val instanceof LocalDateTime) {
				arr.add(new LocalDateTimeStringConverter().toString((LocalDateTime) val));
			} else if (val instanceof LocalDate) {
				arr.add(new LocalDateStringConverter().toString((LocalDate) val));
			} else {
				System.err.println("Warning: could not convert " + val.getClass() + " to JSON");
			}
		}
		return arr;
	}

	public static Record fromJSON(URL resource) throws IOException, JsonToRecordException {
		return fromJSON(IOUtils.toString(resource.openStream(), "UTF-8"));
	}
	
	/**
	 * Reads a Record from a Properties file.
	 * 
	 * @param file JSON file with Record data
	 * @return Record
	 * @throws IOException
	 * @throws JsonToRecordException
	 */
	public static Record fromJSON(File file) throws IOException, JsonToRecordException {
		return fromJSON(Utils.readTextFile(file));
	}

	/**
	 * Converts the JSON compatible String to a Record.
	 * @throws JsonToRecordException 
	 */
	public static Record fromJSON(String string) throws JsonToRecordException {
		try {
			JsonObject jsonObject = JsonObject.readFrom(string);
			return parseJsonObject(jsonObject);
		} catch (ParseException e) {
			throw new JsonToRecordException(e.getMessage());
		}
	}
	
	public static Object fromJSONValue(String string) throws JsonToRecordException {
		try {
			JsonValue json = JsonValue.readFrom(string);
			return parseJsonValue(json);
		} catch (ParseException e) {
			throw new JsonToRecordException(e.getMessage());
		}
	}

	private static Record parseJsonObject(JsonObject json) throws JsonToRecordException {
		try {
			Record record;
			if (json.get("class") != null) {
				try {
					Constructor<?> constructor = Class.forName(json.get("class").asString()).getDeclaredConstructor();
					constructor.setAccessible(true);
					record = (Record) constructor.newInstance(null);
				} catch (ClassNotFoundException e) {
					record = new Record();
				}
			} else {
				record = new Record();
			}
			for(String name : json.names()) {
				if (!name.equals("class")) {
					JsonValue jvalue = json.get(name);
					record.put(name, parseJsonValue(jvalue));
				}
			}
			//System.out.println(json + " " + record);
			return record;
		//} catch (ClassNotFoundException e) {
		//	throw new JsonToRecordException("Class not found: " + e.getMessage());
		} catch (InstantiationException e) {
			throw new JsonToRecordException("Could not create: " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new JsonToRecordException("Could not access: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new JsonToRecordException("Illegal argument: " + e.getMessage());
		} catch (InvocationTargetException e) {
			throw new JsonToRecordException("Invocation problem: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new JsonToRecordException("No such method: " + e.getMessage());
		} catch (SecurityException e) {
			throw new JsonToRecordException("Securiry problem: " + e.getMessage());
		}
	}

	public static class JsonToRecordException extends IOException {

		public JsonToRecordException(String message) {
			super(message);
		}

	}

	private static Object parseJsonValue(JsonValue value) throws JsonToRecordException {
		if (value.isObject()) {
			return parseJsonObject(value.asObject());
		} else if (value.isNumber()) {
			try {
				return value.asInt();
			} catch (NumberFormatException e) {
				return value.asFloat();
			}
		} else if (value.isBoolean()) {
			return value.asBoolean();
		} else if (value.isArray()) {
			ArrayList<Object> array = new ArrayList<Object>();
			JsonArray ja = value.asArray();
			for (int i = 0; i < ja.size(); i++) {
				array.add(parseJsonValue(ja.get(i)));
			}
			return array;
		} else {
			return value.asString();
		}
	}

	public String toStringIndent() {
		return toStringIndent(0);
	}

	protected String toStringIndent(int level) {
		String result = getClass().getSimpleName() + "{";
		int n = 0;
		for (String key : getFields()) {
			Object val = get(key);
			if (val == null)
				continue;
			if (n > 0) {
				result += "\n" + indent(level + 1);
			}
			result += key + ": ";
			String value = value(val, level + 2);
			if (value.contains("\n")) {
				result += "\n" + indent(level + 2) + value;
			} else {
				result += value;
			}
			n++;
		}
		result += "}";
		return result;
	}

	private static String value(Object o, int level) {
		if (o instanceof List) {
			return toStringIndent(((List)o), level);
		} else if (o instanceof Record) {
			return ((Record)o).toStringIndent(level);
		} else if (o instanceof Double || o instanceof Float) {
			float value = asFloat(o);
			if (Math.abs(value) < 1)
				return String.format(Locale.US, "%.3f", value);
			else
				return String.format(Locale.US, "%.2f", value);
		} else {
			return ("" + o).trim(); 
		}
	}

	private static String indent(int level) {
		String result = "";
		for (int i = 0; i < level; i++)
			result += " ";
		return result;
	}

	private static String toStringIndent(List list, int level) {
		String result = "[";
		boolean multiline = false;
		List<String> items = new ArrayList<>();
		for (Object item : list) {
			String value = value(item, level + 1);
			items.add(value);
			multiline = multiline || value.contains("\n");
		}
		int n = 0;
		for (String item : items) {
			if (n > 0 && multiline) {
				result += ",\n" + indent(level+1);
			} else if (n > 0) {
				result += ", ";
			}
			result += item;
			n++;
		}
		result += "]";
		return result;
	}

	/*
	public static void main(String[] args) {
		Record r = new Record();
		r.put("test", 1);
		r.put("asdaafaf", 2);
		Record r2 = new Record();
		r.put("kfjkf", r2);
		r2.put("jsjs", 23);
		r2.put("js", 2324);
		List l1 = new ArrayList();
		r2.put("list", l1);
		l1.add(new Record("a", "b", "casd", "d"));
		l1.add(3);
		System.out.println(r.toStringIndent());
	}
	 */

	/**
	 * Converts the Record data to properties format.
	 * 
	 * @return Properties object with the record data
	 */
	public Properties toProperties() {
		Properties prop = new Properties();
		writeProperties(prop, "", this);
		return prop;
	}

	/**
	 * Saves the record in a properties file. If the file already exists, the content in the file is overwritten with the new record data.
	 * 
	 * @param file The file the record data are stored in
	 * @throws IOException
	 */
	public void toProperties(File file) throws IOException {
		Properties prop = toProperties();
		 //Without the if the method will throw a NullPointer when no parent is directly specified when creating the new file
		if (file.getParent() != null) {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
		}
		OutputStream out = new FileOutputStream(file);
		prop.store(out, "");
		out.close();
		
	}

	private static void writeProperties(Properties prop, String key, Record rec) {
		for (String field : rec.getFields()) {
			Object o = rec.get(field);
			String nkey;
			if (key.length() > 0)
				nkey = key + "." + field;
			else
				nkey = field;
			if (o instanceof Record) {
				writeProperties(prop, nkey, (Record)o);
			} else {
				prop.put(nkey, "" + o);
			}
		}
	}
	
	/**
	 * Converts an InputStream of a Properties file into a Record object.
	 * 
	 * @param inputStream InputStream from properties file with Record data
	 * @return Record
	 */
	public static Record fromProperties(InputStream inputStream) throws IOException {
		Properties prop = new Properties();
		prop.load(inputStream);
		return fromProperties(prop);
	}

	/**
	 * Converts a Properties object into a Record object.
	 * @param prop Properties file with Record data
	 * @return Record
	 */
	public static Record fromProperties(Properties prop) {
		Record rec = new Record();
		for (Object key : prop.keySet()) {
			rec.put(key.toString().replace(".", ":"), prop.get(key));
		}
		return rec;
	}

	/**
	 * Reads a Record from a Properties file.
	 * 
	 * @param file A file with record data in properties format
	 * @return Record
	 * @throws IOException
	 */
	public static Record fromProperties(File file) throws IOException {
		return fromProperties(new FileInputStream(file));
	}
	
	@Override
	public Record clone() {
		try {
			Constructor<?> constructor = getClass().getDeclaredConstructor();
			constructor.setAccessible(true);
			Record clone = (Record) constructor.newInstance(null);
			clone.putAll(this);
			return clone;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Record deepClone() {
		try {
			Constructor<?> constructor = getClass().getDeclaredConstructor();
			constructor.setAccessible(true);
			Record clone = (Record) constructor.newInstance(null);
			for (String field : this.getFields()) {
				Object value = get(field);
				if (value != null) {
					if (value instanceof Record) 
						clone.put(field, ((Record)value).deepClone());
					else
						clone.put(field, value);
				}
			}
			return clone;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (this == obj) {
			return true;
		} else if (obj.getClass() == this.getClass()) {
			for (String field : getFields()) {
				Object o1 = get(field);
				Object o2 = ((Record)obj).get(field);
				if (o1 == null && o2 == null) { 
				} else if (o1 == null || o2 == null) {
					return false;
				} else if (!o1.equals(o2)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public void adjoin(Record record) {
		for (String field : record.getFields()) {
			Object value = record.get(field);
			if (value != null) {
				Object existing = this.get(field);
				if (existing instanceof List) {
					((List)existing).addAll((List)value);
				} else if (existing instanceof Record && value instanceof Record) {
					((Record)existing).adjoin((Record)value);
				} else {
					this.put(field, value);
				}
			}
		}
	}

	public List getValues() {
		ArrayList values = new ArrayList();
		for (String field : getFields()) {
			Object value = get(field);
			if (value != null)
				values.add(value);
		}
		return values;
	}

	@Target({ElementType.METHOD, ElementType.FIELD}) 
	@Retention(RetentionPolicy.RUNTIME)
	public @interface RecordField {
		String name() default "DEFAULT";

		int order() default Integer.MAX_VALUE;
	}


}
