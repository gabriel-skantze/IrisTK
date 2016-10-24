## Tutorial 3: Grammars, Semantics and Slot-filling

In the previous tutorials, the dialog was very simple, and the utterances contained barely any semantics (just simple numbers). In this tutorial, we will look at more complex grammars, semantics and dialog. More specifically, you will learn the following things:

* How to create semantic interpretations with deeper and more complex structures
* How to use an open vocabulary recognizer together with a semantic grammar
* How to write more complex flows, that work in a slot-filling fashion 
* How to extend this to multi-party dialog, allowing two users to fill out different forms

Note: The first part of this tutorial only assumes that you have taken [Tutorial 1](tutorial_first_app.html), and know how to create a new application. The last part also assumes that you have taken [Tutorial 2](tutorial_sitint.html) on situated interaction.  

### The Burger dialog system

Our example application will be a dialog system that we can imagine being used in a hamburger store to receive orders.  

We will start by using the simple_dialog template. Create an application called "burger" and import it into Eclipse:

```
iristk create simple_dialog burger
iristk eclipse
```

### Speech and semantics

To start with, we will create a grammar for the application. Replace the contents of BurgerGrammar.xml with this: 

```xml
<?xml version="1.0" encoding="utf-8"?>
<grammar xml:lang="en-US" version="1.0" root="root"
	xmlns="http://www.w3.org/2001/06/grammar" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.w3.org/2001/06/grammar http://www.iristk.net/xml/srgs.xsd"
	tag-format="semantics/1.0">

	<rule id="root" scope="public">
		<one-of>
			<item>
				<item repeat="0-1">
					<one-of>
						<item>i would like</item>
						<item>i would like to order</item>
						<item>i would like to have</item>
						<item>i want</item>
						<item>i want to order</item>
						<item>i want to have</item>
						<item>could i have</item>
						<item><ruleref uri="#yes"/></item>
					</one-of>
				</item>
				<ruleref uri="#order"/>
				<tag>out.order = rules.order</tag>
				<item repeat="0-1">please</item>
			</item>
			<item>
				<ruleref uri="#yes"/>
				<tag>out.yes=1</tag>
			</item>
			<item>
				<ruleref uri="#no"/>
				<tag>out.no=1</tag>
			</item>
			<item>
				<ruleref uri="#flavor"/>
				<tag>out.flavor = rules.flavor</tag>
			</item>
		</one-of>
	</rule>

	<rule id="order">
		<item repeat="1-3">
			<one-of>
				<item>
					<ruleref uri="#main" />
					<tag>out.main = rules.main</tag>
				</item>
				<item>
					<ruleref uri="#drink" />
					<tag>out.drink = rules.drink</tag>
				</item>
				<item>
					<ruleref uri="#side" />
					<tag>out.side = rules.side</tag>
				</item>
			</one-of>
			<item repeat="0-1">and</item>
		</item>
	</rule>

	<rule id="main">
		<one-of>
			<item>
				<item repeat="0-1">
					<ruleref uri="#count" />
					<tag>out.count = rules.count</tag>
				</item>
				<item repeat="0-1">
					<ruleref uri="#size" />
					<tag>out.size = rules.size</tag>
				</item>
				<one-of>
					<item>
						<one-of>
							<item>burger</item>
							<item>hamburger</item>
							<item>hamburgers</item>
						</one-of>
						<tag>out.type="hamburger"</tag>
					</item>
					<item>
						<one-of>
							<item>cheeseburger</item>
							<item>cheeseburgers</item>
						</one-of>
						<tag>out.type="hamburger"; out.topping="cheese"</tag>
					</item>
				</one-of>
			</item>
		</one-of>
	</rule>

	<rule id="drink" scope="public">
		<one-of>
			<item>
				<item repeat="0-1">
					<ruleref uri="#count" />
					<tag>out.count = rules.count</tag>
				</item>
				<item repeat="0-1">
					<ruleref uri="#size" />
					<tag>out.size = rules.size</tag>
				</item>
				<one-of>
					<item>
						<item repeat="0-1">
							<ruleref uri="#flavor"/>
							<tag>out.flavor = rules.flavor</tag>
						</item>
						<item>milkshake</item>
						<tag>out.type="milkshake"</tag>
					</item>
					<item>coke<tag>out.type="coke"</tag></item>
					<item>sprite<tag>out.type="sprite"</tag></item>
					<item>fanta<tag>out.type="fanta"</tag></item>
				</one-of>
			</item>
		</one-of>
	</rule>

	<rule id="side" scope="public">
		<one-of>
			<item>
				<item repeat="0-1">
					<ruleref uri="#count" />
					<tag>out.count = rules.count</tag>
				</item>
				<item repeat="0-1">
					<ruleref uri="#size" />
					<tag>out.size = rules.size</tag>
				</item>
				<one-of>
					<item>
						<one-of>
							<item>fries</item>
							<item>some fries</item>
							<item>bag of fries</item>
						</one-of>
						<tag>out.type="fries"</tag>
					</item>
					<item>sallad<tag>out.type="sallad"</tag></item>
				</one-of>
			</item>
		</one-of>
	</rule>

	<rule id="flavor">
		<one-of>
			<item>strawberry</item>
			<item>chocolate</item>
			<item>banana</item>
			<item>vanilla</item>
		</one-of>
	</rule>

	<rule id="size">
		<one-of>
			<item>large</item>
			<item>medium</item>
			<item>small</item>
		</one-of>			
	</rule>

	<rule id="count">
		<one-of>
			<item>a<tag>out=1</tag></item>
			<item>one<tag>out=1</tag></item>
			<item>two<tag>out=2</tag></item>
		</one-of>
	</rule>
	
	<rule id="yes">
		<one-of>
			<item>yes</item>
			<item>yes I do</item>
			<item>sure</item>
			<item>yeah</item>
			<item>of course</item>
			<item>okay</item>
		</one-of>
	</rule>

	<rule id="no">
		<one-of>
			<item>no</item>
			<item>no way</item>
			<item>nope</item>
			<item>not really</item>
			<item>I don't think so</item>
		</one-of>
	</rule>

</grammar>
```

