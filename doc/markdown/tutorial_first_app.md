## Tutorial 1: Your first application

In this tutorial, you will learn the basics of how to set up an application and how a simple dialogue system can be defined.

Note: this tutorial assumes that:

* You have installed IrisTK correctly
* You have imported the project into Eclipse
* You are using an English version of Windows (for ASR and TTS to work)

IrisTK provides tools for rapidly setting up a skeleton for an application based on templates. We will use this to set up a simple dialogue system, based on a template called "simple_dialog", which is useful for speech only-applications.  It creates a very simple application - a game in which you are asked to guess a number between 1 and 10. Since it provides the stubs for flow and grammar, you can use it in the future to set up skeletons for new applications, giving them any name of your choice. All places below where the name "guess" is used, the name of your application should then be used.   

Open a command window and type the following command:

```
iristk create simple_dialog guess
```

This will create an application called "guess" based on the "simple_dialog" template in the "app" folder under the IrisTK installation. 

Now type: 

```
iristk eclipse
```

This will set up eclipse properly. Open up eclipse, or refresh the IrisTK project if eclipse is already open. The new application should come up as a source folder.

Have a headset or microphone ready before running the application. Locate the file GuessSystem.java in the source folder, right click and choose "Run As" -> "Java application". The application has a very limited grammar of what can be recognized (numbers between 1 and 10, "yes" and "no"). As you can see, a GUI is also opened which shows the different modules with their current state and the events they produce. 

Once eclipse has compiled the Java code, you can also run the application from the command line:

```
iristk guess
```

### Understanding IrisSystem

Open the file GuessSystem.java. The constructor should contain the following:

```java
// Create the system
SimpleDialogSystem system = new SimpleDialogSystem(this.getClass());
// Set the language of the system
system.setLanguage(Language.ENGLISH_US);
// Set up the GUI
system.setupGUI();
// Add the recognizer to the system
system.setupRecognizer(new WindowsRecognizerFactory());
// Add a synthesizer to the system		
system.setupSynthesizer(new WindowsSynthesizer(), Gender.FEMALE);
// Add the flow
system.addModule(new FlowModule(new GuessFlow()));
// Load a grammar context in the recognizer
system.loadContext("default", new SpeechGrammarContext(
       new SRGSGrammar(getClass().getResource("GuessGrammar.xml").toURI())));
// Start the interaction
system.sendStartSignal();
``` 

The base class for all IrisTK systems is IrisSystem. For convenience, the template uses a class called SimpleDialogSystem that extends IrisSystem and makes the configuration simpler for basic speech-based dialogue systems. The system consists of a number of modules (subclasses of iristk.system.IrisModule) that send and receive events. Events can represent anything that updates the system, typically some action that the user has done which is perceived by a module in the system, or an action that some module wants some other module to execute. By default, IrisSystem relays all events to all modules and it is up to each module whether to process the event or not. Modules can be added with the addModule() method, but SimpleDialogSystem will do this for us, so we only need to tell it which recognizer and synthesizer to use, etc. 

If you are interested in how the system is actually set up, you can inspect the class SimpleDialogSystem. If you later on want to build systems with novel and mode advanced module configurations, you could use the class IrisSystem directly instead. 

### Understanding the flow

Open the file GuessFlow.xml. It starts like this:

```xml
<flow name="GuessFlow" package="iristk.app.guess" 
	initial="Start"	xmlns="iristk.flow" xmlns:p="iristk.flow.param" xmlns:dialog="iristk.flow.SimpleDialogFlow" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="iristk.flow flow.xsd iristk.flow.SimpleDialogFlow SimpleDialogFlow.xsd">
	
	<var name="number" type="Integer"/>
	<var name="guesses" type="Integer"/>
	
	<state id="Start">
		<onentry>
			<exec>number = new java.util.Random().nextInt(10) + 1</exec>
			<exec>guesses = 0</exec>
			<dialog:say>I am thinking of a number between 1 and 10, let's see if you can guess which one it is.</dialog:say>
			<goto state="Guess"/>
		</onentry>
	</state>
```

