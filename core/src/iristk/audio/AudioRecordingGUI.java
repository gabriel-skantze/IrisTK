package iristk.audio;

import iristk.kinect.KinectAudioSource;
import iristk.kinect.KinectV2;
import iristk.system.InitializationException;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

import com.portaudio.DeviceInfo;
import com.portaudio.PortAudio;

public class AudioRecordingGUI extends JFrame {

	private JPanel contentPane;
	private JComboBox audioDevicesList;

	private List<AudioRecorder> audioRecorders = new ArrayList<AudioRecorder>();
	private JList selectedDevicesList;
	private List<String> selectedDevices = new ArrayList<String>();
	private JButton stopRecordingButton;
	private JButton startRecordingButton;
	private JTextField audioDeviceNameField;
	private JButton kinectAddButton;
	private JTextField recordingFolderField;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					AudioRecordingGUI frame = new AudioRecordingGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initializeGUI() throws Exception {
		PortAudioUtil.initialize();
		for (int i = 0; i < PortAudio.getDeviceCount(); i++) {
			DeviceInfo deviceInfo = PortAudio.getDeviceInfo(i);
			if (deviceInfo.hostApi == PortAudio.HOST_API_TYPE_DEV && deviceInfo.maxInputChannels > 0) {
				audioDevicesList.addItem(deviceInfo.name);
			}
		}
	}
	
	/**
	 * Create the frame.
	 * @throws Exception 
	 */
	public AudioRecordingGUI() throws Exception {
		 UIManager.setLookAndFeel(
		            UIManager.getSystemLookAndFeelClassName());
		
		setResizable(false);
		setTitle("Audio Recorder");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 545, 330);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblAudioDevices = new JLabel("Audio Devices");
		lblAudioDevices.setBounds(10, 11, 83, 19);
		contentPane.add(lblAudioDevices);
		
		JLabel lblKinect = new JLabel("Kinect");
		lblKinect.setBounds(10, 73, 46, 14);
		contentPane.add(lblKinect);
		
		kinectAddButton = new JButton("Add");
		kinectAddButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				kinectAddButton.setEnabled(false);
				addKinect();
			}
		});
		kinectAddButton.setBounds(107, 69, 89, 23);
		contentPane.add(kinectAddButton);
		
		selectedDevicesList = new JList();
		selectedDevicesList.setBounds(107, 103, 373, 121);
		contentPane.add(selectedDevicesList);
		
		audioDevicesList = new JComboBox();
		audioDevicesList.setBounds(103, 12, 377, 19);
		audioDevicesList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				audioDeviceNameField.setText(audioDevicesList.getSelectedItem().toString());
			}
		});
		contentPane.add(audioDevicesList);
		
		JLabel lblSelectedDevices = new JLabel("Selected Devices");
		lblSelectedDevices.setBounds(10, 104, 83, 14);
		contentPane.add(lblSelectedDevices);
		
		startRecordingButton = new JButton("Start Recording");
		startRecordingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startRecording();
			}
		});
		startRecordingButton.setBounds(10, 269, 124, 23);
		contentPane.add(startRecordingButton);
		
		JButton addMonoButton = new JButton("Add Mono");
		addMonoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addAudioDevice(1);
			}
		});
		addMonoButton.setBounds(293, 41, 89, 23);
		contentPane.add(addMonoButton);
		
		JButton addStereoButton = new JButton("Add Stereo");
		addStereoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addAudioDevice(2);
			}
		});
		addStereoButton.setBounds(391, 41, 89, 23);
		contentPane.add(addStereoButton);
		
		recordingFolderField = new JTextField("c:\\recordings");
		recordingFolderField.setBounds(110, 235, 272, 20);
		contentPane.add(recordingFolderField);
		recordingFolderField.setColumns(10);
		
		JLabel lblRecordingFolder = new JLabel("Recording Folder");
		lblRecordingFolder.setBounds(10, 238, 83, 14);
		contentPane.add(lblRecordingFolder);
		
		JButton chooseFolderButton = new JButton("Choose...");
		chooseFolderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Select target directory");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = chooser.showOpenDialog(AudioRecordingGUI.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					recordingFolderField.setText(chooser.getSelectedFile().getAbsolutePath());
				}
				
			}
		});
		chooseFolderButton.setBounds(391, 235, 89, 23);
		contentPane.add(chooseFolderButton);
		
		stopRecordingButton = new JButton("Stop Recording");
		stopRecordingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopRecording();
			}
		});
		stopRecordingButton.setEnabled(false);
		stopRecordingButton.setBounds(144, 269, 124, 23);
		contentPane.add(stopRecordingButton);
		
		audioDeviceNameField = new JTextField();
		audioDeviceNameField.setBounds(107, 42, 176, 20);
		contentPane.add(audioDeviceNameField);
		audioDeviceNameField.setColumns(10);
		
		initializeGUI();
	}

	protected void stopRecording() {
		for (AudioRecorder audioRecorder : audioRecorders) {
			audioRecorder.stopRecording();
		}
		startRecordingButton.setEnabled(true);
		stopRecordingButton.setEnabled(false);
	}

	protected void startRecording() {
		long timestamp = System.currentTimeMillis();
		new File(recordingFolderField.getText()).mkdirs();
		for (int i = 0; i < audioRecorders.size(); i++) {
			AudioRecorder audioRecorder = audioRecorders.get(i);
			String name = selectedDevices.get(i).toString();
			audioRecorder.startRecording(new File(recordingFolderField.getText() + "/" + timestamp + "." + name + ".wav"));
		}
		startRecordingButton.setEnabled(false);
		stopRecordingButton.setEnabled(true);
	}

	protected void addKinect() {
		KinectV2 kinect = new KinectV2();
		kinect.setManualBeamAngle(0);
		KinectAudioSource kinectAudio = new KinectAudioSource(kinect);
		AudioRecorder recorder = new AudioRecorder(kinectAudio);
		addRecorder("Kinect", recorder);
	}
	
	protected void addAudioDevice(int channels) {
		try {
			addRecorder(audioDeviceNameField.getText(),
					new AudioRecorder(new Microphone(audioDevicesList.getSelectedItem().toString(), 16000, channels)));
		} catch (InitializationException e) {
			e.printStackTrace();
		}
	}

	protected void addRecorder(String name, AudioRecorder recorder) {
		audioRecorders.add(recorder);
		selectedDevices.add(name);
		selectedDevicesList.setListData(selectedDevices.toArray());
	}
}
