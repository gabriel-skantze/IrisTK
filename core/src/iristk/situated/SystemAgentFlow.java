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
			flowThread.addEventClock(1000, 5000, "timer_1919892312");
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
				count = getCount(1347137144) + 1;
				if (event.triggers("action.attend")) {
					if (eq(event.get("target"),"nobody") && eq(event.get("agent"), agent)) {
						incrCount(1347137144);
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
				count = getCount(1973336893) + 1;
				if (event.triggers("action.attend.nobody")) {
					incrCount(1973336893);
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
				count = getCount(1174290147) + 1;
				if (event.triggers("action.attend.think")) {
					incrCount(1174290147);
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
				count = getCount(1285044316) + 1;
				if (event.triggers("action.attend.asleep")) {
					if (eq(event.get("agent"), agent)) {
						incrCount(1285044316);
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
				count = getCount(1811075214) + 1;
				if (event.triggers("action.attend.all")) {
					if (eq(event.get("agent"), agent)) {
						incrCount(1811075214);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 28
							AttendingAll state4 = new AttendingAll();
							state4.setSelect(event.get("select"));
							flowThread.gotoState(state4, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 28, 56)));
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
				count = getCount(1407343478) + 1;
				if (event.triggers("action.attend")) {
					if (event.has("location") && eq(event.get("agent"), agent)) {
						incrCount(1407343478);
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
				count = getCount(245565335) + 1;
				if (event.triggers("action.attend")) {
					if (event.has("target") && systemAgent.hasUser(asString(event.get("target"))) && eq(event.get("agent"), agent)) {
						incrCount(245565335);
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
				count = getCount(1066376662) + 1;
				if (event.triggers("action.attend")) {
					if (systemAgent.hasItem(asString(event.get("target")))) {
						incrCount(1066376662);
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
				count = getCount(476402209) + 1;
				if (event.triggers("monitor.speech.prominence")) {
					incrCount(476402209);
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
			count = getCount(1919892312) + 1;
			if (event.triggers("timer_1919892312")) {
				incrCount(1919892312);
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
			// Line: 47
			try {
				count = getCount(250075633) + 1;
				if (event.triggers("blink")) {
					incrCount(250075633);
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
					int count = getCount(110718392) + 1;
					incrCount(110718392);
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
			flowThread.addEventClock(500, 500, "timer_32374789");
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 62
			try {
				EXECUTION: {
					int count = getCount(114935352) + 1;
					incrCount(114935352);
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
			count = getCount(32374789) + 1;
			if (event.triggers("timer_32374789")) {
				incrCount(32374789);
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
		public String select = asString(null);

		public void setSelect(Object value) {
			if (value != null) {
				select = asString(value);
				params.put("select", value);
			}
		}

		public Agent gazeTarget = systemAgent.getRandomUser();
		public Location middle;

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
			flowThread.addEventClock(1000, 1000, "timer_2111991224");
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 75
			try {
				EXECUTION: {
					int count = getCount(1651191114) + 1;
					incrCount(1651191114);
					// Line: 76
					Event raiseEvent15 = new Event("adjustHeadPose");
					if (flowThread.raiseEvent(raiseEvent15, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 76, 35))) == State.EVENT_ABORTED) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 77
					Event sendEvent16 = new Event("monitor.attend.all");
					flowRunner.sendEvent(sendEvent16, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 77, 38)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 75, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 79
			try {
				count = getCount(932583850) + 1;
				if (event.triggers("adjustHeadPose")) {
					incrCount(932583850);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 80
						Location newMiddle = Agent.getMiddleLocation(systemAgent.getUsers(select));
						// Line: 81
						if (middle == null || newMiddle.distance(middle) > 0.2) {
							// Line: 82
							middle = newMiddle;
							// Line: 83
							Event sendEvent17 = new Event("action.gaze");
							sendEvent17.putIfNotNull("mode", "headpose");
							sendEvent17.putIfNotNull("location", systemAgent.getRelative(middle));
							flowRunner.sendEvent(sendEvent17, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 83, 97)));
						}
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 79, 34));
			}
			// Line: 86
			count = getCount(2111991224) + 1;
			if (event.triggers("timer_2111991224")) {
				incrCount(2111991224);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					// Line: 87
					Event raiseEvent18 = new Event("adjustHeadPose");
					if (flowThread.raiseEvent(raiseEvent18, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 87, 35))) == State.EVENT_ABORTED) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 88
					gazeTarget = systemAgent.getOtherUserThan(gazeTarget.id, select);
					// Line: 89
					Event sendEvent19 = new Event("action.gaze");
					sendEvent19.putIfNotNull("mode", "eyes");
					sendEvent19.putIfNotNull("location", systemAgent.getRelative(gazeTarget.getHeadLocation()));
					flowRunner.sendEvent(sendEvent19, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 89, 114)));
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
			// Line: 98
			try {
				EXECUTION: {
					int count = getCount(123961122) + 1;
					incrCount(123961122);
					// Line: 99
					user = systemAgent.getUser(target);
					// Line: 100
					Event sendEvent20 = new Event("action.gaze");
					sendEvent20.putIfNotNull("mode", mode);
					sendEvent20.putIfNotNull("agent", agent);
					sendEvent20.putIfNotNull("location", systemAgent.getRelative(user.getHeadLocation()));
					sendEvent20.putIfNotNull("speed", speed);
					flowRunner.sendEvent(sendEvent20, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 100, 138)));
					// Line: 101
					if (eq(mode,"headpose")) {
						// Line: 102
						mode = "default";
					}
					// Line: 104
					Event sendEvent21 = new Event("monitor.attend");
					sendEvent21.putIfNotNull("agent", agent);
					sendEvent21.putIfNotNull("target", target);
					flowRunner.sendEvent(sendEvent21, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 104, 68)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 98, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 106
			try {
				count = getCount(971848845) + 1;
				if (event.triggers("sense.user.move")) {
					if (eq(event.get("agent"), agent) && event.has("" + user.id + ":head:location")) {
						incrCount(971848845);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 107
							Event sendEvent22 = new Event("action.gaze");
							sendEvent22.putIfNotNull("mode", mode);
							sendEvent22.putIfNotNull("agent", agent);
							sendEvent22.putIfNotNull("location", systemAgent.getRelative((Location)event.get("" + user.id + ":head:location")));
							flowRunner.sendEvent(sendEvent22, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 107, 139)));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 106, 100));
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
			// Line: 115
			try {
				EXECUTION: {
					int count = getCount(1940030785) + 1;
					incrCount(1940030785);
					// Line: 116
					if (systemAgent.hasItem(target)) {
						// Line: 117
						Event sendEvent23 = new Event("action.gaze");
						sendEvent23.putIfNotNull("mode", mode);
						sendEvent23.putIfNotNull("location", systemAgent.getRelative(systemAgent.getItem(target).location));
						sendEvent23.putIfNotNull("speed", speed);
						flowRunner.sendEvent(sendEvent23, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 117, 137)));
					}
					// Line: 119
					Event sendEvent24 = new Event("monitor.attend");
					sendEvent24.putIfNotNull("target", target);
					flowRunner.sendEvent(sendEvent24, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 119, 52)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 115, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 121
			try {
				count = getCount(125993742) + 1;
				if (event.triggers("sense.item.move")) {
					if (systemAgent.hasItem(target)) {
						incrCount(125993742);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 122
							Event sendEvent25 = new Event("action.gaze");
							sendEvent25.putIfNotNull("location", systemAgent.getRelative(systemAgent.getItem(target).location));
							flowRunner.sendEvent(sendEvent25, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 122, 106)));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 121, 70));
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
			// Line: 130
			try {
				EXECUTION: {
					int count = getCount(237852351) + 1;
					incrCount(237852351);
					// Line: 131
					systemAgent.setAttending(systemAgent.getNobody().id);
					// Line: 132
					Event sendEvent26 = new Event("action.gaze");
					sendEvent26.putIfNotNull("mode", mode);
					sendEvent26.putIfNotNull("agent", agent);
					sendEvent26.putIfNotNull("location", systemAgent.getRelative(location));
					sendEvent26.putIfNotNull("speed", speed);
					flowRunner.sendEvent(sendEvent26, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 132, 124)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 130, 12));
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
			// Line: 137
			try {
				EXECUTION: {
					int count = getCount(511833308) + 1;
					incrCount(511833308);
					// Line: 138
					Event sendEvent27 = new Event("action.gaze");
					sendEvent27.putIfNotNull("mode", "headpose");
					sendEvent27.putIfNotNull("agent", agent);
					sendEvent27.putIfNotNull("location", systemAgent.getRelative(systemAgent.getNobody().getHeadLocation()));
					flowRunner.sendEvent(sendEvent27, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 138, 147)));
					// Line: 139
					Event sendEvent28 = new Event("action.gesture");
					sendEvent28.putIfNotNull("agent", agent);
					sendEvent28.putIfNotNull("name", "sleep");
					flowRunner.sendEvent(sendEvent28, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 139, 67)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 137, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 144
			try {
				count = getCount(1556956098) + 1;
				if (event.triggers("blink")) {
					incrCount(1556956098);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 144, 26));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}
		// Line: 141
		@Override
		public void onexit() {
			int eventResult;
			Event event = new Event("state.exit");
			EXECUTION: {
				// Line: 142
				Event sendEvent29 = new Event("action.gesture");
				sendEvent29.putIfNotNull("agent", agent);
				sendEvent29.putIfNotNull("name", "blink");
				flowRunner.sendEvent(sendEvent29, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 142, 67)));
			}
			super.onexit();
		}

	}


	public class gesture extends State {

		final State currentState = this;
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

		public String actionId;

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 152
			try {
				EXECUTION: {
					int count = getCount(739498517) + 1;
					incrCount(739498517);
					// Line: 153
					Event sendEvent30 = new Event("action.gesture");
					sendEvent30.putIfNotNull("agent", agent);
					sendEvent30.putIfNotNull("name", name);
					sendEvent30.putIfNotNull("text", text);
					flowRunner.sendEvent(sendEvent30, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 153, 96)));
					actionId = sendEvent30.getId();
					// Line: 154
					if (async) {
						// Line: 155
						flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 155, 14)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 152, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 158
			try {
				count = getCount(991505714) + 1;
				if (event.triggers("monitor.gesture.end")) {
					if (eq(actionId,event.get("action"))) {
						incrCount(991505714);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 159
							flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 159, 13)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 158, 72));
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
			// Line: 166
			try {
				EXECUTION: {
					int count = getCount(1887400018) + 1;
					incrCount(1887400018);
					// Line: 167
					Event sendEvent31 = new Event("action.voice");
					sendEvent31.putIfNotNull("agent", agent);
					sendEvent31.putIfNotNull("gender", gender);
					sendEvent31.putIfNotNull("name", name);
					flowRunner.sendEvent(sendEvent31, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 167, 80)));
					// Line: 168
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 168, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 166, 12));
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
			// Line: 174
			try {
				EXECUTION: {
					int count = getCount(791885625) + 1;
					incrCount(791885625);
					// Line: 175
					Event sendEvent32 = new Event("action.face.texture");
					sendEvent32.putIfNotNull("agent", agent);
					sendEvent32.putIfNotNull("name", name);
					flowRunner.sendEvent(sendEvent32, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 175, 69)));
					// Line: 176
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 176, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 174, 12));
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
			// Line: 189
			try {
				EXECUTION: {
					int count = getCount(2065530879) + 1;
					incrCount(2065530879);
					// Line: 190
					systemAgent.setAttending(target);
					// Line: 191
					if (location != null) {
						// Line: 192
						Event sendEvent33 = new Event("action.attend");
						sendEvent33.putIfNotNull("mode", mode);
						sendEvent33.putIfNotNull("agent", agent);
						sendEvent33.putIfNotNull("location", location);
						sendEvent33.putIfNotNull("speed", speed);
						flowRunner.sendEvent(sendEvent33, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 192, 102)));
						// Line: 193
					} else if (x != null && y != null && z != null) {
						// Line: 194
						Event sendEvent34 = new Event("action.attend");
						sendEvent34.putIfNotNull("mode", mode);
						sendEvent34.putIfNotNull("agent", agent);
						sendEvent34.putIfNotNull("location", new Location(x, y, z));
						sendEvent34.putIfNotNull("speed", speed);
						flowRunner.sendEvent(sendEvent34, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 194, 115)));
						// Line: 195
					} else {
						// Line: 196
						Event sendEvent35 = new Event("action.attend");
						sendEvent35.putIfNotNull("mode", mode);
						sendEvent35.putIfNotNull("agent", agent);
						sendEvent35.putIfNotNull("part", part);
						sendEvent35.putIfNotNull("speed", speed);
						sendEvent35.putIfNotNull("target", target);
						flowRunner.sendEvent(sendEvent35, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 196, 112)));
					}
					// Line: 198
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 198, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 189, 12));
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
			// Line: 203
			try {
				EXECUTION: {
					int count = getCount(242131142) + 1;
					incrCount(242131142);
					// Line: 204
					systemAgent.setAttending("nobody");
					// Line: 205
					Event sendEvent36 = new Event("action.attend");
					sendEvent36.putIfNotNull("agent", agent);
					sendEvent36.putIfNotNull("target", "nobody");
					flowRunner.sendEvent(sendEvent36, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 205, 69)));
					// Line: 206
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 206, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 203, 12));
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
			// Line: 215
			try {
				EXECUTION: {
					int count = getCount(1023714065) + 1;
					incrCount(1023714065);
					// Line: 216
					systemAgent.setAttending(random);
					// Line: 217
					Event sendEvent37 = new Event("action.attend");
					sendEvent37.putIfNotNull("mode", mode);
					sendEvent37.putIfNotNull("agent", agent);
					sendEvent37.putIfNotNull("speed", speed);
					sendEvent37.putIfNotNull("target", random);
					flowRunner.sendEvent(sendEvent37, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 217, 97)));
					// Line: 218
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 218, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 215, 12));
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
			// Line: 227
			try {
				EXECUTION: {
					int count = getCount(1395089624) + 1;
					incrCount(1395089624);
					// Line: 228
					systemAgent.setAttending(other);
					// Line: 229
					Event sendEvent38 = new Event("action.attend");
					sendEvent38.putIfNotNull("mode", mode);
					sendEvent38.putIfNotNull("agent", agent);
					sendEvent38.putIfNotNull("speed", speed);
					sendEvent38.putIfNotNull("target", other);
					flowRunner.sendEvent(sendEvent38, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 229, 96)));
					// Line: 230
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 230, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 227, 12));
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
			// Line: 236
			try {
				EXECUTION: {
					int count = getCount(1330106945) + 1;
					incrCount(1330106945);
					// Line: 237
					if (systemAgent.hasManyUsers()) {
						// Line: 238
						systemAgent.setAttendingAll();
						// Line: 239
						Event sendEvent39 = new Event("action.attend.all");
						sendEvent39.putIfNotNull("agent", agent);
						sendEvent39.putIfNotNull("select", select);
						flowRunner.sendEvent(sendEvent39, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 239, 72)));
					}
					// Line: 241
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 241, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 236, 12));
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
			// Line: 246
			try {
				EXECUTION: {
					int count = getCount(1689843956) + 1;
					incrCount(1689843956);
					// Line: 247
					systemAgent.setAttending("nobody");
					// Line: 248
					Event sendEvent40 = new Event("action.attend.asleep");
					sendEvent40.putIfNotNull("agent", agent);
					flowRunner.sendEvent(sendEvent40, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 248, 56)));
					// Line: 249
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 249, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 246, 12));
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
			// Line: 255
			try {
				EXECUTION: {
					int count = getCount(5592464) + 1;
					incrCount(5592464);
					// Line: 256
					Event sendEvent41 = new Event("action.speech");
					sendEvent41.putIfNotNull("agent", agent);
					sendEvent41.putIfNotNull("text", text);
					flowRunner.sendEvent(sendEvent41, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 256, 64)));
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 255, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 258
			try {
				count = getCount(1013423070) + 1;
				if (event.triggers("monitor.speech.start")) {
					incrCount(1013423070);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 259
						Event sendEvent42 = new Event("action.listen");
						sendEvent42.putIfNotNull("endSilTimeout", endSil);
						sendEvent42.putIfNotNull("context", context);
						sendEvent42.putIfNotNull("noSpeechTimeout", timeout + asInteger(event.get("length")));
						flowRunner.sendEvent(sendEvent42, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 259, 156)));
						listenActionId = sendEvent42.getId();
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 258, 40));
			}
			// Line: 261
			try {
				count = getCount(142638629) + 1;
				if (event.triggers("sense.user.speech.start")) {
					if (eq(event.get("speakers"), 1)) {
						incrCount(142638629);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 262
							Event sendEvent43 = new Event("action.speech.stop");
							flowRunner.sendEvent(sendEvent43, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 262, 38)));
							// Line: 263
							eventResult = EVENT_IGNORED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 261, 72));
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
			// Line: 268
			try {
				EXECUTION: {
					int count = getCount(317983781) + 1;
					incrCount(317983781);
					// Line: 269
					Event sendEvent44 = new Event("action.speech.stop");
					sendEvent44.putIfNotNull("agent", agent);
					flowRunner.sendEvent(sendEvent44, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 269, 54)));
					// Line: 270
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 270, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 268, 12));
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

		public String action;

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 283
			try {
				EXECUTION: {
					int count = getCount(392292416) + 1;
					incrCount(392292416);
					// Line: 284
					if (speechTextProcessor != null) {
						// Line: 285
						text = speechTextProcessor.process(text);
						display = speechTextProcessor.process(display);
					}
					// Line: 290
					display = SystemAgent.processDisplay(text, display);
					// Line: 299
					Event sendEvent45 = new Event("action.speech");
					sendEvent45.putIfNotNull("async", async);
					sendEvent45.putIfNotNull("agent", agent);
					sendEvent45.putIfNotNull("abort", abort);
					sendEvent45.putIfNotNull("display", display);
					sendEvent45.putIfNotNull("text", text);
					sendEvent45.putIfNotNull("audio", audio);
					sendEvent45.putIfNotNull("ifsilent", ifsilent);
					flowRunner.sendEvent(sendEvent45, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 299, 23)));
					action = sendEvent45.getId();
					// Line: 300
					gestures.putIfNotNull("" + action, gesture);
					// Line: 301
					if (async) {
						// Line: 302
						flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 302, 14)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 283, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 305
			try {
				count = getCount(578866604) + 1;
				if (event.triggers("monitor.speech.done")) {
					if (eq(event.get("agent"), agent)) {
						incrCount(578866604);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 306
							Event returnEvent46 = new Event("monitor.speech.done");
							returnEvent46.putIfNotNull("agent", agent);
							flowThread.returnFromCall(this, returnEvent46, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 306, 57)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 305, 69));
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

		public MultiSpeech multispeech = new MultiSpeech(systemAgent);
		public String listenActionId = asString("");

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 319
			try {
				EXECUTION: {
					int count = getCount(124313277) + 1;
					incrCount(124313277);
					// Line: 320
					Event sendEvent47 = new Event("action.listen");
					sendEvent47.putIfNotNull("endSilTimeout", endSil);
					sendEvent47.putIfNotNull("context", context);
					sendEvent47.putIfNotNull("noSpeechTimeout", timeout);
					sendEvent47.putIfNotNull("nbest", nbest);
					flowRunner.sendEvent(sendEvent47, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 320, 146)));
					listenActionId = sendEvent47.getId();
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 319, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 322
			try {
				count = getCount(2101842856) + 1;
				if (event.triggers("sense.speech.start")) {
					if (eq(event.get("action"), listenActionId)) {
						incrCount(2101842856);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 323
							String agent = systemAgent.getUserId(event);
							// Line: 324
							boolean isAttendingSystem = systemAgent.getUser(agent).isAttending(systemAgent.id);
							// Line: 325
							multispeech.addStart(agent, isAttendingSystem);
							// Line: 326
							Event sendEvent48 = new Event("sense.user.speech.start");
							sendEvent48.putIfNotNull("agent", systemAgent.id);
							sendEvent48.putIfNotNull("attsys", isAttendingSystem);
							sendEvent48.putIfNotNull("speakers", multispeech.speakers);
							sendEvent48.putIfNotNull("action", listenActionId);
							sendEvent48.putIfNotNull("sensor", event.get("sensor"));
							sendEvent48.putIfNotNull("user", agent);
							flowRunner.sendEvent(sendEvent48, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 326, 196)));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 322, 78));
			}
			// Line: 328
			try {
				count = getCount(2080166188) + 1;
				if (event.triggers("sense.user.attend")) {
					if (eq(event.get("target"), systemAgent.id) && multispeech.hasStarted(asString(event.get("user")))) {
						incrCount(2080166188);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 329
							multispeech.attendingSystem(asString(event.get("agent")));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 328, 126));
			}
			// Line: 331
			try {
				count = getCount(606548741) + 1;
				if (event.triggers("sense.speech.end")) {
					if (eq(event.get("action"), listenActionId)) {
						incrCount(606548741);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 332
							String agent = systemAgent.getUserId(event);
							// Line: 333
							multispeech.speakers--;
							// Line: 334
							Event sendEvent49 = new Event("sense.user.speech.end");
							sendEvent49.putIfNotNull("agent", systemAgent.id);
							sendEvent49.putIfNotNull("attsys", multispeech.someAttendingSystem());
							sendEvent49.putIfNotNull("speakers", multispeech.speakers);
							sendEvent49.putIfNotNull("action", listenActionId);
							sendEvent49.putIfNotNull("sensor", event.get("sensor"));
							sendEvent49.putIfNotNull("user", agent);
							flowRunner.sendEvent(sendEvent49, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 334, 210)));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 331, 76));
			}
			// Line: 336
			try {
				count = getCount(26117480) + 1;
				if (event.triggers("sense.speech.rec**")) {
					if (eq(event.get("action"), listenActionId)) {
						incrCount(26117480);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 337
							multispeech.addRec(systemAgent.getUserId(event), event);
							// Line: 338
							if (multispeech.runningRecognizers == 0) {
								// Line: 339
								Event result = multispeech.getEvent();
								// Line: 340
								Event returnEvent50 = new Event(result.getName());
								returnEvent50.copyParams(result);
								flowThread.returnFromCall(this, returnEvent50, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 340, 28)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 336, 78));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}
		// Line: 343
		@Override
		public void onexit() {
			int eventResult;
			Event event = new Event("state.exit");
			EXECUTION: {
				// Line: 344
				Event sendEvent51 = new Event("action.listen.stop");
				sendEvent51.putIfNotNull("action", listenActionId);
				flowRunner.sendEvent(sendEvent51, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 344, 64)));
			}
			super.onexit();
		}

	}


}
