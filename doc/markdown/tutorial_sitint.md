## Tutorial 2: Situated interaction

In this tutorial, you will learn the basics of situated interaction, where several users can interact with an animated agent or a robot. 

Note: this tutorial assumes that you have learned the basics of how to [create an application](tutorial_first_app.html). 

This tutorial will benefit from the following devices (although they are not required):

* A set of handheld stereo microphones, such as a pair of [Singstar microphones](microphones.html).
* A Microsoft Kinect sensor (V1 or V2). Please make sure that the corresponding SDK is also installed on the system.
* The [Furhat robot head](http://www.furhatrobotics.com), which can be used instead of the animated agent that comes with IrisTK.

We will use a template called situated_dialog. Create an application called "multiguess":

```
iristk create situated_dialog multiguess
iristk eclipse
```

This application is basically the same as in [Tutorial 1](tutorial_first_app.html): the agent will ask you to guess a number between 1 and 10. However, in this case, the agent will be spatially situated and allow multiple users to interact at the same time.

Open MultiguessSystem.java and inspect it:

```java
SituatedDialogSystem system = new SituatedDialogSystem(this.getClass());
SystemAgentFlow systemAgentFlow = system.addSystemAgent();

system.setLanguage(Language.ENGLISH_US);

//system.setupLogging(new File("c:/iristk_logging"), true);

system.setupGUI();

//system.setupKinect();

//system.setupMonoMicrophone(new WindowsRecognizerFactory());
system.setupStereoMicrophones(new WindowsRecognizerFactory());
//system.setupKinectMicrophone(new KinectRecognizerFactory());
		
//system.connectToBroker("furhat", "127.0.0.1");
system.setupFace(new WindowsSynthesizer(), Gender.FEMALE);

system.addModule(new FlowModule(new MultiguessFlow(systemAgentFlow)));
system.loadContext("default", new SpeechGrammarContext(
       new SRGSGrammar(getClass().getResource("MultiguessGrammar.xml").toURI())));
system.loadPositions(system.getDataFile("situation.properties"));		
system.sendStartSignal();
```

In this case, the class SituatedDialogSystem is used instead of SimpleDialogSystem. The default configuration (shown above) assumes that you run without a Kinect. It might be good to start this way, even if you have a Kinect, to see that everything works. It will also use the animated agent that comes with IrisTK (setupFace).  

**NB**: It also assumes that you have a set of stereo microphones (such as [Singstar](microphones.html)), one for each user. If this is not the case (e.g., if you are using a headset), change from "setupStereoMicrophone" to "setupMonoMicrophone". 

Run MultiguessSystem. In the GUI, you should see the situation from the top view with the system agent in the center. Around the system agent, there is a large grey circle which indicates the agent's interaction space. Only users that are within this space will be considered as interaction partners. You can now simulate that a user is entering the interaction in front of the agent by double-clicking in front of the agent in the top view (within the grey circle). The agent will start to interact with this user. You can move the user around by dragging with the mouse and see how the agent tracks the user with the eyes. By double-clicking on the user again (or by dragging it outside the grey circle), it will leave the interaction. You can also try to add a second user. If you are talking to the system with the singstar microphones, use the red microphone for the left user and blue for the right user. 

![](img/sitint_gui.jpg)

### Using a Kinect

Make sure Kinect is plugged in and positioned below the screen. Uncomment the line system.setupKinect(). Restart the system. The camera of the Kinect should show up in the GUI. If it does not, you can choose which panels should be visible and save the current perspective (panel configuration) in the Window menu. The GUI is flexible, similar to Eclipse, so you can move around the panels as you see fit.

Stand in front of the Kinect and make sure you are clearly visible. When Kinect starts to track you, the face and hands will be marked in the camera view. Then you should also appear in the situation view and the system should start interacting with you. If you have Kinect V2, you head pose will also be tracked. 

If you want to use the Kinect array microphone instead of the Singstar or mono microphones, uncomment "setupKinectMicrophone" and comment the other lines. Be aware that Kinect may not be so good at picking up short single words like "five". The grammar in this example also allows you to say "my guess is five", which might be more easily recognized.

### Understanding the flow

Compared the the simple dialogue in [Tutorial 1](tutorial_first_app.html), some things have been changed in the flow to accommodate the situated interaction. This is what the flow starts like:

```xml
<flow name="MultiguessFlow" package="iristk.app.multiguess" 
	initial="Idle" xmlns:this="iristk.app.multiguess.MultiguessFlow" xmlns="iristk.flow" 
	xmlns:p="iristk.flow.param" xmlns:agent="iristk.situated.SystemAgentFlow" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="iristk.flow flow.xsd iristk.situated.SystemAgentFlow SystemAgentFlow.xsd">
	
	<param name="agent" type="iristk.situated.SystemAgentFlow"/>
	
	<var name="system" type="iristk.situated.SystemAgent" value="agent.getSystem()"/> 
	 
	<var name="number" type="Integer"/>
```

As can be seen, the "agent" namespace is now linked to another reusable flow called "iristk.situated.SystemAgentFlow" (similarly to how "dialog" was linked to SimpleDialogFlow in the first tutorial). One thing that differs from the SimpleDialogFlow is that SystemAgentFlow has a state (it contains variables), and we therefore need to have an instance of the flow. This instance is passed to the "MultiguessFlow" as a parameter:

```xml
<param name="agent" type="iristk.situated.SystemAgentFlow"/>
``` 

The SystemAgentFlow is instantiated when the SystemAgent is added to the system, and then passed to the flow at creation:

```java
SystemAgentFlow systemAgentFlow = system.addSystemAgent();
...
system.addModule(new FlowModule(new MultiguessFlow(systemAgentFlow)));
```

To access and store information about the users and the system agent, a variable called "system" (of type SystemAgent) is defined in the flow and retrieved from the SystemAgentFlow:

```xml
<var name="system" type="iristk.situated.SystemAgent" value="agent.getSystemAgent()"/>
``` 

The SystemAgent class contains (among other things) information about the system agent's attentional state, and a collection of the users currently interacting with the system agent. Unlike Tutorial 1, we no longer have a variable containing the number of guesses made, since we may have several users interacting, and this value will be different for each user. Instead, we will store this value for each user through the "system" variable:

```java
// Assign the value 0 to the field "guesses" for all users:
system.putUsers("guesses", 0)
// Access  the "guesses" field for the user currently attended to:
system.getCurrentUser().getInteger("guesses")
// Increment the "guesses" field for the user currently attended to:
system.getCurrentUser().incrInteger("guesses")
```

The "system" variable also provides many other useful methods (for a complete list, see the javadoc for the [SystemAgent](http://www.iristk.net/javadoc/iristk/situated/SystemAgent.html)):

```java
// Check whether there are any users interacting
system.hasUsers()
// Check whether there are more than one user interacting
system.hasManyUsers()
// Access a user the system is not currently attending
system.getOtherUser()
// Access a user that is not associated with this event
system.getOtherUser(event)
```

Unlike Tutorial 1, we now start the flow in a state called "Idle", in which the system agent is waiting for users to interact with:

```xml
<state id="Idle" extends="Dialog">
	<onentry>
		<agent:attendNobody/>
	</onentry>
	<onevent name="sense.user.enter">
		<agent:attend target="event:user"/>
		<goto state="Greeting"/>
	</onevent>
</state>
```

Upon entry, the system agent is instructed to attend to nobody (slightly looking down). When a user enters the interaction (**sense.user.enter**), the agent should attend and greet the user, and then start the game. Functions for controlling the system agent's attention is provided by the SystemAgentFlow:

```xml
<!-- Attend to the user that is associated with this event -->
<agent:attend target="event:user"/> 
<!-- Attend to all users (eyes moving between them) -->
<agent:attendAll/>
<!-- Attend to a random user -->
<agent:attendRandom/>
<!-- Attend to a different user than is currently attended -->
<agent:attendOther/>
<!-- Attend nobody (look down slightly) -->
<agent:attendNobody/>  
```

We can also check the system's current attention:

```java
// Is the system attending the user associated with this event?
system.isAttending(event)
// Is the system attending all users?
system.isAttendingAll()
```

If we compare the generic "Dialog" state with Tutorial 1, we can see that it looks a bit different:

```xml
<state id="Dialog">
	<onevent name="sense.user.speech.start" cond="system.isAttending(event) and eq(event:speakers, 1)">
		<send event="action.gesture" p:name="'smile'"/>
	</onevent>
	<onevent name="sense.user.speak">
		<agent:say>Sorry, I didn't get that.</agent:say>
		<reentry/>
	</onevent>
	<onevent name="sense.user.speak.side">
		<agent:attendOther mode="'eyes'" />
		<agent:say>I didn't ask you.</agent:say>
		<agent:attendOther mode="'eyes'" />
		<reentry/>
	</onevent>
	<onevent name="sense.user.speak.multi">
		<agent:say>Don't speak at the same time.</agent:say>
		<reentry/>
	</onevent>
	<onevent name="sense.user.silence">
		<agent:say>Sorry, I didn't hear anything.</agent:say>
		<reentry/>
	</onevent>
	<onevent name="sense.user.leave" cond="system.isAttending(event)">
		<if cond="system.hasUsers()">
			<agent:attendRandom/>
			<goto state="Guess" />
		<else />
			<goto state="Idle"/>
		</if>
	</onevent>
	<onevent name="repeat">
		<reentry/>
	</onevent>
</state>
```

First, we have added a generic event handler that is triggered when a user that the system attends to starts to speak (**sense.user.speech.start**). In this case, we want the agent to smile to acknowledge this. If you are interested in how to customize gestures and see which gestures are available, you can read more about the [Gesture Builder](gestures.html) tool. 

Second, two new event handlers need to be added when listening for speech from the user:

* **sense.user.speak.multi**: Raised when both users speak at the same time. This requires stereo microphones (such as singstar) and will not be called when the Kinect or Mono microphones are used (since they can only receive one audio stream).
* **sense.user.speak.side**: Raised when the user who is not currently addressed has spoken. This will never happen if a Mono microphone is used, since the system then assumes that the audio comes from the addressed user. However, it can happen if the Kinect microphone is used, since it can localise the source of the sound, or if stereo microphones are used. 

Third, there is also an event handler that is triggered when the user leaves the interaction called **sense.user.leave**. As specified in the "cond" attribute, it will only be triggered if the user that is currently being attended is leaving, otherwise we just continue interacting with the current user. If a user currently being attended to is leaving, the system will check whether there is still some user left (system.hasUsers()). If so, the system will attend this user and continue the interaction, otherwise it will go to the Idle state. 

### Future directions

If you want to read more about which actions and events are offered by SystemAgentFlow, please refer to the [SystemAgentFlow reference](system_agent_flow.html). 

You can also run the system with the [Furhat robot head](furhat.html), instead of the animated agent.

<!--

### Understanding the situation model

For a situated dialogue, we need a situation model. The situation model keeps track of the users' position in relation to the agent and can therefore be used by the system to gaze at the users, or map speech recognition input from a microphone to the correct user. To do this, information about the sensors that are being used must be added to the model. 

-->
