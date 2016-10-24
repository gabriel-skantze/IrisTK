package iristk.audio;

import iristk.speech.EnergyVAD;

import java.util.Arrays;
import java.util.LinkedList;

import javax.sound.sampled.AudioFormat;

/**
 * An AudioGate listens to an AudioPort and provides a new AudioPort which gates
 * the audio using a VAD. The gate starts closed, during which audio will be
 * muted to silence. Once speech is detected, the gate will open and the audio
 * will be passed through. It is possible to close the gate again using
 * closeGate().
 */
public class AudioGate extends AudioPort {

	private AudioPort port;

	private int lag;
	private EnergyVAD vad;
	private LinkedList<byte[]> queue = new LinkedList<>();
	private int queueSize;
	private boolean gateOpen = false;
	public boolean autoClose = false;
	private byte[] silence = new byte[0];

	public AudioGate(AudioPort port) {
		this.port = port;
		this.vad = new EnergyVAD(port.getDeviceName(), port.getAudioFormat());
		lag = EnergyVAD.WINSIZE * 10;
		port.addAudioListener(new MyAudioListener());
	}

	private class MyAudioListener implements AudioListener {

		@Override
		public synchronized void listenAudio(byte[] frame, int pos, int len) {
			vad.processSamples(frame, 0, len);
			if (autoClose && gateOpen && !vad.isInSpeech()) {
				gateOpen = false;
			}
			if (!gateOpen) {
				byte[] qframe = new byte[frame.length];
				System.arraycopy(frame, pos, qframe, 0, len);
				queue.add(qframe);
				queueSize += (int) (AudioUtil.byteLengthToSeconds(
						port.getAudioFormat(), len) * 1000.0);
				if (vad.isInSpeech()) {
					//System.out.println("Gate opening " + vad.getParameters().deltaSpeech);
					gateOpen = true;
					while (queue.size() > 0) {
						byte[] qBuf = queue.removeFirst();
						writeListeners(qBuf, 0, qBuf.length);
					}
					queueSize = 0;
				} else if (queueSize >= lag) {
					byte[] qBuf = queue.removeFirst();
					queueSize -= (int) (AudioUtil.byteLengthToSeconds(
							port.getAudioFormat(), qBuf.length) * 1000.0);
					if (silence.length != qBuf.length) {
						silence = new byte[qBuf.length];
						Arrays.fill(silence, 0, silence.length, (byte) 0);
					}
					writeListeners(silence, 0, silence.length);
				}
			} else {
				writeListeners(frame, pos, len);
			}
		}

		@Override
		public void startListening() {
			startListeners();
		}

		@Override
		public void stopListening() {
			stopListeners();
		}
	}
	
	public EnergyVAD getVAD() {
		return vad;
	}

	public synchronized void closeGate() {
		gateOpen = false;
		queueSize = 0;
		queue.clear();
	}

	public boolean isGateOpen() {
		return gateOpen;
	}

	@Override
	public AudioFormat getAudioFormat() {
		return port.getAudioFormat();
	}
	
	@Override
	public String getDeviceName() {
		return port.getDeviceName();
	}

}