The top-level \<flow\> element starts by defining the following things:

* **name** and **package**: The package and name of the resulting Java class after compilation
* **initial**: The initial state when the flow starts. 
* **xmlns:dialog**: An external flow called iristk.flow.SimpleDialogFlow with reusable states is linked by including it as an XML namespace.

Then two flow-level variables are defined: **number** (the number that the system is thinking of) and **guesses** (the number of guesses that the user has made). 

The initial state **Start** contains one event handler called \<onentry\>, which is triggered when the state is entered. The event handler does in turn contain a set of actions that will be executed:

* \<exec\>: Executes Java code
* \<dialog:say\>: Calls the state **say** in the linked flow iristk.flow.SimpleFlow with the contents of the element (the text to speak) as a parameter. The called state will send an event to the synthesizer to synthesize the text, and then return when the synthesizer is done. If you want to see what DialogFlow.xml looks like, you can find it in the folder core/src/iristk/flow.
* \<goto\>: Will make a transition to the state **Guess**. This is what the state looks like:

```xml
<state id="Guess" extends="Dialog">
	<onentry>
		<dialog:listen/>
	</onentry>
	<onevent name="sense.user.speak" cond="event?:sem:number">
		<exec>guesses++</exec>
		<if cond="asInteger(event:sem:number) == number">
			<dialog:say>That was correct, you only needed <expr>guesses</expr> guesses.</dialog:say>
			<goto state="CheckAgain"/>
		<elseif cond="asInteger(event:sem:number) &gt; number"/>
			<dialog:say>That was too high, let's try again.</dialog:say>
			<reentry/>
		<else/>
			<dialog:say>That was too low, let's try again.</dialog:say>
			<reentry/>
		</if>
	</onevent>
</state>
	
<state id="Dialog">
	<onevent name="sense.user.speak">
		<dialog:say>I am sorry, I didn't get that.</dialog:say>
		<reentry/>
	</onevent>
	<onevent name="sense.user.silence">
		<dialog:say>I am sorry, I didn't hear anything.</dialog:say>
		<reentry/>
	</onevent>
</state>
```

When the **Guess** state is entered, the external state **listen** (again defined in iristk.flow.SimpleFlow) is called. This state triggers the speech recognizer to start listening using the default grammar and default silence thresholds (these can be changed with parameters). When a speech recognition result is received, one of two events will be raised by the called flow, and it is very important that event handlers are defined for these (otherwise the flow execution will die):

* **sense.user.speak**: The user has said something. The event parameter **sem** contains the semantics of what was said. 
* **sense.user.silence**: The user didn't say anything. 
	
As you can see, the event handler for "sense.user.speak" in the **Guess** state has a condition (**cond**) that is "event?:sem:number". This means that the event should have a parameter called "sem", which in turn should have a field called "number" set. The event handler then checks whether the guessed number was correct, too low, or too high, using \<if\>, \<elseif\> and \<else\>. (Since the characters \< and \> have a special meaning in XML, \&lt; and \&gt; are used instead for numerical comparison). 

But what happens if this condition is not true? There is no event handler for that. There is also no event handler for "sense.user.silence" in the **Guess** state. As can be seen, the **Guess** state has an **extends** attribute. This means that all event handlers in the **Dialog** state are inherited by the **Guess** state, but they are checked after the event handlers in the **Guess** state. This is a very important functionality: it allows you to define generic behaviour across states. In this case, the **Dialog** state defines what will happen if the user says something the system doesn't understand or doesn't say anything. In both cases, the user will first be informed, after which the \<reentry\> action re-triggers the \<onentry\> event handler of the current state. 

Finally, the **CheckAgain** state checks whether the user wants to play again. Notice that the behaviour in the **Dialog** state is reused: 

