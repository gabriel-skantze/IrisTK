package iristk.speech;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.apache.batik.util.gui.xmleditor.XMLTextEditor;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import iristk.audio.Microphone;
import iristk.audio.Sound;
import iristk.cfg.ABNFGrammar;
import iristk.cfg.ParseResult;
import iristk.cfg.Parser;
import iristk.cfg.SRGSGrammar;
import iristk.project.Project;
import iristk.system.Event;
import iristk.system.EventListener;
import iristk.system.IrisGUI;
import iristk.system.IrisUtils;
import iristk.system.SimpleDialogSystem;
import iristk.util.FileDrop;
import iristk.util.Language;
import iristk.util.PropertiesPanel;
import iristk.util.PropertiesPanel.PropertiesListener;
import iristk.util.Record;
import iristk.util.Utils;
import iristk.xml.XmlUtils;

public class TestRecognizer implements EventListener {

	private static org.slf4j.Logger logger = IrisUtils.getLogger(TestRecognizer.class);
	
	private IrisGUI gui;
	private SpeechGrammarPanel speechGrammarPanel;
	private SemanticGrammarPanel semanticGrammarPanel;
	private RecognizerPanel recognizerPanel;
	private List<RecognizerFactory> recognizerFactories = new ArrayList<>();
	private Microphone microphone;
	private RecognizerModule recModule;
	private SimpleDialogSystem system;
	private OutputPanel outputPanel;
	private boolean listening;
	private JButton listenButton;
	private Set<Integer> loadedOpen = new HashSet<>();
	private HashMap<Recognizer, Settings> settings = new HashMap<>();
	public PropertiesPanel properties;
	private BatchPanel batchPanel;

	private Parser parser = new Parser();

	public TestRecognizer() throws Exception {
		system = new SimpleDialogSystem(this.getClass());
		gui = new IrisGUI(system);
		
		parser.activateGrammar("default");
		
		microphone = new Microphone();

		for (String factoryClass : Project.main.getPackageProvides("iristk.speech.RecognizerFactory")) {
			RecognizerFactory factory = (RecognizerFactory) Class.forName(factoryClass).newInstance();
			try {
				factory.checkSupported();
				recognizerFactories.add(factory);
			} catch (RecognizerException re) {
				logger.info("Recognizer " + factory.getName() + " not supported : " + re.getMessage());
			}
		}

		speechGrammarPanel = new SpeechGrammarPanel();
		speechGrammarPanel.setText(SIMPLE_GRAMMAR);
		semanticGrammarPanel = new SemanticGrammarPanel();
		semanticGrammarPanel.setText(SIMPLE_GRAMMAR);
		recognizerPanel = new RecognizerPanel();
		outputPanel = new OutputPanel();
		gui.addDockPanel("Speech-Grammar", "Speech Grammar",
				speechGrammarPanel, true);
		gui.addDockPanel("Semantic-Grammar", "Semantic Grammar",
				semanticGrammarPanel, true);
		gui.addDockPanel("Recognizer", "Recognizer", recognizerPanel, true);
		gui.addDockPanel("Output", "Output", outputPanel, true);

		batchPanel = new BatchPanel();
		gui.addDockPanel("Batch", "Batch", batchPanel, true);
		
		system.addEventListener(this);

		Logger.getRootLogger().addAppender(new AppenderSkeleton() {
			
			@Override
			public boolean requiresLayout() {
				return false;
			}
			
			@Override
			public void close() {
			}
			
			@Override
			protected void append(LoggingEvent event) {
				if (event.getLevel().isGreaterOrEqual(Priority.WARN))
					outputPanel.append(event.getLoggerName() + ": " + event.getMessage());
			}
		});
		/*
		setErrorHandler(new ErrorHandler() {
			@Override
			public void handle(Exception e, String component) {
				outputPanel.append(component + ": " + e.getMessage());
			}
		});*/

		system.sendStartSignal();
	}

	private class BatchPanel extends JPanel {
		
		private JList fileList;
		private DefaultListModel<String> listModel;
		private JButton recButton;

