## System overview

An IrisTK system (iristk.system.IrisSystem) consists of a number of modules (subclasses of iristk.system.IrisModule) that send and receive events. Events can represent anything that updates the system, typically some action that the user has done which is perceived by a module in the system or an action that some module wants some other module to execute. By default, IrisSystem relays all events to all modules and it is up to each module whether to process the event or not.

Each event (iristk.system.Event) has a name and a set of parameters. By convention, the name of events starts with one of the following types:

* **action** represents things that the system should do, such as saying something, producing a gesture or turning the head. 
* **sense** represents things that the system perceives (such as a user saying something or approaching the system). 
* **monitor** represents feedback sensations that modules should produce when performing an action.  

Sense events are typically created by sensor modules (speech recognizers, face trackers, etc). These are typically picked up by a Flow module that models the interaction and give rise to system behaviour. Thus, the Flow generate Action events which are then picked up by actuator modules (speech synthesis, facial animation, etc). The actuator modules send monitor events when executing actions. Such signals are essential to real-time systems, since they allow other modules to know when actions have been performed or are about to be performed, which facilitates synchronization, sequencing and interruptions of actions.  

The following illustration shows how different kinds of events typically may be sent and picked up by modules:   

![](img/simpledialog.png)

1. The DialogFlow module send an action.speech event which informs the Synthesizer to start speaking.
2. The Synthesizer sends an monitor.speech.end event which informs the DialogFlow that the system is done speaking.
3. The DialogFlow module send an action.listen event which informs the Recognizer to start listening.
4. The Recognizer sends (several) sense.speech events which informs the DialogFlow that the user has said something.  

Modules are free to invent their own events, but some events are standardized in IrisTK (see [Events](events.html)).

<!--

### A simple dialogue system example

A simple dialogue system typically consists of a Recognizer a Flow and a Synthesizer. The following figure illustrates which modules send which events, and which modules are interested in them.  

![](img/simpledialog.png)

In a typical dialogue turn exchange, the following events are sent:

1. The DialogFlow module send an action.speech event which informs the Synthesizer to start speaking.
2. The Synthesizer sends an monitor.speech.end event which informs the DialogFlow that the system is done speaking.
3. The DialogFlow module send an action.listen event which informs the Recognizer to start listening.
4. The Recognizer sends (several) sense.speech events which informs the DialogFlow that the user has said something.  

### Multi-party face-to-face dialogue system example

If we want to model multi-party face-to-face interaction we might want to add a FaceTracker, a MultiPartyFlow module (which keeps track of users and the system's current focus of attention), and an animated Agent module:

![](img/multiparty.png)

Here is how a typical course of events might take place in this scenario (some monitor events have been left out for simplicity):

1. The FaceTracker tracks a user and sends a sense.body event
2. Since this user is new to the MultiPartyFlow module, it sends a sense.agent.enter event
3. The DialogFlow module decides to attend to this agent and sends an action.attend event
4. The MultiPartyFlow module sends an action.gaze event which informs the Agent module to gaze at the user
5. The DialogFlow module send an action.speech event which informs the Synthesizer to start speaking.
6. The Synthesizer sends an monitor.speech.start event with the phoneme timings which informs the Agent module to move the lips in synchronization.
7. The Synthesizer sends an monitor.speech.end event which informs the DialogFlow that the system is done speaking.
8. The DialogFlow module send an action.listen event which informs the Recognizer to start listening.
9. The Recognizer sends (several) sense.speech events which informs the DialogFlow that the user has said something.  

-->
