## Distributed systems

It is possible to distribute the system over different processes and/or computers, using serialized communication over TCP/IP. This also makes it possible to write modules in other programming languages than Java.

First, you need to start a Broker that relays all messages. Thus, all systems only need to know the address of the Broker and inform the Broker of which events they subscribe to. The Broker can be started from the command like like this:

```
iristk broker [port]
```

The port is optional and is assigned to 1932 in the default case. Then, it is possible to connect the IrisSystem to the broker like this:

```java
irisSystem.connectToBroker("myticket", "127.0.0.1", 1932);
```

All systems (running in different processes or on different machines) that connect to the broker like this and share the same ticket ("myticket" in this case) will automatically share events.
 
By default, all modules subscribe to all events. This is typically not a problem when everything runs in the same process and only pointers are passed around. However, when using a broker, serialization and network messaging might be inefficient if too many events are passed around. If you want to restrict which events are relayed by the broker, you have to make sure that the modules only subscribe to messages they are interested in by using the subscribe() method in the IrisModule, like this (check the javadoc for iristk.util.NameFilter for how to write filters):

```java
subscribe("action.listen**");
```

### Connecting from another programming language

If you want to communicate with the IrisBroker from another programming language, you have to follow the broker protocol.
 
1.	Connect to the broker using a socket on the right port. Create two threads, one for writing to the socket and one for reading from it.
2.	Write "CONNECT [ticket]\\n" on the socket, where [ticket] is the name of the ticket you want to share events with.
3.	Read on the socket and wait for "CONNECTED\\n". 
4.	Write "SUBSCRIBE [filter]\\n" to tell the broker which events you are interested in (e.g., "\*\*" for all events).
5.	Accept messages from the broker:
	* "SUBSCRIBE [filter]\\n", which informs you on what events the other clients connected to the broker are interested in. This might be updated over time. Initially, you could of course ignore this and send everything to the broker that you know is important.
	* "EVENT \[name\] \[nBytes\]\\n" followed by a serialized event in JSON format. The [nBytes] is a number which tells you how many bytes the JSON event takes. Thus, you have to continue to read these bytes and then parse the JSON.
	* "CLOSE\\n" when the broker wants to shut you off
6.	Send events to the broker by writing to the socket: "EVENT \[name\] \[nBytes\]\\n", followed by the serialized JSON event, where [nBytes] denotes the number of bytes in the JSON event.
7.	Shut down the connection nicely by writing "CLOSE\\n" when you want to terminate your client.

### Event JSON format

The events are serialized into JSON like this:

```json
{
  "class" : "iristk.system.Event",
  "event_name" : "action.speech",
  "text" : "Hello there"
}

{
  "class" : "iristk.system.Event",
  "event_name" : "action.listen",
  "timeout" : 4000
}
``` 


