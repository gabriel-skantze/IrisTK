package iristk.audio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import iristk.speech.prosody.ProsodyData;
import iristk.speech.prosody.ProsodyListener;
import iristk.speech.prosody.ProsodyTracker;
import iristk.util.ArgParser;
import iristk.util.Record;
import iristk.util.Utils;

public class MicrophoneConfiguration extends Record {

	private int nChannels;
	private boolean listening = false;
	private int listeningChannel = 0;
	private List<ChannelCandidate> candidates = new ArrayList<>(); 
	
	@RecordField
	public List<ChannelConfiguration> channels = new ArrayList<>();
		
	public MicrophoneConfiguration() {
	}
	
	private class ChannelCandidate implements ProsodyListener {

		public final String device;
		public int channel;
		public List<List<Double>> energyL = new ArrayList<>();
		
		public ChannelCandidate(String mic, int channel) {
			this.device = mic;
			this.channel = channel;
			for (int i = 0; i < nChannels; i++) {
				energyL.add(new ArrayList<>());
			}
		}

		@Override
		public void prosodyData(ProsodyData pd) {
			if (listening && pd.conf > -1) {
				energyL.get(listeningChannel).add(pd.energy);
			}
		}

		public double getMeanEnergy(int i) {
			if (energyL.get(i).size() >= 50)
				return Utils.mean(energyL.get(i));
			else
				return -1;
		}
		
	}
	
	public void startConfiguration(int nChannels) throws Exception {
		if (nChannels > 2)
			throw new IllegalArgumentException("Cannot configure more than 2 channels");
		this.nChannels = nChannels;
		for (String dev : Microphone.getDevices()) {
			if (dev.contains("Microsoft Sound Mapper"))
				continue;
			int inch = Microphone.getDeviceInfo(dev).maxInputChannels; 
			if (inch == 2) {
				addChannel(dev, 0, new Microphone(dev, 16000, 2, 0));
				addChannel(dev, 1, new Microphone(dev, 16000, 2, 1));
			} else if (inch == 1) {
				addChannel(dev, 0, new Microphone(dev, 16000, 1));
			}
		}
	}

	private void addChannel(String name, int channel, AudioPort port) {
		ProsodyTracker tracker = new ProsodyTracker(port.getAudioFormat(), 70, 500);
		port.addAudioListener(tracker);
		ChannelCandidate candidate = new ChannelCandidate(name, channel);
		tracker.addProsodyListener(candidate);
		candidates.add(candidate);
	}

	public static MicrophoneConfiguration configureStereoMicrophones() throws Exception {
		MicrophoneConfiguration config = new MicrophoneConfiguration();
		System.out.println("Configuring stereo microphones");
		config.startConfiguration(2);
		System.out.println("Talk into the left microphone");
		while (true) {
			try {
				config.listen(0, 5000);
				break;
			} catch (ConfigurationException e) {
				System.out.println(e.getMessage());
				System.out.println("Talk into the left microphone again");
			}
		}
		System.out.println("Talk into the right microphone");
		while (true) {
			try {
				config.listen(1, 5000);
				break;
			} catch (ConfigurationException e) {
				System.out.println(e.getMessage());
				System.out.println("Talk into the right microphone again");
			}
		}
		config.build();
		System.out.println("Left microphone: " + config.channels.get(0).device + ", channel: " + config.channels.get(0).channel + ", speech level: " + config.channels.get(0).speechMean);
		System.out.println("Right microphone: " + config.channels.get(1).device + ", channel: " + config.channels.get(1).channel + ", speech level: " + config.channels.get(1).speechMean);
		return config;
	}
	
