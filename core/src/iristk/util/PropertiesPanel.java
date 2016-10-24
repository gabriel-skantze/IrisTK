package iristk.util;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PropertiesPanel extends JPanel {

	private Record record;
	private HashMap<String,JComponent> components = new HashMap<>();
	private List<PropertiesListener> listeners = new ArrayList<>();

	public PropertiesPanel(Record rec) {
		this(rec, null);
	}
	
	public PropertiesPanel(Record rec, Map<String,String[]> options) {
		super(new GridBagLayout());
		//setBackground(Color.red);
		this.record = rec;
		int n = 0;
		for (final String field : record.getFieldsOrdered()) {
			JLabel label = new JLabel(field);
			Object value = record.get(field);
			final JComponent input;
			if (options != null && options.containsKey(field)) {
				String[] olist = options.get(field);
				input = new JComboBox<String>(olist);
				((JComboBox)input).setSelectedItem(value);
				((JComboBox)input).addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						record.put(field, ((JComboBox)input).getSelectedItem());
						propertyChanged(field, ((JComboBox)input).getSelectedItem());
					}
				});
			} else if (value instanceof Boolean) {
				input = new JCheckBox();
				((JCheckBox)input).setSelected((boolean) value);
				((JCheckBox)input).addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Object value = ((JCheckBox)input).isSelected();
						record.put(field, value);
						propertyChanged(field, value);
					}
				});
			} else {
				input = new JTextField(record.getString(field));
				//((JTextField)input).setPreferredSize(new Dimension(100,20));
				((JTextField)input).getDocument().addDocumentListener(new DocumentListener() {
					  @Override
					public void changedUpdate(DocumentEvent e) {
						    update();
						  }
						  @Override
						public void removeUpdate(DocumentEvent e) {
							  update();
						  }
						  @Override
						public void insertUpdate(DocumentEvent e) {
							  update();
						  }

						  public void update() {
							  String text = ((JTextField)input).getText().trim();
							  if (record.get(field) instanceof Integer) {
								  try {
									  int v = Integer.parseInt(text);
									  record.put(field, v);
									  propertyChanged(field, v);
								  } catch (NumberFormatException e) {
								  }
							  } else if (record.get(field) instanceof String) {
								  record.put(field, text);
								  propertyChanged(field, text);
							  }
						  }
						});
			} 
			label.setAlignmentX(0);
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = n;
			c.insets = new Insets(5, 5, 5, 5);
			//c.anchor = GridBagConstraints.FIRST_LINE_START;
			add(label, c);
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = n;
			c.weightx = 1;
			c.weighty = 1;
			c.insets = new Insets(5, 5, 5, 5);
			add(input, c);
			components.put(field, input);
			n++;
		}
	}
	
	@Override
	public void setEnabled(boolean b) {
		for (JComponent component : components.values()) {
			component.setEnabled(b);
		}
	}
	
	protected synchronized void propertyChanged(String field, Object value) {
		for (PropertiesListener listener : listeners) {
			listener.propertyChanged(field, value);
		}
	}

	public synchronized void addListener(PropertiesListener listener) {
		listeners.add(listener);
	}
	
	public static interface PropertiesListener {

		void propertyChanged(String field, Object value);
		
	}
	
	public void setRecord(Record rec) {
		this.record = rec;
		for (String field : record.getFields()) {
			if (components.containsKey(field)) {
				JComponent component = components.get(field);
				if (component instanceof JTextField) {
					((JTextField)component).setText(record.getString(field));
				} else if (component instanceof JCheckBox) {
					((JCheckBox)component).setSelected(record.getBoolean(field));
				} else if (component instanceof JComboBox) {
					((JComboBox<String>)component).setSelectedItem(record.getString(field));
				}
			}
		}
	}

	public void setProperty(String name, Object value) {
		Object old = record.get(name);
		if (old instanceof Boolean && value instanceof Boolean) {
			JCheckBox check = (JCheckBox)components.get(name);
			if (check != null) {
				check.setSelected((Boolean)value);
				record.put(name, value);
				propertyChanged(name, value);
			}
		}
	}
	
}
