/*******************************************************************************
 * Copyright (c) 2014 Gabriel Skantze.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Gabriel Skantze - initial API and implementation
 ******************************************************************************/
package iristk.speech;

import iristk.audio.AudioPort;
import iristk.system.Event;
import iristk.system.InitializationException;
import iristk.system.IrisGUI;
import iristk.system.IrisModule;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Console extends IrisModule {

	private JTextPane textPane;
	private StyledDocument doc;
	private String actionId;
	private Integer timeout;
	private String lastAction = "";
	private int lastPos = 0;
	private boolean startOfSpeech;
	private JTextField textInput;
	//private boolean synthesizer = true;
	//private boolean recognizer = true;
	private JPanel window;
	
	private Map<String,Color> colorMap = new HashMap<>();
	private ArrayList<Color> colorList = new ArrayList<>();
	
	private ConsoleRecognizer recognizer;
	
	{
		colorList.add(Color.CYAN);
		colorList.add(Color.MAGENTA);
		colorList.add(Color.PINK);
		colorList.add(Color.ORANGE);
		colorList.add(Color.BLUE);
		colorList.add(Color.GREEN);
	}
	
	
	public Console(IrisGUI gui) {
		window = new JPanel(new BorderLayout());
		textPane = new JTextPane();
		textPane.setEditable(false);
		doc = textPane.getStyledDocument();

		window.add(new JScrollPane(textPane));
		
		textInput = new JTextField();
		textInput.setEditable(false);
		textInput.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent key) {	
			}
			@Override
			public void keyReleased(KeyEvent key) {
			}
			@Override
			public void keyPressed(KeyEvent key) {
				if (key.getKeyCode() == 10) {
					sendSpeech(textInput.getText());
					textInput.setText("");
					textInput.setEditable(false);
				} else if (!startOfSpeech) {
					startOfSpeech();
				}
			}
		});
		
		window.add(textInput, BorderLayout.PAGE_END);
		
		gui.addDockPanel("console", "Console", window, true);
	}
	
	/*
	public void useSynthesizer(boolean cond) {
		this.synthesizer = cond;
	}

	public void useRecognizer(boolean cond) {
		this.recognizer = cond;
	}
	*/
	
	@Override
	public void init() throws InitializationException {
	}

	private void startOfSpeech() {
		((ConsoleRecognizer)getRecognizer()).getListeners().startOfSpeech(0);
		startOfSpeech = true;
	}

	private void sendSpeech(String text) {
		if (text.length() > 0) {
			((ConsoleRecognizer)getRecognizer()).getListeners().endOfSpeech(3);
			RecResult result = new RecResult(RecResult.FINAL);
			result.put("text", text);
			((ConsoleRecognizer)getRecognizer()).getListeners().recognitionResult(result);
		} else {
			RecResult result = new RecResult(RecResult.SILENCE);
			((ConsoleRecognizer)getRecognizer()).getListeners().recognitionResult(result);
		}
	}

	@Override
	public void onEvent(final Event event) {
		if (event.getName().equals("action.speech")) {
			
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						String text = getText(event);
						textPane.setParagraphAttributes(getStyle(event.getString("agent", "system")), true);
						doc.insertString(doc.getLength(), text + "\n", null);
						textPane.setCaretPosition(doc.getLength());
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
			});

			//if (!synthesizer) {
			//	Event speech = new Event("monitor.speech.end");
			//	speech.put("action", event.getId());
			//	send(speech);
			//}
			
		} else if (event.getName().equals("sense.speech.rec")) {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					addSpeechRecResult(event);
				}
			});
		} else if (event.getName().equals("sense.speech.partial")) {
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					addPartialSpeechRecResult(event);
				}
			});
		} else if (event.getName().equals("action.listen")) {
			if (recognizer != null) {
				textInput.setEditable(true);
				actionId = event.getId();
				timeout = event.getInteger("timeout");
				startOfSpeech = false;
			}
		} 
	}
	
	private String getText(Event event) {
		if (event.has("display")) {
			return event.getString("display");
		} else if (event.has("text")) {
			String text = event.getString("text");
			text = text.replaceAll("<.*?>", "").trim();
			if (text.length() == 0)
				text = "?";
			return text;
		} else {
			return "???";
		}
	}

	protected synchronized void addPartialSpeechRecResult(Event event) {
		try {
			String action = event.getString("action") + event.getString("sensor");
			if (action.equals(lastAction)) 
				doc.remove(lastPos, doc.getLength() - lastPos);
			lastPos = doc.getLength();
			lastAction = action;
			String text = getText(event);
			textPane.setParagraphAttributes(getStyle(event.getString("sensor", "user")), true);
			doc.insertString(doc.getLength(), text + "\n", null);
			textPane.setCaretPosition(doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	protected synchronized void addSpeechRecResult(Event event) {
		try {
			String action = event.getString("action") + event.getString("sensor");
			if (action.equals(lastAction)) 
				doc.remove(lastPos, doc.getLength() - lastPos);
			lastPos = doc.getLength();
			lastAction = action;
			String text = getText(event);
			textPane.setParagraphAttributes(getStyle(event.getString("sensor", "user")), true);
			doc.insertString(doc.getLength(), text + "\n", null);
			textPane.setCaretPosition(doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	protected AttributeSet getStyle(String key) {
		SimpleAttributeSet style = new SimpleAttributeSet();
		Color color;
		if (key.equals("system"))
			color = Color.RED;
		else
			color = getColor(key);
		StyleConstants.setForeground(style, color);
		return style;
	}
	
	private Color getColor(String key) {
		if (!colorMap.containsKey(key)) {
			Color color = colorList.get(colorList.size()-1);
			colorMap.put(key, color);
		}
		Color color = colorMap.get(key);
		colorList.remove(color);
		colorList.add(0, color);
		return color;
	}
	
	public Recognizer getRecognizer() {
		if (recognizer == null)
			recognizer = new ConsoleRecognizer();
		return recognizer;
	}
	
	private static class ConsoleRecognizer implements Recognizer {
		
		private RecognizerListeners listeners = new RecognizerListeners();
		
		public RecognizerListeners getListeners() {
			return listeners;
		}
		
		@Override
		public void startListen() throws RecognizerException {
		}

		@Override
		public boolean stopListen() throws RecognizerException {
			return false;
		}

		@Override
		public void setNoSpeechTimeout(int msec) throws RecognizerException {
		}

		@Override
		public void setEndSilTimeout(int msec) throws RecognizerException {
		}

		@Override
		public void setMaxSpeechTimeout(int msec) throws RecognizerException {
		}

		@Override
		public void addRecognizerListener(RecognizerListener listener, int priority) {
			listeners.add(listener, priority);
		}

		@Override
		public AudioFormat getAudioFormat() {
			return null;
		}

		@Override
		public AudioPort getAudioPort() {
			return null;
		}

		@Override
		public void setPartialResults(boolean cond) {
		}

		@Override
		public RecResult recognizeFile(File file) throws RecognizerException {
			return null;
		}

		@Override
		public void setNbestLength(int length) {
		}

		@Override
		public void activateContext(String name, float weight) throws RecognizerException {
		}

		@Override
		public void deactivateContext(String name) throws RecognizerException {
		}

		@Override
		public RecognizerFactory getRecognizerFactory() {
			return null;
		}
		
	}
}
