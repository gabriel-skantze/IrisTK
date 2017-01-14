package iristk.app.chess;

import java.io.File;
import iristk.xml.XmlMarshaller.XMLLocation;
import iristk.system.Event;
import iristk.flow.*;
import iristk.util.Record;
import static iristk.util.Converters.*;

public class ChessFlow extends iristk.flow.Flow {

	private ChessGame chess;
	private Record move;

	private void initVariables() {
	}

	public Record getMove() {
		return this.move;
	}

	public void setMove(Record value) {
		this.move = value;
	}

	public ChessGame getChess() {
		return this.chess;
	}

	@Override
	public Object getVariable(String name) {
		if (name.equals("move")) return this.move;
		if (name.equals("chess")) return this.chess;
		return null;
	}


	public ChessFlow(ChessGame chess) {
		this.chess = chess;
		initVariables();
	}

	@Override
	public State getInitialState() {return new Start();}


	private class Game extends State {

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
			// Line: 11
			try {
				count = getCount(997608398) + 1;
				if (event.triggers("chess.restart")) {
					incrCount(997608398);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						iristk.flow.DialogFlow.say state0 = new iristk.flow.DialogFlow.say();
						StringCreator string1 = new StringCreator();
						string1.append("Okay, let's restart");
						state0.setText(string1.toString());
						if (!flowThread.callState(state0, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 11, 33)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						// Line: 13
						AwaitUser state2 = new AwaitUser();
						flowThread.gotoState(state2, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 13, 29)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 11, 33));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	public class Start extends Game implements Initial {

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
					iristk.flow.DialogFlow.say state3 = new iristk.flow.DialogFlow.say();
					StringCreator string4 = new StringCreator();
					string4.append("Okay, let's start");
					state3.setText(string4.toString());
					if (!flowThread.callState(state3, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 18, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 20
					AwaitUser state5 = new AwaitUser();
					flowThread.gotoState(state5, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 20, 29)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 18, 12));
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


	private class AwaitUser extends Game {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 25
			try {
				EXECUTION: {
					int count = getCount(1811075214) + 1;
					incrCount(1811075214);
					// Line: 26
					boolean chosen6 = false;
					boolean matching7 = true;
					while (!chosen6 && matching7) {
						int rand8 = random(1588970020, 3, iristk.util.RandomList.RandomModel.DECK_RESHUFFLE_NOREPEAT);
						matching7 = false;
						if (true) {
							matching7 = true;
							if (rand8 >= 0 && rand8 < 1) {
								chosen6 = true;
								iristk.app.chess.ChessFlow.ask state9 = new ask();
								StringCreator string10 = new StringCreator();
								string10.append("So, what is your move?");
								state9.setText(string10.toString());
								if (!flowThread.callState(state9, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 26, 12)))) {
									eventResult = EVENT_ABORTED;
									break EXECUTION;
								}
							}
						}
						if (true) {
							matching7 = true;
							if (rand8 >= 1 && rand8 < 2) {
								chosen6 = true;
								iristk.app.chess.ChessFlow.ask state11 = new ask();
								StringCreator string12 = new StringCreator();
								string12.append("Your move");
								state11.setText(string12.toString());
								if (!flowThread.callState(state11, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 26, 12)))) {
									eventResult = EVENT_ABORTED;
									break EXECUTION;
								}
							}
						}
						if (true) {
							matching7 = true;
							if (rand8 >= 2 && rand8 < 3) {
								chosen6 = true;
								iristk.app.chess.ChessFlow.ask state13 = new ask();
								StringCreator string14 = new StringCreator();
								string14.append("Your turn");
								state13.setText(string14.toString());
								if (!flowThread.callState(state13, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 26, 12)))) {
									eventResult = EVENT_ABORTED;
									break EXECUTION;
								}
							}
						}
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 25, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 32
			try {
				count = getCount(1407343478) + 1;
				if (event.triggers("chess.move.user")) {
					incrCount(1407343478);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 33
						chess.identifyMoves(move);
						// Line: 34
						if (chess.availableMoves() == 1) {
							// Line: 35
							PerformMove state15 = new PerformMove();
							flowThread.gotoState(state15, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 35, 32)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
							// Line: 36
						} else if (chess.availableMoves() == 0) {
							iristk.flow.DialogFlow.say state16 = new iristk.flow.DialogFlow.say();
							StringCreator string17 = new StringCreator();
							string17.append("Sorry, I can't do that");
							state16.setText(string17.toString());
							if (!flowThread.callState(state16, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 34, 43)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
							// Line: 38
							AwaitUser state18 = new AwaitUser();
							flowThread.gotoState(state18, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 38, 31)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
							// Line: 39
						} else {
							// Line: 40
							chess.displayAvailableMoves();
							// Line: 41
							Record clarify = chess.chooseClarification();
							// Line: 42
							if (clarify.has("steps")) {
								// Line: 43
								ClarifySteps state19 = new ClarifySteps();
								flowThread.gotoState(state19, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 43, 34)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
								// Line: 44
							} else if (clarify.has("direction")) {
								// Line: 45
								ClarifyDirection state20 = new ClarifyDirection();
								flowThread.gotoState(state20, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 45, 38)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
								// Line: 46
							} else if (clarify.has("piece")) {
								// Line: 47
								ClarifyPiece state21 = new ClarifyPiece();
								state21.setPiece(clarify.get("piece"));
								flowThread.gotoState(state21, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 47, 58)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
								// Line: 48
							} else if (clarify.has("square")) {
								// Line: 49
								ClarifySquare state22 = new ClarifySquare();
								flowThread.gotoState(state22, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 49, 35)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 32, 35));
			}
			// Line: 53
			try {
				count = getCount(2143192188) + 1;
				if (event.triggers("sense.user.speak")) {
					if (event.has("sem:act_move")) {
						incrCount(2143192188);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 54
							chess.newMove();
							// Line: 55
							move = asRecord(event.get("sem"));
							// Line: 56
							Event raiseEvent23 = new Event("chess.move.user");
							if (flowThread.raiseEvent(raiseEvent23, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 56, 36))) == State.EVENT_ABORTED) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 53, 63));
			}
			// Line: 58
			try {
				count = getCount(114935352) + 1;
				if (event.triggers("sense.speech.partial")) {
					if (event.has("sem:act_move")) {
						incrCount(114935352);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 59
							chess.displayAvailableMoves(asRecord(event.get("sem")));
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 58, 67));
			}
			// Line: 61
			try {
				count = getCount(32374789) + 1;
				if (event.triggers("sense.user.silence")) {
					incrCount(32374789);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 62
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 62, 15)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 61, 38));
			}
			// Line: 64
			try {
				count = getCount(1865127310) + 1;
				if (event.triggers("sense.user.speak")) {
					incrCount(1865127310);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						iristk.flow.DialogFlow.say state24 = new iristk.flow.DialogFlow.say();
						StringCreator string25 = new StringCreator();
						string25.append("Sorry");
						state24.setText(string25.toString());
						if (!flowThread.callState(state24, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 64, 36)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						// Line: 66
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 66, 15)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 64, 36));
			}
			// Line: 68
			try {
				count = getCount(1694819250) + 1;
				if (event.triggers("dialog.repeat")) {
					incrCount(1694819250);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 69
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 69, 15)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 68, 33));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class PerformMove extends Game {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 74
			try {
				EXECUTION: {
					int count = getCount(1586600255) + 1;
					incrCount(1586600255);
					iristk.flow.DialogFlow.say state26 = new iristk.flow.DialogFlow.say();
					StringCreator string27 = new StringCreator();
					string27.append("Okay");
					state26.setText(string27.toString());
					if (!flowThread.callState(state26, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 74, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 76
					chess.performMove();
					// Line: 77
					AwaitSystem state28 = new AwaitSystem();
					flowThread.gotoState(state28, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 77, 32)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 74, 12));
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


	private class ClarifySteps extends AwaitUser {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 82
			try {
				EXECUTION: {
					int count = getCount(1579572132) + 1;
					incrCount(1579572132);
					iristk.app.chess.ChessFlow.ask state29 = new ask();
					StringCreator string30 = new StringCreator();
					string30.append("How many steps?");
					state29.setText(string30.toString());
					if (!flowThread.callState(state29, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 82, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 82, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 85
			try {
				count = getCount(359023572) + 1;
				if (event.triggers("sense.user.speak")) {
					if (event.has("sem:number")) {
						incrCount(359023572);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 86
							move.putIfNotNull("movement:steps", event.get("sem:number"));
							// Line: 87
							Event raiseEvent31 = new Event("chess.move.user");
							if (flowThread.raiseEvent(raiseEvent31, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 87, 36))) == State.EVENT_ABORTED) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 85, 61));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class ClarifyDirection extends AwaitUser {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 92
			try {
				EXECUTION: {
					int count = getCount(917142466) + 1;
					incrCount(917142466);
					iristk.app.chess.ChessFlow.ask state32 = new ask();
					StringCreator string33 = new StringCreator();
					string33.append("In which direction?");
					state32.setText(string33.toString());
					if (!flowThread.callState(state32, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 92, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 92, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 95
			try {
				count = getCount(1993134103) + 1;
				if (event.triggers("sense.user.speak")) {
					if (event.has("sem:direction")) {
						incrCount(1993134103);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 96
							move.putIfNotNull("movement:direction", event.get("sem:direction"));
							// Line: 97
							Event raiseEvent34 = new Event("chess.move.user");
							if (flowThread.raiseEvent(raiseEvent34, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 97, 36))) == State.EVENT_ABORTED) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 95, 64));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class ClarifyPiece extends AwaitUser {

		final State currentState = this;

		public String piece = null;

		public void setPiece(Object value) {
			if (value != null) {
				piece = asString(value);
				params.put("piece", value);
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
			// Line: 103
			try {
				EXECUTION: {
					int count = getCount(604107971) + 1;
					incrCount(604107971);
					iristk.app.chess.ChessFlow.ask state35 = new ask();
					StringCreator string36 = new StringCreator();
					string36.append("Which");
					// Line: 103
					string36.append(piece);
					string36.append("?");
					state35.setText(string36.toString());
					if (!flowThread.callState(state35, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 103, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 103, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 106
			try {
				count = getCount(123961122) + 1;
				if (event.triggers("sense.user.speak")) {
					if (event.has("sem:piece")) {
						incrCount(123961122);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 107
							move.putIfNotNull("piece", event.get("sem:piece"));
							// Line: 108
							Event raiseEvent37 = new Event("chess.move.user");
							if (flowThread.raiseEvent(raiseEvent37, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 108, 36))) == State.EVENT_ABORTED) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 106, 60));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class ClarifySquare extends AwaitUser {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() throws Exception {
			int eventResult;
			Event event = new Event("state.enter");
			// Line: 113
			try {
				EXECUTION: {
					int count = getCount(1101288798) + 1;
					incrCount(1101288798);
					iristk.app.chess.ChessFlow.ask state38 = new ask();
					StringCreator string39 = new StringCreator();
					string39.append("To which square?");
					state38.setText(string39.toString());
					if (!flowThread.callState(state38, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 113, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 113, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 116
			try {
				count = getCount(942731712) + 1;
				if (event.triggers("sense.user.speak")) {
					if (event.has("sem:piece:square")) {
						incrCount(942731712);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 117
							move.putIfNotNull("movement:square", event.get("sem:piece:square"));
							// Line: 118
							Event raiseEvent40 = new Event("chess.move.user");
							if (flowThread.raiseEvent(raiseEvent40, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 118, 36))) == State.EVENT_ABORTED) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 116, 67));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class AwaitSystem extends Game {

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
			// Line: 123
			try {
				count = getCount(758529971) + 1;
				if (event.triggers("chess.move.system")) {
					incrCount(758529971);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 124
						AwaitUser state41 = new AwaitUser();
						flowThread.gotoState(state41, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 124, 30)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 123, 37));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class ask extends State {

		final State currentState = this;

		public String text = null;
		public Float threshold = asFloat(0.7);

		public void setText(Object value) {
			if (value != null) {
				text = asString(value);
				params.put("text", value);
			}
		}

		public void setThreshold(Object value) {
			if (value != null) {
				threshold = asFloat(value);
				params.put("threshold", value);
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
			// Line: 131
			try {
				EXECUTION: {
					int count = getCount(1763847188) + 1;
					incrCount(1763847188);
					iristk.flow.DialogFlow.say state42 = new iristk.flow.DialogFlow.say();
					state42.setText(text);
					if (!flowThread.callState(state42, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 131, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					iristk.flow.DialogFlow.listen state43 = new iristk.flow.DialogFlow.listen();
					if (!flowThread.callState(state43, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 131, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 131, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 135
			try {
				count = getCount(1617791695) + 1;
				if (event.triggers("sense.user.speak")) {
					if (threshold > asFloat(event.get("conf"))) {
						incrCount(1617791695);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 136
							confirm state44 = new confirm();
							state44.setCevent(event);
							flowThread.gotoState(state44, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 136, 44)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 135, 75));
			}
			// Line: 138
			try {
				count = getCount(1192108080) + 1;
				if (event.triggers("sense.user.speak")) {
					incrCount(1192108080);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 139
						Event returnEvent45 = new Event(event.getName());
						returnEvent45.copyParams(event);
						flowThread.returnFromCall(this, returnEvent45, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 139, 26)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 138, 36));
			}
			// Line: 141
			try {
				count = getCount(864237698) + 1;
				if (event.triggers("sense.user.silence")) {
					incrCount(864237698);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 142
						Event returnEvent46 = new Event(event.getName());
						returnEvent46.copyParams(event);
						flowThread.returnFromCall(this, returnEvent46, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 142, 26)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 141, 38));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class confirm extends State {

		final State currentState = this;

		public Event cevent = null;

		public void setCevent(Object value) {
			if (value != null) {
				cevent = (Event) value;
				params.put("cevent", value);
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
			// Line: 148
			try {
				EXECUTION: {
					int count = getCount(608188624) + 1;
					incrCount(608188624);
					iristk.flow.DialogFlow.say state47 = new iristk.flow.DialogFlow.say();
					StringCreator string48 = new StringCreator();
					string48.append("Did you say");
					// Line: 148
					string48.append(cevent.get("text"));
					state47.setText(string48.toString());
					if (!flowThread.callState(state47, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 148, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					iristk.flow.DialogFlow.listen state49 = new iristk.flow.DialogFlow.listen();
					if (!flowThread.callState(state49, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 148, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 148, 12));
			}
		}

		@Override
		public int onFlowEvent(Event event) throws Exception {
			int eventResult;
			int count;
			// Line: 152
			try {
				count = getCount(511833308) + 1;
				if (event.triggers("sense.user.speak")) {
					if (event.has("sem:yes")) {
						incrCount(511833308);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 153
							Event returnEvent50 = new Event(cevent.getName());
							returnEvent50.copyParams(cevent);
							flowThread.returnFromCall(this, returnEvent50, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 153, 27)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 152, 58));
			}
			// Line: 155
			try {
				count = getCount(1705929636) + 1;
				if (event.triggers("sense.user.speak")) {
					if (event.has("sem:no")) {
						incrCount(1705929636);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 156
							Event returnEvent51 = new Event("dialog.repeat");
							flowThread.returnFromCall(this, returnEvent51, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 156, 35)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						if (eventResult != EVENT_IGNORED) return eventResult;
					}
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 155, 57));
			}
			// Line: 158
			try {
				count = getCount(1509514333) + 1;
				if (event.triggers("sense.user.speak sense.user.silence")) {
					incrCount(1509514333);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 159
						Event returnEvent52 = new Event(event.getName());
						returnEvent52.copyParams(event);
						flowThread.returnFromCall(this, returnEvent52, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 159, 26)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			} catch (Exception e) {
				throw new FlowException(e, currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 158, 55));
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


}
