package iristk.speech.nuancecloud;

/******************************************************************************
 *                                                                            *
 * Copyright (c) 1999-2003 Wimba S.A., All Rights Reserved.                   *
 *                                                                            *
 * COPYRIGHT:                                                                 *
 *      This software is the property of Wimba S.A.                           *
 *      This software is redistributed under the Xiph.org variant of          *
 *      the BSD license.                                                      *
 *      Redistribution and use in source and binary forms, with or without    *
 *      modification, are permitted provided that the following conditions    *
 *      are met:                                                              *
 *      - Redistributions of source code must retain the above copyright      *
 *      notice, this list of conditions and the following disclaimer.         *
 *      - Redistributions in binary form must reproduce the above copyright   *
 *      notice, this list of conditions and the following disclaimer in the   *
 *      documentation and/or other materials provided with the distribution.  *
 *      - Neither the name of Wimba, the Xiph.org Foundation nor the names of *
 *      its contributors may be used to endorse or promote products derived   *
 *      from this software without specific prior written permission.         *
 *                                                                            *
 * WARRANTIES:                                                                *
 *      This software is made available by the authors in the hope            *
 *      that it will be useful, but without any warranty.                     *
 *      Wimba S.A. is not liable for any consequence related to the           *
 *      use of the provided software.                                         *
 *                                                                            *
 * Class: JSpeexEnc.java                                                      *
 *                                                                            *
 * Author: Marc GIMPEL                                                        *
 * Based on code by: Jean-Marc VALIN                                          *
 *                                                                            *
 * Date: 9th April 2003                                                       *
 *                                                                            *
 ******************************************************************************/

/* $Id: JSpeexEnc.java,v 1.5 2005/05/27 13:14:39 mgimpel Exp $ */

/* Copyright (C) 2002 Jean-Marc Valin 

   Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions
   are met:

   - Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

   - Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.

   - Neither the name of the Xiph.org Foundation nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
   ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
   LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
   A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE FOUNDATION OR
   CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
   EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
   PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
   LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
   NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
   SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import iristk.audio.Sound;
import iristk.util.BlockingByteQueue;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.xiph.speex.SpeexEncoder;



/**
 * Java Speex Command Line Encoder.
 * 
 * Currently this code has been updated to be compatible with release 1.0.3.
 * 
 * @author Marc Gimpel, Wimba S.A. (mgimpel@horizonwimba.com)
 * @version $Revision: 1.5 $
 */
public class JSpeexEnc 
{
	/** Version of the Speex Encoder */
	public static final String VERSION = "Java Speex Command Line Encoder v0.9.7 ($Revision: 1.5 $)";
	/** Copyright display String */
	public static final String COPYRIGHT = "Copyright (C) 2002-2004 Wimba S.A.";

	/** Print level for messages : Print debug information */
	public static final int DEBUG = 0;
	/** Print level for messages : Print basic information */
	public static final int INFO  = 1;
	/** Print level for messages : Print only warnings and errors */
	public static final int WARN  = 2;
	/** Print level for messages : Print only errors */
	public static final int ERROR = 3;
	/** Print level for messages */
	protected int printlevel = INFO;

	/** File format for input or output audio file: Raw */
	public static final int FILE_FORMAT_RAW  = 0;
	/** File format for input or output audio file: Ogg */
	public static final int FILE_FORMAT_OGG  = 1;
	/** File format for input or output audio file: Wave */
	public static final int FILE_FORMAT_WAVE = 2;
	/** Defines File format for input audio file (Raw, Ogg or Wave). */
	protected int srcFormat  = FILE_FORMAT_OGG;
	/** Defines File format for output audio file (Raw or Wave). */
	protected int destFormat = FILE_FORMAT_WAVE;

	/** Defines the encoder mode (0=NB, 1=WB and 2=UWB). */
	protected int mode       = -1;
	/** Defines the encoder quality setting (integer from 0 to 10). */
	protected int quality    = 8;
	/** Defines the encoders algorithmic complexity. */
	protected int complexity = 3;
	/** Defines the number of frames per speex packet. */
	protected int nframes    = 1;
	/** Defines the desired bitrate for the encoded audio. */
	protected int bitrate    = -1;
	/** Defines the sampling rate of the audio input. */
	protected int sampleRate = -1;
	/** Defines the number of channels of the audio input (1=mono, 2=stereo). */
	protected int channels   = 1;
	/** Defines the encoder VBR quality setting (float from 0 to 10). */
	protected float vbr_quality = -1;
	/** Defines whether or not to use VBR (Variable Bit Rate). */
	protected boolean vbr    = false;
	/** Defines whether or not to use VAD (Voice Activity Detection). */
	protected boolean vad    = false;
	/** Defines whether or not to use DTX (Discontinuous Transmission). */
	protected boolean dtx    = false;

