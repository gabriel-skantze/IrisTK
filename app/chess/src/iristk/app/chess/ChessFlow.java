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
				count = getCount(399573350) + 1;
				if (event.triggers("chess.restart")) {
					incrCount(399573350);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						iristk.flow.DialogFlow.say state0 = new iristk.flow.DialogFlow.say();
						state0.setText("Okay, let's restart");
						if (!flowThread.callState(state0, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 11, 33)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						// Line: 13
						AwaitUser state1 = new AwaitUser();
						flowThread.gotoState(state1, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 13, 29)));
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


	public class Start extends Game {

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
					int count = getCount(1347137144) + 1;
					incrCount(1347137144);
					iristk.flow.DialogFlow.say state2 = new iristk.flow.DialogFlow.say();
					state2.setText("Okay, let's start");
					if (!flowThread.callState(state2, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 18, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 20
					AwaitUser state3 = new AwaitUser();
					flowThread.gotoState(state3, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 20, 29)));
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
					int count = getCount(1212899836) + 1;
					incrCount(1212899836);
					// Line: 26
					boolean chosen4 = false;
					boolean matching5 = true;
					while (!chosen4 && matching5) {
						int rand6 = random(1174290147, 3, iristk.util.RandomList.RandomModel.DECK_RESHUFFLE_NOREPEAT);
						matching5 = false;
						if (true) {
							matching5 = true;
							if (rand6 >= 0 && rand6 < 1) {
								chosen4 = true;
								iristk.app.chess.ChessFlow.ask state7 = new ask();
								state7.setText("So, what is your move?");
								if (!flowThread.callState(state7, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 26, 12)))) {
									eventResult = EVENT_ABORTED;
									break EXECUTION;
								}
							}
						}
						if (true) {
							matching5 = true;
							if (rand6 >= 1 && rand6 < 2) {
								chosen4 = true;
								iristk.app.chess.ChessFlow.ask state8 = new ask();
								state8.setText("Your move");
								if (!flowThread.callState(state8, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 26, 12)))) {
									eventResult = EVENT_ABORTED;
									break EXECUTION;
								}
							}
						}
						if (true) {
							matching5 = true;
							if (rand6 >= 2 && rand6 < 3) {
								chosen4 = true;
								iristk.app.chess.ChessFlow.ask state9 = new ask();
								state9.setText("Your turn");
								if (!flowThread.callState(state9, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 26, 12)))) {
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
				count = getCount(1289696681) + 1;
				if (event.triggers("chess.move.user")) {
					incrCount(1289696681);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 33
						chess.identifyMoves(move);
						// Line: 34
						if (chess.availableMoves() == 1) {
							// Line: 35
							PerformMove state10 = new PerformMove();
							flowThread.gotoState(state10, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 35, 32)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
							// Line: 36
						} else if (chess.availableMoves() == 0) {
							iristk.flow.DialogFlow.say state11 = new iristk.flow.DialogFlow.say();
							state11.setText("Sorry, I can't do that");
							if (!flowThread.callState(state11, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 34, 43)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
							// Line: 38
							AwaitUser state12 = new AwaitUser();
							flowThread.gotoState(state12, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 38, 31)));
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
								ClarifySteps state13 = new ClarifySteps();
								flowThread.gotoState(state13, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 43, 34)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
								// Line: 44
							} else if (clarify.has("direction")) {
								// Line: 45
								ClarifyDirection state14 = new ClarifyDirection();
								flowThread.gotoState(state14, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 45, 38)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
								// Line: 46
							} else if (clarify.has("piece")) {
								// Line: 47
								ClarifyPiece state15 = new ClarifyPiece();
								state15.setPiece(clarify.get("piece"));
								flowThread.gotoState(state15, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 47, 58)));
								eventResult = EVENT_ABORTED;
								break EXECUTION;
								// Line: 48
							} else if (clarify.has("square")) {
								// Line: 49
								ClarifySquare state16 = new ClarifySquare();
								flowThread.gotoState(state16, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 49, 35)));
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
				count = getCount(358699161) + 1;
				if (event.triggers("sense.user.speak")) {
					if (event.has("sem:act_move")) {
						incrCount(358699161);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 54
							chess.newMove();
							// Line: 55
							move = asRecord(event.get("sem"));
							// Line: 56
							Event raiseEvent17 = new Event("chess.move.user");
							if (flowThread.raiseEvent(raiseEvent17, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 56, 36))) == State.EVENT_ABORTED) {
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
				count = getCount(425918570) + 1;
				if (event.triggers("sense.speech.partial")) {
					if (event.has("sem:act_move")) {
						incrCount(425918570);
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
				count = getCount(1100439041) + 1;
				if (event.triggers("sense.user.silence")) {
					incrCount(1100439041);
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
				count = getCount(114935352) + 1;
				if (event.triggers("sense.user.speak")) {
					incrCount(114935352);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						iristk.flow.DialogFlow.say state18 = new iristk.flow.DialogFlow.say();
						state18.setText("Sorry");
						if (!flowThread.callState(state18, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 64, 36)))) {
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
				count = getCount(32374789) + 1;
				if (event.triggers("dialog.repeat")) {
					incrCount(32374789);
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
					int count = getCount(1865127310) + 1;
					incrCount(1865127310);
					iristk.flow.DialogFlow.say state19 = new iristk.flow.DialogFlow.say();
					state19.setText("Okay");
					if (!flowThread.callState(state19, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 74, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					// Line: 76
					chess.performMove();
					// Line: 77
					AwaitSystem state20 = new AwaitSystem();
					flowThread.gotoState(state20, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 77, 32)));
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
					int count = getCount(1651191114) + 1;
					incrCount(1651191114);
					iristk.app.chess.ChessFlow.ask state21 = new ask();
					state21.setText("How many steps?");
					if (!flowThread.callState(state21, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 82, 12)))) {
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
				count = getCount(1586600255) + 1;
				if (event.triggers("sense.user.speak")) {
					if (event.has("sem:number")) {
						incrCount(1586600255);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 86
							move.putIfNotNull("movement:steps", event.get("sem:number"));
							// Line: 87
							Event raiseEvent22 = new Event("chess.move.user");
							if (flowThread.raiseEvent(raiseEvent22, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 87, 36))) == State.EVENT_ABORTED) {
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
					int count = getCount(1579572132) + 1;
					incrCount(1579572132);
					iristk.app.chess.ChessFlow.ask state23 = new ask();
					state23.setText("In which direction?");
					if (!flowThread.callState(state23, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 92, 12)))) {
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
				count = getCount(359023572) + 1;
				if (event.triggers("sense.user.speak")) {
					if (event.has("sem:direction")) {
						incrCount(359023572);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 96
							move.putIfNotNull("movement:direction", event.get("sem:direction"));
							// Line: 97
							Event raiseEvent24 = new Event("chess.move.user");
							if (flowThread.raiseEvent(raiseEvent24, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 97, 36))) == State.EVENT_ABORTED) {
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
					int count = getCount(1993134103) + 1;
					incrCount(1993134103);
					iristk.app.chess.ChessFlow.ask state25 = new ask();
					state25.setText(concat("Which", piece, "?"));
					if (!flowThread.callState(state25, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 103, 12)))) {
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
				count = getCount(405662939) + 1;
				if (event.triggers("sense.user.speak")) {
					if (event.has("sem:piece")) {
						incrCount(405662939);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 107
							move.putIfNotNull("piece", event.get("sem:piece"));
							// Line: 108
							Event raiseEvent26 = new Event("chess.move.user");
							if (flowThread.raiseEvent(raiseEvent26, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 108, 36))) == State.EVENT_ABORTED) {
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
					int count = getCount(604107971) + 1;
					incrCount(604107971);
					iristk.app.chess.ChessFlow.ask state27 = new ask();
					state27.setText("To which square?");
					if (!flowThread.callState(state27, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 113, 12)))) {
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
				count = getCount(1227229563) + 1;
				if (event.triggers("sense.user.speak")) {
					if (event.has("sem:piece:square")) {
						incrCount(1227229563);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 117
							move.putIfNotNull("movement:square", event.get("sem:piece:square"));
							// Line: 118
							Event raiseEvent28 = new Event("chess.move.user");
							if (flowThread.raiseEvent(raiseEvent28, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 118, 36))) == State.EVENT_ABORTED) {
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
				count = getCount(942731712) + 1;
				if (event.triggers("chess.move.system")) {
					incrCount(942731712);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 124
						AwaitUser state29 = new AwaitUser();
						flowThread.gotoState(state29, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 124, 30)));
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
					int count = getCount(2104457164) + 1;
					incrCount(2104457164);
					iristk.flow.DialogFlow.say state30 = new iristk.flow.DialogFlow.say();
					state30.setText(text);
					if (!flowThread.callState(state30, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 131, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					iristk.flow.DialogFlow.listen state31 = new iristk.flow.DialogFlow.listen();
					if (!flowThread.callState(state31, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 131, 12)))) {
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
				count = getCount(1521118594) + 1;
				if (event.triggers("sense.user.speak")) {
					if (threshold > asFloat(event.get("conf"))) {
						incrCount(1521118594);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 136
							confirm state32 = new confirm();
							state32.setCevent(event);
							flowThread.gotoState(state32, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 136, 44)));
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
				count = getCount(1869997857) + 1;
				if (event.triggers("sense.user.speak")) {
					incrCount(1869997857);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 139
						Event returnEvent33 = new Event(event.getName());
						returnEvent33.copyParams(event);
						flowThread.returnFromCall(this, returnEvent33, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 139, 26)));
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
				count = getCount(1617791695) + 1;
				if (event.triggers("sense.user.silence")) {
					incrCount(1617791695);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 142
						Event returnEvent34 = new Event(event.getName());
						returnEvent34.copyParams(event);
						flowThread.returnFromCall(this, returnEvent34, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 142, 26)));
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
					int count = getCount(864237698) + 1;
					incrCount(864237698);
					iristk.flow.DialogFlow.say state35 = new iristk.flow.DialogFlow.say();
					state35.setText(concat("Did you say", cevent.get("text")));
					if (!flowThread.callState(state35, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 148, 12)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					iristk.flow.DialogFlow.listen state36 = new iristk.flow.DialogFlow.listen();
					if (!flowThread.callState(state36, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 148, 12)))) {
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
				count = getCount(608188624) + 1;
				if (event.triggers("sense.user.speak")) {
					if (event.has("sem:yes")) {
						incrCount(608188624);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 153
							Event returnEvent37 = new Event(cevent.getName());
							returnEvent37.copyParams(cevent);
							flowThread.returnFromCall(this, returnEvent37, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 153, 27)));
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
				count = getCount(1608446010) + 1;
				if (event.triggers("sense.user.speak")) {
					if (event.has("sem:no")) {
						incrCount(1608446010);
						eventResult = EVENT_CONSUMED;
						EXECUTION: {
							// Line: 156
							Event returnEvent38 = new Event("dialog.repeat");
							flowThread.returnFromCall(this, returnEvent38, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 156, 35)));
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
				count = getCount(511833308) + 1;
				if (event.triggers("sense.user.speak sense.user.silence")) {
					incrCount(511833308);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						// Line: 159
						Event returnEvent39 = new Event(event.getName());
						returnEvent39.copyParams(event);
						flowThread.returnFromCall(this, returnEvent39, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 159, 26)));
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
