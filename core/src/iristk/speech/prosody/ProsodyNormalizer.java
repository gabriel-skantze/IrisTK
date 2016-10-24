package iristk.speech.prosody;

import iristk.audio.AudioUtil;
import iristk.util.Record;
import iristk.util.Record.JsonToRecordException;
import iristk.util.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ProsodyNormalizer implements ProsodyListener {

	private static final int maxBufferSize = 6000;
	private List<ProsodyData> buffer = new LinkedList<>();
	private ProsodyProfile profile = null;
	
	@Override
	public void prosodyData(ProsodyData data) {
		profile = null;
		buffer.add(data);
		if (buffer.size() > maxBufferSize) {
			buffer.remove(0);
			buffer.remove(0);
		}
	}

	public void saveProfile(File file) throws IOException {
		if (profile != null) {
			profile.toJSON().writeTo(new FileWriter(file));
		}
	}
	
	public void loadProfile(File file) throws IOException {
		try {
			profile = (ProsodyProfile) Record.fromJSON(file);
		} catch (JsonToRecordException e) {
			throw new IOException("Could not parse JSON: " + e.getMessage());
		}
	}
	
	public ProsodyProfile getProfile() {
		if (profile != null)
			return profile;
		if (buffer.size() < 100)
			return null;
		List<Double> pitchBuffer = new LinkedList<>();
		List<Double> energyBuffer = new LinkedList<>();
		for (ProsodyData data : buffer) {
			if (data.pitch != -1) {
				pitchBuffer.add(data.pitch);
				energyBuffer.add(data.energy);
			}
		}
		double pitchMean = Utils.mean(pitchBuffer);
		double energyMean = Utils.mean(energyBuffer);
		profile = new ProsodyProfile(pitchMean, Utils.stdev(pitchBuffer, pitchMean), energyMean, Utils.stdev(energyBuffer, energyMean));
		return profile;
	}

	public ProsodyData normalize(ProsodyData pd) {
		getProfile();
		if (profile != null && pd.pitch != -1) {
			double pitchZ = ((pd.pitch - profile.pitchMean) / profile.pitchStDev);
			double energyZ = ((pd.energy - profile.energyMean) / profile.energyStDev);
			return new ProsodyData(pitchZ, energyZ, pd.conf, pd.time);
		} else {
			return null;
		}
	}
	
	public void filter(double energyThreshold) {
		buffer = ProsodyTracker.filter(buffer, energyThreshold);
	}

	public double getPitchHzLowerBound() {
		getProfile();
		if (profile != null)
			return AudioUtil.pitchCentToHz(profile.pitchMean - profile.pitchStDev*3);
		else
			return 50;
	}

	public double getPitchHzUpperBound() {
		getProfile();
		if (profile != null)
			return AudioUtil.pitchCentToHz(profile.pitchMean + profile.pitchStDev*3);
		else
			return 500;
	}
}
