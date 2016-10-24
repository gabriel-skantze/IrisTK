package iristk.audio;

import iristk.speech.EnergyVAD;
import iristk.speech.EnergyVADPanel;
import iristk.system.InitializationException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import com.portaudio.DeviceInfo;

public class TestMicrophone {

	JFrame window;
	private DefaultComboBoxModel<String> deviceListModel;
	private JComboBox<String> deviceList;
	private Microphone mic;
	private EnergyVADPanel vadPanel;
	
	public TestMicrophone() {
		window = new JFrame("Test Microphone");

		vadPanel = new EnergyVADPanel();
		window.getContentPane().add(vadPanel);
		
		/*
		JButton calibButton = new JButton("Calibrate");
		calibButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Calibrate();
			}
		});
		controls.add(calibButton);
		*/
		//controls.add(new JPanel());
		
		deviceListModel = new DefaultComboBoxModel<String>();
		deviceList = new JComboBox<>(deviceListModel);
		
		for (String device : Microphone.getDevices())
			deviceListModel.addElement(device);
		
		deviceList.setSelectedItem(Microphone.getDefaultDevice());
		setDevice(Microphone.getDefaultDevice());
		
		window.getContentPane().add(deviceList, BorderLayout.PAGE_START);
		deviceList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setDevice(deviceList.getSelectedItem().toString());
			}
		});
		
		window.setSize(new Dimension(300,400));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
	
	/*
	
	private enum State {SILENCE, SPEECH, NONSPEECH};
	
	private class Calibrate implements VADListener {
	
		private JLabel message;
		
		private JButton okButton;
		private JFrame calibW;
		
		List<Integer> micLevels = new ArrayList<Integer>();
		List<Integer> nonLevels = new ArrayList<Integer>();
		
		private State state = State.SILENCE;
		
		public  Calibrate() {
			calibW = new JFrame("Calibrate microphone");
			message = new JLabel();
			calibW.add(message);
			okButton = new JButton("OK");
			okButton.setEnabled(false);
			okButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					calibW.setVisible(false);
				}
			});
			calibW.add(okButton, BorderLayout.PAGE_END);
			
			calibW.setSize(new Dimension(400, 400));
			calibW.setVisible(true);
			new Thread() {
				@Override
				public void run() {
					step1();
				};
			}.start();
		}
		
		private void step1() {
			message.setText("Please be silent for 5 seconds");
			try {
				adaptive.setSelected(true);
				Thread.sleep(5000);
				adaptive.setSelected(false);
				step2();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		private void step2() {
			message.setText("Please talk into the (left) microphone for 5 seconds");
			try {
				leftVAD.addVADListener(this);
				state = State.SPEECH;
				Thread.sleep(5000);
				state = State.SILENCE;
				step3();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		private void step3() {
			message.setText("Prepare to talk off-mic");
			try {
				Thread.sleep(5000);
				step4();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		private void step4() {
			message.setText("Please talk off-mic for 5 seconds");
			try {
				state = State.NONSPEECH;
				Thread.sleep(5000);
				state = State.SILENCE;
				step5();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		private void step5() {
			Collections.sort(nonLevels);
			Collections.reverse(nonLevels);
			nonLevels = nonLevels.subList(0, 100);
			int maxNonmicEnergy = (int) Utils.mean(nonLevels);
			Collections.sort(micLevels);
			Collections.reverse(micLevels);
			micLevels = micLevels.subList(0, 100);
			int maxMicEnergy = (int) Utils.mean(micLevels);
			if (maxNonmicEnergy > maxMicEnergy) {
				message.setText("Calibration failed, were you talking in the right microphone?");
			} else if (maxMicEnergy - maxNonmicEnergy < 5) {
				message.setText("Calibration failed, could not discriminate microphone");
			} else {
				System.out.println(maxMicEnergy);
				System.out.println(maxNonmicEnergy);
				int deltaSpeech = ((maxMicEnergy - maxNonmicEnergy) / 2 + maxNonmicEnergy) - getSilenceLevel();
				setDeltaSpeech(deltaSpeech);
				setDeltaSil(deltaSpeech / 2);
				message.setText("Calibration successful");
			}
			okButton.setEnabled(true);
		}

		@Override
		public void vadEvent(long streamPos, boolean inSpeech, int energy) {
			if (state == State.SPEECH)
				micLevels.add(energy);
			else if (state == State.NONSPEECH)
				nonLevels.add(energy);
		}
	
	}
	
	*/
	
	
	protected void setDevice(String deviceName) {
		if (mic != null)
			mic.stop();
		DeviceInfo deviceInfo = Microphone.getDeviceInfo(deviceName);
		if (deviceInfo.maxInputChannels > 1) {
			try {
				mic = new Microphone(deviceName, 16000, 2);
				AudioChannel leftChannel = new AudioChannel(mic, 0);
				AudioChannel rightChannel = new AudioChannel(mic, 1);

				EnergyVAD leftVAD = new EnergyVAD(leftChannel);
				vadPanel.setLeftVAD(leftVAD);
				EnergyVAD rightVAD = new EnergyVAD(rightChannel);
				vadPanel.setRightVAD(leftVAD);
			} catch (InitializationException e) {
				e.printStackTrace();
			}
		} else {
			try {
				mic = new Microphone(deviceName, 16000, 1);
				EnergyVAD leftVAD = new EnergyVAD(mic);
				vadPanel.setLeftVAD(leftVAD);
				vadPanel.setRightVAD(null);
			} catch (InitializationException e) {
				e.printStackTrace();
			}
		}
	}


	public static void main(String[] args) {
		new TestMicrophone();
	}
	
}

