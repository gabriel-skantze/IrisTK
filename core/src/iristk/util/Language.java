/*******************************************************************************
 * Copyright (c) 2014 Gabriel Skantze.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Gabriel Skantze - initial API and implementation
 ******************************************************************************/
package iristk.util;

import java.util.HashMap;
import java.util.Map;

public class Language extends Record {

	public static final Language AFRIKAANS = new Language("af-ZA");
	public static final Language INDONESIAN = new Language("id-ID");
	public static final Language MALAY = new Language("ms-MY");
	public static final Language CATALAN = new Language("ca-ES");
	public static final Language CZECH = new Language("cs-CZ");
	public static final Language DANISH = new Language("da-DK");
	public static final Language GERMAN = new Language("de-DE");
	public static final Language ENGLISH_AU = new Language("en-AU");
	public static final Language ENGLISH_CA = new Language("en-CA");
	public static final Language ENGLISH_GB = new Language("en-GB");
	public static final Language ENGLISH_IN = new Language("en-IN");
	public static final Language ENGLISH_IE = new Language("en-IE");
	public static final Language ENGLISH_NZ = new Language("en-NZ");
	public static final Language ENGLISH_PH = new Language("en-PH");
	public static final Language ENGLISH_ZA = new Language("en-ZA");
	public static final Language ENGLISH_US = new Language("en-US");
	public static final Language SPANISH_AR = new Language("es-AR");
	public static final Language SPANISH_BO = new Language("es-BO");
	public static final Language SPANISH_CL = new Language("es-CL");
	public static final Language SPANISH_CO = new Language("es-CO");
	public static final Language SPANISH_CR = new Language("es-CR");
	public static final Language SPANISH_EC = new Language("es-EC");
	public static final Language SPANISH_SV = new Language("es-SV");
	public static final Language SPANISH_ES = new Language("es-ES");
	public static final Language SPANISH_US = new Language("es-US");
	public static final Language SPANISH_GT = new Language("es-GT");
	public static final Language SPANISH_HN = new Language("es-HN");
	public static final Language SPANISH_MX = new Language("es-MX");
	public static final Language SPANISH_NI = new Language("es-NI");
	public static final Language SPANISH_PA = new Language("es-PA");
	public static final Language SPANISH_PY = new Language("es-PY");
	public static final Language SPANISH_PE = new Language("es-PE");
	public static final Language SPANISH_PR = new Language("es-PR");
	public static final Language SPANISH_DO = new Language("es-DO");
	public static final Language SPANISH_UY = new Language("es-UY");
	public static final Language SPANISH_VE = new Language("es-VE");
	public static final Language BASQUE = new Language("eu-ES");
	public static final Language FRENCH = new Language("fr-FR");
	public static final Language GALICIAN = new Language("gl-ES");
	public static final Language CROATIAN = new Language("hr-HR");
	public static final Language ZULU = new Language("zu-ZA");
	public static final Language ICELANDIC = new Language("is-IS");
	public static final Language ITALIAN = new Language("it-IT");
	public static final Language LITHUANIAN = new Language("lt-LT");
	public static final Language HUNGARIAN = new Language("hu-HU");
	public static final Language DUTCH = new Language("nl-NL");
	public static final Language NORWEGIAN = new Language("nb-NO");
	public static final Language POLISH = new Language("pl-PL");
	public static final Language PORTUGUESE_BR = new Language("pt-BR");
	public static final Language PORTUGUESE_PT = new Language("pt-PT");
	public static final Language ROMANIAN = new Language("ro-RO");
	public static final Language SLOVAK = new Language("sk-SK");
	public static final Language SLOVENIAN = new Language("sl-SI");
	public static final Language FINNISH = new Language("fi-FI");
	public static final Language SWEDISH = new Language("sv-SE");
	public static final Language VIETNAMESE = new Language("vi-VN");
	public static final Language TURKISH = new Language("tr-TR");
	public static final Language GREEK = new Language("el-GR");
	public static final Language BULGARIAN = new Language("bg-BG");
	public static final Language RUSSIAN = new Language("ru-RU");
	public static final Language SERBIAN = new Language("sr-RS");
	public static final Language UKRAINIAN = new Language("uk-UA");
	public static final Language HEBREW = new Language("he-IL");
	public static final Language ARABIC_IL = new Language("ar-IL");
	public static final Language ARABIC_JO = new Language("ar-JO");
	public static final Language ARABIC_AE = new Language("ar-AE");
	public static final Language ARABIC_BH = new Language("ar-BH");
	public static final Language ARABIC_DZ = new Language("ar-DZ");
	public static final Language ARABIC_SA = new Language("ar-SA");
	public static final Language ARABIC_IQ = new Language("ar-IQ");
	public static final Language ARABIC_KW = new Language("ar-KW");
	public static final Language ARABIC_MA = new Language("ar-MA");
	public static final Language ARABIC_TN = new Language("ar-TN");
	public static final Language ARABIC_OM = new Language("ar-OM");
	public static final Language ARABIC_PS = new Language("ar-PS");
	public static final Language ARABIC_QA = new Language("ar-QA");
	public static final Language ARABIC_LB = new Language("ar-LB");
	public static final Language ARABIC_EG = new Language("ar-EG");
	public static final Language PERSIAN = new Language("fa-IR");
	public static final Language HINDI = new Language("hi-IN");
	public static final Language THAI = new Language("th-TH");
	public static final Language KOREAN = new Language("ko-KR");
	public static final Language JAPANESE = new Language("ja-JP");

