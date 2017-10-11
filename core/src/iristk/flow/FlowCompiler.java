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

import iristk.util.FileFinder;
import iristk.util.EnumMap;
import iristk.util.RegExp;
import iristk.util.Replacer;
import iristk.xml.XmlUtils;
import iristk.xml.XmlMarshaller.XMLLocation;
import iristk.xml.flow.Block;
import iristk.xml.flow.Onevent;
import iristk.xml.flow.Ontime;
import iristk.xml.flow.Expr;
import iristk.xml.flow.Reentry;
import iristk.xml.flow.Select;
import iristk.xml.flow.Onentry;
import iristk.xml.flow.Onexit;
import iristk.xml.flow.Param;
import iristk.xml.flow.Random;
import iristk.xml.flow.Repeat;
import iristk.xml.flow.Var;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Locator;

import java.lang.String;
import java.lang.Object;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public class FlowCompiler {

	private static final String stringPattern = "\".*?\"";
	//protected String classPackage;
	//protected String className;
	protected CodeStream code;
	private boolean useUniqueNames = false;
	private static int uniqueNameSuffix = 0;
	private FlowXml flowXml = new FlowXml();
	private String currentState = null;
	private int varnameCount = 0;
	private int currentLineNumber = 0;

	public FlowCompiler(File xmlFile) throws FlowCompilerException {
		flowXml.read(xmlFile);
	}

	public FlowCompiler(iristk.xml.flow.Flow xmlFlow) throws FlowCompilerException {
		flowXml.read(xmlFlow);
	}

	public void compileToFile(File srcFile) throws FlowCompilerException {
		try {
			FileOutputStream fstream = new FileOutputStream(srcFile);
			compileToStream(fstream);
			fstream.close();
		} catch (FileNotFoundException e) {
			throw new FlowCompilerException(e.getMessage());
		} catch (IOException e) {
			throw new FlowCompilerException(e.getMessage());
		}
	}

	public void compileToStream(OutputStream out) throws FlowCompilerException {
		if (useUniqueNames){
			uniqueNameSuffix++;
		}
		code = new CodeStream(out);

		String flowName = getLocalFlowName();

		code.println("package " + flowXml.getPackage() + ";");
		code.println();

		printImports();
		for (String imp : flowXml.getImportedClasses()) {
			code.println("import " + imp + ";");
		}
		code.println();

		code.println("public class " + flowName + " extends " + flowXml.getExtends() + " {");
		code.println();

		printFlowContents(flowXml, flowName, true);

		code.println("}");
	} 

	/*
	private void printInitTimers() {
		code.println("@Override");
		code.println("public void initTimers(FlowRunner flowRunner) {");
		for (iristk.xml.flow.State state : flowReader.getStates()) {
			for (ActionSequenceType trigger : state.getTrigger()) {
				if (trigger instanceof Ontime) {
					Ontime ontime = (Ontime) trigger;
					String interval = ontime.getInterval();
					if (interval != null) {
						if (interval.contains("-")) {
							String[] cols = interval.split("-");
							int min = Integer.parseInt(cols[0]);
							int max = Integer.parseInt(cols[1]);
							code.println("new EventClock(flowRunner, " + min + ", " + max + ", \"timer_" + trigger.hashCode() + "\");");
						} else {
							int time = Integer.parseInt(interval);
							code.println("new EventClock(flowRunner, " + time + ", \"timer_" + trigger.hashCode() + "\");");
						}
					}
				} 
			}
		}
		code.println("}");
		code.println();
	}

	 */

	private void printFlowContents(FlowXml flowReader, String flowName, boolean topFlow) throws FlowCompilerException {

		printVariableDeclarations(flowReader.getVariables(), flowReader.getParameters());
		code.println();
		printInit(flowReader.getVariables());
		code.println();
		printVariableAccessors(flowReader.getVariables(), flowReader.getParameters(), !topFlow);
		code.println();

		if (topFlow && flowReader.getParameters().size() > 0) {
			String paramlist = "";
			String paramnamelist = "";
			for (Param param : flowReader.getParameters()) {
				if (paramlist.length() > 0) {
					paramlist += ", ";
					paramnamelist += ", ";
				}
				paramlist += param.getType() + " " + param.getName();
				paramnamelist += param.getName();
			}
			code.println("public " + flowName + "(" + paramlist + ") {");
			for (Param param : flowReader.getParameters()) {
				code.println("this." + param.getName() + " = " + param.getName() + ";");
			}
			code.println("initVariables();");
			code.println("}");
		} else {
			code.println("public " + flowName + "() {");
			code.println("initVariables();");
			code.println("}");
		}
		code.println();

		if (flowReader.getInitial() != null) {
			code.println("@Override");
			code.println("public State getInitialState() {return new " + flowReader.getInitial() + "();}");
			code.println();
		}

		//List<String> publicStates = new ArrayList<>();
		//for (iristk.xml.flow.State state : flowReader.getStates()) {
		//	if (state.isPublic()) {
		//		publicStates.add("\"" + state.getId() + "\"");
		//	}
		//}

		//code.println("public static String[] getPublicStates() {");
		//code.println("return new String[] {" + StringUtils.join(publicStates.toArray(new String[0]), ", ") + "};");
		//code.println("}");

		//printInitTimers();

		printStates(flowReader.getStates());
		printSubFlows(flowReader.getSubFlows());

		code.println();
	}

	private void printSubFlows(List<FlowXml> subFlows) throws FlowCompilerException {
		for (FlowXml flowReader : subFlows) {
			code.println((flowReader.isPublic() ? "public" : "private") +
					(flowReader.isStatic() ? " static" : "") +
					" class " + flowReader.getName() + " extends " + flowReader.getExtends() + " {");
			code.println();

			printFlowContents(flowReader, flowReader.getName(), false);

			code.println("}");
		}
	}

	private boolean stateExists(String name) {
		for (iristk.xml.flow.State state: flowXml.getStates()) {
			if (state.getId().equals(name)){
				return true;
			}
		}
		return false;
	}

	private void printStates(List<iristk.xml.flow.State> states) throws FlowCompilerException {
		for (iristk.xml.flow.State state : states) {
			currentState  = state.getId();
			String ext = "State";
			if (state.getExtends() != null) {
				ext = state.getExtends();
			}
			code.println();
			String decl = "";
			if (state.isPublic() || flowXml.getInitial().equals(state.getId())){
				decl += "public";
			}
			else{
				decl += "private";
			}
			if (state.isStatic()){
				decl += " static";
			}
			decl += " class " + currentState + " extends " + ext;
			if (flowXml.getInitial() != null && flowXml.getInitial().equals(state.getId())) {
				decl += " implements Initial";
			}
			code.println(decl + " {");
			code.println();
			code.println("final State currentState = this;");
			if (state.getParam() != null) {
				printParameters(state.getParam());
			}

			if (state.getVar() != null) {
				printVariables(state.getVar());
				code.println();
			}

			code.println("@Override");
			code.println("public void setFlowThread(FlowRunner.FlowThread flowThread) {");
			code.println("super.setFlowThread(flowThread);");
			for (Object trigger : state.getTrigger()) {
				if (trigger instanceof Ontime) {
					Ontime ontime = (Ontime) trigger;
					String interval = ontime.getInterval();
					if (interval != null) {
						if(interval.contains(",")){// new usage, in case dev wants to have e.g "x , 400-x"
							//Assuming interval is now well-formed and resolves to (int, int)
							code.println("flowThread.addEventClock(" + interval + ", \"timer_" + trigger.hashCode() + "\");");

						}
						else if (interval.contains("-")) {
							String[] cols = interval.split("-");
							try{
								int min = Integer.parseInt(cols[0]);
								int max = Integer.parseInt(cols[1]);
								code.println("flowThread.addEventClock(" + min + ", " + max + ", \"timer_" + trigger.hashCode() + "\");");
							}
							catch(NumberFormatException e){//In case developer uses variable names instead of an integer.
								code.println("flowThread.addEventClock(" + cols[0] + ", " + cols[1] + ", \"timer_" + trigger.hashCode() + "\");");
							}
							//code.println("new EventClock(flowRunner, " + min + ", " + max + ", \"timer_" + trigger.hashCode() + "\");");
						} else {
							try{
								int time = Integer.parseInt(interval);
								code.println("flowThread.addEventClock(" + time + ", " + time + ", \"timer_" + trigger.hashCode() + "\");");
								//code.println("new EventClock(flowRunner, " + time + ", \"timer_" + trigger.hashCode() + "\");");
							}
							catch(NumberFormatException e){//In case developer uses variable names instead of an integer.
								code.println("flowThread.addEventClock(" + interval + ", " + interval + ", \"timer_" + trigger.hashCode() + "\");");
							}
						}
					}//if interval !null
				}//if instanceof ontime 
			}
			code.println("}");
			code.println();

			printOnEntry(state.getTrigger());
			code.println();
			printEventTriggers(state.getTrigger());
			printOnExit(state.getTrigger());
			code.println();
			code.println("}");
			code.println();
		}
	}

	protected void printImports() {
		code.println("import java.util.List;");
		code.println("import java.io.File;");
		code.println("import iristk.xml.XmlMarshaller.XMLLocation;");
		code.println("import iristk.system.Event;");
		code.println("import iristk.flow.*;");
		code.println("import iristk.util.Record;");
		code.println("import static iristk.util.Converters.*;");
		code.println("import static iristk.flow.State.*;");
	}

	private void printVariables(List<Var> vars) throws FlowCompilerException {
		for (Var var : vars) {
			code.println("public " + variable(var));
		}
	}

	private void printVariableDeclarations(List<Var> vars, List<Param> params) throws FlowCompilerException {
		for (Param param : params) {
			code.println("private " + param.getType() + " " + param.getName() + ";");
		}
		for (Var var : vars) {
			code.println("private " + var.getType() + " " + var.getName() + ";");
		}
	}

	private void printInit(List<Var> vars) throws FlowCompilerException {
		code.println("private void initVariables() {");
		for (Var var : vars) {
			String ass = variableAssignment(var);
			if (!ass.equals(";"))
				code.println(var.getName() + ass);
		}
		code.println("}");
	}

	private String variableAssignment(Var var) throws FlowCompilerException {
		String line = "";
		if (var.getValue() != null) {
			if (var.getValue().contains("#")) {
				line += " = " + getExternalVar(var) + ";";
			} else {
				String type = var.getType();
				if (type == null)
					type = "String";
				line += " = " + convertType(type, formatAttrExpr(var.getValue())) + ";";
				if (var.getContent() != null && var.getContent().size() > 0) {
					line += "{\n" + formatExec(XmlUtils.nodesToString(var.getContent())) + "\n}";
				}
			}
		} else if (var.getContent() != null && var.getContent().size() > 0) {
			line += " = " +  createExpression(var.getContent(), var);
		} else {
			line += ";";
		}
		return line;
	}

	private String variable(Var var) throws FlowCompilerException {
		return var.getType() + " " + var.getName() + variableAssignment(var);
	}

	private String getExternalVar(Var var) {
		String flowName = var.getValue().substring(0, var.getValue().indexOf("#"));
		String varName = var.getValue().substring(var.getValue().indexOf("#") + 1);
		return flowName + ".getFlow(flowPool).get" + ucFirst(varName) + "()";
	}

	private void printVariableAccessors(List<Var> vars, List<Param> params, boolean paramSetters) {
		for (Var var : vars) {
			code.println("public " + var.getType() + " get" + ucFirst(var.getName()) + "() {");
			code.println("return this." + var.getName() + ";");
			code.println("}");
			code.println();
			code.println("public void set" + ucFirst(var.getName()) + "(" + var.getType() + " value) {");
			code.println("this." + var.getName() + " = value;");
			code.println("}");
			code.println();
		}
		for (Param param : params) {
			code.println("public " + param.getType() + " get" + ucFirst(param.getName()) + "() {");
			code.println("return this." + param.getName() + ";");
			code.println("}");
			code.println();
			if (paramSetters) {
				code.println("public void set" + ucFirst(param.getName()) + "(" + param.getType() + " value) {");
				code.println("this." + param.getName() + " = value;");
				code.println("}");
				code.println();
			}
		}
		code.println("@Override");
		code.println("public Object getVariable(String name) {");
		for (Var var : vars) {
			code.println("if (name.equals(\"" + var.getName() + "\")) return this." + var.getName() + ";");
		}
		for (Param param : params) {
			code.println("if (name.equals(\"" + param.getName() + "\")) return this." + param.getName() + ";");
		}
		code.println("return null;");
		code.println("}");
		code.println();
	}

	private void printParameters(List<Param> params) throws FlowCompilerException {
		for (Param param : params) {
			String var = "public " + param.getType() + " " + param.getName() + " = ";
			if (param.getDefault() != null)
				var += convertType(param.getType(), formatAttrExpr(param.getDefault()));
			else {
				var += "null";
			}
			var += ";";
			code.println(var);
		}
		code.println();
		for (Param param : params) {
			code.println("public void set" + ucFirst(param.getName()) + "(Object value) {");
			code.println("if (value != null) {");
			code.println(param.getName() + " = " + convertType(param.getType(), "value") + ";");
			code.println("params.put(\"" + param.getName() + "\", value);");
			code.println("}");
			code.println("}");
			code.println();
		}
	}

	private String convertType(String type, String value) {
		if (type.equals("String")) {
			return "asString(" + value + ")";
		} else if (type.equals("Record")) {
			return "asRecord(" + value + ")";
		} else if (type.equals("Boolean")) {
			return "asBoolean(" + value + ")";
		}else if (type.equals("Long")) {
			return "asLong(" + value + ")";
		} else if (type.equals("Integer") || type.equals("int")) {
			return "asInteger(" + value + ")";
		} else if (type.equals("Float") || type.equals("float")) {
			return "asFloat(" + value + ")";
		} else if (type.equals("Double") || type.equals("double")) {
			return "asDouble(" + value + ")";
		} else if (type.equals("List")) {
			return "asList(" + value + ")";
		} else if (type.equals("Object")) {
			return value;
		}  else {
			return "(" + type + ") " + value;
		}
	}

	private void printOnEntry(List<?> eventHandlers) throws FlowCompilerException {
		for (Object trigger : eventHandlers) {
			if (trigger instanceof Ontime) {
				String afterentry = ((Ontime)trigger).getAfterentry();
				if (afterentry != null) {
					code.println("iristk.util.DelayedEvent timer_" + trigger.hashCode() + ";");
				}
			}
		}

		code.println("@Override");
		code.println("public void onentry() throws Exception {");
		code.println("int eventResult;");
		code.println("Event event = new Event(\"state.enter\");");

		for (Object trigger : eventHandlers) {
			if (trigger instanceof Ontime) {
				String afterentry = ((Ontime)trigger).getAfterentry();
				if (afterentry != null) {
					code.println("if (timer_" + trigger.hashCode() + " != null) timer_" + trigger.hashCode() + ".forget();");
					code.println("timer_" + trigger.hashCode() + " = flowThread.raiseEvent(new Event(\"timer_" + trigger.hashCode() + "\"), " + formatAttrExpr(afterentry) + ", new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(trigger)) + "));");
					code.println("forgetOnExit(timer_" + trigger.hashCode() + ");");
				}
			}
		}

		for (Object eventHandler : eventHandlers) {
			if (eventHandler instanceof Onentry) {
				printLocation(eventHandler);
				Onentry onentry = (Onentry) eventHandler;
				code.println("try {");
				code.println("EXECUTION: {");
				code.println("int count = getCount(" + eventHandler.hashCode() + ") + 1;");
				code.println("incrCount(" + eventHandler.hashCode() + ");");
				printActions(onentry.getAny(), onentry, null);
				code.println("}");
				code.println("} catch (Exception e) {");
				code.println("throw new FlowException(e, currentState, event, " + location(flowXml.getLocation(onentry)) + ");");
				code.println("}");
				break;
			}
		}

		code.println("}");
		return;
	}


	private void printOnExit(List<?> eventHandlers) throws FlowCompilerException {
		for (Object eventHandler : eventHandlers) {
			if (eventHandler instanceof Onexit) {
				printLocation(eventHandler);
				Onexit onexit = (Onexit) eventHandler;
				code.println("@Override");
				code.println("public void onexit() {");
				code.println("int eventResult;");
				code.println("Event event = new Event(\"state.exit\");");
				if (onexit != null) {
					code.println("EXECUTION: {");
					printActions(onexit.getAny(), onexit, null);
					code.println("}");
				}
				code.println("super.onexit();");
				code.println("}");
				return;
			}
		}
	}

	private void printEventTriggers(List<?> triggers) throws FlowCompilerException {
		code.println("@Override");
		code.println("public int onFlowEvent(Event event) throws Exception {");
		code.println("int eventResult;");
		code.println("int count;");
		for (Object trigger : triggers) {
			if (trigger instanceof Onevent) {
				printLocation(trigger);
				code.println("try {");
				code.println("count = getCount(" + trigger.hashCode() + ") + 1;");
				Onevent onEventElem = (Onevent) trigger;
				if (onEventElem.getName() != null)
					code.println("if (event.triggers(\"" + onEventElem.getName() + "\")) {");
				if (onEventElem.getCond() != null)
					code.println("if ("+ formatCondExpr(onEventElem.getCond()) + ") {");

				code.println("incrCount(" + trigger.hashCode() + ");");

				code.println("eventResult = EVENT_CONSUMED;");
				code.println("EXECUTION: {");
				printActions(onEventElem.getAny(), trigger, null);
				code.println("}");
				code.println("if (eventResult != EVENT_IGNORED) return eventResult;");

				//for (int i = 0; i < onEventElem.getOtherAttributes().keySet().size(); i++) 
				//	code.println("}");
				if (onEventElem.getCond() != null)
					code.println("}");
				if (onEventElem.getName() != null)
					code.println("}");
				code.println("} catch (Exception e) {");
				code.println("throw new FlowException(e, currentState, event, " + location(flowXml.getLocation(trigger)) + ");");
				code.println("}");

			} else if (trigger instanceof Ontime) {
				printLocation(trigger);
				Ontime onTimeElem = (Ontime) trigger;
				code.println("count = getCount(" + trigger.hashCode() + ") + 1;");
				code.println("if (event.triggers(\"timer_" + trigger.hashCode() + "\")) {");
				code.println("incrCount(" + trigger.hashCode() + ");");
				code.println("eventResult = EVENT_CONSUMED;");
				code.println("EXECUTION: {");
				printActions(onTimeElem.getAny(), trigger, null);
				code.println("}");
				code.println("if (eventResult != EVENT_IGNORED) return eventResult;");
				code.println("}");
			} 
		}
		code.println("eventResult = super.onFlowEvent(event);");
		code.println("if (eventResult != EVENT_IGNORED) return eventResult;");
		code.println("eventResult = callerHandlers(event);");
		code.println("if (eventResult != EVENT_IGNORED) return eventResult;");
		code.println("return EVENT_IGNORED;");
		code.println("}");
	}

	private String ucFirst(String text) {
		return text.substring(0, 1).toUpperCase() + text.substring(1); 
	}

	private String listToString(List<?> list) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			if (i > 0) result.append(", ");
			result.append(list.get(i));
		}
		return result.toString();
	}

	/*
	private List<String> processString(List<?> children) throws FlowCompilerException {
		List<String> result = new ArrayList<String>();
		for (Object child : children) {
			if (child instanceof String) {
				String str = child.toString().trim();
				if (str.length() > 0)
					result.add("\"" + str + "\"");
			} else if (child instanceof Expr) {
				Expr expr = (Expr) child;
				result.add(formatExpr(expr.getValue()));
			} else if (child instanceof Block) {
				Block item = (Block) child;
				if (item.getProb() != null || item.getCond() != null) {
					result.add("str(" + formatCondExpr(item.getCond()) + ", " + item.getProb() + ", " + concatFun(processString(item.getContent())) + ")");
				} else {
					result.add(concatFun(processString(item.getContent())));
				}
			} else if (child instanceof Select || child instanceof Random) {
				String options = (child instanceof Select) ? ((Select)child).getList() : ((Random)child).getList();
				if (options == null) {
					List<Object> actionGroup = (child instanceof Select) ? ((Select)child).getAny() : ((Random)child).getAny();
					if (actionGroup.size() > 0) {
						options = listToString(processString(actionGroup));
					}
				}
				if (options != null) {
					if (child instanceof Random)
						result.add("randstr(" + options  + ")");
					else
						result.add("firststr(" + options  + ")");
				}
			} else if (child instanceof Element) {
				result.add(concatFun(processElement(child)));
			} else if (child instanceof Text) {
				String str = ((Text)child).getNodeValue().trim();
				if (str.length() > 0)
					result.add("\"" + str + "\"");
			}
		}
		return result;
	}

	private String concatFun(List<?> nodes) {
		if (nodes.size() == 0)
			return "\"\"";
		else if (nodes.size() == 1)
			return nodes.get(0).toString();
		else
			return "concat(" + listToString(nodes) + ")";
	}

	private List<String> processElement(Object node) throws FlowCompilerException {
		ArrayList<String> result = new ArrayList<String>();
		if (node instanceof String) {
			String str = ((String)node).trim();
			if (str.length() > 0)
				result.add("\"" + str + "\"");
		} else if (node instanceof Text) {
			String str = ((Text) node).getTextContent().trim();
			if (str.length() > 0)
				result.add("\"" + str + "\"");
		} else if (node instanceof Element) {
			try {
				Object o = flowXml.unmarshal((Element)node);
				List<Object> objects = new ArrayList<Object>();
				objects.add(o);
				//result.addAll(processString(objects));
			} catch (FlowCompilerException e) {
				String estring = "";
				Element en = (Element)node;
				estring += "\"<" + en.getLocalName();
				for (int j = 0; j < en.getAttributes().getLength(); j++) {
					Attr attr = (Attr) en.getAttributes().item(j);
					if (!(attr.getNamespaceURI() != null && attr.getNamespaceURI().equals("http://www.w3.org/2000/xmlns/")) && !attr.getLocalName().equals("xmlns") && !attr.getLocalName().equals("xsi")) {
						estring += " " + attr.getLocalName() + "=\\\"" + attr.getValue() + "\\\"";
					}
				}
				if (en.getChildNodes().getLength() == 0) {
					estring += "/>\"";
					result.add(estring);
				} else {
					estring += ">\"";
					result.add(estring);
					for (int i = 0; i < en.getChildNodes().getLength(); i++) {
						result.addAll(processElement(en.getChildNodes().item(i))); 
					}
					result.add("\"</" + en.getLocalName() + ">\"");
				}
			}
		}
		return result;
	}


	 */

	private String varname(String prefix) {
		return prefix + (varnameCount++);
	}

	private void printLocation(Object obj) {
		if (obj != null) {
			try {
				Method m = obj.getClass().getMethod("sourceLocation", null);
				Locator loc = (Locator) m.invoke(obj, null);
				if (loc != null && loc.getLineNumber() != -1) {
					currentLineNumber  = loc.getLineNumber();
				}
				code.println("// Line: " + currentLineNumber);
			} catch (NoSuchMethodException e) {
			} catch (SecurityException e) {
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) {
			} catch (InvocationTargetException e) {
			}
		}
	}

	protected void printAction(Object action, Object parent, String exprContext) throws FlowCompilerException {
		if (action instanceof JAXBElement<?>) 
			action = ((JAXBElement<?>)action).getValue();
		printLocation(action);
		if (action instanceof iristk.xml.flow.Goto) {
			iristk.xml.flow.Goto gotoAction = (iristk.xml.flow.Goto) action;
			if (exprContext != null)
				throw new FlowCompilerException("<goto> not allowed in expression", gotoAction.sourceLocation().getLineNumber());
			String stateVar = "";
			if (gotoAction.getState() != null) {
				if (!stateExists(gotoAction.getState()))
					throw new FlowCompilerException("State " + gotoAction.getState() + " does not exist", gotoAction.sourceLocation().getLineNumber());
				stateVar = varname("state");
				code.println(stateClass(gotoAction.getState()) + " " + stateVar + " = " + newState(gotoAction.getState()) + ";");
				printSetStateParameters(stateVar, getParameters(gotoAction.getOtherAttributes(), gotoAction.getContent(), gotoAction));
			} else if (gotoAction.getExpr() != null) {
				stateVar = gotoAction.getExpr();
			} else {
				throw new FlowCompilerException("Goto must either have a 'state' or 'expr' attribute", gotoAction.sourceLocation().getLineNumber());
			}
			code.println("flowThread.gotoState(" + stateVar + ", currentState, new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + "));");
			code.println("eventResult = EVENT_ABORTED;");
			code.println("break EXECUTION;");
		} else if (action instanceof iristk.xml.flow.Run) {
			iristk.xml.flow.Run runAction = (iristk.xml.flow.Run) action;
			if (exprContext != null)
				throw new FlowCompilerException("<run> not allowed in expression", runAction.sourceLocation().getLineNumber());
			String stateVar = varname("state");
			code.println(stateClass(runAction.getState()) + " " + stateVar + " = " + newState(runAction.getState()) + ";");
			printSetStateParameters(stateVar, getParameters(runAction.getOtherAttributes(), runAction.getContent(), runAction));
			code.println("FlowRunner.FlowThread " + stateVar + "Thread = flowThread.runState(" + stateVar + ", new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + "));");
		} else if (action instanceof iristk.xml.flow.Call) {
			iristk.xml.flow.Call callAction = (iristk.xml.flow.Call) action;
			if (exprContext != null)
				throw new FlowCompilerException("<call> not allowed in expression", callAction.sourceLocation().getLineNumber());
			String stateVar = varname("state");
			code.println(stateClass(callAction.getState()) + " " + stateVar + " = " + newState(callAction.getState()) + ";");
			printSetStateParameters(stateVar, getParameters(callAction.getOtherAttributes(), callAction.getContent(), callAction));
			code.println("if (!flowThread.callState(" + stateVar + ", new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + "))) {");
			code.println("eventResult = EVENT_ABORTED;");
			code.println("break EXECUTION;");
			code.println("}");
		} else if (action instanceof iristk.xml.flow.Return) {
			iristk.xml.flow.Return returnAction = (iristk.xml.flow.Return)action;
			if (exprContext != null)
				throw new FlowCompilerException("<return> not allowed in expression", returnAction.sourceLocation().getLineNumber());
			if (returnAction.getEvent() != null || returnAction.getCopy() != null) {
				String returnEvent = varname("returnEvent");
				printInitEvent(returnEvent, returnAction.getCopy(), returnAction.getEvent(), getParameters(returnAction.getOtherAttributes(), returnAction.getContent(), returnAction));
				//code.println("flowThread.raiseEvent(" + returnEvent + ", new FlowEventInfo(currentState, event, " + locationConstructor(flowReader.getLocation(action)) + ");");
				code.println("flowThread.returnFromCall(this, " + returnEvent + ", new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + "));");
			} else {
				code.println("flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + "));");
			}
			code.println("eventResult = EVENT_ABORTED;");
			code.println("break EXECUTION;");
		} else if (action instanceof Reentry) {
			//code.println("flowThread.raiseEvent(new EntryEvent(), new FlowEventInfo(currentState, event, " + locationConstructor(flowReader.getLocation(action)) + ");");
			Reentry reentryAction = (Reentry)action;
			if (exprContext != null)
				throw new FlowCompilerException("<reentry> not allowed in expression", reentryAction.sourceLocation().getLineNumber());
			code.println("flowThread.reentryState(this, new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + "));");
			code.println("eventResult = EVENT_ABORTED;");
			code.println("break EXECUTION;");
		} else if (action instanceof iristk.xml.flow.Block) {
			//System.out.println("BLOCK " + currentLineNumber);
			iristk.xml.flow.Block blockAction = (iristk.xml.flow.Block) action;
			String cond = "";
			if (blockAction.getCond() != null) {
				cond = "if (" + formatCondExpr(blockAction.getCond()) + ") ";
			}
			code.println(cond + "{");
			printActions(blockAction.getContent(), action, exprContext);
			code.println("}");
		} else if (action instanceof iristk.xml.flow.Wait) {
			iristk.xml.flow.Wait waitAction = (iristk.xml.flow.Wait) action;
			if (exprContext != null)
				throw new FlowCompilerException("<wait> not allowed in expression", waitAction.sourceLocation().getLineNumber());

			String waitvar = varname("waitState");
			code.println(DialogFlow.class.getName() + ".wait " + waitvar + " = new " + DialogFlow.class.getName() + ".wait();");
			code.println(waitvar + ".setMsec(" + waitAction.getMsec() + ");");
			code.println("if (!flowThread.callState(" + waitvar + ", new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + "))) {");
			code.println("eventResult = EVENT_ABORTED;");
			code.println("break EXECUTION;");
			code.println("}");
		} else if (action instanceof Random) {
			Random randomAction = (Random) action;
			if (randomAction.getList() != null) {
				if (exprContext == null) {
					throw new FlowCompilerException("<random list=\"...\"/> only allowed in expressions", randomAction.sourceLocation().getLineNumber());
				}
				code.println(exprContext + ".append(randstr(" + randomAction.getList()  + "));");
			} else {
				int tot = 0;
				for (Object child : randomAction.getAny()) {
					int inc = 1;
					if (child instanceof JAXBElement)
						child = ((JAXBElement)child).getValue();
					if (child instanceof Block && ((Block)child).getWeight() != null)
						inc = ((Block)child).getWeight();
					tot += inc;
				}
				int n = 0;
				int lastn = 0;
				String chosenVar = varname("chosen");
				String matchingVar = varname("matching");
				code.println("boolean " + chosenVar + " = false;");
				code.println("boolean " + matchingVar + " = true;");
				code.println("while (!"+chosenVar+" && "+matchingVar+") {");
				String randVar = varname("rand");
				String model = "iristk.util.RandomList.RandomModel." + randomAction.getModel().toUpperCase();
				code.println("int " + randVar + " = random(" + randomAction.hashCode() + ", " + tot + ", " + model + ");");
				code.println(matchingVar + " = false;");
				for (Object child : randomAction.getAny()) {
					if (n > 0) 
						code.println("}");
					int inc = 1;
					String cond = "true";
					List<Object> actions = new ArrayList<>();
					if (child instanceof JAXBElement)
						child = ((JAXBElement)child).getValue();
					if (child instanceof Block) {
						Block block = (Block) child;
						if (block.getWeight() != null)
							inc = block.getWeight();
						if (block.getCond() != null) 
							cond = formatCondExpr(block.getCond());
						actions.addAll(block.getContent());
					} else {
						actions.add(child);
					}
					n += inc;
					code.println("if (" + cond + ") {");
					code.println(matchingVar + " = true;");
					code.println("if (" + randVar + " >= " + lastn + " && " + randVar + " < " + n + ") {");
					code.println(chosenVar + " = true;");
					printActions(actions, randomAction, exprContext);
					code.println("}");
					lastn = n;
				}
				code.println("}");
				code.println("}");
			}
		} else if (action instanceof Select) {
			Select select = (Select) action;
			String label = varname("SELECTION");
			code.println(label + ":");
			code.println("{");
			for (Object child : select.getAny()) {
				String cond = "";
				List<Object> actions = new ArrayList<>();
				if (child instanceof JAXBElement)
					child = ((JAXBElement)child).getValue();
				if (child instanceof Block) {
					Block block = (Block) child;
					if (block.getCond() != null) 
						cond = "if (" + formatCondExpr(block.getCond()) + ") ";
					actions.addAll(block.getContent());
				} else {
					actions.add(child);
				}
				code.println(cond + "{");
				printActions(actions, select, exprContext);
				code.println("break " + label + ";");
				code.println("}");
				if (cond.equals(""))
					break;
			}
			code.println("}");
		} else if (action instanceof iristk.xml.flow.Raise) {
			iristk.xml.flow.Raise raiseAction = (iristk.xml.flow.Raise) action;
			if (exprContext != null)
				throw new FlowCompilerException("<raise> not allowed in expression", raiseAction.sourceLocation().getLineNumber());

			String raiseEvent = varname("raiseEvent");
			printInitEvent(raiseEvent, raiseAction.getCopy(), raiseAction.getEvent(), getParameters(raiseAction.getOtherAttributes(), raiseAction.getContent(), raiseAction));
			if (raiseAction.getDelay() != null) {
				String delayedEvent = "flowThread.raiseEvent(" + raiseEvent + ", " + formatAttrExpr(raiseAction.getDelay()) + ", new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + "))";
				if (raiseAction.isForgetOnExit()) {
					code.println("forgetOnExit(" + delayedEvent + ");");
				} else {
					code.println(delayedEvent + ";");
				}
			} else {
				code.println("if (flowThread.raiseEvent(" + raiseEvent + ", new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + ")) == State.EVENT_ABORTED) {");
				code.println("eventResult = EVENT_ABORTED;");
				code.println("break EXECUTION;");
				code.println("}");
			}
		} else if (action instanceof iristk.xml.flow.Send) {
			iristk.xml.flow.Send sendAction = (iristk.xml.flow.Send) action;
			if (exprContext != null)
				throw new FlowCompilerException("<send> not allowed in expression", sendAction.sourceLocation().getLineNumber());
			String sendEvent = varname("sendEvent");
			printInitEvent(sendEvent, sendAction.getCopy(), sendAction.getEvent(), getParameters(sendAction.getOtherAttributes(), sendAction.getContent(), sendAction));
			if (sendAction.getDelay() != null) 
				code.println("flowRunner.sendEvent(" + sendEvent + ", " + formatAttrExpr(sendAction.getDelay()) + ", new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + "));");
			else
				code.println("flowRunner.sendEvent(" + sendEvent + ", new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + "));");
			if (sendAction.getBindId() != null) {
				code.println(sendAction.getBindId() + " = " + sendEvent + ".getId();");
			}
		} else if (action instanceof iristk.xml.flow.If) {
			iristk.xml.flow.If ifAction = (iristk.xml.flow.If) action;
			code.println("if (" + formatCondExpr(ifAction.getCond()) + ") {");
			printActions(ifAction.getContent(), ifAction, exprContext);
			code.println("}");
		} else if (action instanceof iristk.xml.flow.Else) {
			code.println("} else {");
		} else if (action instanceof iristk.xml.flow.Elseif) {
			iristk.xml.flow.Elseif eifAction = (iristk.xml.flow.Elseif) action;
			code.println("} else if (" + formatCondExpr(eifAction.getCond()) + ") {");
		} else if (action instanceof iristk.xml.flow.Propagate) {
			iristk.xml.flow.Propagate propagateAction = (iristk.xml.flow.Propagate) action;
			if (exprContext != null)
				throw new FlowCompilerException("<propagate> not allowed in expression", propagateAction.sourceLocation().getLineNumber());

			code.println("eventResult = EVENT_IGNORED;");
			code.println("break EXECUTION;");
		} else if (action instanceof Repeat) {
			Repeat repeat = (Repeat) action;
			String handler = repeat.getHandler();
			if (handler == null) {
				handler = varname("handler");
			}
			code.println("{");
			if (repeat.getTimes() != null) {
				code.println("RepeatHandler " + handler + " = new RepeatHandler(" + formatAttrExpr(repeat.getTimes()) + ");");
				code.println("while (" + handler + ".getPosition() < " + handler + ".getLength()) {");
			} else if (repeat.getWhile() != null) {
				code.println("RepeatHandler " + handler + " = new RepeatHandler();");
				code.println("while (" + formatAttrExpr(repeat.getWhile()) + ") {");
			} else if (repeat.getList() != null) {
				code.println("RepeatHandler " + handler + " = new RepeatHandler(" + formatAttrExpr(repeat.getList()) + ");");
				code.println("while (" + handler + ".getPosition() < " + handler + ".getLength()) {");
			}
			printActions(repeat.getContent(), repeat, exprContext);
			code.println(handler + ".next();");
			code.println("}");
			code.println("}");
		} else if (action instanceof Var) {
			Var varAction = (Var) action;
			if (exprContext != null)
				throw new FlowCompilerException("<var> not allowed in expression", varAction.sourceLocation().getLineNumber());
			code.println(variable((Var) action));
		} else if (action instanceof iristk.xml.flow.Exec) {
			code.println(formatExec(((iristk.xml.flow.Exec)action).getValue().trim()));
		} else if (action instanceof iristk.xml.flow.Log) {
			code.println("log(" + createExpression(((iristk.xml.flow.Log)action).getContent(), action) + ");");
		} else if (action instanceof iristk.xml.flow.Expr) {
			if (exprContext == null) {
				throw new FlowCompilerException("<expr> not allowed", currentLineNumber);
			} else {
				code.println(exprContext + ".append(" + formatExpr(((Expr)action).getValue()) + ");");
			}
		} else if (action instanceof Element) {//perhaps implement variable name here
			Element elem = (Element)action;
			if (exprContext == null) {
				if (elem.getNamespaceURI().equals("iristk.flow") || elem.getPrefix() == null) {
					throw new FlowCompilerException("Bad element: <" + elem.getLocalName() + ">", currentLineNumber);
				}
				String stateVar = varname("state");
				code.println(stateClass(elem) + " " + stateVar + " = " + newState(elem) + ";");
				printSetStateParameters(stateVar, getParameters(elem));

				code.println("if (!flowThread.callState(" + stateVar + ", new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(parent)) + "))) {");
				code.println("eventResult = EVENT_ABORTED;");
				code.println("break EXECUTION;");
				code.println("}");
			} else {
				try {
					Object o = flowXml.unmarshal(elem);
					printAction(o, parent, exprContext);
				} catch (FlowCompilerException e) {
					code.println(exprContext + ".append(" + createExpression(elem) + ");");
				}
			}
		} else if (action instanceof Text) {
			printAction(((Text)action).getTextContent(), parent, exprContext);
		} else if (action instanceof String) {
			String str = action.toString().trim();
			if (str.length() > 0) {
				if (exprContext == null) {
					throw new FlowCompilerException("Text node not allowed: " + str, currentLineNumber);
				} else {
					code.println(exprContext + ".append(\"" + str.replaceAll("\\n", " ") + "\");");
				}
			}
		} else {
			throw new FlowCompilerException("Could not parse " + action, currentLineNumber);
		}
	}

	private String createExpression(List<Object> content, Object parent) throws FlowCompilerException {
		String varName = varname("string");
		code.println("StringCreator " + varName + " = new StringCreator();");
		printActions(content, parent, varName);
		return varName + ".toString()";
	}

	private String createExpression(Element en) throws FlowCompilerException {
		String estring = "";
		estring += "<" + en.getLocalName();
		for (int j = 0; j < en.getAttributes().getLength(); j++) {
			Attr attr = (Attr) en.getAttributes().item(j);
			if (!(attr.getNamespaceURI() != null && attr.getNamespaceURI().equals("http://www.w3.org/2000/xmlns/")) && !attr.getLocalName().equals("xmlns") && !attr.getLocalName().equals("xsi")) {
				estring += " " + attr.getLocalName() + "=\\\"" + attr.getValue() + "\\\"";
			}
		}
		if (en.getChildNodes().getLength() == 0) {
			estring += "/>";
			return  "\"" + estring + "\"";
		} else {
			String varName = varname("string");
			code.println("StringCreator " + varName + " = new StringCreator();");
			List<Object> children = new ArrayList<>();
			estring += ">";
			children.add(estring);
			for (int i = 0; i < en.getChildNodes().getLength(); i++) {
				children.add(en.getChildNodes().item(i)); 
			}
			children.add("</" + en.getLocalName() + ">");
			printActions(children, en, varName);
			return varName + ".toString()";
		}
	}

	private String location(XMLLocation location) {
		if (location == null)
			return "null";
		else
			return "new XMLLocation(new File(\"" + StringEscapeUtils.escapeJava(location.getFile().getAbsolutePath()) + "\"), " + location.getLineNumber() + ", " + location.getColumnNumber() + ")";
	}

	private String newState(String state) throws FlowCompilerException {
		if (state.contains("#")) {
			String flowName = state.substring(0, state.indexOf("#"));
			String stateName = state.substring(state.indexOf("#") + 1);
			if (flowName.equals("this")) {
				return "new " + stateName + "()";
			}
			for (Var var : flowXml.getVariables()) {
				if (var.getName().equals(flowName))
					return flowName + ".new " + stateName + "()";
			}
			for (Param param : flowXml.getParameters()) {
				if (param.getName().equals(flowName))
					return flowName + ".new " + stateName + "()";
			}
			return "new " + flowName + "." + stateName + "()";
		} else {
			return "new " + state + "()";
		}
	}

	private String newState(Element elem) {
		if (elem.getPrefix().equals("this")) {
			return "new " + elem.getLocalName() + "()";
		}
		for (Var var : flowXml.getVariables()) {
			if (var.getName().equals(elem.getPrefix()))
				return elem.getPrefix() + ".new " + elem.getLocalName() + "()";
		}
		for (Param param : flowXml.getParameters()) {
			if (param.getName().equals(elem.getPrefix()))
				return elem.getPrefix() + ".new " + elem.getLocalName() + "()";
		}
		return "new " + elem.getNamespaceURI() + "." + elem.getLocalName() + "()";
	}

	private String stateClass(String state) throws FlowCompilerException {
		if (state.contains("#")) {
			String flowName = state.substring(0, state.indexOf("#"));
			String stateName = state.substring(state.indexOf("#") + 1);
			for (Var var : flowXml.getVariables()) {
				if (var.getName().equals(flowName))
					return var.getType() + "." + stateName;
			}
			for (Param param : flowXml.getParameters()) {
				if (param.getName().equals(flowName))
					return param.getType() + "." + stateName;
			}
			throw new FlowCompilerException("Cannot resolve " + state);
		} else {
			return state;
		}
	}

	private String stateClass(Element elem) {
		return elem.getNamespaceURI() + "." + elem.getLocalName();
	}

	private Map<String,String> getParameters(Element elem) throws FlowCompilerException {
		NamedNodeMap attributes = elem.getAttributes();
		NodeList childNodes = elem.getChildNodes();
		Map<QName, String> otherAttributes = new HashMap<>();
		for (int i = 0; i < attributes.getLength(); i++) {
			if (attributes.item(i).getNamespaceURI() == null || !attributes.item(i).getNamespaceURI().equals("http://www.w3.org/2000/xmlns/")) {
				otherAttributes.put(new QName("iristk.flow.param", attributes.item(i).getLocalName()), attributes.item(i).getNodeValue());
			}
		}
		ArrayList<Object> content = new ArrayList<>();
		for (int i = 0; i < childNodes.getLength(); i++) {
			content.add(childNodes.item(i));
		}
		return getParameters(otherAttributes, content, elem);
	}

	private Map<String,String> getParameters(Map<QName, String> attributes, List<Object> content, Object parent) throws FlowCompilerException {
		Map<String,String> result = new HashMap<String,String>();
		for (QName attr : attributes.keySet()) {
			if (attr.getNamespaceURI() != null && attr.getNamespaceURI().equals("http://www.w3.org/2000/xmlns/")) continue;
			String name = attr.getLocalPart();
			String value = formatAttrExpr(attributes.get(attr));
			result.put(name, value);
		}
		if (content.size() > 0) {
			EnumMap<String,String> paramList = new EnumMap<>();
			for (Object child : content) {
				if (child instanceof Element) {
					Element elem = (Element) child;
					if (elem.getNamespaceURI().equals("iristk.flow.param")) {
						String key = elem.getLocalName();
						List<Object> paramChildren = new ArrayList<Object>();
						for (int j = 0; j < elem.getChildNodes().getLength(); j++) {
							paramChildren.add(elem.getChildNodes().item(j));
						}
						String text = createExpression(paramChildren, elem);
						paramList.add(key, text);
					}
				}
			}
			if (paramList.size() == 0) {
				paramList.add("text", createExpression(content, parent));
			}
			for (String key : paramList.keySet()) {
				if (paramList.get(key).size() > 1) {
					result.put(key, "java.util.Arrays.asList(" + listToString(paramList.get(key)) + ")");
				} else {
					result.put(key, paramList.get(key).get(0));
				}
			}
		}
		return result;
	}

	private void printSetStateParameters(String stateName, Map<String,String> paramMap) throws FlowCompilerException {
		for (String name : paramMap.keySet()) {
			code.println(stateName + ".set" + ucFirst(name) + "(" + paramMap.get(name) + ");");
		}
	}

	private void printInitEvent(String varName, String copy, String eventName, Map<String,String> paramMap) throws FlowCompilerException {
		//if (copy != null) {
		//	code.println("Event copy = " + formatAttrExpr(copy) + ";");
		//}
		if (eventName != null)
			code.println("Event " + varName + " = new Event(\"" + eventName + "\");");
		else if (copy != null)
			code.println("Event " + varName + " = new Event(" + formatAttrExpr(copy) + ".getName());");
		else throw new FlowCompilerException("Must have either copy or event parameter set when sending event");
		if (copy != null)
			code.println(varName + ".copyParams(" + formatAttrExpr(copy) + ");");
		for (String name : paramMap.keySet()) {
			code.println(varName + ".putIfNotNull(\"" + name + "\", " + paramMap.get(name) + ");");
		}
	}

	private void printActions(List<Object> list, Object parent, String exprContext) throws FlowCompilerException {
		for (Object action : list) {
			printAction(action, parent, exprContext);
		}
	}

	private static String replaceIgnoreStrings(String expr, String pattern, final String repl) {
		return new Replacer(pattern, stringPattern) {	
			@Override
			public String replace(Matcher matcher) {
				return repl;
			}
		}.replaceAll(expr);
	}

	private String formatCondExpr(String cond) throws FlowCompilerException {
		if (cond == null)
			return null;
		cond = formatAttrExpr(cond);
		cond = replaceIgnoreStrings(cond, " and ", " && ");
		cond = replaceIgnoreStrings(cond, " or ", " || ");
		return cond;
		//return "makeBool(" + cond + ")";
	}

	private String formatAttrExpr(String expr) throws FlowCompilerException {
		if (expr == null)
			return null;
		// ' => " (if not preceded by \)
		expr = expr.replaceAll("(?<!\\\\)'", "\"");
		// \' => ' 
		expr = expr.replaceAll("\\\\'", "'");
		// \ => \\ (if not followed by ") 
		expr = expr.replaceAll("\\\\(?!\")", "\\\\\\\\");
		expr = formatExpr(expr);
		return expr;
	}

	private String formatExec(String exec) throws FlowCompilerException {
		exec = formatExpr(exec);
		if (!exec.endsWith(";"))
			return exec + ";";
		else
			return exec;
	}

	public static String formatRecordExpr(String expr) throws FlowCompilerException {
		try {
			// return new Replacer("([A-Za-z0-9_]+)(\\?)?:([A-Za-z0-9_\\:\\.\\(\\)\\*]*)( *=(?!=)[^;]*)?", stringPattern) {
			return new Replacer("(\\?)?:([A-Za-z0-9_\\:\\.\\(\\)\\*]*)( *=(?!=)[^;]*)?", stringPattern) {
				@Override
				public String replace(Matcher matcher) {
					//String recordVar = matcher.group(1);
					String hasSign = matcher.group(1);
					String getStr = matcher.group(2);
					String assignStr = matcher.group(3);
					String[] split = Replacer.paraSplit(getStr);
					getStr = split[0];
					String rest = split[1];
					boolean dynamic = false;
					if (getStr.endsWith("(")) {
						dynamic = true;
						getStr = getStr.substring(0, getStr.length() - 1);
					}
					int para = 0;
					String getExpr = "";
					String getPart = "";
					getStr += ":";
					for (int i = 0; i < getStr.length(); i++) {
						String c = getStr.substring(i, i + 1); 
						if (c.equals("(")) {
							getPart += c;
							para++;
						} else if (c.equals(")")) {
							getPart += c;
							para--;
						} else if (c.equals(":") && para == 0) {
							if (getPart.startsWith("("))
								getPart = getPart.substring(1, getPart.length() - 1);
							else 
								getPart = "\"" + getPart + "\"";
							if (getExpr.length() == 0)
								getExpr = getPart;
							else
								getExpr += " + \":\" + " + getPart;
							getPart = "";
						} else {
							getPart += c;
						}
					}
					getExpr = "\"\" + " + getExpr;
					getExpr = getExpr.replaceAll("\" \\+ \"", "");
					if (assignStr != null && rest.length() == 0) {
						String put = assignStr.trim().substring(1).trim();
						return ".putIfNotNull(" + getExpr + ", " + put + ")";
					} else if (dynamic) {
						return ".getDynamic(" + getExpr + ", " + rest;
					} else if (hasSign != null) {
						//String sign = boolSign;
						//if (boolSign.equals("?")) sign = "";
						//if (getExpr.contains("*"))
						return ".has(" + getExpr + ")" + rest;
						//else
						//	return sign + "makeBool(" + recordVar + ".get(" + getExpr + "))" + rest;
					} else {
						return ".get(" + getExpr + ")" + rest;
					}
				} 
			}.replaceIter(expr);
		} catch (RuntimeException e) {
			throw new FlowCompilerException(e.getMessage());
		}
	}

	public static String formatExpr(String expr) throws FlowCompilerException {
		expr = formatRecordExpr(expr);
		expr = new Replacer(" *:=([^;]*)", stringPattern) {
			@Override
			public String replace(Matcher matcher) {
				return ".putAll(" + matcher.group(1).trim() + ")";
			} 
		}.replaceIter(expr);
		expr = formatEqExpr(expr);
		return expr;
	}

	private static int findExpr(String expr, int pos, int dir) {
		int para = 0;
		boolean inQuote = false;
		boolean hasChar = false;
		int i = pos;
		for (; i > 0 && i < expr.length(); i += dir) {
			String c = expr.substring(i, i+1);
			if (c.equals("(")) {
				para += dir;
			} else if (c.equals(")")) {
				para -= dir;
			} else if (c.equals("\"")) {
				inQuote = !inQuote;
			} else if (c.equals(" ") && para <= 0 && !inQuote && hasChar) {
				return i;
			} 
			if (para < 0) {
				return i - dir;
			}
			if (!c.equals(" ")) hasChar = true;
		}
		return i;
	}

	public static String formatEqExpr(String expr) {
		return expr;
	}

	public static void compile(File flowFile, boolean binary) throws FlowCompilerException {
		System.out.println("Compiling flow: " + flowFile.getAbsolutePath());
		FlowCompiler fcompiler = new FlowCompiler(flowFile);
		File srcFile = new File(flowFile.getAbsolutePath().replaceFirst("\\.[A-Za-z]+$", "\\.java"));
		fcompiler.compileToFile(srcFile);
		System.out.println("Compiled to source code: " + srcFile.getAbsolutePath());
		if (fcompiler.flowXml.hasPublicStates()) {
			FlowSchemaCompiler.compile(flowFile.getAbsolutePath(), System.getProperty("user.dir"));
		}
		if (binary) {
			compileJavaFlow(srcFile);
			System.out.println("Compiled to byte code");
			/*
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

			if (compiler == null) {
				throw new RuntimeException("Could not find Java Compiler");
			}
			String binDir = flowFile.getAbsolutePath().replaceFirst("([\\\\/])src[\\\\/].*", "$1") + "bin";
			if (new File(binDir).exists()) {
				if (compiler.run(null, null, null, srcFile.getPath(), "-d", binDir) == 0) {
					System.out.println("Compiled to byte code folder: " + binDir);
				}
			} else {
				throw new RuntimeException("Directory " + binDir + " does not exist");
			}
			 */
		}
	}

	public static void compile(String flowFileName, String dir, boolean binary) throws FlowCompilerException {
		File fdir = new File(dir);
		File flowFile = null;
		if (new File(flowFileName).exists()) {
			flowFile = new File(flowFileName);
		} else if (new File(fdir, flowFileName).exists()) {
			flowFile = new File(fdir, flowFileName);
		} else {
			String f = FileFinder.findFirst(dir + "/src", flowFileName);
			if (f != null) {
				flowFile = new File(f);
			}
		}
		if (flowFile != null) {
			compile(flowFile, binary);
		}
	}

	public static void compileJavaFlow(File srcFile) throws FlowCompilerException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (ToolProvider.getSystemJavaCompiler() == null) {
			throw new FlowCompilerException("Could not find Java Compiler");
		}
		if (!srcFile.exists()) {
			throw new FlowCompilerException(srcFile.getAbsolutePath() + " does not exist");
		}
		File outputDir = new File(srcFile.getAbsolutePath().replaceFirst("([\\\\/])src[\\\\/].*", "$1") + "bin");
		if (!outputDir.exists()) {
			throw new FlowCompilerException("Directory " + outputDir.getAbsolutePath() + " does not exist");
		}	
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.US, StandardCharsets.UTF_8);
		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(Arrays.asList(srcFile.getAbsolutePath()));
		Iterable<String> args = Arrays.asList("-d", outputDir.getAbsolutePath());
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, args, null, compilationUnits);
		boolean success = task.call();
		try {
			fileManager.close();
		} catch (IOException e) {
		}
		for (Diagnostic<? extends JavaFileObject> diag : diagnostics.getDiagnostics()) {
			if (diag.getKind() == Kind.ERROR) {
				int javaLine = (int) diag.getLineNumber();
				int flowLine = mapLine(javaLine, srcFile);
				String message = diag.getMessage(Locale.US);
				message = message.replace("line " + javaLine, "line " + flowLine);
				throw new FlowCompilerException(message, flowLine);
			}
		}
		if (!success) {
			throw new FlowCompilerException("Compilation failed for unknown reason");
		}
	}

	private static int mapLine(int line, File file) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String l;
			int lnn = 0;
			int lcount = 0;
			while ((l = br.readLine()) != null) {
				String ln = RegExp.getGroup(l, "// Line: (\\d+)", 1);
				if (ln != null) {
					lnn = Integer.parseInt(ln);
				}
				if (lcount >= line)
					return lnn;
				lcount++;
			}

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
				}
		}
		return 0;
	}

	public static void main(String[] args)  {
		try {
			boolean compileToBinary = false;
			//boolean compileToXSD = false;
			String file = null;
			for (String arg : args) {
				if (arg.equals("-b")) 
					compileToBinary = true;
				//else if (arg.equals("-x")) 
				//	compileToXSD = true;
				else
					file = arg;
			}
			if (file != null) {
				compile(file, System.getProperty("user.dir"), compileToBinary);

				//if (compileToXSD)
				//	FlowSchemaCompiler.compile(file, System.getProperty("user.dir"));
			} else {
				System.out.println("Compiles flow XML to Java source.\n");
				System.out.println("Usage:");
				System.out.println("iristk cflow [OPTIONS] XML\n");
				System.out.println("Options:");
				System.out.println("-b  Also compile Java source to binary");
				//System.out.println("-x  Also compile templates to XSD Schema");
			}
		} catch (FlowCompilerException e) {
			System.err.println("Error on line " + e.getLineNumber() + ": " + e.getMessage());
		}
	}

	public void useUniqueNames(boolean b) {
		this.useUniqueNames   = b;
	}

	public String getFlowName() {
		return flowXml.getPackage() + "." + getLocalFlowName();
	}

	public String getLocalFlowName() {
		if (useUniqueNames)
			return flowXml.getName() + uniqueNameSuffix;
		else
			return flowXml.getName();
	}

}
