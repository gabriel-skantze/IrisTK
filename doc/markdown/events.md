## Standardized Events

### General event parameters

Each event has the follow standard parameters:

----------		----	-------
event_name		String	The name of the event (such as "action.speech")
event_id		String	A unique ID for the event 
event_sender	String	The name of the module that sent the event
event_time		String	A timestamp when the event was created
----------		----	-------

### System-related events

#### monitor.system.start

Reported when the system has started (as issued by system.sendStartSignal()).

----------		----	-------
system			String	The name of the system
----------		----	-------

#### monitor.module.start

Reported by a module when it starts.

----------		----	-------
system			String	The name of the system 
----------		----	-------

#### monitor.module.state

Reported by a module when it changes state (for visualization and logging purposes)

----------		----	-------
states			List	A list of state names 
----------		----	-------

### Speech synthesis


#### action.speech

An action that can be sent to a synthesizer to add an utterance to the speech queue 

----------		----		-------
text			String		The text to speak
start			int			The number of milliseconds into the utterance that the synthesis should start
audio			String		An audio file to play instead of synthesizing
display			String		A nicely formatted text representation for display purposes
agent			String		The ID of the agent that is associated with this synthesis. If this is omitted, all synthesizers should start 
ifsilent		Boolean		Only add utterance if the system is silent. (Default is false)
abort			Boolean		Whether to abort the current speech queue. (Default is false, i.e., append the utterance).
monitorWords	Boolean		Whether to send monitor.speech.word events before each word (Default is false)
----------		----		-------

#### action.speech.stop

An action that can be sent to a synthesizer to stop speaking (and clearing the speech queue)

----------		----	-------
action			String	The ID of the action.speech event that started the speech synthesis. If this is omitted, all synthesizers should stop 
----------		----	-------

#### monitor.speech.start
 
Monitors that a speech synthesizer has started an utterance

----------		----		-------
action			String		The ID of the action.speech event that started the speech synthesis
text			String		Taken from action.speech
start			int			Taken from action.speech
agent			String		Taken from action.speech
length			int			The length of the utterance (in msec)
prominence		int			The position in the utterance (in msec) where the prominence is located
----------		----		-------
 
#### monitor.speech.end
 
Monitors that a speech synthesizer has completed an utterance 

----------		----	-------
action			String	The ID of the action.speech event that started the speech synthesis
stopped			int		If the speech synthesis is stopped prematurely, this parameter reports the position in the utterance (in msec) where it stopped 
agent			String	The ID of the agent that is associated with this synthesis
----------		----	------- 

#### monitor.speech.done

Monitors that a speech synthesizer has completed an utterance and the speech queue is empty

----------		----	------- 
agent			String	The ID of the agent that is associated with this synthesis
----------		----	------- 

#### monitor.speech.prominence

Reported by the synthesizer when prominence in the utterance is detected. Can be useful when linked to facial gestures.

----------		----	-------
action			String	The ID of the action.speech event that started the speech synthesis
----------		----	-------

#### monitor.speech.mark

Reported by the synthesizer just before a \<mark\> is being reached in the utterance (part of the SSML specification).

----------		----	-------
action			String	The ID of the action.speech event that started the speech synthesis
name			String	The name of the mark
----------		----	-------

#### monitor.speech.word

Reported by the synthesizer just before each word is being spoken (if monitorWords is set to true in the action.speech event).

----------		----	-------
action			String	The ID of the action.speech event that started the speech synthesis
word			String	The word that is about to be spoken
pos				int		The position of the word in the utterance (0 being the first word)
----------		----	-------

#### action.voice

An action that can be sent to a synthesizer to change the voice. Either name, lang or gender (or a combination thereof) must be provided.  

----------		----	-------
name			String	The name of the voice
lang			String	The language code for the voice (such as "en-US")
gender			String	The gender of the voice ("male" or "female")
agent			String	The ID of the agent that is associated with this synthesis 
----------		----	-------


### Speech recognition

#### action.listen

An action that can be sent to a recognizer to make it start listening.

----------			----			-------
context				String			A name filter with the contexts to use. If omitted, the default context is used (as set with action.context.default).
endSilTimeout		int				The silence timeout (in msec) to detect end-of-speech
noSpeechTimeout		int				The silence timout (in msec) if no speech is detected
maxSpeechTimeout	int				The maximum length of the speech (in msec)
nbest				int				The maximum number of hypotheses to generate
----------			----			-------

#### action.listen.stop

An action that can be sent to a recognizer to make it stop listening.

----------		----	-------
action			String	The ID of the action.listen event that started the speech recognizer. If this is omitted, all recognizers should stop 
----------		----	-------

#### monitor.listen.start

Reported by each RecognizerModule when it starts listening

----------			----			-------
action				String			The ID of the action.listen event that issued this recognition
sensor				String			The ID of the recognizer
----------			----			-------

#### action.context.load

Makes each RecognizerModule load a context (such as a grammar or semantic interpreter). 

----------			----			-------
context				Context			The context to load
----------			----			-------

#### monitor.context.load

