package iristk.app.$situated_dialog$;

import java.io.File;

import org.slf4j.Logger;

import iristk.cfg.SRGSGrammar;
import iristk.furhat.skill.FlowResource;
import iristk.furhat.skill.Skill;
import iristk.furhat.skill.SkillHandler;
import iristk.furhat.skill.TextFileResource;
import iristk.furhat.skill.XmlResource;
import iristk.speech.OpenVocabularyContext;
import iristk.speech.SemanticGrammarContext;
import iristk.speech.SpeechGrammarContext;
import iristk.system.IrisUtils;
import iristk.util.Language;
import iristk.util.Record;

public class $Situated_dialog$Skill extends Skill {

	private static final String RECOGNIZER_GRAMMAR = "grammar";
	private static final String RECOGNIZER_OPEN = "open";
	
	private static Logger logger = IrisUtils.getLogger($Situated_dialog$Skill.class); 
	
	private $Situated_dialog$Flow flow;
	private File propertiesFile;
	private String name = "$Situated_dialog$Skill";
	private Language language = Language.ENGLISH_US;
	private String recognizer = "grammar";

	public $Situated_dialog$Skill() {
		this.propertiesFile = getPackageFile("skill.properties");
		addResource(new TextFileResource(this, "Properties", propertiesFile));
		try {
			Record config = Record.fromProperties(propertiesFile);
			name = config.getString("name", name);
			language = new Language(config.getString("language", language.getCode()));
			recognizer = config.getString("recognizer", recognizer);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		addResource(new FlowResource(this, "Flow", getSrcFile("$Situated_dialog$Flow.xml")));
		addResource(new XmlResource(this, "Grammar", getPackageFile("$Situated_dialog$Grammar.xml")));
		getRequirements().setLanguage(language);
		getRequirements().setSpeechGrammar(recognizer.equals(RECOGNIZER_GRAMMAR));
		getRequirements().setOpenVocabulary(recognizer.equals(RECOGNIZER_OPEN));
		addEntriesFromFlow($Situated_dialog$Flow.class, () -> flow);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void init(SkillHandler handler) throws Exception {
		if (recognizer.equals(RECOGNIZER_GRAMMAR))  {
			handler.loadContext("default", new SpeechGrammarContext(new SRGSGrammar(getPackageFile("$Situated_dialog$Grammar.xml"))));
			handler.setDefaultContext("default");
		} else if (recognizer.equals(RECOGNIZER_OPEN)) {
			handler.loadContext("default", new OpenVocabularyContext(language));
			handler.loadContext("default", new SemanticGrammarContext(new SRGSGrammar(getPackageFile("$Situated_dialog$Grammar.xml"))));
			handler.setDefaultContext("default");
		}
		flow = new $Situated_dialog$Flow(handler.getSystemAgentFlow());
	}

	@Override
	public void stop(SkillHandler handler) throws Exception {
	}

}
