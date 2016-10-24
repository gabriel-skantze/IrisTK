package iristk.app.$situated_dialog$;

import java.util.List;
import java.io.File;
import iristk.xml.XmlMarshaller.XMLLocation;
import iristk.system.Event;
import iristk.flow.*;
import iristk.util.Record;
import static iristk.util.Converters.*;
import static iristk.flow.State.*;

public class $Situated_dialog$Flow extends iristk.flow.Flow {

	private iristk.situated.SystemAgentFlow agent;
	private iristk.situated.SystemAgent system;
	private Integer number;

	private void initVariables() {
		system = (iristk.situated.SystemAgent) agent.getSystemAgent();
	}

	public iristk.situated.SystemAgent getSystem() {
		return this.system;
	}

	public void setSystem(iristk.situated.SystemAgent value) {
		this.system = value;
	}

	public Integer getNumber() {
		return this.number;
	}

	public void setNumber(Integer value) {
		this.number = value;
	}

	public iristk.situated.SystemAgentFlow getAgent() {
		return this.agent;
	}

	@Override
	public Object getVariable(String name) {
		if (name.equals("system")) return this.system;
		if (name.equals("number")) return this.number;
		if (name.equals("agent")) return this.agent;
		return null;
	}


	public $Situated_dialog$Flow(iristk.situated.SystemAgentFlow agent) {
		this.agent = agent;
		initVariables();
	}

	@Override
	public State getInitialState() {return new Idle();}


