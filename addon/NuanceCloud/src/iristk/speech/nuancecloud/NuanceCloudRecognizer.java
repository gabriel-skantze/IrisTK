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
package iristk.speech.nuancecloud;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import iristk.audio.AudioPort;
import iristk.speech.Endpointer;
import iristk.speech.EndpointerRecognizer;
import iristk.speech.EnergyEndpointer;
import iristk.speech.EnergyVAD;
import iristk.speech.EnergyVADContainer;
import iristk.speech.OpenVocabularyContext;
import iristk.speech.OpenVocabularyRecognizer;
import iristk.speech.RecResult;
import iristk.speech.RecognizerException;
import iristk.speech.RecognizerFactory;
import iristk.speech.RecognizerListeners;
import iristk.system.InitializationException;

public class NuanceCloudRecognizer extends EndpointerRecognizer implements OpenVocabularyRecognizer, EnergyVADContainer {

	private NuanceCloudRecognizerListener nuance;
	private HashSet<String> activeContexts = new HashSet<>();
	private HashMap<String,OpenVocabularyContext> loadedContexts = new HashMap<>();
	
	public NuanceCloudRecognizer() throws RecognizerException, InitializationException {
		this(new EnergyEndpointer());
	}
	
	public NuanceCloudRecognizer(AudioPort audioPort) throws RecognizerException {
		this(audioPort, null);
	}
	
	public NuanceCloudRecognizer(Endpointer endpointer) throws RecognizerException {
		this(endpointer, null);
	}
	
	public NuanceCloudRecognizer(AudioPort audioPort, NuanceCloudLicense license) {
		this(new EnergyEndpointer(audioPort), license);
	}
	
	public NuanceCloudRecognizer(Endpointer endpointer, NuanceCloudLicense license) {
		super(endpointer);
		nuance = new NuanceCloudRecognizerListener(license);
		addRecognizerListener(nuance, RecognizerListeners.PRIORITY_RECOGNIZER);
	}

	@Override
	public RecResult recognizeFile(File file) throws RecognizerException {
		return EndpointerRecognizer.recognizeFile(file, nuance);
	}
	
	@Override
	public void setNbestLength(int length) {
		nuance.setNBestLength(length);
	}

	@Override
	public void activateContext(String name, float weight) throws RecognizerException {
		activeContexts.add(name);
		checkActive();
	}

	@Override
	public void deactivateContext(String name) throws RecognizerException {
		activeContexts.remove(name);
		checkActive();
	}

	private void checkActive() {
		for (String context : loadedContexts.keySet()) {
			if (activeContexts.contains(context)) {
				nuance.setActive(true);
				nuance.setLanguage(loadedContexts.get(context).language);
				return;
			}
		}
		nuance.setActive(false);
	}

	@Override
	public void loadOpenVocabulary(String contextName, OpenVocabularyContext context) throws RecognizerException {
		loadedContexts.put(contextName, context);
	}

	@Override
	public void unloadOpenVocabulary(String contextName) throws RecognizerException {
		loadedContexts.remove(contextName);
	}

	@Override
	public EnergyVAD getEnergyVAD() {
		if (getEndpointer() instanceof EnergyEndpointer) {
			return ((EnergyEndpointer)getEndpointer()).getEnergyVAD();
		} else {
			return null;
		}
	}

	@Override
	public RecognizerFactory getRecognizerFactory() {
		return new NuanceCloudRecognizerFactory();
	}

}
