package it.unifi.micc.homer;

import it.unifi.micc.homer.model.AsciiTextDocument;
import it.unifi.micc.homer.model.KeywordType;
import it.unifi.micc.homer.model.SemanticKeyword;
import it.unifi.micc.homer.model.language.LanguageDetector;
import it.unifi.micc.homer.model.topic.TopicDetector;
import it.unifi.micc.homer.util.HomerException;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Analyser {

	public static JSONObject topicAnalysisJSON(String text, int numTopics, int numTopWords, String langModelsPath, String langStopwordPath) {
		Vector<SemanticKeyword> semanticKeywordVector = TopicDetector.process(text, numTopics, numTopWords, langModelsPath, langStopwordPath, null);
		JSONObject result = new JSONObject();
		try {
			JSONArray keywords = Analyser.semanticKeywordVector2JSON(semanticKeywordVector);
			result.put("success", keywords);
		} catch (JSONException e) {
			System.err.println(e.getMessage());
		}
		return result;
	}

	public static JSONObject languageDetectionJSON(String text, String langModelsPath, String langStopwordPath) {
		JSONObject result = new JSONObject();
		AsciiTextDocument textDocument = new AsciiTextDocument(text);
		try {
			textDocument.autoSetLanguage(langModelsPath, langStopwordPath);
		} catch (HomerException e1) {
			System.err.println(e1.getMessage());
			return result;
		}
		Vector<SemanticKeyword> v = new Vector<SemanticKeyword>();
		SemanticKeyword sw = new SemanticKeyword(textDocument.getLanguage().toString(), (float) 1.0, KeywordType.LANGUAGE, 0.0);
		v.add(sw);
		try {
			JSONArray keywords = Analyser.semanticKeywordVector2JSON(v);
			result.put("success", keywords);
		} catch (JSONException e) {
			System.err.println(e.getMessage());
		}
		return result;
	}
	
	private static JSONArray semanticKeywordVector2JSON(Vector<SemanticKeyword> semanticKeywordVector) throws JSONException {
		JSONArray keywords = new JSONArray();
		for(SemanticKeyword semanticKeyword : semanticKeywordVector) {
			JSONObject keyword = new JSONObject();
			keyword.put("keyword", semanticKeyword.getKeyword());
			keyword.put("type", semanticKeyword.getType());
			keyword.put("confidence", semanticKeyword.getConfidence());
			keyword.put("num_occurences", semanticKeyword.getNumOccurrences());
			keyword.put("tf", semanticKeyword.getTf());
			keywords.put(keyword);
		}
		return keywords;
	}
}
