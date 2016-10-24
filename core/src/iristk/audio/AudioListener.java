package iristk.audio;

public interface AudioListener {

	public void listenAudio(byte[] buffer, int pos, int len);
	
	public void startListening();

	public void stopListening();
	
}