	private static Map<String,String> names = new HashMap<>();
	
	static {
		names.put("af-ZA","Afrikaans (South Africa)");
		names.put("id-ID","Indonesian (Indonesia)");
		names.put("ms-MY","Malay (Malaysia)");
		names.put("ca-ES","Catalan (Spain)");
		names.put("cs-CZ","Czech (Czech Republic)");
		names.put("da-DK","Danish (Denmark)");
		names.put("de-DE","German (Germany)");
		names.put("en-AU","English (Australia)");
		names.put("en-CA","English (Canada)");
		names.put("en-GB","English (United Kingdom)");
		names.put("en-IN","English (India)");
		names.put("en-IE","English (Ireland)");
		names.put("en-NZ","English (New Zealand)");
		names.put("en-PH","English (Philippines)");
		names.put("en-ZA","English (South Africa)");
		names.put("en-US","English (United States)");
		names.put("es-AR","Spanish (Argentina)");
		names.put("es-BO","Spanish (Bolivia)");
		names.put("es-CL","Spanish (Chile)");
		names.put("es-CO","Spanish (Colombia)");
		names.put("es-CR","Spanish (Costa Rica)");
		names.put("es-EC","Spanish (Ecuador)");
		names.put("es-SV","Spanish (El Salvador)");
		names.put("es-ES","Spanish (Spain)");
		names.put("es-US","Spanish (United States)");
		names.put("es-GT","Spanish (Guatemala)");
		names.put("es-HN","Spanish (Honduras)");
		names.put("es-MX","Spanish (Mexico)");
		names.put("es-NI","Spanish (Nicaragua)");
		names.put("es-PA","Spanish (Panama)");
		names.put("es-PY","Spanish (Paraguay)");
		names.put("es-PE","Spanish (Peru)");
		names.put("es-PR","Spanish (Puerto Rico)");
		names.put("es-DO","Spanish (Dominican Republic)");
		names.put("es-UY","Spanish (Uruguay)");
		names.put("es-VE","Spanish (Venezuela)");
		names.put("eu-ES","Basque (Spain)");
		names.put("fr-FR","French (France)");
		names.put("gl-ES","Galician (Spain)");
		names.put("hr-HR","Croatian (Croatia)");
		names.put("zu-ZA","Zulu (South Africa)");
		names.put("is-IS","Icelandic (Iceland)");
		names.put("it-IT","Italian (Italy)");
		names.put("lt-LT","Lithuanian (Lithuania)");
		names.put("hu-HU","Hungarian (Hungary)");
		names.put("nl-NL","Dutch (Netherlands)");
		names.put("nb-NO","Norwegian Bokm�l (Norway)");
		names.put("pl-PL","Polish (Poland)");
		names.put("pt-BR","Portuguese (Brazil)");
		names.put("pt-PT","Portuguese (Portugal)");
		names.put("ro-RO","Romanian (Romania)");
		names.put("sk-SK","Slovak (Slovakia)");
		names.put("sl-SI","Slovenian (Slovenia)");
		names.put("fi-FI","Finnish (Finland)");
		names.put("sv-SE","Swedish (Sweden)");
		names.put("vi-VN","Vietnamese (Vietnam)");
		names.put("tr-TR","Turkish (Turkey)");
		names.put("el-GR","Greek (Greece)");
		names.put("bg-BG","Bulgarian (Bulgaria)");
		names.put("ru-RU","Russian (Russia)");
		names.put("sr-RS","Serbian (Serbia)");
		names.put("uk-UA","Ukrainian (Ukraine)");
		names.put("he-IL","Hebrew (Israel)");
		names.put("ar-IL","Arabic (Israel)");
		names.put("ar-JO","Arabic (Jordan)");
		names.put("ar-AE","Arabic (United Arab Emirates)");
		names.put("ar-BH","Arabic (Bahrain)");
		names.put("ar-DZ","Arabic (Algeria)");
		names.put("ar-SA","Arabic (Saudi Arabia)");
		names.put("ar-IQ","Arabic (Iraq)");
		names.put("ar-KW","Arabic (Kuwait)");
		names.put("ar-MA","Arabic (Morocco)");
		names.put("ar-TN","Arabic (Tunisia)");
		names.put("ar-OM","Arabic (Oman)");
		names.put("ar-PS","Arabic (State of Palestine)");
		names.put("ar-QA","Arabic (Qatar)");
		names.put("ar-LB","Arabic (Lebanon)");
		names.put("ar-EG","Arabic (Egypt)");
		names.put("fa-IR","Persian (Iran)");
		names.put("hi-IN","Hindi (India)");
		names.put("th-TH","Thai (Thailand)");
		names.put("ko-KR","Korean (South Korea)");
		names.put("ja-JP","Japanese (Japan)");

	}
	