	SpeexEncoder speexEncoder;
	private EncodingThread encodingThread;

	/**
	 * Builds a plain JSpeex Encoder with default values.
	 */
	public JSpeexEnc(int sampleRate)
	{
		//TODO HARCODED for PCM 8K to SPEEX NB, need to add WB
		if(sampleRate <= 8000)
			mode = 0;
		else {
			mode = 1;
		}
		this.sampleRate = sampleRate;
		vbr_quality = 7;
		quality = 10;
		
	    speexEncoder = new SpeexEncoder();
		speexEncoder.init(this.mode, this.quality, this.sampleRate, this.channels);
		// Construct a new encoder
		//

	/*	if (this.complexity > 0) {
			speexEncoder.getEncoder().setComplexity(this.complexity);
		}
		if (this.bitrate > 0) {
			speexEncoder.getEncoder().setBitRate(this.bitrate);
		}
		if (this.vbr) {
			speexEncoder.getEncoder().setVbr(this.vbr);
			if (this.vbr_quality > 0) {
				speexEncoder.getEncoder().setVbrQuality(this.vbr_quality);
			}
		}
		if (this.vad) {
			speexEncoder.getEncoder().setVad(this.vad);
		}
		if (this.dtx) {
			speexEncoder.getEncoder().setDtx(this.dtx);
		}*/

	}
	
	public void startEncoding(BlockingByteQueue in, BlockingByteQueue out) {
		encodingThread = new EncodingThread(in, out);
	}
	
	private class EncodingThread implements Runnable {

		private Thread thread;
		private BlockingByteQueue out;
		private BlockingByteQueue in;

		public EncodingThread(BlockingByteQueue in, BlockingByteQueue out) {
			this.in = in;
			this.out = out;
			this.thread = new Thread(this);
			thread.start();
		}

		@Override
		public void run() {
			try {
				byte[] temp = new byte[2560]; // stereo UWB requires one to read 2560b
	
				OggSpeexHeaderWriter writer = new OggSpeexHeaderWriter(mode, sampleRate, channels, nframes, vbr, out);
	
					writer.writeHeader("Encoded with: " + VERSION );
				
	
				int pcmPacketSize = 2 * channels * speexEncoder.getFrameSize();
	
				try {
					// read until we get to EOF
					while (true) {
						int readBytes = in.read(temp, 0, nframes*pcmPacketSize);
						if (readBytes == -1)
							break;
						for (int i=0; i<nframes; i++){
							speexEncoder.processData(temp, i*pcmPacketSize, pcmPacketSize);		
						}
						int encsize = speexEncoder.getProcessedData(temp, 0);
						if (encsize > 0) {
							writer.writePacket(temp, 0, encsize);
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				writer.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
	public byte[] encode(byte[] audioData) throws Exception {
		byte[] temp = new byte[2560]; // stereo UWB requires one to read 2560b

		byte[] encodedAudio = null;

		// Open the input stream
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(audioData));
	
		OggSpeexHeaderWriter writer = new OggSpeexHeaderWriter(mode, sampleRate, channels, nframes, vbr, new BlockingByteQueue());

		writer.writeHeader("Encoded with: " + VERSION );

		int pcmPacketSize = 2 * channels * speexEncoder.getFrameSize();

		try {
			// read until we get to EOF
			while (true) {
				dis.readFully(temp, 0, nframes*pcmPacketSize);
				for (int i=0; i<nframes; i++){
					speexEncoder.processData(temp, i*pcmPacketSize, pcmPacketSize);		
				}
				int encsize = speexEncoder.getProcessedData(temp, 0);
				if (encsize > 0) {
					writer.writePacket(temp, 0, encsize);
				}
			}
		}
		catch (Exception e) {
		}
		writer.close();
		encodedAudio = writer.getEncodedAudio();
		dis.close();
		return encodedAudio;
	}
	
	public static void main(String[] args) throws Exception {
		Sound s = new Sound(new File("system.wav"));
		JSpeexEnc encoder = new JSpeexEnc(16000);
		byte[] encoded  = encoder.encode(s.getBytes());
		FileOutputStream fout = new FileOutputStream("system.ogg");
		fout.write(encoded);
		fout.close();
	}
}