	public static MicrophoneConfiguration configureMonoMicrophone() throws Exception {
		MicrophoneConfiguration config = new MicrophoneConfiguration();
		System.out.println("Configuring mono microphone");
		config.startConfiguration(1);
		System.out.println("Talk into the microphone");
		while (true) {
			try {
				config.listen(0, 5000);
				break;
			} catch (ConfigurationException e) {
				System.out.println(e.getMessage());
				System.out.println("Talk into the microphone again");
			}
		}
		config.build();
		System.out.println("Microphone: " + config.channels.get(0).device + ", channel: " + config.channels.get(0).channel + ", speech level: " + config.channels.get(0).speechMean);
		return config;
	}

	private void build() throws ConfigurationException {
		if (nChannels == 1) {
			Double maxMean = Double.MIN_VALUE;
			for (ChannelCandidate cand : candidates) {
				Double mean = cand.getMeanEnergy(0);
				if (mean == -1)
					continue;
				if (mean > maxMean) {
					channels.clear();
					channels.add(new ChannelConfiguration(cand.device, cand.channel, mean.intValue()));
					maxMean = mean;
				}
			}
			if (channels.size() != 1)
				throw new ConfigurationException("Cannot find any microphone with speech");
		} if (nChannels == 2) {
			Double maxMean = Double.MIN_VALUE;
			for (ChannelCandidate cand0 : candidates) {
				for (ChannelCandidate cand1 : candidates) {
					if (cand0 == cand1)
						continue;
					Double mean1 = cand0.getMeanEnergy(0);
					Double mean2 = cand1.getMeanEnergy(0);
					Double mean3 = cand0.getMeanEnergy(1);
					Double mean4 = cand1.getMeanEnergy(1);
					//System.out.println(cand0.device + " " + mean1 + "/" + mean3 + "  " + cand1.device + " " + mean4 + "/" + mean3);
					if (mean1 == -1 || mean4 == -1)
						continue;
					double mean = (mean1 - mean2) + (mean4 - mean3);
					if (mean > maxMean) {
						channels.clear();
						channels.add(new ChannelConfiguration(cand0.device, cand0.channel, mean1.intValue()));
						channels.add(new ChannelConfiguration(cand1.device, cand1.channel, mean4.intValue()));
						maxMean = mean;
					}
				}
			}
			if (channels.size() != 2)
				throw new ConfigurationException("Cannot differentiate sound level between 2 microphones");
		}
	}

	private void listen(int channel, int time) throws ConfigurationException {
		if (channel >= nChannels)
			throw new IllegalArgumentException("Cannot configure channel " + channel + ", configuring " + nChannels + " channels");
		listeningChannel = channel;
		listening = true;
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		listening = false;
		for (ChannelCandidate cand : candidates) {
			Double mean = cand.getMeanEnergy(channel);
			if (mean != -1)
				return;
		}
		throw new IllegalArgumentException("No speech detected"); 
	}
	
	public boolean isDone() {
		for (ChannelConfiguration config : channels) {
			if (config == null)
				return false;
		}
		return true;
	}
	
	public static class ConfigurationException extends Exception {

		public ConfigurationException(String msg) {
			super(msg);
		}
		
	}
	
	public static class ChannelConfiguration extends Record {

		@RecordField
		public String device;
		@RecordField
		public int channel;
		@RecordField
		public int speechMean;

		public ChannelConfiguration(String device, int channel, int speechMean) {
			this.device = device;
			this.channel = channel;
			this.speechMean = speechMean;
		}
		
	}

	public static void main(String[] args) throws Exception {
		ArgParser argParser = new ArgParser();
		argParser.addOptionalArg("t", "Type of microphones", "mono|stereo", String.class, "mono");
		argParser.addOptionalArg("o", "Output file", "filename", String.class, "mic.config");
		argParser.parse(args);
		String type = argParser.get("t").toString();
		MicrophoneConfiguration config;
		if (type.equals("stereo")) {
			config = configureStereoMicrophones();
		} else {
			config = configureMonoMicrophone();
		}
		File outFile = new File(argParser.get("o").toString());
		Utils.writeTextFile(outFile, config.toJSON().toString());
		System.out.println("Written configuration to " + outFile.getAbsolutePath());
		System.exit(0);
	}
	
}
