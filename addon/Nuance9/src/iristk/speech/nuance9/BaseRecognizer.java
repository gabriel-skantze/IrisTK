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
package iristk.speech.nuance9;

import iristk.speech.RecResult;
import iristk.speech.nuance9.SWIep.SWIepAudioSamples;
import iristk.speech.nuance9.SWIrec.SWIrecAudioSamples;
import iristk.system.IrisUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

import org.apache.commons.io.FileUtils;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class BaseRecognizer extends Thread {
	
	/*
	 * Enpointer parameters
	 
		bargein 
		incompletetimeout 
		maxspeechtimeout 
		secure_context 
		sensitivity 
		swiep_BOS_backoff 
		swiep_EOS_backoff 
		swiep_in_prompt_sensitivity_percent 
		swiep_magic_word_max_msec 
		swiep_magic_word_min_msec 
		swiep_mode 
		swiep_suppress_barge_in_time 
		swiep_suppress_waveform_logging 
		timeout
	
	* Recognizer parameters
	
		completetimeout 
		confidencelevel 
		incompletetimeout 
		maxspeechtimeout 
		secure_context 
		sensitivity 
		speedvsaccuracy 
		swirec_acoustic_adapt_suppress_adaptation 
		swirec_app_state_tokens
		swirec_application_name 
		swirec_barge_in_mode 
		swirec_company_name 
		swiep_EOS_backoff 
		swirec_busy_cpu_start 
		swirec_extra_nbest_keys 
		swirec_grammar_script 
		swirec_grammar_script_sisr 
		swirec_load_adjusted_speedvsaccuracy 
		swirec_magic_word_conf_thresh 
		swirec_max_arcs 
		swirec_max_cpu_time 
		swirec_max_logged_nbest 
		swirec_max_parses_per_literal 
		swirec_max_sentences_tried 
		swirec_nbest_list_length 
		swirec_normal_cpu_start 
		swirec_return_waveform 
		swirec_phoneme_lookahead_beam 
		swirec_selective_barge_in_conf_thresh 
		swirec_silence_prune_offset 
		swirec_state_beam 
		swirec_suppress_event_logging 
		swirec_suppress_waveform_logging 
		swirec_tenant_name 
		swirec_waveform_begin_silence 
		swirec_word_confidence_enabled 
		swissm_confidence_threshold 

	 */ 

	
	public static boolean debug = false;

	private Pointer recHandle;
	private Pointer epHandle;
	private static Boolean initialized = false;
	private HashMap<String,SWIrec.SWIrecGrammarData> grammars = new HashMap<String,SWIrec.SWIrecGrammarData>();
	private boolean makeWords = false;
	private boolean makeNbest = false;

	private HashMap<String,String> recParameters = new HashMap<String,String>();
	private HashMap<String,String> epParameters = new HashMap<String,String>();

	private RecognizerThread recognizerThread;

	private NuanceResult result = null;

	public static String getEncoding(AudioFormat format) throws IllegalArgumentException {
		if (format.getFrameRate() != 8000 && format.getFrameRate() != 16000) 
			throw new IllegalArgumentException("Can only process 8khz or 16khz");
		if (format.isBigEndian())
			throw new IllegalArgumentException("Can only process little-endian");
		if (format.getChannels() != 1)
			throw new IllegalArgumentException("Can only process mono sound");
		if (format.getEncoding() == Encoding.ULAW) 
			return "audio/basic;rate=8000";
		else if (format.getEncoding() == Encoding.PCM_SIGNED) {
			if (format.getFrameSize() != 2) 
				throw new IllegalArgumentException("Can only process 16 bit PCM sound");
			return "audio/L16;rate=8000";
		} else
			throw new IllegalArgumentException("Bad audio encoding: " + format.getEncoding());
	}
	
	static void call(String cmd, int code) throws NuanceException {
		if (code != 0) {
			throw new NuanceException(cmd, code);
		} else {
			if (debug)
				System.out.println(cmd + " Succeeded");
		}
	}

	public void makeWords(boolean cond) {
		this.makeWords = cond;
	}
	
	public void makeNbest(boolean cond) {
		this.makeNbest = cond;
	}
	
	protected void epStart() throws NuanceException {
		for (String param : epParameters.keySet()) {
			SWIepSetParameter(param, epParameters.get(param));
		}
		call("SWIepStart", SWIep.INSTANCE.SWIepStart(epHandle));
	}
	
	protected void epStop() throws NuanceException {
		call("SWIepStop", SWIep.INSTANCE.SWIepStop(epHandle, 0, null));
	}
	
	protected void epPromptDone() throws NuanceException {
		call("SWIepPromptDone", SWIep.INSTANCE.SWIepPromptDone(epHandle));
	}
	
	public void epAcousticStateReset() throws NuanceException {
		call("SWIepAcousticStateReset", SWIep.INSTANCE.SWIepAcousticStateReset(epHandle));
	}
	
	protected void epWrite(SWIepAudioSamples samples, IntByReference state, IntByReference beginSample, IntByReference endSample) throws NuanceException {
		call("SWIepWrite", SWIep.INSTANCE.SWIepWrite(epHandle, samples, state, beginSample, endSample));
	}
	
	protected void epRead(SWIrecAudioSamples samples, IntByReference state, int maxLen) throws NuanceException {
		call("SWIepRead", SWIep.INSTANCE.SWIepRead(epHandle, samples, state, maxLen));
	}
	
	protected void recRecognizerStart() throws NuanceException {
		for (String param : recParameters.keySet()) {
			SWIrecRecognizerSetParameter(param, recParameters.get(param));
		}
		call("SWIrecRecognizerStart", SWIrec.INSTANCE.SWIrecRecognizerStart(recHandle));
	}
	
	protected void recRecognizerStop() throws NuanceException {
		call("SWIrecRecognizerStop", SWIrec.INSTANCE.SWIrecRecognizerStop(recHandle, 0));
	}
	
	protected void recAudioWrite(SWIrecAudioSamples samples) throws NuanceException {
		call("SWIrecAudioWrite", SWIrec.INSTANCE.SWIrecAudioWrite(recHandle, samples));
	}
	
	protected void recRecognizerCompute(int maxComputeTime, IntByReference status, IntByReference type, PointerByReference resultData) throws NuanceException {
		call("SWIrecRecognizerCompute", SWIrec.INSTANCE.SWIrecRecognizerCompute(recHandle, maxComputeTime, status, type, resultData));
	}
	
	protected void SWIrecRecognizerSetParameter(String param, String value) {
		if (SWIrec.INSTANCE.SWIrecRecognizerSetParameter(recHandle, new WString(param), new WString(value)) != 0) {
			System.err.println("Invalid recognizer parameter: " + param + "=" + value);
		}	
	}
	
	protected void SWIepSetParameter(String param, String value) {
		if (SWIep.INSTANCE.SWIepSetParameter(epHandle, new WString(param), new WString(value)) != 0) {
			System.err.println("Invalid endpointer parameter: " + param + "=" + value);
		}
	}
	
	public void setRecParameter(String param, String value) {
		recParameters.put(param, value);
	}
	
	public void setEpParameter(String param, String value) {
		epParameters.put(param, value);
	}
	
	private void init(File config) throws NuanceException {
		synchronized (initialized) {
			if (!initialized) {
				try {
					call("SWIrecInit", SWIrec.INSTANCE.SWIrecInit(new WString(config.getAbsolutePath())));
					call("SWIepInit", SWIep.INSTANCE.SWIepInit());
				} catch (UnsatisfiedLinkError e) {
					System.err.println("ERROR: You must run in 32-bit mode for Nuance to work!");
					throw new NuanceException("Couldn't initialize Nuance");
				}
				initialized = true;
			}
			PointerByReference ref = new PointerByReference();
			call("SWIrecRecognizerCreate", SWIrec.INSTANCE.SWIrecRecognizerCreate(ref, null, null));
			this.recHandle = ref.getValue();
			ref = new PointerByReference();
			call("SWIepDCreate", SWIep.INSTANCE.SWIepDetectorCreate(ref));
			this.epHandle = ref.getValue();
			call("SWIrecSessionStart", SWIrec.INSTANCE.SWIrecSessionStart(recHandle, new WString("1"), null));
			call("SWIepSessionStart", SWIep.INSTANCE.SWIepSessionStart(epHandle, new WString("1"), null));
		}
	}
	
	//public BaseRecognizer(File config) throws NuanceException {
	//	init(config);
	//}
	
	public BaseRecognizer() throws NuanceException {
		File configFile = Nuance9Package.PACKAGE.getPath("config.xml");
		File tempConfigDir = IrisUtils.getTempDir(Nuance9Package.NAME);
		tempConfigDir.mkdirs();
		File tempConfigFile = new File(tempConfigDir, "config.xml");
		try {
			FileUtils.copyFile(configFile, tempConfigFile);
		} catch (IOException e) {
			throw new NuanceException("Couldn't copy config.xml to temp dir " + tempConfigDir.getAbsolutePath());
		}
		init(tempConfigFile);
	}
	
	public void loadGrammar(String id, URI uri) throws NuanceException {
		SWIrec.SWIrecGrammarData grammar = new SWIrec.SWIrecGrammarData();
		grammar.type = new WString("uri");
		grammar.data = new WString(uri.toString());
		call("SWIrecGrammarLoad", SWIrec.INSTANCE.SWIrecGrammarLoad(recHandle, grammar));
		grammars.put(id, grammar);
	}

	public void loadGrammar(String id, String grammarString) throws NuanceException {
		SWIrec.SWIrecGrammarData grammar = new SWIrec.SWIrecGrammarData();
		grammar.type = new WString("string/2.0"); 
		grammar.data = null;
		grammar.properties = null;
		Pointer m = new Memory(grammarString.length() + 1);
		m.setString(0, grammarString);
		grammar.binary_data = m;
		grammar.media_type = new WString("application/srgs+xml");
		grammar.length = grammarString.length() + 1;
		call("SWIrecGrammarLoad", SWIrec.INSTANCE.SWIrecGrammarLoad(recHandle, grammar));
		grammars.put(id, grammar);
	}
	
	public void unloadGrammar(String id) throws NuanceException {
		call("SWIrecGrammarFree", SWIrec.INSTANCE.SWIrecGrammarFree(recHandle, grammars.get(id)));
	}
	
	public void activateGrammar(String id, int weight) throws NuanceException {
		call("SWIrecGrammarActivate", SWIrec.INSTANCE.SWIrecGrammarActivate(recHandle, grammars.get(id), weight, id));
	}
	
	public void activateGrammar(String id, String grammarString, int weight) throws NuanceException {
		loadGrammar(id, grammarString);
		activateGrammar(id, weight);
	}
	
	public void activateGrammar(String id, URI uri, int weight) throws NuanceException {
		loadGrammar(id, uri);
		activateGrammar(id, weight);
	}

	public void acousticStateReset() throws NuanceException {
		call("SWIrecAcousticStateReset", SWIrec.INSTANCE.SWIrecAcousticStateReset(recHandle));
	}
	
	public void deactivateGrammar(String id) throws NuanceException {
		call("SWIrecGrammarDeactivate", SWIrec.INSTANCE.SWIrecGrammarDeactivate(recHandle, grammars.get(id)));
	}

	public synchronized void startRecognize() throws NuanceException {
		result = null;
		log("Starting recognizer");
		recRecognizerStart();
		recognizerThread = new RecognizerThread();
		recognizerThread.start();
	}
	
	public synchronized void stopRecognize() {
		if (isRunning()) {
			recognizerThread.cont = false;
			try {
				log("Stopping recognizer");
				recRecognizerStop();
			} catch (NuanceException e) {
			}
			try {
				recognizerThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static int c = 0;
	private int id = c++;
	public void log(String l) {
		//if (id == 0)
		//	System.out.println(id + ": " + new Timestamp(System.currentTimeMillis()) + ": " + l);
	}
	
	public boolean isRunning() {
		return recognizerThread != null && recognizerThread.running;
	}
	
	public NuanceResult getResult() {
		return result;
	}
	
	private class RecognizerThread extends Thread {
		private boolean running;
		private boolean cont;
		
		public RecognizerThread() {
			super("RecognizerThread");
		}
		
		@Override
		public void run() {
			running = true;
			IntByReference status = new IntByReference();
			IntByReference type = new IntByReference();
			PointerByReference resultRef = new PointerByReference();
			cont = true;
			try {
				OUTER:
				while (cont) {
					recRecognizerCompute(-1, status, type, resultRef);
					if (status.getValue() == SWIrec.SWIrec_STATUS_SUCCESS) {
						result = new NuanceResult(resultRef.getValue(), makeWords, makeNbest);
						break OUTER;
					} else if (status.getValue() == SWIrec.SWIrec_STATUS_NO_MATCH) {
						result = new NuanceResult(RecResult.FINAL, RecResult.NOMATCH);
						break OUTER;
					} else if (status.getValue() == SWIrec.SWIrec_STATUS_STOPPED) {
						result = null;
						break OUTER;	
					} else if (status.getValue() == SWIrec.SWIrec_STATUS_MAX_SPEECH) {
						result = new NuanceResult(RecResult.MAXSPEECH);
						break OUTER;	
					}
				}
			} catch (NuanceException e) {
				e.printStackTrace();
			}
			log("RecognizerThread done");
			running = false;
		}
				
	}
}
