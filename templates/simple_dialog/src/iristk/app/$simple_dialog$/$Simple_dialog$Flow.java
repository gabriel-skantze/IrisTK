package iristk.app.$simple_dialog$;

import java.util.List;
import java.io.File;
import iristk.xml.XmlMarshaller.XMLLocation;
import iristk.system.Event;
import iristk.flow.*;
import iristk.util.Record;
import static iristk.util.Converters.*;
import static iristk.flow.State.*;

public class $Simple_dialog$Flow extends iristk.flow.Flow {

	private Integer number;
	private Integer guesses;

	private void initVariables() {
	}

	public Integer getNumber() {
		return this.number;
	}

	public void setNumber(Integer value) {
		this.number = value;
	}

	public Integer getGuesses() {
		return this.guesses;
	}

	public void setGuesses(Integer value) {
		this.guesses = value;
	}

	@Override
	public Object getVariable(String name) {
		if (name.equals("number")) return this.number;
		if (name.equals("guesses")) return this.guesses;
		return null;
	}


	public $Simple_dialog$Flow() {
		initVariables();
	}

	@Override
	public State getInitialState() {return new Start();}


	public class Start extends State implements Initial {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 10
			try {
				EXECUTION: {
					int count = getCount(1334729950) + 1;
					incrCount(1334729950);
					// Line: 11
					number = new java.util.Random().nextInt(10) + 1;
					// Line: 12
					guesses = 0;
					iristk.flow.DialogFlow.say state0 = new iristk.flow.DialogFlow.say();
					StringCreator string1 = new StringCreator();
					string1.append("I am thinking of a number between 1 and 10, let's see if you can guess which one it is.");
					state0.setText(string1.toString());
					if (!flowThread.callState(state0, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 10, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 14
					Guess state2 = new Guess();
					flowThread.gotoState(state2, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 14, 25)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 10, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class Guess extends Dialog {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 19
			try {
				EXECUTION: {
					int count = getCount(1289696681) + 1;
					incrCount(1289696681);
					iristk.flow.DialogFlow.listen state3 = new iristk.flow.DialogFlow.listen();
					if (!flowThread.callState(state3, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 19, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 19, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 22
			try {
				count = getCount(1285044316) + 1;
				if (event.triggers("sense.user.speak")) {
					if (event.has("sem:number")) {
						incrCount(1285044316);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 23
							guesses++;
							// Line: 24
							if (asInteger(event.get("sem:number")) == number) {
								iristk.flow.DialogFlow.say state4 = new iristk.flow.DialogFlow.say();
								StringCreator string5 = new StringCreator();
								string5.append("That was correct, you only needed");
								// Line: 24
								string5.append(guesses);
								string5.append("guesses.");
								state4.setText(string5.toString());
								if (!flowThread.callState(state4, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 24, 53)))) {
									eventResult = EVENT_ABORTED;
									break EXECUTION;
								}
								// Line: 26
								CheckAgain state6 = new CheckAgain();
								flowThread.gotoState(state6, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 26, 31)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
								// Line: 27
							} else if (asInteger(event.get("sem:number")) > number) {
								iristk.flow.DialogFlow.say state7 = new iristk.flow.DialogFlow.say();
								StringCreator string8 = new StringCreator();
								string8.append("That was too high, let's try again.");
								state7.setText(string8.toString());
								if (!flowThread.callState(state7, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 24, 53)))) {
									eventResult = EVENT_ABORTED;
									break EXECUTION;
								}
								// Line: 29
								flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 29, 15)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
								// Line: 30
							} else {
								iristk.flow.DialogFlow.say state9 = new iristk.flow.DialogFlow.say();
								StringCreator string10 = new StringCreator();
								string10.append("That was too low, let's try again.");
								state9.setText(string10.toString());
								if (!flowThread.callState(state9, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 24, 53)))) {
									eventResult = EVENT_ABORTED;
									break EXECUTION;
								}
								// Line: 32
								flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 32, 15)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 22, 61));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class CheckAgain extends Dialog {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 38
			try {
				EXECUTION: {
					int count = getCount(183264084) + 1;
					incrCount(183264084);
					iristk.flow.DialogFlow.say state11 = new iristk.flow.DialogFlow.say();
					StringCreator string12 = new StringCreator();
					string12.append("Do you want to play again?");
					state11.setText(string12.toString());
					if (!flowThread.callState(state11, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 38, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					iristk.flow.DialogFlow.listen state13 = new iristk.flow.DialogFlow.listen();
					if (!flowThread.callState(state13, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 38, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 38, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 42
			try {
				count = getCount(476402209) + 1;
				if (event.triggers("sense.user.speak")) {
					if (event.has("sem:yes")) {
						incrCount(476402209);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							iristk.flow.DialogFlow.say state14 = new iristk.flow.DialogFlow.say();
							StringCreator string15 = new StringCreator();
							string15.append("Okay, let's play again.");
							state14.setText(string15.toString());
							if (!flowThread.callState(state14, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 42, 58)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
							// Line: 44
							Start state16 = new Start();
							flowThread.gotoState(state16, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 44, 25)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 42, 58));
			}
			// Line: 46
			try {
				count = getCount(460332449) + 1;
				if (event.triggers("sense.user.speak")) {
					if (event.has("sem:no")) {
						incrCount(460332449);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							iristk.flow.DialogFlow.say state17 = new iristk.flow.DialogFlow.say();
							StringCreator string18 = new StringCreator();
							string18.append("Okay, goodbye");
							state17.setText(string18.toString());
							if (!flowThread.callState(state17, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 46, 57)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
							// Line: 48
							System.exit(0);
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 46, 57));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class Dialog extends State {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 53
			try {
				count = getCount(250075633) + 1;
				if (event.triggers("sense.user.silence")) {
					incrCount(250075633);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						iristk.flow.DialogFlow.say state19 = new iristk.flow.DialogFlow.say();
						StringCreator string20 = new StringCreator();
						string20.append("I am sorry, I didn't hear anything.");
						state19.setText(string20.toString());
						if (!flowThread.callState(state19, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 53, 38)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						// Line: 55
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 55, 14)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 53, 38));
			}
			// Line: 57
			try {
				count = getCount(517938326) + 1;
				if (event.triggers("sense.user.speak")) {
					incrCount(517938326);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						iristk.flow.DialogFlow.say state21 = new iristk.flow.DialogFlow.say();
						StringCreator string22 = new StringCreator();
						string22.append("I am sorry, I didn't get that.");
						state21.setText(string22.toString());
						if (!flowThread.callState(state21, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 57, 36)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						// Line: 59
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 59, 14)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\simple_dialog\\src\\iristk\\app\\$simple_dialog$\\$Simple_dialog$Flow.xml"), 57, 36));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


}
