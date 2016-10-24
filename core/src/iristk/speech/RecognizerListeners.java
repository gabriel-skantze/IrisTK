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

import java.util.*;

import javax.sound.sampled.AudioFormat;

/**
 * A pipeline of RecognizerListener:s or RecognizerProcessor:s that process and/or listens to recognition events serially, 
 * allowing them to add information to the recognition result.
 * Each RecognizerListener can be added with a priority in order to sort them in the pipeline. 
 */
public class RecognizerListeners implements RecognizerListener {

	public static final int PRIORITY_RECOGNIZER = -100;
	public static final int PRIORITY_PROSODY = -50;
	public static final int PRIORITY_SECONDARY_RECOGNIZER = 0;
	public static final int PRIORITY_SEMANTICS = 90;
	public static final int PRIORITY_FINAL = 100;
	
	ArrayList<RecognizerProcessor> pipeline = new ArrayList<>();
	HashMap<RecognizerProcessor,Integer> priorities = new HashMap<>();
	
	/**
	 * @param priority The listeners with the lowest priority will be called first
	 */
	public void add(RecognizerListener listener, int priority) {
		RecognizerProcessor processor;
		if (listener instanceof RecognizerProcessor) {
			//((RecognizerProcessor)listener).setRecognizerListener(new ProcessorListener(listener));
			processor = (RecognizerProcessor)listener;
		} else {
			processor = new ListenerWrapper(listener);
		}
		priorities.put(processor, priority);
		ADD: {
		for (int i = 0; i < pipeline.size(); i++) {
			int lp = priorities.get(pipeline.get(i));
			if (priority < lp) {
				pipeline.add(i, processor);
				break ADD;
			}
		}
		pipeline.add(processor);
		}
		for (int i = 0; i < pipeline.size(); i++) {
			if (i < pipeline.size()-1)
				pipeline.get(i).setNext(pipeline.get(i+1));
			else
				pipeline.get(i).setNext(null);
		}
	}
	
	private class ListenerWrapper extends RecognizerProcessor {

		private RecognizerListener listener;

		public ListenerWrapper(RecognizerListener listener) {
			this.listener = listener;
		}

		@Override
		public void initRecognition(AudioFormat format) {
			listener.initRecognition(format);
			if (getNext() != null)
				getNext().initRecognition(format);
		}

		@Override
		public void startOfSpeech(float timestamp) {
			listener.startOfSpeech(timestamp);
			if (getNext() != null)
				getNext().startOfSpeech(timestamp);
		}

		@Override
		public void endOfSpeech(float timestamp) {
			listener.endOfSpeech(timestamp);
			if (getNext() != null)
				getNext().endOfSpeech(timestamp);
		}

		@Override
		public void speechSamples(byte[] samples, int pos, int len) {
			listener.speechSamples(samples, pos, len);
			if (getNext() != null)
				getNext().speechSamples(samples, pos, len);
		}

		@Override
		public void recognitionResult(RecResult result) {
			listener.recognitionResult(result);
			if (getNext() != null)
				getNext().recognitionResult(result);
		}
		
	}
	
	
	//public boolean hasListener(RecognizerListener listener) {
	//	return listeners.contains(listener);
	//}
	
	@Override
	public void startOfSpeech(float timestamp) {
		if (pipeline.size() > 0)
			pipeline.get(0).startOfSpeech(timestamp);
	}

	@Override
	public void endOfSpeech(float timestamp) {
		if (pipeline.size() > 0)
			pipeline.get(0).endOfSpeech(timestamp);
	}

	@Override
	public void speechSamples(byte[] samples, int pos, int len) {
		if (pipeline.size() > 0)
			pipeline.get(0).speechSamples(samples, pos, len);
	}

	@Override
	public void recognitionResult(RecResult result) {
		if (pipeline.size() > 0)
			pipeline.get(0).recognitionResult(result);
	}

	@Override
	public void initRecognition(AudioFormat format) {
		if (pipeline.size() > 0)
			pipeline.get(0).initRecognition(format);
	}
	
}
