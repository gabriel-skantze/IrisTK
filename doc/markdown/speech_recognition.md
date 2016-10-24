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

### Open vocabulary recognizers

Whereas a speech grammar needs to match exactly what the user said, you can also use an open vocabulary recognizer that does not rely on a speech grammar. In most cases, the speech recognition will not be perfect with such a recognizer, and as long as the user speaks in-grammar, grammar-based speech recognition will often perform better. But in many cases it is simply impossible to write a grammar that covers everything the user might say.

Although the WindowsRecognizer supports open vocabulary recognition, the accuracy is very poor. IrisTK also comes with the NuanceCloudRecognizer, which supports open vocabulary recognition (but not SpeechGrammars), and has a relatively high accuracy. As the name indicates, it does the recognition in the cloud, so you need a relatively fast Internet connection. You will also need to [sign up for a developer account](http://developer.nuance.com/public/index.php?task=register), and get your APP_ID and APP_KEY. Create a file called license.properties in the addon/NuanceCloud folder under the IrisTK installation. Enter your credentials like this:

```
APP_ID = NMDPTRIAL...
APP_KEY = 8e26ad5...
```

For this recognizer, you should add an [OpenVocabularyContext](http://www.iristk.net/javadoc/iristk/speech/OpenVocabularyContext.html). You have to specify which language should be used for the context:

```java
system.loadContext("default", new OpenVocabularyContext(Language.US);
```

### Semantic grammars

Whereas the SpeechGrammar describes both what the recognizer should listen for, and how this should be interpreted into semantics, an open vocabulary recognizer does not produce any semantic interpretation by itself. Thus we should provide it with a Semantic Grammar. The semantic grammar uses the same format as the speech grammar (SRGS), but the parsing is more relaxed. Thus, there is no requirement that the "root" rule must match the whole input. Instead, the grammar can match individual phrases in the input with "garbage" words in between. The parser tries to match all rules that are marked with the attribute public="true", to cover as many words as possible with as high-level rules as possible. 

<!--
In our grammar example, only the "root" rule is marked as public. However, this rule is designed so that it can also match short key phrases such as "a cheese burger" and "a large coke". Thus, even if the user says something that is not covered by the root rule, such as "I'm gonna have a cheese burger and then I want a large coke", the parser will match these phrases, and then combine the semantics.

To try this out, choose the Nuance Cloud recognizer from the drop-down. Open BurgerGrammar.xml into the Semantic Grammar window (bottom right), and press Load. Then you can press Listen and say something to see the result. Be aware that since the recognition is cloud-based, the result will take a little bit more time than with the Windows recognizer. You can also try to write a text string in the bottom of the Semantic Grammar window and press Parse. Then you will also see how the individual phrases match.
-->

The semantic grammar also allows for some more tricks that are not supported by the speech grammar. You can use the "_" symbol in words to match zero to many characters. Thus "\_burger\_" would match both "burger", "hamburger" and "cheeseburgers". (In fact you can use any [regular expression](https://en.wikipedia.org/wiki/Regular_expression), but "\_" is interpreted as ".*").       

Here is an example of you setup Nuance Cloud recognizer together with a semantic grammar in a dialog system. As you can see, we associate both an open vocabulary grammar and a semantic grammar to the context "default":

```java
system.setupRecognizer(new NuanceCloudRecognizerFactory());
system.loadContext("default", new OpenVocabularyContext(system.getLanguage()));
system.loadContext("default", new SemanticGrammarContext(new SRGSGrammar(getClass().getResource("BurgerGrammar.xml").toURI())));
```

Note that it is also possible to combine speech grammars with semantic grammars in recognizers that use speech grammars, such as the Windows recognizer (using both SpeechGrammarContext and SemanticGrammarContext). In this case, the speech grammar will define what can be said (but the \<tag\> elements will be ignored), and the resulting text string will be parsed and interpreted with the semantic grammar.

### Implementing your own semantic context

You can implement your own semantic processor. For this you need to implement two classes:

1. A class implementing [RecognizerListener](http://www.iristk.net/javadoc/iristk/speech/RecognizerListener.html). This class investigate the *text* field of the speech recognition result and add a *sem* field which contains a Record with the semantic interpretation. For examples of how this is done, see the Parser or WitListener classes. 

2. A class extending [Context](http://www.iristk.net/javadoc/iristk/speech/Context.html). For examples of how this is done, see the SemanticGrammarContext or WitContext classes.

