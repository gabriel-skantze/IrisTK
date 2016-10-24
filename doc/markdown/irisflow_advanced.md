## IrisFlow: Advanced topics

### Re-usable Flows

You have probably already seen how states from one flow (such as "say" and "listen" in  the re-usable flow iristk.flow.DialogFlow) can be called from another flow. By placing these states in separate flows, they do not have to be re-implemented. This way, you can create a library of useful flows and states. 

As an example of re-usable flows, we can start with iristk/flow/DialogFlow.xml. In order to make the "listen" state accessible from another flow, it has to be made public:

```xml
<state id="listen" public="true" static="true">
```

In this case, we have also declared the state "static", which means that the state can be called without a reference to a flow instance. This corresponds to the notion of static methods and classes in Java. In order to call this state from another flow, we can simple make a reference to it like this: 

```xml
<call state="iristk.flow.DialogFlow.listen"/>
```

### Custom tags

As you probably have seen, there is also a more convenient way of calling states, using custom tags:

```xml
<dialog:listen/>
```

For this to work, we have to associate the "dialog" prefix to the iristk.flow.DialogFlow class, using XML namespaces:

```xml
<flow name="ChessFlow" 
      xmlns:dialog="iristk.flow.DialogFlow" ...
```

When compiling a flow with public states, the compiler will also produce an XML Schema (such as DialogFlow.xsd). This is useful for validating custom tags in the calling flow (their names and parameters). To link this Schema, place it in the same folder as your flow, and link it in the root element (in addition to flow.xsd):

```xml
<flow name="ChessFlow" 
      xmlns:dialog="iristk.flow.DialogFlow" 
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	  xsi:schemaLocation="iristk.flow flow.xsd iristk.flow.DialogFlow DialogFlow.xsd"
	  ...
```

### Flow instances

The examples above work fine when the states are declared static, and no flow instance is needed. However if the states we are calling need access to flow-level variables, they cannot be declared static. You may have seen an example of this in iristk/situated/SituatedDialogFlow.xml. If you examine it, you will see that a reference to a SystemAgent object is taken as a parameter:

```xml
<flow name="SituatedDialogFlow" package="iristk.situated"  
	initial="Idle" xmlns="iristk.flow" xmlns:p="iristk.flow.param"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="iristk.flow flow.xsd">
	
	<param name="system" type="SystemAgent"/>
```

If you examine the "listen" state in the same flow, you will see that this flow-level "system" parameter is referenced, which means this state cannot be declared static. Thus, in order to call this state from another flow, we need a reference to a SituatedDialogFlow object in the calling flow. This can either be a variable (\<var\>) or a parameter (\<param\>). If we look at the Quiz example app, we can see that it is passed as a parameter:

```xml
<flow name="QuizFlow" package="iristk.app.quiz" 
	initial="Idle" xmlns="iristk.flow"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:p="iristk.flow.param"
	xmlns:dialog="iristk.situated.SituatedDialogFlow"
	xsi:schemaLocation="iristk.flow flow.xsd iristk.situated.SituatedDialogFlow SituatedDialogFlow.xsd">
	
	<param name="questions" type="QuestionSet"/>
	<param name="dialog" type="iristk.situated.SituatedDialogFlow"/>
```

Now, it is very important that the name of the parameter ("dialog") matches the namespace prefix (xmlns:dialog). Otherwise, the compiler will not understand which object reference to use, when the custom tag is used (&lt;dialog:listen/&gt;), and it will try to make a static reference (which will fail).  

If you want to call a non-static state without using a custom tag, you have to use this special notation:

```xml
<call state="dialog#listen"/>
```

If you want to use custom tags to call states in the same flow instance, you can use the special namespace prefix "this":

```xml
<flow name="ChessFlow" 
      package="iristk.app.chess"
      xmlns:this="iristk.app.chess.ChessFlow" ...>
```