```xml
<state id="CheckAgain" extends="Dialog">
	<onentry>
		<dialog:say>Do you want to play again?</dialog:say>
		<dialog:listen/>
	</onentry>
	<onevent name="sense.user.speak" cond="event?:sem:yes">
		<dialog:say>Okay, let's play again.</dialog:say>
		<goto state="Start"/>
	</onevent>
	<onevent name="sense.user.speak" cond="event?:sem:no">
		<dialog:say>Okay, goodbye</dialog:say>
		<exec>System.exit(0)</exec>
	</onevent>		
</state>
```

### Compiling the flow

If you change the flow XML, it needs to be compiled to Java code for the change to take effect. You can do this from the command line like this:

```
cd %IrisTK%\app\guess
iristk cflow GuessFlow.xml
```

Then refresh the "guess" source folder (or the whole IrisTK project) in eclipse.

If you want to compile the flow from within eclipse, there are two other ways of doing it:

1. There is also an Ant task set up for you. Locate the build.xml file in the app/guess folder in eclipse (not the source folder!). Right-click and choose "Run As"->"Ant Build". Remember to refresh afterwards.
2. [Download the eclipse plug-in](develop_in_eclipse.html) and compile the flow by right-clicking on it and choose "Compile Flow" from the context menu. This is the most convenient way of compiling the flow, since it also notifies Eclipse (and you don't have to refresh).

### Understanding the grammar

The grammar of what the user can say is defined in GuessGrammar.xml, according to SRGS, the [Speech Recognition Grammar Specification](http://www.w3.org/TR/speech-grammar/). It also specifies the semantics of the utterance using \<tag\> elements.

```xml
<?xml version="1.0" encoding="utf-8"?>
<grammar xml:lang="en-US" version="1.0" root="root"
	xmlns="http://www.w3.org/2001/06/grammar" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.w3.org/2001/06/grammar http://www.iristk.net/xml/srgs.xsd" tag-format="semantics/1.0">

  <rule id="root" scope="public">
      <one-of>
          <item>one <tag>out.number=1</tag></item>
          <item>two <tag>out.number=2</tag></item>
          <item>three <tag>out.number=3</tag></item>
          <item>four <tag>out.number=4</tag></item>
          <item>five <tag>out.number=5</tag></item>
          <item>six <tag>out.number=6</tag></item>
          <item>seven <tag>out.number=7</tag></item>
          <item>eight <tag>out.number=8</tag></item>
          <item>nine <tag>out.number=9</tag></item>
          <item>ten <tag>out.number=10</tag></item>
          <item>yes <tag>out.yes=1</tag></item>
          <item>no <tag>out.no=1</tag></item>
      </one-of>
  </rule>
  
</grammar>
```

It is also possible to use the ABNF format for grammars, which is described in the [speech recognition reference](speech_recognition.html).
 
### Logging

It is easy to log both events and the input and output audio in the system. To turn on logging, simply add this line after the SimpleDialogSystem constructor (is important to do this before any other modules are added to the system):

```java
system.setupLogging(new File("c:/iristk_logging"), true);
``` 

The first parameter defines the directory in which the logs will be placed. The second parameter (set to true) instructs the logger to automatically start logging once the system starts. Thus, there will be one log file for the whole system lifetime. If the system has a long lifetime and has many interactions (with the same or different users), it is sometimes desirable to split these into different files. In that case you can set the second parameter to false and then instruct the logger when to start and stop logging. You do this by sending "action.logging.start" and "action.logging.stop" events. This can for example be done from the flow when the system initiates and ends the dialogue:

```xml
<send event="action.logging.start"/>
<send event="action.logging.stop"/>
```

The logger will create one wav-file for the user and one for the system. Note that if the system does not shut down nicely, these will not be proper wav-files (they will end with .tmp). However, the next time the system starts, these will be converted to wav files. The logger will also create one txt-file with all events sent in the system (in json format), and one html-file that visualises the flow execution, which can be handy when debugging or just for understanding how the flow works. 





 