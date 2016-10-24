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

import iristk.agent.face.FaceModule;
import iristk.audio.SoundPlayer;
import iristk.project.Project;
import iristk.speech.Voice.Gender;
import iristk.system.Event;
import iristk.system.InitializationException;
import iristk.system.IrisSystem;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TestSynth {

	IrisSystem system;
	JTextArea textField;
	SoundPlayer player;
	SynthesizerModule synthModule = null;
	//private HashMap<String,SynthesizerModule> synthmap = new HashMap<String,SynthesizerModule>();
	
	private HashMap<String,Synthesizer> synthmap = new HashMap<>();
	private HashMap<String,Voice> voicemap = new HashMap<>();
	
	private JComboBox<String> synthList;
	private JComboBox<String> voiceList;
	private String currentSynthName = "";
	private boolean updating = false;
	private boolean agent = false;
	private JFrame window;
	private JButton agentButton;
	private Voice currentVoice;

	public TestSynth() throws Exception {
		system = new IrisSystem("Test Synthesizer");

		synthModule = new SynthesizerModule();
		system.addModule(synthModule);
		
		window = new JFrame("Test Synthesizer");

		ArrayList<String> synthesizers = new ArrayList<>();
		synthesizers.add("");
		synthesizers.addAll(Project.main.getPackageProvides("iristk.speech.Synthesizer"));

		synthList = new JComboBox<String>(synthesizers.toArray(new String[0]));
		voiceList = new JComboBox<String>();
		synthList.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (!updating) {
					setSynth();
				}
			}
		});
		voiceList.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (!updating) {
					setVoice();
				}
			}
		});

		textField = new JTextArea();
		textField.setWrapStyleWord(true);

		JButton playButton = new JButton("Speak");
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = textField.getText().trim();
				startSpeaking(text);
			}
		});
		JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopSpeaking();
			}
		});
		JButton saveButton = new JButton("Save...");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAudio();
			}
		});
		
		agentButton = new JButton("Show agent");
		agentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				agentButton.setEnabled(false);
				agent = true;
				synthModule.doLipsync(true);
				String model = "male";
				if (currentVoice != null && currentVoice.getGender() == Gender.FEMALE) {
					model = "female";
				}
				try {
					system.addModule(new FaceModule(model));
				} catch (InitializationException e1) {
					e1.printStackTrace();
				}
			}
		});

		JPanel top = new JPanel(new FlowLayout());
		top.add(synthList);
		top.add(voiceList);
		top.add(agentButton);
		window.add(top, BorderLayout.PAGE_START);
		window.add(textField, BorderLayout.CENTER);
		
		JPanel bottom = new JPanel(new FlowLayout());
		window.add(bottom, BorderLayout.PAGE_END);
		bottom.add(playButton);
		bottom.add(stopButton);
		bottom.add(saveButton);

		window.setPreferredSize(new Dimension(800,600));
		window.pack();
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void setVoice() {
		String item = (String) voiceList.getSelectedItem();
		if (item.length() == 0) {
			currentVoice = null;
		} else if (currentVoice == null || !item.equals(currentVoice.getName())) {
			try {
				currentVoice = voicemap.get(item);
				synthModule.setVoice(currentVoice);
			} catch (VoiceNotFoundException e) {
				e.printStackTrace();
			} catch (InitializationException e) {
				e.printStackTrace();
			}
		}
	}

	private void setSynth() {
		updating  = true;
		try {
			String selected = (String) synthList.getSelectedItem();
			if (selected.length() == 0) {
				currentVoice = null;
				currentSynthName = "";
			} else if (!selected.equals(currentSynthName)) {
				currentVoice = null;
				currentSynthName = selected;
				Synthesizer synth;
				if (!synthmap.containsKey(currentSynthName)) {
					synth = (Synthesizer)Class.forName(currentSynthName).newInstance();
					synthmap.put(currentSynthName, synth);
					synthModule.addSynthesizer(synth);
				} else {
					synth = synthmap.get(currentSynthName);
				}
		    	voiceList.removeAllItems();
		    	voiceList.addItem("");
				voicemap.clear();
				for (Voice voice : synth.getVoices()) {
					voiceList.addItem(voice.getName());
					voicemap.put(voice.getName(), voice);
				}
				voiceList.setSelectedIndex(0);
				//setVoice();
			}
		} catch (Exception e) {
			System.out.println("Error initializing " + synthList.getSelectedItem() + ": " + e.getMessage());
			synthList.setSelectedItem("");
			voiceList.removeAllItems();
		}
		updating = false;
	}

	private String speechAction;
	
	public void startSpeaking(String text) {
		if (currentVoice != null) {
			Event event = new Event("action.speech");
			event.put("text", text);
			event.put("abort", true);
			system.send(event);
			speechAction = event.getId();
		}
	}
	
	public void stopSpeaking() {
		if (speechAction != null) {
			Event event = new Event("action.speech.stop");
			event.put("action", speechAction);
			system.send(event);
		}
	}
	
	private void saveAudio() {
		if (currentVoice != null) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter("WAV files", "wav"));
			if (fileChooser.showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {
			  File file = fileChooser.getSelectedFile();
			  if (file != null) {
				  if (!file.getName().endsWith(".wav"))
					  file = new File(file.getAbsolutePath() + ".wav");
				  synthModule.getCurrentEngine().synthesize(textField.getText().trim(), file);
			  }
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new TestSynth();
	}

}
