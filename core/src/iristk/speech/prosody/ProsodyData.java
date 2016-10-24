package iristk.speech.prosody;

import iristk.audio.AudioUtil;

import java.util.Locale;

public class ProsodyData {

	public double pitch;
	public double conf;
	public double time;
	public double energy;

	ProsodyData(double pitch, double energy, double conf, double time) {
		this.pitch = pitch;
		this.conf = conf;
		this.time = time;
		this.energy = energy;
	}

	ProsodyData(ProsodyData clone) {
		this.pitch = clone.pitch;
		this.conf = clone.conf;
		this.time = clone.time;
		this.energy = clone.energy;
	}

	@Override
	public String toString() {
		return String.format(Locale.US,  "pitchHz: %.2f, power: %.2f, conf: %.2f, time: %.2f", AudioUtil.pitchCentToHz(pitch), energy, conf, time);
	}

}