We will now introduce a useful tool called TestRecognizer for experimenting with grammars and speech recognition. Start the tool like this:

```
iristk asr
```

![](img/asr_tool.jpg)

On the top left, you can choose a recognizer to test. Choose WindowsRecognizer. Below, you can see two grammar windows: a Speech Grammar and a Semantic Grammar. In this tutorial, we will only use the Speech Grammar. 

Either you can copy and paste the BurgerGrammar.xml into the Speech Grammar window, or you can drag-and-drop the file from Eclipse. If you choose the latter, you can then save any changes you make directly from the tool. In order to use the grammar, we must load it into the recognizer. Press the Load button to do so. It is important to remember that you have to do this every time you change the grammar for the changes to take effect. If everything works fine, you will see a positive message in the Output window on the upper right, otherwise you will get an error message (for example if the grammar is ill-formed). 

When you have loaded the grammar, you can test the recognizer by pressing the Listen button (have your microphone ready). If you say "I would like a cheese burger and a large coke", the resulting semantics (shown in the Output window) should look like this:

```
{order: 
 {drink: 
   {count: 1
    type: coke
    size: large}
  main: 
   {count: 1
    topping: cheese
    type: hamburger}}}
```

Examine the grammar and try to understand what it allows. Remember that the Speech Grammar must match exactly what you say. You could for example try:   

* "I would like to order a strawberry milkshake please"
* "I want to have two large hamburgers and a sallad"
* "a small hamburger"
* "banana"
* "yes"

The basic elements of the grammar are: 

