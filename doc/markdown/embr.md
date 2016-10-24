## EMBR Animated agent

This add-on adds support the for the [EMBR animated agent system](http://embots.dfki.de/EMBR/) developed at DFKI. The system must be installed and run before running your IrisTK application. The IrisTK EMBR module then communicates with the animation engine through a socket.

The full body animation is not used yet, so you can zoom in on the agent's head by dragging with the left (move) and right (zoom) mouse button. 
Make it look something like this:

![](img/embr.png)

### Usage

To use EMBR in IrisTK, add the EMBRModule to the system:

```java
system.addModule(new EMBRModule());
```

You must also enable lipsync for the synthesizer module:

```java
SynthesizerModule synth = new SynthesizerModule(
                              new WindowsSynthesizer(Gender.FEMALE));
synth.doLipsync(true);
system.addModule(synth);
```

To control the gaze and gestures of the animated agent with events, see the [Events](events.html) section. 

The following gestures are supported:

* brow_raise
* smile
* smile_open
* anger
* disgust
* fear
* sad
* surprise
* nod_head
* shake_head







