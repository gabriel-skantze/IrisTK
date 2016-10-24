package iristk.cfg;

import iristk.util.ResourceList;
import iristk.xml.XmlUtils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.xml.bind.JAXBException;

public class GrammarWorkshop {

	JFrame window;
	HashMap<String,SRGSGrammar> grammars = new HashMap<>();
	HashMap<String,Parser> parsers = new HashMap<>();
	private JTextField addSentenceField;
	private JTextArea phraseGrammar;
	
	private Table<Phrase> phraseTable = new Table<Phrase>(new Phrase()){
		@Override
		protected void rowSelected(Phrase phrase) {
			showPhrase(phrase);
		}
	};
	
	private Table<Example> exampleTable = new Table<Example>(new Example());
	private Table<Example> sentenceTable;

	public GrammarWorkshop() {
		findGrammars();

		window = new JFrame();
		window.setTitle("IrisTK Grammar Workshop");
		
		JPanel sentencePane = new JPanel(new BorderLayout());
		
		sentenceTable = new Table<Example>(new Example()){
			@Override
			protected void rowSelected(Example example) {
				parseImport(example.phrase);
			}
		};
		sentencePane.add(sentenceTable, BorderLayout.CENTER);
		
		JPanel addSentencePane = new JPanel(new BorderLayout());
		addSentenceField = new JTextField();
		addSentencePane.add(addSentenceField, BorderLayout.CENTER);
		JButton addSentenceButton = new JButton("Add");
		addSentenceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sentenceTable.add(new Example(addSentenceField.getText(), ""));
			}
		});
		addSentencePane.add(addSentenceButton, BorderLayout.LINE_END);
		sentencePane.add(addSentencePane, BorderLayout.PAGE_END);
				
		JTabbedPane tabPane = new JTabbedPane();
		
		window.add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, sentencePane, tabPane));
		
		JPanel grammarPane = new JPanel(new BorderLayout());
		JPanel importPane = new JPanel(new BorderLayout());
		
		tabPane.add("Grammar", grammarPane);
		tabPane.add("Import", importPane);

		phraseGrammar = new JTextArea();
		JSplitPane phrasePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(phraseGrammar), new JScrollPane(exampleTable));
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(phraseTable), phrasePane);
		importPane.add(splitPane, BorderLayout.CENTER);

		window.setPreferredSize(new Dimension(1000, 800));
		window.pack();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}

	protected void showPhrase(Phrase phrase) {
		SRGSGrammar grammar = grammars.get(phrase.name);
		SRGSGrammar pGrammar = new SRGSGrammar(grammar.getLanguage()); 
		Object rule = GrammarUtils.getRule(grammar, phrase.ruleId);
		pGrammar.addRule(grammar, rule, false, false);
		for (String deprule : GrammarUtils.getDependentRules(grammar, rule)) {
			pGrammar.addRule(grammar, GrammarUtils.getRule(grammar, deprule), false, false);
		}
		phraseGrammar.setText(XmlUtils.indentXml(pGrammar.toString()));
		showExamples(phrase);
	}

	private void showExamples(Phrase phrase) {
		Grammar grammar = grammars.get(phrase.name);
		Parser parser = parsers.get(phrase.name);
		exampleTable.clear();
		List<Example> examples = new ArrayList<>();
		HashSet<String> exset = new HashSet<String>();
		for (int i = 0; i < 20; i++) {
			String exstr = GrammarUtils.generate(grammar, phrase.ruleId);
			if (!exset.contains(exstr)) {
				parser.parse(exstr);
				String sem = "";
				for (Edge edge : parser.getPassiveChart().get(0)) {
					if (edge instanceof RuleEdge) {
						RuleEdge rule = (RuleEdge)edge;
						if (rule.getEnd() == parser.getWords().size() && rule.getRuleId().equals(phrase.ruleId)) {
							sem = rule.getSem() + "";
						}
					}
				}
				Example example = new Example(exstr, sem);
				exampleTable.add(example);
				exset.add(exstr);
			}
		}
		exampleTable.addAll(examples);
	}

	protected void parseImport(String text) {
		HashSet<String> keys = new HashSet<>();
		phraseTable.clear();
		for (String name : parsers.keySet()) {
			Parser parser = parsers.get(name);
			parser.parse(text);
			Chart result = parser.getPassiveChart();
			for (Integer v : result.getVertices()) {
				for (Edge edge : result.get(v)) {
					if (edge instanceof RuleEdge) {
						RuleEdge rule = (RuleEdge)edge;
						String key = name + rule.getRuleId();
						if (!keys.contains(key)) {
							phraseTable.add(new Phrase(name, rule.getRuleId(), rule.getWordString(), rule.getSem().toString()));
							keys.add(key);
						}
					}
				}
			}
		}
	}

	private void findGrammars() {
		Pattern pattern = Pattern.compile(".*\\.(srgs|xml|grammar)");
		final Collection<String> list = ResourceList.getResources(pattern);
		for(final String name : list){
			if (!new File(name).exists())
				continue;
			try {
				SRGSGrammar grammar = new SRGSGrammar(new File(name));
				Parser parser = new Parser();
				//parser.loadGrammar(grammar);
				parser.onlyPublicRules = false;
				grammars.put(name, grammar);
				parsers.put(name,parser);
			} catch (FileNotFoundException e) {
			} catch (JAXBException e) {
			}
		}
	}

	public class Table<T extends TableRow> extends JTable {

		private TableModel<T> tableModel;

		public Table(T proto) {
			super(new TableModel<T>(proto));
			setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
			tableModel = (TableModel<T>) getModel();
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (getSelectedRow() > -1)
						rowSelected(tableModel.rows.get(getSelectedRow()));
				}
			});
		}

		protected void rowSelected(T row) {
		}
		
		public void add(T row) {
			tableModel.rows.add(row);
			tableModel.fireTableStructureChanged();
		}
		
		public void addAll(List<T> rows) {
			tableModel.rows.addAll(rows);
			tableModel.fireTableStructureChanged();
		}

		public void clear() {
			clearSelection();
			tableModel.rows.clear();
			tableModel.fireTableStructureChanged();
		}
		
	}
	
	private class TableModel<T extends TableRow> extends AbstractTableModel {

		private ArrayList<T> rows = new ArrayList<>();
		private TableRow proto;
		
		public TableModel(TableRow proto) {
			this.proto = proto;
		}
		
		@Override
		public String getColumnName(int column) {
		    return proto.headers[column];
		}
		
		@Override
		public int getRowCount() {
			return rows.size();
		}

		@Override
		public int getColumnCount() {
			return proto.headers.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			T row = rows.get(rowIndex);
			return row.columns[columnIndex];
		}

	}
	
	private static abstract class TableRow {

		public Object[] columns;
		public String[] headers;
		
		public String getHeader(int i) {
			return headers[i];
		}
		
		public Object getColumn(int i) {
			return columns[i];
		}
		
	}

	public static class Phrase extends TableRow {

		public String words;
		public String ruleId;
		public String name;
		public String sem;
		
		public Phrase() {
			headers = new String[]{"Phrase", "Semantics", "Grammar", "Rule"};
		}

		public Phrase(String name, String ruleId, String words, String sem) {
			this.name = name;
			this.ruleId = ruleId;
			this.words = words;
			this.sem = sem;
			columns = new Object[]{words, sem, name, ruleId};
		}
		
	}
	
	public static class Example extends TableRow {

		public String phrase;
		public String sem;
		
		public Example() {
			headers = new String[]{"Phrase", "Semantics"};
		}

		public Example(String phrase, String sem) {
			this.phrase = phrase;
			this.sem = sem;
			columns = new Object[]{phrase, sem};
		}
		
	}

	public static void main(String[] args) {
		new GrammarWorkshop();
	}

}
