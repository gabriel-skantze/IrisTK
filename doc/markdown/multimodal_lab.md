## Lab: Multimodal Dialogue Systems

[IrisTK](http://www.iristk.net) is a an open source Java-based framework for developing conversational systems, developed at KTH. In this lab, you will experiment with a Quiz game application, where two users can play a quiz with an animated agent.

### Preparation before the lab

1. IrisTK uses an XML-based formalism for controlling the flow of the dialogue. Thus, the lab requires some basic familiarity with XML. If you don't have that, please read through [this tutorial](http://www.xmlnews.org/docs/xml-basics.html) (or some other tutorial you find on the web). 
2. Read through these instructions to get an idea of what the lab is about. Check the reference at the end of this document to get an idea of what the XML flow looks like and what the different elements mean.
3. Please bring a pair of headphones (with standard 3.5mm plug) to the lab, if you have one!

### Prerequisites

The lab requires the following things, in case you would like to test out the lab on your own:

1. A computer running Windows 7 or 8, English version. 
2. Eclipse and Java installed
3. [Singstar microphones](http://us.playstation.com/ps3/accessories/singstar-microphones-ps2-ps3.html) (wired or wireless). 

### Preparation at the lab

#### Download and install IrisTK

1. We have created a minimum installer for IrisTK. You might have to tell the browser that you allow the file to be executed, or even rename the file, if necessary.
	* [32 bit version](iristk-min.exe) 
	* [64 bit version](iristk-min-64.exe) 
2. Follow the installation instructions. Accept the default location if you don't have a good reason for choosing a different location. Please note the location for later use.
2. Open a [command window](http://pcsupport.about.com/od/windows7/a/command-prompt-windows-7.htm) and run "iristk eclipse quiz"
3. Open Eclipse from the start menu, set the workspace if you have not already done so before
4. Choose "File" > "Import" > "General" > "Existing Projects into Workspace"
5. Select the IrisTK root directory (where you chose to install IrisTK) and press "Finish"

#### Headphones

Please bring your own headphones, we only have a limited number. Plug in two pairs of headphones in the computer, using a splitter (provided by the lab assistant).

#### Microphones

Microphones are provided by the lab assistant. We use regular Singstar microphones.

1. Plug in the stereo microphones and wait until windows have detected them
2. Right-click on the speaker icon in the bottom right corner of the screen and choose "Recording devices"
3. Make sure the "USBMIC" microphone is selected as default (green symbol). You should see the level bars move when you speak in the microphones. 
4. Select the "USBMIC" microphone and choose Properties
5. Open the "Levels" tab and set the level to 22
6. Open the "Advanced" tab and set the format to 2 channels, DVD quality
7. Close all sound control windows by pressing OK

The person on the left should have the red microphone and the person on the right the blue microphone

#### Test the Quiz game

1. Open Eclipse
2. Locate "app/quiz/src" in the Package explorer
3. Right click QuizSystem.java and choose "Run As" > "Java Application"
4. Make sure you don't get any errors (red text) in the Console view in Eclipse. Warnings can be safely ignored.
5. Make sure the IrisTK window and the Agent are visible
6. In the Situation view, double-click in front the the system agent in order to add one or two users to the interaction (see picture below). The system should start speaking. You can drag the users to move them around, in which case the agent should follow them with the gaze. You can also remove the users by double-clicking on them, which should end the dialog.
7. Again, make sure you don't get any errors (red text) in the Console view in Eclipse.
8. To stop the game, press the red square in the Console view in Eclipse. This is important, so that you don't run several instances simultaneously.

<img src="screenshot.png" style="width:100%"/>

#### Understand the flow and the grammar

1. Locate QuizFlow.xml in "app/quiz/src" and double-click to open it. This files defines the interaction.
2. Locate QuizGrammar.xml in "app/quiz/src" and double-click to open it. This files defines things the user can say (apart from answers to questions). The format is SRGS, [Speech Recognition Grammar Specification](http://www.w3.org/TR/speech-grammar/).  
3. Try to understand the structure of the flow and play the game in different ways. Try to understand how different events are handled in the flow.
	* What happens if you say "no" when the agent asks if you want to play? 
	* What happens if the agent asks the question to one person and the other person answers?
	* What happens if the agent asks the question to both persons and both answer at the same time?
4. Locate "questions.txt" in "app/quiz/src", which contains the questions that the system asks. If you want, you can change the questions. Each line contains seven columns (separated with semicolon): (1) the question, (2) the correct answer, (3-5) wrong alternatives, (6) the category, and (7) the difficulty.
	
#### Program and compile the flow
	
1. Go to the state named Idle and change what the system says when the dialog starts (in the &lt;dialog:say&gt; element)
2. The XML is dynamically checked by Eclipse for syntactic errors. Look out for red marks in the right margin of the editor window which indicate errors. 
3. Before the changes take effect, you must compile the XML flow: 
	* Locate app &gt; quiz &gt; build.xml, right click on it and choose "Run As" &gt; "Ant Build"
	* You should see the results of the compilation in the Console view. Make sure no errors are reported. Note that errors are not necessarily printed in red. The flow XML has now been converted to Java. 
	* **Important**: Refresh the IrisTK folder by selecting it and pressing F5. You need to do this every time you have compiled the flow in order for Eclipse to compile the Java source code to byte code.
	* Make sure that there were no errors in this final step. If there were errors, you will see a red mark next to QuizFlow.java in the Package Explorer.
4. Run the game again and confirm that your changes have taken effect.

### Exercises

You are now ready for some exercises! Try to solve as many as you can, given the time you have. You don't have to do the exercises in the specified order.

#### Make the system's utterances more varied

The dialogue can be much more engaging and natural if you add variations to what the system says. Add alternative utterances throughout the flow and see the effects.

*Tip*: Enclose actions with the \<random\> element to make random choices between actions:

```xml
<random>
	<dialog:say>Would you like to play a game with me?</dialog:say>
	<dialog:say>Are you up for some game?</dialog:say>
</random>
```

#### Extend the grammar

The things the user can say are defined in the speech recognition grammar QuizGrammar.xml. It is currently very limited. Add more alternative things the user can say for "yes" and "no". 

*Tip*: Check the [Speech Recognition Grammar Specification](http://www.w3.org/TR/speech-grammar/) if you want to try more advanced constructions. 

**Note**:  You can only use letters and numbers in the grammar, no punctuation such as question marks.

#### Avoiding the question

Add the possibility of saying "I don't know", which should have the same effect as not saying anything ("sense.user.silence"). 

*Tip*: Extend the QuizGrammar.xml with a new rule with a new semantic tag. Add an event handler to the AwaitAnswer state (at the right place) which makes the flow raise the "skip" event.

#### Repeating the question

The question is only read once and there is currently not way of getting the agent to repeat the question. If the user says "can you read the question again", the agent should do so.
 
#### Comment on the users' performance

When a user has answered correctly, the system simply says "That is correct". Use the current scores of the players to comment on their performance. For example, if a player has many points, the agent could say something like "You seem to be very good at this!".

*Tip*: Use an &lt;if&gt; construction (see the rest of the CorrectAnswer state for examples, or check the reference below). To access the score you should use asInteger("users:current:score", 0). This makes sure that the value is treated as an integer, and gives it a default value of 0 if there is no score already.

If you want, you could also take the other player's score into account. This can be accessed with "users:other:score". 

#### Shifting attention

If the user says "I don't know" as an answer to the question, or does not answer at all, the system says "Give me your best guess" and listens for the answer again. 
If there are several users, make the system instead switch to the other player and ask "Maybe you know the answer?". 

*Tip*: 

* A precondition for switching attention is that there are several users. You can test that with the condition "users.hasMany()". Use an &lt;if&gt;,&lt;else&gt; construction (see the rest of the flow for examples). 
* Switch attention with the action &lt;dialog:attendOther/&gt;

#### Adding gestures

You can make the agent do gestures. Try to add some gestures to the flow that make sense. You make the agent do a gesture by issuing the following action:

```xml
<send event="action.gesture" p:name="'smile'"/>
```

See the reference further down for a complete list of gestures that the agent supports.

#### Adding categories

Each question has a category (Literature, Music, etc) specified. Before each question, the agent could state which category the next question is and then ask the user if (s)he wants to have that question or another one. 

*Tip*: The category can be read back to the user with this action:

```xml
<dialog:say>The next question is about <expr>question:category</expr></dialog:say>
```

#### Restart the game

After the game, the system just says goodbye and goes to the Idle state. It should ask if the players want to play again. To speed up the game, you can change the number of points that are required for winning (defined in the variable "winningScore" which defaults to 3).

#### Your own ideas

Try to come up with interesting ways of improving the game! If you know how to program Java, you can of course also change other parts of the code than the flow. 

#### Clean up

If you do not want to keep the IrisTK folder in your roaming profile, you should delte it from there.

### Reference

This is what the initial version of the Quiz flow looks like:

```xml
<?xml version="1.0" encoding="utf-8"?>
<flow name="QuizFlow" package="iristk.app.quiz" 
	initial="Idle" xmlns="iristk.flow"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:p="iristk.flow.param"
	xmlns:dialog="iristk.situated.SituatedDialog"
	xsi:schemaLocation="iristk.flow flow.xsd iristk.situated.SituatedDialog SituatedDialog.xsd">
	
	<param name="questions" type="QuestionSet"/>
	<param name="dialog" type="iristk.situated.SituatedDialog"/>
	
	<var name="users" type="iristk.situated.UserModel" value="dialog.getUsers()"/>
	<var name="question" type="Question"/>
	<var name="guess" type="int" value="0"/>
	<var name="winningScore" type="int" value="3"/>
	
	<state id="Idle">
		<onentry>
			<dialog:attendNobody />
		</onentry>
		<onevent name="sense.user.enter">
			<dialog:attend target="event:agent"/>
			<dialog:say>Hi there</dialog:say>
			<goto state="RequestGame"/>
		</onevent>
	</state>
		
	<state id="Dialog">
		<onevent name="sense.user.leave"  cond="users.isAttending(event) or users.isAttendingAll()">
			<if cond="users.hasAny()">
				<dialog:attendRandom/>
				<reentry/>
			<else />
				<goto state="Goodbye"/>
			</if>
		</onevent>
	</state>
	
	<state id="RequestGame" extends="Dialog">
		<onentry>
			<if cond="users.hasMany()">
				<dialog:attendAll/>
			</if>
			<dialog:say>Do you want to play a game?</dialog:say>
			<dialog:listen/>
		</onentry>
		<onevent name="sense.user.speak**" cond="event?:sem:yes">
			<goto state="StartGame"/>
		</onevent>
		<onevent name="sense.user.speak**" cond="event?:sem:no">
			<dialog:say>Okay, maybe another time then</dialog:say>
			<goto state="Goodbye"/>
		</onevent>
		<onevent name="sense.user.speak**">
			<reentry/>
		</onevent>
	</state>
	
	<state id="StartGame" extends="Dialog">
		<onentry>
			<if cond="users.hasMany()">
				<dialog:attendAll/>
				<dialog:say>Okay, let's play a game of quiz. The first to reach <expr>winningScore</expr> points is the winner.</dialog:say>
			<else/>
				<dialog:say>Okay, let's play a game of quiz. Let's try to see if you can get <expr>winningScore</expr> points.</dialog:say>
			</if>
			<goto state="NextQuestion"/>
		</onentry>
	</state>
	
	<state id="Goodbye">
		<onentry>
			<dialog:say>Goodbye</dialog:say>
			<goto state="Idle"/>
		</onentry>
	</state>
	
	<state id="NextQuestion" extends="Dialog">
		<onentry>
			<exec>question = questions.next(); guess = 0</exec>
			<if cond="users.hasMany()">
				<random>
					<block>
						<dialog:attendOther/>
						<dialog:say>The next one is for you</dialog:say>
					</block>
					<block>
						<dialog:attendAll/>
						<dialog:say>Let's see who answers first</dialog:say>
					</block>
				</random>
			<else/>
				<dialog:say>Here comes the next question</dialog:say>
			</if>
			<goto state="ReadQuestion"/>
		</onentry>
	</state>
	
	<state id="ReadQuestion" extends="AwaitAnswer">
		<onentry>
			<dialog:prompt text="question.getFullQuestion()" grammar="asList('default', question.getId())"/>
		</onentry>
	</state>
	
	<state id="ReadOptions" extends="AwaitAnswer">
		<onentry>
			<dialog:prompt text="question.getOptions()" grammar="asList('default', question.getId())"/>
		</onentry>
	</state>
	
	<state id="AwaitAnswer" extends="Dialog">
		<onentry>
			<dialog:listen grammar="asList('default', question.getId())"/>
		</onentry>
		<onevent name="sense.user.speak.multi">
			<if cond="eq(event:sem:answer,question:correct)">
				<dialog:attend target="event:agent"/>
				<goto state="CorrectAnswer"/>
			<elseif cond="eq(event:other:sem:answer,question:correct)"/>
				<dialog:attend target="event:other:agent"/>
				<goto state="CorrectAnswer"/>
			<else/>
				<dialog:say>None of you were correct, let's try another question.</dialog:say>
				<goto state="NextQuestion"/>
			</if>
		</onevent>
		<onevent name="sense.user.speak">
			<if cond="eq(event:sem:answer,question:correct)">
				<goto state="CorrectAnswer"/>
				<else/>
				<goto state="IncorrectAnswer"/>
			</if>
		</onevent>
		<onevent name="sense.user.speak.side">
			<dialog:attendOther mode="'eyes'"/>
			<dialog:say>You were not supposed to answer that</dialog:say>
			<dialog:attendOther mode="'eyes'"/>
			<dialog:say>So, what is your answer?</dialog:say>
			<goto state="AwaitAnswer"/>
		</onevent>
		<onevent name="sense.user.silence">
			<raise event="skip"/>
		</onevent>
		<onevent name="skip">
			<dialog:say>Give me your best guess</dialog:say>
			<goto state="AwaitAnswer"/>
		</onevent>
	</state>
	
	<state id="CorrectAnswer" extends="Dialog">
		<onentry>
			<exec>users:current:score = asInteger(users:current:score,0) + 1</exec>
			<dialog:say>That is correct, you now have a score of <expr>users:current:score</expr></dialog:say>
			<if cond="asInteger(users:current:score,0) &gt;= winningScore">
				<goto state="Winner"/>
			<else/>
				<goto state="NextQuestion"/>
			</if>
		</onentry>
	</state>
	
	<state id="IncorrectAnswer" extends="Dialog">
		<onentry>
			<dialog:say>That was wrong</dialog:say>
			<if cond="users.hasMany() and guess == 0">
				<exec>guess++</exec>
				<dialog:attendOther/>
				<dialog:say>Maybe you know the answer?</dialog:say>
				<goto state="AwaitAnswer"/>
			</if>
			<dialog:say>The correct answer was <expr>question:(question:correct)</expr></dialog:say>
			<goto state="NextQuestion"/>
		</onentry>
	</state>
	
	<state id="Winner" extends="Dialog">
		<onentry>
			<exec>users:current:score = 0</exec>
			<dialog:say>Congratulations, you are the winner</dialog:say>
			<if cond="users.hasMany()">
				<dialog:attendOther/>
				<exec>users:current:score = 0</exec>
				<dialog:say>I am sorry, but you lost.</dialog:say>
			</if>
			<goto state="Goodbye"/>
		</onentry>
	</state>

</flow>
```

The flow basically contains a number of variables and states. The flow is always in one particular state, but can change state through the action \<goto\>. Each state can be thought of as a collection of event handlers (starting with "on"), which in turn contain actions. 

Here is a reference of the different XML tags used in the quiz flow. You can try to use them in different ways. Note that some elements have the "dialog:" namespace prefix. These are calling actions that are defined in another flow, specifically designed for multi-party dialogue with an animated agent. Other elements are generic to all kinds of flow.

```xml
<var name="guess" type="int" value="0"/>
```

Defines a variable with a certain (Java) type and an initial value.

```xml
<state id="Winner" extends="Dialog">
```

Defines a state with an id. The state can extend another state, which means that all the event handlers of the extended state can also trigger. Thus, you can define generic event handlers in the "Dialog" state. However, there event handlers are checked after the event handlers of the current state are checked.

#### Event handlers

The flow contains two types of event handlers:

* \<onentry\>: Triggered when the state is entered.
* \<onevent name="eventname" cond="condition"\>: Triggered by an event. Optionally, a condition can be given for the event handler to trigger.  

When a user is entering or leaving the interaction, the following events are fired:

```java
sense.user.enter
sense.user.leave
```

When a listen action is issued (\<dialog:listen/\>), one of these events will be fired:

```java 
sense.user.speak         // The current speaker has said something
sense.user.speak.multi   // Both users have said something
sense.user.speak.side    // The user not attended to has said something
sense.user.silence       // None of the users said anything
```

The contents of the spoken utterances can be accessed with:

* text: The text that was spoken
* sem: The semantic representation of what was spoken (a Record structure) 

Note that it is possible to use wildcards. Thus, the pattern "sense.user.speak**" catches all of the events above. 

#### Generic Actions

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
	<dialog:say>Hi there</dialog:say>
	<dialog:say>Nice to see you</dialog:say>
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

#### Actions related to Multi-party dialogue

These are actions that control what the agent is doing (speaking, listening, attending, gesturing).

```xml
<dialog:say>Would you like to play a game with me?</dialog:say>
```

Makes the agent say something.

```xml
<dialog:listen/>
```

Makes the agent turn on the speech recogniser and listen to the user.

```xml
<dialog:attend target="event:agent" />
```

Makes the agent attend to the user specified by "target". In this case, the event parameter "agent" is used.

```xml
<dialog:attendAll/>
```

Attend to both users at the same time. The agent will shift the gaze back and forth betwen the users to indicate this. Both users are allowed to answer.

```xml
<dialog:attendOther/>
```

Attend to the user not currently attended to.

```xml
<dialog:attendRandom/>
```

Attend to a randomly selected user.


#### Other Event handlers and Actions

If you want to look at other things supported by the flow, but which are not used in the Quiz example, please refer to the [IrisFlow reference](http://www.iristk.net/irisflow_reference.html).