	@RecordField
	private String code;
	
	private Language() {
	}
	
	public Language(String code) {
		code=code.trim();
		if (!code.toLowerCase().matches("[a-z][a-z]-[a-z][a-z]"))
			throw new RuntimeException("Bad language code: " + code);
		if (!code.matches("[a-z][a-z]-[A-Z][A-Z]")) {
			code = code.substring(0, 2).toLowerCase() + "-" + code.substring(3, 5).toUpperCase();
		}
		//if (!names.containsKey(code))
		//	throw new RuntimeException("Bad language code: " + code);
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	@Override
	public String toString() {
		return code;
	}
	
	@Override
	public boolean equals(Object lang) {
		if (lang instanceof Language) {
			return this.code.equalsIgnoreCase(((Language)lang).getCode());
		} else {
			return false;
		}
	}
	
	public boolean equalsIgnoreDialect(Object lang) {
		if (lang instanceof Language) {
			return this.getMain().equalsIgnoreCase(((Language)lang).getMain());
		} else {
			return false;
		}
	}
	
	/**
	 * @return The main part of the code (such as "en")
	 */
	public String getMain() {
		return getCode().substring(0, 2);
	}
	
	public String getName() {
		return names.get(code);
	}

	public static String[] getCodes() {
		return names.keySet().toArray(new String[0]);
	}
	
}
