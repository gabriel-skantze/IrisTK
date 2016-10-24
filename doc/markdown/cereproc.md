## Cereproc Synthesizer

The Cereproc add-on provides the following classes 

* iristk.speech.cereproc.CereVoiceSynthesiser
* iristk.speech.cereproc.CereVoiceModule

### Installing voices

The Cereproc synthesizer searches for voices in the "voices" directory under the Cereproc addon folder. To add a voice, create a folder under this directory with the name of the voice (e.g. addon/Cereproc/voices/william).

Each voice folder should contain the following files (where [voice] is the name of the voice):

* [voice].lic: The license file for the voice
* [voice].properties: A properties file for the voice (see below)
* [voice].voice: The voice file

The properties file should contain the language and gender of the voice:

```
gender = Male
language = en-GB
```

