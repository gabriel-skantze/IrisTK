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

import iristk.audio.Sound;
import iristk.audio.SoundPlayer;
import java.io.*;
import java.util.ArrayList;

import javax.sound.sampled.UnsupportedAudioFileException;

public class BatchRec {

	private SoundPlayer soundPlayer = null;
	
	public void run(File batchset, File audioPath, Recognizer recognizer, boolean playback, OutputStream... outs) throws IOException {
		ArrayList<PrintStream> printStreams = new ArrayList<PrintStream>();
		for (OutputStream out : outs) {
			printStreams.add(new PrintStream(out));
		}
		BufferedReader br = new BufferedReader(new FileReader(batchset));
		String line;
		while ((line = br.readLine()) != null) {
			line = line.trim().replaceAll("  +", " ");
			if (line.length() > 0) {
				String filename, ref = null;
				String cols[] = line.split("\t");
				if (cols.length == 2) {
					filename = cols[0];
					ref = cols[1];
				} else {
					filename = line;
				}
				File audioFile = new File(audioPath, filename);
				if (playback && audioFile.exists()) {
					Sound sound;
					try {
						sound = new Sound(audioFile);
						if (soundPlayer == null) {
							soundPlayer = new SoundPlayer(sound.getAudioFormat());
						}
						soundPlayer.playAsync(sound);
					} catch (UnsupportedAudioFileException e1) {
						e1.printStackTrace();
					}
				}
				for (PrintStream ps : printStreams) {
					if (ref == null) {
						ps.print(filename + "\t");
					} else {
						ps.print(filename + "\t" + ref + "\t");
					}
				}
				if (!audioFile.exists()) {
					for (PrintStream ps : printStreams) {
						ps.println("ERROR: could not find file");
					}
				} else {
					String rstring = "";
					try {
						RecResult result = recognizer.recognizeFile(audioFile);
						rstring = result.text;
					} catch (RecognizerException e) {
						rstring = "ERROR: " + e.getMessage(); 
					} 
					for (PrintStream ps : printStreams) {
						ps.println(rstring);
					}
				}
				if (playback && audioFile.exists()) {
					soundPlayer.waitForPlayingDone();
				}
			}
		}
	}

/*
	public void run(String[] args) throws IOException {
		ArgParser argParser = new ArgParser();
		argParser.addOptionalArg("b", "Batch file", "filename", String.class, false);
		argParser.addOptionalArg("g", "Grammar file", "filename", String.class, false);
		argParser.addOptionalArg("a", "Audio file path", "path", String.class, false);
		argParser.addOptionalArg("r", "Result file", "filename", String.class, false);
		argParser.addOptionalArg("l", "Language", "lang", String.class, false);
		argParser.addBooleanArg("p", "Playback", false);
		argParser.addBooleanArg("e", "Evaluate", false);
		argParser.parse(args);
		
		if (argParser.has("l")) {
			getRecognizer().setLanguage(Language.fromCode(argParser.get("l").toString()));
		}
		if (argParser.has("g")) {
			try {
				getRecognizer().loadGrammar("default", argParser.get("g").toString());
				getRecognizer().activateGrammar("default", 1);
			} catch (RecognizerException e) {
				e.printStackTrace();
			}
		}
		playback = argParser.has("p");
		if (argParser.has("b")) {
			File batchSet = new File(argParser.get("b").toString());
			File audioPath = new File(".");
			if (argParser.has("a")) {
				audioPath = new File(argParser.get("a").toString());
			}
			if (argParser.has("r")) {
				FileOutputStream out = new FileOutputStream(new File(argParser.get("r").toString()));
				run(batchSet, audioPath, System.out, out);
				out.close();
			} else {
				run(batchSet, audioPath, System.out);
			}		
		}
		if (argParser.has("e") && argParser.has("r")) {
			evaluate(new File(argParser.get("r").toString()));
		}
	}
*/
	public static void evaluate(File resultSet) {
		NISTAlign align = new NISTAlign(true, true);

		try {
			BufferedReader batchFile = new BufferedReader(new FileReader(resultSet));
			String line;
			try {
				while ((line = batchFile.readLine()) != null) {
					String[] cols = line.trim().split("\t");
					if (cols.length == 3) {
						if (!cols[2].startsWith("ERROR")) { 
							align.align(cols[1], cols[2]);
							align.printNISTSentenceSummary();
						}
					}
				}
			} catch (java.io.IOException e) {
			}
			align.printNISTTotalSummary();
		} catch (Exception e) {
			System.err.println(e);
		}
	}

}
