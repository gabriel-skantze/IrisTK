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

import iristk.audio.Microphone;
import iristk.speech.EnergyEndpointer;
import iristk.speech.RecognizerModule;
import iristk.system.Event;
import iristk.system.InitializationException;
import iristk.system.IrisModule;
import iristk.system.IrisSystem;

public class TestNuanceCloud extends IrisModule{

	IrisSystem system;
	
	public TestNuanceCloud() throws Exception {
		system = new IrisSystem(this.getClass());
		Microphone source = new Microphone(16000, 1);
		RecognizerModule rec = new RecognizerModule(new NuanceCloudRecognizer(new EnergyEndpointer(source)));
		system.addModule("nuance", rec);
		system.addModule("listener", this);
		new Thread() {
			@Override
			public void run() {
				try {
					system.sendStartSignal();
				} catch (InitializationException e) {
					e.printStackTrace();
				}
			};
		}.start();
		listen();
	}
	
	private void listen() {
		Event event = new Event("action.listen");
		event.put("endSil", 1000);
		system.onEvent(event);
		System.out.println("LYSSNAR");
	}
	
	public static void main(String[] args) throws Exception {
		new TestNuanceCloud();
	}

	@Override
	public void onEvent(Event event) {
		if (event.getName().equals("sense.speech.rec")) {
			System.out.println(event.getString("text"));
			listen();
		} else if (event.getName().startsWith("sense.speech.rec.")) {
			System.out.println(event.getName());
			listen();
		} else if (event.getName().equals("sense.speech.start")) {
			System.out.println("START OF SPEECH");
		} else if (event.getName().equals("sense.speech.end")) {
			System.out.println("END OF SPEECH");
		}
	}

	@Override
	public void init() throws InitializationException {
		// TODO Auto-generated method stub
		
	}
	
}
