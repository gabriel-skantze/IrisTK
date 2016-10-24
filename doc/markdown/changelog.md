### 2016-10-19

- Bug fixes
- Support for Google Cloud ASR (GoogleRecognizer). 
- OpenVocabulary context now supports list of hint words (used by Google ASR)
- Synthesizer Interface has changed 

### 2016-04-25

- It is now possible to incrementally monitor the output of the speech synthesizer, for example to synchronize with gestures. See "Standardized events" (section monitor.speech.mark) in the documentation. 
- The log4j logging framework is now being used (instead of just printing to the console). All system logs can be found in %TEMP%/iristk/logs/system
- The Console (in SimpleDialogSystem) now accepts typed input (which is parsed with semantic grammars). To use this, set it up like this:
	1. Remove system.setupRecognizer(...)
	2. Add system.setupConsoleRecognizer();
	3. If you only use a SpeechGrammar (in loadContext()), switch to SemanticGrammar
- Bug fixes

### 2015-11-04

- Bug fixes
- Addon Dragon has changed name to NuanceCloud, the file containing the API key is now called license.properties
- A new model for controlling which models are being used for speech recognition has been introduced, called "Context". Grammars (speech and semantics) are now loaded with loadContext(). Also, to listen with a specific grammar, use the attribute "context" and not "grammar". See the tutorials for examples on how to do this.

### 2015-08-10

- Bug fixes
- SituatedDialogSystem is set up slightly differently (see tutorial 2)
- SituatedDialogFlow is now called SystemAgentFlow
- Add possibility of several system agents
- All events are now documented
- Some events have changed names, for example:
	- sense.user.speech -> sense.user.speak 
- A new tutorial (3) is provided that explains semantics and dialog in more depth
- Prosodic analysis
- A new tool: TestRecognizer ("iristk asr") 