		public BatchPanel() {
			super(new BorderLayout());
			listModel = new DefaultListModel<>();
			fileList = new JList(listModel);
			add(new JScrollPane(fileList));
			fileList.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2 && fileList.getSelectedValue() != null) {
						File file = new File(fileList.getSelectedValue().toString());
						try {
							Sound s = new Sound(file);
							s.playAsync();
						} catch (UnsupportedAudioFileException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						
					}
				}
			});
			JPanel controls = new JPanel(new FlowLayout());
			add(controls, BorderLayout.PAGE_END);
			JButton addButton = new JButton("Add...");
			controls.add(addButton);
			addButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fc = new JFileChooser();
					fc.setMultiSelectionEnabled(true);
					fc.setFileFilter(new FileFilter() {
						@Override
						public String getDescription() {
							return "WAV files";
						}
						@Override
						public boolean accept(File f) {
							return f.isDirectory() || (f.getName().endsWith(".wav")); 
						}
					});
					int returnVal = fc.showOpenDialog(BatchPanel.this);
					 if (returnVal == JFileChooser.APPROVE_OPTION) {
			            for (File file : fc.getSelectedFiles()) {
			            	if (!listModel.contains(file.getAbsolutePath()))
			            		listModel.add(listModel.size(), file.getAbsolutePath());
			            }
			        }
				}
			});
			JButton removeButton = new JButton("Remove");
			controls.add(removeButton);
			removeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					for (Object selected : fileList.getSelectedValuesList()) {
						listModel.removeElement(selected);
					}
				}
			});
			recButton = new JButton("Recognize");
			controls.add(recButton);
			recButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (recModule != null && fileList.getSelectedIndices().length > 0) {
						new Thread() {
							@Override
							public void run() {
								recButton.setEnabled(false);
								Settings s = settings.get(recModule.getRecognizer());
								recModule.setPartialResults(s.partialResults);
								recModule.getRecognizer().setNbestLength(s.nBestLength);
								for (Object selected : fileList.getSelectedValuesList()) {
									try {
										RecResult result = recModule.getRecognizer().recognizeFile(new File(selected.toString()));
										//if (parser.getGrammars().size() > 0)
										//	result.sem = parser.parse(result.text).getSem();
										outputPanel.append(selected + "\n" + result.toStringIndent());
									} catch (RecognizerException e1) {
										logger.error("Recognition failed", e1);
									}
								}
								recButton.setEnabled(true);
							};
						
						}.start();
					}
				}
			});
		}
		
	}
	
	private class RecognizerPanel extends JPanel {

		private JComboBox<String> recList;
		private HashMap<Class, Recognizer> recognizers = new HashMap<>();

		public RecognizerPanel() {
			super(new BorderLayout());

			JPanel controls = new JPanel();
			add(controls, BorderLayout.CENTER);
			controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

			ArrayList<String> flist = new ArrayList<>();
			flist.add("");
			for (RecognizerFactory fact : recognizerFactories) {
				flist.add(fact.getName());
			}
			recList = new JComboBox<String>(flist.toArray(new String[0]));
			// recList.setPreferredSize(new Dimension(10,10));
			recList.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setRecognizer();
				}
			});
			// recList.setMaximumSize( recList.getPreferredSize() );
			recList.setAlignmentX(0);
			
			JPanel top = new JPanel();
			top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
			top.add(recList);
			
			add(top, BorderLayout.PAGE_START);

			Map<String, String[]> options = new HashMap<>();
			options.put(
					"language",
					new String[] { Language.ENGLISH_US.getCode(),
							Language.ENGLISH_GB.getCode(),
							Language.GERMAN.getCode(),
							Language.SWEDISH.getCode(),
							Language.TURKISH.getCode()});
			Settings s = new Settings();
			properties = new PropertiesPanel(s, options);
			properties.setEnabled(false);
			properties.addListener(new MyPropertiesListener());
			controls.add(properties);

			listenButton = new JButton("Listen");
			// listenButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 10,
			// 10));
			add(listenButton, BorderLayout.PAGE_END);
			listenButton.addActionListener(new ActionListener() {
				private String actionId;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (recModule != null) {
						Settings s = settings.get(recModule.getRecognizer());
						String context = "";
						if (speechGrammarPanel.useGrammarCheck.isSelected()) {
							context += " speechGrammar";
						}
						if (s.useOpen) {
							context += " open";
						}
						if (semanticGrammarPanel.useCheck.isSelected()) {
							context += " semanticGrammar";
						}
						context = context.trim();
						if (context.length() == 0)
							return;
						if (!listening) {
							recModule.setPartialResults(s.partialResults);
							//currentRec.setLanguage(new Language(s.language));
							Event event = new Event("action.listen");
							event.put("endSilTimeout", s.endSilTimeout);
							event.put("noSpeechTimeout", s.noSpeechTimeout);
							event.put("maxSpeechTimeout", s.maxSpeechTimeout);
							event.put("nbest", s.nBestLength);
							event.put("context", context);
							system.send(event, "test");
							actionId = event.getId();
						} else {
							Event event = new Event("action.listen.stop");
							event.put("action", actionId);
							system.send(event, "test");
						}
					}
				}
			});

		}

		private synchronized void setRecognizer() {
			try {
				int selected = recList.getSelectedIndex();
				if (selected != 0) {
					RecognizerFactory factory = recognizerFactories.get(selected - 1);
					if (!recognizers.containsKey(factory.getClass())) {
						Recognizer rec = factory.newRecognizer(microphone);
						//rec.addRecognizerListener(new ProsodyRecognizerListener(microphone.getAudioFormat()), RecognizerListeners.PRIORITY_PROSODY);
						if (recModule == null) {
							properties.setEnabled(true);
							recModule = new RecognizerModule(rec);
							system.addModule(recModule);
						} else {
							recModule.setRecognizer(rec);
						}
						recognizers.put(factory.getClass(), rec);
						Settings s = new Settings();
						settings.put(rec, s);
						properties.setRecord(s);
						if (factory.supportsOpenVocabulary() && !factory.supportsSpeechGrammar())
							properties.setProperty("useOpen", true);
					} else {
						Recognizer rec = recognizers.get(factory.getClass());
						properties.setRecord(settings.get(rec));
						recModule.setRecognizer(rec);
					}
				}
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error("Error initializing " + recList.getSelectedItem(), e);
				recList.setSelectedItem("");
				//recModule = null;
			}
		}

	}
	
	private class MyPropertiesListener implements PropertiesListener {

		@Override
		public void propertyChanged(String field, Object value) {
			if (field.equals("useOpen") && ((Boolean)value)) {
				checkLoadOpen();
			}
			if (field.equals("language")) {
				checkLoadOpen();
			}
		}
		
	}

	private class SpeechGrammarPanel extends GrammarPanel {

		public JCheckBox useGrammarCheck;
		//public JCheckBox useOpenCheck;
		
		public SpeechGrammarPanel() {
			super();
			useGrammarCheck = new JCheckBox("Use grammar");
			buttons.add(useGrammarCheck);
			//useOpenCheck = new JCheckBox("Use open");
			//buttons.add(useOpenCheck);
		}
		
		@Override
		protected void loadGrammar() throws Exception {
			system.unloadContext("speechGrammar");
			system.loadContext("speechGrammar", new SpeechGrammarContext(getAsSRGS()));
			useGrammarCheck.setSelected(true);
		}

	}

	private class SemanticGrammarPanel extends GrammarPanel {

		private JTextField input;
		public JCheckBox useCheck;

		public SemanticGrammarPanel() {
			super();
			JPanel panel = new JPanel();
			add(panel, BorderLayout.PAGE_END);
			input = new JTextField();
			input.setPreferredSize(new Dimension(250, (int) input
					.getPreferredSize().getHeight()));
			panel.add(input);
			JButton button = new JButton("Parse");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ParseResult result = parser.parse(input.getText());
					outputPanel.append(result.toString());
				}
			});
			panel.add(button);
			
			useCheck = new JCheckBox("Use grammar");
			buttons.add(useCheck);
		}
		
		@Override
		protected void loadGrammar() throws Exception {
			SRGSGrammar srgs = new SRGSGrammar(getAsSRGS());
			system.unloadContext("semanticGrammar");
			system.loadContext("semanticGrammar", new SemanticGrammarContext(srgs));
			parser.loadGrammar("default", srgs);
			useCheck.setSelected(true);
		}

	}

	private abstract class GrammarPanel extends JPanel {

		private XMLTextEditor editor;
		private File file;
		private JButton saveButton;
		JComboBox<String> modes;
		protected JPanel buttons;

		public GrammarPanel() {
			super(new BorderLayout());
			editor = new XMLTextEditor();
		    editor.addKeyListener(new KeyListener() {
	            @Override
	            public void keyTyped(KeyEvent e) {
	            }
	            @Override
	            public void keyPressed(KeyEvent e) {
	                if ((e.getKeyCode() == KeyEvent.VK_Z) && ((e.getModifiers() & InputEvent.CTRL_MASK) != 0)) {
	                    editor.undo();
	                } else if ((e.getKeyCode() == KeyEvent.VK_Y) && ((e.getModifiers() & InputEvent.CTRL_MASK) != 0)) {
	                    editor.redo();
	                } 
	            }
	            @Override
	            public void keyReleased(KeyEvent e) {
	            }
	        });
			add(new JScrollPane(editor));
			buttons = new JPanel(new FlowLayout());
			modes = new JComboBox<>(new String[]{"SRGS", "ABNF"});
			modes.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (modes.getSelectedItem().equals("ABNF")) {
						// SRGS -> ABNF
						try {
							editor.setText(new ABNFGrammar(new SRGSGrammar(editor.getText())).toString());
							file = null;
						} catch (Exception e1) {
							modes.setSelectedItem("SRGS");
							outputPanel.append(e1.getMessage());
						}
					} else {
						// ABNF -> SRGS
						try {
							editor.setText(XmlUtils.indentXml(new SRGSGrammar(new ABNFGrammar(editor.getText())).toString()));
							file = null;
						} catch (Exception e1) {
							modes.setSelectedItem("ABNF");
							outputPanel.append(e1.getMessage());
						}
					}
				}
			});
			buttons.add(modes);
			saveButton = new JButton("Save");
			saveButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					save();
				}
			});
			buttons.add(saveButton);
			saveButton.setEnabled(false);
			JButton loadButton = new JButton("Load");
			loadButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						loadGrammar();
					} catch (Exception ex) {
						outputPanel.append(ex.getMessage());
					}
				}
			});
			buttons.add(loadButton);
			add(buttons, BorderLayout.PAGE_START);

			new FileDrop(editor, new FileDrop.Listener() {
				@Override
				public void filesDropped(java.io.File[] files) {
					load(files[0]);
				}
			});
		}
		
		protected void save() {
			if (file != null)
				try {
					Utils.writeTextFile(file, getText());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
		}

		private void load(File file) {
			try {
				setText(Utils.readTextFile(file));
				this.file = file;
				saveButton.setText("Save " + file.getName());
				saveButton.setEnabled(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public String getText() {
			return editor.getText();
		}

		public String getAsSRGS() throws Exception {
			if (modes.getSelectedItem().equals("SRGS"))
				return editor.getText();
			else
				return new SRGSGrammar(new ABNFGrammar(editor.getText())).toString();
		}

		public void setText(String text) {
			editor.setText(text);
		}

		protected abstract void loadGrammar() throws Exception;

	}

	private class OutputPanel extends JPanel {

		private JTextArea textArea;

		public OutputPanel() {
			super(new BorderLayout());
			textArea = new JTextArea();
			add(new JScrollPane(textArea));
			textArea.setFont(new Font("Courier", Font.PLAIN, 12));
			textArea.setTabSize(3);
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
		}

		public void append(String string) {
			textArea.append(string + "\n\n");
			textArea.setCaretPosition(textArea.getDocument().getLength());
		}

	}

	@Override
	public void onEvent(Event event) {
		if (event.triggers("sense.speech.rec**")) {
			outputPanel.append(event.getEventParams().toStringIndent());
			listening = false;
			listenButton.setText("Listen");
		} else if (event.triggers("sense.speech.partial")) {
			outputPanel.append(event.getEventParams().toStringIndent());
		} else if (event.triggers("sense.speech.start")) {
			listenButton.setText("Start of speech... (Stop)");
		} else if (event.triggers("sense.speech.end")) {
			listenButton.setText("End of speech... (Stop)");
		} else if (event.triggers("monitor.listen.start")) {
			listening = true;
			listenButton.setText("Listening... (Stop)");
		} else if (event.triggers("monitor.context.load")) {
			String context = event.getString("context") + " (" + event.getString("language") + ")";
			if (event.getBoolean("success"))
				outputPanel.append(event.getSender() + ": " + context + " loaded");
			else
				outputPanel.append(event.getSender() + ": " + context + " loading FAILED: " + event.getString("message"));
		}
	}

	public void checkLoadOpen() {
		Settings s = settings.get(recModule.getRecognizer());
		if (s.useOpen) {
			int key = recModule.getRecognizer().hashCode() + s.language.hashCode();
			if (!loadedOpen.contains(key)) {
				loadedOpen.add(key);
				system.loadContext("open", new OpenVocabularyContext(new Language(s.language)));
			}
		}
	}

	public static class Settings extends Record {

		@RecordField(order = 1)
		public String language = Language.ENGLISH_US.getCode();
		@RecordField(order = 2)
		public boolean useOpen = false;
		@RecordField(order = 3)
		public int noSpeechTimeout = 5000;
		@RecordField(order = 4)
		public int endSilTimeout = 500;
		@RecordField(order = 5)
		public int maxSpeechTimeout = 10000;
		@RecordField(order = 6)
		public int nBestLength = 1;
		@RecordField(order = 7)
		public boolean partialResults = false;

	}

	public static void main(String[] args) throws Exception {
		new TestRecognizer();
	}

	private static final String SIMPLE_GRAMMAR = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<grammar xml:lang=\"en-US\" version=\"1.0\" root=\"root\"\n"
			+ "	xmlns=\"http://www.w3.org/2001/06/grammar\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
			+ "	xsi:schemaLocation=\"http://www.w3.org/2001/06/grammar http://www.w3.org/TR/speech-grammar/grammar.xsd\" tag-format=\"semantics/1.0\">\n"
			+ "\n"
			+ "	<rule id=\"root\" scope=\"public\">\n"
			+ "		<ruleref uri=\"#fruit\"/>\n"
			+ "		<tag>out.fruit=rules.fruit</tag>\n"
			+ "	</rule>\n"
			+ "	\n"
			+ "	<rule id=\"fruit\">\n"
			+ "		<one-of>\n"
			+ "			<item>banana</item>\n"
			+ "			<item>orange</item>\n"
			+ "			<item>apple</item>\n"
			+ "		</one-of>\n"
			+ "	</rule>\n"
			+ "	\n" + "</grammar>";

}
