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
		JSONArray keywords = new JSONArray();
		try {
			for(SemanticKeyword semanticKeyword : semanticKeywordVector) {
				result.put("keyword", semanticKeyword.getKeyword());
				result.put("type", semanticKeyword.getType());
				result.put("confidence", semanticKeyword.getConfidence());
				result.put("num_occurences", semanticKeyword.getNumOccurrences());
				result.put("tf", semanticKeyword.getTf());
			}
			result.append("success", keywords);
		} catch (JSONException e) {
			System.err.println(e.getMessage());
		} finally {
			return result;
		}
	}

	public static Vector<SemanticKeyword> processLanguage(String text, String langModelsPath, String langStopwordPath) throws HomerException {
	
		AsciiTextDocument textDocument = new AsciiTextDocument(text);
		textDocument.autoSetLanguage(langModelsPath, langStopwordPath);
		Vector<SemanticKeyword> v = new Vector<SemanticKeyword>();
		SemanticKeyword sw = new SemanticKeyword(textDocument.getLanguage().toString(), (float) 1.0, KeywordType.LANGUAGE, 0.0);
		v.add(sw);
		return v;
	}
}
