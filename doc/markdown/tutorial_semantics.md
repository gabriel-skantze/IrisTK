## Tutorial 3: Semantics and dialog

In the previous tutorials, the dialog was very simple, and the utterances contained barely any semantics (just simple numbers). In this tutorial, we will look at more complex grammars, semantics and dialog. More specifically, you will learn the following things:

* How to use an open vocabulary recognizer together with a semantic grammar
* How to create semantic interpretations with deeper and more complex structures
* How to write more complex flows, that work in a slot-filling fashion 

Note: The first part of this tutorial only assumes that you have taken [Tutorial 1](tutorial_first_app.html), and know how to create a new application. The last part also assumes that you have taken [Tutorial 2](tutorial_sitint.html) on situated interaction.  

### The Burger dialog system

Our example application will be a dialog system that we can imagine being used in a hamburger store to receive orders.  

We will start by using the simple_dialog template. Create an application called "burger" and import it into Eclipse:

```
iristk create simple_dialog burger
iristk eclipse
```

### Open vocabulary recognizers

Whereas a speech grammar needs to match exactly what the user said, you can also use an open vocabulary recognizer that does not rely on a speech grammar. IrisTK comes with two such recognizers: NuanceCloud and Google. Both of them are cloud-based, which means that you have to create an account, and have an Internet connection when the recognizer is running.  

