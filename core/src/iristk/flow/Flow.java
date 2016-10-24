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
package iristk.flow;

import iristk.system.IrisUtils;
import iristk.util.InMemoryCompiler;
import iristk.util.RandomList.RandomModel;
import iristk.util.RandomMap;
import iristk.util.Record;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

public abstract class Flow {
	
	private static Logger logger = IrisUtils.getLogger(Flow.class);
	
	private RandomMap randomMap = new RandomMap();
	protected Object flowPool;

	public Class<? extends State> getInitialStateClass() {
		return null;
	}
	
	public State getInitialState() {
		return null;
	}
	
	final public Class<? extends State> getStateClass(String name) {
		try {
			return (Class<? extends State>) Class.forName(getClass().getName() + "$" + name, true, getClass().getClassLoader());
		} catch (Exception e) {
			logger.error("Could not get state with name " + name, e);
		}
		return null;
	}
	
	final public State getState(String name) {
		try {
			Class<? extends State> clazz = getStateClass(name);
			if (clazz == null)
				return null;
			Constructor<? extends State> constr = clazz.getDeclaredConstructor(getClass());
			constr.setAccessible(true);
			return constr.newInstance(this);
		} catch (Exception e) {
			logger.error("Could not get state with name " + name, e);
		}
		return null;
	}
	
	//protected int random(int seed, int n) {
	//	return randomMap.next(seed, n);
	//}
	
	protected int random(int seed, int n, RandomModel model) {
		return randomMap.next(seed, n, model);
	}
	
	protected int randomInt(Number min, Number max) {
		return randomMap.getInt(1 + max.intValue() - min.intValue()) + min.intValue();
	}
	
	protected float randomFloat(Number min, Number max) {
		return min.floatValue() + (randomMap.getFloat() * (max.floatValue() - min.floatValue()));
	}
	
	protected List newList(Object... init) {
		List result = new ArrayList();
		for (Object item : init) {
			result.add(item);
		}
		return result;
	}
	
	protected static boolean eq(Object s1, Object s2) {
		return (s1 == s2 || (s1 != null && s2 != null && s1.equals(s2)));
	}
	
	protected static boolean eqnn(Object s1, Object s2) {
		return (s1 != null && s2 != null && s1.equals(s2));
	}
	
	private Matcher matcher;
	
	protected boolean matches(Object str, String regexp) {
		if (str != null) {
			matcher = Pattern.compile(regexp).matcher(str.toString());
			return (matcher.find());
		} else {
			matcher = null;
			return false;
		}
	}
	
	protected String lastMatch(int group) {
		if (matcher != null)
			return matcher.group(0);
		else
			return null;
	}
	
	protected String lastMatch() {
		return lastMatch(0);
	}
	
	protected List<String> getFields(Object record) {
		if (record instanceof Record) {
			return new ArrayList<>(((Record)record).getFields());
		} else {
			return new ArrayList<>();
		}
	}
	
	public abstract Object getVariable(String name);
	
	/*
	protected Record parameters(Object... args) {
		Record params = new Record();
		int start = 0;
		if (args.length  % 2 == 1 && args[0] instanceof FlowEvent) {
			start = 1;
			params.putAll((FlowEvent)args[0]);
		}
		for (int i = start; i < args.length; i += 2) {
			if (i < args.length - 1) {
				params.put(args[i].toString(), args[i + 1]);
			}
		}
		return params;
	}
	*/
	
	protected String randstr(Object... strings) {
		return randstr(Arrays.asList(strings));
	}
	
	protected String randstr(List<Object> strings) {
		StringBuilder istr = new StringBuilder();
		List<String> alt = new ArrayList<String>(strings.size());
		for (Object ostr : strings) {
			if (ostr != null) {
				String str = ostr.toString().trim();
				if (str.length() > 0) {
					istr.append(str);
					alt.add(str);
				}
			}
		}
		if (alt.size() == 0)
			return "";
		int index = istr.toString().hashCode();
		int i = randomMap.next(index, alt.size());
		return alt.get(i);
	}
	
