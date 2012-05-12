package it.unifi.micc.homer;

import it.unifi.micc.homer.model.SemanticKeyword;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Analyser {

	public static JSONObject topicAnalysisJSON(String text, int numTopics, int numTopWords, String langModelsPath, String langStopwordPath) {
		Vector<SemanticKeyword> semanticKeywordVector = TopicDetectionAnalyser.process(text, numTopics, numTopWords, langModelsPath, langStopwordPath, null);
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
}
