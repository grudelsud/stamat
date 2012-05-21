package it.unifi.micc.homer;

import it.unifi.micc.homer.model.AsciiTextDocument;
import it.unifi.micc.homer.model.KeywordType;
import it.unifi.micc.homer.model.SemanticKeyword;
import it.unifi.micc.homer.model.namedentity.AnnieNEDDetector;
import it.unifi.micc.homer.model.namedentity.NamedEntity;
import it.unifi.micc.homer.model.namedentity.NamedEntityDetector;
import it.unifi.micc.homer.model.namedentity.StanfordNERecognizer;
import it.unifi.micc.homer.model.topic.Topic;
import it.unifi.micc.homer.model.topic.TopicDetector;
import it.unifi.micc.homer.model.topic.TopicWord;
import it.unifi.micc.homer.util.WordCounter;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author alisi
 *
 */
public class Analyser {

	public static JSONObject entityExtractStanford(String text, String classifierPath) {
		JSONObject result = new JSONObject();
		NamedEntityDetector ned = StanfordNERecognizer.getInstance(classifierPath);
		ArrayList<NamedEntity> entityList = ned.extractEntity(text, null);

		Vector<SemanticKeyword> semanticKeywordVector = namedEntityList2semanticKeywordList(text, entityList);
		JSONArray entities = Analyser.semanticKeywordList2JSON(semanticKeywordVector);
		try {
			result.put("success", entities);
		} catch (JSONException e) {
			System.err.println(e.getMessage());
		}
		return result;
	}

	public static JSONObject entityExractAnnie(String text, ArrayList<KeywordType> keywordTypes, String gateHome) {
		JSONObject result = new JSONObject();
		NamedEntityDetector ned = AnnieNEDDetector.getInstance(gateHome);
		ArrayList<NamedEntity> entityList = ned.extractEntity(text, keywordTypes);	//returns entities without repetitions
		
		Vector<SemanticKeyword> semanticKeywordVector = namedEntityList2semanticKeywordList(text, entityList);
		JSONArray entities = Analyser.semanticKeywordList2JSON(semanticKeywordVector);
		try {
			result.put("success", entities);
		} catch (JSONException e) {
			System.err.println(e.getMessage());
		}
		return result;
	}

	public static JSONObject topicExtractJSON(List<String> texts, int numTopics, int numTopWords, String langModelsPath, String langStopwordPath)
	{
		JSONObject result = new JSONObject();
		try {
			Vector<SemanticKeyword> semanticKeywordVector = TopicDetector.extract(texts, langModelsPath, langStopwordPath, numTopics, numTopWords);
			JSONArray keywords = Analyser.semanticKeywordList2JSON(semanticKeywordVector);
			result.put("success", keywords);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return result;
	}

	public static JSONObject topicExtractJSON(List<String> texts, int numTopics, int numTopWords, String langModelsPath, String langStopwordPath, String ldaModelPath) 
	{
		JSONObject result = new JSONObject();
		try {
			Vector<SemanticKeyword> semanticKeywordVector = TopicDetector.extract(texts, langModelsPath, langStopwordPath, numTopics, numTopWords, ldaModelPath);
			JSONArray keywords = Analyser.semanticKeywordList2JSON(semanticKeywordVector);
			result.put("success", keywords);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return result;
	}

	public static JSONObject topicInferJSON(List<String> texts, String langModelsPath, String langStopwordPath, int numTopWords, String ldaModelPath)
	{
		JSONObject result = new JSONObject();
		try {
			List<Topic> topics = TopicDetector.infer(texts, langModelsPath, langStopwordPath, numTopWords, ldaModelPath);
			JSONArray topicsJSON = Analyser.topicList2JSON(topics);
			result.put("success", topicsJSON);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		return result;
	}

	public static JSONObject trainModelJSON(List<String> trainingTexts, int numTopics, String langModelsPath, String langStopwordPath, String ldaModelPath) 
	{
		JSONObject result = new JSONObject();
		try {
			TopicDetector.train(trainingTexts, numTopics, langModelsPath, langStopwordPath, ldaModelPath);
			result.put("success", "all good");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return result;
	}

	public static JSONObject languageDetectionJSON(String text, String langModelsPath, String langStopwordPath) 
	{
		JSONObject result = new JSONObject();
		try {
			AsciiTextDocument textDocument = new AsciiTextDocument(text);
			Vector<SemanticKeyword> semanticKeywordVector = new Vector<SemanticKeyword>();
			textDocument.autoSetLanguage(langModelsPath, langStopwordPath);
			SemanticKeyword sw = new SemanticKeyword(textDocument.getLanguage().toString(), (float) 1.0, KeywordType.LANGUAGE, 0.0);
			semanticKeywordVector.add(sw);
			JSONArray keywords = Analyser.semanticKeywordList2JSON(semanticKeywordVector);
			result.put("success", keywords);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return result;
	}
	
	private static JSONArray topicList2JSON(List<Topic> topicList) 
	{
		JSONArray topicsJSON = new JSONArray();
		for(Topic topic : topicList) {
			JSONObject topicJSON = new JSONObject();
			JSONArray wordsJSON = new JSONArray();
			try {
				List<TopicWord> words = topic.getWords();
				for(TopicWord word : words) {
					JSONObject wordJSON = new JSONObject();
					wordJSON.put("word", word.getWord());
					wordJSON.put("count", word.getCount());
					wordJSON.put("weight", word.getWeight());
					wordsJSON.put(wordJSON);
				}
				topicJSON.put("words", wordsJSON);
				topicJSON.put("alpha", topic.getAlpha());
				topicJSON.put("language", topic.getLanguage());
				topicsJSON.put(topicJSON);
			} catch(Exception e) {
				System.err.println(e.getMessage());
			}
		}
		return topicsJSON;
	}

	private static JSONArray semanticKeywordList2JSON(List<SemanticKeyword> semanticKeywordList) 
	{
		JSONArray keywordsJSON = new JSONArray();
		for(SemanticKeyword semanticKeyword : semanticKeywordList) {
			JSONObject semanticKeywordJSON = new JSONObject();
			try {
				semanticKeywordJSON.put("keyword", semanticKeyword.getKeyword());
				semanticKeywordJSON.put("type", semanticKeyword.getType());
				semanticKeywordJSON.put("confidence", semanticKeyword.getConfidence());
				semanticKeywordJSON.put("num_occurences", semanticKeyword.getNumOccurrences());
				semanticKeywordJSON.put("tf", semanticKeyword.getTf());
				keywordsJSON.put(semanticKeywordJSON);				
			} catch(Exception e) {
				System.err.println(e.getMessage());
			}
		}
		return keywordsJSON;
	}

	private static Vector<SemanticKeyword> namedEntityList2semanticKeywordList(String text, ArrayList<NamedEntity> result) 
	{
		Vector<SemanticKeyword> v = new Vector<SemanticKeyword>(result.size());
		int docSize = WordCounter.countWords(text);
		for( NamedEntity an : result ) {
			SemanticKeyword as = new SemanticKeyword(an.getValue(), (float) 1.0, an.getType(), 0.0);
			as.setNumOccurrences(WordCounter.countWordInstances(text, as.getKeyword().trim()));
			as.setTf((float)((float)as.getNumOccurrences()/(float)docSize));
			if(as.getTf()==0)	// the regex of countWordInstances compute the presence of only separated words:
				as.setTf(1);	// if entity has some symbols next to it then it won't be counted	 
			v.add(as);
		}
		return v;
	}
}
