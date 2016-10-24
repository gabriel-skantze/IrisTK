package iristk.speech.nuance9;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.regex.Matcher;

import iristk.cfg.ParseResult;
import iristk.cfg.ParseResult.Phrase;
import iristk.cfg.Parser;
import iristk.cfg.SRGSGrammar;
import iristk.cfg.Word;
import iristk.util.Record;
import iristk.util.Replacer;
import iristk.util.Utils;

public class ParseClasses {

	public static void run(final File grammar, File training, File output) throws Exception {
		final Parser parser = new Parser();
		parser.loadGrammar("grammar", new SRGSGrammar(grammar));
		parser.activateGrammar("grammar");
		String lines = Utils.readTextFile(training);
		PrintStream writer = new PrintStream(new FileOutputStream(output), true, "UTF-8");
		final HashSet<String> rulerefs = new HashSet<>();
		String result = new Replacer("<sentence>(.*?)</sentence>") {
			@Override
			public String replace(Matcher matcher) {
				String text = matcher.group(1);
				ParseResult res = parser.parse(text);
				String repl = "";
				for (Phrase phrase : res) {
					if (phrase.getRuleId() != null) {
						repl += " <ruleref uri=\"" + grammar.getName() + "#" + phrase.getRuleId() + "\" words=\"" + string(phrase) + "\" />";
						rulerefs.add("<ruleref uri=\"" + grammar.getName() + "#" + phrase.getRuleId() + "\"/>");
					} else {
						repl += " " + string(phrase);
					}
				}
				System.out.println(repl.trim());
				String str = "<sentence>" + repl.trim() + "</sentence>";
				Record sem = res.getSem();
				if (sem != null && sem.size() > 0) {
					str += " <!-- " + sem.toString() + " -->";
				}
				return str;
			}
		}.replaceAll(lines);
		String rfs = "";
		for (String ruleref : rulerefs) {
			rfs += "\n" + ruleref;
		}
		result = result.replace("<vocab>", "<vocab>" + rfs);
		writer.print(result);
	}

	protected static String string(Phrase phrase) {
		String res = "";
		for (Word word : phrase.getWords()) {
			res += word.getWordString() + " ";
		}
		return res.trim();
	}
	
}
