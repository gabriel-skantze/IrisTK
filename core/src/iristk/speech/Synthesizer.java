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

import iristk.system.InitializationException;

public interface Synthesizer {
	
	/**
	 * @return a list of voices supported by this synthesizer
	 */
	VoiceList getVoices();
	
	/**
	 * @return a synthesizer engine for the voice
	 */
	SynthesizerEngine getEngine(Voice voice) throws InitializationException;
	
	/**
	 * @return a pretty name for the synthesizer
	 */
	String getName();
	
}
