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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import iristk.flow.FlowRunner.FlowThread;
import iristk.system.Event;
import iristk.system.IrisModule;
import iristk.system.IrisUtils;

public class FlowModule extends IrisModule implements FlowListener {
	
	private static Logger logger = IrisUtils.getLogger(FlowModule.class);

	private FlowRunner flowRunner;
	private String flowModuleName;
	private FlowThread mainFlowThread;
	private boolean started = false;
	
	public FlowModule(String name, Flow flow) {
		this(name, flow.getInitialState());
	}
	
	public FlowModule(String name, State state) {
		flowModuleName = name;
		flowRunner = new FlowRunner();
		flowRunner.addFlowListener(this);
		mainFlowThread = flowRunner.addFlowThread(state);
	}
	
	public FlowModule(Flow flow) {
		this(flow.getClass().getSimpleName(), flow);
	}
	
	//public void setLogStream(OutputStream out) {
	//	flowRunner.logStream = new PrintStream(out);
	//}
	
	@Override
	public void init() {
	}

	@Override
	protected void systemStarted() {
		if (!started) {
			started  = true;
			mainFlowThread.start();
		}
	}

	@Override
	public void onEvent(Event event) {
		flowRunner.addEvent(event);
	}
	
	public void addFlowListener(FlowListener listener) {
		flowRunner.addFlowListener(listener);
	}
	
	public void gotoState(Flow flow) {
		mainFlowThread.gotoState(flow);
	}
	
	public void gotoState(State state) {
		mainFlowThread.gotoState(state);
	}
	
	private void monitorState(State state) {
		List<String> substates = new ArrayList<String>();
		List<String> states = new ArrayList<String>();
		for (FlowThread fthread : flowRunner.flowThreads) {
			if (fthread.willExit) 
				continue;
			State s;
			if (state != null && state.flowThread == fthread) {
				s = state;
			} else {
				s = fthread.currentState;
			}
			//String name = "";
			while (s != null) {
				//name = name + (name.length() == 0 ? "" : ":") + s.getClass().getSimpleName();
				substates.add(0, s.getClass().getSimpleName());
				s = s.caller;
			}
			states.addAll(substates);
			substates.clear();
			//states.add(name);
		}
		super.monitorState(states.toArray(new String[0]));
		/*
		int depth = 0;
		State s = state;
		while (s != null) {
			depth++;
			s = s.caller;
		}
		String[] states = new String[depth];
		s = state;
		int i = depth - 1;
		while (s != null) {
			states[i] = s.getClass().getSimpleName();
			i--;
			s = s.caller;
		}
		super.monitorState(states);
		*/
	}

	@Override
	public void stop() {
		flowRunner.stop();
		super.stop();
	}
	
	@Override
	public String getDefaultName() {
		return flowModuleName;
	}

	@Override
	public void onFlowEvent(Event event, FlowEventInfo info) {
	}

	@Override
	public void onSendEvent(Event event, FlowEventInfo info) {
		send(event);
	}

	@Override
	public void onGotoState(State toState, FlowEventInfo info) {
		monitorState(toState);
	}

	@Override
	public void onCallState(State toState, FlowEventInfo info) {
		monitorState(toState);
	}

	@Override
	public void onReturnState(State fromState, State toState, FlowEventInfo info) {
		monitorState(toState);
	}

	@Override
	public void onFlowException(FlowException e) {
		logger.error(e.getMessage(), e.getCause());
	}

}
