package iristk.situated;

import java.io.File;
import iristk.xml.XmlMarshaller.XMLLocation;
import iristk.system.Event;
import iristk.flow.*;
import iristk.util.Record;
import static iristk.util.Converters.*;

public class SystemAgentFlow extends iristk.flow.Flow {

	private SystemAgent systemAgent;
	private String agent;
	private Record gestures;
	private iristk.speech.SpeechTextProcessor speechTextProcessor;

	private void initVariables() {
		agent = asString(systemAgent.id);
		gestures = asRecord(new Record());
	}

	public String getAgent() {
		return this.agent;
	}

	public void setAgent(String value) {
		this.agent = value;
	}

	public Record getGestures() {
		return this.gestures;
	}

	public void setGestures(Record value) {
		this.gestures = value;
	}

	public iristk.speech.SpeechTextProcessor getSpeechTextProcessor() {
		return this.speechTextProcessor;
	}

	public void setSpeechTextProcessor(iristk.speech.SpeechTextProcessor value) {
		this.speechTextProcessor = value;
	}

	public SystemAgent getSystemAgent() {
		return this.systemAgent;
	}

	@Override
	public Object getVariable(String name) {
		if (name.equals("agent")) return this.agent;
		if (name.equals("gestures")) return this.gestures;
		if (name.equals("speechTextProcessor")) return this.speechTextProcessor;
		if (name.equals("systemAgent")) return this.systemAgent;
		return null;
	}


	public SystemAgentFlow(SystemAgent systemAgent) {
		this.systemAgent = systemAgent;
		initVariables();
	}

	@Override
	public State getInitialState() {return new Idle();}


