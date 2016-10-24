## Facial gestures

Both the animated agent that comes with IrisTK (the Face module) and the [Furhat robot head](furhat.html) support a set of extendable gestures. These can be performed with the [action.gesture event](events.html).

The gesture library is stored in a file called gestures.xml, which can be found under addon/Face. On Furhat, you can access and update it from the web interface. The XML schema for the file (gestures.xsd) can be found in the same folder. 

IrisTK comes with a tool called "Gesture Builder" for experimenting with the gestures. You can start the tool with:

```
iristk gestures
```

This is what it should look like:

![](img/gestures.jpg)

On the left side, there is a list of all parameters that can be controlled. By dragging the sliders you can see the effect they have on the face. On the right, you can see the gestures.xml file, which you can edit. If you want to keep your changes, you can press Control-S to save the file. Watch out for any error messages in the console from which you started the tool.  

Each gesture looks something like this:

```xml
<gesture name="brow_raise">
	<param name="BROW_UP_LEFT">16(1) 16(1) 16(0)</param>		
	<param name="BROW_UP_RIGHT">16(1) 16(1) 16(0)</param>
</gesture>
```

If you place the cursor on one of these gestures and press Control-P, the gesture will be performed so that you can see what it looks like. 

As you can see, each gesture has a name and a set of parameters that are controlled concurrently. Each parameter also has a name (that should match one of the parameters on the left) and a trajectory. The trajectory contains a list of space-separated target positions (in parenthesis), and the time it should take to reach that target as a prefix. Thus, "16(1)" means that the face should reach target position 1 in 16 frames, where 1 frame is 1/50 of a second. The parameter always start in its present position. Thus, if the parameter is already at position 1, nothing will happen. The composite trajectory "16(1) 16(1) 16(0)" means that the parameter should reach position 1 during 16 frames, then stay there for 16 frames, and then go to 0 during 16 frames.  


