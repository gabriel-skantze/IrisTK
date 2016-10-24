## Overview of IrisTK

IrisTK is a Java-based framework for developing dialogue systems. It works well for just spoken interaction, but it is especially designed to handle multi-modal and situated (human-robot) interaction. 

### Packages

IrisTK consists of a set of **packages** (illustrated as green boxes below). Each package is self-contained (with the necessary source-, binary- and library-files) in its own folder, so they are easy to add or remove (although they might rely on each other). Notice that each package might also have its own usage license. At start-up, IrisTK detects which packages are installed and can build up the necessary classpath. 

There is a **core** package, but IrisTK can also be extended with **addon** packages (containing reusable modules). Applications created with IrisTK (so-called **app**) are also packages. 

![](img/packages.png)

### System, Modules and Events

The base class for creating a dialogue system is called **IrisSystem**. In order for the system to do anything useful, you have to add modules (subclasses of **IrisModule**) to the system. Modules typically manage input (such as speech recognition), output (such as speech synthesis), or some kind of control (mapping input to output). 

![](img/system_and_modules.png)

IrisTK is **event**-based. Events are typically sent by one module and then relayed to other modules in the system. By default, all modules receive all events and can then decide whether to react on them, but this can be restricted for optimization purposes. 

Events have a name and a set of parameters. These are some examples (see [here](events.html) for a complete list):

```javascript
// Make the system say something
action.speech {
	text: "Hello there"
}

// A speech synthesizer has started to speak
monitor.speech.start {}

// Listen for speech from the user(s) with a 5000 ms timeout
action.listen {
	noSpeechTimeout: 5000
}

// Detect end of speech from a speech recognizer
sense.speech.end {
	length: 3540
}
```

### Distributed systems

Systems can also be **distributed** over different processes or machines. In this case, several IrisSystems are connected to a central broker (**IrisBroker**), which relays events to all connected systems. In this case, events are serialized (in JSON) and sent over TCP/IP. This way, it is also possible to connect modules that are implemented in other programming languages than Java.

![](img/distributed_systems.png)

### IrisFlow: Scripting the dialogue with statecharts

IrisTK provides an XML-based scripting language for defining a dialogue **Flow**. The flow defines a [statechart](http://en.wikipedia.org/wiki/UML_state_machine) that maps input to output events depending on which state the flow is in. The flow can be used in a **FlowModule** that is added to the system, and thereby orchestrates the interaction. 

This is what a state in the flow can looke like:

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
```
