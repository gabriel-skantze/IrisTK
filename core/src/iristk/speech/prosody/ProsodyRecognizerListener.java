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
package iristk.speech.prosody;

import iristk.speech.RecResult;
import iristk.speech.RecognizerListener;

import javax.sound.sampled.AudioFormat;

public class ProsodyRecognizerListener implements RecognizerListener {

	private ProsodyTracker prosodyTracker;
	private ProsodyFeatureExtractor extractor;
	private ProsodyNormalizer normalizer;

	public ProsodyRecognizerListener(AudioFormat format) {
		this.prosodyTracker = new ProsodyTracker(format);
		normalizer = new ProsodyNormalizer();
		prosodyTracker.addProsodyListener(normalizer);
		extractor = new ProsodyFeatureExtractor(normalizer);
		prosodyTracker.addProsodyListener(extractor);
	}
	
	@Override
	public void recognitionResult(RecResult result) {
		result.put("prosody", extractor.getFeatures());
	}

	@Override
	public void speechSamples(byte[] samples, int pos, int length) {
		prosodyTracker.listenAudio(samples, pos, length);
	}

	@Override
	public void initRecognition(AudioFormat format) {
		extractor.newSegment();
	}

	@Override
	public void startOfSpeech(float timestamp) {
	}

	@Override
	public void endOfSpeech(float timestamp) {
	}

}
