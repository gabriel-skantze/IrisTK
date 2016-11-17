package iristk.app.quiz;

import java.util.List;
import java.io.File;
import iristk.xml.XmlMarshaller.XMLLocation;
import iristk.system.Event;
import iristk.flow.*;
import iristk.util.Record;
import static iristk.util.Converters.*;
import static iristk.flow.State.*;

public class QuizFlow extends iristk.flow.Flow {

	private QuestionSet questions;
	private iristk.situated.SystemAgentFlow agent;
	private iristk.situated.SystemAgent system;
	private Question question;
	private int guess;
	private int winningScore;

	private void initVariables() {
		system = (iristk.situated.SystemAgent) agent.getSystemAgent();
		guess = asInteger(0);
		winningScore = asInteger(3);
	}

	public iristk.situated.SystemAgent getSystem() {
		return this.system;
	}

	public void setSystem(iristk.situated.SystemAgent value) {
		this.system = value;
	}

	public Question getQuestion() {
		return this.question;
	}

	public void setQuestion(Question value) {
		this.question = value;
	}

	public int getGuess() {
		return this.guess;
	}

	public void setGuess(int value) {
		this.guess = value;
	}

	public int getWinningScore() {
		return this.winningScore;
	}

	public void setWinningScore(int value) {
		this.winningScore = value;
	}

	public QuestionSet getQuestions() {
		return this.questions;
	}

	public iristk.situated.SystemAgentFlow getAgent() {
		return this.agent;
	}

	@Override
	public Object getVariable(String name) {
		if (name.equals("system")) return this.system;
		if (name.equals("question")) return this.question;
		if (name.equals("guess")) return this.guess;
		if (name.equals("winningScore")) return this.winningScore;
		if (name.equals("questions")) return this.questions;
		if (name.equals("agent")) return this.agent;
		return null;
	}


	public QuizFlow(QuestionSet questions, iristk.situated.SystemAgentFlow agent) {
		this.questions = questions;
		this.agent = agent;
		initVariables();
	}

	@Override
	public State getInitialState() {return new Idle();}


