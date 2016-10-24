## IrisFlow overview

IrisTK provides a statechart-based framework for defining the flow of the interaction called IrisFlow. Statecharts are similar to finite state machines (FSM), but they are more flexible:

* States can be hierarchically structured; allowing the designer to define generic event handlers (i.e., transitions) on one level and more specific event handlers in the sub-states. In IrisFlow, this is accomplished by allowing states to extend other states (like a class extends another class in Java) and thereby adopt its parent's event handlers.
* It is possible to define flow variables which affect the execution. 
* Transitions may not only be triggered by events, but they may also have guard conditions that checks the event parameters as well as the state of the flow variables. 
* Apart from ordinary state transitions IrisFlow also allows states to be called, blocking the current execution, after which the called state might return, and the execution continues.

The flow is defined using XML and then compiled to Java source code. For instructions on how to compile the flow, refer to [this tutorial](creating_an_app.html).

Here is an abstract view of the general structure of the flow XML:

```xml
<flow name="FLOW_NAME" package="FLOW_PACKAGE" initial="INITIAL_STATE_ID">
	
	<!-- Zero or more flow-level parameters -->
	<param name="VAR_NAME" type="VAR_TYPE"/>
	
	<!-- Zero or more flow-level variables -->
	<var name="VAR_NAME" type="VAR_TYPE"/>
	
	<!-- One or more states -->
	<state id="STATE_ID">
		<!-- Zero or more state-level parameters -->
		<param name="PARAM_NAME" type="PARAM_TYPE"/>
		<!-- Zero or more state-level variables -->
		<var name="VAR_NAME" type="VAR_TYPE"/>
		<!-- Zero or one onentry event handler -->
		<onentry>
			<!-- actions to execute when the state is entered -->
		</onentry>
		<!-- Zero or more onevent event handler -->
		<onevent name="EVENT_NAME" cond="CONDITIONS">
			<!-- actions to execute when the event is received -->
		</onevent>
		<!-- Zero or one onenxit event handler -->
		<onexit>
			<!-- actions to execute when the state is exited -->
		</onexit>
	</state>

</flow>
```

As can be seen, the flow basically contains a number of global variables and states. The flow is always in one particular state, but can change state through either \<call\> or \<goto\>, as described further down. Each state can be thought of as a collection of event handlers, which in turn contain actions. As can be seen, it is also possible to add parameters \<param\> to the state, as well as local variables \<var\>. Note that unlike flow-level variables, which are created when the flow is initialized and live through the whole flow execution, state variables are forgotten once the state is left and re-created if the state is reached again.

When running the flow, the flow module starts by checking its event queue and then goes through the event handlers for the current state in order to see if any of them matches. If so, the actions for that event handler are taken (which could include transitions to other states or the raising of new events). Once all actions are taken, the event queue is checked again for new events (so called run-to-completion). Once an event handler consumes an event, it will not be checked against event handlers further down the list (unless a \<propagate\> action is issued by the consuming event handler). If no event handler consumes the event, the flow module checks the next event in the event queue. If there are no more events, the FlowModule blocks and waits for events to arrive. 

The events in the FlowModule can either be local or global events. All events that are relayed to the FlowModule by IrisSystem (i.e., all events that are produced by all modules in the default case) end up as global events in the FlowModule event queue and can thus be reacted upon. The flow itself may also raise both global events (using the \<send\> action) and local events (using the \<raise\> action). Global event are sent to the IrisSystem and relayed to other modules, whereas local events are only put on the flow's own event queue.

### State transitions

There are two types of state transitions: \<call\> and \<goto\>. When a \<goto\> is issued, the current execution is aborted. However, it is also possible to just \<call\> another state (similar to calling a function in any programming language) as part of an execution and then continue the execution when the called state issues a \<return\>. The following example shows how to call a state twice, making the system say two utterances after each other. The Speaking state issues a command to the speech synthesizer to speak and then waits for the end-of-speech before returning. As can be seen, by using parameters, this creates a powerful means for creating reusable states and issuing complex actions with short statements. 

```xml
<state id="Test">
	<onentry>
		<call state="Speaking" p:text="'This is the first part'"/>
		<call state="Speaking" p:text="'And this is the second part'"/>
	</onentry>
	<onevent name="sense.leave">
		<goto state="Idle"/>
	</onevent>
<state>

<state id="Speaking">
	<param name="text" type="String"/>
	<onentry>
		<send event="action.speech" p:text="text"/>
	</onentry>
	<onevent name="monitor.speech.end">
		<return/>
	</onevent>
	<onexit>
		<send event="action.speech.stop"/>
	</onexit>
</state>
```

One important aspect of calling states is that the event handlers of the calling state are still active when in the called state. Thus, when in the Speaking state in the example above, the event handlers of that state are first checked when events arrive, but if none of these trigger, the event handlers in the Test state will also be checked. Thus, if a sense.leave event is received while the system is speaking (i.e., while the system is still in the Speaking state), this will immediately result in a transition to the Idle state. Note also that there is an <onExit> handler in the Speaking state that will be triggered in that case, causing the system to stop speaking.

To help you understand the workings of \<call\> and \<goto\> when combined with extended states, we will give you a schematic example. The picture below shows a visual representation of a statechart. Boxes represent states. The numbers assigned to the arrows indicate a possible sequence of transitions. The boxing of the states illustrates the hierarchical structure. Thus, State B and C are extensions to state A - if an event is received when in state B, first all event handlers in state B are checked, then the event handlers in state A. As can be seen, transition (2) and (3) are of the type call and (4) and (5) are returns to the calling states. When state E is called a second time (7), an event triggers an event handler in the super-state D, which issues a goto (8) to state B. Unlike the case for call, the goto transition automatically clears the call stack, which means that there will be no return to the calling state H in this case. 

As described above, when in a called state, events are not only checked against the event handlers of the current state and its super-states, but also the event handlers of the calling states. Thus, when an event is processed when in state F, the event handlers of the following states will be checked (in order): F, E, D, C, A.

![](img/transitions.png)


