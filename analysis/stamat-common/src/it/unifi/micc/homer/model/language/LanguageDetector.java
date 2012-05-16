package it.unifi.micc.homer.model.language;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

import it.unifi.micc.homer.model.TextDocument;
import it.unifi.micc.homer.util.HomerException;
import it.unifi.micc.homer.util.StringOperations;

public class LanguageDetector implements LanguageIdentifier {

	private static String langModelsPath;
	private static String langStopwordPath;
	private static LanguageDetector instance = null;

	private LanguageDetector(String modelsPath, String stopwordPath) throws LangDetectException {
		LanguageDetector.langModelsPath = modelsPath;
		LanguageDetector.langStopwordPath = stopwordPath;
		
		DetectorFactory.loadProfile(modelsPath);
		LanguageDetector.instance = this;
	}
	
	public static LanguageDetector getInstance(String modelsPath, String stopwordPath) throws HomerException {
		if(LanguageDetector.instance == null) {
			try {
				LanguageDetector.instance = new LanguageDetector(modelsPath, stopwordPath);
			} catch (LangDetectException e) {
				throw new HomerException(e);
			}
		}
		return LanguageDetector.instance;
	}

	public String identifyLanguageOf(String text) {
		try {
			Detector detector = DetectorFactory.create();
			detector.append(text);
			return detector.detect();
		} catch (LangDetectException e) {
			return "unknown";
		}
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
		String lang = text.getLanguage();
		String t = text.getContent();
		if( lang == null )
			lang = identifyLanguageOf(t);
		if( lang == "unknown" )
			lang = "english";
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
		LanguageDetector.langModelsPath = langModelsPath;
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
		LanguageDetector.langStopwordPath = langStopwordPath;
	}

}