	public class Idle extends Dialog implements Initial {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 14
			try {
				EXECUTION: {
					int count = getCount(1347137144) + 1;
					incrCount(1347137144);
					// Line: 15
					if (system.hasUsers()) {
						iristk.situated.SystemAgentFlow.attendRandom state0 = agent.new attendRandom();
						if (!flowThread.callState(state0, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 15, 33)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						// Line: 17
						Greeting state1 = new Greeting();
						flowThread.gotoState(state1, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 17, 29)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
						// Line: 18
					} else {
						iristk.situated.SystemAgentFlow.attendNobody state2 = agent.new attendNobody();
						if (!flowThread.callState(state2, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 15, 33)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 14, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 22
			try {
				count = getCount(1289696681) + 1;
				if (event.triggers("sense.user.enter")) {
					incrCount(1289696681);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						iristk.situated.SystemAgentFlow.attend state3 = agent.new attend();
						state3.setTarget(event.get("user"));
						if (!flowThread.callState(state3, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 22, 36)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						// Line: 24
						Greeting state4 = new Greeting();
						flowThread.gotoState(state4, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 24, 28)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 22, 36));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class Greeting extends Dialog {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 29
			try {
				EXECUTION: {
					int count = getCount(1811075214) + 1;
					incrCount(1811075214);
					iristk.situated.SystemAgentFlow.say state5 = agent.new say();
					StringCreator string6 = new StringCreator();
					string6.append("Hi there, let's play a game");
					state5.setText(string6.toString());
					if (!flowThread.callState(state5, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 29, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 31
					Start state7 = new Start();
					flowThread.gotoState(state7, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 31, 25)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 29, 12));
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


	private class Start extends Dialog {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 36
			try {
				EXECUTION: {
					int count = getCount(1940447180) + 1;
					incrCount(1940447180);
					// Line: 37
					number = new java.util.Random().nextInt(10) + 1;
					// Line: 38
					system.putUsers("guesses", 0);
					iristk.situated.SystemAgentFlow.say state8 = agent.new say();
					StringCreator string9 = new StringCreator();
					string9.append("I am thinking of a number between 1 and 10.");
					state8.setText(string9.toString());
					if (!flowThread.callState(state8, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 36, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 40
					Guess state10 = new Guess();
					flowThread.gotoState(state10, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 40, 25)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 36, 12));
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
			// Line: 45
			try {
				EXECUTION: {
					int count = getCount(476402209) + 1;
					incrCount(476402209);
					iristk.situated.SystemAgentFlow.say state11 = agent.new say();
					StringCreator string12 = new StringCreator();
					string12.append("What is your guess?");
					state11.setText(string12.toString());
					if (!flowThread.callState(state11, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 45, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					iristk.situated.SystemAgentFlow.listen state13 = agent.new listen();
					if (!flowThread.callState(state13, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 45, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 45, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 49
			try {
				count = getCount(1490180672) + 1;
				if (event.triggers("sense.user.speak")) {
					if (event.has("sem:number")) {
						incrCount(1490180672);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 50
							if (system.isAttendingAll()) {
								iristk.situated.SystemAgentFlow.attend state14 = agent.new attend();
								state14.setTarget(event.get("user"));
								if (!flowThread.callState(state14, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 50, 39)))) {
									eventResult = EVENT_ABORTED;
									break EXECUTION;
								}
							}
							// Line: 53
							system.getCurrentUser().incrInteger("guesses");
							// Line: 54
							if (asInteger(event.get("sem:number")) == number) {
								iristk.situated.SystemAgentFlow.say state15 = agent.new say();
								StringCreator string16 = new StringCreator();
								// Line: 54
								string16.append(event.get("sem:number"));
								string16.append("is correct, you only needed");
								// Line: 54
								string16.append(system.getCurrentUser().get("guesses"));
								string16.append("guesses.");
								state15.setText(string16.toString());
								if (!flowThread.callState(state15, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 54, 53)))) {
									eventResult = EVENT_ABORTED;
									break EXECUTION;
								}
								// Line: 61
								CheckAgain state17 = new CheckAgain();
								flowThread.gotoState(state17, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 61, 31)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
								// Line: 62
							} else {
								// Line: 63
								if (asInteger(event.get("sem:number")) > number) {
									iristk.situated.SystemAgentFlow.say state18 = agent.new say();
									StringCreator string19 = new StringCreator();
									// Line: 63
									string19.append(event.get("sem:number"));
									string19.append("is too high.");
									state18.setText(string19.toString());
									if (!flowThread.callState(state18, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 63, 56)))) {
										eventResult = EVENT_ABORTED;
										break EXECUTION;
									}
									// Line: 65
								} else {
									iristk.situated.SystemAgentFlow.say state20 = agent.new say();
									StringCreator string21 = new StringCreator();
									// Line: 65
									string21.append(event.get("sem:number"));
									string21.append("is too low.");
									state20.setText(string21.toString());
									if (!flowThread.callState(state20, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 63, 56)))) {
										eventResult = EVENT_ABORTED;
										break EXECUTION;
									}
								}
								// Line: 68
								if (system.hasManyUsers()) {
									// Line: 69
									boolean chosen22 = false;
									boolean matching23 = true;
									while (!chosen22 && matching23) {
										int rand24 = random(2143192188, 2, iristk.util.RandomList.RandomModel.DECK_RESHUFFLE_NOREPEAT);
										matching23 = false;
										if (true) {
											matching23 = true;
											if (rand24 >= 0 && rand24 < 1) {
												chosen22 = true;
												iristk.situated.SystemAgentFlow.attendOther state25 = agent.new attendOther();
												if (!flowThread.callState(state25, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 69, 14)))) {
													eventResult = EVENT_ABORTED;
													break EXECUTION;
												}
											}
										}
										if (true) {
											matching23 = true;
											if (rand24 >= 1 && rand24 < 2) {
												chosen22 = true;
												iristk.situated.SystemAgentFlow.attendAll state26 = agent.new attendAll();
												if (!flowThread.callState(state26, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 69, 14)))) {
													eventResult = EVENT_ABORTED;
													break EXECUTION;
												}
											}
										}
									}
								}
								// Line: 74
								flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 74, 15)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 49, 61));
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
			// Line: 80
			try {
				EXECUTION: {
					int count = getCount(231685785) + 1;
					incrCount(231685785);
					iristk.situated.SystemAgentFlow.say state27 = agent.new say();
					StringCreator string28 = new StringCreator();
					string28.append("Do you want to play again?");
					state27.setText(string28.toString());
					if (!flowThread.callState(state27, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 80, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					iristk.situated.SystemAgentFlow.listen state29 = agent.new listen();
					if (!flowThread.callState(state29, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 80, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 80, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 84
			try {
				count = getCount(2110121908) + 1;
				if (event.triggers("sense.user.speak")) {
					if (event.has("sem:yes")) {
						incrCount(2110121908);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							iristk.situated.SystemAgentFlow.say state30 = agent.new say();
							StringCreator string31 = new StringCreator();
							string31.append("Okay, let's play again.");
							state30.setText(string31.toString());
							if (!flowThread.callState(state30, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 84, 58)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
							// Line: 86
							Start state32 = new Start();
							flowThread.gotoState(state32, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 86, 25)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 84, 58));
			}
			// Line: 88
			try {
				count = getCount(1023487453) + 1;
				if (event.triggers("sense.user.speak")) {
					if (event.has("sem:no")) {
						incrCount(1023487453);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							iristk.situated.SystemAgentFlow.say state33 = agent.new say();
							StringCreator string34 = new StringCreator();
							string34.append("Okay, goodbye");
							state33.setText(string34.toString());
							if (!flowThread.callState(state33, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 88, 57)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
							// Line: 90
							Idle state35 = new Idle();
							flowThread.gotoState(state35, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 90, 24)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 88, 57));
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
			// Line: 95
			try {
				count = getCount(1694819250) + 1;
				if (event.triggers("sense.user.speech.start")) {
					if (system.isAttending(event) && eq(event.get("speakers"), 1)) {
						incrCount(1694819250);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							iristk.situated.SystemAgentFlow.gesture state36 = agent.new gesture();
							state36.setName("smile");
							if (!flowThread.callState(state36, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 95, 102)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 95, 102));
			}
			// Line: 98
			try {
				count = getCount(1365202186) + 1;
				if (event.triggers("sense.user.speak")) {
					incrCount(1365202186);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						iristk.situated.SystemAgentFlow.say state37 = agent.new say();
						StringCreator string38 = new StringCreator();
						string38.append("Sorry, I didn't get that.");
						state37.setText(string38.toString());
						if (!flowThread.callState(state37, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 98, 36)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						// Line: 100
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 100, 14)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 98, 36));
			}
			// Line: 102
			try {
				count = getCount(1586600255) + 1;
				if (event.triggers("sense.user.speak.side")) {
					incrCount(1586600255);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						iristk.situated.SystemAgentFlow.attendOther state39 = agent.new attendOther();
						state39.setMode("eyes");
						if (!flowThread.callState(state39, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 102, 41)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						iristk.situated.SystemAgentFlow.say state40 = agent.new say();
						StringCreator string41 = new StringCreator();
						string41.append("I didn't ask you.");
						state40.setText(string41.toString());
						if (!flowThread.callState(state40, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 102, 41)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						iristk.situated.SystemAgentFlow.attendOther state42 = agent.new attendOther();
						state42.setMode("eyes");
						if (!flowThread.callState(state42, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 102, 41)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						// Line: 106
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 106, 14)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 102, 41));
			}
			// Line: 108
			try {
				count = getCount(932583850) + 1;
				if (event.triggers("sense.user.speak.multi")) {
					incrCount(932583850);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						iristk.situated.SystemAgentFlow.say state43 = agent.new say();
						StringCreator string44 = new StringCreator();
						string44.append("Don't speak at the same time.");
						state43.setText(string44.toString());
						if (!flowThread.callState(state43, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 108, 42)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						// Line: 110
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 110, 14)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 108, 42));
			}
			// Line: 112
			try {
				count = getCount(1579572132) + 1;
				if (event.triggers("sense.user.silence")) {
					incrCount(1579572132);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						iristk.situated.SystemAgentFlow.say state45 = agent.new say();
						StringCreator string46 = new StringCreator();
						string46.append("Sorry, I didn't hear anything.");
						state45.setText(string46.toString());
						if (!flowThread.callState(state45, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 112, 38)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						// Line: 114
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 114, 14)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 112, 38));
			}
			// Line: 116
			try {
				count = getCount(305808283) + 1;
				if (event.triggers("sense.user.leave")) {
					if (system.isAttending(event)) {
						incrCount(305808283);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 117
							if (system.hasUsers()) {
								iristk.situated.SystemAgentFlow.attendRandom state47 = agent.new attendRandom();
								if (!flowThread.callState(state47, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 117, 33)))) {
									eventResult = EVENT_ABORTED;
									break EXECUTION;
								}
								// Line: 119
								Guess state48 = new Guess();
								flowThread.gotoState(state48, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 119, 27)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
								// Line: 120
							} else {
								// Line: 121
								Idle state49 = new Idle();
								flowThread.gotoState(state49, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 121, 25)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 116, 69));
			}
			// Line: 124
			try {
				count = getCount(405662939) + 1;
				if (event.triggers("repeat")) {
					incrCount(405662939);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 125
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 125, 14)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\templates\\situated_dialog\\src\\iristk\\app\\$situated_dialog$\\$Situated_dialog$Flow.xml"), 124, 26));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


}
