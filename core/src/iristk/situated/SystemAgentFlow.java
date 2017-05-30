package iristk.situated;

import java.util.List;
import java.io.File;
import iristk.xml.XmlMarshaller.XMLLocation;
import iristk.system.Event;
import iristk.flow.*;
import iristk.util.Record;
import static iristk.util.Converters.*;
import static iristk.flow.State.*;
import java.util.Random;

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
			flowThread.addEventClock(1000, 5000, "timer_1143839598");
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
			// Line: 18
			try {
				count = getCount(997608398) + 1;
				if (event.triggers("action.attend")) {
					if (eq(event.get("target"),"nobody") && eq(event.get("agent"), agent)) {
						incrCount(997608398);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 19
							Idle state0 = new Idle();
							flowThread.gotoState(state0, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 19, 24)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 18, 93));
			}
			// Line: 21
			try {
				count = getCount(1212899836) + 1;
				if (event.triggers("action.attend.nobody")) {
					incrCount(1212899836);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 22
						Idle state1 = new Idle();
						flowThread.gotoState(state1, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 22, 24)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 21, 40));
			}
			// Line: 24
			try {
				count = getCount(1289696681) + 1;
				if (event.triggers("action.attend.think")) {
					incrCount(1289696681);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 25
						Think state2 = new Think();
						flowThread.gotoState(state2, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 25, 25)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 24, 39));
			}
			// Line: 27
			try {
				count = getCount(1607460018) + 1;
				if (event.triggers("action.attend.asleep")) {
					if (eq(event.get("agent"), agent)) {
						incrCount(1607460018);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 28
							Asleep state3 = new Asleep();
							flowThread.gotoState(state3, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 28, 26)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 27, 70));
			}
			// Line: 30
			try {
				count = getCount(1588970020) + 1;
				if (event.triggers("action.attend.all")) {
					if (eq(event.get("agent"), agent)) {
						incrCount(1588970020);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 31
							AttendingAll state4 = new AttendingAll();
							state4.setSelect(event.get("select"));
							flowThread.gotoState(state4, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 31, 56)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 30, 67));
			}
			// Line: 33
			try {
				count = getCount(1940447180) + 1;
				if (event.triggers("action.attend")) {
					if (event.has("location") && eq(event.get("agent"), agent)) {
						incrCount(1940447180);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 34
							AttendingLocation state5 = new AttendingLocation();
							state5.setMode(event.get("mode"));
							state5.setLocation(event.get("location"));
							state5.setSpeed(event.get("speed"));
							flowThread.gotoState(state5, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 34, 107)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 33, 83));
			}
			// Line: 36
			try {
				count = getCount(2121744517) + 1;
				if (event.triggers("action.attend")) {
					if (event.has("target") && systemAgent.hasUser(asString(event.get("target"))) && eq(event.get("agent"), agent)) {
						incrCount(2121744517);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 37
							AttendingAgent state6 = new AttendingAgent();
							state6.setMode(event.get("mode"));
							state6.setSpeed(event.get("speed"));
							state6.setTarget(event.get("target"));
							flowThread.gotoState(state6, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 37, 100)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 36, 129));
			}
			// Line: 39
			try {
				count = getCount(183264084) + 1;
				if (event.triggers("action.attend")) {
					if (systemAgent.hasItem(asString(event.get("target")))) {
						incrCount(183264084);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 40
							AttendingItem state7 = new AttendingItem();
							state7.setMode(event.get("mode"));
							state7.setSpeed(event.get("speed"));
							state7.setTarget(event.get("target"));
							flowThread.gotoState(state7, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 40, 99)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 39, 84));
			}
			// Line: 42
			try {
				count = getCount(1490180672) + 1;
				if (event.triggers("monitor.speech.prominence")) {
					incrCount(1490180672);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 43
						if (gestures.has("" + event.get("action"))) {
							// Line: 44
							Event sendEvent8 = new Event("action.gesture");
							sendEvent8.putIfNotNull("name", gestures.get("" + event.get("action")));
							flowRunner.sendEvent(sendEvent8, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 44, 68)));
						}
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 42, 46));
			}
			// Line: 47
			count = getCount(1143839598) + 1;
			if (event.triggers("timer_1143839598")) {
				incrCount(1143839598);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					// Line: 48
					Event raiseEvent9 = new Event("blink");
					if (flowThread.raiseEvent(raiseEvent9, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 48, 26))) == State.EVENT_ABORTED) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			// Line: 50
			try {
				count = getCount(358699161) + 1;
				if (event.triggers("blink")) {
					incrCount(358699161);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 51
						Event sendEvent10 = new Event("action.gesture");
						sendEvent10.putIfNotNull("agent", agent);
						sendEvent10.putIfNotNull("name", "blink");
						flowRunner.sendEvent(sendEvent10, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 51, 67)));
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 50, 25));
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
			// Line: 56
			try {
				EXECUTION: {
					int count = getCount(425918570) + 1;
					incrCount(425918570);
					// Line: 57
					Event sendEvent11 = new Event("action.gaze");
					sendEvent11.putIfNotNull("mode", "headpose");
					sendEvent11.putIfNotNull("agent", agent);
					sendEvent11.putIfNotNull("location", systemAgent.getRelative(systemAgent.getNobody().getHeadLocation()));
					flowRunner.sendEvent(sendEvent11, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 57, 147)));
					// Line: 58
					Event sendEvent12 = new Event("monitor.attend");
					sendEvent12.putIfNotNull("agent", agent);
					sendEvent12.putIfNotNull("target", "nobody");
					flowRunner.sendEvent(sendEvent12, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 58, 70)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 56, 12));
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
		public Location nobody = (Location) systemAgent.getRelative(systemAgent.getNobody().getHeadLocation());

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
			flowThread.addEventClock(500, 500, "timer_1973538135");
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 65
			try {
				EXECUTION: {
					int count = getCount(2110121908) + 1;
					incrCount(2110121908);
					// Line: 66
					Event sendEvent13 = new Event("action.gaze");
					sendEvent13.putIfNotNull("mode", "headpose");
					sendEvent13.putIfNotNull("agent", agent);
					sendEvent13.putIfNotNull("location", nobody);
					flowRunner.sendEvent(sendEvent13, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 66, 87)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 65, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 68
			count = getCount(1973538135) + 1;
			if (event.triggers("timer_1973538135")) {
				incrCount(1973538135);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					// Line: 69
					dir = -dir;
					// Line: 70
					Event sendEvent14 = new Event("action.gaze");
					sendEvent14.putIfNotNull("mode", "eyes");
					sendEvent14.putIfNotNull("location", nobody.add(new Location(0.3 * dir, 0, 0)));
					flowRunner.sendEvent(sendEvent14, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 70, 102)));
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
		public String select = asString(null);

		public void setSelect(Object value) {
			if (value != null) {
				select = asString(value);
				params.put("select", value);
			}
		}

		public Agent gazeTarget = (Agent) systemAgent.getRandomUser();
		public Location middle;

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
			flowThread.addEventClock(1000, 1000, "timer_292938459");
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 78
			try {
				EXECUTION: {
					int count = getCount(1586600255) + 1;
					incrCount(1586600255);
					// Line: 79
					Event raiseEvent15 = new Event("adjustHeadPose");
					if (flowThread.raiseEvent(raiseEvent15, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 79, 35))) == State.EVENT_ABORTED) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 80
					Event sendEvent16 = new Event("monitor.attend.all");
					flowRunner.sendEvent(sendEvent16, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 80, 38)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 78, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 82
			try {
				count = getCount(212628335) + 1;
				if (event.triggers("adjustHeadPose")) {
					incrCount(212628335);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 83
						Location newMiddle = Agent.getMiddleLocation(systemAgent.getUsers(select));
						// Line: 84
						if (middle == null || newMiddle.distance(middle) > 0.2) {
							// Line: 85
							middle = newMiddle;
							// Line: 86
							Event sendEvent17 = new Event("action.gaze");
							sendEvent17.putIfNotNull("mode", "headpose");
							sendEvent17.putIfNotNull("location", systemAgent.getRelative(middle));
							flowRunner.sendEvent(sendEvent17, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 86, 97)));
						}
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 82, 34));
			}
			// Line: 89
			count = getCount(292938459) + 1;
			if (event.triggers("timer_292938459")) {
				incrCount(292938459);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					// Line: 90
					Event raiseEvent18 = new Event("adjustHeadPose");
					if (flowThread.raiseEvent(raiseEvent18, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 90, 35))) == State.EVENT_ABORTED) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 91
					gazeTarget = systemAgent.getOtherUserThan(gazeTarget.id, select);
					// Line: 92
					Event sendEvent19 = new Event("action.gaze");
					sendEvent19.putIfNotNull("mode", "eyes");
					sendEvent19.putIfNotNull("location", systemAgent.getRelative(gazeTarget.getHeadLocation()));
					flowRunner.sendEvent(sendEvent19, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 92, 114)));
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

		public Agent user;

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 101
			try {
				EXECUTION: {
					int count = getCount(1227229563) + 1;
					incrCount(1227229563);
					// Line: 102
					user = systemAgent.getUser(target);
					// Line: 103
					Event sendEvent20 = new Event("action.gaze");
					sendEvent20.putIfNotNull("mode", mode);
					sendEvent20.putIfNotNull("agent", agent);
					sendEvent20.putIfNotNull("location", systemAgent.getRelative(user.getHeadLocation()));
					sendEvent20.putIfNotNull("speed", speed);
					flowRunner.sendEvent(sendEvent20, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 103, 138)));
					// Line: 104
					if (eq(mode,"headpose")) {
						// Line: 105
						mode = "default";
					}
					// Line: 107
					Event sendEvent21 = new Event("monitor.attend");
					sendEvent21.putIfNotNull("agent", agent);
					sendEvent21.putIfNotNull("target", target);
					flowRunner.sendEvent(sendEvent21, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 107, 68)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 101, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 109
			try {
				count = getCount(1910163204) + 1;
				if (event.triggers("sense.user.move")) {
					if (eq(event.get("agent"), agent) && event.has("" + user.id + ":head:location")) {
						incrCount(1910163204);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 110
							Event sendEvent22 = new Event("action.gaze");
							sendEvent22.putIfNotNull("mode", mode);
							sendEvent22.putIfNotNull("agent", agent);
							sendEvent22.putIfNotNull("location", systemAgent.getRelative((Location)event.get("" + user.id + ":head:location")));
							flowRunner.sendEvent(sendEvent22, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 110, 139)));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 109, 100));
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
			// Line: 118
			try {
				EXECUTION: {
					int count = getCount(1869997857) + 1;
					incrCount(1869997857);
					// Line: 119
					if (systemAgent.hasItem(target)) {
						// Line: 120
						Event sendEvent23 = new Event("action.gaze");
						sendEvent23.putIfNotNull("mode", mode);
						sendEvent23.putIfNotNull("location", systemAgent.getRelative(systemAgent.getItem(target).location));
						sendEvent23.putIfNotNull("speed", speed);
						flowRunner.sendEvent(sendEvent23, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 120, 137)));
					}
					// Line: 122
					Event sendEvent24 = new Event("monitor.attend");
					sendEvent24.putIfNotNull("target", target);
					flowRunner.sendEvent(sendEvent24, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 122, 52)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 118, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 124
			try {
				count = getCount(1192108080) + 1;
				if (event.triggers("sense.item.move")) {
					if (systemAgent.hasItem(target)) {
						incrCount(1192108080);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 125
							Event sendEvent25 = new Event("action.gaze");
							sendEvent25.putIfNotNull("location", systemAgent.getRelative(systemAgent.getItem(target).location));
							flowRunner.sendEvent(sendEvent25, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 125, 106)));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 124, 70));
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
			// Line: 133
			try {
				EXECUTION: {
					int count = getCount(608188624) + 1;
					incrCount(608188624);
					// Line: 134
					systemAgent.setAttending(systemAgent.getNobody().id);
					// Line: 135
					Event sendEvent26 = new Event("action.gaze");
					sendEvent26.putIfNotNull("mode", mode);
					sendEvent26.putIfNotNull("agent", agent);
					sendEvent26.putIfNotNull("location", systemAgent.getRelative(location));
					sendEvent26.putIfNotNull("speed", speed);
					flowRunner.sendEvent(sendEvent26, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 135, 124)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 133, 12));
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
			// Line: 140
			try {
				EXECUTION: {
					int count = getCount(1297685781) + 1;
					incrCount(1297685781);
					// Line: 141
					Event sendEvent27 = new Event("action.gaze");
					sendEvent27.putIfNotNull("mode", "headpose");
					sendEvent27.putIfNotNull("agent", agent);
					sendEvent27.putIfNotNull("location", systemAgent.getRelative(systemAgent.getNobody().getHeadLocation()));
					flowRunner.sendEvent(sendEvent27, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 141, 147)));
					// Line: 142
					Event sendEvent28 = new Event("action.gesture");
					sendEvent28.putIfNotNull("agent", agent);
					sendEvent28.putIfNotNull("name", "sleep");
					flowRunner.sendEvent(sendEvent28, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 142, 67)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 140, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 147
			try {
				count = getCount(1252585652) + 1;
				if (event.triggers("blink")) {
					incrCount(1252585652);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 147, 26));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}
		// Line: 144
		@Override
		public void onexit() {
			int eventResult;
			Event event = new Event("state.exit");
			EXECUTION: {
				// Line: 145
				Event sendEvent29 = new Event("action.gesture");
				sendEvent29.putIfNotNull("agent", agent);
				sendEvent29.putIfNotNull("name", "blink");
				flowRunner.sendEvent(sendEvent29, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 145, 67)));
			}
			super.onexit();
		}

	}


	public class gesture extends State {

		final State currentState = this;
		public boolean async = (boolean) true;
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

		public String actionId;

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 155
			try {
				EXECUTION: {
					int count = getCount(125130493) + 1;
					incrCount(125130493);
					// Line: 156
					Event sendEvent30 = new Event("action.gesture");
					sendEvent30.putIfNotNull("agent", agent);
					sendEvent30.putIfNotNull("name", name);
					sendEvent30.putIfNotNull("text", text);
					flowRunner.sendEvent(sendEvent30, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 156, 96)));
					actionId = sendEvent30.getId();
					// Line: 157
					if (async) {
						// Line: 158
						flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 158, 14)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 155, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 161
			try {
				count = getCount(385242642) + 1;
				if (event.triggers("monitor.gesture.end")) {
					if (eq(actionId,event.get("action"))) {
						incrCount(385242642);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 162
							flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 162, 13)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 161, 72));
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
			// Line: 169
			try {
				EXECUTION: {
					int count = getCount(285377351) + 1;
					incrCount(285377351);
					// Line: 170
					Event sendEvent31 = new Event("action.voice");
					sendEvent31.putIfNotNull("agent", agent);
					sendEvent31.putIfNotNull("gender", gender);
					sendEvent31.putIfNotNull("name", name);
					flowRunner.sendEvent(sendEvent31, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 170, 80)));
					// Line: 171
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 171, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 169, 12));
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
			// Line: 177
			try {
				EXECUTION: {
					int count = getCount(2001112025) + 1;
					incrCount(2001112025);
					// Line: 178
					Event sendEvent32 = new Event("action.face.texture");
					sendEvent32.putIfNotNull("agent", agent);
					sendEvent32.putIfNotNull("name", name);
					flowRunner.sendEvent(sendEvent32, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 178, 69)));
					// Line: 179
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 179, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 177, 12));
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
		public Location location = (Location) null;
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
			// Line: 192
			try {
				EXECUTION: {
					int count = getCount(697960108) + 1;
					incrCount(697960108);
					// Line: 193
					systemAgent.setAttending(target);
					// Line: 194
					if (location != null) {
						// Line: 195
						Event sendEvent33 = new Event("action.attend");
						sendEvent33.putIfNotNull("mode", mode);
						sendEvent33.putIfNotNull("agent", agent);
						sendEvent33.putIfNotNull("location", location);
						sendEvent33.putIfNotNull("speed", speed);
						flowRunner.sendEvent(sendEvent33, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 195, 102)));
						// Line: 196
					} else if (x != null && y != null && z != null) {
						// Line: 197
						Event sendEvent34 = new Event("action.attend");
						sendEvent34.putIfNotNull("mode", mode);
						sendEvent34.putIfNotNull("agent", agent);
						sendEvent34.putIfNotNull("location", new Location(x, y, z));
						sendEvent34.putIfNotNull("speed", speed);
						flowRunner.sendEvent(sendEvent34, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 197, 115)));
						// Line: 198
					} else {
						// Line: 199
						Event sendEvent35 = new Event("action.attend");
						sendEvent35.putIfNotNull("mode", mode);
						sendEvent35.putIfNotNull("agent", agent);
						sendEvent35.putIfNotNull("part", part);
						sendEvent35.putIfNotNull("speed", speed);
						sendEvent35.putIfNotNull("target", target);
						flowRunner.sendEvent(sendEvent35, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 199, 112)));
					}
					// Line: 201
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 201, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 192, 12));
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
			// Line: 206
			try {
				EXECUTION: {
					int count = getCount(1782113663) + 1;
					incrCount(1782113663);
					// Line: 207
					systemAgent.setAttending("nobody");
					// Line: 208
					Event sendEvent36 = new Event("action.attend");
					sendEvent36.putIfNotNull("agent", agent);
					sendEvent36.putIfNotNull("target", "nobody");
					flowRunner.sendEvent(sendEvent36, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 208, 69)));
					// Line: 209
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 209, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 206, 12));
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
		public String mode = asString("headpose");
		public String speed = asString("medium");
		public String select = asString(null);

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

		public void setSelect(Object value) {
			if (value != null) {
				select = asString(value);
				params.put("select", value);
			}
		}

		public String random = asString(systemAgent.getRandomUser(select).id);

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 218
			try {
				EXECUTION: {
					int count = getCount(2051450519) + 1;
					incrCount(2051450519);
					// Line: 219
					systemAgent.setAttending(random);
					// Line: 220
					Event sendEvent37 = new Event("action.attend");
					sendEvent37.putIfNotNull("mode", mode);
					sendEvent37.putIfNotNull("agent", agent);
					sendEvent37.putIfNotNull("speed", speed);
					sendEvent37.putIfNotNull("target", random);
					flowRunner.sendEvent(sendEvent37, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 220, 97)));
					// Line: 221
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 221, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 218, 12));
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
		public String mode = asString("headpose");
		public String speed = asString("medium");
		public String select = asString(null);

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

		public void setSelect(Object value) {
			if (value != null) {
				select = asString(value);
				params.put("select", value);
			}
		}

		public String other = asString(systemAgent.getOtherUser(select).id);

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 230
			try {
				EXECUTION: {
					int count = getCount(1476011703) + 1;
					incrCount(1476011703);
					// Line: 231
					systemAgent.setAttending(other);
					// Line: 232
					Event sendEvent38 = new Event("action.attend");
					sendEvent38.putIfNotNull("mode", mode);
					sendEvent38.putIfNotNull("agent", agent);
					sendEvent38.putIfNotNull("speed", speed);
					sendEvent38.putIfNotNull("target", other);
					flowRunner.sendEvent(sendEvent38, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 232, 96)));
					// Line: 233
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 233, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 230, 12));
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
		public String select = asString(null);

		public void setSelect(Object value) {
			if (value != null) {
				select = asString(value);
				params.put("select", value);
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
			// Line: 239
			try {
				EXECUTION: {
					int count = getCount(1279149968) + 1;
					incrCount(1279149968);
					// Line: 240
					if (systemAgent.hasManyUsers()) {
						// Line: 241
						systemAgent.setAttendingAll();
						// Line: 242
						Event sendEvent39 = new Event("action.attend.all");
						sendEvent39.putIfNotNull("agent", agent);
						sendEvent39.putIfNotNull("select", select);
						flowRunner.sendEvent(sendEvent39, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 242, 72)));
					}
					// Line: 244
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 244, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 239, 12));
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
			// Line: 249
			try {
				EXECUTION: {
					int count = getCount(766572210) + 1;
					incrCount(766572210);
					// Line: 250
					systemAgent.setAttending("nobody");
					// Line: 251
					Event sendEvent40 = new Event("action.attend.asleep");
					sendEvent40.putIfNotNull("agent", agent);
					flowRunner.sendEvent(sendEvent40, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 251, 56)));
					// Line: 252
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 252, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 249, 12));
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
			// Line: 258
			try {
				EXECUTION: {
					int count = getCount(1830712962) + 1;
					incrCount(1830712962);
					// Line: 259
					Event sendEvent41 = new Event("action.speech");
					sendEvent41.putIfNotNull("agent", agent);
					sendEvent41.putIfNotNull("text", text);
					flowRunner.sendEvent(sendEvent41, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 259, 64)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 258, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 261
			try {
				count = getCount(380936215) + 1;
				if (event.triggers("monitor.speech.start")) {
					incrCount(380936215);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 262
						Event sendEvent42 = new Event("action.listen");
						sendEvent42.putIfNotNull("endSilTimeout", endSil);
						sendEvent42.putIfNotNull("context", context);
						sendEvent42.putIfNotNull("noSpeechTimeout", timeout + asInteger(event.get("length")));
						flowRunner.sendEvent(sendEvent42, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 262, 156)));
						listenActionId = sendEvent42.getId();
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 261, 40));
			}
			// Line: 264
			try {
				count = getCount(707806938) + 1;
				if (event.triggers("sense.user.speech.start")) {
					if (eq(event.get("speakers"), 1)) {
						incrCount(707806938);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 265
							Event sendEvent43 = new Event("action.speech.stop");
							flowRunner.sendEvent(sendEvent43, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 265, 38)));
							// Line: 266
							eventResult = EVENT_IGNORED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 264, 72));
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
			// Line: 271
			try {
				EXECUTION: {
					int count = getCount(987405879) + 1;
					incrCount(987405879);
					// Line: 272
					Event sendEvent44 = new Event("action.speech.stop");
					sendEvent44.putIfNotNull("agent", agent);
					flowRunner.sendEvent(sendEvent44, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 272, 54)));
					// Line: 273
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 273, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 271, 12));
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
		public String text = null;
		public boolean async = (boolean) false;
		public boolean ifsilent = (boolean) false;
		public String gesture = asString("brow_raise");
		public String display = asString(null);
		public String audio = asString(null);
		public boolean abort = (boolean) false;

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

		public String action;

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 286
			try {
				EXECUTION: {
					int count = getCount(1818402158) + 1;
					incrCount(1818402158);
					// Line: 287
					if (speechTextProcessor != null) {
						// Line: 288
						text = speechTextProcessor.process(text);
						display = speechTextProcessor.process(display);
					}
					// Line: 293
					display = SystemAgent.processDisplay(text, display);
					// Line: 302
					Event sendEvent45 = new Event("action.speech");
					sendEvent45.putIfNotNull("async", async);
					sendEvent45.putIfNotNull("agent", agent);
					sendEvent45.putIfNotNull("abort", abort);
					sendEvent45.putIfNotNull("display", display);
					sendEvent45.putIfNotNull("text", text);
					sendEvent45.putIfNotNull("audio", audio);
					sendEvent45.putIfNotNull("ifsilent", ifsilent);
					flowRunner.sendEvent(sendEvent45, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 302, 23)));
					action = sendEvent45.getId();
					// Line: 303
					gestures.putIfNotNull("" + action, gesture);
					// Line: 304
					if (async) {
						// Line: 305
						flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 305, 14)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 286, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 308
			try {
				count = getCount(353842779) + 1;
				if (event.triggers("monitor.speech.done")) {
					if (eq(event.get("agent"), agent)) {
						incrCount(353842779);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 309
							Event returnEvent46 = new Event("monitor.speech.done");
							returnEvent46.putIfNotNull("agent", agent);
							flowThread.returnFromCall(this, returnEvent46, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 309, 57)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 308, 69));
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

		public MultiSpeech multispeech = (MultiSpeech) new MultiSpeech(systemAgent);
		public String listenActionId = asString("");

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 322
			try {
				EXECUTION: {
					int count = getCount(1225616405) + 1;
					incrCount(1225616405);
					// Line: 323
					Event sendEvent47 = new Event("action.listen");
					sendEvent47.putIfNotNull("endSilTimeout", endSil);
					sendEvent47.putIfNotNull("context", context);
					sendEvent47.putIfNotNull("noSpeechTimeout", timeout);
					sendEvent47.putIfNotNull("nbest", nbest);
					flowRunner.sendEvent(sendEvent47, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 323, 146)));
					listenActionId = sendEvent47.getId();
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 322, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 325
			try {
				count = getCount(1151020327) + 1;
				if (event.triggers("sense.speech.start")) {
					if (eq(event.get("action"), listenActionId)) {
						incrCount(1151020327);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 326
							String agent = systemAgent.getUserId(event);
							// Line: 327
							boolean isAttendingSystem = systemAgent.getUser(agent).isAttending(systemAgent.id);
							// Line: 328
							multispeech.addStart(agent, isAttendingSystem);
							// Line: 329
							Event sendEvent48 = new Event("sense.user.speech.start");
							sendEvent48.putIfNotNull("agent", systemAgent.id);
							sendEvent48.putIfNotNull("attsys", isAttendingSystem);
							sendEvent48.putIfNotNull("speakers", multispeech.speakers);
							sendEvent48.putIfNotNull("action", listenActionId);
							sendEvent48.putIfNotNull("sensor", event.get("sensor"));
							sendEvent48.putIfNotNull("user", agent);
							flowRunner.sendEvent(sendEvent48, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 329, 196)));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 325, 78));
			}
			// Line: 331
			try {
				count = getCount(1123225098) + 1;
				if (event.triggers("sense.user.attend")) {
					if (eq(event.get("target"), systemAgent.id) && multispeech.hasStarted(asString(event.get("user")))) {
						incrCount(1123225098);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 332
							multispeech.attendingSystem(asString(event.get("agent")));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 331, 126));
			}
			// Line: 334
			try {
				count = getCount(1528637575) + 1;
				if (event.triggers("sense.speech.end")) {
					if (eq(event.get("action"), listenActionId)) {
						incrCount(1528637575);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 335
							String agent = systemAgent.getUserId(event);
							// Line: 336
							multispeech.speakers--;
							// Line: 337
							Event sendEvent49 = new Event("sense.user.speech.end");
							sendEvent49.putIfNotNull("agent", systemAgent.id);
							sendEvent49.putIfNotNull("attsys", multispeech.someAttendingSystem());
							sendEvent49.putIfNotNull("speakers", multispeech.speakers);
							sendEvent49.putIfNotNull("action", listenActionId);
							sendEvent49.putIfNotNull("sensor", event.get("sensor"));
							sendEvent49.putIfNotNull("user", agent);
							flowRunner.sendEvent(sendEvent49, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 337, 210)));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 334, 76));
			}
			// Line: 339
			try {
				count = getCount(870698190) + 1;
				if (event.triggers("sense.speech.rec**")) {
					if (eq(event.get("action"), listenActionId)) {
						incrCount(870698190);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 340
							multispeech.addRec(systemAgent.getUserId(event), event);
							// Line: 341
							if (multispeech.runningRecognizers == 0) {
								// Line: 342
								Event result = multispeech.getEvent();
								// Line: 343
								Event returnEvent50 = new Event(result.getName());
								returnEvent50.copyParams(result);
								flowThread.returnFromCall(this, returnEvent50, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 343, 28)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 339, 78));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}
		// Line: 346
		@Override
		public void onexit() {
			int eventResult;
			Event event = new Event("state.exit");
			EXECUTION: {
				// Line: 347
				Event sendEvent51 = new Event("action.listen.stop");
				sendEvent51.putIfNotNull("action", listenActionId);
				flowRunner.sendEvent(sendEvent51, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 347, 64)));
			}
			super.onexit();
		}

	}


}
