using System;
using System.IO;
using System.Collections.Generic;
using System.Threading;
using Microsoft.Speech.Recognition;
using Microsoft.Speech.Recognition.SrgsGrammar;
using Microsoft.Speech.AudioFormat;
using Microsoft.Speech.Synthesis;
using System.Collections;
using System.Globalization;
using System.Xml;

namespace IrisTK.Net.Speech
{

    public class SpeechPlatformRecognizer : IRecognizer {

        private IResultListener listener = null;

        private SpeechRecognitionEngine speechRecognitionEngine = null;

        private int maxAudioLevel = 0;
        private SpeechAudioStream audioStream = null;
        private SpeechAudioFormatInfo audioFormat = null;
        
        public int getRecognizerSetting(String name)
        {
            return (int) speechRecognitionEngine.QueryRecognizerSetting(name);
        }

        public void setRecognizerSetting(String name, int value)
        {
            speechRecognitionEngine.UpdateRecognizerSetting(name, value);
        }

        public void setMaxAlternates(int max)
        {
            speechRecognitionEngine.MaxAlternates = max;
        }

        public void startRecognizing()
        {
            speechRecognitionEngine.RecognizeAsync();
        }

        private SemanticStruct createStruct(SemanticValue init)
        {
            SemanticStruct result = new SemanticStruct();
            if (init.Value != null)
            {
                result.value = init.Value;
                result.confidence = init.Confidence;
            }
            foreach (KeyValuePair<String, SemanticValue> child in init)
            {
                result.Add(child.Key, createStruct(child.Value));
            }
            return result;
        }

        private Hypothesis createHypothesis(RecognizedPhrase phrase)
        {
            Hypothesis hyp = new Hypothesis();
            hyp.text = phrase.Text;
            hyp.confidence = phrase.Confidence;
            if (phrase.Semantics != null)
                hyp.semantics = createStruct(phrase.Semantics);
            foreach (RecognizedWordUnit word in phrase.Words)
            {
                Word rword = new Word();
                rword.text = word.Text;
                rword.confidence = word.Confidence;
                hyp.Add(rword);
            }
            return hyp;
        }

        private void processSpeechHypothesizedEventArgs(SpeechHypothesizedEventArgs result)
        {
            if (listener != null)
            {
                Result res = new Result();
                res.cancelled = false;
                res.timeout = false;

                if (result.Result != null)
                {
                    res.Add(createHypothesis(result.Result));
                }

                listener.recognizeHypothesis(res);
            }
        }

        private void processRecognizeCompletedEventArgs(RecognizeCompletedEventArgs result)
        {
            if (listener != null) {
                Result res = new Result();

                res.cancelled = result.Cancelled;
                res.timeout = result.InitialSilenceTimeout;

                if (result.Result != null)
                {
                    fillResult(result.Result, res);
                }
               
                listener.recognizeCompleted(res);
            }
        }

        private void fillResult(RecognitionResult result, Result res)
        {
            if (result.Alternates.Count > 0)
            {
                foreach (RecognizedPhrase phrase in result.Alternates)
                {
                    res.Add(createHypothesis(phrase));
                }
            }
            res.grammar = result.Grammar.Name;
            res.length = (int)Math.Round(result.Audio.Duration.TotalMilliseconds);
        }

        public void registerListener(IResultListener listener) {
            this.listener = listener;
        }

        public static String getLanguages(Boolean kinect)
        {
            String result = "";
            foreach (RecognizerInfo config in SpeechRecognitionEngine.InstalledRecognizers())
            {
                if (kinect)
                {
                    string value;
                    config.AdditionalInfo.TryGetValue("Kinect", out value);
                    if (!"True".Equals(value, StringComparison.OrdinalIgnoreCase))
                        continue;
                }
                result += " " + config.Culture.IetfLanguageTag;
            }
            return result.Trim();
        }

