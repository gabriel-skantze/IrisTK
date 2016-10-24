## SystemAgentFlow reference

The SystemAgentFlow is a reusable flow that is being used in Situated Dialog Systems (see [Tutorial 2](tutorial_sitint.html)), where an animated agent or a robot is interacting with one or more users. It controls the attention of the agent, and provides a set of states that can be called. We assume that you have read Tutorial 2 and are familiar with how the SystemAgentFlow is initialized and included in your flow, along with the SystemAgent object (referred to as **system**). 

### Speech synthesis

To make the agent say something, you can call the state **say**:

```xml
<agent:say>Hello there</agent:say>
```

The synthesis action is by default blocking, which means that the synthesis is completed before the next action is taking place. If you want to continue with the next action immediately, you can pass the **async** parameter:

```xml
<agent:say async="true">Hello there</agent:say>
```

#### The speech synthesis queue

By default, new speech synthesis actions are added to a speech synthesis queue. If the queue is empty (i.e., the system is silent), it is synthesized directly, otherwise it will be played when the currently queued utterances are completed. 

There are two flags that can be used to control this behaviour (see events further down):

* **abort**: Abort the current speech queue (empty it), and play the new utterance directly. (Default is false)
* **ifsilent**: Will only synthesize the utterance if the queue is currently empty. (Default is false)

```xml
<agent:say abort="true">Hello there</agent:say>
```

It is also possible to stop the current speech synthesis (and clear the queue): 

```xml
<agent:stopSpeaking/>
```

#### Monitor the speech synthesis

Sometimes it can be useful to monitor the ongoing speech synthesis, in order to for example synchronize with gestures. 
This can be done by adding marks to the synthesized text: 

```xml
Go from <mark name="here"/> here, to <mark name="there"/> there!
```

When the synthesizer reaches these marks, it will generate events called **monitor.speech.mark**, which can for example be caught in the flow:

```xml
<!-- To catch a specific mark -->
<onevent name="monitor.speech.mark" cond="eq(event:name,'here')">
	<log>Mark: here</log>
</onevent>
<!-- To catch all marks -->
<onevent name="monitor.speech.mark">
	<log>Mark: <expr>event:name</expr></log>
</onevent>
```

#### Prominence gestures

The SynthesizerModule will automatically analyze the speech signal for each utterance and find the location with most energy. For this location, it will generate a **monitor.speech.prominence** event. The SystemAgentFlow makes use of this event to synchronize it with gestures. The default action is to raise the eyebrows, but you can change this behaviour when calling the say state:

```xml
<agent:say gesture="'smile'">Hello there</agent:say>
```

#### Playing audio

It is also possible to play an audio file instead of synthesizing a text. Let's say you want to play a pre-recorded laughter from the web (the URL is made up):

```xml
<agent:say audio="'http://www.funnyaudio.com/laughter.wav'">LAUGHTER</agent:say>
```

Or, if you want to provide your own audio, placed in your package:

```xml
<agent:say audio="'iristk://PACKAGE/audio/laughter.wav'">LAUGHTER</agent:say>
```

The PACKAGE part of the url should be replaced with the name of your package, and there should be a folder named "audio" in the root of that package, containing the wav-file. 

**NB**: the audio must be in mono. 

It is also advisable to add a text that represents your utterance (like "LAUGHTER" in the examples above), for display and logging purposes. This text is also used to drive the lipsync of the animated agent. Since the timing will most likely differ from the recorded audio, or be totally misrepresentative (as in the case of "LAGHTER" above), it is better to provide the lipsync information in a separate file, alongside the "laughter.wav" file, called "laughter.pho" (i.e., the same name but with a .pho extension instead of .wav). Here is an example of what the .pho file might look like for the word "yeah":

```json
{"class":"iristk.speech.Transcription","phones":
  [ {"class":"iristk.speech.Phone","name":"_s","start":0,"end":0.01},
    {"class":"iristk.speech.Phone","name":"J","start":0.01,"end":0.13},
    {"class":"iristk.speech.Phone","name":"AE","start":0.13,"end":0.32},
    {"class":"iristk.speech.Phone","name":"_s","start":0.32,"end":0.33} ] }
```