	protected String firststr(String... strings) {
		return firststr(Arrays.asList(strings));
	}
	
	protected String firststr(List<String> strings) {
		for (String str : strings) {
			if (str != null && str.length() > 0) {
				return str;
			}
		}
		return "";
	}
	
	protected String str(Boolean cond, Double prob, Object string) {
		if ((cond == null || cond) && string != null && (prob == null || randomMap.getFloat() < prob)) {
			return string.toString();
		} else {
			return "";
		}
	}
	
	protected String concat(Object... objects) {
		if (objects.length == 1)
			return objects[0] + "";
		StringBuilder result = new StringBuilder();
		for (Object object : objects) {
			String string = object + "";
			if (string.length() > 0) {
				if (result.length() > 0)
					result.append(" ");
				result.append(string);
			}
		}
		return result.toString();
	}

	/*
	public static Flow read(String file, String srcFolder, String binFolder) throws FlowCompilerException {
		try {
			File binDir = new File(binFolder);
			if (!binDir.exists())
				binDir.mkdirs();
			URLClassLoader classLoader = new URLClassLoader(new URL[] {binDir.toURI().toURL()}); 
			File flowFile = new File(file);
			flowFile.lastModified();
			FlowCompiler compiler = new FlowCompiler(flowFile);
			File modFile = new File(binFolder + "/" + compiler.getFlowName().replaceAll("\\.", "/") + ".mod");
			try {
				if (modFile.exists()) {
					long lastMod = Long.parseLong(Utils.readTextFile(modFile));
					if (lastMod == flowFile.lastModified()) {
						Class<Flow> flowClass = (Class<Flow>) classLoader.loadClass(compiler.getFlowName());
						return flowClass.newInstance();
					}
				}
			} catch (ClassNotFoundException e1) {
			} catch (SecurityException e) {
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (IOException e) {
			} 
			System.out.println("Compiling " + compiler.getFlowName());
			File srcFile = compiler.compile(new File(srcFolder));
			JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
			if (javaCompiler == null) {
				throw new FlowCompilerException("Could not find Java Compiler");
			}
			if (javaCompiler.run(null, null, null, srcFile.getPath(), "-d", binDir.getAbsolutePath()) == 0) {
				Utils.writeTextFile(modFile, "" + flowFile.lastModified());
				Class<Flow> flowClass = (Class<Flow>) classLoader.loadClass(compiler.getFlowName());
			    return flowClass.newInstance();
			} else {
				throw new FlowCompilerException("Could not compile to Jave byte code");
			}
		} catch (InstantiationException e) {
			throw new FlowCompilerException(e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new FlowCompilerException(e.getMessage());
	    } catch (IllegalAccessException e) {
			throw new FlowCompilerException(e.getMessage());
		} catch (IOException e) {
			throw new FlowCompilerException(e.getMessage());
		}
	}

	public static Flow read(String flowFile) throws FlowCompilerException {
		String path = System.getProperty("java.io.tmpdir") + File.separator + "iristk" + File.separator + "flow";
		return read(flowFile, path, path);
	} 
	 */
	
	public static Flow compile(File flowFile) throws FlowCompilerException {
		return compile(new FlowCompiler(flowFile));
	}
	
	public static Flow compile(iristk.xml.flow.Flow flowXml) throws FlowCompilerException {
		return compile(new FlowCompiler(flowXml));
	}
	
	private static Flow compile(FlowCompiler compiler) throws FlowCompilerException {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			compiler.useUniqueNames(true);
			compiler.compileToStream(out);
			return (Flow) InMemoryCompiler.newInstance(compiler.getFlowName(), new String(out.toByteArray()));
		} catch (ClassNotFoundException e) {
			throw new FlowCompilerException(e.getMessage());
		} catch (InstantiationException e) {
			throw new FlowCompilerException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new FlowCompilerException(e.getMessage());
		}
	}

	//public void initTimers(FlowRunner flowRunner) {
	//}
	

	protected void log(Object object) {
		IrisUtils.getLogger(getClass()).info(object + "");
	}
	

}

