package iristk.situated;

import java.util.List;
import java.io.File;
import iristk.xml.XmlMarshaller.XMLLocation;
import iristk.system.Event;
import iristk.flow.*;
import iristk.util.Record;
import static iristk.util.Converters.*;
import static iristk.flow.State.*;
import iristk.util.IIRFilter;
import java.util.Random;

public class SystemAgentFlow extends iristk.flow.Flow {

	private SystemAgent systemAgent;
	private String agent;
	private Record gestures;
	private iristk.speech.SpeechTextProcessor speechTextProcessor;
	private IIRFilter xfilter1;
	private IIRFilter xfilter2;
	private IIRFilter xfilter3;
	private Random random;

	private void initVariables() {
		agent = asString(systemAgent.id);
		gestures = asRecord(new Record());
		xfilter1 = (IIRFilter) IIRFilter.newLowPassFilter();
		xfilter2 = (IIRFilter) IIRFilter.newLowPassFilter();
		xfilter3 = (IIRFilter) IIRFilter.newLowPassFilter();
		random = (Random) new Random();
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

	public IIRFilter getXfilter1() {
		return this.xfilter1;
	}

	public void setXfilter1(IIRFilter value) {
		this.xfilter1 = value;
	}

	public IIRFilter getXfilter2() {
		return this.xfilter2;
	}

	public void setXfilter2(IIRFilter value) {
		this.xfilter2 = value;
	}

	public IIRFilter getXfilter3() {
		return this.xfilter3;
	}

	public void setXfilter3(IIRFilter value) {
		this.xfilter3 = value;
	}

	public Random getRandom() {
		return this.random;
	}

	public void setRandom(Random value) {
		this.random = value;
	}

	public SystemAgent getSystemAgent() {
		return this.systemAgent;
	}

	@Override
	public Object getVariable(String name) {
		if (name.equals("agent")) return this.agent;
		if (name.equals("gestures")) return this.gestures;
		if (name.equals("speechTextProcessor")) return this.speechTextProcessor;
		if (name.equals("xfilter1")) return this.xfilter1;
		if (name.equals("xfilter2")) return this.xfilter2;
		if (name.equals("xfilter3")) return this.xfilter3;
		if (name.equals("random")) return this.random;
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
			flowThread.addEventClock(1000, 5000, "timer_914424520");
			flowThread.addEventClock(18, 20, "timer_1100439041");
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
			// Line: 24
			try {
				count = getCount(1289696681) + 1;
				if (event.triggers("action.attend")) {
					if (eq(event.get("target"),"nobody") && eq(event.get("agent"), agent)) {
						incrCount(1289696681);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 25
							Idle state0 = new Idle();
							flowThread.gotoState(state0, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 25, 24)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 24, 93));
			}
			// Line: 27
			try {
				count = getCount(1607460018) + 1;
				if (event.triggers("action.attend.nobody")) {
					incrCount(1607460018);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 28
						Idle state1 = new Idle();
						flowThread.gotoState(state1, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 28, 24)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 27, 40));
			}
			// Line: 30
			try {
				count = getCount(1588970020) + 1;
				if (event.triggers("action.attend.think")) {
					incrCount(1588970020);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 31
						Think state2 = new Think();
						flowThread.gotoState(state2, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 31, 25)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 30, 39));
			}
			// Line: 33
			try {
				count = getCount(1940447180) + 1;
				if (event.triggers("action.attend.asleep")) {
					if (eq(event.get("agent"), agent)) {
						incrCount(1940447180);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 34
							Asleep state3 = new Asleep();
							flowThread.gotoState(state3, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 34, 26)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 33, 70));
			}
			// Line: 36
			try {
				count = getCount(2121744517) + 1;
				if (event.triggers("action.attend.all")) {
					if (eq(event.get("agent"), agent)) {
						incrCount(2121744517);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 37
							AttendingAll state4 = new AttendingAll();
							state4.setSelect(event.get("select"));
							flowThread.gotoState(state4, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 37, 56)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 36, 67));
			}
			// Line: 39
			try {
				count = getCount(183264084) + 1;
				if (event.triggers("action.attend")) {
					if (event.has("location") && eq(event.get("agent"), agent)) {
						incrCount(183264084);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 40
							AttendingLocation state5 = new AttendingLocation();
							state5.setMode(event.get("mode"));
							state5.setLocation(event.get("location"));
							state5.setSpeed(event.get("speed"));
							flowThread.gotoState(state5, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 40, 107)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 39, 83));
			}
			// Line: 42
			try {
				count = getCount(1490180672) + 1;
				if (event.triggers("action.attend")) {
					if (event.has("target") && systemAgent.hasUser(asString(event.get("target"))) && eq(event.get("agent"), agent)) {
						incrCount(1490180672);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 43
							AttendingAgent state6 = new AttendingAgent();
							state6.setMode(event.get("mode"));
							state6.setSpeed(event.get("speed"));
							state6.setTarget(event.get("target"));
							flowThread.gotoState(state6, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 43, 100)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 42, 129));
			}
			// Line: 45
			try {
				count = getCount(1919892312) + 1;
				if (event.triggers("action.attend")) {
					if (systemAgent.hasItem(asString(event.get("target")))) {
						incrCount(1919892312);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 46
							AttendingItem state7 = new AttendingItem();
							state7.setMode(event.get("mode"));
							state7.setSpeed(event.get("speed"));
							state7.setTarget(event.get("target"));
							flowThread.gotoState(state7, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 46, 99)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 45, 84));
			}
			// Line: 48
			try {
				count = getCount(250075633) + 1;
				if (event.triggers("monitor.speech.prominence")) {
					incrCount(250075633);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 49
						if (gestures.has("" + event.get("action"))) {
							// Line: 50
							Event sendEvent8 = new Event("action.gesture");
							sendEvent8.putIfNotNull("name", gestures.get("" + event.get("action")));
							flowRunner.sendEvent(sendEvent8, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 50, 68)));
						}
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 48, 46));
			}
			// Line: 54
			count = getCount(914424520) + 1;
			if (event.triggers("timer_914424520")) {
				incrCount(914424520);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					// Line: 55
					if (systemAgent.shouldBlink()) {
						// Line: 56
						Event raiseEvent9 = new Event("blink");
						if (flowThread.raiseEvent(raiseEvent9, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 56, 27))) == State.EVENT_ABORTED) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
					}
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			// Line: 60
			count = getCount(1100439041) + 1;
			if (event.triggers("timer_1100439041")) {
				incrCount(1100439041);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					// Line: 61
					if (systemAgent.shouldPerformRandomMovements()) {
						// Line: 62
						Event sendEvent10 = new Event("action.face.param.adj");
						sendEvent10.putIfNotNull("name", "BROW_UP_LEFT");
						sendEvent10.putIfNotNull("value", Math.max(0, 0.4 + 2*xfilter1.flow(random.nextDouble() - 0.5)));
						flowRunner.sendEvent(sendEvent10, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 62, 139)));
						// Line: 63
						Event sendEvent11 = new Event("action.face.param.adj");
						sendEvent11.putIfNotNull("name", "BROW_UP_RIGHT");
						sendEvent11.putIfNotNull("value", Math.max(0, 0.4 + 2*xfilter2.flow(random.nextDouble() - 0.5)));
						flowRunner.sendEvent(sendEvent11, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 63, 140)));
						// Line: 64
						Event sendEvent12 = new Event("action.face.param.adj");
						sendEvent12.putIfNotNull("name", "SMILE_CLOSED");
						sendEvent12.putIfNotNull("value", Math.max(0, 0.4 + 2*xfilter3.flow(random.nextDouble() - 0.5)));
						flowRunner.sendEvent(sendEvent12, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 64, 139)));
					}
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			// Line: 67
			try {
				count = getCount(32374789) + 1;
				if (event.triggers("blink")) {
					incrCount(32374789);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 68
						Event sendEvent13 = new Event("action.gesture");
						sendEvent13.putIfNotNull("agent", agent);
						sendEvent13.putIfNotNull("name", "blink");
						flowRunner.sendEvent(sendEvent13, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 68, 67)));
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 67, 25));
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
			// Line: 73
			try {
				EXECUTION: {
					int count = getCount(1865127310) + 1;
					incrCount(1865127310);
					// Line: 74
					Event sendEvent14 = new Event("action.gaze");
					sendEvent14.putIfNotNull("mode", "headpose");
					sendEvent14.putIfNotNull("agent", agent);
					sendEvent14.putIfNotNull("location", systemAgent.getRelative(systemAgent.getNobody().getHeadLocation()));
					flowRunner.sendEvent(sendEvent14, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 74, 147)));
					// Line: 75
					Event sendEvent15 = new Event("monitor.attend");
					sendEvent15.putIfNotNull("agent", agent);
					sendEvent15.putIfNotNull("target", "nobody");
					flowRunner.sendEvent(sendEvent15, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 75, 70)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 73, 12));
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
			flowThread.addEventClock(500, 500, "timer_212628335");
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 82
			try {
				EXECUTION: {
					int count = getCount(474675244) + 1;
					incrCount(474675244);
					// Line: 83
					Event sendEvent16 = new Event("action.gaze");
					sendEvent16.putIfNotNull("mode", "headpose");
					sendEvent16.putIfNotNull("agent", agent);
					sendEvent16.putIfNotNull("location", nobody);
					flowRunner.sendEvent(sendEvent16, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 83, 87)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 82, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 85
			count = getCount(212628335) + 1;
			if (event.triggers("timer_212628335")) {
				incrCount(212628335);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					// Line: 86
					dir = -dir;
					// Line: 87
					Event sendEvent17 = new Event("action.gaze");
					sendEvent17.putIfNotNull("mode", "eyes");
					sendEvent17.putIfNotNull("location", nobody.add(new Location(0.3 * dir, 0, 0)));
					flowRunner.sendEvent(sendEvent17, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 87, 102)));
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
			flowThread.addEventClock(1000, 1000, "timer_1982791261");
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 95
			try {
				EXECUTION: {
					int count = getCount(1993134103) + 1;
					incrCount(1993134103);
					// Line: 96
					Event raiseEvent18 = new Event("adjustHeadPose");
					if (flowThread.raiseEvent(raiseEvent18, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 96, 35))) == State.EVENT_ABORTED) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 97
					Event sendEvent19 = new Event("monitor.attend.all");
					flowRunner.sendEvent(sendEvent19, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 97, 38)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 95, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 99
			try {
				count = getCount(1130478920) + 1;
				if (event.triggers("adjustHeadPose")) {
					incrCount(1130478920);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 100
						Location newMiddle = Agent.getMiddleLocation(systemAgent.getUsers(select));
						// Line: 101
						if (middle == null || newMiddle.distance(middle) > 0.2) {
							// Line: 102
							middle = newMiddle;
							// Line: 103
							Event sendEvent20 = new Event("action.gaze");
							sendEvent20.putIfNotNull("mode", "headpose");
							sendEvent20.putIfNotNull("location", systemAgent.getRelative(middle));
							flowRunner.sendEvent(sendEvent20, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 103, 97)));
						}
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 99, 34));
			}
			// Line: 106
			count = getCount(1982791261) + 1;
			if (event.triggers("timer_1982791261")) {
				incrCount(1982791261);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					// Line: 107
					Event raiseEvent21 = new Event("adjustHeadPose");
					if (flowThread.raiseEvent(raiseEvent21, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 107, 35))) == State.EVENT_ABORTED) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 108
					gazeTarget = systemAgent.getOtherUserThan(gazeTarget.id, select);
					// Line: 109
					Event sendEvent22 = new Event("action.gaze");
					sendEvent22.putIfNotNull("mode", "eyes");
					sendEvent22.putIfNotNull("location", systemAgent.getRelative(gazeTarget.getHeadLocation()));
					flowRunner.sendEvent(sendEvent22, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 109, 114)));
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
			// Line: 118
			try {
				EXECUTION: {
					int count = getCount(1521118594) + 1;
					incrCount(1521118594);
					// Line: 119
					user = systemAgent.getUser(target);
					// Line: 120
					Event sendEvent23 = new Event("action.gaze");
					sendEvent23.putIfNotNull("mode", mode);
					sendEvent23.putIfNotNull("agent", agent);
					sendEvent23.putIfNotNull("location", systemAgent.getRelative(user.getHeadLocation()));
					sendEvent23.putIfNotNull("speed", speed);
					flowRunner.sendEvent(sendEvent23, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 120, 138)));
					// Line: 121
					if (eq(mode,"headpose")) {
						// Line: 122
						mode = "default";
					}
					// Line: 124
					Event sendEvent24 = new Event("monitor.attend");
					sendEvent24.putIfNotNull("agent", agent);
					sendEvent24.putIfNotNull("target", target);
					flowRunner.sendEvent(sendEvent24, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 124, 68)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 118, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 126
			try {
				count = getCount(1192108080) + 1;
				if (event.triggers("sense.user.move")) {
					if (eq(event.get("agent"), agent) && event.has("" + user.id + ":head:location")) {
						incrCount(1192108080);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 127
							Event sendEvent25 = new Event("action.gaze");
							sendEvent25.putIfNotNull("mode", mode);
							sendEvent25.putIfNotNull("agent", agent);
							sendEvent25.putIfNotNull("location", systemAgent.getRelative((Location)event.get("" + user.id + ":head:location")));
							flowRunner.sendEvent(sendEvent25, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 127, 139)));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 126, 100));
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
			// Line: 135
			try {
				EXECUTION: {
					int count = getCount(608188624) + 1;
					incrCount(608188624);
					// Line: 136
					if (systemAgent.hasItem(target)) {
						// Line: 137
						Event sendEvent26 = new Event("action.gaze");
						sendEvent26.putIfNotNull("mode", mode);
						sendEvent26.putIfNotNull("location", systemAgent.getRelative(systemAgent.getItem(target).location));
						sendEvent26.putIfNotNull("speed", speed);
						flowRunner.sendEvent(sendEvent26, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 137, 137)));
					}
					// Line: 139
					Event sendEvent27 = new Event("monitor.attend");
					sendEvent27.putIfNotNull("target", target);
					flowRunner.sendEvent(sendEvent27, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 139, 52)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 135, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 141
			try {
				count = getCount(1297685781) + 1;
				if (event.triggers("sense.item.move")) {
					if (systemAgent.hasItem(target)) {
						incrCount(1297685781);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 142
							Event sendEvent28 = new Event("action.gaze");
							sendEvent28.putIfNotNull("location", systemAgent.getRelative(systemAgent.getItem(target).location));
							flowRunner.sendEvent(sendEvent28, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 142, 106)));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 141, 70));
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
			// Line: 150
			try {
				EXECUTION: {
					int count = getCount(2036368507) + 1;
					incrCount(2036368507);
					// Line: 151
					systemAgent.setAttending(systemAgent.getNobody().id);
					// Line: 152
					Event sendEvent29 = new Event("action.gaze");
					sendEvent29.putIfNotNull("mode", mode);
					sendEvent29.putIfNotNull("agent", agent);
					sendEvent29.putIfNotNull("location", systemAgent.getRelative(location));
					sendEvent29.putIfNotNull("speed", speed);
					flowRunner.sendEvent(sendEvent29, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 152, 124)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 150, 12));
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
			// Line: 157
			try {
				EXECUTION: {
					int count = getCount(739498517) + 1;
					incrCount(739498517);
					// Line: 158
					Event sendEvent30 = new Event("action.gaze");
					sendEvent30.putIfNotNull("mode", "headpose");
					sendEvent30.putIfNotNull("agent", agent);
					sendEvent30.putIfNotNull("location", systemAgent.getRelative(systemAgent.getNobody().getHeadLocation()));
					flowRunner.sendEvent(sendEvent30, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 158, 147)));
					// Line: 159
					Event sendEvent31 = new Event("action.gesture");
					sendEvent31.putIfNotNull("agent", agent);
					sendEvent31.putIfNotNull("name", "sleep");
					flowRunner.sendEvent(sendEvent31, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 159, 67)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 157, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 164
			try {
				count = getCount(385242642) + 1;
				if (event.triggers("blink")) {
					incrCount(385242642);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 164, 26));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}
		// Line: 161
		@Override
		public void onexit() {
			int eventResult;
			Event event = new Event("state.exit");
			EXECUTION: {
				// Line: 162
				Event sendEvent32 = new Event("action.gesture");
				sendEvent32.putIfNotNull("agent", agent);
				sendEvent32.putIfNotNull("name", "blink");
				flowRunner.sendEvent(sendEvent32, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 162, 67)));
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
			// Line: 172
			try {
				EXECUTION: {
					int count = getCount(285377351) + 1;
					incrCount(285377351);
					// Line: 173
					Event sendEvent33 = new Event("action.gesture");
					sendEvent33.putIfNotNull("agent", agent);
					sendEvent33.putIfNotNull("name", name);
					sendEvent33.putIfNotNull("text", text);
					flowRunner.sendEvent(sendEvent33, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 173, 96)));
					actionId = sendEvent33.getId();
					// Line: 174
					if (async) {
						// Line: 175
						flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 175, 14)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 172, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 178
			try {
				count = getCount(791885625) + 1;
				if (event.triggers("monitor.gesture.end")) {
					if (eq(actionId,event.get("action"))) {
						incrCount(791885625);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 179
							flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 179, 13)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 178, 72));
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
			// Line: 186
			try {
				EXECUTION: {
					int count = getCount(1908153060) + 1;
					incrCount(1908153060);
					// Line: 187
					Event sendEvent34 = new Event("action.voice");
					sendEvent34.putIfNotNull("agent", agent);
					sendEvent34.putIfNotNull("gender", gender);
					sendEvent34.putIfNotNull("name", name);
					flowRunner.sendEvent(sendEvent34, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 187, 80)));
					// Line: 188
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 188, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 186, 12));
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
			// Line: 194
			try {
				EXECUTION: {
					int count = getCount(1627800613) + 1;
					incrCount(1627800613);
					// Line: 195
					Event sendEvent35 = new Event("action.face.texture");
					sendEvent35.putIfNotNull("agent", agent);
					sendEvent35.putIfNotNull("name", name);
					flowRunner.sendEvent(sendEvent35, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 195, 69)));
					// Line: 196
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 196, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 194, 12));
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
			// Line: 209
			try {
				EXECUTION: {
					int count = getCount(1782113663) + 1;
					incrCount(1782113663);
					// Line: 210
					systemAgent.setAttending(target);
					// Line: 211
					if (location != null) {
						// Line: 212
						Event sendEvent36 = new Event("action.attend");
						sendEvent36.putIfNotNull("mode", mode);
						sendEvent36.putIfNotNull("agent", agent);
						sendEvent36.putIfNotNull("location", location);
						sendEvent36.putIfNotNull("speed", speed);
						flowRunner.sendEvent(sendEvent36, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 212, 102)));
						// Line: 213
					} else if (x != null && y != null && z != null) {
						// Line: 214
						Event sendEvent37 = new Event("action.attend");
						sendEvent37.putIfNotNull("mode", mode);
						sendEvent37.putIfNotNull("agent", agent);
						sendEvent37.putIfNotNull("location", new Location(x, y, z));
						sendEvent37.putIfNotNull("speed", speed);
						flowRunner.sendEvent(sendEvent37, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 214, 115)));
						// Line: 215
					} else {
						// Line: 216
						Event sendEvent38 = new Event("action.attend");
						sendEvent38.putIfNotNull("mode", mode);
						sendEvent38.putIfNotNull("agent", agent);
						sendEvent38.putIfNotNull("part", part);
						sendEvent38.putIfNotNull("speed", speed);
						sendEvent38.putIfNotNull("target", target);
						flowRunner.sendEvent(sendEvent38, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 216, 112)));
					}
					// Line: 218
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 218, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 209, 12));
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
			// Line: 223
			try {
				EXECUTION: {
					int count = getCount(99747242) + 1;
					incrCount(99747242);
					// Line: 224
					systemAgent.setAttending("nobody");
					// Line: 225
					Event sendEvent39 = new Event("action.attend");
					sendEvent39.putIfNotNull("agent", agent);
					sendEvent39.putIfNotNull("target", "nobody");
					flowRunner.sendEvent(sendEvent39, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 225, 69)));
					// Line: 226
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 226, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 223, 12));
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
			// Line: 235
			try {
				EXECUTION: {
					int count = getCount(1603195447) + 1;
					incrCount(1603195447);
					// Line: 236
					systemAgent.setAttending(random);
					// Line: 237
					Event sendEvent40 = new Event("action.attend");
					sendEvent40.putIfNotNull("mode", mode);
					sendEvent40.putIfNotNull("agent", agent);
					sendEvent40.putIfNotNull("speed", speed);
					sendEvent40.putIfNotNull("target", random);
					flowRunner.sendEvent(sendEvent40, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 237, 97)));
					// Line: 238
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 238, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 235, 12));
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
			// Line: 247
			try {
				EXECUTION: {
					int count = getCount(668849042) + 1;
					incrCount(668849042);
					// Line: 248
					systemAgent.setAttending(other);
					// Line: 249
					Event sendEvent41 = new Event("action.attend");
					sendEvent41.putIfNotNull("mode", mode);
					sendEvent41.putIfNotNull("agent", agent);
					sendEvent41.putIfNotNull("speed", speed);
					sendEvent41.putIfNotNull("target", other);
					flowRunner.sendEvent(sendEvent41, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 249, 96)));
					// Line: 250
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 250, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 247, 12));
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
			// Line: 256
			try {
				EXECUTION: {
					int count = getCount(429313384) + 1;
					incrCount(429313384);
					// Line: 257
					if (systemAgent.hasManyUsers()) {
						// Line: 258
						systemAgent.setAttendingAll();
						// Line: 259
						Event sendEvent42 = new Event("action.attend.all");
						sendEvent42.putIfNotNull("agent", agent);
						sendEvent42.putIfNotNull("select", select);
						flowRunner.sendEvent(sendEvent42, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 259, 72)));
					}
					// Line: 261
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 261, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 256, 12));
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
			// Line: 266
			try {
				EXECUTION: {
					int count = getCount(142638629) + 1;
					incrCount(142638629);
					// Line: 267
					systemAgent.setAttending("nobody");
					// Line: 268
					Event sendEvent43 = new Event("action.attend.asleep");
					sendEvent43.putIfNotNull("agent", agent);
					flowRunner.sendEvent(sendEvent43, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 268, 56)));
					// Line: 269
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 269, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 266, 12));
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
			// Line: 275
			try {
				EXECUTION: {
					int count = getCount(1555845260) + 1;
					incrCount(1555845260);
					// Line: 276
					Event sendEvent44 = new Event("action.speech");
					sendEvent44.putIfNotNull("agent", agent);
					sendEvent44.putIfNotNull("text", text);
					flowRunner.sendEvent(sendEvent44, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 276, 64)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 275, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 278
			try {
				count = getCount(1761291320) + 1;
				if (event.triggers("monitor.speech.start")) {
					incrCount(1761291320);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 279
						Event sendEvent45 = new Event("action.listen");
						sendEvent45.putIfNotNull("endSilTimeout", endSil);
						sendEvent45.putIfNotNull("context", context);
						sendEvent45.putIfNotNull("noSpeechTimeout", timeout + asInteger(event.get("length")));
						flowRunner.sendEvent(sendEvent45, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 279, 156)));
						listenActionId = sendEvent45.getId();
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 278, 40));
			}
			// Line: 281
			try {
				count = getCount(783286238) + 1;
				if (event.triggers("sense.user.speech.start")) {
					if (eq(event.get("speakers"), 1)) {
						incrCount(783286238);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 282
							Event sendEvent46 = new Event("action.speech.stop");
							flowRunner.sendEvent(sendEvent46, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 282, 38)));
							// Line: 283
							eventResult = EVENT_IGNORED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 281, 72));
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
			// Line: 288
			try {
				EXECUTION: {
					int count = getCount(105704967) + 1;
					incrCount(105704967);
					// Line: 289
					Event sendEvent47 = new Event("action.speech.stop");
					sendEvent47.putIfNotNull("agent", agent);
					flowRunner.sendEvent(sendEvent47, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 289, 54)));
					// Line: 290
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 290, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 288, 12));
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
			// Line: 303
			try {
				EXECUTION: {
					int count = getCount(1156060786) + 1;
					incrCount(1156060786);
					// Line: 304
					if (speechTextProcessor != null) {
						// Line: 305
						text = speechTextProcessor.process(text);
						display = speechTextProcessor.process(display);
					}
					// Line: 310
					display = SystemAgent.processDisplay(text, display);
					// Line: 319
					Event sendEvent48 = new Event("action.speech");
					sendEvent48.putIfNotNull("async", async);
					sendEvent48.putIfNotNull("agent", agent);
					sendEvent48.putIfNotNull("abort", abort);
					sendEvent48.putIfNotNull("display", display);
					sendEvent48.putIfNotNull("text", text);
					sendEvent48.putIfNotNull("audio", audio);
					sendEvent48.putIfNotNull("ifsilent", ifsilent);
					flowRunner.sendEvent(sendEvent48, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 319, 23)));
					action = sendEvent48.getId();
					// Line: 320
					gestures.putIfNotNull("" + action, gesture);
					// Line: 321
					if (async) {
						// Line: 322
						flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 322, 14)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 303, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 325
			try {
				count = getCount(1151020327) + 1;
				if (event.triggers("monitor.speech.done")) {
					if (eq(event.get("agent"), agent)) {
						incrCount(1151020327);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 326
							Event returnEvent49 = new Event("monitor.speech.done");
							returnEvent49.putIfNotNull("agent", agent);
							flowThread.returnFromCall(this, returnEvent49, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 326, 57)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 325, 69));
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
			// Line: 339
			try {
				EXECUTION: {
					int count = getCount(472654579) + 1;
					incrCount(472654579);
					// Line: 340
					Event sendEvent50 = new Event("action.listen");
					sendEvent50.putIfNotNull("endSilTimeout", endSil);
					sendEvent50.putIfNotNull("context", context);
					sendEvent50.putIfNotNull("noSpeechTimeout", timeout);
					sendEvent50.putIfNotNull("nbest", nbest);
					flowRunner.sendEvent(sendEvent50, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 340, 146)));
					listenActionId = sendEvent50.getId();
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 339, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 342
			try {
				count = getCount(870698190) + 1;
				if (event.triggers("sense.speech.start")) {
					if (eq(event.get("action"), listenActionId)) {
						incrCount(870698190);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 343
							String agent = systemAgent.getUserId(event);
							// Line: 344
							boolean isAttendingSystem = systemAgent.getUser(agent).isAttending(systemAgent.id);
							// Line: 345
							multispeech.addStart(agent, isAttendingSystem);
							// Line: 346
							Event sendEvent51 = new Event("sense.user.speech.start");
							sendEvent51.putIfNotNull("agent", systemAgent.id);
							sendEvent51.putIfNotNull("attsys", isAttendingSystem);
							sendEvent51.putIfNotNull("speakers", multispeech.speakers);
							sendEvent51.putIfNotNull("action", listenActionId);
							sendEvent51.putIfNotNull("sensor", event.get("sensor"));
							sendEvent51.putIfNotNull("user", agent);
							flowRunner.sendEvent(sendEvent51, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 346, 196)));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 342, 78));
			}
			// Line: 348
			try {
				count = getCount(1634198) + 1;
				if (event.triggers("sense.user.attend")) {
					if (eq(event.get("target"), systemAgent.id) && multispeech.hasStarted(asString(event.get("user")))) {
						incrCount(1634198);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 349
							multispeech.attendingSystem(asString(event.get("agent")));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 348, 126));
			}
			// Line: 351
			try {
				count = getCount(1989972246) + 1;
				if (event.triggers("sense.speech.end")) {
					if (eq(event.get("action"), listenActionId)) {
						incrCount(1989972246);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 352
							String agent = systemAgent.getUserId(event);
							// Line: 353
							multispeech.speakers--;
							// Line: 354
							Event sendEvent52 = new Event("sense.user.speech.end");
							sendEvent52.putIfNotNull("agent", systemAgent.id);
							sendEvent52.putIfNotNull("attsys", multispeech.someAttendingSystem());
							sendEvent52.putIfNotNull("speakers", multispeech.speakers);
							sendEvent52.putIfNotNull("action", listenActionId);
							sendEvent52.putIfNotNull("sensor", event.get("sensor"));
							sendEvent52.putIfNotNull("user", agent);
							flowRunner.sendEvent(sendEvent52, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 354, 210)));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 351, 76));
			}
			// Line: 356
			try {
				count = getCount(314337396) + 1;
				if (event.triggers("sense.speech.rec**")) {
					if (eq(event.get("action"), listenActionId)) {
						incrCount(314337396);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 357
							multispeech.addRec(systemAgent.getUserId(event), event);
							// Line: 358
							if (multispeech.runningRecognizers == 0) {
								// Line: 359
								Event result = multispeech.getEvent();
								// Line: 360
								Event returnEvent53 = new Event(result.getName());
								returnEvent53.copyParams(result);
								flowThread.returnFromCall(this, returnEvent53, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 360, 28)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 356, 78));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}
		// Line: 363
		@Override
		public void onexit() {
			int eventResult;
			Event event = new Event("state.exit");
			EXECUTION: {
				// Line: 364
				Event sendEvent54 = new Event("action.listen.stop");
				sendEvent54.putIfNotNull("action", listenActionId);
				flowRunner.sendEvent(sendEvent54, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 364, 64)));
			}
			super.onexit();
		}

	}


}
