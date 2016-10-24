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

import iristk.speech.nuance9.SWIrec.SWIrecAudioSamples;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

interface SWIep extends StdCallLibrary {
	
	SWIep INSTANCE = (SWIep) Native.loadLibrary("SWIep", SWIep.class);

	public static final int SWIep_LOOKING_FOR_SPEECH = 0;
	public static final int SWIep_IN_SPEECH = 1;
	public static final int SWIep_AFTER_SPEECH = 3;
	public static final int SWIep_TIMEOUT = 4;
	public static final int SWIep_AUDIO_ERROR = 5;
	public static final int SWIep_MAX_SPEECH = 6;
	public static final int SWIep_IDLE = 7;
	
	int SWIepInit();
	
	int SWIepTerminate();
	
	int SWIepSessionStart(Pointer rec, WString channelName, WString params);
	
	int SWIepSessionEnd(Pointer rec);

	int SWIepDetectorCreate(PointerByReference det);
	
	int SWIepAcousticStateReset(Pointer det);

	int SWIepStart(Pointer det);
	
	int SWIepStop(Pointer det, int code, WString hints);
	
	int SWIepPromptDone(Pointer det);
	
	int SWIepSetParameter(Pointer det, WString param, WString value);
	
	int SWIepWrite(Pointer det, SWIepAudioSamples samples, IntByReference state, IntByReference beginSample, IntByReference endSample);

	int SWIepRead(Pointer det, SWIrecAudioSamples samples, IntByReference state, int maxLen);
	
	class SWIepAudioSamples extends Structure {
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
