## Furhat robot head

<img src="img/furhat.png" width="200" style="float:right"/>

IrisTK is closely integrated with the [Furhat robot head](http://www.furhatrobotics.com). Furhat supports speech synthesis, facial animation and neck movements, which all run on a separate on-board computer. Since IrisTK is designed to run in a [distributed fashion](distributed_systems.html), it is very easy to integrate Furhat into your system. 

There are two different modes in which you can run Furhat:

* **Passive mode**: In this mode, Furhat only runs the facial animation and synthesis. By connecting to the Furhat computer from your own computer, you can control Furhat's gaze, speech and gestures. 
* **Live mode**: In this mode, Furhat also runs speech recognition, kinect and all modules necessary to be fully autonomous. You can upload you own _skills_ to Furhat. A skill contains instructions on which grammars to load and a flow for the dialog. 

### Using Furhat in Passive mode from your IrisTK dialogue system

If you already have a situated dialogue system running with the facial animation that comes with IrisTK (see [Tutorial 2](tutorial_sitint.html)), it is very easy to use Furhat instead of the animated agent. Make sure Furhat is connected to the same network and that Furhat is running in Passive mode. Then switching the following line of code in your system setup (using the correct IP address of Furhat):

```java
system.connectToBroker("furhat", "127.0.0.1");
//system.setupFace(new WindowsSynthesizer(), Gender.FEMALE);
```

### Using Furhat in Live mode

You can also upload your application to the Furhat computer, so that the whole system is running there. Applications uploaded to Furhat are referred to as _skills_. If you have created an application following [Tutorial 2](tutorial_sitint.html), there is a class called MultiguessSkill. If you open it, you will see how the skill is defined:

```java
public class MultiguessSkill extends Skill {

	private MultiguessFlow flow;

	// This is created once, when the Live Mode starts
	public MultiguessSkill() {
		// This makes the flow editable in the web interface
		addResource(new FlowResource(this, "Flow", getSrcFile("MultiguessFlow.xml")));
		// This makes the grammar editable in the web interface
		addResource(new XmlResource(this, "Grammar", getPackageFile("MultiguessGrammar.xml")));
		// The skill requires an English recognizer
		getRequirements().setLanguage(Language.ENGLISH_US);
		// The skill requires a recognizer that supports grammars 
		getRequirements().setSpeechGrammar(true);
		// This adds the initial state of the flow, plus any public states as possible entry points
		addEntriesFromFlow(MultiguessFlow.class, () -> flow);
	}
	
	@Override
	public String getName() {
		return "Multiguess";
	}

	// This is run right before the skill starts
	@Override
	public void init(SkillHandler handler) throws Exception {
		// The speech recognition grammar (contextt) is loaded 
		handler.loadContext("default", new SpeechGrammarContext(new SRGSGrammar(getPackageFile("MultiguessGrammar.xml"))));
		// The default speech recognition context is defined
		handler.setDefaultContext("default");
		// The flow is initialized
		flow = new MultiguessFlow(handler.getSystemAgentFlow());
	}

	@Override
	public void stop(SkillHandler handler) throws Exception {
		// Any code that needs to be run when the skill stops should go here
	}

}
```

Note that it is possible for a package to provide several skills. The skills that are provided are defined in the package.xml file in the application root folder:

```xml
<package name="multiguess">
	<provide>
		<class type="iristk.furhat.skill.Skill" name="iristk.app.multiguess.MultiguessSkill" />
	</provide>
	<classpath>
		<src path="src" output="bin"/>
		<!-- Here you can add references to jar-files, if needed. Look into core/package.xml for examples -->
	</classpath>
</package> 
```

To upload the package/skill to Furhat, you must first make zip file with your package. Open a command window and run:

```
iristk zip Multiguess
```

This will create a file called Multiguess.zip in the folder you are currently in. Make sure that Furhat is running in Live mode. In the top menu in the Furhat web interface, choose System->Packages. There you can upload your zip file. Then restart the mode (Mode->Restart). You should then see your new skill in the list of skills and be able to run it. 

### Using Furhat with another dialogue system (from Java)

The Furhat robot head can also be controlled from a simple Java API, if you are not interested in the other features of IrisTK. This also does not require Windows. In this case, Furhat should run in Passive mode. You will need furhat-proxy.jar, which you can [download from here](/download). 

<img src="img/furhat_coordinates.png" style="float:right;margin-left:10px"/>

Then you can control Furhat like this:

```java
FurhatProxy furhat = new FurhatProxy(FURHAT_ADDRESS);
// Make Furhat say something
furhat.say("Hello there");
// Make Furhat make a gesture
furhat.gesture("smile");
// Make Furhat gaze in a certain direction
furhat.gaze(1f, 1f, 2f);
// Make Furhat gaze using eyes only
furhat.gaze(1f, 1f, 2f, "eyes");
```

The target for Furhat's gaze are provided as a point in space with X, Y, Z (in meters) according the illustration on the right.

### Using Furhat from another programming language

If you want to control Furhat from another programming language, you can simply [connect to the Furhat broker using TCP/IP](distributed_systems.html). Use the ticket "furhat". Again, you should use the Passive mode.

You can then control Furhat by sending the following JSON instructions:

```json
// Make Furhat say something
{
  "class" : "iristk.system.Event",
  "event_name" : "action.speech",
  "event_id" : "my_unique_id_123",
  "text" : "Hello there"
}
// Make Furhat perform a gesture
{
  "class" : "iristk.system.Event",
  "event_name" : "action.gesture",
  "event_id" : "my_unique_id_123",
  "name" : "smile"
}
// Make Furhat gaze in a certain direction
{
  "class" : "iristk.system.Event",
  "event_name" : "action.gaze",
  "event_id" : "my_unique_id_123",
  "location" : {"x": 1, "y" : 1, "z" : 2}
}
// Make Furhat gaze using eyes only
{
  "class" : "iristk.system.Event",
  "event_name" : "action.gaze",
  "event_id" : "my_unique_id_123",
  "location" : {"x": 1, "y" : 1, "z" : 2},
  "mode" : "eyes"
}
// Make Furhat move the lips
{ "class" : "iristk.system.Event",
  "event_name" : "action.lipsync",
  "event_id" : "my_unique_id_123",
  "phones" : 
	{"class" : "iristk.speech.Transcription",
	 "phones" : [
	   {"class":"iristk.speech.Phone","name":"_s","start":0,"end":0.01},
       {"class":"iristk.speech.Phone","name":"J","start":0.01,"end":0.13},
       {"class":"iristk.speech.Phone","name":"AE","start":0.13,"end":0.32},
       {"class":"iristk.speech.Phone","name":"_s","start":0.32,"end":0.33}]}}
```

The phone inventory for the phonetic transcription is described in the [Speech Synthesis Reference](speech_synthesis.html).