Sent when a context is loaded.

----------			----			-------
success				Boolean			Whether loading the context succeeded
message				String			An error message in case the loading failed
action				String			The ID of the action.context.load event
----------			----			-------

#### action.context.default

Sets the default context to be used by each RecognizerModule. This is the context that is used if no context is provided in the action.listen event.

----------			----			-------
context				String			A name filter with the contexts to use as default
----------			----			-------

#### sense.speech.start

Sent by the recognizer when it has detected start-of-speech.

----------			----			-------
action				String			The ID of the action.listen event that issued this recognition
sensor				String			The ID of the recognizer
----------			----			-------

#### sense.speech.end

Sent by the recognizer when it has detected end-of-speech.

----------			----			-------
action				String			The ID of the action.listen event that issued this recognition
length				int				The length of the utterance (in msec)
sensor				String			The ID of the recognizer
----------			----			-------

#### sense.speech.rec

Once the recognition is done, one event that matches sense.speech.rec\*\* is reported. See the different variants below. 

If speech recognition is successful, sense.speech.rec will be generated (without suffix). Note that this includes cases where no grammar matched the recogniton, in which case the "text" parameter will be set to "NO_MATCH". 

----------			----			-------
text				String			The result in text
length				int				The length of the utterance (in msec)
sem					Record			The semantic interpretation
conf				float			Confidence score (from 0.0 to 1.0)
words				List			A list of records containing the individual words
nbest				List			An n-best list of hypotheses (each a Record containing the attributes in this table)
context				String			The name of the context that matched
action				String			The ID of the action.listen event that issued this recognition
sensor				String			The ID of the recognizer
----------			----			-------

#### sense.speech.rec.silence

Reported if no speech was detected and the noSpeechTimeout has been reached.

----------			----			-------
action				String			The ID of the action.listen event that issued this recognition
sensor				String			The ID of the recognizer
----------			----			-------

#### sense.speech.rec.maxspeech

Reported if the user's utterance is too long, and the maxSpeechTimeout has been reached. 

Parameters are the same as for sense.speech.rec.silence.

#### sense.speech.rec.failed

Reported if the speech recognition failed due to a technical problem.

Parameters are the same as for sense.speech.rec.silence.

#### sense.speech.partial

Partial speech recognition result that is reported several times during recognition, if partial results is turned on.  

Parameters are the same as for sense.speech.rec.

### Embodied agents

#### action.gesture

Makes the agent perform a specific gesture.

----------		----			-------
name			String			The name of the gesture
agent			String			The ID of the agent
----------		----			------- 

#### monitor.gesture.start

----------		----			------- 
agent			String			The ID of the agent
action			String			The ID of the action.gesture event that issued this gesture
----------		----			------- 

#### monitor.gesture.end

Sent when the gesture ends

----------		----			------- 
agent			String			The ID of the agent
action			String			The ID of the action.gesture event that issued this gesture
----------		----			------- 

#### action.gaze

Makes the agent shift gaze to a certain location in 3D space

----------		----			-------
location		Location		The 3D location where the agent should gaze
mode			String			See below
agent			String			The ID of the agent
speed			String			How fast the head should move
----------		----			-------

 The mode can be one of these:

* **default**: Uses eyes and neck in a flexible way
* **eyes**: Uses eyes only, hold the neck still
* **headpose**: Center the eyes and use the neck

Speed can be one of **x-slow**, **slow**, **medium**, **fast**, **x-fast**

#### monitor.gaze

Sent when the gaze is shifted

----------		----			-------
action			String			The ID of the action.gaze event that issued this gaze shift
location		Location		The resulting gaze target location
head:rotation	Rotation		The resulting head rotation
agent			String			The ID of the agent
----------		----			-------

#### action.face.texture

Makes the agent change the texture of the face.

----------		----			-------
name			String			The name of the texture
agent			String			The ID of the agent
----------		----			-------

#### action.lipsync

An action sent from the speech synthesizer to the embodied agent if lipsync is turned on.

----------		----			-------
action			String			The ID of the action.speech event that started the speech synthesis
start			int				Taken from action.speech
phones			Transcription	The transcription (phones with timings). 
agent			String			The ID of the agent that is associated with this synthesis
----------		----			------- 

#### action.lipsync.stop

Stop the lipsync.

----------		----			-------
action			String			The ID of the action.speech event that started the speech synthesis
agent			String			The ID of the agent
----------		----			-------

#### monitor.lipsync.start

Sent by an embodied agent to tell the synthesizer that lipsync has started. If lipsync is turned on, the synthesizer should not start until this has been received.

----------		----			-------
action			String			The ID of the action.speech event that started the speech synthesis
agent			String			The ID of the agent
----------		----			------- 

### Vision

#### sense.body

Reported when a camera (or another device) senses bodies

----------			----			-------
bodies				Record			A record with all bodies (with body ID:s as keys and Body objects as values)
sensor				String			The ID of the camera or sensor that detected the bodies
----------			----			-------

#### sense.item

Reported when an item is detected or moved, such as an object that can be referred to during the interaction.

