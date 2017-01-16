package iristk.speech;

import iristk.audio.AudioListener;

public interface VAD extends AudioListener {

	void addVADListener(Listener vadListener);
	
	public static interface Listener {

		/**
		 * 
		 * @param streamPos The position in the stream (counted in samples)
		 * @param inSpeech Voice activity at position
		 * @param energy Average energy at position
		 */
		public void vadEvent(long streamPos, boolean inSpeech, int energy);
	}
	
}
