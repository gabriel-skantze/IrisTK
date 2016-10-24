package iristk.audio;

import java.io.File;

import javax.sound.sampled.AudioFormat;

import iristk.system.CorePackage;
import iristk.system.IrisUtils;
import iristk.util.BlockingByteQueue;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

public class SpeexEchoCanceller extends AudioPort {
	
	private static libspeexdsp LIBSPEEXDSP;
	
	private BlockingByteQueue recQueue = new BlockingByteQueue();
	private BlockingByteQueue playQueue = new BlockingByteQueue();
	private Pointer state;
	private int frameSize;
	private int filterLength;
	private byte[] recBuffer;
	private byte[] playBuffer;
	private byte[] outBuffer;

	private AudioFormat format;

	protected boolean running = true;

	private byte[] stereoBuffer;

	private StereoAudioPort stereoAudioPort;
	
	interface libspeexdsp extends StdCallLibrary {
		/** Creates a new echo canceller state
		 * @param frame_size Number of samples to process at one time (should correspond to 10-20 ms)
		 * @param filter_length Number of samples of echo to cancel (should generally correspond to 100-500 ms)
		 * @return Newly-created echo canceller state
		 */
		Pointer speex_echo_state_init(int frame_size, int filter_length);
		
		/** Destroys an echo canceller state 
		 * @param st Echo canceller state
		*/
		void speex_echo_state_destroy(Pointer st);
		
		/** Performs echo cancellation a frame, based on the audio sent to the speaker (no delay is added
		 * to playback in this form)
		 *
		 * @param st Echo canceller state
		 * @param rec Signal from the microphone (near end + far end echo)
		 * @param play Signal played to the speaker (received from far end)
		 * @param out Returns near-end signal with echo removed
		 */
		void speex_echo_cancellation(Pointer st, byte[] rec, byte[] play, byte[] out);
		
		/** Perform echo cancellation using internal playback buffer, which is delayed by two frames
		 * to account for the delay introduced by most soundcards (but it could be off!)
		 * @param st Echo canceller state
		 * @param rec Signal from the microphone (near end + far end echo)
		 * @param out Returns near-end signal with echo removed
		*/
		void speex_echo_capture(Pointer st, byte[] rec, byte[] out);

		/** Let the echo canceller know that a frame was just queued to the soundcard
		 * @param st Echo canceller state
		 * @param play Signal played to the speaker (received from far end)
		*/
		void speex_echo_playback(Pointer st, byte[] play);
		
		/** Reset the echo canceller to its original state 
		 * @param st Echo canceller state
		 */
		void speex_echo_state_reset(Pointer st);
	}
	
	public SpeexEchoCanceller(AudioPort speaker, AudioPort mic) {
		if (!AudioUtil.equalFormats(speaker.getAudioFormat(), mic.getAudioFormat()))
			throw new IllegalArgumentException("Speaker and microphone must have the same audio format");
		if (LIBSPEEXDSP == null) {
			IrisUtils.addCoreLibPath();
			System.load(CorePackage.PACKAGE.getPath("lib/x86/libspeexdsp.dll").getAbsolutePath());
			LIBSPEEXDSP = (libspeexdsp) Native.loadLibrary("libspeexdsp", libspeexdsp.class);
		}
		this.format = speaker.getAudioFormat();
		this.frameSize = AudioUtil.secondLengthToSamples(format, 0.02);
		this.recBuffer = new byte[frameSize*2];
		this.playBuffer = new byte[frameSize*2];
		/*
		Arrays.fill(playBuffer, (byte)0);
		playQueue.write(playBuffer);
		playQueue.write(playBuffer);
		playQueue.write(playBuffer);
		playQueue.write(playBuffer);
		playQueue.write(playBuffer);
		*/
		this.outBuffer = new byte[frameSize*2];
		this.stereoBuffer = new byte[frameSize*4];
		this.filterLength = AudioUtil.secondLengthToSamples(format, 0.1);
		this.state = LIBSPEEXDSP.speex_echo_state_init(frameSize, filterLength);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				running  = false;
				LIBSPEEXDSP.speex_echo_state_destroy(state);
				super.run();
			}
		});
		speaker.addAudioListener(new PlayAudioListener());
		mic.addAudioListener(new RecAudioListener());
	}
	
	private class PlayAudioListener implements AudioListener {

		@Override
		public void listenAudio(byte[] buffer, int pos, int len) {
			playQueue.write(buffer, pos, len);
			update();
		}

		@Override
		public void startListening() {
		}

		@Override
		public void stopListening() {
		}
		
	}
	
	private class RecAudioListener implements AudioListener {

		@Override
		public void listenAudio(byte[] buffer, int pos, int len) {
			recQueue.write(buffer, pos, len);
			update();
		}

		@Override
		public void startListening() {
		}

		@Override
		public void stopListening() {
		}
		
	}
	
	private synchronized void update() {
		if (!running)
			return;
		while (playQueue.available() >= playBuffer.length && recQueue.available() >= recBuffer.length) {
			try {
				playQueue.read(playBuffer);
				recQueue.read(recBuffer);
				LIBSPEEXDSP.speex_echo_cancellation(state, recBuffer, playBuffer, outBuffer);
				writeListeners(outBuffer, 0, outBuffer.length);
				if (stereoAudioPort != null) {
					for (int i = 0; i < frameSize; i++) {
						stereoBuffer[i*4] = playBuffer[i*2];
						stereoBuffer[i*4+1] = playBuffer[i*2+1];
						stereoBuffer[i*4+2] = recBuffer[i*2];
						stereoBuffer[i*4+3] = recBuffer[i*2+1];
					}
					stereoAudioPort.send(stereoBuffer);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public AudioFormat getAudioFormat() {
		return format;
	}

	public AudioPort getStereoInput() {
		stereoAudioPort = new StereoAudioPort();
		return stereoAudioPort;
	}
	
	private class StereoAudioPort extends AudioPort {
		
		@Override
		public AudioFormat getAudioFormat() {
			return AudioUtil.setChannels(format, 2);
		}

		public void send(byte[] buffer) {
			writeListeners(buffer, 0, buffer.length);
		}
	}

	public static void main(String[] args) throws Exception {

		SoundAudioSource sas = new SoundAudioSource(new Sound(new File("C:/Dropbox/iristk/app/telepresence/dagensdikt.wav")));
		Speaker speaker = new Speaker(16000, 1);

		sas.addAudioListener(speaker);
		sas.start();
		
		Microphone mic = new Microphone(16000, 1);
		

		SpeexEchoCanceller sec = new SpeexEchoCanceller(speaker, mic);
		
		
		AudioRecorder recorder = new AudioRecorder(sec);
		recorder.startRecording(new File("c:/test.wav"));
		
		AudioRecorder stereoRecorder = new AudioRecorder(sec.getStereoInput());
		stereoRecorder.startRecording(new File("c:/test_stereo.wav"));
		
		Thread.sleep(10000);
		
		recorder.stopRecording();
		stereoRecorder.stopRecording();
		System.exit(0);
		
	}


}