----------			----			-------
items				Record			A record with all items (with item ID:s as keys and Item objects as values)
sensor				String			The ID of the camera or sensor that detected the items
----------			----			-------

### Situated interaction

#### action.situation.detect

An action to retrieve information about the situation, i.e. cause all situation-aware modules to send sense.situation events.

#### sense.situation

Sent by each situation-aware module when it starts, or when action.situation.detect is issued. The event either contains sensors or system agents. 

Each parameter contains either a Sensor or SystemAgent record. The name of each parameter corresponds to the ID of the Sensor or SystemAgent. 

#### action.attend

An action to attend to a specific target, picked up by SystemAgentFlow. Either target or location must be provided.

----------			----			-------
target				String			The ID of a user or item that is to be attended (or "nobody" if nobody is to be attended)
location			Location		A location in 3D space to attend to
mode				String			See action.gaze above
agent				String			The ID of the system agent
speed				String			See action.gaze above
----------			----			-------

#### action.attend.all

An action to attend to all users, picked up by SystemAgentFlow.

#### action.attend.asleep

An action to make the agent fall asleep, picked up by SystemAgentFlow.

#### monitor.attend

Reported when the agent's attention changes, sent by SystemAgentFlow.

#### sense.user.enter

Reported by SystemAgentModule when a user enters the interaction space of the system agent.

----------			----			-------
user				String			The ID of the user agent
head:location		Location		The location of the user's head in absolute space
head:rotation		Rotation		The rotation of the user's head in absolute space
sensor				String			An ID on the form "SID-BID", where SID is the ID of the camera sensor, and BID is the ID of the body reported by the camera sensor
agent				String			The ID of the system agent
----------			----			-------

#### sense.user.leave

Reported by SystemAgentModule when a user leaves the interaction space of the system agent.

----------			----			-------
user				String			The ID of the user agent
agent				String			The ID of the system agent
----------			----			-------

#### sense.user.move

Reported by SystemAgentModule when one or more users move. 

Each user is represented as a record under a parameter that is the user agent ID. Each such user record has the same parameters as sense.user.enter.

----------			----			-------
agent				String			The ID of the system agent
----------			----			-------

#### sense.user.attend

Reported by SystemAgentModule when a user's attention shifts.

----------			----			-------
user				String			The ID of the user agent
target				String			The ID of the agent that is being attended
sensor				String			See sense.user.enter
agent				String			The ID of the system agent
----------			----			-------

#### sense.item.move

Items relevant to the system agent have been moved.

----------			----			-------
agent				String			The ID of the system agent
items				Record			All items that have moved, with the ID of each item as keys
prominent			String			The ID of the items that was moved most
----------			----			-------

#### sense.user.speak

This is a higher-level event that is produced by the SystemAgentFlow. It corresponds to sense.speech.rec. However, even if there several recognizers, only one sense.user.speak event will be generated. Thus, it is very useful for handling multi-party interaction.   

The action.listen will result in either

* sense.user.speak
* sense.user.silence
* sense.user.speak.side (only for multi-party interaction)
* sense.user.speak.multi (only for multi-party interaction)

The parameters are the same as sense.speech.rec above, with the addition of:

----------			----			-------
user				String			The ID of the user agent
attsys				boolean			Whether or not the user was attending the system during the utterance
all					List			If several users were speaking, all events will be placed here, each with the same parameters as sense.speech.rec above.
----------			----			-------

#### sense.user.speak.side

Reported if one of the users was attended to by the system, and some other user answered.

The parameters are the same as for sense.user.speak.

#### sense.user.speak.multi

Reported if the system was attending all users and several users replied while attending the system, or if no user attended the system, but several of them replied. If only one user replied, or if only one user replied while attending the system, sense.user.speak is generated instead.

The parameters are the same as for sense.user.speak, with the speech data from the different users merged. The separate speech events can be accessed by the parameter "all". 

#### sense.user.silence

Reported if no user replied and the end-of-speech silence threshold was reached.

#### sense.user.speech.start

Reported at the start of speech for each user. Corresponds to sense.speech.start, but with the user identified.

----------	----		-------
user		String		The ID of the user agent
speakers	int			The numbers of simultaneous speakers (1 when the first user has started speaking)
attsys		boolean		Whether or not the user was attending the system at the start of the utterance
agent		String		The ID of the system agent
----------	----		-------

#### sense.user.speech.end

Reported at the end of speech for each user. Corresponds to sense.speech.end, but with the user identified.

----------	----		-------
user		String		The ID of the user agent
speakers	int			The numbers of simultaneous speakers (0 when the last user has stopped speaking)
attsys		boolean		Whether or not the user was attending the system during the utterance
agent		String		The ID of the system agent
----------	----		-------

### Logging


#### action.logging.start

Triggers the LoggingModule to start logging.

----------	----		-------
timestamp	String		A timestamp to use when naming the log files. Can be omitted, in which case System.currentTimeMillis() will be used as a name.
----------	----		-------

#### action.logging.stop

Triggers the LoggingModule to stop logging.
