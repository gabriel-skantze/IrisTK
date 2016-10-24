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
package iristk.kinect;

import org.slf4j.Logger;
import iristk.audio.AudioPort;
import iristk.net.speech.*;
import iristk.speech.RecognizerException;
import iristk.speech.windows.WindowsRecognizer;
import iristk.system.InitializationException;
import iristk.system.IrisUtils;
import iristk.util.Language;

public class KinectRecognizer extends WindowsRecognizer {

	private static Logger logger = IrisUtils.getLogger(WindowsRecognizer.class);
	
	public KinectRecognizer(AudioPort audioPort) throws InitializationException {
		super(audioPort);
	}
	
	@Override
	protected IRecognizer createRecognizer(Language language) throws RecognizerException {
		if (!SpeechPlatformRecognizer.getLanguages(true).contains(language.getCode())) {
			String supported = SpeechPlatformRecognizer.getLanguages(true).trim();
			if (supported.length() == 0)
				throw new RecognizerException("KinectRecognizer not supported on this system");
			else
				throw new RecognizerException("Language " + language  + " not supported by KinectRecognizer. Supported languages: " + supported);
		}
		return new SpeechPlatformRecognizer(language.getCode(), true);
	}

}
