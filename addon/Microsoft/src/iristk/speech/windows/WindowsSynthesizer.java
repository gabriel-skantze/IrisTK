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
package iristk.speech.windows;

import iristk.net.speech.*;
import iristk.audio.Sound;
import iristk.speech.Synthesizer;
import iristk.speech.SynthesizerEngine;
import iristk.speech.Transcription;
import iristk.speech.Voice;
import iristk.speech.VoiceList;
import iristk.speech.Phone;
import iristk.system.InitializationException;
import iristk.util.Language;
import iristk.util.Mapper;
import iristk.util.Replacer;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Arrays;

// TODO: should encode phones to IPA, now SAPI ID is used
// http://msdn.microsoft.com/en-us/library/hh361632(v=office.14).aspx

public class WindowsSynthesizer implements Synthesizer {
	
	static {
		WindowsSpeech.init();
	}

	private final DesktopSynthesizer synth;
	private String currentVoice = "";
	private VoiceList voices = new VoiceList();
	private AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
	private static Mapper ipa2ups = new Mapper("ipa2ups", WindowsSynthesizer.class.getResourceAsStream("ipa2ups.map"));
	private static Mapper sapi2ups = new Mapper("sapi2ups", WindowsSynthesizer.class.getResourceAsStream("sapi2ups.map"));
	private static Mapper vis2ups = new Mapper("vis2ups", WindowsSynthesizer.class.getResourceAsStream("vis2ups.map"));
	
	public WindowsSynthesizer() {
		synth = new DesktopSynthesizer();
		Voices voiceL = synth.getVoices();
		for (int i = 0; i < voiceL.getLength(); i++) {
			Voice.Gender gender = voiceL.getVoice(i).getGender().equals("Female") ? Voice.Gender.FEMALE : Voice.Gender.MALE;
			voices.add(new Voice(this, voiceL.getVoice(i).getName(), gender, new Language(voiceL.getVoice(i).getLang()), true));
		}
	}

	@Override
	public SynthesizerEngine getEngine(Voice voice) throws InitializationException {
		return new MicrosoftSynthesizerEngine(voice);
	}

	@Override
	public VoiceList getVoices() {
		return voices;
	}
	

	@Override
	public String getName() {
		return "Windows";
	}
	
	
	private class MicrosoftSynthesizerEngine implements SynthesizerEngine { 

		private Voice voice;

		MicrosoftSynthesizerEngine(Voice voice) {
			this.voice = voice;
		}

		@Override
		public Transcription synthesize(String text, File audioFile) {
			if (!currentVoice.equals(voice.getName())) {
				currentVoice = voice.getName();
				synth.setVoice(voice.getName());
			}
			String ssml = makeSSML(text);
			Phonemes phonemes = synth.synthesize(ssml, audioFile.getAbsolutePath());
			Transcription trans = makeTrans(phonemes);
			float trim = trans.trimSilence();
			if (trim > 0) {
				try {
					Sound sound = new Sound(audioFile);
					sound.cropSeconds(0, sound.getSecondsLength() - trim);
					sound.save(audioFile);
				} catch (UnsupportedAudioFileException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return trans;
		}
	
		@Override
		public Transcription transcribe(String text) {
			if (!currentVoice.equals(voice.getName())) {
				currentVoice = voice.getName();
				synth.setVoice(voice.getName());
			}
			String ssml = makeSSML(text);
			Phonemes phonemes = synth.transcribe(ssml);
			Transcription trans = makeTrans(phonemes);
			trans.trimSilence();
			return trans;
		}
	
		@Override
		public AudioFormat getAudioFormat() {
			return audioFormat;
		}
		

		private String labelToUnicode(String label) {
			try {
				byte[] b = label.getBytes("UTF-16");
				if (label.length() == 3) {
					return String.format("%02X%02X+%02X%02X", b[2], b[3], b[6], b[7]);
				} else {
					return String.format("%02X%02X", b[2], b[3]);
				} 
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}	
			return "";
		}
		
		private boolean isIPA(Phonemes phonemes) {
			int ipa = 0;
			int sapi = 0;
			for (int i = 0; i < phonemes.getLength(); i++) {
				Phoneme phon = phonemes.getPhoneme(i);
				String unicode = labelToUnicode(phon.getLabel());
				if (ipa2ups.containsKey(unicode))
					ipa++;
				if (sapi2ups.containsKey(unicode))
					sapi++;
			}
			return ipa > sapi;
		}
		
		private Transcription makeTrans(Phonemes phonemes) {
			if (phonemes.isVisemes()) {
				Mapper mapper = vis2ups;
				Transcription trans = new Transcription();
				float pos = 0.0f;
				for (int i = 0; i < phonemes.getLength(); i++) {
					Phoneme phon = phonemes.getPhoneme(i);
					String key = phon.getLabel();
					trans.add(mapper.map(key, Transcription.SILENCE), phon.getWord(), pos, pos + phon.getDuration());
					pos += phon.getDuration();
				}
				return trans;
			} else {
				Mapper mapper = (isIPA(phonemes) ? ipa2ups : sapi2ups);
				Transcription trans = new Transcription();
				float pos = 0.0f;
				boolean compound = false;
				String lastUnicode = "";
				for (int i = 0; i < phonemes.getLength(); i++) {
					Phoneme phon = phonemes.getPhoneme(i);
					String unicode = labelToUnicode(phon.getLabel());
					if (unicode.equals("0361")) {
						compound = true;
					} else if (compound) {
						Phone last = trans.getPhones().remove(trans.getPhones().size() - 1);
						String key = lastUnicode + "+" + unicode;
						trans.add(mapper.map(key, Transcription.SILENCE), phon.getWord(), last.start, pos + phon.getDuration());
						compound = false;
					} else {
						trans.add(mapper.map(unicode, Transcription.SILENCE), phon.getWord(), pos, pos + phon.getDuration());
						lastUnicode = unicode;
						compound = false;
					}
					pos += phon.getDuration();
				}
				return trans;
			}
		}
		
		@SuppressWarnings("unchecked")
		private Set<String> allowedElements = new HashSet<>(Arrays.asList(new String[]{"lexicon", "meta", "metadata", "p", "s", 
				"say-as", "phoneme", "sub", "voice", "emphasis", "break", "prosody", "audio", "mark", "desc"}));
		
		private Replacer ssmlReplacer = new Replacer("</?([^\\s>]*).*?>") {	
			@Override
			public String replace(Matcher matcher) {
				if (allowedElements.contains(matcher.group(1))) {
					return matcher.group(0);
				} else {
					return "";
				}
			}
		};
		
		private String makeSSML(String text) {
			text = ssmlReplacer.replaceAll(text);
			text = text.replaceAll("&(?![a-z]+;)", "&amp;");
			String ssml = "<speak version=\"1.0\"";
			ssml += " xmlns=\"http://www.w3.org/2001/10/synthesis\"";
			ssml += " xml:lang=\"" + voice.getLanguage()+ "\">";
			ssml += text.trim();
			ssml += "</speak>";
			//System.out.println(ssml);
			return ssml;
		}

		@Override
		public Voice getVoice() {
			return voice;
		}
		
	}

	public void printVoices() {
		synth.printVoices();
	}
	
	
}
