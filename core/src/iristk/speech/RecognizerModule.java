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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import org.slf4j.Logger;
import iristk.situated.SensorModule;
import iristk.system.Event;
import iristk.system.InitializationException;
import iristk.system.IrisUtils;
import iristk.util.NameFilter;

public class RecognizerModule extends SensorModule implements RecognizerListener {

	private static Logger logger = IrisUtils.getLogger(RecognizerModule.class);

	private String listenActionId = null;
	private boolean inSpeech = false;
	private Float segmentLength;
	private Float startTime;
	private NameFilter defaultContext = NameFilter.ALL;
	private Recognizer recognizer;
	private ArrayList<Context> loadedContexts = new ArrayList<>();
	private HashSet<Context> activatedContexts = new HashSet<>();
	//private RecognizerListeners listenerProxy = new RecognizerListeners();
	private List<Recognizer> recognizers = new ArrayList<>();
	private boolean partialResults = false;
	private HashMap<Integer,RecognizerListener> semanticProcessors = new HashMap<>();
	private long endOfSpeechTime;

	public RecognizerModule(Recognizer recognizer) {
		setRecognizer(recognizer);
		//listenerProxy.add(this, RecognizerListeners.PRIORITY_FINAL);
	}

	@Override
	public void init() throws InitializationException {
		super.init();
	}

	public void setRecognizer(Recognizer rec) {
		if (recognizer != null) {
			try {
				recognizer.stopListen();
			} catch (RecognizerException e) {
			}
		}
		recognizer = rec;
		if (!recognizers.contains(recognizer)) {
			recognizer.addRecognizerListener(this, RecognizerListeners.PRIORITY_FINAL);
			recognizers.add(recognizer);
		}
	}

	public Recognizer getRecognizer() {
		return recognizer;
	}

	public void setDefaultContext(String contextName) {
		defaultContext = NameFilter.compile(contextName);
	}

	public void loadContext(String name, Context context) throws RecognizerException {
		context.name = name;
		loadContext(context);
	}

	private void loadContext(Context context) throws RecognizerException {
		//logger.info("Loading context " + context);
		context.load(this);
		loadedContexts.add(context);
	}

	public void unloadContext(String contextNameFilter) throws RecognizerException {
		NameFilter cnf = NameFilter.compile(contextNameFilter);
		for (Context context : new ArrayList<Context>(loadedContexts)) {
			if (cnf.accepts(context.name)) {
				//System.out.println("Unloading context " + context);
				context.unload(this);
				loadedContexts.remove(context);
			}
		}
	}

	@Override
	public void startOfSpeech(float timestamp) {
		inSpeech = true;
		startTime = timestamp;
		Event event = new Event("sense.speech.start");
		if (getSensor() != null)
			event.put("sensor", getSensor().id);
		if (listenActionId != null)
			event.put("action", listenActionId);
		monitorState("Speech");
		send(event);
	}

	@Override
	public void endOfSpeech(float timestamp) {
		inSpeech = false;
		segmentLength = timestamp - startTime;
		endOfSpeechTime = System.currentTimeMillis();
		Event event = new Event("sense.speech.end");
		if (getSensor() != null)
			event.put("sensor", getSensor().id);
		event.put("length", segmentLength);
		if (listenActionId != null)
			event.put("action", listenActionId);
		monitorState();
		send(event);
	}

	@Override
	public void speechSamples(byte[] samples, int pos, int len) {
	}

	@Override
	public void recognitionResult(RecResult result) {
		log("Y: " + result.type);
		if (!result.isPartial()) {
			//listening = false;
			monitorState();
		}
		if (result != null) {
			if (segmentLength != null) {
				result.put("length", segmentLength);
				result.put("rectime", (System.currentTimeMillis() - endOfSpeechTime) / 1000f);
			}
			sendResult(result);
		}
	}

	private void sendResult(RecResult result) {
		String name;
		if (result.type.equals(RecResult.FINAL))
			name="sense.speech.rec";
		else if (result.type.equals(RecResult.PARTIAL))
			name="sense.speech.partial";
		else 
			name="sense.speech.rec." + result.type;
		Event event = new Event(name);
		event.putAllExceptNull(result);
		if (getSensor() != null)
			event.put("sensor", getSensor().id);
		if (listenActionId != null)
			event.put("action", listenActionId);
		send(event);
	}

