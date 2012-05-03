package it.unifi.micc.homer.model.language;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unifi.micc.homer.model.TextDocument;
import it.unifi.micc.homer.util.StringOperations;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import net.olivo.lc4j.LanguageCategorization;

public class Lc4jLangIdentifier implements LanguageIdentifier {
	private static String langModelsPath;
	private static String langStopwordPath;

	private LanguageCategorization lc;
	
	public Lc4jLangIdentifier(String modelsPath, String stopwordPath){
		this.lc = new LanguageCategorization();
		this.langModelsPath = modelsPath;
		this.langStopwordPath = stopwordPath;
		lc.setLanguageModelsDir(langModelsPath);
		lc.setMaxLanguages(4);
	}
	

	/* (non-Javadoc)
	 * @see it.unifi.micc.homer.model.language.LanguageIdentifier#identifyLanguageOf(java.lang.String)
	 */
	@Override
	public Language identifyLanguageOf(String text) {
		@SuppressWarnings("rawtypes")
		List res = lc.findLanguage(new BufferedReader(new StringReader(text)));
		if( res == null )
			return Language.unknown;
		if(res.size()==1)
			return getLanguageFromLanguageNameString(res.get(0).toString());
		else if(res.size()>0){ // handle the case with more languages !!
			List<Language> langs = new ArrayList<Language>();
			for(int i=0; i<res.size(); i++)
				langs.add(getLanguageFromLanguageNameString((String)res.get(i)));
			List<Integer> stopwPerLang = new ArrayList<Integer>();
			for (Language language : langs) {
				// count the number of stopwords of each possible language that has been found in the text
				stopwPerLang.add( StringOperations.countStopwords(text, language, langStopwordPath) );
			}
			// the language of the stopword list that caused to find the higher number of stopwords is selected as most probable language
			return langs.get( stopwPerLang.indexOf(Collections.max(stopwPerLang)) );
		}
		return Language.unknown;
	}

	/**
	 * @param res
	 * @return
	 */
	private Language getLanguageFromLanguageNameString(String lang) {
		int barPosition = lang.indexOf("-");
		if(barPosition == -1){
			int dotPosition = lang.indexOf(".");
			if(dotPosition == -1){
				return Language.unknown;
			}
			else {
				lang = lang.substring(0, dotPosition);					
			}
		} else {
			lang = lang.substring(0, barPosition);
		}
		try{
			Language language = Enum.valueOf(Language.class, lang);
			return language;
		} catch (IllegalArgumentException ex) {
			return Language.unknown;
		}
	}


	@Override
	public TextDocument cleanTextDocumentStopwords(TextDocument text) {
		TextDocument result;
		Language lang = text.getLanguage();
		String t = text.getContent();
		if( lang == null )
			lang = identifyLanguageOf(t);
		if( lang == Language.unknown )
			lang = Language.english;
		else
			text.setLanguage(lang);
		result = new TextDocument(StringOperations.removeStopwords(t, lang, langStopwordPath));
		result.setLanguage(text.getLanguage());
		return result;
	}
	
	/**
	 * @return the langModelsPath
	 */
	public static String getLangModelsPath() {
		return langModelsPath;
	}

	/**
	 * @param langModelsPath the langModelsPath to set
	 */
	public static void setLangModelsPath(String langModelsPath) {
		Lc4jLangIdentifier.langModelsPath = langModelsPath;
	}


	/**
	 * @return the langStopwordPath
	 */
	public static String getLangStopwordPath() {
		return langStopwordPath;
	}

	/**
	 * @param langStopwordPath the langStopwordPath to set
	 */
	public static void setLangStopwordPath(String langStopwordPath) {
		Lc4jLangIdentifier.langStopwordPath = langStopwordPath;
	}

}
