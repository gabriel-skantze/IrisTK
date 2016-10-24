/*******************************************************************************
 * Copyright (c) 2014 Gabriel Skantze.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Gabriel Skantze - initial API and implementation
 ******************************************************************************/
package iristk.speech.nuance9;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

interface SWIrec extends StdCallLibrary {

	SWIrec INSTANCE = (SWIrec) Native.loadLibrary("SWIrec", SWIrec.class);

	public static final int SWIrec_SAMPLE_FIRST = 0x01;
	public static final int SWIrec_SAMPLE_CONTINUE = 0x02;
	public static final int SWIrec_SAMPLE_LAST = 0x04;
	public static final int SWIrec_SAMPLE_SUPPRESSED = 0x08;
	public static final int SWIrec_SAMPLE_LOST = 0x10;
	public static final int SWIrec_SAMPLE_NEW_CHUNK = 0x20;
	public static final int SWIrec_SAMPLE_END_CHUNK = 0x40;

	public static final int SWIrec_STATUS_SUCCESS = 0;
	public static final int SWIrec_STATUS_NO_MATCH = 1;
	public static final int SWIrec_STATUS_INCOMPLETE = 2;
	public static final int SWIrec_STATUS_NON_SPEECH_DETECTED = 3;
	public static final int SWIrec_STATUS_SPEECH_DETECTED = 4;
	public static final int SWIrec_STATUS_SPEECH_COMPLETE = 5;
	public static final int SWIrec_STATUS_MAX_CPU_TIME = 6;
	public static final int SWIrec_STATUS_MAX_SPEECH = 7;
	public static final int SWIrec_STATUS_STOPPED = 8;
	public static final int SWIrec_STATUS_REJECTED = 9;
	public static final int SWIrec_STATUS_NO_SPEECH_FOUND = 10;

	int SWIrecInit(WString uri);

	int SWIrecRecognizerCreate(PointerByReference rec, Pointer inet,
			Pointer cache);

	int SWIrecSessionStart(Pointer rec, WString channelName, WString params);

	int SWIrecSessionEnd(Pointer rec);

	int SWIrecGrammarLoad(Pointer rec, SWIrecGrammarData grammarData);
	
	int SWIrecGrammarFree(Pointer rec, SWIrecGrammarData grammar);

	int SWIrecGrammarActivate(Pointer rec, SWIrecGrammarData grammar, int weight, String grammarID);

	int SWIrecGrammarDeactivate(Pointer rec, SWIrecGrammarData grammar);

	int SWIrecAcousticStateReset(Pointer rec);

	int SWIrecRecognizerSetParameter(Pointer rec, WString param, WString value);

	int SWIrecRecognizerStart(Pointer rec);

	int SWIrecAudioWrite(Pointer rec, SWIrecAudioSamples samples);

	/**
	 * Compute results for current recognition.
	 * Blocks until recognizer produces complete or partial recognition results
	 * (as requested); or is halted; or returns an audio status.
	 *
	 * The results data are valid until the next call to RecognizerStart(),
	 * RecognizerCompute(), or ParseDTMFResults().  If the status returned by
	 * RecognizerCompute() is SUCCESS, FAILURE, STOPPED, MAX_SPEECH, or
	 * MAX_CPU_TIME, and complete results were requested, then subsequent calls
	 * to RecognizerCompute() return the same status value until the next call to
	 * RecognizerStart().
	 *
	 * @param rec Recognizer handle
	 * @param maxComputeTime Maximum time to compute results during this call
	 *                       (in ms of real time); -1 indicates block until
	 *                       completion or change in state; 
	 * @param status Status of recognition upon completion
	 * @param type Type of results returned.
	 * @param resultData  Result vector containing results of current
	 *                    recognition including, recognized string,
	 *                    confidence, key/value list w/ confidences.
	 *                    Results are only returned if result type is
	 *                    PARTIAL or COMPLETE.
	 *
	 * @return SWIrec_SUCCESS on success
	 * @return SWIrec_ERROR_GRAMMAR_ERROR
	 *         run-time error during grammar parsing (usually ECMAscript bug)
	 * @return SWIrec_ERROR_INVALID_PARAMETER_VALUE
	 *         input parameter maxComputeTime is 0
	 * @return SWIrec_ERROR_INACTIVE if
	 *         RecognizerStart has not been called.
	 */
	int SWIrecRecognizerCompute(Pointer rec, int maxComputeTime, IntByReference status, IntByReference type, PointerByReference resultData);

