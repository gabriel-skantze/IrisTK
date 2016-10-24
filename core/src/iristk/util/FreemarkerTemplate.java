package iristk.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class FreemarkerTemplate {

	private Template template;

	public FreemarkerTemplate(String name, InputStream is) {
		Configuration freemarker = new Configuration(Configuration.VERSION_2_3_21);
		freemarker.setDefaultEncoding("UTF-8");
		freemarker.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
		try {
			template = new Template(name, Utils.readString(is), freemarker);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String process(Record data) throws TemplateException, IOException {
		StringWriter sw = new StringWriter();
		template.process(data, sw);
		return sw.toString();
	}
	
}
