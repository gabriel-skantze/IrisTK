package iristk.speech.prosody;

import java.util.LinkedList;
import java.util.List;

import iristk.util.Record;
import iristk.util.Utils;

public class ProsodyFeatureExtractor implements ProsodyListener {

	List<ProsodyData> dataList = new LinkedList<>();
	private ProsodyNormalizer normalizer;
	
	public ProsodyFeatureExtractor(ProsodyNormalizer normalizer) {
		this.normalizer = normalizer;
	}
	
	public ProsodyFeatureExtractor() {
		this(null);
	}
	
	@Override
	public void prosodyData(ProsodyData data) {
		dataList.add(data);
	}
	
	public void newSegment() {
		dataList.clear();
	}
	
	public Record getFeatures() {
		List<Double> pitch200 = new LinkedList<>();
		List<Double> pitchZ200 = new LinkedList<>();
		List<Double> energyZ200 = new LinkedList<>();
		List<Double> pitchZ500 = new LinkedList<>();
		List<Double> pitch500 = new LinkedList<>();
		List<Double> energyZ500 = new LinkedList<>();
		List<Double> pitchZall = new LinkedList<>();
		List<Double> energyZall = new LinkedList<>();
		
		List<ProsodyData> newList = ProsodyTracker.filter(dataList);
		
		Integer length = null;
		for (int i = newList.size()-1; i >= 0; i--) {
			ProsodyData data = newList.get(i);
			ProsodyData normalized = null;
			if (data.pitch != -1 && normalizer != null)
				normalized = normalizer.normalize(data);

			if (data.pitch != -1) {
				if (length == null) {
					length = 0;
				}
				if (length < 20) 
					pitch200.add(0, data.pitch);
				if (length < 50)
					pitch500.add(0, data.pitch);
			}
			if (normalized != null) {				 
				if (length < 20) {
					pitchZ200.add(0, normalized.pitch);
					energyZ200.add(0, normalized.energy);
				}
				if (length < 50) {
					pitchZ500.add(0, normalized.pitch);
					energyZ500.add(0, normalized.energy);
				}
				pitchZall.add(0, normalized.pitch);
				energyZall.add(0, normalized.energy);
			} 
			if (length != null) {
				length++;
			}
		}
		
		Record result = new Record();
		
		if (pitchZ200.size() >= 6)  {
			double mean = Utils.mean(pitchZ200);
			result.put("pitch_200_mean", mean);
			result.put("pitch_200_stdev", Utils.stdev(pitchZ200, mean));
			result.put("pitch_200_max", Utils.max(pitchZ200));
			result.put("energy_200_max", Utils.max(energyZ200));
		} 
		if (pitch200.size() >= 6)  {
			result.put("pitch_200_slope", slope(pitch200));
		}
		if (pitchZ500.size() >= 6) {
			double mean = Utils.mean(pitchZ500);
			result.put("pitch_500_mean", mean);
			result.put("pitch_500_stdev", Utils.stdev(pitchZ500, mean));
			result.put("pitch_500_max", Utils.max(pitchZ500));
			result.put("energy_500_max", Utils.max(energyZ500));
		}
		if (pitch500.size() >= 6)  {
			result.put("pitch_500_slope", slope(pitch500));
		}
		if (pitchZall.size() >= 6) {
			result.put("pitch_all_stdev", Utils.stdev(pitchZall, Utils.mean(pitchZall)));
			result.put("pitch_all_max", Utils.max(pitchZall));
			result.put("energy_all_max", Utils.max(energyZall));
		}
		
		return result;
	}

	private static double slope(List<Double> list) {
		return Utils.mean(list.subList(list.size() / 2, list.size())) - Utils.mean(list.subList(0, list.size() / 2));
	}

	public List<ProsodyData> getData() {
		return ProsodyTracker.filter(dataList);
	}
	

}