To use NuanceCloud, you need to [sign up for a developer account](http://developer.nuance.com/public/index.php?task=register), and get your APP_ID and APP_KEY. Create a file called license.properties in the addon/NuanceCloud folder under the IrisTK installation. Enter your credentials like this:

```
APP_ID = NMDPTRIAL...
APP_KEY = 8e26ad5...
```

If you want to use Google, you need to [sign up for a developer account here](https://cloud.google.com/speech/). You then need to create a [Service Account](https://cloud.google.com/speech/docs/common/auth) and download the JSON key file. This file should be named credentials.json and placed in the addon/Google folder.   

To test if the recognizer is working, we will use a tool called TestRecognizer, which will also allow you to experiment with grammars. Start the tool from the command line like this:

```
iristk asr
```

![](img/asr_tool.jpg)

On the top left, you can choose a recognizer to test. If you choose one of the open vocabulary recognizers (NuanceCloud or Google), you should be able to press the Listen button. Talk into the microphone and you will see the result in the Output window. 

### Semantic grammars

Whereas a SpeechGrammar (which was used in Tutorial 1) describes both what the recognizer should listen for, and how this should be interpreted into semantics, an open vocabulary recognizer does not produce any semantic interpretation by itself. Thus we should provide it with a Semantic Grammar. The semantic grammar uses the same format as the speech grammar (SRGS), but the parsing is more relaxed. 

We will now create a semantic grammar for the application. Replace the contents of BurgerGrammar.xml with this: 

```xml
<?xml version="1.0" encoding="utf-8"?>
<grammar xml:lang="en-US" version="1.0"
	xmlns="http://www.w3.org/2001/06/grammar" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.w3.org/2001/06/grammar http://www.iristk.net/xml/srgs.xsd"
	tag-format="semantics/1.0">

	<rule id="main" scope="public">
		<tag>out.order={};out.order.main={}</tag>
		<item repeat="0-1">
			<ruleref uri="#count" />
			<tag>out.order.main.count = rules.count</tag>
		</item>
		<item repeat="0-1">
			<ruleref uri="#size" />
			<tag>out.order.main.size = rules.size</tag>
		</item>
		<one-of>
			<item>
				<one-of>
					<item>burger_</item>
					<item>hamburger_</item>
				</one-of>
				<tag>out.order.main.type="hamburger"</tag>
			</item>
			<item>
				<one-of>
					<item>cheeseburger_</item>
				</one-of>
				<tag>out.order.main.type="hamburger"; out.order.main.topping="cheese"</tag>
			</item>
		</one-of>
	</rule>

	<rule id="drink" scope="public">
		<tag>out.order={};out.order.drink={}</tag>
		<item repeat="0-1">
			<ruleref uri="#count" />
			<tag>out.order.drink.count = rules.count</tag>
		</item>
		<item repeat="0-1">
			<ruleref uri="#size" />
			<tag>out.order.drink.size = rules.size</tag>
		</item>
		<one-of>
			<item>
				<item repeat="0-1">
					<ruleref uri="#flavor"/>
					<tag>out.order.drink.flavor = rules.flavor</tag>
				</item>
				<item>milkshake</item>
				<tag>out.order.drink.type="milkshake"</tag>
			</item>
			<item>coke<tag>out.order.drink.type="coke"</tag></item>
			<item>sprite<tag>out.order.drink.type="sprite"</tag></item>
			<item>fanta<tag>out.order.drink.type="fanta"</tag></item>
		</one-of>
	</rule>

	<rule id="side" scope="public">
		<tag>out.order={};out.order.side={}</tag>
		<item repeat="0-1">
			<ruleref uri="#count" />
			<tag>out.order.side.count = rules.count</tag>
		</item>
		<item repeat="0-1">
			<ruleref uri="#size" />
			<tag>out.order.side.size = rules.size</tag>
		</item>
		<one-of>
			<item>
				<one-of>
					<item>fries</item>
					<item>some fries</item>
					<item>bag of fries</item>
				</one-of>
				<tag>out.order.side.type="fries"</tag>
			</item>
			<item>sallad<tag>out.order.side.type="sallad"</tag></item>
		</one-of>
	</rule>

	<rule id="flavor_answer" scope="public">
		<ruleref uri="#flavor"/>
		<tag>out.flavor = rules.flavor</tag>
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
	
	<rule id="yes" scope="public">
		<one-of>
			<item>yes</item>
			<item>yes I do</item>
			<item>sure</item>
			<item>yeah</item>
			<item>of course</item>
			<item>okay</item>
		</one-of>
		<tag>out.yes=1</tag>
	</rule>

	<rule id="no" scope="public">
		<one-of>
			<item>no</item>
			<item>no way</item>
			<item>nope</item>
			<item>not really</item>
			<item>I don't think so</item>
		</one-of>
		<tag>out.no=1</tag>
	</rule>

</grammar>
```

This grammar does not contain any root rule. Instead, we mark all rules that will match a fragment and return a semantic structure as "public". This includes fragments such as "a cheeseburger" and "a large coke". Thus, if the user says something like "I'm gonna have a cheeseburger and then I want a large coke", the parser will match these phrases, and then combine the semantics. The parser tries to match all rules that are marked as public, to cover as many words as possible with as high-level rules as possible. 

We will now try our semantic grammar out in the TestRecognizer tool. Either you can copy and paste the BurgerGrammar.xml into the Semantic Grammar window, or you can drag-and-drop the file from Eclipse. If you choose the latter, you can then save any changes you make directly from the tool. In order to use the grammar, we must load it into the recognizer. Press the Load button to do so. It is important to remember that you have to do this every time you change the grammar for the changes to take effect. If everything works fine, you will see a positive message in the Output window on the upper right, otherwise you will get an error message (for example if the grammar is ill-formed).

Once the grammar is loaded, you can press Listen and say something to see the result, which will now include the semantic interpretation. Try to say for example "I would like a cheeseburger and a large coke". The semantic output should look like this: 

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

Examine the grammar and try to understand how it works. Try with different utterances, such as:   

* "I would like to order a strawberry milkshake please"
* "I want to have two large hamburgers and a sallad"
* "a small hamburger"
* "banana"
* "yes"

You can also try to write a text string in the bottom of the Semantic Grammar window and press Parse. Then you will also see how the individual phrases match.
    
The semantic grammar also allows for some more tricks that are not supported by the speech grammar. You can use the "_" symbol in words to match zero to many characters. Thus "\burger\_"  matches both "burger" and "burgers". In fact you can use any [regular expression](https://en.wikipedia.org/wiki/Regular_expression), but "\_" is interpreted as ".*".  

Note that it is also possible to combine speech grammars with semantic grammars in recognizers that use speech grammars, such as the Windows recognizer (using both SpeechGrammarContext and SemanticGrammarContext). In this case, the speech grammar will define what can be said (but the \<tag\> elements will be ignored), and the resulting text string will be parsed and interpreted with the semantic grammar.

To use the recognizer in your system, you must replace the recognizer setup in the BurgerSystem class with this:

```java
// Here we use Google, but you can instead use the NuanceCloudRecognizerFactory if you want
system.setupRecognizer(new GoogleRecognizerFactory());
system.loadContext("default", new OpenVocabularyContext(system.getLanguage()));
system.loadContext("default", new SemanticGrammarContext(new SRGSGrammar(system.getPackageFile("BurgerGrammar.xml"))));
```

As you can see, we associate both an open vocabulary grammar and a semantic grammar to the context "default".

### Understanding the grammar

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

If you want to understand how the grammar works in more depth, you can read the official [W3C SRGS specification](http://www.w3.org/TR/speech-grammar/), as well as the [W3C SISR specification](https://www.w3.org/TR/semantic-interpretation/) (for how to use the semantic tags).  

**NB**: If you want to read more about how speech recognition is done in IrisTK, please refer to the [Speech recognition reference](speech_recognition.html).

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

### Situated interaction: Handling multiple customers

TBA
