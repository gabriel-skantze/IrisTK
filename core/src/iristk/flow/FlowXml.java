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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import iristk.xml.XmlMarshaller;
import iristk.xml.XmlMarshaller.XMLLocation;
import iristk.xml.XmlUtils;
import iristk.xml.flow.Flow;
import iristk.xml.flow.Include;
import iristk.xml.flow.Param;
import iristk.xml.flow.State;
import iristk.xml.flow.Flow.Import;
import iristk.xml.flow.Var;

import javax.xml.bind.JAXBException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class FlowXml {

	private Flow flow;
	private List<String> importedClasses;
	private List<State> states;
	private List<FlowXml> subFlows;
	//private List<Action> actions;
	private List<Var> vars;
	private XmlMarshaller<Object> flowMarshaller = new XmlMarshaller<Object>("iristk.xml.flow");
	//private List<Object> xmlTemplates;
	private List<Param> params;
	
	public FlowXml() {
		flowMarshaller.storeLocations();
	}
	
	public XMLLocation getLocation(Object object) {
		return flowMarshaller.getLocation(object);
	}
	
	public void read(File xmlfile) throws FlowCompilerException {
		try {
			XmlUtils.validate(xmlfile);
			read((Flow) flowMarshaller.unmarshal(xmlfile));
		} catch (JAXBException e) {
			if (e.getMessage() != null) {
				throw new FlowCompilerException(e.getMessage());
			} else if (e.getLinkedException() != null) {
				if (e.getLinkedException() instanceof SAXParseException) {
					SAXParseException spe = (SAXParseException)e.getLinkedException();
					throw new FlowCompilerException(spe.getMessage(), spe.getLineNumber());
				} else if (e.getLinkedException().getMessage() != null) {
					throw new FlowCompilerException(e.getLinkedException().getMessage());
				} else {
					throw new FlowCompilerException("Unknown XML parsing exception");
				}
			} else {
				throw new FlowCompilerException("Unknown XML parsing exception");
			}
		} catch (FileNotFoundException e) {
			throw new FlowCompilerException("Could not find file " + xmlfile.getAbsolutePath());
		} catch (SAXParseException e) {
			throw new FlowCompilerException(e.getMessage(), e.getLineNumber());
		} catch (SAXException e) {
			throw new FlowCompilerException(e.getMessage());
		} catch (IOException e) {
			throw new FlowCompilerException(e.getMessage());
		}
	}
	
	public void read(Flow xmlflow) throws FlowCompilerException {
		flow = xmlflow;
		importedClasses = new ArrayList<String>();
		states = new ArrayList<State>();
		subFlows = new ArrayList<FlowXml>();
		
		addStatesAndFlows(flow.getStateOrFlow());
		
		vars = new ArrayList<Var>();
		vars.addAll(flow.getVar());
		
		params = new ArrayList<Param>();
		params.addAll(flow.getParam());
		
		for (Import imp : flow.getImport()) {
			importedClasses.add(imp.getClazz());
		}
		
		for (Include include : flow.getInclude()) {
			include(null, include.getHref());
		}
	}
	
	private void addStatesAndFlows(List<Object> list) throws FlowCompilerException {
		for (Object stateOrFlow : list) {
			if (stateOrFlow instanceof State) {
				states.add((State)stateOrFlow);
			} else {
				FlowXml subFlow = new FlowXml();
				subFlow.read((Flow)stateOrFlow);
				subFlows.add(subFlow);
			}
		}
	}
	
	private void include(File context, String href) throws FlowCompilerException {
		File file;
		if (context == null)
			file = new File(href);
		else
			file = new File(context.getParent(), href);
		Flow flow;
		try {
			flow = (Flow) flowMarshaller.unmarshal(file);
		} catch (JAXBException e) {
			throw new FlowCompilerException(e.getMessage());
		} catch (FileNotFoundException e) {
			throw new FlowCompilerException(e.getMessage());
		}
		for (Include inc : flow.getInclude()) {
			include(file, inc.getHref());
		}
		addStatesAndFlows(flow.getStateOrFlow());
		vars.addAll(flow.getVar());
		for (Import imp : flow.getImport()) {
			if (!importedClasses.contains(imp.getClazz())) 
				importedClasses.add(imp.getClazz());
		}
	}

	public String getPackage() {
		return flow.getPackage();
	}

	public List<String> getImportedClasses() {
		return importedClasses;
	}

	public String getExtends() {
		return flow.getExtends();
	}

	public List<Var> getVariables() {
		return vars;
	}
	
	public List<Param> getParameters() {
		return params;
	}

	public String getInitial() {
		return flow.getInitial();
	}

	public List<State> getStates() {
		return states;
	}
	
	public List<Object> unmarshal(NodeList childNodes) throws FlowCompilerException {
		try {
			return flowMarshaller.unmarshal(childNodes);
		} catch (JAXBException e) {
			throw new FlowCompilerException(e.getMessage());
		}
	}

	public Object unmarshal(Element node) throws FlowCompilerException {
		try {
			return flowMarshaller.unmarshal(node);
		} catch (JAXBException e) {
			throw new FlowCompilerException(e.getMessage());
		}
	}
	
	public Flow getFlow() {
		return flow;
	}
	
	public String getName() {
		return flow.getName();
	}
	
	public boolean isStatic() {
		return flow.isStatic();
	}
	
	public boolean isPublic() {
		return flow.isPublic();
	}
	
	public State getParentState(State state) {
		if (state.getExtends() != null)
			for (State s : states) {
				if (state.getExtends().equals(s.getId()))
					return s;
			}
		return null;
	}

	public List<Param> getAllStateParams(State state) {
		List<Param> params = new ArrayList<>();
		while (state != null) {
			params.addAll(state.getParam());
			state = getParentState(state);
		}
		return params;
	}

	public boolean hasPublicStates() {
		for (State state : states) {
			if (state.isPublic())
				return true;
		}
		for (FlowXml flow : subFlows) {
			if (flow.isPublic())
				return true;
		}
		return false;
	}

	public List<FlowXml> getSubFlows() {
		return subFlows;
	}
	
}
