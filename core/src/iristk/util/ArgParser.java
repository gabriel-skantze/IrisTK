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
import java.util.HashMap;
import java.util.List;

public class ArgParser {

	HashMap<String,Object> args = new HashMap<String,Object>();
	HashMap<String,String> descs = new HashMap<String,String>();
	HashMap<String,String> tokens = new HashMap<String,String>();
	HashMap<String,Class<?>> types = new HashMap<String,Class<?>>();
	HashMap<String,Boolean> required = new HashMap<String,Boolean>();
	HashMap<String,Object> defaults = new HashMap<String,Object>();
	ArrayList<String> argList = new ArrayList<String>();
	
	private boolean allowRestArguments = false;
	private List<String> restArguments = new ArrayList<>();

	public void addRequiredArg(String name, String desc, String token, Class<?> type) {
		this.argList.add(name);
		this.descs.put(name, desc);
		this.types.put(name, type);
		this.tokens.put(name, token);
		this.required.put(name, true);
	}

	public void addOptionalArg(String name, String desc, String token, Class<?> type, Object defaultv) {
		this.argList.add(name);
		this.descs.put(name, desc);
		this.types.put(name, type);
		this.tokens.put(name, token);
		this.required.put(name, false);
		this.defaults.put(name, defaultv);
	}

	public void addBooleanArg(String name, String desc) {
		this.argList.add(name);
		this.descs.put(name, desc);
		this.types.put(name, Boolean.class);
		this.required.put(name, false);
		this.defaults.put(name, false);
	}

	public Object get(String name) {
		if (has(name))
			return args.get(name);
		else
			return defaults.get(name);
	}

	public boolean has(String name) {
		return args.containsKey(name);
	}

	public void parse(String[] cmdargs, int pos, int len) {
		parse(Arrays.copyOfRange(cmdargs, pos, len+pos));
	}
	
	public List<String> getRestArguments() {
		return restArguments;
	}
	
	public void allowRestArguments(boolean b) {
		this.allowRestArguments = b;
	}
	
	public void parse(String[] cmdargs) {
		try {
			String name = null;
			for (int i = 0; i < cmdargs.length; i++) {
				String value = cmdargs[i];
				if (value.startsWith("-")) {
					name = value.substring(1);
					if (argList.contains(name)) {
						if (types.get(name) == Boolean.class) {
							args.put(name, true);
							name = null;
						}
					} else {
						throw new ParseException("Cannot recognize argument: -" + name);
					}
				} else if (name != null) {
					if (types.get(name) == List.class) {
						if (!has(name)) {
							args.put(name, new ArrayList<String>());
						}
						((List)get(name)).add(value);	
					} else {
						Object cval = Converters.asType(value, types.get(name));
						if (cval == null)
							throw new ParseException("Argument '" + value + "' is of incorrect type");
						args.put(name, cval);
						name = null;
					}
				} else {
					if (allowRestArguments) {
						restArguments .add(value);
					} else {
						throw new ParseException("Could not parse: " + value);
					}
				}
			}
			for (String arg : argList) {
				if (required.get(arg) && !has(arg))
					throw new ParseException("Required argument '" + arg + "' is missing");
			}
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			System.err.println("\nArguments: ");
			System.err.println(help());
			System.exit(0);
		}
	}
	
	private class ParseException extends Exception {
		public ParseException(String msg) {
			super(msg);
		}
	}

	public String help() {
		StringBuilder help = new StringBuilder();
		for (String arg : argList) {
			String use = "-" + arg;
			if (tokens.containsKey(arg))
				use += " [" + tokens.get(arg) + "]";
			for (int i = use.length(); i < 20; i++) {
				use += " ";
			}
			help.append("  " + use + descs.get(arg));
			if (required.get(arg)) 
				help.append(" (required)");
			else
				help.append(" (optional)");
			help.append("\n");
		}
		return help.toString();
	}

	public static void main(String[] args) {
		ArgParser parser = new ArgParser();
		parser.allowRestArguments(true);
		parser.addOptionalArg("i", "test", "test2", List.class, null);
		parser.parse(new String[]{"one", "two", "-i", "three"});
		System.out.println(parser.getRestArguments() + " " + parser.get("i"));
	}


}
