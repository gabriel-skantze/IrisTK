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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Unmarshaller.Listener;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XmlMarshaller<T> extends XmlUtils {

	private JAXBContext jc;
	public Unmarshaller unmarshaller;
	public Marshaller marshaller;
	private Map<Object, XMLLocation> locations = new HashMap<>();
	private XMLStreamReader xsr = null;
	private File file;	
	
	public XmlMarshaller(String namespace) {
		super();
		try {
			jc = JAXBContext.newInstance(namespace);
			unmarshaller = jc.createUnmarshaller();
			marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		} catch (JAXBException e) {
			e.printStackTrace();
		} 
	}
	
	public XmlMarshaller(Class clazz) {
		this(clazz.getPackage().getName());
	}

	public void storeLocations() {
		unmarshaller.setListener(new LocationListener());
	}
	
	private class LocationListener extends Listener {

        @Override
        public void beforeUnmarshal(Object target, Object parent) {
        	
        	if (xsr != null) {
        		Location location = xsr.getLocation();
        		XMLLocation loc = new XMLLocation(file, location.getLineNumber(), location.getColumnNumber());
        		locations.put(target, loc);
        	}
        }
        
	}

    public XMLLocation getLocation(Object o) {
        return locations.get(o);
    }
	
	public T unmarshal(String xml) throws JAXBException {
		synchronized (unmarshaller) {
			try {
				return (T) unmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes("utf-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
	public T unmarshal(byte[] bytes) throws JAXBException {
		synchronized (unmarshaller) {
			return (T) unmarshaller.unmarshal(new ByteArrayInputStream(bytes));
		}
	}
	
	private class MyXmlReader extends StreamReaderDelegate {
		
		public MyXmlReader(XMLStreamReader reader) {
			super(reader);
		}
		
	}
	
	public T unmarshal(URL url) throws JAXBException, FileNotFoundException {
		synchronized (unmarshaller) {
			return (T) unmarshaller.unmarshal(url);
		}
	}
	
	public T unmarshal(File file) throws JAXBException, FileNotFoundException, XMLStreamException {
		synchronized (unmarshaller) {
			this.file = file;
			XMLInputFactory xif = XMLInputFactory.newFactory();
	        FileInputStream xml = new FileInputStream(file);
	        xsr = new MyXmlReader(xif.createXMLStreamReader(xml));
			T result = (T) unmarshaller.unmarshal(xsr);
			xsr = null;
			return result;
		}
	}
	
	public T unmarshal(Node node) throws JAXBException {
		synchronized (unmarshaller) {
			return (T) unmarshaller.unmarshal(node);
		}
	}
	
	public Document marshalToDOM(T data) throws JAXBException {
		synchronized (marshaller) {
			Document doc = getBuilder().newDocument();
			marshaller.marshal(data, doc);
			return doc;
		}
	}
	
	public void marshal(T data, Node node) throws JAXBException {
		synchronized (marshaller) {
			marshaller.marshal(data, node);
		}
	}
	
	public void marshal(T data, File file) throws JAXBException, IOException {
		FileOutputStream fout = new FileOutputStream(file);
		synchronized (marshaller) {
			marshaller.marshal(data, fout);
		}
		fout.close();
	}
	
	public String marshal(T data) throws JAXBException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		synchronized (marshaller) {
			marshaller.marshal(data, baos);
		}
		try {
			String result = baos.toString("UTF-8");
			result = result.replaceAll("\\s+", " ");
			result = result.replaceAll("> <", "><");
			return result;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		// Remove the XML declaration
		//if (!xmlDecl) {
		//	result = result.replaceFirst("<.*?>", "");
		//}
	}
	

	public void marshal(T data, OutputStream out) throws JAXBException {
		marshaller.marshal(data, out);
	}
	
	public List<Object> unmarshal(NodeList childNodes) throws JAXBException {
		List<Object> result = new ArrayList<Object>();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node child = childNodes.item(i);
			if (child instanceof Text) {
				result.add(((Text)child).getNodeValue());
			} else if (!child.getNamespaceURI().equals("iristk.flow")) {
				result.add(child);
			} else {
				result.add(unmarshal(childNodes.item(i)));
			}
		}
		return result;
	}

	
	public static class XMLLocation {

		private File file;
		private int lineNumber;
		private int columnNumber;

		public XMLLocation(File file, int lineNumber, int columnNumber) {
			this.setFile(file);
			this.setLineNumber(lineNumber);
			this.setColumnNumber(columnNumber);
		}

		public File getFile() {
			return file;
		}

		public void setFile(File file) {
			this.file = file;
		}

		public int getLineNumber() {
			return lineNumber;
		}

		public void setLineNumber(int lineNumber) {
			this.lineNumber = lineNumber;
		}

		public int getColumnNumber() {
			return columnNumber;
		}

		public void setColumnNumber(int columnNumber) {
			this.columnNumber = columnNumber;
		}
		

	}


}