        public SpeechPlatformRecognizer(String lang, Boolean kinect) {

            //Console.WriteLine("Looking for Speech Platform recognizers");
            foreach (RecognizerInfo config in SpeechRecognitionEngine.InstalledRecognizers()) {
                //if (config.Culture.Equals(requiredCulture) && config.Id == requiredId) {
                //Console.WriteLine("  Found: " + config.Description);
                if (speechRecognitionEngine == null && lang.Equals(config.Culture.IetfLanguageTag))
                {
                    if (kinect)
                    {
                        string value;
                        config.AdditionalInfo.TryGetValue("Kinect", out value);
                        if (!"True".Equals(value, StringComparison.OrdinalIgnoreCase))
                            continue;
                    }
                    speechRecognitionEngine = new SpeechRecognitionEngine(config);
                }
            }
            
            if (speechRecognitionEngine == null)
            {
                return;
            }
            
            speechRecognitionEngine.MaxAlternates = 1;

            speechRecognitionEngine.SpeechDetected +=
                delegate(object sender, SpeechDetectedEventArgs eventArgs) {
                    if (listener != null) {
                        listener.speechDetected(maxAudioLevel);
                    }
                };

            speechRecognitionEngine.RecognizeCompleted +=
                delegate(object sender, RecognizeCompletedEventArgs eventArgs)
                {
                    processRecognizeCompletedEventArgs(eventArgs);
                };

           
            speechRecognitionEngine.SpeechHypothesized +=
                delegate(object sender, SpeechHypothesizedEventArgs eventArgs) {
                    processSpeechHypothesizedEventArgs(eventArgs);
                };

            speechRecognitionEngine.AudioLevelUpdated +=
                delegate(object sender, AudioLevelUpdatedEventArgs eventArgs) {
                    //Console.WriteLine(eventArgs.AudioLevel);
                    maxAudioLevel = Math.Max(maxAudioLevel, eventArgs.AudioLevel);
                };

            //setInputToDefaultAudioDevice();
        }

        public int getAudioLevel()
        {
            return speechRecognitionEngine.AudioLevel;
        }

        public SpeechAudioStream setupAudioStream(int sampleRate, int bufferSize)
        {
            audioStream = new SpeechAudioStream(bufferSize);
            audioFormat = new SpeechAudioFormatInfo(sampleRate, AudioBitsPerSample.Sixteen, AudioChannel.Mono);
            return audioStream;
        }

        public void setInputToAudioStream()
        {
            speechRecognitionEngine.SetInputToAudioStream(audioStream, audioFormat);
        }
               
        public void setInputToDefaultAudioDevice()
        {
            speechRecognitionEngine.SetInputToDefaultAudioDevice();
        }

        public void setInputToWaveFile(string path)
        {
            speechRecognitionEngine.SetInputToWaveFile(path);
        }

        private Dictionary<String, Grammar> grammars = new Dictionary<string, Grammar>();

        public void deactivateGrammar(String name)
        {
            speechRecognitionEngine.UnloadGrammar(grammars[name]);
        }

        public void activateGrammar(String name, float weight)
        {
            grammars[name].Weight = weight;
            activateGrammar(name);
        }

        public void activateGrammar(String name)
        {
            speechRecognitionEngine.LoadGrammar(grammars[name]);
        }

        public void loadGrammarFromPath(String name, String path)
        {
            grammars[name] = new Grammar(path);
        }

        public void loadDictationGrammar(String name)
        {
            Console.Error.WriteLine("ERROR: Speech Platform does not support dictation grammars");
        }

        public void loadGrammarFromString(String name, String grammar)
        {
            byte[] bytes = new byte[grammar.Length * sizeof(char)];
            System.Buffer.BlockCopy(grammar.ToCharArray(), 0, bytes, 0, bytes.Length);
            grammars[name] = new Grammar(new SrgsDocument(XmlReader.Create(new MemoryStream(bytes))));
        }

        public void recognizeCancel()
        {
            speechRecognitionEngine.RecognizeAsyncCancel();
        }

	    public void setNoSpeechTimeout(int msec) {
            speechRecognitionEngine.InitialSilenceTimeout = new TimeSpan(0, 0, 0, 0, msec);
	    }

        public void setEndSilTimeout(int msec)
        {
            speechRecognitionEngine.EndSilenceTimeout = new TimeSpan(0, 0, 0, 0, msec);
	    }

	    public void setMaxSpeechTimeout(int msec) {
            speechRecognitionEngine.BabbleTimeout = new TimeSpan(0, 0, 0, 0, msec);
	    }

        public void recognizeAsync() {
           // this.audioId = id;
            this.maxAudioLevel = 0;
            //speechRecognitionEngine.EndSilenceTimeoutAmbiguous = new TimeSpan(0, 0, 0, 0, endSilence);
            speechRecognitionEngine.RecognizeAsync();
        }

        public Result recognize()
        {
            RecognitionResult result = speechRecognitionEngine.Recognize();
            Result res = new Result();
            if (result != null)
                fillResult(result, res);
            return res;
        }
    }

}

