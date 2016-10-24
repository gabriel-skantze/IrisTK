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

import iristk.system.Event;
import iristk.util.DelayedEvent;
import iristk.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

public class FlowRunner {
	
	List<FlowThread> flowThreads = new ArrayList<FlowThread>();
	private ArrayList<FlowListener> listeners = new ArrayList<FlowListener>();
	private boolean entry;
	private List<State> callStack = new LinkedList<State>();
	
	public void addFlowListener(FlowListener listener) {
		listeners.add(listener);
	}
	
	public synchronized void addEvent(Event event) {
		for (FlowThread thread : flowThreads) {
			if (!thread.addEvent(event)) {
				Utils.logOnce("Flow thread stopped responding!");
			}
		}
	}
	
	public void sendEvent(Event event, FlowEventInfo flowEventInfo) {
		for (FlowListener listener : listeners) {
			listener.onSendEvent(event, flowEventInfo);
		}
	}
	
	public DelayedEvent sendEvent(final Event message, final int delay, final FlowEventInfo flowEventInfo) {
		return new DelayedEvent(delay) {
			@Override
			public void run() {
				sendEvent(message, flowEventInfo);
			}
		};
	}
	
	public synchronized FlowThread addFlowThread(State state) {
		FlowThread thread = new FlowThread(state);
		flowThreads.add(thread);
		return thread;
	}
	
	public synchronized void removeFlowThread(FlowThread thread) {
		flowThreads.remove(thread);
	}

	public void stop() {
		for (FlowThread thread : flowThreads) {
			thread.stop();
		}
	}

	public class FlowThread implements Runnable {

		ArrayBlockingQueue<Event> eventQueue = new ArrayBlockingQueue<Event>(1000);
		State currentState;
		private State gotoState;
		private boolean running = true;
		boolean willExit = false;
		private Map<String,EventClock> eventClocks = new HashMap<>();
		private Thread thread;
		
		public FlowThread(State state) {
			currentState = state;
			thread = new Thread(this);
		}

		public void start() {
			thread.start();
		}
		
		public boolean isRunning() {
			return running;
		}

		public void stop() {
			willExit = true;
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			currentState.setFlowThread(this);
			for (FlowListener listener : listeners) {
				listener.onGotoState(currentState, null);
			}
			processState();
			running = false;
			removeFlowThread(this);
		}

		public boolean addEvent(Event event) {
			try {
				eventQueue.add(event);
				return true;
			} catch (IllegalStateException e) {
				return false;
			}
		}
		
		public FlowThread runState(State state, FlowEventInfo flowEventInfo) {
			FlowThread thread = new FlowThread(state);
			flowThreads.add(thread);
			thread.start();
			return thread;
		}
		
		public void gotoState(Flow flow) {
			gotoState(flow.getInitialState(), null, new FlowEventInfo(null, null));
		}
		
		public void gotoState(State state) {
			gotoState(state, null, new FlowEventInfo(null, null));
		}
		
		public void gotoState(State state, State fromState, FlowEventInfo flowEventInfo) {
			//debug("Going to state " + state + " from " + currentState);
			gotoState = state;
			state.setFlowThread(this);
			if (fromState != null) {
				state.caller = fromState.caller;
			}
			for (FlowListener listener : listeners) {
				listener.onGotoState(state, flowEventInfo);
			}
		}
		
		public boolean callState(Flow subFlow, FlowEventInfo flowEventInfo) throws Exception { 
			return callState(subFlow.getInitialState(), flowEventInfo);
		}
		
		//int depth = 0;
		
