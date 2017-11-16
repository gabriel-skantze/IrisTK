## Installation

### Requirements

* Windows 7, 8 or 10 (64-bit)
	* The core framework should be portable to other platforms (since it is Java-based), but you will have to do this on your own. Also, to take advantage of the different modules that are supported, you would need to work under Windows as most of them require a Windows operating system.
	* IrisTK comes with support for Windows ASR and TTS (which supports any SAPI voices). For these to work, you need an English version of Windows.
* [Java 8](https://www.java.com/en/download/)
* For development, you are advised to use the [Eclipse IDE](develop_in_eclipse.html)

### Using the GitHub repository

To install IrisTK, start by cloning the IrisTK repository from GitHub: 

[https://github.com/gabriel-skantze/IrisTK](https://github.com/gabriel-skantze/IrisTK)

There, you can choose to either download the repository as a ZIP file, or clone it using you Git client. 

NB: To avoid troubles, choose a path on your computer that does not contain spaces. 

After cloning or unzipping the repository, go to the root IrisTK directory, and type:

```
iristk install
``` 

[Here](develop_in_eclipse.html) you can find instructions on how to do this procedure directly in Eclipse.

### Test

IrisTK comes with two example applications: Chess (speech-only interaction) and Quiz (multi-party interaction with an animated agent). To test the Chess app, open a command window and type: 

```
iristk chess
``` 

The app will use the Windows ASR and TTS, which assumes that you are running on an English version of Windows. To test the game, you are advised to use a headset. You can say commands such as "move the pawn that stands in front of the queen", "move the right knight to the left", etc. In case of ambiguity, the system will make clarification requests. 

You can also test the game without ASR and TTS, using the keyboard. To do so, start the game with "iristk chess console". To interact with the game, you must first click in the text entry field in the bottom the console panel. Then you can type your command (such as "move the pawn") and press enter. 

The quiz app have a few more requirements. For more information on these, see [Tutorial 2](tutorial_sitint.html).  

### Troubleshoot

If there are any problems during installation, or if the test doesn't work, check the following:

1. The installation should have created a user environment variable called "IrisTK", pointing it to the root IrisTK folder
2. The installation should have added %IrisTK% to the user's PATH environment variable
3. If the Windows ASR and TTS doesn't work, check that you have English-US installed as input language. On Windows 8, it is not enough to change the display language, you also need to download the [English-US language pack](http://windows.microsoft.com/en-us/windows/language-packs#lptabs=win8)
4. If the Face (animated agent) doesn't work, check that you have Microsoft Visual C++ 2010 Redistributable installed

If you don't know how to change or set environment variables in Windows, please check [these instructions](http://www.java.com/en/download/help/path.xml).

If these things don't help, use the [iristk-users](http://groups.google.com/group/iristk-users) mailing list. Try first to search the list for similar problems, and if that doesn't help, you can post your problem there. 

###	Folder structure

The IrisTK installation consists of a set of packages. Each package is self-contained in its own folder, which makes them easy to add or remove (although packages may rely on each other). 

The IrisTK installation contains four main folders:

----------  -----------------------
core        The core package for IrisTK (no modules for ASR or TTS are included here)
addon       Add-on packages that add support for input and output, or any other kinds of extensions to the core. 
app         Application packages built with IrisTK. Two example applications, "chess" and "quiz" are included. This is a convenient place for new applications.
doc			Documentation		
----------  -----------------------

Each package folder has the following contents:

----------   -----------------------
package.xml  A description of the package
src          Java source files
bin          Java class files (compiled from src)
lib          Java library files (mainly jar files, but also dll:s)
----------   -----------------------

### Command-line tool

If you have installed IrisTK properly, you can open a command window and run the iristk.exe tool from anywhere. This is a convenient command to use instead of java.exe, since it provides short names for typical actions and programs, locates the correct Java version and sets the classpath for you. To check which commands iristk.exe provides, simply run "iristk" without any parameters. The set of commands are defined in the "package.xml" files and are thus possible to extend.