	/**
	 * Stop current recognition.
	 * Aborts recognition of speech, discards all audio input, and discard all
	 * temporary recognition storage for this utterance.
	 *
	 * This function should be called to interrupt a currently active
	 * SWIrecRecognizerCompute() or after SWIrecRecognizerCompute() returns if no
	 * more recognition is required on the current utterance.
	 * This function must be called before RecognizerStart() can be called unless
	 * RecognizerCompute() returned a status (NOT return code) of:
	 * SUCCESS, FAILURE, MAX_SPEECH, or MAX_CPU_TIME.
	 * Subsequent calls to RecognizerCompute() will return a STOPPED status until
	 * RecognizerStart() is called.  Subsequent calls to ParseDTMFResults() are
	 * still valid.
	 *
	 * @param rec Recognizer handle
	 * @param code Reason for the stop.  This should be one of: SWIrec_STOP_SPEECH,
	 *             SWIrec_STOP_DTMF, SWIrec_STOP_HANGUP, SWIrec_STOP_TIMEOUT, 
	 *             SWIrec_STOP_OTHER
	 *
	 * @return SWIrec_SUCCESS on success
	 * @return SWIrec_ERROR_INACTIVE if recognition is not active.
	 */
	int SWIrecRecognizerStop(Pointer rec, int code);

	/**
	 * Return an XML result
	 *
	 * As with other result functions, the results are valid until the
	 * next call to RecognizerStart(), RecognizerCompute(), or this
	 * function again
	 *
	 * @param resultData  Result vector containing results of current recognition 
	 *                    including, recognized string, confidence, key/value list
	 *                    w/ confidences
	 * @param format      Format of the XML grammar
	 * @param xmlResult   XML result to be returned
	 *
	 * @return SWIrec_SUCCESS on success
	 * @return SWIrec_ERROR_INVALID_MEDIA_TYPE
	 *         if format is invalid
	 */
	int SWIrecGetXMLResult(Pointer result_data, WString format, PointerByReference xmlResult);

	class SWIrecGrammarData extends Structure {
		/**
		 * Grammar identifier
		 * 
		 * @param type
		 *            Grammar type ("uri", "uri/2.0", "string", or
		 *            "string/2.0")
		 * @param webData
		 *            type uri, uri/2.0: a pointer to the uri type string: a
		 *            pointer to the string grammar type string/2.0: NULL
		 * @param properties
		 *            URI caching properties for VXIinet to use-can be set
		 *            to NULL Properties understood only by SWIrec: -
		 *            SWIREC_DISK_CACHE_LOCK : When Loading or Compiling a
		 *            grammar, lock the cached grammar into the disk cache.
		 * @param media_type
		 *            type uri,string: should be NULL type string/2.0:
		 *            "text/xml", "application/srgs+xml",
		 *            "application/x-swi-grammar" type uri/2.0: all of the
		 *            string/2.0 types + NULL
		 * @param binary_data
		 *            type uri,uri/2.0,string: should be NULL type
		 *            string/2.0: a pointer to the data
		 * @param length
		 *            length of binary_data (if set)
		 */
		public WString type;
		public WString data;
		public Pointer properties; // const VXIMap *
		public WString media_type;
		public Pointer binary_data; // void *
		public int length; // unsigned

	}

	class SWIrecAudioSamples extends Structure {
		/**
		 * Audio sample data.  An audio sample of zero length can be used to
		 * indicate last buffer status without including any additional audio
		 * samples.
		 *
		 * @param samples Sample buffer
		 * @param len Length of sample buffer, in bytes
		 * @param type MEDIA type of samples
		 *   "audio/basic" 8-bit 8 KHz u-law encoding [unsigned char *]
		 *   "audio/x-alaw-basic" 8-bit 8 KHz A-law encoding [unsigned char *]
		 *   "audio/L16;rate=8000" 16-bit 8 KHz linear encoding [short *]
		 * @param status Sample status: first buffer, last buffer
		 */
		public Pointer samples; 
		public int len; 
		public WString type;
		public int status;
		
		
	}

}
