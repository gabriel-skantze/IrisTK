##	Develop in Eclipse

The examples in this guide will assume that you are developing in Eclipse. Of course, it is not necessary to use Eclipse, but if not, you will have to adapt the instructions accordingly on your own.

You can [download Eclipse from here](http://www.eclipse.org/downloads/). If you do not know which version to choose, we recommend "Eclipse IDE for Java Developers" (32 or 64 bit), which includes support for XML editing.

### Setting up the classpath

Open a command window and type:

```
iristk eclipse
```

This will create a .classpath file in the IrisTK folder which makes it possible to import it as a project in Eclipse. This should be done each time you add a new addon or app to the IrisTK folder, if you don't want to configure these things manually in Eclipse.

### Import into Eclipse

1. Open Eclipse and choose "File > Import..."
2. Choose "Existing Projects into Workspace" select the IrisTK folder as root directory.
3. If you are using 64 bit Windows and want to use 32-bit addon (such as Nuance9), open "Window > Preferences", then "Java > Installed JRE". Make sure that you are using 32 bit Java. Otherwise, you must add that and select it as default.

### Eclipse Plug-in

There is an Eclipse Plug-in which lets you easily compile the dialogue flow. Here is how you install it:

1. Choose "Help > Install New Software..."
2. In "Work with", type "http://www.iristk.net/eclipse" and hit return.
3. Expand the group that comes up in the list and select "IrisTK Eclipse Plug-in". You may have to uncheck the "Group items by category" checkbox in order to see it. We also advide you to uncheck "Contact all update sites...", to speed up the installation.
4. Choose "Next" and follow the instructions (Eclipse needs to be restarted). 

Now you can compile a flow easily by right-clicking the flow XML file (either in the editor or in the Package Explorer) and choose "Compile Flow" in the context menu. The result of the flow compilation will be reported in the Console. Eclipse will then automatically compile the generated Java file to a class file. If there are any errors in this process, you will see that the Java file gets a red mark in the Package Explorer .