Note that you should always end the transcription with silence ("_s"), otherwise the agent might not close the mouth. The phone inventory for the phonetic transcription is described in the [Speech Synthesis Reference](speech_synthesis.html).

#### Changing the voice

It is also possible to the change the voice of the synthesizer:

```xml
<!-- Using a name of the voice (can be a substring, and does not have to match case): -->
<agent:voice name="'Mark'"/>
<!-- Choosing the voice by gender (keeping the current language) -->
<agent:voice gender="'female'"/>
```

### Speech recognition

#### Listening for speech

To receive any speech recognition events, you have to make the agent listen:

```xml
<agent:listen/>

<!-- Listen with a shorter timeout -->
<agent:listen timeout="1000"/>
```

The parameters you can provide are:

----------	-------
context		Which contexts to use (see the [Speech recognition](speech_recognition.html) reference)
timeout		The silence timout (in msec) if no speech is detected (default is 8000)
endSil		The silence timeout (in msec) to detect end-of-speech (default is 700)
nbest		The maximum number of hypotheses to generate (default is 1)
----------	-------

#### Receiving the speech recognition result

The listen action will result in one of the following events:

----------				-------
sense.user.speak		One of the users said something
sense.user.silence		None of the users said anything
sense.user.speak.side	A user who was no attended to said something
sense.user.speak.multi  Several users spoke at the same time
----------				-------

**NB**: It is important that you have event handlers for all these events. Otherwise, the flow execution might get stuck. 

These are the most important parameters in the events (for a complete list, see the [Events reference](events.html)):

----------			----			
text				The result in text
length				The length of the utterance (in msec)
sem					The semantic interpretation
user				The ID of the user agent
attsys				Whether or not the user was attending the system during the utterance
----------			----	

#### Handling speech from a non-attended user

If you receive a **sense.user.speak.side** event, a user who was no attended to said something (see the section on Attention further down). This can only happen if the system was attending to a specific user. If the agent was attending to all users, it will not be raised.

You can for example create a generic handler for speech that comes from a non-attended user (by putting it in a generic state that all other states extends). This will make the agent glance at the speaker, tell him "could you just wait a second", glance back at the originally attended user, say "so, let's see", and then re-enter the current state:

```xml
<onevent name="sense.user.speak.side">
	<agent:attendOther mode="'eyes'" />
	<agent:say>could you just wait a second</agent:say>
	<agent:attendOther mode="'eyes'" />
	<agent:say>so, let's see</agent:say>	
	<reentry/>
</onevent>
```

If you don't care whether the speaker was attended or not, you can make the system turn to the speaker, and then handle the event as an ordinary sense.user.speak event (again, it would be wise to put this in a generic state):

```xml
<onevent name="sense.user.speak.side">
	<agent:attend target="event:user"/>
	<raise event="sense.user.speak" copy="event"/>
</onevent>
```

#### Handling simultaneous speech

If you receive a **sense.user.speak.multi** event, several users spoke at the same time. This can only happen if you have parallel recognizers running and the system is attending several users. If you want to check the different speech recognition results, all individual events are stored in the **all** parameter of the event, as a list. Here is an example from a quiz application, where the different answers are checked for correctness:

```xml
<onevent name="sense.user.speak.multi">
	<if cond="question.isCorrect(event:all:0:sem:answer)">
		<agent:attend target="event:all:0:user"/>
		<goto state="CorrectAnswer"/>
	<elseif cond="question.isCorrect(event:all:1:sem:answer)"/>
		<agent:attend target="event:all:1:user"/>
		<goto state="CorrectAnswer"/>
	<else/>
		<agent:say>None of you were correct, let's try another question.</agent:say>
		<goto state="NextQuestion"/>
	</if>
</onevent>
```

If you want to treat these events just like ordinary speech events, you can easily transform them into sense.user.speak events in a generic state:

```xml
<onevent name="sense.user.speak.multi">
	<agent:attend target="event:user"/>
	<raise event="sense.user.speak" copy="event"/>
</onevent>
```

