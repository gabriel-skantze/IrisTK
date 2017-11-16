##	Develop in Eclipse

The examples in this guide will assume that you are developing in Eclipse. Of course, it is not necessary to use Eclipse, but if not, you will have to adapt the instructions accordingly on your own.

You can [download Eclipse from here](http://www.eclipse.org/downloads/). If you do not know which version to choose, we recommend "Eclipse IDE for Java Developers" (32 or 64 bit), which includes support for XML editing.

### Downloading the IrisTK repository from Eclipse

If you have already cloned/downloaded and installed IrisTK, you can skip this section. (Go directly to "Setting up the classpath" below). 

First, make the git repository view is visible in Eclipse from the menu 'Window > Show views > Other > Git > Git Repositories'

<img src="img/git_eclipse_2.png"/>

Eclipse will show the view somewhere. Click on 'Clone a Git repository' to begin the process. 

<img src="img/git_eclipse_1.png"/>

In Location, paste the following URI: https://github.com/gabriel-skantze/IrisTK. The rest should be filled in automatically. You should see the following:

<img src="img/git_eclipse_4.png"/>

Click Next. In Branch Selection, only select the "master" branch. Click Next.

In Local Destination, choose the directory where you want to install IrisTK, for example C:\\IrisTK

<img src="img/git_eclipse_3.png"/>

Click Finish and wait for the repository to be cloned.

Open a command window. Go to the path where you chose to install IrisTK. Type:

```
iristk install
```

### Setting up the classpath

Open a command window (unless you already have one open) and type:

```
iristk eclipse
```

This will create a .classpath file in the IrisTK folder which makes it possible to import it as a project in Eclipse. This should be done each time you add a new addon or app to the IrisTK folder, if you don't want to configure these things manually in Eclipse.

### Import into Eclipse

1. Open Eclipse (or close it and re-open it if it was already open) and choose "File > Import..."
2. Choose "Existing Projects into Workspace" select the IrisTK folder as root directory.

### Eclipse Plug-in

There is an Eclipse Plug-in which lets you easily compile the dialogue flow. Here is how you install it:

1. Choose "Help > Install New Software..."
2. In "Work with", type "http://www.iristk.net/eclipse" and hit return.
3. Expand the group that comes up in the list and select "IrisTK Eclipse Plug-in". You may have to uncheck the "Group items by category" checkbox in order to see it. We also advide you to uncheck "Contact all update sites...", to speed up the installation.
4. Choose "Next" and follow the instructions (Eclipse needs to be restarted). 

Now you can compile a flow easily by right-clicking the flow XML file (either in the editor or in the Package Explorer) and choose "Compile Flow" in the context menu. The result of the flow compilation will be reported in the Console. Eclipse will then automatically compile the generated Java file to a class file. If there are any errors in this process, you will see that the Java file gets a red mark in the Package Explorer .