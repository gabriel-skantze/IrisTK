package iristk.speech;

import iristk.system.IrisGUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class EnergyVADPanel extends JPanel {

	private LevelBar leftLevelBar;
	private LevelBar rightLevelBar;
	private EnergyVAD leftVAD;
	private EnergyVAD rightVAD;
	//private JCheckBox adaptiveCheckbox;
	private JTextField speechLevelField;
	
	public EnergyVADPanel() {
		super(new BorderLayout());
		leftLevelBar = new LevelBar(0);
		rightLevelBar = new LevelBar(1);
		
		GridLayout layout = new GridLayout(1, 2);
		layout.setHgap(20);
		JPanel bars = new JPanel(layout);
		bars.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		bars.add(rightLevelBar);
		bars.add(leftLevelBar);
		add(bars);

		//JPanel controls = new JPanel(new GridLayout(3, 3));
		JPanel controls = new JPanel(new FlowLayout());
	
		/*
		adaptiveCheckbox = new JCheckBox("Adaptive");
		adaptiveCheckbox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				setAdaptive(adaptiveCheckbox.isSelected());
				repaint();
			}
		});
		if (!editable)
			adaptiveCheckbox.setEnabled(false);
			*/
		
		speechLevelField = new JTextField("", 5);
		speechLevelField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSpeechLevel(Integer.parseInt(speechLevelField.getText()));
				repaint();
			}
		});

	//	controls.add(adaptiveCheckbox);
		controls.add(new JLabel("Speech level: "));
		controls.add(speechLevelField);
		controls.add(new JPanel());
		
		JButton saveButton = new JButton("Update");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSpeechLevel(Integer.parseInt(speechLevelField.getText()));
				repaint();
			}
		});
		controls.add(saveButton);
		add(controls, BorderLayout.PAGE_END);
		
	}
	
	public void addToGUI(IrisGUI gui) {
		gui.addDockPanel("vad", "VAD", this, false);
	}

	/*
	protected void setAdaptive(boolean b) {
		speechLevelField.setEditable(!b);
		if (leftVAD != null)
			leftVAD.adaptSpeechLevel.set(b);
		if (rightVAD != null)
			rightVAD.adaptSpeechLevel.set(b);
	}
	*/

	protected void setSpeechLevel(int level) {
		if (leftVAD != null)
			leftVAD.speechLevel.set(level);
		if (rightVAD != null)
			rightVAD.speechLevel.set(level);
		speechLevelField.setText(level + "");
	}
	
	private class LevelBar extends JPanel implements EnergyVAD.Listener {

		private boolean inSpeech;
		private int energy;
		private int channel;

		public LevelBar(int channel) {
			this.channel = channel;
		}
		
		@Override
		public void vadEvent(long streamPos, boolean inSpeech, int energy) {
			this.inSpeech = inSpeech;
			this.energy = energy;
			repaint();
		}
		
		private int getY(int energy) {
			return (int) (getHeight() * (1.0 - (energy / 100.0)));
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), getHeight());
			int y = getY(energy);
			if (inSpeech)
				g.setColor(Color.ORANGE);
			else
				g.setColor(Color.GREEN);
			if (channel == 0 && leftVAD != null || channel == 1 && rightVAD != null) {
				g.fillRect(0, y, getWidth(), getHeight() - y);
				EnergyVAD vad = channel == 0 ? leftVAD : rightVAD;
				g.setColor(Color.white);
				y = getY(vad.getSilenceLevel());
				g.drawLine(0, y, getWidth(), y);
				y = getY(vad.speechLevel.get());
				g.drawLine(0, y, getWidth(), y);
				if (vad.adaptSpeechLevel.get())
					speechLevelField.setText("" + vad.speechLevel.get());
			}
		}

		public void clear() {
			energy = 0;
			repaint();
		}
		
	}
	
	private void setFormValues() {
		speechLevelField.setText("" + leftVAD.speechLevel.get());
	//	adaptiveCheckbox.setSelected(leftVAD.adaptSpeechLevel.get());
		//speechLevelField.setEditable(!leftVAD.adaptSpeechLevel.get());
	}

	public void setLeftVAD(EnergyVAD vad) {
		this.leftVAD = vad;
		leftVAD.addVADListener(leftLevelBar);
		setFormValues();
	}
	
	public void setRightVAD(EnergyVAD vad) {
		this.rightVAD = vad;
		if (rightVAD != null)
			rightVAD.addVADListener(rightLevelBar);
	}
}