	private void activateContext(NameFilter nameFilter) throws RecognizerException {
		for (Context context : loadedContexts) {
			if (nameFilter.accepts(context.name)) {
				if (!activatedContexts.contains(context)) {
					context.activate(this);
					activatedContexts.add(context);
				}
			} else if (activatedContexts.contains(context)) {
				context.deactivate(this);
				activatedContexts.remove(context);
			}
		}
	}


	private static int c = 0;
	private int id = c++;

	public void log(String l) {
		//System.out.println(id + ": " + new Timestamp(System.currentTimeMillis()) + ": " + l);
	}

	@Override
	public void onEvent(Event event) {
		super.onEvent(event);
		if (event.triggers("action.listen")) {

			try {
				String recname = event.getString("recognizer");
				if (recname ==  null || recname.equals(getName())) {
					log(event.getName());
					recognizer.stopListen();

					if (event.has("context"))
						activateContext(NameFilter.compile(event.getString("context")));
					else
						activateContext(defaultContext);

					//TODO: check this
					//if (recognizer instanceof GrammarRecognizer && activatedGrammars.size() == 0) {
					//	throw new RecognizerException("No contexts activated");
					//}

					Event monitor = new Event("monitor.listen.start");
					if (getSensor() != null)
						monitor.put("sensor", getSensor().id);
					listenActionId  = event.getId();
					monitor.put("action", listenActionId);
					send(monitor);	

					inSpeech = false;
					segmentLength = null;
					startTime = null;
					monitorState("Listen");
					recognizer.setEndSilTimeout(event.getInteger("endSilTimeout", 500));
					recognizer.setNoSpeechTimeout(event.getInteger("noSpeechTimeout", 5000));
					recognizer.setMaxSpeechTimeout(event.getInteger("maxSpeechTimeout", 10000));
					recognizer.setNbestLength(event.getInteger("nbest", 1));
					recognizer.setPartialResults(partialResults);
					recognizer.startListen();
				}

			} catch (RecognizerException e) {
				logger.error("Problem start listening", e);
			}
		} else if (event.triggers("action.listen.stop")) {
			if (!event.has("action") || event.getString("action").equals(listenActionId)) {
				try {
					if (recognizer.stopListen())
						monitorState();
				} catch (Exception e) {
					logger.error("Problem stop listening", e);
				}
			}
		} else if (event.triggers("action.context.load")) {
			Event monitor = new Event("monitor.context.load");
			monitor.put("action", event.getId());
			try {
				Context context = (Context) event.getRecord("context");
				monitor.put("context", context.name);
				monitor.put("language", context.language);
				loadContext(context);
				monitor.put("success", true);
			} catch (RecognizerException e) {
				logger.error(getRecognizer().getClass().getSimpleName() + " could not load context: " + e.getMessage());
				monitor.put("success", false);
				monitor.put("message", e.getMessage());
			}
			send(monitor);
		} else if (event.triggers("action.context.unload")) {
			try {
				unloadContext(event.getString("context"));
			} catch (RecognizerException e) {
				logger.error("Problem unloading context", e);
			}
		} else if (event.triggers("action.context.default")) {
			setDefaultContext(event.getString("context"));
		}
	}

	private void sendConfigResult(String eventName, boolean success, Event event, String message) {
		Event error = new Event(eventName);
		error.put("success", success);
		error.put("message", message);
		error.put("action", event.getId());
		send(error);
	}

	public boolean isInSpeech() {
		return inSpeech;
	}

	public void setPartialResults(boolean cond) {
		this.partialResults  = cond;
	}

	//@Override
	//public String getDefaultName() {
	//	return recognizer.getClass().getSimpleName();
	//}

	public void addResultListener(RecognizerListener listener, int priority) {
		recognizer.addRecognizerListener(listener, priority);
	}

	@Override
	public void initRecognition(AudioFormat format) {
	}

	public void addSemanticProcessor(String name, RecognizerListener processor) {
		int key = name.hashCode() + recognizer.hashCode();
		semanticProcessors.put(key, processor);
		addResultListener(processor, RecognizerListeners.PRIORITY_SEMANTICS);
	}

	public RecognizerListener getSemanticProcessor(String name) {
		int key = name.hashCode() + recognizer.hashCode();
		return semanticProcessors.get(key);
	}

}
