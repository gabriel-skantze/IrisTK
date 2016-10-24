## Creating new modules

It is easy to create your own module by sub-classing IrisModule. Let's say you want to create your own dialogue manager (instead of using IrisFlow). This example shows a dialog manager that will simply repeat everything that the user says: 

```java
import iristk.speech.RecResult;
import iristk.system.Event;
import iristk.system.InitializationException;
import iristk.system.IrisModule;

public class MyDialogManager extends IrisModule {
	
	@Override
	public void onEvent(Event event) {
		// We have received an event from some other module, 
		// check whether we should react to it
		if (event.triggers("monitor.system.start")) {
			// The system started, listen for speech
			listen();
		} else if (event.triggers("sense.speech.rec**")) {
			// We received a speech recognition result, get the text
			String text = event.getString("text");
			// Check whether there is any result
			if (text != null && text.length() > 0) {
				// Create an event for the speech synthesizer
				Event newEvent = new Event("action.speech");
				if (text.equals(RecResult.NOMATCH))
					// We got a NOMATCH, inform the user
					newEvent.put("text", "I didn't understand that");
				else
					// Add the parameter text, which will repeat the speech recognition result
					newEvent.put("text", "You said " + text);
				send(newEvent);
			} else {
				// If not, listen again
				listen();
			}
		} else if (event.triggers("monitor.speech.end")) {
			// The synthesizer completed, listen for speech again
			listen();
		}
	}
	
	// Make the recognizer start listening
	private void listen() {
		send(new Event("action.listen"));
	}

	@Override
	public void init() throws InitializationException {
		// Initialize the module
	}

}
```

Now, if you create a [simple dialogue system](tutorial_first_app.html), you can remove the Flow module and add this module instead:

```java
//system.addModule(new FlowModule(new GuessFlow()));
system.addModule(new MyDialogManager());
```

To see which events you can react to and produce, please refer to the list of [standardized events](events.html).

Note that the init() method is called when the module is added to the system. If you want to do something special when the system actually starts, you should catch the "monitor.system.start" event (as done in the example above). 

### Creating a new speech recognizer

If you want to add your own speech recognizer, it is more convenient to implement the iristk.speech.Recognizer interface and then use it as an argument in the RecognzierModule constructor. 

### Create a new speech synthesizer

If you want to add your own speech synthesizer, it is more convenient to implement the iristk.speech.Synthesizer interface and then use it as an argument in the SynthesizerModule constructor.
