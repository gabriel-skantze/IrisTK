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

import java.io.File;

import iristk.project.Project;
import iristk.system.IrisUtils;
import iristk.util.Language;

public class Voice {

	public enum Gender {
		MALE, FEMALE, NEUTRAL;

		public static Gender fromString(String string) {
			for (Gender gender : values()) {
				if (gender.name().equalsIgnoreCase(string))
					return gender;
			}
			return null;
		}
		
		public static Gender fromInt(int value){
			switch(value){
				case 0:
					return NEUTRAL;
				case 1:
					return FEMALE;
				case 2:
					return MALE;
				default:
					return null;
			}
		}
	}

	private final String name;
	private final Language language;
	private final Gender gender;
	private final Synthesizer synthesizer;
	private boolean supportsTranscription;

	public Voice(Synthesizer synthesizer, String name, Gender gender, Language language, boolean supportsTranscription) {
		this.synthesizer = synthesizer;
		this.name = name;
		this.language = language;
		this.gender = gender;
		this.supportsTranscription = supportsTranscription;
	}

	public String getName() {
		return name;
	}

	public Language getLanguage() {
		return language;
	}

	public Gender getGender() {
		return gender;
	}
	
	public String getUniqueName() {
		return synthesizer.getName() + " - " + getName() + " - " + getLanguage().getCode();
	}

	boolean supportsTranscription() { 
		return supportsTranscription;
	}

	@Override
	public String toString() {
		return getUniqueName();
	}

	public File getPrerecPath() {
		return new File(Project.main.getPackage(synthesizer.getClass()).getPath(), "voices/" + name + "/prerec");
	}
	
	public File getCachePath() {
		File cachePath = IrisUtils.getTempDir("Synthesizer/" + synthesizer.getClass().getSimpleName() + "/" + name);
		if (!cachePath.exists()) {
			cachePath.mkdirs();
		}
		return cachePath;
	}
}
