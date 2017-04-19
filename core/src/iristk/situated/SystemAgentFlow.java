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
			// Line: 53
			count = getCount(914424520) + 1;
			if (event.triggers("timer_914424520")) {
				incrCount(914424520);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					// Line: 54
					if (systemAgent.shouldBlink()) {
						// Line: 55
						Event raiseEvent9 = new Event("blink");
						if (flowThread.raiseEvent(raiseEvent9, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 55, 27))) == State.EVENT_ABORTED) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
					}
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			// Line: 59
			count = getCount(1100439041) + 1;
			if (event.triggers("timer_1100439041")) {
				incrCount(1100439041);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					// Line: 60
					if (systemAgent.shouldPerformRandomMovements()) {
						// Line: 61
						Event sendEvent10 = new Event("action.face.param.adj");
						sendEvent10.putIfNotNull("name", "BROW_UP_LEFT");
						sendEvent10.putIfNotNull("value", Math.max(0, 0.4 + 2*xfilter1.flow(random.nextDouble() - 0.5)));
						flowRunner.sendEvent(sendEvent10, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 61, 139)));
						// Line: 62
						Event sendEvent11 = new Event("action.face.param.adj");
						sendEvent11.putIfNotNull("name", "BROW_UP_RIGHT");
						sendEvent11.putIfNotNull("value", Math.max(0, 0.4 + 2*xfilter2.flow(random.nextDouble() - 0.5)));
						flowRunner.sendEvent(sendEvent11, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 62, 140)));
						// Line: 63
						Event sendEvent12 = new Event("action.face.param.adj");
						sendEvent12.putIfNotNull("name", "SMILE_CLOSED");
						sendEvent12.putIfNotNull("value", Math.max(0, 0.4 + 2*xfilter3.flow(random.nextDouble() - 0.5)));
						flowRunner.sendEvent(sendEvent12, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 63, 139)));
					}
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			// Line: 66
			try {
				count = getCount(32374789) + 1;
				if (event.triggers("blink")) {
					incrCount(32374789);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 67
						Event sendEvent13 = new Event("action.gesture");
						sendEvent13.putIfNotNull("agent", agent);
						sendEvent13.putIfNotNull("name", "blink");
						flowRunner.sendEvent(sendEvent13, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 67, 67)));
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 66, 25));
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
			// Line: 72
			try {
				EXECUTION: {
					int count = getCount(1865127310) + 1;
					incrCount(1865127310);
					// Line: 73
					Event sendEvent14 = new Event("action.gaze");
					sendEvent14.putIfNotNull("mode", "headpose");
					sendEvent14.putIfNotNull("agent", agent);
					sendEvent14.putIfNotNull("location", systemAgent.getRelative(systemAgent.getNobody().getHeadLocation()));
					flowRunner.sendEvent(sendEvent14, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 73, 147)));
					// Line: 74
					Event sendEvent15 = new Event("monitor.attend");
					sendEvent15.putIfNotNull("agent", agent);
					sendEvent15.putIfNotNull("target", "nobody");
					flowRunner.sendEvent(sendEvent15, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 74, 70)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 72, 12));
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
			// Line: 81
			try {
				EXECUTION: {
					int count = getCount(474675244) + 1;
					incrCount(474675244);
					// Line: 82
					Event sendEvent16 = new Event("action.gaze");
					sendEvent16.putIfNotNull("mode", "headpose");
					sendEvent16.putIfNotNull("agent", agent);
					sendEvent16.putIfNotNull("location", nobody);
					flowRunner.sendEvent(sendEvent16, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 82, 87)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 81, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 84
			count = getCount(212628335) + 1;
			if (event.triggers("timer_212628335")) {
				incrCount(212628335);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					// Line: 85
					dir = -dir;
					// Line: 86
					Event sendEvent17 = new Event("action.gaze");
					sendEvent17.putIfNotNull("mode", "eyes");
					sendEvent17.putIfNotNull("location", nobody.add(new Location(0.3 * dir, 0, 0)));
					flowRunner.sendEvent(sendEvent17, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 86, 102)));
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
			// Line: 94
			try {
				EXECUTION: {
					int count = getCount(1993134103) + 1;
					incrCount(1993134103);
					// Line: 95
					Event raiseEvent18 = new Event("adjustHeadPose");
					if (flowThread.raiseEvent(raiseEvent18, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 95, 35))) == State.EVENT_ABORTED) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 96
					Event sendEvent19 = new Event("monitor.attend.all");
					flowRunner.sendEvent(sendEvent19, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 96, 38)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 94, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 98
			try {
				count = getCount(1130478920) + 1;
				if (event.triggers("adjustHeadPose")) {
					incrCount(1130478920);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 99
						Location newMiddle = Agent.getMiddleLocation(systemAgent.getUsers(select));
						// Line: 100
						if (middle == null || newMiddle.distance(middle) > 0.2) {
							// Line: 101
							middle = newMiddle;
							// Line: 102
							Event sendEvent20 = new Event("action.gaze");
							sendEvent20.putIfNotNull("mode", "headpose");
							sendEvent20.putIfNotNull("location", systemAgent.getRelative(middle));
							flowRunner.sendEvent(sendEvent20, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 102, 97)));
						}
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 98, 34));
			}
			// Line: 105
			count = getCount(1982791261) + 1;
			if (event.triggers("timer_1982791261")) {
				incrCount(1982791261);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					// Line: 106
					Event raiseEvent21 = new Event("adjustHeadPose");
					if (flowThread.raiseEvent(raiseEvent21, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 106, 35))) == State.EVENT_ABORTED) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 107
					gazeTarget = systemAgent.getOtherUserThan(gazeTarget.id, select);
					// Line: 108
					Event sendEvent22 = new Event("action.gaze");
					sendEvent22.putIfNotNull("mode", "eyes");
					sendEvent22.putIfNotNull("location", systemAgent.getRelative(gazeTarget.getHeadLocation()));
					flowRunner.sendEvent(sendEvent22, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 108, 114)));
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
			// Line: 117
			try {
				EXECUTION: {
					int count = getCount(1521118594) + 1;
					incrCount(1521118594);
					// Line: 118
					user = systemAgent.getUser(target);
					// Line: 119
					Event sendEvent23 = new Event("action.gaze");
					sendEvent23.putIfNotNull("mode", mode);
					sendEvent23.putIfNotNull("agent", agent);
					sendEvent23.putIfNotNull("location", systemAgent.getRelative(user.getHeadLocation()));
					sendEvent23.putIfNotNull("speed", speed);
					flowRunner.sendEvent(sendEvent23, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 119, 138)));
					// Line: 120
					if (eq(mode,"headpose")) {
						// Line: 121
						mode = "default";
					}
					// Line: 123
					Event sendEvent24 = new Event("monitor.attend");
					sendEvent24.putIfNotNull("agent", agent);
					sendEvent24.putIfNotNull("target", target);
					flowRunner.sendEvent(sendEvent24, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 123, 68)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 117, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 125
			try {
				count = getCount(1192108080) + 1;
				if (event.triggers("sense.user.move")) {
					if (eq(event.get("agent"), agent) && event.has("" + user.id + ":head:location")) {
						incrCount(1192108080);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 126
							Event sendEvent25 = new Event("action.gaze");
							sendEvent25.putIfNotNull("mode", mode);
							sendEvent25.putIfNotNull("agent", agent);
							sendEvent25.putIfNotNull("location", systemAgent.getRelative((Location)event.get("" + user.id + ":head:location")));
							flowRunner.sendEvent(sendEvent25, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 126, 139)));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 125, 100));
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
			// Line: 134
			try {
				EXECUTION: {
					int count = getCount(608188624) + 1;
					incrCount(608188624);
					// Line: 135
					if (systemAgent.hasItem(target)) {
						// Line: 136
						Event sendEvent26 = new Event("action.gaze");
						sendEvent26.putIfNotNull("mode", mode);
						sendEvent26.putIfNotNull("location", systemAgent.getRelative(systemAgent.getItem(target).location));
						sendEvent26.putIfNotNull("speed", speed);
						flowRunner.sendEvent(sendEvent26, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 136, 137)));
					}
					// Line: 138
					Event sendEvent27 = new Event("monitor.attend");
					sendEvent27.putIfNotNull("target", target);
					flowRunner.sendEvent(sendEvent27, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 138, 52)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 134, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 140
			try {
				count = getCount(1297685781) + 1;
				if (event.triggers("sense.item.move")) {
					if (systemAgent.hasItem(target)) {
						incrCount(1297685781);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 141
							Event sendEvent28 = new Event("action.gaze");
							sendEvent28.putIfNotNull("location", systemAgent.getRelative(systemAgent.getItem(target).location));
							flowRunner.sendEvent(sendEvent28, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 141, 106)));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 140, 70));
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
			// Line: 149
			try {
				EXECUTION: {
					int count = getCount(2036368507) + 1;
					incrCount(2036368507);
					// Line: 150
					systemAgent.setAttending(systemAgent.getNobody().id);
					// Line: 151
					Event sendEvent29 = new Event("action.gaze");
					sendEvent29.putIfNotNull("mode", mode);
					sendEvent29.putIfNotNull("agent", agent);
					sendEvent29.putIfNotNull("location", systemAgent.getRelative(location));
					sendEvent29.putIfNotNull("speed", speed);
					flowRunner.sendEvent(sendEvent29, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 151, 124)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 149, 12));
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
			// Line: 156
			try {
				EXECUTION: {
					int count = getCount(739498517) + 1;
					incrCount(739498517);
					// Line: 157
					Event sendEvent30 = new Event("action.gaze");
					sendEvent30.putIfNotNull("mode", "headpose");
					sendEvent30.putIfNotNull("agent", agent);
					sendEvent30.putIfNotNull("location", systemAgent.getRelative(systemAgent.getNobody().getHeadLocation()));
					flowRunner.sendEvent(sendEvent30, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 157, 147)));
					// Line: 158
					Event sendEvent31 = new Event("action.gesture");
					sendEvent31.putIfNotNull("agent", agent);
					sendEvent31.putIfNotNull("name", "sleep");
					flowRunner.sendEvent(sendEvent31, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 158, 67)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 156, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 163
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
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 163, 26));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}
		// Line: 160
		@Override
		public void onexit() {
			int eventResult;
			Event event = new Event("state.exit");
			EXECUTION: {
				// Line: 161
				Event sendEvent32 = new Event("action.gesture");
				sendEvent32.putIfNotNull("agent", agent);
				sendEvent32.putIfNotNull("name", "blink");
				flowRunner.sendEvent(sendEvent32, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 161, 67)));
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
			// Line: 171
			try {
				EXECUTION: {
					int count = getCount(285377351) + 1;
					incrCount(285377351);
					// Line: 172
					Event sendEvent33 = new Event("action.gesture");
					sendEvent33.putIfNotNull("agent", agent);
					sendEvent33.putIfNotNull("name", name);
					sendEvent33.putIfNotNull("text", text);
					flowRunner.sendEvent(sendEvent33, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 172, 96)));
					actionId = sendEvent33.getId();
					// Line: 173
					if (async) {
						// Line: 174
						flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 174, 14)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 171, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 177
			try {
				count = getCount(791885625) + 1;
				if (event.triggers("monitor.gesture.end")) {
					if (eq(actionId,event.get("action"))) {
						incrCount(791885625);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 178
							flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 178, 13)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 177, 72));
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
			// Line: 185
			try {
				EXECUTION: {
					int count = getCount(1908153060) + 1;
					incrCount(1908153060);
					// Line: 186
					Event sendEvent34 = new Event("action.voice");
					sendEvent34.putIfNotNull("agent", agent);
					sendEvent34.putIfNotNull("gender", gender);
					sendEvent34.putIfNotNull("name", name);
					flowRunner.sendEvent(sendEvent34, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 186, 80)));
					// Line: 187
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 187, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 185, 12));
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
			// Line: 193
			try {
				EXECUTION: {
					int count = getCount(1627800613) + 1;
					incrCount(1627800613);
					// Line: 194
					Event sendEvent35 = new Event("action.face.texture");
					sendEvent35.putIfNotNull("agent", agent);
					sendEvent35.putIfNotNull("name", name);
					flowRunner.sendEvent(sendEvent35, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 194, 69)));
					// Line: 195
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 195, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 193, 12));
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
			// Line: 208
			try {
				EXECUTION: {
					int count = getCount(1782113663) + 1;
					incrCount(1782113663);
					// Line: 209
					systemAgent.setAttending(target);
					// Line: 210
					if (location != null) {
						// Line: 211
						Event sendEvent36 = new Event("action.attend");
						sendEvent36.putIfNotNull("mode", mode);
						sendEvent36.putIfNotNull("agent", agent);
						sendEvent36.putIfNotNull("location", location);
						sendEvent36.putIfNotNull("speed", speed);
						flowRunner.sendEvent(sendEvent36, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 211, 102)));
						// Line: 212
					} else if (x != null && y != null && z != null) {
						// Line: 213
						Event sendEvent37 = new Event("action.attend");
						sendEvent37.putIfNotNull("mode", mode);
						sendEvent37.putIfNotNull("agent", agent);
						sendEvent37.putIfNotNull("location", new Location(x, y, z));
						sendEvent37.putIfNotNull("speed", speed);
						flowRunner.sendEvent(sendEvent37, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 213, 115)));
						// Line: 214
					} else {
						// Line: 215
						Event sendEvent38 = new Event("action.attend");
						sendEvent38.putIfNotNull("mode", mode);
						sendEvent38.putIfNotNull("agent", agent);
						sendEvent38.putIfNotNull("part", part);
						sendEvent38.putIfNotNull("speed", speed);
						sendEvent38.putIfNotNull("target", target);
						flowRunner.sendEvent(sendEvent38, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 215, 112)));
					}
					// Line: 217
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 217, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 208, 12));
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
			// Line: 222
			try {
				EXECUTION: {
					int count = getCount(99747242) + 1;
					incrCount(99747242);
					// Line: 223
					systemAgent.setAttending("nobody");
					// Line: 224
					Event sendEvent39 = new Event("action.attend");
					sendEvent39.putIfNotNull("agent", agent);
					sendEvent39.putIfNotNull("target", "nobody");
					flowRunner.sendEvent(sendEvent39, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 224, 69)));
					// Line: 225
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 225, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 222, 12));
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
			// Line: 234
			try {
				EXECUTION: {
					int count = getCount(1603195447) + 1;
					incrCount(1603195447);
					// Line: 235
					systemAgent.setAttending(random);
					// Line: 236
					Event sendEvent40 = new Event("action.attend");
					sendEvent40.putIfNotNull("mode", mode);
					sendEvent40.putIfNotNull("agent", agent);
					sendEvent40.putIfNotNull("speed", speed);
					sendEvent40.putIfNotNull("target", random);
					flowRunner.sendEvent(sendEvent40, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 236, 97)));
					// Line: 237
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 237, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 234, 12));
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
			// Line: 246
			try {
				EXECUTION: {
					int count = getCount(668849042) + 1;
					incrCount(668849042);
					// Line: 247
					systemAgent.setAttending(other);
					// Line: 248
					Event sendEvent41 = new Event("action.attend");
					sendEvent41.putIfNotNull("mode", mode);
					sendEvent41.putIfNotNull("agent", agent);
					sendEvent41.putIfNotNull("speed", speed);
					sendEvent41.putIfNotNull("target", other);
					flowRunner.sendEvent(sendEvent41, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 248, 96)));
					// Line: 249
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 249, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 246, 12));
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
			// Line: 255
			try {
				EXECUTION: {
					int count = getCount(429313384) + 1;
					incrCount(429313384);
					// Line: 256
					if (systemAgent.hasManyUsers()) {
						// Line: 257
						systemAgent.setAttendingAll();
						// Line: 258
						Event sendEvent42 = new Event("action.attend.all");
						sendEvent42.putIfNotNull("agent", agent);
						sendEvent42.putIfNotNull("select", select);
						flowRunner.sendEvent(sendEvent42, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 258, 72)));
					}
					// Line: 260
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 260, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 255, 12));
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
			// Line: 265
			try {
				EXECUTION: {
					int count = getCount(142638629) + 1;
					incrCount(142638629);
					// Line: 266
					systemAgent.setAttending("nobody");
					// Line: 267
					Event sendEvent43 = new Event("action.attend.asleep");
					sendEvent43.putIfNotNull("agent", agent);
					flowRunner.sendEvent(sendEvent43, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 267, 56)));
					// Line: 268
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 268, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 265, 12));
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
			// Line: 274
			try {
				EXECUTION: {
					int count = getCount(1555845260) + 1;
					incrCount(1555845260);
					// Line: 275
					Event sendEvent44 = new Event("action.speech");
					sendEvent44.putIfNotNull("agent", agent);
					sendEvent44.putIfNotNull("text", text);
					flowRunner.sendEvent(sendEvent44, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 275, 64)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 274, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 277
			try {
				count = getCount(1761291320) + 1;
				if (event.triggers("monitor.speech.start")) {
					incrCount(1761291320);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 278
						Event sendEvent45 = new Event("action.listen");
						sendEvent45.putIfNotNull("endSilTimeout", endSil);
						sendEvent45.putIfNotNull("context", context);
						sendEvent45.putIfNotNull("noSpeechTimeout", timeout + asInteger(event.get("length")));
						flowRunner.sendEvent(sendEvent45, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 278, 156)));
						listenActionId = sendEvent45.getId();
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 277, 40));
			}
			// Line: 280
			try {
				count = getCount(783286238) + 1;
				if (event.triggers("sense.user.speech.start")) {
					if (eq(event.get("speakers"), 1)) {
						incrCount(783286238);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 281
							Event sendEvent46 = new Event("action.speech.stop");
							flowRunner.sendEvent(sendEvent46, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 281, 38)));
							// Line: 282
							eventResult = EVENT_IGNORED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 280, 72));
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
			// Line: 287
			try {
				EXECUTION: {
					int count = getCount(105704967) + 1;
					incrCount(105704967);
					// Line: 288
					Event sendEvent47 = new Event("action.speech.stop");
					sendEvent47.putIfNotNull("agent", agent);
					flowRunner.sendEvent(sendEvent47, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 288, 54)));
					// Line: 289
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 289, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 287, 12));
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
			// Line: 302
			try {
				EXECUTION: {
					int count = getCount(1156060786) + 1;
					incrCount(1156060786);
					// Line: 303
					if (speechTextProcessor != null) {
						// Line: 304
						text = speechTextProcessor.process(text);
						display = speechTextProcessor.process(display);
					}
					// Line: 309
					display = SystemAgent.processDisplay(text, display);
					// Line: 318
					Event sendEvent48 = new Event("action.speech");
					sendEvent48.putIfNotNull("async", async);
					sendEvent48.putIfNotNull("agent", agent);
					sendEvent48.putIfNotNull("abort", abort);
					sendEvent48.putIfNotNull("display", display);
					sendEvent48.putIfNotNull("text", text);
					sendEvent48.putIfNotNull("audio", audio);
					sendEvent48.putIfNotNull("ifsilent", ifsilent);
					flowRunner.sendEvent(sendEvent48, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 318, 23)));
					action = sendEvent48.getId();
					// Line: 319
					gestures.putIfNotNull("" + action, gesture);
					// Line: 320
					if (async) {
						// Line: 321
						flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 321, 14)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 302, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 324
			try {
				count = getCount(1151020327) + 1;
				if (event.triggers("monitor.speech.done")) {
					if (eq(event.get("agent"), agent)) {
						incrCount(1151020327);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 325
							Event returnEvent49 = new Event("monitor.speech.done");
							returnEvent49.putIfNotNull("agent", agent);
							flowThread.returnFromCall(this, returnEvent49, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 325, 57)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 324, 69));
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
			// Line: 338
			try {
				EXECUTION: {
					int count = getCount(472654579) + 1;
					incrCount(472654579);
					// Line: 339
					Event sendEvent50 = new Event("action.listen");
					sendEvent50.putIfNotNull("endSilTimeout", endSil);
					sendEvent50.putIfNotNull("context", context);
					sendEvent50.putIfNotNull("noSpeechTimeout", timeout);
					sendEvent50.putIfNotNull("nbest", nbest);
					flowRunner.sendEvent(sendEvent50, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 339, 146)));
					listenActionId = sendEvent50.getId();
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 338, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 341
			try {
				count = getCount(870698190) + 1;
				if (event.triggers("sense.speech.start")) {
					if (eq(event.get("action"), listenActionId)) {
						incrCount(870698190);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 342
							String agent = systemAgent.getUserId(event);
							// Line: 343
							boolean isAttendingSystem = systemAgent.getUser(agent).isAttending(systemAgent.id);
							// Line: 344
							multispeech.addStart(agent, isAttendingSystem);
							// Line: 345
							Event sendEvent51 = new Event("sense.user.speech.start");
							sendEvent51.putIfNotNull("agent", systemAgent.id);
							sendEvent51.putIfNotNull("attsys", isAttendingSystem);
							sendEvent51.putIfNotNull("speakers", multispeech.speakers);
							sendEvent51.putIfNotNull("action", listenActionId);
							sendEvent51.putIfNotNull("sensor", event.get("sensor"));
							sendEvent51.putIfNotNull("user", agent);
							flowRunner.sendEvent(sendEvent51, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 345, 196)));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 341, 78));
			}
			// Line: 347
			try {
				count = getCount(1634198) + 1;
				if (event.triggers("sense.user.attend")) {
					if (eq(event.get("target"), systemAgent.id) && multispeech.hasStarted(asString(event.get("user")))) {
						incrCount(1634198);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 348
							multispeech.attendingSystem(asString(event.get("agent")));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 347, 126));
			}
			// Line: 350
			try {
				count = getCount(1989972246) + 1;
				if (event.triggers("sense.speech.end")) {
					if (eq(event.get("action"), listenActionId)) {
						incrCount(1989972246);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 351
							String agent = systemAgent.getUserId(event);
							// Line: 352
							multispeech.speakers--;
							// Line: 353
							Event sendEvent52 = new Event("sense.user.speech.end");
							sendEvent52.putIfNotNull("agent", systemAgent.id);
							sendEvent52.putIfNotNull("attsys", multispeech.someAttendingSystem());
							sendEvent52.putIfNotNull("speakers", multispeech.speakers);
							sendEvent52.putIfNotNull("action", listenActionId);
							sendEvent52.putIfNotNull("sensor", event.get("sensor"));
							sendEvent52.putIfNotNull("user", agent);
							flowRunner.sendEvent(sendEvent52, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 353, 210)));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 350, 76));
			}
			// Line: 355
			try {
				count = getCount(314337396) + 1;
				if (event.triggers("sense.speech.rec**")) {
					if (eq(event.get("action"), listenActionId)) {
						incrCount(314337396);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 356
							multispeech.addRec(systemAgent.getUserId(event), event);
							// Line: 357
							if (multispeech.runningRecognizers == 0) {
								// Line: 358
								Event result = multispeech.getEvent();
								// Line: 359
								Event returnEvent53 = new Event(result.getName());
								returnEvent53.copyParams(result);
								flowThread.returnFromCall(this, returnEvent53, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 359, 28)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 355, 78));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}
		// Line: 362
		@Override
		public void onexit() {
			int eventResult;
			Event event = new Event("state.exit");
			EXECUTION: {
				// Line: 363
				Event sendEvent54 = new Event("action.listen.stop");
				sendEvent54.putIfNotNull("action", listenActionId);
				flowRunner.sendEvent(sendEvent54, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\IrisTK\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 363, 64)));
			}
			super.onexit();
		}

	}


}
