package iristk.speech.prosody;

import iristk.util.Record;

public class ProsodyProfile extends Record {
		
	@RecordField
	public double pitchMean;
	@RecordField
	public double pitchStDev;
	@RecordField
	public double energyMean;
	@RecordField
	public double energyStDev;
	
	public ProsodyProfile(double pitchMean, double pitchStDev, double energyMean, double energyStDev) {
		this.pitchMean = pitchMean;
		this.pitchStDev = pitchStDev;
		this.energyMean = energyMean;
		this.energyStDev = energyStDev;
	}
	

	//public static NormData male = new NormData(-475.1, 197);
	
}