* **\<rule\>** defines a rule. The "root" rule must cover the whole utterance. Each rule contains a list of elements that should match.
* **\<ruleref\>** matches another rule.
* **\<one-of\>** matches one of the sub-items.
* **\<item\>** matches a string or a list of other elements. 
* **\<item repeat="0-1"\>** matches the contents 0 to 1 times (which means that it is optional). 
* **\<tag\>** contains a Javascript that creates the resulting semantics
	* The resulting object can be referred to with the "out" variable. By default, it is assumed to be a Javascript object. Javascript objects can be regarded as a record with any number of fields, which can be assigned with dot-notation (out.topping="cheese"). 
	* If you assign a field with another object (such as the output of another rule), you will created nested records (as in the example above). To refer to the output of the matching rules, you can use rule.[name\_of\_the\_rule] (e.g. "out.size = rules.size"). 
	* Not all rules gives a Javascript object as output. If no \<tag\> is specified, the result will be a string with the matching words of the rule (see for example the "flavor" rule). But you can also specify a string output like this: out="banana", or an integer output like this: out=1.
	* In IrisTK, the Javascript object is transformed into a Java-object of type [iristk.util.Record](http://www.iristk.net/javadoc/iristk/util/Record.html).

If you want to understand how the grammar works in more depth, you can read the official [W3C SRGS specification](http://www.w3.org/TR/speech-grammar/). 

**NB**: If you want to read more about how speech recognition and grammars are used in IrisTK, including how to use Open Vocabulary recognizers and Semantic grammars, please refer to the [Speech recognition reference](speech_recognition.html).

### A slot-filling dialog flow

To manage the burger orders, we will implement a simple slot-filling algorithm in the flow. By "slot-filling", we mean that the system will check that it has received all pieces of information that are necessary before finishing the order. In case there are slots missing, the system will request more detailed information from the user.

Open up the BurgerFlow.xml and replace the contents with this:

```xml
<?xml version="1.0" encoding="utf-8"?>
<flow name="BurgerFlow" package="iristk.app.burger" 
	initial="Start"	xmlns="iristk.flow" xmlns:p="iristk.flow.param" xmlns:dialog="iristk.flow.DialogFlow" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="iristk.flow flow.xsd iristk.flow.DialogFlow DialogFlow.xsd">
	
	<var name="order" type="Record" value="new Record()"/>
	
	<state id="Start" extends="Dialog">
		<onentry>
			<if cond="count == 1">
				<dialog:say>Welcome</dialog:say>
			</if>
			<dialog:say>May I please take your order</dialog:say>
			<dialog:listen/>
		</onentry>
	</state>	
	
	<state id="Dialog">
		<onevent name="sense.user.speak" cond="event?:sem:order">
			<exec>order.adjoin(asRecord(event:sem:order))</exec>
			<goto state="CheckOrder"/>	
		</onevent>
		<onevent name="sense.user.speak">
			<dialog:say>Sorry, I <str cond="count > 1">still</str> didn't get that</dialog:say>
			<reentry/>	
		</onevent>
		<onevent name="sense.user.silence">
			<dialog:say>Sorry, I <str cond="count > 1">still</str> didn't hear anything</dialog:say>
			<reentry/>	
		</onevent>
	</state>
	
	<state id="CheckOrder">
		<onentry>
			<if cond="!order?:main">
				<goto state="RequestMain"/>
			<elseif cond="!order?:drink"/>
				<goto state="RequestDrink"/>
			<elseif cond="eq(order:drink:type, 'milkshake') and !order?:drink:flavor"/>
				<goto state="RequestFlavor"/>
			<elseif cond="!order?:side"/>
				<goto state="RequestSide"/>	
			<else/>
				<goto state="Done"/>
			</if>
		</onentry>
	</state>
	
	<state id="RequestMain" extends="Dialog">
		<onentry>
			<dialog:say>Do you want a hamburger?</dialog:say>
			<dialog:listen/>
		</onentry>
		<onevent name="sense.user.speak" cond="event?:sem:yes">
			<exec>order:main:type = "hamburger"</exec>
			<goto state="CheckOrder"/>	
		</onevent>
		<onevent name="sense.user.speak" cond="event?:sem:no">
			<exec>order:main:type = "none"</exec>
			<goto state="CheckOrder"/>	
		</onevent>
	</state>
	
	<state id="RequestDrink" extends="Dialog">
		<onentry>
			<dialog:say>Do you want anything to drink?</dialog:say>
			<dialog:listen/>
		</onentry>
		<onevent name="sense.user.speak" cond="event?:sem:yes">
			<dialog:say>So what do you want to drink?</dialog:say>
			<dialog:listen/>
		</onevent>
		<onevent name="sense.user.speak" cond="event?:sem:no">
			<exec>order:drink:type = "none"</exec>
			<goto state="CheckOrder"/>	
		</onevent>
	</state>
	
	<state id="RequestFlavor" extends="Dialog">
		<onentry>
			<dialog:say>What flavor do you want in your milkshake?</dialog:say>
			<dialog:listen/>
		</onentry>
		<onevent name="sense.user.speak" cond="event?:sem:flavor">
			<exec>order:drink:flavor = event:sem:flavor</exec>
			<goto state="CheckOrder"/>	
		</onevent>
	</state>
	
	<state id="RequestSide" extends="Dialog">
		<onentry>
			<dialog:say>Do you want anything on the side, such as fries or sallad?</dialog:say>
			<dialog:listen/>
		</onentry>
		<onevent name="sense.user.speak" cond="event?:sem:yes">
			<dialog:say>So what do you want on the side?</dialog:say>
			<dialog:listen/>
		</onevent>
		<onevent name="sense.user.speak" cond="event?:sem:no">
			<exec>order:side:type = "none"</exec>
			<goto state="CheckOrder"/>	
		</onevent>
	</state>
	
	<state id="Done">
		<onentry>
			<dialog:say>Okay, thanks for your order</dialog:say>
			<log><expr>order.toStringIndent()</expr></log>
			<exec>System.exit(0)</exec>
		</onentry>
	</state>
	
</flow>
```

You should now be able to compile the flow and then run BurgerSystem.java to try out the application.

Let's now look at the flow. To keep track of the order, we start by creating a variable "order" of type "Record". The initial state in the flow is "Start", which looks like this: 

```xml
<state id="Start" extends="Dialog">
	<onentry>
		<if cond="count == 1">
			<dialog:say>Welcome</dialog:say>
		</if>
		<dialog:say>May I please take your order</dialog:say>
		<dialog:listen/>
	</onentry>
</state>	
```

The contents of this state should be familiar (if you have taken Tutorial 1), but there is one new thing: We make use of the special variable "count". This variable is very useful, it keep tracks of how many times this event handler has been triggered throughout this state's lifetime. If there is a transition (with \<goto\>), this counter will be reset. Thus, we can make sure that the system only says "Welcome" once, in case there is a \<reentry\>. Note that there are no event handlers to handle the result of the \<dialog:listen\> action, they are defined in the generic "Dialog" state:

```xml
<state id="Dialog">
	<onevent name="sense.user.speak" cond="event?:sem:order">
		<exec>order.adjoin(asRecord(event:sem:order))</exec>
		<goto state="CheckOrder"/>	
	</onevent>
	<onevent name="sense.user.speak">
		<dialog:say>Sorry, I <str cond="count > 1">still</str> didn't get that</dialog:say>
		<reentry/>	
	</onevent>
	<onevent name="sense.user.silence">
		<dialog:say>Sorry, I <str cond="count > 1">still</str> didn't hear anything</dialog:say>
		<reentry/>	
	</onevent>
</state>
```

The first event handler will be triggered if the user utterance contains an order. Notice how the deep record structure of the semantic can be queried with "event?:sem:order" (meaning: "does the 'event' contain a 'sem' field, which in turn contains an 'order' field?"). Compare with the example semantic records we have seen from the recognizer. The semantics if the order is then adjoined to the flow-level "order" variable, after which we transition to the "CheckOrder" state to see if the order is complete. We also have two event handlers to handle utterances without any meaningful semantics, and silence from the user. Note again how we make use of the "count" variable in the \<str\> tag to make the system utterances more varied and context-aware.   

The "CheckOrder" state then goes through the fields and checks if there are any missing slots:

```xml
<state id="CheckOrder">
	<onentry>
		<if cond="!order?:main">
			<goto state="RequestMain"/>
		<elseif cond="!order?:drink"/>
			<goto state="RequestDrink"/>
		<elseif cond="eq(order:drink:type, 'milkshake') and !order?:drink:flavor"/>
			<goto state="RequestFlavor"/>
		<elseif cond="!order?:side"/>
			<goto state="RequestSide"/>	
		<else/>
			<goto state="Done"/>
		</if>
	</onentry>
</state>
```

The code should be self-explanatory. Note that we can check both more shallow slots such as "order:drink", but also deeper slots such as "order:drink:flavor", in case the user ordered a milkshake but did not specify the flavor. For each type of slot that can be requested, a specific state is defined. Let's look at "RequestDrink":

```xml
<state id="RequestDrink" extends="Dialog">
	<onentry>
		<dialog:say>Do you want anything to drink?</dialog:say>
		<dialog:listen/>
	</onentry>
	<onevent name="sense.user.speak" cond="event?:sem:yes">
		<dialog:say>So what do you want to drink?</dialog:say>
		<dialog:listen/>
	</onevent>
	<onevent name="sense.user.speak" cond="event?:sem:no">
		<exec>order:drink:type = "none"</exec>
		<goto state="CheckOrder"/>	
	</onevent>
</state>
```

The state starts by asking "Do you want anything to drink?". Note that the design of the flow now allows the user to reply in a number of different ways:

* If the user says "I want a coke" or just "a coke", this is handled in the "Dialog" state. Since the drink will be adjoined to the order, the CheckOrder will not ask for the drink again.
* If the user just says "yes", the system will ask a more direct question "So what do you want to drink?". 
* If the user says "no", the system will fill this slot with the value "none", so that the CheckOrder will not ask for the drink again.
* The user can also say something else such as "I want a sallad", which will be adjoined to the order. The CheckOrder will then ask for the drink again. However, it will not ask if the user wants a sallad later on, since this information has already been provided. 
* Similarly, the user can over-answer the question with "a coke and a sallad please". Again, this extra information will not be lost.

This shows how a relatively compact dialog design with hierarchical states can be used to allow for a very flexible dialog with many different scenarios. It should be possible to apply the same pattern to many different applications, such as ticket or restaurant booking. In the Chess example that comes with IrisTK, you can also see how this pattern is used.  

Once the order is complete, it will be printed in the console (with the \<log\> action), so that you can see what it looks like.

### Allowing barge-in

As it is now, the system is either speaking or listening. Thus, it is not possible for the user to iterrupt the system. A common feature in dialog systems is to allow barge-in, which means that the user can interrupt the system while it is speaking. For this, there is a reusable behavior called \<dialog:prompt\> that we can use instead of \<dialog:say\> and \<dialog:listen\>. 

Try to replace this:

```xml
<dialog:say>May I please take your order</dialog:say>
<dialog:listen/>
```

with this:
  
```xml  
<dialog:prompt>May I please take your order</dialog:prompt>
```

Similarly, you can do this at all places where you have a \<dialog:say\> and \<dialog:listen\>. Now, you should be able to interrupt the system while it is speaking.  

*Note*: there is currently no echo cancellation built into IrisTK. Thus, if you have an open microphone and an open speaker, the system may interrupt itself. However, if you are using a headset or a laptop with built-in echo cancellation, you should be fine.

### Adding a resusable confirmation dialog

So far, the system has simply accepted all speech recognition results without any confirmation (such as "did you say a large coke?"). This may lead to misunderstandings. However, confirming everything the user says is very tedious, so a common strategy is to look at the confidence score from the recognizer to determine whether to confirm or not. 

It would not be very elegant to add such a check for confirmation and implement a confirmation dialog after each request the system makes. The confirmation pattern is also similar regardless of what the system requested. Thus, the best thing would be if we could create a behavior that could be reused between different applications. The \<dialog:say\>, \<dialog:listen\> and \<dialog:prompt\> are already examples of such reusable behaviors. So, let's create such a behavior for requests that can be confirmed. In IrisTK, reusable behaviors are implemented as states, and then we _call_ these states. There are two important differences between "call" and "goto": (1) When a state is called, the event handlers of the calling state are still checked for (after the event handlers of the called state), (2) The called state can make a \<return\> to the calling state. If that state was in the middle of a series of executing actions, this execution will continue from where it was. You can read more about the difference between _call_ and _goto_ in [IrisFlow overview](irisflow_overview.html).

We will name this new behavior "ask". It also needs another help-state "confirm":

```xml
<state id="ask">
	<param name="text"/>
	<param name="threshold" type="Float" default="0.7"/>
	<onentry>
		<dialog:prompt text="text"/>
	</onentry>
	<onevent name="sense.user.speak" cond="threshold > asFloat(event:conf)">
		<goto state="confirm" p:cevent="event"/>
	</onevent>
	<onevent name="sense.user.speak sense.user.silence">
		<return copy="event"/>
	</onevent>
</state>

<state id="confirm">
	<param name="cevent" type="Event"/>
	<onentry>
		<if cond="cevent?:sem:confirm">
			<dialog:prompt>Did you say <expr>cevent:sem:confirm</expr></dialog:prompt>
		<else/>
			<dialog:prompt>Did you say <expr>cevent:text</expr></dialog:prompt>
		</if>
	</onentry>
	<onevent name="sense.user.speak" cond="event?:sem:yes">
		<return copy="cevent"/>
	</onevent>
	<onevent name="sense.user.speak" cond="event?:sem:no">
		<return event="sense.user.speak"/>
	</onevent>
	<onevent name="sense.user.speak sense.user.silence">
		<return copy="event"/>
	</onevent>
</state>
```
   
The state "ask" takes two parameters: "text" (what to ask) and a "threshold". The threshold is used to determine whether the input from the user should be confirmed or not, based on the confidence score. This is 0.7 by default, but by having it as a parameter, it can be set differently for different questions. You should be aware that different recognizers provides different typical scores. The Nuance Cloud recognizer always returns 1, so it will never engage in confirmation. 

On entry, the "ask" state prompts the user with the question (note that the value of the "text" parameter here is the provided parameter). If a speech recognition result is returned (sense.user.speak), the system checks whether the confidence is below the threshold and it should be confirmed. Otherwise, the called state returns and the speech event is raised in the calling state. 

Note that if a transition to the "confirm" state takes place, the flow still keeps the calling state. This means that the calling states event handlers are still active (but not the event handlers of "ask"). Thus, if we make a "return" from the "confirm" state, we end up in the state that called the "ask" state (as seen on the left in the picture below). However, if an event handler in the calling state issues a "goto", the called states is aborted (as illustrated on the right).      

![](img/transitions2.png)
   
The "confirm" state takes the original event (cevent) as a parameter. It then asks a yes/no question to the user using the recognized text. However, we have added an extra trick here to avoid very tedious wordings. For example, it would not be very elegant if the system would say "Did you say I want to order a hamburger and a coke please"?. We would rather want it to say "Did you say a hamburger and a milkshake?". To make this possible, we add an extra (optional) field to the semantics called "confirm" that collects the words that should be used in case of a confirmation. In the root rule, change the semantic instruction \<tag\>out.order = rules.order\</tag\> into:   
   
```xml
<tag>out.order = rules.order; out.confirm = meta.order.text</tag>
```   

The "meta.order.text" returns the text that matched the "order" rule, and this is now placed int the "confirm" field of the output. If this field is present, it will be used by the "confirm" state, otherwise the whole text will be used. Now if the user confirms with a "yes", the state will return, and the original speech event will be raised in the calling state. If the user says "no", a new "sense.user.speak" state will be raised, which will trigger a non-understanding in the calling state (since it doesn't contain any text or semantics). If the user is silent or says simething else, the corresponding event will be raised in the calling state. Thus, if the user says something like "I want a coke" instead of replying to the yes/no question, it will be handled appropriately.  
   
Now, we can call this "ask" state with this constuction:

```xml  
<call state="ask">May I please take your order</call>
``` 

However, it is more elegant to call it with a custom tag. To do this, add the following attribute to the \<flow\> tag:

```xml
xmlns:this="iristk.app.burger.BurgerFlow"
```  

You can now replace all \<dialog:prompt\> tags with \<this:ask\> tags like this:

```xml  
<this:ask>May I please take your order</this:ask>
``` 

If you reply to any of these question in a "sloppy" way, the system should engage in a confirmation. Try to respond in different ways to the confirmation and see what happens. You might have to adjust the default threshold for this to happen. 

If you open core/src/flow/DialogFlow.xml, you could actually add this new behavior there, so that it could be reused from other flows (with \<dialog:ask\>). It would then need to be declared "static" and "public". To read more about this, please refer to [IrisFlow: Advanced topics](irisflow_advanced.html).  

### Situated interaction: Handling multiple customers

TBA