	public class Idle extends State implements Initial {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 18
			try {
				EXECUTION: {
					int count = getCount(1289696681) + 1;
					incrCount(1289696681);
					iristk.situated.SystemAgentFlow.attendNobody state0 = agent.new attendNobody();
					if (!flowThread.callState(state0, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 18, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 18, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 21
			try {
				count = getCount(1607460018) + 1;
				if (event.triggers("sense.user.enter")) {
					incrCount(1607460018);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						iristk.situated.SystemAgentFlow.attend state1 = agent.new attend();
						state1.setTarget(event.get("user"));
						if (!flowThread.callState(state1, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 21, 36)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						// Line: 23
						Greeting state2 = new Greeting();
						flowThread.gotoState(state2, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 23, 28)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 21, 36));
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
			// Line: 28
			try {
				count = getCount(1407343478) + 1;
				if (event.triggers("sense.user.leave")) {
					if (system.isAttending(event)) {
						incrCount(1407343478);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 29
							if (system.hasUsers()) {
								iristk.situated.SystemAgentFlow.attendRandom state3 = agent.new attendRandom();
								if (!flowThread.callState(state3, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 29, 33)))) {
									eventResult = EVENT_ABORTED;
									break EXECUTION;
								}
								// Line: 31
								flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 31, 15)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
								// Line: 32
							} else {
								// Line: 33
								Goodbye state4 = new Goodbye();
								flowThread.gotoState(state4, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 33, 28)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 28, 70));
			}
			// Line: 36
			try {
				count = getCount(183264084) + 1;
				if (event.triggers("sense.user.speech.start")) {
					if (system.isAttending(event) && eq(event.get("speakers"), 1)) {
						incrCount(183264084);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							iristk.situated.SystemAgentFlow.gesture state5 = agent.new gesture();
							state5.setName("smile");
							if (!flowThread.callState(state5, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 36, 102)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 36, 102));
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
			// Line: 42
			try {
				EXECUTION: {
					int count = getCount(1490180672) + 1;
					incrCount(1490180672);
					iristk.situated.SystemAgentFlow.say state6 = agent.new say();
					StringCreator string7 = new StringCreator();
					string7.append("Hi there");
					state6.setText(string7.toString());
					if (!flowThread.callState(state6, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 42, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 44
					RequestGame state8 = new RequestGame();
					flowThread.gotoState(state8, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 44, 31)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 42, 12));
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


	private class RequestGame extends Dialog {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 49
			try {
				EXECUTION: {
					int count = getCount(1143839598) + 1;
					incrCount(1143839598);
					// Line: 50
					if (system.hasManyUsers()) {
						iristk.situated.SystemAgentFlow.attendAll state9 = agent.new attendAll();
						if (!flowThread.callState(state9, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 50, 37)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
					}
					iristk.situated.SystemAgentFlow.say state10 = agent.new say();
					StringCreator string11 = new StringCreator();
					string11.append("Do you want to play a game?");
					state10.setText(string11.toString());
					if (!flowThread.callState(state10, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 49, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					iristk.situated.SystemAgentFlow.listen state12 = agent.new listen();
					if (!flowThread.callState(state12, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 49, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 49, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 56
			try {
				count = getCount(358699161) + 1;
				if (event.triggers("sense.user.speak**")) {
					if (event.has("sem:yes")) {
						incrCount(358699161);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 57
							StartGame state13 = new StartGame();
							flowThread.gotoState(state13, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 57, 29)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 56, 60));
			}
			// Line: 59
			try {
				count = getCount(110718392) + 1;
				if (event.triggers("sense.user.speak**")) {
					if (event.has("sem:no")) {
						incrCount(110718392);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							iristk.situated.SystemAgentFlow.say state14 = agent.new say();
							StringCreator string15 = new StringCreator();
							string15.append("Okay, maybe another time then");
							state14.setText(string15.toString());
							if (!flowThread.callState(state14, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 59, 59)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
							// Line: 61
							Goodbye state16 = new Goodbye();
							flowThread.gotoState(state16, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 61, 27)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 59, 59));
			}
			// Line: 63
			try {
				count = getCount(2143192188) + 1;
				if (event.triggers("sense.user.silence sense.user.speak**")) {
					incrCount(2143192188);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 64
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 64, 14)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 63, 57));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class StartGame extends Dialog {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 69
			try {
				EXECUTION: {
					int count = getCount(231685785) + 1;
					incrCount(231685785);
					// Line: 70
					if (system.hasManyUsers()) {
						iristk.situated.SystemAgentFlow.say state17 = agent.new say();
						StringCreator string18 = new StringCreator();
						string18.append("Okay, let's play a game of quiz. The first to reach");
						// Line: 70
						string18.append(winningScore);
						string18.append("points is the winner.");
						state17.setText(string18.toString());
						if (!flowThread.callState(state17, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 70, 37)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						// Line: 72
					} else {
						iristk.situated.SystemAgentFlow.say state19 = agent.new say();
						StringCreator string20 = new StringCreator();
						string20.append("Okay, let's play a game of quiz. Let's try to see if you can get");
						// Line: 72
						string20.append(winningScore);
						string20.append("points.");
						state19.setText(string20.toString());
						if (!flowThread.callState(state19, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 70, 37)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
					}
					// Line: 75
					NextQuestion state21 = new NextQuestion();
					flowThread.gotoState(state21, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 75, 32)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 69, 12));
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


	private class Goodbye extends State {

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
					int count = getCount(1023487453) + 1;
					incrCount(1023487453);
					iristk.situated.SystemAgentFlow.say state22 = agent.new say();
					StringCreator string23 = new StringCreator();
					string23.append("Goodbye");
					state22.setText(string23.toString());
					if (!flowThread.callState(state22, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 80, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 82
					Idle state24 = new Idle();
					flowThread.gotoState(state24, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 82, 24)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 80, 12));
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


	private class NextQuestion extends Dialog {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 87
			try {
				EXECUTION: {
					int count = getCount(1694819250) + 1;
					incrCount(1694819250);
					// Line: 88
					question = questions.next(); guess = 0;
					// Line: 89
					if (system.hasManyUsers()) {
						// Line: 90
						boolean chosen25 = false;
						boolean matching26 = true;
						while (!chosen25 && matching26) {
							int rand27 = random(1586600255, 2, iristk.util.RandomList.RandomModel.DECK_RESHUFFLE_NOREPEAT);
							matching26 = false;
							if (true) {
								matching26 = true;
								if (rand27 >= 0 && rand27 < 1) {
									chosen25 = true;
									iristk.situated.SystemAgentFlow.attendOther state28 = agent.new attendOther();
									if (!flowThread.callState(state28, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 90, 13)))) {
										eventResult = EVENT_ABORTED;
										break EXECUTION;
									}
									iristk.situated.SystemAgentFlow.say state29 = agent.new say();
									StringCreator string30 = new StringCreator();
									string30.append("The next one is for you");
									state29.setText(string30.toString());
									if (!flowThread.callState(state29, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 90, 13)))) {
										eventResult = EVENT_ABORTED;
										break EXECUTION;
									}
								}
							}
							if (true) {
								matching26 = true;
								if (rand27 >= 1 && rand27 < 2) {
									chosen25 = true;
									iristk.situated.SystemAgentFlow.attendAll state31 = agent.new attendAll();
									if (!flowThread.callState(state31, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 90, 13)))) {
										eventResult = EVENT_ABORTED;
										break EXECUTION;
									}
									iristk.situated.SystemAgentFlow.say state32 = agent.new say();
									StringCreator string33 = new StringCreator();
									string33.append("Let's see who answers first");
									state32.setText(string33.toString());
									if (!flowThread.callState(state32, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 90, 13)))) {
										eventResult = EVENT_ABORTED;
										break EXECUTION;
									}
								}
							}
						}
						// Line: 100
					} else {
						iristk.situated.SystemAgentFlow.say state34 = agent.new say();
						StringCreator string35 = new StringCreator();
						string35.append("Here comes the next question");
						state34.setText(string35.toString());
						if (!flowThread.callState(state34, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 89, 37)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
					}
					// Line: 103
					ReadQuestion state36 = new ReadQuestion();
					flowThread.gotoState(state36, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 103, 32)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 87, 12));
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


	private class ReadQuestion extends AwaitAnswer {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 108
			try {
				EXECUTION: {
					int count = getCount(292938459) + 1;
					incrCount(292938459);
					iristk.situated.SystemAgentFlow.say state37 = agent.new say();
					state37.setText(question.getFullQuestion());
					if (!flowThread.callState(state37, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 108, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					iristk.situated.SystemAgentFlow.listen state38 = agent.new listen();
					state38.setContext("default " + question.getId());
					if (!flowThread.callState(state38, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 108, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 108, 12));
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


	private class ReadOptions extends AwaitAnswer {

		final State currentState = this;


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
					int count = getCount(1993134103) + 1;
					incrCount(1993134103);
					iristk.situated.SystemAgentFlow.say state39 = agent.new say();
					state39.setText(question.getOptions());
					if (!flowThread.callState(state39, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 115, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					iristk.situated.SystemAgentFlow.listen state40 = agent.new listen();
					state40.setContext("default " + question.getId());
					if (!flowThread.callState(state40, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 115, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 115, 12));
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


	private class AwaitAnswer extends Dialog {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 122
			try {
				EXECUTION: {
					int count = getCount(653305407) + 1;
					incrCount(653305407);
					iristk.situated.SystemAgentFlow.listen state41 = agent.new listen();
					state41.setContext("default " + question.getId());
					if (!flowThread.callState(state41, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 122, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 122, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 125
			try {
				count = getCount(1130478920) + 1;
				if (event.triggers("sense.user.speak.multi")) {
					incrCount(1130478920);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 126
						if (question.isCorrect(event.get("all:0:sem:answer"))) {
							iristk.situated.SystemAgentFlow.attend state42 = agent.new attend();
							state42.setTarget(event.get("all:0:user"));
							if (!flowThread.callState(state42, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 126, 58)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
							// Line: 128
							CorrectAnswer state43 = new CorrectAnswer();
							flowThread.gotoState(state43, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 128, 34)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
							// Line: 129
						} else if (question.isCorrect(event.get("all:1:sem:answer"))) {
							iristk.situated.SystemAgentFlow.attend state44 = agent.new attend();
							state44.setTarget(event.get("all:1:user"));
							if (!flowThread.callState(state44, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 126, 58)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
							// Line: 131
							CorrectAnswer state45 = new CorrectAnswer();
							flowThread.gotoState(state45, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 131, 34)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
							// Line: 132
						} else {
							iristk.situated.SystemAgentFlow.say state46 = agent.new say();
							StringCreator string47 = new StringCreator();
							string47.append("None of you were correct, let's try another question.");
							state46.setText(string47.toString());
							if (!flowThread.callState(state46, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 126, 58)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
							// Line: 134
							NextQuestion state48 = new NextQuestion();
							flowThread.gotoState(state48, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 134, 33)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 125, 42));
			}
			// Line: 137
			try {
				count = getCount(1101288798) + 1;
				if (event.triggers("sense.user.speak")) {
					incrCount(1101288798);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 138
						if (system.isAttendingAll()) {
							iristk.situated.SystemAgentFlow.attend state49 = agent.new attend();
							state49.setTarget(event.get("user"));
							if (!flowThread.callState(state49, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 138, 39)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
						// Line: 141
						if (question.isCorrect(event.get("sem:answer"))) {
							// Line: 142
							CorrectAnswer state50 = new CorrectAnswer();
							flowThread.gotoState(state50, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 142, 34)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
							// Line: 143
						} else {
							// Line: 144
							IncorrectAnswer state51 = new IncorrectAnswer();
							flowThread.gotoState(state51, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 144, 36)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 137, 36));
			}
			// Line: 147
			try {
				count = getCount(2104457164) + 1;
				if (event.triggers("sense.user.speak.side")) {
					incrCount(2104457164);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						iristk.situated.SystemAgentFlow.attendOther state52 = agent.new attendOther();
						state52.setMode("eyes");
						if (!flowThread.callState(state52, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 147, 41)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						iristk.situated.SystemAgentFlow.say state53 = agent.new say();
						StringCreator string54 = new StringCreator();
						string54.append("You were not supposed to answer that");
						state53.setText(string54.toString());
						if (!flowThread.callState(state53, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 147, 41)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						iristk.situated.SystemAgentFlow.attendOther state55 = agent.new attendOther();
						state55.setMode("eyes");
						if (!flowThread.callState(state55, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 147, 41)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						iristk.situated.SystemAgentFlow.say state56 = agent.new say();
						StringCreator string57 = new StringCreator();
						string57.append("So, what is your answer?");
						state56.setText(string57.toString());
						if (!flowThread.callState(state56, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 147, 41)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						// Line: 152
						AwaitAnswer state58 = new AwaitAnswer();
						flowThread.gotoState(state58, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 152, 31)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 147, 41));
			}
			// Line: 154
			try {
				count = getCount(1940030785) + 1;
				if (event.triggers("sense.user.silence")) {
					incrCount(1940030785);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 155
						Event raiseEvent59 = new Event("skip");
						if (flowThread.raiseEvent(raiseEvent59, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 155, 25))) == State.EVENT_ABORTED) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 154, 38));
			}
			// Line: 157
			try {
				count = getCount(1763847188) + 1;
				if (event.triggers("skip")) {
					incrCount(1763847188);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						iristk.situated.SystemAgentFlow.say state60 = agent.new say();
						StringCreator string61 = new StringCreator();
						string61.append("Give me your best guess");
						state60.setText(string61.toString());
						if (!flowThread.callState(state60, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 157, 24)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						// Line: 159
						AwaitAnswer state62 = new AwaitAnswer();
						flowThread.gotoState(state62, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 159, 31)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 157, 24));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class CorrectAnswer extends Dialog {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 164
			try {
				EXECUTION: {
					int count = getCount(1192108080) + 1;
					incrCount(1192108080);
					// Line: 165
					system.getCurrentUser().incrInteger("score");
					iristk.situated.SystemAgentFlow.say state63 = agent.new say();
					StringCreator string64 = new StringCreator();
					string64.append("That is correct, you now have a score of");
					// Line: 165
					string64.append(system.getCurrentUser().get("score"));
					state63.setText(string64.toString());
					if (!flowThread.callState(state63, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 164, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 167
					if (asInteger(system.getCurrentUser().get("score"), 0) >= winningScore) {
						// Line: 168
						Winner state65 = new Winner();
						flowThread.gotoState(state65, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 168, 27)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
						// Line: 169
					} else {
						// Line: 170
						NextQuestion state66 = new NextQuestion();
						flowThread.gotoState(state66, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 170, 33)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 164, 12));
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


	private class IncorrectAnswer extends Dialog {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 176
			try {
				EXECUTION: {
					int count = getCount(1608446010) + 1;
					incrCount(1608446010);
					iristk.situated.SystemAgentFlow.say state67 = agent.new say();
					StringCreator string68 = new StringCreator();
					string68.append("That was wrong");
					state67.setText(string68.toString());
					if (!flowThread.callState(state67, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 176, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 178
					if (system.hasManyUsers() && guess == 0) {
						// Line: 179
						guess++;
						iristk.situated.SystemAgentFlow.attendOther state69 = agent.new attendOther();
						if (!flowThread.callState(state69, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 178, 52)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						iristk.situated.SystemAgentFlow.say state70 = agent.new say();
						StringCreator string71 = new StringCreator();
						string71.append("Maybe you know the answer?");
						state70.setText(string71.toString());
						if (!flowThread.callState(state70, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 178, 52)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						// Line: 182
						AwaitAnswer state72 = new AwaitAnswer();
						flowThread.gotoState(state72, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 182, 32)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					iristk.situated.SystemAgentFlow.say state73 = agent.new say();
					StringCreator string74 = new StringCreator();
					string74.append("The correct answer was");
					// Line: 182
					string74.append(question.getCorrectAnswer());
					state73.setText(string74.toString());
					if (!flowThread.callState(state73, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 176, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 185
					NextQuestion state75 = new NextQuestion();
					flowThread.gotoState(state75, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 185, 32)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 176, 12));
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


	private class Winner extends Dialog {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 190
			try {
				EXECUTION: {
					int count = getCount(1509514333) + 1;
					incrCount(1509514333);
					// Line: 191
					system.putUsers("score", 0);
					iristk.situated.SystemAgentFlow.say state76 = agent.new say();
					StringCreator string77 = new StringCreator();
					string77.append("Congratulations, you are the winner");
					state76.setText(string77.toString());
					if (!flowThread.callState(state76, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 190, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 193
					if (system.hasManyUsers()) {
						iristk.situated.SystemAgentFlow.attendOther state78 = agent.new attendOther();
						if (!flowThread.callState(state78, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 193, 37)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						iristk.situated.SystemAgentFlow.say state79 = agent.new say();
						StringCreator string80 = new StringCreator();
						string80.append("I am sorry, but you lost.");
						state79.setText(string80.toString());
						if (!flowThread.callState(state79, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 193, 37)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
					}
					// Line: 197
					Goodbye state81 = new Goodbye();
					flowThread.gotoState(state81, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 197, 27)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 190, 12));
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


}
