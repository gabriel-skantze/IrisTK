## IrisFlow Reference

### General words on XML and Java

Since the flow is written in XML which is then converted to Java, there are some things to look out for, but also some convenient tricks that the flow compiler allows for.

* XML does not allow less-than-brackets (<) in expressions. Either reverse the expression (> is allowed), or use the XML entity &amp;lt; instead. Although ">" is allowed, it is also possible to use &amp;gt; instead. 
* Since & is reserved for XML entities, you cannot directly write && for Java "and". You can write &amp;amp;&amp;amp;, but it doesn't look very pretty. Therefore the flow compiler allows you to use the words "and" and "or" which will be transformed to && and ||.
* Within XML attributes (such as cond), you cannot use double quotes (") for strings, use single quotes (') instead.
* You cannot compare for example strings in Java with ==, you should use the equal() method instead. However, this may result in a NullPointerException if the object is null. There is a convenient way of testing equality that avoids this - you can use the static method eq(A,B) instead, which allows for null values.

###	Defining the flow: \<flow\>

The \<flow\> tag is the top-level element for the flow. It can have the following attributes:

-----   ---------
name	The name of the flow. It should match the name of the file (without the .xml extension).
package	The Java package where the flow is located. Must match the folder structure.
initial	The initial state the flow should start in
-----   ---------

### Importing external classes

Directly, after the \<flow\> top element, you can import external classes (translated to "import" in Java):

```xml
<!-- Importing a class -->
<import class="java.util.List"/>
<!-- Importing all classes in a package -->
<import class="java.util.*"/>
<!-- Importing static methods -->
<import class="static java.lang.Math.*"/>
```

###	Defining variables: \<var\>

Variables can be declared on three levels: flow, state or as an action to assign a value.

The \<var\> tag contains the following attributes:

-----   ---------
name	The name of the variable
type	The type of the variable (a Java class or primitive Java type, String if omitted)
value	An initial value for the variable (null if omitted)
-----   ---------

Flow variables can be accessed from outside the flow (in Java): 

```java
flow.setChess(chess);
flow.getChess();
```

###	Defining parameters : \<param\>

Parameters can be either flow-level or state-level. Parameters are also variables, but they are assigned when instantiating the flow or state. 

The \<param\> tag contains the same attributes as the \<var\> tag (see above). The "value" attribute then defines the default value (which can be omitted).

How state-level parameters are used when calling or going to a state is described further down.

Flow-level parameters can be used to give the flow access to Java objects defined outside the flow, such as external flows or for querying or interacting with a back-end system. Here is an example from the Chess application:

```xml
<flow name="ChessFlow" ...>

	<param name="chess" type="ChessGame"/>
```

This way, the flow can access the "chess" object in for example \<exec\>,  \<expr\> and "cond" attributes. 

When declaring flow-level parameters, they are required in the flow constructor on the Java-side:

```java
ChessGame chessGame = new ChessGame(system.getGUI());
flowModule = new FlowModule(new ChessFlow(chessGame));
```

### Defining a state: \<state\>

The \<state\> tag may contain the following attributes:

----       --------------
id         A unique name for the state
extends    An extension of another state (see below)
static     Whether the state is static (Boolean, default false). Static states cannot access any flow variables or refer to non-static states.
public     Whether the state is public (Boolean, default false). Public states can be accessed from external flows.
----       ---------------

By extending a state using extends, it is possible to define generic event handlers in one parent state and then reuse them in states that extend it. Thus each time an event is processed by the flow, first all event handlers of the leaf state are checked, then the event handlers of the parent state, and then the event handlers of that parent's state, and so on. 

For the use of "static" and "public", please refer to [Advanced Topics](irisflow_advanced.html). 

States can also define parameters using \<param\>, as well as defining variables using \<var\>:

```xml
<state id="Listening">
	<param name="timeout" type="int" default="8000"/>
	<var name="speechStarted" type="boolean" value="false"/>
	<onentry>
		<send event="action.listen" p:timeout="timeout"/>
	</onentry>
	<onevent name="sense.speech.start">
		<exec>speechStarted = true</exec>
	</onevent>
	...
</state>	
```

### Event handler: \<onevent\>

The \<onevent\> event handler is used to catch local or global events. It can take two attributes:

-----   -------------
name	A pattern for matching the event. Only characters, dots and stars are allowed in the pattern. The pattern can be an exact match or use wildcards:
		* dot (.) : matches a dot literally
		* star (\*) : matches 0 or more characters, excluding dots
		* double star (\*\*): matches 0 or more characters, including dots 
cond	A condition that must also be satisfied for the event handler to trigger. Note that Java expressions should not contain double quotes (since they will conflict with the attributes quotes). Use single quotes instead, and they will be replaced in the compilation process.
-----   ---------------

Two special variables are created that you can access in both the "cond" attribute and the event handler actions:

-----   -------------
event	The event object, which is of type Record (see further down and the Javadoc). This is useful if you want to check the event parameters. 
count	The number of time this event handler has triggered while being in this state. It is set to 1 the first time the event handler matches (or is about to match), and then reset if the flow transitions with a \<goto\>.  
-----   -------------

### Event handler: \<onentry\>

Actions in the \<onentry\> event handler are executed when the state is entered, either through \<call\> or \<goto\>, or when the \<reentry\> action is issued.  

You can also access the "count" variable, as described above.

### Event handler: \<onexit\>

Actions in the \<onexit\> event handler are executed when there is a transition to another state (either through \<call\> or \<goto\>). **Note**: The \<onexit\> event handler should not contain any instructions that changes the flow execution or that will take up time, such as \<call\>, \<goto\> or \<wait\>.

### Event handler: \<ontime\>

The \<ontime\> event handler triggers actions after some time has passed. It can take one of two attributes:

-----       -------------
interval	Triggers several times at a certain time interval (in milliseconds). The time interval can either be a random interval, for example "1000-3000" (the time changes after each trigger), or a fixed interval, such as "2000". 
afterentry  Triggers after some time has passed since the state was entered (unless the state is exited before that). Expressed in milliseconds, for example "2500".  
-----       -------------

You can also access the "count" variable, as described above.

### Action: \<goto\>

Issues an immediate transition to another state. 
Thus, there is no point in having more actions afterwards in the same event handler, which means that \<goto\> should always be placed last. 
The name of the target state is given with the state attribute. You can also provide any number of custom parameters using the p: namespace prefix (iristk.flow.param), however these must match the parameters that the target state accepts.

```xml
<state id="Start">
	<onentry>
		<goto state="SayHello" p:name="'Peter'"/>
	</onentry>
</state>

<state id="SayHello">
	<param name="name" type="String"/>
	<onentry>
		<send event="action.speech" p:text="'Hello ' + name"/>
	</onentry>
</state>
```

This example will make the system say "Hello Peter". 

### Action: \<call\>, \<return\>

Issues a call to another state (similar to calling a function in any programming language) as part of an execution and then continue the execution when the called state issues a \<return\>. 

```xml
<state id="Start">
	<onentry>
		<call state="SayHello" p:name="'Peter'"/>
		<call state="SayHello" p:name="'Susan'"/>
	</onentry>
</state>

<state id="SayHello">
	<param name="name" type="String"/>
	<onentry>
		<send event="action.speech" p:text="'Hello ' + name"/>
	</onentry>
	<onevent name="monitor.speech.end">
		<return/>
	</onevent>
</state>
```

When a state is called, the calling state will pause. In the example above, the SayHello state will send an "action.speech" event to the speech synthesizer. When the speech synthesis is done, the state will catch the "monitor.speech.end" event and issue a \<return\> to the calling state (Start), which will then continue with the next action (another call to SayHello). Thus, the system will first say "Hello Peter" and then "Hello Susan". 

It is also possible to raise an event when the flow returns. This is similar to how functions in most programming languages can return a value:

```xml
// Return a simple event which will trigger event handlers in the calling state
<return event="speech.done"/>
// Set event parameters
<return event="speech.done" p:text="..."/>
// Copy event parameters (see <raise> and <send> below)
<return event="speech.done" copy="event"/>
```

### Action: \<send\>, \<raise\>

The \<send\> action will send a global event to the IrisSystem. The \<raise\> action will raise a local event in the flow. The name of the event is given with the event attribute. Just like with \<goto\>, parameters are specified using the p: namespace prefix. Examples:

```xml
<send event="action.speech" p:text="'hello'"/>
<raise event="chess.move.user"/>
```

Note: the parameter hello is enclosed in single quotes. If it were not, it would be treated as a variable. Note also that when \<raise\> is used, the event will not be placed on the event queue. Instead, relevant event handlers will be triggered **immediately**, before any more actions are taken. 

In both \<send\> and \<raise\>, you can also copy all parameters of another event:

```xml
<onevent name="sense.speech.rec.final">
	<raise event="my.speech" copy="event" p:text="'hello'"/>
</onevent>
```

This will copy all parameters of the "event" object and add (or overwrite) the parameter "text".

In both \<send\> and \<raise\>, it is also possible to delay the dispatch of the event with the following attributes:

-----         -------------
delay         The number of milliseconds that should pass before the event is dispatched. 
forgetOnExit  Can be set to true (default is false), which means that the event will not be dispatched if the state is exited before the delay time has passed.
-----         -------------

### Action: \<reentry\>

The \<reentry\> tag will make a reentry into the current state, triggering the \<onentry\> event handler again.

###  Action: \<if\>, \<else\>, \<elseif\>

Follow the following pattern to create an if-then-else construction:

```xml
<if cond="test == 1">
	<goto state="state1"/>
<elseif cond="test == 2"/>
	<goto state="state2"/>
<elseif cond="test == 3"/>
	<goto state="state3"/>
<else/>
	<goto state="state4"/>
</if>
```

### Action: \<repeat\>

The \<repeat\> tag will repeat a block of actions.

To repeat a specified number of times:
 
```xml
<repeat times="10">
	<log>Test</log>
</repeat>
```

It is also possible to add a handler object which keeps track of information about the iteration:

```xml
<repeat times="10" handler="loop">
	<log>Test <expr>loop.getPosition()</expr></log>
</repeat>
```

To repeat while a certain condition (boolean expression) holds:

```xml
<repeat while="i &lt; 5">
	<log>Test</log>
</repeat>
```

To repeat through a list:

```xml
<repeat list="myList" handler="loop">
	<log>Test <expr>loop.getItem()</expr></log>
</repeat>
```

The following methods are available in the handler object:

-----			-------------
getPosition()	Returns the current position (starting from 0). 
getLength()		Returns the length of the iteration. Returns -1 if a while-iteration is being used.
getItem()		Returns the current item, if a list-iteration is being used.
isFirst()		Returns true if at the first position.
isLast()		Returns true if at the last position.
-----			-------------

### Action: \<random\>, \<block\>, \<select\>

The \<random\> tag will make a random choice between one of the children and perform that action. In this example, the system will randomly go to either state1, state2 or state3. 

```xml
<random>
	<goto state="state1"/>
	<goto state="state2"/>
	<goto state="state3"/>
</random>
```

How the random choice is made can also be controlled by providing an attribute called "model" to the \<random\> tag. The attribute can have the following values:

-----								-------------
deck								The options are treated as a deck of cards that is randomized the first time. Then the order of the cards is preserved across iterations. 
deck_reshuffle						Same as "deck", but the deck is reshuffled after each iteration (i.e. when the deck is empty).   
deck_reshuffle_norepeat (default)	Same as "deck_reshuffle", but it is also guaranteed that there will be no repetitions when the deck is reshuffled.
dice								Completely random choice with no memory.
dice_norepeat						Same as "dice", but it is also guaranteed that there will be no repetitions.
nonrandom							Like a "deck" but with no randomization (the options are taken in the order they are provided. 
-----								-------------

To make a random choice between sequences of actions, or to manipulate the random choice, use \<block\> tags directly under \<random\> to group them. In the following example, the system will either go to state1 or first call state2 and then go to state3:

```xml
<random>
	<block>
		<goto state="state1"/>
	</block>
	<block>
		<call state="state2"/>
		<goto state="state3"/>
	</block>
</random>
```

The \<block\> tag can have two optional attributes:

------  -----------------------
weight  An integer that puts a weight on the block for the random choice. For example, if one block has weight 1 and another weight 2, the second block will be chosen two times out of three. 
cond    A guard condition (see \<catch\> above) that must evaluate to true for the block to be a candidate for selection.
------  -----------------------

If a non-random choice is to be made (The nonrandom model), the \<select\> tag can be used instead. This is very similar to a if-then-else construction:

```xml
<select>
	<block cond="a == 1">
		<log>a was equal to 1</log>
	</block>
	<block cond="a == 2">
		<log>a was equal to 2</log>
	</block>
	<block>
		<log>a was equal to something else</log>
	</block>
</select>
```

### Action: \<exec\>

```xml
<exec>System.exit(0)</exec>
```

The \<exec\> tag can contain any Java code that is to be executed. It does not have to end with ";", but you must use ";" to separate multiple instructions.
 
###	Action: \<propagate\>

The \<propagate\> tag will instruct the FlowModule to continue to match the event against other event handlers (i.e. it is not consumed).

### Action: \<wait\>

Pauses the flow execution. During the pause, events are still checked for. Thus, if an event causes a state transition, the pause will be aborted.

```xml
<!-- Pause 2000 milliseconds -->
<wait msec="2000"/>
```

### Action: \<log\>

For development purposes, it can be useful to log things: 

```xml
<log>Log this line</log>
```

Note that if you want to log an expression, you have to use the \<expr\> tag (see further down):

```xml
<log>The time is now <expr>System.currentTimeMillis()</expr></log>
```

###	Parameters

As seen above, parameters (in \<call\>, \<goto\>, \<send\>, etc) are provided with the p: namespace prefix (which should be mapped to iristk.flow.param). It is important to note that if the value is not quoted (again, with single quotes), it is treated as a Java expression:

```java
p:param="text"   // The variable text
p:param="'text'" // The string "text"
p:param="true"   // The boolean value true
p:param="'true'" // The string "true"
p:param="0"      // The integer value 0
p:param="'0'"    // The string "0"
```

For convenience, there is a special parameter named "text" of the type String that can be provided as a XML text child (or as an arbitrary XML structure). Thus, the following two expressions are equal:

```xml
<call state="Speaking">Hello there</call>
<call state="Speaking" p:text="'Hello there'"/>
```

### String expressions: \<expr\>, \<random\>, \<if\>, \<repeat\>

For the XML text child, it is also possible to insert expressions in the string using the \<expr\> tag. These two expressions are equal and will result in "One plus one is 2":
 
```xml
<call state="Speaking">One plus one is <expr>1 + 1</expr></call>
<call state="Speaking" p:text="'One plus one is ' + (1 + 1)"/>
```

It is also possible to use the \<random\>, \<if\>, \<select\> and \<repeat\> tags in this context:

```xml
<call state="Speaking">
	That was <if cond="score > 100">very</if> good
</call>

<call state="Speaking">
	You have <expr>n</expr> 
	<if cond="n==1">point<else/>points</if>
</call>

<call state="Speaking">
	<random>
		<block>Hi</block>
		<block>Hello</block>
	</random>
	there
</call>
```

Here is an example of how a list can be enumerated in natural language ("apples, oranges and pears") using a mix of \<repeat\> and \<if\> constructions:

```xml
<log>
	<repeat list="myList" handler="enumeration">
		<if cond="!enumeration.isFirst()">
			<if cond="enumeration.isLast()"> and <else/> , </if>
		</if>
		<expr>enumeration.getItem()</expr>
	</repeat>
</log>
```

### Events and Records

When an event is caught in an \<onevent\> handler, a special object "event" is provided and can be checked in for example the "cond" expression. This object is of type iristk.system.Event, which in turn extends the iristk.util.Record class. This is essentially a map of key-value pairs (much like any java Map). However, it supports a convenient way of accessing nestled values deep in the hierarchy by using colon (:) notation:

```java
event:text           // Access the field "text"
event:sem:direction  // Access the nestled field "sem:direction"
event:sem:(field)    // Evaluates the expression "field" and uses it for accessing
```

If the event/record does not contain the requested field, null will be returned. Otherwise, a value of type Object will be returned. If you need to treat the value as some specific type (e.g. to do a numeric comparison using >), you need to cast it, otherwise the Java compiler will object. The flow compiler allows for a set of static methods for easily casting values and even converting them if necessary (for example the string "2" to the integer 2):

```java
asString(event:text)
asBoolean(event:check)   
asFloat(event:length)
asInteger(event:age)
asRecord(event:sem)
```

If the value is null, these methods will also return null (except for asBoolean, see below), otherwise they will do their best to return a value of the right type (converting strings to numbers, etc). 

asBoolean can be very useful in cond-expressions. It will return the following depending on the parameter:

* Boolean: same
* List: false if empty, true otherwise
* Number: false if 0, true otherwise
* String: false if "false" (case insensitive), true otherwise
* Other object: false if null, true otherwise

You can also provide a default value as a second argument in case the value is null (i.e. no such field exists):

```java
asString(event:sem:direction, "none")
```

You can also check whether a field is present or not, which can be very useful in cond-expressions:

```java
event?:sem:direction  // Check whether the nestled field "sem:direction" is present
event?:text           // Check whether the field "text" is present
```

In \<exec\> statements, you can also manipulate events/records with these operators:

```java
// Puts the string "left" in the field "direction" of the record nestled under "sem", 
// creating this nestled record if necessary
newEvent:sem:direction = "left"    
// Copies all fields of "event" to "newEvent"
newEvent := event                  
```

On the Java side, records can be created and manipulated like this:

```java
// Create a record
Record person = new Record()
// Store the Integer 30 in the field "age" of the record "person"
person.put("age", 30)
// Store the value 60 in field "age" of the sub-record that is located 
// in the field "father" of the record "person". If the field "father" does 
// not already contain a Record, it will be created.
person.put("father:age", 60)
// Access the value of the field "age" (as an Object)
person.get("age")
// Access the value of the field "age" as an Integer (null if there is no such value)
person.getInteger("age")
// Access the value of the field "age" as an Integer with default value
person.getInteger("age", 0)
// Access a nestled value
person.getInteger("father:age", 0)
// Check if a value is present
person.has("age");
// Kleen stars (*) are allowed in the has() method.
person.has("*:age")
```
