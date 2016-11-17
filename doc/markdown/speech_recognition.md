## Speech recognition and semantics

### Speech recognizers in IrisTK

Each speech recognizer in IrisTK listens to one stream of audio, and runs within a [RecognizerModule](http://www.iristk.net/javadoc/iristk/speech/RecognizerModule.html). The RecognizerModule is initialized with a [Recognizer](http://www.iristk.net/javadoc/iristk/speech/Recognizer.html), such as the WindowsRecognizer or NuanceCloudRecognizer, like this:

```java
system.addModule(new RecognizerModule(new WindowsRecognizer(microphone)));
```

However, if you are using a dialogue system helper class (such as SimpleDialogSystem or SituatedDialogSystem), the recognizer can be added using a factory:

```java
system.setupRecognizer(new WindowsRecognizerFactory());
```

The factory allows the system to create any number of recognizers, depending on the microphone configuration (how many audio channels are used). 

If you want to support a new recognizer in IrisTK, you should implement the Recognizer interface, and extend the RecognizerFactory class. Look at WindowsRecognizer and WindowsRecognizerFactory for how this is done. 

### Speech recognition contexts

Speech recognizers are dependent on a certain Context for their operation. The Context specifies which language model to use (such as a grammar), but also the semantic processing of the speech recognition result. For a context to be used, it must first be loaded by each Recognizer. You can load a context using the *action.context.load* event (see further down), but there is also a convenience method for it when configuring your dialog system. Here is an example of how a Context in the form of a SpeechGrammar with the name "default" is loaded:

```java
system.loadContext("default", new SpeechGrammarContext(new SRGSGrammar(system.getPackageFile("MyGrammar.xml"))));
```

The SpeechGrammar specifies the words and phrases that the recognizer should listen for, as well as the semantic interpretation (although this is not needed). Note that all recognizers (such as NuanceCloudRecognizer) do not support SpeechGrammars. 

It is possible to load several Contexts with different names. In this case, you should specify which one of these Contexts is the default one to use, either through the *action.context.default* event (see further down), or through the convenience method:

```java
system.setDefaultContext("mycontext");
``` 

When the recognizers starts to listen for speech, it will use this default context, if nothing else is specified. But it is also possible to specify certain contexts to use, either by adding the *context* attribute to the **action.listen** event (see further down), or if you are triggering the listening in the flow, like this (where two contexts are activated): 

```xml
<dialog:listen context="'mycontext1 mycontext2'"/>
```

Switching between different contexts can be very powerful, since it allows the recognizer to listen to certain phrases, depending on the dialogue state, thereby increasing the recognition accuracy. 

### Speech Grammars

A SpeechGrammar is defined as a so-called [Context Free Grammar](https://en.wikipedia.org/wiki/Context-free_grammar). The standard format is SRGS ([Speech Recognition Grammar Specification](https://www.w3.org/TR/speech-grammar/)), which is an open XML-based standard defined by W3C. A very simple SRGS grammar can look like this:

```xml
<?xml version="1.0" encoding="utf-8"?>
<grammar xml:lang="en-US" version="1.0" root="root"
	xmlns="http://www.w3.org/2001/06/grammar">

  <rule id="root" scope="public">
      <one-of>
          <item>one <tag>out.number=1</tag></item>
          <item>two <tag>out.number=2</tag></item>
          <item>three <tag>out.number=3</tag></item>
      </one-of>
  </rule>
  
</grammar>
``` 

As can be seen, besides specifying valid phrases for the recognizer, the grammar also provides tags (\<tag\>...\<tag\>) for semantic interpretation. This is following the specification [Semantic Interpretation for Speech Recognition (SISR)](https://www.w3.org/TR/semantic-interpretation/).

### Grammar formats

The SpeechGrammarContext supports any grammar format, not just SRGS, as long as it is implemented with the [Grammar](http://www.iristk.net/javadoc/iristk/cfg/Grammar.html) interface. 

One such format, that is implemented in IrisTK, is the ABNF format. It has the same expressiveness as SRGS, but has a less wordy syntax, since it is not XML. An ABNF grammar can be loaded like this:

```java
system.loadContext("default", new SpeechGrammarContext(new ABNFGrammar(system.getPackageFile("GuessGrammar.abnf"))));
``` 

An ABNF grammar corresponding to the one above would look like this:

```
#ABNF 1.0 UTF-8;

language en-US;
root $root;

public $root = 
((one   {out.number=1})  | 
 (two   {out.number=2})  | 
 (three {out.number=3}));
``` 

You can convert between ABNF and SRGS using the iristk command:

``` 
iristk abnf2srgs [abnf-file] > [srgs-file]
iristk srgs2abnf [srgs-file] > [abnf-file]
```

### Combining grammars

IrisTK does not support import or include statements in grammars, but you can combine rules from different grammars. Let's say you want to have a travel grammar, where the cities are storied in a text file. The main grammar (TravelGrammar.xml) could look like this:

```xml
<grammar xml:lang="en-US" version="1.0" root="ROOT"
	xmlns="http://www.w3.org/2001/06/grammar">
	
	<rule id="ROOT" scope="public">
			<item>
				I want to travel to
				<ruleref uri="#CITY" />
			</item>
	</rule>		

</grammar>
```

IrisTK comes with a very simple grammar format called [ListGrammar](http://www.iristk.net/javadoc/iristk/cfg/ListGrammar.html). It simply reads a text file and generates a rule (with a specified name), where each line is an option. You can use this to add the cities as a new rule to the grammar above:

```java
SRGSGrammar travelGrammar = new SRGSGrammar(system.getPackageFile("TravelGrammar.xml"));
travelGrammar.addRules(new ListGrammar(system.getPackageFile("cities.txt"), Language.ENGLISH_US, "CITY"));
```

### Open vocabulary recognizers and semantic grammars

Please refer to [Tutorial 3](tutorial_semantics.html) for a description of how to use open vocabulary recognizers and semantic grammars. 

### Implementing your own semantic context

You can implement your own semantic processor. For this you need to implement two classes:

1. A class implementing [RecognizerListener](http://www.iristk.net/javadoc/iristk/speech/RecognizerListener.html). This class investigate the *text* field of the speech recognition result and add a *sem* field which contains a Record with the semantic interpretation. For examples of how this is done, see the Parser or WitListener classes. 

2. A class extending [Context](http://www.iristk.net/javadoc/iristk/speech/Context.html). For examples of how this is done, see the SemanticGrammarContext or WitContext classes.

