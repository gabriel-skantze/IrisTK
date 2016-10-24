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
package iristk.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class XmlUtils {
	
	public static DocumentBuilder getBuilder() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		try {
			return dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void validate(String xml, File path) throws FileNotFoundException, SAXException, IOException {
		Document doc = getBuilder().parse(new ByteArrayInputStream(xml.getBytes("utf-8")));
		Attr attr = doc.getDocumentElement().getAttributeNodeNS("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");
		if (attr != null) {
			String[] alist = attr.getValue().split(" +");
			StreamSource[] xsdfiles = new StreamSource[alist.length / 2];
			for (int i = 1; i < alist.length; i += 2) {
				xsdfiles[i/2] = new StreamSource(new File(path, alist[i]));
			}
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(xsdfiles);
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
		}
	}

	public static void validate(File file) throws FileNotFoundException, SAXException, IOException {
		Document doc = getBuilder().parse(new FileInputStream(file));
		Attr attr = doc.getDocumentElement().getAttributeNodeNS("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");
		if (attr != null) {
			String[] alist = attr.getValue().split(" +");
			StreamSource[] xsdfiles = new StreamSource[alist.length / 2];
			for (int i = 1; i < alist.length; i += 2) {
				xsdfiles[i/2] = new StreamSource(new File(file.getParentFile(), alist[i]));
			}
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(xsdfiles);
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(file));
		}
	}

	public static void validate(String xml, String... xsdFiles) throws SAXException, IOException {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		StreamSource[] xsdfiles = new StreamSource[xsdFiles.length];
		//xsdfiles[0] = new StreamSource();
		int i = 0;
		for (String xsdFile : xsdFiles) {
			xsdfiles[i++] = new StreamSource(new ByteArrayInputStream(xsdFile.getBytes()));
		}
		Schema schema = schemaFactory.newSchema(xsdfiles);
		Validator validator = schema.newValidator();
		validator.validate(new StreamSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
	}

	public static String nodesToString(List<Object> nodes) {
		String result = "";
		for (Object node : nodes) {
			result += nodeToString(node);
		}
		return result;
	}

	public static String nodeToString(Object node) {
		String result = "";
		if (node instanceof String) {
			String str = ((String)node).replaceAll("\\s+", " ").trim();
			result += str;
		} else if (node instanceof Text) {
			String str = ((Text) node).getTextContent().replaceAll("\\s+", " ").trim();
			result += str;
		} else if (node instanceof Element) {
			Element elem = (Element)node;
			Set namespaces = collectNamespaces(elem);
			result += "<" + elem.getTagName();
			for (int j = 0; j < elem.getAttributes().getLength(); j++) {
				Attr attr = (Attr) elem.getAttributes().item(j);
				if (attr.getNamespaceURI() != null && attr.getNamespaceURI().equals("http://www.w3.org/2000/xmlns/") && !namespaces.contains(attr.getValue()))
					continue;
				result += " " + attr.getName() + "=\"" + attr.getValue() + "\"";
			}
			if (elem.getChildNodes().getLength() == 0) {
				result += "/>";
			} else {
				result += ">";
				ArrayList<Object> children = new ArrayList<Object>();
				for (int i = 0; i < elem.getChildNodes().getLength(); i++) {
					children.add(elem.getChildNodes().item(i)); 
				}
				result += nodesToString(children);
				result += "</" + elem.getTagName() + ">";
			}
		}
		return result;
	}
	
	public static Set<String> collectNamespaces(Element elem) {
		Set<String> set = new HashSet<String>();
		collectNamespaces(elem, set);
		return set;
	}
	
	private static void collectNamespaces(Element elem, Set set) {
		if (elem.getNamespaceURI() != null) {
			set.add(elem.getNamespaceURI());
		}
		for (int j = 0; j < elem.getAttributes().getLength(); j++) {
			Attr attr = (Attr) elem.getAttributes().item(j);
			if (attr.getNamespaceURI() != null) {
				set.add(attr.getNamespaceURI());
			}
		}
		for (int i = 0; i < elem.getChildNodes().getLength(); i++) {
			Node child = elem.getChildNodes().item(i);
			if (child instanceof Element) {
				collectNamespaces((Element) child, set);
			}
		}
	}

	public static Document stringToDocument(String string) throws SAXException, IOException {
		return getBuilder().parse(new ByteArrayInputStream(string.getBytes()));
	}

	public static void indentXmlFile(File file) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			Document doc = getBuilder().parse(file);
			OutputFormat format = new OutputFormat(doc);
			//format.setLineWidth(80);
			format.setIndenting(true);
			format.setIndent(4);
			format.setEncoding("UTF-8");
			XMLSerializer serializer = new XMLSerializer(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"), format);
			serializer.serialize(doc);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String indentXml(String xml) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			Document doc =  getBuilder().parse(new InputSource(new StringReader(xml)));
			OutputFormat format = new OutputFormat(doc);
			format.setLineWidth(80);
			format.setIndenting(true);
			format.setIndent(4);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			XMLSerializer serializer = new XMLSerializer(out, format);
			serializer.serialize(doc);
			return out.toString();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return xml;
	}
	
	public static Document parse(File file) throws SAXException, IOException {
		return getBuilder().parse(file);
	}
	
	public static void checkWellformed(String xml) throws SAXException, IOException {
		getBuilder().parse(new InputSource(new StringReader(xml)));
	}

	public static Node removeNamespaces(Node node, Document doc) {
		if (node instanceof Element) {
			Element elem = (Element)node;
			Element nelem = doc.createElement(elem.getLocalName());
			for (int i = 0; i < elem.getChildNodes().getLength(); i++) {
				Node nchild = removeNamespaces(elem.getChildNodes().item(i), doc);
				if (nchild != null)
					nelem.appendChild(nchild);
			}
			for (int i = 0; i < elem.getAttributes().getLength(); i++) {
				if ((elem.getAttributes().item(i).getNamespaceURI() == null || !elem.getAttributes().item(i).getNamespaceURI().equals("http://www.w3.org/2000/xmlns/")) && !elem.getAttributes().item(i).getLocalName().equals("xmlns")) {
					nelem.setAttribute(elem.getAttributes().item(i).getLocalName(), elem.getAttributes().item(i).getNodeValue());
				}
			}
			return nelem;
		} else {
			return doc.importNode(node, true);
		}
	}


}