		// Returns true if the state has run to completion
		public boolean callState(State subState, FlowEventInfo flowEventInfo) throws Exception { 
			//debug("Calling state " + subState + " from " + currentState);
			
			State callingState = currentState;
			
			/*
			 TODO: we should pop out here
			while (callingState != flowEventInfo.getTriggeringState()) {
				callingState.onexit();
				callingState = callingState.caller;
			}
			*/
			//System.out.println("Call triggered in " + flowEventInfo.getTriggeringState() + " called from " + callingState);
			
			currentState = subState;
			currentState.caller = callingState;
			currentState.setFlowThread(this);
			for (FlowListener listener : listeners) {
				listener.onCallState(subState, flowEventInfo);
			}
			boolean complete = processState();
			callStack.remove(0);
			if (!complete) return false;
			if (currentState.returnEvent != null) {
				if (raiseEvent(currentState.returnEvent, flowEventInfo) == State.EVENT_ABORTED) return false;
			}
			return true;
		}
		
		public void returnFromCall(State fromState, Event returnEvent, FlowEventInfo flowEventInfo) {
			if (fromState.caller != null) {
				State state = currentState;
				while (state.caller != null) {
					if (state.caller == fromState.caller) {
						currentState.returnTo = fromState.caller;
						currentState.returnEvent = returnEvent;
						for (FlowListener listener : listeners) {
							listener.onReturnState(currentState, currentState.returnTo, flowEventInfo);
						}
						return;
					}
					state = state.caller;
				}
			} else {
				willExit = true;
				for (FlowListener listener : listeners) {
					listener.onReturnState(currentState, null, flowEventInfo);
				}
			}
		}
		
		public void reentryState(State toState, FlowEventInfo flowEventInfo) {
			State state = currentState;
			while (state.caller != null) {
				if (state.caller == toState) {
					currentState.returnTo = toState;
					for (FlowListener listener : listeners) {
						listener.onReturnState(currentState, toState, flowEventInfo);
					}
					return;
				}
				state = state.caller;
			}
			entry = true;
		}
		
		//int depth = 0;

		// Returns false if execution should be aborted
		private boolean processState() {
			//depth++;
			//System.out.println("Processing " + currentState);
			callStack.add(0, currentState);
			entry = true;
			try {
				while (true) {
					if (willExit) {
						currentState.onexit();
						return false;
					}
					if (entry) {
						entry = false;
						try {
							currentState.onentry();
						} catch (Exception e) {
							flowException(e, new Event("state.enter"));
						}
					} else {
						Event event = eventQueue.take();
						try {
							currentState.onFlowEvent(event);
						} catch (Exception e) {
							flowException(e, event);
						}
					}
					if (gotoState != null) {
						currentState.onexit();
						if (gotoState.caller != currentState.caller) {
							currentState = currentState.caller;
							return false;
						} else {
							currentState = gotoState;
							gotoState = null;
							entry = true;
						}
					} else if (currentState.returnTo != null) {
						State fromState = currentState;
						fromState.onexit();
						currentState = fromState.caller;
						currentState.returnEvent = fromState.returnEvent;
						if (currentState == fromState.returnTo) {
							return true;
						} else {
							currentState.returnTo = fromState.returnTo;
							return false;
						}
					}// else if (currentState != callStack.get(0)) {
					//	return false;
					//} 
				}
			} catch (InterruptedException e1) {
			}
			return false;
		}
		
		private void flowException(Exception e, Event event) {
			for (FlowListener listener : listeners) {
				if (e instanceof FlowException) {
					listener.onFlowException((FlowException)e);
				} else {
					listener.onFlowException(new FlowException(e, currentState, event));
				}
			}
		}

		public int raiseEvent(Event event, FlowEventInfo flowEventInfo) throws Exception {
			for (FlowListener listener : listeners) {
				listener.onFlowEvent(event, flowEventInfo);
			}
			return currentState.onFlowEvent(event);
		}
		
		public DelayedEvent raiseEvent(final Event event, final int delay, FlowEventInfo flowEventInfo) {
			return new DelayedEvent(delay) {
				@Override
				public void run() {
					FlowThread.this.addEvent(event);
				}
			};
		}
		
		public void addEventClock(int min, int max, String name) {
			if (!eventClocks.containsKey(name)) {
				eventClocks.put(name, new EventClock(this, min, max, name));
			}
		}

		public FlowRunner getFlowRunner() {
			return FlowRunner.this;
		}

	}

	

}
