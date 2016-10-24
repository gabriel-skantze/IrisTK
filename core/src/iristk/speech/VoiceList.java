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

import iristk.speech.Voice.Gender;
import iristk.util.Language;

import java.util.ArrayList;

public class VoiceList extends ArrayList<Voice> {

	public VoiceList getByName(String name) throws VoiceNotFoundException {
		VoiceList result = new VoiceList();
		for (Voice voice : this) {
			if (voice.getName().toUpperCase().contains(name.toUpperCase()) || voice.getUniqueName().equals(name))
				result.add(voice);
		}
		if (result.size() == 0)
			throw new VoiceNotFoundException("No voice with the name '" + name + "' was found");
		return result;
	}
	
	public VoiceList getByLanguage(Language lang) throws VoiceNotFoundException {
		VoiceList result = new VoiceList();
		for (Voice voice : this) {
			if (voice.getLanguage().equals(lang)) {
				result.add(voice);
			}
		}
		if (result.size() == 0) {
			// No exact languages found, look for languages in other dialects
			for (Voice voice : this) {
				if (voice.getLanguage().equalsIgnoreDialect(lang)) {
					result.add(voice);
				}
			}
		}
		if (result.size() == 0)
			throw new VoiceNotFoundException("No voice with the language '" + lang.getCode() + "' was found");
		return result;
	}
	
	public VoiceList getByGender(Gender gender) throws VoiceNotFoundException {
		VoiceList result = new VoiceList();
		for (Voice voice : this) {
			if (voice.getGender() == gender)
				result.add(voice);
		}
		if (result.size() == 0)
			throw new VoiceNotFoundException("No voice with the gender '" + gender.name() + "' was found");
		return result;
	}
	
	public Voice getFirst() throws VoiceNotFoundException {
		if (size() == 0)
			throw new VoiceNotFoundException("No voice found");
		else
			return get(0);
	}

	/**
	 * 
	 * @return a list of voices that supports transcription 
	 * @throws VoiceNotFoundException
	 */
	public VoiceList getSupportsTranscription() throws VoiceNotFoundException {
		VoiceList result = new VoiceList();
		for (Voice voice : this) {
			if (voice.supportsTranscription())
				result.add(voice);
		}
		if (result.size() == 0)
			throw new VoiceNotFoundException("No voice that supports transcription was found");
		return result;
	}
	
}
