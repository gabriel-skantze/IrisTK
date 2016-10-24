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
package iristk.cfg;

import iristk.util.Record;

import java.util.ArrayList;
import java.util.HashMap;

import org.mozilla.javascript.*;

public class TagScript {

	private final Function function;
	private final Context jsContext;
	private final Scriptable jsScope;
	private static int tagCounter = 0;
	private static HashMap<String,TagScript> cache = new HashMap<String,TagScript>();
	
	public static TagScript newTagScript(String id, String tags) {
		if (!tags.endsWith(";"))
			tags = tags += ";";
		if (cache.containsKey(id)) {
			return cache.get(id);
		} else {
			TagScript script = new TagScript(id, tags);
			cache.put(id, script);
			return script;
		}
	}
	
	private TagScript(String id, String tags) {
		jsContext = Context.enter();
		jsScope = new ImporterTopLevel(jsContext);
		function = jsContext.compileFunction(jsScope, "function sem(rules, meta, out) {" + tags + "return out}", id, 1, null);
		Context.exit();
	}

	public Object eval(Object rules, Object meta, Object out) {
		 return function.call(jsContext, jsScope, jsScope, new Object[]{rules, meta, out});
	}
	
	public static Object eval(String tagScript, RuleEdge ruleEdge, Object out) {
		if (tagScript.contains("=") || tagScript.contains(".")) {
			TagScript script = TagScript.newTagScript("tag" + tagCounter++, tagScript);
			NativeObject rules = new NativeObject();
			NativeObject meta = new NativeObject();
			for (RuleEdge re : ruleEdge.getSubRules()) {
				ScriptableObject.putProperty(rules, re.getRuleId(), re.getNativeSem());
				NativeObject metaInfo = new NativeObject();
				ScriptableObject.putProperty(meta, re.getRuleId(), metaInfo);
				ScriptableObject.putProperty(metaInfo, "text", re.getWordString());
			}
			if (out == null)
				out = new NativeObject();
			Object result = script.eval(rules, meta, out);
			return result;
		} else {
			tagScript = tagScript.trim();
			try {
				return Integer.parseInt(tagScript);
			} catch (NumberFormatException e) {
				return tagScript;
			}
		}
	}
	
	private static TagScript cloneScript = new TagScript("2323", "out = JSON.parse(JSON.stringify(rules));");
	
	public static Object cloneNativeObject(Object obj) {
		NativeObject meta = new NativeObject();
		return cloneScript.eval(obj, meta, null);
	}
	
	public static Object jsObjectToSem(Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof NativeObject) {
			Record record = new Record();
			NativeObject no = (NativeObject) obj;
			for (Object pid : ScriptableObject.getPropertyIds(no)) {
				Object prop = ScriptableObject.getProperty(no,pid.toString());
				record.put(pid.toString(), jsObjectToSem(prop));
			}
			return record;
		} else if (obj instanceof NativeArray) {
			NativeArray arr = (NativeArray)obj;
			ArrayList result = new ArrayList();
			for (int i = 0; i < arr.getLength(); i++) {
				result.add(jsObjectToSem(arr.get(i)));
			}
			return result;
		} else {
			if (obj instanceof Number) {
				// TODO: this is actually Double, regardless of number type, we only create integers now
				return ((Number)obj).intValue();
			} else if (obj instanceof Boolean) {
				return obj;
			} else {
				return obj.toString();
			}
		}
	}
	
	/*
	public static Record nativeSemToRecord(Object nativeSem) {
		Object sem = jsObjectToSem(nativeSem);
		if (sem != null && sem instanceof Record)
			return (Record) sem;
		else
			return new Record();
	}
	*/

	public static void main(String[] args) {
		TagScript script = TagScript.newTagScript("53", "JSON.parse(JSON.stringify(rules))");
		NativeObject rec = new NativeObject();
		ScriptableObject.putProperty(rec, "a", "b");
		NativeObject rules = new NativeObject();
		ScriptableObject.putProperty(rules, "kalle", rec);
		NativeObject meta = new NativeObject();
		Object o = script.eval(rules, meta, null);
		if (o instanceof NativeObject) {
			NativeObject no = (NativeObject) o; 
			System.out.println(ScriptableObject.getProperty(no, "_value"));
		} else {
			System.out.println(o);
		}
	}

	
}