	private class Base extends State {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
			flowThread.addEventClock(1000, 5000, "timer_1490180672");
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
			// Line: 15
			try {
				count = getCount(195600860) + 1;
				if (event.triggers("action.attend")) {
					if (eq(event.get("target"),"nobody") && eq(event.get("agent"), agent)) {
						incrCount(195600860);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 16
							Idle state0 = new Idle();
							flowThread.gotoState(state0, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 16, 24)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 15, 93));
			}
			// Line: 18
			try {
				count = getCount(1347137144) + 1;
				if (event.triggers("action.attend.nobody")) {
					incrCount(1347137144);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 19
						Idle state1 = new Idle();
						flowThread.gotoState(state1, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 19, 24)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 18, 40));
			}
			// Line: 21
			try {
				count = getCount(1973336893) + 1;
				if (event.triggers("action.attend.think")) {
					incrCount(1973336893);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 22
						Think state2 = new Think();
						flowThread.gotoState(state2, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 22, 25)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 21, 39));
			}
			// Line: 24
			try {
				count = getCount(1174290147) + 1;
				if (event.triggers("action.attend.asleep")) {
					if (eq(event.get("agent"), agent)) {
						incrCount(1174290147);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 25
							Asleep state3 = new Asleep();
							flowThread.gotoState(state3, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 25, 26)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 24, 70));
			}
			// Line: 27
			try {
				count = getCount(1285044316) + 1;
				if (event.triggers("action.attend.all")) {
					if (eq(event.get("agent"), agent)) {
						incrCount(1285044316);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 28
							AttendingAll state4 = new AttendingAll();
							flowThread.gotoState(state4, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 28, 32)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 27, 67));
			}
			// Line: 30
			try {
				count = getCount(1811075214) + 1;
				if (event.triggers("action.attend")) {
					if (event.has("location") && eq(event.get("agent"), agent)) {
						incrCount(1811075214);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 31
							AttendingLocation state5 = new AttendingLocation();
							state5.setMode(event.get("mode"));
							state5.setLocation(event.get("location"));
							state5.setSpeed(event.get("speed"));
							flowThread.gotoState(state5, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 31, 107)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 30, 83));
			}
			// Line: 33
			try {
				count = getCount(1407343478) + 1;
				if (event.triggers("action.attend")) {
					if (event.has("target") && systemAgent.hasUser(asString(event.get("target"))) && eq(event.get("agent"), agent)) {
						incrCount(1407343478);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 34
							AttendingAgent state6 = new AttendingAgent();
							state6.setMode(event.get("mode"));
							state6.setSpeed(event.get("speed"));
							state6.setTarget(event.get("target"));
							flowThread.gotoState(state6, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 34, 100)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 33, 129));
			}
			// Line: 36
			try {
				count = getCount(245565335) + 1;
				if (event.triggers("action.attend")) {
					if (systemAgent.hasItem(asString(event.get("target")))) {
						incrCount(245565335);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 37
							AttendingItem state7 = new AttendingItem();
							state7.setMode(event.get("mode"));
							state7.setSpeed(event.get("speed"));
							state7.setTarget(event.get("target"));
							flowThread.gotoState(state7, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 37, 99)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 36, 84));
			}
			// Line: 39
			try {
				count = getCount(1066376662) + 1;
				if (event.triggers("monitor.speech.prominence")) {
					incrCount(1066376662);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 40
						if (gestures.has("" + event.get("action"))) {
							// Line: 41
							Event sendEvent8 = new Event("action.gesture");
							sendEvent8.putIfNotNull("name", gestures.get("" + event.get("action")));
							flowRunner.sendEvent(sendEvent8, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 41, 68)));
						}
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 39, 46));
			}
			// Line: 44
			count = getCount(1490180672) + 1;
			if (event.triggers("timer_1490180672")) {
				incrCount(1490180672);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					// Line: 45
					Event raiseEvent9 = new Event("blink");
					if (flowThread.raiseEvent(raiseEvent9, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 45, 26))) == State.EVENT_ABORTED) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			// Line: 47
			try {
				count = getCount(1919892312) + 1;
				if (event.triggers("blink")) {
					incrCount(1919892312);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 48
						Event sendEvent10 = new Event("action.gesture");
						sendEvent10.putIfNotNull("agent", agent);
						sendEvent10.putIfNotNull("name", "blink");
						flowRunner.sendEvent(sendEvent10, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 48, 67)));
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 47, 25));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	public class Idle extends Base implements Initial {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 53
			try {
				EXECUTION: {
					int count = getCount(517938326) + 1;
					incrCount(517938326);
					// Line: 54
					Event sendEvent11 = new Event("action.gaze");
					sendEvent11.putIfNotNull("mode", "headpose");
					sendEvent11.putIfNotNull("agent", agent);
					sendEvent11.putIfNotNull("location", systemAgent.getRelative(systemAgent.getNobody().getHeadLocation()));
					flowRunner.sendEvent(sendEvent11, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 54, 147)));
					// Line: 55
					Event sendEvent12 = new Event("monitor.attend");
					sendEvent12.putIfNotNull("agent", agent);
					sendEvent12.putIfNotNull("target", "nobody");
					flowRunner.sendEvent(sendEvent12, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 55, 70)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 53, 12));
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


	private class Think extends Base {

		final State currentState = this;
		public Integer dir = asInteger(1);
		public Location nobody = systemAgent.getRelative(systemAgent.getNobody().getHeadLocation());


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
			flowThread.addEventClock(500, 500, "timer_114935352");
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 62
			try {
				EXECUTION: {
					int count = getCount(204349222) + 1;
					incrCount(204349222);
					// Line: 63
					Event sendEvent13 = new Event("action.gaze");
					sendEvent13.putIfNotNull("mode", "headpose");
					sendEvent13.putIfNotNull("agent", agent);
					sendEvent13.putIfNotNull("location", nobody);
					flowRunner.sendEvent(sendEvent13, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 63, 87)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 62, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 65
			count = getCount(114935352) + 1;
			if (event.triggers("timer_114935352")) {
				incrCount(114935352);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					// Line: 66
					dir = -dir;
					// Line: 67
					Event sendEvent14 = new Event("action.gaze");
					sendEvent14.putIfNotNull("mode", "eyes");
					sendEvent14.putIfNotNull("location", nobody.add(new Location(0.3 * dir, 0, 0)));
					flowRunner.sendEvent(sendEvent14, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 67, 102)));
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class AttendingAll extends Base {

		final State currentState = this;
		public Agent gazeTarget = systemAgent.getRandomUser();
		public Location middle;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
			flowThread.addEventClock(1000, 1000, "timer_1579572132");
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 74
			try {
				EXECUTION: {
					int count = getCount(515132998) + 1;
					incrCount(515132998);
					// Line: 75
					Event raiseEvent15 = new Event("adjustHeadPose");
					if (flowThread.raiseEvent(raiseEvent15, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 75, 35))) == State.EVENT_ABORTED) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 76
					Event sendEvent16 = new Event("monitor.attend.all");
					flowRunner.sendEvent(sendEvent16, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 76, 38)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 74, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 78
			try {
				count = getCount(1651191114) + 1;
				if (event.triggers("adjustHeadPose")) {
					incrCount(1651191114);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 79
						Location newMiddle = systemAgent.getUsersMiddleLocation();
						// Line: 80
						if (middle == null || newMiddle.distance(middle) > 0.2) {
							// Line: 81
							middle = newMiddle;
							// Line: 82
							Event sendEvent17 = new Event("action.gaze");
							sendEvent17.putIfNotNull("mode", "headpose");
							sendEvent17.putIfNotNull("location", systemAgent.getRelative(middle));
							flowRunner.sendEvent(sendEvent17, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 82, 97)));
						}
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 78, 34));
			}
			// Line: 85
			count = getCount(1579572132) + 1;
			if (event.triggers("timer_1579572132")) {
				incrCount(1579572132);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					// Line: 86
					Event raiseEvent18 = new Event("adjustHeadPose");
					if (flowThread.raiseEvent(raiseEvent18, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 86, 35))) == State.EVENT_ABORTED) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 87
					gazeTarget = systemAgent.getOtherUser(gazeTarget.id);
					// Line: 88
					Event sendEvent19 = new Event("action.gaze");
					sendEvent19.putIfNotNull("mode", "eyes");
					sendEvent19.putIfNotNull("location", systemAgent.getRelative(gazeTarget.getHeadLocation()));
					flowRunner.sendEvent(sendEvent19, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 88, 114)));
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class AttendingAgent extends Base {

		final State currentState = this;
		public Agent user;

		public String target = null;
		public String mode = asString("headpose");
		public String speed = asString("medium");

		public void setTarget(Object value) {
			if (value != null) {
				target = asString(value);
				params.put("target", value);
			}
		}

		public void setMode(Object value) {
			if (value != null) {
				mode = asString(value);
				params.put("mode", value);
			}
		}

		public void setSpeed(Object value) {
			if (value != null) {
				speed = asString(value);
				params.put("speed", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 97
			try {
				EXECUTION: {
					int count = getCount(1130478920) + 1;
					incrCount(1130478920);
					// Line: 98
					user = systemAgent.getUser(target);
					// Line: 99
					Event sendEvent20 = new Event("action.gaze");
					sendEvent20.putIfNotNull("mode", mode);
					sendEvent20.putIfNotNull("agent", agent);
					sendEvent20.putIfNotNull("location", systemAgent.getRelative(user.getHeadLocation()));
					sendEvent20.putIfNotNull("speed", speed);
					flowRunner.sendEvent(sendEvent20, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 99, 138)));
					// Line: 100
					if (eq(mode,"headpose")) {
						// Line: 101
						mode = "default";
					}
					// Line: 103
					Event sendEvent21 = new Event("monitor.attend");
					sendEvent21.putIfNotNull("agent", agent);
					sendEvent21.putIfNotNull("target", target);
					flowRunner.sendEvent(sendEvent21, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 103, 68)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 97, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 105
			try {
				count = getCount(1562557367) + 1;
				if (event.triggers("sense.user.move")) {
					if (eq(event.get("agent"), agent) && event.has("" + user.id + ":head:location")) {
						incrCount(1562557367);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 106
							Event sendEvent22 = new Event("action.gaze");
							sendEvent22.putIfNotNull("mode", mode);
							sendEvent22.putIfNotNull("agent", agent);
							sendEvent22.putIfNotNull("location", systemAgent.getRelative((Location)event.get("" + user.id + ":head:location")));
							flowRunner.sendEvent(sendEvent22, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 106, 139)));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 105, 100));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class AttendingItem extends Base {

		final State currentState = this;

		public String target = null;
		public String mode = asString("default");
		public String speed = asString("medium");

		public void setTarget(Object value) {
			if (value != null) {
				target = asString(value);
				params.put("target", value);
			}
		}

		public void setMode(Object value) {
			if (value != null) {
				mode = asString(value);
				params.put("mode", value);
			}
		}

		public void setSpeed(Object value) {
			if (value != null) {
				speed = asString(value);
				params.put("speed", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 114
			try {
				EXECUTION: {
					int count = getCount(758529971) + 1;
					incrCount(758529971);
					// Line: 115
					if (systemAgent.hasItem(target)) {
						// Line: 116
						Event sendEvent23 = new Event("action.gaze");
						sendEvent23.putIfNotNull("mode", mode);
						sendEvent23.putIfNotNull("location", systemAgent.getRelative(systemAgent.getItem(target).location));
						sendEvent23.putIfNotNull("speed", speed);
						flowRunner.sendEvent(sendEvent23, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 116, 137)));
					}
					// Line: 118
					Event sendEvent24 = new Event("monitor.attend");
					sendEvent24.putIfNotNull("target", target);
					flowRunner.sendEvent(sendEvent24, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 118, 52)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 114, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 120
			try {
				count = getCount(1869997857) + 1;
				if (event.triggers("sense.item.move")) {
					if (systemAgent.hasItem(target)) {
						incrCount(1869997857);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 121
							Event sendEvent25 = new Event("action.gaze");
							sendEvent25.putIfNotNull("location", systemAgent.getRelative(systemAgent.getItem(target).location));
							flowRunner.sendEvent(sendEvent25, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 121, 106)));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 120, 70));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class AttendingLocation extends Base {

		final State currentState = this;

		public Location location = null;
		public String mode = asString("default");
		public String speed = asString("medium");

		public void setLocation(Object value) {
			if (value != null) {
				location = (Location) value;
				params.put("location", value);
			}
		}

		public void setMode(Object value) {
			if (value != null) {
				mode = asString(value);
				params.put("mode", value);
			}
		}

		public void setSpeed(Object value) {
			if (value != null) {
				speed = asString(value);
				params.put("speed", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 129
			try {
				EXECUTION: {
					int count = getCount(864237698) + 1;
					incrCount(864237698);
					// Line: 130
					systemAgent.setAttending(systemAgent.getNobody().id);
					// Line: 131
					Event sendEvent26 = new Event("action.gaze");
					sendEvent26.putIfNotNull("mode", mode);
					sendEvent26.putIfNotNull("agent", agent);
					sendEvent26.putIfNotNull("location", systemAgent.getRelative(location));
					sendEvent26.putIfNotNull("speed", speed);
					flowRunner.sendEvent(sendEvent26, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 131, 124)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 129, 12));
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


	private class Asleep extends Base {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 136
			try {
				EXECUTION: {
					int count = getCount(1451270520) + 1;
					incrCount(1451270520);
					// Line: 137
					Event sendEvent27 = new Event("action.gaze");
					sendEvent27.putIfNotNull("mode", "headpose");
					sendEvent27.putIfNotNull("agent", agent);
					sendEvent27.putIfNotNull("location", systemAgent.getRelative(systemAgent.getNobody().getHeadLocation()));
					flowRunner.sendEvent(sendEvent27, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 137, 147)));
					// Line: 138
					Event sendEvent28 = new Event("action.gesture");
					sendEvent28.putIfNotNull("agent", agent);
					sendEvent28.putIfNotNull("name", "sleep");
					flowRunner.sendEvent(sendEvent28, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 138, 67)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 136, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 143
			try {
				count = getCount(1705929636) + 1;
				if (event.triggers("blink")) {
					incrCount(1705929636);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 143, 26));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}
		// Line: 140
		@Override
		public void onexit() {
			int eventResult;
			Event event = new Event("state.exit");
			EXECUTION: {
				// Line: 141
				Event sendEvent29 = new Event("action.gesture");
				sendEvent29.putIfNotNull("agent", agent);
				sendEvent29.putIfNotNull("name", "blink");
				flowRunner.sendEvent(sendEvent29, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 141, 67)));
			}
			super.onexit();
		}

	}


	public class gesture extends State {

		final State currentState = this;
		public String actionId;

		public boolean async = true;
		public String name = asString("smile");
		public String text = null;

		public void setAsync(Object value) {
			if (value != null) {
				async = (boolean) value;
				params.put("async", value);
			}
		}

		public void setName(Object value) {
			if (value != null) {
				name = asString(value);
				params.put("name", value);
			}
		}

		public void setText(Object value) {
			if (value != null) {
				text = asString(value);
				params.put("text", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 151
			try {
				EXECUTION: {
					int count = getCount(1785210046) + 1;
					incrCount(1785210046);
					// Line: 152
					Event sendEvent30 = new Event("action.gesture");
					sendEvent30.putIfNotNull("agent", agent);
					sendEvent30.putIfNotNull("name", name);
					sendEvent30.putIfNotNull("text", text);
					flowRunner.sendEvent(sendEvent30, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 152, 96)));
					actionId = sendEvent30.getId();
					// Line: 153
					if (async) {
						// Line: 154
						flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 154, 14)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 151, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 157
			try {
				count = getCount(125130493) + 1;
				if (event.triggers("monitor.gesture.end")) {
					if (eq(actionId,event.get("action"))) {
						incrCount(125130493);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 158
							flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 158, 13)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 157, 72));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	public class voice extends State {

		final State currentState = this;

		public String name = asString(null);
		public String gender = asString(null);

		public void setName(Object value) {
			if (value != null) {
				name = asString(value);
				params.put("name", value);
			}
		}

		public void setGender(Object value) {
			if (value != null) {
				gender = asString(value);
				params.put("gender", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 165
			try {
				EXECUTION: {
					int count = getCount(824009085) + 1;
					incrCount(824009085);
					// Line: 166
					Event sendEvent31 = new Event("action.voice");
					sendEvent31.putIfNotNull("agent", agent);
					sendEvent31.putIfNotNull("gender", gender);
					sendEvent31.putIfNotNull("name", name);
					flowRunner.sendEvent(sendEvent31, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 166, 80)));
					// Line: 167
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 167, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 165, 12));
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


	public class texture extends State {

		final State currentState = this;

		public String name = asString(null);

		public void setName(Object value) {
			if (value != null) {
				name = asString(value);
				params.put("name", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 173
			try {
				EXECUTION: {
					int count = getCount(344560770) + 1;
					incrCount(344560770);
					// Line: 174
					Event sendEvent32 = new Event("action.face.texture");
					sendEvent32.putIfNotNull("agent", agent);
					sendEvent32.putIfNotNull("name", name);
					flowRunner.sendEvent(sendEvent32, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 174, 69)));
					// Line: 175
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 175, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 173, 12));
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


	public class attend extends State {

		final State currentState = this;

		public String mode = asString("headpose");
		public String target = asString("nobody");
		public Location location = null;
		public Double x = asDouble(null);
		public Double y = asDouble(null);
		public Double z = asDouble(null);
		public String part = asString("head");
		public String speed = asString("medium");

		public void setMode(Object value) {
			if (value != null) {
				mode = asString(value);
				params.put("mode", value);
			}
		}

		public void setTarget(Object value) {
			if (value != null) {
				target = asString(value);
				params.put("target", value);
			}
		}

		public void setLocation(Object value) {
			if (value != null) {
				location = (Location) value;
				params.put("location", value);
			}
		}

		public void setX(Object value) {
			if (value != null) {
				x = asDouble(value);
				params.put("x", value);
			}
		}

		public void setY(Object value) {
			if (value != null) {
				y = asDouble(value);
				params.put("y", value);
			}
		}

		public void setZ(Object value) {
			if (value != null) {
				z = asDouble(value);
				params.put("z", value);
			}
		}

		public void setPart(Object value) {
			if (value != null) {
				part = asString(value);
				params.put("part", value);
			}
		}

		public void setSpeed(Object value) {
			if (value != null) {
				speed = asString(value);
				params.put("speed", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 188
			try {
				EXECUTION: {
					int count = getCount(529116035) + 1;
					incrCount(529116035);
					// Line: 189
					systemAgent.setAttending(target);
					// Line: 190
					if (location != null) {
						// Line: 191
						Event sendEvent33 = new Event("action.attend");
						sendEvent33.putIfNotNull("mode", mode);
						sendEvent33.putIfNotNull("agent", agent);
						sendEvent33.putIfNotNull("location", location);
						sendEvent33.putIfNotNull("speed", speed);
						flowRunner.sendEvent(sendEvent33, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 191, 102)));
						// Line: 192
					} else if (x != null && y != null && z != null) {
						// Line: 193
						Event sendEvent34 = new Event("action.attend");
						sendEvent34.putIfNotNull("mode", mode);
						sendEvent34.putIfNotNull("agent", agent);
						sendEvent34.putIfNotNull("location", new Location(x, y, z));
						sendEvent34.putIfNotNull("speed", speed);
						flowRunner.sendEvent(sendEvent34, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 193, 115)));
						// Line: 194
					} else {
						// Line: 195
						Event sendEvent35 = new Event("action.attend");
						sendEvent35.putIfNotNull("mode", mode);
						sendEvent35.putIfNotNull("agent", agent);
						sendEvent35.putIfNotNull("part", part);
						sendEvent35.putIfNotNull("speed", speed);
						sendEvent35.putIfNotNull("target", target);
						flowRunner.sendEvent(sendEvent35, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 195, 112)));
					}
					// Line: 197
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 197, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 188, 12));
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


	public class attendNobody extends State {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 202
			try {
				EXECUTION: {
					int count = getCount(1937962514) + 1;
					incrCount(1937962514);
					// Line: 203
					systemAgent.setAttending("nobody");
					// Line: 204
					Event sendEvent36 = new Event("action.attend");
					sendEvent36.putIfNotNull("agent", agent);
					sendEvent36.putIfNotNull("target", "nobody");
					flowRunner.sendEvent(sendEvent36, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 204, 69)));
					// Line: 205
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 205, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 202, 12));
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


	public class attendRandom extends State {

		final State currentState = this;
		public String random = asString(systemAgent.getRandomUser().id);

		public String mode = asString("headpose");
		public String speed = asString("medium");

		public void setMode(Object value) {
			if (value != null) {
				mode = asString(value);
				params.put("mode", value);
			}
		}

		public void setSpeed(Object value) {
			if (value != null) {
				speed = asString(value);
				params.put("speed", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 213
			try {
				EXECUTION: {
					int count = getCount(1254526270) + 1;
					incrCount(1254526270);
					// Line: 214
					systemAgent.setAttending(random);
					// Line: 215
					Event sendEvent37 = new Event("action.attend");
					sendEvent37.putIfNotNull("mode", mode);
					sendEvent37.putIfNotNull("agent", agent);
					sendEvent37.putIfNotNull("speed", speed);
					sendEvent37.putIfNotNull("target", random);
					flowRunner.sendEvent(sendEvent37, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 215, 97)));
					// Line: 216
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 216, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 213, 12));
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


	public class attendOther extends State {

		final State currentState = this;
		public String other = asString(systemAgent.getOtherUser().id);

		public String mode = asString("headpose");
		public String speed = asString("medium");

		public void setMode(Object value) {
			if (value != null) {
				mode = asString(value);
				params.put("mode", value);
			}
		}

		public void setSpeed(Object value) {
			if (value != null) {
				speed = asString(value);
				params.put("speed", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 224
			try {
				EXECUTION: {
					int count = getCount(1971489295) + 1;
					incrCount(1971489295);
					// Line: 225
					systemAgent.setAttending(other);
					// Line: 226
					Event sendEvent38 = new Event("action.attend");
					sendEvent38.putIfNotNull("mode", mode);
					sendEvent38.putIfNotNull("agent", agent);
					sendEvent38.putIfNotNull("speed", speed);
					sendEvent38.putIfNotNull("target", other);
					flowRunner.sendEvent(sendEvent38, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 226, 96)));
					// Line: 227
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 227, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 224, 12));
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


	public class attendAll extends State {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 232
			try {
				EXECUTION: {
					int count = getCount(1476011703) + 1;
					incrCount(1476011703);
					// Line: 233
					if (systemAgent.hasManyUsers()) {
						// Line: 234
						systemAgent.setAttendingAll();
						// Line: 235
						Event sendEvent39 = new Event("action.attend.all");
						sendEvent39.putIfNotNull("agent", agent);
						flowRunner.sendEvent(sendEvent39, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 235, 54)));
					}
					// Line: 237
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 237, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 232, 12));
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


	public class fallAsleep extends State {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 242
			try {
				EXECUTION: {
					int count = getCount(1279149968) + 1;
					incrCount(1279149968);
					// Line: 243
					systemAgent.setAttending("nobody");
					// Line: 244
					Event sendEvent40 = new Event("action.attend.asleep");
					sendEvent40.putIfNotNull("agent", agent);
					flowRunner.sendEvent(sendEvent40, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 244, 56)));
					// Line: 245
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 245, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 242, 12));
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


	public class prompt extends listen {

		final State currentState = this;

		public String text = null;

		public void setText(Object value) {
			if (value != null) {
				text = asString(value);
				params.put("text", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 251
			try {
				EXECUTION: {
					int count = getCount(1689843956) + 1;
					incrCount(1689843956);
					// Line: 252
					Event sendEvent41 = new Event("action.speech");
					sendEvent41.putIfNotNull("agent", agent);
					sendEvent41.putIfNotNull("text", text);
					flowRunner.sendEvent(sendEvent41, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 252, 64)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 251, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 254
			try {
				count = getCount(977993101) + 1;
				if (event.triggers("monitor.speech.start")) {
					incrCount(977993101);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 255
						Event sendEvent42 = new Event("action.listen");
						sendEvent42.putIfNotNull("endSilTimeout", endSil);
						sendEvent42.putIfNotNull("context", context);
						sendEvent42.putIfNotNull("noSpeechTimeout", timeout + asInteger(event.get("length")));
						flowRunner.sendEvent(sendEvent42, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 255, 156)));
						listenActionId = sendEvent42.getId();
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 254, 40));
			}
			// Line: 257
			try {
				count = getCount(859417998) + 1;
				if (event.triggers("sense.user.speech.start")) {
					if (eq(event.get("speakers"), 1)) {
						incrCount(859417998);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 258
							Event sendEvent43 = new Event("action.speech.stop");
							flowRunner.sendEvent(sendEvent43, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 258, 38)));
							// Line: 259
							eventResult = EVENT_IGNORED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 257, 72));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	public class stopSpeaking extends State {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 264
			try {
				EXECUTION: {
					int count = getCount(1013423070) + 1;
					incrCount(1013423070);
					// Line: 265
					Event sendEvent44 = new Event("action.speech.stop");
					sendEvent44.putIfNotNull("agent", agent);
					flowRunner.sendEvent(sendEvent44, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 265, 54)));
					// Line: 266
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 266, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 264, 12));
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


	public class say extends State {

		final State currentState = this;
		public String action;

		public String text = null;
		public boolean async = false;
		public boolean ifsilent = false;
		public String gesture = asString("brow_raise");
		public String display = asString(null);
		public String audio = asString(null);
		public boolean abort = false;

		public void setText(Object value) {
			if (value != null) {
				text = asString(value);
				params.put("text", value);
			}
		}

		public void setAsync(Object value) {
			if (value != null) {
				async = (boolean) value;
				params.put("async", value);
			}
		}

		public void setIfsilent(Object value) {
			if (value != null) {
				ifsilent = (boolean) value;
				params.put("ifsilent", value);
			}
		}

		public void setGesture(Object value) {
			if (value != null) {
				gesture = asString(value);
				params.put("gesture", value);
			}
		}

		public void setDisplay(Object value) {
			if (value != null) {
				display = asString(value);
				params.put("display", value);
			}
		}

		public void setAudio(Object value) {
			if (value != null) {
				audio = asString(value);
				params.put("audio", value);
			}
		}

		public void setAbort(Object value) {
			if (value != null) {
				abort = (boolean) value;
				params.put("abort", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 279
			try {
				EXECUTION: {
					int count = getCount(1451043227) + 1;
					incrCount(1451043227);
					// Line: 280
					if (speechTextProcessor != null) {
						// Line: 281
						text = speechTextProcessor.process(text);
						display = speechTextProcessor.process(display);
					}
					// Line: 286
					display = SystemAgent.processDisplay(text, display);
					// Line: 295
					Event sendEvent45 = new Event("action.speech");
					sendEvent45.putIfNotNull("async", async);
					sendEvent45.putIfNotNull("agent", agent);
					sendEvent45.putIfNotNull("abort", abort);
					sendEvent45.putIfNotNull("display", display);
					sendEvent45.putIfNotNull("text", text);
					sendEvent45.putIfNotNull("audio", audio);
					sendEvent45.putIfNotNull("ifsilent", ifsilent);
					flowRunner.sendEvent(sendEvent45, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 295, 23)));
					action = sendEvent45.getId();
					// Line: 296
					gestures.putIfNotNull("" + action, gesture);
					// Line: 297
					if (async) {
						// Line: 298
						flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 298, 14)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 279, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 301
			try {
				count = getCount(1058025095) + 1;
				if (event.triggers("monitor.speech.done")) {
					if (eq(event.get("agent"), agent)) {
						incrCount(1058025095);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 302
							Event returnEvent46 = new Event("monitor.speech.done");
							returnEvent46.putIfNotNull("agent", agent);
							flowThread.returnFromCall(this, returnEvent46, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 302, 57)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 301, 69));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	public class listen extends State {

		final State currentState = this;
		public MultiSpeech multispeech = new MultiSpeech(systemAgent);
		public String listenActionId = asString("");

		public int timeout = asInteger(8000);
		public int endSil = asInteger(700);
		public int nbest = asInteger(1);
		public String context = null;

		public void setTimeout(Object value) {
			if (value != null) {
				timeout = asInteger(value);
				params.put("timeout", value);
			}
		}

		public void setEndSil(Object value) {
			if (value != null) {
				endSil = asInteger(value);
				params.put("endSil", value);
			}
		}

		public void setNbest(Object value) {
			if (value != null) {
				nbest = asInteger(value);
				params.put("nbest", value);
			}
		}

		public void setContext(Object value) {
			if (value != null) {
				context = asString(value);
				params.put("context", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 315
			try {
				EXECUTION: {
					int count = getCount(1156060786) + 1;
					incrCount(1156060786);
					// Line: 316
					Event sendEvent47 = new Event("action.listen");
					sendEvent47.putIfNotNull("endSilTimeout", endSil);
					sendEvent47.putIfNotNull("context", context);
					sendEvent47.putIfNotNull("noSpeechTimeout", timeout);
					sendEvent47.putIfNotNull("nbest", nbest);
					flowRunner.sendEvent(sendEvent47, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 316, 146)));
					listenActionId = sendEvent47.getId();
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 315, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 318
			try {
				count = getCount(1286084959) + 1;
				if (event.triggers("sense.speech.start")) {
					if (eq(event.get("action"), listenActionId)) {
						incrCount(1286084959);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 319
							String agent = systemAgent.getUserId(event);
							// Line: 320
							boolean isAttendingSystem = systemAgent.getUser(agent).isAttending(systemAgent.id);
							// Line: 321
							multispeech.addStart(agent, isAttendingSystem);
							// Line: 322
							Event sendEvent48 = new Event("sense.user.speech.start");
							sendEvent48.putIfNotNull("agent", systemAgent.id);
							sendEvent48.putIfNotNull("attsys", isAttendingSystem);
							sendEvent48.putIfNotNull("speakers", multispeech.speakers);
							sendEvent48.putIfNotNull("action", listenActionId);
							sendEvent48.putIfNotNull("sensor", event.get("sensor"));
							sendEvent48.putIfNotNull("user", agent);
							flowRunner.sendEvent(sendEvent48, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 322, 196)));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 318, 78));
			}
			// Line: 329
			try {
				count = getCount(1225616405) + 1;
				if (event.triggers("sense.user.attend")) {
					if (eq(event.get("target"), systemAgent.id) && multispeech.hasStarted(asString(event.get("user")))) {
						incrCount(1225616405);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 330
							multispeech.attendingSystem(asString(event.get("agent")));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 329, 126));
			}
			// Line: 332
			try {
				count = getCount(1151020327) + 1;
				if (event.triggers("sense.speech.end")) {
					if (eq(event.get("action"), listenActionId)) {
						incrCount(1151020327);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 333
							String agent = systemAgent.getUserId(event);
							// Line: 334
							multispeech.speakers--;
							// Line: 335
							Event sendEvent49 = new Event("sense.user.speech.end");
							sendEvent49.putIfNotNull("agent", systemAgent.id);
							sendEvent49.putIfNotNull("attsys", multispeech.someAttendingSystem());
							sendEvent49.putIfNotNull("speakers", multispeech.speakers);
							sendEvent49.putIfNotNull("action", listenActionId);
							sendEvent49.putIfNotNull("sensor", event.get("sensor"));
							sendEvent49.putIfNotNull("user", agent);
							flowRunner.sendEvent(sendEvent49, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 335, 210)));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 332, 76));
			}
			// Line: 337
			try {
				count = getCount(2080166188) + 1;
				if (event.triggers("sense.speech.rec**")) {
					if (eq(event.get("action"), listenActionId)) {
						incrCount(2080166188);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 338
							multispeech.addRec(systemAgent.getUserId(event), event);
							// Line: 339
							if (multispeech.runningRecognizers == 0) {
								// Line: 340
								Event result = multispeech.getEvent();
								// Line: 341
								Event returnEvent50 = new Event(result.getName());
								returnEvent50.copyParams(result);
								flowThread.returnFromCall(this, returnEvent50, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 341, 28)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 337, 78));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}
		// Line: 344
		@Override
		public void onexit() {
			int eventResult;
			Event event = new Event("state.exit");
			EXECUTION: {
				// Line: 345
				Event sendEvent51 = new Event("action.listen.stop");
				sendEvent51.putIfNotNull("action", listenActionId);
				flowRunner.sendEvent(sendEvent51, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 345, 64)));
			}
			super.onexit();
		}

	}


}
