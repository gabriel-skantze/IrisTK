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
import iristk.xml.XmlUtils;
import iristk.xml.flow.Param;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

public class FlowSchemaCompiler {

	private FlowXml flowReader = new FlowXml();
	PrintStream xsd;

	public FlowSchemaCompiler(File xmlFile) throws FlowCompilerException {
		flowReader.read(xmlFile);
	}

	public static void compile(String flowFileName, String dir) throws FlowCompilerException {
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
			System.out.println("Compiling flow: " + flowFile.getAbsolutePath());
			FlowSchemaCompiler fcompiler = new FlowSchemaCompiler(flowFile);
			File srcFile = new File(flowFile.getAbsolutePath().replaceFirst("\\.[A-Za-z]+$", "\\.xsd"));
			fcompiler.compileToFile(srcFile);
			System.out.println("Compiled to xsd: " + srcFile.getAbsolutePath());
		}
	}

	public void compileToFile(File xsdFile) throws FlowCompilerException {
		try {
			FileOutputStream fstream = new FileOutputStream(xsdFile);
			compileToStream(fstream);
			fstream.close();
			XmlUtils.indentXmlFile(xsdFile);
		} catch (Exception e) {
			throw new FlowCompilerException(e.getMessage());
		}
	}

	public void compileToStream(OutputStream outStream) {
		xsd = new PrintStream(outStream);
		xsd.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		String targetNamespace = flowReader.getPackage() + "." + flowReader.getName();
		xsd.println("<schema xmlns=\"http://www.w3.org/2001/XMLSchema\" xmlns:flow=\"iristk.flow\" elementFormDefault=\"qualified\" xmlns:tns=\"" + targetNamespace + "\" targetNamespace=\"" + targetNamespace + "\">");
		xsd.println("<import schemaLocation=\"flow.xsd\" namespace=\"iristk.flow\"/>");
		for (iristk.xml.flow.State state : flowReader.getStates()) {
			if (state.isPublic()) {
				printElement(state.getId(), flowReader.getAllStateParams(state));
			}
		}
		for (FlowXml flow : flowReader.getSubFlows()) {
			if (flow.isPublic()) {
				printElement(flow.getName(), flow.getParameters());
			}
		}
		if (flowReader.getFlow().getSchema() != null) {
			for (Object any : flowReader.getFlow().getSchema().getAny()) {
				xsd.println(XmlUtils.nodeToString(any));
			}
		}
		xsd.println("</schema>");
	}
	
	private void printElement(String name, List<Param> params) {
		Param textParam = null;
		for (Param param : params) {
			if (param.getName().equals("text"))
				textParam = param;
		}
		
		if (textParam != null && textParam.getElementType() != null) {
			xsd.println("<element name=\"" + name + "\" type=\"tns:" + textParam.getElementType() + "\"/>");
		} else {
			xsd.println("<element name=\"" + name + "\">");
			if (textParam != null)  {
				xsd.println("<complexType mixed=\"true\">");
				xsd.println("<sequence>");
				xsd.println("<any processContents=\"lax\" namespace=\"##any\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>");
				xsd.println("</sequence>");
			} else {
				xsd.println("<complexType>");
			}
			
			for (Param param : params) {
				String attr = "";
				attr = "<attribute name=\"" + param.getName() + "\"";
				if (param.getDefault() != null) 
					attr += " default=\"" + param.getDefault() + "\"";
				if (param.getAlt() != null && param.getAlt().size() > 0) {
					attr += ">";
					attr += "<simpleType>";
					attr += "<restriction base=\"string\">";
					for (String alt : param.getAlt()) {
						attr += "<enumeration value=\"" + alt + "\" />";
					}
					attr += "</restriction>";
					attr += "</simpleType>";
				} else {
					attr += " type=\"string\">";
				}
				if (param.getHelp() != null)
					attr += "<annotation><documentation>" + param.getHelp() + "</documentation></annotation>";
				attr += "</attribute>";
				xsd.println(attr);
			}
			xsd.println("</complexType>");
			xsd.println("</element>");
		}
	}

}
