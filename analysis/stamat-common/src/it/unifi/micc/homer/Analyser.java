package it.unifi.micc.homer;

import it.unifi.micc.homer.controller.namedentity.AnnieNEDDetector;
import it.unifi.micc.homer.controller.namedentity.NamedEntityDetector;
import it.unifi.micc.homer.controller.namedentity.StanfordNERecognizer;
import it.unifi.micc.homer.controller.topic.TopicDetector;
import it.unifi.micc.homer.model.AsciiTextDocument;
import it.unifi.micc.homer.model.KeywordType;
import it.unifi.micc.homer.model.NamedEntity;
import it.unifi.micc.homer.model.SemanticKeyword;
import it.unifi.micc.homer.model.Topic;
import it.unifi.micc.homer.model.TopicWord;
import it.unifi.micc.homer.util.HomerException;
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

	/**
	 * @param text
	 * @param classifierPath
	 * @return
	 */
	public static ArrayList<NamedEntity> entityExtractStanford(String text, String classifierPath)
	{
		NamedEntityDetector ned = StanfordNERecognizer.getInstance(classifierPath);
		ArrayList<NamedEntity> entityList = ned.extractEntity(text, null);
		return evalTFs(text, entityList);
	}

	/**
	 * @param text
	 * @param classifierPath
	 * @return
	 */
	public static JSONObject entityExtractStanfordJSON(String text, String classifierPath)
	{
		JSONObject result = new JSONObject();
		JSONArray entities = Analyser.semanticKeywordList2JSON(Analyser.entityExtractStanford(text, classifierPath));
		try {
			result.put("success", entities);
		} catch (JSONException e) {
			System.err.println(e.getMessage());
		}
		return result;
	}

	/**
	 * @param text
	 * @param keywordTypes
	 * @param gateHome
	 * @return
	 */
	public static ArrayList<NamedEntity> entityExractAnnie(String text, ArrayList<KeywordType> keywordTypes, String gateHome)
	{
		NamedEntityDetector ned = AnnieNEDDetector.getInstance(gateHome);
		ArrayList<NamedEntity> entityList = ned.extractEntity(text, keywordTypes);	//returns entities without repetitions		
		return evalTFs(text, entityList);
	}

	/**
	 * @param text
	 * @param keywordTypes
	 * @param gateHome
	 * @return
	 */
	public static JSONObject entityExractAnnieJSON(String text, ArrayList<KeywordType> keywordTypes, String gateHome)
	{
		JSONObject result = new JSONObject();
		JSONArray entities = Analyser.semanticKeywordList2JSON(Analyser.entityExractAnnie(text, keywordTypes, gateHome));
		try {
			result.put("success", entities);
		} catch (JSONException e) {
			System.err.println(e.getMessage());
		}
		return result;
	}

	/**
	 * @param texts
	 * @param numTopics
	 * @param numTopWords
	 * @param langModelsPath
	 * @param langStopwordPath
	 * @return
	 * @throws Exception
	 */
	public static Vector<SemanticKeyword> topicExtract(List<String> texts, int numTopics, int numTopWords, String langModelsPath, String langStopwordPath) throws Exception
	{
		Vector<SemanticKeyword> semanticKeywordVector = TopicDetector.extract(texts, langModelsPath, langStopwordPath, numTopics, numTopWords);
		return semanticKeywordVector;
	}

	/**
	 * @param texts
	 * @param numTopics
	 * @param numTopWords
	 * @param langModelsPath
	 * @param langStopwordPath
	 * @return
	 */
	public static JSONObject topicExtractJSON(List<String> texts, int numTopics, int numTopWords, String langModelsPath, String langStopwordPath)
	{
		JSONObject result = new JSONObject();
		try {
			JSONArray keywords = Analyser.semanticKeywordList2JSON(Analyser.topicExtract(texts, numTopics, numTopWords, langModelsPath, langStopwordPath));
			result.put("success", keywords);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return result;
	}

	/**
	 * @param texts
	 * @param numTopics
	 * @param numTopWords
	 * @param langModelsPath
	 * @param langStopwordPath
	 * @param ldaModelPath
	 * @return
	 * @throws Exception
	 */
	public static Vector<SemanticKeyword> topicExtract(List<String> texts, int numTopics, int numTopWords, String langModelsPath, String langStopwordPath, String ldaModelPath) throws Exception 
	{
		Vector<SemanticKeyword> semanticKeywordVector = TopicDetector.extract(texts, langModelsPath, langStopwordPath, numTopics, numTopWords, ldaModelPath);
		return semanticKeywordVector;
	}

	/**
	 * @param texts
	 * @param numTopics
	 * @param numTopWords
	 * @param langModelsPath
	 * @param langStopwordPath
	 * @param ldaModelPath
	 * @return
	 */
	public static JSONObject topicExtractJSON(List<String> texts, int numTopics, int numTopWords, String langModelsPath, String langStopwordPath, String ldaModelPath) 
	{
		JSONObject result = new JSONObject();
		try {
			JSONArray keywords = Analyser.semanticKeywordList2JSON(Analyser.topicExtract(texts, numTopics, numTopWords, langModelsPath, langStopwordPath, ldaModelPath));
			result.put("success", keywords);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return result;
	}

	/**
	 * @param texts
	 * @param langModelsPath
	 * @param langStopwordPath
	 * @param numTopWords
	 * @param ldaModelPath
	 * @return
	 * @throws Exception
	 */
	public static List<Topic> topicInfer(List<String> texts, String langModelsPath, String langStopwordPath, int numTopWords, String ldaModelPath) throws Exception
	{
		List<Topic> topics = TopicDetector.infer(texts, langModelsPath, langStopwordPath, numTopWords, ldaModelPath);
		return topics;
	}

	/**
	 * @param texts
	 * @param langModelsPath
	 * @param langStopwordPath
	 * @param numTopWords
	 * @param ldaModelPath
	 * @return
	 */
	public static JSONObject topicInferJSON(List<String> texts, String langModelsPath, String langStopwordPath, int numTopWords, String ldaModelPath)
	{
		JSONObject result = new JSONObject();
		try {
			JSONArray topicsJSON = Analyser.topicList2JSON(Analyser.topicInfer(texts, langModelsPath, langStopwordPath, numTopWords, ldaModelPath));
			result.put("success", topicsJSON);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		return result;
	}

	/**
	 * @param trainingTexts
	 * @param numTopics
	 * @param langModelsPath
	 * @param langStopwordPath
	 * @param ldaModelPath
	 * @return
	 */
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

	/**
	 * @param text
	 * @param langModelsPath
	 * @param langStopwordPath
	 * @return
	 * @throws Exception
	 */
	public static Vector<SemanticKeyword> languageDetection(String text, String langModelsPath, String langStopwordPath) throws Exception 
	{
		AsciiTextDocument textDocument = new AsciiTextDocument(text);
		Vector<SemanticKeyword> semanticKeywordVector = new Vector<SemanticKeyword>();
		textDocument.autoSetLanguage(langModelsPath, langStopwordPath);
		SemanticKeyword sw = new SemanticKeyword(textDocument.getLanguage().toString(), (float)1.0, KeywordType.LANGUAGE, 1.0);
		semanticKeywordVector.add(sw);
		return semanticKeywordVector;
	}

	/**
	 * @param text
	 * @param langModelsPath
	 * @param langStopwordPath
	 * @return
	 */
	public static JSONObject languageDetectionJSON(String text, String langModelsPath, String langStopwordPath) 
	{
		JSONObject result = new JSONObject();
		try {
			JSONArray keywords = Analyser.semanticKeywordList2JSON(Analyser.languageDetection(text, langModelsPath, langStopwordPath));
			result.put("success", keywords);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return result;
	}
	
	/**
	 * @param topicList
	 * @return
	 */
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

	/**
	 * @param semanticKeywordList
	 * @return
	 */
	private static JSONArray semanticKeywordList2JSON(List<? extends SemanticKeyword> semanticKeywordList) 
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

	/**
	 * @param text
	 * @param result
	 * @return
	 */
	private static ArrayList<NamedEntity> evalTFs(String text, ArrayList<NamedEntity> result) 
	{
		int docSize = WordCounter.countWords(text);
		for( NamedEntity an : result ) {
			String keyword = an.getKeyword().trim();
			int numOccurrences = WordCounter.countWordInstances(text, keyword);
			an.setNumOccurrences(numOccurrences);
			an.setTf((float)numOccurrences / (float)docSize);

			// the regex of countWordInstances compute the presence of only separated words: if entity has some symbols next to it then it won't be counted	 
			if(an.getTf()==0) {
				an.setTf(1);
			}
		}
		return result;
	}
}
