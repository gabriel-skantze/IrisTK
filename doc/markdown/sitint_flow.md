## Situated Interaction Flow

### Generic Actions

```xml
<goto state="Greeting" />
```

Go to another state.

```xml
<if cond="COND1">
	ACTION1
<elseif cond="COND2"/>
	ACTION2
<else/>
	ACTION3
</if>
```

If COND1, do ACTION1, else if COND2, do ACTION2, else do ACTION3.

```xml
<reentry/>
```

Enter the current state again, re-triggering the \<onEntry\> event handler.

```xml
<raise event="skip"/>
```

Raises a custom event that can be caught with \<catch event="..."\>

```xml
<random>
	<agent:say>Hi there</agent:say>
	<agent:say>Nice to see you</agent:say>
</random>
```

Randomly selects an action to execute (from any number of actions). If you want to group actions, you can enclose sequence of actions with a \<block\> element.

```xml
<exec>question = questions.next()</exec>
```

Executes Java code. Several statements can be separated with semicolon (;).

```xml
<send event="action.gesture" p:name="'smile'"/>
```

Sends an event. "action.gesture" makes the agent do a gesture. The following gestures are supported:

* smile
* brow_raise
* brow_frown
* express_disgust
* express_sad

### Actions related to Multi-party dialogue

These are actions that control what the agent is doing (speaking, listening, attending, gesturing).

```xml
<agent:say>Would you like to play a game with me?</agent:say>
```

Makes the agent say something.

```xml
<agent:listen/>
```

Makes the agent turn on the speech recogniser and listen to the user.

```xml
<agent:attend target="event:agent" />
```

Makes the agent attend to the user specified by "target". In this case, the event parameter "agent" is used.

```xml
<agent:attendAll/>
```

Attend to both users at the same time. The agent will shift the gaze back and forth betwen the users to indicate this. Both users are allowed to answer.

```xml
<agent:attendOther/>
```

Attend to the user not currently attended to.

```xml
<agent:attendRandom/>
```

Attend to a randomly selected user.


### Other Event handlers and Actions

If you want to look at other things supported by the flow, but which are not used in the Quiz example, please refer to the [IrisFlow reference](irisflow_reference.html).