#### Detecting start and end of speech

Apart from the events described above, the following events will also be generated:

----------					-------
sense.user.speech.start		Reported at the start of speech for each user
sense.user.speech.end		Reported at the start of speech for each user
----------					-------

The most important event parameters are:

----------	-------
user		The ID of the user agent
speakers	The numbers of simultaneous speakers (1 when the first user has started speaking)
attsys		Whether or not the user was attending the system at the start of the utterance
----------	-------

Here is an example of how sense.user.speech.start might be used. It triggers when the first user starts to speak and if that user is attended to by the system, in which case the system will make a smile to acknowledge this. If the system is attending to all users, it will also attend to this speaker.  

```xml
<onevent name="sense.user.speech.start" cond="system.isAttending(event) and eq(event:speakers, 1)">
	<if cond="system.isAttendingAll()">
		<agent:attend target="event:user"/>
	</if>
	<agent:gesture name="'smile'"/>
</onevent>
```

### Users

The **system** object (of type [SystemAgent](http://www.iristk.net/javadoc/iristk/situated/SystemAgent.html)) holds information about the system agent and the users interacting with the agent. Both the SystemAgent and the users are of the same basic class, [Agent](http://www.iristk.net/javadoc/iristk/situated/Agent.html), and therefore share many methods related to attention, location in space, etc. 

You might also want to configure the SystemAgent outside of the flow, when setting up the system. You can get hold of the SystemAgent object like this (we are using the name "systemAgent" here, since "system" refers to the whole IrisSystem in this context): 

```java
SystemAgentFlow systemAgentFlow = system.addSystemAgent();
SystemAgent systemAgent = systemAgentFlow.getSystemAgent();
```

#### The interaction space

The SystemAgent has a certain interaction space defined, and people outside of this space are not considered users. There is also a maximum number of users the system can have, and when this number is reached, the system will not consider new people within the interaction space as users, unless someone else leaves. 

If you want detailed control over the interaction space, you might want to use the **setInteractionSpaces**  method. However, there is a more convenient method that just sets a distance from the agent:

```java
// Set the distance to 2 meters
systemAgent.setInteractionDistance(2)
```

To adjust the maximum number of users (default is 2):

```java
systemAgent.setMaxUsers(3)
```

#### Accessing user objects

First of all, it is possible to check how many users are currently interacting (we are assuming you are doing this within the flow, where the SystemAgent is referred to by the "system" object):

```java
// Checks whether there are any users (more than 0)
system.hasUsers()
// Checks whether there are many users (more than 1)
system.hasManyUsers()
// Returns the number of users
system.getNumUsers()
```

To get hold of a specific user object (of type Agent), there are a number of different ways.  You can also provide an id of the user:

```java
// If you are in an event handler (such as sense.user.speak), and want to get the user associated with the event:
system.getUser(event)
// If you have an id of the user:
system.getUser(userId)
// Get the "current" user (the one currently attended to):
system.getCurrentUser()
// Get a random user:
system.getRandomUser()
// Get a random user who is not the current user:
system.getOtherUser()
// Get a user who does not have the specified id:
system.getOtherUser(userId)
```

User objects are of type Agent, which in turn extends [Record](http://www.iristk.net/javadoc/index.html). This means that you can store any information you like in them:

```java
// To store a value
system.getCurrentUser().put("name", "Adam");
// To access a value
system.getCurrentUser().get("name");
// To store a value for all users:
system.putUsers("guesses", 0)
``` 

You can also check if a user or the system is currently speaking:

```java
// Is the system speaking?
system.isSpeaking();
// Is the current user speaking?
system.getCurrentUser().isSpeaking();
```

#### Engagement and location of users

When a user enters or leaves the interaction space, **sense.user.enter** and **sense.user.leave** events are generated, and can be caught:

```xml
<onevent name="sense.user.enter">
	<!-- A user entered, let's attend to her and start interacting -->
	<agent:attend target="event:user"/>
	<goto state="Greeting"/>
</onevent>
<onevent name="sense.user.leave"  cond="system.isAttending(event)">
	<!-- A user that the system was currently attending to left -->
	<if cond="system.hasUsers()">
		<!-- The system still has users left, let's attend to one of them and continue -->
		<agent:attendRandom/>
		<reentry/>
	<else />
		<!-- There is nobody left, let's go to idle -->
		<goto state="Idle"/>
	</if>
</onevent>
```

Each agent (both system and users) has a location in space which can be accessed with the **getHeadLocation** method. As an example, here is how you would find out the distance from the system agent to the current user:

```java
system.getHeadLocation().distance(system.getCurrentUser().getHeadLocation());
```

### Attention

Attention is an important aspect of multi-party interaction. When an agent (user or system) is attending to (looking at) a another agent, this typically means that the attended agent is supposed to respond. Thus, we need to be able to both control the attention of the system agent in an appropriate way, and check the attention of the users. 

#### Controlling the system's attention

There are a number of states that can be called to control the system's attention:

```xml
<!-- Attend to the user that is associated with an event -->
<agent:attend target="event:user"/> 
<!-- Attend to all users (eyes moving between them) -->
<agent:attendAll/>
<!-- Attend to a random user -->
<agent:attendRandom/>
<!-- Attend to a different user than is currently attended -->
<agent:attendOther/>
<!-- Attend to a certain location -->
<agent:attend location="new iristk.situated.Location(0, 0, 2)"/> 
<!-- Attend nobody (look down slightly) -->
<agent:attendNobody/>  
<!-- Fall asleep (look down and close the eyes) -->
<agent:fallAsleep/>  
```

When the system is targeting a user, it will make sure to keep the attention to the user, even if the user moves. This is not the case if a certain location is being attended (with the "location" parameter). 

You can also control how the agent shifts the attention:

```xml
// The default method is to only uses the eyes if the distance is small, and move the head otherwise
<agent:attendOther/>
// If you only want to use the eyes (keeping head still)
<agent:attendOther mode="'eyes'"/>
// If you want to make sure the head moves
<agent:attendOther mode="'headpose'"/>

// If you want to control the speed of the head movement (one of x-slow, slow, medium, fast, x-fast)
<agent:attendOther speed="'slow'"/>
```

#### Checking the attention

It is also possible to check the system's current attention:

```java
// Is the system attending the user associated with this event?
system.isAttending(event)
// Is the system attending a user associated with an id:
system.isAttending(userId)
// Is the system attending all users?
system.isAttendingAll()
// Is the system attending a specific user?
system.isOnlyAttending(event)
// Is the system attending nobody?
system.isAttendingNobody()
```

Similarly, it is possible to check the users' attention:

```java
// Is the current user attending the system?
system.getCurrentUser().isAttending(system.id)
```

You can also catch the event **sense.user.attend** when the users' attention shifts:

```xml
<onevent name="sense.user.attend" cond="eq(event:target, system.id)">
	<!-- If the user shifted attention to the system, attend to that user -->
	<agent:attend target="event:user"/>
</onevent>
```

Speech events also carries a special parameter **attsys** which tells whether the user was attending to the system sometime during the utterance. This can be useful to know whether the user was most likely addressing the system or some other person:

```xml
<onevent name="sense.user.speak" cond="!asBoolean(event:attsys)">
	<!-- The user was not attending the system, attend to the user, but do not say anything -->
	<agent:attend target="event:user"/>
</onevent>
```

### Gestures

Gestures can be performed by providing a name of the gesture:

```xml
<agent:gesture name="'smile'"/>
```

The default behaviour is to make the call asynchonously. If you want to block and wait for the gesture to be completed, you can add the **async** parameter:

```xml
<agent:gesture name="'smile'" async="false"/>
```

For information about how to pre-define gestures, see the [Facial gestures reference](gestures.html). As an alternative, you can also provide the gestures on-the-fly:

```xml
<agent:gesture>
	<gesture name="brow_raise">
	    <param name="BROW_UP_LEFT">16(1) 16(1) 16(0)</param>        
	    <param name="BROW_UP_RIGHT">16(1) 16(1) 16(0)</param>
	</gesture>
</agent:gesture>
```



 